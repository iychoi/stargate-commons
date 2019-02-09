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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author iychoi
 */
public class JsonSerializer {

    private static ObjectMapper MAPPER = new ObjectMapper();
    
    public static String toJson(Object obj) throws IOException {
        // obj can be null
        
        StringWriter writer = new StringWriter();
        MAPPER.writeValue(writer, obj);
        return writer.getBuffer().toString();
    }

    public static void toJsonFile(File file, Object obj) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        MAPPER.writeValue(file, obj);
    }

    public static Object fromJson(String json, Class<?> cls) throws IOException {
        if(json == null) {
            return null;
        }
        
        StringReader reader = new StringReader(json);
        return MAPPER.readValue(reader, cls);
    }

    public static Object fromJsonFile(File file, Class<?> cls) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        if(cls == null) {
            return null;
        }
        
        return MAPPER.readValue(file, cls);
    }
    
    public static String formatPretty(String json) throws IOException {
        if(json == null) {
            return null;
        }
        
        StringReader reader = new StringReader(json);
        Object obj = MAPPER.readValue(reader, Object.class);
        return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
}
