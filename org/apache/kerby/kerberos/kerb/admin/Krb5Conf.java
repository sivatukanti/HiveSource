// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin;

import java.io.InputStream;
import org.apache.kerby.util.IOUtil;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.server.KdcConfig;
import java.io.File;

public class Krb5Conf
{
    public static final String KRB5_CONF = "java.security.krb5.conf";
    private static final String KRB5_CONF_FILE = "krb5.conf";
    private File confDir;
    private KdcConfig kdcConfig;
    
    public Krb5Conf(final File confDir, final KdcConfig kdcConfig) {
        this.confDir = confDir;
        this.kdcConfig = kdcConfig;
    }
    
    public void initKrb5conf() throws IOException {
        final File confFile = this.generateConfFile();
        System.setProperty("java.security.krb5.conf", confFile.getAbsolutePath());
    }
    
    private File generateConfFile() throws IOException {
        final String resourcePath = this.kdcConfig.allowUdp() ? "/krb5_udp.conf" : "/krb5.conf";
        final InputStream templateResource = this.getClass().getResourceAsStream(resourcePath);
        String content;
        final String templateContent = content = IOUtil.readInput(templateResource);
        content = content.replaceAll("_REALM_", "" + this.kdcConfig.getKdcRealm());
        final int kdcPort = this.kdcConfig.allowUdp() ? this.kdcConfig.getKdcUdpPort() : this.kdcConfig.getKdcTcpPort();
        content = content.replaceAll("_KDC_PORT_", String.valueOf(kdcPort));
        if (this.kdcConfig.allowTcp()) {
            content = content.replaceAll("#_KDC_TCP_PORT_", "kdc_tcp_port = " + this.kdcConfig.getKdcTcpPort());
        }
        if (this.kdcConfig.allowUdp()) {
            content = content.replaceAll("#_KDC_UDP_PORT_", "kdc_udp_port = " + this.kdcConfig.getKdcUdpPort());
        }
        final int udpLimit = this.kdcConfig.allowUdp() ? 4096 : 1;
        content = content.replaceAll("_UDP_LIMIT_", String.valueOf(udpLimit));
        final File confFile = new File(this.confDir, "krb5.conf");
        if (confFile.exists()) {
            final boolean delete = confFile.delete();
            if (!delete) {
                throw new RuntimeException("File delete error!");
            }
        }
        IOUtil.writeFile(content, confFile);
        return confFile;
    }
}
