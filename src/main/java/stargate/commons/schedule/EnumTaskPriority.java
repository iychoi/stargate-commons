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
package stargate.commons.schedule;

/**
 *
 * @author iychoi
 */
public enum EnumTaskPriority {
    TASK_PRIORITY_HIGH(10),
    TASK_PRIORITY_MID(5),
    TASK_PRIORITY_LOW(1);
    
    private int numPriority;
    
    EnumTaskPriority(int num) {
        this.numPriority = num;
    }
    
    public int getPriorityNum() {
        return this.numPriority;
    }
    
    public static int getPriorityNum(EnumTaskPriority priority) {
        return priority.numPriority;
    }
}
