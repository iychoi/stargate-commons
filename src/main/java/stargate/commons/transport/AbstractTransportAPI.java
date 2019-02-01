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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import stargate.commons.cluster.Cluster;
import stargate.commons.dataobject.DataObjectMetadata;
import stargate.commons.dataobject.DataObjectURI;
import stargate.commons.dataobject.Directory;
import stargate.commons.recipe.Recipe;
import stargate.commons.service.FSServiceInfo;

/**
 *
 * @author iychoi
 */
public abstract class AbstractTransportAPI {
    public abstract boolean isLive() throws IOException;
    public abstract FSServiceInfo getFSServiceInfo() throws IOException;
    
    public abstract Cluster getLocalCluster() throws IOException;
    
    public abstract DataObjectMetadata getDataObjectMetadata(DataObjectURI uri) throws FileNotFoundException, IOException;
    public abstract Collection<DataObjectMetadata> listDataObjectMetadata(DataObjectURI uri) throws IOException;
    public abstract Directory getDirectory(DataObjectURI uri) throws IOException;
    
    public abstract Recipe getRecipe(DataObjectURI uri) throws IOException;
    
    public abstract InputStream getDataChunk(String hash) throws IOException;
}
