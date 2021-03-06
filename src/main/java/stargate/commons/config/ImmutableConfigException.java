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
package stargate.commons.config;

/**
 *
 * @author iychoi
 */
public class ImmutableConfigException extends RuntimeException {

    public ImmutableConfigException() {
        super();
    }

    public ImmutableConfigException(String string) {
        super(string);
    }

    public ImmutableConfigException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public ImmutableConfigException(Throwable thrwbl) {
        super(thrwbl);
    }
}
