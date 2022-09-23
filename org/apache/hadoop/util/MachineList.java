// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import com.google.common.net.InetAddresses;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Collection;
import org.apache.commons.net.util.SubnetUtils;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;

public class MachineList
{
    public static final Logger LOG;
    public static final String WILDCARD_VALUE = "*";
    private final boolean all;
    private final Set<String> ipAddresses;
    private final List<SubnetUtils.SubnetInfo> cidrAddresses;
    private final Set<String> hostNames;
    private final InetAddressFactory addressFactory;
    
    public MachineList(final String hostEntries) {
        this(StringUtils.getTrimmedStringCollection(hostEntries));
    }
    
    public MachineList(final Collection<String> hostEntries) {
        this(hostEntries, InetAddressFactory.S_INSTANCE);
    }
    
    public MachineList(final Collection<String> hostEntries, final InetAddressFactory addressFactory) {
        this.addressFactory = addressFactory;
        if (hostEntries != null) {
            if (hostEntries.size() == 1 && hostEntries.contains("*")) {
                this.all = true;
                this.ipAddresses = null;
                this.hostNames = null;
                this.cidrAddresses = null;
            }
            else {
                this.all = false;
                final Set<String> ips = new HashSet<String>();
                final List<SubnetUtils.SubnetInfo> cidrs = new LinkedList<SubnetUtils.SubnetInfo>();
                final Set<String> hosts = new HashSet<String>();
                for (final String hostEntry : hostEntries) {
                    if (hostEntry.indexOf("/") > -1) {
                        try {
                            final SubnetUtils subnet = new SubnetUtils(hostEntry);
                            subnet.setInclusiveHostCount(true);
                            cidrs.add(subnet.getInfo());
                            continue;
                        }
                        catch (IllegalArgumentException e) {
                            MachineList.LOG.warn("Invalid CIDR syntax : " + hostEntry);
                            throw e;
                        }
                    }
                    if (InetAddresses.isInetAddress(hostEntry)) {
                        ips.add(hostEntry);
                    }
                    else {
                        hosts.add(hostEntry);
                    }
                }
                this.ipAddresses = ((ips.size() > 0) ? ips : null);
                this.cidrAddresses = ((cidrs.size() > 0) ? cidrs : null);
                this.hostNames = ((hosts.size() > 0) ? hosts : null);
            }
        }
        else {
            this.all = false;
            this.ipAddresses = null;
            this.hostNames = null;
            this.cidrAddresses = null;
        }
    }
    
    public boolean includes(final String ipAddress) {
        if (this.all) {
            return true;
        }
        if (ipAddress == null) {
            throw new IllegalArgumentException("ipAddress is null.");
        }
        if (this.ipAddresses != null && this.ipAddresses.contains(ipAddress)) {
            return true;
        }
        if (this.cidrAddresses != null) {
            for (final SubnetUtils.SubnetInfo cidrAddress : this.cidrAddresses) {
                if (cidrAddress.isInRange(ipAddress)) {
                    return true;
                }
            }
        }
        if (this.hostNames != null) {
            try {
                final InetAddress hostAddr = this.addressFactory.getByName(ipAddress);
                if (hostAddr != null && this.hostNames.contains(hostAddr.getCanonicalHostName())) {
                    return true;
                }
            }
            catch (UnknownHostException ex) {}
            for (final String host : this.hostNames) {
                InetAddress hostAddr;
                try {
                    hostAddr = this.addressFactory.getByName(host);
                }
                catch (UnknownHostException e) {
                    continue;
                }
                if (hostAddr.getHostAddress().equals(ipAddress)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @VisibleForTesting
    public Collection<String> getCollection() {
        final Collection<String> list = new ArrayList<String>();
        if (this.all) {
            list.add("*");
        }
        else {
            if (this.ipAddresses != null) {
                list.addAll(this.ipAddresses);
            }
            if (this.hostNames != null) {
                list.addAll(this.hostNames);
            }
            if (this.cidrAddresses != null) {
                for (final SubnetUtils.SubnetInfo cidrAddress : this.cidrAddresses) {
                    list.add(cidrAddress.getCidrSignature());
                }
            }
        }
        return list;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MachineList.class);
    }
    
    public static class InetAddressFactory
    {
        static final InetAddressFactory S_INSTANCE;
        
        public InetAddress getByName(final String host) throws UnknownHostException {
            return InetAddress.getByName(host);
        }
        
        static {
            S_INSTANCE = new InetAddressFactory();
        }
    }
}
