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
package stargate.commons.datasource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class SourceFileMetadata {
    private static final Log LOG = LogFactory.getLog(SourceFileMetadata.class);
    
    private URI uri;
    private boolean exist;
    private boolean directory;
    private long fileSize;
    private long lastModifiedTime;
    
    public static SourceFileMetadata createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (SourceFileMetadata) JsonSerializer.fromJsonFile(file, SourceFileMetadata.class);
    }
    
    public static SourceFileMetadata createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (SourceFileMetadata) JsonSerializer.fromJson(json, SourceFileMetadata.class);
    }
    
    SourceFileMetadata() {
        this.uri = null;
        this.exist = false;
        this.directory = false;
        this.fileSize = 0;
        this.lastModifiedTime = 0;
    }
    
    public SourceFileMetadata(URI uri, boolean exist, boolean directory, long fileSize, long lastModifiedTime) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        if(fileSize < 0) {
            throw new IllegalArgumentException("fileSize cannot be a negative number");
        }
        
        if(lastModifiedTime < 0) {
            throw new IllegalArgumentException("lastModifiedTime cannot be a negative number");
        }
        
        initialize(uri, exist, directory, fileSize, lastModifiedTime);
    }
    
    private void initialize(URI uri, boolean exist, boolean directory, long fileSize, long lastModifiedTime) {
        this.uri = uri;
        this.exist = exist;
        this.directory = directory;
        this.fileSize = fileSize;
        this.lastModifiedTime = lastModifiedTime;
    }
    
    @JsonProperty("uri")
    public URI getURI() {
        return this.uri;
    }
    
    @JsonProperty("uri")
    public void setURI(URI uri) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        this.uri = uri;
    }
    
    @JsonProperty("exist")
    public boolean exist() {
        return this.exist;
    }
    
    @JsonProperty("exist")
    public void setExist(boolean exist) {
        this.exist = exist;
    }
    
    @JsonProperty("directory")
    public boolean isDirectory() {
        return this.directory;
    }
    
    @JsonIgnore
    public boolean isFile() {
        return !this.directory;
    }
    
    @JsonProperty("directory")
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
    
    @JsonIgnore
    public void setFile(boolean file) {
        this.directory = !file;
    }
    
    @JsonProperty("file_size")
    public long getFileSize() {
        return this.fileSize;
    }
    
    @JsonProperty("file_size")
    public void setFileSize(long size) {
        if(size < 0) {
            throw new IllegalArgumentException("size is invalid");
        }
        
        this.fileSize = size;
    }
    
    @JsonProperty("last_modified_time")
    public long getLastModifiedTime() {
        return this.lastModifiedTime;
    }
    
    @JsonProperty("last_modified_time")
    public void setLastModifiedTime(long lastModifiedTime) {
        if(lastModifiedTime < 0) {
            throw new IllegalArgumentException("lastModifiedTime cannot be a negative number");
        }
        
        this.lastModifiedTime = lastModifiedTime;
    }
    
    @JsonIgnore
    public boolean isEmpty() {
        if(this.uri == null) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return this.uri.toString();
    }
    
    @JsonIgnore
    public String toJson() throws IOException {
        return JsonSerializer.toJson(this);
    }
    
    @JsonIgnore
    public void saveTo(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        JsonSerializer.toJsonFile(file, this);
    }
}
