// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ProtocolException;

public interface SaslAuthenticator
{
    boolean authenticate(final String[] p0, final String p1, final String p2, final String p3, final String p4) throws ProtocolException;
}
