// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import java.util.List;
import org.apache.hadoop.yarn.util.StringHelper;
import com.google.common.collect.Lists;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class TwoColumnLayout extends HtmlPage
{
    @Override
    protected void render(final Hamlet.HTML<_> html) {
        this.preHead(html);
        html.title(this.$("title")).link(this.root_url("static", "yarn.css")).style("#layout { height: 100%; }", "#layout thead td { height: 3em; }", "#layout #navcell { width: 11em; padding: 0 1em; }", "#layout td.content { padding-top: 0 }", "#layout tbody { vertical-align: top; }", "#layout tfoot td { height: 4em; }")._(JQueryUI.class);
        this.postHead(html);
        JQueryUI.jsnotice(html);
        ((Hamlet.HTML)((Hamlet.TABLE)((Hamlet.TBODY)((Hamlet.TR)((Hamlet.TR)((Hamlet.TABLE)((Hamlet.TFOOT)((Hamlet.TR)html.table("#layout.ui-widget-content").thead().tr().td().$colspan(2)._(this.header())._()._()._().tfoot().tr().td().$colspan(2)._(this.footer())._())._())._()).tbody().tr().td().$id("navcell")._(this.nav())._()).td().$class("content")._(this.content())._())._())._())._())._();
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
    
    protected void setTableStyles(final Hamlet.HTML<_> html, final String tableId, final String... innerStyles) {
        final List<String> styles = (List<String>)Lists.newArrayList();
        styles.add(StringHelper.join('#', tableId, "_paginate span {font-weight:normal}"));
        styles.add(StringHelper.join('#', tableId, " .progress {width:8em}"));
        styles.add(StringHelper.join('#', tableId, "_processing {top:-1.5em; font-size:1em;"));
        styles.add("  color:#000; background:rgba(255, 255, 255, 0.8)}");
        for (final String style : innerStyles) {
            styles.add(StringHelper.join('#', tableId, " ", style));
        }
        html.style(styles.toArray());
    }
}
