// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.util.HashMap;

class CharGroup
{
    final int sign;
    final int[] cls;
    private static final int[] code1;
    private static final int[] code2;
    private static final int[] code3;
    static final HashMap<String, CharGroup> PERL_GROUPS;
    private static final int[] code4;
    private static final int[] code5;
    private static final int[] code6;
    private static final int[] code7;
    private static final int[] code8;
    private static final int[] code9;
    private static final int[] code10;
    private static final int[] code11;
    private static final int[] code12;
    private static final int[] code13;
    private static final int[] code14;
    private static final int[] code15;
    private static final int[] code16;
    private static final int[] code17;
    static final HashMap<String, CharGroup> POSIX_GROUPS;
    
    private CharGroup(final int sign, final int[] cls) {
        this.sign = sign;
        this.cls = cls;
    }
    
    static {
        code1 = new int[] { 48, 57 };
        code2 = new int[] { 9, 10, 12, 13, 32, 32 };
        code3 = new int[] { 48, 57, 65, 90, 95, 95, 97, 122 };
        (PERL_GROUPS = new HashMap<String, CharGroup>()).put("\\d", new CharGroup(1, CharGroup.code1));
        CharGroup.PERL_GROUPS.put("\\D", new CharGroup(-1, CharGroup.code1));
        CharGroup.PERL_GROUPS.put("\\s", new CharGroup(1, CharGroup.code2));
        CharGroup.PERL_GROUPS.put("\\S", new CharGroup(-1, CharGroup.code2));
        CharGroup.PERL_GROUPS.put("\\w", new CharGroup(1, CharGroup.code3));
        CharGroup.PERL_GROUPS.put("\\W", new CharGroup(-1, CharGroup.code3));
        code4 = new int[] { 48, 57, 65, 90, 97, 122 };
        code5 = new int[] { 65, 90, 97, 122 };
        code6 = new int[] { 0, 127 };
        code7 = new int[] { 9, 9, 32, 32 };
        code8 = new int[] { 0, 31, 127, 127 };
        code9 = new int[] { 48, 57 };
        code10 = new int[] { 33, 126 };
        code11 = new int[] { 97, 122 };
        code12 = new int[] { 32, 126 };
        code13 = new int[] { 33, 47, 58, 64, 91, 96, 123, 126 };
        code14 = new int[] { 9, 13, 32, 32 };
        code15 = new int[] { 65, 90 };
        code16 = new int[] { 48, 57, 65, 90, 95, 95, 97, 122 };
        code17 = new int[] { 48, 57, 65, 70, 97, 102 };
        (POSIX_GROUPS = new HashMap<String, CharGroup>()).put("[:alnum:]", new CharGroup(1, CharGroup.code4));
        CharGroup.POSIX_GROUPS.put("[:^alnum:]", new CharGroup(-1, CharGroup.code4));
        CharGroup.POSIX_GROUPS.put("[:alpha:]", new CharGroup(1, CharGroup.code5));
        CharGroup.POSIX_GROUPS.put("[:^alpha:]", new CharGroup(-1, CharGroup.code5));
        CharGroup.POSIX_GROUPS.put("[:ascii:]", new CharGroup(1, CharGroup.code6));
        CharGroup.POSIX_GROUPS.put("[:^ascii:]", new CharGroup(-1, CharGroup.code6));
        CharGroup.POSIX_GROUPS.put("[:blank:]", new CharGroup(1, CharGroup.code7));
        CharGroup.POSIX_GROUPS.put("[:^blank:]", new CharGroup(-1, CharGroup.code7));
        CharGroup.POSIX_GROUPS.put("[:cntrl:]", new CharGroup(1, CharGroup.code8));
        CharGroup.POSIX_GROUPS.put("[:^cntrl:]", new CharGroup(-1, CharGroup.code8));
        CharGroup.POSIX_GROUPS.put("[:digit:]", new CharGroup(1, CharGroup.code9));
        CharGroup.POSIX_GROUPS.put("[:^digit:]", new CharGroup(-1, CharGroup.code9));
        CharGroup.POSIX_GROUPS.put("[:graph:]", new CharGroup(1, CharGroup.code10));
        CharGroup.POSIX_GROUPS.put("[:^graph:]", new CharGroup(-1, CharGroup.code10));
        CharGroup.POSIX_GROUPS.put("[:lower:]", new CharGroup(1, CharGroup.code11));
        CharGroup.POSIX_GROUPS.put("[:^lower:]", new CharGroup(-1, CharGroup.code11));
        CharGroup.POSIX_GROUPS.put("[:print:]", new CharGroup(1, CharGroup.code12));
        CharGroup.POSIX_GROUPS.put("[:^print:]", new CharGroup(-1, CharGroup.code12));
        CharGroup.POSIX_GROUPS.put("[:punct:]", new CharGroup(1, CharGroup.code13));
        CharGroup.POSIX_GROUPS.put("[:^punct:]", new CharGroup(-1, CharGroup.code13));
        CharGroup.POSIX_GROUPS.put("[:space:]", new CharGroup(1, CharGroup.code14));
        CharGroup.POSIX_GROUPS.put("[:^space:]", new CharGroup(-1, CharGroup.code14));
        CharGroup.POSIX_GROUPS.put("[:upper:]", new CharGroup(1, CharGroup.code15));
        CharGroup.POSIX_GROUPS.put("[:^upper:]", new CharGroup(-1, CharGroup.code15));
        CharGroup.POSIX_GROUPS.put("[:word:]", new CharGroup(1, CharGroup.code16));
        CharGroup.POSIX_GROUPS.put("[:^word:]", new CharGroup(-1, CharGroup.code16));
        CharGroup.POSIX_GROUPS.put("[:xdigit:]", new CharGroup(1, CharGroup.code17));
        CharGroup.POSIX_GROUPS.put("[:^xdigit:]", new CharGroup(-1, CharGroup.code17));
    }
}
