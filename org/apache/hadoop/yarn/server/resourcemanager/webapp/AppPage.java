// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

public class AppPage extends RmView
{
    @Override
    protected void preHead(final Hamlet.HTML<_> html) {
        this.commonPreHead(html);
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return AppBlock.class;
    }
}
