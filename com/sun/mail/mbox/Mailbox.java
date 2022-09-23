// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

public abstract class Mailbox
{
    public abstract MailFile getMailFile(final String p0, final String p1);
    
    public abstract String filename(final String p0, final String p1);
}
