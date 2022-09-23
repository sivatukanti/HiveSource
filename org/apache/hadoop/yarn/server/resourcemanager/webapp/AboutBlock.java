// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.webapp.view.InfoBlock;
import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterInfo;
import org.apache.hadoop.yarn.webapp.SubView;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class AboutBlock extends HtmlBlock
{
    final ResourceManager rm;
    
    @Inject
    AboutBlock(final ResourceManager rm, final ViewContext ctx) {
        super(ctx);
        this.rm = rm;
    }
    
    @Override
    protected void render(final Block html) {
        html._(MetricsOverviewTable.class);
        final ResourceManager rm = this.getInstance(ResourceManager.class);
        final ClusterInfo cinfo = new ClusterInfo(rm);
        this.info("Cluster overview")._("Cluster ID:", cinfo.getClusterId())._("ResourceManager state:", cinfo.getState())._("ResourceManager HA state:", cinfo.getHAState())._("ResourceManager RMStateStore:", cinfo.getRMStateStore())._("ResourceManager started on:", Times.format(cinfo.getStartedOn()))._("ResourceManager version:", cinfo.getRMBuildVersion() + " on " + cinfo.getRMVersionBuiltOn())._("Hadoop version:", cinfo.getHadoopBuildVersion() + " on " + cinfo.getHadoopVersionBuiltOn());
        html._(InfoBlock.class);
    }
}
