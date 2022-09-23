// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

import java.io.IOException;
import org.apache.thrift.protocol.TProtocol;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TIOStreamTransport;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.ArrayList;
import javax.servlet.ServletConfig;
import java.util.Map;
import java.util.Collection;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.TProcessor;
import javax.servlet.http.HttpServlet;

public abstract class TExtensibleServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private TProcessor processor;
    private TProtocolFactory inFactory;
    private TProtocolFactory outFactory;
    private Collection<Map.Entry<String, String>> customHeaders;
    
    protected abstract TProcessor getProcessor();
    
    protected abstract TProtocolFactory getInProtocolFactory();
    
    protected abstract TProtocolFactory getOutProtocolFactory();
    
    @Override
    public final void init(final ServletConfig config) throws ServletException {
        super.init(config);
        this.processor = this.getProcessor();
        this.inFactory = this.getInProtocolFactory();
        this.outFactory = this.getOutProtocolFactory();
        this.customHeaders = new ArrayList<Map.Entry<String, String>>();
        if (this.processor == null) {
            throw new ServletException("processor must be set");
        }
        if (this.inFactory == null) {
            throw new ServletException("inFactory must be set");
        }
        if (this.outFactory == null) {
            throw new ServletException("outFactory must be set");
        }
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        TTransport inTransport = null;
        TTransport outTransport = null;
        try {
            response.setContentType("application/x-thrift");
            if (null != this.customHeaders) {
                for (final Map.Entry<String, String> header : this.customHeaders) {
                    response.addHeader(header.getKey(), header.getValue());
                }
            }
            final InputStream in = request.getInputStream();
            final OutputStream out = response.getOutputStream();
            inTransport = (outTransport = new TIOStreamTransport(in, out));
            final TProtocol inProtocol = this.inFactory.getProtocol(inTransport);
            final TProtocol outProtocol = this.inFactory.getProtocol(outTransport);
            this.processor.process(inProtocol, outProtocol);
            out.flush();
        }
        catch (TException te) {
            throw new ServletException(te);
        }
    }
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    
    public void addCustomHeader(final String key, final String value) {
        this.customHeaders.add(new Map.Entry<String, String>() {
            public String getKey() {
                return key;
            }
            
            public String getValue() {
                return value;
            }
            
            public String setValue(final String value) {
                return null;
            }
        });
    }
    
    public void setCustomHeaders(final Collection<Map.Entry<String, String>> headers) {
        this.customHeaders.clear();
        this.customHeaders.addAll(headers);
    }
}
