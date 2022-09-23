// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class HeaderBlock extends HtmlBlock
{
    @Override
    protected void render(final Block html) {
        String loggedIn = "";
        if (this.request().getRemoteUser() != null) {
            loggedIn = "Logged in as: " + this.request().getRemoteUser();
        }
        ((Hamlet.DIV)html.div("#header.ui-widget").div("#user")._(loggedIn)._().div("#logo").img("/static/hadoop-st.png")._()).h1(this.$("title"))._();
    }
}
