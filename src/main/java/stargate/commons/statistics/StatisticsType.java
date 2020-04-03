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
package stargate.commons.statistics;

/**
 *
 * @author iychoi
 */
public enum StatisticsType {
    STATISTICS_TYPE_RECIPE_CHUNK_CREATION ("CHUNK"),
    STATISTICS_TYPE_DATA_CHUNK_TRANSFER_RECEIVE ("TRANSFER_RECV"),
    STATISTICS_TYPE_DATA_CHUNK_TRANSFER_SEND ("TRANSFER_SEND"),
    STATISTICS_TYPE_LOCAL_NODE_DATA_CHUNK_TRANSFER_SEND ("TRANSFER_SEND_LOCAL_NODE_CHUNK"),
    STATISTICS_TYPE_REMOTE_NODE_DATA_CHUNK_TRANSFER_SEND ("TRANSFER_SEND_REMOTE_NODE_CHUNK");
    
    private String strVal;
    
    StatisticsType(String strVal) {
        this.strVal = strVal;
    }
    
    public String getStrVal() {
        return this.strVal;
    }
    
    public static StatisticsType fromStrVal(String strVal) {
        for(StatisticsType type : StatisticsType.values()) {
            if(type.getStrVal().equalsIgnoreCase(strVal)) {
                return type;
            }
            
            if(type.name().equalsIgnoreCase(strVal)) {
                return type;
            }
        }
        return null;
    }
}
