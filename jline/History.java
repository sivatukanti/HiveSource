// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.Collections;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.List;

public class History
{
    private List history;
    private PrintWriter output;
    private int maxSize;
    private int currentIndex;
    
    public History() {
        this.history = new ArrayList();
        this.output = null;
        this.maxSize = 500;
        this.currentIndex = 0;
    }
    
    public History(final File historyFile) throws IOException {
        this.history = new ArrayList();
        this.output = null;
        this.maxSize = 500;
        this.currentIndex = 0;
        this.setHistoryFile(historyFile);
    }
    
    public void setHistoryFile(final File historyFile) throws IOException {
        if (historyFile.isFile()) {
            this.load(new FileInputStream(historyFile));
        }
        this.setOutput(new PrintWriter(new FileWriter(historyFile), true));
        this.flushBuffer();
    }
    
    public void load(final InputStream in) throws IOException {
        this.load(new InputStreamReader(in));
    }
    
    public void load(final Reader reader) throws IOException {
        final BufferedReader breader = new BufferedReader(reader);
        final List lines = new ArrayList();
        String line;
        while ((line = breader.readLine()) != null) {
            lines.add(line);
        }
        final Iterator i = lines.iterator();
        while (i.hasNext()) {
            this.addToHistory(i.next());
        }
    }
    
    public int size() {
        return this.history.size();
    }
    
    public void clear() {
        this.history.clear();
        this.currentIndex = 0;
    }
    
    public void addToHistory(final String buffer) {
        if (this.history.size() != 0 && buffer.equals(this.history.get(this.history.size() - 1))) {
            return;
        }
        this.history.add(buffer);
        while (this.history.size() > this.getMaxSize()) {
            this.history.remove(0);
        }
        this.currentIndex = this.history.size();
        if (this.getOutput() != null) {
            this.getOutput().println(buffer);
            this.getOutput().flush();
        }
    }
    
    public void flushBuffer() throws IOException {
        if (this.getOutput() != null) {
            final Iterator i = this.history.iterator();
            while (i.hasNext()) {
                this.getOutput().println(i.next());
            }
            this.getOutput().flush();
        }
    }
    
    public boolean moveToLastEntry() {
        final int lastEntry = this.history.size() - 1;
        if (lastEntry >= 0 && lastEntry != this.currentIndex) {
            this.currentIndex = this.history.size() - 1;
            return true;
        }
        return false;
    }
    
    public void moveToEnd() {
        this.currentIndex = this.history.size();
    }
    
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
    
    public int getMaxSize() {
        return this.maxSize;
    }
    
    public void setOutput(final PrintWriter output) {
        this.output = output;
    }
    
    public PrintWriter getOutput() {
        return this.output;
    }
    
    public int getCurrentIndex() {
        return this.currentIndex;
    }
    
    public String current() {
        if (this.currentIndex >= this.history.size()) {
            return "";
        }
        return this.history.get(this.currentIndex);
    }
    
    public boolean previous() {
        if (this.currentIndex <= 0) {
            return false;
        }
        --this.currentIndex;
        return true;
    }
    
    public boolean next() {
        if (this.currentIndex >= this.history.size()) {
            return false;
        }
        ++this.currentIndex;
        return true;
    }
    
    public List getHistoryList() {
        return Collections.unmodifiableList((List<?>)this.history);
    }
    
    public String toString() {
        return this.history.toString();
    }
    
    public boolean moveToFirstEntry() {
        if (this.history.size() > 0 && this.currentIndex != 0) {
            this.currentIndex = 0;
            return true;
        }
        return false;
    }
}
