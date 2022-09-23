// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.File;

public class DefaultMailbox extends Mailbox
{
    private String home;
    
    public DefaultMailbox() {
        this.home = System.getProperty("user.home");
    }
    
    public MailFile getMailFile(final String user, final String folder) {
        return new DefaultMailFile(this.filename(user, folder));
    }
    
    public String filename(final String user, String folder) {
        try {
            final char c = folder.charAt(0);
            if (c == File.separatorChar) {
                return folder;
            }
            if (c == '~') {
                final int i = folder.indexOf(File.separatorChar);
                String tail = "";
                if (i > 0) {
                    tail = folder.substring(i);
                    folder = folder.substring(0, i);
                }
                return this.home + tail;
            }
            if (folder.equalsIgnoreCase("INBOX")) {
                folder = "INBOX";
            }
            return this.home + File.separator + folder;
        }
        catch (StringIndexOutOfBoundsException e) {
            return folder;
        }
    }
}
