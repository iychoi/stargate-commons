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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import stargate.commons.utils.JsonSerializer;

/**
 *
 * @author iychoi
 */
public class BigKeyValueStoreMetadata {

    private String key;
    private int partNum;
    private long entrySize;
    private byte[] extra;

    public static BigKeyValueStoreMetadata createInstance(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }

        return (BigKeyValueStoreMetadata) JsonSerializer.fromJsonFile(file, BigKeyValueStoreMetadata.class);
    }
    
    public static BigKeyValueStoreMetadata createInstance(String json) throws IOException {
        if(json == null || json.isEmpty()) {
            throw new IllegalArgumentException("json is null or empty");
        }
        
        return (BigKeyValueStoreMetadata) JsonSerializer.fromJson(json, BigKeyValueStoreMetadata.class);
    }
    
    public BigKeyValueStoreMetadata(String key) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty");
        }
        
        this.key = key;
        this.partNum = 0;
        this.entrySize = 0;
        this.extra = null;
    }
    
    public BigKeyValueStoreMetadata(String key, int partNum, long entrySize, byte[] extra) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty");
        }
        
        if(partNum < 0) {
            throw new IllegalArgumentException("partNum is negative");
        }
        
        if(entrySize < 0) {
            throw new IllegalArgumentException("entrySize is negative");
        }
        
        //if(extra == null) {
        //    throw new IllegalArgumentException("extra is null");
        //}
        
        this.key = key;
        this.partNum = partNum;
        this.entrySize = entrySize;
        this.extra = extra;
    }
    
    public BigKeyValueStoreMetadata(String key, int partNum, long entrySize) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty");
        }
        
        if(partNum < 0) {
            throw new IllegalArgumentException("partNum is negative");
        }
        
        if(entrySize < 0) {
            throw new IllegalArgumentException("entrySize is negative");
        }
        
        //if(extra == null) {
        //    throw new IllegalArgumentException("extra is null");
        //}
        
        this.key = key;
        this.partNum = partNum;
        this.entrySize = entrySize;
        this.extra = null;
    }
    
    @JsonProperty("key")
    public String getKey() {
        return this.key;
    }
    
    @JsonProperty("key")
    public void setKey(String key) {
        if(key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key is null or empty");
        }
        
        this.key = key;
    }
    
    @JsonProperty("part_num")
    public int getPartNum() {
        return this.partNum;
    }
    
    @JsonProperty("part_num")
    public void setPartNum(int partNum) {
        if(partNum < 0) {
            throw new IllegalArgumentException("partNum is negative");
        }
        
        this.partNum = partNum;
    }
    
    @JsonProperty("entry_size")
    public long getEntrySize() {
        return this.entrySize;
    }
    
    @JsonProperty("entry_size")
    public void setEntrySize(long entrySize) {
        if(entrySize < 0) {
            throw new IllegalArgumentException("entrySize is negative");
        }
        
        this.entrySize = entrySize;
    }
    
    @JsonProperty("extra")
    public byte[] getExtra() {
        return this.extra;
    }
    
    @JsonProperty("extra")
    public void setExtra(byte[] extra) {
        //if(extra == null) {
        //    throw new IllegalArgumentException("extra is null");
        //}
        
        this.extra = extra;
    }
    
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        
        byte[] keyBytes = this.key.getBytes();
        oos.writeInt(keyBytes.length);
        oos.write(keyBytes);
        
        oos.writeInt(this.partNum);
        oos.writeLong(this.entrySize);
        
        if(this.extra == null) {
            oos.writeInt(0);
        } else {
            oos.writeInt(this.extra.length);
            oos.write(this.extra);
        }
        
        oos.close();
        output.close();
        return output.toByteArray();
    }
    
    @JsonIgnore
    public static BigKeyValueStoreMetadata fromBytes(byte[] buf) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(buf);
        ObjectInputStream ois = new ObjectInputStream(input);
        
        int keyBytesLen = ois.readInt();
        byte[] keyBytes = new byte[keyBytesLen];
        ois.readFully(keyBytes, 0, keyBytesLen);
        String key = new String(keyBytes);
        
        int partNum = ois.readInt();
        long entrySize = ois.readLong();
        
        int extraLen = ois.readInt();
        byte[] extra = null;
        if(extraLen > 0) {
            extra = new byte[extraLen];
            ois.readFully(extra, 0, extraLen);
        }
        
        ois.close();
        input.close();
        
        return new BigKeyValueStoreMetadata(key, partNum, entrySize, extra);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.key);
        hash = 89 * hash + this.partNum;
        hash = 89 * hash + (int) (this.entrySize ^ (this.entrySize >>> 32));
        hash = 89 * hash + Arrays.hashCode(this.extra);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BigKeyValueStoreMetadata other = (BigKeyValueStoreMetadata) obj;
        if (this.partNum != other.partNum) {
            return false;
        }
        if (this.entrySize != other.entrySize) {
            return false;
        }
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Arrays.equals(this.extra, other.extra)) {
            return false;
        }
        return true;
    }
    
    @Override
    @JsonIgnore
    public String toString() {
        return "BigKeyValueStoreMetadata{" + "key=" + key + ", partNum=" + partNum + ", entrySize=" + entrySize + ", extraLen=" + extra.length + '}';
    }
    
    @JsonIgnore
    public String toJson() throws IOException {
        return JsonSerializer.toJson(this);
    }
    
    @JsonIgnore
    public void saveTo(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("file is null");
        }
        
        JsonSerializer.toJsonFile(file, this);
    }
}
