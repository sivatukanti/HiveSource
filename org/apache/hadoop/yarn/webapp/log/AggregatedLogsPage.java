// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.log;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.JQueryUI;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.webapp.view.TwoColumnLayout;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class AggregatedLogsPage extends TwoColumnLayout
{
    @Override
    protected void preHead(final Hamlet.HTML<_> html) {
        String logEntity = this.$("entity.string");
        if (logEntity == null || logEntity.isEmpty()) {
            logEntity = this.$("container.id");
        }
        if (logEntity == null || logEntity.isEmpty()) {
            logEntity = "UNKNOWN";
        }
        this.set("title", StringHelper.join("Logs for ", logEntity));
        this.set("ui.accordion.id", "nav");
        this.set(JQueryUI.initID("ui.accordion", "nav"), "{autoHeight:false, active:0}");
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return AggregatedLogsBlock.class;
    }
    
    @Override
    protected Class<? extends SubView> nav() {
        return AggregatedLogsNavBlock.class;
    }
}
