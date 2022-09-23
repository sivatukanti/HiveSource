// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import java.io.InputStream;
import org.apache.kerby.kerberos.kerb.server.KdcSetting;
import org.apache.kerby.util.IOUtil;
import java.io.IOException;
import java.io.File;
import org.apache.kerby.kerberos.kerb.server.SimpleKdcServer;

public class Krb5Conf
{
    public static final String KRB5_CONF = "java.security.krb5.conf";
    private static final String KRB5_CONF_FILE = "krb5.conf";
    private SimpleKdcServer kdcServer;
    private File confFile;
    
    public Krb5Conf(final SimpleKdcServer kdcServer) {
        this.kdcServer = kdcServer;
    }
    
    public void initKrb5conf() throws IOException {
        final File confFile = this.generateConfFile();
        System.setProperty("java.security.krb5.conf", confFile.getAbsolutePath());
    }
    
    private File generateConfFile() throws IOException {
        final KdcSetting setting = this.kdcServer.getKdcSetting();
        final String resourcePath = setting.allowUdp() ? "/krb5_udp-template.conf" : "/krb5-template.conf";
        final InputStream templateResource = this.getClass().getResourceAsStream(resourcePath);
        String content;
        final String templateContent = content = IOUtil.readInput(templateResource);
        content = content.replaceAll("_REALM_", "" + setting.getKdcRealm());
        final int kdcPort = setting.allowUdp() ? setting.getKdcUdpPort() : setting.getKdcTcpPort();
        content = content.replaceAll("_KDC_PORT_", String.valueOf(kdcPort));
        if (setting.allowTcp()) {
            content = content.replaceAll("#_KDC_TCP_PORT_", "kdc_tcp_port = " + setting.getKdcTcpPort());
        }
        if (setting.allowUdp()) {
            content = content.replaceAll("#_KDC_UDP_PORT_", "kdc_udp_port = " + setting.getKdcUdpPort());
        }
        final int udpLimit = setting.allowUdp() ? 4096 : 1;
        content = content.replaceAll("_UDP_LIMIT_", String.valueOf(udpLimit));
        IOUtil.writeFile(content, this.confFile = new File(this.kdcServer.getWorkDir(), "krb5.conf"));
        return this.confFile;
    }
    
    public void deleteKrb5conf() throws IOException {
        if (!this.confFile.delete()) {
            throw new IOException();
        }
    }
}
