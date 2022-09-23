// 
// Decompiled by Procyon v0.5.36
// 

package javax.mail.internet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;

public class ParameterList
{
    private Map list;
    private Set multisegmentNames;
    private Map slist;
    private String lastName;
    private static boolean encodeParameters;
    private static boolean decodeParameters;
    private static boolean decodeParametersStrict;
    private static boolean applehack;
    private static final char[] hex;
    
    public ParameterList() {
        this.list = new LinkedHashMap();
        this.lastName = null;
        if (ParameterList.decodeParameters) {
            this.multisegmentNames = new HashSet();
            this.slist = new HashMap();
        }
    }
    
    public ParameterList(final String s) throws ParseException {
        this();
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        while (true) {
            HeaderTokenizer.Token tk = h.next();
            int type = tk.getType();
            if (type == -4) {
                break;
            }
            if ((char)type == ';') {
                tk = h.next();
                if (tk.getType() == -4) {
                    break;
                }
                if (tk.getType() != -1) {
                    throw new ParseException("Expected parameter name, got \"" + tk.getValue() + "\"");
                }
                final String name = tk.getValue().toLowerCase(Locale.ENGLISH);
                tk = h.next();
                if ((char)tk.getType() != '=') {
                    throw new ParseException("Expected '=', got \"" + tk.getValue() + "\"");
                }
                tk = h.next();
                type = tk.getType();
                if (type != -1 && type != -2) {
                    throw new ParseException("Expected parameter value, got \"" + tk.getValue() + "\"");
                }
                final String value = tk.getValue();
                this.lastName = name;
                if (ParameterList.decodeParameters) {
                    this.putEncodedName(name, value);
                }
                else {
                    this.list.put(name, value);
                }
            }
            else {
                if (!ParameterList.applehack || type != -1 || this.lastName == null || (!this.lastName.equals("name") && !this.lastName.equals("filename"))) {
                    throw new ParseException("Expected ';', got \"" + tk.getValue() + "\"");
                }
                final String lastValue = this.list.get(this.lastName);
                final String value = lastValue + " " + tk.getValue();
                this.list.put(this.lastName, value);
            }
        }
        if (ParameterList.decodeParameters) {
            this.combineMultisegmentNames(false);
        }
    }
    
    private void putEncodedName(String name, final String value) throws ParseException {
        final int star = name.indexOf(42);
        if (star < 0) {
            this.list.put(name, value);
        }
        else if (star == name.length() - 1) {
            name = name.substring(0, star);
            this.list.put(name, decodeValue(value));
        }
        else {
            final String rname = name.substring(0, star);
            this.multisegmentNames.add(rname);
            this.list.put(rname, "");
            Object v;
            if (name.endsWith("*")) {
                v = new Value();
                ((Value)v).encodedValue = value;
                ((Value)v).value = value;
                name = name.substring(0, name.length() - 1);
            }
            else {
                v = value;
            }
            this.slist.put(name, v);
        }
    }
    
    private void combineMultisegmentNames(final boolean keepConsistentOnFailure) throws ParseException {
        boolean success = false;
        try {
            for (final String name : this.multisegmentNames) {
                final StringBuffer sb = new StringBuffer();
                final MultiValue mv = new MultiValue();
                String charset = null;
                int segment = 0;
                while (true) {
                    final String sname = name + "*" + segment;
                    final Object v = this.slist.get(sname);
                    if (v == null) {
                        break;
                    }
                    mv.add(v);
                    String value = null;
                    if (v instanceof Value) {
                        try {
                            final Value vv = (Value)v;
                            final String evalue = value = vv.encodedValue;
                            if (segment == 0) {
                                final Value vnew = decodeValue(evalue);
                                final Value value2 = vv;
                                final String charset2 = vnew.charset;
                                value2.charset = charset2;
                                charset = charset2;
                                final Value value3 = vv;
                                final String value4 = vnew.value;
                                value3.value = value4;
                                value = value4;
                            }
                            else {
                                if (charset == null) {
                                    this.multisegmentNames.remove(name);
                                    break;
                                }
                                final Value value5 = vv;
                                final String decodeBytes = decodeBytes(evalue, charset);
                                value5.value = decodeBytes;
                                value = decodeBytes;
                            }
                        }
                        catch (NumberFormatException nex) {
                            if (ParameterList.decodeParametersStrict) {
                                throw new ParseException(nex.toString());
                            }
                        }
                        catch (UnsupportedEncodingException uex) {
                            if (ParameterList.decodeParametersStrict) {
                                throw new ParseException(uex.toString());
                            }
                        }
                        catch (StringIndexOutOfBoundsException ex) {
                            if (ParameterList.decodeParametersStrict) {
                                throw new ParseException(ex.toString());
                            }
                        }
                    }
                    else {
                        value = (String)v;
                    }
                    sb.append(value);
                    this.slist.remove(sname);
                    ++segment;
                }
                if (segment == 0) {
                    this.list.remove(name);
                }
                else {
                    mv.value = sb.toString();
                    this.list.put(name, mv);
                }
            }
            success = true;
        }
        finally {
            if (keepConsistentOnFailure || success) {
                if (this.slist.size() > 0) {
                    for (final Object v2 : this.slist.values()) {
                        if (v2 instanceof Value) {
                            final Value vv2 = (Value)v2;
                            final Value vnew2 = decodeValue(vv2.encodedValue);
                            vv2.charset = vnew2.charset;
                            vv2.value = vnew2.value;
                        }
                    }
                    this.list.putAll(this.slist);
                }
                this.multisegmentNames.clear();
                this.slist.clear();
            }
        }
    }
    
