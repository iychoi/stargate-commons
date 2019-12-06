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

/**
 *
 * @author iychoi
 */
public class ChunkDataInputStream extends InputStream implements Closeable {

    private DiskBufferInputStream inputStream;
    private long chunkStartOffset;
    private int chunkSize;
    
    public ChunkDataInputStream(InputStream is, long chunkStartOffset, int chunkSize) throws IOException {
        if(is == null) {
            throw new IllegalArgumentException("is is null");
        }
        
        if(chunkStartOffset < 0) {
            throw new IllegalArgumentException("chunkStartOffset is negative");
        }
        
        if(chunkSize < 0) {
            throw new IllegalArgumentException("chunkSize is negative");
        }
        
        this.inputStream = new DiskBufferInputStream(is, chunkSize);
        this.chunkStartOffset = chunkStartOffset;
        this.chunkSize = chunkSize;
    }

    public DiskBufferInputStream getDiskBufferInputStream() {
        return this.inputStream;
    }
    
    public int getOffset() {
        return (int) this.inputStream.getOffset();
    }
    
    public long getChunkStartOffset() {
        return this.chunkStartOffset;
    }

    public int getChunkSize() {
        return this.chunkSize;
    }

    public boolean containsOffset(long offset) {
        if (this.chunkStartOffset <= offset &&
            (this.chunkStartOffset + this.chunkSize) > offset) {
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized int read() throws IOException {
        return this.inputStream.read();
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
    
    public synchronized void seek(long offset) throws IOException {
        this.inputStream.seek(offset);
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
