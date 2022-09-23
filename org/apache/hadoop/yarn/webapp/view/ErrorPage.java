// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.CharArrayWriter;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class ErrorPage extends HtmlPage
{
    @Override
    protected void render(final Hamlet.HTML<_> html) {
        this.set("ui.accordion.id", "msg");
        final String title = "Sorry, got error " + this.status();
        ((Hamlet.HTML)((Hamlet.DIV)html.title(title).link(this.root_url("static", "yarn.css"))._(JQueryUI.class).style("#msg { margin: 1em auto; width: 88%; }", "#msg h1 { padding: 0.2em 1.5em; font: bold 1.3em serif; }").div("#msg").h1(title).div()._("Please consult").a("http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html", "RFC 2616")._(" for meanings of the error code.")._().h1("Error Details").pre()._(this.errorDetails())._())._())._();
    }
    
    protected String errorDetails() {
        if (!this.$("error.details").isEmpty()) {
            return this.$("error.details");
        }
        if (this.error() != null) {
            return toStackTrace(this.error(), 65536);
        }
        return "No exception was thrown.";
    }
    
    public static String toStackTrace(final Throwable error, final int cutoff) {
        final CharArrayWriter buffer = new CharArrayWriter(8192);
        error.printStackTrace(new PrintWriter(buffer));
        return (buffer.size() < cutoff) ? buffer.toString() : buffer.toString().substring(0, cutoff);
    }
}
