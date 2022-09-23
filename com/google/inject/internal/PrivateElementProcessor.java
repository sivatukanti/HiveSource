// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.PrivateElements;
import com.google.inject.internal.util.$Lists;
import java.util.List;

final class PrivateElementProcessor extends AbstractProcessor
{
    private final List<InjectorShell.Builder> injectorShellBuilders;
    
    PrivateElementProcessor(final Errors errors) {
        super(errors);
        this.injectorShellBuilders = (List<InjectorShell.Builder>)$Lists.newArrayList();
    }
    
    @Override
    public Boolean visit(final PrivateElements privateElements) {
        final InjectorShell.Builder builder = new InjectorShell.Builder().parent(this.injector).privateElements(privateElements);
        this.injectorShellBuilders.add(builder);
        return true;
    }
    
    public List<InjectorShell.Builder> getInjectorShellBuilders() {
        return this.injectorShellBuilders;
    }
}
