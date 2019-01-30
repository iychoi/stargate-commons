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

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import stargate.commons.manager.AbstractManager;

/**
 *
 * @author iychoi
 */
public abstract class AbstractDriver {
    
    private static final Log LOG = LogFactory.getLog(AbstractDriver.class);
    
    protected AbstractManager manager;
    protected boolean initialized = false;
    
    public void setManager(AbstractManager manager) {
        this.manager = manager;
    }
    
    public AbstractManager getManager() {
        return this.manager;
    }
    
    public void init() throws IOException {
        if(this.initialized) {
            throw new IllegalStateException(getName() + " is already initialized");
        }
        
        LOG.debug(String.format("Driver %s is initializing...", getName()));
        
        this.initialized = true;
    }
    
    public void uninit() throws IOException {
        if(!this.initialized) {
            throw new IllegalStateException(getName() + " is already uninitialized");
        }
        
        this.initialized = false;
        
        LOG.debug(String.format("Driver %s is uninitialized...", getName()));
    }
    
    public boolean isStarted() {
        return this.initialized;
    }
    
    public String getName() {
        return this.getClass().getName();
    }
}
