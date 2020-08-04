package com.bytedance.ug.agentmain;

/**
 * @author dmrfcoder
 * @date 2020-07-30
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {
    public static final int BUFFER_SIZE = 8192;

    public StreamUtil() {
    }

    public static int copy(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[8192];

        int count;
        int i;
        for(count = 0; (i = is.read(buf)) != -1; count += i) {
            os.write(buf, 0, i);
        }

        return count;
    }

    public static void closeInputStreamIgnoreException(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }
}
