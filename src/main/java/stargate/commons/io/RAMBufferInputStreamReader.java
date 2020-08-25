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
public class RAMBufferInputStreamReader implements Runnable {

    private static final Log LOG = LogFactory.getLog(RAMBufferInputStreamReader.class);
    
    private RAMBufferInputStream bufferInputStream;
    private InputStream inputStream;
    private byte[] dataBuffer;
    private int offset;
    private Object notifyObject = new Object();
    private long notifySize;
    private boolean done;
    private static int READ_LEN = 1024*64;
    
    public RAMBufferInputStreamReader(RAMBufferInputStream bis, InputStream is, byte[] buffer) throws IOException {
        if(bis == null) {
            throw new IllegalArgumentException("bis is null");
        }
        
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        if(buffer == null) {
            throw new IllegalArgumentException("buffer is null");
        }
        
        this.bufferInputStream = bis;
        this.inputStream = is;
        this.dataBuffer = buffer;
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
            while((readLen = this.inputStream.read(this.dataBuffer, this.offset, Math.min(this.dataBuffer.length - this.offset, READ_LEN))) >= 0) {
                this.offset += readLen;
                this.bufferInputStream.notifyBufferWrite(readLen);
                
                synchronized(this.notifyObject) {
                    if(this.notifySize >= 0 && this.notifySize <= this.offset) {
                        this.notifyObject.notifyAll();
                        this.notifySize = -1;
                    }
                }
                
                if(this.offset >= this.dataBuffer.length) {
                    break;
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
            
            this.done = true;
        }
    }
}
