// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.internal.InternalInjectorCreator;
import java.util.Arrays;

public final class Guice
{
    private Guice() {
    }
    
    public static Injector createInjector(final Module... modules) {
        return createInjector(Arrays.asList(modules));
    }
    
    public static Injector createInjector(final Iterable<? extends Module> modules) {
        return createInjector(Stage.DEVELOPMENT, modules);
    }
    
    public static Injector createInjector(final Stage stage, final Module... modules) {
        return createInjector(stage, Arrays.asList(modules));
    }
    
    public static Injector createInjector(final Stage stage, final Iterable<? extends Module> modules) {
        return new InternalInjectorCreator().stage(stage).addModules(modules).build();
    }
}
