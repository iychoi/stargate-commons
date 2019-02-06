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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.utils.HexUtils;

/**
 *
 * @author iychoi
 */
public class RecipeChunk {
    
    private static final Log LOG = LogFactory.getLog(RecipeChunk.class);
    
    public static final Integer NODE_ID_ALL_NODES = Recipe.NODE_ID_ALL_NODES;
    
    private long offset;
    private int length;
    private String hash;
    // IDs of nodes that have the chunks
    // we can find names of the nodes from the Recipe instance.
    private List<Integer> nodeIDs = new ArrayList<Integer>();
    
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
    
    public RecipeChunk(RecipeChunk that) {
        this.offset = that.offset;
        this.length = that.length;
        this.hash = that.hash;
        this.nodeIDs.addAll(that.nodeIDs);
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
        if(offset < 0) {
            throw new IllegalArgumentException("offset is invalid");
        }
        
        if(length < 0) {
            throw new IllegalArgumentException("length is invalid");
        }
        
        if(hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("hash is null or empty");
        }
        
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
        this.offset = offset;
    }
    
    @JsonProperty("length")
    public int getLength() {
        return this.length;
    }
    
    @JsonProperty("length")
    public void setLength(int len) {
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
        this.hash = hash.toLowerCase();
    }

    @JsonIgnore
    public boolean hasHash(String hash) {
        if(this.hash == null) {
            return false;
        }
        
        return this.hash.equalsIgnoreCase(hash);
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
        this.nodeIDs.addAll(nodeIDs);
    }
    
    @JsonIgnore
    public void addNodeID(int nodeID) {
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
