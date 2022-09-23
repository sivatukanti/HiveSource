// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.impl;

import com.sun.jersey.localization.Localizer;
import com.sun.jersey.localization.LocalizableMessageFactory;

public final class ApiMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    static {
        messageFactory = new LocalizableMessageFactory("com.sun.jersey.impl.api");
        localizer = new Localizer();
    }
}
