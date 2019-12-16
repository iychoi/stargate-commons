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
package stargate.commons.datastore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import stargate.commons.io.AbstractSeekableInputStream;
import stargate.commons.utils.DateTimeUtils;

/**
 *
 * @author iychoi
 */
public class DirectCacheFileInputStream extends AbstractSeekableInputStream {

    private static final Log LOG = LogFactory.getLog(DirectCacheFileInputStream.class);
    
    private static final int WAIT_TIMEOUT_SEC = 300;
    private static final int POLLING_INTERVAL_MSEC = 100;
    
    private long beginOffset;
    private long offset;
    private long size;
    private File cacheFile;
    private long lastCacheFileLength;
    private FileInputStream cacheInputStream;
        
    public DirectCacheFileInputStream(File cacheFile, long beginOffset, int size) throws IOException {
        if(cacheFile == null) {
            throw new IllegalArgumentException("cacheFile is null");
        }
        
        if(beginOffset < 0) {
            throw new IllegalArgumentException("beginOffset is negative");
        }
        
        if(size < 0) {
            throw new IllegalArgumentException("size is negative");
        }
        
        this.beginOffset = beginOffset;
        this.offset = 0;
        this.size = size;
        
        this.cacheFile = cacheFile;
        this.lastCacheFileLength = -1;
        this.cacheInputStream = null;
    }
    
    private synchronized void safeInitCacheFileInputStream() throws IOException {
        if(this.cacheInputStream == null) {
            // wait
            waitData(this.beginOffset);
            
            try {
                this.cacheInputStream = new FileInputStream(this.cacheFile);
                if(this.beginOffset > 0) {
                    this.cacheInputStream.getChannel().position(this.beginOffset);
                }
            } catch (FileNotFoundException ex) {
                LOG.error(ex);
                throw new IOException(ex);
            }
        }
    }
    
    private synchronized void waitData(long offset) throws IOException {
        if(this.lastCacheFileLength >= offset) {
            return;
        }
        
        long beginTime = DateTimeUtils.getTimestamp();
        long curTime = beginTime;
        
        // check file existance
        if(this.lastCacheFileLength < 0) {
            while(!this.cacheFile.exists()) {
                if(DateTimeUtils.timeElapsedSec(beginTime, curTime, WAIT_TIMEOUT_SEC)) {
                    // timeout
                    LOG.error("Timeout occurred while waiting data");
                    throw new IOException(String.format("cannot open data after %d sec waiting", WAIT_TIMEOUT_SEC));
                } else {
                    LOG.info("Wait data");
                    try {
                        Thread.sleep(POLLING_INTERVAL_MSEC);
                    } catch (InterruptedException ex) {
                        throw new IOException(ex);
                    }
                    
                    curTime = DateTimeUtils.getTimestamp();
                }
            }
        }
        
        this.lastCacheFileLength = this.cacheFile.length();
        if(this.lastCacheFileLength >= offset) {
            return;
        }
        
        while(this.lastCacheFileLength < offset) {
            if(DateTimeUtils.timeElapsedSec(beginTime, curTime, WAIT_TIMEOUT_SEC)) {
                // timeout
                LOG.error(String.format("Timeout occurred while reading data at offset %d", offset));
                throw new IOException(String.format("cannot read data at offset - %d after %d sec waiting", offset, WAIT_TIMEOUT_SEC));
            } else {
                LOG.info(String.format("Wait data at offset %d", offset));
                try {
                    Thread.sleep(POLLING_INTERVAL_MSEC);
                } catch (InterruptedException ex) {
                    throw new IOException(ex);
                }

                this.lastCacheFileLength = this.cacheFile.length();
                curTime = DateTimeUtils.getTimestamp();
            }
        }
    }
    
    @Override
    public synchronized int available() throws IOException {
        if(this.cacheInputStream == null) {
            return 0;
        } else {
            return (int) Math.min(this.cacheInputStream.available(), this.size - this.offset);
        }
    }
    
    @Override
    public synchronized long skip(long size) throws IOException {
        if(size <= 0) {
            return 0;
        }
        
        if(this.offset >= this.size) {
            return 0;
        }
        
        safeInitCacheFileInputStream();
        
        long lavailable = this.size - this.offset;
        if(size >= lavailable) {
            this.offset = this.size;
            waitData(this.beginOffset + this.offset);
            this.cacheInputStream.skip(lavailable);
            return lavailable;
        } else {
            this.offset += size;
            waitData(this.beginOffset + this.offset);
            this.cacheInputStream.skip(size);
            return size;
        }
    }

    @Override
    public synchronized long getOffset() throws IOException {
        return this.offset;
    }
    
    @Override
    public synchronized void seek(long offset) throws IOException {
        if(offset < 0) {
            return;
        }
        
        safeInitCacheFileInputStream();
        
        long seekable = (int) Math.min(this.size, offset);
        waitData(this.beginOffset + seekable);
        
        this.cacheInputStream.getChannel().position(this.beginOffset + seekable);
        this.offset = seekable;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if(this.offset >= this.size) {
            return -1;
        }
        
        safeInitCacheFileInputStream();
        
        waitData(this.beginOffset + this.offset + 1);
        
        int ch = this.cacheInputStream.read();
        this.offset++;
        return ch;
    }
    
    @Override
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        if(this.offset >= this.size) {
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
        
        safeInitCacheFileInputStream();
        
        int available = (int) Math.min(this.size - this.offset, len);
        
        waitData(this.beginOffset + this.offset + available);
        int readLen = this.cacheInputStream.read(bytes, off, available);
        if(readLen >= 0) {
            this.offset += readLen;
        }
        
        return readLen;
    }
    
    @Override
    public synchronized void close() throws IOException {
        if(this.cacheInputStream != null) {
            this.cacheInputStream.close();
            this.cacheInputStream = null;
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
