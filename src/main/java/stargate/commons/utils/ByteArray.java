package stargate.commons.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Arrays;
import java.util.Comparator;

public class ByteArray implements Comparable<ByteArray> {

    public static final ByteArrayComparator BYTE_ARRAY_COMPARATOR = new ByteArrayComparator();

    private byte[] array;
    
    public ByteArray(byte[] array) {
        this.array = array;
    }

    public byte[] getArray() {
        return array;
    }

    public int getLength() {
        return array.length;
    }
    
    public byte getAdjusted(int pos) {
        return array[pos];
    }
    
    @Override
    public int compareTo(ByteArray o) {
        return BYTE_ARRAY_COMPARATOR.compare(this, o);
    }

    @Override
    public int hashCode() {
        return array != null ? Arrays.hashCode(array) : 0;
    }

    @Override
    public String toString() {
        return "ByteArray{"
                + "array=" + Arrays.toString(array)
                + '}';
    }

    public static boolean equals(ByteArray array1, ByteArray array2) {
        if (array1 == null) {
            if (array2 == null) {
                return true;
            } else {
                return false;
            }
        }
        if (array2 == null) {
            return false;
        }
        if (array1.getArray() == null) {
            if (array2.getArray() == null) {
                return true;
            } else {
                return false;
            }
        }

        if (array2.getArray() == null) {
            return false;
        }

        return Arrays.equals(array1.getArray(), array2.getArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ByteArray)) {
            return false;
        }

        final ByteArray that = (ByteArray) o;

        return ByteArray.equals(this, that);
    }

    private static class ByteArrayComparator implements Comparator<ByteArray> {

        @Override
        public int compare(ByteArray o1, ByteArray o2) {
            if (o1 == null) {
                if (o2 == null) {
                    return 0;
                } else {
                    return -1;
                }
            }

            if (o2 == null) {
                return 1;
            }

            if (o1.getArray() == null) {
                if (o2.getArray() == null) {
                    return 0;
                } else {
                    return -1;
                }
            }

            if (o2.getArray() == null) {
                return 1;
            }

            int array1Length = o1.getLength();
            int array2Length = o2.getLength();

            int length = Math.min(array1Length, array2Length);

            for (int i = 0; i < length; i++) {
                if (o1.getAdjusted(i) < o2.getAdjusted(i)) {
                    return -1;
                } else if (o1.getAdjusted(i) > o2.getAdjusted(i)) {
                    return 1;
                }
            }

            if (array1Length < array2Length) {
                return -1;
            } else if (array1Length > array2Length) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
