// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.ASdebug.TokenOffsetInfo;
import antlr.ASdebug.ASDebugStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import antlr.collections.impl.BitSet;
import java.util.Map;
import java.util.List;
import antlr.ASdebug.IASDebugStream;

public class TokenStreamRewriteEngine implements TokenStream, IASDebugStream
{
    public static final int MIN_TOKEN_INDEX = 0;
    public static final String DEFAULT_PROGRAM_NAME = "default";
    public static final int PROGRAM_INIT_SIZE = 100;
    protected List tokens;
    protected Map programs;
    protected Map lastRewriteTokenIndexes;
    protected int index;
    protected TokenStream stream;
    protected BitSet discardMask;
    
    public TokenStreamRewriteEngine(final TokenStream tokenStream) {
        this(tokenStream, 1000);
    }
    
    public TokenStreamRewriteEngine(final TokenStream stream, final int initialCapacity) {
        this.programs = null;
        this.lastRewriteTokenIndexes = null;
        this.index = 0;
        this.discardMask = new BitSet();
        this.stream = stream;
        this.tokens = new ArrayList(initialCapacity);
        (this.programs = new HashMap()).put("default", new ArrayList(100));
        this.lastRewriteTokenIndexes = new HashMap();
    }
    
    public Token nextToken() throws TokenStreamException {
        TokenWithIndex tokenWithIndex;
        do {
            tokenWithIndex = (TokenWithIndex)this.stream.nextToken();
            if (tokenWithIndex != null) {
                tokenWithIndex.setIndex(this.index);
                if (tokenWithIndex.getType() != 1) {
                    this.tokens.add(tokenWithIndex);
                }
                ++this.index;
            }
        } while (tokenWithIndex != null && this.discardMask.member(tokenWithIndex.getType()));
        return tokenWithIndex;
    }
    
    public void rollback(final int n) {
        this.rollback("default", n);
    }
    
    public void rollback(final String s, final int n) {
        final List list = this.programs.get(s);
        if (list != null) {
            this.programs.put(s, list.subList(0, n));
        }
    }
    
    public void deleteProgram() {
        this.deleteProgram("default");
    }
    
    public void deleteProgram(final String s) {
        this.rollback(s, 0);
    }
    
    protected void addToSortedRewriteList(final RewriteOperation rewriteOperation) {
        this.addToSortedRewriteList("default", rewriteOperation);
    }
    
