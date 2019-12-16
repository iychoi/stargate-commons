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
    
    public static File createTempFile(String name) throws IOException {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        File f = new File(getTempRoot(), name);
        f.createNewFile();
        f.setReadable(true, false);
        f.setWritable(true, false);
        f.deleteOnExit();
        return f;
    }
    
    public static File createTempFile(String prefix, String extension) throws IOException {
        // prefix and extension can be null
        
        File f = File.createTempFile(prefix, extension, getTempRoot());
        f.setReadable(true, false);
        f.setWritable(true, false);
        f.deleteOnExit();
        return f;
    }
    
    public static File createTempDir(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        File f = new File(getTempRoot(), name);
        f.deleteOnExit();
        f.mkdirs();
        f.setReadable(true, false);
        f.setWritable(true, false);
        return f;
    }
    
    public static boolean makeTempRoot() {
        File tempRoot = getTempRoot();
        boolean makeDir = DirUtils.makeDir(tempRoot);
        tempRoot.setReadable(true, false);
        tempRoot.setWritable(true, false);
        return makeDir;
    }
    
    public static void clearTempRoot() {
        DirUtils.clearDir(getTempRoot());
    }
}
