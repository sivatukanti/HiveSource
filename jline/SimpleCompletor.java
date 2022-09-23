// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.SortedSet;

public class SimpleCompletor implements Completor, Cloneable
{
    SortedSet candidates;
    String delimiter;
    final SimpleCompletorFilter filter;
    
    public SimpleCompletor(final String candidateString) {
        this(new String[] { candidateString });
    }
    
    public SimpleCompletor(final String[] candidateStrings) {
        this(candidateStrings, null);
    }
    
    public SimpleCompletor(final String[] strings, final SimpleCompletorFilter filter) {
        this.filter = filter;
        this.setCandidateStrings(strings);
    }
    
    public SimpleCompletor(final Reader reader) throws IOException {
        this(getStrings(reader));
    }
    
    public SimpleCompletor(final InputStream in) throws IOException {
        this(getStrings(new InputStreamReader(in)));
    }
    
    private static String[] getStrings(final Reader in) throws IOException {
        final Reader reader = (in instanceof BufferedReader) ? in : new BufferedReader(in);
        final List words = new LinkedList();
        String line;
        while ((line = ((BufferedReader)reader).readLine()) != null) {
            final StringTokenizer tok = new StringTokenizer(line);
            while (tok.hasMoreTokens()) {
                words.add(tok.nextToken());
            }
        }
        return words.toArray(new String[words.size()]);
    }
    
    public int complete(final String buffer, final int cursor, final List clist) {
        final String start = (buffer == null) ? "" : buffer;
        final SortedSet matches = this.candidates.tailSet(start);
        for (String can : matches) {
            if (!can.startsWith(start)) {
                break;
            }
            if (this.delimiter != null) {
                final int index = can.indexOf(this.delimiter, cursor);
                if (index != -1) {
                    can = can.substring(0, index + 1);
                }
            }
            clist.add(can);
        }
        if (clist.size() == 1) {
            clist.set(0, clist.get(0) + " ");
        }
        return (clist.size() == 0) ? -1 : 0;
    }
    
    public void setDelimiter(final String delimiter) {
        this.delimiter = delimiter;
    }
    
    public String getDelimiter() {
        return this.delimiter;
    }
    
    public void setCandidates(final SortedSet candidates) {
        if (this.filter != null) {
            final TreeSet filtered = new TreeSet();
            for (String element : candidates) {
                element = this.filter.filter(element);
                if (element != null) {
                    filtered.add(element);
                }
            }
            this.candidates = filtered;
        }
        else {
            this.candidates = candidates;
        }
    }
    
    public SortedSet getCandidates() {
        return Collections.unmodifiableSortedSet((SortedSet<Object>)this.candidates);
    }
    
    public void setCandidateStrings(final String[] strings) {
        this.setCandidates(new TreeSet(Arrays.asList(strings)));
    }
    
    public void addCandidateString(final String candidateString) {
        final String string = (this.filter == null) ? candidateString : this.filter.filter(candidateString);
        if (string != null) {
            this.candidates.add(string);
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public static class NoOpFilter implements SimpleCompletorFilter
    {
        public String filter(final String element) {
            return element;
        }
    }
    
    public interface SimpleCompletorFilter
    {
        String filter(final String p0);
    }
}
