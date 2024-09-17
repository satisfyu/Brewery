package net.satisfy.brewery.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public final class CodecUtil {
    // Thanks https://github.com/riverbytheocean/riverkeys-fabric
    // ^^
    // These are credits btw
    public static StreamCodec<ByteBuf, int[]> INT_ARRAY = new StreamCodec<>() {

        public int @NotNull [] decode(ByteBuf buf) {
            return readIntArray(buf, buf.readableBytes());
        }

        @Override
        public void encode(ByteBuf buf, int[] array) {
            writeVarInt(buf, array.length);

            for (int i : array)
                writeVarInt(buf, i);
        }
    };

    private static int[] readIntArray(ByteBuf buf, int maxSize) {
        int i = readVarInt(buf);
        if (i > maxSize) {
            throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + maxSize);
        } else {
            int[] is = new int[i];

            for (int j = 0; j < is.length; ++j)
                is[j] = readVarInt(buf);

            return is;
        }
    }

    private static int readVarInt(ByteBuf buf) {
        byte b0;
        int i = 0;
        int j = 0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 0x7F) << j++ * 7;
            if (j <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((b0 & 0x80) == 128);
        return i;
    }

    private static void writeVarInt(ByteBuf buf, int value) {
        while ((value & -128) != 0) {
            buf.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        buf.writeByte(value);
    }
}
