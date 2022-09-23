// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Hashtable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ChannelSubsystem extends ChannelSession
{
    boolean xforwading;
    boolean pty;
    boolean want_reply;
    String subsystem;
    
    public ChannelSubsystem() {
        this.xforwading = false;
        this.pty = false;
        this.want_reply = true;
        this.subsystem = "";
    }
    
    @Override
    public void setXForwarding(final boolean foo) {
        this.xforwading = foo;
    }
    
    @Override
    public void setPty(final boolean foo) {
        this.pty = foo;
    }
    
    public void setWantReply(final boolean foo) {
        this.want_reply = foo;
    }
    
    public void setSubsystem(final String foo) {
        this.subsystem = foo;
    }
    
    @Override
    public void start() throws JSchException {
        final Session _session = this.getSession();
        try {
            if (this.xforwading) {
                final Request request = new RequestX11();
                request.request(_session, this);
            }
            if (this.pty) {
                final Request request = new RequestPtyReq();
                request.request(_session, this);
            }
            final Request request = new RequestSubsystem();
            ((RequestSubsystem)request).request(_session, this, this.subsystem, this.want_reply);
        }
        catch (Exception e) {
            if (e instanceof JSchException) {
                throw (JSchException)e;
            }
            if (e instanceof Throwable) {
                throw new JSchException("ChannelSubsystem", e);
            }
            throw new JSchException("ChannelSubsystem");
        }
        if (this.io.in != null) {
            (this.thread = new Thread(this)).setName("Subsystem for " + _session.host);
            if (_session.daemon_thread) {
                this.thread.setDaemon(_session.daemon_thread);
            }
            this.thread.start();
        }
    }
    
    @Override
    void init() throws JSchException {
        this.io.setInputStream(this.getSession().in);
        this.io.setOutputStream(this.getSession().out);
    }
    
    public void setErrStream(final OutputStream out) {
        this.setExtOutputStream(out);
    }
    
    public InputStream getErrStream() throws IOException {
        return this.getExtInputStream();
    }
}
