// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.example;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.Controller;
import org.apache.hadoop.yarn.webapp.WebApps;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class MyApp
{
    public String anyAPI() {
        return "anything, really!";
    }
    
    public static void main(final String[] args) throws Exception {
        WebApps.$for(new MyApp()).at(8888).inDevMode().start().joinThread();
    }
    
    public static class MyController extends Controller
    {
        final MyApp app;
        
        @Inject
        MyController(final MyApp app, final RequestContext ctx) {
            super(ctx);
            this.app = app;
        }
        
        @Override
        public void index() {
            this.set("anything", "something");
        }
        
        public void anythingYouWant() {
            this.set("anything", this.app.anyAPI());
        }
    }
    
    public static class MyView extends HtmlPage
    {
        public void render(final Hamlet.HTML<_> html) {
            html.title("My App").p("#content_id_for_css_styling")._("You can have", this.$("anything"))._()._();
        }
    }
}
