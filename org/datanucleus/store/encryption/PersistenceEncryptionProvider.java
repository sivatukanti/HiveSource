// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.encryption;

import org.datanucleus.metadata.AbstractMemberMetaData;

public interface PersistenceEncryptionProvider
{
    Object encryptValue(final AbstractMemberMetaData p0, final Object p1);
    
    Object decryptValue(final AbstractMemberMetaData p0, final Object p1);
}
