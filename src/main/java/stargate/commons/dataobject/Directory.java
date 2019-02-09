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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class Directory {
    
    private DataObjectURI uri;
    private long lastModifiedTime;
    private Set<DataObjectMetadata> entries = new HashSet<DataObjectMetadata>();
    
    public static final long DIRECTORY_METADATA_SIZE = 4*1024;
    
    public static Directory createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (Directory) JsonSerializer.fromJsonFile(file, Directory.class);
    }
    
    public static Directory createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (Directory) JsonSerializer.fromJson(json, Directory.class);
    }
    
    Directory() {
    }
    
    public Directory(DataObjectURI uri, long lastModifiedTime) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        if(lastModifiedTime < 0) {
            throw new IllegalArgumentException("lastModifiedTime is invalid");
        }
        
        this.uri = uri;
        this.lastModifiedTime = lastModifiedTime;
    }
    
    public Directory(DataObjectURI uri, long lastModifiedTime, Collection<DataObjectMetadata> entries) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        if(lastModifiedTime < 0) {
            throw new IllegalArgumentException("lastModifiedTime is invalid");
        }
        
        this.uri = uri;
        this.lastModifiedTime = lastModifiedTime;
        
        if(entries != null) {
            this.entries.addAll(entries);
        }
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
    
    @JsonProperty("entries")
    public Collection<DataObjectMetadata> getEntries() {
        return Collections.unmodifiableCollection(this.entries);
    }
    
    @JsonIgnore
    public DataObjectMetadata getEntry(DataObjectURI uri) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        for(DataObjectMetadata metadata : this.entries) {
            if(metadata.getURI().equals(uri)) {
                return metadata;
            }
        }
        return null;
    }
    
    @JsonProperty("entries")
    public void addEntries(Collection<DataObjectMetadata> entries) {
        if(entries == null) {
            throw new IllegalArgumentException("entries is null");
        }
        
        this.entries.addAll(entries);
    }
    
    @JsonIgnore
    public void addEntry(DataObjectMetadata entry) {
        if(entry == null) {
            throw new IllegalArgumentException("entry is null or empty");
        }
        
        this.entries.add(entry);
    }
    
    @JsonIgnore
    public void remoteEntry(DataObjectMetadata entry) {
        if(entry == null) {
            throw new IllegalArgumentException("entry is null or empty");
        }
        
        this.entries.remove(entry);
    }
    
    @Override
    public String toString() {
        return this.uri.toString();
    }
    
    @JsonIgnore
    public synchronized String toJson() throws IOException {
        return JsonSerializer.toJson(this);
    }
    
    @JsonIgnore
    public synchronized void saveTo(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        JsonSerializer.toJsonFile(file, this);
    }
    
    @JsonIgnore
    public DataObjectMetadata toDataObjectMetadata() {
        return new DataObjectMetadata(this.uri, DIRECTORY_METADATA_SIZE, true, this.lastModifiedTime);
    }
}
