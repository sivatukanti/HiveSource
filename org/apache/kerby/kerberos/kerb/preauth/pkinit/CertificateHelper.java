// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth.pkinit;

import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.security.cert.CertificateFactory;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.File;
import java.security.cert.Certificate;
import java.util.List;
import org.slf4j.Logger;

public class CertificateHelper
{
    private static final Logger LOG;
    
    public static List<Certificate> loadCerts(final String filename) throws KrbException {
        final File file = new File(filename);
        InputStream res = null;
        if (file.isFile()) {
            try {
                res = Files.newInputStream(file.toPath(), new OpenOption[0]);
            }
            catch (IOException e) {
                CertificateHelper.LOG.error("Can't load cert, file not found. " + e);
            }
        }
        else {
            res = CertificateHelper.class.getClassLoader().getResourceAsStream(filename);
        }
        return loadCerts(res);
    }
    
    public static List<Certificate> loadCerts(final InputStream inputStream) throws KrbException {
        CertificateFactory certFactory = null;
        try {
            certFactory = CertificateFactory.getInstance("X.509");
            final Collection<? extends Certificate> certs = certFactory.generateCertificates(inputStream);
            return new ArrayList<Certificate>(certs);
        }
        catch (CertificateException e) {
            throw new KrbException("Failed to load certificates", e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CertificateHelper.class);
    }
}