    protected void addToSortedRewriteList(final String s, final RewriteOperation key) {
        final List program = this.getProgram(s);
        int binarySearch = Collections.binarySearch(program, key, new Comparator() {
            public int compare(final Object o, final Object o2) {
                final RewriteOperation rewriteOperation = (RewriteOperation)o;
                final RewriteOperation rewriteOperation2 = (RewriteOperation)o2;
                if (rewriteOperation.index < rewriteOperation2.index) {
                    return -1;
                }
                if (rewriteOperation.index > rewriteOperation2.index) {
                    return 1;
                }
                return 0;
            }
        });
        if (binarySearch >= 0) {
            while (binarySearch >= 0 && ((RewriteOperation)program.get(binarySearch)).index >= key.index) {
                --binarySearch;
            }
            ++binarySearch;
            if (key instanceof ReplaceOp) {
                boolean b = false;
                int i;
                for (i = binarySearch; i < program.size(); ++i) {
                    final RewriteOperation rewriteOperation = program.get(binarySearch);
                    if (rewriteOperation.index != key.index) {
                        break;
                    }
                    if (rewriteOperation instanceof ReplaceOp) {
                        program.set(binarySearch, key);
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    program.add(i, key);
                }
            }
            else {
                program.add(binarySearch, key);
            }
        }
        else {
            program.add(-binarySearch - 1, key);
        }
    }
    
    public void insertAfter(final Token token, final String s) {
        this.insertAfter("default", token, s);
    }
    
    public void insertAfter(final int n, final String s) {
        this.insertAfter("default", n, s);
    }
    
    public void insertAfter(final String s, final Token token, final String s2) {
        this.insertAfter(s, ((TokenWithIndex)token).getIndex(), s2);
    }
    
    public void insertAfter(final String s, final int n, final String s2) {
        this.insertBefore(s, n + 1, s2);
    }
    
    public void insertBefore(final Token token, final String s) {
        this.insertBefore("default", token, s);
    }
    
    public void insertBefore(final int n, final String s) {
        this.insertBefore("default", n, s);
    }
    
    public void insertBefore(final String s, final Token token, final String s2) {
        this.insertBefore(s, ((TokenWithIndex)token).getIndex(), s2);
    }
    
    public void insertBefore(final String s, final int n, final String s2) {
        this.addToSortedRewriteList(s, new InsertBeforeOp(n, s2));
    }
    
    public void replace(final int n, final String s) {
        this.replace("default", n, n, s);
    }
    
    public void replace(final int n, final int n2, final String s) {
        this.replace("default", n, n2, s);
    }
    
    public void replace(final Token token, final String s) {
        this.replace("default", token, token, s);
    }
    
    public void replace(final Token token, final Token token2, final String s) {
        this.replace("default", token, token2, s);
    }
    
    public void replace(final String s, final int n, final int n2, final String s2) {
        this.addToSortedRewriteList(new ReplaceOp(n, n2, s2));
    }
    
    public void replace(final String s, final Token token, final Token token2, final String s2) {
        this.replace(s, ((TokenWithIndex)token).getIndex(), ((TokenWithIndex)token2).getIndex(), s2);
    }
    
    public void delete(final int n) {
        this.delete("default", n, n);
    }
    
    public void delete(final int n, final int n2) {
        this.delete("default", n, n2);
    }
    
    public void delete(final Token token) {
        this.delete("default", token, token);
    }
    
    public void delete(final Token token, final Token token2) {
        this.delete("default", token, token2);
    }
    
    public void delete(final String s, final int n, final int n2) {
        this.replace(s, n, n2, null);
    }
    
    public void delete(final String s, final Token token, final Token token2) {
        this.replace(s, token, token2, null);
    }
    
    public void discard(final int n) {
        this.discardMask.add(n);
    }
    
    public TokenWithIndex getToken(final int n) {
        return this.tokens.get(n);
    }
    
    public int getTokenStreamSize() {
        return this.tokens.size();
    }
    
    public String toOriginalString() {
        return this.toOriginalString(0, this.getTokenStreamSize() - 1);
    }
    
    public String toOriginalString(final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        for (int n3 = n; n3 >= 0 && n3 <= n2 && n3 < this.tokens.size(); ++n3) {
            sb.append(this.getToken(n3).getText());
        }
        return sb.toString();
    }
    
    public String toString() {
        return this.toString(0, this.getTokenStreamSize() - 1);
    }
    
    public String toString(final String s) {
        return this.toString(s, 0, this.getTokenStreamSize() - 1);
    }
    
    public String toString(final int n, final int n2) {
        return this.toString("default", n, n2);
    }
    
    public String toString(final String s, final int n, final int n2) {
        final List<RewriteOperation> list = this.programs.get(s);
        if (list == null || list.size() == 0) {
            return this.toOriginalString(n, n2);
        }
        final StringBuffer sb = new StringBuffer();
        int n3 = 0;
        for (int execute = n; execute >= 0 && execute <= n2 && execute < this.tokens.size(); ++execute) {
            if (n3 < list.size()) {
                RewriteOperation rewriteOperation;
                for (rewriteOperation = list.get(n3); rewriteOperation.index < execute && n3 < list.size(); rewriteOperation = list.get(n3)) {
                    if (++n3 < list.size()) {}
                }
                while (execute == rewriteOperation.index && n3 < list.size()) {
                    execute = rewriteOperation.execute(sb);
                    if (++n3 < list.size()) {
                        rewriteOperation = list.get(n3);
                    }
                }
            }
            if (execute <= n2) {
                sb.append(this.getToken(execute).getText());
            }
        }
        for (int i = n3; i < list.size(); ++i) {
            final RewriteOperation rewriteOperation2 = list.get(i);
            if (rewriteOperation2.index >= this.size()) {
                rewriteOperation2.execute(sb);
            }
        }
        return sb.toString();
    }
    
    public String toDebugString() {
        return this.toDebugString(0, this.getTokenStreamSize() - 1);
    }
    
    public String toDebugString(final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        for (int n3 = n; n3 >= 0 && n3 <= n2 && n3 < this.tokens.size(); ++n3) {
            sb.append(this.getToken(n3));
        }
        return sb.toString();
    }
    
    public int getLastRewriteTokenIndex() {
        return this.getLastRewriteTokenIndex("default");
    }
    
    protected int getLastRewriteTokenIndex(final String s) {
        final Integer n = this.lastRewriteTokenIndexes.get(s);
        if (n == null) {
            return -1;
        }
        return n;
    }
    
    protected void setLastRewriteTokenIndex(final String s, final int value) {
        this.lastRewriteTokenIndexes.put(s, new Integer(value));
    }
    
    protected List getProgram(final String s) {
        List initializeProgram = this.programs.get(s);
        if (initializeProgram == null) {
            initializeProgram = this.initializeProgram(s);
        }
        return initializeProgram;
    }
    
    private List initializeProgram(final String s) {
        final ArrayList list = new ArrayList(100);
        this.programs.put(s, list);
        return list;
    }
    
    public int size() {
        return this.tokens.size();
    }
    
    public int index() {
        return this.index;
    }
    
    public String getEntireText() {
        return ASDebugStream.getEntireText(this.stream);
    }
    
    public TokenOffsetInfo getOffsetInfo(final Token token) {
        return ASDebugStream.getOffsetInfo(this.stream, token);
    }
    
    static class RewriteOperation
    {
        protected int index;
        protected String text;
        
        protected RewriteOperation(final int index, final String text) {
            this.index = index;
            this.text = text;
        }
        
        public int execute(final StringBuffer sb) {
            return this.index;
        }
        
        public String toString() {
            final String name = this.getClass().getName();
            return name.substring(name.indexOf(36) + 1, name.length()) + "@" + this.index + '\"' + this.text + '\"';
        }
    }
    
    static class InsertBeforeOp extends RewriteOperation
    {
        public InsertBeforeOp(final int n, final String s) {
            super(n, s);
        }
        
        public int execute(final StringBuffer sb) {
            sb.append(this.text);
            return this.index;
        }
    }
    
    static class ReplaceOp extends RewriteOperation
    {
        protected int lastIndex;
        
        public ReplaceOp(final int n, final int lastIndex, final String s) {
            super(n, s);
            this.lastIndex = lastIndex;
        }
        
        public int execute(final StringBuffer sb) {
            if (this.text != null) {
                sb.append(this.text);
            }
            return this.lastIndex + 1;
        }
    }
    
    static class DeleteOp extends ReplaceOp
    {
        public DeleteOp(final int n, final int n2) {
            super(n, n2, null);
        }
    }
}
