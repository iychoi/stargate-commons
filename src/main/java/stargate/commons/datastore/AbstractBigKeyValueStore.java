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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 *
 * @author iychoi
 */
public abstract class AbstractBigKeyValueStore {
    public abstract String getName();
    public abstract DataStoreProperties getProperties();
    
    public abstract boolean containsKey(String key);
    
    public abstract BigKeyValueStoreMetadata getMetadata(String key) throws IOException;
    public abstract InputStream getData(String key) throws IOException;
    public abstract void warmData(String key) throws IOException;
    public abstract void warmData(String key, BigKeyValueStoreMetadata metadata) throws IOException;
    public abstract InputStream getDataPart(String key, int partNo) throws IOException;
    public abstract void put(String key, InputStream dataIS, long size, byte[] extra) throws IOException;
    public abstract boolean putIfAbsent(String key, InputStream dataIS, long size, byte[] extra) throws IOException;
    public abstract boolean replace(String key, BigKeyValueStoreMetadata oldMetadata, BigKeyValueStoreMetadata newMetadata) throws IOException;
    
    public abstract void remove(String key) throws IOException;
    
    public abstract String getPrimaryNodeForData(String key) throws IOException;
    public abstract Collection<String> getBackupNodesForData(String key) throws IOException;
    public abstract Collection<String> getPrimaryAndBackupNodesForData(String key) throws IOException;
    
    public abstract void clear() throws IOException;
}
