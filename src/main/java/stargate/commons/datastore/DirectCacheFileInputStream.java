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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;
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
    private static final int BUFFER_SIZE = 32 * 1024;
    
    private long beginOffset;
    private long offset;
    private long size;
    private File cacheFile;
    private long lastCacheFileLength;
    private BufferedInputStream cacheInputStream;
    
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
                FileInputStream is = new FileInputStream(this.cacheFile);
                if(this.beginOffset > 0) {
                    is.getChannel().position(this.beginOffset);
                }
                this.cacheInputStream = new BufferedInputStream(is, BUFFER_SIZE);
                
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
            if(!this.cacheFile.exists()) {
                WatchService fsWatcher = FileSystems.getDefault().newWatchService();
                boolean created = false;
                
                Path dir = Paths.get(this.cacheFile.getParentFile().toURI());
                dir.register(fsWatcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
                
                while(true) {
                    WatchKey key;
                    try {
                        key = fsWatcher.poll(5, TimeUnit.SECONDS);
                        
                        curTime = DateTimeUtils.getTimestamp();
                        if(DateTimeUtils.timeElapsedSec(beginTime, curTime, WAIT_TIMEOUT_SEC)) {
                            LOG.error(String.format("Timeout occurred while waiting data - %s", this.cacheFile.toString()));
                            
                            if(!this.cacheFile.exists()) {
                                LOG.error(String.format("File does not exist - %s", this.cacheFile.toString()));
                            }
                            
                            if(!this.cacheFile.isFile()) {
                                LOG.error(String.format("File is not a file - %s", this.cacheFile.toString()));
                            }
                            
                            for(String f : this.cacheFile.getParentFile().list()) {
                                LOG.error(String.format("File %s is in a dir %s", f, this.cacheFile.getParentFile().toString()));
                            }
                            
                            throw new IOException(String.format("cannot open data (%s) after %d sec waiting", this.cacheFile.toString(), WAIT_TIMEOUT_SEC));
                        }
                        
                        if(key == null) {
                            continue;
                        }
                    } catch (InterruptedException ex) {
                        LOG.error(ex);
                        throw new IOException(ex);
                    }
                    
                    for(WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                    
                        if(kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();
                        
                        if(filename.equals(this.cacheFile)) {
                            if(kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                LOG.info(String.format("File is created or updated - %s, %s", filename.toString(), kind.name()));
                                created = true;
                            } else {
                                LOG.info(String.format("Unknown event raised from filesystem - %s, %s", filename.toString(), kind.name()));
                            }
                        }
                        
                        if(created) {
                            break;
                        }
                    }
                    
                    if(created) {
                        break;
                    }
                    
                    boolean valid = key.reset();
                    if(!valid) {
                        LOG.error("key is not valid");
                        break;
                    }
                }
                
                fsWatcher.close();
            }
        }
        
        if(this.lastCacheFileLength >= offset) {
            return;
        }
        
        // update and try again
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
        
        if(offset == this.offset) {
            return;
        }
        
        safeInitCacheFileInputStream();
        
        long seekable = (int) Math.min(this.size, offset);
        // wait
        waitData(this.beginOffset + seekable);
        
        if(this.cacheInputStream != null) {
            this.cacheInputStream.close();
            
            try {
                FileInputStream is = new FileInputStream(this.cacheFile);
                is.getChannel().position(this.beginOffset + seekable);
                this.cacheInputStream = new BufferedInputStream(is, BUFFER_SIZE);
            } catch (FileNotFoundException ex) {
                LOG.error(ex);
                throw new IOException(ex);
            }
        }
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
