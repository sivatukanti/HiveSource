// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class QuotedQualityCSV extends QuotedCSV implements Iterable<String>
{
    private static final Double ZERO;
    private static final Double ONE;
    private final List<Double> _quality;
    private boolean _sorted;
    
    public QuotedQualityCSV(final String... values) {
        super(new String[0]);
        this._quality = new ArrayList<Double>();
        this._sorted = false;
        for (final String v : values) {
            this.addValue(v);
        }
    }
    
    @Override
    public void addValue(final String value) {
        super.addValue(value);
        while (this._quality.size() < this._values.size()) {
            this._quality.add(QuotedQualityCSV.ONE);
        }
    }
    
    @Override
    protected void parsedValue(final StringBuffer buffer) {
        super.parsedValue(buffer);
    }
    
    @Override
    protected void parsedParam(final StringBuffer buffer, final int valueLength, final int paramName, final int paramValue) {
        if (paramName < 0 && buffer.charAt(buffer.length() - 1) == ';') {
            buffer.setLength(buffer.length() - 1);
        }
        if (paramValue >= 0 && buffer.charAt(paramName) == 'q' && paramValue > paramName && buffer.length() >= paramName && buffer.charAt(paramName + 1) == '=') {
            Double q;
            try {
                q = ((this._keepQuotes && buffer.charAt(paramValue) == '\"') ? new Double(buffer.substring(paramValue + 1, buffer.length() - 1)) : new Double(buffer.substring(paramValue)));
            }
            catch (Exception e) {
                q = QuotedQualityCSV.ZERO;
            }
            buffer.setLength(paramName - 1);
            while (this._quality.size() < this._values.size()) {
                this._quality.add(QuotedQualityCSV.ONE);
            }
            this._quality.add(q);
        }
    }
    
    @Override
    public List<String> getValues() {
        if (!this._sorted) {
            this.sort();
        }
        return this._values;
    }
    
    @Override
    public Iterator<String> iterator() {
        if (!this._sorted) {
            this.sort();
        }
        return this._values.iterator();
    }
    
    protected void sort() {
        this._sorted = true;
        Double last = QuotedQualityCSV.ZERO;
        int i = this._values.size();
        while (i-- > 0) {
            final String v = this._values.get(i);
            final Double q = this._quality.get(i);
            final int compare = last.compareTo(q);
            if (compare > 0) {
                this._values.set(i, this._values.get(i + 1));
                this._values.set(i + 1, v);
                this._quality.set(i, this._quality.get(i + 1));
                this._quality.set(i + 1, q);
                last = QuotedQualityCSV.ZERO;
                i = this._values.size();
            }
            else {
                last = q;
            }
        }
        int last_element = this._quality.size();
        while (last_element > 0 && this._quality.get(--last_element).equals(QuotedQualityCSV.ZERO)) {
            this._quality.remove(last_element);
            this._values.remove(last_element);
        }
    }
    
    static {
        ZERO = new Double(0.0);
        ONE = new Double(1.0);
    }
}
