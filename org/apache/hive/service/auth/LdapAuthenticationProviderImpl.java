// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import org.apache.hive.service.ServiceUtils;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.security.sasl.AuthenticationException;
import java.util.Hashtable;
import org.apache.hadoop.hive.conf.HiveConf;

public class LdapAuthenticationProviderImpl implements PasswdAuthenticationProvider
{
    private final String ldapURL;
    private final String baseDN;
    private final String ldapDomain;
    
    LdapAuthenticationProviderImpl() {
        final HiveConf conf = new HiveConf();
        this.ldapURL = conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_PLAIN_LDAP_URL);
        this.baseDN = conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_PLAIN_LDAP_BASEDN);
        this.ldapDomain = conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_PLAIN_LDAP_DOMAIN);
    }
    
    @Override
    public void Authenticate(String user, final String password) throws AuthenticationException {
        final Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("java.naming.provider.url", this.ldapURL);
        if (!this.hasDomain(user) && this.ldapDomain != null) {
            user = user + "@" + this.ldapDomain;
        }
        if (password == null || password.isEmpty() || password.getBytes()[0] == 0) {
            throw new AuthenticationException("Error validating LDAP user: a null or blank password has been provided");
        }
        String bindDN;
        if (this.baseDN == null) {
            bindDN = user;
        }
        else {
            bindDN = "uid=" + user + "," + this.baseDN;
        }
        env.put("java.naming.security.authentication", "simple");
        env.put("java.naming.security.principal", bindDN);
        env.put("java.naming.security.credentials", password);
        try {
            final Context ctx = new InitialDirContext(env);
            ctx.close();
        }
        catch (NamingException e) {
            throw new AuthenticationException("Error validating LDAP user", e);
        }
    }
    
    private boolean hasDomain(final String userName) {
        return ServiceUtils.indexOfDomainMatch(userName) > 0;
    }
}
