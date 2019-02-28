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
package stargate.commons.manager;

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
import stargate.commons.config.AbstractImmutableConfig;
import stargate.commons.driver.AbstractDriver;
import stargate.commons.driver.DriverFactory;
import stargate.commons.driver.DriverFailedToLoadException;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.driver.DriverInjection;

/**
 *
 * @author iychoi
 */
public class ManagerConfig extends AbstractImmutableConfig {
    
    private List<DriverInjection> driverSettings = new ArrayList<DriverInjection>();
    private Map<String, String> params = new HashMap<String, String>();
    
    public static ManagerConfig createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (ManagerConfig) JsonSerializer.fromJsonFile(file, ManagerConfig.class);
    }
    
    public static ManagerConfig createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (ManagerConfig) JsonSerializer.fromJson(json, ManagerConfig.class);
    }
    
    public ManagerConfig() {
    }
    
    @JsonProperty("driver_setting")
    public void addDriverSettings(Collection<DriverInjection> setting) {
        if(setting == null || setting.isEmpty()) {
            throw new IllegalArgumentException("setting is null or empty");
        }
        
        super.checkMutableAndRaiseException();
        
        this.driverSettings.addAll(setting);
    }
    
    @JsonIgnore
    public void addDriverSetting(DriverInjection setting) {
        if(setting == null) {
            throw new IllegalArgumentException("setting is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.driverSettings.add(setting);
    }
    
    @JsonProperty("driver_setting")
    public Collection<DriverInjection> getDriverSettings() {
        return Collections.unmodifiableCollection(this.driverSettings);
    }
    
    @JsonProperty("params")
    public void addParams(Map<String, String> params) {
        if(params == null || params.isEmpty()) {
            throw new IllegalArgumentException("params is null or empty");
        }
        
        super.checkMutableAndRaiseException();
        
        this.params.putAll(params);
    }
    
    @JsonIgnore
    public void addParam(String key, String value) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty");
        }
        
        if(value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        
        super.checkMutableAndRaiseException();
        
        this.params.put(key, value);
    }
    
    @JsonProperty("params")
    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(this.params);
    }
    
    @JsonIgnore
    public String getParam(String key) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty");
        }
        
        return this.params.get(key);
    }
    
    @JsonIgnore
    public String getParam(String key, String defaultValue) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty");
        }
        
        String val = this.params.get(key);
        if(val == null) {
            return defaultValue;
        }
        return val;
    }
    
    @Override
    public void setImmutable() {
        super.setImmutable();
        
        for(DriverInjection setting : this.driverSettings) {
            setting.setImmutable();
        }
    }
    
    @JsonIgnore
    public Collection<AbstractDriver> getDrivers() throws DriverFailedToLoadException {
        List<AbstractDriver> drivers = new ArrayList<AbstractDriver>();
        for(DriverInjection setting : this.driverSettings) {
            AbstractDriver driver = DriverFactory.createDriver(setting);
            drivers.add(driver);
        }
        return drivers;
    }
}
