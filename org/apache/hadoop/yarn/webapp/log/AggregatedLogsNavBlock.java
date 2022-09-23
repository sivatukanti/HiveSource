// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.log;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class AggregatedLogsNavBlock extends HtmlBlock
{
    @Override
    protected void render(final Block html) {
        html.div("#nav").h3()._("Logs")._()._();
    }
}
