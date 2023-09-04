package com.annimon.ownlang.modules.zip;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class zip implements Module {

    @Override
    public void init() {
        ScopeHandler.setFunction("zip", this::zipWithMapper);
        ScopeHandler.setFunction("zipFiles", this::zipFiles);
        ScopeHandler.setFunction("unzip", this::unzip);
        ScopeHandler.setFunction("unzipFiles", this::unzipFiles);
        ScopeHandler.setFunction("listZipEntries", this::listZipEntries);
    }

    private Value zipWithMapper(Value[] args) {
        Arguments.checkOrOr(2, 3, args.length);
        final String inputPath = args[0].asString();
        final File input = new File(inputPath);
        if (!input.exists()) {
            return NumberValue.MINUS_ONE;
        }
        final File output = new File(args[1].asString());
        if (output.isDirectory()) {
            return NumberValue.MINUS_ONE;
        }
        final Function mapper = getMapperOrNull(args, 2);
        
        final Map<File, String> mappings = new HashMap<>();
        final String rootPath = (input.isFile() ? input.getParent() : input.getAbsolutePath());
        generateFileList(mappings, rootPath, input, mapper);
        return zipFileList(mappings, output);
    }
    
    private Value zipFiles(Value[] args) {
        Arguments.check(2, args.length);
        
        final Map<File, String> mappings = new HashMap<>();
        switch (args[0].type()) {
            case Types.STRING: {
                final File file = new File(args[0].asString());
                mappings.put(file, file.getName());
            } break;
            case Types.ARRAY:
                for (Value value : ((ArrayValue) args[0])) {
                    final File file = new File(value.asString());
                    mappings.put(file, file.getName());
                }
                break;
            case Types.MAP:
                for (Map.Entry<Value, Value> entry : ((MapValue) args[0])) {
                    final File file = new File(entry.getKey().asString());
                    mappings.put(file, entry.getValue().asString());
                }
                break;
            default:
                throw new TypeException("Single file path, file paths array or file mappings expected at first argument");
        }
        
        final File output = new File(args[1].asString());
        if (output.isDirectory()) {
            return NumberValue.MINUS_ONE;
        }
        return zipFileList(mappings, output);
    }
    
    private Value zipFileList(Map<File, String> mappings, File output) {
        int count = 0;
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output))) {
            for (Map.Entry<File, String> entry : mappings.entrySet()) {
                final File node = entry.getKey();
                final String entryName = entry.getValue();
                if (node.isDirectory()) {
                    zos.putNextEntry(new ZipEntry(entryName + (entryName.endsWith("/") ? "" : "/")));
                } else {
                    zipFile(zos, entry.getKey(), entry.getValue());
                    count++;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("zip files", ex);
        }
        return NumberValue.of(count);
    }

    private void zipFile(ZipOutputStream zos, File file, String entryPath) throws IOException {
        final ZipEntry entry = new ZipEntry(entryPath);
        entry.setTime(file.lastModified());
        zos.putNextEntry(entry);
        try (FileInputStream fis = new FileInputStream(file)) {
            copy(fis, zos);
        }
    }

    private Value unzip(Value[] args) {
        Arguments.checkOrOr(2, 3, args.length);
        final File input = new File(args[0].asString());
        if (!input.exists() || !input.canRead() || input.isDirectory()) {
            return NumberValue.MINUS_ONE;
        }
        final File output = new File(args[1].asString());
        if (!output.exists()) {
            output.mkdirs();
        }
        final Function mapper = getMapperOrNull(args, 2);
        
        final Map<String, File> mappings = new HashMap<>();
        for (String entryName : listEntries(input)) {
            String fileName = entryName;
            if (mapper != null) {
                fileName = mapper.execute(new StringValue(entryName)).asString();
            }
            if (!fileName.isEmpty()) {
                mappings.put(entryName, new File(output, fileName));
            }
        }
        return unzipFileList(input, mappings);
    }
    
    private Value unzipFiles(Value[] args) {
        Arguments.check(2, args.length);
        
        final File input = new File(args[0].asString());
        if (!input.exists() || !input.canRead() || input.isDirectory()) {
            return NumberValue.MINUS_ONE;
        }
        
        final Map<String, File> mappings = new HashMap<>();
        switch (args[1].type()) {
            case Types.STRING: {
                final String entryPath = args[1].asString();
                mappings.put(entryPath, new File(entryPath));
            } break;
            case Types.ARRAY:
                for (Value value : ((ArrayValue) args[1])) {
                    final String entryPath = value.asString();
                    mappings.put(entryPath, new File(entryPath));
                }
                break;
            case Types.MAP:
                for (Map.Entry<Value, Value> entry : ((MapValue) args[1])) {
                    final File file = new File(entry.getValue().asString());
                    mappings.put(entry.getKey().asString(), file);
                }
                break;
            default:
                throw new TypeException("Single entry path, entry paths array or entry mappings expected at second argument");
        }
        return unzipFileList(input, mappings);
    }

    private Value unzipFileList(File input, Map<String, File> mappings) {
        int count = 0;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(input))) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                final String entryName = ze.getName();
                final File file = mappings.get(entryName);
                if (file == null) continue;
                
                if (entryName.endsWith("/")) {
                    safeMkdirs(file);
                } else {
                    safeMkdirs(file.getParentFile());
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        copy(zis, fos);
                    }
                    if (ze.getTime() > 0) {
                        file.setLastModified(ze.getTime());
                    }
                    count++;
                }
            }
            zis.closeEntry();
        } catch (IOException ex) {
            throw new RuntimeException("unzip files", ex);
        }
        return NumberValue.of(count);
    }
    
    private Value listZipEntries(Value[] args) {
        Arguments.check(1, args.length);
        final File input = new File(args[0].asString());
        if (!input.exists() || !input.canRead() || input.isDirectory()) {
            return new ArrayValue(0);
        }
        return ArrayValue.of(listEntries(input));
    }
    
    private Function getMapperOrNull(Value[] args, int index) {
        Function mapper;
        if (args.length >= (index + 1)) {
            mapper = ValueUtils.consumeFunction(args[index], index);
        } else {
            mapper = null;
        }
        return mapper;
    }
    
    private void copy(InputStream is, OutputStream os) throws IOException {
        final byte[] buffer = new byte[8192];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
    }
    
    private void generateFileList(Map<File, String> mappings, String rootPath, File node, Function mapper) {
        if (rootPath != null && !rootPath.equals(node.getAbsolutePath())) {
            String entryPath = node.getAbsolutePath().substring(rootPath.length() + 1);
            if (mapper != null) {
                entryPath = mapper.execute(new StringValue(entryPath)).asString();
                if (entryPath.isEmpty()) {
                    return;
                }
            }
            mappings.put(node, entryPath);
        }
        
        if (node.isDirectory()) {
            for (File file : safeListFiles(node)) {
                generateFileList(mappings, rootPath, file, mapper);
            }
        }
    }

    private File[] safeListFiles(File node) {
        final File[] files = node.listFiles();
        if (files != null) {
            return files;
        }
        return new File[0];
    }
    
    private String[] listEntries(File input) {
        final List<String> entries = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(input))) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                entries.add(ze.getName());
            }
            zis.closeEntry();
        } catch (IOException ex) {
            throw new RuntimeException("list zip entries", ex);
        }
        return entries.toArray(new String[0]);
    }

    private void safeMkdirs(File file) {
        if (file != null) {
            file.mkdirs();
        }
    }
}
