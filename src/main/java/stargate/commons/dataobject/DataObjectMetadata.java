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
package stargate.commons.dataobject;

import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class DataObjectMetadata {
    
    private static final Log LOG = LogFactory.getLog(DataObjectMetadata.class);
    
    private DataObjectURI uri;
    private long size;
    private boolean directory;
    private long lastModifiedTime;
    
    public static DataObjectMetadata createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        JsonSerializer serializer = new JsonSerializer();
        return (DataObjectMetadata) serializer.fromJsonFile(file, DataObjectMetadata.class);
    }
    
    public static DataObjectMetadata createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        JsonSerializer serializer = new JsonSerializer();
        return (DataObjectMetadata) serializer.fromJson(json, DataObjectMetadata.class);
    }
    
    DataObjectMetadata() {
        this.uri = null;
        this.size = 0;
        this.directory = false;
        this.lastModifiedTime = 0;
    }
    
    public DataObjectMetadata(DataObjectURI uri, long size, long lastModifiedTime) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        if(size < 0) {
            throw new IllegalArgumentException("size is invalid");
        }
        
        if(lastModifiedTime < 0) {
            throw new IllegalArgumentException("lastModifiedTime is invalid");
        }
        
        initialize(uri, size, false, lastModifiedTime);
    }
    
    public DataObjectMetadata(DataObjectURI uri, long size, boolean directory, long lastModifiedTime) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        if(size < 0) {
            throw new IllegalArgumentException("size is invalid");
        }
        
        if(lastModifiedTime < 0) {
            throw new IllegalArgumentException("lastModifiedTime is invalid");
        }
        
        initialize(uri, size, directory, lastModifiedTime);
    }
    
    private void initialize(DataObjectURI uri, long size, boolean directory, long lastModifiedTime) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        if(size < 0) {
            throw new IllegalArgumentException("size is invalid");
        }
        
        if(lastModifiedTime < 0) {
            throw new IllegalArgumentException("lastModifiedTime is invalid");
        }
        
        this.uri = uri;
        this.size = size;
        this.directory = directory;
        this.lastModifiedTime = lastModifiedTime;
    }
    
    @JsonProperty("uri")
    public DataObjectURI getURI() {
        return this.uri;
    }
    
    @JsonProperty("uri")
    public void setURI(DataObjectURI uri) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        this.uri = uri;
    }
    
    @JsonProperty("size")
    public long getSize() {
        return this.size;
    }
    
    @JsonProperty("size")
    public void setSize(long size) {
        if(size < 0) {
            throw new IllegalArgumentException("size is invalid");
        }
        
        this.size = size;
    }
    
    @JsonProperty("directory")
    public boolean isDirectory() {
        return this.directory;
    }
    
    @JsonProperty("directory")
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
    
    @JsonProperty("last_modified_time")
    public long getLastModifiedTime() {
        return this.lastModifiedTime;
    }
    
    @JsonProperty("last_modified_time")
    public void setLastModifiedTime(long lastModifiedTime) {
        if(lastModifiedTime < 0) {
            throw new IllegalArgumentException("lastModifiedTime is invalid");
        }
        
        this.lastModifiedTime = lastModifiedTime;
    }
    
    @Override
    public String toString() {
        return this.uri.toString();
    }
    
    @JsonIgnore
    public synchronized String toJson() throws IOException {
        JsonSerializer serializer = new JsonSerializer();
        return serializer.toJson(this);
    }
    
    @JsonIgnore
    public synchronized void saveTo(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        JsonSerializer serializer = new JsonSerializer();
        serializer.toJsonFile(file, this);
    }
}
