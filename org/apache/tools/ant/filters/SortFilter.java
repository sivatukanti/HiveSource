// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.filters;

import java.util.Collections;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.BuildException;
import java.io.IOException;
import java.util.ArrayList;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;

public final class SortFilter extends BaseParamFilterReader implements ChainableReader
{
    private static final String REVERSE_KEY = "reverse";
    private static final String COMPARATOR_KEY = "comparator";
    private Comparator<? super String> comparator;
    private boolean reverse;
    private List<String> lines;
    private String line;
    private Iterator<String> iterator;
    
    public SortFilter() {
        this.comparator = null;
        this.line = null;
        this.iterator = null;
    }
    
    public SortFilter(final Reader in) {
        super(in);
        this.comparator = null;
        this.line = null;
        this.iterator = null;
    }
    
    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.line != null) {
            ch = this.line.charAt(0);
            if (this.line.length() == 1) {
                this.line = null;
            }
            else {
                this.line = this.line.substring(1);
            }
        }
        else {
            if (this.lines == null) {
                this.lines = new ArrayList<String>();
                this.line = this.readLine();
                while (this.line != null) {
                    this.lines.add(this.line);
                    this.line = this.readLine();
                }
                this.sort();
                this.iterator = this.lines.iterator();
            }
            if (this.iterator.hasNext()) {
                this.line = this.iterator.next();
            }
            else {
                this.line = null;
                this.lines = null;
                this.iterator = null;
            }
            if (this.line != null) {
                return this.read();
            }
        }
        return ch;
    }
    
    public Reader chain(final Reader rdr) {
        final SortFilter newFilter = new SortFilter(rdr);
        newFilter.setReverse(this.isReverse());
        newFilter.setComparator(this.getComparator());
        newFilter.setInitialized(true);
        return newFilter;
    }
    
    public boolean isReverse() {
        return this.reverse;
    }
    
    public void setReverse(final boolean reverse) {
        this.reverse = reverse;
    }
    
    public Comparator<? super String> getComparator() {
        return this.comparator;
    }
    
    public void setComparator(final Comparator<? super String> comparator) {
        this.comparator = comparator;
    }
    
    public void add(final Comparator<? super String> comparator) {
        if (this.comparator != null && comparator != null) {
            throw new BuildException("can't have more than one comparator");
        }
        this.setComparator(comparator);
    }
    
    private void initialize() throws IOException {
        final Parameter[] params = this.getParameters();
        if (params != null) {
            for (int i = 0; i < params.length; ++i) {
                final String paramName = params[i].getName();
                if ("reverse".equals(paramName)) {
                    this.setReverse(Boolean.valueOf(params[i].getValue()));
                }
                else if ("comparator".equals(paramName)) {
                    try {
                        final String className = params[i].getValue();
                        final Comparator<? super String> comparatorInstance = (Comparator<? super String>)Class.forName(className).newInstance();
                        this.setComparator(comparatorInstance);
                    }
                    catch (InstantiationException e) {
                        throw new BuildException(e);
                    }
                    catch (IllegalAccessException e2) {
                        throw new BuildException(e2);
                    }
                    catch (ClassNotFoundException e3) {
                        throw new BuildException(e3);
                    }
                    catch (ClassCastException e5) {
                        throw new BuildException("Value of comparator attribute should implement java.util.Comparator interface");
                    }
                    catch (Exception e4) {
                        throw new BuildException(e4);
                    }
                }
            }
        }
    }
    
    private void sort() {
        if (this.comparator == null) {
            if (this.reverse) {
                Collections.sort(this.lines, new Comparator<String>() {
                    public int compare(final String s1, final String s2) {
                        return -s1.compareTo(s2);
                    }
                });
            }
            else {
                Collections.sort(this.lines);
            }
        }
        else {
            Collections.sort(this.lines, this.comparator);
        }
    }
}
