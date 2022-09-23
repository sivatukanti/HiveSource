// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import java.net.InetSocketAddress;
import org.apache.kerby.kerberos.kerb.transport.TransportPair;
import org.apache.kerby.config.Config;
import org.apache.kerby.kerberos.kerb.identity.backend.MemoryIdentityBackend;
import org.apache.kerby.config.ConfigKey;
import org.apache.kerby.kerberos.kerb.identity.backend.IdentityBackend;
import org.apache.kerby.kerberos.kerb.identity.backend.BackendConfig;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.io.File;

public final class KdcUtil
{
    private KdcUtil() {
    }
    
    public static KdcConfig getKdcConfig(final File confDir) throws KrbException {
        final File kdcConfFile = new File(confDir, "kdc.conf");
        if (kdcConfFile.exists()) {
            final KdcConfig kdcConfig = new KdcConfig();
            try {
                kdcConfig.addKrb5Config(kdcConfFile);
            }
            catch (IOException e) {
                throw new KrbException("Can not load the kdc configuration file " + kdcConfFile.getAbsolutePath());
            }
            return kdcConfig;
        }
        return null;
    }
    
    public static BackendConfig getBackendConfig(final File confDir) throws KrbException {
        final File backendConfigFile = new File(confDir, "backend.conf");
        if (backendConfigFile.exists()) {
            final BackendConfig backendConfig = new BackendConfig();
            try {
                backendConfig.addIniConfig(backendConfigFile);
            }
            catch (IOException e) {
                throw new KrbException("Can not load the backend configuration file " + backendConfigFile.getAbsolutePath());
            }
            return backendConfig;
        }
        return null;
    }
    
    public static IdentityBackend getBackend(final BackendConfig backendConfig) throws KrbException {
        String backendClassName = backendConfig.getString(KdcConfigKey.KDC_IDENTITY_BACKEND, true);
        if (backendClassName == null) {
            backendClassName = MemoryIdentityBackend.class.getCanonicalName();
        }
        Class<?> backendClass;
        try {
            backendClass = Class.forName(backendClassName);
        }
        catch (ClassNotFoundException e2) {
            throw new KrbException("Failed to load backend class: " + backendClassName);
        }
        IdentityBackend backend;
        try {
            backend = (IdentityBackend)backendClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new KrbException("Failed to create backend: " + backendClassName);
        }
        backend.setConfig(backendConfig);
        backend.initialize();
        return backend;
    }
    
    public static TransportPair getTransportPair(final KdcSetting setting) throws KrbException {
        final TransportPair result = new TransportPair();
        final int tcpPort = setting.checkGetKdcTcpPort();
        if (tcpPort > 0) {
            result.tcpAddress = new InetSocketAddress(setting.getKdcHost(), tcpPort);
        }
        final int udpPort = setting.checkGetKdcUdpPort();
        if (udpPort > 0) {
            result.udpAddress = new InetSocketAddress(setting.getKdcHost(), udpPort);
        }
        return result;
    }
}
