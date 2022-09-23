// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusFatalUserException;

public class InvalidMetaDataException extends NucleusFatalUserException
{
    protected String messageKey;
    
    protected InvalidMetaDataException(final String key, final String message) {
        super(message);
        this.messageKey = key;
    }
    
    public InvalidMetaDataException(final Localiser localiser, final String key) {
        this(key, localiser.msg(key));
    }
    
    public InvalidMetaDataException(final Localiser localiser, final String key, final Object param1) {
        this(key, localiser.msg(key, param1));
    }
    
    public InvalidMetaDataException(final Localiser localiser, final String key, final Object param1, final Object param2) {
        this(key, localiser.msg(key, param1, param2));
    }
    
    public InvalidMetaDataException(final Localiser localiser, final String key, final Object param1, final Object param2, final Object param3) {
        this(key, localiser.msg(key, param1, param2, param3));
    }
    
    public InvalidMetaDataException(final Localiser localiser, final String key, final Object param1, final Object param2, final Object param3, final Object param4) {
        this(key, localiser.msg(key, param1, param2, param3, param4));
    }
    
    public InvalidMetaDataException(final Localiser localiser, final String key, final Object param1, final Object param2, final Object param3, final Object param4, final Object param5) {
        this(key, localiser.msg(key, param1, param2, param3, param4, param5));
    }
    
    public String getMessageKey() {
        return this.messageKey;
    }
}
