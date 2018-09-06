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

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

/**
 *
 * @author iychoi
 */
public class NameUtils {
    
    private static String cached_machine_name;
    
    public static String generateRandomName(int length) {
        Random random = new Random();
        
        if(length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<length;i++) {
            int rnd = random.nextInt(10+'z'-'a');
            if(rnd < 10) {
                sb.append('0' + rnd);
            } else {
                sb.append('a' + rnd);
            }
        }
        
        return sb.toString();
    }
    
    public static String generateMachineName() throws IOException {
        if(cached_machine_name != null && !cached_machine_name.isEmpty()) {
            return cached_machine_name;
        } else {
            try {
                Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = nis.nextElement();
                    if(ni.getHardwareAddress() != null) {
                        return HexUtils.toHexString(ni.getHardwareAddress());
                    }
                }
                
                throw new IOException("unable to find Network interface");
            } catch (SocketException ex) {
                throw new IOException(ex);
            }
        }
    }
}
