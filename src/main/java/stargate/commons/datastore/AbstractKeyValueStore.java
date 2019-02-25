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
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author iychoi
 */
public abstract class AbstractKeyValueStore {
    public abstract String getName();
    public abstract Class getValueClass();
    public abstract EnumDataStoreProperty getProperty();
    
    public abstract int size();
    public abstract boolean isEmpty();
    public abstract boolean containsKey(String key);
    
    public abstract Object get(String key) throws IOException;
    public abstract void put(String key, Object value) throws IOException;
    public abstract boolean putIfAbsent(String key, Object value) throws IOException;
    public abstract boolean replace(String key, Object oldValue, Object newValue) throws IOException;
    public abstract void remove(String key) throws IOException;
    public abstract Collection<String> keys() throws IOException;
    public abstract String getNodeForData(String key) throws IOException;
    
    public abstract void clear() throws IOException;
    public abstract Map<String, Object> toMap() throws IOException;
    
    public abstract void addLayoutEventHandler(AbstractDataStoreLayoutEventHandler eventHandler);
    public abstract void removeLayoutEventHandler(AbstractDataStoreLayoutEventHandler eventHandler);
}
