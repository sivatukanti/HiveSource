// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.cfg;

import javax.xml.stream.XMLStreamConstants;

public class ErrorConsts implements XMLStreamConstants
{
    public static String WT_ENT_DECL;
    public static String WT_ELEM_DECL;
    public static String WT_ATTR_DECL;
    public static String WT_XML_DECL;
    public static String WT_DT_DECL;
    public static String WT_NS_DECL;
    public static String WT_VALIDATION;
    public static String W_UNDEFINED_ELEM;
    public static String W_MIXED_ENCODINGS;
    public static String W_MISSING_DTD;
    public static String W_DTD_DUP_ATTR;
    public static String W_DTD_ATTR_REDECL;
    public static String ERR_INTERNAL;
    public static String ERR_NULL_ARG;
    public static String ERR_UNKNOWN_FEATURE;
    public static String ERR_STATE_NOT_STELEM;
    public static String ERR_STATE_NOT_ELEM;
    public static String ERR_STATE_NOT_PI;
    public static String ERR_STATE_NOT_ELEM_OR_TEXT;
    public static String ERR_XML_10_VS_11;
    public static String ERR_DTD_IN_EPILOG;
    public static String ERR_DTD_DUP;
    public static String ERR_CDATA_IN_EPILOG;
    public static String ERR_HYPHENS_IN_COMMENT;
    public static String ERR_BRACKET_IN_TEXT;
    public static String ERR_UNEXP_KEYWORD;
    public static String ERR_WF_PI_MISSING_TARGET;
    public static String ERR_WF_PI_XML_TARGET;
    public static String ERR_WF_PI_XML_MISSING_SPACE;
    public static String ERR_WF_ENTITY_EXT_DECLARED;
    public static String ERR_WF_GE_UNDECLARED;
    public static String ERR_WF_GE_UNDECLARED_SA;
    public static String ERR_NS_UNDECLARED;
    public static String ERR_NS_UNDECLARED_FOR_ATTR;
    public static String ERR_NS_REDECL_XML;
    public static String ERR_NS_REDECL_XMLNS;
    public static String ERR_NS_REDECL_XML_URI;
    public static String ERR_NS_REDECL_XMLNS_URI;
    public static String ERR_NS_EMPTY;
    public static String ERR_DTD_MAINLEVEL_KEYWORD;
    public static String ERR_DTD_ATTR_TYPE;
    public static String ERR_DTD_DEFAULT_TYPE;
    public static String ERR_DTD_ELEM_REDEFD;
    public static String ERR_DTD_NOTATION_REDEFD;
    public static String ERR_DTD_UNDECLARED_ENTITY;
    public static String ERR_DTD_XML_SPACE;
    public static String ERR_DTD_XML_ID;
    public static String ERR_VLD_UNKNOWN_ELEM;
    public static String ERR_VLD_EMPTY;
    public static String ERR_VLD_NON_MIXED;
    public static String ERR_VLD_ANY;
    public static String ERR_VLD_UNKNOWN_ATTR;
    public static String ERR_VLD_WRONG_ROOT;
    public static String WERR_PROLOG_CDATA;
    public static String WERR_PROLOG_NONWS_TEXT;
    public static String WERR_PROLOG_SECOND_ROOT;
    public static String WERR_CDATA_CONTENT;
    public static String WERR_COMMENT_CONTENT;
    public static String WERR_ATTR_NO_ELEM;
    public static String WERR_NAME_EMPTY;
    public static String WERR_NAME_ILLEGAL_FIRST_CHAR;
    public static String WERR_NAME_ILLEGAL_CHAR;
    
    public static String tokenTypeDesc(final int type) {
        switch (type) {
            case 1: {
                return "START_ELEMENT";
            }
            case 2: {
                return "END_ELEMENT";
            }
            case 7: {
                return "START_DOCUMENT";
            }
            case 8: {
                return "END_DOCUMENT";
            }
            case 4: {
                return "CHARACTERS";
            }
            case 12: {
                return "CDATA";
            }
            case 6: {
                return "SPACE";
            }
            case 5: {
                return "COMMENT";
            }
            case 3: {
                return "PROCESSING_INSTRUCTION";
            }
            case 11: {
                return "DTD";
            }
            case 9: {
                return "ENTITY_REFERENCE";
            }
            default: {
                return "[" + type + "]";
            }
        }
    }
    
