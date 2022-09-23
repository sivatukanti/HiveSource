// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;

public class LabelNode extends AbstractInsnNode
{
    private Label label;
    
    public LabelNode() {
        super(-1);
    }
    
    public LabelNode(final Label label) {
        super(-1);
        this.label = label;
    }
    
    public int getType() {
        return 7;
    }
    
    public Label getLabel() {
        if (this.label == null) {
            this.label = new Label();
        }
        return this.label;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitLabel(this.getLabel());
    }
    
    public AbstractInsnNode clone(final Map map) {
        return map.get(this);
    }
    
    public void resetLabel() {
        this.label = null;
    }
}
