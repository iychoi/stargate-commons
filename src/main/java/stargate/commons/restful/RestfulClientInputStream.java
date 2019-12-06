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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author iychoi
 */
public class RestfulClientInputStream extends InputStream implements Closeable {

    private RestfulClient restfulClient;
    private InputStream inputStream;
    
    public RestfulClientInputStream(RestfulClient restfulClient, InputStream is) throws IOException {
        if(restfulClient == null) {
            throw new IllegalArgumentException("restfulClient is null");
        }
        
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        this.restfulClient = restfulClient;
        this.inputStream = is;
    }

    public RestfulClient getRestfulClient() {
        return this.restfulClient;
    }
    
    public InputStream getInputStream() {
        return this.inputStream;
    }
    
    @Override
    public synchronized int read() throws IOException {
        return this.read();
    }
    
    @Override
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        return this.inputStream.read(bytes, off, len);
    }
    
    @Override
    public synchronized int read(byte bytes[]) throws IOException {
        return this.inputStream.read(bytes);
    }
    
    @Override
    public synchronized long skip(long skip) throws IOException {
        return this.inputStream.skip(skip);
    }
    
    @Override
    public synchronized int available() throws IOException {
        return this.inputStream.available();
    }

    @Override
    public synchronized void close() throws IOException {
        if(this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (IOException ex) {
            } finally {
                this.inputStream = null;
            }
        }
        
        if(this.restfulClient != null) {
            this.restfulClient.close();
        }
    }

    @Override
    public boolean markSupported() {
        return this.inputStream.markSupported();
    }

    @Override
    public void mark(int readLimit) {
        this.inputStream.mark(readLimit);
    }

    @Override
    public void reset() throws IOException {
        this.inputStream.reset();
    }
}
