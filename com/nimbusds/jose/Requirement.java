// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

public enum Requirement
{
    REQUIRED("REQUIRED", 0), 
    RECOMMENDED("RECOMMENDED", 1), 
    OPTIONAL("OPTIONAL", 2);
    
    private Requirement(final String name, final int ordinal) {
    }
}
