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
package stargate.commons.userinterface;

import java.io.File;
import java.io.IOException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class DataChunkStatus {
    
    private DataChunkSourceType sourceType;
    
    public static DataChunkStatus createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (DataChunkStatus) JsonSerializer.fromJsonFile(file, DataChunkStatus.class);
    }
    
    public static DataChunkStatus createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (DataChunkStatus) JsonSerializer.fromJson(json, DataChunkStatus.class);
    }
    
    DataChunkStatus() {
    }
    
    public DataChunkStatus(DataChunkSourceType sourceType) {
        if(sourceType == null) {
            throw new IllegalArgumentException("sourceType is null");
        }
        
        this.sourceType = sourceType;
    }
    
    @JsonProperty("source_type")
    public DataChunkSourceType getSourceType() {
        return sourceType;
    }

    @JsonProperty("source_type")
    public void setSourceURI(DataChunkSourceType sourceType) {
        if(sourceType == null) {
            throw new IllegalArgumentException("sourceType is null");
        }
        
        this.sourceType = sourceType;
    }
    
    @Override
    public String toString() {
        return String.format("%s", this.sourceType.toString());
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
