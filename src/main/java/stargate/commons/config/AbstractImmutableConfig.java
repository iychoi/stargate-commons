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
package stargate.commons.config;

import stargate.commons.utils.JsonSerializer;
import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author iychoi
 */
public abstract class AbstractImmutableConfig {
    protected boolean immutable = false;
    
    public void setImmutable() {
        this.immutable = true;
    }
    
    public void checkMutableAndRaiseException() {
        if(this.immutable) {
            throw new ImmutableConfigException("configuration is immutable");
        }
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
