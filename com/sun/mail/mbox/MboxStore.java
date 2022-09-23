// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.URLName;
import javax.mail.Session;
import javax.mail.Flags;
import javax.mail.Store;

public class MboxStore extends Store
{
    String user;
    String home;
    Mailbox mb;
    static Flags permFlags;
    
    public MboxStore(final Session session, final URLName url) {
        super(session, url);
        this.user = System.getProperty("user.name");
        this.home = System.getProperty("user.home");
        final String os = System.getProperty("os.name");
        try {
            final String cl = "com.sun.mail.mbox." + os + "Mailbox";
            this.mb = (Mailbox)Class.forName(cl).newInstance();
        }
        catch (Exception e) {
            this.mb = new DefaultMailbox();
        }
    }
    
    protected boolean protocolConnect(final String host, final int port, final String user, final String passwd) throws MessagingException {
        return true;
    }
    
    protected void setURLName(URLName url) {
        if (url != null && (url.getUsername() != null || url.getHost() != null || url.getFile() != null)) {
            url = new URLName(url.getProtocol(), null, -1, null, null, null);
        }
        super.setURLName(url);
    }
    
    public Folder getDefaultFolder() throws MessagingException {
        this.checkConnected();
        return new MboxFolder(this, null);
    }
    
    public Folder getFolder(final String name) throws MessagingException {
        this.checkConnected();
        return new MboxFolder(this, name);
    }
    
    public Folder getFolder(final URLName url) throws MessagingException {
        this.checkConnected();
        return this.getFolder(url.getFile());
    }
    
    private void checkConnected() throws MessagingException {
        if (!this.isConnected()) {
            throw new MessagingException("Not connected");
        }
    }
    
    MailFile getMailFile(final String folder) {
        return this.mb.getMailFile(this.user, folder);
    }
    
    Session getSession() {
        return this.session;
    }
    
    static {
        (MboxStore.permFlags = new Flags()).add(Flags.Flag.SEEN);
        MboxStore.permFlags.add(Flags.Flag.RECENT);
        MboxStore.permFlags.add(Flags.Flag.DELETED);
        MboxStore.permFlags.add(Flags.Flag.FLAGGED);
        MboxStore.permFlags.add(Flags.Flag.ANSWERED);
        MboxStore.permFlags.add(Flags.Flag.DRAFT);
        MboxStore.permFlags.add(Flags.Flag.USER);
    }
}
