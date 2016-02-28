package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class files implements Module {

    private static Map<Integer, FileInfo> files;
    
    @Override
    public void init() {
        files = new HashMap<>();
        
        Functions.set("fopen", new fopen());
        Functions.set("listFiles", new listFiles());
        Functions.set("delete", new delete());
        Functions.set("exists", new exists());
        Functions.set("isDirectory", new isDirectory());
        Functions.set("isFile", new isFile());
        Functions.set("mkdir", new mkdir());
        Functions.set("fileSize", new fileSize());
        Functions.set("readBoolean", new readBoolean());
        Functions.set("readByte", new readByte());
        Functions.set("readBytes", new readBytes());
        Functions.set("readAllBytes", new readAllBytes());
        Functions.set("readChar", new readChar());
        Functions.set("readShort", new readShort());
        Functions.set("readInt", new readInt());
        Functions.set("readLong", new readLong());
        Functions.set("readFloat", new readFloat());
        Functions.set("readDouble", new readDouble());
        Functions.set("readUTF", new readUTF());
        Functions.set("readLine", new readLine());
        Functions.set("readText", new readText());
        Functions.set("writeBoolean", new writeBoolean());
        Functions.set("writeByte", new writeByte());
        Functions.set("writeChar", new writeChar());
        Functions.set("writeShort", new writeShort());
        Functions.set("writeInt", new writeInt());
        Functions.set("writeLong", new writeLong());
        Functions.set("writeFloat", new writeFloat());
        Functions.set("writeDouble", new writeDouble());
        Functions.set("writeUTF", new writeUTF());
        Functions.set("writeLine", new writeLine());
        Functions.set("writeText", new writeText());
        Functions.set("flush", new flush());
        Functions.set("fclose", new fclose());
    }
    
    private static class fopen implements Function {

        @Override
        public Value execute(Value... args) {
            if (args.length < 1) throw new ArgumentsMismatchException("At least one argument expected");
            
            final File file = new File(args[0].asString());
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
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            }
            
            DataOutputStream dos = null;
            BufferedWriter writer = null;
            if (mode.contains("wb")) {
                dos = new DataOutputStream(new FileOutputStream(file));
            } else if (mode.contains("w")) {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            }
            
            final int key = files.size();
            files.put(key, new FileInfo(file, dis, dos, reader, writer));
            return new NumberValue(key);
        }
    }
    
    private static abstract class FileFunction implements Function {
        
        @Override
        public Value execute(Value... args) {
            if (args.length < 1) throw new ArgumentsMismatchException("File descriptor expected");
            final int key = args[0].asInt();
            try {
                return execute(files.get(key), args);
            } catch (IOException ioe) {
                return NumberValue.ZERO;
            }
        }
        
        protected abstract Value execute(FileInfo fileInfo, Value[] args) throws IOException;
    }
    
    private static class listFiles extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final String[] files = fileInfo.file.list();
            final int size = files.length;
            final ArrayValue result = new ArrayValue(size);
            for (int i = 0; i < size; i++) {
                result.set(i, new StringValue(files[i]));
            }
            return result;
        }
    }
    
    private static class delete extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.fromBoolean(fileInfo.file.delete());
        }
    }
    
    private static class exists extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.fromBoolean(fileInfo.file.exists());
        }
    }
    
    private static class isDirectory extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.fromBoolean(fileInfo.file.isDirectory());
        }
    }
    
    private static class isFile extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.fromBoolean(fileInfo.file.isFile());
        }
    }
    
    private static class mkdir extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return NumberValue.fromBoolean(fileInfo.file.mkdir());
        }
    }
    
    private static class fileSize extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new NumberValue(fileInfo.file.length());
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
            return new NumberValue(fileInfo.dis.readByte());
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
            final int readed = fileInfo.dis.read(buffer, offset, length);
            for (int i = 0; i < readed; i++) {
                array.set(i, new NumberValue(buffer[i]));
            }
            return new NumberValue(readed);
        }
    }
    
    private static class readAllBytes extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final int bufferSize = 4096;
            final byte[] buffer = new byte[bufferSize];
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int readed;
            while ((readed = fileInfo.dis.read(buffer, 0, bufferSize)) != -1) {
                baos.write(buffer, 0, readed);
            }
            baos.flush();
            baos.close();
            final byte[] bytes = baos.toByteArray();
            final int size = bytes.length;
            final ArrayValue result = new ArrayValue(size);
            for (int i = 0; i < size; i++) {
                result.set(i, new NumberValue(bytes[i]));
            }
            return result;
        }
    }
    
    private static class readChar extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new NumberValue((short)fileInfo.dis.readChar());
        }
    }
    
    private static class readShort extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new NumberValue(fileInfo.dis.readShort());
        }
    }
    
    private static class readInt extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new NumberValue(fileInfo.dis.readInt());
        }
    }
    
    private static class readLong extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new NumberValue(fileInfo.dis.readLong());
        }
    }
    
    private static class readFloat extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new NumberValue(fileInfo.dis.readFloat());
        }
    }
    
    private static class readDouble extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            return new NumberValue(fileInfo.dis.readDouble());
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
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            final StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = fileInfo.reader.read()) != -1) {
                sb.append((char) ch);
            }
            return new StringValue(sb.toString());
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
            fileInfo.dos.writeLong((long) args[1].asNumber());
            return NumberValue.ONE;
        }
    }
    
    private static class writeFloat extends FileFunction {
        @Override
        protected Value execute(FileInfo fileInfo, Value[] args) throws IOException {
            fileInfo.dos.writeFloat((float) args[1].asNumber());
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
