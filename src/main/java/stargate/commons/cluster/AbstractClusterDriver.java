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

import java.io.IOException;
import stargate.commons.driver.AbstractDriver;
import stargate.commons.driver.DriverNotInitializedException;

/**
 *
 * @author iychoi
 */
public abstract class AbstractClusterDriver extends AbstractDriver {
    
    public abstract void activateCluster() throws IOException, DriverNotInitializedException;
    public abstract boolean isClusterActive() throws IOException, DriverNotInitializedException;
    
    public abstract Node getLocalNode() throws IOException, DriverNotInitializedException;
    public abstract Cluster getLocalCluster() throws IOException, DriverNotInitializedException;
    public abstract String getLeaderNodeName() throws IOException, DriverNotInitializedException;
    public abstract boolean isLeaderNode() throws IOException, DriverNotInitializedException;
    
    public abstract void addLocalClusterEventHandler(AbstractLocalClusterEventHandler eventHandler);
    public abstract void removeLocalClusterEventHandler(AbstractLocalClusterEventHandler eventHandler);

}
