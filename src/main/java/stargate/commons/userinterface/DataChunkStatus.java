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
package stargate.commons.userinterface;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class DataChunkStatus {
    
    private DataChunkSource source;
    private int chunkSize;
    private int partSize;
    private String cacheNodeName;
    private File localCacheFilePath;
    
    public static DataChunkStatus createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (DataChunkStatus) JsonSerializer.fromJsonFile(file, DataChunkStatus.class);
    }
    
    public static DataChunkStatus createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (DataChunkStatus) JsonSerializer.fromJson(json, DataChunkStatus.class);
    }
    
    DataChunkStatus() {
    }
    
    public DataChunkStatus(DataChunkSource source, int chunkSize, int partSize, String cacheNodeName, File localCacheFilePath) {
        if(source == null) {
            throw new IllegalArgumentException("source is null");
        }
        
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is negative");
        }
        
        if(partSize < 0) {
            throw new IllegalArgumentException("partSize is negative");
        }
//        
//        if(cacheNodeName == null || cacheNodeName.isEmpty()) {
//            throw new IllegalArgumentException("cacheNodeName is null or empty");
//        }
//        
//        if(localCacheFilePath == null) {
//            throw new IllegalArgumentException("localCacheFilePath is null");
//        }
        
        this.source = source;
        this.chunkSize = chunkSize;
        this.partSize = partSize;
        this.cacheNodeName = cacheNodeName;
        this.localCacheFilePath = localCacheFilePath;
    }
    
    @JsonProperty("source")
    public DataChunkSource getSource() {
        return this.source;
    }

    @JsonProperty("source")
    public void setSource(DataChunkSource source) {
        if(source == null) {
            throw new IllegalArgumentException("source is null");
        }
        
        this.source = source;
    }
    
    @JsonProperty("chunk_size")
    public int getChunkSize() {
        return this.chunkSize;
    }

    @JsonProperty("chunk_size")
    public void setChunkSize(int chunkSize) {
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is negative");
        }
        
        this.chunkSize = chunkSize;
    }
    
    @JsonProperty("part_size")
    public int getPartSize() {
        return this.partSize;
    }

    @JsonProperty("part_size")
    public void setPartSize(int partSize) {
        if(partSize < 0) {
            throw new IllegalArgumentException("partSize is negative");
        }
        
        this.partSize = partSize;
    }
    
    @JsonProperty("cache_nodename")
    public String getCacheNodeName() {
        return this.cacheNodeName;
    }

    @JsonProperty("cache_nodename")
    public void setCacheNodeName(String cacheNodeName) {
        this.cacheNodeName = cacheNodeName;
    }
    
    @JsonProperty("local_cache_path")
    public File getLocalCachePath() {
        return this.localCacheFilePath;
    }

    @JsonProperty("local_cache_path")
    public void setLocalCachePath(File localCachePath) {
        this.localCacheFilePath = localCachePath;
    }
    
    @Override
    public String toString() {
        return String.format("%s", this.source.toString());
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
