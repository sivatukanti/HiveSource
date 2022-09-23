// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import java.util.Map;
import org.eclipse.jetty.util.resource.Resource;
import java.util.concurrent.Callable;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.eclipse.jetty.util.log.Logger;

public class JettyWebXmlConfiguration extends AbstractConfiguration
{
    private static final Logger LOG;
    @Deprecated
    public static final String PROPERTY_THIS_WEB_INF_URL = "this.web-inf.url";
    public static final String PROPERTY_WEB_INF_URI = "web-inf.uri";
    public static final String PROPERTY_WEB_INF = "web-inf";
    public static final String XML_CONFIGURATION = "org.eclipse.jetty.webapp.JettyWebXmlConfiguration";
    public static final String JETTY_WEB_XML = "jetty-web.xml";
    
    @Override
    public void configure(final WebAppContext context) throws Exception {
        if (context.isStarted()) {
            JettyWebXmlConfiguration.LOG.debug("Cannot configure webapp after it is started", new Object[0]);
            return;
        }
        JettyWebXmlConfiguration.LOG.debug("Configuring web-jetty.xml", new Object[0]);
        final Resource web_inf = context.getWebInf();
        if (web_inf != null && web_inf.isDirectory()) {
            Resource jetty = web_inf.addPath("jetty8-web.xml");
            if (!jetty.exists()) {
                jetty = web_inf.addPath("jetty-web.xml");
            }
            if (!jetty.exists()) {
                jetty = web_inf.addPath("web-jetty.xml");
            }
            if (jetty.exists()) {
                if (JettyWebXmlConfiguration.LOG.isDebugEnabled()) {
                    JettyWebXmlConfiguration.LOG.debug("Configure: " + jetty, new Object[0]);
                }
                final Object xml_attr = context.getAttribute("org.eclipse.jetty.webapp.JettyWebXmlConfiguration");
                context.removeAttribute("org.eclipse.jetty.webapp.JettyWebXmlConfiguration");
                final XmlConfiguration jetty_config = (XmlConfiguration)((xml_attr instanceof XmlConfiguration) ? xml_attr : new XmlConfiguration(jetty.getURI().toURL()));
                this.setupXmlConfiguration(jetty_config, web_inf);
                try {
                    final XmlConfiguration config = jetty_config;
                    context.runWithoutCheckingServerClasses(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            config.configure(context);
                            return null;
                        }
                    });
                }
                catch (Exception e) {
                    JettyWebXmlConfiguration.LOG.warn("Error applying {}", jetty);
                    throw e;
                }
            }
        }
    }
    
    private void setupXmlConfiguration(final XmlConfiguration jetty_config, final Resource web_inf) throws IOException {
        final Map<String, String> props = jetty_config.getProperties();
        props.put("this.web-inf.url", web_inf.getURI().toString());
        props.put("web-inf.uri", web_inf.getURI().toString());
        props.put("web-inf", web_inf.toString());
    }
    
    static {
        LOG = Log.getLogger(JettyWebXmlConfiguration.class);
    }
}
