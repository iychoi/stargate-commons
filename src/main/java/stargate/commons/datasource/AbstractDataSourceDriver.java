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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import stargate.commons.cluster.Cluster;
import stargate.commons.driver.AbstractDriver;

/**
 *
 * @author iychoi
 */
public abstract class AbstractDataSourceDriver extends AbstractDriver {
    
    public abstract String getScheme();
    
    public abstract SourceFileMetadata getMetadata(URI uri) throws IOException, FileNotFoundException;
    
    public boolean exist(URI uri) throws IOException {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        try {
            SourceFileMetadata metadata = getMetadata(uri);
            return metadata.exist();
        } catch(FileNotFoundException ex) {
            return false;
        }
    }
    
    public boolean isDirectory(URI uri) throws IOException, FileNotFoundException {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        SourceFileMetadata metadata = getMetadata(uri);
        return metadata.isDirectory();
    }
    
    public boolean isFile(URI uri) throws IOException, FileNotFoundException {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        SourceFileMetadata metadata = getMetadata(uri);
        return metadata.isFile();
    }
    
    public long getFileSize(URI uri) throws IOException, FileNotFoundException {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        SourceFileMetadata metadata = getMetadata(uri);
        return metadata.getFileSize();
    }
    public long getLastModifiedTime(URI uri) throws IOException, FileNotFoundException {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        SourceFileMetadata metadata = getMetadata(uri);
        return metadata.getLastModifiedTime();
    }
    
    public abstract Collection<URI> listDirectory(URI uri) throws IOException, FileNotFoundException;
    public abstract Collection<SourceFileMetadata> listDirectoryWithMetadata(URI uri) throws IOException, FileNotFoundException;
    
    public abstract InputStream openFile(URI uri) throws IOException, FileNotFoundException;
    public abstract InputStream openFile(URI uri, long offset, int size) throws IOException, FileNotFoundException;
    
    // returns node names
    public abstract Collection<String> listBlockLocations(Cluster cluster, URI uri, long offset, int size) throws IOException, FileNotFoundException;
}
