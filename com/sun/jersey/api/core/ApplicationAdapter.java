// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.core;

import java.util.Collection;
import javax.ws.rs.core.Application;

public class ApplicationAdapter extends DefaultResourceConfig
{
    public ApplicationAdapter(final Application ac) {
        if (ac.getClasses() != null) {
            this.getClasses().addAll(ac.getClasses());
        }
        if (ac.getSingletons() != null) {
            this.getSingletons().addAll(ac.getSingletons());
        }
    }
}
