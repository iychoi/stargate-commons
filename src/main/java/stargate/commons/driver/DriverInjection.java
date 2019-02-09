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
package stargate.commons.driver;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.config.AbstractImmutableConfig;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.utils.ClassUtils;

/**
 *
 * @author iychoi
 */
public class DriverInjection extends AbstractImmutableConfig {
    
    private Class driverClass;
    private AbstractDriverConfig driverConfig;
    
    public static DriverInjection createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (DriverInjection) JsonSerializer.fromJsonFile(file, DriverInjection.class);
    }
    
    public static DriverInjection createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (DriverInjection) JsonSerializer.fromJson(json, DriverInjection.class);
    }
    
    public DriverInjection() {
    }
    
    @JsonProperty("driver_class")
    public String getDriverClassString() {
        return this.driverClass.getName();
    }
    
    @JsonIgnore
    public Class getDriverClass() {
        return this.driverClass;
    }
    
    @JsonProperty("driver_class")
    public void setDriverClass(String clazz) throws ClassNotFoundException {
        if(clazz == null || clazz.isEmpty()) {
            throw new IllegalArgumentException("clazz is null or empty");
        }
        
        super.checkMutableAndRaiseException();
        
        this.driverClass = ClassUtils.findClass(clazz);
    }
    
    @JsonIgnore
    public void setDriverClass(Class clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.driverClass = clazz;
    }
    
    @JsonProperty("driver_config")
    public AbstractDriverConfig getDriverConfig() {
        return this.driverConfig;
    }
    
    @JsonProperty("driver_config")
    public void setDriverConfig(AbstractDriverConfig driverConfig) {
        if(driverConfig == null) {
            throw new IllegalArgumentException("driverConfig is null");
        }
        
        super.checkMutableAndRaiseException();
        
        this.driverConfig = driverConfig;
    }
    
    @Override
    public void setImmutable() {
        super.setImmutable();
        
        this.driverConfig.setImmutable();
    }
}
