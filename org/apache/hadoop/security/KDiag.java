// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.util.ToolRunner;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.io.Text;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.keytab.KeytabEntry;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.hadoop.util.Shell;
import java.lang.reflect.InvocationTargetException;
import org.apache.hadoop.security.authentication.util.KerberosName;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.io.InputStream;
import java.util.List;
import org.apache.hadoop.util.StringUtils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.util.regex.Pattern;
import java.io.File;
import java.io.PrintWriter;
import org.slf4j.Logger;
import java.io.Closeable;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.conf.Configured;

public class KDiag extends Configured implements Tool, Closeable
{
    private static final Logger LOG;
    public static final String KRB5_CCNAME = "KRB5CCNAME";
    public static final String KRB5_CONFIG = "KRB5_CONFIG";
    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public static final String JAVA_SECURITY_KRB5_REALM = "java.security.krb5.realm";
    public static final String JAVA_SECURITY_KRB5_KDC_ADDRESS = "java.security.krb5.kdc";
    public static final String SUN_SECURITY_KRB5_DEBUG = "sun.security.krb5.debug";
    public static final String SUN_SECURITY_SPNEGO_DEBUG = "sun.security.spnego.debug";
    public static final String SUN_SECURITY_JAAS_FILE = "java.security.auth.login.config";
    public static final String KERBEROS_KINIT_COMMAND = "hadoop.kerberos.kinit.command";
    public static final String HADOOP_AUTHENTICATION_IS_DISABLED = "Hadoop authentication is disabled";
    public static final String UNSET = "(unset)";
    public static final String NO_DEFAULT_REALM = "Cannot locate default realm";
    public static final int KDIAG_FAILURE = 41;
    public static final String DFS_DATA_TRANSFER_SASLPROPERTIES_RESOLVER_CLASS = "dfs.data.transfer.saslproperties.resolver.class";
    public static final String DFS_DATA_TRANSFER_PROTECTION = "dfs.data.transfer.protection";
    public static final String ETC_KRB5_CONF = "/etc/krb5.conf";
    public static final String ETC_NTP = "/etc/ntp.conf";
    public static final String HADOOP_JAAS_DEBUG = "HADOOP_JAAS_DEBUG";
    private PrintWriter out;
    private File keytab;
    private String principal;
    private long minKeyLength;
    private boolean securityRequired;
    private boolean nofail;
    private boolean nologin;
    private boolean jaas;
    private boolean checkShortName;
    private static final Pattern nonSimplePattern;
    private boolean probeHasFailed;
    public static final String CAT_CONFIG = "CONFIG";
    public static final String CAT_JAAS = "JAAS";
    public static final String CAT_JVM = "JVM";
    public static final String CAT_KERBEROS = "KERBEROS";
    public static final String CAT_LOGIN = "LOGIN";
    public static final String CAT_OS = "JAAS";
    public static final String CAT_SASL = "SASL";
    public static final String CAT_UGI = "UGI";
    public static final String CAT_TOKEN = "TOKEN";
    public static final String ARG_KEYLEN = "--keylen";
    public static final String ARG_KEYTAB = "--keytab";
    public static final String ARG_JAAS = "--jaas";
    public static final String ARG_NOFAIL = "--nofail";
    public static final String ARG_NOLOGIN = "--nologin";
    public static final String ARG_OUTPUT = "--out";
    public static final String ARG_PRINCIPAL = "--principal";
    public static final String ARG_RESOURCE = "--resource";
    public static final String ARG_SECURE = "--secure";
    public static final String ARG_VERIFYSHORTNAME = "--verifyshortname";
    
    public KDiag(final Configuration conf, final PrintWriter out, final File keytab, final String principal, final long minKeyLength, final boolean securityRequired) {
        super(conf);
        this.minKeyLength = 256L;
        this.nofail = false;
        this.nologin = false;
        this.jaas = false;
        this.checkShortName = false;
        this.probeHasFailed = false;
        this.keytab = keytab;
        this.principal = principal;
        this.out = out;
        this.minKeyLength = minKeyLength;
        this.securityRequired = securityRequired;
    }
    
