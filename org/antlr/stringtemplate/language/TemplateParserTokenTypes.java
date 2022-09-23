// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

public interface TemplateParserTokenTypes
{
    public static final int EOF = 1;
    public static final int NULL_TREE_LOOKAHEAD = 3;
    public static final int LITERAL = 4;
    public static final int NEWLINE = 5;
    public static final int ACTION = 6;
    public static final int IF = 7;
    public static final int ELSEIF = 8;
    public static final int ELSE = 9;
    public static final int ENDIF = 10;
    public static final int REGION_REF = 11;
    public static final int REGION_DEF = 12;
    public static final int EXPR = 13;
    public static final int TEMPLATE = 14;
    public static final int IF_EXPR = 15;
    public static final int ESC_CHAR = 16;
    public static final int ESC = 17;
    public static final int HEX = 18;
    public static final int SUBTEMPLATE = 19;
    public static final int NESTED_PARENS = 20;
    public static final int INDENT = 21;
    public static final int COMMENT = 22;
    public static final int LINE_BREAK = 23;
}
