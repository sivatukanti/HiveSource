// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp;

import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;

public class NavBlock extends HtmlBlock
{
    public void render(final Block html) {
        final Hamlet.UL<Hamlet.DIV<Hamlet>> mainList = (Hamlet.UL<Hamlet.DIV<Hamlet>>)html.div("#nav").h3("Cluster").ul().li().a(this.url("cluster"), "About")._().li().a(this.url("nodes"), "Nodes")._();
        final Hamlet.UL<Hamlet.LI<Hamlet.UL<Hamlet.DIV<Hamlet>>>> subAppsList = mainList.li().a(this.url("apps"), "Applications").ul();
        subAppsList.li()._();
        for (final YarnApplicationState state : YarnApplicationState.values()) {
            subAppsList.li().a(this.url("apps", state.toString()), state.toString())._();
        }
        subAppsList._()._();
        ((Hamlet.DIV)((Hamlet.UL)((Hamlet.UL)((Hamlet.UL)((Hamlet.UL)mainList.li().a(this.url("scheduler"), "Scheduler")._()._().h3("Tools").ul().li().a("/conf", "Configuration")._()).li().a("/logs", "Local logs")._()).li().a("/stacks", "Server stacks")._()).li().a("/metrics", "Server metrics")._())._())._();
    }
}
