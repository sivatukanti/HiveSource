// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

public class WindowList extends OrderedColumnList
{
    public void addWindow(final WindowDefinitionNode windowDefinitionNode) {
        this.addElement(windowDefinitionNode);
    }
}
