// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import org.apache.hadoop.yarn.server.webapp.AppBlock;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.JQueryUI;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

public class AppPage extends AHSView
{
    @Override
    protected void preHead(final Hamlet.HTML<_> html) {
        this.commonPreHead(html);
        final String appId = this.$("app.id");
        this.set("title", appId.isEmpty() ? "Bad request: missing application ID" : StringHelper.join("Application ", this.$("app.id")));
        this.set("ui.dataTables.id", "attempts");
        this.set(JQueryUI.initID("ui.dataTables", "attempts"), this.attemptsTableInit());
        this.setTableStyles(html, "attempts", ".queue {width:6em}", ".ui {width:8em}");
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return AppBlock.class;
    }
    
    private String attemptsTableInit() {
        return JQueryUI.tableInit().append(", 'aaData': attemptsTableData").append(", bDeferRender: true").append(", bProcessing: true").append("\n, aoColumnDefs: ").append(this.getAttemptsTableColumnDefs()).append(", aaSorting: [[0, 'desc']]}").toString();
    }
    
    protected String getAttemptsTableColumnDefs() {
        final StringBuilder sb = new StringBuilder();
        return sb.append("[\n").append("{'sType':'numeric', 'aTargets': [0]").append(", 'mRender': parseHadoopID }").append("\n, {'sType':'numeric', 'aTargets': [1]").append(", 'mRender': renderHadoopDate }]").toString();
    }
}
