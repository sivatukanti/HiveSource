// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.StringTokenizer;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import org.datanucleus.exceptions.NucleusException;
import java.net.URLDecoder;
import java.util.jar.JarFile;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils
{
    public static String getStringFromStackTrace(final Throwable ex) {
        if (ex == null) {
            return "";
        }
        final StringWriter str = new StringWriter();
        final PrintWriter writer = new PrintWriter(str);
        try {
            ex.printStackTrace(writer);
            return str.getBuffer().toString();
        }
        finally {
            try {
                str.close();
                writer.close();
            }
            catch (IOException ex2) {}
        }
    }
    
    public static File getFileForFilename(final String filename) {
        return new File(getDecodedStringFromURLString(filename));
    }
    
    public static JarFile getJarFileForFilename(final String filename) throws IOException {
        return new JarFile(getDecodedStringFromURLString(filename));
    }
    
    public static String getDecodedStringFromURLString(final String urlString) {
        final String str = urlString.replace("+", "%2B");
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            throw new NucleusException("Error attempting to decode string", uee);
        }
    }
    
    public static String getEncodedURLStringFromString(final String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        }
        catch (UnsupportedEncodingException uee) {
            throw new NucleusException("Error attempting to encode string", uee);
        }
    }
    
    public static String replaceAll(final String theString, final String toReplace, final String replacement) {
        if (theString == null) {
            return null;
        }
        if (theString.indexOf(toReplace) == -1) {
            return theString;
        }
        final StringBuilder stringBuffer = new StringBuilder(theString);
        int index = theString.length();
        final int offset = toReplace.length();
        while ((index = theString.lastIndexOf(toReplace, index - 1)) > -1) {
            stringBuffer.replace(index, index + offset, replacement);
        }
        return stringBuffer.toString();
    }
    
    public static boolean isWhitespace(final String str) {
        return str == null || str.length() == 0 || str.trim().length() == 0;
    }
    
    public static boolean areStringsEqual(final String str1, final String str2) {
        return (str1 == null && str2 == null) || ((str1 != null || str2 == null) && (str1 == null || str2 != null) && str1.equals(str2));
    }
    
    public static String leftAlignedPaddedString(final String input, final int length) {
        if (length <= 0) {
            return null;
        }
        final StringBuilder output = new StringBuilder();
        final char space = ' ';
        if (input != null) {
            if (input.length() < length) {
                output.append(input);
                for (int i = input.length(); i < length; ++i) {
                    output.append(space);
                }
            }
            else {
                output.append(input.substring(0, length));
            }
        }
        else {
            for (int i = 0; i < length; ++i) {
                output.append(space);
            }
        }
        return output.toString();
    }
    
    public static String rightAlignedPaddedString(final String input, final int length) {
        if (length <= 0) {
            return null;
        }
        final StringBuilder output = new StringBuilder();
        final char space = ' ';
        if (input != null) {
            if (input.length() < length) {
                for (int i = input.length(); i < length; ++i) {
                    output.append(space);
                }
                output.append(input);
            }
            else {
                output.append(input.substring(0, length));
            }
        }
        else {
            for (int i = 0; i < length; ++i) {
                output.append(space);
            }
        }
        return output.toString();
    }
    
    public static String[] split(final String valuesString, final String token) {
        String[] values;
        if (valuesString != null) {
            final StringTokenizer tokenizer = new StringTokenizer(valuesString, token);
            values = new String[tokenizer.countTokens()];
            int count = 0;
            while (tokenizer.hasMoreTokens()) {
                values[count++] = tokenizer.nextToken();
            }
        }
        else {
            values = null;
        }
        return values;
    }
    
    public static String toJVMIDString(final Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(obj));
    }
    
    public static String booleanArrayToString(final boolean[] ba) {
        if (ba == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ba.length; ++i) {
            sb.append(ba[i] ? 'Y' : 'N');
        }
        sb.append(']');
        return sb.toString();
    }
    
    public static String intArrayToString(final int[] ia) {
        if (ia == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ia.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(ia[i]);
        }
        sb.append(']');
        return sb.toString();
    }
    
    public static String objectArrayToString(final Object[] arr) {
        if (arr == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(arr[i]);
        }
        sb.append(']');
        return sb.toString();
    }
    
    public static String collectionToString(final Collection coll) {
        if (coll == null) {
            return "<null>";
        }
        if (coll.isEmpty()) {
            return "<none>";
        }
        final StringBuilder s = new StringBuilder();
        for (final Object obj : coll) {
            if (s.length() > 0) {
                s.append(", ");
            }
            s.append(obj);
        }
        return s.toString();
    }
    
    public static String mapToString(final Map map) {
        if (map == null) {
            return "<null>";
        }
        if (map.isEmpty()) {
            return "<none>";
        }
        final StringBuilder s = new StringBuilder();
        for (final Map.Entry entry : map.entrySet()) {
            if (s.length() > 0) {
                s.append(", ");
            }
            s.append("<" + entry.getKey() + "," + entry.getValue() + ">");
        }
        return s.toString();
    }
    
    public static int getIntValueForProperty(final Properties props, final String propName, final int defaultValue) {
        int value = defaultValue;
        if (props != null && props.containsKey(propName)) {
            try {
                value = Integer.valueOf(props.getProperty(propName));
            }
            catch (NumberFormatException ex) {}
        }
        return value;
    }
    
    public static boolean isEmpty(final String s) {
        return s == null || s.length() == 0;
    }
    
    public static boolean notEmpty(final String s) {
        return s != null && s.length() > 0;
    }
    
    public static String exponentialFormatBigDecimal(final BigDecimal bd) {
        String digits = bd.unscaledValue().abs().toString();
        int scale = bd.scale();
        int len;
        for (len = digits.length(); len > 1 && digits.charAt(len - 1) == '0'; --len) {
            --scale;
        }
        if (len < digits.length()) {
            digits = digits.substring(0, len);
        }
        final StringBuilder sb = new StringBuilder();
        if (bd.signum() < 0) {
            sb.append('-');
        }
        final int exponent = len - scale;
        if (exponent < 0 || exponent > len) {
            sb.append('.').append(digits).append('E').append(exponent);
        }
        else if (exponent == len) {
            sb.append(digits);
        }
        else {
            sb.append(digits.substring(0, exponent)).append('.').append(digits.substring(exponent));
        }
        return sb.toString();
    }
    
    public static String removeSpecialTagsFromString(String str) {
        if (str == null) {
            return null;
        }
        str = str.replace('\n', ' ');
        str = str.replace('\t', ' ');
        str = str.replace('\r', ' ');
        return str;
    }
}
