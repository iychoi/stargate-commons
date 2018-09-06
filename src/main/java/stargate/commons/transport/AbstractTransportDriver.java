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
package stargate.commons.transport;

import java.io.IOException;
import java.net.URI;
import stargate.commons.cluster.Node;
import stargate.commons.driver.AbstractDriver;

/**
 *
 * @author iychoi
 */
public abstract class AbstractTransportDriver extends AbstractDriver {
    public abstract void startServer() throws IOException;
    public abstract void stopServer() throws IOException;
    public abstract boolean isServerStarted();
    public abstract URI getServiceURI() throws IOException;
    
    public abstract AbstractTransportClient getClient(Node node) throws IOException;
}
