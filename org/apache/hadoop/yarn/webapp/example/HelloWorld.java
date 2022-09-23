// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.example;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.yarn.webapp.view.HtmlPage;
import org.apache.hadoop.yarn.webapp.Controller;
import org.apache.hadoop.yarn.webapp.WebApps;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class HelloWorld
{
    public static void main(final String[] args) {
        WebApps.$for(new HelloWorld()).at(8888).inDevMode().start().joinThread();
    }
    
    public static class Hello extends Controller
    {
        @Override
        public void index() {
            this.renderText("Hello world!");
        }
        
        public void html() {
            this.setTitle("Hello world!");
        }
        
        public void json() {
            this.renderJSON("Hello world!");
        }
    }
    
    public static class HelloView extends HtmlPage
    {
        @Override
        protected void render(final Hamlet.HTML<_> html) {
            html.title(this.$("title")).p("#hello-for-css")._(this.$("title"))._()._();
        }
    }
}
