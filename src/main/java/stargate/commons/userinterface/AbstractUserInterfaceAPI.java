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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import stargate.commons.cluster.Cluster;
import stargate.commons.config.AbstractImmutableConfig;
import stargate.commons.dataobject.DataObjectMetadata;
import stargate.commons.dataobject.DataObjectURI;
import stargate.commons.datasource.DataExportEntry;
import stargate.commons.recipe.Recipe;

/**
 *
 * @author iychoi
 */
public abstract class AbstractUserInterfaceAPI {
    public abstract boolean isLive() throws IOException;
    public abstract AbstractImmutableConfig getServiceConfig() throws IOException;
    
    public abstract Cluster getCluster() throws IOException;
    
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
    
    public abstract DataObjectMetadata getDataObjectMetadata(DataObjectURI uri) throws IOException;
    public abstract Collection<DataObjectMetadata> listDataObjectMetadata(DataObjectURI uri) throws IOException;
    
    public abstract Collection<String> listRecipes() throws IOException;
    public abstract Recipe getRecipe(DataObjectURI uri) throws IOException;
    public abstract void removeRecipe(DataObjectURI uri) throws IOException;
    public abstract void syncRecipes() throws IOException;
    
    public abstract InputStream getDataChunk(String hash) throws IOException;
    
    public abstract boolean schedulePrefetch(DataObjectURI uri, String hash) throws IOException;
    public abstract Collection<String> listDataSources() throws IOException;
}
