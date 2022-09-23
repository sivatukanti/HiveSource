// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

public interface StringTemplateGroupLoader
{
    StringTemplateGroup loadGroup(final String p0);
    
    StringTemplateGroup loadGroup(final String p0, final StringTemplateGroup p1);
    
    StringTemplateGroup loadGroup(final String p0, final Class p1, final StringTemplateGroup p2);
    
    StringTemplateGroupInterface loadInterface(final String p0);
}
