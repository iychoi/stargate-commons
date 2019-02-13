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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class StargateEvent {
    
    private StargateEventType eventType;
    private Set<String> receiverNodeNames = new HashSet<String>();
    private String senderNodeName;
    private String jsonValue;
    
    public static StargateEvent createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (StargateEvent) JsonSerializer.fromJsonFile(file, StargateEvent.class);
    }
    
    public static StargateEvent createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (StargateEvent) JsonSerializer.fromJson(json, StargateEvent.class);
    }
    
    StargateEvent() {
    }
    
    public StargateEvent(StargateEventType eventType, Collection<String> receiverNodeNames, String senderNodeName, String jsonValue) {
        if(eventType == null) {
            throw new IllegalArgumentException("eventType is null");
        }
        
        if(receiverNodeNames == null) {
            throw new IllegalArgumentException("receiverNodeNames is null");
        }
        
        if(senderNodeName == null || senderNodeName.isEmpty()) {
            throw new IllegalArgumentException("senderNodeName is null or empty");
        }
        
        if(jsonValue == null) {
            throw new IllegalArgumentException("jsonValue is null");
        }
        
        this.eventType = eventType;
        this.receiverNodeNames.addAll(receiverNodeNames);
        this.senderNodeName = senderNodeName;
        this.jsonValue = jsonValue;
    }
    
    public StargateEvent(StargateEventType eventType, String receiverNodeName, String senderNodeName, String jsonValue) {
        if(eventType == null) {
            throw new IllegalArgumentException("eventType is null");
        }
        
        if(receiverNodeName == null || receiverNodeName.isEmpty()) {
            throw new IllegalArgumentException("receiverNodeName is null or empty");
        }
        
        if(senderNodeName == null || senderNodeName.isEmpty()) {
            throw new IllegalArgumentException("senderNodeName is null or empty");
        }
        
        if(jsonValue == null) {
            throw new IllegalArgumentException("jsonValue is null");
        }
        
        this.eventType = eventType;
        this.receiverNodeNames.add(receiverNodeName);
        this.senderNodeName = senderNodeName;
        this.jsonValue = jsonValue;
    }
    
    @JsonProperty("event_type")
    public StargateEventType getEventType() {
        return this.eventType;
    }
    
    @JsonProperty("event_type")
    public void setEventType(StargateEventType eventType) {
        if(eventType == null) {
            throw new IllegalArgumentException("eventType is null");
        }
        
        this.eventType = eventType;
    }
    
    @JsonProperty("receiver_node_names")
    public Collection<String> getReceiverNodeNames() {
        return Collections.unmodifiableCollection(this.receiverNodeNames);
    }
    
    @JsonProperty("receiver_node_names")
    public void addReceiverNodeNames(Collection<String> receiverNodeNames) {
        if(receiverNodeNames == null) {
            throw new IllegalArgumentException("receiverNodeNames is null");
        }
        
        for(String receiverNodeName : receiverNodeNames) {
            addReceiverNodeName(receiverNodeName);
        }
    }
    
    @JsonIgnore
    public void addReceiverNodeName(String receiverNodeName) {
        if(receiverNodeName == null || receiverNodeName.isEmpty()) {
            throw new IllegalArgumentException("receiverNodeName is null or empty");
        }
        
        this.receiverNodeNames.add(receiverNodeName);
    }
    
    @JsonProperty("sender_node_name")
    public String getSenderNodeName() {
        return this.senderNodeName;
    }
    
    @JsonProperty("sender_node_name")
    public void setSenderNodeName(String senderNodeName) {
        if(senderNodeName == null || senderNodeName.isEmpty()) {
            throw new IllegalArgumentException("senderNodeName is null");
        }
        
        this.senderNodeName = senderNodeName;
    }
    
    @JsonProperty("json_value")
    public String getJsonValue() {
        return this.jsonValue;
    }
    
    @JsonProperty("json_value")
    public void setJsonValue(String value) {
        if(value == null) {
            throw new IllegalArgumentException("value is null");
        }
        
        this.jsonValue = value;
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
