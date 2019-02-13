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
    private String transferNode;
    
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
    
    TransferAssignment(DataObjectURI uri, String hash, String transferNode) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        if(hash == null || hash.isEmpty()) {
            throw new IllegalArgumentException("hash is null or empty");
        }
        
        if(transferNode == null || transferNode.isEmpty()) {
            throw new IllegalArgumentException("transferNode is null or empty");
        }
        
        this.uri = uri;
        this.hash = hash.toLowerCase();
        this.transferNode = transferNode;
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
    
    @Override
    @JsonIgnore
    public String toString() {
        return "TransferAssignment{" + "uri=" + uri + ", hash=" + hash + ", transferNode=" + transferNode + '}';
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
