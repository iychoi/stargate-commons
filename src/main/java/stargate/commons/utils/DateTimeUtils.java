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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author iychoi
 */
public class DateTimeUtils {
    
    //public static ZoneId GMT_ZONEID = ZoneId.of("GMT");
    public static ZoneId LOCAL_ZONEID = ZoneId.systemDefault();
    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static long getTimestamp() {
        return Instant.now().toEpochMilli();
    }
    
    public static boolean timeElapsedSec(long prev_time, long cur_time, long expiration_time) {
        return timeElapsed(prev_time, cur_time, expiration_time * 1000);
    }
    
    public static boolean timeElapsed(long prev_time, long cur_time, long expiration_time) {
        if(prev_time < 0) {
            throw new IllegalArgumentException("prev_time is negative");
        }
        
        if(cur_time < 0) {
            throw new IllegalArgumentException("cur_time is negative");
        }
        
        if(expiration_time < 0) {
            throw new IllegalArgumentException("expiration_time is negative");
        }
        
        if(cur_time - prev_time >= expiration_time) {
            return true;
        }
        return false;
    }
    
    public static LocalDateTime getDateTime(long timestamp) {
        if(timestamp < 0) {
            throw new IllegalArgumentException("timestamp is negative");
        }
        
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), LOCAL_ZONEID);
    }
    
    public static String getDateTimeString(long timestamp) {
        if(timestamp < 0) {
            throw new IllegalArgumentException("timestamp is negative");
        }
        
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), LOCAL_ZONEID);
        return time.format(FORMATTER);
    }
    
    public static long getMilliseconds(TimeUnit timeunit, long value) {
        if(timeunit == null) {
            throw new IllegalArgumentException("timeunit is null");
        }
        
        if(value < 0) {
            throw new IllegalArgumentException("value is negative");
        }
        
        return timeunit.toMillis(value);
    }
}
