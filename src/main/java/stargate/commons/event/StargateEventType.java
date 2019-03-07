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
package stargate.commons.event;

/**
 *
 * @author iychoi
 */
public enum StargateEventType {
    STARGATE_EVENT_TYPE_DATAEXPORT (0),
    STARGATE_EVENT_TYPE_REMOTECLUSTER (1),
    STARGATE_EVENT_TYPE_NODE (2),
    STARGATE_EVENT_TYPE_TRANSPORT (3),
    STARGATE_EVENT_TYPE_TRANSFER_WORKLOAD (4);
    
    private int numVal;
    
    StargateEventType(int numVal) {
        this.numVal = numVal;
    }
    
    public int getNumVal() {
        return this.numVal;
    }
    
    public static StargateEventType fromNumVal(int numVal) {
        for(StargateEventType type : StargateEventType.values()) {
            if(type.getNumVal() == numVal) {
                return type;
            }
        }
        return null;
    }
}
