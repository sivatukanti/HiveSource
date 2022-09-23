// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.util.RMHAUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.ha.HAServiceProtocol;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.Controller;
import org.apache.hadoop.yarn.server.resourcemanager.security.QueueACLsManager;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.webapp.YarnWebParams;
import org.apache.hadoop.yarn.webapp.WebApp;

public class RMWebApp extends WebApp implements YarnWebParams
{
    private final ResourceManager rm;
    private boolean standby;
    
    public RMWebApp(final ResourceManager rm) {
        this.standby = false;
        this.rm = rm;
    }
    
    @Override
    public void setup() {
        this.bind(JAXBContextResolver.class);
        this.bind(RMWebServices.class);
        this.bind(GenericExceptionHandler.class);
        this.bind(RMWebApp.class).toInstance(this);
        if (this.rm != null) {
            this.bind(ResourceManager.class).toInstance(this.rm);
            this.bind(RMContext.class).toInstance(this.rm.getRMContext());
            this.bind(ApplicationACLsManager.class).toInstance(this.rm.getApplicationACLsManager());
            this.bind(QueueACLsManager.class).toInstance(this.rm.getQueueACLsManager());
        }
        this.route("/", RmController.class);
        this.route(StringHelper.pajoin("/nodes", "node.state"), RmController.class, "nodes");
        this.route(StringHelper.pajoin("/apps", "app.state"), RmController.class);
        this.route("/cluster", RmController.class, "about");
        this.route(StringHelper.pajoin("/app", "app.id"), RmController.class, "app");
        this.route("/scheduler", RmController.class, "scheduler");
        this.route(StringHelper.pajoin("/queue", "queue.name"), RmController.class, "queue");
    }
    
    @Override
    protected Class<? extends GuiceContainer> getWebAppFilterClass() {
        return RMWebAppFilter.class;
    }
    
    public void checkIfStandbyRM() {
        this.standby = (this.rm.getRMContext().getHAServiceState() == HAServiceProtocol.HAServiceState.STANDBY);
    }
    
    public boolean isStandby() {
        return this.standby;
    }
    
    @Override
    public String getRedirectPath() {
        if (this.standby) {
            return this.buildRedirectPath();
        }
        return super.getRedirectPath();
    }
    
    private String buildRedirectPath() {
        final YarnConfiguration yarnConf = new YarnConfiguration(this.rm.getConfig());
        final String activeRMHAId = RMHAUtils.findActiveRMHAId(yarnConf);
        String path = "";
        if (activeRMHAId != null) {
            yarnConf.set("yarn.resourcemanager.ha.id", activeRMHAId);
            final InetSocketAddress sock = YarnConfiguration.useHttps(yarnConf) ? yarnConf.getSocketAddr("yarn.resourcemanager.webapp.https.address", "0.0.0.0:8090", 8090) : yarnConf.getSocketAddr("yarn.resourcemanager.webapp.address", "0.0.0.0:8088", 8088);
            path = sock.getHostName() + ":" + Integer.toString(sock.getPort());
            path = (YarnConfiguration.useHttps(yarnConf) ? ("https://" + path) : ("http://" + path));
        }
        return path;
    }
}
