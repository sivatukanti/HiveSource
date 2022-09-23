// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public abstract class TextPage extends TextView
{
    protected TextPage() {
        super(null, "text/plain; charset=UTF-8");
    }
    
    protected TextPage(final ViewContext ctx) {
        super(ctx, "text/plain; charset=UTF-8");
    }
}
