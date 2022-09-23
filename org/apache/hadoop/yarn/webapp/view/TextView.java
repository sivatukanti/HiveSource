// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import java.io.PrintWriter;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.webapp.View;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public abstract class TextView extends View
{
    private final String contentType;
    
    protected TextView(final ViewContext ctx, final String contentType) {
        super(ctx);
        this.contentType = contentType;
    }
    
    @Override
    public PrintWriter writer() {
        this.response().setContentType(this.contentType);
        return super.writer();
    }
    
    public void echo(final Object... args) {
        final PrintWriter out = this.writer();
        for (final Object s : args) {
            out.print(s);
        }
    }
    
    public void puts(final Object... args) {
        this.echo(args);
        this.writer().println();
    }
}