    public int size() {
        return this.list.size();
    }
    
    public String get(final String name) {
        final Object v = this.list.get(name.trim().toLowerCase(Locale.ENGLISH));
        String value;
        if (v instanceof MultiValue) {
            value = ((MultiValue)v).value;
        }
        else if (v instanceof Value) {
            value = ((Value)v).value;
        }
        else {
            value = (String)v;
        }
        return value;
    }
    
    public void set(String name, final String value) {
        if (name == null && value != null && value.equals("DONE")) {
            if (ParameterList.decodeParameters && this.multisegmentNames.size() > 0) {
                try {
                    this.combineMultisegmentNames(true);
                }
                catch (ParseException ex) {}
            }
            return;
        }
        name = name.trim().toLowerCase(Locale.ENGLISH);
        if (ParameterList.decodeParameters) {
            try {
                this.putEncodedName(name, value);
            }
            catch (ParseException pex) {
                this.list.put(name, value);
            }
        }
        else {
            this.list.put(name, value);
        }
    }
    
    public void set(final String name, final String value, final String charset) {
        if (ParameterList.encodeParameters) {
            final Value ev = encodeValue(value, charset);
            if (ev != null) {
                this.list.put(name.trim().toLowerCase(Locale.ENGLISH), ev);
            }
            else {
                this.set(name, value);
            }
        }
        else {
            this.set(name, value);
        }
    }
    
    public void remove(final String name) {
        this.list.remove(name.trim().toLowerCase(Locale.ENGLISH));
    }
    
    public Enumeration getNames() {
        return new ParamEnum(this.list.keySet().iterator());
    }
    
    public String toString() {
        return this.toString(0);
    }
    
    public String toString(final int used) {
        final ToStringBuffer sb = new ToStringBuffer(used);
        for (final String name : this.list.keySet()) {
            final Object v = this.list.get(name);
            if (v instanceof MultiValue) {
                final MultiValue vv = (MultiValue)v;
                final String ns = name + "*";
                for (int i = 0; i < vv.size(); ++i) {
                    final Object va = vv.get(i);
                    if (va instanceof Value) {
                        sb.addNV(ns + i + "*", ((Value)va).encodedValue);
                    }
                    else {
                        sb.addNV(ns + i, (String)va);
                    }
                }
            }
            else if (v instanceof Value) {
                sb.addNV(name + "*", ((Value)v).encodedValue);
            }
            else {
                sb.addNV(name, (String)v);
            }
        }
        return sb.toString();
    }
    
    private static String quote(final String value) {
        return MimeUtility.quote(value, "()<>@,;:\\\"\t []/?=");
    }
    
    private static Value encodeValue(final String value, final String charset) {
        if (MimeUtility.checkAscii(value) == 1) {
            return null;
        }
        byte[] b;
        try {
            b = value.getBytes(MimeUtility.javaCharset(charset));
        }
        catch (UnsupportedEncodingException ex) {
            return null;
        }
        final StringBuffer sb = new StringBuffer(b.length + charset.length() + 2);
        sb.append(charset).append("''");
        for (int i = 0; i < b.length; ++i) {
            final char c = (char)(b[i] & 0xFF);
            if (c <= ' ' || c >= '\u007f' || c == '*' || c == '\'' || c == '%' || "()<>@,;:\\\"\t []/?=".indexOf(c) >= 0) {
                sb.append('%').append(ParameterList.hex[c >> 4]).append(ParameterList.hex[c & '\u000f']);
            }
            else {
                sb.append(c);
            }
        }
        final Value v = new Value();
        v.charset = charset;
        v.value = value;
        v.encodedValue = sb.toString();
        return v;
    }
    
