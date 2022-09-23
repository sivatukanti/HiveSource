// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.annotation;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.servlet.ServletHolder;
import javax.servlet.ServletException;
import java.util.HashMap;
import org.eclipse.jetty.util.log.Logger;

public class RunAsCollection
{
    private static final Logger LOG;
    public static final String RUNAS_COLLECTION = "org.eclipse.jetty.runAsCollection";
    private HashMap<String, RunAs> _runAsMap;
    
    public RunAsCollection() {
        this._runAsMap = new HashMap<String, RunAs>();
    }
    
    public void add(final RunAs runAs) {
        if (runAs == null || runAs.getTargetClassName() == null) {
            return;
        }
        if (RunAsCollection.LOG.isDebugEnabled()) {
            RunAsCollection.LOG.debug("Adding run-as for class=" + runAs.getTargetClassName(), new Object[0]);
        }
        this._runAsMap.put(runAs.getTargetClassName(), runAs);
    }
    
    public RunAs getRunAs(final Object o) throws ServletException {
        if (o == null) {
            return null;
        }
        return this._runAsMap.get(o.getClass().getCanonicalName());
    }
    
    public void setRunAs(final Object o) throws ServletException {
        if (o == null) {
            return;
        }
        if (!ServletHolder.class.isAssignableFrom(o.getClass())) {
            return;
        }
        final RunAs runAs = this._runAsMap.get(o.getClass().getName());
        if (runAs == null) {
            return;
        }
        runAs.setRunAs((ServletHolder)o);
    }
    
    static {
        LOG = Log.getLogger(RunAsCollection.class);
    }
}
