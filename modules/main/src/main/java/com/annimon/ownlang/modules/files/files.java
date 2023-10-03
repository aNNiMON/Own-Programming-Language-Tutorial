package com.annimon.ownlang.modules.files;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class files implements Module {

    private static Map<Integer, FileInfo> files;

    @Override
    public Map<String, Value> constants() {
        return Map.of(
                "FILES_COMPARATOR", new FunctionValue(new filesComparatorFunction())
        );
    }

    @Override
    public Map<String, Function> functions() {
        files = new HashMap<>();
        final var result = new LinkedHashMap<String, Function>(50);
        result.put("fopen", new fopen());
        result.put("flush", new flush());
        result.put("fclose", new fclose());

        // Operations
        result.put("copy", new copy());
        result.put("delete", fileToBoolean(File::delete));
        result.put("listFiles", new listFiles());
        result.put("mkdir", fileToBoolean(File::mkdir));
        result.put("mkdirs", fileToBoolean(File::mkdirs));
        result.put("rename", new rename());

        // Permissions and statuses
        result.put("canExecute", fileToBoolean(File::canExecute));
        result.put("canRead", fileToBoolean(File::canRead));
        result.put("canWrite", fileToBoolean(File::canWrite));
        result.put("isDirectory", fileToBoolean(File::isDirectory));
        result.put("isFile", fileToBoolean(File::isFile));
        result.put("isHidden", fileToBoolean(File::isHidden));
        result.put("setExecutable", new setExecutable());
        result.put("setReadable", new setReadable());
        result.put("setReadOnly", new setReadOnly());
        result.put("setWritable", new setWritable());

        result.put("exists", fileToBoolean(File::exists));
        result.put("fileSize", new fileSize());
        result.put("getParent", new getParent());
        result.put("lastModified", new lastModified());
        result.put("setLastModified", new setLastModified());

        // IO
        result.put("readBoolean", new readBoolean());
        result.put("readByte", new readByte());
        result.put("readBytes", new readBytes());
        result.put("readAllBytes", new readAllBytes());
        result.put("readChar", new readChar());
        result.put("readShort", new readShort());
        result.put("readInt", new readInt());
        result.put("readLong", new readLong());
        result.put("readFloat", new readFloat());
        result.put("readDouble", new readDouble());
        result.put("readUTF", new readUTF());
        result.put("readLine", new readLine());
        result.put("readText", new readText());
        result.put("writeBoolean", new writeBoolean());
        result.put("writeByte", new writeByte());
        result.put("writeBytes", new writeBytes());
        result.put("writeChar", new writeChar());
        result.put("writeShort", new writeShort());
        result.put("writeInt", new writeInt());
        result.put("writeLong", new writeLong());
        result.put("writeFloat", new writeFloat());
        result.put("writeDouble", new writeDouble());
        result.put("writeUTF", new writeUTF());
        result.put("writeLine", new writeLine());
        result.put("writeText", new writeText());
        return result;
    }

    private static class filesComparatorFunction implements Function {

        @Override
        public Value execute(Value[] args) {
            Arguments.checkAtLeast(2, args.length);
            
            final int fd1 = args[0].asInt();
            final int fd2 = args[1].asInt();
            if (!files.containsKey(fd1)) {
                return NumberValue.of(files.containsKey(fd2) ? 1 : 0);
            }
            if (!files.containsKey(fd2)) {
                return NumberValue.of(files.containsKey(fd1) ? -1 : 0);
            }

            final File file1 = files.get(fd1).file;
            final File file2 = files.get(fd2).file;
            return NumberValue.of(file1.compareTo(file2));
        }
    }
    
    private static class fopen implements Function {

        @Override
        public Value execute(Value[] args) {
            Arguments.checkOrOr(1, 2, args.length);
            
            final File file = Console.fileInstance(args[0].asString());
            try {
                final String mode = (args.length == 2)
                        ? args[1].asString().toLowerCase()
                        : "r";
                return process(file, mode);
            } catch (IOException ioe) {
                final int key = -files.size() - 1;
                files.put(key, new FileInfo(ioe.getMessage()));
                return NumberValue.of(key);
            }
        }
        
        private Value process(File file, String mode) throws IOException {
            DataInputStream dis = null;
            BufferedReader reader = null;
            if (mode.contains("rb")) {
                dis = new DataInputStream(new FileInputStream(file));
            } else if (mode.contains("r")) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            }
            
            DataOutputStream dos = null;
            BufferedWriter writer = null;
            final boolean append = mode.contains("+");
            if (mode.contains("wb")) {
                dos = new DataOutputStream(new FileOutputStream(file, append));
            } else if (mode.contains("w")) {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8));
            }
            
            final int key = files.size();
            files.put(key, new FileInfo(file, dis, dos, reader, writer));
            return NumberValue.of(key);
        }
    }
    
    private abstract static class FileFunction implements Function {
        
        @Override
        public Value execute(Value[] args) {
            if (args.length < 1) throw new ArgumentsMismatchException("File descriptor expected");
            final int key = args[0].asInt();
            final FileInfo fileInfo = files.get(key);
            if (key < 0) throw new ArgumentsMismatchException(fileInfo.error);
            try {
                return execute(fileInfo, args);
            } catch (IOException ioe) {
                return NumberValue.MINUS_ONE;
            }
        }
        
        protected abstract Value execute(FileInfo fileInfo, Value[] args) throws IOException;
    }

    private static class listFiles extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return ArrayValue.of(fileInfo.file.list());
        }
    }

    private static class copy implements Function {

        @Override
        public Value execute(Value[] args) {
            Arguments.check(2, args.length);
            try {
                final FileInputStream is = new FileInputStream(fileFrom(args[0]));
                final FileOutputStream os = new FileOutputStream(fileFrom(args[1]));
                final FileChannel ic = is.getChannel();
                ic.transferTo(0, ic.size(), os.getChannel());
                is.close();
                os.close();
                return NumberValue.ONE;
            } catch (IOException ioe) {
                return NumberValue.MINUS_ONE;
            }
        }
    }

    private static class rename implements Function {

        @Override
        public Value execute(Value[] args) {
            Arguments.check(2, args.length);
            return NumberValue.fromBoolean( fileFrom(args[0]).renameTo(fileFrom(args[1])) );
        }
    }
    
    private static class fileSize extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of(fileInfo.file.length());
        }
    }

    private static class getParent extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final String parent = fileInfo.file.getParent();
            return new StringValue(parent == null ? "" : parent);
        }
    }

    private static class lastModified extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of(fileInfo.file.lastModified());
        }
    }

    private static class setLastModified extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final long time;
            if (args[1].type() == Types.NUMBER) {
                time = ((NumberValue)args[1]).asLong();
            } else {
                time = (long) args[1].asNumber();
            }
            return NumberValue.fromBoolean(fileInfo.file.setLastModified(time));
        }
    }

    private static class setReadOnly extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.fromBoolean(fileInfo.file.setReadOnly());
        }
    }

    private static class setExecutable extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final boolean ownerOnly = (args.length < 3) || (args[2].asInt() != 0);
            return NumberValue.fromBoolean(
                    fileInfo.file.setExecutable(args[1].asInt() != 0, ownerOnly));
        }
    }

    private static class setReadable extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final boolean ownerOnly = (args.length < 3) || (args[2].asInt() != 0);
            return NumberValue.fromBoolean(
                    fileInfo.file.setReadable(args[1].asInt() != 0, ownerOnly));
        }
    }

    private static class setWritable extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final boolean ownerOnly = (args.length < 3) || (args[2].asInt() != 0);
            return NumberValue.fromBoolean(
                    fileInfo.file.setWritable(args[1].asInt() != 0, ownerOnly));
        }
    }
    
    private static class readBoolean extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.fromBoolean(fileInfo.dis.readBoolean());
        }
    }
    
    private static class readByte extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of(fileInfo.dis.readByte());
        }
    }
    
    private static class readBytes extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final ArrayValue array = (ArrayValue) args[1];
            int offset = 0, length = array.size();
            if (args.length > 3) {
                offset = args[2].asInt();
                length = args[3].asInt();
            }
            
            final byte[] buffer = new byte[length];
            final int read = fileInfo.dis.read(buffer, 0, length);
            for (int i = 0; i < read; i++) {
                array.set(offset + i, NumberValue.of(buffer[i]));
            }
            return NumberValue.of(read);
        }
    }
    
    private static class readAllBytes extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final int bufferSize = 4096;
            final byte[] buffer = new byte[bufferSize];
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read;
            while ((read = fileInfo.dis.read(buffer, 0, bufferSize)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
            baos.close();
            return ArrayValue.of(baos.toByteArray());
        }
    }
    
    private static class readChar extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of((short)fileInfo.dis.readChar());
        }
    }
    
    private static class readShort extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of(fileInfo.dis.readShort());
        }
    }
    
    private static class readInt extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of(fileInfo.dis.readInt());
        }
    }
    
    private static class readLong extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of(fileInfo.dis.readLong());
        }
    }
    
    private static class readFloat extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of(fileInfo.dis.readFloat());
        }
    }
    
    private static class readDouble extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.of(fileInfo.dis.readDouble());
        }
    }
    
    private static class readUTF extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new StringValue(fileInfo.dis.readUTF());
        }
    }
    
    private static class readLine extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new StringValue(fileInfo.reader.readLine());
        }
    }
    
    private static class readText extends FileFunction {

        private static final int BUFFER_SIZE = 4096;

        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final StringBuilder result = new StringBuilder();
            final char[] buffer = new char[BUFFER_SIZE];
            int read;
            while ((read = fileInfo.reader.read(buffer, 0, BUFFER_SIZE)) != -1) {
                result.append(buffer, 0, read);
            }
            return new StringValue(result.toString());
        }
    }
    
    
    private static class writeBoolean extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.dos.writeBoolean(args[1].asInt() != 0);
            return NumberValue.ONE;
        }
    }
    
    private static class writeByte extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.dos.writeByte((byte) args[1].asInt());
            return NumberValue.ONE;
        }
    }

    private static class writeBytes extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final ArrayValue array = (ArrayValue) args[1];
            int offset = 0, length = array.size();
            final byte[] bytes = new byte[length];
            for (int i = 0; i < length; i++) {
                bytes[i] = (byte) (array.get(i).asInt() & 0xFF);
            }
            if (args.length > 3) {
                offset = args[2].asInt();
                length = args[3].asInt();
            }
            fileInfo.dos.write(bytes, offset, length);
            return NumberValue.ONE;
        }
    }
    
    private static class writeChar extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final char ch = (args[1].type() == Types.NUMBER)
                    ? ((char) args[1].asInt())
                    : args[1].asString().charAt(0);
            fileInfo.dos.writeChar(ch);
            return NumberValue.ONE;
        }
    }
    
    private static class writeShort extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.dos.writeShort((short) args[1].asInt());
            return NumberValue.ONE;
        }
    }
    
    private static class writeInt extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.dos.writeInt(args[1].asInt());
            return NumberValue.ONE;
        }
    }
    
    private static class writeLong extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final long value;
            if (args[1].type() == Types.NUMBER) {
                value = ((NumberValue)args[1]).asLong();
            } else {
                value = (long) args[1].asNumber();
            }
            fileInfo.dos.writeLong(value);
            return NumberValue.ONE;
        }
    }
    
    private static class writeFloat extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final float value;
            if (args[1].type() == Types.NUMBER) {
                value = ((NumberValue)args[1]).asFloat();
            } else {
                value = (float) args[1].asNumber();
            }
            fileInfo.dos.writeFloat(value);
            return NumberValue.ONE;
        }
    }
    
    private static class writeDouble extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.dos.writeDouble(args[1].asNumber());
            return NumberValue.ONE;
        }
    }
    
    private static class writeUTF extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.dos.writeUTF(args[1].asString());
            return NumberValue.ONE;
        }
    }
    
    private static class writeLine extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.writer.write(args[1].asString());
            fileInfo.writer.newLine();
            return NumberValue.ONE;
        }
    }

    private static class writeText extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.writer.write(args[1].asString());
            return NumberValue.ONE;
        }
    }
    
    private static class flush extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            if (fileInfo.dos != null) {
                fileInfo.dos.flush();
            }
            if (fileInfo.writer != null) {
                fileInfo.writer.flush();
            }
            return NumberValue.ONE;
        }
    }
    
    private static class fclose extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            if (fileInfo.dis != null) {
                fileInfo.dis.close();
            }
            if (fileInfo.dos != null) {
                fileInfo.dos.close();
            }
            if (fileInfo.reader != null) {
                fileInfo.reader.close();
            }
            if (fileInfo.writer != null) {
                fileInfo.writer.close();
            }
            return NumberValue.ONE;
        }
    }

    private static File fileFrom(Value value) {
        if (value.type() == Types.NUMBER) {
            return files.get(value.asInt()).file;
        }
        return Console.fileInstance(value.asString());
    }

    private interface FileToBooleanFunction {

        boolean apply(File file);
    }

    private static Function fileToBoolean(FileToBooleanFunction f) {
        return args -> {
            Arguments.check(1, args.length);
            return NumberValue.fromBoolean(f.apply(fileFrom(args[0])));
        };
    }
    
    private static class FileInfo {
        File file;
        String error;
        DataInputStream dis;
        DataOutputStream dos;
        BufferedReader reader;
        BufferedWriter writer;

        FileInfo(String errorMessage) {
            this.error = errorMessage;
        }

        FileInfo(File file, DataInputStream dis, DataOutputStream dos, BufferedReader reader, BufferedWriter writer) {
            this.file = file;
            this.dis = dis;
            this.dos = dos;
            this.reader = reader;
            this.writer = writer;
        }
    }
}
