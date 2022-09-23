// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class LookupSwitchInsnNode extends AbstractInsnNode
{
    public LabelNode dflt;
    public List keys;
    public List labels;
    
    public LookupSwitchInsnNode(final LabelNode dflt, final int[] array, final LabelNode[] a) {
        super(171);
        this.dflt = dflt;
        this.keys = new ArrayList((array == null) ? 0 : array.length);
        this.labels = new ArrayList((a == null) ? 0 : a.length);
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                this.keys.add(new Integer(array[i]));
            }
        }
        if (a != null) {
            this.labels.addAll(Arrays.asList(a));
        }
    }
    
    public int getType() {
        return 11;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        final int[] array = new int[this.keys.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (int)this.keys.get(i);
        }
        final Label[] array2 = new Label[this.labels.size()];
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = ((LabelNode)this.labels.get(j)).getLabel();
        }
        methodVisitor.visitLookupSwitchInsn(this.dflt.getLabel(), array, array2);
    }
    
    public AbstractInsnNode clone(final Map map) {
        final LookupSwitchInsnNode lookupSwitchInsnNode = new LookupSwitchInsnNode(AbstractInsnNode.clone(this.dflt, map), null, AbstractInsnNode.clone(this.labels, map));
        lookupSwitchInsnNode.keys.addAll(this.keys);
        return lookupSwitchInsnNode;
    }
}
