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
package stargate.commons.transport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.dataobject.DataObjectURI;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class TransferAssignment {

    private DataObjectURI uri;
    private String hash;
    private long offset;
    private String transferNode;
    private List<String> accessNodes = new ArrayList<String>();
    
    public static TransferAssignment createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (TransferAssignment) JsonSerializer.fromJsonFile(file, TransferAssignment.class);
    }
    
    public static TransferAssignment createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (TransferAssignment) JsonSerializer.fromJson(json, TransferAssignment.class);
    }
    
    TransferAssignment() {
    }
    
    public TransferAssignment(DataObjectURI uri, String hash, long offset, String transferNode, Collection<String> accessNodes) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        if(hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("hash is null or empty");
        }
        
        if(offset < 0) {
            throw new IllegalArgumentException("offset is negative");
        }
        
        if(transferNode == null || transferNode.isEmpty()) {
            throw new IllegalArgumentException("transferNode is null or empty");
        }
        
        if(accessNodes == null) {
            throw new IllegalArgumentException("accessNodes is null");
        }
        
        this.uri = uri;
        this.hash = hash.toLowerCase();
        this.offset = offset;
        this.transferNode = transferNode;
        this.accessNodes.addAll(accessNodes);
    }
    
    @JsonProperty("uri")
    public DataObjectURI getDataObjectURI() {
        return uri;
    }
    
    @JsonProperty("uri")
    public void setDataObjectURI(DataObjectURI uri) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        this.uri = uri;
    }

    @JsonProperty("hash")
    public String getHash() {
        if(this.hash == null) {
            return null;
        }
        return this.hash.toLowerCase();
    }
    
    @JsonProperty("hash")
    public void setHash(String hash) {
        if(hash == null) {
            this.hash = null;
        } else {
            this.hash = hash.toLowerCase();
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
    
    @JsonProperty("transfer_node")
    public String getTransferNode() {
        return this.transferNode;
    }
    
    @JsonProperty("transfer_node")
    public void setTransferNode(String transferNode) {
        if(transferNode == null || transferNode.isEmpty()) {
            throw new IllegalArgumentException("transferNode is null or empty");
        }
        
        this.transferNode = transferNode;
    }
    
    @JsonProperty("access_nodes")
    public Collection<String> getAccessNodes() {
        return Collections.unmodifiableCollection(this.accessNodes);
    }
    
    @JsonProperty("access_nodes")
    public void addAccessNodes(Collection<String> accessNodes) {
        if(accessNodes == null) {
            throw new IllegalArgumentException("accessNodes is null");
        }
        
        for(String accessNode : accessNodes) {
            addAccessNode(accessNode);
        }
    }
    
    @JsonIgnore
    public void addAccessNode(String accessNode) {
        if(accessNode == null || accessNode.isEmpty()) {
            throw new IllegalArgumentException("accessNode is null or empty");
        }
        
        this.accessNodes.add(accessNode);
    }
    
    
    @Override
    @JsonIgnore
    public String toString() {
        return "TransferAssignment{" + "uri=" + uri + ", hash=" + hash + ", offset=" + offset + ", transferNode=" + transferNode + "}";
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
