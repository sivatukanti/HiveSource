// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class TwoColumnCssLayout extends HtmlPage
{
    @Override
    protected void render(final Hamlet.HTML<_> html) {
        this.preHead(html);
        html.title(this.$("title")).link(this.root_url("static", "yarn.css")).style(".main { min-height: 100%; height: auto !important; height: 100%;", "  margin: 0 auto -4em; border: 0; }", ".footer, .push { height: 4em; clear: both; border: 0 }", ".main.ui-widget-content, .footer.ui-widget-content { border: 0; }", ".cmask { position: relative; clear: both; float: left;", "  width: 100%; overflow: hidden; }", ".leftnav .c1right { float: left; width: 200%; position: relative;", "  left: 13em; border: 0; /* background: #fff; */ }", ".leftnav .c1wrap { float: right; width: 50%; position: relative;", "  right: 13em; padding-bottom: 1em; }", ".leftnav .content { margin: 0 1em 0 14em; position: relative;", "  right: 100%; overflow: hidden; }", ".leftnav .nav { float: left; width: 11em; position: relative;", "  right: 12em; overflow: hidden; }")._(JQueryUI.class);
        this.postHead(html);
        JQueryUI.jsnotice(html);
        ((Hamlet.HTML)((Hamlet.HTML)((Hamlet.DIV)((Hamlet.DIV)((Hamlet.DIV)((Hamlet.DIV)html.div(".main.ui-widget-content")._(this.header()).div(".cmask.leftnav").div(".c1right").div(".c1wrap").div(".content")._(this.content())._()._().div(".nav")._(this.nav()).div(".push")._())._())._())._())._()).div(".footer.ui-widget-content")._(this.footer())._())._();
    }
    
    protected void preHead(final Hamlet.HTML<_> html) {
    }
    
    protected void postHead(final Hamlet.HTML<_> html) {
    }
    
    protected Class<? extends SubView> header() {
        return HeaderBlock.class;
    }
    
    protected Class<? extends SubView> content() {
        return LipsumBlock.class;
    }
    
    protected Class<? extends SubView> nav() {
        return NavBlock.class;
    }
    
    protected Class<? extends SubView> footer() {
        return FooterBlock.class;
    }
}
