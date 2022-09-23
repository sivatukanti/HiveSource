// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNodeReport;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.mortbay.util.ajax.JSON;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import java.util.ArrayList;
import javax.management.NotCompliantMBeanException;
import org.apache.hadoop.metrics2.util.MBeans;
import javax.management.StandardMBean;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.commons.logging.Log;

public class RMNMInfo implements RMNMInfoBeans
{
    private static final Log LOG;
    private RMContext rmContext;
    private ResourceScheduler scheduler;
    
    public RMNMInfo(final RMContext rmc, final ResourceScheduler sched) {
        this.rmContext = rmc;
        this.scheduler = sched;
        try {
            final StandardMBean bean = new StandardMBean((T)this, (Class<T>)RMNMInfoBeans.class);
            MBeans.register("ResourceManager", "RMNMInfo", bean);
        }
        catch (NotCompliantMBeanException e) {
            RMNMInfo.LOG.warn("Error registering RMNMInfo MBean", e);
        }
        RMNMInfo.LOG.info("Registered RMNMInfo MBean");
    }
    
    @Override
    public String getLiveNodeManagers() {
        final Collection<RMNode> nodes = this.rmContext.getRMNodes().values();
        final List<InfoMap> nodesInfo = new ArrayList<InfoMap>();
        for (final RMNode ni : nodes) {
            final SchedulerNodeReport report = this.scheduler.getNodeReport(ni.getNodeID());
            final InfoMap info = new InfoMap();
            ((HashMap<String, String>)info).put("HostName", ni.getHostName());
            ((HashMap<String, String>)info).put("Rack", ni.getRackName());
            ((HashMap<String, String>)info).put("State", ni.getState().toString());
            ((HashMap<String, NodeId>)info).put("NodeId", ni.getNodeID());
            ((HashMap<String, String>)info).put("NodeHTTPAddress", ni.getHttpAddress());
            ((HashMap<String, Long>)info).put("LastHealthUpdate", ni.getLastHealthReportTime());
            ((HashMap<String, String>)info).put("HealthReport", ni.getHealthReport());
            ((HashMap<String, String>)info).put("NodeManagerVersion", ni.getNodeManagerVersion());
            if (report != null) {
                ((HashMap<String, Integer>)info).put("NumContainers", report.getNumContainers());
                ((HashMap<String, Integer>)info).put("UsedMemoryMB", report.getUsedResource().getMemory());
                ((HashMap<String, Integer>)info).put("AvailableMemoryMB", report.getAvailableResource().getMemory());
            }
            nodesInfo.add(info);
        }
        return JSON.toString(nodesInfo);
    }
    
    static {
        LOG = LogFactory.getLog(RMNMInfo.class);
    }
    
    static class InfoMap extends LinkedHashMap<String, Object>
    {
        private static final long serialVersionUID = 1L;
    }
}
