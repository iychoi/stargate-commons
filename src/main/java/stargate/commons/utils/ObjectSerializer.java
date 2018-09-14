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

/**
 *
 * @author iychoi
 */
public class ObjectSerializer {
    
    public static String toString(Object obj) throws IOException {
        if(obj == null) {
            throw new IllegalArgumentException("obj is null");
        }

        if(obj.getClass() == String.class) {
            return (String) obj;
        }

        return JsonSerializer.toJson(obj);
    }
    
    public static Object fromString(String str, Class clazz) throws IOException {
        if(str == null || str.isEmpty()) {
            throw new IllegalArgumentException("str is null or empty");
        }
        
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        // primitive types
        if(clazz == String.class) {
            return str;
        } else if(clazz.isPrimitive()) {
            return JsonSerializer.fromJson(str, clazz);
        }
        
        // cast
        try {
            Object instance = ClassUtils.invokeCreateInstance(clazz, str);
            return instance;
        } catch (NoSuchMethodException ex) {
            // failed
            return JsonSerializer.fromJson(str, clazz);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }
    
    public static byte[] toByteArray(Object obj) throws IOException {
        if(obj == null) {
            throw new IllegalArgumentException("obj is null");
        }

        if(obj.getClass() == byte[].class) {
            byte[] valueBytes = (byte[]) obj;
            return valueBytes;
        } else if(obj.getClass() == String.class) {
            String str = (String) obj;
            return str.getBytes();
        } else {
            // other complex classes
            String json = JsonSerializer.toJson(obj);
            return json.getBytes();
        }
    }
    
    public static Object fromByteArray(byte[] byteArr, Class clazz) throws IOException {
        if(byteArr == null) {
            throw new IllegalArgumentException("byteArr is null or empty");
        }
        
        if(clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        
        // primitive types
        if(clazz == byte[].class) {
            return byteArr;
        } else if(clazz == String.class) {
            return new String(byteArr);
        } else if(clazz.isPrimitive()) {
            String json = new String(byteArr);
            return JsonSerializer.fromJson(json, clazz);
        }
        
        // cast
        String json = new String(byteArr);
        try {
            Object instance = ClassUtils.invokeCreateInstance(clazz, json);
            return instance;
        } catch (NoSuchMethodException ex) {
            // failed
            return JsonSerializer.fromJson(json, clazz);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }
}
