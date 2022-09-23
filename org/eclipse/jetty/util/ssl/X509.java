// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ssl;

import org.eclipse.jetty.util.log.Log;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.util.StringUtil;
import javax.naming.InvalidNameException;
import java.security.cert.CertificateParsingException;
import java.util.Iterator;
import java.util.Collection;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.List;
import java.security.cert.X509Certificate;
import org.eclipse.jetty.util.log.Logger;

public class X509
{
    private static final Logger LOG;
    private static final int KEY_USAGE__KEY_CERT_SIGN = 5;
    private static final int SUBJECT_ALTERNATIVE_NAMES__DNS_NAME = 2;
    private final X509Certificate _x509;
    private final String _alias;
    private final List<String> _hosts;
    private final List<String> _wilds;
    
    public static boolean isCertSign(final X509Certificate x509) {
        final boolean[] key_usage = x509.getKeyUsage();
        return key_usage != null && key_usage[5];
    }
    
    public X509(final String alias, final X509Certificate x509) throws CertificateParsingException, InvalidNameException {
        this._hosts = new ArrayList<String>();
        this._wilds = new ArrayList<String>();
        this._alias = alias;
        this._x509 = x509;
        boolean named = false;
        final Collection<List<?>> altNames = x509.getSubjectAlternativeNames();
        if (altNames != null) {
            for (final List<?> list : altNames) {
                if (((Number)list.get(0)).intValue() == 2) {
                    final String cn = list.get(1).toString();
                    if (X509.LOG.isDebugEnabled()) {
                        X509.LOG.debug("Certificate SAN alias={} CN={} in {}", alias, cn, this);
                    }
                    if (cn == null) {
                        continue;
                    }
                    named = true;
                    this.addName(cn);
                }
            }
        }
        if (!named) {
            final LdapName name = new LdapName(x509.getSubjectX500Principal().getName("RFC2253"));
            for (final Rdn rdn : name.getRdns()) {
                if (rdn.getType().equalsIgnoreCase("CN")) {
                    final String cn2 = rdn.getValue().toString();
                    if (X509.LOG.isDebugEnabled()) {
                        X509.LOG.debug("Certificate CN alias={} CN={} in {}", alias, cn2, this);
                    }
                    if (cn2 == null || !cn2.contains(".") || cn2.contains(" ")) {
                        continue;
                    }
                    this.addName(cn2);
                }
            }
        }
    }
    
    protected void addName(String cn) {
        cn = StringUtil.asciiToLowerCase(cn);
        if (cn.startsWith("*.")) {
            this._wilds.add(cn.substring(2));
        }
        else {
            this._hosts.add(cn);
        }
    }
    
    public String getAlias() {
        return this._alias;
    }
    
    public X509Certificate getCertificate() {
        return this._x509;
    }
    
    public Set<String> getHosts() {
        return new HashSet<String>(this._hosts);
    }
    
    public Set<String> getWilds() {
        return new HashSet<String>(this._wilds);
    }
    
    public boolean matches(String host) {
        host = StringUtil.asciiToLowerCase(host);
        if (this._hosts.contains(host) || this._wilds.contains(host)) {
            return true;
        }
        final int dot = host.indexOf(46);
        if (dot >= 0) {
            final String domain = host.substring(dot + 1);
            if (this._wilds.contains(domain)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x(%s,h=%s,w=%s)", this.getClass().getSimpleName(), this.hashCode(), this._alias, this._hosts, this._wilds);
    }
    
    static {
        LOG = Log.getLogger(X509.class);
    }
}
