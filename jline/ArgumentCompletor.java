// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.LinkedList;
import java.util.List;

public class ArgumentCompletor implements Completor
{
    final Completor[] completors;
    final ArgumentDelimiter delim;
    boolean strict;
    
    public ArgumentCompletor(final Completor completor) {
        this(new Completor[] { completor });
    }
    
    public ArgumentCompletor(final List completors) {
        this(completors.toArray(new Completor[completors.size()]));
    }
    
    public ArgumentCompletor(final Completor[] completors) {
        this(completors, new WhitespaceArgumentDelimiter());
    }
    
    public ArgumentCompletor(final Completor completor, final ArgumentDelimiter delim) {
        this(new Completor[] { completor }, delim);
    }
    
    public ArgumentCompletor(final Completor[] completors, final ArgumentDelimiter delim) {
        this.strict = true;
        this.completors = completors;
        this.delim = delim;
    }
    
    public void setStrict(final boolean strict) {
        this.strict = strict;
    }
    
    public boolean getStrict() {
        return this.strict;
    }
    
    public int complete(final String buffer, final int cursor, final List candidates) {
        final ArgumentList list = this.delim.delimit(buffer, cursor);
        final int argpos = list.getArgumentPosition();
        final int argIndex = list.getCursorArgumentIndex();
        if (argIndex < 0) {
            return -1;
        }
        Completor comp;
        if (argIndex >= this.completors.length) {
            comp = this.completors[this.completors.length - 1];
        }
        else {
            comp = this.completors[argIndex];
        }
        for (int i = 0; this.getStrict() && i < argIndex; ++i) {
            final Completor sub = this.completors[(i >= this.completors.length) ? (this.completors.length - 1) : i];
            final String[] args = list.getArguments();
            final String arg = (args == null || i >= args.length) ? "" : args[i];
            final List subCandidates = new LinkedList();
            if (sub.complete(arg, arg.length(), subCandidates) == -1) {
                return -1;
            }
            if (subCandidates.size() == 0) {
                return -1;
            }
        }
        final int ret = comp.complete(list.getCursorArgument(), argpos, candidates);
        if (ret == -1) {
            return -1;
        }
        final int pos = ret + (list.getBufferPosition() - argpos);
        if (cursor != buffer.length() && this.delim.isDelimiter(buffer, cursor)) {
            for (int j = 0; j < candidates.size(); ++j) {
                String val;
                for (val = candidates.get(j).toString(); val.length() > 0 && this.delim.isDelimiter(val, val.length() - 1); val = val.substring(0, val.length() - 1)) {}
                candidates.set(j, val);
            }
        }
        ConsoleReader.debug("Completing " + buffer + "(pos=" + cursor + ") " + "with: " + candidates + ": offset=" + pos);
        return pos;
    }
    
    public abstract static class AbstractArgumentDelimiter implements ArgumentDelimiter
    {
        private char[] quoteChars;
        private char[] escapeChars;
        
        public AbstractArgumentDelimiter() {
            this.quoteChars = new char[] { '\'', '\"' };
            this.escapeChars = new char[] { '\\' };
        }
        
        public void setQuoteChars(final char[] quoteChars) {
            this.quoteChars = quoteChars;
        }
        
        public char[] getQuoteChars() {
            return this.quoteChars;
        }
        
        public void setEscapeChars(final char[] escapeChars) {
            this.escapeChars = escapeChars;
        }
        
        public char[] getEscapeChars() {
            return this.escapeChars;
        }
        
        public ArgumentList delimit(final String buffer, final int cursor) {
            final List args = new LinkedList();
            final StringBuffer arg = new StringBuffer();
            int argpos = -1;
            int bindex = -1;
            for (int i = 0; buffer != null && i <= buffer.length(); ++i) {
                if (i == cursor) {
                    bindex = args.size();
                    argpos = arg.length();
                }
                if (i == buffer.length() || this.isDelimiter(buffer, i)) {
                    if (arg.length() > 0) {
                        args.add(arg.toString());
                        arg.setLength(0);
                    }
                }
                else {
                    arg.append(buffer.charAt(i));
                }
            }
            return new ArgumentList(args.toArray(new String[args.size()]), bindex, argpos, cursor);
        }
        
        public boolean isDelimiter(final String buffer, final int pos) {
            return !this.isQuoted(buffer, pos) && !this.isEscaped(buffer, pos) && this.isDelimiterChar(buffer, pos);
        }
        
        public boolean isQuoted(final String buffer, final int pos) {
            return false;
        }
        
        public boolean isEscaped(final String buffer, final int pos) {
            if (pos <= 0) {
                return false;
            }
            for (int i = 0; this.escapeChars != null && i < this.escapeChars.length; ++i) {
                if (buffer.charAt(pos) == this.escapeChars[i]) {
                    return !this.isEscaped(buffer, pos - 1);
                }
            }
            return false;
        }
        
        public abstract boolean isDelimiterChar(final String p0, final int p1);
    }
    
    public static class WhitespaceArgumentDelimiter extends AbstractArgumentDelimiter
    {
        public boolean isDelimiterChar(final String buffer, final int pos) {
            return Character.isWhitespace(buffer.charAt(pos));
        }
    }
    
    public static class ArgumentList
    {
        private String[] arguments;
        private int cursorArgumentIndex;
        private int argumentPosition;
        private int bufferPosition;
        
        public ArgumentList(final String[] arguments, final int cursorArgumentIndex, final int argumentPosition, final int bufferPosition) {
            this.arguments = arguments;
            this.cursorArgumentIndex = cursorArgumentIndex;
            this.argumentPosition = argumentPosition;
            this.bufferPosition = bufferPosition;
        }
        
        public void setCursorArgumentIndex(final int cursorArgumentIndex) {
            this.cursorArgumentIndex = cursorArgumentIndex;
        }
        
        public int getCursorArgumentIndex() {
            return this.cursorArgumentIndex;
        }
        
        public String getCursorArgument() {
            if (this.cursorArgumentIndex < 0 || this.cursorArgumentIndex >= this.arguments.length) {
                return null;
            }
            return this.arguments[this.cursorArgumentIndex];
        }
        
        public void setArgumentPosition(final int argumentPosition) {
            this.argumentPosition = argumentPosition;
        }
        
        public int getArgumentPosition() {
            return this.argumentPosition;
        }
        
        public void setArguments(final String[] arguments) {
            this.arguments = arguments;
        }
        
        public String[] getArguments() {
            return this.arguments;
        }
        
        public void setBufferPosition(final int bufferPosition) {
            this.bufferPosition = bufferPosition;
        }
        
        public int getBufferPosition() {
            return this.bufferPosition;
        }
    }
    
    public interface ArgumentDelimiter
    {
        ArgumentList delimit(final String p0, final int p1);
        
        boolean isDelimiter(final String p0, final int p1);
    }
}
