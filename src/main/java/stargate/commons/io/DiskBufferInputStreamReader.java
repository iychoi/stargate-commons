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
package stargate.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class DiskBufferInputStreamReader implements Runnable {

    private static final Log LOG = LogFactory.getLog(DiskBufferInputStreamReader.class);
    
    
    private static final int BUFFER_SIZE = 16 * 1024; // 16KB
    
    private InputStream inputStream;
    private OutputStream outputStream;
    private long offset;
    private Object notifyObject = new Object();
    private long notifyOffset;
    private int bufferSize = BUFFER_SIZE;
    private boolean done;
    
    public DiskBufferInputStreamReader(InputStream is, OutputStream os, int bufferSize) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        if(os == null) {
            throw new IllegalArgumentException("os is null");
        }
        
        if(bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize is negative");
        }
        
        this.inputStream = is;
        this.outputStream = os;
        this.offset = 0;
        this.notifyOffset = 0;
        this.bufferSize = bufferSize;
        this.done = false;
    }
    
    public DiskBufferInputStreamReader(InputStream is, OutputStream os) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        if(os == null) {
            throw new IllegalArgumentException("os is null");
        }
        
        this.inputStream = is;
        this.outputStream = os;
        this.offset = 0;
        this.notifyOffset = 0;
        this.done = false;
    }
    
    public long getOffset() {
        return this.offset;
    }
    
    public boolean isDone() {
        return this.done;
    }
    
    public boolean waitUntil(long offset, int size) {
        if(this.offset >= offset + size) {
            return true;
        }
        
        if(this.done) {
            return false;
        }
        
        try {
            this.notifyOffset = offset + size;
            
            while(this.offset < offset + size) {
                synchronized(this.notifyObject) {
                    this.notifyObject.wait(3000);
                }
                
                if(this.offset >= offset + size) {
                    return true;
                }
                
                if(this.done) {
                    return false;
                }
            }
            
            return true;
        } catch (InterruptedException ex) {
            if(this.offset >= offset + size) {
                return true;
            }
            return false;
        }
    }
    
    @Override
    public void run() {
        if(this.done) {
           return; 
        }
        
        int read;
        try {
            byte[] buffer = new byte[this.bufferSize];
    
            while((read = this.inputStream.read(buffer, 0, this.bufferSize)) >= 0) {
                this.outputStream.write(buffer, 0, read);
                this.outputStream.flush();
                
                this.offset += read;
                if(this.notifyOffset > 0) {
                    if(this.notifyOffset <= this.offset) {
                        synchronized(this.notifyObject) {
                            this.notifyObject.notifyAll();
                        }
                    }
                }
            }
        } catch(Exception ex) {
            LOG.error(ex);
        } finally {
            // CLOSE
            try {
                this.inputStream.close();
            } catch(Exception e) {
            }
            
            try {
                this.outputStream.close();
            } catch(Exception e) {
            }
            
            this.done = true;
        }
    }
}
