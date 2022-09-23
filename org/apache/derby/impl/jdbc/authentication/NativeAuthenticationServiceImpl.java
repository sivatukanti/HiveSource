// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc.authentication;

import org.apache.derby.iapi.sql.dictionary.UserDescriptor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.error.SQLWarningFactory;
import java.util.Arrays;
import org.apache.derby.iapi.sql.dictionary.PasswordHasher;
import java.util.Dictionary;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.catalog.SystemProcedures;
import org.apache.derby.iapi.util.IdUtil;
import javax.sql.DataSource;
import java.sql.SQLWarning;
import java.sql.Connection;
import org.apache.derby.jdbc.InternalDriver;
import java.sql.SQLException;
import org.apache.derby.impl.jdbc.Util;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Properties;
import org.apache.derby.authentication.UserAuthenticator;

public final class NativeAuthenticationServiceImpl extends AuthenticationServiceBase implements UserAuthenticator
{
    private boolean _creatingCredentialsDB;
    private String _credentialsDB;
    private boolean _authenticateDatabaseOperationsLocally;
    private long _passwordLifetimeMillis;
    private double _passwordExpirationThreshold;
    private String _badlyFormattedPasswordProperty;
    
    public NativeAuthenticationServiceImpl() {
        this._creatingCredentialsDB = false;
        this._passwordLifetimeMillis = 2678400000L;
        this._passwordExpirationThreshold = 0.125;
    }
    
    public boolean canSupport(final Properties properties) {
        if (!this.requireAuthentication(properties)) {
            return false;
        }
        if (PropertyUtil.nativeAuthenticationEnabled(properties)) {
            this.parseNativeSpecification(properties);
            return true;
        }
        return false;
    }
    
    private void parseNativeSpecification(final Properties properties) {
        final String propertyFromSet = PropertyUtil.getPropertyFromSet(properties, "derby.authentication.provider");
        this._authenticateDatabaseOperationsLocally = PropertyUtil.localNativeAuthenticationEnabled(properties);
        final int beginIndex = propertyFromSet.indexOf(":") + 1;
        final int endIndex = this._authenticateDatabaseOperationsLocally ? propertyFromSet.lastIndexOf(":") : propertyFromSet.length();
        if (endIndex > beginIndex) {
            this._credentialsDB = propertyFromSet.substring(beginIndex, endIndex);
            if (this._credentialsDB.length() == 0) {
                this._credentialsDB = null;
            }
        }
        this._badlyFormattedPasswordProperty = null;
        final String propertyFromSet2 = PropertyUtil.getPropertyFromSet(properties, "derby.authentication.native.passwordLifetimeMillis");
        if (propertyFromSet2 != null) {
            final Long passwordLifetime = this.parsePasswordLifetime(propertyFromSet2);
            if (passwordLifetime != null) {
                this._passwordLifetimeMillis = passwordLifetime;
            }
            else {
                this._badlyFormattedPasswordProperty = "derby.authentication.native.passwordLifetimeMillis";
            }
        }
        final String propertyFromSet3 = PropertyUtil.getPropertyFromSet(properties, "derby.authentication.native.passwordLifetimeThreshold");
        if (propertyFromSet3 != null) {
            final Double passwordThreshold = this.parsePasswordThreshold(propertyFromSet3);
            if (passwordThreshold != null) {
                this._passwordExpirationThreshold = passwordThreshold;
            }
            else {
                this._badlyFormattedPasswordProperty = "derby.authentication.native.passwordLifetimeThreshold";
            }
        }
    }
    
    private boolean validAuthenticationProvider() throws StandardException {
        final boolean b = this.getServiceName() == null;
        if (this._credentialsDB == null) {
            return !b && this._authenticateDatabaseOperationsLocally;
        }
        if (Monitor.getMonitor().getCanonicalServiceName(this._credentialsDB) == null) {
            throw StandardException.newException("4251L", this._credentialsDB);
        }
        return true;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
        if (!this.validAuthenticationProvider()) {
            throw StandardException.newException("4251H");
        }
        if (this._badlyFormattedPasswordProperty != null) {
            throw StandardException.newException("4251J", this._badlyFormattedPasswordProperty);
        }
        try {
            MessageDigest.getInstance("SHA-1").reset();
        }
        catch (NoSuchAlgorithmException ex) {
            throw Monitor.exceptionStartingModule(ex);
        }
        if (b && this.authenticatingInThisService(this.getCanonicalServiceName())) {
            this._creatingCredentialsDB = true;
        }
        else {
            this._creatingCredentialsDB = false;
        }
        this.setAuthenticationService(this);
    }
    
    public String getSystemCredentialsDatabaseName() {
        return this._credentialsDB;
    }
    
