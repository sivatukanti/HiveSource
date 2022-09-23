// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.ssl;

import java.util.Collection;
import java.security.cert.CertificateParsingException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.LinkedList;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.util.StringUtils;
import java.util.Arrays;
import java.io.InputStream;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import org.slf4j.Logger;
import javax.net.ssl.SSLException;
import java.security.cert.X509Certificate;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSession;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.net.ssl.HostnameVerifier;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public interface SSLHostnameVerifier extends HostnameVerifier
{
    public static final SSLHostnameVerifier DEFAULT = new AbstractVerifier() {
        @Override
        public final void check(final String[] hosts, final String[] cns, final String[] subjectAlts) throws SSLException {
            this.check(hosts, cns, subjectAlts, false, false);
        }
        
        @Override
        public final String toString() {
            return "DEFAULT";
        }
    };
    public static final SSLHostnameVerifier DEFAULT_AND_LOCALHOST = new AbstractVerifier() {
        @Override
        public final void check(final String[] hosts, final String[] cns, final String[] subjectAlts) throws SSLException {
            if (AbstractVerifier.isLocalhost(hosts[0])) {
                return;
            }
            this.check(hosts, cns, subjectAlts, false, false);
        }
        
        @Override
        public final String toString() {
            return "DEFAULT_AND_LOCALHOST";
        }
    };
    public static final SSLHostnameVerifier STRICT = new AbstractVerifier() {
        @Override
        public final void check(final String[] host, final String[] cns, final String[] subjectAlts) throws SSLException {
            this.check(host, cns, subjectAlts, false, true);
        }
        
        @Override
        public final String toString() {
            return "STRICT";
        }
    };
    public static final SSLHostnameVerifier STRICT_IE6 = new AbstractVerifier() {
        @Override
        public final void check(final String[] host, final String[] cns, final String[] subjectAlts) throws SSLException {
            this.check(host, cns, subjectAlts, true, true);
        }
        
        @Override
        public final String toString() {
            return "STRICT_IE6";
        }
    };
    public static final SSLHostnameVerifier ALLOW_ALL = new AbstractVerifier() {
        @Override
        public final void check(final String[] host, final String[] cns, final String[] subjectAlts) {
        }
        
        @Override
        public final String toString() {
            return "ALLOW_ALL";
        }
    };
    
    boolean verify(final String p0, final SSLSession p1);
    
    void check(final String p0, final SSLSocket p1) throws IOException;
    
    void check(final String p0, final X509Certificate p1) throws SSLException;
    
    void check(final String p0, final String[] p1, final String[] p2) throws SSLException;
    
    void check(final String[] p0, final SSLSocket p1) throws IOException;
    
    void check(final String[] p0, final X509Certificate p1) throws SSLException;
    
    void check(final String[] p0, final String[] p1, final String[] p2) throws SSLException;
    
    public abstract static class AbstractVerifier implements SSLHostnameVerifier
    {
        static final Logger LOG;
        private static final String[] BAD_COUNTRY_2LDS;
        private static final String[] LOCALHOSTS;
        
        protected AbstractVerifier() {
        }
        
        @Override
        public boolean verify(final String host, final SSLSession session) {
            try {
                final Certificate[] certs = session.getPeerCertificates();
                final X509Certificate x509 = (X509Certificate)certs[0];
                this.check(new String[] { host }, x509);
                return true;
            }
            catch (SSLException e) {
                return false;
            }
        }
        
        @Override
        public void check(final String host, final SSLSocket ssl) throws IOException {
            this.check(new String[] { host }, ssl);
        }
        
        @Override
        public void check(final String host, final X509Certificate cert) throws SSLException {
            this.check(new String[] { host }, cert);
        }
        
        @Override
        public void check(final String host, final String[] cns, final String[] subjectAlts) throws SSLException {
            this.check(new String[] { host }, cns, subjectAlts);
        }
        
        @Override
        public void check(final String[] host, final SSLSocket ssl) throws IOException {
            if (host == null) {
                throw new NullPointerException("host to verify is null");
            }
            SSLSession session = ssl.getSession();
            if (session == null) {
                final InputStream in = ssl.getInputStream();
                in.available();
                session = ssl.getSession();
                if (session == null) {
                    ssl.startHandshake();
                    session = ssl.getSession();
                }
            }
            Certificate[] certs;
            try {
                certs = session.getPeerCertificates();
            }
            catch (SSLPeerUnverifiedException spue) {
                final InputStream in2 = ssl.getInputStream();
                in2.available();
                throw spue;
            }
            final X509Certificate x509 = (X509Certificate)certs[0];
            this.check(host, x509);
        }
        
        @Override
        public void check(final String[] host, final X509Certificate cert) throws SSLException {
            final String[] cns = Certificates.getCNs(cert);
            final String[] subjectAlts = Certificates.getDNSSubjectAlts(cert);
            try {
                this.check(host, cns, subjectAlts);
            }
            catch (SSLException e) {
                AbstractVerifier.LOG.error("Host check error {}", e);
                throw e;
            }
        }
        
        public void check(final String[] hosts, final String[] cns, final String[] subjectAlts, final boolean ie6, final boolean strictWithSubDomains) throws SSLException {
            if (AbstractVerifier.LOG.isTraceEnabled()) {
                AbstractVerifier.LOG.trace("Hosts:{}, CNs:{} subjectAlts:{}, ie6:{}, strictWithSubDomains{}", Arrays.toString(hosts), Arrays.toString(cns), Arrays.toString(subjectAlts), ie6, strictWithSubDomains);
            }
            StringBuffer buf = new StringBuffer(32);
            buf.append('<');
            for (int i = 0; i < hosts.length; ++i) {
                String h = hosts[i];
                h = ((h != null) ? StringUtils.toLowerCase(h.trim()) : "");
                hosts[i] = h;
                if (i > 0) {
                    buf.append('/');
                }
                buf.append(h);
            }
            buf.append('>');
            final String hostnames = buf.toString();
            final Set<String> names = new TreeSet<String>();
            if (cns != null && cns.length > 0 && cns[0] != null) {
                names.add(cns[0]);
                if (ie6) {
                    for (int j = 1; j < cns.length; ++j) {
                        names.add(cns[j]);
                    }
                }
            }
            if (subjectAlts != null) {
                for (int j = 0; j < subjectAlts.length; ++j) {
                    if (subjectAlts[j] != null) {
                        names.add(subjectAlts[j]);
                    }
                }
            }
            if (names.isEmpty()) {
                final String msg = "Certificate for " + hosts[0] + " doesn't contain CN or DNS subjectAlt";
                throw new SSLException(msg);
            }
            buf = new StringBuffer();
            boolean match = false;
            final Iterator<String> it = names.iterator();
        Label_0550:
            while (it.hasNext()) {
                final String cn = StringUtils.toLowerCase(it.next());
                buf.append(" <");
                buf.append(cn);
                buf.append('>');
                if (it.hasNext()) {
                    buf.append(" OR");
                }
                final boolean doWildcard = cn.startsWith("*.") && cn.lastIndexOf(46) >= 0 && !isIP4Address(cn) && acceptableCountryWildcard(cn);
                for (int k = 0; k < hosts.length; ++k) {
                    final String hostName = StringUtils.toLowerCase(hosts[k].trim());
                    if (doWildcard) {
                        match = hostName.endsWith(cn.substring(1));
                        if (match && strictWithSubDomains) {
                            match = (countDots(hostName) == countDots(cn));
                        }
                    }
                    else {
                        match = hostName.equals(cn);
                    }
                    if (match) {
                        break Label_0550;
                    }
                }
            }
            if (!match) {
                throw new SSLException("hostname in certificate didn't match: " + hostnames + " !=" + (Object)buf);
            }
        }
        
        public static boolean isIP4Address(final String cn) {
            boolean isIP4 = true;
            String tld = cn;
            final int x = cn.lastIndexOf(46);
            if (x >= 0 && x + 1 < cn.length()) {
                tld = cn.substring(x + 1);
            }
            for (int i = 0; i < tld.length(); ++i) {
                if (!Character.isDigit(tld.charAt(0))) {
                    isIP4 = false;
                    break;
                }
            }
            return isIP4;
        }
        
        public static boolean acceptableCountryWildcard(final String cn) {
            final int cnLen = cn.length();
            if (cnLen >= 7 && cnLen <= 9 && cn.charAt(cnLen - 3) == '.') {
                final String s = cn.substring(2, cnLen - 3);
                final int x = Arrays.binarySearch(AbstractVerifier.BAD_COUNTRY_2LDS, s);
                return x < 0;
            }
            return true;
        }
        
        public static boolean isLocalhost(String host) {
            host = ((host != null) ? StringUtils.toLowerCase(host.trim()) : "");
            if (host.startsWith("::1")) {
                final int x = host.lastIndexOf(37);
                if (x >= 0) {
                    host = host.substring(0, x);
                }
            }
            final int x = Arrays.binarySearch(AbstractVerifier.LOCALHOSTS, host);
            return x >= 0;
        }
        
        public static int countDots(final String s) {
            int count = 0;
            for (int i = 0; i < s.length(); ++i) {
                if (s.charAt(i) == '.') {
                    ++count;
                }
            }
            return count;
        }
        
        static {
            LOG = LoggerFactory.getLogger(SSLFactory.class);
            BAD_COUNTRY_2LDS = new String[] { "ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org" };
            LOCALHOSTS = new String[] { "::1", "127.0.0.1", "localhost", "localhost.localdomain" };
            Arrays.sort(AbstractVerifier.BAD_COUNTRY_2LDS);
            Arrays.sort(AbstractVerifier.LOCALHOSTS);
        }
    }
    
    public static class Certificates
    {
        public static String[] getCNs(final X509Certificate cert) {
            final List<String> cnList = new LinkedList<String>();
            final String subjectPrincipal = cert.getSubjectX500Principal().toString();
            final StringTokenizer st = new StringTokenizer(subjectPrincipal, ",");
            while (st.hasMoreTokens()) {
                final String tok = st.nextToken();
                final int x = tok.indexOf("CN=");
                if (x >= 0) {
                    cnList.add(tok.substring(x + 3));
                }
            }
            if (!cnList.isEmpty()) {
                final String[] cns = new String[cnList.size()];
                cnList.toArray(cns);
                return cns;
            }
            return null;
        }
        
        public static String[] getDNSSubjectAlts(final X509Certificate cert) {
            final List<String> subjectAltList = new LinkedList<String>();
            Collection<List<?>> c = null;
            try {
                c = cert.getSubjectAlternativeNames();
            }
            catch (CertificateParsingException cpe) {
                cpe.printStackTrace();
            }
            if (c != null) {
                for (final List<?> list : c) {
                    final int type = (int)list.get(0);
                    if (type == 2) {
                        final String s = (String)list.get(1);
                        subjectAltList.add(s);
                    }
                }
            }
            if (!subjectAltList.isEmpty()) {
                final String[] subjectAlts = new String[subjectAltList.size()];
                subjectAltList.toArray(subjectAlts);
                return subjectAlts;
            }
            return null;
        }
    }
}
