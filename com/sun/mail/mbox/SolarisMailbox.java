// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.File;

public class SolarisMailbox extends Mailbox
{
    private String home;
    private String user;
    
    public SolarisMailbox() {
        this.home = System.getProperty("user.home");
        this.user = System.getProperty("user.name");
    }
    
    public MailFile getMailFile(final String user, final String folder) {
        if (folder.equalsIgnoreCase("INBOX")) {
            return new UNIXInbox(user, this.filename(user, folder));
        }
        return new UNIXFolder(this.filename(user, folder));
    }
    
    public String filename(String user, String folder) {
        try {
            switch (folder.charAt(0)) {
                case '/': {
                    return folder;
                }
                case '~': {
                    final int i = folder.indexOf(File.separatorChar);
                    String tail = "";
                    if (i > 0) {
                        tail = folder.substring(i);
                        folder = folder.substring(0, i);
                    }
                    if (folder.length() == 1) {
                        return this.home + tail;
                    }
                    return "/home/" + folder.substring(1) + tail;
                }
                default: {
                    if (folder.equalsIgnoreCase("INBOX")) {
                        if (user == null) {
                            user = this.user;
                        }
                        return "/var/mail/" + user;
                    }
                    return this.home + File.separator + folder;
                }
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            return folder;
        }
    }
}
