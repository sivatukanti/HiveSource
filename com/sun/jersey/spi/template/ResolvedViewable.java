// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.template;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.jersey.api.view.Viewable;

public class ResolvedViewable<T> extends Viewable
{
    private final ViewProcessor<T> vp;
    private final T templateObject;
    
    public ResolvedViewable(final ViewProcessor<T> vp, final T t, final Viewable v) {
        this((ViewProcessor<Object>)vp, t, v, null);
    }
    
    public ResolvedViewable(final ViewProcessor<T> vp, final T t, final Viewable v, final Class<?> resolvingClass) {
        super(v.getTemplateName(), v.getModel(), resolvingClass);
        this.vp = vp;
        this.templateObject = t;
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        this.vp.writeTo(this.templateObject, this, out);
    }
}
