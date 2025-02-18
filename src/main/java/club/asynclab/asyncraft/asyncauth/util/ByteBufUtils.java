package club.asynclab.asyncraft.asyncauth.util;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ByteBufUtils {

    public static void writeString(FriendlyByteBuf byteBuf, String... str) {
        for (String s : str) {
            byte[] strBytes = s.getBytes(StandardCharsets.UTF_8);
            byteBuf.writeInt(strBytes.length);
            byteBuf.writeBytes(strBytes);
        }
    }

    public static String readString(FriendlyByteBuf byteBuf) {
        int length = byteBuf.readInt();
        ByteBuf bytes = byteBuf.readBytes(length);
        byte[] data = new byte[length];
        bytes.readBytes(data);
        return new String(data, StandardCharsets.UTF_8);
    }

    public static void writeObject(FriendlyByteBuf byteBuf,Object obj) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream  =null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            AsyncAuth.LOGGER.error("处理数据包出错",e);
        } finally {
            try {
                assert objectOutputStream != null;
                objectOutputStream.close();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                AsyncAuth.LOGGER.error("处理数据包出错",e);
            }
        }
        if (bytes == null) {
            throw new RuntimeException("Write object failed ");
        }
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public static Object readObject(FriendlyByteBuf byteBuf) throws Exception {
        int length = byteBuf.readInt();
        ByteBuf bytesBuf = byteBuf.readBytes(length);
        byte[] bytes = new byte[length];
        bytesBuf.readBytes(bytes);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        Object result = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            result = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            AsyncAuth.LOGGER.error("处理数据包出错",e);
        }
        if (result == null) {
            throw new RuntimeException("Read Object bytes failed");
        }
        return result;
    }

}