    public KDiag() {
        this.minKeyLength = 256L;
        this.nofail = false;
        this.nologin = false;
        this.jaas = false;
        this.checkShortName = false;
        this.probeHasFailed = false;
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        if (this.out != null) {
            this.out.close();
        }
    }
    
    @Override
    public int run(final String[] argv) throws Exception {
        final List<String> args = new LinkedList<String>(Arrays.asList(argv));
        final String keytabName = StringUtils.popOptionWithArgument("--keytab", args);
        if (keytabName != null) {
            this.keytab = new File(keytabName);
        }
        this.principal = StringUtils.popOptionWithArgument("--principal", args);
        final String outf = StringUtils.popOptionWithArgument("--out", args);
        final String mkl = StringUtils.popOptionWithArgument("--keylen", args);
        if (mkl != null) {
            this.minKeyLength = Integer.parseInt(mkl);
        }
        this.securityRequired = StringUtils.popOption("--secure", args);
        this.nofail = StringUtils.popOption("--nofail", args);
        this.jaas = StringUtils.popOption("--jaas", args);
        this.nologin = StringUtils.popOption("--nologin", args);
        this.checkShortName = StringUtils.popOption("--verifyshortname", args);
        String resource;
        while (null != (resource = StringUtils.popOptionWithArgument("--resource", args))) {
            KDiag.LOG.info("Loading resource {}", resource);
            try (final InputStream in = this.getClass().getClassLoader().getResourceAsStream(resource)) {
                if (this.verify(in != null, "CONFIG", "No resource %s", resource)) {
                    Configuration.addDefaultResource(resource);
                }
            }
        }
        if (!args.isEmpty()) {
            this.println("Unknown arguments in command:", new Object[0]);
            for (final String s : args) {
                this.println("  \"%s\"", s);
            }
            this.println();
            this.println(this.usage(), new Object[0]);
            return -1;
        }
        if (outf != null) {
            this.println("Printing output to %s", outf);
            this.out = new PrintWriter(new File(outf), "UTF-8");
        }
        this.execute();
        return this.probeHasFailed ? 41 : 0;
    }
    
    private String usage() {
        return "KDiag: Diagnose Kerberos Problems\n" + this.arg("-D", "key=value", "Define a configuration option") + this.arg("--jaas", "", "Require a JAAS file to be defined in java.security.auth.login.config") + this.arg("--keylen", "<keylen>", "Require a minimum size for encryption keys supported by the JVM. Default value : " + this.minKeyLength) + this.arg("--keytab", "<keytab> --principal <principal>", "Login from a keytab as a specific principal") + this.arg("--nofail", "", "Do not fail on the first problem") + this.arg("--nologin", "", "Do not attempt to log in") + this.arg("--out", "<file>", "Write output to a file") + this.arg("--resource", "<resource>", "Load an XML configuration resource") + this.arg("--secure", "", "Require the hadoop configuration to be secure") + this.arg("--verifyshortname", "--principal <principal>", "Verify the short name of the specific principal does not contain '@' or '/'");
    }
    
    private String arg(final String name, final String params, final String meaning) {
        return String.format("  [%s%s%s] : %s", name, params.isEmpty() ? "" : " ", params, meaning) + ".\n";
    }
    
