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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import stargate.commons.driver.AbstractDriver;
import stargate.commons.service.AbstractService;

/**
 *
 * @author iychoi
 */
public abstract class AbstractManager<D extends AbstractDriver> {
    
    private static final Log LOG = LogFactory.getLog(AbstractManager.class);
    
    protected AbstractService service;
    protected List<D> drivers = new ArrayList<D>();
    protected boolean started = false;
    
    public void setService(AbstractService service) {
        this.service = service;
    }
    
    public AbstractService getService() {
        return this.service;
    }
    
    public boolean needDriver() {
        return true;
    }
    
    public Collection<D> getDrivers() {
        return Collections.unmodifiableCollection(this.drivers);
    }
    
    public D getDriver(String driverName) {
        if(driverName == null || driverName.isEmpty()) {
            throw new IllegalArgumentException("driverName is null or empty");
        }
        
        for(D driver : this.drivers) {
            if(driverName.equals(driver.getName())) {
                return driver;
            }
        }
        return null;
    }
    
    public synchronized void start() throws IOException {
        if(this.started) {
            throw new IllegalStateException(getName() + " is already started");
        }
        
        LOG.info(String.format("Manager %s is starting...", getName()));
        
        for(D driver : this.drivers) {
            driver.setManager(this);
            driver.init();
        }
        
        this.started = true;
    }
    
    public synchronized void stop() throws IOException {
        if(!this.started) {
            throw new IllegalStateException(getName() + " is already stopped");
        }
        
        for(D driver : this.drivers) {
            driver.uninit();
            driver.setManager(null);
        }
        
        LOG.info(String.format("Manager %s is stopped...", getName()));
        
        this.started = false;
    }
    
    public boolean isStarted() {
        return this.started;
    }
    
    public String getName() {
        return this.getClass().getName();
    }
}
