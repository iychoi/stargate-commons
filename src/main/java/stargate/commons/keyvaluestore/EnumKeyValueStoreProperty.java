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
package stargate.commons.keyvaluestore;

/**
 *
 * @author iychoi
 */
public enum EnumKeyValueStoreProperty {
    KEY_VALUE_STORE_PROP_VOLATILE_REPLICATED,
    KEY_VALUE_STORE_PROP_VOLATILE_DISTRIBUTED,
    KEY_VALUE_STORE_PROP_PERSISTENT_REPLICATED,
    KEY_VALUE_STORE_PROP_PERSISTENT_DISTRIBUTED;
    
    public static boolean isReplciated(EnumKeyValueStoreProperty property) {
        return (property == KEY_VALUE_STORE_PROP_VOLATILE_REPLICATED || property == KEY_VALUE_STORE_PROP_PERSISTENT_REPLICATED);
    }
    
    public static boolean isDistributed(EnumKeyValueStoreProperty property) {
        return (property == KEY_VALUE_STORE_PROP_VOLATILE_DISTRIBUTED || property == KEY_VALUE_STORE_PROP_PERSISTENT_DISTRIBUTED);
    }
    
    public static boolean isVolatile(EnumKeyValueStoreProperty property) {
        return (property == KEY_VALUE_STORE_PROP_VOLATILE_REPLICATED || property == KEY_VALUE_STORE_PROP_VOLATILE_DISTRIBUTED);
    }
    
    public static boolean isPersistent(EnumKeyValueStoreProperty property) {
        return (property == KEY_VALUE_STORE_PROP_PERSISTENT_REPLICATED || property == KEY_VALUE_STORE_PROP_PERSISTENT_DISTRIBUTED);
    }
}
