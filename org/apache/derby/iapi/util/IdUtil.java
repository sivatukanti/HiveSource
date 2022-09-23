// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

import java.util.Properties;
import java.util.HashSet;
import java.io.IOException;
import java.util.Vector;
import org.apache.derby.iapi.error.StandardException;
import java.io.Reader;
import java.io.StringReader;

public abstract class IdUtil
{
    public static final int DBCP_SCHEMA_NAME = 0;
    public static final int DBCP_SQL_JAR_NAME = 1;
    
    public static String normalToDelimited(final String s) {
        return StringUtil.quoteString(s, '\"');
    }
    
    public static String mkQualifiedName(final String s, final String s2) {
        if (null == s) {
            return normalToDelimited(s2);
        }
        return normalToDelimited(s) + "." + normalToDelimited(s2);
    }
    
    public static String mkQualifiedName(final String[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                sb.append(".");
            }
            sb.append(normalToDelimited(array[i]));
        }
        return sb.toString();
    }
    
    public static String[] parseMultiPartSQLIdentifier(final String s) throws StandardException {
        final StringReader stringReader = new StringReader(s);
        final String[] multiPartSQLIdentifier = parseMultiPartSQLIdentifier(stringReader);
        verifyEmpty(stringReader);
        return multiPartSQLIdentifier;
    }
    
    private static String[] parseMultiPartSQLIdentifier(final StringReader stringReader) throws StandardException {
        final Vector vector = new Vector<String>();
        while (true) {
            vector.add(parseId(stringReader, true));
            try {
                stringReader.mark(0);
                final int read = stringReader.read();
                if (read != 46) {
                    if (read != -1) {
                        stringReader.reset();
                    }
                    break;
                }
                continue;
            }
            catch (IOException ex) {
                throw StandardException.newException("XCXA0.S", ex);
            }
        }
        final String[] anArray = new String[vector.size()];
        vector.copyInto(anArray);
        return anArray;
    }
    
    public static String parseSQLIdentifier(final String s) throws StandardException {
        final StringReader stringReader = new StringReader(s);
        final String id = parseId(stringReader, true);
        verifyEmpty(stringReader);
        return id;
    }
    
    private static String parseId(final StringReader stringReader, final boolean b) throws StandardException {
        try {
            stringReader.mark(0);
            final int read = stringReader.read();
            if (read == -1) {
                throw StandardException.newException("XCXA0.S");
            }
            stringReader.reset();
            if (read == 34) {
                return parseQId(stringReader, b);
            }
            return parseUnQId(stringReader, b);
        }
        catch (IOException ex) {
            throw StandardException.newException("XCXA0.S", ex);
        }
    }
    
    public static String SQLIdentifier2CanonicalPropertyUsername(final String s) {
        boolean b = false;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if ((char1 < 'A' || char1 > 'Z') && char1 != '_' && (i <= 0 || char1 < '0' || char1 > '9')) {
                b = true;
                break;
            }
        }
        String s2;
        if (!b) {
            s2 = s.toLowerCase();
        }
        else {
            s2 = normalToDelimited(s);
        }
        return s2;
    }
    
    private static String parseUnQId(final StringReader stringReader, final boolean b) throws IOException, StandardException {
        final StringBuffer sb = new StringBuffer();
        boolean b2 = true;
        int read;
        while (true) {
            stringReader.mark(0);
            if (!idChar(b2, read = stringReader.read())) {
                break;
            }
            sb.append((char)read);
            b2 = false;
        }
        if (read != -1) {
            stringReader.reset();
        }
        final String string = sb.toString();
        if (b) {
            return StringUtil.SQLToUpperCase(string);
        }
        return string;
    }
    
    private static boolean idChar(final boolean b, final int n) {
        return (n >= 97 && n <= 122) || (n >= 65 && n <= 90) || (!b && n >= 48 && n <= 57) || (!b && n == 95) || Character.isLetter((char)n) || (!b && Character.isDigit((char)n));
    }
    
    private static String parseQId(final StringReader stringReader, final boolean b) throws IOException, StandardException {
        final StringBuffer sb = new StringBuffer();
        if (stringReader.read() != 34) {
            throw StandardException.newException("XCXA0.S");
        }
        while (true) {
            final int read = stringReader.read();
            if (read == 34) {
                stringReader.mark(0);
                final int read2 = stringReader.read();
                if (read2 != 34) {
                    if (read2 != -1) {
                        stringReader.reset();
                    }
                    if (sb.length() == 0) {
                        throw StandardException.newException("XCXA0.S");
                    }
                    if (b) {
                        return sb.toString();
                    }
                    return normalToDelimited(sb.toString());
                }
            }
            else if (read == -1) {
                throw StandardException.newException("XCXA0.S");
            }
            sb.append((char)read);
        }
    }
    
    private static void verifyEmpty(final Reader reader) throws StandardException {
        try {
            if (reader.read() != -1) {
                throw StandardException.newException("XCXA0.S");
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XCXA0.S", ex);
        }
    }
    
    public static String[][] parseDbClassPath(final String s) throws StandardException {
        if (s.length() == 0) {
            return new String[0][];
        }
        final Vector vector = new Vector<String[]>();
        final StringReader stringReader = new StringReader(s);
        try {
            while (true) {
                final String[] multiPartSQLIdentifier = parseMultiPartSQLIdentifier(stringReader);
                if (multiPartSQLIdentifier.length != 2) {
                    throw StandardException.newException("XCXB0.S", s);
                }
                vector.add(multiPartSQLIdentifier);
                final int read = stringReader.read();
                if (read == 58) {
                    continue;
                }
                if (read != -1) {
                    throw StandardException.newException("XCXB0.S", s);
                }
                break;
            }
        }
        catch (StandardException ex) {
            if (ex.getMessageId().equals("XCXA0.S")) {
                throw StandardException.newException("XCXB0.S", ex, s);
            }
            throw ex;
        }
        catch (IOException ex2) {
            throw StandardException.newException("XCXB0.S", ex2, s);
        }
        final String[][] anArray = new String[vector.size()][];
        vector.copyInto(anArray);
        return anArray;
    }
    
    public static String[] parseIdList(final String s) throws StandardException {
        if (s == null) {
            return null;
        }
        final StringReader stringReader = new StringReader(s);
        final String[] idList = parseIdList(stringReader, true);
        verifyEmpty(stringReader);
        return idList;
    }
    
    private static String[] parseIdList(final StringReader stringReader, final boolean b) throws StandardException {
        final Vector vector = new Vector<String>();
        while (true) {
            try {
                vector.add(parseId(stringReader, b));
                stringReader.mark(0);
                final int read = stringReader.read();
                if (read != 44) {
                    if (read != -1) {
                        stringReader.reset();
                    }
                    break;
                }
                continue;
            }
            catch (StandardException ex) {
                if (ex.getMessageId().equals("XCXC0.S")) {
                    throw StandardException.newException("XCXC0.S", ex);
                }
                throw ex;
            }
            catch (IOException ex2) {
                throw StandardException.newException("XCXC0.S", ex2);
            }
        }
        if (vector.size() == 0) {
            return null;
        }
        final String[] anArray = new String[vector.size()];
        vector.copyInto(anArray);
        return anArray;
    }
    
    public static String intersect(final String[] array, final String[] array2) {
        if (array == null || array2 == null) {
            return null;
        }
        final HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < array2.length; ++i) {
            set.add(array2[i]);
        }
        final Vector<String> vector = new Vector<String>();
        for (int j = 0; j < array.length; ++j) {
            if (set.contains(array[j])) {
                vector.add(array[j]);
            }
        }
        return vectorToIdList(vector, true);
    }
    
    private static String vectorToIdList(final Vector vector, final boolean b) {
        if (vector.size() == 0) {
            return null;
        }
        final String[] anArray = new String[vector.size()];
        vector.copyInto(anArray);
        if (b) {
            return mkIdList(anArray);
        }
        return mkIdListAsEntered(anArray);
    }
    
    public static String getUserAuthorizationId(final String s) throws StandardException {
        try {
            if (s != null) {
                return parseSQLIdentifier(s);
            }
        }
        catch (StandardException ex) {}
        throw StandardException.newException("28502", s);
    }
    
    public static String getUserNameFromURLProps(final Properties properties) {
        String property = properties.getProperty("user", "APP");
        if (property.equals("")) {
            property = "APP";
        }
        return property;
    }
    
    public static String dups(final String[] array) {
        if (array == null) {
            return null;
        }
        final HashSet set = new HashSet<String>();
        final Vector<String> vector = new Vector<String>();
        for (int i = 0; i < array.length; ++i) {
            if (!set.contains(array[i])) {
                set.add(array[i]);
            }
            else {
                vector.add(array[i]);
            }
        }
        return vectorToIdList(vector, true);
    }
    
    public static String pruneDups(final String s) throws StandardException {
        if (s == null) {
            return null;
        }
        final String[] idList = parseIdList(s);
        final String[] idList2 = parseIdList(new StringReader(s), false);
        final HashSet set = new HashSet<String>();
        final Vector<String> vector = new Vector<String>();
        for (int i = 0; i < idList.length; ++i) {
            if (!set.contains(idList[i])) {
                set.add(idList[i]);
                vector.add(idList2[i]);
            }
        }
        return vectorToIdList(vector, false);
    }
    
    public static String mkIdList(final String[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(normalToDelimited(array[i]));
        }
        return sb.toString();
    }
    
    private static String mkIdListAsEntered(final String[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }
    
    public static boolean idOnList(final String s, final String s2) throws StandardException {
        if (s2 == null) {
            return false;
        }
        final String[] idList = parseIdList(s2);
        for (int i = 0; i < idList.length; ++i) {
            if (s.equals(idList[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static String deleteId(final String s, final String s2) throws StandardException {
        if (s2 == null) {
            return null;
        }
        final Vector vector = new Vector<String>();
        final String[] idList = parseIdList(new StringReader(s2), false);
        for (int i = 0; i < idList.length; ++i) {
            if (!s.equals(parseSQLIdentifier(idList[i]))) {
                vector.add(idList[i]);
            }
        }
        if (vector.size() == 0) {
            return null;
        }
        return vectorToIdList(vector, false);
    }
    
    public static String appendNormalToList(final String s, final String str) throws StandardException {
        final String normalToDelimited = normalToDelimited(s);
        if (str == null) {
            return normalToDelimited;
        }
        return str + "," + normalToDelimited;
    }
    
    public static String parseRoleId(String s) throws StandardException {
        s = s.trim();
        if (StringUtil.SQLToUpperCase(s).equals("NONE")) {
            throw StandardException.newException("XCXA0.S");
        }
        s = parseSQLIdentifier(s);
        checkIdentifierLengthLimit(s, 128);
        return s;
    }
    
    public static void checkIdentifierLengthLimit(final String s, final int i) throws StandardException {
        if (s.length() > i) {
            throw StandardException.newException("42622", s, String.valueOf(i));
        }
    }
}
