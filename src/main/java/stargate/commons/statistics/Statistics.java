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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class Statistics {
    private StatisticsType type;
    private Queue<StatisticsEntry> entries = new LinkedList<StatisticsEntry>();
    private int capacity = Integer.MAX_VALUE; // unlimited
    
    public static Statistics createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (Statistics) JsonSerializer.fromJsonFile(file, Statistics.class);
    }
    
    public static Statistics createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (Statistics) JsonSerializer.fromJson(json, Statistics.class);
    }
    
    Statistics() {
    }

    public Statistics(StatisticsType type, int capacity) {
        if(type == null) {
            throw new IllegalArgumentException("type is null");
        }
        
        if(capacity <= 0) {
            throw new IllegalArgumentException("capacity is negative");
        }
        
        this.type = type;
        this.capacity = capacity;
    }
    
    @JsonProperty("type")
    public synchronized StatisticsType getType() {
        return this.type;
    }
    
    @JsonProperty("type")
    public synchronized void setType(StatisticsType type) {
        if(type == null) {
            throw new IllegalArgumentException("type is null");
        }
        
        this.type = type;
    }
    
    @JsonProperty("capacity")
    public synchronized int getCapacity() {
        return this.capacity;
    }
    
    @JsonProperty("capacity")
    public synchronized void setCapacity(int capacity) {
        if(capacity <= 0) {
            throw new IllegalArgumentException("capacity is negative");
        }
        
        this.capacity = capacity;
        
        compact();
    }
    
    private void compact() {
        if(this.entries.size() > this.capacity) {
            int del = this.entries.size() - this.capacity;
            for(int i=0;i<del;i++) {
                this.entries.poll();
            }
        }
    }
    
    @JsonProperty("entries")
    public synchronized Collection<StatisticsEntry> getEntries() {
        return Collections.unmodifiableCollection(this.entries);
    }
    
    @JsonProperty("entries")
    public synchronized void addEntries(Collection<StatisticsEntry> entries) {
        if(entries == null) {
            throw new IllegalArgumentException("entries is null");
        }
        
        for(StatisticsEntry entry : entries) {
            addEntry(entry);
        }
    }
    
    @JsonIgnore
    public synchronized void addEntry(StatisticsEntry entry) {
        if(entry == null) {
            throw new IllegalArgumentException("entry is null");
        }
        
        this.entries.add(entry);
        compact();
    }
    
    @JsonIgnore
    public synchronized void clear() {
        this.entries.clear();
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
