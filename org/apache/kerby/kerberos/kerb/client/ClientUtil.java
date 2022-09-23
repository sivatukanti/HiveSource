// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.net.InetSocketAddress;
import org.apache.kerby.kerberos.kerb.transport.TransportPair;
import java.util.Map;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.io.File;
import org.slf4j.Logger;

public final class ClientUtil
{
    private static final Logger LOG;
    private static final String KRB5_FILE_NAME = "krb5.conf";
    private static final String KRB5_ENV_NAME = "KRB5_CONFIG";
    
    private ClientUtil() {
    }
    
    public static KrbConfig getConfig(final File confDir) throws KrbException {
        final File confFile = new File(confDir, "krb5.conf");
        if (!confFile.exists()) {
            throw new KrbException("krb5.conf not found");
        }
        if (confFile != null && confFile.exists()) {
            final KrbConfig krbConfig = new KrbConfig();
            try {
                krbConfig.addKrb5Config(confFile);
                return krbConfig;
            }
            catch (IOException e) {
                throw new KrbException("Failed to load krb config " + confFile.getAbsolutePath());
            }
        }
        return null;
    }
    
    public static KrbConfig getDefaultConfig() throws KrbException {
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
        final KrbConfig krbConfig = new KrbConfig();
        if (confFile != null && confFile.exists()) {
            try {
                krbConfig.addKrb5Config(confFile);
            }
            catch (IOException e2) {
                throw new KrbException("Failed to load krb config " + confFile.getAbsolutePath());
            }
        }
        return krbConfig;
    }
    
    public static TransportPair getTransportPair(final KrbSetting setting, final String kdcString) throws KrbException, IOException {
        final TransportPair result = new TransportPair();
        int tcpPort = setting.checkGetKdcTcpPort();
        int udpPort = setting.checkGetKdcUdpPort();
        int port = 0;
        String portStr = null;
        String kdc;
        if (kdcString.charAt(0) == '[') {
            final int pos = kdcString.indexOf(93, 1);
            if (pos == -1) {
                throw new IOException("Illegal KDC: " + kdcString);
            }
            kdc = kdcString.substring(1, pos);
            if (pos != kdcString.length() - 1) {
                if (kdcString.charAt(pos + 1) != ':') {
                    throw new IOException("Illegal KDC: " + kdcString);
                }
                portStr = kdcString.substring(pos + 2);
            }
        }
        else {
            final int colon = kdcString.indexOf(58);
            if (colon == -1) {
                kdc = kdcString;
            }
            else {
                final int nextColon = kdcString.indexOf(58, colon + 1);
                if (nextColon > 0) {
                    kdc = kdcString;
                }
                else {
                    kdc = kdcString.substring(0, colon);
                    portStr = kdcString.substring(colon + 1);
                }
            }
        }
        if (portStr != null) {
            final int tempPort = parsePositiveIntString(portStr);
            if (tempPort > 0) {
                port = tempPort;
            }
        }
        if (port != 0) {
            tcpPort = port;
            udpPort = port;
        }
        if (tcpPort > 0) {
            result.tcpAddress = new InetSocketAddress(kdc, tcpPort);
        }
        if (udpPort > 0) {
            result.udpAddress = new InetSocketAddress(kdc, udpPort);
        }
        return result;
    }
    
    private static int parsePositiveIntString(final String intString) {
        if (intString == null) {
            return -1;
        }
        int ret = -1;
        try {
            ret = Integer.parseInt(intString);
        }
        catch (Exception exc) {
            return -1;
        }
        if (ret >= 0) {
            return ret;
        }
        return -1;
    }
    
    public static List<String> getKDCList(final KrbSetting krbSetting) throws KrbException {
        final List<String> kdcList = new ArrayList<String>();
        kdcList.add(krbSetting.getKdcHost());
        final String realm = krbSetting.getKdcRealm();
        if (realm != null) {
            final KrbConfig krbConfig = krbSetting.getKrbConfig();
            final List<Object> kdcs = krbConfig.getRealmSectionItems(realm, "kdc");
            if (!kdcs.isEmpty()) {
                for (final Object object : kdcs) {
                    kdcList.add((object != null) ? object.toString() : null);
                }
            }
            if (kdcList.isEmpty()) {
                ClientUtil.LOG.error("Cannot get kdc for realm " + realm);
            }
            return kdcList;
        }
        throw new KrbException("Can't get the realm");
    }
    
    static {
        LOG = LoggerFactory.getLogger(ClientUtil.class);
    }
}
