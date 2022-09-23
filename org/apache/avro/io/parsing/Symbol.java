// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io.parsing;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.avro.Schema;
import java.util.List;
import java.util.Map;

public abstract class Symbol
{
    public final Kind kind;
    public final Symbol[] production;
    public static final Symbol NULL;
    public static final Symbol BOOLEAN;
    public static final Symbol INT;
    public static final Symbol LONG;
    public static final Symbol FLOAT;
    public static final Symbol DOUBLE;
    public static final Symbol STRING;
    public static final Symbol BYTES;
    public static final Symbol FIXED;
    public static final Symbol ENUM;
    public static final Symbol UNION;
    public static final Symbol ARRAY_START;
    public static final Symbol ARRAY_END;
    public static final Symbol MAP_START;
    public static final Symbol MAP_END;
    public static final Symbol ITEM_END;
    public static final Symbol FIELD_ACTION;
    public static final Symbol RECORD_START;
    public static final Symbol RECORD_END;
    public static final Symbol UNION_END;
    public static final Symbol FIELD_END;
    public static final Symbol DEFAULT_END_ACTION;
    public static final Symbol MAP_KEY_MARKER;
    
    protected Symbol(final Kind kind) {
        this(kind, null);
    }
    
    protected Symbol(final Kind kind, final Symbol[] production) {
        this.production = production;
        this.kind = kind;
    }
    
    static Symbol root(final Symbol... symbols) {
        return new Root(symbols);
    }
    
    static Symbol seq(final Symbol... production) {
        return new Sequence(production);
    }
    
    static Symbol repeat(final Symbol endSymbol, final Symbol... symsToRepeat) {
        return new Repeater(endSymbol, symsToRepeat);
    }
    
    static Symbol alt(final Symbol[] symbols, final String[] labels) {
        return new Alternative(symbols, labels);
    }
    
    static Symbol error(final String e) {
        return new ErrorAction(e);
    }
    
    static Symbol resolve(final Symbol w, final Symbol r) {
        return new ResolvingAction(w, r);
    }
    
    public Symbol flatten(final Map<Sequence, Sequence> map, final Map<Sequence, List<Fixup>> map2) {
        return this;
    }
    
    public int flattenedSize() {
        return 1;
    }
    
    static void flatten(final Symbol[] in, final int start, final Symbol[] out, final int skip, final Map<Sequence, Sequence> map, final Map<Sequence, List<Fixup>> map2) {
        int i = start;
        int j = skip;
        while (i < in.length) {
            final Symbol s = in[i].flatten(map, map2);
            if (s instanceof Sequence) {
                final Symbol[] p = s.production;
                final List<Fixup> l = map2.get(s);
                if (l == null) {
                    System.arraycopy(p, 0, out, j, p.length);
                }
                else {
                    l.add(new Fixup(out, j));
                }
                j += p.length;
            }
            else {
                out[j++] = s;
            }
            ++i;
        }
    }
    
    protected static int flattenedSize(final Symbol[] symbols, final int start) {
        int result = 0;
        for (int i = start; i < symbols.length; ++i) {
            if (symbols[i] instanceof Sequence) {
                final Sequence s = (Sequence)symbols[i];
                result += s.flattenedSize();
            }
            else {
                ++result;
            }
        }
        return result;
    }
    
    public static boolean hasErrors(final Symbol symbol) {
        switch (symbol.kind) {
            case ALTERNATIVE: {
                return hasErrors(symbol, ((Alternative)symbol).symbols);
            }
            case EXPLICIT_ACTION: {
                return false;
            }
            case IMPLICIT_ACTION: {
                return symbol instanceof ErrorAction;
            }
            case REPEATER: {
                final Repeater r = (Repeater)symbol;
                return hasErrors(r.end) || hasErrors(symbol, r.production);
            }
            case ROOT:
            case SEQUENCE: {
                return hasErrors(symbol, symbol.production);
            }
            case TERMINAL: {
                return false;
            }
            default: {
                throw new RuntimeException("unknown symbol kind: " + symbol.kind);
            }
        }
    }
    
