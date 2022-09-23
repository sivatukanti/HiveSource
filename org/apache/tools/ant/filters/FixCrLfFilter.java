// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.filters;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.BuildException;
import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.taskdefs.condition.Os;

public final class FixCrLfFilter extends BaseParamFilterReader implements ChainableReader
{
    private static final int DEFAULT_TAB_LENGTH = 8;
    private static final int MIN_TAB_LENGTH = 2;
    private static final int MAX_TAB_LENGTH = 80;
    private static final char CTRLZ = '\u001a';
    private int tabLength;
    private CrLf eol;
    private AddAsisRemove ctrlz;
    private AddAsisRemove tabs;
    private boolean javafiles;
    private boolean fixlast;
    private boolean initialized;
    
    public FixCrLfFilter() {
        this.tabLength = 8;
        this.javafiles = false;
        this.fixlast = true;
        this.initialized = false;
        this.tabs = AddAsisRemove.ASIS;
        if (Os.isFamily("mac") && !Os.isFamily("unix")) {
            this.ctrlz = AddAsisRemove.REMOVE;
            this.setEol(CrLf.MAC);
        }
        else if (Os.isFamily("dos")) {
            this.ctrlz = AddAsisRemove.ASIS;
            this.setEol(CrLf.DOS);
        }
        else {
            this.ctrlz = AddAsisRemove.REMOVE;
            this.setEol(CrLf.UNIX);
        }
    }
    
    public FixCrLfFilter(final Reader in) throws IOException {
        super(in);
        this.tabLength = 8;
        this.javafiles = false;
        this.fixlast = true;
        this.initialized = false;
        this.tabs = AddAsisRemove.ASIS;
        if (Os.isFamily("mac") && !Os.isFamily("unix")) {
            this.ctrlz = AddAsisRemove.REMOVE;
            this.setEol(CrLf.MAC);
        }
        else if (Os.isFamily("dos")) {
            this.ctrlz = AddAsisRemove.ASIS;
            this.setEol(CrLf.DOS);
        }
        else {
            this.ctrlz = AddAsisRemove.REMOVE;
            this.setEol(CrLf.UNIX);
        }
    }
    
