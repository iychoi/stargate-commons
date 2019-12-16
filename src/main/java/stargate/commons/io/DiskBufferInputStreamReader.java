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
    private long notifySize;
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
        this.notifySize = -1;
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
        this.notifySize = 0;
        this.done = false;
    }
    
    public long getOffset() {
        return this.offset;
    }
    
    public boolean isDone() {
        return this.done;
    }
    
    public void waitUntil(long size) throws IOException {
        synchronized(this.notifyObject) {
            if(this.offset >= size) {
                return;
            }

            if(this.done) {
                throw new IOException(String.format("cannot wait size %d (offset %d) - stream is already closed", size, this.offset));
            }
        }
        
        try {
            synchronized(this.notifyObject) {
                this.notifySize = size;
                
                while(this.offset < size) {
                    this.notifyObject.wait(3000);
                    
                    if(this.offset >= size) {
                        this.notifySize = -1;
                        return;
                    }

                    if(this.done) {
                        throw new IOException(String.format("cannot finish waiting size %d (offset %d) - stream is closed", size, this.offset));
                    }
                }
            }
            
            return;
        } catch (InterruptedException ex) {
            if(this.offset >= size) {
                return;
            }
            throw new IOException(ex);
        }
    }
    
    @Override
    public void run() {
        if(this.done) {
           return; 
        }
        
        int readLen;
        try {
            byte[] buffer = new byte[this.bufferSize];
    
            while((readLen = this.inputStream.read(buffer, 0, this.bufferSize)) >= 0) {
                this.outputStream.write(buffer, 0, readLen);
                this.outputStream.flush();
             
                this.offset += readLen;
                
                synchronized(this.notifyObject) {
                    if(this.notifySize >= 0 && this.notifySize <= this.offset) {
                        this.notifyObject.notifyAll();
                        this.notifySize = -1;
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
