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
package stargate.commons.userinterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import stargate.commons.cluster.Cluster;
import stargate.commons.cluster.Node;
import stargate.commons.dataobject.DataObjectMetadata;
import stargate.commons.dataobject.DataObjectURI;
import stargate.commons.datasource.DataExportEntry;
import stargate.commons.recipe.Recipe;
import stargate.commons.service.FSServiceInfo;
import stargate.commons.transport.TransferAssignment;

/**
 *
 * @author iychoi
 */
public abstract class AbstractUserInterfaceAPI {
    public abstract boolean isLive() throws IOException;
    public abstract String getServiceConfig() throws IOException;
    public abstract FSServiceInfo getFSServiceInfo() throws IOException;
    
    public abstract Cluster getCluster(String name) throws IOException;
    
    public abstract Cluster getLocalCluster() throws IOException;
    public abstract void activateCluster() throws IOException;
    public abstract boolean isClusterActive() throws IOException;
    public abstract Node getLocalNode() throws IOException;
    
    public abstract Collection<String> listRemoteClusters() throws IOException;
    public abstract Collection<Cluster> getRemoteClusters() throws IOException;
    public abstract Cluster getRemoteCluster(String name) throws IOException;
    public abstract void addRemoteCluster(Cluster cluster) throws IOException;
    public abstract void removeRemoteCluster(String name) throws IOException;

    public abstract Collection<String> listDataExportEntries() throws IOException;
    public abstract Collection<DataExportEntry> getDataExportEntries() throws IOException;
    public abstract DataExportEntry getDataExportEntry(DataObjectURI uri) throws IOException;
    public abstract void addDataExportEntry(DataExportEntry entry) throws IOException;
    public abstract void removeDataExportEntry(DataObjectURI uri) throws IOException;
    
    public abstract DataObjectMetadata getDataObjectMetadata(DataObjectURI uri) throws FileNotFoundException, IOException;
    public abstract Collection<DataObjectMetadata> listDataObjectMetadata(DataObjectURI uri) throws IOException;
    
    public abstract Collection<String> listRecipes() throws IOException;
    public abstract Recipe getRecipe(DataObjectURI uri) throws IOException;
    public abstract void removeRecipe(DataObjectURI uri) throws IOException;
    public abstract void syncRecipes() throws IOException;
    
    public abstract InputStream getDataChunk(DataObjectURI uri, String hash) throws IOException;
    
    public abstract TransferAssignment schedulePrefetch(DataObjectURI uri, String hash) throws IOException;
    public abstract Recipe getRemoteRecipeWithTransferSchedule(DataObjectURI uri) throws IOException;
    public abstract Collection<String> listDataSources() throws IOException;
}
