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
package stargate.commons.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author iychoi
 */
public class ConfigSerializer {
    
    public ConfigSerializer() {
    }
    
    public Map<String, Object> toMap(Object obj) throws IOException {
        Map kv = new HashMap<String, Object>();
        
        Class<?> clazz = obj.getClass();
        for(Field field : clazz.getFields()) {
            if(field.isAnnotationPresent(KVField.class)) {
                KVField annotation = field.getAnnotation(KVField.class);
                String key = annotation.key();
                if(key == null || key.isEmpty()) {
                    key = field.getName();
                }
                
                try {
                    Object value = field.get(obj);
                    kv.put(key, value);
                } catch(IllegalArgumentException ex) {
                    throw new IOException(ex);
                } catch (IllegalAccessException ex) {
                    throw new IOException(ex);
                }
            }
        }
        
        return kv;
    }
    
    public Object fromMap(Map<String, Object> map, Class<?> clazz) throws IOException {
        if(map == null) {
            return null;
        }
        
        try {
            Object obj = ClassUtils.getClassInstance(clazz);
            for(Field field : clazz.getFields()) {
                if(field.isAnnotationPresent(KVField.class)) {
                    KVField annotation = field.getAnnotation(KVField.class);
                    String key = annotation.key();
                    if(key == null || key.isEmpty()) {
                        key = field.getName();
                    }

                    if(map.containsKey(key)) {
                        Object value = map.get(key);
                        field.set(obj, value);
                    }
                }
            }
            
            return obj;
        } catch (InstantiationException ex) {
            throw new IOException(ex);
        } catch (IllegalAccessException ex) {
            throw new IOException(ex);
        }
    }
}
