// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.eclipse.jetty.util.log.Log;
import javax.annotation.Resource;
import javax.naming.NamingException;
import org.eclipse.jetty.plus.jndi.NamingEntryUtil;
import javax.annotation.Resources;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.log.Logger;

public class ResourcesAnnotationHandler extends AnnotationIntrospector.AbstractIntrospectableAnnotationHandler
{
    private static final Logger LOG;
    protected WebAppContext _wac;
    
    public ResourcesAnnotationHandler(final WebAppContext wac) {
        super(true);
        this._wac = wac;
    }
    
    @Override
    public void doHandle(final Class clazz) {
        final Resources resources = clazz.getAnnotation(Resources.class);
        if (resources != null) {
            final Resource[] resArray = resources.value();
            if (resArray == null || resArray.length == 0) {
                ResourcesAnnotationHandler.LOG.warn("Skipping empty or incorrect Resources annotation on " + clazz.getName(), new Object[0]);
                return;
            }
            for (int j = 0; j < resArray.length; ++j) {
                final String name = resArray[j].name();
                final String mappedName = resArray[j].mappedName();
                final Resource.AuthenticationType auth = resArray[j].authenticationType();
                final Class type = resArray[j].type();
                final boolean shareable = resArray[j].shareable();
                if (name == null || name.trim().equals("")) {
                    throw new IllegalStateException("Class level Resource annotations must contain a name (Common Annotations Spec Section 2.3)");
                }
                try {
                    if (!NamingEntryUtil.bindToENC(this._wac, name, mappedName) && !NamingEntryUtil.bindToENC(this._wac.getServer(), name, mappedName)) {
                        ResourcesAnnotationHandler.LOG.warn("Skipping Resources(Resource) annotation on " + clazz.getName() + " for name " + name + ": No resource bound at " + ((mappedName == null) ? name : mappedName), new Object[0]);
                    }
                }
                catch (NamingException e) {
                    ResourcesAnnotationHandler.LOG.warn(e);
                }
            }
        }
    }
    
    static {
        LOG = Log.getLogger(ResourcesAnnotationHandler.class);
    }
}