    static {
        ErrorConsts.WT_ENT_DECL = "entity declaration";
        ErrorConsts.WT_ELEM_DECL = "element declaration";
        ErrorConsts.WT_ATTR_DECL = "attribute declaration";
        ErrorConsts.WT_XML_DECL = "xml declaration";
        ErrorConsts.WT_DT_DECL = "doctype declaration";
        ErrorConsts.WT_NS_DECL = "namespace declaration";
        ErrorConsts.WT_VALIDATION = "schema validation";
        ErrorConsts.W_UNDEFINED_ELEM = "Undefined element \"{0}\"; referred to by attribute(s)";
        ErrorConsts.W_MIXED_ENCODINGS = "Inconsistent text encoding; declared as \"{0}\" in xml declaration, application had passed \"{1}\"";
        ErrorConsts.W_MISSING_DTD = "Missing DOCTYPE declaration in validating mode; can not validate elements or attributes";
        ErrorConsts.W_DTD_DUP_ATTR = "Attribute \"{0}\" (for element <{1}>) declared multiple times";
        ErrorConsts.W_DTD_ATTR_REDECL = "Attribute \"{0}\" already declared for element <{1}>; ignoring re-declaration";
        ErrorConsts.ERR_INTERNAL = "Internal error";
        ErrorConsts.ERR_NULL_ARG = "Illegal to pass null as argument";
        ErrorConsts.ERR_UNKNOWN_FEATURE = "Unrecognized feature \"{0}\"";
        ErrorConsts.ERR_STATE_NOT_STELEM = "Current event not START_ELEMENT";
        ErrorConsts.ERR_STATE_NOT_ELEM = "Current event not START_ELEMENT or END_ELEMENT";
        ErrorConsts.ERR_STATE_NOT_PI = "Current event not PROCESSING_INSTRUCTION";
        ErrorConsts.ERR_STATE_NOT_ELEM_OR_TEXT = "Current event ({0}) not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA";
        ErrorConsts.ERR_XML_10_VS_11 = "XML 1.0 document can not refer to XML 1.1 parsed external entities";
        ErrorConsts.ERR_DTD_IN_EPILOG = "Can not have DOCTYPE declaration in epilog";
        ErrorConsts.ERR_DTD_DUP = "Duplicate DOCTYPE declaration";
        ErrorConsts.ERR_CDATA_IN_EPILOG = " (CDATA not allowed in prolog/epilog)";
        ErrorConsts.ERR_HYPHENS_IN_COMMENT = "String '--' not allowed in comment (missing '>'?)";
        ErrorConsts.ERR_BRACKET_IN_TEXT = "String ']]>' not allowed in textual content, except as the end marker of CDATA section";
        ErrorConsts.ERR_UNEXP_KEYWORD = "Unexpected keyword \"{0}\"; expected \"{1}\"";
        ErrorConsts.ERR_WF_PI_MISSING_TARGET = "Missing processing instruction target";
        ErrorConsts.ERR_WF_PI_XML_TARGET = "Illegal processing instruction target (\"{0}\"); 'xml' (case insensitive) is reserved by the specs.";
        ErrorConsts.ERR_WF_PI_XML_MISSING_SPACE = "excepted either space or \"?>\" after PI target";
        ErrorConsts.ERR_WF_ENTITY_EXT_DECLARED = "Entity \"{0}\" declared externally, but referenced from a document declared 'standalone=\"yes\"'";
        ErrorConsts.ERR_WF_GE_UNDECLARED = "Undeclared general entity \"{0}\"";
        ErrorConsts.ERR_WF_GE_UNDECLARED_SA = "Undeclared general entity \"{0}\" (document in stand-alone mode; perhaps declared externally?)";
        ErrorConsts.ERR_NS_UNDECLARED = "Undeclared namespace prefix \"{0}\"";
        ErrorConsts.ERR_NS_UNDECLARED_FOR_ATTR = "Undeclared namespace prefix \"{0}\" (for attribute \"{1}\")";
        ErrorConsts.ERR_NS_REDECL_XML = "Trying to redeclare prefix 'xml' from its default URI 'http://www.w3.org/XML/1998/namespace' to \"{0}\"";
        ErrorConsts.ERR_NS_REDECL_XMLNS = "Trying to declare prefix 'xmlns' (illegal as per NS 1.1 #4)";
        ErrorConsts.ERR_NS_REDECL_XML_URI = "Trying to bind URI 'http://www.w3.org/XML/1998/namespace to prefix \"{0}\" (can only bind to 'xml')";
        ErrorConsts.ERR_NS_REDECL_XMLNS_URI = "Trying to bind URI 'http://www.w3.org/2000/xmlns/ to prefix \"{0}\" (can not be explicitly bound)";
        ErrorConsts.ERR_NS_EMPTY = "Non-default namespace can not map to empty URI (as per Namespace 1.0 # 2) in XML 1.0 documents";
        ErrorConsts.ERR_DTD_MAINLEVEL_KEYWORD = "; expected a keyword (ATTLIST, ELEMENT, ENTITY, NOTATION), comment, or conditional section";
        ErrorConsts.ERR_DTD_ATTR_TYPE = "; expected one of type (CDATA, ID, IDREF, IDREFS, ENTITY, ENTITIES NOTATION, NMTOKEN or NMTOKENS)";
        ErrorConsts.ERR_DTD_DEFAULT_TYPE = "; expected #REQUIRED, #IMPLIED or #FIXED";
        ErrorConsts.ERR_DTD_ELEM_REDEFD = "Trying to redefine element \"{0}\" (originally defined at {1})";
        ErrorConsts.ERR_DTD_NOTATION_REDEFD = "Trying to redefine notation \"{0}\" (originally defined at {1})";
        ErrorConsts.ERR_DTD_UNDECLARED_ENTITY = "Undeclared {0} entity \"{1}\"";
        ErrorConsts.ERR_DTD_XML_SPACE = "Attribute xml:space has to be defined of type enumerated, and have 1 or 2 values, 'default' and/or 'preserve'";
        ErrorConsts.ERR_DTD_XML_ID = "Attribute xml:id has to have attribute type of ID, as per Xml:id specification";
        ErrorConsts.ERR_VLD_UNKNOWN_ELEM = "Undefined element <{0}> encountered";
        ErrorConsts.ERR_VLD_EMPTY = "Element <{0}> has EMPTY content specification; can not contain {1}";
        ErrorConsts.ERR_VLD_NON_MIXED = "Element <{0}> has non-mixed content specification; can not contain non-white space text, or any CDATA sections";
        ErrorConsts.ERR_VLD_ANY = "Element <{0}> has ANY content specification; can not contain {1}";
        ErrorConsts.ERR_VLD_UNKNOWN_ATTR = "Element <{0}> has no attribute \"{1}\"";
        ErrorConsts.ERR_VLD_WRONG_ROOT = "Unexpected root element <{0}>; expected <{0}> as per DOCTYPE declaration";
        ErrorConsts.WERR_PROLOG_CDATA = "Trying to output a CDATA block outside main element tree (in prolog or epilog)";
        ErrorConsts.WERR_PROLOG_NONWS_TEXT = "Trying to output non-whitespace characters outside main element tree (in prolog or epilog)";
        ErrorConsts.WERR_PROLOG_SECOND_ROOT = "Trying to output second root, <{0}>";
        ErrorConsts.WERR_CDATA_CONTENT = "Illegal input: CDATA block has embedded ']]>' in it (index {0})";
        ErrorConsts.WERR_COMMENT_CONTENT = "Illegal input: comment content has embedded '--' in it (index {0})";
        ErrorConsts.WERR_ATTR_NO_ELEM = "Trying to write an attribute when there is no open start element.";
        ErrorConsts.WERR_NAME_EMPTY = "Illegal to pass empty name";
        ErrorConsts.WERR_NAME_ILLEGAL_FIRST_CHAR = "Illegal first name character {0}";
        ErrorConsts.WERR_NAME_ILLEGAL_CHAR = "Illegal name character {0}";
    }
}
