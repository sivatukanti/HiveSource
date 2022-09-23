// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler.generated;

public interface RccConstants
{
    public static final int EOF = 0;
    public static final int MODULE_TKN = 11;
    public static final int RECORD_TKN = 12;
    public static final int INCLUDE_TKN = 13;
    public static final int BYTE_TKN = 14;
    public static final int BOOLEAN_TKN = 15;
    public static final int INT_TKN = 16;
    public static final int LONG_TKN = 17;
    public static final int FLOAT_TKN = 18;
    public static final int DOUBLE_TKN = 19;
    public static final int USTRING_TKN = 20;
    public static final int BUFFER_TKN = 21;
    public static final int VECTOR_TKN = 22;
    public static final int MAP_TKN = 23;
    public static final int LBRACE_TKN = 24;
    public static final int RBRACE_TKN = 25;
    public static final int LT_TKN = 26;
    public static final int GT_TKN = 27;
    public static final int SEMICOLON_TKN = 28;
    public static final int COMMA_TKN = 29;
    public static final int DOT_TKN = 30;
    public static final int CSTRING_TKN = 31;
    public static final int IDENT_TKN = 32;
    public static final int DEFAULT = 0;
    public static final int WithinOneLineComment = 1;
    public static final int WithinMultiLineComment = 2;
    public static final String[] tokenImage = { "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"//\"", "<token of kind 6>", "<token of kind 7>", "\"/*\"", "\"*/\"", "<token of kind 10>", "\"module\"", "\"class\"", "\"include\"", "\"byte\"", "\"boolean\"", "\"int\"", "\"long\"", "\"float\"", "\"double\"", "\"ustring\"", "\"buffer\"", "\"vector\"", "\"map\"", "\"{\"", "\"}\"", "\"<\"", "\">\"", "\";\"", "\",\"", "\".\"", "<CSTRING_TKN>", "<IDENT_TKN>" };
}
