// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.jce;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.crypto.CipherFactory;
import java.util.Properties;
import org.apache.derby.iapi.services.crypto.CipherFactoryBuilder;

public class JCECipherFactoryBuilder implements CipherFactoryBuilder
{
    public CipherFactory createCipherFactory(final boolean b, final Properties properties, final boolean b2) throws StandardException {
        return new JCECipherFactory(b, properties, b2);
    }
}
