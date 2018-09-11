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
package stargate.commons.restful;

import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

/**
 *
 * @author iychoi
 */
public class RestfulClient {
    
    private static final Log LOG = LogFactory.getLog(RestfulClient.class);
    
    private URI serviceURL;
    private ClientConnectionManager connectionManager;
    private ClientConfig httpClientConfig;
    private ApacheHttpClient4 httpClient;
    
    public RestfulClient(URI serviceURL, String username, String password) {
        if(serviceURL == null) {
            throw new IllegalArgumentException("serviceURL is null");
        }
        
        this.serviceURL = serviceURL;
        
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", this.serviceURL.getPort(), PlainSocketFactory.getSocketFactory()));
        this.connectionManager = new ThreadSafeClientConnManager(registry);
        
        this.httpClientConfig = new DefaultClientConfig();
        this.httpClientConfig.getClasses().add(JacksonJsonProvider.class);
        this.httpClientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        this.httpClientConfig.getProperties().put(ApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER, this.connectionManager);
        this.httpClientConfig.getProperties().put(ApacheHttpClient4Config.PROPERTY_DISABLE_COOKIES, Boolean.FALSE);
        
        this.httpClient = ApacheHttpClient4.create(this.httpClientConfig);
        if(username != null && !username.isEmpty()) {
            if(password == null) {
                password = "";
            }
            
            this.httpClient.addFilter(new HTTPBasicAuthFilter(username, password));
        }
        
        LOG.debug("RestfulClient for " + this.serviceURL.toString() + " is created");
    }
    
    public void close() {
        LOG.debug("Destroying RestfulClient for " + this.serviceURL.toString());
        
        //this.httpClient.getExecutorService().shutdownNow();
        if(this.httpClient != null) {
            this.httpClient.destroy();
            this.httpClient = null;
        }
        
        if(this.connectionManager != null) {
            this.connectionManager.shutdown();
            this.connectionManager = null;
        }
        
        LOG.debug("RestfulClient for " + this.serviceURL.toString() + " is destroyed");
    }
    
    private URI makeRequestURL(String path) {
        String urlString = this.serviceURL.toASCIIString();
        if(!urlString.endsWith("/")) {
            urlString = urlString + "/";
        }
        
        URI requestURL;
        try {
            if(path.startsWith("/")) {
                requestURL = new URI(urlString + path.substring(1));
            } else {
                requestURL = new URI(urlString + path);
            }
        } catch (URISyntaxException ex) {
            LOG.error(ex);
            requestURL = this.serviceURL.resolve(path);
        }
        return requestURL;
    }
    
