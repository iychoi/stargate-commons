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
package stargate.commons.cluster;

import java.util.Comparator;

/**
 *
 * @author iychoi
 */
public class NodeNameComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        if(o1 == null && o2 == null) {
            return 0;
        }
        
        if(o1 == null) {
            return -1;
        }
            
        if(o2 == null) {
            return 1;
        }
        
        return o1.getName().compareTo(o2.getName());
    }
}
