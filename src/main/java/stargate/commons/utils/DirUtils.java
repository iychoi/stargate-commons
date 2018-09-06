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

/**
 *
 * @author iychoi
 */
public class DirUtils {
    public static boolean makeDir(File f) {
        if(!f.exists()) {
            return f.mkdirs();
        } else {
            return true;
        }
    }
    
    public static void clearDir(File f) {
        if(f.exists()) {
            File[] files = f.listFiles();
            for(File file : files) {
                removeRecursively(file);
            }
        }
    }
    
    private static void removeRecursively(File f) {
        if(f.isFile()) {
            f.delete();
        } else if(f.isDirectory()) {
            File[] files = f.listFiles();
            for(File file : files) {
                removeRecursively(file);
            }
        }
    }
}
