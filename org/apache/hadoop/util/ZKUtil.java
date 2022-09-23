// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.HadoopIllegalArgumentException;
import java.io.IOException;
import com.google.common.io.Files;
import java.io.File;
import com.google.common.base.Charsets;
import java.util.Iterator;
import org.apache.zookeeper.data.Id;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ZKUtil
{
    private static int getPermFromString(final String permString) {
        int perm = 0;
        for (int i = 0; i < permString.length(); ++i) {
            final char c = permString.charAt(i);
            switch (c) {
                case 'r': {
                    perm |= 0x1;
                    break;
                }
                case 'w': {
                    perm |= 0x2;
                    break;
                }
                case 'c': {
                    perm |= 0x4;
                    break;
                }
                case 'd': {
                    perm |= 0x8;
                    break;
                }
                case 'a': {
                    perm |= 0x10;
                    break;
                }
                default: {
                    throw new BadAclFormatException("Invalid permission '" + c + "' in permission string '" + permString + "'");
                }
            }
        }
        return perm;
    }
    
    public static int removeSpecificPerms(final int perms, final int remove) {
        return perms ^ remove;
    }
    
    public static List<ACL> parseACLs(final String aclString) throws BadAclFormatException {
        final List<ACL> acl = (List<ACL>)Lists.newArrayList();
        if (aclString == null) {
            return acl;
        }
        final List<String> aclComps = (List<String>)Lists.newArrayList((Iterable<?>)Splitter.on(',').omitEmptyStrings().trimResults().split(aclString));
        for (final String a : aclComps) {
            final int firstColon = a.indexOf(58);
            final int lastColon = a.lastIndexOf(58);
            if (firstColon == -1 || lastColon == -1 || firstColon == lastColon) {
                throw new BadAclFormatException("ACL '" + a + "' not of expected form scheme:id:perm");
            }
            final ACL newAcl = new ACL();
            newAcl.setId(new Id(a.substring(0, firstColon), a.substring(firstColon + 1, lastColon)));
            newAcl.setPerms(getPermFromString(a.substring(lastColon + 1)));
            acl.add(newAcl);
        }
        return acl;
    }
    
    public static List<ZKAuthInfo> parseAuth(final String authString) throws BadAuthFormatException {
        final List<ZKAuthInfo> ret = (List<ZKAuthInfo>)Lists.newArrayList();
        if (authString == null) {
            return ret;
        }
        final List<String> authComps = (List<String>)Lists.newArrayList((Iterable<?>)Splitter.on(',').omitEmptyStrings().trimResults().split(authString));
        for (final String comp : authComps) {
            final String[] parts = comp.split(":", 2);
            if (parts.length != 2) {
                throw new BadAuthFormatException("Auth '" + comp + "' not of expected form scheme:auth");
            }
            ret.add(new ZKAuthInfo(parts[0], parts[1].getBytes(Charsets.UTF_8)));
        }
        return ret;
    }
    
    public static String resolveConfIndirection(final String valInConf) throws IOException {
        if (valInConf == null) {
            return null;
        }
        if (!valInConf.startsWith("@")) {
            return valInConf;
        }
        final String path = valInConf.substring(1).trim();
        return Files.toString(new File(path), Charsets.UTF_8).trim();
    }
    
    @InterfaceAudience.Private
    public static class ZKAuthInfo
    {
        private final String scheme;
        private final byte[] auth;
        
        public ZKAuthInfo(final String scheme, final byte[] auth) {
            this.scheme = scheme;
            this.auth = auth;
        }
        
        public String getScheme() {
            return this.scheme;
        }
        
        public byte[] getAuth() {
            return this.auth;
        }
    }
    
    @InterfaceAudience.Private
    public static class BadAclFormatException extends HadoopIllegalArgumentException
    {
        private static final long serialVersionUID = 1L;
        
        public BadAclFormatException(final String message) {
            super(message);
        }
    }
    
    @InterfaceAudience.Private
    public static class BadAuthFormatException extends HadoopIllegalArgumentException
    {
        private static final long serialVersionUID = 1L;
        
        public BadAuthFormatException(final String message) {
            super(message);
        }
    }
}