    private static boolean hasErrors(final Symbol root, final Symbol[] symbols) {
        if (null != symbols) {
            for (final Symbol s : symbols) {
                if (s != root) {
                    if (hasErrors(s)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static IntCheckAction intCheckAction(final int size) {
        return new IntCheckAction(size);
    }
    
    public static EnumAdjustAction enumAdjustAction(final int rsymCount, final Object[] adj) {
        return new EnumAdjustAction(rsymCount, adj);
    }
    
    public static WriterUnionAction writerUnionAction() {
        return new WriterUnionAction();
    }
    
    public static SkipAction skipAction(final Symbol symToSkip) {
        return new SkipAction(symToSkip);
    }
    
    public static FieldAdjustAction fieldAdjustAction(final int rindex, final String fname) {
        return new FieldAdjustAction(rindex, fname);
    }
    
    public static FieldOrderAction fieldOrderAction(final Schema.Field[] fields) {
        return new FieldOrderAction(fields);
    }
    
    public static DefaultStartAction defaultStartAction(final byte[] contents) {
        return new DefaultStartAction(contents);
    }
    
    public static UnionAdjustAction unionAdjustAction(final int rindex, final Symbol sym) {
        return new UnionAdjustAction(rindex, sym);
    }
    
    public static EnumLabelsAction enumLabelsAction(final List<String> symbols) {
        return new EnumLabelsAction(symbols);
    }
    
    static {
        NULL = new Terminal("null");
        BOOLEAN = new Terminal("boolean");
        INT = new Terminal("int");
        LONG = new Terminal("long");
        FLOAT = new Terminal("float");
        DOUBLE = new Terminal("double");
        STRING = new Terminal("string");
        BYTES = new Terminal("bytes");
        FIXED = new Terminal("fixed");
        ENUM = new Terminal("enum");
        UNION = new Terminal("union");
        ARRAY_START = new Terminal("array-start");
        ARRAY_END = new Terminal("array-end");
        MAP_START = new Terminal("map-start");
        MAP_END = new Terminal("map-end");
        ITEM_END = new Terminal("item-end");
        FIELD_ACTION = new Terminal("field-action");
        RECORD_START = new ImplicitAction(false);
        RECORD_END = new ImplicitAction(true);
        UNION_END = new ImplicitAction(true);
        FIELD_END = new ImplicitAction(true);
        DEFAULT_END_ACTION = new ImplicitAction(true);
        MAP_KEY_MARKER = new Terminal("map-key-marker");
    }
    
    public enum Kind
    {
        TERMINAL, 
        ROOT, 
        SEQUENCE, 
        REPEATER, 
        ALTERNATIVE, 
        IMPLICIT_ACTION, 
        EXPLICIT_ACTION;
    }
    
    private static class Fixup
    {
        public final Symbol[] symbols;
        public final int pos;
        
        public Fixup(final Symbol[] symbols, final int pos) {
            this.symbols = symbols;
            this.pos = pos;
        }
    }
    
    private static class Terminal extends Symbol
    {
        private final String printName;
        
        public Terminal(final String printName) {
            super(Kind.TERMINAL);
            this.printName = printName;
        }
        
        @Override
        public String toString() {
            return this.printName;
        }
    }
    
    public static class ImplicitAction extends Symbol
    {
        public final boolean isTrailing;
        
        private ImplicitAction() {
            this(false);
        }
        
        private ImplicitAction(final boolean isTrailing) {
            super(Kind.IMPLICIT_ACTION);
            this.isTrailing = isTrailing;
        }
    }
    
    protected static class Root extends Symbol
    {
        private Root(final Symbol... symbols) {
            super(Kind.ROOT, makeProduction(symbols));
            this.production[0] = this;
        }
        
        private static Symbol[] makeProduction(final Symbol[] symbols) {
            final Symbol[] result = new Symbol[Symbol.flattenedSize(symbols, 0) + 1];
            Symbol.flatten(symbols, 0, result, 1, new HashMap<Sequence, Sequence>(), new HashMap<Sequence, List<Fixup>>());
            return result;
        }
    }
    
    protected static class Sequence extends Symbol implements Iterable<Symbol>
    {
        private Sequence(final Symbol[] productions) {
            super(Kind.SEQUENCE, productions);
        }
        
        public Symbol get(final int index) {
            return this.production[index];
        }
        
        public int size() {
            return this.production.length;
        }
        
        @Override
        public Iterator<Symbol> iterator() {
            return new Iterator<Symbol>() {
                private int pos = Sequence.this.production.length;
                
                @Override
                public boolean hasNext() {
                    return 0 < this.pos;
                }
                
                @Override
                public Symbol next() {
                    if (0 < this.pos) {
                        final Symbol[] production = Sequence.this.production;
                        final int pos = this.pos - 1;
                        this.pos = pos;
                        return production[pos];
                    }
                    throw new NoSuchElementException();
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        @Override
        public Sequence flatten(final Map<Sequence, Sequence> map, final Map<Sequence, List<Fixup>> map2) {
            Sequence result = map.get(this);
            if (result == null) {
                result = new Sequence(new Symbol[this.flattenedSize()]);
                map.put(this, result);
                final List<Fixup> l = new ArrayList<Fixup>();
                map2.put(result, l);
                Symbol.flatten(this.production, 0, result.production, 0, map, map2);
                for (final Fixup f : l) {
                    System.arraycopy(result.production, 0, f.symbols, f.pos, result.production.length);
                }
                map2.remove(result);
            }
            return result;
        }
        
        @Override
        public final int flattenedSize() {
            return Symbol.flattenedSize(this.production, 0);
        }
    }
    
    public static class Repeater extends Symbol
    {
        public final Symbol end;
        
        private Repeater(final Symbol end, final Symbol... sequenceToRepeat) {
            super(Kind.REPEATER, makeProduction(sequenceToRepeat));
            this.end = end;
            this.production[0] = this;
        }
        
        private static Symbol[] makeProduction(final Symbol[] p) {
            final Symbol[] result = new Symbol[p.length + 1];
            System.arraycopy(p, 0, result, 1, p.length);
            return result;
        }
        
        @Override
        public Repeater flatten(final Map<Sequence, Sequence> map, final Map<Sequence, List<Fixup>> map2) {
            final Repeater result = new Repeater(this.end, new Symbol[Symbol.flattenedSize(this.production, 1)]);
            Symbol.flatten(this.production, 1, result.production, 1, map, map2);
            return result;
        }
    }
    
    public static class Alternative extends Symbol
    {
        public final Symbol[] symbols;
        public final String[] labels;
        
        private Alternative(final Symbol[] symbols, final String[] labels) {
            super(Kind.ALTERNATIVE);
            this.symbols = symbols;
            this.labels = labels;
        }
        
        public Symbol getSymbol(final int index) {
            return this.symbols[index];
        }
        
        public String getLabel(final int index) {
            return this.labels[index];
        }
        
        public int size() {
            return this.symbols.length;
        }
        
        public int findLabel(final String label) {
            if (label != null) {
                for (int i = 0; i < this.labels.length; ++i) {
                    if (label.equals(this.labels[i])) {
                        return i;
                    }
                }
            }
            return -1;
        }
        
        @Override
        public Alternative flatten(final Map<Sequence, Sequence> map, final Map<Sequence, List<Fixup>> map2) {
            final Symbol[] ss = new Symbol[this.symbols.length];
            for (int i = 0; i < ss.length; ++i) {
                ss[i] = this.symbols[i].flatten(map, map2);
            }
            return new Alternative(ss, this.labels);
        }
    }
    
    public static class ErrorAction extends ImplicitAction
    {
        public final String msg;
        
        private ErrorAction(final String msg) {
            this.msg = msg;
        }
    }
    
    public static class IntCheckAction extends Symbol
    {
        public final int size;
        
        @Deprecated
        public IntCheckAction(final int size) {
            super(Kind.EXPLICIT_ACTION);
            this.size = size;
        }
    }
    
    public static class EnumAdjustAction extends IntCheckAction
    {
        public final Object[] adjustments;
        
        @Deprecated
        public EnumAdjustAction(final int rsymCount, final Object[] adjustments) {
            super(rsymCount);
            this.adjustments = adjustments;
        }
    }
    
    public static class WriterUnionAction extends ImplicitAction
    {
        private WriterUnionAction() {
        }
    }
    
    public static class ResolvingAction extends ImplicitAction
    {
        public final Symbol writer;
        public final Symbol reader;
        
        private ResolvingAction(final Symbol writer, final Symbol reader) {
            this.writer = writer;
            this.reader = reader;
        }
        
        @Override
        public ResolvingAction flatten(final Map<Sequence, Sequence> map, final Map<Sequence, List<Fixup>> map2) {
            return new ResolvingAction(this.writer.flatten(map, map2), this.reader.flatten(map, map2));
        }
    }
    
    public static class SkipAction extends ImplicitAction
    {
        public final Symbol symToSkip;
        
        @Deprecated
        public SkipAction(final Symbol symToSkip) {
            super(true);
            this.symToSkip = symToSkip;
        }
        
        @Override
        public SkipAction flatten(final Map<Sequence, Sequence> map, final Map<Sequence, List<Fixup>> map2) {
            return new SkipAction(this.symToSkip.flatten(map, map2));
        }
    }
    
    public static class FieldAdjustAction extends ImplicitAction
    {
        public final int rindex;
        public final String fname;
        
        @Deprecated
        public FieldAdjustAction(final int rindex, final String fname) {
            this.rindex = rindex;
            this.fname = fname;
        }
    }
    
    public static final class FieldOrderAction extends ImplicitAction
    {
        public final Schema.Field[] fields;
        
        @Deprecated
        public FieldOrderAction(final Schema.Field[] fields) {
            this.fields = fields;
        }
    }
    
    public static class DefaultStartAction extends ImplicitAction
    {
        public final byte[] contents;
        
        @Deprecated
        public DefaultStartAction(final byte[] contents) {
            this.contents = contents;
        }
    }
    
    public static class UnionAdjustAction extends ImplicitAction
    {
        public final int rindex;
        public final Symbol symToParse;
        
        @Deprecated
        public UnionAdjustAction(final int rindex, final Symbol symToParse) {
            this.rindex = rindex;
            this.symToParse = symToParse;
        }
        
        @Override
        public UnionAdjustAction flatten(final Map<Sequence, Sequence> map, final Map<Sequence, List<Fixup>> map2) {
            return new UnionAdjustAction(this.rindex, this.symToParse.flatten(map, map2));
        }
    }
    
    public static class EnumLabelsAction extends IntCheckAction
    {
        public final List<String> symbols;
        
        @Deprecated
        public EnumLabelsAction(final List<String> symbols) {
            super(symbols.size());
            this.symbols = symbols;
        }
        
        public String getLabel(final int n) {
            return this.symbols.get(n);
        }
        
        public int findLabel(final String l) {
            if (l != null) {
                for (int i = 0; i < this.symbols.size(); ++i) {
                    if (l.equals(this.symbols.get(i))) {
                        return i;
                    }
                }
            }
            return -1;
        }
    }
}
