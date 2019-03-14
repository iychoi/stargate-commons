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
package stargate.commons.recipe;

import java.util.Comparator;

/**
 *
 * @author iychoi
 */
public class RecipeChunkComparator implements Comparator<RecipeChunk> {

    @Override
    public int compare(RecipeChunk c1, RecipeChunk c2) {
        if(c1 == c2) {
            return 0;
        }
        
        if(c1 == null || c2 == null) {
            if(c1 == null) {
                return -1;
            }
            
            if(c2 == null) {
                return 1;
            }
        }
        
        return (int) (c1.getOffset() - c2.getOffset());
    }
}
