// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.Element;
import java.util.List;
import java.util.Iterator;
import com.google.inject.spi.DefaultElementVisitor;

abstract class AbstractProcessor extends DefaultElementVisitor<Boolean>
{
    protected Errors errors;
    protected InjectorImpl injector;
    
    protected AbstractProcessor(final Errors errors) {
        this.errors = errors;
    }
    
    public void process(final Iterable<InjectorShell> isolatedInjectorBuilders) {
        for (final InjectorShell injectorShell : isolatedInjectorBuilders) {
            this.process(injectorShell.getInjector(), injectorShell.getElements());
        }
    }
    
    public void process(final InjectorImpl injector, final List<Element> elements) {
        final Errors errorsAnyElement = this.errors;
        this.injector = injector;
        try {
            final Iterator<Element> i = elements.iterator();
            while (i.hasNext()) {
                final Element element = i.next();
                this.errors = errorsAnyElement.withSource(element.getSource());
                final Boolean allDone = element.acceptVisitor((ElementVisitor<Boolean>)this);
                if (allDone) {
                    i.remove();
                }
            }
        }
        finally {
            this.errors = errorsAnyElement;
            this.injector = null;
        }
    }
    
    @Override
    protected Boolean visitOther(final Element element) {
        return false;
    }
}
