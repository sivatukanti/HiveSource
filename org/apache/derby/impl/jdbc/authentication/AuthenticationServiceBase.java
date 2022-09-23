// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc.authentication;

import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.PasswordHasher;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import java.io.Serializable;
import java.util.Dictionary;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PropertyFactory;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.authentication.UserAuthenticator;
import org.apache.derby.iapi.services.property.PropertySetCallback;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.jdbc.AuthenticationService;

public abstract class AuthenticationServiceBase implements AuthenticationService, ModuleControl, ModuleSupportable, PropertySetCallback
{
    protected UserAuthenticator authenticationScheme;
    private AccessFactory store;
    public static final String AuthenticationTrace;
    protected static final int SECMEC_USRSSBPWD = 8;
    
    protected void setAuthenticationService(final UserAuthenticator authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.store = (AccessFactory)Monitor.getServiceModule(this, "org.apache.derby.iapi.store.access.AccessFactory");
        final PropertyFactory propertyFactory = (PropertyFactory)Monitor.getServiceModule(this, "org.apache.derby.iapi.services.property.PropertyFactory");
        if (propertyFactory != null) {
            propertyFactory.addPropertySetNotification(this);
        }
    }
    
    public void stop() {
    }
    
    public boolean authenticate(final String s, final Properties properties) throws SQLException {
        if (properties == null) {
            return false;
        }
        final String property = properties.getProperty("user");
        return (property == null || property.length() <= 128) && this.authenticationScheme.authenticateUser(property, properties.getProperty("password"), s, properties);
    }
    
    public String getSystemCredentialsDatabaseName() {
        return null;
    }
    
    public String getProperty(final String s) {
        String serviceProperty = null;
        try {
            final TransactionController transaction = this.getTransaction();
            serviceProperty = PropertyUtil.getServiceProperty(transaction, s, null);
            if (transaction != null) {
                transaction.commit();
            }
        }
        catch (StandardException ex) {}
        return serviceProperty;
    }
    
    protected TransactionController getTransaction() throws StandardException {
        if (this.store == null) {
            return null;
        }
        return this.store.getTransaction(ContextService.getFactory().getCurrentContextManager());
    }
    
    Properties getDatabaseProperties() throws StandardException {
        Properties properties = null;
        final TransactionController transaction = this.getTransaction();
        if (transaction != null) {
            try {
                properties = transaction.getProperties();
            }
            finally {
                transaction.commit();
            }
        }
        return properties;
    }
    
    protected String getServiceName() {
        if (this.store == null) {
            return null;
        }
        return Monitor.getServiceName(this.store);
    }
    
    public String getDatabaseProperty(final String s) {
        String databaseProperty = null;
        TransactionController transaction = null;
        try {
            if (this.store != null) {
                transaction = this.store.getTransaction(ContextService.getFactory().getCurrentContextManager());
            }
            databaseProperty = PropertyUtil.getDatabaseProperty(transaction, s);
            if (transaction != null) {
                transaction.commit();
            }
        }
        catch (StandardException ex) {}
        return databaseProperty;
    }
    
    public String getSystemProperty(final String s) {
        if (Boolean.valueOf(this.getDatabaseProperty("derby.database.propertiesOnly"))) {
            return null;
        }
        return PropertyUtil.getSystemProperty(s);
    }
    
    public void init(final boolean b, final Dictionary dictionary) {
    }
    
    public boolean validate(final String anObject, final Serializable s, final Dictionary dictionary) throws StandardException {
        if (anObject.startsWith("derby.user.")) {
            return true;
        }
        final String anObject2 = (String)s;
        final boolean equals = "NATIVE::LOCAL".equals(anObject2);
        if ("derby.authentication.provider".equals(anObject)) {
            if (anObject2 != null && anObject2.startsWith("NATIVE:") && !equals) {
                throw StandardException.newException("XCY05.S.3");
            }
            final String s2 = dictionary.get("derby.authentication.provider");
            if (s2 != null && s2.startsWith("NATIVE:")) {
                throw StandardException.newException("XCY05.S.2");
            }
            if (equals) {
                final DataDictionary dataDictionary = getDataDictionary();
                if (dataDictionary.getUser(dataDictionary.getAuthorizationDatabaseOwner()) == null) {
                    throw StandardException.newException("XCY05.S.3");
                }
            }
        }
        if ("derby.authentication.native.passwordLifetimeMillis".equals(anObject) && this.parsePasswordLifetime(anObject2) == null) {
            throw StandardException.newException("4251J", "derby.authentication.native.passwordLifetimeMillis");
        }
        if ("derby.authentication.native.passwordLifetimeThreshold".equals(anObject) && this.parsePasswordThreshold(anObject2) == null) {
            throw StandardException.newException("4251J", "derby.authentication.native.passwordLifetimeThreshold");
        }
        return false;
    }
    
