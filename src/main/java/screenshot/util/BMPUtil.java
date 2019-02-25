package screenshot.util;

import java.nio.ByteBuffer;

public class BMPUtil {

    public static void fillHeader(ByteBuffer buf, int w, int h) {
        buf.put((byte) 'B');
        buf.put((byte) 'M');
        buf.putInt(getFileSize(w, h));
        buf.putInt(0);
        buf.putInt(54);
        buf.putInt(40);
        buf.putInt(w);
        buf.putInt(h);
        buf.putShort((short) 1);
        buf.putShort((short) 24);
        buf.putInt(0);
        buf.putInt(getPaddedSize(w, h));
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
        buf.putInt(0);
    }

    public static int getPaddedSize(int w, int h) {
        return (w * 3 + (4 - (w * 3 % 4)) % 4) * h;
    }

    public static int getFileSize(int w, int h) {
        return getPaddedSize(w, h) + 54;
    }
}
