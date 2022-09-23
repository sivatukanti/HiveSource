// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.eclipse.jetty.util.log.Log;
import java.util.List;
import org.eclipse.jetty.webapp.Descriptor;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.plus.annotation.RunAsCollection;
import javax.annotation.security.RunAs;
import javax.servlet.Servlet;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.log.Logger;

public class RunAsAnnotationHandler extends AnnotationIntrospector.AbstractIntrospectableAnnotationHandler
{
    private static final Logger LOG;
    protected WebAppContext _context;
    
    public RunAsAnnotationHandler(final WebAppContext wac) {
        super(false);
        this._context = wac;
    }
    
    @Override
    public void doHandle(final Class clazz) {
        if (!Servlet.class.isAssignableFrom(clazz)) {
            return;
        }
        final RunAs runAs = clazz.getAnnotation(RunAs.class);
        if (runAs != null) {
            final String role = runAs.value();
            if (role != null) {
                final ServletHolder holder = this.getServletHolderForClass(clazz);
                if (holder != null) {
                    final MetaData metaData = this._context.getMetaData();
                    final Descriptor d = metaData.getOriginDescriptor(holder.getName() + ".servlet.run-as");
                    if (d == null) {
                        metaData.setOrigin(holder.getName() + ".servlet.run-as");
                        final org.eclipse.jetty.plus.annotation.RunAs ra = new org.eclipse.jetty.plus.annotation.RunAs();
                        ra.setTargetClassName(clazz.getCanonicalName());
                        ra.setRoleName(role);
                        RunAsCollection raCollection = (RunAsCollection)this._context.getAttribute("org.eclipse.jetty.runAsCollection");
                        if (raCollection == null) {
                            raCollection = new RunAsCollection();
                            this._context.setAttribute("org.eclipse.jetty.runAsCollection", raCollection);
                        }
                        raCollection.add(ra);
                    }
                }
            }
            else {
                RunAsAnnotationHandler.LOG.warn("Bad value for @RunAs annotation on class " + clazz.getName(), new Object[0]);
            }
        }
    }
    
    public void handleField(final String className, final String fieldName, final int access, final String fieldType, final String signature, final Object value, final String annotation, final List<AnnotationParser.Value> values) {
        RunAsAnnotationHandler.LOG.warn("@RunAs annotation not applicable for fields: " + className + "." + fieldName, new Object[0]);
    }
    
    public void handleMethod(final String className, final String methodName, final int access, final String params, final String signature, final String[] exceptions, final String annotation, final List<AnnotationParser.Value> values) {
        RunAsAnnotationHandler.LOG.warn("@RunAs annotation ignored on method: " + className + "." + methodName + " " + signature, new Object[0]);
    }
    
    private ServletHolder getServletHolderForClass(final Class clazz) {
        ServletHolder holder = null;
        final ServletHolder[] holders = this._context.getServletHandler().getServlets();
        if (holders != null) {
            for (final ServletHolder h : holders) {
                if (h.getClassName().equals(clazz.getName())) {
                    holder = h;
                }
            }
        }
        return holder;
    }
    
    static {
        LOG = Log.getLogger(RunAsAnnotationHandler.class);
    }
}
