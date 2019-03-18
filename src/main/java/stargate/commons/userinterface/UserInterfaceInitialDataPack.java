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
import stargate.commons.cluster.Cluster;
import stargate.commons.dataobject.DataObjectMetadata;
import stargate.commons.service.FSServiceInfo;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class UserInterfaceInitialDataPack {
    private boolean live;
    private Cluster localCluster;
    private FSServiceInfo fsServiceInfo;
    private DataObjectMetadata rootDataObjectMetadata;
    
    public static UserInterfaceInitialDataPack createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (UserInterfaceInitialDataPack) JsonSerializer.fromJsonFile(file, UserInterfaceInitialDataPack.class);
    }
    
    public static UserInterfaceInitialDataPack createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (UserInterfaceInitialDataPack) JsonSerializer.fromJson(json, UserInterfaceInitialDataPack.class);
    }
    
    UserInterfaceInitialDataPack() {
    }
    
    public UserInterfaceInitialDataPack(boolean live, Cluster localCluster, FSServiceInfo fsServiceInfo, DataObjectMetadata rootDataObjectMetadata) {
        if(localCluster == null) {
            throw new IllegalArgumentException("localCluster is null");
        }
        
        if(localCluster == null) {
            throw new IllegalArgumentException("localCluster is null");
        }
        
        if(fsServiceInfo == null) {
            throw new IllegalArgumentException("fsServiceInfo is null");
        }
        
        if(rootDataObjectMetadata == null) {
            throw new IllegalArgumentException("rootDataObjectMetadata is null");
        }
        
        this.live = live;
        this.localCluster = localCluster;
        this.fsServiceInfo = fsServiceInfo;
        this.rootDataObjectMetadata = rootDataObjectMetadata;
    }
    
    @JsonProperty("live")
    public void setLive(boolean live) {
        this.live = live;
    }
    
    @JsonProperty("live")
    public boolean getLive() {
        return this.live;
    }
    
    @JsonProperty("local_cluster")
    public Cluster getLocalCluster() {
        return this.localCluster;
    }
    
    @JsonProperty("local_cluster")
    public void setLocalCluster(Cluster localCluster) {
        if(localCluster == null) {
            throw new IllegalArgumentException("localCluster is null");
        }
        
        this.localCluster = localCluster;
    }
    
    @JsonProperty("fs_service_info")
    public FSServiceInfo getFSServiceInfo() {
        return this.fsServiceInfo;
    }
    
    @JsonProperty("fs_service_info")
    public void setFSServiceInfo(FSServiceInfo fsServiceInfo) {
        if(fsServiceInfo == null) {
            throw new IllegalArgumentException("fsServiceInfo is null");
        }
        
        this.fsServiceInfo = fsServiceInfo;
    }
    
    @JsonProperty("root_dataobj_metadata")
    public DataObjectMetadata getRootDataObjectMetadata() {
        return this.rootDataObjectMetadata;
    }
    
    @JsonProperty("root_dataobj_metadata")
    public void setRootDataObjectMetadata(DataObjectMetadata rootDataObjectMetadata) {
        if(rootDataObjectMetadata == null) {
            throw new IllegalArgumentException("rootDataObjectMetadata is null");
        }
        
        this.rootDataObjectMetadata = rootDataObjectMetadata;
    }
    
    @JsonIgnore
    @Override
    public synchronized String toString() {
        return this.localCluster.getName();
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