    public boolean authenticateUser(final String s, final String s2, final String s3, final Properties properties) throws SQLException {
        try {
            if (s == null) {
                return false;
            }
            if (s2 == null) {
                return false;
            }
            if (s3 == null || !this.authenticatingInThisDatabase(s3)) {
                return this.authenticateRemotely(s, s2, s3);
            }
            return this.authenticateLocally(s, s2, s3);
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    private boolean authenticatingInThisDatabase(final String s) throws StandardException {
        return this.authenticatingInThisService(Monitor.getMonitor().getCanonicalServiceName(s));
    }
    
    private boolean authenticatingInThisService(final String s) throws StandardException {
        return this._authenticateDatabaseOperationsLocally || this.isCredentialsService(s);
    }
    
    private boolean isCredentialsService(final String anObject) throws StandardException {
        final String canonicalServiceName = this.getCanonicalServiceName(this._credentialsDB);
        Monitor.getMonitor().getCanonicalServiceName(anObject);
        return canonicalServiceName != null && canonicalServiceName.equals(anObject);
    }
    
    private String getCanonicalServiceName() throws StandardException {
        return this.getCanonicalServiceName(this.getServiceName());
    }
    
    private String getCanonicalServiceName(final String s) throws StandardException {
        return Monitor.getMonitor().getCanonicalServiceName(s);
    }
    
    private boolean authenticateRemotely(final String value, final String value2, final String s) throws StandardException, SQLWarning {
        if (this._credentialsDB == null) {
            throw StandardException.newException("4251H");
        }
        SQLWarning warnings;
        try {
            final Properties properties = new Properties();
            properties.setProperty("user", value);
            properties.setProperty("password", value2);
            final Connection connect = InternalDriver.activeDriver().connect("jdbc:derby:" + this._credentialsDB, properties, 0);
            warnings = connect.getWarnings();
            connect.close();
        }
        catch (SQLException ex) {
            final String sqlState = ex.getSQLState();
            if ("08004".equals(sqlState)) {
                return false;
            }
            if ("XJ004.C".startsWith(sqlState)) {
                throw StandardException.newException("4251I", this._credentialsDB);
            }
            throw this.wrap(ex);
        }
        if (warnings != null) {
            throw warnings;
        }
        return true;
    }
    
    private void callDataSourceSetter(final DataSource obj, final String name, final String s) throws StandardException {
        try {
            obj.getClass().getMethod(name, String.class).invoke(obj, s);
        }
        catch (Exception ex) {
            throw this.wrap(ex);
        }
    }
    
    private StandardException wrap(final Throwable t) {
        return StandardException.plainWrapException(t);
    }
    
    private boolean authenticateLocally(String userAuthorizationId, final String s, final String s2) throws StandardException, SQLException {
        userAuthorizationId = IdUtil.getUserAuthorizationId(userAuthorizationId);
        if (this._creatingCredentialsDB) {
            this._creatingCredentialsDB = false;
            final TransactionController transaction = this.getTransaction();
            SystemProcedures.addUser(userAuthorizationId, s, transaction);
            transaction.commit();
            return true;
        }
        final DataDictionary dataDictionary = (DataDictionary)Monitor.getServiceModule(this, "org.apache.derby.iapi.sql.dictionary.DataDictionary");
        final UserDescriptor user = dataDictionary.getUser(userAuthorizationId);
        if (user == null) {
            dataDictionary.makePasswordHasher(this.getDatabaseProperties()).hashPasswordIntoString(userAuthorizationId, s).toCharArray();
            return false;
        }
        final char[] charArray = new PasswordHasher(user.getHashingScheme()).hashPasswordIntoString(userAuthorizationId, s).toCharArray();
        final char[] andZeroPassword = user.getAndZeroPassword();
        try {
            if (charArray == null || andZeroPassword == null) {
                return false;
            }
            if (charArray.length != andZeroPassword.length) {
                return false;
            }
            for (int i = 0; i < charArray.length; ++i) {
                if (charArray[i] != andZeroPassword[i]) {
                    return false;
                }
            }
        }
        finally {
            if (charArray != null) {
                Arrays.fill(charArray, '\0');
            }
            if (andZeroPassword != null) {
                Arrays.fill(andZeroPassword, '\0');
            }
        }
        if (this._passwordLifetimeMillis > 0L) {
            long n = this._passwordLifetimeMillis - (System.currentTimeMillis() - user.getLastModified().getTime());
            if (n <= 0L) {
                if (!dataDictionary.getAuthorizationDatabaseOwner().equals(userAuthorizationId)) {
                    return false;
                }
                n = 0L;
            }
            if (n <= (long)(this._passwordLifetimeMillis * this._passwordExpirationThreshold)) {
                if (dataDictionary.getAuthorizationDatabaseOwner().equals(userAuthorizationId)) {
                    throw SQLWarningFactory.newSQLWarning("01J16", s2);
                }
                throw SQLWarningFactory.newSQLWarning("01J15", Long.toString(n / 86400000L), s2);
            }
        }
        return true;
    }
}
