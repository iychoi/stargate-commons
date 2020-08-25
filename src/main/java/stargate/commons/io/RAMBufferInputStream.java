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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class RAMBufferInputStream extends AbstractSeekableInputStream {

    private static final Log LOG = LogFactory.getLog(RAMBufferInputStream.class);
    
    private int offset = 0;
    private int dataSize = 0;
    private int dataSizeFilled = 0;
    
    private byte[] dataBuffer;
    private RAMBufferInputStreamReader inputStreamReader;
    private Thread readThread;
    private InputStream inputStream;
    
    public RAMBufferInputStream(InputStream is, int dataSize) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        if(dataSize < 0) {
            throw new IllegalArgumentException("dataSize is negative");
        }
        
        initialize(is, dataSize);
    }
    
    private void initialize(InputStream is, int dataSize) throws IOException {
        this.dataBuffer = new byte[dataSize];
        this.inputStream = is;

        this.inputStreamReader = new RAMBufferInputStreamReader(this, is, dataBuffer);
        this.readThread = new Thread(this.inputStreamReader);
        this.readThread.start();
        
        this.offset = 0;
        this.dataSize = dataSize;
    }
    
    private synchronized void waitData(int offset) throws IOException {
        if(this.offset >= this.dataSize) {
            return;
        }
        
        long waitOffset = offset;
        if(offset > this.dataSize) {
            waitOffset = this.dataSize;
        }
        
        // seek
        this.inputStreamReader.waitUntil(waitOffset);
    }
    
    protected void notifyBufferWrite(int len) {
        this.dataSizeFilled += len;
    }
    
    public synchronized long getOffset() {
        return this.offset;
    }
    
    @Override
    public synchronized int available() throws IOException {
        return Math.min(this.dataSizeFilled - this.offset, 64*1024);
    }
    
    @Override
    public synchronized void seek(long offset) throws IOException {
        if(this.offset == offset) {
            return;
        }
        
        if(offset < 0) {
            throw new IOException("cannot seek to negative offset : " + offset);
        }
        
        if(offset >= this.dataSize) {
            this.offset = this.dataSize;
        } else {
            this.offset = (int)offset;
        }
        
        waitData(this.offset);
    }
    
    @Override
    public synchronized long skip(long size) throws IOException {
        if(size <= 0) {
            return 0;
        }
        
        if(this.offset >= this.dataSize) {
            return 0;
        }
        
        long lavailable = this.dataSize - this.offset;
        if(size >= lavailable) {
            this.offset = this.dataSize;
            waitData(this.offset);
            return lavailable;
        } else {
            this.offset += size;
            waitData(this.offset);
            return size;
        }
    }
    
    @Override
    public synchronized int read() throws IOException {
        if(this.offset >= this.dataSize) {
            return -1;
        }
        
        waitData(this.offset + 1);
        
        int ch = (int) this.dataBuffer[this.offset];
        
        this.offset++;
        return ch;
    }
    
    @Override
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        if(this.offset >= this.dataSize) {
            return -1;
        }
        
        if(bytes == null) {
            throw new IllegalArgumentException("bytes is null");
        }
        
        if(off < 0) {
            throw new IllegalArgumentException("off is negative");
        }
        
        if(len < 0) {
            throw new IllegalArgumentException("len is negative");
        }
            
        int available = this.dataSize - this.offset;
        int toRead = Math.min(available, len);
        
        waitData(this.offset + toRead);
        System.arraycopy(this.dataBuffer, this.offset, bytes, off, toRead);
        
        this.offset += toRead;
        return toRead;
    }
    
    @Override
    public synchronized void close() {
        this.offset = 0;
        this.dataSize = 0;
        this.dataSizeFilled = 0;
        
        if(this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (Exception ex) {
            } finally {
                this.inputStream = null;
            }
        }
        
        if(this.readThread != null) {
            if(this.readThread.isAlive()) {
                this.readThread.interrupt();
            }
            
            this.readThread = null;
        }
        
        this.dataBuffer = null;
    }
}
