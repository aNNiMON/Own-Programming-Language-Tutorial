package com.annimon.ownlang.modules.gzip;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class gzip implements Module {

    @Override
    public void init() {
        ScopeHandler.setFunction("gzip", this::gzipFile);
        ScopeHandler.setFunction("gzipBytes", this::gzipBytes);
        ScopeHandler.setFunction("ungzip", this::ungzipFile);
        ScopeHandler.setFunction("ungzipBytes", this::ungzipBytes);
    }

    private Value gzipFile(Value[] args) {
        Arguments.check(2, args.length);
        
        final File input = new File(args[0].asString());
        if (!input.exists() || !input.canRead() || input.isDirectory()) {
            return NumberValue.MINUS_ONE;
        }
        final File output = new File(args[1].asString());
        if (output.isDirectory()) {
            return NumberValue.MINUS_ONE;
        }
        
        try (InputStream is = new FileInputStream(input);
             OutputStream os = new FileOutputStream(output);
             GZIPOutputStream gzos = new GZIPOutputStream(os)) {
            copy(is, gzos);
            gzos.finish();
            return NumberValue.ONE;
        } catch (IOException ex) {
            throw new RuntimeException("gzipFile", ex);
        }
    }
    
    private Value gzipBytes(Value[] args) {
        Arguments.check(1, args.length);
        
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Byte array expected at first argument");
        }
        final byte[] input = ValueUtils.toByteArray(((ArrayValue) args[0]));
        try (InputStream is = new ByteArrayInputStream(input);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            copy(is, gzos);
            gzos.finish();
            return ArrayValue.of(baos.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeException("gzipBytes", ex);
        }
    }
    
    private Value ungzipFile(Value[] args) {
        Arguments.check(2, args.length);
        
        final File input = new File(args[0].asString());
        if (!input.exists() || !input.canRead() || input.isDirectory()) {
            return NumberValue.MINUS_ONE;
        }
        final File output = new File(args[1].asString());
        if (output.isDirectory()) {
            return NumberValue.MINUS_ONE;
        }
        
        try (InputStream is = new FileInputStream(input);
             GZIPInputStream gzis = new GZIPInputStream(is);
             OutputStream os = new FileOutputStream(output)) {
            copy(gzis, os);
            return NumberValue.ONE;
        } catch (IOException ex) {
            throw new RuntimeException("ungzipFile", ex);
        }
    }
    
    private Value ungzipBytes(Value[] args) {
        Arguments.check(1, args.length);
        
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Byte array expected at first argument");
        }
        final byte[] input = ValueUtils.toByteArray(((ArrayValue) args[0]));
        try (InputStream is = new ByteArrayInputStream(input);
             GZIPInputStream gzis = new GZIPInputStream(is);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            copy(gzis, baos);
            return ArrayValue.of(baos.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeException("ungzipBytes", ex);
        }
    }
    
    private void copy(InputStream is, OutputStream os) throws IOException {
        final byte[] buffer = new byte[8192];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }
    }
}
