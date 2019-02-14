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
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author iychoi
 */
public class PathUtils {
    public static String getFileName(URI path) {
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        
        return getFileName(path.getPath());
    }
    
    public static String getFileName(String path) {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        int idx = path.lastIndexOf("/");
        String filename = path;
        if(idx >= 0) {
            if(idx+1 >= path.length()) {
                filename = null;
            } else {
                filename = path.substring(idx+1);
            }
        }
        return filename;
    }
    
    public static String getFileExtension(URI path) {
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        
        return getFileExtension(path.getPath());
    }
    
    public static String getFileExtension(String path) {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        String filename = getFileName(path);
        int idx = filename.lastIndexOf(".");
        if(idx >= 0) {
            if(idx+1 >= path.length()) {
                return null;
            } else {
                return filename.substring(idx+1);
            }
        }
        return null;
    }
    
    public static String getParent(URI path) {
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        
        return getParent(path.normalize().getPath());
    }
    
    public static String getParent(String path) {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        // check root
        if(path.equals("/")) {
            return null;
        }
        
        int lastIdx = path.lastIndexOf("/");
        if(lastIdx > 0) {
            return path.substring(0, lastIdx);
        } else {
            return "/";
        }
    }
    
    public static Collection<String> getParents(String path) {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        // check root
        if(path.equals("/")) {
            return null;
        }
        
        String absPath = path;
        if(!path.startsWith("/")) {
            absPath = "/" + path;
        }
        
        List<String> parents = new ArrayList<String>();
        int start = 0;
        int idx = 0;
        while((idx = absPath.indexOf("/", start)) >= 0) {
            if(idx == 0) {
                parents.add("/");
            } else {
                String dir = absPath.substring(0, idx);
                parents.add(dir);
            }
            
            start = idx + 1;
            if(start >= absPath.length()) {
                break;
            }
        }
        return Collections.unmodifiableCollection(parents);
    }
    
    public static String concatPath(String path1, String path2) {
        // path1 and path2 can be null
        
        StringBuilder sb = new StringBuilder();
        sb.append("/");
        
        if(path1 != null && !path1.isEmpty()) {
            if(path1.startsWith("/")) {
                sb.append(path1.substring(1, path1.length()));
            } else {
                sb.append(path1);
            }
            
            if(!path1.endsWith("/")) {
                sb.append("/");
            }
        }
        
        if(path2 != null && !path2.isEmpty()) {
            if(path2.startsWith("/")) {
                sb.append(path2.substring(1, path2.length()));
            } else {
                sb.append(path2);
            }
        }
        
        return sb.toString();
    }
    
    public static String makeAbsolutePath(String path) {
        //if(path == null || path.isEmpty()) {
        //    throw new IllegalArgumentException("path is null or empty");
        //}
        // path can be null
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("/");
        if(path != null) {
            if(path.startsWith("/")) {
                sb.append(path.substring(1));
            } else {
                sb.append(path);
            }
        }
        
        return sb.toString();
    }
    
    public static String getWorkingDir() {
        File currentDir = new File("");
        return currentDir.getAbsolutePath();
    }
}
