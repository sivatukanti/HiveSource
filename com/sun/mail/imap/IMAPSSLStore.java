// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.imap;

import javax.mail.URLName;
import javax.mail.Session;

public class IMAPSSLStore extends IMAPStore
{
    public IMAPSSLStore(final Session session, final URLName url) {
        super(session, url, "imaps", 993, true);
    }
}
