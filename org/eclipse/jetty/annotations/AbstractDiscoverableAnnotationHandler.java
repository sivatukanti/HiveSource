// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import java.util.ArrayList;
import org.eclipse.jetty.webapp.DiscoveredAnnotation;
import java.util.List;
import org.eclipse.jetty.webapp.WebAppContext;

public abstract class AbstractDiscoverableAnnotationHandler implements AnnotationParser.DiscoverableAnnotationHandler
{
    protected WebAppContext _context;
    protected List<DiscoveredAnnotation> _annotations;
    
    public AbstractDiscoverableAnnotationHandler(final WebAppContext context) {
        this._annotations = new ArrayList<DiscoveredAnnotation>();
        this._context = context;
    }
    
    public List<DiscoveredAnnotation> getAnnotationList() {
        return this._annotations;
    }
    
    public void resetList() {
        this._annotations.clear();
    }
    
    public void addAnnotation(final DiscoveredAnnotation a) {
        this._annotations.add(a);
    }
}
