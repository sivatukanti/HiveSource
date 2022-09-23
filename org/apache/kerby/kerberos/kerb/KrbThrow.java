// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb;

public class KrbThrow
{
    public static KrbException out(final MessageCode messageCode) throws KrbException {
        throw new KrbException(Message.getMessage(messageCode));
    }
    
    public static void out(final MessageCode messageCode, final Exception e) throws KrbException {
        throw new KrbException(Message.getMessage(messageCode), e);
    }
    
    public static void out(final MessageCode messageCode, final String message) throws KrbException {
        throw new KrbException(Message.getMessage(messageCode) + ":" + message);
    }
}
