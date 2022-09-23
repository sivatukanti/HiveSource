// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ipfilter;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.Inet6Address;
import java.math.BigInteger;
import org.jboss.netty.logging.InternalLogger;

public class CIDR6 extends CIDR
{
    private static final InternalLogger logger;
    private BigInteger addressBigInt;
    private final BigInteger addressEndBigInt;
    
    protected CIDR6(final Inet6Address newaddress, final int newmask) {
        this.cidrMask = newmask;
        this.addressBigInt = ipv6AddressToBigInteger(newaddress);
        final BigInteger mask = ipv6CidrMaskToMask(newmask);
        try {
            this.addressBigInt = this.addressBigInt.and(mask);
            this.baseAddress = bigIntToIPv6Address(this.addressBigInt);
        }
        catch (UnknownHostException ex) {}
        this.addressEndBigInt = this.addressBigInt.add(ipv6CidrMaskToBaseAddress(this.cidrMask)).subtract(BigInteger.ONE);
    }
    
    @Override
    public InetAddress getEndAddress() {
        try {
            return bigIntToIPv6Address(this.addressEndBigInt);
        }
        catch (UnknownHostException e) {
            if (CIDR6.logger.isErrorEnabled()) {
                CIDR6.logger.error("invalid ip address calculated as an end address");
            }
            return null;
        }
    }
    
    public int compareTo(final CIDR arg) {
        if (arg instanceof CIDR4) {
            final BigInteger net = ipv6AddressToBigInteger(arg.baseAddress);
            final int res = net.compareTo(this.addressBigInt);
            if (res != 0) {
                return res;
            }
            if (arg.cidrMask == this.cidrMask) {
                return 0;
            }
            if (arg.cidrMask < this.cidrMask) {
                return -1;
            }
            return 1;
        }
        else {
            final CIDR6 o = (CIDR6)arg;
            if (o.addressBigInt.equals(this.addressBigInt) && o.cidrMask == this.cidrMask) {
                return 0;
            }
            final int res = o.addressBigInt.compareTo(this.addressBigInt);
            if (res != 0) {
                return res;
            }
            if (o.cidrMask < this.cidrMask) {
                return -1;
            }
            return 1;
        }
    }
    
    @Override
    public boolean contains(final InetAddress inetAddress) {
        if (inetAddress == null) {
            throw new NullPointerException("inetAddress");
        }
        if (this.cidrMask == 0) {
            return true;
        }
        final BigInteger search = ipv6AddressToBigInteger(inetAddress);
        return search.compareTo(this.addressBigInt) >= 0 && search.compareTo(this.addressEndBigInt) <= 0;
    }
    
    private static BigInteger ipv6CidrMaskToBaseAddress(final int cidrMask) {
        return BigInteger.ONE.shiftLeft(128 - cidrMask);
    }
    
    private static BigInteger ipv6CidrMaskToMask(final int cidrMask) {
        return BigInteger.ONE.shiftLeft(128 - cidrMask).subtract(BigInteger.ONE).not();
    }
    
    private static BigInteger ipv6AddressToBigInteger(final InetAddress addr) {
        byte[] ipv6;
        if (addr instanceof Inet4Address) {
            ipv6 = CIDR.getIpV6FromIpV4((Inet4Address)addr);
        }
        else {
            ipv6 = addr.getAddress();
        }
        if (ipv6[0] == -1) {
            return new BigInteger(1, ipv6);
        }
        return new BigInteger(ipv6);
    }
    
    private static InetAddress bigIntToIPv6Address(final BigInteger addr) throws UnknownHostException {
        final byte[] a = new byte[16];
        final byte[] b = addr.toByteArray();
        if (b.length > 16 && (b.length != 17 || b[0] != 0)) {
            throw new UnknownHostException("invalid IPv6 address (too big)");
        }
        if (b.length == 16) {
            return InetAddress.getByAddress(b);
        }
        if (b.length == 17) {
            System.arraycopy(b, 1, a, 0, 16);
        }
        else {
            final int p = 16 - b.length;
            System.arraycopy(b, 0, a, p, b.length);
        }
        return InetAddress.getByAddress(a);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(CIDR6.class);
    }
}
