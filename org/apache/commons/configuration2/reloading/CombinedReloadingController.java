// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.reloading;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;

public class CombinedReloadingController extends ReloadingController
{
    private static final ReloadingDetector DUMMY;
    private final Collection<ReloadingController> controllers;
    private final ReloadingDetector detector;
    
    public CombinedReloadingController(final Collection<? extends ReloadingController> subCtrls) {
        super(CombinedReloadingController.DUMMY);
        this.controllers = checkManagedControllers(subCtrls);
        this.detector = new MultiReloadingControllerDetector(this);
    }
    
    public Collection<ReloadingController> getSubControllers() {
        return this.controllers;
    }
    
    @Override
    public ReloadingDetector getDetector() {
        return this.detector;
    }
    
    public void resetInitialReloadingState() {
        this.getDetector().reloadingPerformed();
    }
    
    private static Collection<ReloadingController> checkManagedControllers(final Collection<? extends ReloadingController> subCtrls) {
        if (subCtrls == null) {
            throw new IllegalArgumentException("Collection with sub controllers must not be null!");
        }
        final Collection<ReloadingController> ctrls = new ArrayList<ReloadingController>(subCtrls);
        for (final ReloadingController rc : ctrls) {
            if (rc == null) {
                throw new IllegalArgumentException("Collection with sub controllers contains a null entry!");
            }
        }
        return Collections.unmodifiableCollection((Collection<? extends ReloadingController>)ctrls);
    }
    
    static {
        DUMMY = new MultiReloadingControllerDetector(null);
    }
    
    private static class MultiReloadingControllerDetector implements ReloadingDetector
    {
        private final CombinedReloadingController owner;
        
        public MultiReloadingControllerDetector(final CombinedReloadingController o) {
            this.owner = o;
        }
        
        @Override
        public boolean isReloadingRequired() {
            for (final ReloadingController rc : this.owner.getSubControllers()) {
                if (rc.checkForReloading(null)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public void reloadingPerformed() {
            for (final ReloadingController rc : this.owner.getSubControllers()) {
                rc.resetReloadingState();
            }
        }
    }
}
