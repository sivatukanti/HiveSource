// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Preconditions;
import com.google.inject.Stage;
import com.google.inject.spi.RequireExplicitBindingsOption;
import com.google.inject.spi.DisableCircularProxiesOption;

class InjectorOptionsProcessor extends AbstractProcessor
{
    private boolean disableCircularProxies;
    private boolean jitDisabled;
    
    InjectorOptionsProcessor(final Errors errors) {
        super(errors);
        this.disableCircularProxies = false;
        this.jitDisabled = false;
    }
    
    @Override
    public Boolean visit(final DisableCircularProxiesOption option) {
        this.disableCircularProxies = true;
        return true;
    }
    
    @Override
    public Boolean visit(final RequireExplicitBindingsOption option) {
        this.jitDisabled = true;
        return true;
    }
    
    InjectorImpl.InjectorOptions getOptions(final Stage stage, final InjectorImpl.InjectorOptions parentOptions) {
        $Preconditions.checkNotNull(stage, (Object)"stage must be set");
        if (parentOptions == null) {
            return new InjectorImpl.InjectorOptions(stage, this.jitDisabled, this.disableCircularProxies);
        }
        $Preconditions.checkState(stage == parentOptions.stage, (Object)"child & parent stage don't match");
        return new InjectorImpl.InjectorOptions(stage, this.jitDisabled || parentOptions.jitDisabled, this.disableCircularProxies || parentOptions.disableCircularProxies);
    }
}
