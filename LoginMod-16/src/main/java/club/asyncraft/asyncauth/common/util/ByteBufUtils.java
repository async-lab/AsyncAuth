package club.asyncraft.asyncauth.common.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class ByteBufUtils {

    public static void writeString(ByteBuf byteBuf, String... str) {
        for (String s : str) {
            byte[] strBytes = s.getBytes(StandardCharsets.UTF_8);
            byteBuf.writeInt(strBytes.length);
            byteBuf.writeBytes(strBytes);
        }
    }

    public static String readString(ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        ByteBuf bytes = byteBuf.readBytes(length);
        byte[] data = new byte[length];
        bytes.readBytes(data);
        return new String(data, StandardCharsets.UTF_8);
    }

}
