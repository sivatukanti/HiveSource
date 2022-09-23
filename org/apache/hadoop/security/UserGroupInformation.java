// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.apache.hadoop.security.authentication.util.KerberosUtil;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.EnumMap;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.hadoop.io.retry.RetryPolicies;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.util.Shell;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.spi.LoginModule;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.metrics2.lib.MutableGaugeInt;
import org.apache.hadoop.metrics2.lib.MutableGaugeLong;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableRate;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.slf4j.LoggerFactory;
import java.security.PrivilegedActionException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import java.util.List;
import org.apache.hadoop.util.Time;
import javax.security.auth.DestroyFailedException;
import org.apache.hadoop.io.retry.RetryPolicy;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.kerberos.KerberosTicket;
import java.io.FileNotFoundException;
import java.io.File;
import org.apache.hadoop.util.StringUtils;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.security.AccessControlContext;
import java.security.AccessController;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.hadoop.util.PlatformName;
import org.apache.hadoop.security.authentication.util.KerberosName;
import org.apache.hadoop.metrics2.lib.MutableQuantiles;
import java.io.IOException;
import java.security.Principal;
import javax.security.auth.Subject;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class UserGroupInformation
{
    @VisibleForTesting
    static final Logger LOG;
    private static final float TICKET_RENEW_WINDOW = 0.8f;
    private static boolean shouldRenewImmediatelyForTests;
    static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";
    static final String HADOOP_PROXY_USER = "HADOOP_PROXY_USER";
    static UgiMetrics metrics;
    private static AuthenticationMethod authenticationMethod;
    private static Groups groups;
    private static long kerberosMinSecondsBeforeRelogin;
    private static Configuration conf;
    public static final String HADOOP_TOKEN_FILE_LOCATION = "HADOOP_TOKEN_FILE_LOCATION";
    private static final AtomicReference<UserGroupInformation> loginUserRef;
    private final Subject subject;
    private final User user;
    private static String OS_LOGIN_MODULE_NAME;
    private static Class<? extends Principal> OS_PRINCIPAL_CLASS;
    private static final boolean windows;
    private static final boolean is64Bit;
    private static final boolean aix;
    
    @VisibleForTesting
    public static void setShouldRenewImmediatelyForTests(final boolean immediate) {
        UserGroupInformation.shouldRenewImmediatelyForTests = immediate;
    }
    
    public static void reattachMetrics() {
        UgiMetrics.reattach();
    }
    
    public static boolean isInitialized() {
        return UserGroupInformation.conf != null;
    }
    
    private static void ensureInitialized() {
        if (!isInitialized()) {
            synchronized (UserGroupInformation.class) {
                if (!isInitialized()) {
                    initialize(new Configuration(), false);
                }
            }
        }
    }
    
    private static synchronized void initialize(final Configuration conf, final boolean overrideNameRules) {
        UserGroupInformation.authenticationMethod = SecurityUtil.getAuthenticationMethod(conf);
        Label_0036: {
            if (!overrideNameRules) {
                if (HadoopKerberosName.hasRulesBeenSet()) {
                    break Label_0036;
                }
            }
            try {
                HadoopKerberosName.setConfiguration(conf);
            }
            catch (IOException ioe) {
                throw new RuntimeException("Problem with Kerberos auth_to_local name configuration", ioe);
            }
            try {
                UserGroupInformation.kerberosMinSecondsBeforeRelogin = 1000L * conf.getLong("hadoop.kerberos.min.seconds.before.relogin", 60L);
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid attribute value for hadoop.kerberos.min.seconds.before.relogin of " + conf.get("hadoop.kerberos.min.seconds.before.relogin"));
            }
        }
        if (!(UserGroupInformation.groups instanceof TestingGroups)) {
            UserGroupInformation.groups = Groups.getUserToGroupsMappingService(conf);
        }
        UserGroupInformation.conf = conf;
        if (UserGroupInformation.metrics.getGroupsQuantiles == null) {
            final int[] intervals = conf.getInts("hadoop.user.group.metrics.percentiles.intervals");
            if (intervals != null && intervals.length > 0) {
                final int length = intervals.length;
                final MutableQuantiles[] getGroupsQuantiles = new MutableQuantiles[length];
                for (int i = 0; i < length; ++i) {
                    getGroupsQuantiles[i] = UserGroupInformation.metrics.registry.newQuantiles("getGroups" + intervals[i] + "s", "Get groups", "ops", "latency", intervals[i]);
                }
                UserGroupInformation.metrics.getGroupsQuantiles = getGroupsQuantiles;
            }
        }
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static void setConfiguration(final Configuration conf) {
        initialize(conf, true);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public static void reset() {
        UserGroupInformation.authenticationMethod = null;
        UserGroupInformation.conf = null;
        UserGroupInformation.groups = null;
        UserGroupInformation.kerberosMinSecondsBeforeRelogin = 0L;
        setLoginUser(null);
        KerberosName.setRules(null);
    }
    
    public static boolean isSecurityEnabled() {
        return !isAuthenticationMethodEnabled(AuthenticationMethod.SIMPLE);
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Evolving
    private static boolean isAuthenticationMethodEnabled(final AuthenticationMethod method) {
        ensureInitialized();
        return UserGroupInformation.authenticationMethod == method;
    }
    
    private static String getOSLoginModuleName() {
        if (!PlatformName.IBM_JAVA) {
            return UserGroupInformation.windows ? "com.sun.security.auth.module.NTLoginModule" : "com.sun.security.auth.module.UnixLoginModule";
        }
        if (UserGroupInformation.windows) {
            return UserGroupInformation.is64Bit ? "com.ibm.security.auth.module.Win64LoginModule" : "com.ibm.security.auth.module.NTLoginModule";
        }
        if (UserGroupInformation.aix) {
            return UserGroupInformation.is64Bit ? "com.ibm.security.auth.module.AIX64LoginModule" : "com.ibm.security.auth.module.AIXLoginModule";
        }
        return "com.ibm.security.auth.module.LinuxLoginModule";
    }
    
    private static Class<? extends Principal> getOsPrincipalClass() {
        final ClassLoader cl = ClassLoader.getSystemClassLoader();
        try {
            String principalClass = null;
            if (PlatformName.IBM_JAVA) {
                if (UserGroupInformation.is64Bit) {
                    principalClass = "com.ibm.security.auth.UsernamePrincipal";
                }
                else if (UserGroupInformation.windows) {
                    principalClass = "com.ibm.security.auth.NTUserPrincipal";
                }
                else if (UserGroupInformation.aix) {
                    principalClass = "com.ibm.security.auth.AIXPrincipal";
                }
                else {
                    principalClass = "com.ibm.security.auth.LinuxPrincipal";
                }
            }
            else {
                principalClass = (UserGroupInformation.windows ? "com.sun.security.auth.NTUserPrincipal" : "com.sun.security.auth.UnixPrincipal");
            }
            return (Class<? extends Principal>)cl.loadClass(principalClass);
        }
        catch (ClassNotFoundException e) {
            UserGroupInformation.LOG.error("Unable to find JAAS classes:" + e.getMessage());
            return null;
        }
    }
    
    private static HadoopLoginContext newLoginContext(final String appName, final Subject subject, final HadoopConfiguration loginConf) throws LoginException {
        final Thread t = Thread.currentThread();
        final ClassLoader oldCCL = t.getContextClassLoader();
        t.setContextClassLoader(HadoopLoginModule.class.getClassLoader());
        try {
            return new HadoopLoginContext(appName, subject, loginConf);
        }
        finally {
            t.setContextClassLoader(oldCCL);
        }
    }
    
    private HadoopLoginContext getLogin() {
        final LoginContext login = this.user.getLogin();
        return (login instanceof HadoopLoginContext) ? ((HadoopLoginContext)login) : null;
    }
    
    private void setLogin(final LoginContext login) {
        this.user.setLogin(login);
    }
    
    UserGroupInformation(final Subject subject) {
        this.subject = subject;
        this.user = subject.getPrincipals(User.class).iterator().next();
        if (this.user == null || this.user.getName() == null) {
            throw new IllegalStateException("Subject does not contain a valid User");
        }
    }
    
    public boolean hasKerberosCredentials() {
        return this.user.getAuthenticationMethod() == AuthenticationMethod.KERBEROS;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static UserGroupInformation getCurrentUser() throws IOException {
        final AccessControlContext context = AccessController.getContext();
        final Subject subject = Subject.getSubject(context);
        if (subject == null || subject.getPrincipals(User.class).isEmpty()) {
            return getLoginUser();
        }
        return new UserGroupInformation(subject);
    }
    
    public static UserGroupInformation getBestUGI(final String ticketCachePath, final String user) throws IOException {
        if (ticketCachePath != null) {
            return getUGIFromTicketCache(ticketCachePath, user);
        }
        if (user == null) {
            return getCurrentUser();
        }
        return createRemoteUser(user);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static UserGroupInformation getUGIFromTicketCache(final String ticketCache, final String user) throws IOException {
        if (!isAuthenticationMethodEnabled(AuthenticationMethod.KERBEROS)) {
            return getBestUGI(null, user);
        }
        final LoginParams params = new LoginParams();
        params.put(LoginParam.PRINCIPAL, user);
        params.put(LoginParam.CCACHE, ticketCache);
        return doSubjectLogin(null, params);
    }
    
    public static UserGroupInformation getUGIFromSubject(final Subject subject) throws IOException {
        if (subject == null) {
            throw new KerberosAuthException("Subject must not be null");
        }
        if (subject.getPrincipals(KerberosPrincipal.class).isEmpty()) {
            throw new KerberosAuthException("Provided Subject must contain a KerberosPrincipal");
        }
        return doSubjectLogin(subject, null);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static UserGroupInformation getLoginUser() throws IOException {
        UserGroupInformation loginUser = UserGroupInformation.loginUserRef.get();
        if (loginUser == null) {
            final UserGroupInformation newLoginUser = createLoginUser(null);
            do {
                if (UserGroupInformation.loginUserRef.compareAndSet(null, newLoginUser)) {
                    loginUser = newLoginUser;
                    loginUser.spawnAutoRenewalThreadForUserCreds(false);
                }
                else {
                    loginUser = UserGroupInformation.loginUserRef.get();
                }
            } while (loginUser == null);
        }
        return loginUser;
    }
    
    public static String trimLoginMethod(String userName) {
        final int spaceIndex = userName.indexOf(32);
        if (spaceIndex >= 0) {
            userName = userName.substring(0, spaceIndex);
        }
        return userName;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static void loginUserFromSubject(final Subject subject) throws IOException {
        setLoginUser(createLoginUser(subject));
    }
    
    private static UserGroupInformation createLoginUser(final Subject subject) throws IOException {
        final UserGroupInformation realUser = doSubjectLogin(subject, null);
        UserGroupInformation loginUser = null;
        try {
            String proxyUser = System.getenv("HADOOP_PROXY_USER");
            if (proxyUser == null) {
                proxyUser = System.getProperty("HADOOP_PROXY_USER");
            }
            loginUser = ((proxyUser == null) ? realUser : createProxyUser(proxyUser, realUser));
            String tokenFileLocation = System.getProperty("hadoop.token.files");
            if (tokenFileLocation == null) {
                tokenFileLocation = UserGroupInformation.conf.get("hadoop.token.files");
            }
            if (tokenFileLocation != null) {
                for (final String tokenFileName : StringUtils.getTrimmedStrings(tokenFileLocation)) {
                    if (tokenFileName.length() > 0) {
                        final File tokenFile = new File(tokenFileName);
                        if (tokenFile.exists() && tokenFile.isFile()) {
                            final Credentials cred = Credentials.readTokenStorageFile(tokenFile, UserGroupInformation.conf);
                            loginUser.addCredentials(cred);
                        }
                        else {
                            UserGroupInformation.LOG.info("tokenFile(" + tokenFileName + ") does not exist");
                        }
                    }
                }
            }
            final String fileLocation = System.getenv("HADOOP_TOKEN_FILE_LOCATION");
            if (fileLocation != null) {
                final File source = new File(fileLocation);
                UserGroupInformation.LOG.debug("Reading credentials from location set in {}: {}", "HADOOP_TOKEN_FILE_LOCATION", source.getCanonicalPath());
                if (!source.isFile()) {
                    throw new FileNotFoundException("Source file " + source.getCanonicalPath() + " from " + "HADOOP_TOKEN_FILE_LOCATION" + " not found");
                }
                final Credentials cred2 = Credentials.readTokenStorageFile(source, UserGroupInformation.conf);
                UserGroupInformation.LOG.debug("Loaded {} tokens", (Object)cred2.numberOfTokens());
                loginUser.addCredentials(cred2);
            }
        }
        catch (IOException ioe) {
            UserGroupInformation.LOG.debug("failure to load login credentials", ioe);
            throw ioe;
        }
        if (UserGroupInformation.LOG.isDebugEnabled()) {
            UserGroupInformation.LOG.debug("UGI loginUser:" + loginUser);
        }
        return loginUser;
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    public static void setLoginUser(final UserGroupInformation ugi) {
        UserGroupInformation.loginUserRef.set(ugi);
    }
    
    private String getKeytab() {
        final HadoopLoginContext login = this.getLogin();
        return (login != null) ? ((EnumMap<K, String>)login.getConfiguration().getParameters()).get(LoginParam.KEYTAB) : null;
    }
    
    private boolean isHadoopLogin() {
        return this.getLogin() != null;
    }
    
    public boolean isFromKeytab() {
        return this.hasKerberosCredentials() && this.isHadoopLogin() && this.getKeytab() != null;
    }
    
    private boolean isFromTicket() {
        return this.hasKerberosCredentials() && this.isHadoopLogin() && this.getKeytab() == null;
    }
    
    private KerberosTicket getTGT() {
        final Set<KerberosTicket> tickets = this.subject.getPrivateCredentials(KerberosTicket.class);
        for (final KerberosTicket ticket : tickets) {
            if (SecurityUtil.isOriginalTGT(ticket)) {
                return ticket;
            }
        }
        return null;
    }
    
    private long getRefreshTime(final KerberosTicket tgt) {
        final long start = tgt.getStartTime().getTime();
        final long end = tgt.getEndTime().getTime();
        return start + (long)((end - start) * 0.8f);
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public boolean shouldRelogin() {
        return this.hasKerberosCredentials() && this.isHadoopLogin();
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    void spawnAutoRenewalThreadForUserCreds(final boolean force) {
        if (!force && (!this.shouldRelogin() || this.isFromKeytab())) {
            return;
        }
        final KerberosTicket tgt = this.getTGT();
        if (tgt == null) {
            return;
        }
        final String cmd = UserGroupInformation.conf.get("hadoop.kerberos.kinit.command", "kinit");
        final long nextRefresh = this.getRefreshTime(tgt);
        final Thread t = new Thread(new AutoRenewalForUserCredsRunnable(tgt, cmd, nextRefresh));
        t.setDaemon(true);
        t.setName("TGT Renewer for " + this.getUserName());
        t.start();
    }
    
    @VisibleForTesting
    static long getNextTgtRenewalTime(final long tgtEndTime, final long now, final RetryPolicy rp) throws Exception {
        final long lastRetryTime = tgtEndTime - UserGroupInformation.kerberosMinSecondsBeforeRelogin;
        final RetryPolicy.RetryAction ra = rp.shouldRetry(null, UserGroupInformation.metrics.renewalFailures.value(), 0, false);
        return Math.min(lastRetryTime, now + ra.delayMillis);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static void loginUserFromKeytab(final String user, final String path) throws IOException {
        if (!isSecurityEnabled()) {
            return;
        }
        setLoginUser(loginUserFromKeytabAndReturnUGI(user, path));
        UserGroupInformation.LOG.info("Login successful for user " + user + " using keytab file " + path);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public void logoutUserFromKeytab() throws IOException {
        if (!this.hasKerberosCredentials()) {
            return;
        }
        final HadoopLoginContext login = this.getLogin();
        final String keytabFile = this.getKeytab();
        if (login == null || keytabFile == null) {
            throw new KerberosAuthException("loginUserFromKeyTab must be done first");
        }
        try {
            if (UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("Initiating logout for " + this.getUserName());
            }
            login.logout();
        }
        catch (LoginException le) {
            final KerberosAuthException kae = new KerberosAuthException("Logout failure", le);
            kae.setUser(this.user.toString());
            kae.setKeytabFile(keytabFile);
            throw kae;
        }
        UserGroupInformation.LOG.info("Logout successful for user " + this.getUserName() + " using keytab file " + keytabFile);
    }
    
    public void checkTGTAndReloginFromKeytab() throws IOException {
        this.reloginFromKeytab(true);
    }
    
    @VisibleForTesting
    void fixKerberosTicketOrder() {
        final Set<Object> creds = this.getSubject().getPrivateCredentials();
        synchronized (creds) {
            final Iterator<Object> iter = creds.iterator();
            while (iter.hasNext()) {
                final Object cred = iter.next();
                if (cred instanceof KerberosTicket) {
                    final KerberosTicket ticket = (KerberosTicket)cred;
                    if (ticket.isDestroyed() || ticket.getServer() == null) {
                        UserGroupInformation.LOG.warn("Ticket is already destroyed, remove it.");
                        iter.remove();
                    }
                    else {
                        if (ticket.getServer().getName().startsWith("krbtgt")) {
                            return;
                        }
                        UserGroupInformation.LOG.warn("The first kerberos ticket is not TGT(the server principal is {}), remove and destroy it.", ticket.getServer());
                        iter.remove();
                        try {
                            ticket.destroy();
                        }
                        catch (DestroyFailedException e) {
                            UserGroupInformation.LOG.warn("destroy ticket failed", e);
                        }
                    }
                }
            }
        }
        UserGroupInformation.LOG.warn("Warning, no kerberos ticket found while attempting to renew ticket");
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public void reloginFromKeytab() throws IOException {
        this.reloginFromKeytab(false);
    }
    
    private void reloginFromKeytab(final boolean checkTGT) throws IOException {
        if (!this.shouldRelogin() || !this.isFromKeytab()) {
            return;
        }
        final HadoopLoginContext login = this.getLogin();
        if (login == null) {
            throw new KerberosAuthException("loginUserFromKeyTab must be done first");
        }
        if (checkTGT) {
            final KerberosTicket tgt = this.getTGT();
            if (tgt != null && !UserGroupInformation.shouldRenewImmediatelyForTests && Time.now() < this.getRefreshTime(tgt)) {
                return;
            }
        }
        this.relogin(login);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public void reloginFromTicketCache() throws IOException {
        if (!this.shouldRelogin() || !this.isFromTicket()) {
            return;
        }
        final HadoopLoginContext login = this.getLogin();
        if (login == null) {
            throw new KerberosAuthException("login must be done first");
        }
        this.relogin(login);
    }
    
    private void relogin(final HadoopLoginContext login) throws IOException {
        synchronized (login.getSubjectLock()) {
            if (login == this.getLogin()) {
                this.unprotectedRelogin(login);
            }
        }
    }
    
    private void unprotectedRelogin(HadoopLoginContext login) throws IOException {
        assert Thread.holdsLock(login.getSubjectLock());
        final long now = Time.now();
        if (!this.hasSufficientTimeElapsed(now)) {
            return;
        }
        this.user.setLastLogin(now);
        try {
            if (UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("Initiating logout for " + this.getUserName());
            }
            login.logout();
            login = newLoginContext(login.getAppName(), login.getSubject(), login.getConfiguration());
            if (UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("Initiating re-login for " + this.getUserName());
            }
            login.login();
            this.fixKerberosTicketOrder();
            this.setLogin(login);
        }
        catch (LoginException le) {
            final KerberosAuthException kae = new KerberosAuthException("Login failure", le);
            kae.setUser(this.getUserName());
            throw kae;
        }
    }
    
    public static UserGroupInformation loginUserFromKeytabAndReturnUGI(final String user, final String path) throws IOException {
        if (!isSecurityEnabled()) {
            return getCurrentUser();
        }
        final LoginParams params = new LoginParams();
        params.put(LoginParam.PRINCIPAL, user);
        params.put(LoginParam.KEYTAB, path);
        return doSubjectLogin(null, params);
    }
    
    private boolean hasSufficientTimeElapsed(final long now) {
        if (!UserGroupInformation.shouldRenewImmediatelyForTests && now - this.user.getLastLogin() < UserGroupInformation.kerberosMinSecondsBeforeRelogin) {
            UserGroupInformation.LOG.warn("Not attempting to re-login since the last re-login was attempted less than " + UserGroupInformation.kerberosMinSecondsBeforeRelogin / 1000L + " seconds before. Last Login=" + this.user.getLastLogin());
            return false;
        }
        return true;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static boolean isLoginKeytabBased() throws IOException {
        return getLoginUser().isFromKeytab();
    }
    
    public static boolean isLoginTicketBased() throws IOException {
        return getLoginUser().isFromTicket();
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static UserGroupInformation createRemoteUser(final String user) {
        return createRemoteUser(user, SaslRpcServer.AuthMethod.SIMPLE);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static UserGroupInformation createRemoteUser(final String user, final SaslRpcServer.AuthMethod authMethod) {
        if (user == null || user.isEmpty()) {
            throw new IllegalArgumentException("Null user");
        }
        final Subject subject = new Subject();
        subject.getPrincipals().add(new User(user));
        final UserGroupInformation result = new UserGroupInformation(subject);
        result.setAuthenticationMethod(authMethod);
        return result;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static UserGroupInformation createProxyUser(final String user, final UserGroupInformation realUser) {
        if (user == null || user.isEmpty()) {
            throw new IllegalArgumentException("Null user");
        }
        if (realUser == null) {
            throw new IllegalArgumentException("Null real user");
        }
        final Subject subject = new Subject();
        final Set<Principal> principals = subject.getPrincipals();
        principals.add(new User(user, AuthenticationMethod.PROXY, null));
        principals.add(new RealUser(realUser));
        return new UserGroupInformation(subject);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public UserGroupInformation getRealUser() {
        final Iterator<RealUser> iterator = this.subject.getPrincipals(RealUser.class).iterator();
        if (iterator.hasNext()) {
            final RealUser p = iterator.next();
            return p.getRealUser();
        }
        return null;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static UserGroupInformation createUserForTesting(final String user, final String[] userGroups) {
        ensureInitialized();
        final UserGroupInformation ugi = createRemoteUser(user);
        if (!(UserGroupInformation.groups instanceof TestingGroups)) {
            UserGroupInformation.groups = new TestingGroups(UserGroupInformation.groups);
        }
        ((TestingGroups)UserGroupInformation.groups).setUserGroups(ugi.getShortUserName(), userGroups);
        return ugi;
    }
    
    public static UserGroupInformation createProxyUserForTesting(final String user, final UserGroupInformation realUser, final String[] userGroups) {
        ensureInitialized();
        final UserGroupInformation ugi = createProxyUser(user, realUser);
        if (!(UserGroupInformation.groups instanceof TestingGroups)) {
            UserGroupInformation.groups = new TestingGroups(UserGroupInformation.groups);
        }
        ((TestingGroups)UserGroupInformation.groups).setUserGroups(ugi.getShortUserName(), userGroups);
        return ugi;
    }
    
    public String getShortUserName() {
        return this.user.getShortName();
    }
    
    public String getPrimaryGroupName() throws IOException {
        final List<String> groups = this.getGroups();
        if (groups.isEmpty()) {
            throw new IOException("There is no primary group for UGI " + this);
        }
        return groups.get(0);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public String getUserName() {
        return this.user.getName();
    }
    
    public synchronized boolean addTokenIdentifier(final TokenIdentifier tokenId) {
        return this.subject.getPublicCredentials().add(tokenId);
    }
    
    public synchronized Set<TokenIdentifier> getTokenIdentifiers() {
        return this.subject.getPublicCredentials(TokenIdentifier.class);
    }
    
    public boolean addToken(final Token<? extends TokenIdentifier> token) {
        return token != null && this.addToken(token.getService(), token);
    }
    
    public boolean addToken(final Text alias, final Token<? extends TokenIdentifier> token) {
        synchronized (this.subject) {
            this.getCredentialsInternal().addToken(alias, token);
            return true;
        }
    }
    
    public Collection<Token<? extends TokenIdentifier>> getTokens() {
        synchronized (this.subject) {
            return Collections.unmodifiableCollection((Collection<? extends Token<? extends TokenIdentifier>>)new ArrayList<Token<? extends TokenIdentifier>>(this.getCredentialsInternal().getAllTokens()));
        }
    }
    
    public Credentials getCredentials() {
        synchronized (this.subject) {
            final Credentials creds = new Credentials(this.getCredentialsInternal());
            final Iterator<Token<?>> iter = (Iterator<Token<?>>)creds.getAllTokens().iterator();
            while (iter.hasNext()) {
                if (iter.next().isPrivate()) {
                    iter.remove();
                }
            }
            return creds;
        }
    }
    
    public void addCredentials(final Credentials credentials) {
        synchronized (this.subject) {
            this.getCredentialsInternal().addAll(credentials);
        }
    }
    
    private synchronized Credentials getCredentialsInternal() {
        final Set<Credentials> credentialsSet = this.subject.getPrivateCredentials(Credentials.class);
        Credentials credentials;
        if (!credentialsSet.isEmpty()) {
            credentials = credentialsSet.iterator().next();
        }
        else {
            credentials = new Credentials();
            this.subject.getPrivateCredentials().add(credentials);
        }
        return credentials;
    }
    
    public String[] getGroupNames() {
        final List<String> groups = this.getGroups();
        return groups.toArray(new String[groups.size()]);
    }
    
    public List<String> getGroups() {
        ensureInitialized();
        try {
            return UserGroupInformation.groups.getGroups(this.getShortUserName());
        }
        catch (IOException ie) {
            if (UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("Failed to get groups for user " + this.getShortUserName() + " by " + ie);
                UserGroupInformation.LOG.trace("TRACE", ie);
            }
            return Collections.emptyList();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getUserName());
        sb.append(" (auth:" + this.getAuthenticationMethod() + ")");
        if (this.getRealUser() != null) {
            sb.append(" via ").append(this.getRealUser().toString());
        }
        return sb.toString();
    }
    
    public synchronized void setAuthenticationMethod(final AuthenticationMethod authMethod) {
        this.user.setAuthenticationMethod(authMethod);
    }
    
    public void setAuthenticationMethod(final SaslRpcServer.AuthMethod authMethod) {
        this.user.setAuthenticationMethod(AuthenticationMethod.valueOf(authMethod));
    }
    
    public synchronized AuthenticationMethod getAuthenticationMethod() {
        return this.user.getAuthenticationMethod();
    }
    
    public synchronized AuthenticationMethod getRealAuthenticationMethod() {
        UserGroupInformation ugi = this.getRealUser();
        if (ugi == null) {
            ugi = this;
        }
        return ugi.getAuthenticationMethod();
    }
    
    public static AuthenticationMethod getRealAuthenticationMethod(final UserGroupInformation ugi) {
        AuthenticationMethod authMethod = ugi.getAuthenticationMethod();
        if (authMethod == AuthenticationMethod.PROXY) {
            authMethod = ugi.getRealUser().getAuthenticationMethod();
        }
        return authMethod;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && this.getClass() == o.getClass() && this.subject == ((UserGroupInformation)o).subject);
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this.subject);
    }
    
    protected Subject getSubject() {
        return this.subject;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public <T> T doAs(final PrivilegedAction<T> action) {
        this.logPrivilegedAction(this.subject, action);
        return Subject.doAs(this.subject, action);
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public <T> T doAs(final PrivilegedExceptionAction<T> action) throws IOException, InterruptedException {
        try {
            this.logPrivilegedAction(this.subject, action);
            return Subject.doAs(this.subject, action);
        }
        catch (PrivilegedActionException pae) {
            final Throwable cause = pae.getCause();
            if (UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("PrivilegedActionException as:" + this + " cause:" + cause);
            }
            if (cause == null) {
                throw new RuntimeException("PrivilegedActionException with no underlying cause. UGI [" + this + "]: " + pae, pae);
            }
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof InterruptedException) {
                throw (InterruptedException)cause;
            }
            throw new UndeclaredThrowableException(cause);
        }
    }
    
    private void logPrivilegedAction(final Subject subject, final Object action) {
        if (UserGroupInformation.LOG.isDebugEnabled()) {
            final String where = new Throwable().getStackTrace()[2].toString();
            UserGroupInformation.LOG.debug("PrivilegedAction as:" + this + " from:" + where);
        }
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "KMS" })
    @InterfaceStability.Unstable
    public static void logUserInfo(final Logger log, final String caption, final UserGroupInformation ugi) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug(caption + " UGI: " + ugi);
            for (final Token<?> token : ugi.getTokens()) {
                log.debug("+token:" + token);
            }
        }
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "KMS" })
    @InterfaceStability.Unstable
    public static void logAllUserInfo(final Logger log, final UserGroupInformation ugi) throws IOException {
        if (log.isDebugEnabled()) {
            logUserInfo(log, "Current", getCurrentUser());
            if (ugi.getRealUser() != null) {
                logUserInfo(log, "Real", ugi.getRealUser());
            }
            logUserInfo(log, "Login", getLoginUser());
        }
    }
    
    public static void logAllUserInfo(final UserGroupInformation ugi) throws IOException {
        logAllUserInfo(UserGroupInformation.LOG, ugi);
    }
    
    private void print() throws IOException {
        System.out.println("User: " + this.getUserName());
        System.out.print("Group Ids: ");
        System.out.println();
        final String[] groups = this.getGroupNames();
        System.out.print("Groups: ");
        for (int i = 0; i < groups.length; ++i) {
            System.out.print(groups[i] + " ");
        }
        System.out.println();
    }
    
    private static UserGroupInformation doSubjectLogin(final Subject subject, LoginParams params) throws IOException {
        ensureInitialized();
        if (subject == null && params == null) {
            params = LoginParams.getDefaults();
        }
        final HadoopConfiguration loginConf = new HadoopConfiguration(params);
        try {
            final HadoopLoginContext login = newLoginContext(UserGroupInformation.authenticationMethod.getLoginAppName(), subject, loginConf);
            login.login();
            final UserGroupInformation ugi = new UserGroupInformation(login.getSubject());
            if (subject == null) {
                params.put(LoginParam.PRINCIPAL, ugi.getUserName());
                ugi.setLogin(login);
            }
            return ugi;
        }
        catch (LoginException le) {
            final KerberosAuthException kae = new KerberosAuthException("failure to login:", le);
            if (params != null) {
                kae.setPrincipal(((EnumMap<K, String>)params).get(LoginParam.PRINCIPAL));
                kae.setKeytabFile(((EnumMap<K, String>)params).get(LoginParam.KEYTAB));
                kae.setTicketCacheFile(((EnumMap<K, String>)params).get(LoginParam.CCACHE));
            }
            throw kae;
        }
    }
    
    public static void main(final String[] args) throws Exception {
        System.out.println("Getting UGI for current user");
        final UserGroupInformation ugi = getCurrentUser();
        ugi.print();
        System.out.println("UGI: " + ugi);
        System.out.println("Auth method " + ugi.user.getAuthenticationMethod());
        System.out.println("Keytab " + ugi.isFromKeytab());
        System.out.println("============================================================");
        if (args.length == 2) {
            System.out.println("Getting UGI from keytab....");
            loginUserFromKeytab(args[0], args[1]);
            getCurrentUser().print();
            System.out.println("Keytab: " + ugi);
            final UserGroupInformation loginUgi = getLoginUser();
            System.out.println("Auth method " + loginUgi.getAuthenticationMethod());
            System.out.println("Keytab " + loginUgi.isFromKeytab());
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(UserGroupInformation.class);
        UserGroupInformation.shouldRenewImmediatelyForTests = false;
        UserGroupInformation.metrics = UgiMetrics.create();
        loginUserRef = new AtomicReference<UserGroupInformation>();
        windows = System.getProperty("os.name").startsWith("Windows");
        is64Bit = (System.getProperty("os.arch").contains("64") || System.getProperty("os.arch").contains("s390x"));
        aix = System.getProperty("os.name").equals("AIX");
        UserGroupInformation.OS_LOGIN_MODULE_NAME = getOSLoginModuleName();
        UserGroupInformation.OS_PRINCIPAL_CLASS = getOsPrincipalClass();
    }
    
    @Metrics(about = "User and group related metrics", context = "ugi")
    static class UgiMetrics
    {
        final MetricsRegistry registry;
        @Metric({ "Rate of successful kerberos logins and latency (milliseconds)" })
        MutableRate loginSuccess;
        @Metric({ "Rate of failed kerberos logins and latency (milliseconds)" })
        MutableRate loginFailure;
        @Metric({ "GetGroups" })
        MutableRate getGroups;
        MutableQuantiles[] getGroupsQuantiles;
        @Metric({ "Renewal failures since startup" })
        private MutableGaugeLong renewalFailuresTotal;
        @Metric({ "Renewal failures since last successful login" })
        private MutableGaugeInt renewalFailures;
        
        UgiMetrics() {
            this.registry = new MetricsRegistry("UgiMetrics");
        }
        
        static UgiMetrics create() {
            return DefaultMetricsSystem.instance().register(new UgiMetrics());
        }
        
        static void reattach() {
            UserGroupInformation.metrics = create();
        }
        
        void addGetGroups(final long latency) {
            this.getGroups.add(latency);
            if (this.getGroupsQuantiles != null) {
                for (final MutableQuantiles q : this.getGroupsQuantiles) {
                    q.add(latency);
                }
            }
        }
        
        MutableGaugeInt getRenewalFailures() {
            return this.renewalFailures;
        }
    }
    
    @InterfaceAudience.Private
    public static class HadoopLoginModule implements LoginModule
    {
        private Subject subject;
        
        @Override
        public boolean abort() throws LoginException {
            return true;
        }
        
        private <T extends Principal> T getCanonicalUser(final Class<T> cls) {
            final Iterator<T> iterator = this.subject.getPrincipals(cls).iterator();
            if (iterator.hasNext()) {
                final T user = iterator.next();
                return user;
            }
            return null;
        }
        
        @Override
        public boolean commit() throws LoginException {
            if (UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("hadoop login commit");
            }
            if (!this.subject.getPrincipals(User.class).isEmpty()) {
                if (UserGroupInformation.LOG.isDebugEnabled()) {
                    UserGroupInformation.LOG.debug("using existing subject:" + this.subject.getPrincipals());
                }
                return true;
            }
            Principal user = this.getCanonicalUser(KerberosPrincipal.class);
            if (user != null && UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("using kerberos user:" + user);
            }
            if (!UserGroupInformation.isSecurityEnabled() && user == null) {
                String envUser = System.getenv("HADOOP_USER_NAME");
                if (envUser == null) {
                    envUser = System.getProperty("HADOOP_USER_NAME");
                }
                user = ((envUser == null) ? null : new User(envUser));
            }
            if (user == null) {
                user = this.getCanonicalUser(UserGroupInformation.OS_PRINCIPAL_CLASS);
                if (UserGroupInformation.LOG.isDebugEnabled()) {
                    UserGroupInformation.LOG.debug("using local user:" + user);
                }
            }
            if (user != null) {
                if (UserGroupInformation.LOG.isDebugEnabled()) {
                    UserGroupInformation.LOG.debug("Using user: \"" + user + "\" with name " + user.getName());
                }
                User userEntry = null;
                try {
                    final AuthenticationMethod authMethod = (user instanceof KerberosPrincipal) ? AuthenticationMethod.KERBEROS : AuthenticationMethod.SIMPLE;
                    userEntry = new User(user.getName(), authMethod, null);
                }
                catch (Exception e) {
                    throw (LoginException)new LoginException(e.toString()).initCause(e);
                }
                if (UserGroupInformation.LOG.isDebugEnabled()) {
                    UserGroupInformation.LOG.debug("User entry: \"" + userEntry.toString() + "\"");
                }
                this.subject.getPrincipals().add(userEntry);
                return true;
            }
            UserGroupInformation.LOG.error("Can't find user in " + this.subject);
            throw new LoginException("Can't find user name");
        }
        
        @Override
        public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
            this.subject = subject;
        }
        
        @Override
        public boolean login() throws LoginException {
            if (UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("hadoop login");
            }
            return true;
        }
        
        @Override
        public boolean logout() throws LoginException {
            if (UserGroupInformation.LOG.isDebugEnabled()) {
                UserGroupInformation.LOG.debug("hadoop logout");
            }
            return true;
        }
    }
    
    private static class RealUser implements Principal
    {
        private final UserGroupInformation realUser;
        
        RealUser(final UserGroupInformation realUser) {
            this.realUser = realUser;
        }
        
        @Override
        public String getName() {
            return this.realUser.getUserName();
        }
        
        public UserGroupInformation getRealUser() {
            return this.realUser;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o != null && this.getClass() == o.getClass() && this.realUser.equals(((RealUser)o).realUser));
        }
        
        @Override
        public int hashCode() {
            return this.realUser.hashCode();
        }
        
        @Override
        public String toString() {
            return this.realUser.toString();
        }
    }
    
    @VisibleForTesting
    class AutoRenewalForUserCredsRunnable implements Runnable
    {
        private KerberosTicket tgt;
        private RetryPolicy rp;
        private String kinitCmd;
        private long nextRefresh;
        private boolean runRenewalLoop;
        
        AutoRenewalForUserCredsRunnable(final KerberosTicket tgt, final String kinitCmd, final long nextRefresh) {
            this.runRenewalLoop = true;
            this.tgt = tgt;
            this.kinitCmd = kinitCmd;
            this.nextRefresh = nextRefresh;
            this.rp = null;
        }
        
        public void setRunRenewalLoop(final boolean runRenewalLoop) {
            this.runRenewalLoop = runRenewalLoop;
        }
        
        @Override
        public void run() {
            do {
                try {
                    final long now = Time.now();
                    if (UserGroupInformation.LOG.isDebugEnabled()) {
                        UserGroupInformation.LOG.debug("Current time is " + now);
                        UserGroupInformation.LOG.debug("Next refresh is " + this.nextRefresh);
                    }
                    if (now < this.nextRefresh) {
                        Thread.sleep(this.nextRefresh - now);
                    }
                    final String output = Shell.execCommand(this.kinitCmd, "-R");
                    if (UserGroupInformation.LOG.isDebugEnabled()) {
                        UserGroupInformation.LOG.debug("Renewed ticket. kinit output: {}", output);
                    }
                    UserGroupInformation.this.reloginFromTicketCache();
                    this.tgt = UserGroupInformation.this.getTGT();
                    if (this.tgt == null) {
                        UserGroupInformation.LOG.warn("No TGT after renewal. Aborting renew thread for " + UserGroupInformation.this.getUserName());
                        return;
                    }
                    this.nextRefresh = Math.max(UserGroupInformation.this.getRefreshTime(this.tgt), now + UserGroupInformation.kerberosMinSecondsBeforeRelogin);
                    UserGroupInformation.metrics.renewalFailures.set(0);
                    this.rp = null;
                }
                catch (InterruptedException ie2) {
                    UserGroupInformation.LOG.warn("Terminating renewal thread");
                    return;
                }
                catch (IOException ie) {
                    UserGroupInformation.metrics.renewalFailuresTotal.incr();
                    final long now2 = Time.now();
                    if (this.tgt.isDestroyed()) {
                        UserGroupInformation.LOG.error("TGT is destroyed. Aborting renew thread for {}.", UserGroupInformation.this.getUserName());
                        return;
                    }
                    long tgtEndTime;
                    try {
                        tgtEndTime = this.tgt.getEndTime().getTime();
                    }
                    catch (NullPointerException npe) {
                        UserGroupInformation.LOG.error("NPE thrown while getting KerberosTicket endTime. Aborting renew thread for {}.", UserGroupInformation.this.getUserName());
                        return;
                    }
                    UserGroupInformation.LOG.warn("Exception encountered while running the renewal command for {}. (TGT end time:{}, renewalFailures: {},renewalFailuresTotal: {})", UserGroupInformation.this.getUserName(), tgtEndTime, UserGroupInformation.metrics.renewalFailures.value(), UserGroupInformation.metrics.renewalFailuresTotal.value(), ie);
                    if (this.rp == null) {
                        this.rp = RetryPolicies.exponentialBackoffRetry(62, UserGroupInformation.kerberosMinSecondsBeforeRelogin, TimeUnit.MILLISECONDS);
                    }
                    try {
                        this.nextRefresh = UserGroupInformation.getNextTgtRenewalTime(tgtEndTime, now2, this.rp);
                    }
                    catch (Exception e) {
                        UserGroupInformation.LOG.error("Exception when calculating next tgt renewal time", e);
                        return;
                    }
                    UserGroupInformation.metrics.renewalFailures.incr();
                    if (now2 > this.nextRefresh) {
                        UserGroupInformation.LOG.error("TGT is expired. Aborting renew thread for {}.", UserGroupInformation.this.getUserName());
                        return;
                    }
                }
            } while (this.runRenewalLoop);
        }
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public enum AuthenticationMethod
    {
        SIMPLE(SaslRpcServer.AuthMethod.SIMPLE, "hadoop-simple"), 
        KERBEROS(SaslRpcServer.AuthMethod.KERBEROS, "hadoop-kerberos"), 
        TOKEN(SaslRpcServer.AuthMethod.TOKEN), 
        CERTIFICATE((SaslRpcServer.AuthMethod)null), 
        KERBEROS_SSL((SaslRpcServer.AuthMethod)null), 
        PROXY((SaslRpcServer.AuthMethod)null);
        
        private final SaslRpcServer.AuthMethod authMethod;
        private final String loginAppName;
        
        private AuthenticationMethod(final SaslRpcServer.AuthMethod authMethod) {
            this(authMethod, null);
        }
        
        private AuthenticationMethod(final SaslRpcServer.AuthMethod authMethod, final String loginAppName) {
            this.authMethod = authMethod;
            this.loginAppName = loginAppName;
        }
        
        public SaslRpcServer.AuthMethod getAuthMethod() {
            return this.authMethod;
        }
        
        String getLoginAppName() {
            if (this.loginAppName == null) {
                throw new UnsupportedOperationException(this + " login authentication is not supported");
            }
            return this.loginAppName;
        }
        
        public static AuthenticationMethod valueOf(final SaslRpcServer.AuthMethod authMethod) {
            for (final AuthenticationMethod value : values()) {
                if (value.getAuthMethod() == authMethod) {
                    return value;
                }
            }
            throw new IllegalArgumentException("no authentication method for " + authMethod);
        }
    }
    
    private static class TestingGroups extends Groups
    {
        private final Map<String, List<String>> userToGroupsMapping;
        private Groups underlyingImplementation;
        
        private TestingGroups(final Groups underlyingImplementation) {
            super(new Configuration());
            this.userToGroupsMapping = new HashMap<String, List<String>>();
            this.underlyingImplementation = underlyingImplementation;
        }
        
        @Override
        public List<String> getGroups(final String user) throws IOException {
            List<String> result = this.userToGroupsMapping.get(user);
            if (result == null) {
                result = this.underlyingImplementation.getGroups(user);
            }
            return result;
        }
        
        private void setUserGroups(final String user, final String[] groups) {
            this.userToGroupsMapping.put(user, Arrays.asList(groups));
        }
    }
    
    enum LoginParam
    {
        PRINCIPAL, 
        KEYTAB, 
        CCACHE;
    }
    
    private static class LoginParams extends EnumMap<LoginParam, String> implements Configuration.Parameters
    {
        LoginParams() {
            super(LoginParam.class);
        }
        
        @Override
        public String put(final LoginParam param, final String val) {
            final boolean add = val != null && !this.containsKey(param);
            return add ? super.put(param, val) : null;
        }
        
        static LoginParams getDefaults() {
            final LoginParams params = new LoginParams();
            params.put(LoginParam.PRINCIPAL, System.getenv("KRB5PRINCIPAL"));
            params.put(LoginParam.KEYTAB, System.getenv("KRB5KEYTAB"));
            params.put(LoginParam.CCACHE, System.getenv("KRB5CCNAME"));
            return params;
        }
    }
    
    private static class HadoopLoginContext extends LoginContext
    {
        private final String appName;
        private final HadoopConfiguration conf;
        private AtomicBoolean isLoggedIn;
        
        HadoopLoginContext(final String appName, final Subject subject, final HadoopConfiguration conf) throws LoginException {
            super(appName, subject, null, conf);
            this.isLoggedIn = new AtomicBoolean();
            this.appName = appName;
            this.conf = conf;
        }
        
        String getAppName() {
            return this.appName;
        }
        
        HadoopConfiguration getConfiguration() {
            return this.conf;
        }
        
        Object getSubjectLock() {
            final Subject subject = this.getSubject();
            return (subject == null) ? this : subject.getPrivateCredentials();
        }
        
        @Override
        public void login() throws LoginException {
            synchronized (this.getSubjectLock()) {
                MutableRate metric = UserGroupInformation.metrics.loginFailure;
                final long start = Time.monotonicNow();
                try {
                    super.login();
                    this.isLoggedIn.set(true);
                    metric = UserGroupInformation.metrics.loginSuccess;
                }
                finally {
                    metric.add(Time.monotonicNow() - start);
                }
            }
        }
        
        @Override
        public void logout() throws LoginException {
            synchronized (this.getSubjectLock()) {
                if (this.isLoggedIn.compareAndSet(true, false)) {
                    super.logout();
                }
            }
        }
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    private static class HadoopConfiguration extends Configuration
    {
        static final String KRB5_LOGIN_MODULE;
        static final String SIMPLE_CONFIG_NAME = "hadoop-simple";
        static final String KERBEROS_CONFIG_NAME = "hadoop-kerberos";
        private static final Map<String, String> BASIC_JAAS_OPTIONS;
        static final AppConfigurationEntry OS_SPECIFIC_LOGIN;
        static final AppConfigurationEntry HADOOP_LOGIN;
        private final LoginParams params;
        
        HadoopConfiguration(final LoginParams params) {
            this.params = params;
        }
        
        @Override
        public LoginParams getParameters() {
            return this.params;
        }
        
        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(final String appName) {
            final ArrayList<AppConfigurationEntry> entries = new ArrayList<AppConfigurationEntry>();
            if (this.params == null || appName.equals("hadoop-simple")) {
                entries.add(HadoopConfiguration.OS_SPECIFIC_LOGIN);
            }
            else if (appName.equals("hadoop-kerberos")) {
                if (!this.params.containsKey(LoginParam.PRINCIPAL)) {
                    entries.add(HadoopConfiguration.OS_SPECIFIC_LOGIN);
                }
                entries.add(this.getKerberosEntry());
            }
            entries.add(HadoopConfiguration.HADOOP_LOGIN);
            return entries.toArray(new AppConfigurationEntry[0]);
        }
        
        private AppConfigurationEntry getKerberosEntry() {
            final Map<String, String> options = new HashMap<String, String>(HadoopConfiguration.BASIC_JAAS_OPTIONS);
            AppConfigurationEntry.LoginModuleControlFlag controlFlag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
            final String principal = ((EnumMap<K, String>)this.params).get(LoginParam.PRINCIPAL);
            if (principal != null) {
                options.put("principal", principal);
                controlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
            }
            if (PlatformName.IBM_JAVA) {
                if (this.params.containsKey(LoginParam.KEYTAB)) {
                    final String keytab = ((EnumMap<K, String>)this.params).get(LoginParam.KEYTAB);
                    if (keytab != null) {
                        options.put("useKeytab", prependFileAuthority(keytab));
                    }
                    else {
                        options.put("useDefaultKeytab", "true");
                    }
                    options.put("credsType", "both");
                }
                else {
                    final String ticketCache = ((EnumMap<K, String>)this.params).get(LoginParam.CCACHE);
                    if (ticketCache != null) {
                        options.put("useCcache", prependFileAuthority(ticketCache));
                    }
                    else {
                        options.put("useDefaultCcache", "true");
                    }
                    options.put("renewTGT", "true");
                }
            }
            else {
                if (this.params.containsKey(LoginParam.KEYTAB)) {
                    options.put("useKeyTab", "true");
                    final String keytab = ((EnumMap<K, String>)this.params).get(LoginParam.KEYTAB);
                    if (keytab != null) {
                        options.put("keyTab", keytab);
                    }
                    options.put("storeKey", "true");
                }
                else {
                    options.put("useTicketCache", "true");
                    final String ticketCache = ((EnumMap<K, String>)this.params).get(LoginParam.CCACHE);
                    if (ticketCache != null) {
                        options.put("ticketCache", ticketCache);
                    }
                    options.put("renewTGT", "true");
                }
                options.put("doNotPrompt", "true");
            }
            options.put("refreshKrb5Config", "true");
            return new AppConfigurationEntry(HadoopConfiguration.KRB5_LOGIN_MODULE, controlFlag, options);
        }
        
        private static String prependFileAuthority(final String keytabPath) {
            return keytabPath.startsWith("file://") ? keytabPath : ("file://" + keytabPath);
        }
        
        static {
            KRB5_LOGIN_MODULE = KerberosUtil.getKrb5LoginModuleName();
            BASIC_JAAS_OPTIONS = new HashMap<String, String>();
            if ("true".equalsIgnoreCase(System.getenv("HADOOP_JAAS_DEBUG"))) {
                HadoopConfiguration.BASIC_JAAS_OPTIONS.put("debug", "true");
            }
            OS_SPECIFIC_LOGIN = new AppConfigurationEntry(UserGroupInformation.OS_LOGIN_MODULE_NAME, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, HadoopConfiguration.BASIC_JAAS_OPTIONS);
            HADOOP_LOGIN = new AppConfigurationEntry(HadoopLoginModule.class.getName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, HadoopConfiguration.BASIC_JAAS_OPTIONS);
        }
    }
}
