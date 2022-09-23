// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.NodeInfo;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.api.records.NodeState;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.JQueryUI;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

class NodesPage extends RmView
{
    @Override
    protected void preHead(final Hamlet.HTML<_> html) {
        this.commonPreHead(html);
        final String type = this.$("node.state");
        String title = "Nodes of the cluster";
        if (type != null && !type.isEmpty()) {
            title = title + " (" + type + ")";
        }
        this.setTitle(title);
        this.set("ui.dataTables.id", "nodes");
        this.set(JQueryUI.initID("ui.dataTables", "nodes"), this.nodesTableInit());
        this.setTableStyles(html, "nodes", ".healthStatus {width:10em}", ".healthReport {width:10em}");
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return NodesBlock.class;
    }
    
    private String nodesTableInit() {
        final StringBuilder b = JQueryUI.tableInit().append(", aoColumnDefs: [");
        b.append("{'bSearchable': false, 'aTargets': [ 6 ]}");
        b.append(", {'sType': 'title-numeric', 'bSearchable': false, 'aTargets': [ 7, 8 ] }");
        b.append(", {'sType': 'title-numeric', 'aTargets': [ 4 ]}");
        b.append("]}");
        return b.toString();
    }
    
    static class NodesBlock extends HtmlBlock
    {
        final RMContext rmContext;
        final ResourceManager rm;
        private static final long BYTES_IN_MB = 1048576L;
        
        @Inject
        NodesBlock(final RMContext context, final ResourceManager rm, final ViewContext ctx) {
            super(ctx);
            this.rmContext = context;
            this.rm = rm;
        }
        
        @Override
        protected void render(final Block html) {
            html._(MetricsOverviewTable.class);
            final ResourceScheduler sched = this.rm.getResourceScheduler();
            final String type = this.$("node.state");
            final Hamlet.TBODY<Hamlet.TABLE<Hamlet>> tbody = html.table("#nodes").thead().tr().th(".nodelabels", "Node Labels").th(".rack", "Rack").th(".state", "Node State").th(".nodeaddress", "Node Address").th(".nodehttpaddress", "Node HTTP Address").th(".lastHealthUpdate", "Last health-update").th(".healthReport", "Health-report").th(".containers", "Containers").th(".mem", "Mem Used").th(".mem", "Mem Avail").th(".vcores", "VCores Used").th(".vcores", "VCores Avail").th(".nodeManagerVersion", "Version")._()._().tbody();
            NodeState stateFilter = null;
            if (type != null && !type.isEmpty()) {
                stateFilter = NodeState.valueOf(type.toUpperCase());
            }
            Collection<RMNode> rmNodes = this.rmContext.getRMNodes().values();
            boolean isInactive = false;
            if (stateFilter != null) {
                switch (stateFilter) {
                    case DECOMMISSIONED:
                    case LOST:
                    case REBOOTED: {
                        rmNodes = this.rmContext.getInactiveRMNodes().values();
                        isInactive = true;
                        break;
                    }
                }
            }
            for (final RMNode ni : rmNodes) {
                if (stateFilter != null) {
                    final NodeState state = ni.getState();
                    if (!stateFilter.equals(state)) {
                        continue;
                    }
                }
                else if (ni.getState() == NodeState.UNHEALTHY) {
                    continue;
                }
                final NodeInfo info = new NodeInfo(ni, sched);
                final int usedMemory = (int)info.getUsedMemory();
                final int availableMemory = (int)info.getAvailableMemory();
                final Hamlet.TR<Hamlet.TBODY<Hamlet.TABLE<Hamlet>>> row = tbody.tr().td(StringUtils.join(",", info.getNodeLabels())).td(info.getRack()).td(info.getState()).td(info.getNodeId());
                if (isInactive) {
                    row.td()._("N/A")._();
                }
                else {
                    final String httpAddress = info.getNodeHTTPAddress();
                    row.td().a("//" + httpAddress, httpAddress)._();
                }
                ((Hamlet.TR)((Hamlet.TD)((Hamlet.TR)((Hamlet.TD)((Hamlet.TR)row.td().br().$title(String.valueOf(info.getLastHealthUpdate()))._()._(Times.format(info.getLastHealthUpdate()))._()).td(info.getHealthReport()).td(String.valueOf(info.getNumContainers())).td().br().$title(String.valueOf(usedMemory))._())._(StringUtils.byteDesc(usedMemory * 1048576L))._()).td().br().$title(String.valueOf(availableMemory))._())._(StringUtils.byteDesc(availableMemory * 1048576L))._()).td(String.valueOf(info.getUsedVirtualCores())).td(String.valueOf(info.getAvailableVirtualCores())).td(ni.getNodeManagerVersion())._();
            }
            tbody._()._();
        }
    }
}