    public Reader chain(final Reader rdr) {
        try {
            final FixCrLfFilter newFilter = new FixCrLfFilter(rdr);
            newFilter.setJavafiles(this.getJavafiles());
            newFilter.setEol(this.getEol());
            newFilter.setTab(this.getTab());
            newFilter.setTablength(this.getTablength());
            newFilter.setEof(this.getEof());
            newFilter.setFixlast(this.getFixlast());
            newFilter.initInternalFilters();
            return newFilter;
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }
    
    public AddAsisRemove getEof() {
        return this.ctrlz.newInstance();
    }
    
    public CrLf getEol() {
        return this.eol.newInstance();
    }
    
    public boolean getFixlast() {
        return this.fixlast;
    }
    
    public boolean getJavafiles() {
        return this.javafiles;
    }
    
    public AddAsisRemove getTab() {
        return this.tabs.newInstance();
    }
    
    public int getTablength() {
        return this.tabLength;
    }
    
    private static String calculateEolString(final CrLf eol) {
        if (eol == CrLf.CR || eol == CrLf.MAC) {
            return "\r";
        }
        if (eol == CrLf.CRLF || eol == CrLf.DOS) {
            return "\r\n";
        }
        return "\n";
    }
    
    private void initInternalFilters() {
        this.in = ((this.ctrlz == AddAsisRemove.REMOVE) ? new RemoveEofFilter(this.in) : this.in);
        if (this.eol != CrLf.ASIS) {
            this.in = new NormalizeEolFilter(this.in, calculateEolString(this.eol), this.getFixlast());
        }
        if (this.tabs != AddAsisRemove.ASIS) {
            if (this.getJavafiles()) {
                this.in = new MaskJavaTabLiteralsFilter(this.in);
            }
            this.in = ((this.tabs == AddAsisRemove.ADD) ? new AddTabFilter(this.in, this.getTablength()) : new RemoveTabFilter(this.in, this.getTablength()));
        }
        this.in = ((this.ctrlz == AddAsisRemove.ADD) ? new AddEofFilter(this.in) : this.in);
        this.initialized = true;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (!this.initialized) {
            this.initInternalFilters();
        }
        return this.in.read();
    }
    
    public void setEof(final AddAsisRemove attr) {
        this.ctrlz = attr.resolve();
    }
    
    public void setEol(final CrLf attr) {
        this.eol = attr.resolve();
    }
    
    public void setFixlast(final boolean fixlast) {
        this.fixlast = fixlast;
    }
    
    public void setJavafiles(final boolean javafiles) {
        this.javafiles = javafiles;
    }
    
    public void setTab(final AddAsisRemove attr) {
        this.tabs = attr.resolve();
    }
    
    public void setTablength(final int tabLength) throws IOException {
        if (tabLength < 2 || tabLength > 80) {
            throw new IOException("tablength must be between 2 and 80");
        }
        this.tabLength = tabLength;
    }
    
    private static class SimpleFilterReader extends Reader
    {
        private static final int PREEMPT_BUFFER_LENGTH = 16;
        private Reader in;
        private int[] preempt;
        private int preemptIndex;
        
        public SimpleFilterReader(final Reader in) {
            this.preempt = new int[16];
            this.preemptIndex = 0;
            this.in = in;
        }
        
        public void push(final char c) {
            this.push((int)c);
        }
        
        public void push(final int c) {
            try {
                this.preempt[this.preemptIndex++] = c;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                final int[] p2 = new int[this.preempt.length * 2];
                System.arraycopy(this.preempt, 0, p2, 0, this.preempt.length);
                this.preempt = p2;
                this.push(c);
            }
        }
        
        public void push(final char[] cs, final int start, final int length) {
            int i = start + length - 1;
            while (i >= start) {
                this.push(cs[i--]);
            }
        }
        
        public void push(final char[] cs) {
            this.push(cs, 0, cs.length);
        }
        
        public boolean editsBlocked() {
            return this.in instanceof SimpleFilterReader && ((SimpleFilterReader)this.in).editsBlocked();
        }
        
        @Override
        public int read() throws IOException {
            int read;
            if (this.preemptIndex > 0) {
                final int[] preempt = this.preempt;
                final int preemptIndex = this.preemptIndex - 1;
                this.preemptIndex = preemptIndex;
                read = preempt[preemptIndex];
            }
            else {
                read = this.in.read();
            }
            return read;
        }
        
        @Override
        public void close() throws IOException {
            this.in.close();
        }
        
        @Override
        public void reset() throws IOException {
            this.in.reset();
        }
        
        @Override
        public boolean markSupported() {
            return this.in.markSupported();
        }
        
        @Override
        public boolean ready() throws IOException {
            return this.in.ready();
        }
        
        @Override
        public void mark(final int i) throws IOException {
            this.in.mark(i);
        }
        
        @Override
        public long skip(final long i) throws IOException {
            return this.in.skip(i);
        }
        
        @Override
        public int read(final char[] buf) throws IOException {
            return this.read(buf, 0, buf.length);
        }
        
        @Override
        public int read(final char[] buf, int start, int length) throws IOException {
            int count = 0;
            int c = 0;
            while (length-- > 0 && (c = this.read()) != -1) {
                buf[start++] = (char)c;
                ++count;
            }
            return (count == 0 && c == -1) ? -1 : count;
        }
    }
    
    private static class MaskJavaTabLiteralsFilter extends SimpleFilterReader
    {
        private boolean editsBlocked;
        private static final int JAVA = 1;
        private static final int IN_CHAR_CONST = 2;
        private static final int IN_STR_CONST = 3;
        private static final int IN_SINGLE_COMMENT = 4;
        private static final int IN_MULTI_COMMENT = 5;
        private static final int TRANS_TO_COMMENT = 6;
        private static final int TRANS_FROM_MULTI = 8;
        private int state;
        
        public MaskJavaTabLiteralsFilter(final Reader in) {
            super(in);
            this.editsBlocked = false;
            this.state = 1;
        }
        
        @Override
        public boolean editsBlocked() {
            return this.editsBlocked || super.editsBlocked();
        }
        
        @Override
        public int read() throws IOException {
            final int thisChar = super.read();
            this.editsBlocked = (this.state == 2 || this.state == 3);
            Label_0395: {
                switch (this.state) {
                    case 1: {
                        switch (thisChar) {
                            case 39: {
                                this.state = 2;
                                break Label_0395;
                            }
                            case 34: {
                                this.state = 3;
                                break Label_0395;
                            }
                            case 47: {
                                this.state = 6;
                                break Label_0395;
                            }
                            default: {
                                break Label_0395;
                            }
                        }
                        break;
                    }
                    case 2: {
                        switch (thisChar) {
                            case 39: {
                                this.state = 1;
                                break Label_0395;
                            }
                            default: {
                                break Label_0395;
                            }
                        }
                        break;
                    }
                    case 3: {
                        switch (thisChar) {
                            case 34: {
                                this.state = 1;
                                break Label_0395;
                            }
                            default: {
                                break Label_0395;
                            }
                        }
                        break;
                    }
                    case 4: {
                        switch (thisChar) {
                            case 10:
                            case 13: {
                                this.state = 1;
                                break Label_0395;
                            }
                            default: {
                                break Label_0395;
                            }
                        }
                        break;
                    }
                    case 5: {
                        switch (thisChar) {
                            case 42: {
                                this.state = 8;
                                break Label_0395;
                            }
                            default: {
                                break Label_0395;
                            }
                        }
                        break;
                    }
                    case 6: {
                        switch (thisChar) {
                            case 42: {
                                this.state = 5;
                                break Label_0395;
                            }
                            case 47: {
                                this.state = 4;
                                break Label_0395;
                            }
                            case 39: {
                                this.state = 2;
                                break Label_0395;
                            }
                            case 34: {
                                this.state = 3;
                                break Label_0395;
                            }
                            default: {
                                this.state = 1;
                                break Label_0395;
                            }
                        }
                        break;
                    }
                    case 8: {
                        switch (thisChar) {
                            case 47: {
                                this.state = 1;
                                break Label_0395;
                            }
                            default: {
                                break Label_0395;
                            }
                        }
                        break;
                    }
                }
            }
            return thisChar;
        }
    }
    
    private static class NormalizeEolFilter extends SimpleFilterReader
    {
        private boolean previousWasEOL;
        private boolean fixLast;
        private int normalizedEOL;
        private char[] eol;
        
        public NormalizeEolFilter(final Reader in, final String eolString, final boolean fixLast) {
            super(in);
            this.normalizedEOL = 0;
            this.eol = null;
            this.eol = eolString.toCharArray();
            this.fixLast = fixLast;
        }
        
        @Override
        public int read() throws IOException {
            int thisChar = super.read();
            if (this.normalizedEOL == 0) {
                int numEOL = 0;
                boolean atEnd = false;
                switch (thisChar) {
                    case 26: {
                        final int c = super.read();
                        if (c != -1) {
                            this.push(c);
                            break;
                        }
                        atEnd = true;
                        if (this.fixLast && !this.previousWasEOL) {
                            numEOL = 1;
                            this.push(thisChar);
                            break;
                        }
                        break;
                    }
                    case -1: {
                        atEnd = true;
                        if (this.fixLast && !this.previousWasEOL) {
                            numEOL = 1;
                            break;
                        }
                        break;
                    }
                    case 10: {
                        numEOL = 1;
                        break;
                    }
                    case 13: {
                        numEOL = 1;
                        final int c2 = super.read();
                        final int c3 = super.read();
                        if (c2 == 13 && c3 == 10) {
                            break;
                        }
                        if (c2 == 13) {
                            numEOL = 2;
                            this.push(c3);
                            break;
                        }
                        if (c2 == 10) {
                            this.push(c3);
                            break;
                        }
                        this.push(c3);
                        this.push(c2);
                        break;
                    }
                }
                if (numEOL > 0) {
                    while (numEOL-- > 0) {
                        this.push(this.eol);
                        this.normalizedEOL += this.eol.length;
                    }
                    this.previousWasEOL = true;
                    thisChar = this.read();
                }
                else if (!atEnd) {
                    this.previousWasEOL = false;
                }
            }
            else {
                --this.normalizedEOL;
            }
            return thisChar;
        }
    }
    
    private static class AddEofFilter extends SimpleFilterReader
    {
        private int lastChar;
        
        public AddEofFilter(final Reader in) {
            super(in);
            this.lastChar = -1;
        }
        
        @Override
        public int read() throws IOException {
            final int thisChar = super.read();
            if (thisChar == -1) {
                if (this.lastChar != 26) {
                    return this.lastChar = 26;
                }
            }
            else {
                this.lastChar = thisChar;
            }
            return thisChar;
        }
    }
    
    private static class RemoveEofFilter extends SimpleFilterReader
    {
        private int lookAhead;
        
        public RemoveEofFilter(final Reader in) {
            super(in);
            this.lookAhead = -1;
            try {
                this.lookAhead = in.read();
            }
            catch (IOException e) {
                this.lookAhead = -1;
            }
        }
        
        @Override
        public int read() throws IOException {
            final int lookAhead2 = super.read();
            if (lookAhead2 == -1 && this.lookAhead == 26) {
                return -1;
            }
            final int i = this.lookAhead;
            this.lookAhead = lookAhead2;
            return i;
        }
    }
    
    private static class AddTabFilter extends SimpleFilterReader
    {
        private int columnNumber;
        private int tabLength;
        
        public AddTabFilter(final Reader in, final int tabLength) {
            super(in);
            this.columnNumber = 0;
            this.tabLength = 0;
            this.tabLength = tabLength;
        }
        
        @Override
        public int read() throws IOException {
            int c = super.read();
            switch (c) {
                case 10:
                case 13: {
                    this.columnNumber = 0;
                    break;
                }
                case 32: {
                    ++this.columnNumber;
                    if (!this.editsBlocked()) {
                        int colNextTab = (this.columnNumber + this.tabLength - 1) / this.tabLength * this.tabLength;
                        int countSpaces = 1;
                        int numTabs = 0;
                    Label_0200:
                        while ((c = super.read()) != -1) {
                            switch (c) {
                                case 32: {
                                    if (++this.columnNumber == colNextTab) {
                                        ++numTabs;
                                        countSpaces = 0;
                                        colNextTab += this.tabLength;
                                        continue;
                                    }
                                    ++countSpaces;
                                    continue;
                                }
                                case 9: {
                                    this.columnNumber = colNextTab;
                                    ++numTabs;
                                    countSpaces = 0;
                                    colNextTab += this.tabLength;
                                    continue;
                                }
                                default: {
                                    this.push(c);
                                    break Label_0200;
                                }
                            }
                        }
                        while (countSpaces-- > 0) {
                            this.push(' ');
                            --this.columnNumber;
                        }
                        while (numTabs-- > 0) {
                            this.push('\t');
                            this.columnNumber -= this.tabLength;
                        }
                        c = super.read();
                        switch (c) {
                            case 32: {
                                ++this.columnNumber;
                                break;
                            }
                            case 9: {
                                this.columnNumber += this.tabLength;
                                break;
                            }
                        }
                        break;
                    }
                    break;
                }
                case 9: {
                    this.columnNumber = (this.columnNumber + this.tabLength - 1) / this.tabLength * this.tabLength;
                    break;
                }
                default: {
                    ++this.columnNumber;
                    break;
                }
            }
            return c;
        }
    }
    
    private static class RemoveTabFilter extends SimpleFilterReader
    {
        private int columnNumber;
        private int tabLength;
        
        public RemoveTabFilter(final Reader in, final int tabLength) {
            super(in);
            this.columnNumber = 0;
            this.tabLength = 0;
            this.tabLength = tabLength;
        }
        
        @Override
        public int read() throws IOException {
            int c = super.read();
            switch (c) {
                case 10:
                case 13: {
                    this.columnNumber = 0;
                    break;
                }
                case 9: {
                    int width = this.tabLength - this.columnNumber % this.tabLength;
                    if (!this.editsBlocked()) {
                        while (width > 1) {
                            this.push(' ');
                            --width;
                        }
                        c = 32;
                    }
                    this.columnNumber += width;
                    break;
                }
                default: {
                    ++this.columnNumber;
                    break;
                }
            }
            return c;
        }
    }
    
    public static class AddAsisRemove extends EnumeratedAttribute
    {
        private static final AddAsisRemove ASIS;
        private static final AddAsisRemove ADD;
        private static final AddAsisRemove REMOVE;
        
        @Override
        public String[] getValues() {
            return new String[] { "add", "asis", "remove" };
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof AddAsisRemove && this.getIndex() == ((AddAsisRemove)other).getIndex();
        }
        
        @Override
        public int hashCode() {
            return this.getIndex();
        }
        
        AddAsisRemove resolve() throws IllegalStateException {
            if (this.equals(AddAsisRemove.ASIS)) {
                return AddAsisRemove.ASIS;
            }
            if (this.equals(AddAsisRemove.ADD)) {
                return AddAsisRemove.ADD;
            }
            if (this.equals(AddAsisRemove.REMOVE)) {
                return AddAsisRemove.REMOVE;
            }
            throw new IllegalStateException("No replacement for " + this);
        }
        
        private AddAsisRemove newInstance() {
            return newInstance(this.getValue());
        }
        
        public static AddAsisRemove newInstance(final String value) {
            final AddAsisRemove a = new AddAsisRemove();
            a.setValue(value);
            return a;
        }
        
        static {
            ASIS = newInstance("asis");
            ADD = newInstance("add");
            REMOVE = newInstance("remove");
        }
    }
    
    public static class CrLf extends EnumeratedAttribute
    {
        private static final CrLf ASIS;
        private static final CrLf CR;
        private static final CrLf CRLF;
        private static final CrLf DOS;
        private static final CrLf LF;
        private static final CrLf MAC;
        private static final CrLf UNIX;
        
        @Override
        public String[] getValues() {
            return new String[] { "asis", "cr", "lf", "crlf", "mac", "unix", "dos" };
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof CrLf && this.getIndex() == ((CrLf)other).getIndex();
        }
        
        @Override
        public int hashCode() {
            return this.getIndex();
        }
        
        CrLf resolve() {
            if (this.equals(CrLf.ASIS)) {
                return CrLf.ASIS;
            }
            if (this.equals(CrLf.CR) || this.equals(CrLf.MAC)) {
                return CrLf.CR;
            }
            if (this.equals(CrLf.CRLF) || this.equals(CrLf.DOS)) {
                return CrLf.CRLF;
            }
            if (this.equals(CrLf.LF) || this.equals(CrLf.UNIX)) {
                return CrLf.LF;
            }
            throw new IllegalStateException("No replacement for " + this);
        }
        
        private CrLf newInstance() {
            return newInstance(this.getValue());
        }
        
        public static CrLf newInstance(final String value) {
            final CrLf c = new CrLf();
            c.setValue(value);
            return c;
        }
        
        static {
            ASIS = newInstance("asis");
            CR = newInstance("cr");
            CRLF = newInstance("crlf");
            DOS = newInstance("dos");
            LF = newInstance("lf");
            MAC = newInstance("mac");
            UNIX = newInstance("unix");
        }
    }
}
