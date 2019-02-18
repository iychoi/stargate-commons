/*
   Copyright 2018 The Trustees of University of Arizona

   Licensed under the Apache License, Version 2.0 (the "License" );
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package stargate.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author iychoi
 */
public class IOUtils {
    
    private static int BUFFER_SIZE = 64*1024;
    
    public static String readString(InputStream is) throws IOException {
        return new String(read(is));
    }
    
    public static byte[] read(InputStream is) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        
        byte[] buffer = new byte[BUFFER_SIZE];
        int read = 0;
        
        while((read = bis.read(buffer, 0, BUFFER_SIZE)) > 0) {
            baos.write(buffer, 0, read);
        }
        
        return baos.toByteArray();
    }
    
    public static void writeString(OutputStream os, String json) throws IOException {
        write(os, json.getBytes());
    }
    
    public static void write(OutputStream os, byte[] bytes) throws IOException {
        if(os == null) {
            throw new IllegalArgumentException("os is null");
        }
        
        if(bytes == null) {
            throw new IllegalArgumentException("bytes is null");
        }
        
        BufferedOutputStream bos = new BufferedOutputStream(os);
        bos.write(bytes);
    }
    
    public static byte[] toByteArray(final InputStream is) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = is.read(buffer)) > 0) {
            output.write(buffer, 0, read);
        }
        
        output.close();
        return output.toByteArray();
    }
}
