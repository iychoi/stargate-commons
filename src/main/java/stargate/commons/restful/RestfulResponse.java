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
package stargate.commons.restful;

import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.ClassUtils;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class RestfulResponse {
    private Class responseClass;
    private String responseJson;
    private Class exceptionClass;
    private Exception exception;
    
    public RestfulResponse() {   
    }
    
    public RestfulResponse(Object response) throws IOException {
        if(response != null) {
            if(response.getClass() != String.class) {
                this.responseClass = response.getClass();
                JsonSerializer serializer = new JsonSerializer();
                this.responseJson = serializer.toJson(response);
            } else {
                this.responseClass = response.getClass();
                this.responseJson = (String) response;
            }
        } else {
            this.responseClass = null;
            this.responseJson = null;
        }
        
        this.exception = null;
        this.exceptionClass = null;
    }
    
    public RestfulResponse(Exception ex) {
        if(ex == null) {
            throw new IllegalArgumentException("ex is null");
        }
        
        this.exception = ex;
        this.exceptionClass = ex.getClass();
    }
    
    @JsonIgnore
    public boolean hasResponse() {
        return this.responseClass != null;
    }
    
    @JsonIgnore
    public boolean hasException() {
        return this.exceptionClass != null;
    }
    
    @JsonIgnore
    public Class getResponseClass() {
        return this.responseClass;
    }
    
    @JsonProperty("response_class")
    public String getResponseClassString() {
        return this.responseClass.getCanonicalName();
    }
    
    @JsonIgnore
    public void setResponseClass(Class clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        this.responseClass = clazz;
    }
    
    @JsonProperty("response_class")
    public void setResponseClass(String clazz) throws ClassNotFoundException {
        if(clazz == null || clazz.isEmpty()) {
            throw new IllegalArgumentException("clazz is null or empty");
        }
        
        this.responseClass = ClassUtils.findClass(clazz);
    }
    
    @JsonIgnore
    public void setResponse(Object response) throws IOException {
        if(response == null) {
            this.responseJson = null;
        }
        
        if(response.getClass() != String.class) {
            JsonSerializer serializer = new JsonSerializer();
            this.responseJson = serializer.toJson(response);
        } else {
            this.responseJson = (String) response;
        }
    }
    
    @JsonProperty("response")
    public void setResponseJson(String json) throws IOException {
        this.responseJson = json;
    }
    
    @JsonIgnore
    public Object getResponse() throws IOException {
        if(this.responseJson == null) {
            return null;
        }
        
        if(this.responseClass == String.class) {
            return this.responseJson;
        } else {
            JsonSerializer serializer = new JsonSerializer();
            return serializer.fromJson(this.responseJson, this.responseClass);
        }
    }
    
    @JsonProperty("response")
    public String getResponseJson() throws IOException {
        return this.responseJson;
    }
    
    @JsonIgnore
    public Class getExceptionClass() {
        return this.exceptionClass;
    }
    
    @JsonProperty("exception_class")
    public String getExceptionClassString() {
        if(this.exceptionClass == null) {
            return null;
        }
        return this.exceptionClass.getCanonicalName();
    }
    
    @JsonIgnore
    public void setExceptionClass(Class clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        this.exceptionClass = clazz;
    }
    
    @JsonProperty("exception_class")
    public void setExceptionClass(String clazz) throws ClassNotFoundException {
        if(clazz == null || clazz.isEmpty()) {
            this.exceptionClass = null;
            return;
        }
        
        this.exceptionClass = ClassUtils.findClass(clazz);
    }
    
    @JsonProperty("exception")
    public void setException(Exception ex) {
        this.exception = ex;
    }
    
    @JsonProperty("exception")
    public Exception getException() {
        return this.exception;
    }
}
