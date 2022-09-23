// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Hashtable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ChannelExec extends ChannelSession
{
    byte[] command;
    
    public ChannelExec() {
        this.command = new byte[0];
    }
    
    @Override
    public void start() throws JSchException {
        final Session _session = this.getSession();
        try {
            this.sendRequests();
            final Request request = new RequestExec(this.command);
            request.request(_session, this);
        }
        catch (Exception e) {
            if (e instanceof JSchException) {
                throw (JSchException)e;
            }
            if (e instanceof Throwable) {
                throw new JSchException("ChannelExec", e);
            }
            throw new JSchException("ChannelExec");
        }
        if (this.io.in != null) {
            (this.thread = new Thread(this)).setName("Exec thread " + _session.getHost());
            if (_session.daemon_thread) {
                this.thread.setDaemon(_session.daemon_thread);
            }
            this.thread.start();
        }
    }
    
    public void setCommand(final String command) {
        this.command = Util.str2byte(command);
    }
    
    public void setCommand(final byte[] command) {
        this.command = command;
    }
    
    @Override
    void init() throws JSchException {
        this.io.setInputStream(this.getSession().in);
        this.io.setOutputStream(this.getSession().out);
    }
    
    public void setErrStream(final OutputStream out) {
        this.setExtOutputStream(out);
    }
    
    public void setErrStream(final OutputStream out, final boolean dontclose) {
        this.setExtOutputStream(out, dontclose);
    }
    
    public InputStream getErrStream() throws IOException {
        return this.getExtInputStream();
    }
}
