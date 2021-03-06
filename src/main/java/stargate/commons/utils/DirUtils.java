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
    public synchronized static boolean makeDir(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        if(!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }
    
    public synchronized static void clearDir(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        if(file.exists()) {
            File[] files = file.listFiles();
            for(File f : files) {
                removeRecursively(f);
            }
        }
    }
    
    private synchronized static void removeRecursively(File file) {
        if(file.isFile()) {
            file.delete();
        } else if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f : files) {
                removeRecursively(f);
            }
        }
    }
}
