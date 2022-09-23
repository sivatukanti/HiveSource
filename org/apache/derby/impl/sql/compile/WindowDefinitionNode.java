// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public final class WindowDefinitionNode extends WindowNode
{
    private boolean inlined;
    private OrderByList orderByList;
    
    public void init(final Object o, final Object o2) throws StandardException {
        final String s = (String)o;
        this.orderByList = (OrderByList)o2;
        if (s != null) {
            super.init(o);
            this.inlined = false;
        }
        else {
            super.init("IN-LINE");
            this.inlined = true;
        }
        if (this.orderByList != null) {
            throw StandardException.newException("0A000.S", "WINDOW/ORDER BY");
        }
    }
    
    public String toString() {
        return "name: " + this.getName() + "\n" + "inlined: " + this.inlined + "\n" + "()\n";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public WindowDefinitionNode findEquivalentWindow(final WindowList list) {
        for (int i = 0; i < list.size(); ++i) {
            final WindowDefinitionNode windowDefinitionNode = (WindowDefinitionNode)list.elementAt(i);
            if (this.isEquivalent(windowDefinitionNode)) {
                return windowDefinitionNode;
            }
        }
        return null;
    }
    
    private boolean isEquivalent(final WindowDefinitionNode windowDefinitionNode) {
        return this.orderByList == null && windowDefinitionNode.getOrderByList() == null;
    }
    
    public OrderByList getOrderByList() {
        return this.orderByList;
    }
}
