// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.jdbc.InternalDriver;
import java.io.PrintWriter;
import java.util.Properties;

public class JDBCBoot
{
    private Properties bootProperties;
    private static final String NETWORK_SERVER_AUTOSTART_CLASS_NAME = "org.apache.derby.iapi.jdbc.DRDAServerStarter";
    
    public JDBCBoot() {
        this.bootProperties = new Properties();
    }
    
    void addProperty(final String key, final String value) {
        this.bootProperties.put(key, value);
    }
    
    public void boot(final String s, final PrintWriter printWriter) {
        if (InternalDriver.activeDriver() == null) {
            this.addProperty("derby.service.jdbc", "org.apache.derby.jdbc.InternalDriver");
            this.addProperty("derby.service.authentication", "org.apache.derby.iapi.jdbc.AuthenticationService");
            Monitor.startMonitor(this.bootProperties, printWriter);
            if (Boolean.valueOf(PropertyUtil.getSystemProperty("derby.drda.startNetworkServer"))) {
                try {
                    Monitor.startSystemModule("org.apache.derby.iapi.jdbc.DRDAServerStarter");
                }
                catch (StandardException ex) {
                    Monitor.logTextMessage("J102", ex.getMessage());
                }
            }
        }
    }
}
