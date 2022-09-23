// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class NavBlock extends HtmlBlock
{
    @Override
    protected void render(final Block html) {
        ((Hamlet.DIV)((Hamlet.UL)((Hamlet.UL)((Hamlet.UL)((Hamlet.UL)html.div("#nav").h3("Heading1").ul().li("Item 1").li("Item 2").li("...")._().h3("Tools").ul().li().a("/conf", "Configuration")._()).li().a("/stacks", "Thread dump")._()).li().a("/logs", "Logs")._()).li().a("/metrics", "Metrics")._())._())._();
    }
}
