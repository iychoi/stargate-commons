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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.transport.TransportServiceInfo;
import stargate.commons.userinterface.UserInterfaceServiceInfo;
import stargate.commons.utils.IPUtils;

/**
 *
 * @author iychoi
 */
public class Node {
    private static final Log LOG = LogFactory.getLog(Node.class);
    
    private String name;
    private String clusterName;
    private NodeStatus status;
    private TransportServiceInfo transportServiceInfo;
    private UserInterfaceServiceInfo userInterfaceServiceInfo;
    private HashSet<String> hostNames = new HashSet<String>();
    
    public static Node createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (Node) JsonSerializer.fromJsonFile(file, Node.class);
    }
    
    public static Node createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (Node) JsonSerializer.fromJson(json, Node.class);
    }
    
    Node() {
    }
    
    public Node(String name, String clusterName, NodeStatus status, TransportServiceInfo transportServiceInfo, UserInterfaceServiceInfo userInterfaceServiceInfo) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        if(clusterName == null || clusterName.isEmpty()) {
            throw new IllegalArgumentException("clusterName is null or empty");
        }
        
        if(status == null) {
            throw new IllegalArgumentException("status is null");
        }
        
        if(transportServiceInfo == null) {
            throw new IllegalArgumentException("transportServiceInfo is null");
        }
        
        if(userInterfaceServiceInfo == null) {
            throw new IllegalArgumentException("userInterfaceServiceInfo is null");
        }
        
        initialize(name, clusterName, status, transportServiceInfo, userInterfaceServiceInfo, null);
    }
    
    public Node(String name, String clusterName, NodeStatus status, TransportServiceInfo transportServiceInfo, UserInterfaceServiceInfo userInterfaceServiceInfo, Collection<String> hostNames) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        
        if(clusterName == null || clusterName.isEmpty()) {
            throw new IllegalArgumentException("clusterName is null or empty");
        }
        
        if(status == null) {
            throw new IllegalArgumentException("status is null");
        }
        
        if(transportServiceInfo == null) {
            throw new IllegalArgumentException("transportServiceInfo is null");
        }
        
        if(userInterfaceServiceInfo == null) {
            throw new IllegalArgumentException("userInterfaceServiceInfo is null");
        }
        
        initialize(name, clusterName, status, transportServiceInfo, userInterfaceServiceInfo, hostNames);
    }
    
    private void initialize(String name, String clusterName, NodeStatus status, TransportServiceInfo transportServiceInfo, UserInterfaceServiceInfo userInterfaceServiceInfo, Collection<String> hostNames) {
        this.name = name;
        this.clusterName = clusterName;
        this.status = status;
        this.transportServiceInfo = transportServiceInfo;
        this.userInterfaceServiceInfo = userInterfaceServiceInfo;
        
        if(hostNames != null) {
            addHostNames(hostNames);
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
    
    @JsonProperty("cluster_name")
    public String getClusterName() {
        return this.clusterName;
    }
    
    @JsonProperty("cluster_name")
    public void setClusterName(String clusterName) {
        if(clusterName == null || clusterName.isEmpty()) {
            throw new IllegalArgumentException("clusterName is null or empty");
        }
        
        this.clusterName = clusterName;
    }
    
    @JsonProperty("status")
    public NodeStatus getStatus() {
        return this.status;
    }
    
    @JsonProperty("status")
    public void setStatus(NodeStatus status) {
        if(status == null) {
            throw new IllegalArgumentException("status is null");
        }
        
        this.status = status;
    }

    @JsonProperty("transport_service_info")
    public TransportServiceInfo getTransportServiceInfo() {
        return this.transportServiceInfo;
    }

    @JsonProperty("transport_service_info")
    public void setTransportServiceInfo(TransportServiceInfo transportServiceInfo) {
        if(transportServiceInfo == null) {
            throw new IllegalArgumentException("transportServiceInfo is null");
        }
        
        this.transportServiceInfo = transportServiceInfo;
    }
    
    @JsonProperty("userinterface_service_info")
    public UserInterfaceServiceInfo getUserInterfaceServiceInfo() {
        return this.userInterfaceServiceInfo;
    }

    @JsonProperty("userinterface_service_info")
    public void setUserInterfaceServiceInfo(UserInterfaceServiceInfo userInterfaceServiceInfo) {
        if(userInterfaceServiceInfo == null) {
            throw new IllegalArgumentException("userInterfaceServiceInfo is null");
        }
        
        this.userInterfaceServiceInfo = userInterfaceServiceInfo;
    }
    
    @JsonProperty("hostnames")
    public Collection<String> getHostnames() {
        return Collections.unmodifiableSet(this.hostNames);
    }
    
    @JsonProperty("hostnames")
    public void addHostNames(Collection<String> hostNames) {
        if(hostNames == null) {
            throw new IllegalArgumentException("hostNames is null");
        }
        
        for(String hostName : hostNames) {
            addHostName(hostName);
        }
    }
    
    @JsonIgnore
    public void addHostName(String hostName) {
        if(hostName == null || hostName.isEmpty()) {
            throw new IllegalArgumentException("hostName is null or empty");
        }
        
        this.hostNames.add(hostName);
    }
    
    @JsonIgnore
    public boolean hasHostName(String hostName) {
        return this.hostNames.contains(hostName);
    }
    
    @JsonIgnore
    public boolean isLocal() {
        try {
            Collection<String> localHostNames = IPUtils.getHostNames();

            for(String localHostName : localHostNames) {
                if(this.hostNames.contains(localHostName)) {
                    return true;
                }
            }
            return false;
        } catch (IOException ex) {
            LOG.error("IOException", ex);
            return false;
        }
    }
    
    @JsonIgnore
    public long getLastUpdateTime() {
        return this.status.getLastUpdateTime();
    }
    
    @Override
    @JsonIgnore
    public String toString() {
        return this.name + "(" + this.transportServiceInfo.toString() + ")";
    }
    
    @Override
    @JsonIgnore
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.name);
        hash = 73 * hash + Objects.hashCode(this.clusterName);
        return hash;
    }

    @Override
    @JsonIgnore
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.clusterName, other.clusterName)) {
            return false;
        }
        return true;
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