// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

class AppsBlockWithMetrics extends HtmlBlock
{
    public void render(final Block html) {
        html._(MetricsOverviewTable.class);
        html._(AppsBlock.class);
    }
}
