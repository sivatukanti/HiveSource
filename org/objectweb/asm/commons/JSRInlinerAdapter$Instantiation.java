// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import java.util.HashMap;
import org.objectweb.asm.tree.LabelNode;
import java.util.Map;
import java.util.AbstractMap;

class JSRInlinerAdapter$Instantiation extends AbstractMap
{
    final JSRInlinerAdapter$Instantiation previous;
    public final JSRInlinerAdapter$Subroutine subroutine;
    public final Map rangeTable;
    public final LabelNode returnLabel;
    private final /* synthetic */ JSRInlinerAdapter this$0;
    
    private JSRInlinerAdapter$Instantiation(final JSRInlinerAdapter this$0, final JSRInlinerAdapter$Instantiation previous, final JSRInlinerAdapter$Subroutine jsrInlinerAdapter$Subroutine) {
        this.this$0 = this$0;
        this.rangeTable = new HashMap();
        this.previous = previous;
        this.subroutine = jsrInlinerAdapter$Subroutine;
        for (JSRInlinerAdapter$Instantiation previous2 = previous; previous2 != null; previous2 = previous2.previous) {
            if (previous2.subroutine == jsrInlinerAdapter$Subroutine) {
                throw new RuntimeException("Recursive invocation of " + jsrInlinerAdapter$Subroutine);
            }
        }
        if (previous != null) {
            this.returnLabel = new LabelNode();
        }
        else {
            this.returnLabel = null;
        }
        Object o = null;
        for (int i = 0; i < this$0.instructions.size(); ++i) {
            final AbstractInsnNode value = this$0.instructions.get(i);
            if (value.getType() == 7) {
                final LabelNode labelNode = (LabelNode)value;
                if (o == null) {
                    o = new LabelNode();
                }
                this.rangeTable.put(labelNode, o);
            }
            else if (this.findOwner(i) == this) {
                o = null;
            }
        }
    }
    
    public JSRInlinerAdapter$Instantiation findOwner(final int bitIndex) {
        if (!this.subroutine.ownsInstruction(bitIndex)) {
            return null;
        }
        if (!this.this$0.dualCitizens.get(bitIndex)) {
            return this;
        }
        JSRInlinerAdapter$Instantiation jsrInlinerAdapter$Instantiation = this;
        for (JSRInlinerAdapter$Instantiation jsrInlinerAdapter$Instantiation2 = this.previous; jsrInlinerAdapter$Instantiation2 != null; jsrInlinerAdapter$Instantiation2 = jsrInlinerAdapter$Instantiation2.previous) {
            if (jsrInlinerAdapter$Instantiation2.subroutine.ownsInstruction(bitIndex)) {
                jsrInlinerAdapter$Instantiation = jsrInlinerAdapter$Instantiation2;
            }
        }
        return jsrInlinerAdapter$Instantiation;
    }
    
    public LabelNode gotoLabel(final LabelNode labelNode) {
        return this.findOwner(this.this$0.instructions.indexOf(labelNode)).rangeTable.get(labelNode);
    }
    
    public LabelNode rangeLabel(final LabelNode labelNode) {
        return this.rangeTable.get(labelNode);
    }
    
    public Set entrySet() {
        return null;
    }
    
    public Object get(final Object o) {
        return this.gotoLabel((LabelNode)o);
    }
}
