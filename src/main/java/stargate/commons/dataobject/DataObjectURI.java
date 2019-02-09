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
package stargate.commons.dataobject;

import java.net.URI;
import java.net.URISyntaxException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author iychoi
 */
public class DataObjectURI implements Comparable {

    /*
    DataObjectURI consists of 
     - sgfs://<CLUSTER_ID>/path/to/resource
    */
    public static final String STARGATE_SCHEME = "sgfs";
    public static final String WILDCARD_LOCAL_CLUSTER_NAME = "local";

    private URI uri;
    
    DataObjectURI() {
    }
    
    public DataObjectURI(String uri) {
        if(uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("uri is null or empty");
        }
        
        if(!uri.startsWith(STARGATE_SCHEME + "://")) {
            if(uri.startsWith("/")) {
                if(uri.equals("/")) {
                    initialize(STARGATE_SCHEME + ":///");
                } else {
                    initialize(STARGATE_SCHEME + "://" + uri.substring(1));
                }
            } else {
                initialize(STARGATE_SCHEME + "://" + uri);
            }
        } else {
            if(uri.equals(STARGATE_SCHEME + "://")) {
                initialize(STARGATE_SCHEME + ":///");
            } else {
                initialize(uri);
            }
        }
    }
    
    public DataObjectURI(String cluster, String parent, String child) {
        if(parent == null || parent.isEmpty()) {
            throw new IllegalArgumentException("parent is null or empty");
        }
        
        if(child == null || child.isEmpty()) {
            throw new IllegalArgumentException("child is null or empty");
        }
        
        if(cluster == null || cluster.isEmpty()) {
            initialize("", parent, child);
        } else {
            initialize(cluster, parent, child);
        }
    }
    
    public DataObjectURI(String cluster, String uri) {
        if(cluster == null || cluster.isEmpty()) {
            initialize("", uri);
        } else {
            initialize(cluster, uri);
        }
    }
    
