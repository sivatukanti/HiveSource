// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.util;

import java.security.PrivilegedActionException;
import javax.security.sasl.SaslServer;
import org.ietf.jgss.GSSCredential;
import javax.security.sasl.SaslException;
import java.security.PrivilegedExceptionAction;
import org.apache.zookeeper.server.auth.KerberosName;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSManager;
import java.security.Principal;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.Sasl;
import org.apache.zookeeper.SaslClientCallbackHandler;
import javax.security.sasl.SaslClient;
import org.slf4j.Logger;
import javax.security.auth.Subject;

public final class SecurityUtils
{
    public static final String QUORUM_HOSTNAME_PATTERN = "_HOST";
    
    public static SaslClient createSaslClient(final Subject subject, final String servicePrincipal, final String protocol, final String serverName, final Logger LOG, final String entity) throws SaslException {
        if (subject.getPrincipals().isEmpty()) {
            LOG.info("{} will use DIGEST-MD5 as SASL mechanism.", entity);
            final String[] mechs = { "DIGEST-MD5" };
            final String username = (String)subject.getPublicCredentials().toArray()[0];
            final String password = (String)subject.getPrivateCredentials().toArray()[0];
            final SaslClient saslClient = Sasl.createSaslClient(mechs, username, protocol, serverName, null, new SaslClientCallbackHandler(password, entity));
            return saslClient;
        }
        final Object[] principals = subject.getPrincipals().toArray();
        final Principal clientPrincipal = (Principal)principals[0];
        final boolean usingNativeJgss = Boolean.getBoolean("sun.security.jgss.native");
        if (usingNativeJgss) {
            try {
                final GSSManager manager = GSSManager.getInstance();
                final Oid krb5Mechanism = new Oid("1.2.840.113554.1.2.2");
                final GSSCredential cred = manager.createCredential(null, 0, krb5Mechanism, 1);
                subject.getPrivateCredentials().add(cred);
                LOG.debug("Added private credential to {} principal name: '{}'", entity, clientPrincipal);
            }
            catch (GSSException ex) {
                LOG.warn("Cannot add private credential to subject; authentication at the server may fail", ex);
            }
        }
        final KerberosName clientKerberosName = new KerberosName(clientPrincipal.getName());
        final String serverRealm = System.getProperty("zookeeper.server.realm", clientKerberosName.getRealm());
        final KerberosName serviceKerberosName = new KerberosName(servicePrincipal + "@" + serverRealm);
        final String serviceName = serviceKerberosName.getServiceName();
        final String serviceHostname = serviceKerberosName.getHostName();
        final String clientPrincipalName = clientKerberosName.toString();
        try {
            final SaslClient saslClient = Subject.doAs(subject, (PrivilegedExceptionAction<SaslClient>)new PrivilegedExceptionAction<SaslClient>() {
                @Override
                public SaslClient run() throws SaslException {
                    LOG.info("{} will use GSSAPI as SASL mechanism.", entity);
                    final String[] mechs = { "GSSAPI" };
                    LOG.debug("creating sasl client: {}={};service={};serviceHostname={}", entity, clientPrincipalName, serviceName, serviceHostname);
                    final SaslClient saslClient = Sasl.createSaslClient(mechs, clientPrincipalName, serviceName, serviceHostname, null, new SaslClientCallbackHandler(null, entity));
                    return saslClient;
                }
            });
            return saslClient;
        }
        catch (Exception e) {
            LOG.error("Exception while trying to create SASL client", e);
            return null;
        }
    }
    
    public static SaslServer createSaslServer(final Subject subject, final String protocol, final String serverName, final CallbackHandler callbackHandler, final Logger LOG) {
        if (subject != null) {
            if (subject.getPrincipals().size() > 0) {
                try {
                    final Object[] principals = subject.getPrincipals().toArray();
                    final Principal servicePrincipal = (Principal)principals[0];
                    final String servicePrincipalNameAndHostname = servicePrincipal.getName();
                    int indexOf = servicePrincipalNameAndHostname.indexOf("/");
                    final String servicePrincipalName = servicePrincipalNameAndHostname.substring(0, indexOf);
                    final String serviceHostnameAndKerbDomain = servicePrincipalNameAndHostname.substring(indexOf + 1, servicePrincipalNameAndHostname.length());
                    indexOf = serviceHostnameAndKerbDomain.indexOf("@");
                    final String serviceHostname = serviceHostnameAndKerbDomain.substring(0, indexOf);
                    final String mech = "GSSAPI";
                    LOG.debug("serviceHostname is '" + serviceHostname + "'");
                    LOG.debug("servicePrincipalName is '" + servicePrincipalName + "'");
                    LOG.debug("SASL mechanism(mech) is 'GSSAPI'");
                    final boolean usingNativeJgss = Boolean.getBoolean("sun.security.jgss.native");
                    if (usingNativeJgss) {
                        try {
                            final GSSManager manager = GSSManager.getInstance();
                            final Oid krb5Mechanism = new Oid("1.2.840.113554.1.2.2");
                            final GSSName gssName = manager.createName(servicePrincipalName + "@" + serviceHostname, GSSName.NT_HOSTBASED_SERVICE);
                            final GSSCredential cred = manager.createCredential(gssName, 0, krb5Mechanism, 2);
                            subject.getPrivateCredentials().add(cred);
                            LOG.debug("Added private credential to service principal name: '{}', GSSCredential name: {}", servicePrincipalName, cred.getName());
                        }
                        catch (GSSException ex) {
                            LOG.warn("Cannot add private credential to subject; clients authentication may fail", ex);
                        }
                    }
                    try {
                        return Subject.doAs(subject, (PrivilegedExceptionAction<SaslServer>)new PrivilegedExceptionAction<SaslServer>() {
                            @Override
                            public SaslServer run() {
                                try {
                                    final SaslServer saslServer = Sasl.createSaslServer("GSSAPI", servicePrincipalName, serviceHostname, null, callbackHandler);
                                    return saslServer;
                                }
                                catch (SaslException e) {
                                    LOG.error("Zookeeper Server failed to create a SaslServer to interact with a client during session initiation: ", e);
                                    return null;
                                }
                            }
                        });
                    }
                    catch (PrivilegedActionException e) {
                        LOG.error("Zookeeper Quorum member experienced a PrivilegedActionException exception while creating a SaslServer using a JAAS principal context:", e);
                    }
                }
                catch (IndexOutOfBoundsException e2) {
                    LOG.error("server principal name/hostname determination error: ", e2);
                    return null;
                }
            }
            try {
                final SaslServer saslServer = Sasl.createSaslServer("DIGEST-MD5", protocol, serverName, null, callbackHandler);
                return saslServer;
            }
            catch (SaslException e3) {
                LOG.error("Zookeeper Quorum member failed to create a SaslServer to interact with a client during session initiation", e3);
            }
        }
        return null;
    }
    
    public static String getServerPrincipal(final String principalConfig, final String hostname) {
        final String[] components = getComponents(principalConfig);
        if (components == null || components.length != 2 || !components[1].equals("_HOST")) {
            return principalConfig;
        }
        return replacePattern(components, hostname);
    }
    
    private static String[] getComponents(final String principalConfig) {
        if (principalConfig == null) {
            return null;
        }
        return principalConfig.split("[/]");
    }
    
    private static String replacePattern(final String[] components, final String hostname) {
        return components[0] + "/" + hostname.toLowerCase();
    }
}
