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
import java.io.PrintWriter;
import java.io.StringWriter;
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
    private boolean exception;
    
    RestfulResponse() {   
    }
    
    public RestfulResponse(Object response) throws IOException {
        if(response != null) {
            if(response instanceof Exception) {
                Exception ex = (Exception) response;
                this.responseClass = String.class;
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                this.responseJson = JsonSerializer.toJson(sw.toString());
                this.exception = true;
            } else {
                this.responseClass = response.getClass();
                this.responseJson = JsonSerializer.toJson(response);
                this.exception = false;
            }
        } else {
            this.responseClass = null;
            this.responseJson = null;
            this.exception = false;
        }
    }
    
    @JsonProperty("exception")
    public boolean isException() {
        return this.exception;
    }
    
    @JsonProperty("exception")
    public void setException(boolean exception) {
        this.exception = exception;
    }
    
    @JsonIgnore
    public Class getResponseClass() {
        return this.responseClass;
    }
    
    @JsonProperty("response_class")
    public String getResponseClassString() {
        return this.responseClass.getName();
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
        
        this.responseJson = JsonSerializer.toJson(response);
    }
    
    @JsonProperty("response")
    public void setResponseJson(String json) throws IOException {
        // json can be null
        
        this.responseJson = json;
    }
    
    @JsonIgnore
    public Object getResponse() throws IOException {
        if(this.responseJson == null) {
            return null;
        }
        
        return JsonSerializer.fromJson(this.responseJson, this.responseClass);
    }
    
    @JsonProperty("response")
    public String getResponseJson() throws IOException {
        return this.responseJson;
    }
}
