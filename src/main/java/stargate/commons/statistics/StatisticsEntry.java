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
package stargate.commons.statistics;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class StatisticsEntry {
    private long timestamp;
    private String value;
    
    public static StatisticsEntry createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (StatisticsEntry) JsonSerializer.fromJsonFile(file, StatisticsEntry.class);
    }
    
    public static StatisticsEntry createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (StatisticsEntry) JsonSerializer.fromJson(json, StatisticsEntry.class);
    }
    
    StatisticsEntry() {
    }
    
    public StatisticsEntry(String value, long timestamp) {
        if(value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        
        if(timestamp < 0) {
            throw new IllegalArgumentException("timestamp is negative");
        }
        
        this.value = value;
        this.timestamp = timestamp;
    }
    
    @JsonProperty("value")
    public String getValue() {
        return this.value;
    }
    
    @JsonProperty("value")
    public void setValue(String value) {
        if(value == null || value.isEmpty()) {
            throw new IllegalArgumentException("value is null or empty");
        }
        
        this.value = value;
    }
    
    @JsonProperty("timestamp")
    public long getTimestamp() {
        return this.timestamp;
    }
    
    @JsonProperty("timestamp")
    public void setTimestamp(long timestamp) {
        if(timestamp < 0) {
            throw new IllegalArgumentException("timestamp is negative");
        }
        
        this.timestamp = timestamp;
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
