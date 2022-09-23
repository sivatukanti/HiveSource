// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.crypto;

import org.apache.derby.io.StorageFactory;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import java.security.SecureRandom;

public interface CipherFactory
{
    public static final int MIN_BOOTPASS_LENGTH = 8;
    public static final int ENCRYPT = 1;
    public static final int DECRYPT = 2;
    
    SecureRandom getSecureRandom();
    
    CipherProvider createNewCipher(final int p0) throws StandardException;
    
    String changeBootPassword(final String p0, final Properties p1, final CipherProvider p2) throws StandardException;
    
    void verifyKey(final boolean p0, final StorageFactory p1, final Properties p2) throws StandardException;
    
    void saveProperties(final Properties p0);
}
