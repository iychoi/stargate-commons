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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author iychoi
 */
public class IPUtils {
    private static String cached_public_ip_address = null;
    private static HashSet<String> cached_dns_host_names = new HashSet<String>();
    private static HashSet<String> cached_nic_host_names = new HashSet<String>();
    private static HashSet<String> cached_dns_ip_addresses = new HashSet<String>();
    private static HashSet<String> cached_nic_ip_addresses = new HashSet<String>();
    
    private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    
    private static final String DOMAINNAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
    
    private static Pattern IP_pattern = Pattern.compile(IPADDRESS_PATTERN);
    private static Pattern Domain_pattern = Pattern.compile(DOMAINNAME_PATTERN);
    
    private static boolean isMeaningfulHostAddress(String address) {
        if(address == null || address.isEmpty()) {
            return false;
        }
        
        if(address.equalsIgnoreCase("localhost")) {
            return false;
        } else if(address.equalsIgnoreCase("ip6-localhost")) {
            return false;
        } else if(address.equalsIgnoreCase("127.0.0.1")) {
            return false;
        } else if(address.indexOf(":") > 0) {
            return false;
        }
        return true;
    }
    
    private static void fillCache() throws IOException {
        if(cached_dns_host_names.isEmpty() || cached_dns_ip_addresses.isEmpty()) {
            // DNS
            InetAddress localHost = InetAddress.getLocalHost();
        
            String hostName = localHost.getHostName();
            if(isMeaningfulHostAddress(hostName)) {
                if(!cached_dns_host_names.contains(hostName)) {
                    cached_dns_host_names.add(hostName);
                }
            }
            
            String hostAddress = localHost.getHostAddress();
            if(isMeaningfulHostAddress(hostAddress)) {
                if(!cached_dns_ip_addresses.contains(hostAddress)) {
                    cached_dns_ip_addresses.add(hostAddress);
                }
            }
            
            String canonicalHostName = localHost.getCanonicalHostName();
            if(isMeaningfulHostAddress(canonicalHostName)) {
                if(!cached_dns_host_names.contains(canonicalHostName)) {
                    cached_dns_host_names.add(canonicalHostName);
                }
            }
        }
        
        if(cached_nic_host_names.isEmpty() || cached_nic_ip_addresses.isEmpty()) {
            // NIC
            try {
                Enumeration e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements()) {
                    NetworkInterface n = (NetworkInterface) e.nextElement();
                    Enumeration ee = n.getInetAddresses();
                    while (ee.hasMoreElements()) {
                        InetAddress i = (InetAddress) ee.nextElement();
                        if(!i.isLoopbackAddress()) {
                            String hostAddress = i.getHostAddress();
                            if(isMeaningfulHostAddress(hostAddress)) {
                                if(!cached_nic_ip_addresses.contains(hostAddress)) {
                                    cached_nic_ip_addresses.add(hostAddress);
                                }
                            }

                            String hostName = i.getHostName();
                            if(isMeaningfulHostAddress(hostName)) {
                                if(!cached_nic_host_names.contains(hostName)) {
                                    cached_nic_host_names.add(hostName);
                                }
                            }

                            String canonicalHostName = i.getCanonicalHostName();
                            if(isMeaningfulHostAddress(canonicalHostName)) {
                                if(!cached_nic_host_names.contains(canonicalHostName)) {
                                    cached_nic_host_names.add(canonicalHostName);
                                }
                            }
                        }
                    }
                }
            } catch (SocketException ex) {
                throw new IOException(ex);
            }
        }
        
