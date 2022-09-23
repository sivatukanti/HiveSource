// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.Key;
import com.google.inject.spi.UntargettedBinding;
import com.google.inject.Binding;

class UntargettedBindingProcessor extends AbstractBindingProcessor
{
    UntargettedBindingProcessor(final Errors errors, final ProcessedBindingData bindingData) {
        super(errors, bindingData);
    }
    
    @Override
    public <T> Boolean visit(final Binding<T> binding) {
        return binding.acceptTargetVisitor((BindingTargetVisitor<? super T, Boolean>)new Processor<T, Boolean>((BindingImpl)binding) {
            @Override
            public Boolean visit(final UntargettedBinding<? extends T> untargetted) {
                this.prepareBinding();
                if (this.key.getAnnotationType() != null) {
                    UntargettedBindingProcessor.this.errors.missingImplementation(this.key);
                    UntargettedBindingProcessor.this.putBinding(UntargettedBindingProcessor.this.invalidBinding(UntargettedBindingProcessor.this.injector, this.key, this.source));
                    return true;
                }
                try {
                    final BindingImpl<T> binding = UntargettedBindingProcessor.this.injector.createUninitializedBinding((Key<T>)this.key, this.scoping, this.source, UntargettedBindingProcessor.this.errors, false);
                    this.scheduleInitialization(binding);
                    UntargettedBindingProcessor.this.putBinding(binding);
                }
                catch (ErrorsException e) {
                    UntargettedBindingProcessor.this.errors.merge(e.getErrors());
                    UntargettedBindingProcessor.this.putBinding(UntargettedBindingProcessor.this.invalidBinding(UntargettedBindingProcessor.this.injector, this.key, this.source));
                }
                return true;
            }
            
            @Override
            protected Boolean visitOther(final Binding<? extends T> binding) {
                return false;
            }
        });
    }
}
