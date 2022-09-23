// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FairSchedulerInfo;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.ResponseInfo;
import org.apache.hadoop.yarn.webapp.view.InfoBlock;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FairSchedulerLeafQueueInfo;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FairSchedulerQueueInfo;
import com.google.inject.servlet.RequestScoped;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

public class FairSchedulerPage extends RmView
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
        ((Hamlet.HTML)html.style().$type("text/css")._("#cs { padding: 0.5em 0 1em 0; margin-bottom: 1em; position: relative }", "#cs ul { list-style: none }", "#cs a { font-weight: normal; margin: 2px; position: relative }", "#cs a span { font-weight: normal; font-size: 80% }", "#cs-wrapper .ui-widget-header { padding: 0.2em 0.5em }", ".qstats { font-weight: normal; font-size: 80%; position: absolute }", ".qlegend { font-weight: normal; padding: 0 1em; margin: 1em }", "table.info tr th {width: 50%}")._().script("/static/jt/jquery.jstree.js").script().$type("text/javascript")._("$(function() {", "  $('#cs a span').addClass('ui-corner-all').css('position', 'absolute');", "  $('#cs').bind('loaded.jstree', function (e, data) {", "    var callback = { call:reopenQueryNodes }", "    data.inst.open_node('#pq', callback);", "   }).", "    jstree({", "    core: { animation: 188, html_titles: true },", "    plugins: ['themeroller', 'html_data', 'ui'],", "    themeroller: { item_open: 'ui-icon-minus',", "      item_clsd: 'ui-icon-plus', item_leaf: 'ui-icon-gear'", "    }", "  });", "  $('#cs').bind('select_node.jstree', function(e, data) {", "    var queues = $('.q', data.rslt.obj);", "    var q = '^' + queues.first().text();", "    q += queues.length == 1 ? '$' : '\\\\.';", "    $('#apps').dataTable().fnFilter(q, 4, true);", "  });", "  $('#cs').show();", "});")._())._(SchedulerPageUtil.QueueBlockUtil.class);
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
    
    @Override
    protected String getAppsTableColumnDefs() {
        final StringBuilder sb = new StringBuilder();
        return sb.append("[\n").append("{'sType':'numeric', 'aTargets': [0]").append(", 'mRender': parseHadoopID }").append("\n, {'sType':'numeric', 'aTargets': [6, 7]").append(", 'mRender': renderHadoopDate }").append("\n, {'sType':'numeric', bSearchable:false, 'aTargets': [9]").append(", 'mRender': parseHadoopProgress }]").toString();
    }
    
    @RequestScoped
    static class FSQInfo
    {
        FairSchedulerQueueInfo qinfo;
    }
    
    static class LeafQueueBlock extends HtmlBlock
    {
        final FairSchedulerLeafQueueInfo qinfo;
        
        @Inject
        LeafQueueBlock(final ViewContext ctx, final FSQInfo info) {
            super(ctx);
            this.qinfo = (FairSchedulerLeafQueueInfo)info.qinfo;
        }
        
        @Override
        protected void render(final Block html) {
            final ResponseInfo ri = this.info("'" + this.qinfo.getQueueName() + "' Queue Status")._("Used Resources:", this.qinfo.getUsedResources().toString())._("Num Active Applications:", this.qinfo.getNumActiveApplications())._("Num Pending Applications:", this.qinfo.getNumPendingApplications())._("Min Resources:", this.qinfo.getMinResources().toString())._("Max Resources:", this.qinfo.getMaxResources().toString());
            final int maxApps = this.qinfo.getMaxApplications();
            if (maxApps < Integer.MAX_VALUE) {
                ri._("Max Running Applications:", this.qinfo.getMaxApplications());
            }
            ri._("Fair Share:", this.qinfo.getFairShare().toString());
            html._(InfoBlock.class);
            ri.clear();
        }
    }
    
    static class QueueBlock extends HtmlBlock
    {
        final FSQInfo fsqinfo;
        
        @Inject
        QueueBlock(final FSQInfo info) {
            this.fsqinfo = info;
        }
        
        public void render(final Block html) {
            final Collection<FairSchedulerQueueInfo> subQueues = this.fsqinfo.qinfo.getChildQueues();
            final Hamlet.UL<Hamlet> ul = html.ul("#pq");
            for (final FairSchedulerQueueInfo info : subQueues) {
                final float capacity = info.getMaxResourcesFraction();
                final float fairShare = info.getFairShareMemoryFraction();
                final float used = info.getUsedMemoryFraction();
                final Hamlet.LI<Hamlet.UL<Hamlet>> li = (Hamlet.LI<Hamlet.UL<Hamlet>>)((Hamlet.LI)((Hamlet.A)ul.li().a(".ui-state-default.ui-corner-all").$style(FairSchedulerPage.width(capacity * 0.8f)).$title(StringHelper.join("Fair Share:", FairSchedulerPage.percent(fairShare))).span().$style(StringHelper.join("left:0%;background:none;border:1px dashed rgba(0,0,0,0.25)", ";font-size:1px;", FairSchedulerPage.width(fairShare / capacity)))._('.')._().span().$style(StringHelper.join(FairSchedulerPage.width(used / capacity), ";font-size:1px;left:0%;", (used > fairShare) ? "background:rgba(255, 140, 0, 0.8)" : "background:rgba(50, 205, 50, 0.8)"))._('.')._()).span(".q", info.getQueueName())._()).span().$class("qstats").$style(FairSchedulerPage.left(0.85f))._(StringHelper.join(FairSchedulerPage.percent(used), " used"))._();
                this.fsqinfo.qinfo = info;
                if (info instanceof FairSchedulerLeafQueueInfo) {
                    li.ul("#lq").li()._(LeafQueueBlock.class)._()._();
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
        final FairScheduler fs;
        final FSQInfo fsqinfo;
        
        @Inject
        QueuesBlock(final ResourceManager rm, final FSQInfo info) {
            this.fs = (FairScheduler)rm.getResourceScheduler();
            this.fsqinfo = info;
        }
        
        public void render(final Block html) {
            html._(MetricsOverviewTable.class);
            final Hamlet.UL<Hamlet.DIV<Hamlet.DIV<Hamlet>>> ul = html.div("#cs-wrapper.ui-widget").div(".ui-widget-header.ui-corner-top")._("Application Queues")._().div("#cs.ui-widget-content.ui-corner-bottom").ul();
            if (this.fs == null) {
                ((Hamlet.LI)ul.li().a(".ui-state-default.ui-corner-all").$style(FairSchedulerPage.width(0.8f)).span().$style("left:101%")._("100% ")._().span(".q", "default")._())._();
            }
            else {
                final FairSchedulerInfo sinfo = new FairSchedulerInfo(this.fs);
                this.fsqinfo.qinfo = sinfo.getRootQueueInfo();
                final float used = this.fsqinfo.qinfo.getUsedMemoryFraction();
                ((Hamlet.LI)((Hamlet.LI)((Hamlet.A)((Hamlet.UL)((Hamlet.LI)((Hamlet.LI)((Hamlet.LI)((Hamlet.LI)ul.li().$style("margin-bottom: 1em").span().$style("font-weight: bold")._("Legend:")._().span().$class("qlegend ui-corner-all").$style("left:0%;background:none;border:1px dashed rgba(0,0,0,0.25)")._("Fair Share")._()).span().$class("qlegend ui-corner-all").$style("background:rgba(50, 205, 50, 0.8)")._("Used")._()).span().$class("qlegend ui-corner-all").$style("background:rgba(255, 140, 0, 0.8)")._("Used (over fair share)")._()).span().$class("qlegend ui-corner-all ui-state-default")._("Max Capacity")._())._()).li().a(".ui-state-default.ui-corner-all").$style(FairSchedulerPage.width(0.8f)).span().$style(StringHelper.join(FairSchedulerPage.width(used), ";left:0%;", (used > 1.0f) ? "background:rgba(255, 140, 0, 0.8)" : "background:rgba(50, 205, 50, 0.8)"))._(".")._()).span(".q", "root")._()).span().$class("qstats").$style(FairSchedulerPage.left(0.85f))._(StringHelper.join(FairSchedulerPage.percent(used), " used"))._())._(QueueBlock.class)._();
            }
            ((Hamlet)((Hamlet.DIV)ul._()._().script().$type("text/javascript")._("$('#cs').hide();")._())._())._(FairSchedulerAppsBlock.class);
        }
    }
}