        if(cached_public_ip_address == null) {
            try {
                URL whatismyip = new URL("http://checkip.amazonaws.com");
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                    String ip = in.readLine().trim();
                    
                    if(isIPAddress(ip)) {
                        // cache
                        cached_public_ip_address = ip;
                    } else {
                        throw new IOException(String.format("Cannot obtain correct public IP address - got %s", ip));
                    }
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                        }
                    }
                }
            } catch (MalformedURLException ex) {
                throw new IOException(ex);
            }
        }
    }
    
    public static Collection<String> getAllHostNames() throws IOException {
        fillCache();
        
        HashSet<String> hostnames = new HashSet<String>();
        hostnames.addAll(cached_dns_host_names);
        hostnames.addAll(cached_nic_host_names);
        hostnames.addAll(cached_dns_ip_addresses);
        hostnames.addAll(cached_nic_ip_addresses);
        String publicIPAddress = getPublicIPAddress();
        if(!hostnames.contains(publicIPAddress)) {
            hostnames.add(publicIPAddress);
        }
        
        return Collections.unmodifiableSet(hostnames);
    }
    
    public static Collection<String> getHostNames() throws IOException {
        fillCache();
        
        HashSet<String> hostnames = new HashSet<String>();
        hostnames.addAll(cached_dns_host_names);
        hostnames.addAll(cached_nic_host_names);
        
        return Collections.unmodifiableSet(hostnames);
    }
    
    public static Collection<String> getDNSHostNames() throws IOException {
        fillCache();
        
        return Collections.unmodifiableSet(cached_dns_host_names);
    }
    
    public static Collection<String> getNICHostNames() throws IOException {
        fillCache();
        
        return Collections.unmodifiableSet(cached_nic_host_names);
    }
    
    public static Collection<String> getIPAddresses() throws IOException {
        fillCache();
        
        HashSet<String> ipaddresses = new HashSet<String>();
        ipaddresses.addAll(cached_dns_ip_addresses);
        ipaddresses.addAll(cached_nic_ip_addresses);
        String publicIPAddress = getPublicIPAddress();
        if(!ipaddresses.contains(publicIPAddress)) {
            ipaddresses.add(publicIPAddress);
        }
        
        return Collections.unmodifiableSet(ipaddresses);
    }
    
    public static Collection<String> getDNSIPAddresses() throws IOException {
        fillCache();
        
        return Collections.unmodifiableSet(cached_dns_ip_addresses);
    }
    
    public static Collection<String> getNICIPAddresses() throws IOException {
        fillCache();
        
        return Collections.unmodifiableSet(cached_nic_ip_addresses);
    }
    
    public static String getPublicIPAddress() throws IOException {
        fillCache();
        
        return cached_public_ip_address;
    }
    
    public static boolean isIPAddress(String address) {
        if(address == null || address.isEmpty()) {
            throw new IllegalArgumentException("address is null or empty");
        }
        
        Matcher matcher = IP_pattern.matcher(address);
        return matcher.matches();
    }
    
    public static boolean isDomainName(String address) {
        if(address == null || address.isEmpty()) {
            throw new IllegalArgumentException("address is null or empty");
        }
        
        Matcher matcher = Domain_pattern.matcher(address);
        return matcher.matches();
    }

    public static boolean isPublicIPAddress(String address) {
        if(address == null || address.isEmpty()) {
            throw new IllegalArgumentException("address is null or empty");
        }
        
        Matcher matcher = IP_pattern.matcher(address);
        if(matcher.matches()) {
            String first = matcher.group(1);
            //String second = matcher.group(2);
            
            int f = Integer.parseInt(first);
            //int s = Integer.parseInt(second);
            
            if(f == 192 || f == 172 || f == 10) {
                return false;
            }
            
            return true;
        }
        
        return false;
    }
    
    public static boolean isLocalIPAddress(String address) throws IOException {
        if(address == null || address.isEmpty()) {
            throw new IllegalArgumentException("address is null or empty");
        }
        
        Collection<String> localhostAddress = IPUtils.getAllHostNames();
        Set<String> localhostAddrSet = new HashSet<String>();
        localhostAddrSet.addAll(localhostAddress);
        
        String hostname = address;
        int pos = hostname.indexOf(":");
        if(pos > 0) {
            hostname = hostname.substring(0, pos);
        }

        if(hostname.equalsIgnoreCase("localhost") || hostname.equals("127.0.0.1")) {
            // localloop
            return true;
        }

        if(localhostAddrSet.contains(hostname)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean containLocalIPAddress(Collection<String> addresses) throws IOException {
        if(addresses == null) {
            throw new IllegalArgumentException("addresses is null");
        }
        
        for(String address : addresses) {
            boolean result = isLocalIPAddress(address);
            if(result) {
                return true;
            }
        }
        return false;
    }
}
