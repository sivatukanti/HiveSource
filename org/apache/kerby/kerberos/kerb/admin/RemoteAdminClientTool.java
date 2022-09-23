// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin;

import org.apache.kerby.util.OSUtil;
import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command.RemoteCommand;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command.RemotePrintUsageCommand;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command.RemoteGetprincsCommand;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command.RemoteRenamePrincipalCommand;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command.RemoteDeletePrincipalCommand;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.command.RemoteAddPrincipalCommand;
import org.apache.kerby.kerberos.kerb.transport.TransportPair;
import java.util.Scanner;
import javax.security.auth.Subject;
import java.nio.ByteBuffer;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.Sasl;
import java.util.HashMap;
import java.security.PrivilegedAction;
import javax.security.auth.login.LoginException;
import org.apache.kerby.kerberos.kerb.common.KrbUtil;
import org.apache.kerby.kerberos.kerb.transport.KrbNetwork;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminUtil;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminClient;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.KdcConfig;
import org.apache.kerby.kerberos.kerb.server.KdcUtil;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminConfig;
import java.io.File;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;
import org.slf4j.Logger;

public class RemoteAdminClientTool
{
    private static final Logger LOG;
    private static final byte[] EMPTY;
    private static KrbTransport transport;
    private static final String PROMPT;
    private static final String USAGE;
    private static final String LEGAL_COMMANDS = "Available commands are: \nadd_principal, addprinc\n                         Add principal\ndelete_principal, delprinc\n                         Delete principal\nrename_principal, renprinc\n                         Rename principal\nlistprincs\n          List principals\n";
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println(RemoteAdminClientTool.USAGE);
            System.exit(1);
        }
        final String confDirPath = args[0];
        final File confFile = new File(confDirPath, "adminClient.conf");
        final AdminConfig adminConfig = new AdminConfig();
        adminConfig.addKrb5Config(confFile);
        KdcConfig tmpKdcConfig = KdcUtil.getKdcConfig(new File(confDirPath));
        if (tmpKdcConfig == null) {
            tmpKdcConfig = new KdcConfig();
        }
        try {
            final Krb5Conf krb5Conf = new Krb5Conf(new File(confDirPath), tmpKdcConfig);
            krb5Conf.initKrb5conf();
        }
        catch (IOException e) {
            throw new KrbException("Failed to make krb5.conf", e);
        }
        final AdminClient adminClient = new AdminClient(adminConfig);
        final File keytabFile = new File(adminConfig.getKeyTabFile());
        if (keytabFile == null || !keytabFile.exists()) {
            System.err.println("Need the valid keytab file value in conf file.");
            return;
        }
        final String adminRealm = adminConfig.getAdminRealm();
        adminClient.setAdminRealm(adminRealm);
        adminClient.setAllowTcp(true);
        adminClient.setAllowUdp(false);
        adminClient.setAdminTcpPort(adminConfig.getAdminPort());
        adminClient.init();
        System.out.println("admin init successful");
        TransportPair tpair = null;
        try {
            tpair = AdminUtil.getTransportPair(adminClient.getSetting());
        }
        catch (KrbException e2) {
            RemoteAdminClientTool.LOG.error("Fail to get transport pair. " + e2);
        }
        final KrbNetwork network = new KrbNetwork();
        network.setSocketTimeout(adminClient.getSetting().getTimeout());
        try {
            RemoteAdminClientTool.transport = network.connect(tpair);
        }
        catch (IOException e3) {
            throw new KrbException("Failed to create transport", e3);
        }
        final String adminPrincipal = KrbUtil.makeKadminPrincipal(adminClient.getSetting().getKdcRealm()).getName();
        Subject subject = null;
        try {
            subject = AuthUtil.loginUsingKeytab(adminPrincipal, new File(adminConfig.getKeyTabFile()));
        }
        catch (LoginException e4) {
            RemoteAdminClientTool.LOG.error("Fail to login using keytab. " + e4);
        }
        Subject.doAs(subject, (PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    final Map<String, String> props = new HashMap<String, String>();
                    props.put("javax.security.sasl.qop", "auth-conf");
                    props.put("javax.security.sasl.server.authentication", "true");
                    SaslClient saslClient = null;
                    try {
                        final String protocol = adminConfig.getProtocol();
                        final String serverName = adminConfig.getServerName();
                        saslClient = Sasl.createSaslClient(new String[] { "GSSAPI" }, null, protocol, serverName, props, null);
                    }
                    catch (SaslException e) {
                        RemoteAdminClientTool.LOG.error("Fail to create sasl client. " + e);
                    }
                    if (saslClient == null) {
                        throw new KrbException("Unable to find client implementation for: GSSAPI");
                    }
                    byte[] response = new byte[0];
                    try {
                        response = (saslClient.hasInitialResponse() ? saslClient.evaluateChallenge(RemoteAdminClientTool.EMPTY) : RemoteAdminClientTool.EMPTY);
                    }
                    catch (SaslException e2) {
                        RemoteAdminClientTool.LOG.error("Sasl client evaluate challenge failed." + e2);
                    }
                    sendMessage(response, saslClient);
                    ByteBuffer message = RemoteAdminClientTool.transport.receiveMessage();
                    while (!saslClient.isComplete()) {
                        final int ssComplete = message.getInt();
                        if (ssComplete == 0) {
                            System.out.println("Sasl Server completed");
                        }
                        final byte[] arr = new byte[message.remaining()];
                        message.get(arr);
                        final byte[] challenge = saslClient.evaluateChallenge(arr);
                        sendMessage(challenge, saslClient);
                        if (!saslClient.isComplete()) {
                            message = RemoteAdminClientTool.transport.receiveMessage();
                        }
                    }
                }
                catch (Exception e3) {
                    RemoteAdminClientTool.LOG.error("Failed to run. " + e3.toString());
                }
                return null;
            }
        });
        System.out.println("enter \"command\" to see legal commands.");
        try (final Scanner scanner = new Scanner(System.in, "UTF-8")) {
            for (String input = scanner.nextLine(); !input.equals("quit") && !input.equals("exit") && !input.equals("q"); input = scanner.nextLine()) {
                excute(adminClient, input);
                System.out.print(RemoteAdminClientTool.PROMPT);
            }
        }
    }
    
    private static void sendMessage(final byte[] challenge, final SaslClient saslClient) throws SaslException {
        final ByteBuffer buffer = ByteBuffer.allocate(challenge.length + 8);
        buffer.putInt(challenge.length + 4);
        final int scComplete = saslClient.isComplete() ? 0 : 1;
        buffer.putInt(scComplete);
        buffer.put(challenge);
        buffer.flip();
        try {
            RemoteAdminClientTool.transport.sendMessage(buffer);
        }
        catch (IOException e) {
            RemoteAdminClientTool.LOG.error("Failed to send Kerberos message. " + e.toString());
        }
    }
    
    private static void excute(final AdminClient adminClient, String input) throws KrbException {
        input = input.trim();
        if (input.startsWith("command")) {
            System.out.println("Available commands are: \nadd_principal, addprinc\n                         Add principal\ndelete_principal, delprinc\n                         Delete principal\nrename_principal, renprinc\n                         Rename principal\nlistprincs\n          List principals\n");
            return;
        }
        RemoteCommand executor = null;
        if (input.startsWith("add_principal") || input.startsWith("addprinc")) {
            executor = new RemoteAddPrincipalCommand(adminClient);
        }
        else if (input.startsWith("delete_principal") || input.startsWith("delprinc")) {
            executor = new RemoteDeletePrincipalCommand(adminClient);
        }
        else if (input.startsWith("rename_principal") || input.startsWith("renprinc")) {
            executor = new RemoteRenamePrincipalCommand(adminClient);
        }
        else if (input.startsWith("list_principals")) {
            executor = new RemoteGetprincsCommand(adminClient);
        }
        else {
            if (!input.startsWith("listprincs")) {
                System.out.println("Available commands are: \nadd_principal, addprinc\n                         Add principal\ndelete_principal, delprinc\n                         Delete principal\nrename_principal, renprinc\n                         Rename principal\nlistprincs\n          List principals\n");
                return;
            }
            executor = new RemotePrintUsageCommand();
        }
        executor.execute(input);
    }
    
    static {
        LOG = LoggerFactory.getLogger(RemoteAdminClientTool.class);
        EMPTY = new byte[0];
        PROMPT = RemoteAdminClientTool.class.getSimpleName() + ".local:";
        USAGE = (OSUtil.isWindows() ? "Usage: bin\\remote-admin-client.cmd" : "Usage: sh bin/remote-admin-client.sh") + " <conf-file>\n" + "\tExample:\n" + "\t\t" + (OSUtil.isWindows() ? "bin\\remote-admin-client.cmd" : "sh bin/remote-admin-client.sh") + " conf\n";
    }
}