    private static Value decodeValue(String value) throws ParseException {
        final Value v = new Value();
        v.encodedValue = value;
        v.value = value;
        try {
            final int i = value.indexOf(39);
            if (i <= 0) {
                if (ParameterList.decodeParametersStrict) {
                    throw new ParseException("Missing charset in encoded value: " + value);
                }
                return v;
            }
            else {
                final String charset = value.substring(0, i);
                final int li = value.indexOf(39, i + 1);
                if (li < 0) {
                    if (ParameterList.decodeParametersStrict) {
                        throw new ParseException("Missing language in encoded value: " + value);
                    }
                    return v;
                }
                else {
                    final String lang = value.substring(i + 1, li);
                    value = value.substring(li + 1);
                    v.charset = charset;
                    v.value = decodeBytes(value, charset);
                }
            }
        }
        catch (NumberFormatException nex) {
            if (ParameterList.decodeParametersStrict) {
                throw new ParseException(nex.toString());
            }
        }
        catch (UnsupportedEncodingException uex) {
            if (ParameterList.decodeParametersStrict) {
                throw new ParseException(uex.toString());
            }
        }
        catch (StringIndexOutOfBoundsException ex) {
            if (ParameterList.decodeParametersStrict) {
                throw new ParseException(ex.toString());
            }
        }
        return v;
    }
    
    private static String decodeBytes(final String value, final String charset) throws UnsupportedEncodingException {
        final byte[] b = new byte[value.length()];
        int i = 0;
        int bi = 0;
        while (i < value.length()) {
            char c = value.charAt(i);
            if (c == '%') {
                final String hex = value.substring(i + 1, i + 3);
                c = (char)Integer.parseInt(hex, 16);
                i += 2;
            }
            b[bi++] = (byte)c;
            ++i;
        }
        return new String(b, 0, bi, MimeUtility.javaCharset(charset));
    }
    
    static {
        ParameterList.encodeParameters = false;
        ParameterList.decodeParameters = false;
        ParameterList.decodeParametersStrict = false;
        ParameterList.applehack = false;
        try {
            String s = System.getProperty("mail.mime.encodeparameters");
            ParameterList.encodeParameters = (s != null && s.equalsIgnoreCase("true"));
            s = System.getProperty("mail.mime.decodeparameters");
            ParameterList.decodeParameters = (s != null && s.equalsIgnoreCase("true"));
            s = System.getProperty("mail.mime.decodeparameters.strict");
            ParameterList.decodeParametersStrict = (s != null && s.equalsIgnoreCase("true"));
            s = System.getProperty("mail.mime.applefilenames");
            ParameterList.applehack = (s != null && s.equalsIgnoreCase("true"));
        }
        catch (SecurityException ex) {}
        hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
    
    private static class Value
    {
        String value;
        String charset;
        String encodedValue;
    }
    
    private static class MultiValue extends ArrayList
    {
        String value;
    }
    
    private static class ParamEnum implements Enumeration
    {
        private Iterator it;
        
        ParamEnum(final Iterator it) {
            this.it = it;
        }
        
        public boolean hasMoreElements() {
            return this.it.hasNext();
        }
        
        public Object nextElement() {
            return this.it.next();
        }
    }
    
    private static class ToStringBuffer
    {
        private int used;
        private StringBuffer sb;
        
        public ToStringBuffer(final int used) {
            this.sb = new StringBuffer();
            this.used = used;
        }
        
        public void addNV(final String name, String value) {
            value = quote(value);
            this.sb.append("; ");
            this.used += 2;
            final int len = name.length() + value.length() + 1;
            if (this.used + len > 76) {
                this.sb.append("\r\n\t");
                this.used = 8;
            }
            this.sb.append(name).append('=');
            this.used += name.length() + 1;
            if (this.used + value.length() > 76) {
                final String s = MimeUtility.fold(this.used, value);
                this.sb.append(s);
                final int lastlf = s.lastIndexOf(10);
                if (lastlf >= 0) {
                    this.used += s.length() - lastlf - 1;
                }
                else {
                    this.used += s.length();
                }
            }
            else {
                this.sb.append(value);
                this.used += value.length();
            }
        }
        
        public String toString() {
            return this.sb.toString();
        }
    }
}
