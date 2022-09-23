// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class NavBlock extends HtmlBlock
{
    public void render(final Block html) {
        ((Hamlet.DIV)((Hamlet.UL)((Hamlet.LI)((Hamlet.UL)((Hamlet.UL)html.div("#nav").h3("Application History").ul().li().a(this.url("apps"), "Applications").ul().li().a(this.url("apps", YarnApplicationState.FINISHED.toString()), YarnApplicationState.FINISHED.toString())._().li().a(this.url("apps", YarnApplicationState.FAILED.toString()), YarnApplicationState.FAILED.toString())._()).li().a(this.url("apps", YarnApplicationState.KILLED.toString()), YarnApplicationState.KILLED.toString())._())._())._())._())._();
    }
}
