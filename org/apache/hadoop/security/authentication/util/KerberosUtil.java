// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.util;

import java.util.NoSuchElementException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.ByteBuffer;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;
import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.util.HashSet;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import java.io.File;
import java.util.Locale;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.ietf.jgss.GSSException;
import org.apache.hadoop.util.PlatformName;
import org.ietf.jgss.Oid;

public class KerberosUtil
{
    public static final Oid GSS_SPNEGO_MECH_OID;
    public static final Oid GSS_KRB5_MECH_OID;
    public static final Oid NT_GSS_KRB5_PRINCIPAL_OID;
    
    public static String getKrb5LoginModuleName() {
        return PlatformName.IBM_JAVA ? "com.ibm.security.auth.module.Krb5LoginModule" : "com.sun.security.auth.module.Krb5LoginModule";
    }
    
    private static Oid getNumericOidInstance(final String oidName) {
        try {
            return new Oid(oidName);
        }
        catch (GSSException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    public static Oid getOidInstance(final String oidName) throws ClassNotFoundException, GSSException, NoSuchFieldException, IllegalAccessException {
        Class<?> oidClass;
        if (PlatformName.IBM_JAVA) {
            if ("NT_GSS_KRB5_PRINCIPAL".equals(oidName)) {
                return new Oid("1.2.840.113554.1.2.2.1");
            }
            oidClass = Class.forName("com.ibm.security.jgss.GSSUtil");
        }
        else {
            oidClass = Class.forName("sun.security.jgss.GSSUtil");
        }
        final Field oidField = oidClass.getDeclaredField(oidName);
        return (Oid)oidField.get(oidClass);
    }
    
    public static String getDefaultRealm() throws ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Class<?> classRef;
        if (PlatformName.IBM_JAVA) {
            classRef = Class.forName("com.ibm.security.krb5.internal.Config");
        }
        else {
            classRef = Class.forName("sun.security.krb5.Config");
        }
        final Method getInstanceMethod = classRef.getMethod("getInstance", (Class<?>[])new Class[0]);
        final Object kerbConf = getInstanceMethod.invoke(classRef, new Object[0]);
        final Method getDefaultRealmMethod = classRef.getDeclaredMethod("getDefaultRealm", (Class<?>[])new Class[0]);
        return (String)getDefaultRealmMethod.invoke(kerbConf, new Object[0]);
    }
    
    public static String getDefaultRealmProtected() {
        String realmString = null;
        try {
            realmString = getDefaultRealm();
        }
        catch (RuntimeException ex) {}
        catch (Exception ex2) {}
        return realmString;
    }
    
    public static String getDomainRealm(final String shortprinc) {
        String realmString = null;
        try {
            Class<?> classRef;
            if (PlatformName.IBM_JAVA) {
                classRef = Class.forName("com.ibm.security.krb5.PrincipalName");
            }
            else {
                classRef = Class.forName("sun.security.krb5.PrincipalName");
            }
            final int tKrbNtSrvHst = classRef.getField("KRB_NT_SRV_HST").getInt(null);
            final Object principalName = classRef.getConstructor(String.class, Integer.TYPE).newInstance(shortprinc, tKrbNtSrvHst);
            realmString = (String)classRef.getMethod("getRealmString", (Class<?>[])new Class[0]).invoke(principalName, new Object[0]);
        }
        catch (RuntimeException ex) {}
        catch (Exception ex2) {}
        if (null == realmString || realmString.equals("")) {
            return getDefaultRealmProtected();
        }
        return realmString;
    }
    
    static String getLocalHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getCanonicalHostName();
    }
    
    public static final String getServicePrincipal(final String service, final String hostname) throws UnknownHostException {
        String fqdn = hostname;
        String shortprinc = null;
        String realmString = null;
        if (null == fqdn || fqdn.equals("") || fqdn.equals("0.0.0.0")) {
            fqdn = getLocalHostName();
        }
        fqdn = fqdn.toLowerCase(Locale.US);
        shortprinc = service + "/" + fqdn;
        realmString = getDomainRealm(shortprinc);
        if (null == realmString || realmString.equals("")) {
            return shortprinc;
        }
        return shortprinc + "@" + realmString;
    }
    
    static final String[] getPrincipalNames(final String keytabFileName) throws IOException {
        final Keytab keytab = Keytab.loadKeytab(new File(keytabFileName));
        final Set<String> principals = new HashSet<String>();
        final List<PrincipalName> entries = keytab.getPrincipals();
        for (final PrincipalName entry : entries) {
            principals.add(entry.getName().replace("\\", "/"));
        }
        return principals.toArray(new String[0]);
    }
    
