// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Writer;
import java.util.HashMap;
import java.io.OutputStream;
import java.util.Map;
import java.io.PrintWriter;

public class PrintWriterWithSMAP extends PrintWriter
{
    private int currentOutputLine;
    private int currentSourceLine;
    private Map sourceMap;
    private boolean lastPrintCharacterWasCR;
    private boolean mapLines;
    private boolean mapSingleSourceLine;
    private boolean anythingWrittenSinceMapping;
    
    public PrintWriterWithSMAP(final OutputStream out) {
        super(out);
        this.currentOutputLine = 1;
        this.currentSourceLine = 0;
        this.sourceMap = new HashMap();
        this.lastPrintCharacterWasCR = false;
        this.mapLines = false;
        this.mapSingleSourceLine = false;
        this.anythingWrittenSinceMapping = false;
    }
    
    public PrintWriterWithSMAP(final OutputStream out, final boolean autoFlush) {
        super(out, autoFlush);
        this.currentOutputLine = 1;
        this.currentSourceLine = 0;
        this.sourceMap = new HashMap();
        this.lastPrintCharacterWasCR = false;
        this.mapLines = false;
        this.mapSingleSourceLine = false;
        this.anythingWrittenSinceMapping = false;
    }
    
    public PrintWriterWithSMAP(final Writer out) {
        super(out);
        this.currentOutputLine = 1;
        this.currentSourceLine = 0;
        this.sourceMap = new HashMap();
        this.lastPrintCharacterWasCR = false;
        this.mapLines = false;
        this.mapSingleSourceLine = false;
        this.anythingWrittenSinceMapping = false;
    }
    
    public PrintWriterWithSMAP(final Writer out, final boolean autoFlush) {
        super(out, autoFlush);
        this.currentOutputLine = 1;
        this.currentSourceLine = 0;
        this.sourceMap = new HashMap();
        this.lastPrintCharacterWasCR = false;
        this.mapLines = false;
        this.mapSingleSourceLine = false;
        this.anythingWrittenSinceMapping = false;
    }
    
    public void startMapping(final int currentSourceLine) {
        this.mapLines = true;
        if (currentSourceLine != -888) {
            this.currentSourceLine = currentSourceLine;
        }
    }
    
    public void startSingleSourceLineMapping(final int currentSourceLine) {
        this.mapSingleSourceLine = true;
        this.mapLines = true;
        if (currentSourceLine != -888) {
            this.currentSourceLine = currentSourceLine;
        }
    }
    
    public void endMapping() {
        this.mapLine(false);
        this.mapLines = false;
        this.mapSingleSourceLine = false;
    }
    
    protected void mapLine(final boolean b) {
        if (this.mapLines && this.anythingWrittenSinceMapping) {
            final Integer n = new Integer(this.currentSourceLine);
            final Integer n2 = new Integer(this.currentOutputLine);
            List<Integer> list = this.sourceMap.get(n);
            if (list == null) {
                list = new ArrayList<Integer>();
                this.sourceMap.put(n, list);
            }
            if (!list.contains(n2)) {
                list.add(n2);
            }
        }
        if (b) {
            ++this.currentOutputLine;
        }
        if (!this.mapSingleSourceLine) {
            ++this.currentSourceLine;
        }
        this.anythingWrittenSinceMapping = false;
    }
    
    public void dump(final PrintWriter printWriter, final String str, final String s) {
        printWriter.println("SMAP");
        printWriter.println(str + ".java");
        printWriter.println("G");
        printWriter.println("*S G");
        printWriter.println("*F");
        printWriter.println("+ 0 " + s);
        printWriter.println(s);
        printWriter.println("*L");
        final ArrayList<Integer> list = new ArrayList<Integer>(this.sourceMap.keySet());
        Collections.sort((List<Comparable>)list);
        for (final Integer obj : list) {
            final Iterator<Integer> iterator2 = this.sourceMap.get(obj).iterator();
            while (iterator2.hasNext()) {
                printWriter.println(obj + ":" + iterator2.next());
            }
        }
        printWriter.println("*E");
        printWriter.close();
    }
    
    public void write(final char[] buf, final int off, final int len) {
        for (int n = off + len, i = off; i < n; ++i) {
            this.checkChar(buf[i]);
        }
        super.write(buf, off, len);
    }
    
    public void checkChar(final int n) {
        if (this.lastPrintCharacterWasCR && n != 10) {
            this.mapLine(true);
        }
        else if (n == 10) {
            this.mapLine(true);
        }
        else if (!Character.isWhitespace((char)n)) {
            this.anythingWrittenSinceMapping = true;
        }
        this.lastPrintCharacterWasCR = (n == 13);
    }
    
    public void write(final int c) {
        this.checkChar(c);
        super.write(c);
    }
    
    public void write(final String s, final int off, final int len) {
        for (int n = off + len, i = off; i < n; ++i) {
            this.checkChar(s.charAt(i));
        }
        super.write(s, off, len);
    }
    
    public void println() {
        this.mapLine(true);
        super.println();
        this.lastPrintCharacterWasCR = false;
    }
    
    public Map getSourceMap() {
        return this.sourceMap;
    }
    
    public int getCurrentOutputLine() {
        return this.currentOutputLine;
    }
}