    protected Long parsePasswordLifetime(final String s) {
        try {
            long long1 = Long.parseLong(s);
            if (long1 < 0L) {
                long1 = 0L;
            }
            return new Long(long1);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    protected Double parsePasswordThreshold(final String s) {
        try {
            final double double1 = Double.parseDouble(s);
            if (double1 <= 0.0) {
                return null;
            }
            return new Double(double1);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public Serviceable apply(final String s, final Serializable s2, final Dictionary dictionary) {
        return null;
    }
    
    public Serializable map(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        if (!s.startsWith("derby.user.")) {
            return null;
        }
        final String s3 = dictionary.get("derby.authentication.provider");
        if (s3 != null && StringUtil.SQLEqualsIgnoreCase(s3, "LDAP")) {
            return null;
        }
        String hashUsingDefaultAlgorithm = (String)s2;
        if (hashUsingDefaultAlgorithm != null) {
            hashUsingDefaultAlgorithm = this.hashUsingDefaultAlgorithm(s.substring("derby.user.".length()), hashUsingDefaultAlgorithm, dictionary);
        }
        return hashUsingDefaultAlgorithm;
    }
    
    protected final boolean requireAuthentication(final Properties properties) {
        return Boolean.valueOf(PropertyUtil.getPropertyFromSet(properties, "derby.connection.requireAuthentication")) || PropertyUtil.nativeAuthenticationEnabled(properties);
    }
    
    protected String hashPasswordSHA1Scheme(final String s) {
        if (s == null) {
            return null;
        }
        MessageDigest instance = null;
        try {
            instance = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException ex) {}
        instance.reset();
        instance.update(toHexByte(s));
        final byte[] digest = instance.digest();
        return "3b60" + StringUtil.toHexString(digest, 0, digest.length);
    }
    
    private static byte[] toHexByte(final String s) {
        final byte[] array = new byte[s.length() * 2];
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            final int n = (char1 & '\u00f0') >>> 4;
            final int n2 = char1 & '\u000f';
            array[i] = (byte)n;
            array[i + 1] = (byte)n2;
        }
        return array;
    }
    
    String hashUsingDefaultAlgorithm(final String s, final String s2, final Dictionary dictionary) throws StandardException {
        if (s2 == null) {
            return null;
        }
        final PasswordHasher passwordHasher = getDataDictionary().makePasswordHasher(dictionary);
        if (passwordHasher != null) {
            return passwordHasher.hashAndEncode(s, s2);
        }
        return this.hashPasswordSHA1Scheme(s2);
    }
    
    private static DataDictionary getDataDictionary() {
        return ((LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext")).getDataDictionary();
    }
    
    protected String substitutePassword(final String s, final String s2, final Properties properties, final boolean b) {
        MessageDigest instance = null;
        final byte[] input = { 0, 0, 0, 0, 0, 0, 0, 1 };
        try {
            instance = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException ex) {}
        instance.reset();
        final byte[] hexByte = toHexByte(s);
        final String property = properties.getProperty("drdaSecTokenIn");
        final String property2 = properties.getProperty("drdaSecTokenOut");
        final byte[] fromHexString = StringUtil.fromHexString(property, 0, property.length());
        final byte[] fromHexString2 = StringUtil.fromHexString(property2, 0, property2.length());
        String string;
        if (!b) {
            instance.update(toHexByte(s2));
            final byte[] digest = instance.digest();
            string = "3b60" + StringUtil.toHexString(digest, 0, digest.length);
        }
        else {
            string = s2;
        }
        instance.update(hexByte);
        instance.update(toHexByte(string));
        instance.update(instance.digest());
        instance.update(fromHexString2);
        instance.update(fromHexString);
        instance.update(hexByte);
        instance.update(input);
        final byte[] digest2 = instance.digest();
        return StringUtil.toHexString(digest2, 0, digest2.length);
    }
    
    static {
        AuthenticationTrace = null;
    }
}
