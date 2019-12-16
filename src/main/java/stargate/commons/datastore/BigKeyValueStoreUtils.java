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

/**
 *
 * @author iychoi
 */
public class BigKeyValueStoreUtils {
    public static String makePartkey(String key, int part) {
        return key + ":" + part;
    }
    
    public static String getPartitionKey(String partkey) {
        int index = partkey.lastIndexOf(":");
        if(index > 0) {
            // ignore parts after ':'
            return partkey.substring(0, index);
        } else {
            return partkey;
        }
    }
    
    public static boolean isPartKey(String key) {
        int index = key.indexOf(":");
        if(index > 0) {
            return true;
        }
        return false;
    }
    
    public static long getPartStartOffset(int partSize, int partNo) {
        return (long)partSize * partNo;
    }

    public static int getPartSize(long chunkSize, int partSize, int partNo) {
        long partStartOffset = (long)partSize * partNo;
        
        return (int) Math.min(chunkSize - partStartOffset, partSize);
    }

    public static int getPartNum(long size, int partSize) {
        int parts = (int) (size / partSize);
        if(size % partSize != 0) {
            parts++;
        }
        return parts;
    }
    
    public static int getPartNo(long offset, int partSize) {
        return (int) (offset / partSize);
    }
}
