// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import org.apache.hadoop.yarn.server.webapp.AppAttemptBlock;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.JQueryUI;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

public class AppAttemptPage extends AHSView
{
    @Override
    protected void preHead(final Hamlet.HTML<_> html) {
        this.commonPreHead(html);
        final String appAttemptId = this.$("appattempt.id");
        this.set("title", appAttemptId.isEmpty() ? "Bad request: missing application attempt ID" : StringHelper.join("Application Attempt ", this.$("appattempt.id")));
        this.set("ui.dataTables.id", "containers");
        this.set(JQueryUI.initID("ui.dataTables", "containers"), this.containersTableInit());
        this.setTableStyles(html, "containers", ".queue {width:6em}", ".ui {width:8em}");
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return AppAttemptBlock.class;
    }
    
    private String containersTableInit() {
        return JQueryUI.tableInit().append(", 'aaData': containersTableData").append(", bDeferRender: true").append(", bProcessing: true").append("\n, aoColumnDefs: ").append(this.getContainersTableColumnDefs()).append(", aaSorting: [[0, 'desc']]}").toString();
    }
    
    protected String getContainersTableColumnDefs() {
        final StringBuilder sb = new StringBuilder();
        return sb.append("[\n").append("{'sType':'numeric', 'aTargets': [0]").append(", 'mRender': parseHadoopID }]").toString();
    }
}
