// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import org.eclipse.jetty.util.resource.Resource;
import java.net.URL;
import org.eclipse.jetty.util.Loader;
import javax.servlet.Servlet;
import org.xml.sax.InputSource;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jetty.xml.XmlParser;
import org.eclipse.jetty.util.log.Logger;

public class WebDescriptor extends Descriptor
{
    private static final Logger LOG;
    protected static XmlParser _parserSingleton;
    protected MetaDataComplete _metaDataComplete;
    protected int _majorVersion;
    protected int _minorVersion;
    protected ArrayList<String> _classNames;
    protected boolean _distributable;
    protected boolean _isOrdered;
    protected List<String> _ordering;
    
    @Override
    public void ensureParser() throws ClassNotFoundException {
        synchronized (WebDescriptor.class) {
            if (WebDescriptor._parserSingleton == null) {
                WebDescriptor._parserSingleton = newParser(this.isValidating());
            }
        }
        if (WebDescriptor._parserSingleton.isValidating() == this.isValidating()) {
            this._parser = WebDescriptor._parserSingleton;
        }
        else {
            this._parser = newParser(this.isValidating());
        }
    }
    
    public static XmlParser newParser(final boolean validating) throws ClassNotFoundException {
        final XmlParser xmlParser = new XmlParser(validating) {
            boolean mapped = false;
            
            @Override
            protected InputSource resolveEntity(final String pid, final String sid) {
                if (!this.mapped) {
                    this.mapResources();
                    this.mapped = true;
                }
                final InputSource is = super.resolveEntity(pid, sid);
                return is;
            }
            
            void mapResources() {
                final URL dtd22 = Loader.getResource(Servlet.class, "javax/servlet/resources/web-app_2_2.dtd");
                final URL dtd23 = Loader.getResource(Servlet.class, "javax/servlet/resources/web-app_2_3.dtd");
                final URL j2ee14xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/j2ee_1_4.xsd");
                final URL javaee5 = Loader.getResource(Servlet.class, "javax/servlet/resources/javaee_5.xsd");
                final URL javaee6 = Loader.getResource(Servlet.class, "javax/servlet/resources/javaee_6.xsd");
                final URL javaee7 = Loader.getResource(Servlet.class, "javax/servlet/resources/javaee_7.xsd");
                final URL webapp24xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/web-app_2_4.xsd");
                final URL webapp25xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/web-app_2_5.xsd");
                final URL webapp30xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/web-app_3_0.xsd");
                final URL webapp31xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/web-app_3_1.xsd");
                final URL webcommon30xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/web-common_3_0.xsd");
                final URL webcommon31xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/web-common_3_1.xsd");
                final URL webfragment30xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/web-fragment_3_0.xsd");
                final URL webfragment31xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/web-fragment_3_1.xsd");
                final URL schemadtd = Loader.getResource(Servlet.class, "javax/servlet/resources/XMLSchema.dtd");
                final URL xmlxsd = Loader.getResource(Servlet.class, "javax/servlet/resources/xml.xsd");
                final URL webservice11xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/j2ee_web_services_client_1_1.xsd");
                final URL webservice12xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/javaee_web_services_client_1_2.xsd");
                final URL webservice13xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/javaee_web_services_client_1_3.xsd");
                final URL webservice14xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/javaee_web_services_client_1_4.xsd");
                final URL datatypesdtd = Loader.getResource(Servlet.class, "javax/servlet/resources/datatypes.dtd");
                URL jsp20xsd = null;
                URL jsp21xsd = null;
                URL jsp22xsd = null;
                URL jsp23xsd = null;
                try {
                    jsp20xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/jsp_2_0.xsd");
                    jsp21xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/jsp_2_1.xsd");
                    jsp22xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/jsp_2_2.xsd");
                    jsp23xsd = Loader.getResource(Servlet.class, "javax/servlet/resources/jsp_2_3.xsd");
                }
                catch (Exception e) {
                    WebDescriptor.LOG.ignore(e);
                }
                finally {
                    if (jsp20xsd == null) {
                        jsp20xsd = Loader.getResource(Servlet.class, "javax/servlet/jsp/resources/jsp_2_0.xsd");
                    }
                    if (jsp21xsd == null) {
                        jsp21xsd = Loader.getResource(Servlet.class, "javax/servlet/jsp/resources/jsp_2_1.xsd");
                    }
                    if (jsp22xsd == null) {
                        jsp22xsd = Loader.getResource(Servlet.class, "javax/servlet/jsp/resources/jsp_2_2.xsd");
                    }
                    if (jsp23xsd == null) {
                        jsp23xsd = Loader.getResource(Servlet.class, "javax/servlet/jsp/resources/jsp_2_3.xsd");
                    }
                }
                this.redirectEntity("web-app_2_2.dtd", dtd22);
                this.redirectEntity("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN", dtd22);
                this.redirectEntity("web.dtd", dtd23);
                this.redirectEntity("web-app_2_3.dtd", dtd23);
                this.redirectEntity("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", dtd23);
                this.redirectEntity("XMLSchema.dtd", schemadtd);
                this.redirectEntity("http://www.w3.org/2001/XMLSchema.dtd", schemadtd);
                this.redirectEntity("-//W3C//DTD XMLSCHEMA 200102//EN", schemadtd);
                this.redirectEntity("jsp_2_0.xsd", jsp20xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/j2ee/jsp_2_0.xsd", jsp20xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/jsp_2_1.xsd", jsp21xsd);
                this.redirectEntity("jsp_2_2.xsd", jsp22xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/jsp_2_2.xsd", jsp22xsd);
                this.redirectEntity("jsp_2_3.xsd", jsp23xsd);
                this.redirectEntity("http://xmlns.jcp.org/xml/ns/javaee/jsp_2_3.xsd", jsp23xsd);
                this.redirectEntity("j2ee_1_4.xsd", j2ee14xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/j2ee/j2ee_1_4.xsd", j2ee14xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/javaee_5.xsd", javaee5);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/javaee_6.xsd", javaee6);
                this.redirectEntity("http://xmlns.jcp.org/xml/ns/javaee/javaee_7.xsd", javaee7);
                this.redirectEntity("web-app_2_4.xsd", webapp24xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd", webapp24xsd);
                this.redirectEntity("web-app_2_5.xsd", webapp25xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd", webapp25xsd);
                this.redirectEntity("web-app_3_0.xsd", webapp30xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd", webapp30xsd);
                this.redirectEntity("web-common_3_0.xsd", webcommon30xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/web-common_3_0.xsd", webcommon30xsd);
                this.redirectEntity("web-fragment_3_0.xsd", webfragment30xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/web-fragment_3_0.xsd", webfragment30xsd);
                this.redirectEntity("web-app_3_1.xsd", webapp31xsd);
                this.redirectEntity("http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd", webapp31xsd);
                this.redirectEntity("web-common_3_1.xsd", webcommon30xsd);
                this.redirectEntity("http://xmlns.jcp.org/xml/ns/javaee/web-common_3_1.xsd", webcommon31xsd);
                this.redirectEntity("web-fragment_3_1.xsd", webfragment30xsd);
                this.redirectEntity("http://xmlns.jcp.org/xml/ns/javaee/web-fragment_3_1.xsd", webfragment31xsd);
                this.redirectEntity("xml.xsd", xmlxsd);
                this.redirectEntity("http://www.w3.org/2001/xml.xsd", xmlxsd);
                this.redirectEntity("datatypes.dtd", datatypesdtd);
                this.redirectEntity("http://www.w3.org/2001/datatypes.dtd", datatypesdtd);
                this.redirectEntity("j2ee_web_services_client_1_1.xsd", webservice11xsd);
                this.redirectEntity("http://www.ibm.com/webservices/xsd/j2ee_web_services_client_1_1.xsd", webservice11xsd);
                this.redirectEntity("javaee_web_services_client_1_2.xsd", webservice12xsd);
                this.redirectEntity("http://www.ibm.com/webservices/xsd/javaee_web_services_client_1_2.xsd", webservice12xsd);
                this.redirectEntity("javaee_web_services_client_1_3.xsd", webservice13xsd);
                this.redirectEntity("http://java.sun.com/xml/ns/javaee/javaee_web_services_client_1_3.xsd", webservice13xsd);
                this.redirectEntity("javaee_web_services_client_1_4.xsd", webservice14xsd);
                this.redirectEntity("http://xmlns.jcp.org/xml/ns/javaee/javaee_web_services_client_1_4.xsd", webservice14xsd);
            }
        };
        return xmlParser;
    }
    
    public WebDescriptor(final Resource xml) {
        super(xml);
        this._majorVersion = 3;
        this._minorVersion = 0;
        this._classNames = new ArrayList<String>();
        this._isOrdered = false;
        this._ordering = new ArrayList<String>();
    }
    
    @Override
    public void parse() throws Exception {
        super.parse();
        this.processVersion();
        this.processOrdering();
    }
    
    public MetaDataComplete getMetaDataComplete() {
        return this._metaDataComplete;
    }
    
    public int getMajorVersion() {
        return this._majorVersion;
    }
    
    public int getMinorVersion() {
        return this._minorVersion;
    }
    
    public void processVersion() {
        final String version = this._root.getAttribute("version", "DTD");
        if ("DTD".equals(version)) {
            this._majorVersion = 2;
            this._minorVersion = 3;
            final String dtd = this._parser.getDTD();
            if (dtd != null && dtd.indexOf("web-app_2_2") >= 0) {
                this._majorVersion = 2;
                this._minorVersion = 2;
            }
        }
        else {
            final int dot = version.indexOf(".");
            if (dot > 0) {
                this._majorVersion = Integer.parseInt(version.substring(0, dot));
                this._minorVersion = Integer.parseInt(version.substring(dot + 1));
            }
        }
        if (this._majorVersion <= 2 && this._minorVersion < 5) {
            this._metaDataComplete = MetaDataComplete.True;
        }
        else {
            final String s = this._root.getAttribute("metadata-complete");
            if (s == null) {
                this._metaDataComplete = MetaDataComplete.NotSet;
            }
            else {
                this._metaDataComplete = (Boolean.valueOf(s) ? MetaDataComplete.True : MetaDataComplete.False);
            }
        }
        if (WebDescriptor.LOG.isDebugEnabled()) {
            WebDescriptor.LOG.debug(this._xml.toString() + ": Calculated metadatacomplete = " + this._metaDataComplete + " with version=" + version, new Object[0]);
        }
    }
    
    public void processOrdering() {
        final XmlParser.Node ordering = this._root.get("absolute-ordering");
        if (ordering == null) {
            return;
        }
        this._isOrdered = true;
        final Iterator<Object> iter = ordering.iterator();
        XmlParser.Node node = null;
        while (iter.hasNext()) {
            final Object o = iter.next();
            if (!(o instanceof XmlParser.Node)) {
                continue;
            }
            node = (XmlParser.Node)o;
            if (node.getTag().equalsIgnoreCase("others")) {
                this._ordering.add("others");
            }
            else {
                if (!node.getTag().equalsIgnoreCase("name")) {
                    continue;
                }
                this._ordering.add(node.toString(false, true));
            }
        }
    }
    
    public void addClassName(final String className) {
        if (!this._classNames.contains(className)) {
            this._classNames.add(className);
        }
    }
    
    public ArrayList<String> getClassNames() {
        return this._classNames;
    }
    
    public void setDistributable(final boolean distributable) {
        this._distributable = distributable;
    }
    
    public boolean isDistributable() {
        return this._distributable;
    }
    
    @Override
    public void setValidating(final boolean validating) {
        this._validating = validating;
    }
    
    public boolean isValidating() {
        return this._validating;
    }
    
    public boolean isOrdered() {
        return this._isOrdered;
    }
    
    public List<String> getOrdering() {
        return this._ordering;
    }
    
    static {
        LOG = Log.getLogger(WebDescriptor.class);
    }
}
