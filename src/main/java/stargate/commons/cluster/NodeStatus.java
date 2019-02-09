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
package stargate.commons.cluster;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;
import stargate.commons.utils.DateTimeUtils;

/**
 *
 * @author iychoi
 */
public class NodeStatus {
    private int failureCount;
    private long lastFailureTime;
    
    private boolean blacklisted;
    private long blacklistedTime;
        
    public static NodeStatus createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        return (NodeStatus) JsonSerializer.fromJsonFile(file, NodeStatus.class);
    }
    
    public static NodeStatus createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (NodeStatus) JsonSerializer.fromJson(json, NodeStatus.class);
    }
    
    public NodeStatus() {
    }
    
    @JsonProperty("failures")
    public int getFailureCount() {
        return this.failureCount;
    }
    
    @JsonProperty("failures")
    public void setFailureCount(int count) {
        if(count < 0) {
            throw new IllegalArgumentException("count cannot be a negative number");
        }
        
        this.failureCount = count;
    }
    
    @JsonProperty("last_failure_time")
    public long getLastFailureTime() {
        return this.lastFailureTime;
    }
    
    @JsonProperty("last_failure_time")
    public void setLastFailureTime(long time) {
        if(time < 0) {
            throw new IllegalArgumentException("time cannot be a negative number");
        }
        
        this.lastFailureTime = time;
    }
    
    @JsonIgnore
    public void increaseFailureCount() {
        this.failureCount++;
    }
    
    @JsonIgnore
    public void increaseFailureCount(boolean updateTime) {
        this.failureCount++;
        
        if(updateTime) {
            this.lastFailureTime = DateTimeUtils.getTimestamp();
        }
    }
    
    @JsonProperty("blacklisted")
    public boolean isBlacklisted() {
        return this.blacklisted;
    }
    
    @JsonProperty("blacklisted")
    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }
    
    @JsonIgnore
    public void setBlacklisted(boolean blacklisted, boolean updateTime) {
        setBlacklisted(blacklisted);
        
        if(updateTime) {
            this.blacklistedTime = DateTimeUtils.getTimestamp();
        }
    }
    
    @JsonProperty("blacklisted_time")
    public long getBlacklistedTime() {
        return this.blacklistedTime;
    }
    
    @JsonProperty("blacklisted_time")
    public void setBlacklistedTime(long time) {
        if(time < 0) {
            throw new IllegalArgumentException("time cannot be a negative number");
        }
        
        this.blacklistedTime = time;
    }
    
    @JsonIgnore
    public long getLastUpdateTime() {
        long lastUpdateTime = this.blacklistedTime;
        
        if(this.lastFailureTime > lastUpdateTime) {
            lastUpdateTime = this.lastFailureTime;
        }
        
        return lastUpdateTime;
    }
    
    @JsonIgnore
    public String toJson() throws IOException {
        return JsonSerializer.toJson(this);
    }
    
    @JsonIgnore
    public void saveTo(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        JsonSerializer.toJsonFile(file, this);
    }
}
