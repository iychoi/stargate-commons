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
package stargate.commons.recipe;

import stargate.commons.dataobject.DataObjectMetadata;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class Recipe {
    
    public static final Integer NODE_ID_ALL_NODES = -1;
    
    private DataObjectMetadata metadata;
    private String hashAlgorithm;
    private int chunkSize;
    private List<String> nodeNames = new ArrayList<String>();
    private List<RecipeChunk> chunks = new ArrayList<RecipeChunk>(); // for order
    private Map<String, Integer> chunkHashes = new HashMap<String, Integer>(); // for hash-chunk idx conversion
    private RecipeChunkComparator comparator = new RecipeChunkComparator();

    public static Recipe createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (Recipe) JsonSerializer.fromJsonFile(file, Recipe.class);
    }
    
    public static Recipe createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (Recipe) JsonSerializer.fromJson(json, Recipe.class);
    }
    
    Recipe() {
    }
    
    public Recipe(DataObjectMetadata metadata, String hashAlgorithm, int chunkSize) {
        if(metadata == null) {
            throw new IllegalArgumentException("metadata is null");
        }
        
        if(hashAlgorithm == null || hashAlgorithm.isEmpty()) {
            throw new IllegalArgumentException("hashAlgorithm is null or empty");
        }
        
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is invalid");
        }
        
        this.metadata = metadata;
        this.hashAlgorithm = hashAlgorithm;
        this.chunkSize = chunkSize;
    }
    
    public Recipe(DataObjectMetadata metadata, String hashAlgorithm, int chunkSize, Collection<String> nodeNames) {
        if(metadata == null) {
            throw new IllegalArgumentException("metadata is null");
        }
        
        if(hashAlgorithm == null || hashAlgorithm.isEmpty()) {
            throw new IllegalArgumentException("hashAlgorithm is null or empty");
        }
        
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is invalid");
        }
        
        if(nodeNames == null) {
            throw new IllegalArgumentException("nodeNames is null");
        }
        
        this.metadata = metadata;
        this.hashAlgorithm = hashAlgorithm;
        this.chunkSize = chunkSize;
        
        if(nodeNames != null) {
            this.nodeNames.addAll(nodeNames);
        }
    }
    
    public Recipe(DataObjectMetadata metadata, String hashAlgorithm, int chunkSize, Collection<String> nodeNames, Collection<RecipeChunk> chunks) {
        if(metadata == null) {
            throw new IllegalArgumentException("metadata is null");
        }
        
        if(hashAlgorithm == null || hashAlgorithm.isEmpty()) {
            throw new IllegalArgumentException("hashAlgorithm is null or empty");
        }
        
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is invalid");
        }
        
        if(nodeNames == null) {
            throw new IllegalArgumentException("nodeNames is null");
        }
        
        if(chunks == null) {
            throw new IllegalArgumentException("chunks is null");
        }
        
        this.metadata = metadata;
        this.hashAlgorithm = hashAlgorithm;
        this.chunkSize = chunkSize;
        
        if(nodeNames != null) {
            this.nodeNames.addAll(nodeNames);
        }
        
        if(chunks != null) {
            for(RecipeChunk chunk : chunks) {
                this.chunks.add(chunk);
                this.chunkHashes.put(chunk.getHash(), this.chunks.size() - 1);
            }
        }
    }
    
    @JsonProperty("metadata")
    public synchronized DataObjectMetadata getMetadata() {
        return this.metadata;
    }
    
    @JsonProperty("metadata")
    public synchronized void setMetadata(DataObjectMetadata metadata) {
        if(metadata == null) {
            throw new IllegalArgumentException("metadata is null");
        }
        
        this.metadata = metadata;
    }
    
    @JsonProperty("hash_algorithm")
    public synchronized String getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    @JsonProperty("hash_algorithm")
    public synchronized void setHashAlgorithm(String hashAlgorithm) {
        if(hashAlgorithm == null || hashAlgorithm.isEmpty()) {
            throw new IllegalArgumentException("hashAlgorithm is null");
        }
        
        this.hashAlgorithm = hashAlgorithm;
    }
    
    @JsonProperty("chunk_size")
    public synchronized int getChunkSize() {
        return this.chunkSize;
    }
    
    @JsonProperty("chunk_size")
    public synchronized void setChunkSize(int chunkSize) {
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is negative");
        }
        
        this.chunkSize = chunkSize;
    }
    
    @JsonIgnore
    public synchronized int getEffectiveChunkSize(RecipeChunk chunk) {
        if(chunk == null) {
            throw new IllegalArgumentException("chunk is null");
        }
        
        return Math.min(this.chunkSize, chunk.getLength());
    }
    
    @JsonProperty("node_names")
    public synchronized Collection<String> getNodeNames() {
        return Collections.unmodifiableCollection(this.nodeNames);
    }
    
    @JsonIgnore
    public synchronized String getNodeName(int nodeID) {
        return this.nodeNames.get(nodeID);
    }
    
    @JsonIgnore
    public synchronized Collection<String> getNodeNames(Collection<Integer> nodeIDs) throws IOException {
        if(nodeIDs == null || nodeIDs.isEmpty()) {
            throw new IllegalArgumentException("nodeIDs is null or empty");
        }
        
        List<String> names = new ArrayList<String>();
        for(int id : nodeIDs) {
            if(id == RecipeChunk.NODE_ID_ALL_NODES) {
                names.clear();
                names.addAll(this.nodeNames);
                break;
            } else {
                String name = this.nodeNames.get(id);
                if(name == null || name.isEmpty()) {
                    throw new IOException(String.format("Cannot convert node id (%d) to name", id));
                }
                names.add(name);
            }
        }
        return Collections.unmodifiableCollection(names);
    }
    
    @JsonIgnore
    public synchronized int getNodeID(String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        return this.nodeNames.indexOf(nodeName);
    }
    
    @JsonProperty("node_names")
    public synchronized void addNodeNames(Collection<String> nodeNames) {
        if(nodeNames == null) {
            throw new IllegalArgumentException("nodeNames is null");
        }
        
        this.nodeNames.addAll(nodeNames);
    }
    
    @JsonIgnore
    public synchronized void addNodeName(String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        this.nodeNames.add(nodeName);
    }
    
    @JsonIgnore
    public synchronized int addNodeNameAndReturnID(String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        this.nodeNames.add(nodeName);
        return this.nodeNames.size() - 1;
    }
    
    @JsonIgnore
    public synchronized void clearNodeNames() {
        this.nodeNames.clear();
    }
    
    @JsonProperty("chunks")
    public synchronized Collection<RecipeChunk> getChunks() {
        return Collections.unmodifiableCollection(this.chunks);
    }
    
    @JsonIgnore
    public synchronized RecipeChunk getChunk(String hash) {
        if(hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("hash is null or empty");
        }
        
        Integer idx = this.chunkHashes.get(hash);
        if(idx == null) {
            return null;
        }
        return this.chunks.get(idx);
    }
    
    @JsonIgnore
    public synchronized RecipeChunk getChunk(long offset) throws IOException {
        if(offset < 0) {
            throw new IllegalArgumentException("offset is negative");
        }
        
        if(this.chunkSize != 0) {
            int idx = (int) (offset / this.chunkSize);
            if(this.chunks.size() > idx) {
                RecipeChunk chunk = this.chunks.get(idx);
                if(chunk.getOffset() <= offset &&
                    chunk.getOffset() + chunk.getLength() > offset) {
                    return chunk;
                }
            }
        }
            
        // if chunks are not in order, so we could not find the chunk, 
        // iterate through all chunks
        for(RecipeChunk chunk : this.chunks) {
            if(chunk.getOffset() <= offset &&
                    chunk.getOffset() + chunk.getLength() > offset) {
                return chunk;
            }
        }
        
        throw new IOException(String.format("Cound not find a chunk for an offset : %d", offset));
    }
    
    @JsonProperty("chunks")
    public synchronized void addChunks(Collection<RecipeChunk> chunks) {
        if(chunks == null) {
            throw new IllegalArgumentException("chunks is null");
        }
        
        for(RecipeChunk chunk : chunks) {
            this.chunks.add(chunk);
            this.chunkHashes.put(chunk.getHash(), this.chunks.size() - 1);
        }
        
        this.chunks.sort(this.comparator);
    }
    
    @JsonIgnore
    public synchronized void addChunk(RecipeChunk chunk) {
        if(chunk == null) {
            throw new IllegalArgumentException("chunk is null");
        }
        
        this.chunks.add(chunk);
        this.chunkHashes.put(chunk.getHash(), this.chunks.size() - 1);
        
        this.chunks.sort(this.comparator);
    }
    
    @JsonIgnore
    public synchronized void clearChunks() {
        this.chunks.clear();
        this.chunkHashes.clear();
    }
    
    @Override
    @JsonIgnore
    public synchronized String toString() {
        return this.metadata.toString() + ", " + this.hashAlgorithm;
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
}
