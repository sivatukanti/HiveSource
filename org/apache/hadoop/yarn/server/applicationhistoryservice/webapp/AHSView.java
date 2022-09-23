// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import org.apache.hadoop.yarn.server.webapp.AppsBlock;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.view.JQueryUI;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.view.TwoColumnLayout;

public class AHSView extends TwoColumnLayout
{
    static final int MAX_DISPLAY_ROWS = 100;
    static final int MAX_FAST_ROWS = 1000;
    
    @Override
    protected void preHead(final Hamlet.HTML<_> html) {
        this.commonPreHead(html);
        this.set("ui.dataTables.id", "apps");
        this.set(JQueryUI.initID("ui.dataTables", "apps"), this.appsTableInit());
        this.setTableStyles(html, "apps", ".queue {width:6em}", ".ui {width:8em}");
        String reqState = this.$("app.state");
        reqState = ((reqState == null || reqState.isEmpty()) ? "All" : reqState);
        this.setTitle(StringHelper.sjoin(reqState, "Applications"));
    }
    
    protected void commonPreHead(final Hamlet.HTML<_> html) {
        this.set("ui.accordion.id", "nav");
        this.set(JQueryUI.initID("ui.accordion", "nav"), "{autoHeight:false, active:0}");
    }
    
    @Override
    protected Class<? extends SubView> nav() {
        return NavBlock.class;
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return AppsBlock.class;
    }
    
    private String appsTableInit() {
        return JQueryUI.tableInit().append(", 'aaData': appsTableData").append(", bDeferRender: true").append(", bProcessing: true").append("\n, aoColumnDefs: ").append(this.getAppsTableColumnDefs()).append(", aaSorting: [[0, 'desc']]}").toString();
    }
    
    protected String getAppsTableColumnDefs() {
        final StringBuilder sb = new StringBuilder();
        return sb.append("[\n").append("{'sType':'numeric', 'aTargets': [0]").append(", 'mRender': parseHadoopID }").append("\n, {'sType':'numeric', 'aTargets': [5, 6]").append(", 'mRender': renderHadoopDate }").append("\n, {'sType':'numeric', bSearchable:false, 'aTargets': [9]").append(", 'mRender': parseHadoopProgress }]").toString();
    }
}
