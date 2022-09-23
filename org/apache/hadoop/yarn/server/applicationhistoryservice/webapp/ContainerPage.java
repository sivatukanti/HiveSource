// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import org.apache.hadoop.yarn.server.webapp.ContainerBlock;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

public class ContainerPage extends AHSView
{
    @Override
    protected void preHead(final Hamlet.HTML<_> html) {
        this.commonPreHead(html);
        final String containerId = this.$("container.id");
        this.set("title", containerId.isEmpty() ? "Bad request: missing container ID" : StringHelper.join("Container ", this.$("container.id")));
    }
    
    @Override
    protected Class<? extends SubView> content() {
        return ContainerBlock.class;
    }
}
