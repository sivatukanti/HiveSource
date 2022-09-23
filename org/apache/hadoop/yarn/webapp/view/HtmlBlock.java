// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import java.io.PrintWriter;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.webapp.WebAppException;
import org.apache.hadoop.yarn.webapp.View;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.webapp.SubView;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public abstract class HtmlBlock extends TextView implements SubView
{
    private Block block;
    
    private Block block() {
        if (this.block == null) {
            this.block = new Block(this.writer(), this.context().nestLevel(), this.context().wasInline());
        }
        return this.block;
    }
    
    protected HtmlBlock() {
        this(null);
    }
    
    protected HtmlBlock(final ViewContext ctx) {
        super(ctx, "text/html; charset=UTF-8");
    }
    
    @Override
    public void render() {
        final int nestLevel = this.context().nestLevel();
        HtmlBlock.LOG.debug("Rendering {} @{}", this.getClass(), nestLevel);
        this.render(this.block());
        if (this.block.nestLevel() != nestLevel) {
            throw new WebAppException("Error rendering block: nestLevel=" + this.block.nestLevel() + " expected " + nestLevel);
        }
        this.context().set(nestLevel, this.block.wasInline());
    }
    
    @Override
    public void renderPartial() {
        this.render();
    }
    
    protected abstract void render(final Block p0);
    
    protected UserGroupInformation getCallerUGI() {
        final String remoteUser = this.request().getRemoteUser();
        UserGroupInformation callerUGI = null;
        if (remoteUser != null) {
            callerUGI = UserGroupInformation.createRemoteUser(remoteUser);
        }
        return callerUGI;
    }
    
    public class Block extends Hamlet
    {
        Block(final PrintWriter out, final int level, final boolean wasInline) {
            super(out, level, wasInline);
        }
        
        @Override
        protected void subView(final Class<? extends SubView> cls) {
            HtmlBlock.this.context().set(this.nestLevel(), this.wasInline());
            HtmlBlock.this.render(cls);
            this.setWasInline(HtmlBlock.this.context().wasInline());
        }
    }
}
