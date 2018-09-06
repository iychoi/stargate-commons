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
        File f = new File(getTempRoot(), name);
        f.createNewFile();
        f.deleteOnExit();
        return f;
    }
    
    public static File createTempFile(String prefix, String extension) throws IOException {
        File f = File.createTempFile(prefix, extension, getTempRoot());
        f.deleteOnExit();
        return f;
    }
    
    public static File createTempDir(String name) {
        File f = new File(getTempRoot(), name);
        f.deleteOnExit();
        f.mkdirs();
        return f;
    }
    
    public static boolean makeTempRoot() {
        return DirUtils.makeDir(getTempRoot());
    }
    
    public static void clearTempRoot() {
        DirUtils.clearDir(getTempRoot());
    }
}
