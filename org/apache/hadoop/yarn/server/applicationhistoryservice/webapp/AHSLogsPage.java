// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import org.apache.hadoop.yarn.webapp.log.AggregatedLogsBlock;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

public class AHSLogsPage extends AHSView
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
        this.commonPreHead(html);
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return AggregatedLogsBlock.class;
    }
}