    public boolean execute() throws Exception {
        this.title("Kerberos Diagnostics scan at %s", new Date(System.currentTimeMillis()));
        this.println("Hostname = %s", InetAddress.getLocalHost().getCanonicalHostName());
        this.println("%s = %d", "--keylen", this.minKeyLength);
        this.println("%s = %s", "--keytab", this.keytab);
        this.println("%s = %s", "--principal", this.principal);
        this.println("%s = %s", "--verifyshortname", this.checkShortName);
        this.validateKeyLength();
        this.println("JVM Kerberos Login Module = %s", KerberosUtil.getKrb5LoginModuleName());
        this.title("Core System Properties", new Object[0]);
        for (final String prop : new String[] { "user.name", "java.version", "java.vendor", "java.security.krb5.conf", "java.security.krb5.realm", "java.security.krb5.kdc", "sun.security.krb5.debug", "sun.security.spnego.debug", "java.security.auth.login.config" }) {
            this.printSysprop(prop);
        }
        this.endln();
        this.title("All System Properties", new Object[0]);
        final ArrayList<String> propList = new ArrayList<String>(System.getProperties().stringPropertyNames());
        Collections.sort(propList, String.CASE_INSENSITIVE_ORDER);
        for (final String s : propList) {
            this.printSysprop(s);
        }
        this.endln();
        this.title("Environment Variables", new Object[0]);
        for (final String env : new String[] { "HADOOP_JAAS_DEBUG", "KRB5CCNAME", "KRB5_CONFIG", "HADOOP_USER_NAME", "HADOOP_PROXY_USER", "HADOOP_TOKEN_FILE_LOCATION", "HADOOP_SECURE_LOG", "HADOOP_OPTS", "HADOOP_CLIENT_OPTS" }) {
            this.printEnv(env);
        }
        this.endln();
        this.title("Configuration Options", new Object[0]);
        for (final String prop2 : new String[] { "hadoop.kerberos.kinit.command", "hadoop.security.authentication", "hadoop.security.authorization", "hadoop.kerberos.min.seconds.before.relogin", "hadoop.security.dns.interface", "hadoop.security.dns.nameserver", "hadoop.rpc.protection", "hadoop.security.saslproperties.resolver.class", "hadoop.security.crypto.codec.classes", "hadoop.security.group.mapping", "hadoop.security.impersonation.provider.class", "dfs.data.transfer.protection", "dfs.data.transfer.saslproperties.resolver.class" }) {
            this.printConfOpt(prop2);
        }
        final Configuration conf = this.getConf();
        if (this.isSimpleAuthentication(conf)) {
            this.println("Hadoop authentication is disabled", new Object[0]);
            this.failif(this.securityRequired, "CONFIG", "Hadoop authentication is disabled", new Object[0]);
            KDiag.LOG.warn("Security is not enabled for the Hadoop cluster");
        }
        else if (this.isSimpleAuthentication(new Configuration())) {
            KDiag.LOG.warn("The default cluster security is insecure");
            this.failif(this.securityRequired, "CONFIG", "Hadoop authentication is disabled", new Object[0]);
        }
        final boolean krb5Debug = this.getAndSet("sun.security.krb5.debug");
        final boolean spnegoDebug = this.getAndSet("sun.security.spnego.debug");
        try {
            UserGroupInformation.setConfiguration(conf);
            this.validateHadoopTokenFiles(conf);
            this.validateKrb5File();
            this.printDefaultRealm();
            this.validateSasl("hadoop.security.saslproperties.resolver.class");
            if (conf.get("dfs.data.transfer.saslproperties.resolver.class") != null) {
                this.validateSasl("dfs.data.transfer.saslproperties.resolver.class");
            }
            this.validateKinitExecutable();
            this.validateJAAS(this.jaas);
            this.validateNTPConf();
            if (this.checkShortName) {
                this.validateShortName();
            }
            if (!this.nologin) {
                this.title("Logging in", new Object[0]);
                if (this.keytab != null) {
                    this.dumpKeytab(this.keytab);
                    this.loginFromKeytab();
                }
                else {
                    final UserGroupInformation loginUser = UserGroupInformation.getLoginUser();
                    this.dumpUGI("Log in user", loginUser);
                    this.validateUGI("Login user", loginUser);
                    this.println("Ticket based login: %b", UserGroupInformation.isLoginTicketBased());
                    this.println("Keytab based login: %b", UserGroupInformation.isLoginKeytabBased());
                }
            }
            return true;
        }
        finally {
            System.setProperty("sun.security.krb5.debug", Boolean.toString(krb5Debug));
            System.setProperty("sun.security.spnego.debug", Boolean.toString(spnegoDebug));
        }
    }
    
