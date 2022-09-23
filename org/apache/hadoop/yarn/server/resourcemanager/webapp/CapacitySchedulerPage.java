// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CSQueue;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.ResponseInfo;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.yarn.webapp.view.InfoBlock;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.UserInfo;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceInfo;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerLeafQueueInfo;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerQueueInfo;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CapacitySchedulerInfo;
import com.google.inject.servlet.RequestScoped;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

class CapacitySchedulerPage extends RmView
{
    static final String _Q = ".ui-state-default.ui-corner-all";
    static final float Q_MAX_WIDTH = 0.8f;
    static final float Q_STATS_POS = 0.85f;
    static final String Q_END = "left:101%";
    static final String Q_GIVEN = "left:0%;background:none;border:1px dashed rgba(0,0,0,0.25)";
    static final String Q_OVER = "background:rgba(255, 140, 0, 0.8)";
    static final String Q_UNDER = "background:rgba(50, 205, 50, 0.8)";
    
    @Override
    protected void postHead(final Hamlet.HTML<_> html) {
        ((Hamlet.HTML)html.style().$type("text/css")._("#cs { padding: 0.5em 0 1em 0; margin-bottom: 1em; position: relative }", "#cs ul { list-style: none }", "#cs a { font-weight: normal; margin: 2px; position: relative }", "#cs a span { font-weight: normal; font-size: 80% }", "#cs-wrapper .ui-widget-header { padding: 0.2em 0.5em }", ".qstats { font-weight: normal; font-size: 80%; position: absolute }", ".qlegend { font-weight: normal; padding: 0 1em; margin: 1em }", "table.info tr th {width: 50%}")._().script("/static/jt/jquery.jstree.js").script().$type("text/javascript")._("$(function() {", "  $('#cs a span').addClass('ui-corner-all').css('position', 'absolute');", "  $('#cs').bind('loaded.jstree', function (e, data) {", "    var callback = { call:reopenQueryNodes }", "    data.inst.open_node('#pq', callback);", "   }).", "    jstree({", "    core: { animation: 188, html_titles: true },", "    plugins: ['themeroller', 'html_data', 'ui'],", "    themeroller: { item_open: 'ui-icon-minus',", "      item_clsd: 'ui-icon-plus', item_leaf: 'ui-icon-gear'", "    }", "  });", "  $('#cs').bind('select_node.jstree', function(e, data) {", "    var q = $('.q', data.rslt.obj).first().text();", "    if (q == 'root') q = '';", "    else q = '^' + q.substr(q.lastIndexOf('.') + 1) + '$';", "    $('#apps').dataTable().fnFilter(q, 4, true);", "  });", "  $('#cs').show();", "});")._())._(SchedulerPageUtil.QueueBlockUtil.class);
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return QueuesBlock.class;
    }
    
    static String percent(final float f) {
        return String.format("%.1f%%", f * 100.0f);
    }
    
    static String width(final float f) {
        return String.format("width:%.1f%%", f * 100.0f);
    }
    
    static String left(final float f) {
        return String.format("left:%.1f%%", f * 100.0f);
    }
    
    @RequestScoped
    static class CSQInfo
    {
        CapacitySchedulerInfo csinfo;
        CapacitySchedulerQueueInfo qinfo;
    }
    
    static class LeafQueueInfoBlock extends HtmlBlock
    {
        final CapacitySchedulerLeafQueueInfo lqinfo;
        
        @Inject
        LeafQueueInfoBlock(final ViewContext ctx, final CSQInfo info) {
            super(ctx);
            this.lqinfo = (CapacitySchedulerLeafQueueInfo)info.qinfo;
        }
        
        private String getPercentage(final ResourceInfo numerator, final ResourceInfo denominator) {
            final StringBuilder percentString = new StringBuilder("Memory: ");
            if (numerator != null) {
                percentString.append(numerator.getMemory());
            }
            if (denominator.getMemory() != 0) {
                percentString.append(" (<span title='of used resources in this queue'>").append(StringUtils.format("%.2f", numerator.getMemory() * 100.0 / denominator.getMemory()) + "%</span>)");
            }
            percentString.append(", vCores: ");
            if (numerator != null) {
                percentString.append(numerator.getvCores());
            }
            if (denominator.getvCores() != 0) {
                percentString.append(" (<span title='of used resources in this queue'>").append(StringUtils.format("%.2f", numerator.getvCores() * 100.0 / denominator.getvCores()) + "%</span>)");
            }
            return percentString.toString();
        }
        
