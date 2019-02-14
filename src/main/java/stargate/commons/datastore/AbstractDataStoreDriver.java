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

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import stargate.commons.driver.AbstractDriver;
import stargate.commons.driver.DriverNotInitializedException;

/**
 *
 * @author iychoi
 */
public abstract class AbstractDataStoreDriver extends AbstractDriver {
    public abstract AbstractKeyValueStore getKeyValueStore(String name, Class valueClass, EnumDataStoreProperty property) throws IOException, DriverNotInitializedException;
    public abstract AbstractKeyValueStore getKeyValueStore(String name, Class valueClass, EnumDataStoreProperty property, TimeUnit timeunit, long timeval, boolean allowKeyLock) throws IOException, DriverNotInitializedException;
    public abstract AbstractQueue getQueue(String name, Class valueClass, EnumDataStoreProperty property) throws IOException, DriverNotInitializedException;
}
