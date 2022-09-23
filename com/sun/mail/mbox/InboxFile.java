// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

public interface InboxFile extends MailFile
{
    boolean openLock(final String p0);
    
    void closeLock();
}
