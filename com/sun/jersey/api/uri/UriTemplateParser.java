// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.uri;

import java.util.regex.Matcher;
import java.util.NoSuchElementException;
import java.util.regex.PatternSyntaxException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Set;

public class UriTemplateParser
{
    static final int[] EMPTY_INT_ARRAY;
    private static final Set<Character> RESERVED_REGEX_CHARACTERS;
    private static final Pattern TEMPLATE_VALUE_PATTERN;
    private final String template;
    private final StringBuffer regex;
    private final StringBuffer normalizedTemplate;
    private final StringBuffer literalCharactersBuffer;
    private final Pattern pattern;
    private final List<String> names;
    private final List<Integer> groupCounts;
    private final Map<String, Pattern> nameToPattern;
    private int numOfExplicitRegexes;
    private int literalCharacters;
    
    private static Set<Character> initReserved() {
        final char[] reserved = { '.', '^', '&', '!', '?', '-', ':', '<', '(', '[', '$', '=', ')', ']', ',', '>', '*', '+', '|' };
        final Set<Character> s = new HashSet<Character>(reserved.length);
        for (final char c : reserved) {
            s.add(c);
        }
        return s;
    }
    
    public UriTemplateParser(final String template) throws IllegalArgumentException {
        this.regex = new StringBuffer();
        this.normalizedTemplate = new StringBuffer();
        this.literalCharactersBuffer = new StringBuffer();
        this.names = new ArrayList<String>();
        this.groupCounts = new ArrayList<Integer>();
        this.nameToPattern = new HashMap<String, Pattern>();
        if (template == null || template.length() == 0) {
            throw new IllegalArgumentException();
        }
        this.template = template;
        this.parse(new StringCharacterIterator(template));
        try {
            this.pattern = Pattern.compile(this.regex.toString());
        }
        catch (PatternSyntaxException ex) {
            throw new IllegalArgumentException("Invalid syntax for the template expression '" + (Object)this.regex + "'", ex);
        }
    }
    
    public final String getTemplate() {
        return this.template;
    }
    
    public final Pattern getPattern() {
        return this.pattern;
    }
    
    public final String getNormalizedTemplate() {
        return this.normalizedTemplate.toString();
    }
    
    public final Map<String, Pattern> getNameToPattern() {
        return this.nameToPattern;
    }
    
    public final List<String> getNames() {
        return this.names;
    }
    
    public final List<Integer> getGroupCounts() {
        return this.groupCounts;
    }
    
    public final int[] getGroupIndexes() {
        if (this.names.isEmpty()) {
            return UriTemplateParser.EMPTY_INT_ARRAY;
        }
        final int[] indexes = new int[this.names.size() + 1];
        indexes[0] = 1;
        for (int i = 1; i < indexes.length; ++i) {
            indexes[i] = indexes[i - 1] + this.groupCounts.get(i - 1);
        }
        for (int i = 0; i < indexes.length; ++i) {
            if (indexes[i] != i + 1) {
                return indexes;
            }
        }
        return UriTemplateParser.EMPTY_INT_ARRAY;
    }
    
    public final int getNumberOfExplicitRegexes() {
        return this.numOfExplicitRegexes;
    }
    
    public final int getNumberOfLiteralCharacters() {
        return this.literalCharacters;
    }
    
    protected String encodeLiteralCharacters(final String characters) {
        return characters;
    }
    
    private void parse(final CharacterIterator ci) {
        try {
            while (ci.hasNext()) {
                final char c = ci.next();
                if (c == '{') {
                    this.processLiteralCharacters();
                    this.parseName(ci);
                }
                else {
                    this.literalCharactersBuffer.append(c);
                }
            }
            this.processLiteralCharacters();
        }
        catch (NoSuchElementException ex) {
            throw new IllegalArgumentException("Invalid syntax for the template, \"" + this.template + "\". Check if a path parameter is terminated with a '}'.", ex);
        }
    }
    
