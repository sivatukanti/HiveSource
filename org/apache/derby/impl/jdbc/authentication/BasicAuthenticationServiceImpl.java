// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc.authentication;

import org.apache.derby.iapi.sql.dictionary.PasswordHasher;
import java.sql.SQLException;
import java.util.Dictionary;
import org.apache.derby.impl.jdbc.Util;
import org.apache.derby.iapi.error.StandardException;
import java.security.NoSuchAlgorithmException;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.security.MessageDigest;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Properties;
import org.apache.derby.authentication.UserAuthenticator;

public final class BasicAuthenticationServiceImpl extends AuthenticationServiceBase implements UserAuthenticator
{
    public boolean canSupport(final Properties properties) {
        if (!this.requireAuthentication(properties)) {
            return false;
        }
        final String propertyFromSet = PropertyUtil.getPropertyFromSet(properties, "derby.authentication.provider");
        return propertyFromSet == null || propertyFromSet.length() == 0 || StringUtil.SQLEqualsIgnoreCase(propertyFromSet, "BUILTIN");
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
        try {
            MessageDigest.getInstance("SHA-1").reset();
        }
        catch (NoSuchAlgorithmException ex) {
            throw Monitor.exceptionStartingModule(ex);
        }
        this.setAuthenticationService(this);
    }
    
    public boolean authenticateUser(final String str, final String s, final String s2, final Properties properties) throws SQLException {
        int int1 = 0;
        if (str == null) {
            return false;
        }
        final String property;
        if ((property = properties.getProperty("drdaSecMec")) != null) {
            int1 = Integer.parseInt(property);
        }
        final String concat = "derby.user.".concat(str);
        String s3 = this.getDatabaseProperty(concat);
        String hashPasswordUsingStoredAlgorithm = null;
        Label_0171: {
            if (s3 != null) {
                if (int1 != 8) {
                    try {
                        hashPasswordUsingStoredAlgorithm = this.hashPasswordUsingStoredAlgorithm(str, s, s3);
                        break Label_0171;
                    }
                    catch (StandardException ex) {
                        throw Util.generateCsSQLException(ex);
                    }
                }
                s3 = this.substitutePassword(str, s3, properties, true);
                hashPasswordUsingStoredAlgorithm = s;
            }
            else {
                try {
                    final Properties databaseProperties = this.getDatabaseProperties();
                    if (databaseProperties != null) {
                        this.hashUsingDefaultAlgorithm(str, s, databaseProperties);
                    }
                }
                catch (StandardException ex2) {
                    throw Util.generateCsSQLException(ex2);
                }
                s3 = this.getSystemProperty(concat);
                hashPasswordUsingStoredAlgorithm = s;
                if (s3 != null && int1 == 8) {
                    s3 = this.substitutePassword(str, s3, properties, false);
                }
            }
        }
        final boolean b = s3 != null && s3.equals(hashPasswordUsingStoredAlgorithm);
        if (!b && int1 == 8) {
            throw Util.generateCsSQLException("08004.C.12");
        }
        return b;
    }
    
    private String hashPasswordUsingStoredAlgorithm(final String s, final String s2, final String s3) throws StandardException {
        if (s3.startsWith("3b60")) {
            return this.hashPasswordSHA1Scheme(s2);
        }
        return new PasswordHasher(s3).hashAndEncode(s, s2);
    }
}
