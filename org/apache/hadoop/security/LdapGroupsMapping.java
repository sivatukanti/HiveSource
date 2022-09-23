// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.IOException;
import javax.naming.directory.InitialDirContext;
import com.sun.jndi.ldap.LdapCtxFactory;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import javax.naming.directory.Attribute;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import java.util.Collections;
import javax.naming.NamingException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class LdapGroupsMapping implements GroupMappingServiceProvider, Configurable
{
    public static final String LDAP_CONFIG_PREFIX = "hadoop.security.group.mapping.ldap";
    public static final String LDAP_URL_KEY = "hadoop.security.group.mapping.ldap.url";
    public static final String LDAP_URL_DEFAULT = "";
    public static final String LDAP_USE_SSL_KEY = "hadoop.security.group.mapping.ldap.ssl";
    public static final Boolean LDAP_USE_SSL_DEFAULT;
    public static final String LDAP_KEYSTORE_KEY = "hadoop.security.group.mapping.ldap.ssl.keystore";
    public static final String LDAP_KEYSTORE_DEFAULT = "";
    public static final String LDAP_KEYSTORE_PASSWORD_KEY = "hadoop.security.group.mapping.ldap.ssl.keystore.password";
    public static final String LDAP_KEYSTORE_PASSWORD_DEFAULT = "";
    public static final String LDAP_KEYSTORE_PASSWORD_FILE_KEY = "hadoop.security.group.mapping.ldap.ssl.keystore.password.file";
    public static final String LDAP_KEYSTORE_PASSWORD_FILE_DEFAULT = "";
    public static final String LDAP_TRUSTSTORE_KEY = "hadoop.security.group.mapping.ldap.ssl.truststore";
    public static final String LDAP_TRUSTSTORE_PASSWORD_KEY = "hadoop.security.group.mapping.ldap.ssl.truststore.password";
    public static final String LDAP_TRUSTSTORE_PASSWORD_FILE_KEY = "hadoop.security.group.mapping.ldap.ssl.truststore.password.file";
    public static final String BIND_USER_KEY = "hadoop.security.group.mapping.ldap.bind.user";
    public static final String BIND_USER_DEFAULT = "";
    public static final String BIND_PASSWORD_KEY = "hadoop.security.group.mapping.ldap.bind.password";
    public static final String BIND_PASSWORD_DEFAULT = "";
    public static final String BIND_PASSWORD_FILE_KEY = "hadoop.security.group.mapping.ldap.bind.password.file";
    public static final String BIND_PASSWORD_FILE_DEFAULT = "";
    public static final String BASE_DN_KEY = "hadoop.security.group.mapping.ldap.base";
    public static final String BASE_DN_DEFAULT = "";
    public static final String USER_BASE_DN_KEY = "hadoop.security.group.mapping.ldap.userbase";
    public static final String GROUP_BASE_DN_KEY = "hadoop.security.group.mapping.ldap.groupbase";
    public static final String USER_SEARCH_FILTER_KEY = "hadoop.security.group.mapping.ldap.search.filter.user";
    public static final String USER_SEARCH_FILTER_DEFAULT = "(&(objectClass=user)(sAMAccountName={0}))";
    public static final String GROUP_SEARCH_FILTER_KEY = "hadoop.security.group.mapping.ldap.search.filter.group";
    public static final String GROUP_SEARCH_FILTER_DEFAULT = "(objectClass=group)";
    public static final String MEMBEROF_ATTR_KEY = "hadoop.security.group.mapping.ldap.search.attr.memberof";
    public static final String MEMBEROF_ATTR_DEFAULT = "";
    public static final String GROUP_MEMBERSHIP_ATTR_KEY = "hadoop.security.group.mapping.ldap.search.attr.member";
    public static final String GROUP_MEMBERSHIP_ATTR_DEFAULT = "member";
    public static final String GROUP_NAME_ATTR_KEY = "hadoop.security.group.mapping.ldap.search.attr.group.name";
    public static final String GROUP_NAME_ATTR_DEFAULT = "cn";
    public static final String GROUP_HIERARCHY_LEVELS_KEY = "hadoop.security.group.mapping.ldap.search.group.hierarchy.levels";
    public static final int GROUP_HIERARCHY_LEVELS_DEFAULT = 0;
    public static final String POSIX_UID_ATTR_KEY = "hadoop.security.group.mapping.ldap.posix.attr.uid.name";
    public static final String POSIX_UID_ATTR_DEFAULT = "uidNumber";
    public static final String POSIX_GID_ATTR_KEY = "hadoop.security.group.mapping.ldap.posix.attr.gid.name";
    public static final String POSIX_GID_ATTR_DEFAULT = "gidNumber";
    public static final String POSIX_GROUP = "posixGroup";
    public static final String POSIX_ACCOUNT = "posixAccount";
    public static final String DIRECTORY_SEARCH_TIMEOUT = "hadoop.security.group.mapping.ldap.directory.search.timeout";
    public static final int DIRECTORY_SEARCH_TIMEOUT_DEFAULT = 10000;
    public static final String CONNECTION_TIMEOUT = "hadoop.security.group.mapping.ldap.connection.timeout.ms";
    public static final int CONNECTION_TIMEOUT_DEFAULT = 60000;
    public static final String READ_TIMEOUT = "hadoop.security.group.mapping.ldap.read.timeout.ms";
    public static final int READ_TIMEOUT_DEFAULT = 60000;
    private static final Logger LOG;
    static final SearchControls SEARCH_CONTROLS;
    private DirContext ctx;
    private Configuration conf;
    private String ldapUrl;
    private boolean useSsl;
    private String keystore;
    private String keystorePass;
    private String truststore;
    private String truststorePass;
    private String bindUser;
    private String bindPassword;
    private String userbaseDN;
    private String groupbaseDN;
    private String groupSearchFilter;
    private String userSearchFilter;
    private String memberOfAttr;
    private String groupMemberAttr;
    private String groupNameAttr;
    private int groupHierarchyLevels;
    private String posixUidAttr;
    private String posixGidAttr;
    private boolean isPosix;
    private boolean useOneQuery;
    public static final int RECONNECT_RETRY_COUNT = 3;
    
    @Override
    public synchronized List<String> getGroups(final String user) {
        int retry = 0;
        while (retry < 3) {
            try {
                return this.doGetGroups(user, this.groupHierarchyLevels);
            }
            catch (NamingException e) {
                LdapGroupsMapping.LOG.warn("Failed to get groups for user " + user + " (retry=" + retry + ") by " + e);
                LdapGroupsMapping.LOG.trace("TRACE", e);
                this.ctx = null;
                ++retry;
                continue;
            }
            break;
        }
        return Collections.emptyList();
    }
    
    private String getRelativeDistinguishedName(final String distinguishedName) throws NamingException {
        final LdapName ldn = new LdapName(distinguishedName);
        final List<Rdn> rdns = ldn.getRdns();
        if (rdns.isEmpty()) {
            throw new NamingException("DN is empty");
        }
        final Rdn rdn = rdns.get(rdns.size() - 1);
        if (rdn.getType().equalsIgnoreCase(this.groupNameAttr)) {
            final String groupName = (String)rdn.getValue();
            return groupName;
        }
        throw new NamingException("Unable to find RDN: The DN " + distinguishedName + " is malformed.");
    }
    
    private NamingEnumeration<SearchResult> lookupPosixGroup(final SearchResult result, final DirContext c) throws NamingException {
        String gidNumber = null;
        String uidNumber = null;
        final Attribute gidAttribute = result.getAttributes().get(this.posixGidAttr);
        final Attribute uidAttribute = result.getAttributes().get(this.posixUidAttr);
        String reason = "";
        if (gidAttribute == null) {
            reason = "Can't find attribute '" + this.posixGidAttr + "'.";
        }
        else {
            gidNumber = gidAttribute.get().toString();
        }
        if (uidAttribute == null) {
            reason = "Can't find attribute '" + this.posixUidAttr + "'.";
        }
        else {
            uidNumber = uidAttribute.get().toString();
        }
        if (uidNumber != null && gidNumber != null) {
            return c.search(this.groupbaseDN, "(&" + this.groupSearchFilter + "(|(" + this.posixGidAttr + "={0})(" + this.groupMemberAttr + "={1})))", new Object[] { gidNumber, uidNumber }, LdapGroupsMapping.SEARCH_CONTROLS);
        }
        throw new NamingException("The server does not support posixGroups semantics. Reason: " + reason + " Returned user object: " + result.toString());
    }
    
    private List<String> lookupGroup(final SearchResult result, final DirContext c, final int goUpHierarchy) throws NamingException {
        List<String> groups = new ArrayList<String>();
        final Set<String> groupDNs = new HashSet<String>();
        NamingEnumeration<SearchResult> groupResults = null;
        if (this.isPosix) {
            groupResults = this.lookupPosixGroup(result, c);
        }
        else {
            final String userDn = result.getNameInNamespace();
            groupResults = c.search(this.groupbaseDN, "(&" + this.groupSearchFilter + "(" + this.groupMemberAttr + "={0}))", new Object[] { userDn }, LdapGroupsMapping.SEARCH_CONTROLS);
        }
        if (groupResults != null) {
            while (groupResults.hasMoreElements()) {
                final SearchResult groupResult = groupResults.nextElement();
                this.getGroupNames(groupResult, groups, groupDNs, goUpHierarchy > 0);
            }
            if (goUpHierarchy > 0 && !this.isPosix) {
                final Set<String> groupset = new HashSet<String>(groups);
                this.goUpGroupHierarchy(groupDNs, goUpHierarchy, groupset);
                groups = new ArrayList<String>(groupset);
            }
        }
        return groups;
    }
    
    List<String> doGetGroups(final String user, final int goUpHierarchy) throws NamingException {
        final DirContext c = this.getDirContext();
        final NamingEnumeration<SearchResult> results = c.search(this.userbaseDN, this.userSearchFilter, new Object[] { user }, LdapGroupsMapping.SEARCH_CONTROLS);
        if (!results.hasMoreElements()) {
            if (LdapGroupsMapping.LOG.isDebugEnabled()) {
                LdapGroupsMapping.LOG.debug("doGetGroups(" + user + ") returned no groups because the user is not found.");
            }
            return new ArrayList<String>();
        }
        final SearchResult result = results.nextElement();
        List<String> groups = null;
        if (this.useOneQuery) {
            try {
                final Attribute groupDNAttr = result.getAttributes().get(this.memberOfAttr);
                if (groupDNAttr == null) {
                    throw new NamingException("The user object does not have '" + this.memberOfAttr + "' attribute.Returned user object: " + result.toString());
                }
                groups = new ArrayList<String>();
                final NamingEnumeration groupEnumeration = groupDNAttr.getAll();
                while (groupEnumeration.hasMore()) {
                    final String groupDN = groupEnumeration.next().toString();
                    groups.add(this.getRelativeDistinguishedName(groupDN));
                }
            }
            catch (NamingException e) {
                LdapGroupsMapping.LOG.info("Failed to get groups from the first lookup. Initiating the second LDAP query using the user's DN.", e);
            }
        }
        if (groups == null || groups.isEmpty() || goUpHierarchy > 0) {
            groups = this.lookupGroup(result, c, goUpHierarchy);
        }
        if (LdapGroupsMapping.LOG.isDebugEnabled()) {
            LdapGroupsMapping.LOG.debug("doGetGroups(" + user + ") returned " + groups);
        }
        return groups;
    }
    
    void getGroupNames(final SearchResult groupResult, final Collection<String> groups, final Collection<String> groupDNs, final boolean doGetDNs) throws NamingException {
        final Attribute groupName = groupResult.getAttributes().get(this.groupNameAttr);
        if (groupName == null) {
            throw new NamingException("The group object does not have attribute '" + this.groupNameAttr + "'.");
        }
        groups.add(groupName.get().toString());
        if (doGetDNs) {
            groupDNs.add(groupResult.getNameInNamespace());
        }
    }
    
    void goUpGroupHierarchy(final Set<String> groupDNs, final int goUpHierarchy, final Set<String> groups) throws NamingException {
        if (goUpHierarchy <= 0 || groups.isEmpty()) {
            return;
        }
        final DirContext context = this.getDirContext();
        final Set<String> nextLevelGroups = new HashSet<String>();
        final StringBuilder filter = new StringBuilder();
        filter.append("(&").append(this.groupSearchFilter).append("(|");
        for (final String dn : groupDNs) {
            filter.append("(").append(this.groupMemberAttr).append("=").append(dn).append(")");
        }
        filter.append("))");
        LdapGroupsMapping.LOG.debug("Ldap group query string: " + filter.toString());
        final NamingEnumeration<SearchResult> groupResults = context.search(this.groupbaseDN, filter.toString(), LdapGroupsMapping.SEARCH_CONTROLS);
        while (groupResults.hasMoreElements()) {
            final SearchResult groupResult = groupResults.nextElement();
            this.getGroupNames(groupResult, groups, nextLevelGroups, true);
        }
        this.goUpGroupHierarchy(nextLevelGroups, goUpHierarchy - 1, groups);
    }
    
    DirContext getDirContext() throws NamingException {
        if (this.ctx == null) {
            final Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", LdapCtxFactory.class.getName());
            env.put("java.naming.provider.url", this.ldapUrl);
            env.put("java.naming.security.authentication", "simple");
            if (this.useSsl) {
                env.put("java.naming.security.protocol", "ssl");
                if (!this.keystore.isEmpty()) {
                    System.setProperty("javax.net.ssl.keyStore", this.keystore);
                }
                if (!this.keystorePass.isEmpty()) {
                    System.setProperty("javax.net.ssl.keyStorePassword", this.keystorePass);
                }
                if (!this.truststore.isEmpty()) {
                    System.setProperty("javax.net.ssl.trustStore", this.truststore);
                }
                if (!this.truststorePass.isEmpty()) {
                    System.setProperty("javax.net.ssl.trustStorePassword", this.truststorePass);
                }
            }
            env.put("java.naming.security.principal", this.bindUser);
            env.put("java.naming.security.credentials", this.bindPassword);
            env.put("com.sun.jndi.ldap.connect.timeout", this.conf.get("hadoop.security.group.mapping.ldap.connection.timeout.ms", String.valueOf(60000)));
            env.put("com.sun.jndi.ldap.read.timeout", this.conf.get("hadoop.security.group.mapping.ldap.read.timeout.ms", String.valueOf(60000)));
            this.ctx = new InitialDirContext(env);
        }
        return this.ctx;
    }
    
    @Override
    public void cacheGroupsRefresh() throws IOException {
    }
    
    @Override
    public void cacheGroupsAdd(final List<String> groups) throws IOException {
    }
    
    @Override
    public synchronized Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public synchronized void setConf(final Configuration conf) {
        this.ldapUrl = conf.get("hadoop.security.group.mapping.ldap.url", "");
        if (this.ldapUrl == null || this.ldapUrl.isEmpty()) {
            throw new RuntimeException("LDAP URL is not configured");
        }
        this.useSsl = conf.getBoolean("hadoop.security.group.mapping.ldap.ssl", LdapGroupsMapping.LDAP_USE_SSL_DEFAULT);
        if (this.useSsl) {
            this.loadSslConf(conf);
        }
        this.bindUser = conf.get("hadoop.security.group.mapping.ldap.bind.user", "");
        this.bindPassword = this.getPassword(conf, "hadoop.security.group.mapping.ldap.bind.password", "");
        if (this.bindPassword.isEmpty()) {
            this.bindPassword = this.extractPassword(conf.get("hadoop.security.group.mapping.ldap.bind.password.file", ""));
        }
        final String baseDN = conf.getTrimmed("hadoop.security.group.mapping.ldap.base", "");
        this.userbaseDN = conf.getTrimmed("hadoop.security.group.mapping.ldap.userbase", baseDN);
        if (LdapGroupsMapping.LOG.isDebugEnabled()) {
            LdapGroupsMapping.LOG.debug("Usersearch baseDN: " + this.userbaseDN);
        }
        this.groupbaseDN = conf.getTrimmed("hadoop.security.group.mapping.ldap.groupbase", baseDN);
        if (LdapGroupsMapping.LOG.isDebugEnabled()) {
            LdapGroupsMapping.LOG.debug("Groupsearch baseDN: " + this.userbaseDN);
        }
        this.groupSearchFilter = conf.get("hadoop.security.group.mapping.ldap.search.filter.group", "(objectClass=group)");
        this.userSearchFilter = conf.get("hadoop.security.group.mapping.ldap.search.filter.user", "(&(objectClass=user)(sAMAccountName={0}))");
        this.isPosix = (this.groupSearchFilter.contains("posixGroup") && this.userSearchFilter.contains("posixAccount"));
        this.memberOfAttr = conf.get("hadoop.security.group.mapping.ldap.search.attr.memberof", "");
        this.useOneQuery = !this.memberOfAttr.isEmpty();
        this.groupMemberAttr = conf.get("hadoop.security.group.mapping.ldap.search.attr.member", "member");
        this.groupNameAttr = conf.get("hadoop.security.group.mapping.ldap.search.attr.group.name", "cn");
        this.groupHierarchyLevels = conf.getInt("hadoop.security.group.mapping.ldap.search.group.hierarchy.levels", 0);
        this.posixUidAttr = conf.get("hadoop.security.group.mapping.ldap.posix.attr.uid.name", "uidNumber");
        this.posixGidAttr = conf.get("hadoop.security.group.mapping.ldap.posix.attr.gid.name", "gidNumber");
        final int dirSearchTimeout = conf.getInt("hadoop.security.group.mapping.ldap.directory.search.timeout", 10000);
        LdapGroupsMapping.SEARCH_CONTROLS.setTimeLimit(dirSearchTimeout);
        String[] returningAttributes;
        if (this.useOneQuery) {
            returningAttributes = new String[] { this.groupNameAttr, this.posixUidAttr, this.posixGidAttr, this.memberOfAttr };
        }
        else {
            returningAttributes = new String[] { this.groupNameAttr, this.posixUidAttr, this.posixGidAttr };
        }
        LdapGroupsMapping.SEARCH_CONTROLS.setReturningAttributes(returningAttributes);
        this.conf = conf;
    }
    
    private void loadSslConf(final Configuration sslConf) {
        this.keystore = sslConf.get("hadoop.security.group.mapping.ldap.ssl.keystore", "");
        this.keystorePass = this.getPassword(sslConf, "hadoop.security.group.mapping.ldap.ssl.keystore.password", "");
        if (this.keystorePass.isEmpty()) {
            this.keystorePass = this.extractPassword(sslConf.get("hadoop.security.group.mapping.ldap.ssl.keystore.password.file", ""));
        }
        this.truststore = sslConf.get("hadoop.security.group.mapping.ldap.ssl.truststore", "");
        this.truststorePass = this.getPasswordFromCredentialProviders(sslConf, "hadoop.security.group.mapping.ldap.ssl.truststore.password", "");
        if (this.truststorePass.isEmpty()) {
            this.truststorePass = this.extractPassword(sslConf.get("hadoop.security.group.mapping.ldap.ssl.truststore.password.file", ""));
        }
    }
    
    String getPasswordFromCredentialProviders(final Configuration conf, final String alias, final String defaultPass) {
        String password = defaultPass;
        try {
            final char[] passchars = conf.getPasswordFromCredentialProviders(alias);
            if (passchars != null) {
                password = new String(passchars);
            }
        }
        catch (IOException ioe) {
            LdapGroupsMapping.LOG.warn("Exception while trying to get password for alias {}: {}", alias, ioe);
        }
        return password;
    }
    
    @Deprecated
    String getPassword(final Configuration conf, final String alias, final String defaultPass) {
        String password = defaultPass;
        try {
            final char[] passchars = conf.getPassword(alias);
            if (passchars != null) {
                password = new String(passchars);
            }
        }
        catch (IOException ioe) {
            LdapGroupsMapping.LOG.warn("Exception while trying to get password for alias " + alias + ": ", ioe);
        }
        return password;
    }
    
    String extractPassword(final String pwFile) {
        if (pwFile.isEmpty()) {
            return "";
        }
        final StringBuilder password = new StringBuilder();
        try (final Reader reader = new InputStreamReader(new FileInputStream(pwFile), StandardCharsets.UTF_8)) {
            for (int c = reader.read(); c > -1; c = reader.read()) {
                password.append((char)c);
            }
            return password.toString().trim();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Could not read password file: " + pwFile, ioe);
        }
    }
    
    static {
        LDAP_USE_SSL_DEFAULT = false;
        LOG = LoggerFactory.getLogger(LdapGroupsMapping.class);
        (SEARCH_CONTROLS = new SearchControls()).setSearchScope(2);
    }
}
