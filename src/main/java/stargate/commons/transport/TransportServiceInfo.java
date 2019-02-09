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
import java.net.URI;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.utils.ClassUtils;

/**
 *
 * @author iychoi
 */
public class TransportServiceInfo {
    private String driverClass;
    private URI serviceUri;
    
    public static TransportServiceInfo createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (TransportServiceInfo) JsonSerializer.fromJsonFile(file, TransportServiceInfo.class);
    }
    
    public static TransportServiceInfo createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (TransportServiceInfo) JsonSerializer.fromJson(json, TransportServiceInfo.class);
    }
    
    TransportServiceInfo() {
    }
    
    public TransportServiceInfo(TransportServiceInfo serviceInfo) {
        if(serviceInfo == null) {
            throw new IllegalArgumentException("serviceInfo is null");
        }
        
        this.driverClass = serviceInfo.driverClass;
        this.serviceUri = serviceInfo.serviceUri;
    }
    
    public TransportServiceInfo(String driverClass, URI connectionUri) {
        if(driverClass == null || driverClass.isEmpty()) {
            throw new IllegalArgumentException("driverClass is null or empty");
        }
        
        if(connectionUri == null) {
            throw new IllegalArgumentException("connectionUri is null");
        }
        
        this.driverClass = driverClass;
        this.serviceUri = connectionUri;
    }
    
    @JsonProperty("driver_class")
    public void setDriverClass(String clazz) {
        if(clazz == null || clazz.isEmpty()) {
            throw new IllegalArgumentException("clazz is null or empty");
        }
        
        this.driverClass = clazz;
    }
    
    @JsonIgnore
    public void setDriverClass(Class clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        this.driverClass = clazz.getName();
    }
    
    @JsonProperty("driver_class")
    public String getDriverClassString() {
        return this.driverClass;
    }
    
    @JsonIgnore
    public Class getDriverClass() throws ClassNotFoundException {
        return ClassUtils.findClass(this.driverClass);
    }
    
    @JsonProperty("service_uri")
    public URI getServiceURI() {
        return this.serviceUri;
    }
    
    @JsonProperty("service_uri")
    public void setServiceURI(URI serviceURI) {
        if(serviceURI == null) {
            throw new IllegalArgumentException("serviceURI is null");
        }
        
        this.serviceUri = serviceURI;
    }
    
    @JsonIgnore
    @Override
    public synchronized String toString() {
        return this.driverClass + "\t" + this.serviceUri.toASCIIString();
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
