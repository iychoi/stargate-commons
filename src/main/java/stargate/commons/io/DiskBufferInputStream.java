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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import stargate.commons.utils.TempFileUtils;

/**
 *
 * @author iychoi
 */
public class DiskBufferInputStream extends AbstractSeekableInputStream {

    private static final Log LOG = LogFactory.getLog(DiskBufferInputStream.class);
    
    private long offset = 0;
    private long dataSize = 0;
    
    private File tempFile;
    private FileInputStream fileInputStream;
    private DiskBufferInputStreamReader inputStreamReader;
    private Thread readThread;
    private InputStream inputStream;
    
    public DiskBufferInputStream(InputStream is, int dataSize) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        if(dataSize < 0) {
            throw new IllegalArgumentException("dataSize is negative");
        }
        
        initialize(is, dataSize);
    }
    
    private void initialize(InputStream is, int dataSize) throws IOException {
        // create a temp file
        if(!TempFileUtils.makeTempRoot()) {
            throw new IOException("Failed to create a temp root dir");
        }
        
        this.tempFile = TempFileUtils.createTempFile("BUFFIS", "SGFS");

        FileOutputStream fos = new FileOutputStream(this.tempFile, true);
        fos.getChannel().truncate(0);
        this.inputStream = is;

        this.inputStreamReader = new DiskBufferInputStreamReader(is, fos);
        this.fileInputStream = new FileInputStream(this.tempFile);
        this.readThread = new Thread(this.inputStreamReader);
        this.readThread.start();
        
        this.offset = 0;
        this.dataSize = dataSize;
    }
    
    private synchronized void waitData(long offset) throws IOException {
        if(this.offset >= this.dataSize) {
            return;
        }
        
        long waitOffset = offset;
        if(offset > this.dataSize) {
            waitOffset = this.dataSize;
        }
        
        if(this.fileInputStream != null) {
            // seek
            this.inputStreamReader.waitUntil(waitOffset);
        } else {
            throw new IOException("fileInputStream is null");
        }
    }
    
    public synchronized long getOffset() {
        return this.offset;
    }
    
    @Override
    public synchronized int available() throws IOException {
        return this.fileInputStream.available();
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
            this.offset = offset;
        }
        
        waitData(this.offset);
        this.fileInputStream.getChannel().position(this.offset);
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
            this.fileInputStream.getChannel().position(this.offset);
            return lavailable;
        } else {
            this.offset += size;
            waitData(this.offset);
            this.fileInputStream.getChannel().position(this.offset);
            return size;
        }
    }
    
    @Override
    public synchronized int read() throws IOException {
        if(this.offset >= this.dataSize) {
            return -1;
        }
        
        waitData(this.offset + 1);
        
        int ch = this.fileInputStream.read();
        
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
            
        long lavailable = this.dataSize - this.offset;
        int toRead = (int) Math.min(lavailable, len);
        
        waitData(this.offset + toRead);
        int readLen = this.fileInputStream.read(bytes, off, toRead);
        if(readLen < 0) {
            throw new IOException("EOF found");
        }
        
        this.offset += readLen;
        return readLen;
    }
    
    @Override
    public synchronized void close() {
        this.offset = 0;
        this.dataSize = 0;
        
        if(this.fileInputStream != null) {
            try {
                this.fileInputStream.close();
            } catch (Exception ex) {
            } finally {
                this.fileInputStream = null;
            }
        }
        
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
        
        if(this.tempFile != null) {
            this.tempFile.delete();
            this.tempFile = null;
        }
    }
}
