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
package stargate.commons.datasource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class DataExportEntry {
    
    private static final Log LOG = LogFactory.getLog(DataExportEntry.class);
    
    private URI sourceURI;
    private String stargatePath;
    
    public static DataExportEntry createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (DataExportEntry) JsonSerializer.fromJsonFile(file, DataExportEntry.class);
    }
    
    public static DataExportEntry createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (DataExportEntry) JsonSerializer.fromJson(json, DataExportEntry.class);
    }
    
    DataExportEntry() {
        this.sourceURI = null;
        this.stargatePath = null;
    }
    
    public DataExportEntry(URI sourceURI, String stargatePath) {
        if(sourceURI == null) {
            throw new IllegalArgumentException("sourceURI is null");
        }
        
        if(stargatePath == null || stargatePath.isEmpty()) {
            throw new IllegalArgumentException("stargatePath is null or empty");
        }
        
        initialize(sourceURI, stargatePath);
    }
    
    public DataExportEntry(String sourceURI, String stargatePath) throws URISyntaxException {
        if(sourceURI == null || sourceURI.isEmpty()) {
            throw new IllegalArgumentException("sourceURI is null or empty");
        }
        
        if(stargatePath == null || stargatePath.isEmpty()) {
            throw new IllegalArgumentException("stargatePath is null or empty");
        }
        
        initialize(new URI(sourceURI), stargatePath);
    }
    
    private void initialize(URI sourceURI, String stargatePath) {
        if(sourceURI == null) {
            throw new IllegalArgumentException("sourceURI is null");
        }
        
        if(stargatePath == null || stargatePath.isEmpty()) {
            throw new IllegalArgumentException("stargatePath is null or empty");
        }
        
        this.sourceURI = sourceURI;
        this.stargatePath = stargatePath;
    }
    
    @JsonProperty("source_uri")
    public URI getSourceURI() {
        return sourceURI;
    }

    @JsonProperty("source_uri")
    public void setSourceURI(URI sourceURI) {
        if(sourceURI == null) {
            throw new IllegalArgumentException("sourceURI is null");
        }
        
        this.sourceURI = sourceURI;
    }
    
    @JsonIgnore
    public void setSourceURI(String sourceURI) throws URISyntaxException {
        if(sourceURI == null) {
            throw new IllegalArgumentException("sourceURI is null");
        }
        
        this.sourceURI = new URI(sourceURI);
    }
    
    @JsonProperty("stargate_path")
    public String getStargatePath() {
        return this.stargatePath;
    }
    
    @JsonProperty("stargate_path")
    public void setStargatePath(String stargatePath) {
        if(stargatePath == null || stargatePath.isEmpty()) {
            throw new IllegalArgumentException("stargatePath is null or empty");
        }
        
        this.stargatePath = stargatePath;
    }
    
    @JsonIgnore
    public boolean isEmpty() {
        if(this.sourceURI == null || this.sourceURI.getPath().isEmpty()) {
            return true;
        }
        
        if(this.stargatePath == null || this.stargatePath.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s => %s", this.sourceURI.toString(), this.stargatePath);
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
