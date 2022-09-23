// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.UserMetricsInfo;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterMetricsInfo;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class MetricsOverviewTable extends HtmlBlock
{
    private static final long BYTES_IN_MB = 1048576L;
    private final RMContext rmContext;
    private final ResourceManager rm;
    
    @Inject
    MetricsOverviewTable(final RMContext context, final ResourceManager rm, final ViewContext ctx) {
        super(ctx);
        this.rmContext = context;
        this.rm = rm;
    }
    
    @Override
    protected void render(final Block html) {
        html.style(".metrics {margin-bottom:5px}");
        final ClusterMetricsInfo clusterMetrics = new ClusterMetricsInfo(this.rm, this.rmContext);
        final Hamlet.DIV<Hamlet> div = html.div().$class("metrics");
        ((Hamlet.TABLE)((Hamlet.TBODY)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TABLE)((Hamlet.THEAD)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)div.h3("Cluster Metrics").table("#metricsoverview").thead().$class("ui-widget-header").tr().th().$class("ui-state-default")._("Apps Submitted")._().th().$class("ui-state-default")._("Apps Pending")._()).th().$class("ui-state-default")._("Apps Running")._()).th().$class("ui-state-default")._("Apps Completed")._()).th().$class("ui-state-default")._("Containers Running")._()).th().$class("ui-state-default")._("Memory Used")._()).th().$class("ui-state-default")._("Memory Total")._()).th().$class("ui-state-default")._("Memory Reserved")._()).th().$class("ui-state-default")._("VCores Used")._()).th().$class("ui-state-default")._("VCores Total")._()).th().$class("ui-state-default")._("VCores Reserved")._()).th().$class("ui-state-default")._("Active Nodes")._()).th().$class("ui-state-default")._("Decommissioned Nodes")._()).th().$class("ui-state-default")._("Lost Nodes")._()).th().$class("ui-state-default")._("Unhealthy Nodes")._()).th().$class("ui-state-default")._("Rebooted Nodes")._())._())._()).tbody().$class("ui-widget-content").tr().td(String.valueOf(clusterMetrics.getAppsSubmitted())).td(String.valueOf(clusterMetrics.getAppsPending())).td(String.valueOf(clusterMetrics.getAppsRunning())).td(String.valueOf(clusterMetrics.getAppsCompleted() + clusterMetrics.getAppsFailed() + clusterMetrics.getAppsKilled())).td(String.valueOf(clusterMetrics.getContainersAllocated())).td(StringUtils.byteDesc(clusterMetrics.getAllocatedMB() * 1048576L)).td(StringUtils.byteDesc(clusterMetrics.getTotalMB() * 1048576L)).td(StringUtils.byteDesc(clusterMetrics.getReservedMB() * 1048576L)).td(String.valueOf(clusterMetrics.getAllocatedVirtualCores())).td(String.valueOf(clusterMetrics.getTotalVirtualCores())).td(String.valueOf(clusterMetrics.getReservedVirtualCores())).td().a(this.url("nodes"), String.valueOf(clusterMetrics.getActiveNodes()))._()).td().a(this.url("nodes/decommissioned"), String.valueOf(clusterMetrics.getDecommissionedNodes()))._()).td().a(this.url("nodes/lost"), String.valueOf(clusterMetrics.getLostNodes()))._()).td().a(this.url("nodes/unhealthy"), String.valueOf(clusterMetrics.getUnhealthyNodes()))._()).td().a(this.url("nodes/rebooted"), String.valueOf(clusterMetrics.getRebootedNodes()))._())._())._())._();
        final String user = this.request().getRemoteUser();
        if (user != null) {
            final UserMetricsInfo userMetrics = new UserMetricsInfo(this.rm, this.rmContext, user);
            if (userMetrics.metricsAvailable()) {
                ((Hamlet.TABLE)((Hamlet.TBODY)((Hamlet.TABLE)((Hamlet.THEAD)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)((Hamlet.TR)div.h3("User Metrics for " + user).table("#usermetricsoverview").thead().$class("ui-widget-header").tr().th().$class("ui-state-default")._("Apps Submitted")._().th().$class("ui-state-default")._("Apps Pending")._()).th().$class("ui-state-default")._("Apps Running")._()).th().$class("ui-state-default")._("Apps Completed")._()).th().$class("ui-state-default")._("Containers Running")._()).th().$class("ui-state-default")._("Containers Pending")._()).th().$class("ui-state-default")._("Containers Reserved")._()).th().$class("ui-state-default")._("Memory Used")._()).th().$class("ui-state-default")._("Memory Pending")._()).th().$class("ui-state-default")._("Memory Reserved")._()).th().$class("ui-state-default")._("VCores Used")._()).th().$class("ui-state-default")._("VCores Pending")._()).th().$class("ui-state-default")._("VCores Reserved")._())._())._()).tbody().$class("ui-widget-content").tr().td(String.valueOf(userMetrics.getAppsSubmitted())).td(String.valueOf(userMetrics.getAppsPending())).td(String.valueOf(userMetrics.getAppsRunning())).td(String.valueOf(userMetrics.getAppsCompleted() + userMetrics.getAppsFailed() + userMetrics.getAppsKilled())).td(String.valueOf(userMetrics.getRunningContainers())).td(String.valueOf(userMetrics.getPendingContainers())).td(String.valueOf(userMetrics.getReservedContainers())).td(StringUtils.byteDesc(userMetrics.getAllocatedMB() * 1048576L)).td(StringUtils.byteDesc(userMetrics.getPendingMB() * 1048576L)).td(StringUtils.byteDesc(userMetrics.getReservedMB() * 1048576L)).td(String.valueOf(userMetrics.getAllocatedVirtualCores())).td(String.valueOf(userMetrics.getPendingVirtualCores())).td(String.valueOf(userMetrics.getReservedVirtualCores()))._())._())._();
            }
        }
        div._();
    }
}
