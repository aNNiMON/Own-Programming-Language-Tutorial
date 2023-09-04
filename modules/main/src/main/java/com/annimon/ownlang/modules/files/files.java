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
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class files implements Module {

    private static Map<Integer, FileInfo> files;

    public static void initConstants() {
        ScopeHandler.setConstant("FILES_COMPARATOR", new FunctionValue(new filesComparatorFunction()));
    }

    @Override
    public void init() {
        files = new HashMap<>();
        initConstants();

        ScopeHandler.setFunction("fopen", new fopen());
        ScopeHandler.setFunction("flush", new flush());
        ScopeHandler.setFunction("fclose", new fclose());
        
        // Operations
        ScopeHandler.setFunction("copy", new copy());
        ScopeHandler.setFunction("delete", fileToBoolean(File::delete));
        ScopeHandler.setFunction("listFiles", new listFiles());
        ScopeHandler.setFunction("mkdir", fileToBoolean(File::mkdir));
        ScopeHandler.setFunction("mkdirs", fileToBoolean(File::mkdirs));
        ScopeHandler.setFunction("rename", new rename());

        // Permissions and statuses
        ScopeHandler.setFunction("canExecute", fileToBoolean(File::canExecute));
        ScopeHandler.setFunction("canRead", fileToBoolean(File::canRead));
        ScopeHandler.setFunction("canWrite", fileToBoolean(File::canWrite));
        ScopeHandler.setFunction("isDirectory", fileToBoolean(File::isDirectory));
        ScopeHandler.setFunction("isFile", fileToBoolean(File::isFile));
        ScopeHandler.setFunction("isHidden", fileToBoolean(File::isHidden));
        ScopeHandler.setFunction("setExecutable", new setExecutable());
        ScopeHandler.setFunction("setReadable", new setReadable());
        ScopeHandler.setFunction("setReadOnly", new setReadOnly());
        ScopeHandler.setFunction("setWritable", new setWritable());

        ScopeHandler.setFunction("exists", fileToBoolean(File::exists));
        ScopeHandler.setFunction("fileSize", new fileSize());
        ScopeHandler.setFunction("getParent", new getParent());
        ScopeHandler.setFunction("lastModified", new lastModified());
        ScopeHandler.setFunction("setLastModified", new setLastModified());

        // IO
        ScopeHandler.setFunction("readBoolean", new readBoolean());
        ScopeHandler.setFunction("readByte", new readByte());
        ScopeHandler.setFunction("readBytes", new readBytes());
        ScopeHandler.setFunction("readAllBytes", new readAllBytes());
        ScopeHandler.setFunction("readChar", new readChar());
        ScopeHandler.setFunction("readShort", new readShort());
        ScopeHandler.setFunction("readInt", new readInt());
        ScopeHandler.setFunction("readLong", new readLong());
        ScopeHandler.setFunction("readFloat", new readFloat());
        ScopeHandler.setFunction("readDouble", new readDouble());
        ScopeHandler.setFunction("readUTF", new readUTF());
        ScopeHandler.setFunction("readLine", new readLine());
        ScopeHandler.setFunction("readText", new readText());
        ScopeHandler.setFunction("writeBoolean", new writeBoolean());
        ScopeHandler.setFunction("writeByte", new writeByte());
        ScopeHandler.setFunction("writeBytes", new writeBytes());
        ScopeHandler.setFunction("writeChar", new writeChar());
        ScopeHandler.setFunction("writeShort", new writeShort());
        ScopeHandler.setFunction("writeInt", new writeInt());
        ScopeHandler.setFunction("writeLong", new writeLong());
        ScopeHandler.setFunction("writeFloat", new writeFloat());
        ScopeHandler.setFunction("writeDouble", new writeDouble());
        ScopeHandler.setFunction("writeUTF", new writeUTF());
        ScopeHandler.setFunction("writeLine", new writeLine());
        ScopeHandler.setFunction("writeText", new writeText());
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
            Arguments.checkAtLeast(1, args.length);
            
            final File file = Console.fileInstance(args[0].asString());
            try {
                if (args.length > 1) {
                    return process(file, args[1].asString().toLowerCase());
                }
                return process(file, "r");
            } catch (IOException ioe) {
                return NumberValue.MINUS_ONE;
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
            try {
                return execute(files.get(key), args);
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
        DataInputStream dis;
        DataOutputStream dos;
        BufferedReader reader;
        BufferedWriter writer;

        public FileInfo(File file, DataInputStream dis, DataOutputStream dos, BufferedReader reader, BufferedWriter writer) {
            this.file = file;
            this.dis = dis;
            this.dos = dos;
            this.reader = reader;
            this.writer = writer;
        }
    }
}
