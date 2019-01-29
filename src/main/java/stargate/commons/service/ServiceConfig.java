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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.config.AbstractImmutableConfig;
import stargate.commons.manager.ManagerConfig;
import stargate.commons.utils.JsonSerializer;


/**
 *
 * @author iychoi
 */
public class ServiceConfig extends AbstractImmutableConfig {
    
    private static final Log LOG = LogFactory.getLog(ServiceConfig.class);
    
    protected ManagerConfig clusterManagerConfig;
    protected ManagerConfig dataSourceManagerConfig;
    protected ManagerConfig dataStoreManagerConfig;
    protected ManagerConfig recipeManagerConfig;
    protected ManagerConfig transportManagerConfig;
    protected ManagerConfig userInterfaceManagerConfig;
    protected ManagerConfig scheduleManagerConfig;
    
    public static ServiceConfig createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (ServiceConfig) JsonSerializer.fromJsonFile(file, ServiceConfig.class);
    }
    
    public static ServiceConfig createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (ServiceConfig) JsonSerializer.fromJson(json, ServiceConfig.class);
    }
    
    public ServiceConfig() {
        
    }
    
    @Override
    public void setImmutable() {
        super.setImmutable();
        
        if(this.clusterManagerConfig != null) {
            this.clusterManagerConfig.setImmutable();
        }
        
        if(this.dataSourceManagerConfig != null) {
            this.dataSourceManagerConfig.setImmutable();
        }
        
        if(this.dataStoreManagerConfig != null) {
            this.dataStoreManagerConfig.setImmutable();
        }
    
        if(this.recipeManagerConfig != null) {
            this.recipeManagerConfig.setImmutable();
        }
        
        if(this.transportManagerConfig != null) {
            this.transportManagerConfig.setImmutable();
        }
        
        if(this.userInterfaceManagerConfig != null) {
            this.userInterfaceManagerConfig.setImmutable();
        }
        
        if(this.scheduleManagerConfig != null) {
            this.scheduleManagerConfig.setImmutable();
        }
    }
    
    @JsonProperty("cluster")
    public void setClusterConfig(ManagerConfig clusterConfig) {
        if(clusterConfig == null) {
            throw new IllegalArgumentException("clusterConfig is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.clusterManagerConfig = clusterConfig;
    }
    
    @JsonProperty("cluster")
    public ManagerConfig getClusterConfig() {
        return this.clusterManagerConfig;
    }
    
    @JsonProperty("data_source")
    public void setDataSourceConfig(ManagerConfig dataSourceConfig) {
        if(dataSourceConfig == null) {
            throw new IllegalArgumentException("dataSourceConfig is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.dataSourceManagerConfig = dataSourceConfig;
    }
    
    @JsonProperty("data_source")
    public ManagerConfig getDataSourceConfig() {
        return this.dataSourceManagerConfig;
    }
    
    @JsonProperty("data_store")
    public void setDataStoreConfig(ManagerConfig dataStoreConfig) {
        if(dataStoreConfig == null) {
            throw new IllegalArgumentException("dataStoreConfig is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.dataStoreManagerConfig = dataStoreConfig;
    }
    
    @JsonProperty("data_store")
    public ManagerConfig getDataStoreConfig() {
        return this.dataStoreManagerConfig;
    }
    
    @JsonProperty("recipe")
    public void setRecipeConfig(ManagerConfig recipeConfig) {
        if(recipeConfig == null) {
            throw new IllegalArgumentException("recipeConfig is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.recipeManagerConfig = recipeConfig;
    }
    
    @JsonProperty("recipe")
    public ManagerConfig getRecipeConfig() {
        return this.recipeManagerConfig;
    }
    
    @JsonProperty("transport")
    public void setTransportConfig(ManagerConfig transportConfig) {
        if(transportConfig == null) {
            throw new IllegalArgumentException("transportConfig is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.transportManagerConfig = transportConfig;
    }
    
    @JsonProperty("transport")
    public ManagerConfig getTransportConfig() {
        return this.transportManagerConfig;
    }
    
    @JsonProperty("user_interface")
    public void setUserInterfaceConfig(ManagerConfig userInterfaceConfig) {
        if(userInterfaceConfig == null) {
            throw new IllegalArgumentException("userInterfaceConfig is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.userInterfaceManagerConfig = userInterfaceConfig;
    }
    
    @JsonProperty("schedule")
    public ManagerConfig getScheduleConfig() {
        return this.scheduleManagerConfig;
    }
    
    @JsonProperty("schedule")
    public void setScheduleConfig(ManagerConfig scheduleConfig) {
        if(scheduleConfig == null) {
            throw new IllegalArgumentException("scheduleConfig is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.scheduleManagerConfig = scheduleConfig;
    }
    
    @JsonProperty("user_interface")
    public ManagerConfig getUserInterfaceConfig() {
        return this.userInterfaceManagerConfig;
    }
}