    private void processLiteralCharacters() {
        if (this.literalCharactersBuffer.length() > 0) {
            this.literalCharacters += this.literalCharactersBuffer.length();
            final String s = this.encodeLiteralCharacters(this.literalCharactersBuffer.toString());
            this.normalizedTemplate.append(s);
            for (int i = 0; i < s.length(); ++i) {
                final char c = s.charAt(i);
                if (UriTemplateParser.RESERVED_REGEX_CHARACTERS.contains(c)) {
                    this.regex.append("\\");
                }
                this.regex.append(c);
            }
            this.literalCharactersBuffer.setLength(0);
        }
    }
    
    private void parseName(final CharacterIterator ci) {
        char c = this.consumeWhiteSpace(ci);
        final StringBuilder nameBuffer = new StringBuilder();
        if (Character.isLetterOrDigit(c) || c == '_') {
            nameBuffer.append(c);
            String nameRegexString = "";
            while (true) {
                c = ci.next();
                if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != '.') {
                    break;
                }
                nameBuffer.append(c);
            }
            if (c == ':') {
                nameRegexString = this.parseRegex(ci);
            }
            else if (c != '}') {
                if (c != ' ') {
                    throw new IllegalArgumentException("Illegal character '" + c + "' at position " + ci.pos() + " is not allowed as part of a name");
                }
                c = this.consumeWhiteSpace(ci);
                if (c == ':') {
                    nameRegexString = this.parseRegex(ci);
                }
                else if (c != '}') {
                    throw new IllegalArgumentException("Illegal character '" + c + "' at position " + ci.pos() + " is not allowed after a name");
                }
            }
            final String name = nameBuffer.toString();
            this.names.add(name);
            try {
                if (nameRegexString.length() > 0) {
                    ++this.numOfExplicitRegexes;
                }
                final Pattern namePattern = (nameRegexString.length() == 0) ? UriTemplateParser.TEMPLATE_VALUE_PATTERN : Pattern.compile(nameRegexString);
                if (this.nameToPattern.containsKey(name)) {
                    if (!this.nameToPattern.get(name).equals(namePattern)) {
                        throw new IllegalArgumentException("The name '" + name + "' is declared " + "more than once with different regular expressions");
                    }
                }
                else {
                    this.nameToPattern.put(name, namePattern);
                }
                final Matcher m = namePattern.matcher("");
                final int g = m.groupCount();
                this.groupCounts.add(g + 1);
                this.regex.append('(').append(namePattern).append(')');
                this.normalizedTemplate.append('{').append(name).append('}');
            }
            catch (PatternSyntaxException ex) {
                throw new IllegalArgumentException("Invalid syntax for the expression '" + nameRegexString + "' associated with the name '" + name + "'", ex);
            }
            return;
        }
        throw new IllegalArgumentException("Illegal character '" + c + "' at position " + ci.pos() + " is not as the start of a name");
    }
    
    private String parseRegex(final CharacterIterator ci) {
        final StringBuilder regexBuffer = new StringBuilder();
        int braceCount = 1;
        while (true) {
            final char c = ci.next();
            if (c == '{') {
                ++braceCount;
            }
            else if (c == '}' && --braceCount == 0) {
                break;
            }
            regexBuffer.append(c);
        }
        return regexBuffer.toString().trim();
    }
    
    private char consumeWhiteSpace(final CharacterIterator ci) {
        char c;
        do {
            c = ci.next();
        } while (Character.isWhitespace(c));
        return c;
    }
    
    static {
        EMPTY_INT_ARRAY = new int[0];
        RESERVED_REGEX_CHARACTERS = initReserved();
        TEMPLATE_VALUE_PATTERN = Pattern.compile("[^/]+?");
    }
    
    private static final class StringCharacterIterator implements CharacterIterator
    {
        private int pos;
        private String s;
        
        public StringCharacterIterator(final String s) {
            this.s = s;
            this.pos = 0;
        }
        
        @Override
        public boolean hasNext() {
            return this.pos < this.s.length();
        }
        
        @Override
        public char next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.s.charAt(this.pos++);
        }
        
        @Override
        public char peek() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.s.charAt(this.pos);
        }
        
        @Override
        public int pos() {
            if (this.pos == 0) {
                throw new IllegalStateException("Iterator not used yet.");
            }
            return this.pos - 1;
        }
    }
    
    private interface CharacterIterator
    {
        boolean hasNext();
        
        char next();
        
        char peek();
        
        int pos();
    }
}
