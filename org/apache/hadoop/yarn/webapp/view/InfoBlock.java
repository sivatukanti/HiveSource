// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import java.util.Iterator;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.ResponseInfo;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class InfoBlock extends HtmlBlock
{
    final ResponseInfo info;
    
    @Inject
    InfoBlock(final ResponseInfo info) {
        this.info = info;
    }
    
    @Override
    protected void render(final Block html) {
        final Hamlet.TABLE<Hamlet.DIV<Hamlet>> table = html.div(".info-wrap.ui-widget-content.ui-corner-bottom").table(".info").tr().th().$class(JQueryUI.C_TH).$colspan(2)._(this.info.about())._()._();
        int i = 0;
        for (final ResponseInfo.Item item : this.info) {
            final Hamlet.TR<Hamlet.TABLE<Hamlet.DIV<Hamlet>>> tr = table.tr((++i % 2 != 0) ? ".odd" : ".even").th(item.key);
            final String value = String.valueOf(item.value);
            if (item.url == null) {
                if (!item.isRaw) {
                    final Hamlet.TD<Hamlet.TR<Hamlet.TABLE<Hamlet.DIV<Hamlet>>>> td = tr.td();
                    if (value.lastIndexOf(10) > 0) {
                        final String[] arr$;
                        final String[] lines = arr$ = value.split("\n");
                        for (final String line : arr$) {
                            final Hamlet.DIV<Hamlet.TD<Hamlet.TR<Hamlet.TABLE<Hamlet.DIV<Hamlet>>>>> singleLineDiv = td.div();
                            singleLineDiv._(line);
                            singleLineDiv._();
                        }
                    }
                    else {
                        td._(value);
                    }
                    td._();
                }
                else {
                    tr.td()._r(value)._();
                }
            }
            else {
                tr.td().a(this.url(item.url), value)._();
            }
            tr._();
        }
        table._()._();
    }
}
