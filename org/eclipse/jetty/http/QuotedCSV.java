// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class QuotedCSV implements Iterable<String>
{
    protected final List<String> _values;
    protected final boolean _keepQuotes;
    
    public QuotedCSV(final String... values) {
        this(true, values);
    }
    
    public QuotedCSV(final boolean keepQuotes, final String... values) {
        this._values = new ArrayList<String>();
        this._keepQuotes = keepQuotes;
        for (final String v : values) {
            this.addValue(v);
        }
    }
    
    public void addValue(final String value) {
        final StringBuffer buffer = new StringBuffer();
        final int l = value.length();
        State state = State.VALUE;
        boolean quoted = false;
        boolean sloshed = false;
        int nws_length = 0;
        int last_length = 0;
        int value_length = -1;
        int param_name = -1;
        int param_value = -1;
        for (int i = 0; i <= l; ++i) {
            final char c = (i == l) ? '\0' : value.charAt(i);
            if (quoted && c != '\0') {
                if (sloshed) {
                    sloshed = false;
                }
                else {
                    switch (c) {
                        case '\\': {
                            sloshed = true;
                            if (!this._keepQuotes) {
                                continue;
                            }
                            break;
                        }
                        case '\"': {
                            quoted = false;
                            if (!this._keepQuotes) {
                                continue;
                            }
                            break;
                        }
                    }
                }
                buffer.append(c);
                nws_length = buffer.length();
            }
            else {
                switch (c) {
                    case '\t':
                    case ' ': {
                        if (buffer.length() > last_length) {
                            buffer.append(c);
                            break;
                        }
                        break;
                    }
                    case '\"': {
                        quoted = true;
                        if (this._keepQuotes) {
                            if (state == State.PARAM_VALUE && param_value < 0) {
                                param_value = nws_length;
                            }
                            buffer.append(c);
                        }
                        else if (state == State.PARAM_VALUE && param_value < 0) {
                            param_value = nws_length;
                        }
                        nws_length = buffer.length();
                        break;
                    }
                    case ';': {
                        buffer.setLength(nws_length);
                        if (state == State.VALUE) {
                            this.parsedValue(buffer);
                            value_length = buffer.length();
                        }
                        else {
                            this.parsedParam(buffer, value_length, param_name, param_value);
                        }
                        nws_length = buffer.length();
                        param_value = (param_name = -1);
                        buffer.append(c);
                        last_length = ++nws_length;
                        state = State.PARAM_NAME;
                        break;
                    }
                    case '\0':
                    case ',': {
                        if (nws_length > 0) {
                            buffer.setLength(nws_length);
                            switch (state) {
                                case VALUE: {
                                    this.parsedValue(buffer);
                                    value_length = buffer.length();
                                    break;
                                }
                                case PARAM_NAME:
                                case PARAM_VALUE: {
                                    this.parsedParam(buffer, value_length, param_name, param_value);
                                    break;
                                }
                            }
                            this._values.add(buffer.toString());
                        }
                        buffer.setLength(0);
                        last_length = 0;
                        nws_length = 0;
                        param_name = (value_length = (param_value = -1));
                        state = State.VALUE;
                        break;
                    }
                    case '=': {
                        switch (state) {
                            case VALUE: {
                                param_name = (value_length = 0);
                                buffer.setLength(nws_length);
                                buffer.append(c);
                                last_length = ++nws_length;
                                state = State.PARAM_VALUE;
                                continue;
                            }
                            case PARAM_NAME: {
                                buffer.setLength(nws_length);
                                buffer.append(c);
                                last_length = ++nws_length;
                                state = State.PARAM_VALUE;
                                continue;
                            }
                            case PARAM_VALUE: {
                                if (param_value < 0) {
                                    param_value = nws_length;
                                }
                                buffer.append(c);
                                nws_length = buffer.length();
                                continue;
                            }
                            default: {
                                continue;
                            }
                        }
                        break;
                    }
                    default: {
                        switch (state) {
                            case VALUE: {
                                buffer.append(c);
                                nws_length = buffer.length();
                                continue;
                            }
                            case PARAM_NAME: {
                                if (param_name < 0) {
                                    param_name = nws_length;
                                }
                                buffer.append(c);
                                nws_length = buffer.length();
                                continue;
                            }
                            case PARAM_VALUE: {
                                if (param_value < 0) {
                                    param_value = nws_length;
                                }
                                buffer.append(c);
                                nws_length = buffer.length();
                                continue;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    protected void parsedValue(final StringBuffer buffer) {
    }
    
    protected void parsedParam(final StringBuffer buffer, final int valueLength, final int paramName, final int paramValue) {
    }
    
    public int size() {
        return this._values.size();
    }
    
    public boolean isEmpty() {
        return this._values.isEmpty();
    }
    
    public List<String> getValues() {
        return this._values;
    }
    
    @Override
    public Iterator<String> iterator() {
        return this._values.iterator();
    }
    
    public static String unquote(final String s) {
        final int l = s.length();
        if (s == null || l == 0) {
            return s;
        }
        int i;
        for (i = 0; i < l; ++i) {
            final char c = s.charAt(i);
            if (c == '\"') {
                break;
            }
        }
        if (i == l) {
            return s;
        }
        boolean quoted = true;
        boolean sloshed = false;
        final StringBuffer buffer = new StringBuffer();
        buffer.append(s, 0, i);
        ++i;
        while (i < l) {
            final char c2 = s.charAt(i);
            if (quoted) {
                if (sloshed) {
                    buffer.append(c2);
                    sloshed = false;
                }
                else if (c2 == '\"') {
                    quoted = false;
                }
                else if (c2 == '\\') {
                    sloshed = true;
                }
                else {
                    buffer.append(c2);
                }
            }
            else if (c2 == '\"') {
                quoted = true;
            }
            else {
                buffer.append(c2);
            }
            ++i;
        }
        return buffer.toString();
    }
    
    private enum State
    {
        VALUE, 
        PARAM_NAME, 
        PARAM_VALUE;
    }
}
