// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.webapp.view.InfoBlock;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.FifoSchedulerInfo;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

class DefaultSchedulerPage extends RmView
{
    static final String _Q = ".ui-state-default.ui-corner-all";
    static final float WIDTH_F = 0.8f;
    static final String Q_END = "left:101%";
    static final String OVER = "font-size:1px;background:rgba(255, 140, 0, 0.8)";
    static final String UNDER = "font-size:1px;background:rgba(50, 205, 50, 0.8)";
    static final float EPSILON = 1.0E-8f;
    
    @Override
    protected void postHead(final Hamlet.HTML<_> html) {
        html.style().$type("text/css")._("#cs { padding: 0.5em 0 1em 0; margin-bottom: 1em; position: relative }", "#cs ul { list-style: none }", "#cs a { font-weight: normal; margin: 2px; position: relative }", "#cs a span { font-weight: normal; font-size: 80% }", "#cs-wrapper .ui-widget-header { padding: 0.2em 0.5em }", "table.info tr th {width: 50%}")._().script("/static/jt/jquery.jstree.js").script().$type("text/javascript")._("$(function() {", "  $('#cs a span').addClass('ui-corner-all').css('position', 'absolute');", "  $('#cs').bind('loaded.jstree', function (e, data) {", "    data.inst.open_all(); }).", "    jstree({", "    core: { animation: 188, html_titles: true },", "    plugins: ['themeroller', 'html_data', 'ui'],", "    themeroller: { item_open: 'ui-icon-minus',", "      item_clsd: 'ui-icon-plus', item_leaf: 'ui-icon-gear'", "    }", "  });", "  $('#cs').bind('select_node.jstree', function(e, data) {", "    var q = $('.q', data.rslt.obj).first().text();", "    if (q == 'root') q = '';", "    $('#apps').dataTable().fnFilter(q, 4);", "  });", "  $('#cs').show();", "});")._();
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
    
    static class QueueInfoBlock extends HtmlBlock
    {
        final FifoSchedulerInfo sinfo;
        
        @Inject
        QueueInfoBlock(final RMContext context, final ViewContext ctx, final ResourceManager rm) {
            super(ctx);
            this.sinfo = new FifoSchedulerInfo(rm);
        }
        
        public void render(final Block html) {
            this.info("'" + this.sinfo.getQueueName() + "' Queue Status")._("Queue State:", this.sinfo.getState())._("Minimum Queue Memory Capacity:", Integer.toString(this.sinfo.getMinQueueMemoryCapacity()))._("Maximum Queue Memory Capacity:", Integer.toString(this.sinfo.getMaxQueueMemoryCapacity()))._("Number of Nodes:", Integer.toString(this.sinfo.getNumNodes()))._("Used Node Capacity:", Integer.toString(this.sinfo.getUsedNodeCapacity()))._("Available Node Capacity:", Integer.toString(this.sinfo.getAvailNodeCapacity()))._("Total Node Capacity:", Integer.toString(this.sinfo.getTotalNodeCapacity()))._("Number of Node Containers:", Integer.toString(this.sinfo.getNumContainers()));
            html._(InfoBlock.class);
        }
    }
    
    static class QueuesBlock extends HtmlBlock
    {
        final FifoSchedulerInfo sinfo;
        final FifoScheduler fs;
        
        @Inject
        QueuesBlock(final ResourceManager rm) {
            this.sinfo = new FifoSchedulerInfo(rm);
            this.fs = (FifoScheduler)rm.getResourceScheduler();
        }
        
        public void render(final Block html) {
            html._(MetricsOverviewTable.class);
            final Hamlet.UL<Hamlet.DIV<Hamlet.DIV<Hamlet>>> ul = html.div("#cs-wrapper.ui-widget").div(".ui-widget-header.ui-corner-top")._("FifoScheduler Queue")._().div("#cs.ui-widget-content.ui-corner-bottom").ul();
            if (this.fs == null) {
                ((Hamlet.LI)ul.li().a(".ui-state-default.ui-corner-all").$style(DefaultSchedulerPage.width(0.8f)).span().$style("left:101%")._("100% ")._().span(".q", "default")._())._();
            }
            else {
                final float used = this.sinfo.getUsedCapacity();
                final float set = this.sinfo.getCapacity();
                final float delta = Math.abs(set - used) + 0.001f;
                ((Hamlet.LI)((Hamlet.A)ul.li().a(".ui-state-default.ui-corner-all").$style(DefaultSchedulerPage.width(0.8f)).$title(StringHelper.join("used:", DefaultSchedulerPage.percent(used))).span().$style("left:101%")._("100%")._().span().$style(StringHelper.join(DefaultSchedulerPage.width(delta), ';', (used > set) ? "font-size:1px;background:rgba(255, 140, 0, 0.8)" : "font-size:1px;background:rgba(50, 205, 50, 0.8)", ';', (used > set) ? DefaultSchedulerPage.left(set) : DefaultSchedulerPage.left(used)))._(".")._()).span(".q", this.sinfo.getQueueName())._())._(QueueInfoBlock.class)._();
            }
            ((Hamlet)((Hamlet.DIV)ul._()._().script().$type("text/javascript")._("$('#cs').hide();")._())._())._(AppsBlock.class);
        }
    }
}
