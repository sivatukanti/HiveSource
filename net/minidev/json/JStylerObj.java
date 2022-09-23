// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json;

import java.io.IOException;

class JStylerObj
{
    public static final MPSimple MP_SIMPLE;
    public static final MPTrue MP_TRUE;
    public static final MPAgressive MP_AGGRESIVE;
    public static final EscapeLT ESCAPE_LT;
    public static final Escape4Web ESCAPE4Web;
    
    static {
        MP_SIMPLE = new MPSimple(null);
        MP_TRUE = new MPTrue(null);
        MP_AGGRESIVE = new MPAgressive(null);
        ESCAPE_LT = new EscapeLT(null);
        ESCAPE4Web = new Escape4Web(null);
    }
    
    public static boolean isSpace(final char c) {
        return c == '\r' || c == '\n' || c == '\t' || c == ' ';
    }
    
    public static boolean isSpecialChar(final char c) {
        return c == '\b' || c == '\f' || c == '\n';
    }
    
    public static boolean isSpecialOpen(final char c) {
        return c == '{' || c == '[' || c == ',' || c == ':';
    }
    
    public static boolean isSpecialClose(final char c) {
        return c == '}' || c == ']' || c == ',' || c == ':';
    }
    
    public static boolean isSpecial(final char c) {
        return c == '{' || c == '[' || c == ',' || c == '}' || c == ']' || c == ':' || c == '\'' || c == '\"';
    }
    
    public static boolean isUnicode(final char c) {
        return (c >= '\0' && c <= '\u001f') || (c >= '\u007f' && c <= '\u009f') || (c >= '\u2000' && c <= '\u20ff');
    }
    
    public static boolean isKeyword(final String s) {
        if (s.length() < 3) {
            return false;
        }
        final char c = s.charAt(0);
        if (c == 'n') {
            return s.equals("null");
        }
        if (c == 't') {
            return s.equals("true");
        }
        if (c == 'f') {
            return s.equals("false");
        }
        return c == 'N' && s.equals("NaN");
    }
    
    private static class MPTrue implements MustProtect
    {
        @Override
        public boolean mustBeProtect(final String s) {
            return true;
        }
    }
    
    private static class MPSimple implements MustProtect
    {
        @Override
        public boolean mustBeProtect(final String s) {
            if (s == null) {
                return false;
            }
            final int len = s.length();
            if (len == 0) {
                return true;
            }
            if (s.trim() != s) {
                return true;
            }
            char ch = s.charAt(0);
            if ((ch >= '0' && ch <= '9') || ch == '-') {
                return true;
            }
            for (int i = 0; i < len; ++i) {
                ch = s.charAt(i);
                if (JStylerObj.isSpace(ch)) {
                    return true;
                }
                if (JStylerObj.isSpecial(ch)) {
                    return true;
                }
                if (JStylerObj.isSpecialChar(ch)) {
                    return true;
                }
                if (JStylerObj.isUnicode(ch)) {
                    return true;
                }
            }
            return JStylerObj.isKeyword(s);
        }
    }
    
    private static class MPAgressive implements MustProtect
    {
        @Override
        public boolean mustBeProtect(final String s) {
            if (s == null) {
                return false;
            }
            final int len = s.length();
            if (len == 0) {
                return true;
            }
            if (s.trim() != s) {
                return true;
            }
            char ch = s.charAt(0);
            if (JStylerObj.isSpecial(ch) || JStylerObj.isUnicode(ch)) {
                return true;
            }
            for (int i = 1; i < len; ++i) {
                ch = s.charAt(i);
                if (JStylerObj.isSpecialClose(ch) || JStylerObj.isUnicode(ch)) {
                    return true;
                }
            }
            if (JStylerObj.isKeyword(s)) {
                return true;
            }
            ch = s.charAt(0);
            if ((ch < '0' || ch > '9') && ch != '-') {
                return false;
            }
            int p;
            for (p = 1; p < len; ++p) {
                ch = s.charAt(p);
                if (ch < '0') {
                    break;
                }
                if (ch > '9') {
                    break;
                }
            }
            if (p == len) {
                return true;
            }
            if (ch == '.') {
                ++p;
            }
            while (p < len) {
                ch = s.charAt(p);
                if (ch < '0') {
                    break;
                }
                if (ch > '9') {
                    break;
                }
                ++p;
            }
            if (p == len) {
                return true;
            }
            if (ch == 'E' || ch == 'e') {
                if (++p == len) {
                    return false;
                }
                ch = s.charAt(p);
                if (ch == '+' || ch == '-') {
                    ++p;
                    ch = s.charAt(p);
                }
            }
            if (p == len) {
                return false;
            }
            while (p < len) {
                ch = s.charAt(p);
                if (ch < '0') {
                    break;
                }
                if (ch > '9') {
                    break;
                }
                ++p;
            }
            return p == len;
        }
    }
    
