/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daogenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Internal API */
public class DaoUtil {
    public static String dbName(String javaName) {
        StringBuilder builder = new StringBuilder(javaName);
        for (int i = 1; i < builder.length(); i++) {
            boolean lastWasUpper = Character.isUpperCase(builder.charAt(i - 1));
            boolean isUpper = Character.isUpperCase(builder.charAt(i));
            if (isUpper && !lastWasUpper) {
                builder.insert(i, '_');
                i++;
            }
        }
        return builder.toString().toUpperCase();
    }
    
    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAllBytes(in, out);
        return out.toByteArray();
    }
    
    public static byte[] readAllBytes(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        try {
            return DaoUtil.readAllBytes(is);
        } finally {
            is.close();
        }
    }
    
    public static byte[] readAllBytes(String filename) throws IOException {
        FileInputStream is = new FileInputStream(filename);
        try {
            return DaoUtil.readAllBytes(is);
        } finally {
            is.close();
        }
    }
    
    /**
     * Copies all available data from in to out without closing any stream.
     * 
     * @return number of bytes copied
     */
    public static int copyAllBytes(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            out.write(buffer, 0, read);
            byteCount += read;
        }
        return byteCount;
    }
    


}
