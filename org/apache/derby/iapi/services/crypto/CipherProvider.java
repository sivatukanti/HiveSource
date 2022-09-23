// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.crypto;

import org.apache.derby.iapi.error.StandardException;

public interface CipherProvider
{
    int encrypt(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws StandardException;
    
    int decrypt(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws StandardException;
    
    int getEncryptionBlockSize();
}
