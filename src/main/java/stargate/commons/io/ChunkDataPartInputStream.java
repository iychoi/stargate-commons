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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import stargate.commons.utils.IOUtils;

/**
 *
 * @author iychoi
 */
public class ChunkDataPartInputStream extends InputStream implements Closeable {

    private byte[] chunkData;
    private int offset;
    private long chunkStartOffset;
    private int chunkSize;
    private int chunkPartNo;
    private int chunkPartSize;
    
    public ChunkDataPartInputStream(InputStream is, long chunkStartOffset, int chunkSize, int chunkPartNo, int chunkPartSize) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        if(chunkStartOffset < 0) {
            throw new IllegalArgumentException("chunkStartOffset is negative");
        }
        
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is negative");
        }
        
        if(chunkPartNo < 0) {
            throw new IllegalArgumentException("chunkPartNo is negative");
        }
        
        if(chunkPartSize < 0) {
            throw new IllegalArgumentException("chunkPartSize is negative");
        }
        
        this.chunkData = IOUtils.toByteArray(is, chunkPartSize);
        is.close();
        this.chunkStartOffset = chunkStartOffset;
        this.chunkSize = chunkSize;
        this.chunkPartNo = chunkPartNo;
        this.chunkPartSize = chunkPartSize;
        this.offset = 0;
    }

    public int getOffset() {
        return this.offset;
    }
    
    public long getChunkStartOffset() {
        return this.chunkStartOffset;
    }

    public int getChunkSize() {
        return this.chunkSize;
    }
    
    public long getChunkPartStartOffset() {
        return this.chunkStartOffset + (this.chunkPartSize * this.chunkPartNo);
    }
    
    public int getChunkPartSizeActual() {
        return Math.min(this.chunkSize - (this.chunkPartSize * this.chunkPartNo), this.chunkPartSize);
    }
    
    public int getChunkPartNo() {
        return this.chunkPartNo;
    }
    
    public int getChunkPartSize() {
        return this.chunkPartSize;
    }

    public boolean containsOffset(long offset) {
        if (this.chunkStartOffset + (this.chunkPartSize * this.chunkPartNo) <= offset &&
            (this.chunkStartOffset + this.chunkSize) > offset &&
                this.chunkStartOffset + (this.chunkPartSize * this.chunkPartNo) + this.chunkPartSize > offset) {
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if(this.offset >= this.chunkData.length) {
            return -1;
        }
        
        int read = this.chunkData[this.offset];
        this.offset++;
        return read;
    }
    
    @Override
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        if(bytes == null) {
            throw new IOException("bytes is null");
        }
        
        if(off < 0) {
            throw new IOException("off is negative");
        }
        
        if(len < 0) {
            throw new IOException("len is negative");
        }
        
        if(this.offset >= this.chunkData.length) {
            return -1;
        }
        
        int readLen = Math.min(len, this.chunkData.length - this.offset);
        System.arraycopy(this.chunkData, this.offset, bytes, off, readLen);
        return readLen;
    }
    
    @Override
    public synchronized int read(byte[] bytes) throws IOException {
        if(bytes == null) {
            throw new IOException("bytes is null");
        }
        
        return read(bytes, 0, bytes.length);
    }
    
    @Override
    public synchronized long skip(long skip) throws IOException {
        if(this.offset >= this.chunkData.length) {
            return 0;
        }
        
        if(skip < 0) {
            throw new IOException("cannot skip to negative");
        }
        
        int skippable = (int) Math.min(this.chunkData.length - this.offset, skip);
        this.offset += skippable;
        return skippable;
    }
    
    public synchronized void seek(long offset) throws IOException {
        if(offset < 0) {
            throw new IOException("cannot seek to negative");
        }
        
        int seekable = (int) Math.min(this.chunkData.length, offset);
        this.offset = seekable;
    }
    
    @Override
    public synchronized int available() throws IOException {
        return this.chunkData.length - this.offset;
    }

    @Override
    public synchronized void close() throws IOException {
        this.chunkData = null;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void mark(int readLimit) {
        // Do nothing
    }

    @Override
    public void reset() throws IOException {
        throw new IOException("Mark not supported");
    }
}
