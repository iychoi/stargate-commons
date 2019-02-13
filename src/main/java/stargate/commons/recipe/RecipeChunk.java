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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.utils.HexUtils;

/**
 *
 * @author iychoi
 */
public class RecipeChunk {
    
    public static final Integer NODE_ID_ALL_NODES = Recipe.NODE_ID_ALL_NODES;
    
    private long offset;
    private int length;
    private String hash;
    // IDs of nodes that have the chunks
    // we can find names of the nodes from the Recipe instance.
    private Set<Integer> nodeIDs = new HashSet<Integer>();
    
    public static RecipeChunk createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (RecipeChunk) JsonSerializer.fromJsonFile(file, RecipeChunk.class);
    }
    
    public static RecipeChunk createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (RecipeChunk) JsonSerializer.fromJson(json, RecipeChunk.class);
    }
    
    public RecipeChunk() {
    }
    
    public RecipeChunk(RecipeChunk chunk) {
        if(chunk == null) {
            throw new IllegalArgumentException("chunk is null");
        }
        
        initialize(chunk.offset, chunk.length, chunk.hash, chunk.nodeIDs);
    }
    
    public RecipeChunk(long offset, int length, byte[] hash) {
        if(offset < 0) {
            throw new IllegalArgumentException("offset is invalid");
        }
        
        if(length < 0) {
            throw new IllegalArgumentException("length is invalid");
        }
        
        if(hash == null) {
            throw new IllegalArgumentException("hash is null");
        }
        
        String hashString = HexUtils.toHexString(hash);
        initialize(offset, length, hashString, null);
    }
    
    public RecipeChunk(long offset, int length, String hash) {
        if(offset < 0) {
            throw new IllegalArgumentException("offset is invalid");
        }
        
        if(length < 0) {
            throw new IllegalArgumentException("length is invalid");
        }
        
        if(hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("hash is null or empty");
        }
        
        initialize(offset, length, hash, null);
    }
    
    public RecipeChunk(long offset, int length, byte[] hash, Collection<Integer> nodeIDs) {
        if(offset < 0) {
            throw new IllegalArgumentException("offset is invalid");
        }
        
        if(length < 0) {
            throw new IllegalArgumentException("length is invalid");
        }
        
        if(hash == null) {
            throw new IllegalArgumentException("hash is null");
        }
        
        String hashString = HexUtils.toHexString(hash);
        initialize(offset, length, hashString, nodeIDs);
    }
    
    public RecipeChunk(long offset, int length, String hash, Collection<Integer> nodeIDs) {
        if(offset < 0) {
            throw new IllegalArgumentException("offset is invalid");
        }
        
        if(length < 0) {
            throw new IllegalArgumentException("length is invalid");
        }
        
        if(hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("hash is null or empty");
        }
        
        initialize(offset, length, hash, nodeIDs);
    }
    
    private void initialize(long offset, int length, String hash, Collection<Integer> nodeIDs) {
        this.offset = offset;
        this.length = length;
        this.hash = hash.toLowerCase();
        
        if(nodeIDs != null) {
            this.nodeIDs.addAll(nodeIDs);
        }
    }
    
    @JsonProperty("offset")
    public long getOffset() {
        return this.offset;
    }
    
    @JsonProperty("offset")
    public void setOffset(long offset) {
        if(offset < 0) {
            throw new IllegalArgumentException("offset is negative");
        }
        
        this.offset = offset;
    }
    
    @JsonProperty("length")
    public int getLength() {
        return this.length;
    }
    
    @JsonProperty("length")
    public void setLength(int len) {
        if(len < 0) {
            throw new IllegalArgumentException("len is negative");
        }
        
        this.length = len;
    }

    @JsonProperty("hash")
    public String getHash() {
        return this.hash;
    }
    
    @JsonIgnore
    public byte[] getHashBytes() {
        if(this.hash == null) {
            return null;
        }
        return HexUtils.toBytes(this.hash);
    }
    
    @JsonIgnore
    public void setHashBytes(byte[] hash) {
        if(hash == null) {
            this.hash = null;
        } else {
            this.hash = HexUtils.toHexString(hash).toLowerCase();
        }
    }
    
    @JsonProperty("hash")
    public void setHash(String hash) {
        if(hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("hash is null or empty");
        }
        
        this.hash = hash;
    }

    @JsonIgnore
    public boolean hasHash(String hash) {
        if(hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("hash is null or empty");
        }
        
        return this.hash.equals(hash);
    }
    
    @JsonProperty("node_ids")
    public Collection<Integer> getNodeIDs() {
        return Collections.unmodifiableCollection(this.nodeIDs);
    }
    
    @JsonIgnore
    public boolean isAccessibleFromAllNode() {
        return this.nodeIDs.contains(NODE_ID_ALL_NODES);
    }
    
    @JsonProperty("node_ids")
    public void addNodeIDs(Collection<Integer> nodeIDs) {
        if(nodeIDs == null) {
            throw new IllegalArgumentException("nodeIDs is null");
        }
        
        for(int nodeID : nodeIDs) {
            addNodeID(nodeID);
        }
    }
    
    @JsonIgnore
    public void addNodeID(int nodeID) {
        if(nodeID < 0 && nodeID != NODE_ID_ALL_NODES) {
            throw new IllegalArgumentException("nodeID is negative");
        }
        
        this.nodeIDs.add(nodeID);
    }
    
    @JsonIgnore
    public void setAccessibleFromAllNode() {
        this.nodeIDs.clear();
        this.nodeIDs.add(NODE_ID_ALL_NODES);
    }
    
    @JsonIgnore
    public void clearNodeIDs() {
        this.nodeIDs.clear();
    }
    
    @Override
    @JsonIgnore
    public String toString() {
        return this.offset + ", " + this.length + ", " + this.hash;
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
