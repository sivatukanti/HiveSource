// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.Stage;
import com.google.inject.internal.util.$ImmutableList;
import java.util.Iterator;
import com.google.inject.ConfigurationException;
import com.google.inject.spi.InjectionPoint;
import java.util.Set;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.StaticInjectionRequest;
import com.google.inject.internal.util.$Lists;
import java.util.List;

final class InjectionRequestProcessor extends AbstractProcessor
{
    private final List<StaticInjection> staticInjections;
    private final Initializer initializer;
    
    InjectionRequestProcessor(final Errors errors, final Initializer initializer) {
        super(errors);
        this.staticInjections = (List<StaticInjection>)$Lists.newArrayList();
        this.initializer = initializer;
    }
    
    @Override
    public Boolean visit(final StaticInjectionRequest request) {
        this.staticInjections.add(new StaticInjection(this.injector, request));
        return true;
    }
    
    @Override
    public Boolean visit(final InjectionRequest<?> request) {
        Set<InjectionPoint> injectionPoints;
        try {
            injectionPoints = request.getInjectionPoints();
        }
        catch (ConfigurationException e) {
            this.errors.merge(e.getErrorMessages());
            injectionPoints = e.getPartialValue();
        }
        this.initializer.requestInjection(this.injector, request.getInstance(), request.getSource(), injectionPoints);
        return true;
    }
    
    void validate() {
        for (final StaticInjection staticInjection : this.staticInjections) {
            staticInjection.validate();
        }
    }
    
    void injectMembers() {
        for (final StaticInjection staticInjection : this.staticInjections) {
            staticInjection.injectMembers();
        }
    }
    
    private class StaticInjection
    {
        final InjectorImpl injector;
        final Object source;
        final StaticInjectionRequest request;
        $ImmutableList<SingleMemberInjector> memberInjectors;
        
        public StaticInjection(final InjectorImpl injector, final StaticInjectionRequest request) {
            this.injector = injector;
            this.source = request.getSource();
            this.request = request;
        }
        
        void validate() {
            final Errors errorsForMember = InjectionRequestProcessor.this.errors.withSource(this.source);
            Set<InjectionPoint> injectionPoints;
            try {
                injectionPoints = this.request.getInjectionPoints();
            }
            catch (ConfigurationException e) {
                InjectionRequestProcessor.this.errors.merge(e.getErrorMessages());
                injectionPoints = e.getPartialValue();
            }
            this.memberInjectors = this.injector.membersInjectorStore.getInjectors(injectionPoints, errorsForMember);
        }
        
        void injectMembers() {
            try {
                this.injector.callInContext((ContextualCallable<Object>)new ContextualCallable<Void>() {
                    public Void call(final InternalContext context) {
                        for (final SingleMemberInjector memberInjector : StaticInjection.this.memberInjectors) {
                            if (StaticInjection.this.injector.options.stage != Stage.TOOL || memberInjector.getInjectionPoint().isToolable()) {
                                memberInjector.inject(InjectionRequestProcessor.this.errors, context, null);
                            }
                        }
                        return null;
                    }
                });
            }
            catch (ErrorsException e) {
                throw new AssertionError();
            }
        }
    }
}
