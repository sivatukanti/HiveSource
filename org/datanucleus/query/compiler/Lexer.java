// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusUserException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import org.datanucleus.util.Localiser;

public class Lexer
{
    protected static final Localiser LOCALISER;
    private final String parameterPrefixes;
    private final String input;
    protected final CharacterIterator ci;
    private final boolean parseEscapedChars;
    
    public Lexer(final String input, final String paramPrefixes, final boolean parseEscapedChars) {
        this.input = input;
        this.parameterPrefixes = paramPrefixes;
        this.parseEscapedChars = parseEscapedChars;
        this.ci = new StringCharacterIterator(input);
    }
    
    public String getInput() {
        return this.input;
    }
    
    public int getIndex() {
        return this.ci.getIndex();
    }
    
    public int skipWS() {
        final int startIdx = this.ci.getIndex();
        for (char c = this.ci.current(); Character.isWhitespace(c) || c == '\t' || c == '\f' || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r' || c == ' '; c = this.ci.next()) {}
        return startIdx;
    }
    
    public boolean parseEOS() {
        this.skipWS();
        return this.ci.current() == '\uffff';
    }
    
    public boolean parseChar(final char c) {
        this.skipWS();
        if (this.ci.current() == c) {
            this.ci.next();
            return true;
        }
        return false;
    }
    
    public boolean parseChar(final char c, final char unlessFollowedBy) {
        final int savedIdx = this.skipWS();
        if (this.ci.current() == c && this.ci.next() != unlessFollowedBy) {
            return true;
        }
        this.ci.setIndex(savedIdx);
        return false;
    }
    
    public boolean parseString(final String s) {
        final int savedIdx = this.skipWS();
        final int len = s.length();
        char c = this.ci.current();
        for (int i = 0; i < len; ++i) {
            if (c != s.charAt(i)) {
                this.ci.setIndex(savedIdx);
                return false;
            }
            c = this.ci.next();
        }
        return true;
    }
    
    public boolean parseStringIgnoreCase(final String s) {
        final String lowerCasedString = s.toLowerCase();
        final int savedIdx = this.skipWS();
        final int len = lowerCasedString.length();
        char c = this.ci.current();
        for (int i = 0; i < len; ++i) {
            if (Character.toLowerCase(c) != lowerCasedString.charAt(i)) {
                this.ci.setIndex(savedIdx);
                return false;
            }
            c = this.ci.next();
        }
        return true;
    }
    
    public boolean peekStringIgnoreCase(final String s) {
        final String lowerCasedString = s.toLowerCase();
        final int savedIdx = this.skipWS();
        final int len = lowerCasedString.length();
        char c = this.ci.current();
        for (int i = 0; i < len; ++i) {
            if (Character.toLowerCase(c) != lowerCasedString.charAt(i)) {
                this.ci.setIndex(savedIdx);
                return false;
            }
            c = this.ci.next();
        }
        this.ci.setIndex(savedIdx);
        return true;
    }
    
    public String parseIdentifier() {
        this.skipWS();
        char c = this.ci.current();
        if (!Character.isJavaIdentifierStart(c) && this.parameterPrefixes.indexOf(c) < 0) {
            return null;
        }
        final StringBuilder id = new StringBuilder();
        id.append(c);
        while (Character.isJavaIdentifierPart(c = this.ci.next())) {
            id.append(c);
        }
        return id.toString();
    }
    
    public String parseMethod() {
        final int savedIdx = this.ci.getIndex();
        final String id;
        if ((id = this.parseIdentifier()) == null) {
            this.ci.setIndex(savedIdx);
            return null;
        }
        this.skipWS();
        if (!this.parseChar('(')) {
            this.ci.setIndex(savedIdx);
            return null;
        }
        this.ci.setIndex(this.ci.getIndex() - 1);
        return id;
    }
    
    public String parseName() {
        final int savedIdx = this.skipWS();
        String id;
        if ((id = this.parseIdentifier()) == null) {
            return null;
        }
        final StringBuilder qn = new StringBuilder(id);
        while (this.parseChar('.')) {
            if ((id = this.parseIdentifier()) == null) {
                this.ci.setIndex(savedIdx);
                return null;
            }
            qn.append('.').append(id);
        }
        return qn.toString();
    }
    
    public String parseCast() {
        final int savedIdx = this.skipWS();
        final String typeName;
        if (!this.parseChar('(') || (typeName = this.parseName()) == null || !this.parseChar(')')) {
            this.ci.setIndex(savedIdx);
            return null;
        }
        return typeName;
    }
    
    private static final boolean isDecDigit(final char c) {
        return c >= '0' && c <= '9';
    }
    
    private static final boolean isOctDigit(final char c) {
        return c >= '0' && c <= '7';
    }
    
    private static final boolean isHexDigit(final char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }
    
    public BigInteger parseIntegerLiteral() {
        final int savedIdx = this.skipWS();
        final StringBuilder digits = new StringBuilder();
        char c = this.ci.current();
        boolean negate = false;
        if (c == '-') {
            negate = true;
            c = this.ci.next();
        }
        int radix;
        if (c == '0') {
            c = this.ci.next();
            if (c == 'x' || c == 'X') {
                radix = 16;
                for (c = this.ci.next(); isHexDigit(c); c = this.ci.next()) {
                    digits.append(c);
                }
            }
            else if (isOctDigit(c)) {
                radix = 8;
                do {
                    digits.append(c);
                    c = this.ci.next();
                } while (isOctDigit(c));
            }
            else {
                radix = 10;
                digits.append('0');
            }
        }
        else {
            radix = 10;
            while (isDecDigit(c)) {
                digits.append(c);
                c = this.ci.next();
            }
        }
        if (digits.length() == 0) {
            this.ci.setIndex(savedIdx);
            return null;
        }
        if (c == 'l' || c == 'L') {
            this.ci.next();
        }
        if (negate) {
            return new BigInteger(digits.toString(), radix).negate();
        }
        return new BigInteger(digits.toString(), radix);
    }
    
    public BigDecimal parseFloatingPointLiteral() {
        final int savedIdx = this.skipWS();
        final StringBuilder val = new StringBuilder();
        boolean dotSeen = false;
        boolean expSeen = false;
        boolean sfxSeen = false;
        char c = this.ci.current();
        boolean negate = false;
        if (c == '-') {
            negate = true;
            c = this.ci.next();
        }
        while (isDecDigit(c)) {
            val.append(c);
            c = this.ci.next();
        }
        if (c == '.') {
            dotSeen = true;
            val.append(c);
            for (c = this.ci.next(); isDecDigit(c); c = this.ci.next()) {
                val.append(c);
            }
        }
        if (val.length() < (dotSeen ? 2 : 1)) {
            this.ci.setIndex(savedIdx);
            return null;
        }
        if (c == 'e' || c == 'E') {
            expSeen = true;
            val.append(c);
            c = this.ci.next();
            if (c != '+' && c != '-' && !isDecDigit(c)) {
                this.ci.setIndex(savedIdx);
                return null;
            }
            do {
                val.append(c);
                c = this.ci.next();
            } while (isDecDigit(c));
        }
        if (c == 'f' || c == 'F' || c == 'd' || c == 'D') {
            sfxSeen = true;
            this.ci.next();
        }
        if (!dotSeen && !expSeen && !sfxSeen) {
            this.ci.setIndex(savedIdx);
            return null;
        }
        if (negate) {
            return new BigDecimal(val.toString()).negate();
        }
        return new BigDecimal(val.toString());
    }
    
    public Boolean parseBooleanLiteral() {
        final int savedIdx = this.skipWS();
        final String id;
        if ((id = this.parseIdentifier()) == null) {
            return null;
        }
        if (id.equals("true")) {
            return Boolean.TRUE;
        }
        if (id.equals("false")) {
            return Boolean.FALSE;
        }
        this.ci.setIndex(savedIdx);
        return null;
    }
    
    public Boolean parseBooleanLiteralIgnoreCase() {
        final int savedIdx = this.skipWS();
        final String id;
        if ((id = this.parseIdentifier()) == null) {
            return null;
        }
        if (id.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (id.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        this.ci.setIndex(savedIdx);
        return null;
    }
    
    public boolean nextIsSingleQuote() {
        this.skipWS();
        return this.ci.current() == '\'';
    }
    
    public boolean nextIsDot() {
        return this.ci.current() == '.';
    }
    
    public Character parseCharacterLiteral() {
        this.skipWS();
        if (this.ci.current() != '\'') {
            return null;
        }
        char c = this.ci.next();
        if (c == '\uffff') {
            throw new NucleusUserException("Invalid character literal: " + this.input);
        }
        if (this.parseEscapedChars && c == '\\') {
            c = this.parseEscapedCharacter();
        }
        if (this.ci.next() != '\'') {
            throw new NucleusUserException("Invalid character literal: " + this.input);
        }
        this.ci.next();
        return c;
    }
    
    public String parseStringLiteral() {
        this.skipWS();
        final char quote = this.ci.current();
        if (quote != '\"' && quote != '\'') {
            return null;
        }
        final StringBuilder lit = new StringBuilder();
        char c;
        while ((c = this.ci.next()) != quote) {
            if (c == '\uffff') {
                throw new NucleusUserException("Invalid string literal (End of stream): " + this.input);
            }
            if (this.parseEscapedChars && c == '\\') {
                c = this.parseEscapedCharacter();
            }
            lit.append(c);
        }
        this.ci.next();
        return lit.toString();
    }
    
    private char parseEscapedCharacter() {
        char c;
        if (isOctDigit(c = this.ci.next())) {
            int i = c - '0';
            if (isOctDigit(c = this.ci.next())) {
                i = i * 8 + (c - '0');
                if (isOctDigit(c = this.ci.next())) {
                    i = i * 8 + (c - '0');
                }
                else {
                    this.ci.previous();
                }
            }
            else {
                this.ci.previous();
            }
            if (i > 255) {
                throw new NucleusUserException("Invalid character escape: '\\" + Integer.toOctalString(i) + "'");
            }
            return (char)i;
        }
        else {
            switch (c) {
                case 'b': {
                    return '\b';
                }
                case 't': {
                    return '\t';
                }
                case 'n': {
                    return '\n';
                }
                case 'f': {
                    return '\f';
                }
                case 'r': {
                    return '\r';
                }
                case '\"': {
                    return '\"';
                }
                case '\'': {
                    return '\'';
                }
                case '\\': {
                    return '\\';
                }
                default: {
                    throw new NucleusUserException("Invalid character escape: '\\" + c + "'");
                }
            }
        }
    }
    
    public boolean parseNullLiteral() {
        final int savedIdx = this.skipWS();
        final String id;
        if ((id = this.parseIdentifier()) == null) {
            return false;
        }
        if (id.equals("null")) {
            return true;
        }
        this.ci.setIndex(savedIdx);
        return false;
    }
    
    public boolean parseNullLiteralIgnoreCase() {
        final int savedIdx = this.skipWS();
        final String id;
        if ((id = this.parseIdentifier()) == null) {
            return false;
        }
        if (id.equalsIgnoreCase("null")) {
            return true;
        }
        this.ci.setIndex(savedIdx);
        return false;
    }
    
    public String remaining() {
        final int position = this.ci.getIndex();
        final StringBuilder sb = new StringBuilder();
        for (char c = this.ci.current(); c != '\uffff'; c = this.ci.next()) {
            sb.append(c);
        }
        this.ci.setIndex(position);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.input;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