    public Object post(String path, Object request, GenericType<?> responseType) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        Future<ClientResponse> future = postAsync(path, request);
        return processPost(future, responseType);
    }
    
    public Future<ClientResponse> postAsync(String path, Object request) throws IOException {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        // request can be null
        //if(request == null) {
        //    throw new IllegalArgumentException("request is null");
        //}
        
        URI requestURL = makeRequestURL(path);
        LOG.debug("sending a post request - " + requestURL.toString());
        AsyncWebResource webResource = this.httpClient.asyncResource(requestURL);
        return (Future<ClientResponse>) webResource.accept("application/json").type("application/json").post(ClientResponse.class, request);
    }
    
    public Object processPost(Future<ClientResponse> future, GenericType<?> responseType) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        if(future == null) {
            throw new IllegalArgumentException("future is null");
        }
        
        //if(responseType == null) {
        //    throw new IllegalArgumentException("responseType is null");
        //}
        
        // wait for completion
        try {
            ClientResponse response = future.get();
            if(response.getStatus() >= 200 && response.getStatus() <= 299) {
                if(responseType == null) {
                    response.close();
                    return true;
                } else {
                    Object entity = response.getEntity(responseType);
                    response.close();
                    return entity;
                }
            } else if(response.getStatus() == 401 || response.getStatus() == 403) {
                response.close();
                throw new AuthenticationException();
            } else if(response.getStatus() == 404) {
                String message = response.toString();
                response.close();
                throw new FileNotFoundException(message);
            } else {
                RestfulError err = response.getEntity(new GenericType<RestfulError>(){});
                err.setHttpErrno(response.getStatus());
                if(response.getLocation() != null) {
                    err.setPath(response.getLocation().toString());
                }
                response.close();
                throw err.makeException();
            }
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
    
    public Object put(String path, Object request, GenericType<?> responseType) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        Future<ClientResponse> future = putAsync(path, request);
        return processPut(future, responseType);
    }
    
    public Future<ClientResponse> putAsync(String path, Object request) throws IOException {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        // request can be null
        //if(request == null) {
        //    throw new IllegalArgumentException("request is null");
        //}
        
        URI requestURL = makeRequestURL(path);
        LOG.debug("sending a put request - " + requestURL.toString());
        AsyncWebResource webResource = this.httpClient.asyncResource(requestURL);
        return (Future<ClientResponse>) webResource.accept("application/json").type("application/json").put(ClientResponse.class, request);
    }
    
    public Object processPut(Future<ClientResponse> future, GenericType<?> responseType) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        if(future == null) {
            throw new IllegalArgumentException("future is null");
        }
        
        //if(responseType == null) {
        //    throw new IllegalArgumentException("responseType is null");
        //}
        
        // wait for completion
        try {
            ClientResponse response = future.get();
            if(response.getStatus() >= 200 && response.getStatus() <= 299) {
                if(responseType == null) {
                    response.close();
                    return true;
                } else {
                    Object entity = response.getEntity(responseType);
                    response.close();
                    return entity;
                }
            } else if(response.getStatus() == 401 || response.getStatus() == 403) {
                response.close();
                throw new AuthenticationException();
            } else if(response.getStatus() == 404) {
                String message = response.toString();
                response.close();
                throw new FileNotFoundException(message);
            } else {
                RestfulError err = response.getEntity(new GenericType<RestfulError>(){});
                err.setHttpErrno(response.getStatus());
                if(response.getLocation() != null) {
                    err.setPath(response.getLocation().toString());
                }
                response.close();
                throw err.makeException();
            }
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
    
    public Object get(String path, GenericType<?> generic) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        Future<ClientResponse> future = getAsync(path);
        return processGet(future, generic);
    }
    
    public Future<ClientResponse> getAsync(String path) throws IOException {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        URI requestURL = makeRequestURL(path);
        LOG.debug("sending a get request - " + requestURL.toString());
        AsyncWebResource webResource = this.httpClient.asyncResource(requestURL);
        return (Future<ClientResponse>) webResource.accept("application/json").type("application/json").get(ClientResponse.class);
    }
    
    public Object processGet(Future<ClientResponse> future, GenericType<?> generic) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        if(future == null) {
            throw new IllegalArgumentException("future is null");
        }
        
        if(generic == null) {
            throw new IllegalArgumentException("generic is null");
        }
        
        // wait for completion
        try {
            ClientResponse response = future.get();
            if(response.getStatus() >= 200 && response.getStatus() <= 299) {
                Object entity = response.getEntity(generic);
                response.close();
                return entity;
            } else if(response.getStatus() == 401 || response.getStatus() == 403) {
                response.close();
                throw new AuthenticationException();
            } else if(response.getStatus() == 404) {
                String message = response.toString();
                response.close();
                throw new FileNotFoundException(message);
            } else {
                RestfulError err = response.getEntity(new GenericType<RestfulError>(){});
                err.setHttpErrno(response.getStatus());
                if(response.getLocation() != null) {
                    err.setPath(response.getLocation().toString());
                }
                response.close();
                throw err.makeException();
            }
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
    
    public Object delete(String path, GenericType<?> generic) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        Future<ClientResponse> future = deleteAsync(path);
        return processDelete(future, generic);
    }
    
    public Future<ClientResponse> deleteAsync(String path) throws IOException {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        URI requestURL = makeRequestURL(path);
        LOG.debug("sending a delete request - " + requestURL.toString());
        AsyncWebResource webResource = this.httpClient.asyncResource(requestURL);
        return (Future<ClientResponse>) webResource.accept("application/json").type("application/json").delete(ClientResponse.class);
    }
    
    public Object processDelete(Future<ClientResponse> future, GenericType<?> generic) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        if(future == null) {
            throw new IllegalArgumentException("future is null");
        }
        
        //if(generic == null) {
        //    throw new IllegalArgumentException("generic is null");
        //}
        
        // wait for completion
        try {
            ClientResponse response = future.get();
            if(response.getStatus() >= 200 && response.getStatus() <= 299) {
                if(generic == null) {
                    response.close();
                    return true;
                } else {
                    Object entity = response.getEntity(generic);
                    response.close();
                    return entity;
                }
            } else if(response.getStatus() == 401 || response.getStatus() == 403) {
                response.close();
                throw new AuthenticationException();
            } else if(response.getStatus() == 404) {
                String message = response.toString();
                response.close();
                throw new FileNotFoundException(message);
            } else {
                RestfulError err = response.getEntity(new GenericType<RestfulError>(){});
                err.setHttpErrno(response.getStatus());
                if(response.getLocation() != null) {
                    err.setPath(response.getLocation().toString());
                }
                response.close();
                throw err.makeException();
            }
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
    
    public InputStream download(String path) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        Future<ClientResponse> future = deleteAsync(path);
        return processDownload(future);
    }
    
    public Future<ClientResponse> downloadAsync(String path) throws IOException {
        if(path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is null or empty");
        }
        
        URI requestURL = makeRequestURL(path);
        LOG.debug("sending a download request - " + requestURL.toString());
        AsyncWebResource webResource = this.httpClient.asyncResource(requestURL);
        return (Future<ClientResponse>) webResource.accept("application/octet-stream").type("application/json").get(ClientResponse.class);
    }
    
    public InputStream processDownload(Future<ClientResponse> future) throws IOException, FileNotFoundException, RestfulException, AuthenticationException {
        if(future == null) {
            throw new IllegalArgumentException("future is null");
        }
        
        // wait for completition
        try {
            ClientResponse response = future.get();
            if(response.getStatus() >= 200 && response.getStatus() <= 299) {
                InputStream entityInputStream = response.getEntityInputStream();
                //response.close();
                return entityInputStream;
            } else if(response.getStatus() == 401 || response.getStatus() == 403) {
                response.close();
                throw new AuthenticationException();
            } else if(response.getStatus() == 404) {
                String message = response.toString();
                response.close();
                throw new FileNotFoundException(message);
            } else {
                RestfulError err = response.getEntity(new GenericType<RestfulError>(){});
                err.setHttpErrno(response.getStatus());
                if(response.getLocation() != null) {
                    err.setPath(response.getLocation().toString());
                }
                response.close();
                throw err.makeException();
            }
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }
}