        @Override
        protected void render(final Block html) {
            final StringBuilder activeUserList = new StringBuilder("");
            final ResourceInfo usedResources = this.lqinfo.getResourcesUsed();
            final ArrayList<UserInfo> users = this.lqinfo.getUsers().getUsersList();
            for (final UserInfo entry : users) {
                activeUserList.append(entry.getUsername()).append(" &lt;").append(this.getPercentage(entry.getResourcesUsed(), usedResources)).append(", Schedulable Apps: " + entry.getNumActiveApplications()).append(", Non-Schedulable Apps: " + entry.getNumPendingApplications()).append("&gt;<br style='display:block'>");
            }
            final ResponseInfo ri = this.info("'" + this.lqinfo.getQueuePath().substring(5) + "' Queue Status")._("Queue State:", this.lqinfo.getQueueState())._("Used Capacity:", CapacitySchedulerPage.percent(this.lqinfo.getUsedCapacity() / 100.0f))._("Absolute Used Capacity:", CapacitySchedulerPage.percent(this.lqinfo.getAbsoluteUsedCapacity() / 100.0f))._("Absolute Capacity:", CapacitySchedulerPage.percent(this.lqinfo.getAbsoluteCapacity() / 100.0f))._("Absolute Max Capacity:", CapacitySchedulerPage.percent(this.lqinfo.getAbsoluteMaxCapacity() / 100.0f))._("Used Resources:", this.lqinfo.getResourcesUsed().toString())._("Num Schedulable Applications:", Integer.toString(this.lqinfo.getNumActiveApplications()))._("Num Non-Schedulable Applications:", Integer.toString(this.lqinfo.getNumPendingApplications()))._("Num Containers:", Integer.toString(this.lqinfo.getNumContainers()))._("Max Applications:", Integer.toString(this.lqinfo.getMaxApplications()))._("Max Applications Per User:", Integer.toString(this.lqinfo.getMaxApplicationsPerUser()))._("Max Schedulable Applications:", Integer.toString(this.lqinfo.getMaxActiveApplications()))._("Max Schedulable Applications Per User:", Integer.toString(this.lqinfo.getMaxActiveApplicationsPerUser()))._("Configured Capacity:", CapacitySchedulerPage.percent(this.lqinfo.getCapacity() / 100.0f))._("Configured Max Capacity:", CapacitySchedulerPage.percent(this.lqinfo.getMaxCapacity() / 100.0f))._("Configured Minimum User Limit Percent:", Integer.toString(this.lqinfo.getUserLimit()) + "%")._("Configured User Limit Factor:", String.format("%.1f", this.lqinfo.getUserLimitFactor()))._("Active Users: ", activeUserList.toString())._r("Accessible Node Labels:", StringUtils.join(",", this.lqinfo.getNodeLabels()));
            html._(InfoBlock.class);
            ri.clear();
        }
    }
    
    public static class QueueBlock extends HtmlBlock
    {
        final CSQInfo csqinfo;
        
        @Inject
        QueueBlock(final CSQInfo info) {
            this.csqinfo = info;
        }
        
