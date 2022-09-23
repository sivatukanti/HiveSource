// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.crypto;

import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;

public interface CipherFactoryBuilder
{
    CipherFactory createCipherFactory(final boolean p0, final Properties p1, final boolean p2) throws StandardException;
}
