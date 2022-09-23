// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.security.Key;

public class KeyTypeException extends KeyException
{
    public KeyTypeException(final Class<? extends Key> expectedKeyClass) {
        super("Invalid key: Must be an instance of " + expectedKeyClass);
    }
}
