// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.template;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.template.TemplateProcessor;
import com.sun.jersey.spi.template.ViewProcessor;

public final class TemplateViewProcessor implements ViewProcessor<String>
{
    private final TemplateProcessor tp;
    
    public TemplateViewProcessor(final TemplateProcessor tp) {
        this.tp = tp;
    }
    
    @Override
    public String resolve(final String name) {
        return this.tp.resolve(name);
    }
    
    @Override
    public void writeTo(final String t, final Viewable viewable, final OutputStream out) throws IOException {
        this.tp.writeTo(t, viewable.getModel(), out);
    }
}
