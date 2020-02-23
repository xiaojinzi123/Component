package com.xiaojinzi.component.plugin.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    public static String isToString(InputStream is) throws IOException {
        byte[] bf = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len = -1;
        while ((len = is.read(bf)) != -1) {
            byteArrayOutputStream.write(bf, 0, len);
        }
        is.close();
        return byteArrayOutputStream.toString();
    }

    public static String readFileAsString(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            return isToString(fileInputStream);
        }finally {
            fileInputStream.close();
        }
    }

    public static void stringToFile(File file, String content) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(content.getBytes());
        fileOutputStream.close();
    }

}
