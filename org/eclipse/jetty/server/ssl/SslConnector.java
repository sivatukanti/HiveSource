// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.ssl;

import java.io.File;
import java.security.Security;
import javax.net.ssl.SSLContext;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.Connector;

public interface SslConnector extends Connector
{
    @Deprecated
    public static final String DEFAULT_KEYSTORE_ALGORITHM = (Security.getProperty("ssl.KeyManagerFactory.algorithm") == null) ? "SunX509" : Security.getProperty("ssl.KeyManagerFactory.algorithm");
    @Deprecated
    public static final String DEFAULT_TRUSTSTORE_ALGORITHM = (Security.getProperty("ssl.TrustManagerFactory.algorithm") == null) ? "SunX509" : Security.getProperty("ssl.TrustManagerFactory.algorithm");
    @Deprecated
    public static final String DEFAULT_KEYSTORE = System.getProperty("user.home") + File.separator + ".keystore";
    @Deprecated
    public static final String KEYPASSWORD_PROPERTY = "org.eclipse.jetty.ssl.keypassword";
    @Deprecated
    public static final String PASSWORD_PROPERTY = "org.eclipse.jetty.ssl.password";
    
    SslContextFactory getSslContextFactory();
    
    @Deprecated
    String[] getExcludeCipherSuites();
    
    @Deprecated
    void setExcludeCipherSuites(final String[] p0);
    
    @Deprecated
    String[] getIncludeCipherSuites();
    
    @Deprecated
    void setIncludeCipherSuites(final String[] p0);
    
    @Deprecated
    void setPassword(final String p0);
    
    @Deprecated
    void setTrustPassword(final String p0);
    
    @Deprecated
    void setKeyPassword(final String p0);
    
    @Deprecated
    String getProtocol();
    
    @Deprecated
    void setProtocol(final String p0);
    
    @Deprecated
    void setKeystore(final String p0);
    
    @Deprecated
    String getKeystore();
    
    @Deprecated
    String getKeystoreType();
    
    @Deprecated
    boolean getNeedClientAuth();
    
    @Deprecated
    boolean getWantClientAuth();
    
    @Deprecated
    void setNeedClientAuth(final boolean p0);
    
    @Deprecated
    void setWantClientAuth(final boolean p0);
    
    @Deprecated
    void setKeystoreType(final String p0);
    
    @Deprecated
    String getProvider();
    
    @Deprecated
    String getSecureRandomAlgorithm();
    
    @Deprecated
    String getSslKeyManagerFactoryAlgorithm();
    
    @Deprecated
    String getSslTrustManagerFactoryAlgorithm();
    
    @Deprecated
    String getTruststore();
    
    @Deprecated
    String getTruststoreType();
    
    @Deprecated
    void setProvider(final String p0);
    
    @Deprecated
    void setSecureRandomAlgorithm(final String p0);
    
    @Deprecated
    void setSslKeyManagerFactoryAlgorithm(final String p0);
    
    @Deprecated
    void setSslTrustManagerFactoryAlgorithm(final String p0);
    
    @Deprecated
    void setTruststore(final String p0);
    
    @Deprecated
    void setTruststoreType(final String p0);
    
    @Deprecated
    void setSslContext(final SSLContext p0);
    
    @Deprecated
    SSLContext getSslContext();
    
    @Deprecated
    boolean isAllowRenegotiate();
    
    @Deprecated
    void setAllowRenegotiate(final boolean p0);
}
