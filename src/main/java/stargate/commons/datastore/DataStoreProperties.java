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
package stargate.commons.datastore;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author iychoi
 */
public class DataStoreProperties extends Properties {
    public static final String DATASTORE_PROPERTY_SHARDED = "sharded";
    public static final String DEFAULT_DATASTORE_PROPERTY_SHARDED_VALUE = "false";
    
    public static final String DATASTORE_PROPERTY_PERSISTENT = "persistent";
    public static final String DEFAULT_DATASTORE_PROPERTY_PERSISTENT_VALUE = "false";
    
    public static final String DATASTORE_PROPERTY_NUM_REPLICAS = "num_replicas";
    public static final String DEFAULT_DATASTORE_PROPERTY_NUM_REPLICAS_VALUE = "0";
    
    public static final String DATASTORE_PROPERTY_EXPIRABLE = "expirable";
    public static final String DEFAULT_DATASTORE_PROPERTY_EXPIRABLE_VALUE = "false";
    
    public static final String DATASTORE_PROPERTY_EXPIRE_TIMEUNIT = "expire_timeunit";
    public static final String DEFAULT_DATASTORE_PROPERTY_EXPIRE_TIMEUNIT_VALUE = null;
    
    public static final String DATASTORE_PROPERTY_EXPIRE_TIMEVAL = "expire_timeval";
    public static final String DEFAULT_DATASTORE_PROPERTY_EXPIRE_TIMEVAL_VALUE = "0";
    
    public boolean isSharded() {
        String property = this.getProperty(DATASTORE_PROPERTY_SHARDED, DEFAULT_DATASTORE_PROPERTY_SHARDED_VALUE);
        return Boolean.parseBoolean(property);
    }
    
    public void setSharded(boolean sharded) {
        this.setProperty(DATASTORE_PROPERTY_SHARDED, Boolean.toString(sharded));
    }
    
    public boolean isReplicated() {
        return !this.isSharded();
    }
    
    public void setReplicated(boolean replicated) {
        this.setSharded(!replicated);
    }
    
    public boolean isPersistent() {
        String property = this.getProperty(DATASTORE_PROPERTY_PERSISTENT, DEFAULT_DATASTORE_PROPERTY_PERSISTENT_VALUE);
        return Boolean.parseBoolean(property);
    }
    
    public void setPersistent(boolean persistent) {
        this.setProperty(DATASTORE_PROPERTY_PERSISTENT, Boolean.toString(persistent));
    }
    
    public int getReplicaNum() {
        if(this.isSharded()) {
            String property = this.getProperty(DATASTORE_PROPERTY_NUM_REPLICAS, DEFAULT_DATASTORE_PROPERTY_NUM_REPLICAS_VALUE);
            return Math.max(0, Integer.parseInt(property));
        }
        return 0;
    }
    
    public void setReplicaNum(int replica) {
        this.setProperty(DATASTORE_PROPERTY_NUM_REPLICAS, Integer.toString(replica));
    }
    
    public boolean isExpirable() {
        String property = this.getProperty(DATASTORE_PROPERTY_EXPIRABLE, DEFAULT_DATASTORE_PROPERTY_EXPIRABLE_VALUE);
        return Boolean.parseBoolean(property);
    }
    
    public void setExpirable(boolean expirable) {
        this.setProperty(DATASTORE_PROPERTY_EXPIRABLE, Boolean.toString(expirable));
    }
    
    public TimeUnit getExpireTimeUnit() {
        if(this.isExpirable()) {
            String property = this.getProperty(DATASTORE_PROPERTY_EXPIRE_TIMEUNIT, null);
            if(property == null) {
                return null;
            }
            return TimeUnit.valueOf(property);
        }
        return null;
    }
    
    public void setExpireTimeUnit(TimeUnit timeunit) {
        this.setProperty(DATASTORE_PROPERTY_EXPIRE_TIMEUNIT, timeunit.name());
    }
    
    public long getExpireTimeVal() {
        if(this.isExpirable()) {
            String property = this.getProperty(DATASTORE_PROPERTY_EXPIRE_TIMEVAL, DEFAULT_DATASTORE_PROPERTY_EXPIRE_TIMEVAL_VALUE);
            return Math.max(0, Long.parseLong(property));
        }
        return 0;
    }
    
    public void setExpireTimeVal(long timeval) {
        this.setProperty(DATASTORE_PROPERTY_EXPIRE_TIMEVAL, Long.toString(timeval));
    }
}
