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
package stargate.commons.event;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class BulkStargateEvent {
    
    private List<StargateEvent> events = new ArrayList<StargateEvent>();
    
    public static BulkStargateEvent createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (BulkStargateEvent) JsonSerializer.fromJsonFile(file, BulkStargateEvent.class);
    }
    
    public static BulkStargateEvent createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (BulkStargateEvent) JsonSerializer.fromJson(json, BulkStargateEvent.class);
    }
    
    BulkStargateEvent() {
    }
    
    public BulkStargateEvent(Collection<StargateEvent> events) {
        if(events == null) {
            throw new IllegalArgumentException("events is null");
        }
        
        this.events.addAll(events);
    }
    
    @JsonProperty("events")
    public Collection<StargateEvent> getEvents() {
        return Collections.unmodifiableCollection(this.events);
    }
    
    @JsonProperty("events")
    public void addEvents(Collection<StargateEvent> events) {
        if(events == null) {
            throw new IllegalArgumentException("events is null");
        }
        
        this.events.addAll(events);
    }
    
    @JsonIgnore
    public void addEvent(StargateEvent event) {
        if(event == null) {
            throw new IllegalArgumentException("event is null");
        }
        
        this.events.add(event);
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
