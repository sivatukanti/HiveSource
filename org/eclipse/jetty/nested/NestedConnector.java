// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.nested;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.io.IOException;
import org.eclipse.jetty.server.AbstractConnector;

public class NestedConnector extends AbstractConnector
{
    String _serverInfo;
    
    public NestedConnector() {
        this.setAcceptors(0);
        this.setForwarded(true);
    }
    
    public void open() throws IOException {
    }
    
    public void close() throws IOException {
    }
    
    public int getLocalPort() {
        return -1;
    }
    
    public Object getConnection() {
        return null;
    }
    
    @Override
    protected void accept(final int acceptorID) throws IOException, InterruptedException {
        throw new IllegalStateException();
    }
    
    public void service(final ServletRequest outerRequest, final ServletResponse outerResponse) throws IOException, ServletException {
        final HttpServletRequest outerServletRequest = (HttpServletRequest)outerRequest;
        final HttpServletResponse outerServletResponse = (HttpServletResponse)outerResponse;
        final NestedConnection connection = new NestedConnection(this, new NestedEndPoint(outerServletRequest, outerServletResponse), outerServletRequest, outerServletResponse, this._serverInfo);
        connection.service();
    }
}
