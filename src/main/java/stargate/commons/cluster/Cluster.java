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
package stargate.commons.cluster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.utils.DateTimeUtils;

/**
 *
 * @author iychoi
 */
public class Cluster {
    
    private static final Log LOG = LogFactory.getLog(Cluster.class);
    
    protected String name;
    private Map<String, Node> nodes = new HashMap<String, Node>();
    protected long lastUpdateTime;
    
    public static Cluster createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        JsonSerializer serializer = new JsonSerializer();
        return (Cluster) serializer.fromJsonFile(file, Cluster.class);
    }
    
    public static Cluster createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        JsonSerializer serializer = new JsonSerializer();
        return (Cluster) serializer.fromJson(json, Cluster.class);
    }
    
    Cluster() {
        this.name = null;
        this.lastUpdateTime = 0;
    }
    
    public Cluster(String name) throws IOException {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        initialize(name, null);
    }
    
    public Cluster(String name, Collection<Node> nodes) throws IOException {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        initialize(name, nodes);
    }
    
    private void initialize(String name, Collection<Node> nodes) {
        this.name = name;
        
        if(nodes != null) {
            Cluster.this.addOrUpdateNodes(nodes);
        }
    }
    
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }
    
    @JsonProperty("name")
    public void setName(String name) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        this.name = name;
    }
    
    @JsonIgnore
    public int getNodeNum() {
        return this.nodes.size();
    }
    
    @JsonProperty("nodes")
    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(this.nodes.values());
    }
    
    @JsonIgnore
    public Collection<String> getNodeNames() {
        List<String> nodeNames = new ArrayList<String>();
        for(Node node : this.nodes.values()) {
            nodeNames.add(node.getName());
        }
        return Collections.unmodifiableCollection(nodeNames);
    }
    
    @JsonIgnore
    public Collection<Node> getLiveNodes() throws IOException {
        List<Node> liveNodes = new ArrayList<Node>();
        for(Node node : this.nodes.values()) {
            NodeStatus status = node.getStatus();
            
            if(!status.isBlacklisted()) {
                liveNodes.add(node);
            }
        }
        
        return Collections.unmodifiableCollection(liveNodes);
    }
    
    @JsonIgnore
    public Node getNode(String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        return (Node) this.nodes.get(nodeName);
    }
    
    @JsonProperty("nodes")
    public void addOrUpdateNodes(Collection<Node> nodes) {
        if(nodes == null) {
            throw new IllegalArgumentException("nodes is null");
        }
        
        for(Node node : nodes) {
            addOrUpdateNode(node, false);
        }
    }
    
    @JsonIgnore
    public void addOrUpdateNodes(Collection<Node> nodes, boolean update) {
        if(nodes == null) {
            throw new IllegalArgumentException("nodes is null");
        }
        
        for(Node node : nodes) {
            addOrUpdateNode(node, update);
        }
    }
    
    @JsonIgnore
    public void addOrUpdateNode(Node node, boolean update) {
        if(node == null) {
            throw new IllegalArgumentException("node is null");
        }
        
        // replace if the given node exists
        // otherwise, add the node
        node.setClusterName(this.name);
        this.nodes.put(node.getName(), node);
        
        if(update) {
            this.lastUpdateTime = DateTimeUtils.getTimestamp();
        }
    }
    
    @JsonIgnore
    public void clearNodes() {
        this.nodes.clear();
    }
    
    @JsonIgnore
    public void clearNodes(boolean update) {
        this.nodes.clear();
        
        if(update) {
            this.lastUpdateTime = DateTimeUtils.getTimestamp();
        }
    }
    
    @JsonIgnore
    public boolean removeNode(Node node) {
        if(node == null) {
            throw new IllegalArgumentException("node is null");
        }
        
        Node vNode = this.nodes.remove(node.getName());
        return vNode != null;
    }
    
    @JsonIgnore
    public boolean removeNode(Node node, boolean update) {
        if(node == null) {
            throw new IllegalArgumentException("node is null");
        }
        
        Node vNode = this.nodes.remove(node.getName());
        
        if(update) {
            this.lastUpdateTime = DateTimeUtils.getTimestamp();
        }
        
        return vNode != null;
    }
    
    @JsonIgnore
    public boolean removeNode(String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        Node vNode = this.nodes.remove(nodeName);
        return vNode != null;
    }
    
    @JsonIgnore
    public boolean removeNode(String nodeName, boolean update) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        Node vNode = this.nodes.remove(nodeName);
        
        if(update) {
            this.lastUpdateTime = DateTimeUtils.getTimestamp();
        }
        
        return vNode != null;
    }
    
    @JsonIgnore
    public boolean hasNode(String nodeName) {
        if(nodeName == null || nodeName.isEmpty()) {
            throw new IllegalArgumentException("nodeName is null or empty");
        }
        
        return this.nodes.containsKey(nodeName);
    }
    
    @JsonProperty("last_update_time")
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }
    
    @JsonProperty("last_update_time")
    public void setLastUpdateTime(long time) {
        this.lastUpdateTime = time;
    }
    
    @JsonIgnore
    @Override
    public String toString() {
        return this.name;
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