    public DataObjectURI(URI uri) {
        if(uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        initialize(uri);
    }
    
    public DataObjectURI(DataObjectURI parent, String child) {
        if(parent == null) {
            throw new IllegalArgumentException("parent is null");
        }
        
        if(child == null || child.isEmpty()) {
            throw new IllegalArgumentException("child is null or empty");
        }
        
        initialize(parent, child);
    }
    
    private void initialize(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        String scheme = null;
        String clusterID = null;
        String path = null;
        
        int start = 0;

        // parse uri scheme
        int colon = uri.indexOf(':');
        if (colon < 0) {
            throw new IllegalArgumentException("uri scheme is missing");
        }
        
        scheme = uri.substring(0, colon);
        start = colon + 1;
        
        if(!scheme.equalsIgnoreCase(STARGATE_SCHEME)) {
            throw new IllegalArgumentException("uri scheme given does not match to Stargate scheme (" + scheme + " vs. " + STARGATE_SCHEME + ")");
        }

        // parse uri authority
        if (uri.startsWith("//", start) && (uri.length() - start > 2)) {
            // have authority
            int nextSlash = uri.indexOf('/', start + 2);
            int authEnd;
            if (nextSlash != -1) {
                authEnd = nextSlash;
            } else {
                authEnd = uri.length();
            }

            clusterID = uri.substring(start + 2, authEnd);
            start = authEnd;
        } else {
            throw new IllegalArgumentException("uri authority is missing");
        }
        
        // uri path
        if(start < uri.length()) {
            path = uri.substring(start, uri.length());
        } else {
            path = "/";
        }
        
        this.uri = createURI(clusterID, path);
    }
    
    private void initialize(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        String scheme = uri.getScheme();
        if(scheme != null && !scheme.equalsIgnoreCase(STARGATE_SCHEME)) {
            throw new IllegalArgumentException("uri scheme given does not match to Stargate scheme (" + scheme + " vs. " + STARGATE_SCHEME + ")");
        }
        
        String authority = uri.getAuthority();
        if(authority == null) {
            authority = "";
        }
        
        this.uri = createURI(authority, uri.getPath());
    }
    
    private void initialize(String cluster, String path) {
        this.uri = createURI(cluster, path);
    }
    
    private void initialize(String cluster, String parent, String child) {
        if (parent == null || parent.isEmpty()) {
            throw new IllegalArgumentException("parent is null or empty");
        }
        
        if(parent.endsWith("/")) {
            this.uri = createURI(cluster, parent);
        } else {
            this.uri = createURI(cluster, parent + "/");
        }
        this.uri.resolve(normalizePath(child)).normalize();
    }
    
    private void initialize(DataObjectURI parent, String child) {
        if (parent == null) {
            throw new IllegalArgumentException("parent is null");
        }
        
        if (child == null || child.isEmpty()) {
            throw new IllegalArgumentException("child is null or empty");
        }
        
        if(parent.uri.getPath().endsWith("/")) {
            this.uri = parent.uri.resolve(normalizePath(child)).normalize();
        } else {
            try {
                URI uri = new URI(parent.uri.toASCIIString() + "/");
                this.uri = uri.resolve(normalizePath(child)).normalize();
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    }
    
    private URI createURI(String authority, String path) {
        try {
            String newPath = path;
            if(!path.startsWith("/")) {
                newPath = "/" + path;
            }
            
            if(authority == null || authority.equals("")) {
                URI uri = new URI(STARGATE_SCHEME, "", normalizePath(newPath), null, null);
                return uri.normalize();
            } else {
                URI uri = new URI(STARGATE_SCHEME, authority, normalizePath(newPath), null, null);
                return uri.normalize();
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private String normalizePath(String path) {
        // replace all "//" and "\" to "/"
        path = path.replace("//", "/");
        path = path.replace("\\", "/");

        // trim trailing slash
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }
    
    @JsonProperty("uri")
    public URI toUri() {
        return this.uri;
    }
    
    @JsonProperty("uri")
    public void setUri(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        
        String scheme = uri.getScheme();
        if(scheme != null && !scheme.equalsIgnoreCase(STARGATE_SCHEME)) {
            throw new IllegalArgumentException("uri scheme given does not match to Stargate scheme (" + scheme + " vs. " + STARGATE_SCHEME + ")");
        }
        
        this.uri = uri;
    }
    
    @JsonIgnore
    public String getClusterName() {
        return this.uri.getAuthority();
    }
    
    @JsonIgnore
    public String getFileName() {
        String path = this.uri.getPath();
        int slash = path.lastIndexOf('/');
        return path.substring(slash + 1, path.length());
    }
    
    @JsonIgnore
    public DataObjectURI getParent() {
        String path = this.uri.getPath();
        int lastSlash = path.lastIndexOf('/');
        
        // empty
        if (path.length() == 0) {
            return null;
        }
        
        // root
        if (path.length() == 1 && lastSlash == 0) {
            return null;
        }
        
        if (lastSlash == -1) {
            return new DataObjectURI(createURI(this.uri.getAuthority(), "."));
        } else if (lastSlash == 0) {
            return new DataObjectURI(createURI(this.uri.getAuthority(), "/"));
        } else {
            String parent = path.substring(0, lastSlash);
            return new DataObjectURI(createURI(this.uri.getAuthority(), parent));
        }
    }
    
    @JsonIgnore
    public boolean isRoot() {
        String cluster = this.uri.getAuthority();
        if(cluster == null || cluster.isEmpty()) {
            String path = this.uri.getPath();
            if (path == null || path.isEmpty()) {
                return true;
            }
            
            int lastSlash = path.lastIndexOf('/');

            // root
            if (path.length() == 1 && lastSlash == 0) {
                return true;
            }
        }
        return false;
    }
    
    @JsonIgnore
    public boolean isClusterRoot() {
        if(isRoot()) {
            return false;
        }
        
        String authority = this.uri.getAuthority();
        if(authority == null || authority.isEmpty()) {
            return false;
        }
        
        String path = this.uri.getPath();
        if (path == null) {
            return true;
        }
        
        int lastSlash = path.lastIndexOf('/');
        
        // empty
        if (path.length() == 0) {
            return true;
        }
        
        // root
        if (path.length() == 1 && lastSlash == 0) {
            return true;
        }
        return false;
    }
    
    @Override
    @JsonIgnore
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        String scheme = this.uri.getScheme();
        if (scheme != null) {
            sb.append(scheme);
            sb.append(":");
        }
        
        sb.append("//");
        
        String authority = this.uri.getAuthority();
        if (authority != null) {
            sb.append(authority);
        }
        
        String path = this.uri.getPath();
        if (path != null) {
            sb.append(path);
        }
        
        return sb.toString();
    }
    
    @JsonIgnore
    public String getPath() {
        return this.uri.getPath();
    }
    
    @Override
    @JsonIgnore
    public boolean equals(Object o) {
        if (!(o instanceof DataObjectURI))
            return false;
        
        DataObjectURI other = (DataObjectURI) o;
        return this.uri.equals(other.uri);
    }
    
    @Override
    @JsonIgnore
    public int hashCode() {
        return this.uri.hashCode();
    }
    
    @Override
    @JsonIgnore
    public int compareTo(Object o) {
        DataObjectURI other = (DataObjectURI) o;
        return this.uri.compareTo(other.uri);
    } 
}
