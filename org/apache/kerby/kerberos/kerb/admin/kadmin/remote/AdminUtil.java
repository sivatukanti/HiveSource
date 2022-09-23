// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

import java.net.InetSocketAddress;
import org.apache.kerby.kerberos.kerb.transport.TransportPair;
import java.util.Map;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.io.File;

public final class AdminUtil
{
    private static final String KRB5_FILE_NAME = "krb5.conf";
    private static final String KRB5_ENV_NAME = "KRB5_CONFIG";
    
    private AdminUtil() {
    }
    
    public static AdminConfig getConfig(final File confDir) throws KrbException {
        final File confFile = new File(confDir, "krb5.conf");
        if (!confFile.exists()) {
            throw new KrbException("krb5.conf not found");
        }
        if (confFile != null && confFile.exists()) {
            final AdminConfig adminConfig = new AdminConfig();
            try {
                adminConfig.addKrb5Config(confFile);
                return adminConfig;
            }
            catch (IOException e) {
                throw new KrbException("Failed to load krb config " + confFile.getAbsolutePath());
            }
        }
        return null;
    }
    
    public static AdminConfig getDefaultConfig() throws KrbException {
        File confFile = null;
        String tmpEnv;
        try {
            final Map<String, String> mapEnv = System.getenv();
            tmpEnv = mapEnv.get("KRB5_CONFIG");
        }
        catch (SecurityException e) {
            tmpEnv = null;
        }
        if (tmpEnv != null) {
            confFile = new File(tmpEnv);
            if (!confFile.exists()) {
                throw new KrbException("krb5 conf not found. Invalid env KRB5_CONFIG");
            }
        }
        else {
            final File confDir = new File("/etc/");
            if (confDir.exists()) {
                confFile = new File(confDir, "krb5.conf");
            }
        }
        final AdminConfig adminConfig = new AdminConfig();
        if (confFile != null && confFile.exists()) {
            try {
                adminConfig.addKrb5Config(confFile);
            }
            catch (IOException e2) {
                throw new KrbException("Failed to load krb config " + confFile.getAbsolutePath());
            }
        }
        return adminConfig;
    }
    
    public static TransportPair getTransportPair(final AdminSetting setting) throws KrbException {
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