    private static class EscapeLT implements StringProtector
    {
        @Override
        public void escape(final String s, final Appendable out) {
            try {
                for (int len = s.length(), i = 0; i < len; ++i) {
                    final char ch = s.charAt(i);
                    switch (ch) {
                        case '\"': {
                            out.append("\\\"");
                            break;
                        }
                        case '\\': {
                            out.append("\\\\");
                            break;
                        }
                        case '\b': {
                            out.append("\\b");
                            break;
                        }
                        case '\f': {
                            out.append("\\f");
                            break;
                        }
                        case '\n': {
                            out.append("\\n");
                            break;
                        }
                        case '\r': {
                            out.append("\\r");
                            break;
                        }
                        case '\t': {
                            out.append("\\t");
                            break;
                        }
                        default: {
                            if ((ch >= '\0' && ch <= '\u001f') || (ch >= '\u007f' && ch <= '\u009f') || (ch >= '\u2000' && ch <= '\u20ff')) {
                                out.append("\\u");
                                final String hex = "0123456789ABCDEF";
                                out.append(hex.charAt(ch >> 12 & 0xF));
                                out.append(hex.charAt(ch >> 8 & 0xF));
                                out.append(hex.charAt(ch >> 4 & 0xF));
                                out.append(hex.charAt(ch >> 0 & 0xF));
                                break;
                            }
                            out.append(ch);
                            break;
                        }
                    }
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Impossible Exeption");
            }
        }
    }
    
    private static class Escape4Web implements StringProtector
    {
        @Override
        public void escape(final String s, final Appendable sb) {
            try {
                for (int len = s.length(), i = 0; i < len; ++i) {
                    final char ch = s.charAt(i);
                    switch (ch) {
                        case '\"': {
                            sb.append("\\\"");
                            break;
                        }
                        case '\\': {
                            sb.append("\\\\");
                            break;
                        }
                        case '\b': {
                            sb.append("\\b");
                            break;
                        }
                        case '\f': {
                            sb.append("\\f");
                            break;
                        }
                        case '\n': {
                            sb.append("\\n");
                            break;
                        }
                        case '\r': {
                            sb.append("\\r");
                            break;
                        }
                        case '\t': {
                            sb.append("\\t");
                            break;
                        }
                        case '/': {
                            sb.append("\\/");
                            break;
                        }
                        default: {
                            if ((ch >= '\0' && ch <= '\u001f') || (ch >= '\u007f' && ch <= '\u009f') || (ch >= '\u2000' && ch <= '\u20ff')) {
                                sb.append("\\u");
                                final String hex = "0123456789ABCDEF";
                                sb.append(hex.charAt(ch >> 12 & 0xF));
                                sb.append(hex.charAt(ch >> 8 & 0xF));
                                sb.append(hex.charAt(ch >> 4 & 0xF));
                                sb.append(hex.charAt(ch >> 0 & 0xF));
                                break;
                            }
                            sb.append(ch);
                            break;
                        }
                    }
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Impossible Error");
            }
        }
    }
    
    public interface StringProtector
    {
        void escape(final String p0, final Appendable p1);
    }
    
    public interface MustProtect
    {
        boolean mustBeProtect(final String p0);
    }
}