    public static final String[] getPrincipalNames(final String keytab, final Pattern pattern) throws IOException {
        String[] principals = getPrincipalNames(keytab);
        if (principals.length != 0) {
            final List<String> matchingPrincipals = new ArrayList<String>();
            for (final String principal : principals) {
                if (pattern.matcher(principal).matches()) {
                    matchingPrincipals.add(principal);
                }
            }
            principals = matchingPrincipals.toArray(new String[0]);
        }
        return principals;
    }
    
    public static boolean hasKerberosKeyTab(final Subject subject) {
        return !subject.getPrivateCredentials(KeyTab.class).isEmpty();
    }
    
    public static boolean hasKerberosTicket(final Subject subject) {
        return !subject.getPrivateCredentials(KerberosTicket.class).isEmpty();
    }
    
    public static String getTokenServerName(final byte[] rawToken) {
        DER token = new DER(rawToken);
        DER oid = token.next();
        if (oid.equals(DER.SPNEGO_MECH_OID)) {
            token = token.next().get(160, 48, 162, 4).next();
            oid = token.next();
        }
        if (!oid.equals(DER.KRB5_MECH_OID)) {
            throw new IllegalArgumentException("Malformed gss token");
        }
        if (token.next().getTag() != 1) {
            throw new IllegalArgumentException("Not an AP-REQ token");
        }
        final DER ticket = token.next().get(110, 48, 163, 97, 48);
        final String realm = ticket.get(161, 27).getAsString();
        final DER names = ticket.get(162, 48, 161, 48);
        final StringBuilder sb = new StringBuilder();
        while (names.hasNext()) {
            if (sb.length() > 0) {
                sb.append('/');
            }
            sb.append(names.next().getAsString());
        }
        return sb.append('@').append(realm).toString();
    }
    
    static {
        GSS_SPNEGO_MECH_OID = getNumericOidInstance("1.3.6.1.5.5.2");
        GSS_KRB5_MECH_OID = getNumericOidInstance("1.2.840.113554.1.2.2");
        NT_GSS_KRB5_PRINCIPAL_OID = getNumericOidInstance("1.2.840.113554.1.2.2.1");
    }
    
    private static class DER implements Iterator<DER>
    {
        static final DER SPNEGO_MECH_OID;
        static final DER KRB5_MECH_OID;
        private final int tag;
        private final ByteBuffer bb;
        
        private static DER getDER(final Oid oid) {
            try {
                return new DER(oid.getDER());
            }
            catch (GSSException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        
        DER(final byte[] buf) {
            this(ByteBuffer.wrap(buf));
        }
        
        DER(final ByteBuffer srcbb) {
            this.tag = (srcbb.get() & 0xFF);
            final int length = readLength(srcbb);
            (this.bb = srcbb.slice()).limit(length);
            srcbb.position(srcbb.position() + length);
        }
        
        int getTag() {
            return this.tag;
        }
        
        private static int readLength(final ByteBuffer bb) {
            int length = bb.get();
            if ((length & 0xFFFFFF80) != 0x0) {
                final int varlength = length & 0x7F;
                length = 0;
                for (int i = 0; i < varlength; ++i) {
                    length = (length << 8 | (bb.get() & 0xFF));
                }
            }
            return length;
        }
        
        DER choose(final int subtag) {
            while (this.hasNext()) {
                final DER der = this.next();
                if (der.getTag() == subtag) {
                    return der;
                }
            }
            return null;
        }
        
        DER get(final int... tags) {
            DER der = this;
            for (int i = 0; i < tags.length; ++i) {
                final int expectedTag = tags[i];
                if (der.getTag() != expectedTag) {
                    der = (der.hasNext() ? der.choose(expectedTag) : null);
                }
                if (der == null) {
                    final StringBuilder sb = new StringBuilder("Tag not found:");
                    for (int ii = 0; ii <= i; ++ii) {
                        sb.append(" 0x").append(Integer.toHexString(tags[ii]));
                    }
                    throw new IllegalStateException(sb.toString());
                }
            }
            return der;
        }
        
        String getAsString() {
            try {
                return new String(this.bb.array(), this.bb.arrayOffset() + this.bb.position(), this.bb.remaining(), "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new IllegalCharsetNameException("UTF-8");
            }
        }
        
        @Override
        public int hashCode() {
            return 31 * this.tag + this.bb.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof DER && this.tag == ((DER)o).tag && this.bb.equals(((DER)o).bb);
        }
        
        @Override
        public boolean hasNext() {
            return ((this.tag & 0x30) != 0x0 || this.tag == 4) && this.bb.hasRemaining();
        }
        
        @Override
        public DER next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return new DER(this.bb);
        }
        
        @Override
        public String toString() {
            return "[tag=0x" + Integer.toHexString(this.tag) + " bb=" + this.bb + "]";
        }
        
        static {
            SPNEGO_MECH_OID = getDER(KerberosUtil.GSS_SPNEGO_MECH_OID);
            KRB5_MECH_OID = getDER(KerberosUtil.GSS_KRB5_MECH_OID);
        }
    }
}
