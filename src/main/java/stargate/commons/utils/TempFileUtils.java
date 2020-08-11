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

import java.io.File;
import java.io.IOException;

/**
 *
 * @author iychoi
 */
public class TempFileUtils {
    private static final String TEMP_PATH_ROOT = "/tmp/stargate/";
    
    public static File getTempRoot() {
        return new File(TEMP_PATH_ROOT);
    }
    
    public synchronized static File createTempFile(String name) throws IOException {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        File f = new File(getTempRoot(), name);
        if(!f.exists()) {
            f.createNewFile();
            f.setReadable(true, false);
            f.setWritable(true, false);
        }
        f.deleteOnExit();
        return f;
    }
    
    public synchronized static File createTempFile(String prefix, String extension) throws IOException {
        // prefix and extension can be null
        
        File f = File.createTempFile(prefix, extension, getTempRoot());
        f.setReadable(true, false);
        f.setWritable(true, false);
        f.deleteOnExit();
        return f;
    }
    
    public synchronized static File createTempDir(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        File f = new File(getTempRoot(), name);
        if(!f.exists()) {
            f.mkdirs();
            f.setReadable(true, false);
            f.setWritable(true, false);
        }
        f.deleteOnExit();
        return f;
    }
    
    public synchronized static boolean makeTempRoot() {
        File tempRoot = getTempRoot();
        if(tempRoot.exists()) {
            return true;
        } else {
            boolean makeDir = tempRoot.mkdirs();
            if(makeDir) {
                // created
                tempRoot.setReadable(true, false);
                tempRoot.setWritable(true, false);
                return true;
            } else {
                // not created
                return tempRoot.exists();
            }
        }
    }
    
    public synchronized static void clearTempRoot() {
        DirUtils.clearDir(getTempRoot());
    }
}
