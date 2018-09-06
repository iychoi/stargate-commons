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
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class Recipe {
    
    private static final Log LOG = LogFactory.getLog(Recipe.class);
    
    private DataObjectMetadata metadata;
    private String hashAlgorithm;
    private int chunkSize;
    private List<String> nodeNames = new ArrayList<String>();
    private List<RecipeChunk> chunks = new ArrayList<RecipeChunk>();

    public static Recipe createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        JsonSerializer serializer = new JsonSerializer();
        return (Recipe) serializer.fromJsonFile(file, Recipe.class);
    }
    
    public static Recipe createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        JsonSerializer serializer = new JsonSerializer();
        return (Recipe) serializer.fromJson(json, Recipe.class);
    }
    
    Recipe() {
        this.metadata = null;
        this.hashAlgorithm = null;
        this.chunkSize = 0;
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
        
        initialize(metadata, hashAlgorithm, chunkSize, null, null);
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
        
        initialize(metadata, hashAlgorithm, chunkSize, nodeNames, null);
    }
    
    public Recipe(DataObjectMetadata metadata, String hashAlgorithm, int chunkSize, Collection<String> nodeNames, Collection<RecipeChunk> chunk) {
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
        
        if(chunk == null) {
            throw new IllegalArgumentException("chunk is null");
        }
        
        initialize(metadata, hashAlgorithm, chunkSize, nodeNames, chunk);
    }
    
    private void initialize(DataObjectMetadata metadata, String hashAlgorithm, int chunkSize, Collection<String> nodeNames, Collection<RecipeChunk> chunk) {
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
        
        if(nodeNames != null) {
            this.nodeNames.addAll(nodeNames);
        }
        
        if(chunk != null) {
            this.chunks.addAll(chunk);
        }
    }
    
    @JsonProperty("metadata")
    public DataObjectMetadata getMetadata() {
        return this.metadata;
    }
    
    @JsonProperty("metadata")
    public void setMetadata(DataObjectMetadata metadata) {
        this.metadata = metadata;
    }
    
    @JsonProperty("hash_algorithm")
    public String getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    @JsonProperty("hash_algorithm")
    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
    
    @JsonProperty("chunk_size")
    public int getChunkSize() {
        return this.chunkSize;
    }
    
    @JsonProperty("chunk_size")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
    
    @JsonProperty("node_names")
    public Collection<String> getNodeNames() {
        return Collections.unmodifiableCollection(this.nodeNames);
    }
    
    @JsonIgnore
    public String getNodeName(int nodeID) {
        return this.nodeNames.get(nodeID);
    }
    
    @JsonIgnore
    public int getNodeID(String nodeName) {
        return this.nodeNames.indexOf(nodeName);
    }
    
    @JsonProperty("node_names")
    public void addNodeNames(Collection<String> nodeNames) {
        this.nodeNames.addAll(nodeNames);
    }
    
    @JsonIgnore
    public void addNodeName(String nodeName) {
        this.nodeNames.add(nodeName);
    }
    
    @JsonIgnore
    public int addNodeNameAndReturnID(String nodeName) {
        this.nodeNames.add(nodeName);
        return this.nodeNames.size() - 1;
    }
    
    @JsonIgnore
    public void clearNodeNames() {
        this.nodeNames.clear();
    }
    
    @JsonProperty("chunks")
    public Collection<RecipeChunk> getChunks() {
        return Collections.unmodifiableCollection(this.chunks);
    }
    
    @JsonIgnore
    public RecipeChunk getChunk(String hash) {
        for(RecipeChunk chunk : this.chunks) {
           boolean has = chunk.hasHash(hash);
           if(has) {
               return chunk;
           }
        }
        return null;
    }
    
    @JsonIgnore
    public RecipeChunk getChunk(long offset) throws IOException {
        if(this.chunkSize != 0) {
            // fixed-size chunking
            int index = (int) (offset / this.chunkSize);
            RecipeChunk chunk = this.chunks.get(index);
            
            if(chunk.getOffset() <= offset && 
                    chunk.getOffset() + chunk.getLength() > offset) {
                return chunk;
            } else {
                throw new IOException("unable to find a chunk containing offset - " + offset);
            }
        } else {
            // variable-size chunking
            RecipeChunk searchKey = new RecipeChunk();
            searchKey.setOffset(offset);
            searchKey.setLength(0);
            int location = Collections.binarySearch(this.chunks, searchKey, new Comparator<RecipeChunk>(){

                @Override
                public int compare(RecipeChunk t, RecipeChunk t1) {
                    return (int) (t.getOffset() - t1.getOffset());
                }
            });

            if(location >= 0) {
                return this.chunks.get(location);
            } else {
                RecipeChunk chunk = this.chunks.get(Math.abs(location + 1));
                if(chunk.getOffset() <= offset && 
                        chunk.getOffset() + chunk.getLength() > offset) {
                    return chunk;
                } else {
                    throw new IOException("unable to find a chunk containing offset - " + offset);
                }
            }
        }
    }
    
    @JsonProperty("chunks")
    public void addChunks(Collection<RecipeChunk> chunk) {
        this.chunks.addAll(chunk);
    }
    
    @JsonIgnore
    public void addChunk(RecipeChunk chunk) {
        this.chunks.add(chunk);
    }
    
    @JsonIgnore
    public void clearChunks() {
        this.chunks.clear();
    }
    
    @Override
    public String toString() {
        return this.metadata.toString() + ", " + this.hashAlgorithm;
    }
    
    @JsonIgnore
    public String toJson() throws IOException {
        JsonSerializer serializer = new JsonSerializer();
        return serializer.toJson(this);
    }
    
    @JsonIgnore
    public void saveTo(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        JsonSerializer serializer = new JsonSerializer();
        serializer.toJsonFile(file, this);
    }
}
