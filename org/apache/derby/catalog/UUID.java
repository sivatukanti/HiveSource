// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

import java.io.Externalizable;

public interface UUID extends Externalizable
{
    public static final int UUID_BYTE_LENGTH = 16;
    
    String toANSIidentifier();
    
    UUID cloneMe();
}
