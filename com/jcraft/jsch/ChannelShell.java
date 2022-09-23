// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Hashtable;

public class ChannelShell extends ChannelSession
{
    ChannelShell() {
        this.pty = true;
    }
    
    @Override
    public void start() throws JSchException {
        final Session _session = this.getSession();
        try {
            this.sendRequests();
            final Request request = new RequestShell();
            request.request(_session, this);
        }
        catch (Exception e) {
            if (e instanceof JSchException) {
                throw (JSchException)e;
            }
            if (e instanceof Throwable) {
                throw new JSchException("ChannelShell", e);
            }
            throw new JSchException("ChannelShell");
        }
        if (this.io.in != null) {
            (this.thread = new Thread(this)).setName("Shell for " + _session.host);
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
}
