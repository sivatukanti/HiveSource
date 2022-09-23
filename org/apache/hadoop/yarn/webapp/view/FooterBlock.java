// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class FooterBlock extends HtmlBlock
{
    @Override
    protected void render(final Block html) {
        html.div("#footer.ui-widget")._();
    }
}
