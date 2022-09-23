// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

public class ExtensionPoint extends Target
{
    private static final String NO_CHILDREN_ALLOWED = "you must not nest child elements into an extension-point";
    
    public ExtensionPoint() {
    }
    
    public ExtensionPoint(final Target other) {
        super(other);
    }
    
    @Override
    public final void addTask(final Task task) {
        throw new BuildException("you must not nest child elements into an extension-point");
    }
    
    @Override
    public final void addDataType(final RuntimeConfigurable r) {
        throw new BuildException("you must not nest child elements into an extension-point");
    }
}
