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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.ClassUtils;

/**
 *
 * @author iychoi
 */
public class RestfulResponse<T> {
    private T response;
    private Class exceptionClass;
    private RestfulException exception;
    
    public RestfulResponse() {
        
    }
    
    public RestfulResponse(T response) {
        this.response = response;
    }
    
    public RestfulResponse(Exception ex) {
        if(ex == null) {
            throw new IllegalArgumentException("ex is null");
        }
        
        this.exceptionClass = ex.getClass();
        this.exception = new RestfulException(ex);
    }
    
    @JsonProperty("response")
    public void setResponse(T response) {
        this.response = response;
    }
    
    @JsonProperty("response")
    public T getResponse() {
        return this.response;
    }
    
    @JsonProperty("exception_class")
    public void setExceptionClass(String clazz) throws ClassNotFoundException {
        if(clazz == null || clazz.isEmpty()) {
            this.exceptionClass = null;
            return;
        }
        
        this.exceptionClass = ClassUtils.findClass(clazz);
    }
    
    @JsonIgnore
    public void setExceptionClass(Class clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        this.exceptionClass = clazz;
    }
    
    @JsonProperty("exception_class")
    public String getExceptionClassString() {
        if(this.exceptionClass == null) {
            return null;
        }
        return this.exceptionClass.getCanonicalName();
    }
    
    @JsonIgnore
    public Class getExceptionClass() {
        return this.exceptionClass;
    }
    
    @JsonProperty("exception")
    public void setException(RestfulException ex) {
        this.exception = ex;
    }
    
    @JsonProperty("exception")
    public Exception getException() {
        return this.exception;
    }
}
