// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.security;

import org.eclipse.jetty.util.log.Log;
import java.security.cert.CertPathBuilderResult;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathBuilder;
import java.security.Security;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertSelector;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.InvalidParameterException;
import java.security.cert.CRL;
import java.util.Collection;
import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jetty.util.log.Logger;

public class CertificateValidator
{
    private static final Logger LOG;
    private static AtomicLong __aliasCount;
    private KeyStore _trustStore;
    private Collection<? extends CRL> _crls;
    private int _maxCertPathLength;
    private boolean _enableCRLDP;
    private boolean _enableOCSP;
    private String _ocspResponderURL;
    
    public CertificateValidator(final KeyStore trustStore, final Collection<? extends CRL> crls) {
        this._maxCertPathLength = -1;
        this._enableCRLDP = false;
        this._enableOCSP = false;
        if (trustStore == null) {
            throw new InvalidParameterException("TrustStore must be specified for CertificateValidator.");
        }
        this._trustStore = trustStore;
        this._crls = crls;
    }
    
    public void validate(final KeyStore keyStore) throws CertificateException {
        try {
            final Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                this.validate(keyStore, alias);
            }
        }
        catch (KeyStoreException kse) {
            throw new CertificateException("Unable to retrieve aliases from keystore", kse);
        }
    }
    
    public String validate(final KeyStore keyStore, final String keyAlias) throws CertificateException {
        String result = null;
        if (keyAlias != null) {
            try {
                this.validate(keyStore, keyStore.getCertificate(keyAlias));
            }
            catch (KeyStoreException kse) {
                CertificateValidator.LOG.debug(kse);
                throw new CertificateException("Unable to validate certificate for alias [" + keyAlias + "]: " + kse.getMessage(), kse);
            }
            result = keyAlias;
        }
        return result;
    }
    
    public void validate(final KeyStore keyStore, final Certificate cert) throws CertificateException {
        Certificate[] certChain = null;
        if (cert != null && cert instanceof X509Certificate) {
            ((X509Certificate)cert).checkValidity();
            String certAlias = null;
            try {
                if (keyStore == null) {
                    throw new InvalidParameterException("Keystore cannot be null");
                }
                certAlias = keyStore.getCertificateAlias(cert);
                if (certAlias == null) {
                    certAlias = "JETTY" + String.format("%016X", CertificateValidator.__aliasCount.incrementAndGet());
                    keyStore.setCertificateEntry(certAlias, cert);
                }
                certChain = keyStore.getCertificateChain(certAlias);
                if (certChain == null || certChain.length == 0) {
                    throw new IllegalStateException("Unable to retrieve certificate chain");
                }
            }
            catch (KeyStoreException kse) {
                CertificateValidator.LOG.debug(kse);
                throw new CertificateException("Unable to validate certificate" + ((certAlias == null) ? "" : (" for alias [" + certAlias + "]")) + ": " + kse.getMessage(), kse);
            }
            this.validate(certChain);
        }
    }
    
    public void validate(final Certificate[] certChain) throws CertificateException {
        try {
            final ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();
            for (final Certificate item : certChain) {
                if (item != null) {
                    if (!(item instanceof X509Certificate)) {
                        throw new IllegalStateException("Invalid certificate type in chain");
                    }
                    certList.add((X509Certificate)item);
                }
            }
            if (certList.isEmpty()) {
                throw new IllegalStateException("Invalid certificate chain");
            }
            final X509CertSelector certSelect = new X509CertSelector();
            certSelect.setCertificate(certList.get(0));
            final PKIXBuilderParameters pbParams = new PKIXBuilderParameters(this._trustStore, certSelect);
            pbParams.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList)));
            pbParams.setMaxPathLength(this._maxCertPathLength);
            pbParams.setRevocationEnabled(true);
            if (this._crls != null && !this._crls.isEmpty()) {
                pbParams.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(this._crls)));
            }
            if (this._enableOCSP) {
                Security.setProperty("ocsp.enable", "true");
            }
            if (this._enableCRLDP) {
                System.setProperty("com.sun.security.enableCRLDP", "true");
            }
            final CertPathBuilderResult buildResult = CertPathBuilder.getInstance("PKIX").build(pbParams);
            CertPathValidator.getInstance("PKIX").validate(buildResult.getCertPath(), pbParams);
        }
        catch (GeneralSecurityException gse) {
            CertificateValidator.LOG.debug(gse);
            throw new CertificateException("Unable to validate certificate: " + gse.getMessage(), gse);
        }
    }
    
    public KeyStore getTrustStore() {
        return this._trustStore;
    }
    
    public Collection<? extends CRL> getCrls() {
        return this._crls;
    }
    
    public int getMaxCertPathLength() {
        return this._maxCertPathLength;
    }
    
    public void setMaxCertPathLength(final int maxCertPathLength) {
        this._maxCertPathLength = maxCertPathLength;
    }
    
    public boolean isEnableCRLDP() {
        return this._enableCRLDP;
    }
    
    public void setEnableCRLDP(final boolean enableCRLDP) {
        this._enableCRLDP = enableCRLDP;
    }
    
    public boolean isEnableOCSP() {
        return this._enableOCSP;
    }
    
    public void setEnableOCSP(final boolean enableOCSP) {
        this._enableOCSP = enableOCSP;
    }
    
    public String getOcspResponderURL() {
        return this._ocspResponderURL;
    }
    
    public void setOcspResponderURL(final String ocspResponderURL) {
        this._ocspResponderURL = ocspResponderURL;
    }
    
    static {
        LOG = Log.getLogger(CertificateValidator.class);
        CertificateValidator.__aliasCount = new AtomicLong();
    }
}
