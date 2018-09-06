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
package stargate.commons.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author iychoi
 */
public class HexUtils {

    private static final Log LOG = LogFactory.getLog(HexUtils.class);
    
    public static String toHexString(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : arr) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public static byte[] toBytes(String hex) {
        return toBytes(hex.toCharArray());
    }
    
    public static byte[] toBytes(char[] hex) {
        byte[] raw = new byte[hex.length / 2];
        for (int src = 0, dst = 0; dst < raw.length; ++dst) {
            int hi = Character.digit(hex[src++], 16);
            int lo = Character.digit(hex[src++], 16);
            if ((hi < 0) || (lo < 0)) {
                throw new IllegalArgumentException();
            }
            raw[dst] = (byte) (hi << 4 | lo);
        }
        return raw;
    }
}
