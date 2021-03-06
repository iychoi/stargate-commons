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
package stargate.commons.service;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;


/**
 *
 * @author iychoi
 */
public class FSServiceInfo {
    
    private int chunkSize;
    private String hashAlgorithm;
    private int partSize;
    
    public static FSServiceInfo createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (FSServiceInfo) JsonSerializer.fromJsonFile(file, FSServiceInfo.class);
    }
    
    public static FSServiceInfo createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (FSServiceInfo) JsonSerializer.fromJson(json, FSServiceInfo.class);
    }
    
    FSServiceInfo() {
    }
    
    public FSServiceInfo(int chunkSize, String hashAlgorithm, int partSize) {
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is negative");
        }
        
        if(hashAlgorithm == null || hashAlgorithm.isEmpty()) {
            throw new IllegalArgumentException("hashAlgorithm is null or empty");
        }
        
        if(partSize < 0) {
            throw new IllegalArgumentException("partSize is negative");
        }
        
        this.chunkSize = chunkSize;
        this.hashAlgorithm = hashAlgorithm;
        this.partSize = partSize;
    }

    @JsonProperty("chunk_size")
    public int getChunkSize() {
        return this.chunkSize;
    }
    
    @JsonProperty("chunk_size")
    public void setChunkSize(int chunkSize) {
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize must not be negative");
        }
        
        this.chunkSize = chunkSize;
    }
    
    @JsonProperty("hash_algorithm")
    public String getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    @JsonProperty("hash_algorithm")
    public void setHashAlgorithm(String hashAlgorithm) {
        if(hashAlgorithm == null || hashAlgorithm.isEmpty()) {
            throw new IllegalArgumentException("hashAlgorithm is null or empty");
        }
        
        this.hashAlgorithm = hashAlgorithm;
    }
    
    @JsonProperty("part_size")
    public int getPartSize() {
        return this.partSize;
    }
    
    @JsonProperty("part_size")
    public void setPartSize(int partSize) {
        if(partSize < 0) {
            throw new IllegalArgumentException("partSize must not be negative");
        }
        
        this.partSize = partSize;
    }
    
    @Override
    @JsonIgnore
    public String toString() {
        return this.hashAlgorithm + "/" + this.chunkSize + "/" + this.partSize;
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