    protected boolean isSimpleAuthentication(final Configuration conf) {
        return SecurityUtil.getAuthenticationMethod(conf).equals(UserGroupInformation.AuthenticationMethod.SIMPLE);
    }
    
    protected void validateKeyLength() throws NoSuchAlgorithmException {
        final int aesLen = Cipher.getMaxAllowedKeyLength("AES");
        this.println("Maximum AES encryption key length %d bits", aesLen);
        this.verify(this.minKeyLength <= aesLen, "JVM", "Java Cryptography Extensions are not installed on this JVM. Maximum supported key length %s - minimum required %d", aesLen, this.minKeyLength);
    }
    
    protected void validateShortName() {
        this.failif(this.principal == null, "KERBEROS", "No principal defined", new Object[0]);
        try {
            final KerberosName kn = new KerberosName(this.principal);
            final String result = kn.getShortName();
            if (KDiag.nonSimplePattern.matcher(result).find()) {
                this.warn("KERBEROS", this.principal + " short name: " + result + " still contains @ or /", new Object[0]);
            }
        }
        catch (IOException e) {
            throw new KerberosDiagsFailure("KERBEROS", e, "Failed to get short name for " + this.principal, new Object[] { e });
        }
        catch (IllegalArgumentException e2) {
            this.error("KERBEROS", "KerberosName(" + this.principal + ") failed: %s\n%s", e2, StringUtils.stringifyException(e2));
        }
    }
    
    protected void printDefaultRealm() {
        try {
            final String defaultRealm = KerberosUtil.getDefaultRealm();
            this.println("Default Realm = %s", defaultRealm);
            if (defaultRealm == null) {
                this.warn("KERBEROS", "Host has no default realm", new Object[0]);
            }
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new KerberosDiagsFailure("JVM", e, "Failed to invoke krb5.Config.getDefaultRealm: %s: " + e, new Object[] { e });
        }
        catch (InvocationTargetException e2) {
            final Throwable cause = (e2.getCause() != null) ? e2.getCause() : e2;
            if (cause.toString().contains("Cannot locate default realm")) {
                this.warn("KERBEROS", "Host has no default realm", new Object[0]);
                KDiag.LOG.debug(cause.toString(), cause);
            }
            else {
                this.error("KERBEROS", "Kerberos.getDefaultRealm() failed: %s\n%s", cause, StringUtils.stringifyException(cause));
            }
        }
    }
    
    private void validateHadoopTokenFiles(final Configuration conf) throws ClassNotFoundException, KerberosDiagsFailure, NoSuchMethodException, SecurityException {
        this.title("Locating Hadoop token files", new Object[0]);
        String tokenFileLocation = System.getProperty("hadoop.token.files");
        if (tokenFileLocation != null) {
            this.println("Found hadoop.token.files in system properties : " + tokenFileLocation, new Object[0]);
        }
        if (conf.get("hadoop.token.files") != null) {
            this.println("Found hadoop.token.files in hadoop configuration : " + conf.get("hadoop.token.files"), new Object[0]);
            if (System.getProperty("hadoop.token.files") != null) {
                this.println("hadoop.token.files in the system properties overrides the one specified in hadoop configuration", new Object[0]);
            }
            else {
                tokenFileLocation = conf.get("hadoop.token.files");
            }
        }
        if (tokenFileLocation != null) {
            for (final String tokenFileName : StringUtils.getTrimmedStrings(tokenFileLocation)) {
                if (tokenFileName.length() > 0) {
                    final File tokenFile = new File(tokenFileName);
                    this.verifyFileIsValid(tokenFile, "TOKEN", "token");
                    this.verify(tokenFile, conf, "TOKEN", "token");
                }
            }
        }
    }
    
