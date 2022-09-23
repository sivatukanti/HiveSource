// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public interface thrift_grammarConstants
{
    public static final int EOF = 0;
    public static final int tok_const = 8;
    public static final int tok_namespace = 9;
    public static final int tok_cpp_namespace = 10;
    public static final int tok_cpp_include = 11;
    public static final int tok_cpp_type = 12;
    public static final int tok_java_package = 13;
    public static final int tok_cocoa_prefix = 14;
    public static final int tok_csharp_namespace = 15;
    public static final int tok_php_namespace = 16;
    public static final int tok_py_module = 17;
    public static final int tok_perl_package = 18;
    public static final int tok_ruby_namespace = 19;
    public static final int tok_smalltalk_category = 20;
    public static final int tok_smalltalk_prefix = 21;
    public static final int tok_xsd_all = 22;
    public static final int tok_xsd_optional = 23;
    public static final int tok_xsd_nillable = 24;
    public static final int tok_xsd_namespace = 25;
    public static final int tok_xsd_attrs = 26;
    public static final int tok_include = 27;
    public static final int tok_void = 28;
    public static final int tok_bool = 29;
    public static final int tok_byte = 30;
    public static final int tok_i16 = 31;
    public static final int tok_i32 = 32;
    public static final int tok_i64 = 33;
    public static final int tok_double = 34;
    public static final int tok_string = 35;
    public static final int tok_slist = 36;
    public static final int tok_senum = 37;
    public static final int tok_map = 38;
    public static final int tok_list = 39;
    public static final int tok_set = 40;
    public static final int tok_async = 41;
    public static final int tok_typedef = 42;
    public static final int tok_struct = 43;
    public static final int tok_exception = 44;
    public static final int tok_extends = 45;
    public static final int tok_throws = 46;
    public static final int tok_service = 47;
    public static final int tok_enum = 48;
    public static final int tok_required = 49;
    public static final int tok_optional = 50;
    public static final int tok_skip = 51;
    public static final int tok_int_constant = 52;
    public static final int tok_double_constant = 53;
    public static final int IDENTIFIER = 54;
    public static final int LETTER = 55;
    public static final int DIGIT = 56;
    public static final int tok_literal = 57;
    public static final int tok_st_identifier = 58;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = { "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "<token of kind 5>", "<token of kind 6>", "<token of kind 7>", "\"const\"", "\"namespace\"", "\"cpp_namespace\"", "\"cpp_include\"", "\"cpp_type\"", "\"java_package\"", "\"cocoa_prefix\"", "\"csharp_namespace\"", "\"php_namespace\"", "\"py_module\"", "\"perl_package\"", "\"ruby_namespace\"", "\"smalltalk_category\"", "\"smalltalk_prefix\"", "\"xsd_all\"", "\"xsd_optional\"", "\"xsd_nillable\"", "\"xsd_namespace\"", "\"xsd_attrs\"", "\"include\"", "\"void\"", "\"bool\"", "\"byte\"", "\"i16\"", "\"i32\"", "\"i64\"", "\"double\"", "\"string\"", "\"slist\"", "\"senum\"", "\"map\"", "\"list\"", "\"set\"", "\"async\"", "\"typedef\"", "\"struct\"", "\"exception\"", "\"extends\"", "\"throws\"", "\"service\"", "\"enum\"", "\"required\"", "\"optional\"", "\"skip\"", "<tok_int_constant>", "<tok_double_constant>", "<IDENTIFIER>", "<LETTER>", "<DIGIT>", "<tok_literal>", "<tok_st_identifier>", "\",\"", "\";\"", "\"{\"", "\"}\"", "\"=\"", "\"[\"", "\"]\"", "\":\"", "\"(\"", "\")\"", "\"<\"", "\">\"" };
}
