// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TokenRewriteStream extends CommonTokenStream
{
    public static final String DEFAULT_PROGRAM_NAME = "default";
    public static final int PROGRAM_INIT_SIZE = 100;
    public static final int MIN_TOKEN_INDEX = 0;
    protected Map programs;
    protected Map lastRewriteTokenIndexes;
    
    public TokenRewriteStream() {
        this.programs = null;
        this.lastRewriteTokenIndexes = null;
        this.init();
    }
    
    protected void init() {
        (this.programs = new HashMap()).put("default", new ArrayList(100));
        this.lastRewriteTokenIndexes = new HashMap();
    }
    
    public TokenRewriteStream(final TokenSource tokenSource) {
        super(tokenSource);
        this.programs = null;
        this.lastRewriteTokenIndexes = null;
        this.init();
    }
    
    public TokenRewriteStream(final TokenSource tokenSource, final int channel) {
        super(tokenSource, channel);
        this.programs = null;
        this.lastRewriteTokenIndexes = null;
        this.init();
    }
    
    public void rollback(final int instructionIndex) {
        this.rollback("default", instructionIndex);
    }
    
    public void rollback(final String programName, final int instructionIndex) {
        final List is = this.programs.get(programName);
        if (is != null) {
            this.programs.put(programName, is.subList(0, instructionIndex));
        }
    }
    
    public void deleteProgram() {
        this.deleteProgram("default");
    }
    
    public void deleteProgram(final String programName) {
        this.rollback(programName, 0);
    }
    
    public void insertAfter(final Token t, final Object text) {
        this.insertAfter("default", t, text);
    }
    
    public void insertAfter(final int index, final Object text) {
        this.insertAfter("default", index, text);
    }
    
    public void insertAfter(final String programName, final Token t, final Object text) {
        this.insertAfter(programName, t.getTokenIndex(), text);
    }
    
    public void insertAfter(final String programName, final int index, final Object text) {
        this.insertBefore(programName, index + 1, text);
    }
    
    public void insertBefore(final Token t, final Object text) {
        this.insertBefore("default", t, text);
    }
    
    public void insertBefore(final int index, final Object text) {
        this.insertBefore("default", index, text);
    }
    
    public void insertBefore(final String programName, final Token t, final Object text) {
        this.insertBefore(programName, t.getTokenIndex(), text);
    }
    
    public void insertBefore(final String programName, final int index, final Object text) {
        final RewriteOperation op = new InsertBeforeOp(index, text);
        final List rewrites = this.getProgram(programName);
        op.instructionIndex = rewrites.size();
        rewrites.add(op);
    }
    
    public void replace(final int index, final Object text) {
        this.replace("default", index, index, text);
    }
    
    public void replace(final int from, final int to, final Object text) {
        this.replace("default", from, to, text);
    }
    
    public void replace(final Token indexT, final Object text) {
        this.replace("default", indexT, indexT, text);
    }
    
    public void replace(final Token from, final Token to, final Object text) {
        this.replace("default", from, to, text);
    }
    
    public void replace(final String programName, final int from, final int to, final Object text) {
        if (from > to || from < 0 || to < 0 || to >= this.tokens.size()) {
            throw new IllegalArgumentException("replace: range invalid: " + from + ".." + to + "(size=" + this.tokens.size() + ")");
        }
        final RewriteOperation op = new ReplaceOp(from, to, text);
        final List rewrites = this.getProgram(programName);
        op.instructionIndex = rewrites.size();
        rewrites.add(op);
    }
    
    public void replace(final String programName, final Token from, final Token to, final Object text) {
        this.replace(programName, from.getTokenIndex(), to.getTokenIndex(), text);
    }
    
    public void delete(final int index) {
        this.delete("default", index, index);
    }
    
    public void delete(final int from, final int to) {
        this.delete("default", from, to);
    }
    
    public void delete(final Token indexT) {
        this.delete("default", indexT, indexT);
    }
    
    public void delete(final Token from, final Token to) {
        this.delete("default", from, to);
    }
    
    public void delete(final String programName, final int from, final int to) {
        this.replace(programName, from, to, null);
    }
    
    public void delete(final String programName, final Token from, final Token to) {
        this.replace(programName, from, to, null);
    }
    
    public int getLastRewriteTokenIndex() {
        return this.getLastRewriteTokenIndex("default");
    }
    
    protected int getLastRewriteTokenIndex(final String programName) {
        final Integer I = this.lastRewriteTokenIndexes.get(programName);
        if (I == null) {
            return -1;
        }
        return I;
    }
    
    protected void setLastRewriteTokenIndex(final String programName, final int i) {
        this.lastRewriteTokenIndexes.put(programName, new Integer(i));
    }
    
    protected List getProgram(final String name) {
        List is = this.programs.get(name);
        if (is == null) {
            is = this.initializeProgram(name);
        }
        return is;
    }
    
    private List initializeProgram(final String name) {
        final List is = new ArrayList(100);
        this.programs.put(name, is);
        return is;
    }
    
    public String toOriginalString() {
        this.fill();
        return this.toOriginalString(0, this.size() - 1);
    }
    
    public String toOriginalString(final int start, final int end) {
        final StringBuffer buf = new StringBuffer();
        for (int i = start; i >= 0 && i <= end && i < this.tokens.size(); ++i) {
            if (this.get(i).getType() != -1) {
                buf.append(this.get(i).getText());
            }
        }
        return buf.toString();
    }
    
    public String toString() {
        this.fill();
        return this.toString(0, this.size() - 1);
    }
    
    public String toString(final String programName) {
        this.fill();
        return this.toString(programName, 0, this.size() - 1);
    }
    
    public String toString(final int start, final int end) {
        return this.toString("default", start, end);
    }
    
    public String toString(final String programName, int start, int end) {
        final List rewrites = this.programs.get(programName);
        if (end > this.tokens.size() - 1) {
            end = this.tokens.size() - 1;
        }
        if (start < 0) {
            start = 0;
        }
        if (rewrites == null || rewrites.size() == 0) {
            return this.toOriginalString(start, end);
        }
        final StringBuffer buf = new StringBuffer();
        final Map indexToOp = this.reduceToSingleOperationPerIndex(rewrites);
        int i = start;
        while (i <= end && i < this.tokens.size()) {
            final RewriteOperation op = indexToOp.get(new Integer(i));
            indexToOp.remove(new Integer(i));
            final Token t = this.tokens.get(i);
            if (op == null) {
                if (t.getType() != -1) {
                    buf.append(t.getText());
                }
                ++i;
            }
            else {
                i = op.execute(buf);
            }
        }
        if (end == this.tokens.size() - 1) {
            for (final RewriteOperation op2 : indexToOp.values()) {
                if (op2.index >= this.tokens.size() - 1) {
                    buf.append(op2.text);
                }
            }
        }
        return buf.toString();
    }
    
    protected Map reduceToSingleOperationPerIndex(final List rewrites) {
        for (int i = 0; i < rewrites.size(); ++i) {
            final RewriteOperation op = rewrites.get(i);
            if (op != null) {
                if (op instanceof ReplaceOp) {
                    final ReplaceOp rop = rewrites.get(i);
                    final List inserts = this.getKindOfOps(rewrites, InsertBeforeOp.class, i);
                    for (int j = 0; j < inserts.size(); ++j) {
                        final InsertBeforeOp iop = inserts.get(j);
                        if (iop.index == rop.index) {
                            rewrites.set(iop.instructionIndex, null);
                            rop.text = iop.text.toString() + ((rop.text != null) ? rop.text.toString() : "");
                        }
                        else if (iop.index > rop.index && iop.index <= rop.lastIndex) {
                            rewrites.set(iop.instructionIndex, null);
                        }
                    }
                    final List prevReplaces = this.getKindOfOps(rewrites, ReplaceOp.class, i);
                    for (int k = 0; k < prevReplaces.size(); ++k) {
                        final ReplaceOp prevRop = prevReplaces.get(k);
                        if (prevRop.index >= rop.index && prevRop.lastIndex <= rop.lastIndex) {
                            rewrites.set(prevRop.instructionIndex, null);
                        }
                        else {
                            final boolean disjoint = prevRop.lastIndex < rop.index || prevRop.index > rop.lastIndex;
                            final boolean same = prevRop.index == rop.index && prevRop.lastIndex == rop.lastIndex;
                            if (prevRop.text == null && rop.text == null && !disjoint) {
                                rewrites.set(prevRop.instructionIndex, null);
                                rop.index = Math.min(prevRop.index, rop.index);
                                rop.lastIndex = Math.max(prevRop.lastIndex, rop.lastIndex);
                                System.out.println("new rop " + rop);
                            }
                            else if (!disjoint && !same) {
                                throw new IllegalArgumentException("replace op boundaries of " + rop + " overlap with previous " + prevRop);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < rewrites.size(); ++i) {
            final RewriteOperation op = rewrites.get(i);
            if (op != null) {
                if (op instanceof InsertBeforeOp) {
                    final InsertBeforeOp iop2 = rewrites.get(i);
                    final List prevInserts = this.getKindOfOps(rewrites, InsertBeforeOp.class, i);
                    for (int j = 0; j < prevInserts.size(); ++j) {
                        final InsertBeforeOp prevIop = prevInserts.get(j);
                        if (prevIop.index == iop2.index) {
                            iop2.text = this.catOpText(iop2.text, prevIop.text);
                            rewrites.set(prevIop.instructionIndex, null);
                        }
                    }
                    final List prevReplaces = this.getKindOfOps(rewrites, ReplaceOp.class, i);
                    for (int k = 0; k < prevReplaces.size(); ++k) {
                        final ReplaceOp rop2 = prevReplaces.get(k);
                        if (iop2.index == rop2.index) {
                            rop2.text = this.catOpText(iop2.text, rop2.text);
                            rewrites.set(i, null);
                        }
                        else if (iop2.index >= rop2.index && iop2.index <= rop2.lastIndex) {
                            throw new IllegalArgumentException("insert op " + iop2 + " within boundaries of previous " + rop2);
                        }
                    }
                }
            }
        }
        final Map m = new HashMap();
        for (int l = 0; l < rewrites.size(); ++l) {
            final RewriteOperation op2 = rewrites.get(l);
            if (op2 != null) {
                if (m.get(new Integer(op2.index)) != null) {
                    throw new Error("should only be one op per index");
                }
                m.put(new Integer(op2.index), op2);
            }
        }
        return m;
    }
    
    protected String catOpText(final Object a, final Object b) {
        String x = "";
        String y = "";
        if (a != null) {
            x = a.toString();
        }
        if (b != null) {
            y = b.toString();
        }
        return x + y;
    }
    
    protected List getKindOfOps(final List rewrites, final Class kind) {
        return this.getKindOfOps(rewrites, kind, rewrites.size());
    }
    
    protected List getKindOfOps(final List rewrites, final Class kind, final int before) {
        final List ops = new ArrayList();
        for (int i = 0; i < before && i < rewrites.size(); ++i) {
            final RewriteOperation op = rewrites.get(i);
            if (op != null) {
                if (op.getClass() == kind) {
                    ops.add(op);
                }
            }
        }
        return ops;
    }
    
    public String toDebugString() {
        return this.toDebugString(0, this.size() - 1);
    }
    
    public String toDebugString(final int start, final int end) {
        final StringBuffer buf = new StringBuffer();
        for (int i = start; i >= 0 && i <= end && i < this.tokens.size(); ++i) {
            buf.append(this.get(i));
        }
        return buf.toString();
    }
    
    class RewriteOperation
    {
        protected int instructionIndex;
        protected int index;
        protected Object text;
        
        protected RewriteOperation(final int index) {
            this.index = index;
        }
        
        protected RewriteOperation(final int index, final Object text) {
            this.index = index;
            this.text = text;
        }
        
        public int execute(final StringBuffer buf) {
            return this.index;
        }
        
        public String toString() {
            String opName = this.getClass().getName();
            final int $index = opName.indexOf(36);
            opName = opName.substring($index + 1, opName.length());
            return "<" + opName + "@" + TokenRewriteStream.this.tokens.get(this.index) + ":\"" + this.text + "\">";
        }
    }
    
    class InsertBeforeOp extends RewriteOperation
    {
        public InsertBeforeOp(final int index, final Object text) {
            super(index, text);
        }
        
        public int execute(final StringBuffer buf) {
            buf.append(this.text);
            if (TokenRewriteStream.this.tokens.get(this.index).getType() != -1) {
                buf.append(TokenRewriteStream.this.tokens.get(this.index).getText());
            }
            return this.index + 1;
        }
    }
    
    class ReplaceOp extends RewriteOperation
    {
        protected int lastIndex;
        
        public ReplaceOp(final int from, final int to, final Object text) {
            super(from, text);
            this.lastIndex = to;
        }
        
        public int execute(final StringBuffer buf) {
            if (this.text != null) {
                buf.append(this.text);
            }
            return this.lastIndex + 1;
        }
        
        public String toString() {
            if (this.text == null) {
                return "<DeleteOp@" + TokenRewriteStream.this.tokens.get(this.index) + ".." + TokenRewriteStream.this.tokens.get(this.lastIndex) + ">";
            }
            return "<ReplaceOp@" + TokenRewriteStream.this.tokens.get(this.index) + ".." + TokenRewriteStream.this.tokens.get(this.lastIndex) + ":\"" + this.text + "\">";
        }
    }
}
