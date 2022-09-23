// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import java.util.EnumSet;
import org.apache.hadoop.yarn.webapp.hamlet.HamletImpl;
import org.apache.hadoop.yarn.webapp.SubView;
import java.io.PrintWriter;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.WebAppException;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public abstract class HtmlPage extends TextView
{
    public static final String DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
    private Page page;
    
    private Page page() {
        if (this.page == null) {
            this.page = new Page(this.writer());
        }
        return this.page;
    }
    
    protected HtmlPage() {
        this(null);
    }
    
    protected HtmlPage(final ViewContext ctx) {
        super(ctx, "text/html; charset=UTF-8");
    }
    
    @Override
    public void render() {
        this.puts("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
        this.render(this.page().html().meta_http("X-UA-Compatible", "IE=8").meta_http("Content-type", "text/html; charset=UTF-8"));
        if (this.page().nestLevel() != 0) {
            throw new WebAppException("Error rendering page: nestLevel=" + this.page().nestLevel());
        }
    }
    
    protected abstract void render(final Hamlet.HTML<_> p0);
    
    public static class _ implements HamletSpec._
    {
    }
    
    public class Page extends Hamlet
    {
        Page(final PrintWriter out) {
            super(out, 0, false);
        }
        
        @Override
        protected void subView(final Class<? extends SubView> cls) {
            HtmlPage.this.context().set(this.nestLevel(), this.wasInline());
            HtmlPage.this.render(cls);
            this.setWasInline(HtmlPage.this.context().wasInline());
        }
        
        public HTML<HtmlPage._> html() {
            return new HTML<HtmlPage._>("html", null, EnumSet.of(EOpt.ENDTAG));
        }
    }
}