        public void render(final Block html) {
            final ArrayList<CapacitySchedulerQueueInfo> subQueues = (this.csqinfo.qinfo == null) ? this.csqinfo.csinfo.getQueues().getQueueInfoList() : this.csqinfo.qinfo.getQueues().getQueueInfoList();
            final Hamlet.UL<Hamlet> ul = html.ul("#pq");
            for (final CapacitySchedulerQueueInfo info : subQueues) {
                final float used = info.getUsedCapacity() / 100.0f;
                final float absCap = info.getAbsoluteCapacity() / 100.0f;
                final float absMaxCap = info.getAbsoluteMaxCapacity() / 100.0f;
                final float absUsedCap = info.getAbsoluteUsedCapacity() / 100.0f;
                final Hamlet.LI<Hamlet.UL<Hamlet>> li = (Hamlet.LI<Hamlet.UL<Hamlet>>)((Hamlet.LI)((Hamlet.A)ul.li().a(".ui-state-default.ui-corner-all").$style(CapacitySchedulerPage.width(absMaxCap * 0.8f)).$title(StringHelper.join("Absolute Capacity:", CapacitySchedulerPage.percent(absCap))).span().$style(StringHelper.join("left:0%;background:none;border:1px dashed rgba(0,0,0,0.25)", ";font-size:1px;", CapacitySchedulerPage.width(absCap / absMaxCap)))._('.')._().span().$style(StringHelper.join(CapacitySchedulerPage.width(absUsedCap / absMaxCap), ";font-size:1px;left:0%;", (absUsedCap > absCap) ? "background:rgba(255, 140, 0, 0.8)" : "background:rgba(50, 205, 50, 0.8)"))._('.')._()).span(".q", info.getQueuePath().substring(5))._()).span().$class("qstats").$style(CapacitySchedulerPage.left(0.85f))._(StringHelper.join(CapacitySchedulerPage.percent(used), " used"))._();
                this.csqinfo.qinfo = info;
                if (info.getQueues() == null) {
                    li.ul("#lq").li()._(LeafQueueInfoBlock.class)._()._();
                }
                else {
                    li._(QueueBlock.class);
                }
                li._();
            }
            ul._();
        }
    }
    
    static class QueuesBlock extends HtmlBlock
    {
        final CapacityScheduler cs;
        final CSQInfo csqinfo;
        
        @Inject
        QueuesBlock(final ResourceManager rm, final CSQInfo info) {
            this.cs = (CapacityScheduler)rm.getResourceScheduler();
            this.csqinfo = info;
        }
        
        public void render(final Block html) {
            html._(MetricsOverviewTable.class);
            final Hamlet.UL<Hamlet.DIV<Hamlet.DIV<Hamlet>>> ul = html.div("#cs-wrapper.ui-widget").div(".ui-widget-header.ui-corner-top")._("Application Queues")._().div("#cs.ui-widget-content.ui-corner-bottom").ul();
            if (this.cs == null) {
                ((Hamlet.LI)ul.li().a(".ui-state-default.ui-corner-all").$style(CapacitySchedulerPage.width(0.8f)).span().$style("left:101%")._("100% ")._().span(".q", "default")._())._();
            }
            else {
                final CSQueue root = this.cs.getRootQueue();
                final CapacitySchedulerInfo sinfo = new CapacitySchedulerInfo(root);
                this.csqinfo.csinfo = sinfo;
                this.csqinfo.qinfo = null;
                final float used = sinfo.getUsedCapacity() / 100.0f;
                ((Hamlet.LI)((Hamlet.LI)((Hamlet.A)((Hamlet.UL)((Hamlet.LI)((Hamlet.LI)((Hamlet.LI)((Hamlet.LI)ul.li().$style("margin-bottom: 1em").span().$style("font-weight: bold")._("Legend:")._().span().$class("qlegend ui-corner-all").$style("left:0%;background:none;border:1px dashed rgba(0,0,0,0.25)")._("Capacity")._()).span().$class("qlegend ui-corner-all").$style("background:rgba(50, 205, 50, 0.8)")._("Used")._()).span().$class("qlegend ui-corner-all").$style("background:rgba(255, 140, 0, 0.8)")._("Used (over capacity)")._()).span().$class("qlegend ui-corner-all ui-state-default")._("Max Capacity")._())._()).li().a(".ui-state-default.ui-corner-all").$style(CapacitySchedulerPage.width(0.8f)).span().$style(StringHelper.join(CapacitySchedulerPage.width(used), ";left:0%;", (used > 1.0f) ? "background:rgba(255, 140, 0, 0.8)" : "background:rgba(50, 205, 50, 0.8)"))._(".")._()).span(".q", "root")._()).span().$class("qstats").$style(CapacitySchedulerPage.left(0.85f))._(StringHelper.join(CapacitySchedulerPage.percent(used), " used"))._())._(QueueBlock.class)._();
            }
            ((Hamlet)((Hamlet.DIV)ul._()._().script().$type("text/javascript")._("$('#cs').hide();")._())._())._(AppsBlock.class);
        }
    }
}
