package com.xiaojinzi.component.plugin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * time   : 2019/10/28
 *
 * @author : xiaojinzi
 */
public class IOUtil {

    public static void readAndWrite(InputStream is, OutputStream out) throws IOException {
        byte buff[] = new byte[1024];
        int len = -1;
        while ((len = is.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
    }

}
