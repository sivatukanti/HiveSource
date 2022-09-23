// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.Localiser;

public class InvalidPrimaryKeyException extends InvalidClassMetaDataException
{
    public InvalidPrimaryKeyException(final Localiser localiser, final String key, final String className) {
        super(localiser, key, className);
    }
    
    public InvalidPrimaryKeyException(final Localiser localiser, final String key, final String className, final Object param1) {
        super(localiser, key, className, param1);
    }
    
    public InvalidPrimaryKeyException(final Localiser localiser, final String key, final String className, final Object param1, final Object param2) {
        super(localiser, key, className, param1, param2);
    }
    
    public InvalidPrimaryKeyException(final Localiser localiser, final String key, final String className, final Object param1, final Object param2, final Object param3) {
        super(localiser, key, className, param1, param2, param3);
        this.messageKey = key;
    }
    
    public InvalidPrimaryKeyException(final Localiser localiser, final String key, final String className, final Object param1, final Object param2, final Object param3, final Object param4) {
        super(localiser, key, className, param1, param2, param3, param4);
        this.messageKey = key;
    }
}
