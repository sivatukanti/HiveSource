// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.webapp;

import org.apache.hadoop.yarn.webapp.View;
import com.google.inject.Inject;
import org.apache.hadoop.yarn.webapp.Controller;

public class AHSController extends Controller
{
    @Inject
    AHSController(final RequestContext ctx) {
        super(ctx);
    }
    
    @Override
    public void index() {
        this.setTitle("Application History");
    }
    
    public void app() {
        this.render(AppPage.class);
    }
    
    public void appattempt() {
        this.render(AppAttemptPage.class);
    }
    
    public void container() {
        this.render(ContainerPage.class);
    }
    
    public void logs() {
        this.render(AHSLogsPage.class);
    }
}
