// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.InsnNode;
import java.util.List;
import java.util.ArrayList;
import org.objectweb.asm.tree.InsnList;
import java.util.LinkedList;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import java.util.Iterator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.Label;
import java.util.HashMap;
import java.util.BitSet;
import java.util.Map;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class JSRInlinerAdapter extends MethodNode implements Opcodes
{
    private final MethodVisitor mv;
    private final Map subroutineHeads;
    private final JSRInlinerAdapter$Subroutine mainSubroutine;
    private final BitSet dualCitizens;
    
    public JSRInlinerAdapter(final MethodVisitor mv, final int n, final String s, final String s2, final String s3, final String[] array) {
        super(n, s, s2, s3, array);
        this.subroutineHeads = new HashMap();
        this.mainSubroutine = new JSRInlinerAdapter$Subroutine();
        this.dualCitizens = new BitSet();
        this.mv = mv;
    }
    
    public void visitJumpInsn(final int n, final Label label) {
        super.visitJumpInsn(n, label);
        final LabelNode label2 = ((JumpInsnNode)this.instructions.getLast()).label;
        if (n == 168 && !this.subroutineHeads.containsKey(label2)) {
            this.subroutineHeads.put(label2, new JSRInlinerAdapter$Subroutine());
        }
    }
    
    public void visitEnd() {
        if (!this.subroutineHeads.isEmpty()) {
            this.markSubroutines();
            this.emitCode();
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
    
    private void markSubroutines() {
        final BitSet set = new BitSet();
        this.markSubroutineWalk(this.mainSubroutine, 0, set);
        for (final Map.Entry<LabelNode, V> entry : this.subroutineHeads.entrySet()) {
            this.markSubroutineWalk((JSRInlinerAdapter$Subroutine)entry.getValue(), this.instructions.indexOf(entry.getKey()), set);
        }
    }
    
    private void markSubroutineWalk(final JSRInlinerAdapter$Subroutine jsrInlinerAdapter$Subroutine, final int n, final BitSet set) {
        this.markSubroutineWalkDFS(jsrInlinerAdapter$Subroutine, n, set);
        int i = 1;
        while (i != 0) {
            i = 0;
            for (final TryCatchBlockNode tryCatchBlockNode : this.tryCatchBlocks) {
                final int index = this.instructions.indexOf(tryCatchBlockNode.handler);
                if (jsrInlinerAdapter$Subroutine.instructions.get(index)) {
                    continue;
                }
                final int index2 = this.instructions.indexOf(tryCatchBlockNode.start);
                final int index3 = this.instructions.indexOf(tryCatchBlockNode.end);
                final int nextSetBit = jsrInlinerAdapter$Subroutine.instructions.nextSetBit(index2);
                if (nextSetBit == -1 || nextSetBit >= index3) {
                    continue;
                }
                this.markSubroutineWalkDFS(jsrInlinerAdapter$Subroutine, index, set);
                i = 1;
            }
        }
    }
    
    private void markSubroutineWalkDFS(final JSRInlinerAdapter$Subroutine jsrInlinerAdapter$Subroutine, int bitIndex, final BitSet set) {
        while (true) {
            final AbstractInsnNode value = this.instructions.get(bitIndex);
            if (jsrInlinerAdapter$Subroutine.instructions.get(bitIndex)) {
                return;
            }
            jsrInlinerAdapter$Subroutine.instructions.set(bitIndex);
            if (set.get(bitIndex)) {
                this.dualCitizens.set(bitIndex);
            }
            set.set(bitIndex);
            if (value.getType() == 6 && value.getOpcode() != 168) {
                this.markSubroutineWalkDFS(jsrInlinerAdapter$Subroutine, this.instructions.indexOf(((JumpInsnNode)value).label), set);
            }
            if (value.getType() == 10) {
                final TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode)value;
                this.markSubroutineWalkDFS(jsrInlinerAdapter$Subroutine, this.instructions.indexOf(tableSwitchInsnNode.dflt), set);
                for (int i = tableSwitchInsnNode.labels.size() - 1; i >= 0; --i) {
                    this.markSubroutineWalkDFS(jsrInlinerAdapter$Subroutine, this.instructions.indexOf((AbstractInsnNode)tableSwitchInsnNode.labels.get(i)), set);
                }
            }
            if (value.getType() == 11) {
                final LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode)value;
                this.markSubroutineWalkDFS(jsrInlinerAdapter$Subroutine, this.instructions.indexOf(lookupSwitchInsnNode.dflt), set);
                for (int j = lookupSwitchInsnNode.labels.size() - 1; j >= 0; --j) {
                    this.markSubroutineWalkDFS(jsrInlinerAdapter$Subroutine, this.instructions.indexOf((AbstractInsnNode)lookupSwitchInsnNode.labels.get(j)), set);
                }
            }
            switch (this.instructions.get(bitIndex).getOpcode()) {
                case 167:
                case 169:
                case 170:
                case 171:
                case 172:
                case 173:
                case 174:
                case 175:
                case 176:
                case 177:
                case 191: {}
                default: {
                    ++bitIndex;
                    continue;
                }
            }
        }
    }
    
    private void emitCode() {
        final LinkedList list = new LinkedList<JSRInlinerAdapter$Instantiation>();
        list.add(new JSRInlinerAdapter$Instantiation(this, null, this.mainSubroutine, null));
        final InsnList instructions = new InsnList();
        final ArrayList tryCatchBlocks = new ArrayList();
        final ArrayList localVariables = new ArrayList();
        while (!list.isEmpty()) {
            this.emitSubroutine(list.removeFirst(), list, instructions, tryCatchBlocks, localVariables);
        }
        this.instructions = instructions;
        this.tryCatchBlocks = tryCatchBlocks;
        this.localVariables = localVariables;
    }
    
    private void emitSubroutine(final JSRInlinerAdapter$Instantiation jsrInlinerAdapter$Instantiation, final List list, final InsnList list2, final List list3, final List list4) {
        LabelNode labelNode = null;
        for (int i = 0; i < this.instructions.size(); ++i) {
            final AbstractInsnNode value = this.instructions.get(i);
            final JSRInlinerAdapter$Instantiation owner = jsrInlinerAdapter$Instantiation.findOwner(i);
            if (value.getType() == 7) {
                final LabelNode rangeLabel = jsrInlinerAdapter$Instantiation.rangeLabel((LabelNode)value);
                if (rangeLabel != labelNode) {
                    list2.add(rangeLabel);
                    labelNode = rangeLabel;
                }
            }
            else if (owner == jsrInlinerAdapter$Instantiation) {
                if (value.getOpcode() == 169) {
                    LabelNode returnLabel = null;
                    for (JSRInlinerAdapter$Instantiation previous = jsrInlinerAdapter$Instantiation; previous != null; previous = previous.previous) {
                        if (previous.subroutine.ownsInstruction(i)) {
                            returnLabel = previous.returnLabel;
                        }
                    }
                    if (returnLabel == null) {
                        throw new RuntimeException("Instruction #" + i + " is a RET not owned by any subroutine");
                    }
                    list2.add(new JumpInsnNode(167, returnLabel));
                }
                else if (value.getOpcode() == 168) {
                    final LabelNode label = ((JumpInsnNode)value).label;
                    final JSRInlinerAdapter$Instantiation jsrInlinerAdapter$Instantiation2 = new JSRInlinerAdapter$Instantiation(this, jsrInlinerAdapter$Instantiation, this.subroutineHeads.get(label), null);
                    final LabelNode gotoLabel = jsrInlinerAdapter$Instantiation2.gotoLabel(label);
                    list2.add(new InsnNode(1));
                    list2.add(new JumpInsnNode(167, gotoLabel));
                    list2.add(jsrInlinerAdapter$Instantiation2.returnLabel);
                    list.add(jsrInlinerAdapter$Instantiation2);
                }
                else {
                    list2.add(value.clone(jsrInlinerAdapter$Instantiation));
                }
            }
        }
        for (final TryCatchBlockNode tryCatchBlockNode : this.tryCatchBlocks) {
            final LabelNode rangeLabel2 = jsrInlinerAdapter$Instantiation.rangeLabel(tryCatchBlockNode.start);
            final LabelNode rangeLabel3 = jsrInlinerAdapter$Instantiation.rangeLabel(tryCatchBlockNode.end);
            if (rangeLabel2 == rangeLabel3) {
                continue;
            }
            final LabelNode gotoLabel2 = jsrInlinerAdapter$Instantiation.gotoLabel(tryCatchBlockNode.handler);
            if (rangeLabel2 == null || rangeLabel3 == null || gotoLabel2 == null) {
                throw new RuntimeException("Internal error!");
            }
            list3.add(new TryCatchBlockNode(rangeLabel2, rangeLabel3, gotoLabel2, tryCatchBlockNode.type));
        }
        for (final LocalVariableNode localVariableNode : this.localVariables) {
            final LabelNode rangeLabel4 = jsrInlinerAdapter$Instantiation.rangeLabel(localVariableNode.start);
            final LabelNode rangeLabel5 = jsrInlinerAdapter$Instantiation.rangeLabel(localVariableNode.end);
            if (rangeLabel4 == rangeLabel5) {
                continue;
            }
            list4.add(new LocalVariableNode(localVariableNode.name, localVariableNode.desc, localVariableNode.signature, rangeLabel4, rangeLabel5, localVariableNode.index));
        }
    }
    
    private static void log(final String x) {
        System.err.println(x);
    }
}
