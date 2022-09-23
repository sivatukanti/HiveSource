// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.FileDescriptor;

public interface MailFile extends FileInterface
{
    boolean lock(final String p0);
    
    void unlock();
    
    void touchlock();
    
    FileDescriptor getFD();
}