    private void validateKrb5File() throws IOException {
        if (!Shell.WINDOWS) {
            this.title("Locating Kerberos configuration file", new Object[0]);
            String krbPath = "/etc/krb5.conf";
            final String jvmKrbPath = System.getProperty("java.security.krb5.conf");
            if (jvmKrbPath != null && !jvmKrbPath.isEmpty()) {
                this.println("Setting kerberos path from sysprop %s: \"%s\"", "java.security.krb5.conf", jvmKrbPath);
                krbPath = jvmKrbPath;
            }
            final String krb5name = System.getenv("KRB5_CONFIG");
            if (krb5name != null) {
                this.println("Setting kerberos path from environment variable %s: \"%s\"", "KRB5_CONFIG", krb5name);
                krbPath = krb5name;
                if (jvmKrbPath != null) {
                    this.println("Warning - both %s and %s were set - %s takes priority", "java.security.krb5.conf", "KRB5_CONFIG", "KRB5_CONFIG");
                }
            }
            final File krbFile = new File(krbPath);
            this.println("Kerberos configuration file = %s", krbFile);
            this.dump(krbFile);
            this.endln();
        }
    }
    
    private void dumpKeytab(final File keytabFile) throws IOException {
        this.title("Examining keytab %s", keytabFile);
        final File kt = keytabFile.getCanonicalFile();
        this.verifyFileIsValid(kt, "KERBEROS", "keytab");
        final Keytab loadKeytab = Keytab.loadKeytab(kt);
        final List<PrincipalName> principals = loadKeytab.getPrincipals();
        this.println("keytab principal count: %d", principals.size());
        int entrySize = 0;
        for (final PrincipalName princ : principals) {
            final List<KeytabEntry> entries = loadKeytab.getKeytabEntries(princ);
            entrySize += entries.size();
            for (final KeytabEntry entry : entries) {
                final EncryptionKey key = entry.getKey();
                this.println(" %s: version=%d expires=%s encryption=%s", entry.getPrincipal(), entry.getKvno(), entry.getTimestamp(), key.getKeyType());
            }
        }
        this.println("keytab entry count: %d", entrySize);
        this.endln();
    }
    
    private void loginFromKeytab() throws IOException {
        if (this.keytab != null) {
            final File kt = this.keytab.getCanonicalFile();
            this.println("Using keytab %s principal %s", kt, this.principal);
            final String identity = this.principal;
            this.failif(this.principal == null, "KERBEROS", "No principal defined", new Object[0]);
            final UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(this.principal, kt.getPath());
            this.dumpUGI(identity, ugi);
            this.validateUGI(this.principal, ugi);
            this.title("Attempting to relogin", new Object[0]);
            try {
                UserGroupInformation.setShouldRenewImmediatelyForTests(true);
                ugi.reloginFromKeytab();
            }
            catch (IllegalAccessError e) {
                this.warn("UGI", "Failed to reset UGI -and so could not try to relogin", new Object[0]);
                KDiag.LOG.debug("Failed to reset UGI: {}", e, e);
            }
        }
        else {
            this.println("No keytab: attempting to log in is as current user", new Object[0]);
        }
    }
    
    private void dumpUGI(final String title, final UserGroupInformation ugi) throws IOException {
        this.title(title, new Object[0]);
        this.println("UGI instance = %s", ugi);
        this.println("Has kerberos credentials: %b", ugi.hasKerberosCredentials());
        this.println("Authentication method: %s", ugi.getAuthenticationMethod());
        this.println("Real Authentication method: %s", ugi.getRealAuthenticationMethod());
        this.title("Group names", new Object[0]);
        for (final String name : ugi.getGroupNames()) {
            this.println(name, new Object[0]);
        }
        this.title("Credentials", new Object[0]);
        final List<Text> secretKeys = ugi.getCredentials().getAllSecretKeys();
        this.title("Secret keys", new Object[0]);
        if (!secretKeys.isEmpty()) {
            for (final Text secret : secretKeys) {
                this.println("%s", secret);
            }
        }
        else {
            this.println("(none)", new Object[0]);
        }
        this.dumpTokens(ugi);
    }
    
