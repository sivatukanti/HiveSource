// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc.authentication;

import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.Name;
import javax.naming.directory.SearchResult;
import javax.naming.directory.SearchControls;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.util.Hashtable;
import javax.naming.directory.InitialDirContext;
import java.security.PrivilegedExceptionAction;
import javax.naming.directory.DirContext;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;
import javax.naming.AuthenticationException;
import java.util.Properties;

public final class LDAPAuthenticationSchemeImpl extends JNDIAuthenticationSchemeBase
{
    private static final String dfltLDAPURL = "ldap://";
    private String searchBaseDN;
    private String leftSearchFilter;
    private String rightSearchFilter;
    private boolean useUserPropertyAsDN;
    private String searchAuthDN;
    private String searchAuthPW;
    private static final String[] attrDN;
    private static final String LDAP_SEARCH_BASE = "derby.authentication.ldap.searchBase";
    private static final String LDAP_SEARCH_FILTER = "derby.authentication.ldap.searchFilter";
    private static final String LDAP_SEARCH_AUTH_DN = "derby.authentication.ldap.searchAuthDN";
    private static final String LDAP_SEARCH_AUTH_PW = "derby.authentication.ldap.searchAuthPW";
    private static final String LDAP_LOCAL_USER_DN = "derby.user";
    private static final String LDAP_SEARCH_FILTER_USERNAME = "%USERNAME%";
    
    public LDAPAuthenticationSchemeImpl(final JNDIAuthenticationService jndiAuthenticationService, final Properties properties) {
        super(jndiAuthenticationService, properties);
    }
    
    public boolean authenticateUser(final String s, final String value, final String s2, final Properties properties) throws SQLException {
        if (s == null || s.length() == 0 || value == null || value.length() == 0) {
            return false;
        }
        try {
            final Properties properties2 = (Properties)this.initDirContextEnv.clone();
            String value2 = null;
            if (this.useUserPropertyAsDN) {
                value2 = this.authenticationService.getProperty("derby.user.");
            }
            if (value2 == null) {
                value2 = this.getDNFromUID(s);
            }
            properties2.put("java.naming.security.principal", value2);
            properties2.put("java.naming.security.credentials", value);
            this.privInitialDirContext(properties2);
            return true;
        }
        catch (AuthenticationException ex2) {
            return false;
        }
        catch (NameNotFoundException ex3) {
            return false;
        }
        catch (NamingException ex) {
            throw JNDIAuthenticationSchemeBase.getLoginSQLException(ex);
        }
    }
    
    private DirContext privInitialDirContext(final Properties properties) throws NamingException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<DirContext>)new PrivilegedExceptionAction<DirContext>() {
                public DirContext run() throws NamingException {
                    return new InitialDirContext(properties);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (NamingException)ex.getCause();
        }
    }
    
    @Override
    protected void setJNDIProviderProperties() {
        if (this.initDirContextEnv.getProperty("java.naming.factory.initial") == null) {
            this.initDirContextEnv.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        }
        if (this.initDirContextEnv.getProperty("java.naming.provider.url") == null) {
            final String property = this.authenticationService.getProperty("derby.authentication.server");
            if (property == null) {
                Monitor.logTextMessage("A011", "derby.authentication.server");
                this.providerURL = "ldap:///";
            }
            else if (property.startsWith("ldap://") || property.startsWith("ldaps://")) {
                this.providerURL = property;
            }
            else if (property.startsWith("//")) {
                this.providerURL = "ldap:" + property;
            }
            else {
                this.providerURL = "ldap://" + property;
            }
            this.initDirContextEnv.put("java.naming.provider.url", this.providerURL);
        }
        if (this.initDirContextEnv.getProperty("java.naming.security.authentication") == null) {
            this.initDirContextEnv.put("java.naming.security.authentication", "simple");
        }
        final String property2 = this.authenticationService.getProperty("derby.authentication.ldap.searchBase");
        if (property2 != null) {
            this.searchBaseDN = property2;
        }
        else {
            this.searchBaseDN = "";
        }
        this.searchAuthDN = this.authenticationService.getProperty("derby.authentication.ldap.searchAuthDN");
        this.searchAuthPW = this.authenticationService.getProperty("derby.authentication.ldap.searchAuthPW");
        final String property3 = this.authenticationService.getProperty("derby.authentication.ldap.searchFilter");
        if (property3 == null) {
            this.leftSearchFilter = "(&(objectClass=inetOrgPerson)(uid=";
            this.rightSearchFilter = "))";
        }
        else if (StringUtil.SQLEqualsIgnoreCase(property3, "derby.user")) {
            this.leftSearchFilter = "(&(objectClass=inetOrgPerson)(uid=";
            this.rightSearchFilter = "))";
            this.useUserPropertyAsDN = true;
        }
        else if (property3.indexOf("%USERNAME%") != -1) {
            this.leftSearchFilter = property3.substring(0, property3.indexOf("%USERNAME%"));
            this.rightSearchFilter = property3.substring(property3.indexOf("%USERNAME%") + "%USERNAME%".length());
        }
        else {
            this.leftSearchFilter = "(&(" + property3 + ")" + "(objectClass=inetOrgPerson)(uid=";
            this.rightSearchFilter = "))";
        }
    }
    
    private String getDNFromUID(final String str) throws NamingException {
        Properties initDirContextEnv;
        if (this.searchAuthDN != null) {
            initDirContextEnv = (Properties)this.initDirContextEnv.clone();
            initDirContextEnv.put("java.naming.security.principal", this.searchAuthDN);
            initDirContextEnv.put("java.naming.security.credentials", this.searchAuthPW);
        }
        else {
            initDirContextEnv = this.initDirContextEnv;
        }
        final DirContext privInitialDirContext = this.privInitialDirContext(initDirContextEnv);
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(2);
        searchControls.setReturningAttributes(LDAPAuthenticationSchemeImpl.attrDN);
        final NamingEnumeration<SearchResult> search = privInitialDirContext.search(this.searchBaseDN, this.leftSearchFilter + str + this.rightSearchFilter, searchControls);
        if (search == null || !search.hasMore()) {
            throw new NameNotFoundException();
        }
        final SearchResult searchResult = search.next();
        if (search.hasMore()) {
            throw new NameNotFoundException();
        }
        final NameParser nameParser = privInitialDirContext.getNameParser(this.searchBaseDN);
        final Name parse = nameParser.parse(this.searchBaseDN);
        if (parse == null) {
            throw new NameNotFoundException();
        }
        parse.addAll(nameParser.parse(searchResult.getName()));
        return parse.toString();
    }
    
    static {
        attrDN = new String[] { "dn" };
    }
}
