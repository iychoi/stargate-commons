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
import stargate.commons.datastore.BigKeyValueStoreUtils;

/**
 *
 * @author iychoi
 */
public class UnrewindableChunkDataPartInputStream extends AbstractSeekableInputStream {

    private InputStream inputStream;
    private long chunkStartOffset;
    private int chunkSize;
    private int partSize;
    private int partNo;
    private long partStartOffsetInChunk;
    private int actualPartSize;
    private long offset;
    
    public UnrewindableChunkDataPartInputStream(InputStream is, long chunkStartOffset, int chunkSize, int chunkPartNo, int chunkPartSize) throws IOException {
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
        
        this.inputStream = is;
        
        this.chunkStartOffset = chunkStartOffset;
        this.chunkSize = chunkSize;
        this.partNo = chunkPartNo;
        this.partSize = chunkPartSize;
        this.offset = 0;
        
        this.partStartOffsetInChunk = BigKeyValueStoreUtils.getPartStartOffset(this.partSize, this.partNo);
        this.actualPartSize = BigKeyValueStoreUtils.getPartSize(this.chunkSize, this.partSize, this.partNo);
    }
    
    public long getChunkStartOffset() {
        return this.chunkStartOffset;
    }
    
    public long getPartStartOffsetInChunk() {
        return this.partStartOffsetInChunk;
    }

    public int getChunkSize() {
        return this.chunkSize;
    }
    
    public int getChunkPartNo() {
        return this.partNo;
    }
    
    public int getChunkPartSize() {
        return this.partSize;
    }
    
    public int getActualChunkPartSize() {
        return this.actualPartSize;
    }

    public boolean containsOffset(long offset) {
        if (this.chunkStartOffset + this.partStartOffsetInChunk <= offset &&
            (this.chunkStartOffset + this.partStartOffsetInChunk + this.partSize) > offset) {
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized int read() throws IOException {
        int r = this.inputStream.read();
        if(r >= 0) {
            this.offset++;
        }
        return r;
    }
        
    @Override
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        int read = this.inputStream.read(bytes, off, len);
        if(read >= 0) {
            this.offset += read;
        }
        return read;
    }
        
    @Override
    public synchronized int read(byte bytes[]) throws IOException {
        int read = this.inputStream.read(bytes);
        if(read >= 0) {
            this.offset += read;
        }
        return read;
    }
        
    @Override
    public synchronized long skip(long skip) throws IOException {
        long read = this.inputStream.skip(skip);
        if(read >= 0) {
            this.offset += read;
        }
        return read;
    }

    @Override
    public long getOffset() throws IOException {
        return this.offset;
    }

    @Override
    public synchronized void seek(long offset) throws IOException {
        if(this.offset <= offset) {
            long read = this.inputStream.skip(offset - this.offset);
            if(read >= 0) {
                this.offset += read;
            }
            return;
        } else {
            throw new UnsupportedOperationException("Seek backword is not supported");
        }
    }
    
    @Override
    public synchronized int available() throws IOException {
        return this.inputStream.available();
    }

    @Override
    public synchronized void close() throws IOException {
        if(this.inputStream != null) {
            this.inputStream.close();
            this.inputStream = null;
        }
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