    private void validateUGI(final String messagePrefix, final UserGroupInformation user) {
        if (this.verify(user.getAuthenticationMethod() == UserGroupInformation.AuthenticationMethod.KERBEROS, "LOGIN", "User %s is not authenticated by Kerberos", user)) {
            this.verify(user.hasKerberosCredentials(), "LOGIN", "%s: No kerberos credentials for %s", messagePrefix, user);
            this.verify(user.getAuthenticationMethod() != null, "LOGIN", "%s: Null AuthenticationMethod for %s", messagePrefix, user);
        }
    }
    
    private void validateKinitExecutable() {
        final String kinit = this.getConf().getTrimmed("hadoop.kerberos.kinit.command", "");
        if (!kinit.isEmpty()) {
            final File kinitPath = new File(kinit);
            this.println("%s = %s", "hadoop.kerberos.kinit.command", kinitPath);
            if (kinitPath.isAbsolute()) {
                this.verifyFileIsValid(kinitPath, "KERBEROS", "hadoop.kerberos.kinit.command");
            }
            else {
                this.println("Executable %s is relative -must be on the PATH", kinit);
                this.printEnv("PATH");
            }
        }
    }
    
    private void validateSasl(final String saslPropsResolverKey) {
        this.title("Resolving SASL property %s", saslPropsResolverKey);
        final String saslPropsResolver = this.getConf().getTrimmed(saslPropsResolverKey);
        try {
            final Class<? extends SaslPropertiesResolver> resolverClass = this.getConf().getClass(saslPropsResolverKey, SaslPropertiesResolver.class, SaslPropertiesResolver.class);
            this.println("Resolver is %s", resolverClass);
        }
        catch (RuntimeException e) {
            throw new KerberosDiagsFailure("SASL", e, "Failed to load %s class %s", new Object[] { saslPropsResolverKey, saslPropsResolver });
        }
    }
    
    private void validateJAAS(final boolean jaasRequired) throws IOException {
        final String jaasFilename = System.getProperty("java.security.auth.login.config");
        if (jaasRequired) {
            this.verify(jaasFilename != null, "JAAS", "No JAAS file specified in java.security.auth.login.config", new Object[0]);
        }
        if (jaasFilename != null) {
            this.title("JAAS", new Object[0]);
            final File jaasFile = new File(jaasFilename);
            this.println("JAAS file is defined in %s: %s", "java.security.auth.login.config", jaasFile);
            this.verifyFileIsValid(jaasFile, "JAAS", "JAAS file defined in java.security.auth.login.config");
            this.dump(jaasFile);
            this.endln();
        }
    }
    
    private void validateNTPConf() throws IOException {
        if (!Shell.WINDOWS) {
            final File ntpfile = new File("/etc/ntp.conf");
            if (ntpfile.exists() && this.verifyFileIsValid(ntpfile, "JAAS", "NTP file: " + ntpfile)) {
                this.title("NTP", new Object[0]);
                this.dump(ntpfile);
                this.endln();
            }
        }
    }
    
    private boolean verifyFileIsValid(final File file, final String category, final String text) {
        return this.verify(file.exists(), category, "%s file does not exist: %s", text, file) && this.verify(file.isFile(), category, "%s path does not refer to a file: %s", text, file) && this.verify(file.length() != 0L, category, "%s file is empty: %s", text, file) && this.verify(file.canRead(), category, "%s file is not readable: %s", text, file);
    }
    
    public void dumpTokens(final UserGroupInformation ugi) {
        final Collection<Token<? extends TokenIdentifier>> tokens = ugi.getCredentials().getAllTokens();
        this.title("Token Count: %d", tokens.size());
        for (final Token<? extends TokenIdentifier> token : tokens) {
            this.println("Token %s", token.getKind());
        }
        this.endln();
    }
    
    private boolean getAndSet(final String sysprop) {
        final boolean old = Boolean.getBoolean(sysprop);
        System.setProperty(sysprop, "true");
        return old;
    }
    
