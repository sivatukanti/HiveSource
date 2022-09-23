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
import javax.servlet.ServletException;
import org.apache.thrift.transport.TIOStreamTransport;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.TProcessor;
import javax.servlet.http.HttpServlet;

public class TServlet extends HttpServlet
{
    private final TProcessor processor;
    private final TProtocolFactory inProtocolFactory;
    private final TProtocolFactory outProtocolFactory;
    private final Collection<Map.Entry<String, String>> customHeaders;
    
    public TServlet(final TProcessor processor, final TProtocolFactory inProtocolFactory, final TProtocolFactory outProtocolFactory) {
        this.processor = processor;
        this.inProtocolFactory = inProtocolFactory;
        this.outProtocolFactory = outProtocolFactory;
        this.customHeaders = new ArrayList<Map.Entry<String, String>>();
    }
    
    public TServlet(final TProcessor processor, final TProtocolFactory protocolFactory) {
        this(processor, protocolFactory, protocolFactory);
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
            final TProtocol inProtocol = this.inProtocolFactory.getProtocol(inTransport);
            final TProtocol outProtocol = this.outProtocolFactory.getProtocol(outTransport);
            this.processor.process(inProtocol, outProtocol);
            out.flush();
        }
        catch (TException te) {
            throw new ServletException(te);
        }
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
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
