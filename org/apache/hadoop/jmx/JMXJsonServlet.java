// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.jmx;

import org.slf4j.LoggerFactory;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.CompositeData;
import java.lang.reflect.Array;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import java.util.Iterator;
import java.util.Set;
import javax.management.IntrospectionException;
import javax.management.InstanceNotFoundException;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import javax.management.QueryExp;
import java.io.PrintWriter;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.Writer;
import java.io.IOException;
import org.apache.hadoop.http.HttpServer2;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.lang.management.ManagementFactory;
import com.fasterxml.jackson.core.JsonFactory;
import javax.management.MBeanServer;
import org.slf4j.Logger;
import javax.servlet.http.HttpServlet;

public class JMXJsonServlet extends HttpServlet
{
    private static final Logger LOG;
    static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final long serialVersionUID = 1L;
    protected transient MBeanServer mBeanServer;
    protected transient JsonFactory jsonFactory;
    
    @Override
    public void init() throws ServletException {
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
        this.jsonFactory = new JsonFactory();
    }
    
    protected boolean isInstrumentationAccessAllowed(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        return HttpServer2.isInstrumentationAccessAllowed(this.getServletContext(), request, response);
    }
    
    @Override
    protected void doTrace(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405);
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            if (!this.isInstrumentationAccessAllowed(request, response)) {
                return;
            }
            JsonGenerator jg = null;
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                response.setContentType("application/json; charset=utf8");
                response.setHeader("Access-Control-Allow-Methods", "GET");
                response.setHeader("Access-Control-Allow-Origin", "*");
                jg = this.jsonFactory.createGenerator(writer);
                jg.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
                jg.useDefaultPrettyPrinter();
                jg.writeStartObject();
                final String getmethod = request.getParameter("get");
                if (getmethod != null) {
                    final String[] splitStrings = getmethod.split("\\:\\:");
                    if (splitStrings.length != 2) {
                        jg.writeStringField("result", "ERROR");
                        jg.writeStringField("message", "query format is not as expected.");
                        jg.flush();
                        response.setStatus(400);
                        return;
                    }
                    this.listBeans(jg, new ObjectName(splitStrings[0]), splitStrings[1], response);
                }
                else {
                    String qry = request.getParameter("qry");
                    if (qry == null) {
                        qry = "*:*";
                    }
                    this.listBeans(jg, new ObjectName(qry), null, response);
                }
            }
            finally {
                if (jg != null) {
                    jg.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
        }
        catch (IOException e) {
            JMXJsonServlet.LOG.error("Caught an exception while processing JMX request", e);
            response.setStatus(500);
        }
        catch (MalformedObjectNameException e2) {
            JMXJsonServlet.LOG.error("Caught an exception while processing JMX request", e2);
            response.setStatus(400);
        }
    }
    
    private void listBeans(final JsonGenerator jg, final ObjectName qry, final String attribute, final HttpServletResponse response) throws IOException {
        JMXJsonServlet.LOG.debug("Listing beans for " + qry);
        Set<ObjectName> names = null;
        names = this.mBeanServer.queryNames(qry, null);
        jg.writeArrayFieldStart("beans");
        for (final ObjectName oname : names) {
            String code = "";
            Object attributeinfo = null;
            MBeanInfo minfo;
            try {
                minfo = this.mBeanServer.getMBeanInfo(oname);
                code = minfo.getClassName();
                String prs = "";
                try {
                    if ("org.apache.commons.modeler.BaseModelMBean".equals(code)) {
                        prs = "modelerType";
                        code = (String)this.mBeanServer.getAttribute(oname, prs);
                    }
                    if (attribute != null) {
                        prs = attribute;
                        attributeinfo = this.mBeanServer.getAttribute(oname, prs);
                    }
                }
                catch (AttributeNotFoundException e) {
                    JMXJsonServlet.LOG.error("getting attribute " + prs + " of " + oname + " threw an exception", e);
                }
                catch (MBeanException e2) {
                    JMXJsonServlet.LOG.error("getting attribute " + prs + " of " + oname + " threw an exception", e2);
                }
                catch (RuntimeException e3) {
                    JMXJsonServlet.LOG.error("getting attribute " + prs + " of " + oname + " threw an exception", e3);
                }
                catch (ReflectionException e4) {
                    JMXJsonServlet.LOG.error("getting attribute " + prs + " of " + oname + " threw an exception", e4);
                }
            }
            catch (InstanceNotFoundException e7) {
                continue;
            }
            catch (IntrospectionException e5) {
                JMXJsonServlet.LOG.error("Problem while trying to process JMX query: " + qry + " with MBean " + oname, e5);
                continue;
            }
            catch (ReflectionException e6) {
                JMXJsonServlet.LOG.error("Problem while trying to process JMX query: " + qry + " with MBean " + oname, e6);
                continue;
            }
            jg.writeStartObject();
            jg.writeStringField("name", oname.toString());
            jg.writeStringField("modelerType", code);
            if (attribute != null && attributeinfo == null) {
                jg.writeStringField("result", "ERROR");
                jg.writeStringField("message", "No attribute with name " + attribute + " was found.");
                jg.writeEndObject();
                jg.writeEndArray();
                jg.close();
                response.setStatus(404);
                return;
            }
            if (attribute != null) {
                this.writeAttribute(jg, attribute, attributeinfo);
            }
            else {
                final MBeanAttributeInfo[] attrs = minfo.getAttributes();
                for (int i = 0; i < attrs.length; ++i) {
                    this.writeAttribute(jg, oname, attrs[i]);
                }
            }
            jg.writeEndObject();
        }
        jg.writeEndArray();
    }
    
    private void writeAttribute(final JsonGenerator jg, final ObjectName oname, final MBeanAttributeInfo attr) throws IOException {
        if (!attr.isReadable()) {
            return;
        }
        final String attName = attr.getName();
        if ("modelerType".equals(attName)) {
            return;
        }
        if (attName.indexOf("=") >= 0 || attName.indexOf(":") >= 0 || attName.indexOf(" ") >= 0) {
            return;
        }
        Object value = null;
        try {
            value = this.mBeanServer.getAttribute(oname, attName);
        }
        catch (RuntimeMBeanException e) {
            if (e.getCause() instanceof UnsupportedOperationException) {
                JMXJsonServlet.LOG.debug("getting attribute " + attName + " of " + oname + " threw an exception", e);
            }
            else {
                JMXJsonServlet.LOG.error("getting attribute " + attName + " of " + oname + " threw an exception", e);
            }
            return;
        }
        catch (RuntimeErrorException e2) {
            JMXJsonServlet.LOG.debug("getting attribute " + attName + " of " + oname + " threw an exception", e2);
            return;
        }
        catch (AttributeNotFoundException e6) {
            return;
        }
        catch (MBeanException e3) {
            JMXJsonServlet.LOG.error("getting attribute " + attName + " of " + oname + " threw an exception", e3);
            return;
        }
        catch (RuntimeException e4) {
            JMXJsonServlet.LOG.error("getting attribute " + attName + " of " + oname + " threw an exception", e4);
            return;
        }
        catch (ReflectionException e5) {
            JMXJsonServlet.LOG.error("getting attribute " + attName + " of " + oname + " threw an exception", e5);
            return;
        }
        catch (InstanceNotFoundException e7) {
            return;
        }
        this.writeAttribute(jg, attName, value);
    }
    
    private void writeAttribute(final JsonGenerator jg, final String attName, final Object value) throws IOException {
        jg.writeFieldName(attName);
        this.writeObject(jg, value);
    }
    
    private void writeObject(final JsonGenerator jg, final Object value) throws IOException {
        if (value == null) {
            jg.writeNull();
        }
        else {
            final Class<?> c = value.getClass();
            if (c.isArray()) {
                jg.writeStartArray();
                for (int len = Array.getLength(value), j = 0; j < len; ++j) {
                    final Object item = Array.get(value, j);
                    this.writeObject(jg, item);
                }
                jg.writeEndArray();
            }
            else if (value instanceof Number) {
                final Number n = (Number)value;
                jg.writeNumber(n.toString());
            }
            else if (value instanceof Boolean) {
                final Boolean b = (Boolean)value;
                jg.writeBoolean(b);
            }
            else if (value instanceof CompositeData) {
                final CompositeData cds = (CompositeData)value;
                final CompositeType comp = cds.getCompositeType();
                final Set<String> keys = comp.keySet();
                jg.writeStartObject();
                for (final String key : keys) {
                    this.writeAttribute(jg, key, cds.get(key));
                }
                jg.writeEndObject();
            }
            else if (value instanceof TabularData) {
                final TabularData tds = (TabularData)value;
                jg.writeStartArray();
                for (final Object entry : tds.values()) {
                    this.writeObject(jg, entry);
                }
                jg.writeEndArray();
            }
            else {
                jg.writeString(value.toString());
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(JMXJsonServlet.class);
    }
}
