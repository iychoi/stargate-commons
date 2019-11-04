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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import stargate.commons.utils.IOUtils;
import stargate.commons.utils.TempFileUtils;

/**
 *
 * @author iychoi
 */
public class DiskBufferInputStream extends InputStream {

    private static final int MAX_BUFFER_SIZE = 4 * 1024 * 1024; // 4MB
    private static final int DEFAULT_BUFFER_SIZE = 16 * 1024; // 16KB
    
    
    private byte[] buffer;
    private int bufferSize = 0;
    private long bufferStartOffset = 0;
    private long offset = 0;
    private long dataSize = 0;
    private File tempFile;
    private FileInputStream fileInputStream;
    
    public DiskBufferInputStream(InputStream is, int dataSize) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        if(dataSize < 0) {
            throw new IllegalArgumentException("dataSize is negative");
        }
        
        initialize(is, dataSize);
    }
    
    private void initialize(byte[] inputData, int dataSize, boolean copyData) throws IOException {
        if(dataSize < MAX_BUFFER_SIZE) {
            // hold all data in memory
            this.bufferSize = dataSize;
            if(copyData) {
                this.buffer = new byte[dataSize];
                System.arraycopy(inputData, 0, this.buffer, 0, dataSize);
            } else {
                this.buffer = inputData;
            }
            this.tempFile = null;
        } else {
            // use disk
            this.bufferSize = DEFAULT_BUFFER_SIZE;
            this.buffer = new byte[this.bufferSize];
            
            System.arraycopy(inputData, 0, this.buffer, 0, this.bufferSize);
            
            // save to a temp file
            if(TempFileUtils.makeTempRoot()) {
                this.tempFile = TempFileUtils.createTempFile("BUFFIS", "SGFS");
                IOUtils.writeToFile(inputData, this.tempFile);
                this.fileInputStream = new FileInputStream(this.tempFile);
            } else {
                throw new IOException("Failed to create a temp root dir");
            }
        }
        
        this.bufferStartOffset = 0;
        this.offset = 0;
        this.dataSize = dataSize;
    }
    
    private void initialize(InputStream is, int dataSize) throws IOException {
        if(dataSize < MAX_BUFFER_SIZE) {
            byte[] inputData = IOUtils.toByteArray(is);
            // hold all data in memory
            this.bufferSize = dataSize;
            this.buffer = inputData;
            this.tempFile = null;
        } else {
            // use disk
            this.bufferSize = DEFAULT_BUFFER_SIZE;
            this.buffer = new byte[this.bufferSize];
            byte[] readBuffer = new byte[this.bufferSize];
            
            // save to a temp file
            if(!TempFileUtils.makeTempRoot()) {
                throw new IOException("Failed to create a temp root dir");
            }
            
            this.tempFile = TempFileUtils.createTempFile("BUFFIS", "SGFS");
            FileOutputStream fos = new FileOutputStream(this.tempFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            
            int read;
            int offset = 0;
            int bufferOffset = 0;
            while ((read = is.read(readBuffer, 0, this.bufferSize)) > 0) {
                if(bufferOffset < this.bufferSize) {
                    // load first page
                    System.arraycopy(this.buffer, bufferOffset, readBuffer, 0, Math.min(read, this.bufferSize - bufferOffset));
                    bufferOffset += read;
                }
                
                // write to a file
                bos.write(readBuffer, 0, read);
                offset += read;
            }
            
            bos.close();
            
            this.fileInputStream = new FileInputStream(this.tempFile);
        }
        
        this.bufferStartOffset = 0;
        this.offset = 0;
        this.dataSize = dataSize;
    }
    
    private synchronized void loadBuffer() throws IOException {
        if(this.offset >= this.dataSize) {
            return;
        }
        
        long newBufferStartOffset = (this.offset / this.bufferSize) * this.bufferSize;
        if(this.bufferStartOffset != newBufferStartOffset) {
            this.bufferStartOffset = newBufferStartOffset;
            
            if(this.fileInputStream != null) {
                // seek
                this.fileInputStream.getChannel().position(this.bufferStartOffset);
                
                this.fileInputStream.read(this.buffer, 0, bufferSize);
            } else {
                throw new IOException("RandomAccesFile is null");
            }
        }
    }
    
    public synchronized long getPos() throws IOException {
        return this.offset;
    }
    
    @Override
    public synchronized int available() throws IOException {
        long newBufferStartOffset = (this.offset / this.bufferSize) * this.bufferSize;
        if(this.bufferStartOffset != newBufferStartOffset) {
            return 0;
        }
        
        int bufferOffset = (int)(this.offset - this.bufferStartOffset);
        int remaining = (int)(this.dataSize - this.bufferStartOffset);
        return Math.min(this.bufferSize - bufferOffset, remaining);
    }
    
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
            return lavailable;
        } else {
            this.offset += size;
            return size;
        }
    }
    
    @Override
    public synchronized int read() throws IOException {
        if(this.offset >= this.dataSize) {
            return -1;
        }
        
        loadBuffer();
        
        int offsetInBuffer = (int)(this.offset - this.bufferStartOffset);
        byte ch = this.buffer[offsetInBuffer];
        
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
        int remain = len;
        if(len > lavailable) {
            remain = (int) lavailable;
        }
        
        int copied = remain;
        int outputBufferOffset = off;
        
        while(remain > 0) {
            loadBuffer();

            int bufferOffset = (int)(this.offset - this.bufferStartOffset);
            int bufferLength = (int) Math.min(this.bufferSize - bufferOffset, remain);
            
            System.arraycopy(this.buffer, bufferOffset, bytes, outputBufferOffset, bufferLength);
            this.offset += bufferLength;
            outputBufferOffset += bufferLength;
            remain -= bufferLength;
        }
        
        return copied;
    }
    
    @Override
    public synchronized void close() {
        this.buffer = null;
        this.offset = 0;
        this.dataSize = 0;
        
        if(this.fileInputStream != null) {
            try {
                this.fileInputStream.close();
            } catch (IOException ex) {
            } finally {
                this.fileInputStream = null;
            }
        }
        
        if(this.tempFile != null) {
            this.tempFile.delete();
            this.tempFile = null;
        }
    }
}
