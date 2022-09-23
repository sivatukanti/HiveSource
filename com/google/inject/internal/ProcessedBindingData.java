// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.util.Iterator;
import com.google.inject.internal.util.$Lists;
import java.util.List;

class ProcessedBindingData
{
    private final List<CreationListener> creationListeners;
    private final List<Runnable> uninitializedBindings;
    
    ProcessedBindingData() {
        this.creationListeners = (List<CreationListener>)$Lists.newArrayList();
        this.uninitializedBindings = (List<Runnable>)$Lists.newArrayList();
    }
    
    void addCreationListener(final CreationListener listener) {
        this.creationListeners.add(listener);
    }
    
    void addUninitializedBinding(final Runnable runnable) {
        this.uninitializedBindings.add(runnable);
    }
    
    void initializeBindings() {
        for (final Runnable initializer : this.uninitializedBindings) {
            initializer.run();
        }
    }
    
    void runCreationListeners(final Errors errors) {
        for (final CreationListener creationListener : this.creationListeners) {
            creationListener.notify(errors);
        }
    }
}