    private void flush() {
        if (this.out != null) {
            this.out.flush();
        }
        else {
            System.out.flush();
        }
        System.err.flush();
    }
    
    private void println(final String format, final Object... args) {
        this.flush();
        final String msg = String.format(format, args);
        if (this.out != null) {
            this.out.println(msg);
        }
        else {
            System.out.println(msg);
        }
        this.flush();
    }
    
    private void println() {
        this.println("", new Object[0]);
    }
    
    private void endln() {
        this.println();
        this.println("-----", new Object[0]);
    }
    
    private void title(final String format, final Object... args) {
        this.println();
        this.println();
        this.println("== " + String.format(format, args) + " ==", new Object[0]);
        this.println();
    }
    
    private void printSysprop(final String property) {
        this.println("%s = \"%s\"", property, System.getProperty(property, "(unset)"));
    }
    
    private void printConfOpt(final String option) {
        this.println("%s = \"%s\"", option, this.getConf().get(option, "(unset)"));
    }
    
    private void printEnv(final String variable) {
        final String env = System.getenv(variable);
        this.println("%s = \"%s\"", variable, (env != null) ? env : "(unset)");
    }
    
    private void dump(final File file) throws IOException {
        try (final FileInputStream in = new FileInputStream(file)) {
            for (final String line : IOUtils.readLines(in)) {
                this.println("%s", line);
            }
        }
    }
    
    private void fail(final String category, final String message, final Object... args) throws KerberosDiagsFailure {
        this.error(category, message, args);
        throw new KerberosDiagsFailure(category, message, args);
    }
    
    private boolean verify(final boolean condition, final String category, final String message, final Object... args) throws KerberosDiagsFailure {
        if (!condition) {
            this.probeHasFailed = true;
            if (!this.nofail) {
                this.fail(category, message, args);
            }
            else {
                this.error(category, message, args);
            }
            return false;
        }
        return true;
    }
    
    private boolean verify(final File tokenFile, final Configuration conf, final String category, final String message) throws KerberosDiagsFailure {
        try {
            Credentials.readTokenStorageFile(tokenFile, conf);
        }
        catch (Exception e) {
            if (!this.nofail) {
                this.fail(category, message, new Object[0]);
            }
            else {
                this.error(category, message, new Object[0]);
            }
            return false;
        }
        return true;
    }
    
    private void error(final String category, final String message, final Object... args) {
        this.println("ERROR: %s: %s", category, String.format(message, args));
    }
    
    private void warn(final String category, final String message, final Object... args) {
        this.println("WARNING: %s: %s", category, String.format(message, args));
    }
    
    private void failif(final boolean condition, final String category, final String message, final Object... args) throws KerberosDiagsFailure {
        if (condition) {
            this.fail(category, message, args);
        }
    }
    
    public static int exec(final Configuration conf, final String... argv) throws Exception {
        try (final KDiag kdiag = new KDiag()) {
            return ToolRunner.run(conf, kdiag, argv);
        }
    }
    
    public static void main(final String[] argv) {
        try {
            ExitUtil.terminate(exec(new Configuration(), argv));
        }
        catch (ExitUtil.ExitException e) {
            KDiag.LOG.error(e.toString());
            System.exit(e.status);
        }
        catch (Exception e2) {
            KDiag.LOG.error(e2.toString(), e2);
            ExitUtil.halt(-1, e2);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(KDiag.class);
        nonSimplePattern = Pattern.compile("[/@]");
    }
    
    public static class KerberosDiagsFailure extends ExitUtil.ExitException
    {
        private final String category;
        
        public KerberosDiagsFailure(final String category, final String message) {
            super(41, category + ": " + message);
            this.category = category;
        }
        
        public KerberosDiagsFailure(final String category, final String message, final Object... args) {
            this(category, String.format(message, args));
        }
        
        public KerberosDiagsFailure(final String category, final Throwable throwable, final String message, final Object... args) {
            this(category, message, args);
            this.initCause(throwable);
        }
        
        public String getCategory() {
            return this.category;
        }
    }
}
