// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.util.HashMap;
import java.util.Map;

class UnicodeTables
{
    static final int UpperCase = 0;
    static final int LowerCase = 1;
    static final int TitleCase = 2;
    static final int UpperLower = 1114112;
    private static final int[][] _Lm;
    private static final int[][] _Ll;
    private static final int[][] _C;
    private static final int[][] _M;
    private static final int[][] _L;
    private static final int[][] _N;
    private static final int[][] _P;
    private static final int[][] _S;
    private static final int[][] _Z;
    private static final int[][] _Me;
    private static final int[][] _Mc;
    private static final int[][] _Mn;
    private static final int[][] _Zl;
    private static final int[][] _Zp;
    private static final int[][] _Zs;
    private static final int[][] _Cs;
    private static final int[][] _Co;
    private static final int[][] _Cf;
    private static final int[][] _Cc;
    private static final int[][] _Po;
    private static final int[][] _Pi;
    private static final int[][] _Pf;
    private static final int[][] _Pe;
    private static final int[][] _Pd;
    private static final int[][] _Pc;
    private static final int[][] _Ps;
    private static final int[][] _Nd;
    private static final int[][] _Nl;
    private static final int[][] _No;
    private static final int[][] _So;
    private static final int[][] _Sm;
    private static final int[][] _Sk;
    private static final int[][] _Sc;
    private static final int[][] _Lu;
    private static final int[][] _Lt;
    private static final int[][] _Lo;
    static final int[][] Cc;
    static final int[][] Cf;
    static final int[][] Co;
    static final int[][] Cs;
    static final int[][] Digit;
    static final int[][] Nd;
    static final int[][] Letter;
    static final int[][] L;
    static final int[][] Lm;
    static final int[][] Lo;
    static final int[][] Lower;
    static final int[][] Ll;
    static final int[][] Mark;
    static final int[][] M;
    static final int[][] Mc;
    static final int[][] Me;
    static final int[][] Mn;
    static final int[][] Nl;
    static final int[][] No;
    static final int[][] Number;
    static final int[][] N;
    static final int[][] Other;
    static final int[][] C;
    static final int[][] Pc;
    static final int[][] Pd;
    static final int[][] Pe;
    static final int[][] Pf;
    static final int[][] Pi;
    static final int[][] Po;
    static final int[][] Ps;
    static final int[][] Punct;
    static final int[][] P;
    static final int[][] Sc;
    static final int[][] Sk;
    static final int[][] Sm;
    static final int[][] So;
    static final int[][] Space;
    static final int[][] Z;
    static final int[][] Symbol;
    static final int[][] S;
    static final int[][] Title;
    static final int[][] Lt;
    static final int[][] Upper;
    static final int[][] Lu;
    static final int[][] Zl;
    static final int[][] Zp;
    static final int[][] Zs;
    private static final int[][] _Katakana;
    private static final int[][] _Malayalam;
    private static final int[][] _Phags_Pa;
    private static final int[][] _Inscriptional_Parthian;
    private static final int[][] _Latin;
    private static final int[][] _Inscriptional_Pahlavi;
    private static final int[][] _Osmanya;
    private static final int[][] _Khmer;
    private static final int[][] _Inherited;
    private static final int[][] _Telugu;
    private static final int[][] _Samaritan;
    private static final int[][] _Bopomofo;
    private static final int[][] _Imperial_Aramaic;
    private static final int[][] _Kaithi;
    private static final int[][] _Mandaic;
    private static final int[][] _Old_South_Arabian;
    private static final int[][] _Kayah_Li;
    private static final int[][] _New_Tai_Lue;
    private static final int[][] _Tai_Le;
    private static final int[][] _Kharoshthi;
    private static final int[][] _Common;
    private static final int[][] _Kannada;
    private static final int[][] _Old_Turkic;
    private static final int[][] _Tamil;
    private static final int[][] _Tagalog;
    private static final int[][] _Brahmi;
    private static final int[][] _Arabic;
    private static final int[][] _Tagbanwa;
    private static final int[][] _Canadian_Aboriginal;
    private static final int[][] _Tibetan;
    private static final int[][] _Coptic;
    private static final int[][] _Hiragana;
    private static final int[][] _Limbu;
    private static final int[][] _Egyptian_Hieroglyphs;
    private static final int[][] _Avestan;
    private static final int[][] _Myanmar;
    private static final int[][] _Armenian;
    private static final int[][] _Sinhala;
    private static final int[][] _Bengali;
    private static final int[][] _Greek;
    private static final int[][] _Cham;
    private static final int[][] _Hebrew;
    private static final int[][] _Meetei_Mayek;
    private static final int[][] _Saurashtra;
    private static final int[][] _Hangul;
    private static final int[][] _Runic;
    private static final int[][] _Deseret;
    private static final int[][] _Lisu;
    private static final int[][] _Sundanese;
    private static final int[][] _Glagolitic;
    private static final int[][] _Oriya;
    private static final int[][] _Buhid;
    private static final int[][] _Ethiopic;
    private static final int[][] _Javanese;
    private static final int[][] _Syloti_Nagri;
    private static final int[][] _Vai;
    private static final int[][] _Cherokee;
    private static final int[][] _Ogham;
    private static final int[][] _Batak;
    private static final int[][] _Syriac;
    private static final int[][] _Gurmukhi;
    private static final int[][] _Tai_Tham;
    private static final int[][] _Ol_Chiki;
    private static final int[][] _Mongolian;
    private static final int[][] _Hanunoo;
    private static final int[][] _Cypriot;
    private static final int[][] _Buginese;
    private static final int[][] _Bamum;
    private static final int[][] _Lepcha;
    private static final int[][] _Thaana;
    private static final int[][] _Old_Persian;
    private static final int[][] _Cuneiform;
    private static final int[][] _Rejang;
    private static final int[][] _Georgian;
    private static final int[][] _Shavian;
    private static final int[][] _Lycian;
    private static final int[][] _Nko;
    private static final int[][] _Yi;
    private static final int[][] _Lao;
    private static final int[][] _Linear_B;
    private static final int[][] _Old_Italic;
    private static final int[][] _Tai_Viet;
    private static final int[][] _Devanagari;
    private static final int[][] _Lydian;
    private static final int[][] _Tifinagh;
    private static final int[][] _Ugaritic;
    private static final int[][] _Thai;
    private static final int[][] _Cyrillic;
    private static final int[][] _Gujarati;
    private static final int[][] _Carian;
    private static final int[][] _Phoenician;
    private static final int[][] _Balinese;
    private static final int[][] _Braille;
    private static final int[][] _Han;
    private static final int[][] _Gothic;
    static final int[][] Arabic;
    static final int[][] Armenian;
    static final int[][] Avestan;
    static final int[][] Balinese;
    static final int[][] Bamum;
    static final int[][] Batak;
    static final int[][] Bengali;
    static final int[][] Bopomofo;
    static final int[][] Brahmi;
    static final int[][] Braille;
    static final int[][] Buginese;
    static final int[][] Buhid;
    static final int[][] Canadian_Aboriginal;
    static final int[][] Carian;
    static final int[][] Cham;
    static final int[][] Cherokee;
    static final int[][] Common;
    static final int[][] Coptic;
    static final int[][] Cuneiform;
    static final int[][] Cypriot;
    static final int[][] Cyrillic;
    static final int[][] Deseret;
    static final int[][] Devanagari;
    static final int[][] Egyptian_Hieroglyphs;
    static final int[][] Ethiopic;
    static final int[][] Georgian;
    static final int[][] Glagolitic;
    static final int[][] Gothic;
    static final int[][] Greek;
    static final int[][] Gujarati;
    static final int[][] Gurmukhi;
    static final int[][] Han;
    static final int[][] Hangul;
    static final int[][] Hanunoo;
    static final int[][] Hebrew;
    static final int[][] Hiragana;
    static final int[][] Imperial_Aramaic;
    static final int[][] Inherited;
    static final int[][] Inscriptional_Pahlavi;
    static final int[][] Inscriptional_Parthian;
    static final int[][] Javanese;
    static final int[][] Kaithi;
    static final int[][] Kannada;
    static final int[][] Katakana;
    static final int[][] Kayah_Li;
    static final int[][] Kharoshthi;
    static final int[][] Khmer;
    static final int[][] Lao;
    static final int[][] Latin;
    static final int[][] Lepcha;
    static final int[][] Limbu;
    static final int[][] Linear_B;
    static final int[][] Lisu;
    static final int[][] Lycian;
    static final int[][] Lydian;
    static final int[][] Malayalam;
    static final int[][] Mandaic;
    static final int[][] Meetei_Mayek;
    static final int[][] Mongolian;
    static final int[][] Myanmar;
    static final int[][] New_Tai_Lue;
    static final int[][] Nko;
    static final int[][] Ogham;
    static final int[][] Ol_Chiki;
    static final int[][] Old_Italic;
    static final int[][] Old_Persian;
    static final int[][] Old_South_Arabian;
    static final int[][] Old_Turkic;
    static final int[][] Oriya;
    static final int[][] Osmanya;
    static final int[][] Phags_Pa;
    static final int[][] Phoenician;
    static final int[][] Rejang;
    static final int[][] Runic;
    static final int[][] Samaritan;
    static final int[][] Saurashtra;
    static final int[][] Shavian;
    static final int[][] Sinhala;
    static final int[][] Sundanese;
    static final int[][] Syloti_Nagri;
    static final int[][] Syriac;
    static final int[][] Tagalog;
    static final int[][] Tagbanwa;
    static final int[][] Tai_Le;
    static final int[][] Tai_Tham;
    static final int[][] Tai_Viet;
    static final int[][] Tamil;
    static final int[][] Telugu;
    static final int[][] Thaana;
    static final int[][] Thai;
    static final int[][] Tibetan;
    static final int[][] Tifinagh;
    static final int[][] Ugaritic;
    static final int[][] Vai;
    static final int[][] Yi;
    private static final int[][] _Pattern_Syntax;
    private static final int[][] _Other_ID_Start;
    private static final int[][] _Pattern_White_Space;
    private static final int[][] _Other_Lowercase;
    private static final int[][] _Soft_Dotted;
    private static final int[][] _Hex_Digit;
    private static final int[][] _ASCII_Hex_Digit;
    private static final int[][] _Deprecated;
    private static final int[][] _Terminal_Punctuation;
    private static final int[][] _Quotation_Mark;
    private static final int[][] _Other_ID_Continue;
    private static final int[][] _Bidi_Control;
    private static final int[][] _Variation_Selector;
    private static final int[][] _Noncharacter_Code_Point;
    private static final int[][] _Other_Math;
    private static final int[][] _Unified_Ideograph;
    private static final int[][] _Hyphen;
    private static final int[][] _IDS_Binary_Operator;
    private static final int[][] _Logical_Order_Exception;
    private static final int[][] _Radical;
    private static final int[][] _Other_Uppercase;
    private static final int[][] _STerm;
    private static final int[][] _Other_Alphabetic;
    private static final int[][] _Diacritic;
    private static final int[][] _Extender;
    private static final int[][] _Join_Control;
    private static final int[][] _Ideographic;
    private static final int[][] _Dash;
    private static final int[][] _IDS_Trinary_Operator;
    private static final int[][] _Other_Grapheme_Extend;
    private static final int[][] _Other_Default_Ignorable_Code_Point;
    private static final int[][] _White_Space;
    static final int[][] ASCII_Hex_Digit;
    static final int[][] Bidi_Control;
    static final int[][] Dash;
    static final int[][] Deprecated;
    static final int[][] Diacritic;
    static final int[][] Extender;
    static final int[][] Hex_Digit;
    static final int[][] Hyphen;
    static final int[][] IDS_Binary_Operator;
    static final int[][] IDS_Trinary_Operator;
    static final int[][] Ideographic;
    static final int[][] Join_Control;
    static final int[][] Logical_Order_Exception;
    static final int[][] Noncharacter_Code_Point;
    static final int[][] Other_Alphabetic;
    static final int[][] Other_Default_Ignorable_Code_Point;
    static final int[][] Other_Grapheme_Extend;
    static final int[][] Other_ID_Continue;
    static final int[][] Other_ID_Start;
    static final int[][] Other_Lowercase;
    static final int[][] Other_Math;
    static final int[][] Other_Uppercase;
    static final int[][] Pattern_Syntax;
    static final int[][] Pattern_White_Space;
    static final int[][] Quotation_Mark;
    static final int[][] Radical;
    static final int[][] STerm;
    static final int[][] Soft_Dotted;
    static final int[][] Terminal_Punctuation;
    static final int[][] Unified_Ideograph;
    static final int[][] Variation_Selector;
    static final int[][] White_Space;
    static final int[][] CASE_RANGES;
    static final int[][] CASE_ORBIT;
    private static final int[][] foldLl;
    private static final int[][] foldInherited;
    private static final int[][] foldM;
    private static final int[][] foldL;
    private static final int[][] foldMn;
    private static final int[][] foldCommon;
    private static final int[][] foldGreek;
    private static final int[][] foldLu;
    private static final int[][] foldLt;
    static final Map<String, int[][]> CATEGORIES;
    static final Map<String, int[][]> SCRIPTS;
    static final Map<String, int[][]> PROPERTIES;
    static final Map<String, int[][]> FOLD_CATEGORIES;
    static final Map<String, int[][]> FOLD_SCRIPT;
    
    private static Map<String, int[][]> Categories() {
        final Map<String, int[][]> map = new HashMap<String, int[][]>();
        map.put("Lm", UnicodeTables.Lm);
        map.put("Ll", UnicodeTables.Ll);
        map.put("C", UnicodeTables.C);
        map.put("M", UnicodeTables.M);
        map.put("L", UnicodeTables.L);
        map.put("N", UnicodeTables.N);
        map.put("P", UnicodeTables.P);
        map.put("S", UnicodeTables.S);
        map.put("Z", UnicodeTables.Z);
        map.put("Me", UnicodeTables.Me);
        map.put("Mc", UnicodeTables.Mc);
        map.put("Mn", UnicodeTables.Mn);
        map.put("Zl", UnicodeTables.Zl);
        map.put("Zp", UnicodeTables.Zp);
        map.put("Zs", UnicodeTables.Zs);
        map.put("Cs", UnicodeTables.Cs);
        map.put("Co", UnicodeTables.Co);
        map.put("Cf", UnicodeTables.Cf);
        map.put("Cc", UnicodeTables.Cc);
        map.put("Po", UnicodeTables.Po);
        map.put("Pi", UnicodeTables.Pi);
        map.put("Pf", UnicodeTables.Pf);
        map.put("Pe", UnicodeTables.Pe);
        map.put("Pd", UnicodeTables.Pd);
        map.put("Pc", UnicodeTables.Pc);
        map.put("Ps", UnicodeTables.Ps);
        map.put("Nd", UnicodeTables.Nd);
        map.put("Nl", UnicodeTables.Nl);
        map.put("No", UnicodeTables.No);
        map.put("So", UnicodeTables.So);
        map.put("Sm", UnicodeTables.Sm);
        map.put("Sk", UnicodeTables.Sk);
        map.put("Sc", UnicodeTables.Sc);
        map.put("Lu", UnicodeTables.Lu);
        map.put("Lt", UnicodeTables.Lt);
        map.put("Lo", UnicodeTables.Lo);
        return map;
    }
    
    private static int[][] make_Lm() {
        return new int[][] { { 688, 705, 1 }, { 710, 721, 1 }, { 736, 740, 1 }, { 748, 750, 2 }, { 884, 890, 6 }, { 1369, 1600, 231 }, { 1765, 1766, 1 }, { 2036, 2037, 1 }, { 2042, 2074, 32 }, { 2084, 2088, 4 }, { 2417, 3654, 1237 }, { 3782, 4348, 566 }, { 6103, 6211, 108 }, { 6823, 7288, 465 }, { 7289, 7293, 1 }, { 7468, 7521, 1 }, { 7544, 7579, 35 }, { 7580, 7615, 1 }, { 8305, 8319, 14 }, { 8336, 8348, 1 }, { 11389, 11631, 242 }, { 11823, 12293, 470 }, { 12337, 12341, 1 }, { 12347, 12445, 98 }, { 12446, 12540, 94 }, { 12541, 12542, 1 }, { 40981, 42232, 1251 }, { 42233, 42237, 1 }, { 42508, 42623, 115 }, { 42775, 42783, 1 }, { 42864, 42888, 24 }, { 43471, 43632, 161 }, { 43741, 65392, 21651 }, { 65438, 65439, 1 } };
    }
    
    private static int[][] make_Ll() {
        return new int[][] { { 97, 122, 1 }, { 170, 181, 11 }, { 186, 223, 37 }, { 224, 246, 1 }, { 248, 255, 1 }, { 257, 311, 2 }, { 312, 328, 2 }, { 329, 375, 2 }, { 378, 382, 2 }, { 383, 384, 1 }, { 387, 389, 2 }, { 392, 396, 4 }, { 397, 402, 5 }, { 405, 409, 4 }, { 410, 411, 1 }, { 414, 417, 3 }, { 419, 421, 2 }, { 424, 426, 2 }, { 427, 429, 2 }, { 432, 436, 4 }, { 438, 441, 3 }, { 442, 445, 3 }, { 446, 447, 1 }, { 454, 460, 3 }, { 462, 476, 2 }, { 477, 495, 2 }, { 496, 499, 3 }, { 501, 505, 4 }, { 507, 563, 2 }, { 564, 569, 1 }, { 572, 575, 3 }, { 576, 578, 2 }, { 583, 591, 2 }, { 592, 659, 1 }, { 661, 687, 1 }, { 881, 883, 2 }, { 887, 891, 4 }, { 892, 893, 1 }, { 912, 940, 28 }, { 941, 974, 1 }, { 976, 977, 1 }, { 981, 983, 1 }, { 985, 1007, 2 }, { 1008, 1011, 1 }, { 1013, 1019, 3 }, { 1020, 1072, 52 }, { 1073, 1119, 1 }, { 1121, 1153, 2 }, { 1163, 1215, 2 }, { 1218, 1230, 2 }, { 1231, 1319, 2 }, { 1377, 1415, 1 }, { 7424, 7467, 1 }, { 7522, 7543, 1 }, { 7545, 7578, 1 }, { 7681, 7829, 2 }, { 7830, 7837, 1 }, { 7839, 7935, 2 }, { 7936, 7943, 1 }, { 7952, 7957, 1 }, { 7968, 7975, 1 }, { 7984, 7991, 1 }, { 8000, 8005, 1 }, { 8016, 8023, 1 }, { 8032, 8039, 1 }, { 8048, 8061, 1 }, { 8064, 8071, 1 }, { 8080, 8087, 1 }, { 8096, 8103, 1 }, { 8112, 8116, 1 }, { 8118, 8119, 1 }, { 8126, 8130, 4 }, { 8131, 8132, 1 }, { 8134, 8135, 1 }, { 8144, 8147, 1 }, { 8150, 8151, 1 }, { 8160, 8167, 1 }, { 8178, 8180, 1 }, { 8182, 8183, 1 }, { 8458, 8462, 4 }, { 8463, 8467, 4 }, { 8495, 8505, 5 }, { 8508, 8509, 1 }, { 8518, 8521, 1 }, { 8526, 8580, 54 }, { 11312, 11358, 1 }, { 11361, 11365, 4 }, { 11366, 11372, 2 }, { 11377, 11379, 2 }, { 11380, 11382, 2 }, { 11383, 11388, 1 }, { 11393, 11491, 2 }, { 11492, 11500, 8 }, { 11502, 11520, 18 }, { 11521, 11557, 1 }, { 42561, 42605, 2 }, { 42625, 42647, 2 }, { 42787, 42799, 2 }, { 42800, 42801, 1 }, { 42803, 42865, 2 }, { 42866, 42872, 1 }, { 42874, 42876, 2 }, { 42879, 42887, 2 }, { 42892, 42894, 2 }, { 42897, 42913, 16 }, { 42915, 42921, 2 }, { 43002, 64256, 21254 }, { 64257, 64262, 1 }, { 64275, 64279, 1 }, { 65345, 65370, 1 }, { 66600, 66639, 1 }, { 119834, 119859, 1 }, { 119886, 119892, 1 }, { 119894, 119911, 1 }, { 119938, 119963, 1 }, { 119990, 119993, 1 }, { 119995, 119997, 2 }, { 119998, 120003, 1 }, { 120005, 120015, 1 }, { 120042, 120067, 1 }, { 120094, 120119, 1 }, { 120146, 120171, 1 }, { 120198, 120223, 1 }, { 120250, 120275, 1 }, { 120302, 120327, 1 }, { 120354, 120379, 1 }, { 120406, 120431, 1 }, { 120458, 120485, 1 }, { 120514, 120538, 1 }, { 120540, 120545, 1 }, { 120572, 120596, 1 }, { 120598, 120603, 1 }, { 120630, 120654, 1 }, { 120656, 120661, 1 }, { 120688, 120712, 1 }, { 120714, 120719, 1 }, { 120746, 120770, 1 }, { 120772, 120777, 1 }, { 120779, 120779, 1 } };
    }
    
    private static int[][] make_C() {
        return new int[][] { { 1, 31, 1 }, { 127, 159, 1 }, { 173, 1536, 1363 }, { 1537, 1539, 1 }, { 1757, 1807, 50 }, { 6068, 6069, 1 }, { 8203, 8207, 1 }, { 8234, 8238, 1 }, { 8288, 8292, 1 }, { 8298, 8303, 1 }, { 55296, 63743, 1 }, { 65279, 65529, 250 }, { 65530, 65531, 1 }, { 69821, 119155, 49334 }, { 119156, 119162, 1 }, { 917505, 917536, 31 }, { 917537, 917631, 1 }, { 983040, 1048573, 1 }, { 1048576, 1114109, 1 } };
    }
    
    private static int[][] make_M() {
        return new int[][] { { 768, 879, 1 }, { 1155, 1161, 1 }, { 1425, 1469, 1 }, { 1471, 1473, 2 }, { 1474, 1476, 2 }, { 1477, 1479, 2 }, { 1552, 1562, 1 }, { 1611, 1631, 1 }, { 1648, 1750, 102 }, { 1751, 1756, 1 }, { 1759, 1764, 1 }, { 1767, 1768, 1 }, { 1770, 1773, 1 }, { 1809, 1840, 31 }, { 1841, 1866, 1 }, { 1958, 1968, 1 }, { 2027, 2035, 1 }, { 2070, 2073, 1 }, { 2075, 2083, 1 }, { 2085, 2087, 1 }, { 2089, 2093, 1 }, { 2137, 2139, 1 }, { 2304, 2307, 1 }, { 2362, 2364, 1 }, { 2366, 2383, 1 }, { 2385, 2391, 1 }, { 2402, 2403, 1 }, { 2433, 2435, 1 }, { 2492, 2494, 2 }, { 2495, 2500, 1 }, { 2503, 2504, 1 }, { 2507, 2509, 1 }, { 2519, 2530, 11 }, { 2531, 2561, 30 }, { 2562, 2563, 1 }, { 2620, 2622, 2 }, { 2623, 2626, 1 }, { 2631, 2632, 1 }, { 2635, 2637, 1 }, { 2641, 2672, 31 }, { 2673, 2677, 4 }, { 2689, 2691, 1 }, { 2748, 2750, 2 }, { 2751, 2757, 1 }, { 2759, 2761, 1 }, { 2763, 2765, 1 }, { 2786, 2787, 1 }, { 2817, 2819, 1 }, { 2876, 2878, 2 }, { 2879, 2884, 1 }, { 2887, 2888, 1 }, { 2891, 2893, 1 }, { 2902, 2903, 1 }, { 2914, 2915, 1 }, { 2946, 3006, 60 }, { 3007, 3010, 1 }, { 3014, 3016, 1 }, { 3018, 3021, 1 }, { 3031, 3073, 42 }, { 3074, 3075, 1 }, { 3134, 3140, 1 }, { 3142, 3144, 1 }, { 3146, 3149, 1 }, { 3157, 3158, 1 }, { 3170, 3171, 1 }, { 3202, 3203, 1 }, { 3260, 3262, 2 }, { 3263, 3268, 1 }, { 3270, 3272, 1 }, { 3274, 3277, 1 }, { 3285, 3286, 1 }, { 3298, 3299, 1 }, { 3330, 3331, 1 }, { 3390, 3396, 1 }, { 3398, 3400, 1 }, { 3402, 3405, 1 }, { 3415, 3426, 11 }, { 3427, 3458, 31 }, { 3459, 3530, 71 }, { 3535, 3540, 1 }, { 3542, 3544, 2 }, { 3545, 3551, 1 }, { 3570, 3571, 1 }, { 3633, 3636, 3 }, { 3637, 3642, 1 }, { 3655, 3662, 1 }, { 3761, 3764, 3 }, { 3765, 3769, 1 }, { 3771, 3772, 1 }, { 3784, 3789, 1 }, { 3864, 3865, 1 }, { 3893, 3897, 2 }, { 3902, 3903, 1 }, { 3953, 3972, 1 }, { 3974, 3975, 1 }, { 3981, 3991, 1 }, { 3993, 4028, 1 }, { 4038, 4139, 101 }, { 4140, 4158, 1 }, { 4182, 4185, 1 }, { 4190, 4192, 1 }, { 4194, 4196, 1 }, { 4199, 4205, 1 }, { 4209, 4212, 1 }, { 4226, 4237, 1 }, { 4239, 4250, 11 }, { 4251, 4253, 1 }, { 4957, 4959, 1 }, { 5906, 5908, 1 }, { 5938, 5940, 1 }, { 5970, 5971, 1 }, { 6002, 6003, 1 }, { 6070, 6099, 1 }, { 6109, 6155, 46 }, { 6156, 6157, 1 }, { 6313, 6432, 119 }, { 6433, 6443, 1 }, { 6448, 6459, 1 }, { 6576, 6592, 1 }, { 6600, 6601, 1 }, { 6679, 6683, 1 }, { 6741, 6750, 1 }, { 6752, 6780, 1 }, { 6783, 6912, 129 }, { 6913, 6916, 1 }, { 6964, 6980, 1 }, { 7019, 7027, 1 }, { 7040, 7042, 1 }, { 7073, 7082, 1 }, { 7142, 7155, 1 }, { 7204, 7223, 1 }, { 7376, 7378, 1 }, { 7380, 7400, 1 }, { 7405, 7410, 5 }, { 7616, 7654, 1 }, { 7676, 7679, 1 }, { 8400, 8432, 1 }, { 11503, 11505, 1 }, { 11647, 11744, 97 }, { 11745, 11775, 1 }, { 12330, 12335, 1 }, { 12441, 12442, 1 }, { 42607, 42610, 1 }, { 42620, 42621, 1 }, { 42736, 42737, 1 }, { 43010, 43014, 4 }, { 43019, 43043, 24 }, { 43044, 43047, 1 }, { 43136, 43137, 1 }, { 43188, 43204, 1 }, { 43232, 43249, 1 }, { 43302, 43309, 1 }, { 43335, 43347, 1 }, { 43392, 43395, 1 }, { 43443, 43456, 1 }, { 43561, 43574, 1 }, { 43587, 43596, 9 }, { 43597, 43643, 46 }, { 43696, 43698, 2 }, { 43699, 43700, 1 }, { 43703, 43704, 1 }, { 43710, 43711, 1 }, { 43713, 44003, 290 }, { 44004, 44010, 1 }, { 44012, 44013, 1 }, { 64286, 65024, 738 }, { 65025, 65039, 1 }, { 65056, 65062, 1 }, { 66045, 68097, 2052 }, { 68098, 68099, 1 }, { 68101, 68102, 1 }, { 68108, 68111, 1 }, { 68152, 68154, 1 }, { 68159, 69632, 1473 }, { 69633, 69634, 1 }, { 69688, 69702, 1 }, { 69760, 69762, 1 }, { 69808, 69818, 1 }, { 119141, 119145, 1 }, { 119149, 119154, 1 }, { 119163, 119170, 1 }, { 119173, 119179, 1 }, { 119210, 119213, 1 }, { 119362, 119364, 1 }, { 917760, 917999, 1 } };
    }
    
    private static int[][] make_L() {
        return new int[][] { { 65, 90, 1 }, { 97, 122, 1 }, { 170, 181, 11 }, { 186, 192, 6 }, { 193, 214, 1 }, { 216, 246, 1 }, { 248, 705, 1 }, { 710, 721, 1 }, { 736, 740, 1 }, { 748, 750, 2 }, { 880, 884, 1 }, { 886, 887, 1 }, { 890, 893, 1 }, { 902, 904, 2 }, { 905, 906, 1 }, { 908, 910, 2 }, { 911, 929, 1 }, { 931, 1013, 1 }, { 1015, 1153, 1 }, { 1162, 1319, 1 }, { 1329, 1366, 1 }, { 1369, 1377, 8 }, { 1378, 1415, 1 }, { 1488, 1514, 1 }, { 1520, 1522, 1 }, { 1568, 1610, 1 }, { 1646, 1647, 1 }, { 1649, 1747, 1 }, { 1749, 1765, 16 }, { 1766, 1774, 8 }, { 1775, 1786, 11 }, { 1787, 1788, 1 }, { 1791, 1808, 17 }, { 1810, 1839, 1 }, { 1869, 1957, 1 }, { 1969, 1994, 25 }, { 1995, 2026, 1 }, { 2036, 2037, 1 }, { 2042, 2048, 6 }, { 2049, 2069, 1 }, { 2074, 2084, 10 }, { 2088, 2112, 24 }, { 2113, 2136, 1 }, { 2308, 2361, 1 }, { 2365, 2384, 19 }, { 2392, 2401, 1 }, { 2417, 2423, 1 }, { 2425, 2431, 1 }, { 2437, 2444, 1 }, { 2447, 2448, 1 }, { 2451, 2472, 1 }, { 2474, 2480, 1 }, { 2482, 2486, 4 }, { 2487, 2489, 1 }, { 2493, 2510, 17 }, { 2524, 2525, 1 }, { 2527, 2529, 1 }, { 2544, 2545, 1 }, { 2565, 2570, 1 }, { 2575, 2576, 1 }, { 2579, 2600, 1 }, { 2602, 2608, 1 }, { 2610, 2611, 1 }, { 2613, 2614, 1 }, { 2616, 2617, 1 }, { 2649, 2652, 1 }, { 2654, 2674, 20 }, { 2675, 2676, 1 }, { 2693, 2701, 1 }, { 2703, 2705, 1 }, { 2707, 2728, 1 }, { 2730, 2736, 1 }, { 2738, 2739, 1 }, { 2741, 2745, 1 }, { 2749, 2768, 19 }, { 2784, 2785, 1 }, { 2821, 2828, 1 }, { 2831, 2832, 1 }, { 2835, 2856, 1 }, { 2858, 2864, 1 }, { 2866, 2867, 1 }, { 2869, 2873, 1 }, { 2877, 2908, 31 }, { 2909, 2911, 2 }, { 2912, 2913, 1 }, { 2929, 2947, 18 }, { 2949, 2954, 1 }, { 2958, 2960, 1 }, { 2962, 2965, 1 }, { 2969, 2970, 1 }, { 2972, 2974, 2 }, { 2975, 2979, 4 }, { 2980, 2984, 4 }, { 2985, 2986, 1 }, { 2990, 3001, 1 }, { 3024, 3077, 53 }, { 3078, 3084, 1 }, { 3086, 3088, 1 }, { 3090, 3112, 1 }, { 3114, 3123, 1 }, { 3125, 3129, 1 }, { 3133, 3160, 27 }, { 3161, 3168, 7 }, { 3169, 3205, 36 }, { 3206, 3212, 1 }, { 3214, 3216, 1 }, { 3218, 3240, 1 }, { 3242, 3251, 1 }, { 3253, 3257, 1 }, { 3261, 3294, 33 }, { 3296, 3297, 1 }, { 3313, 3314, 1 }, { 3333, 3340, 1 }, { 3342, 3344, 1 }, { 3346, 3386, 1 }, { 3389, 3406, 17 }, { 3424, 3425, 1 }, { 3450, 3455, 1 }, { 3461, 3478, 1 }, { 3482, 3505, 1 }, { 3507, 3515, 1 }, { 3517, 3520, 3 }, { 3521, 3526, 1 }, { 3585, 3632, 1 }, { 3634, 3635, 1 }, { 3648, 3654, 1 }, { 3713, 3714, 1 }, { 3716, 3719, 3 }, { 3720, 3722, 2 }, { 3725, 3732, 7 }, { 3733, 3735, 1 }, { 3737, 3743, 1 }, { 3745, 3747, 1 }, { 3749, 3751, 2 }, { 3754, 3755, 1 }, { 3757, 3760, 1 }, { 3762, 3763, 1 }, { 3773, 3776, 3 }, { 3777, 3780, 1 }, { 3782, 3804, 22 }, { 3805, 3840, 35 }, { 3904, 3911, 1 }, { 3913, 3948, 1 }, { 3976, 3980, 1 }, { 4096, 4138, 1 }, { 4159, 4176, 17 }, { 4177, 4181, 1 }, { 4186, 4189, 1 }, { 4193, 4197, 4 }, { 4198, 4206, 8 }, { 4207, 4208, 1 }, { 4213, 4225, 1 }, { 4238, 4256, 18 }, { 4257, 4293, 1 }, { 4304, 4346, 1 }, { 4348, 4352, 4 }, { 4353, 4680, 1 }, { 4682, 4685, 1 }, { 4688, 4694, 1 }, { 4696, 4698, 2 }, { 4699, 4701, 1 }, { 4704, 4744, 1 }, { 4746, 4749, 1 }, { 4752, 4784, 1 }, { 4786, 4789, 1 }, { 4792, 4798, 1 }, { 4800, 4802, 2 }, { 4803, 4805, 1 }, { 4808, 4822, 1 }, { 4824, 4880, 1 }, { 4882, 4885, 1 }, { 4888, 4954, 1 }, { 4992, 5007, 1 }, { 5024, 5108, 1 }, { 5121, 5740, 1 }, { 5743, 5759, 1 }, { 5761, 5786, 1 }, { 5792, 5866, 1 }, { 5888, 5900, 1 }, { 5902, 5905, 1 }, { 5920, 5937, 1 }, { 5952, 5969, 1 }, { 5984, 5996, 1 }, { 5998, 6000, 1 }, { 6016, 6067, 1 }, { 6103, 6108, 5 }, { 6176, 6263, 1 }, { 6272, 6312, 1 }, { 6314, 6320, 6 }, { 6321, 6389, 1 }, { 6400, 6428, 1 }, { 6480, 6509, 1 }, { 6512, 6516, 1 }, { 6528, 6571, 1 }, { 6593, 6599, 1 }, { 6656, 6678, 1 }, { 6688, 6740, 1 }, { 6823, 6917, 94 }, { 6918, 6963, 1 }, { 6981, 6987, 1 }, { 7043, 7072, 1 }, { 7086, 7087, 1 }, { 7104, 7141, 1 }, { 7168, 7203, 1 }, { 7245, 7247, 1 }, { 7258, 7293, 1 }, { 7401, 7404, 1 }, { 7406, 7409, 1 }, { 7424, 7615, 1 }, { 7680, 7957, 1 }, { 7960, 7965, 1 }, { 7968, 8005, 1 }, { 8008, 8013, 1 }, { 8016, 8023, 1 }, { 8025, 8031, 2 }, { 8032, 8061, 1 }, { 8064, 8116, 1 }, { 8118, 8124, 1 }, { 8126, 8130, 4 }, { 8131, 8132, 1 }, { 8134, 8140, 1 }, { 8144, 8147, 1 }, { 8150, 8155, 1 }, { 8160, 8172, 1 }, { 8178, 8180, 1 }, { 8182, 8188, 1 }, { 8305, 8319, 14 }, { 8336, 8348, 1 }, { 8450, 8455, 5 }, { 8458, 8467, 1 }, { 8469, 8473, 4 }, { 8474, 8477, 1 }, { 8484, 8490, 2 }, { 8491, 8493, 1 }, { 8495, 8505, 1 }, { 8508, 8511, 1 }, { 8517, 8521, 1 }, { 8526, 8579, 53 }, { 8580, 11264, 2684 }, { 11265, 11310, 1 }, { 11312, 11358, 1 }, { 11360, 11492, 1 }, { 11499, 11502, 1 }, { 11520, 11557, 1 }, { 11568, 11621, 1 }, { 11631, 11648, 17 }, { 11649, 11670, 1 }, { 11680, 11686, 1 }, { 11688, 11694, 1 }, { 11696, 11702, 1 }, { 11704, 11710, 1 }, { 11712, 11718, 1 }, { 11720, 11726, 1 }, { 11728, 11734, 1 }, { 11736, 11742, 1 }, { 11823, 12293, 470 }, { 12294, 12337, 43 }, { 12338, 12341, 1 }, { 12347, 12348, 1 }, { 12353, 12438, 1 }, { 12445, 12447, 1 }, { 12449, 12538, 1 }, { 12540, 12543, 1 }, { 12549, 12589, 1 }, { 12593, 12686, 1 }, { 12704, 12730, 1 }, { 12784, 12799, 1 }, { 13312, 19893, 1 }, { 19968, 40907, 1 }, { 40960, 42124, 1 }, { 42192, 42237, 1 }, { 42240, 42508, 1 }, { 42512, 42527, 1 }, { 42538, 42539, 1 }, { 42560, 42606, 1 }, { 42623, 42647, 1 }, { 42656, 42725, 1 }, { 42775, 42783, 1 }, { 42786, 42888, 1 }, { 42891, 42894, 1 }, { 42896, 42897, 1 }, { 42912, 42921, 1 }, { 43002, 43009, 1 }, { 43011, 43013, 1 }, { 43015, 43018, 1 }, { 43020, 43042, 1 }, { 43072, 43123, 1 }, { 43138, 43187, 1 }, { 43250, 43255, 1 }, { 43259, 43274, 15 }, { 43275, 43301, 1 }, { 43312, 43334, 1 }, { 43360, 43388, 1 }, { 43396, 43442, 1 }, { 43471, 43520, 49 }, { 43521, 43560, 1 }, { 43584, 43586, 1 }, { 43588, 43595, 1 }, { 43616, 43638, 1 }, { 43642, 43648, 6 }, { 43649, 43695, 1 }, { 43697, 43701, 4 }, { 43702, 43705, 3 }, { 43706, 43709, 1 }, { 43712, 43714, 2 }, { 43739, 43741, 1 }, { 43777, 43782, 1 }, { 43785, 43790, 1 }, { 43793, 43798, 1 }, { 43808, 43814, 1 }, { 43816, 43822, 1 }, { 43968, 44002, 1 }, { 44032, 55203, 1 }, { 55216, 55238, 1 }, { 55243, 55291, 1 }, { 63744, 64045, 1 }, { 64048, 64109, 1 }, { 64112, 64217, 1 }, { 64256, 64262, 1 }, { 64275, 64279, 1 }, { 64285, 64287, 2 }, { 64288, 64296, 1 }, { 64298, 64310, 1 }, { 64312, 64316, 1 }, { 64318, 64320, 2 }, { 64321, 64323, 2 }, { 64324, 64326, 2 }, { 64327, 64433, 1 }, { 64467, 64829, 1 }, { 64848, 64911, 1 }, { 64914, 64967, 1 }, { 65008, 65019, 1 }, { 65136, 65140, 1 }, { 65142, 65276, 1 }, { 65313, 65338, 1 }, { 65345, 65370, 1 }, { 65382, 65470, 1 }, { 65474, 65479, 1 }, { 65482, 65487, 1 }, { 65490, 65495, 1 }, { 65498, 65500, 1 }, { 65536, 65547, 1 }, { 65549, 65574, 1 }, { 65576, 65594, 1 }, { 65596, 65597, 1 }, { 65599, 65613, 1 }, { 65616, 65629, 1 }, { 65664, 65786, 1 }, { 66176, 66204, 1 }, { 66208, 66256, 1 }, { 66304, 66334, 1 }, { 66352, 66368, 1 }, { 66370, 66377, 1 }, { 66432, 66461, 1 }, { 66464, 66499, 1 }, { 66504, 66511, 1 }, { 66560, 66717, 1 }, { 67584, 67589, 1 }, { 67592, 67594, 2 }, { 67595, 67637, 1 }, { 67639, 67640, 1 }, { 67644, 67647, 3 }, { 67648, 67669, 1 }, { 67840, 67861, 1 }, { 67872, 67897, 1 }, { 68096, 68112, 16 }, { 68113, 68115, 1 }, { 68117, 68119, 1 }, { 68121, 68147, 1 }, { 68192, 68220, 1 }, { 68352, 68405, 1 }, { 68416, 68437, 1 }, { 68448, 68466, 1 }, { 68608, 68680, 1 }, { 69635, 69687, 1 }, { 69763, 69807, 1 }, { 73728, 74606, 1 }, { 77824, 78894, 1 }, { 92160, 92728, 1 }, { 110592, 110593, 1 }, { 119808, 119892, 1 }, { 119894, 119964, 1 }, { 119966, 119967, 1 }, { 119970, 119973, 3 }, { 119974, 119977, 3 }, { 119978, 119980, 1 }, { 119982, 119993, 1 }, { 119995, 119997, 2 }, { 119998, 120003, 1 }, { 120005, 120069, 1 }, { 120071, 120074, 1 }, { 120077, 120084, 1 }, { 120086, 120092, 1 }, { 120094, 120121, 1 }, { 120123, 120126, 1 }, { 120128, 120132, 1 }, { 120134, 120138, 4 }, { 120139, 120144, 1 }, { 120146, 120485, 1 }, { 120488, 120512, 1 }, { 120514, 120538, 1 }, { 120540, 120570, 1 }, { 120572, 120596, 1 }, { 120598, 120628, 1 }, { 120630, 120654, 1 }, { 120656, 120686, 1 }, { 120688, 120712, 1 }, { 120714, 120744, 1 }, { 120746, 120770, 1 }, { 120772, 120779, 1 }, { 131072, 173782, 1 }, { 173824, 177972, 1 }, { 177984, 178205, 1 }, { 194560, 195101, 1 } };
    }
    
    private static int[][] make_N() {
        return new int[][] { { 48, 57, 1 }, { 178, 179, 1 }, { 185, 188, 3 }, { 189, 190, 1 }, { 1632, 1641, 1 }, { 1776, 1785, 1 }, { 1984, 1993, 1 }, { 2406, 2415, 1 }, { 2534, 2543, 1 }, { 2548, 2553, 1 }, { 2662, 2671, 1 }, { 2790, 2799, 1 }, { 2918, 2927, 1 }, { 2930, 2935, 1 }, { 3046, 3058, 1 }, { 3174, 3183, 1 }, { 3192, 3198, 1 }, { 3302, 3311, 1 }, { 3430, 3445, 1 }, { 3664, 3673, 1 }, { 3792, 3801, 1 }, { 3872, 3891, 1 }, { 4160, 4169, 1 }, { 4240, 4249, 1 }, { 4969, 4988, 1 }, { 5870, 5872, 1 }, { 6112, 6121, 1 }, { 6128, 6137, 1 }, { 6160, 6169, 1 }, { 6470, 6479, 1 }, { 6608, 6618, 1 }, { 6784, 6793, 1 }, { 6800, 6809, 1 }, { 6992, 7001, 1 }, { 7088, 7097, 1 }, { 7232, 7241, 1 }, { 7248, 7257, 1 }, { 8304, 8308, 4 }, { 8309, 8313, 1 }, { 8320, 8329, 1 }, { 8528, 8578, 1 }, { 8581, 8585, 1 }, { 9312, 9371, 1 }, { 9450, 9471, 1 }, { 10102, 10131, 1 }, { 11517, 12295, 778 }, { 12321, 12329, 1 }, { 12344, 12346, 1 }, { 12690, 12693, 1 }, { 12832, 12841, 1 }, { 12881, 12895, 1 }, { 12928, 12937, 1 }, { 12977, 12991, 1 }, { 42528, 42537, 1 }, { 42726, 42735, 1 }, { 43056, 43061, 1 }, { 43216, 43225, 1 }, { 43264, 43273, 1 }, { 43472, 43481, 1 }, { 43600, 43609, 1 }, { 44016, 44025, 1 }, { 65296, 65305, 1 }, { 65799, 65843, 1 }, { 65856, 65912, 1 }, { 65930, 66336, 406 }, { 66337, 66339, 1 }, { 66369, 66378, 9 }, { 66513, 66517, 1 }, { 66720, 66729, 1 }, { 67672, 67679, 1 }, { 67862, 67867, 1 }, { 68160, 68167, 1 }, { 68221, 68222, 1 }, { 68440, 68447, 1 }, { 68472, 68479, 1 }, { 69216, 69246, 1 }, { 69714, 69743, 1 }, { 74752, 74850, 1 }, { 119648, 119665, 1 }, { 120782, 120831, 1 }, { 127232, 127242, 1 } };
    }
    
    private static int[][] make_P() {
        return new int[][] { { 33, 35, 1 }, { 37, 42, 1 }, { 44, 47, 1 }, { 58, 59, 1 }, { 63, 64, 1 }, { 91, 93, 1 }, { 95, 123, 28 }, { 125, 161, 36 }, { 171, 183, 12 }, { 187, 191, 4 }, { 894, 903, 9 }, { 1370, 1375, 1 }, { 1417, 1418, 1 }, { 1470, 1472, 2 }, { 1475, 1478, 3 }, { 1523, 1524, 1 }, { 1545, 1546, 1 }, { 1548, 1549, 1 }, { 1563, 1566, 3 }, { 1567, 1642, 75 }, { 1643, 1645, 1 }, { 1748, 1792, 44 }, { 1793, 1805, 1 }, { 2039, 2041, 1 }, { 2096, 2110, 1 }, { 2142, 2404, 262 }, { 2405, 2416, 11 }, { 3572, 3663, 91 }, { 3674, 3675, 1 }, { 3844, 3858, 1 }, { 3898, 3901, 1 }, { 3973, 4048, 75 }, { 4049, 4052, 1 }, { 4057, 4058, 1 }, { 4170, 4175, 1 }, { 4347, 4961, 614 }, { 4962, 4968, 1 }, { 5120, 5741, 621 }, { 5742, 5787, 45 }, { 5788, 5867, 79 }, { 5868, 5869, 1 }, { 5941, 5942, 1 }, { 6100, 6102, 1 }, { 6104, 6106, 1 }, { 6144, 6154, 1 }, { 6468, 6469, 1 }, { 6686, 6687, 1 }, { 6816, 6822, 1 }, { 6824, 6829, 1 }, { 7002, 7008, 1 }, { 7164, 7167, 1 }, { 7227, 7231, 1 }, { 7294, 7295, 1 }, { 7379, 8208, 829 }, { 8209, 8231, 1 }, { 8240, 8259, 1 }, { 8261, 8273, 1 }, { 8275, 8286, 1 }, { 8317, 8318, 1 }, { 8333, 8334, 1 }, { 9001, 9002, 1 }, { 10088, 10101, 1 }, { 10181, 10182, 1 }, { 10214, 10223, 1 }, { 10627, 10648, 1 }, { 10712, 10715, 1 }, { 10748, 10749, 1 }, { 11513, 11516, 1 }, { 11518, 11519, 1 }, { 11632, 11776, 144 }, { 11777, 11822, 1 }, { 11824, 11825, 1 }, { 12289, 12291, 1 }, { 12296, 12305, 1 }, { 12308, 12319, 1 }, { 12336, 12349, 13 }, { 12448, 12539, 91 }, { 42238, 42239, 1 }, { 42509, 42511, 1 }, { 42611, 42622, 11 }, { 42738, 42743, 1 }, { 43124, 43127, 1 }, { 43214, 43215, 1 }, { 43256, 43258, 1 }, { 43310, 43311, 1 }, { 43359, 43457, 98 }, { 43458, 43469, 1 }, { 43486, 43487, 1 }, { 43612, 43615, 1 }, { 43742, 43743, 1 }, { 44011, 64830, 20819 }, { 64831, 65040, 209 }, { 65041, 65049, 1 }, { 65072, 65106, 1 }, { 65108, 65121, 1 }, { 65123, 65128, 5 }, { 65130, 65131, 1 }, { 65281, 65283, 1 }, { 65285, 65290, 1 }, { 65292, 65295, 1 }, { 65306, 65307, 1 }, { 65311, 65312, 1 }, { 65339, 65341, 1 }, { 65343, 65371, 28 }, { 65373, 65375, 2 }, { 65376, 65381, 1 }, { 65792, 65793, 1 }, { 66463, 66512, 49 }, { 67671, 67871, 200 }, { 67903, 68176, 273 }, { 68177, 68184, 1 }, { 68223, 68409, 186 }, { 68410, 68415, 1 }, { 69703, 69709, 1 }, { 69819, 69820, 1 }, { 69822, 69825, 1 }, { 74864, 74867, 1 } };
    }
    
    private static int[][] make_S() {
        return new int[][] { { 36, 43, 7 }, { 60, 62, 1 }, { 94, 96, 2 }, { 124, 126, 2 }, { 162, 169, 1 }, { 172, 174, 2 }, { 175, 177, 1 }, { 180, 184, 2 }, { 215, 247, 32 }, { 706, 709, 1 }, { 722, 735, 1 }, { 741, 747, 1 }, { 749, 751, 2 }, { 752, 767, 1 }, { 885, 900, 15 }, { 901, 1014, 113 }, { 1154, 1542, 388 }, { 1543, 1544, 1 }, { 1547, 1550, 3 }, { 1551, 1758, 207 }, { 1769, 1789, 20 }, { 1790, 2038, 248 }, { 2546, 2547, 1 }, { 2554, 2555, 1 }, { 2801, 2928, 127 }, { 3059, 3066, 1 }, { 3199, 3449, 250 }, { 3647, 3841, 194 }, { 3842, 3843, 1 }, { 3859, 3863, 1 }, { 3866, 3871, 1 }, { 3892, 3896, 2 }, { 4030, 4037, 1 }, { 4039, 4044, 1 }, { 4046, 4047, 1 }, { 4053, 4056, 1 }, { 4254, 4255, 1 }, { 4960, 5008, 48 }, { 5009, 5017, 1 }, { 6107, 6464, 357 }, { 6622, 6655, 1 }, { 7009, 7018, 1 }, { 7028, 7036, 1 }, { 8125, 8127, 2 }, { 8128, 8129, 1 }, { 8141, 8143, 1 }, { 8157, 8159, 1 }, { 8173, 8175, 1 }, { 8189, 8190, 1 }, { 8260, 8274, 14 }, { 8314, 8316, 1 }, { 8330, 8332, 1 }, { 8352, 8377, 1 }, { 8448, 8449, 1 }, { 8451, 8454, 1 }, { 8456, 8457, 1 }, { 8468, 8470, 2 }, { 8471, 8472, 1 }, { 8478, 8483, 1 }, { 8485, 8489, 2 }, { 8494, 8506, 12 }, { 8507, 8512, 5 }, { 8513, 8516, 1 }, { 8522, 8525, 1 }, { 8527, 8592, 65 }, { 8593, 9000, 1 }, { 9003, 9203, 1 }, { 9216, 9254, 1 }, { 9280, 9290, 1 }, { 9372, 9449, 1 }, { 9472, 9983, 1 }, { 9985, 10087, 1 }, { 10132, 10180, 1 }, { 10183, 10186, 1 }, { 10188, 10190, 2 }, { 10191, 10213, 1 }, { 10224, 10626, 1 }, { 10649, 10711, 1 }, { 10716, 10747, 1 }, { 10750, 11084, 1 }, { 11088, 11097, 1 }, { 11493, 11498, 1 }, { 11904, 11929, 1 }, { 11931, 12019, 1 }, { 12032, 12245, 1 }, { 12272, 12283, 1 }, { 12292, 12306, 14 }, { 12307, 12320, 13 }, { 12342, 12343, 1 }, { 12350, 12351, 1 }, { 12443, 12444, 1 }, { 12688, 12689, 1 }, { 12694, 12703, 1 }, { 12736, 12771, 1 }, { 12800, 12830, 1 }, { 12842, 12880, 1 }, { 12896, 12927, 1 }, { 12938, 12976, 1 }, { 12992, 13054, 1 }, { 13056, 13311, 1 }, { 19904, 19967, 1 }, { 42128, 42182, 1 }, { 42752, 42774, 1 }, { 42784, 42785, 1 }, { 42889, 42890, 1 }, { 43048, 43051, 1 }, { 43062, 43065, 1 }, { 43639, 43641, 1 }, { 64297, 64434, 137 }, { 64435, 64449, 1 }, { 65020, 65021, 1 }, { 65122, 65124, 2 }, { 65125, 65126, 1 }, { 65129, 65284, 155 }, { 65291, 65308, 17 }, { 65309, 65310, 1 }, { 65342, 65344, 2 }, { 65372, 65374, 2 }, { 65504, 65510, 1 }, { 65512, 65518, 1 }, { 65532, 65533, 1 }, { 65794, 65847, 53 }, { 65848, 65855, 1 }, { 65913, 65929, 1 }, { 65936, 65947, 1 }, { 66000, 66044, 1 }, { 118784, 119029, 1 }, { 119040, 119078, 1 }, { 119081, 119140, 1 }, { 119146, 119148, 1 }, { 119171, 119172, 1 }, { 119180, 119209, 1 }, { 119214, 119261, 1 }, { 119296, 119361, 1 }, { 119365, 119552, 187 }, { 119553, 119638, 1 }, { 120513, 120539, 26 }, { 120571, 120597, 26 }, { 120629, 120655, 26 }, { 120687, 120713, 26 }, { 120745, 120771, 26 }, { 126976, 127019, 1 }, { 127024, 127123, 1 }, { 127136, 127150, 1 }, { 127153, 127166, 1 }, { 127169, 127183, 1 }, { 127185, 127199, 1 }, { 127248, 127278, 1 }, { 127280, 127337, 1 }, { 127344, 127386, 1 }, { 127462, 127490, 1 }, { 127504, 127546, 1 }, { 127552, 127560, 1 }, { 127568, 127569, 1 }, { 127744, 127776, 1 }, { 127792, 127797, 1 }, { 127799, 127868, 1 }, { 127872, 127891, 1 }, { 127904, 127940, 1 }, { 127942, 127946, 1 }, { 127968, 127984, 1 }, { 128000, 128062, 1 }, { 128064, 128066, 2 }, { 128067, 128247, 1 }, { 128249, 128252, 1 }, { 128256, 128317, 1 }, { 128336, 128359, 1 }, { 128507, 128511, 1 }, { 128513, 128528, 1 }, { 128530, 128532, 1 }, { 128534, 128540, 2 }, { 128541, 128542, 1 }, { 128544, 128549, 1 }, { 128552, 128555, 1 }, { 128557, 128560, 3 }, { 128561, 128563, 1 }, { 128565, 128576, 1 }, { 128581, 128591, 1 }, { 128640, 128709, 1 }, { 128768, 128883, 1 } };
    }
    
    private static int[][] make_Z() {
        return new int[][] { { 32, 160, 128 }, { 5760, 6158, 398 }, { 8192, 8202, 1 }, { 8232, 8233, 1 }, { 8239, 8287, 48 }, { 12288, 12288, 1 } };
    }
    
    private static int[][] make_Me() {
        return new int[][] { { 1160, 1161, 1 }, { 8413, 8416, 1 }, { 8418, 8420, 1 }, { 42608, 42610, 1 } };
    }
    
    private static int[][] make_Mc() {
        return new int[][] { { 2307, 2363, 56 }, { 2366, 2368, 1 }, { 2377, 2380, 1 }, { 2382, 2383, 1 }, { 2434, 2435, 1 }, { 2494, 2496, 1 }, { 2503, 2504, 1 }, { 2507, 2508, 1 }, { 2519, 2563, 44 }, { 2622, 2624, 1 }, { 2691, 2750, 59 }, { 2751, 2752, 1 }, { 2761, 2763, 2 }, { 2764, 2818, 54 }, { 2819, 2878, 59 }, { 2880, 2887, 7 }, { 2888, 2891, 3 }, { 2892, 2903, 11 }, { 3006, 3007, 1 }, { 3009, 3010, 1 }, { 3014, 3016, 1 }, { 3018, 3020, 1 }, { 3031, 3073, 42 }, { 3074, 3075, 1 }, { 3137, 3140, 1 }, { 3202, 3203, 1 }, { 3262, 3264, 2 }, { 3265, 3268, 1 }, { 3271, 3272, 1 }, { 3274, 3275, 1 }, { 3285, 3286, 1 }, { 3330, 3331, 1 }, { 3390, 3392, 1 }, { 3398, 3400, 1 }, { 3402, 3404, 1 }, { 3415, 3458, 43 }, { 3459, 3535, 76 }, { 3536, 3537, 1 }, { 3544, 3551, 1 }, { 3570, 3571, 1 }, { 3902, 3903, 1 }, { 3967, 4139, 172 }, { 4140, 4145, 5 }, { 4152, 4155, 3 }, { 4156, 4182, 26 }, { 4183, 4194, 11 }, { 4195, 4196, 1 }, { 4199, 4205, 1 }, { 4227, 4228, 1 }, { 4231, 4236, 1 }, { 4239, 4250, 11 }, { 4251, 4252, 1 }, { 6070, 6078, 8 }, { 6079, 6085, 1 }, { 6087, 6088, 1 }, { 6435, 6438, 1 }, { 6441, 6443, 1 }, { 6448, 6449, 1 }, { 6451, 6456, 1 }, { 6576, 6592, 1 }, { 6600, 6601, 1 }, { 6681, 6683, 1 }, { 6741, 6743, 2 }, { 6753, 6755, 2 }, { 6756, 6765, 9 }, { 6766, 6770, 1 }, { 6916, 6965, 49 }, { 6971, 6973, 2 }, { 6974, 6977, 1 }, { 6979, 6980, 1 }, { 7042, 7073, 31 }, { 7078, 7079, 1 }, { 7082, 7143, 61 }, { 7146, 7148, 1 }, { 7150, 7154, 4 }, { 7155, 7204, 49 }, { 7205, 7211, 1 }, { 7220, 7221, 1 }, { 7393, 7410, 17 }, { 43043, 43044, 1 }, { 43047, 43136, 89 }, { 43137, 43188, 51 }, { 43189, 43203, 1 }, { 43346, 43347, 1 }, { 43395, 43444, 49 }, { 43445, 43450, 5 }, { 43451, 43453, 2 }, { 43454, 43456, 1 }, { 43567, 43568, 1 }, { 43571, 43572, 1 }, { 43597, 43643, 46 }, { 44003, 44004, 1 }, { 44006, 44007, 1 }, { 44009, 44010, 1 }, { 44012, 44012, 1 }, { 69632, 69632, 1 }, { 69634, 69762, 128 }, { 69808, 69810, 1 }, { 69815, 69816, 1 }, { 119141, 119142, 1 }, { 119149, 119154, 1 } };
    }
    
    private static int[][] make_Mn() {
        return new int[][] { { 768, 879, 1 }, { 1155, 1159, 1 }, { 1425, 1469, 1 }, { 1471, 1473, 2 }, { 1474, 1476, 2 }, { 1477, 1479, 2 }, { 1552, 1562, 1 }, { 1611, 1631, 1 }, { 1648, 1750, 102 }, { 1751, 1756, 1 }, { 1759, 1764, 1 }, { 1767, 1768, 1 }, { 1770, 1773, 1 }, { 1809, 1840, 31 }, { 1841, 1866, 1 }, { 1958, 1968, 1 }, { 2027, 2035, 1 }, { 2070, 2073, 1 }, { 2075, 2083, 1 }, { 2085, 2087, 1 }, { 2089, 2093, 1 }, { 2137, 2139, 1 }, { 2304, 2306, 1 }, { 2362, 2364, 2 }, { 2369, 2376, 1 }, { 2381, 2385, 4 }, { 2386, 2391, 1 }, { 2402, 2403, 1 }, { 2433, 2492, 59 }, { 2497, 2500, 1 }, { 2509, 2530, 21 }, { 2531, 2561, 30 }, { 2562, 2620, 58 }, { 2625, 2626, 1 }, { 2631, 2632, 1 }, { 2635, 2637, 1 }, { 2641, 2672, 31 }, { 2673, 2677, 4 }, { 2689, 2690, 1 }, { 2748, 2753, 5 }, { 2754, 2757, 1 }, { 2759, 2760, 1 }, { 2765, 2786, 21 }, { 2787, 2817, 30 }, { 2876, 2879, 3 }, { 2881, 2884, 1 }, { 2893, 2902, 9 }, { 2914, 2915, 1 }, { 2946, 3008, 62 }, { 3021, 3134, 113 }, { 3135, 3136, 1 }, { 3142, 3144, 1 }, { 3146, 3149, 1 }, { 3157, 3158, 1 }, { 3170, 3171, 1 }, { 3260, 3263, 3 }, { 3270, 3276, 6 }, { 3277, 3298, 21 }, { 3299, 3393, 94 }, { 3394, 3396, 1 }, { 3405, 3426, 21 }, { 3427, 3530, 103 }, { 3538, 3540, 1 }, { 3542, 3633, 91 }, { 3636, 3642, 1 }, { 3655, 3662, 1 }, { 3761, 3764, 3 }, { 3765, 3769, 1 }, { 3771, 3772, 1 }, { 3784, 3789, 1 }, { 3864, 3865, 1 }, { 3893, 3897, 2 }, { 3953, 3966, 1 }, { 3968, 3972, 1 }, { 3974, 3975, 1 }, { 3981, 3991, 1 }, { 3993, 4028, 1 }, { 4038, 4141, 103 }, { 4142, 4144, 1 }, { 4146, 4151, 1 }, { 4153, 4154, 1 }, { 4157, 4158, 1 }, { 4184, 4185, 1 }, { 4190, 4192, 1 }, { 4209, 4212, 1 }, { 4226, 4229, 3 }, { 4230, 4237, 7 }, { 4253, 4957, 704 }, { 4958, 4959, 1 }, { 5906, 5908, 1 }, { 5938, 5940, 1 }, { 5970, 5971, 1 }, { 6002, 6003, 1 }, { 6071, 6077, 1 }, { 6086, 6089, 3 }, { 6090, 6099, 1 }, { 6109, 6155, 46 }, { 6156, 6157, 1 }, { 6313, 6432, 119 }, { 6433, 6434, 1 }, { 6439, 6440, 1 }, { 6450, 6457, 7 }, { 6458, 6459, 1 }, { 6679, 6680, 1 }, { 6742, 6744, 2 }, { 6745, 6750, 1 }, { 6752, 6754, 2 }, { 6757, 6764, 1 }, { 6771, 6780, 1 }, { 6783, 6912, 129 }, { 6913, 6915, 1 }, { 6964, 6966, 2 }, { 6967, 6970, 1 }, { 6972, 6978, 6 }, { 7019, 7027, 1 }, { 7040, 7041, 1 }, { 7074, 7077, 1 }, { 7080, 7081, 1 }, { 7142, 7144, 2 }, { 7145, 7149, 4 }, { 7151, 7153, 1 }, { 7212, 7219, 1 }, { 7222, 7223, 1 }, { 7376, 7378, 1 }, { 7380, 7392, 1 }, { 7394, 7400, 1 }, { 7405, 7616, 211 }, { 7617, 7654, 1 }, { 7676, 7679, 1 }, { 8400, 8412, 1 }, { 8417, 8421, 4 }, { 8422, 8432, 1 }, { 11503, 11505, 1 }, { 11647, 11744, 97 }, { 11745, 11775, 1 }, { 12330, 12335, 1 }, { 12441, 12442, 1 }, { 42607, 42620, 13 }, { 42621, 42736, 115 }, { 42737, 43010, 273 }, { 43014, 43019, 5 }, { 43045, 43046, 1 }, { 43204, 43232, 28 }, { 43233, 43249, 1 }, { 43302, 43309, 1 }, { 43335, 43345, 1 }, { 43392, 43394, 1 }, { 43443, 43446, 3 }, { 43447, 43449, 1 }, { 43452, 43561, 109 }, { 43562, 43566, 1 }, { 43569, 43570, 1 }, { 43573, 43574, 1 }, { 43587, 43596, 9 }, { 43696, 43698, 2 }, { 43699, 43700, 1 }, { 43703, 43704, 1 }, { 43710, 43711, 1 }, { 43713, 44005, 292 }, { 44008, 44013, 5 }, { 64286, 65024, 738 }, { 65025, 65039, 1 }, { 65056, 65062, 1 }, { 66045, 68097, 2052 }, { 68098, 68099, 1 }, { 68101, 68102, 1 }, { 68108, 68111, 1 }, { 68152, 68154, 1 }, { 68159, 69633, 1474 }, { 69688, 69702, 1 }, { 69760, 69761, 1 }, { 69811, 69814, 1 }, { 69817, 69818, 1 }, { 119143, 119145, 1 }, { 119163, 119170, 1 }, { 119173, 119179, 1 }, { 119210, 119213, 1 }, { 119362, 119364, 1 }, { 917760, 917999, 1 } };
    }
    
    private static int[][] make_Zl() {
        return new int[][] { { 8232, 8232, 1 } };
    }
    
    private static int[][] make_Zp() {
        return new int[][] { { 8233, 8233, 1 } };
    }
    
    private static int[][] make_Zs() {
        return new int[][] { { 32, 160, 128 }, { 5760, 6158, 398 }, { 8192, 8202, 1 }, { 8239, 8287, 48 }, { 12288, 12288, 1 } };
    }
    
    private static int[][] make_Cs() {
        return new int[][] { { 55296, 57343, 1 } };
    }
    
    private static int[][] make_Co() {
        return new int[][] { { 57344, 63743, 1 }, { 983040, 1048573, 1 }, { 1048576, 1114109, 1 } };
    }
    
    private static int[][] make_Cf() {
        return new int[][] { { 173, 1536, 1363 }, { 1537, 1539, 1 }, { 1757, 1807, 50 }, { 6068, 6069, 1 }, { 8203, 8207, 1 }, { 8234, 8238, 1 }, { 8288, 8292, 1 }, { 8298, 8303, 1 }, { 65279, 65529, 250 }, { 65530, 65531, 1 }, { 69821, 119155, 49334 }, { 119156, 119162, 1 }, { 917505, 917536, 31 }, { 917537, 917631, 1 } };
    }
    
    private static int[][] make_Cc() {
        return new int[][] { { 1, 31, 1 }, { 127, 159, 1 } };
    }
    
    private static int[][] make_Po() {
        return new int[][] { { 33, 35, 1 }, { 37, 39, 1 }, { 42, 46, 2 }, { 47, 58, 11 }, { 59, 63, 4 }, { 64, 92, 28 }, { 161, 183, 22 }, { 191, 894, 703 }, { 903, 1370, 467 }, { 1371, 1375, 1 }, { 1417, 1472, 55 }, { 1475, 1478, 3 }, { 1523, 1524, 1 }, { 1545, 1546, 1 }, { 1548, 1549, 1 }, { 1563, 1566, 3 }, { 1567, 1642, 75 }, { 1643, 1645, 1 }, { 1748, 1792, 44 }, { 1793, 1805, 1 }, { 2039, 2041, 1 }, { 2096, 2110, 1 }, { 2142, 2404, 262 }, { 2405, 2416, 11 }, { 3572, 3663, 91 }, { 3674, 3675, 1 }, { 3844, 3858, 1 }, { 3973, 4048, 75 }, { 4049, 4052, 1 }, { 4057, 4058, 1 }, { 4170, 4175, 1 }, { 4347, 4961, 614 }, { 4962, 4968, 1 }, { 5741, 5742, 1 }, { 5867, 5869, 1 }, { 5941, 5942, 1 }, { 6100, 6102, 1 }, { 6104, 6106, 1 }, { 6144, 6149, 1 }, { 6151, 6154, 1 }, { 6468, 6469, 1 }, { 6686, 6687, 1 }, { 6816, 6822, 1 }, { 6824, 6829, 1 }, { 7002, 7008, 1 }, { 7164, 7167, 1 }, { 7227, 7231, 1 }, { 7294, 7295, 1 }, { 7379, 8214, 835 }, { 8215, 8224, 9 }, { 8225, 8231, 1 }, { 8240, 8248, 1 }, { 8251, 8254, 1 }, { 8257, 8259, 1 }, { 8263, 8273, 1 }, { 8275, 8277, 2 }, { 8278, 8286, 1 }, { 11513, 11516, 1 }, { 11518, 11519, 1 }, { 11632, 11776, 144 }, { 11777, 11782, 5 }, { 11783, 11784, 1 }, { 11787, 11790, 3 }, { 11791, 11798, 1 }, { 11800, 11801, 1 }, { 11803, 11806, 3 }, { 11807, 11818, 11 }, { 11819, 11822, 1 }, { 11824, 11825, 1 }, { 12289, 12291, 1 }, { 12349, 12539, 190 }, { 42238, 42239, 1 }, { 42509, 42511, 1 }, { 42611, 42622, 11 }, { 42738, 42743, 1 }, { 43124, 43127, 1 }, { 43214, 43215, 1 }, { 43256, 43258, 1 }, { 43310, 43311, 1 }, { 43359, 43457, 98 }, { 43458, 43469, 1 }, { 43486, 43487, 1 }, { 43612, 43615, 1 }, { 43742, 43743, 1 }, { 44011, 65040, 21029 }, { 65041, 65046, 1 }, { 65049, 65072, 23 }, { 65093, 65094, 1 }, { 65097, 65100, 1 }, { 65104, 65106, 1 }, { 65108, 65111, 1 }, { 65119, 65121, 1 }, { 65128, 65130, 2 }, { 65131, 65281, 150 }, { 65282, 65283, 1 }, { 65285, 65287, 1 }, { 65290, 65294, 2 }, { 65295, 65306, 11 }, { 65307, 65311, 4 }, { 65312, 65340, 28 }, { 65377, 65380, 3 }, { 65381, 65381, 1 }, { 65792, 65792, 1 }, { 65793, 66463, 670 }, { 66512, 67671, 1159 }, { 67871, 67903, 32 }, { 68176, 68184, 1 }, { 68223, 68409, 186 }, { 68410, 68415, 1 }, { 69703, 69709, 1 }, { 69819, 69820, 1 }, { 69822, 69825, 1 }, { 74864, 74867, 1 } };
    }
    
    private static int[][] make_Pi() {
        return new int[][] { { 171, 8216, 8045 }, { 8219, 8220, 1 }, { 8223, 8249, 26 }, { 11778, 11780, 2 }, { 11785, 11788, 3 }, { 11804, 11808, 4 } };
    }
    
    private static int[][] make_Pf() {
        return new int[][] { { 187, 8217, 8030 }, { 8221, 8250, 29 }, { 11779, 11781, 2 }, { 11786, 11789, 3 }, { 11805, 11809, 4 } };
    }
    
    private static int[][] make_Pe() {
        return new int[][] { { 41, 93, 52 }, { 125, 3899, 3774 }, { 3901, 5788, 1887 }, { 8262, 8318, 56 }, { 8334, 9002, 668 }, { 10089, 10101, 2 }, { 10182, 10215, 33 }, { 10217, 10223, 2 }, { 10628, 10648, 2 }, { 10713, 10715, 2 }, { 10749, 11811, 1062 }, { 11813, 11817, 2 }, { 12297, 12305, 2 }, { 12309, 12315, 2 }, { 12318, 12319, 1 }, { 64831, 65048, 217 }, { 65078, 65092, 2 }, { 65096, 65114, 18 }, { 65116, 65118, 2 }, { 65289, 65341, 52 }, { 65373, 65379, 3 } };
    }
    
    private static int[][] make_Pd() {
        return new int[][] { { 45, 1418, 1373 }, { 1470, 5120, 3650 }, { 6150, 8208, 2058 }, { 8209, 8213, 1 }, { 11799, 11802, 3 }, { 12316, 12336, 20 }, { 12448, 65073, 52625 }, { 65074, 65112, 38 }, { 65123, 65293, 170 } };
    }
    
    private static int[][] make_Pc() {
        return new int[][] { { 95, 8255, 8160 }, { 8256, 8276, 20 }, { 65075, 65076, 1 }, { 65101, 65103, 1 }, { 65343, 65343, 1 } };
    }
    
    private static int[][] make_Ps() {
        return new int[][] { { 40, 91, 51 }, { 123, 3898, 3775 }, { 3900, 5787, 1887 }, { 8218, 8222, 4 }, { 8261, 8317, 56 }, { 8333, 9001, 668 }, { 10088, 10100, 2 }, { 10181, 10214, 33 }, { 10216, 10222, 2 }, { 10627, 10647, 2 }, { 10712, 10714, 2 }, { 10748, 11810, 1062 }, { 11812, 11816, 2 }, { 12296, 12304, 2 }, { 12308, 12314, 2 }, { 12317, 64830, 52513 }, { 65047, 65077, 30 }, { 65079, 65091, 2 }, { 65095, 65113, 18 }, { 65115, 65117, 2 }, { 65288, 65339, 51 }, { 65371, 65375, 4 }, { 65378, 65378, 1 } };
    }
    
    private static int[][] make_Nd() {
        return new int[][] { { 48, 57, 1 }, { 1632, 1641, 1 }, { 1776, 1785, 1 }, { 1984, 1993, 1 }, { 2406, 2415, 1 }, { 2534, 2543, 1 }, { 2662, 2671, 1 }, { 2790, 2799, 1 }, { 2918, 2927, 1 }, { 3046, 3055, 1 }, { 3174, 3183, 1 }, { 3302, 3311, 1 }, { 3430, 3439, 1 }, { 3664, 3673, 1 }, { 3792, 3801, 1 }, { 3872, 3881, 1 }, { 4160, 4169, 1 }, { 4240, 4249, 1 }, { 6112, 6121, 1 }, { 6160, 6169, 1 }, { 6470, 6479, 1 }, { 6608, 6617, 1 }, { 6784, 6793, 1 }, { 6800, 6809, 1 }, { 6992, 7001, 1 }, { 7088, 7097, 1 }, { 7232, 7241, 1 }, { 7248, 7257, 1 }, { 42528, 42537, 1 }, { 43216, 43225, 1 }, { 43264, 43273, 1 }, { 43472, 43481, 1 }, { 43600, 43609, 1 }, { 44016, 44025, 1 }, { 65296, 65305, 1 }, { 66720, 66729, 1 }, { 69734, 69743, 1 }, { 120782, 120831, 1 } };
    }
    
    private static int[][] make_Nl() {
        return new int[][] { { 5870, 5872, 1 }, { 8544, 8578, 1 }, { 8581, 8584, 1 }, { 12295, 12321, 26 }, { 12322, 12329, 1 }, { 12344, 12346, 1 }, { 42726, 42735, 1 }, { 65856, 65908, 1 }, { 66369, 66378, 9 }, { 66513, 66517, 1 }, { 74752, 74850, 1 } };
    }
    
    private static int[][] make_No() {
        return new int[][] { { 178, 179, 1 }, { 185, 188, 3 }, { 189, 190, 1 }, { 2548, 2553, 1 }, { 2930, 2935, 1 }, { 3056, 3058, 1 }, { 3192, 3198, 1 }, { 3440, 3445, 1 }, { 3882, 3891, 1 }, { 4969, 4988, 1 }, { 6128, 6137, 1 }, { 6618, 8304, 1686 }, { 8308, 8313, 1 }, { 8320, 8329, 1 }, { 8528, 8543, 1 }, { 8585, 9312, 727 }, { 9313, 9371, 1 }, { 9450, 9471, 1 }, { 10102, 10131, 1 }, { 11517, 12690, 1173 }, { 12691, 12693, 1 }, { 12832, 12841, 1 }, { 12881, 12895, 1 }, { 12928, 12937, 1 }, { 12977, 12991, 1 }, { 43056, 43061, 1 }, { 65799, 65843, 1 }, { 65909, 65912, 1 }, { 65930, 66336, 406 }, { 66337, 66339, 1 }, { 67672, 67679, 1 }, { 67862, 67867, 1 }, { 68160, 68167, 1 }, { 68221, 68222, 1 }, { 68440, 68447, 1 }, { 68472, 68479, 1 }, { 69216, 69246, 1 }, { 69714, 69733, 1 }, { 119648, 119665, 1 }, { 127232, 127242, 1 } };
    }
    
    private static int[][] make_So() {
        return new int[][] { { 166, 167, 1 }, { 169, 174, 5 }, { 176, 182, 6 }, { 1154, 1550, 396 }, { 1551, 1758, 207 }, { 1769, 1789, 20 }, { 1790, 2038, 248 }, { 2554, 2928, 374 }, { 3059, 3064, 1 }, { 3066, 3199, 133 }, { 3449, 3841, 392 }, { 3842, 3843, 1 }, { 3859, 3863, 1 }, { 3866, 3871, 1 }, { 3892, 3896, 2 }, { 4030, 4037, 1 }, { 4039, 4044, 1 }, { 4046, 4047, 1 }, { 4053, 4056, 1 }, { 4254, 4255, 1 }, { 4960, 5008, 48 }, { 5009, 5017, 1 }, { 6464, 6622, 158 }, { 6623, 6655, 1 }, { 7009, 7018, 1 }, { 7028, 7036, 1 }, { 8448, 8449, 1 }, { 8451, 8454, 1 }, { 8456, 8457, 1 }, { 8468, 8470, 2 }, { 8471, 8478, 7 }, { 8479, 8483, 1 }, { 8485, 8489, 2 }, { 8494, 8506, 12 }, { 8507, 8522, 15 }, { 8524, 8525, 1 }, { 8527, 8597, 70 }, { 8598, 8601, 1 }, { 8604, 8607, 1 }, { 8609, 8610, 1 }, { 8612, 8613, 1 }, { 8615, 8621, 1 }, { 8623, 8653, 1 }, { 8656, 8657, 1 }, { 8659, 8661, 2 }, { 8662, 8691, 1 }, { 8960, 8967, 1 }, { 8972, 8991, 1 }, { 8994, 9000, 1 }, { 9003, 9083, 1 }, { 9085, 9114, 1 }, { 9140, 9179, 1 }, { 9186, 9203, 1 }, { 9216, 9254, 1 }, { 9280, 9290, 1 }, { 9372, 9449, 1 }, { 9472, 9654, 1 }, { 9656, 9664, 1 }, { 9666, 9719, 1 }, { 9728, 9838, 1 }, { 9840, 9983, 1 }, { 9985, 10087, 1 }, { 10132, 10175, 1 }, { 10240, 10495, 1 }, { 11008, 11055, 1 }, { 11077, 11078, 1 }, { 11088, 11097, 1 }, { 11493, 11498, 1 }, { 11904, 11929, 1 }, { 11931, 12019, 1 }, { 12032, 12245, 1 }, { 12272, 12283, 1 }, { 12292, 12306, 14 }, { 12307, 12320, 13 }, { 12342, 12343, 1 }, { 12350, 12351, 1 }, { 12688, 12689, 1 }, { 12694, 12703, 1 }, { 12736, 12771, 1 }, { 12800, 12830, 1 }, { 12842, 12880, 1 }, { 12896, 12927, 1 }, { 12938, 12976, 1 }, { 12992, 13054, 1 }, { 13056, 13311, 1 }, { 19904, 19967, 1 }, { 42128, 42182, 1 }, { 43048, 43051, 1 }, { 43062, 43063, 1 }, { 43065, 43639, 574 }, { 43640, 43641, 1 }, { 65021, 65508, 487 }, { 65512, 65517, 5 }, { 65518, 65532, 14 }, { 65533, 65533, 1 }, { 65794, 65794, 1 }, { 65847, 65855, 1 }, { 65913, 65929, 1 }, { 65936, 65947, 1 }, { 66000, 66044, 1 }, { 118784, 119029, 1 }, { 119040, 119078, 1 }, { 119081, 119140, 1 }, { 119146, 119148, 1 }, { 119171, 119172, 1 }, { 119180, 119209, 1 }, { 119214, 119261, 1 }, { 119296, 119361, 1 }, { 119365, 119552, 187 }, { 119553, 119638, 1 }, { 126976, 127019, 1 }, { 127024, 127123, 1 }, { 127136, 127150, 1 }, { 127153, 127166, 1 }, { 127169, 127183, 1 }, { 127185, 127199, 1 }, { 127248, 127278, 1 }, { 127280, 127337, 1 }, { 127344, 127386, 1 }, { 127462, 127490, 1 }, { 127504, 127546, 1 }, { 127552, 127560, 1 }, { 127568, 127569, 1 }, { 127744, 127776, 1 }, { 127792, 127797, 1 }, { 127799, 127868, 1 }, { 127872, 127891, 1 }, { 127904, 127940, 1 }, { 127942, 127946, 1 }, { 127968, 127984, 1 }, { 128000, 128062, 1 }, { 128064, 128066, 2 }, { 128067, 128247, 1 }, { 128249, 128252, 1 }, { 128256, 128317, 1 }, { 128336, 128359, 1 }, { 128507, 128511, 1 }, { 128513, 128528, 1 }, { 128530, 128532, 1 }, { 128534, 128540, 2 }, { 128541, 128542, 1 }, { 128544, 128549, 1 }, { 128552, 128555, 1 }, { 128557, 128560, 3 }, { 128561, 128563, 1 }, { 128565, 128576, 1 }, { 128581, 128591, 1 }, { 128640, 128709, 1 }, { 128768, 128883, 1 } };
    }
    
    private static int[][] make_Sm() {
        return new int[][] { { 43, 60, 17 }, { 61, 62, 1 }, { 124, 126, 2 }, { 172, 177, 5 }, { 215, 247, 32 }, { 1014, 1542, 528 }, { 1543, 1544, 1 }, { 8260, 8274, 14 }, { 8314, 8316, 1 }, { 8330, 8332, 1 }, { 8472, 8512, 40 }, { 8513, 8516, 1 }, { 8523, 8592, 69 }, { 8593, 8596, 1 }, { 8602, 8603, 1 }, { 8608, 8614, 3 }, { 8622, 8654, 32 }, { 8655, 8658, 3 }, { 8660, 8692, 32 }, { 8693, 8959, 1 }, { 8968, 8971, 1 }, { 8992, 8993, 1 }, { 9084, 9115, 31 }, { 9116, 9139, 1 }, { 9180, 9185, 1 }, { 9655, 9665, 10 }, { 9720, 9727, 1 }, { 9839, 10176, 337 }, { 10177, 10180, 1 }, { 10183, 10186, 1 }, { 10188, 10190, 2 }, { 10191, 10213, 1 }, { 10224, 10239, 1 }, { 10496, 10626, 1 }, { 10649, 10711, 1 }, { 10716, 10747, 1 }, { 10750, 11007, 1 }, { 11056, 11076, 1 }, { 11079, 11084, 1 }, { 64297, 65122, 825 }, { 65124, 65126, 1 }, { 65291, 65308, 17 }, { 65309, 65310, 1 }, { 65372, 65374, 2 }, { 65506, 65513, 7 }, { 65514, 65516, 1 }, { 120513, 120539, 26 }, { 120571, 120597, 26 }, { 120629, 120655, 26 }, { 120687, 120713, 26 }, { 120745, 120771, 26 } };
    }
    
    private static int[][] make_Sk() {
        return new int[][] { { 94, 96, 2 }, { 168, 175, 7 }, { 180, 184, 4 }, { 706, 709, 1 }, { 722, 735, 1 }, { 741, 747, 1 }, { 749, 751, 2 }, { 752, 767, 1 }, { 885, 900, 15 }, { 901, 8125, 7224 }, { 8127, 8129, 1 }, { 8141, 8143, 1 }, { 8157, 8159, 1 }, { 8173, 8175, 1 }, { 8189, 8190, 1 }, { 12443, 12444, 1 }, { 42752, 42774, 1 }, { 42784, 42785, 1 }, { 42889, 42890, 1 }, { 64434, 64449, 1 }, { 65342, 65344, 2 }, { 65507, 65507, 1 } };
    }
    
    private static int[][] make_Sc() {
        return new int[][] { { 36, 162, 126 }, { 163, 165, 1 }, { 1547, 2546, 999 }, { 2547, 2555, 8 }, { 2801, 3065, 264 }, { 3647, 6107, 2460 }, { 8352, 8377, 1 }, { 43064, 65020, 21956 }, { 65129, 65284, 155 }, { 65504, 65505, 1 }, { 65509, 65510, 1 } };
    }
    
    private static int[][] make_Lu() {
        return new int[][] { { 65, 90, 1 }, { 192, 214, 1 }, { 216, 222, 1 }, { 256, 310, 2 }, { 313, 327, 2 }, { 330, 376, 2 }, { 377, 381, 2 }, { 385, 386, 1 }, { 388, 390, 2 }, { 391, 393, 2 }, { 394, 395, 1 }, { 398, 401, 1 }, { 403, 404, 1 }, { 406, 408, 1 }, { 412, 413, 1 }, { 415, 416, 1 }, { 418, 422, 2 }, { 423, 425, 2 }, { 428, 430, 2 }, { 431, 433, 2 }, { 434, 435, 1 }, { 437, 439, 2 }, { 440, 444, 4 }, { 452, 461, 3 }, { 463, 475, 2 }, { 478, 494, 2 }, { 497, 500, 3 }, { 502, 504, 1 }, { 506, 562, 2 }, { 570, 571, 1 }, { 573, 574, 1 }, { 577, 579, 2 }, { 580, 582, 1 }, { 584, 590, 2 }, { 880, 882, 2 }, { 886, 902, 16 }, { 904, 906, 1 }, { 908, 910, 2 }, { 911, 913, 2 }, { 914, 929, 1 }, { 931, 939, 1 }, { 975, 978, 3 }, { 979, 980, 1 }, { 984, 1006, 2 }, { 1012, 1015, 3 }, { 1017, 1018, 1 }, { 1021, 1071, 1 }, { 1120, 1152, 2 }, { 1162, 1216, 2 }, { 1217, 1229, 2 }, { 1232, 1318, 2 }, { 1329, 1366, 1 }, { 4256, 4293, 1 }, { 7680, 7828, 2 }, { 7838, 7934, 2 }, { 7944, 7951, 1 }, { 7960, 7965, 1 }, { 7976, 7983, 1 }, { 7992, 7999, 1 }, { 8008, 8013, 1 }, { 8025, 8031, 2 }, { 8040, 8047, 1 }, { 8120, 8123, 1 }, { 8136, 8139, 1 }, { 8152, 8155, 1 }, { 8168, 8172, 1 }, { 8184, 8187, 1 }, { 8450, 8455, 5 }, { 8459, 8461, 1 }, { 8464, 8466, 1 }, { 8469, 8473, 4 }, { 8474, 8477, 1 }, { 8484, 8490, 2 }, { 8491, 8493, 1 }, { 8496, 8499, 1 }, { 8510, 8511, 1 }, { 8517, 8579, 62 }, { 11264, 11310, 1 }, { 11360, 11362, 2 }, { 11363, 11364, 1 }, { 11367, 11373, 2 }, { 11374, 11376, 1 }, { 11378, 11381, 3 }, { 11390, 11392, 1 }, { 11394, 11490, 2 }, { 11499, 11501, 2 }, { 42560, 42604, 2 }, { 42624, 42646, 2 }, { 42786, 42798, 2 }, { 42802, 42862, 2 }, { 42873, 42877, 2 }, { 42878, 42886, 2 }, { 42891, 42893, 2 }, { 42896, 42912, 16 }, { 42914, 42920, 2 }, { 65313, 65338, 1 }, { 66560, 66599, 1 }, { 119808, 119833, 1 }, { 119860, 119885, 1 }, { 119912, 119937, 1 }, { 119964, 119966, 2 }, { 119967, 119973, 3 }, { 119974, 119977, 3 }, { 119978, 119980, 1 }, { 119982, 119989, 1 }, { 120016, 120041, 1 }, { 120068, 120069, 1 }, { 120071, 120074, 1 }, { 120077, 120084, 1 }, { 120086, 120092, 1 }, { 120120, 120121, 1 }, { 120123, 120126, 1 }, { 120128, 120132, 1 }, { 120134, 120138, 4 }, { 120139, 120144, 1 }, { 120172, 120197, 1 }, { 120224, 120249, 1 }, { 120276, 120301, 1 }, { 120328, 120353, 1 }, { 120380, 120405, 1 }, { 120432, 120457, 1 }, { 120488, 120512, 1 }, { 120546, 120570, 1 }, { 120604, 120628, 1 }, { 120662, 120686, 1 }, { 120720, 120744, 1 }, { 120778, 120778, 1 } };
    }
    
    private static int[][] make_Lt() {
        return new int[][] { { 453, 459, 3 }, { 498, 8072, 7574 }, { 8073, 8079, 1 }, { 8088, 8095, 1 }, { 8104, 8111, 1 }, { 8124, 8140, 16 }, { 8188, 8188, 1 } };
    }
    
    private static int[][] make_Lo() {
        return new int[][] { { 443, 448, 5 }, { 449, 451, 1 }, { 660, 1488, 828 }, { 1489, 1514, 1 }, { 1520, 1522, 1 }, { 1568, 1599, 1 }, { 1601, 1610, 1 }, { 1646, 1647, 1 }, { 1649, 1747, 1 }, { 1749, 1774, 25 }, { 1775, 1786, 11 }, { 1787, 1788, 1 }, { 1791, 1808, 17 }, { 1810, 1839, 1 }, { 1869, 1957, 1 }, { 1969, 1994, 25 }, { 1995, 2026, 1 }, { 2048, 2069, 1 }, { 2112, 2136, 1 }, { 2308, 2361, 1 }, { 2365, 2384, 19 }, { 2392, 2401, 1 }, { 2418, 2423, 1 }, { 2425, 2431, 1 }, { 2437, 2444, 1 }, { 2447, 2448, 1 }, { 2451, 2472, 1 }, { 2474, 2480, 1 }, { 2482, 2486, 4 }, { 2487, 2489, 1 }, { 2493, 2510, 17 }, { 2524, 2525, 1 }, { 2527, 2529, 1 }, { 2544, 2545, 1 }, { 2565, 2570, 1 }, { 2575, 2576, 1 }, { 2579, 2600, 1 }, { 2602, 2608, 1 }, { 2610, 2611, 1 }, { 2613, 2614, 1 }, { 2616, 2617, 1 }, { 2649, 2652, 1 }, { 2654, 2674, 20 }, { 2675, 2676, 1 }, { 2693, 2701, 1 }, { 2703, 2705, 1 }, { 2707, 2728, 1 }, { 2730, 2736, 1 }, { 2738, 2739, 1 }, { 2741, 2745, 1 }, { 2749, 2768, 19 }, { 2784, 2785, 1 }, { 2821, 2828, 1 }, { 2831, 2832, 1 }, { 2835, 2856, 1 }, { 2858, 2864, 1 }, { 2866, 2867, 1 }, { 2869, 2873, 1 }, { 2877, 2908, 31 }, { 2909, 2911, 2 }, { 2912, 2913, 1 }, { 2929, 2947, 18 }, { 2949, 2954, 1 }, { 2958, 2960, 1 }, { 2962, 2965, 1 }, { 2969, 2970, 1 }, { 2972, 2974, 2 }, { 2975, 2979, 4 }, { 2980, 2984, 4 }, { 2985, 2986, 1 }, { 2990, 3001, 1 }, { 3024, 3077, 53 }, { 3078, 3084, 1 }, { 3086, 3088, 1 }, { 3090, 3112, 1 }, { 3114, 3123, 1 }, { 3125, 3129, 1 }, { 3133, 3160, 27 }, { 3161, 3168, 7 }, { 3169, 3205, 36 }, { 3206, 3212, 1 }, { 3214, 3216, 1 }, { 3218, 3240, 1 }, { 3242, 3251, 1 }, { 3253, 3257, 1 }, { 3261, 3294, 33 }, { 3296, 3297, 1 }, { 3313, 3314, 1 }, { 3333, 3340, 1 }, { 3342, 3344, 1 }, { 3346, 3386, 1 }, { 3389, 3406, 17 }, { 3424, 3425, 1 }, { 3450, 3455, 1 }, { 3461, 3478, 1 }, { 3482, 3505, 1 }, { 3507, 3515, 1 }, { 3517, 3520, 3 }, { 3521, 3526, 1 }, { 3585, 3632, 1 }, { 3634, 3635, 1 }, { 3648, 3653, 1 }, { 3713, 3714, 1 }, { 3716, 3719, 3 }, { 3720, 3722, 2 }, { 3725, 3732, 7 }, { 3733, 3735, 1 }, { 3737, 3743, 1 }, { 3745, 3747, 1 }, { 3749, 3751, 2 }, { 3754, 3755, 1 }, { 3757, 3760, 1 }, { 3762, 3763, 1 }, { 3773, 3776, 3 }, { 3777, 3780, 1 }, { 3804, 3805, 1 }, { 3840, 3904, 64 }, { 3905, 3911, 1 }, { 3913, 3948, 1 }, { 3976, 3980, 1 }, { 4096, 4138, 1 }, { 4159, 4176, 17 }, { 4177, 4181, 1 }, { 4186, 4189, 1 }, { 4193, 4197, 4 }, { 4198, 4206, 8 }, { 4207, 4208, 1 }, { 4213, 4225, 1 }, { 4238, 4304, 66 }, { 4305, 4346, 1 }, { 4352, 4680, 1 }, { 4682, 4685, 1 }, { 4688, 4694, 1 }, { 4696, 4698, 2 }, { 4699, 4701, 1 }, { 4704, 4744, 1 }, { 4746, 4749, 1 }, { 4752, 4784, 1 }, { 4786, 4789, 1 }, { 4792, 4798, 1 }, { 4800, 4802, 2 }, { 4803, 4805, 1 }, { 4808, 4822, 1 }, { 4824, 4880, 1 }, { 4882, 4885, 1 }, { 4888, 4954, 1 }, { 4992, 5007, 1 }, { 5024, 5108, 1 }, { 5121, 5740, 1 }, { 5743, 5759, 1 }, { 5761, 5786, 1 }, { 5792, 5866, 1 }, { 5888, 5900, 1 }, { 5902, 5905, 1 }, { 5920, 5937, 1 }, { 5952, 5969, 1 }, { 5984, 5996, 1 }, { 5998, 6000, 1 }, { 6016, 6067, 1 }, { 6108, 6176, 68 }, { 6177, 6210, 1 }, { 6212, 6263, 1 }, { 6272, 6312, 1 }, { 6314, 6320, 6 }, { 6321, 6389, 1 }, { 6400, 6428, 1 }, { 6480, 6509, 1 }, { 6512, 6516, 1 }, { 6528, 6571, 1 }, { 6593, 6599, 1 }, { 6656, 6678, 1 }, { 6688, 6740, 1 }, { 6917, 6963, 1 }, { 6981, 6987, 1 }, { 7043, 7072, 1 }, { 7086, 7087, 1 }, { 7104, 7141, 1 }, { 7168, 7203, 1 }, { 7245, 7247, 1 }, { 7258, 7287, 1 }, { 7401, 7404, 1 }, { 7406, 7409, 1 }, { 8501, 8504, 1 }, { 11568, 11621, 1 }, { 11648, 11670, 1 }, { 11680, 11686, 1 }, { 11688, 11694, 1 }, { 11696, 11702, 1 }, { 11704, 11710, 1 }, { 11712, 11718, 1 }, { 11720, 11726, 1 }, { 11728, 11734, 1 }, { 11736, 11742, 1 }, { 12294, 12348, 54 }, { 12353, 12438, 1 }, { 12447, 12449, 2 }, { 12450, 12538, 1 }, { 12543, 12549, 6 }, { 12550, 12589, 1 }, { 12593, 12686, 1 }, { 12704, 12730, 1 }, { 12784, 12799, 1 }, { 13312, 19893, 1 }, { 19968, 40907, 1 }, { 40960, 40980, 1 }, { 40982, 42124, 1 }, { 42192, 42231, 1 }, { 42240, 42507, 1 }, { 42512, 42527, 1 }, { 42538, 42539, 1 }, { 42606, 42656, 50 }, { 42657, 42725, 1 }, { 43003, 43009, 1 }, { 43011, 43013, 1 }, { 43015, 43018, 1 }, { 43020, 43042, 1 }, { 43072, 43123, 1 }, { 43138, 43187, 1 }, { 43250, 43255, 1 }, { 43259, 43274, 15 }, { 43275, 43301, 1 }, { 43312, 43334, 1 }, { 43360, 43388, 1 }, { 43396, 43442, 1 }, { 43520, 43560, 1 }, { 43584, 43586, 1 }, { 43588, 43595, 1 }, { 43616, 43631, 1 }, { 43633, 43638, 1 }, { 43642, 43648, 6 }, { 43649, 43695, 1 }, { 43697, 43701, 4 }, { 43702, 43705, 3 }, { 43706, 43709, 1 }, { 43712, 43714, 2 }, { 43739, 43740, 1 }, { 43777, 43782, 1 }, { 43785, 43790, 1 }, { 43793, 43798, 1 }, { 43808, 43814, 1 }, { 43816, 43822, 1 }, { 43968, 44002, 1 }, { 44032, 55203, 1 }, { 55216, 55238, 1 }, { 55243, 55291, 1 }, { 63744, 64045, 1 }, { 64048, 64109, 1 }, { 64112, 64217, 1 }, { 64285, 64287, 2 }, { 64288, 64296, 1 }, { 64298, 64310, 1 }, { 64312, 64316, 1 }, { 64318, 64320, 2 }, { 64321, 64323, 2 }, { 64324, 64326, 2 }, { 64327, 64433, 1 }, { 64467, 64829, 1 }, { 64848, 64911, 1 }, { 64914, 64967, 1 }, { 65008, 65019, 1 }, { 65136, 65140, 1 }, { 65142, 65276, 1 }, { 65382, 65391, 1 }, { 65393, 65437, 1 }, { 65440, 65470, 1 }, { 65474, 65479, 1 }, { 65482, 65487, 1 }, { 65490, 65495, 1 }, { 65498, 65500, 1 }, { 65536, 65547, 1 }, { 65549, 65574, 1 }, { 65576, 65594, 1 }, { 65596, 65597, 1 }, { 65599, 65613, 1 }, { 65616, 65629, 1 }, { 65664, 65786, 1 }, { 66176, 66204, 1 }, { 66208, 66256, 1 }, { 66304, 66334, 1 }, { 66352, 66368, 1 }, { 66370, 66377, 1 }, { 66432, 66461, 1 }, { 66464, 66499, 1 }, { 66504, 66511, 1 }, { 66640, 66717, 1 }, { 67584, 67589, 1 }, { 67592, 67594, 2 }, { 67595, 67637, 1 }, { 67639, 67640, 1 }, { 67644, 67647, 3 }, { 67648, 67669, 1 }, { 67840, 67861, 1 }, { 67872, 67897, 1 }, { 68096, 68112, 16 }, { 68113, 68115, 1 }, { 68117, 68119, 1 }, { 68121, 68147, 1 }, { 68192, 68220, 1 }, { 68352, 68405, 1 }, { 68416, 68437, 1 }, { 68448, 68466, 1 }, { 68608, 68680, 1 }, { 69635, 69687, 1 }, { 69763, 69807, 1 }, { 73728, 74606, 1 }, { 77824, 78894, 1 }, { 92160, 92728, 1 }, { 110592, 110593, 1 }, { 131072, 173782, 1 }, { 173824, 177972, 1 }, { 177984, 178205, 1 }, { 194560, 195101, 1 } };
    }
    
    private static Map<String, int[][]> Scripts() {
        final Map<String, int[][]> map = new HashMap<String, int[][]>();
        map.put("Katakana", UnicodeTables.Katakana);
        map.put("Malayalam", UnicodeTables.Malayalam);
        map.put("Phags_Pa", UnicodeTables.Phags_Pa);
        map.put("Inscriptional_Parthian", UnicodeTables.Inscriptional_Parthian);
        map.put("Latin", UnicodeTables.Latin);
        map.put("Inscriptional_Pahlavi", UnicodeTables.Inscriptional_Pahlavi);
        map.put("Osmanya", UnicodeTables.Osmanya);
        map.put("Khmer", UnicodeTables.Khmer);
        map.put("Inherited", UnicodeTables.Inherited);
        map.put("Telugu", UnicodeTables.Telugu);
        map.put("Samaritan", UnicodeTables.Samaritan);
        map.put("Bopomofo", UnicodeTables.Bopomofo);
        map.put("Imperial_Aramaic", UnicodeTables.Imperial_Aramaic);
        map.put("Kaithi", UnicodeTables.Kaithi);
        map.put("Mandaic", UnicodeTables.Mandaic);
        map.put("Old_South_Arabian", UnicodeTables.Old_South_Arabian);
        map.put("Kayah_Li", UnicodeTables.Kayah_Li);
        map.put("New_Tai_Lue", UnicodeTables.New_Tai_Lue);
        map.put("Tai_Le", UnicodeTables.Tai_Le);
        map.put("Kharoshthi", UnicodeTables.Kharoshthi);
        map.put("Common", UnicodeTables.Common);
        map.put("Kannada", UnicodeTables.Kannada);
        map.put("Old_Turkic", UnicodeTables.Old_Turkic);
        map.put("Tamil", UnicodeTables.Tamil);
        map.put("Tagalog", UnicodeTables.Tagalog);
        map.put("Brahmi", UnicodeTables.Brahmi);
        map.put("Arabic", UnicodeTables.Arabic);
        map.put("Tagbanwa", UnicodeTables.Tagbanwa);
        map.put("Canadian_Aboriginal", UnicodeTables.Canadian_Aboriginal);
        map.put("Tibetan", UnicodeTables.Tibetan);
        map.put("Coptic", UnicodeTables.Coptic);
        map.put("Hiragana", UnicodeTables.Hiragana);
        map.put("Limbu", UnicodeTables.Limbu);
        map.put("Egyptian_Hieroglyphs", UnicodeTables.Egyptian_Hieroglyphs);
        map.put("Avestan", UnicodeTables.Avestan);
        map.put("Myanmar", UnicodeTables.Myanmar);
        map.put("Armenian", UnicodeTables.Armenian);
        map.put("Sinhala", UnicodeTables.Sinhala);
        map.put("Bengali", UnicodeTables.Bengali);
        map.put("Greek", UnicodeTables.Greek);
        map.put("Cham", UnicodeTables.Cham);
        map.put("Hebrew", UnicodeTables.Hebrew);
        map.put("Meetei_Mayek", UnicodeTables.Meetei_Mayek);
        map.put("Saurashtra", UnicodeTables.Saurashtra);
        map.put("Hangul", UnicodeTables.Hangul);
        map.put("Runic", UnicodeTables.Runic);
        map.put("Deseret", UnicodeTables.Deseret);
        map.put("Lisu", UnicodeTables.Lisu);
        map.put("Sundanese", UnicodeTables.Sundanese);
        map.put("Glagolitic", UnicodeTables.Glagolitic);
        map.put("Oriya", UnicodeTables.Oriya);
        map.put("Buhid", UnicodeTables.Buhid);
        map.put("Ethiopic", UnicodeTables.Ethiopic);
        map.put("Javanese", UnicodeTables.Javanese);
        map.put("Syloti_Nagri", UnicodeTables.Syloti_Nagri);
        map.put("Vai", UnicodeTables.Vai);
        map.put("Cherokee", UnicodeTables.Cherokee);
        map.put("Ogham", UnicodeTables.Ogham);
        map.put("Batak", UnicodeTables.Batak);
        map.put("Syriac", UnicodeTables.Syriac);
        map.put("Gurmukhi", UnicodeTables.Gurmukhi);
        map.put("Tai_Tham", UnicodeTables.Tai_Tham);
        map.put("Ol_Chiki", UnicodeTables.Ol_Chiki);
        map.put("Mongolian", UnicodeTables.Mongolian);
        map.put("Hanunoo", UnicodeTables.Hanunoo);
        map.put("Cypriot", UnicodeTables.Cypriot);
        map.put("Buginese", UnicodeTables.Buginese);
        map.put("Bamum", UnicodeTables.Bamum);
        map.put("Lepcha", UnicodeTables.Lepcha);
        map.put("Thaana", UnicodeTables.Thaana);
        map.put("Old_Persian", UnicodeTables.Old_Persian);
        map.put("Cuneiform", UnicodeTables.Cuneiform);
        map.put("Rejang", UnicodeTables.Rejang);
        map.put("Georgian", UnicodeTables.Georgian);
        map.put("Shavian", UnicodeTables.Shavian);
        map.put("Lycian", UnicodeTables.Lycian);
        map.put("Nko", UnicodeTables.Nko);
        map.put("Yi", UnicodeTables.Yi);
        map.put("Lao", UnicodeTables.Lao);
        map.put("Linear_B", UnicodeTables.Linear_B);
        map.put("Old_Italic", UnicodeTables.Old_Italic);
        map.put("Tai_Viet", UnicodeTables.Tai_Viet);
        map.put("Devanagari", UnicodeTables.Devanagari);
        map.put("Lydian", UnicodeTables.Lydian);
        map.put("Tifinagh", UnicodeTables.Tifinagh);
        map.put("Ugaritic", UnicodeTables.Ugaritic);
        map.put("Thai", UnicodeTables.Thai);
        map.put("Cyrillic", UnicodeTables.Cyrillic);
        map.put("Gujarati", UnicodeTables.Gujarati);
        map.put("Carian", UnicodeTables.Carian);
        map.put("Phoenician", UnicodeTables.Phoenician);
        map.put("Balinese", UnicodeTables.Balinese);
        map.put("Braille", UnicodeTables.Braille);
        map.put("Han", UnicodeTables.Han);
        map.put("Gothic", UnicodeTables.Gothic);
        return map;
    }
    
    private static int[][] make_Katakana() {
        return new int[][] { { 12449, 12538, 1 }, { 12541, 12543, 1 }, { 12784, 12799, 1 }, { 13008, 13054, 1 }, { 13056, 13143, 1 }, { 65382, 65391, 1 }, { 65393, 65437, 1 }, { 110592, 110592, 1 } };
    }
    
    private static int[][] make_Malayalam() {
        return new int[][] { { 3330, 3331, 1 }, { 3333, 3340, 1 }, { 3342, 3344, 1 }, { 3346, 3386, 1 }, { 3389, 3396, 1 }, { 3398, 3400, 1 }, { 3402, 3406, 1 }, { 3415, 3415, 1 }, { 3424, 3427, 1 }, { 3430, 3445, 1 }, { 3449, 3455, 1 } };
    }
    
    private static int[][] make_Phags_Pa() {
        return new int[][] { { 43072, 43127, 1 } };
    }
    
    private static int[][] make_Inscriptional_Parthian() {
        return new int[][] { { 68416, 68437, 1 }, { 68440, 68447, 1 } };
    }
    
    private static int[][] make_Latin() {
        return new int[][] { { 65, 90, 1 }, { 97, 122, 1 }, { 170, 170, 1 }, { 186, 186, 1 }, { 192, 214, 1 }, { 216, 246, 1 }, { 248, 696, 1 }, { 736, 740, 1 }, { 7424, 7461, 1 }, { 7468, 7516, 1 }, { 7522, 7525, 1 }, { 7531, 7543, 1 }, { 7545, 7614, 1 }, { 7680, 7935, 1 }, { 8305, 8305, 1 }, { 8319, 8319, 1 }, { 8336, 8348, 1 }, { 8490, 8491, 1 }, { 8498, 8498, 1 }, { 8526, 8526, 1 }, { 8544, 8584, 1 }, { 11360, 11391, 1 }, { 42786, 42887, 1 }, { 42891, 42894, 1 }, { 42896, 42897, 1 }, { 42912, 42921, 1 }, { 43002, 43007, 1 }, { 64256, 64262, 1 }, { 65313, 65338, 1 }, { 65345, 65370, 1 } };
    }
    
    private static int[][] make_Inscriptional_Pahlavi() {
        return new int[][] { { 68448, 68466, 1 }, { 68472, 68479, 1 } };
    }
    
    private static int[][] make_Osmanya() {
        return new int[][] { { 66688, 66717, 1 }, { 66720, 66729, 1 } };
    }
    
    private static int[][] make_Khmer() {
        return new int[][] { { 6016, 6109, 1 }, { 6112, 6121, 1 }, { 6128, 6137, 1 }, { 6624, 6655, 1 } };
    }
    
    private static int[][] make_Inherited() {
        return new int[][] { { 768, 879, 1 }, { 1157, 1158, 1 }, { 1611, 1621, 1 }, { 1631, 1631, 1 }, { 1648, 1648, 1 }, { 2385, 2386, 1 }, { 7376, 7378, 1 }, { 7380, 7392, 1 }, { 7394, 7400, 1 }, { 7405, 7405, 1 }, { 7616, 7654, 1 }, { 7676, 7679, 1 }, { 8204, 8205, 1 }, { 8400, 8432, 1 }, { 12330, 12333, 1 }, { 12441, 12442, 1 }, { 65024, 65039, 1 }, { 65056, 65062, 1 }, { 66045, 66045, 1 }, { 119143, 119145, 1 }, { 119163, 119170, 1 }, { 119173, 119179, 1 }, { 119210, 119213, 1 }, { 917760, 917999, 1 } };
    }
    
    private static int[][] make_Telugu() {
        return new int[][] { { 3073, 3075, 1 }, { 3077, 3084, 1 }, { 3086, 3088, 1 }, { 3090, 3112, 1 }, { 3114, 3123, 1 }, { 3125, 3129, 1 }, { 3133, 3140, 1 }, { 3142, 3144, 1 }, { 3146, 3149, 1 }, { 3157, 3158, 1 }, { 3160, 3161, 1 }, { 3168, 3171, 1 }, { 3174, 3183, 1 }, { 3192, 3199, 1 } };
    }
    
    private static int[][] make_Samaritan() {
        return new int[][] { { 2048, 2093, 1 }, { 2096, 2110, 1 } };
    }
    
    private static int[][] make_Bopomofo() {
        return new int[][] { { 746, 747, 1 }, { 12549, 12589, 1 }, { 12704, 12730, 1 } };
    }
    
    private static int[][] make_Imperial_Aramaic() {
        return new int[][] { { 67648, 67669, 1 }, { 67671, 67679, 1 } };
    }
    
    private static int[][] make_Kaithi() {
        return new int[][] { { 69760, 69825, 1 } };
    }
    
    private static int[][] make_Mandaic() {
        return new int[][] { { 2112, 2139, 1 }, { 2142, 2142, 1 } };
    }
    
    private static int[][] make_Old_South_Arabian() {
        return new int[][] { { 68192, 68223, 1 } };
    }
    
    private static int[][] make_Kayah_Li() {
        return new int[][] { { 43264, 43311, 1 } };
    }
    
    private static int[][] make_New_Tai_Lue() {
        return new int[][] { { 6528, 6571, 1 }, { 6576, 6601, 1 }, { 6608, 6618, 1 }, { 6622, 6623, 1 } };
    }
    
    private static int[][] make_Tai_Le() {
        return new int[][] { { 6480, 6509, 1 }, { 6512, 6516, 1 } };
    }
    
    private static int[][] make_Kharoshthi() {
        return new int[][] { { 68096, 68099, 1 }, { 68101, 68102, 1 }, { 68108, 68115, 1 }, { 68117, 68119, 1 }, { 68121, 68147, 1 }, { 68152, 68154, 1 }, { 68159, 68167, 1 }, { 68176, 68184, 1 } };
    }
    
    private static int[][] make_Common() {
        return new int[][] { { 0, 64, 1 }, { 91, 96, 1 }, { 123, 169, 1 }, { 171, 185, 1 }, { 187, 191, 1 }, { 215, 215, 1 }, { 247, 247, 1 }, { 697, 735, 1 }, { 741, 745, 1 }, { 748, 767, 1 }, { 884, 884, 1 }, { 894, 894, 1 }, { 901, 901, 1 }, { 903, 903, 1 }, { 1417, 1417, 1 }, { 1548, 1548, 1 }, { 1563, 1563, 1 }, { 1567, 1567, 1 }, { 1600, 1600, 1 }, { 1632, 1641, 1 }, { 1757, 1757, 1 }, { 2404, 2405, 1 }, { 2416, 2416, 1 }, { 3647, 3647, 1 }, { 4053, 4056, 1 }, { 4347, 4347, 1 }, { 5867, 5869, 1 }, { 5941, 5942, 1 }, { 6146, 6147, 1 }, { 6149, 6149, 1 }, { 7379, 7379, 1 }, { 7393, 7393, 1 }, { 7401, 7404, 1 }, { 7406, 7410, 1 }, { 8192, 8203, 1 }, { 8206, 8292, 1 }, { 8298, 8304, 1 }, { 8308, 8318, 1 }, { 8320, 8334, 1 }, { 8352, 8377, 1 }, { 8448, 8485, 1 }, { 8487, 8489, 1 }, { 8492, 8497, 1 }, { 8499, 8525, 1 }, { 8527, 8543, 1 }, { 8585, 8585, 1 }, { 8592, 9203, 1 }, { 9216, 9254, 1 }, { 9280, 9290, 1 }, { 9312, 9983, 1 }, { 9985, 10186, 1 }, { 10188, 10188, 1 }, { 10190, 10239, 1 }, { 10496, 11084, 1 }, { 11088, 11097, 1 }, { 11776, 11825, 1 }, { 12272, 12283, 1 }, { 12288, 12292, 1 }, { 12294, 12294, 1 }, { 12296, 12320, 1 }, { 12336, 12343, 1 }, { 12348, 12351, 1 }, { 12443, 12444, 1 }, { 12448, 12448, 1 }, { 12539, 12540, 1 }, { 12688, 12703, 1 }, { 12736, 12771, 1 }, { 12832, 12895, 1 }, { 12927, 13007, 1 }, { 13144, 13311, 1 }, { 19904, 19967, 1 }, { 42752, 42785, 1 }, { 42888, 42890, 1 }, { 43056, 43065, 1 }, { 64830, 64831, 1 }, { 65021, 65021, 1 }, { 65040, 65049, 1 }, { 65072, 65106, 1 }, { 65108, 65126, 1 }, { 65128, 65131, 1 }, { 65279, 65279, 1 }, { 65281, 65312, 1 }, { 65339, 65344, 1 }, { 65371, 65381, 1 }, { 65392, 65392, 1 }, { 65438, 65439, 1 }, { 65504, 65510, 1 }, { 65512, 65518, 1 }, { 65529, 65533, 1 }, { 65792, 65794, 1 }, { 65799, 65843, 1 }, { 65847, 65855, 1 }, { 65936, 65947, 1 }, { 66000, 66044, 1 }, { 118784, 119029, 1 }, { 119040, 119078, 1 }, { 119081, 119142, 1 }, { 119146, 119162, 1 }, { 119171, 119172, 1 }, { 119180, 119209, 1 }, { 119214, 119261, 1 }, { 119552, 119638, 1 }, { 119648, 119665, 1 }, { 119808, 119892, 1 }, { 119894, 119964, 1 }, { 119966, 119967, 1 }, { 119970, 119970, 1 }, { 119973, 119974, 1 }, { 119977, 119980, 1 }, { 119982, 119993, 1 }, { 119995, 119995, 1 }, { 119997, 120003, 1 }, { 120005, 120069, 1 }, { 120071, 120074, 1 }, { 120077, 120084, 1 }, { 120086, 120092, 1 }, { 120094, 120121, 1 }, { 120123, 120126, 1 }, { 120128, 120132, 1 }, { 120134, 120134, 1 }, { 120138, 120144, 1 }, { 120146, 120485, 1 }, { 120488, 120779, 1 }, { 120782, 120831, 1 }, { 126976, 127019, 1 }, { 127024, 127123, 1 }, { 127136, 127150, 1 }, { 127153, 127166, 1 }, { 127169, 127183, 1 }, { 127185, 127199, 1 }, { 127232, 127242, 1 }, { 127248, 127278, 1 }, { 127280, 127337, 1 }, { 127344, 127386, 1 }, { 127462, 127487, 1 }, { 127489, 127490, 1 }, { 127504, 127546, 1 }, { 127552, 127560, 1 }, { 127568, 127569, 1 }, { 127744, 127776, 1 }, { 127792, 127797, 1 }, { 127799, 127868, 1 }, { 127872, 127891, 1 }, { 127904, 127940, 1 }, { 127942, 127946, 1 }, { 127968, 127984, 1 }, { 128000, 128062, 1 }, { 128064, 128064, 1 }, { 128066, 128247, 1 }, { 128249, 128252, 1 }, { 128256, 128317, 1 }, { 128336, 128359, 1 }, { 128507, 128511, 1 }, { 128513, 128528, 1 }, { 128530, 128532, 1 }, { 128534, 128534, 1 }, { 128536, 128536, 1 }, { 128538, 128538, 1 }, { 128540, 128542, 1 }, { 128544, 128549, 1 }, { 128552, 128555, 1 }, { 128557, 128557, 1 }, { 128560, 128563, 1 }, { 128565, 128576, 1 }, { 128581, 128591, 1 }, { 128640, 128709, 1 }, { 128768, 128883, 1 }, { 917505, 917505, 1 }, { 917536, 917631, 1 } };
    }
    
    private static int[][] make_Kannada() {
        return new int[][] { { 3202, 3203, 1 }, { 3205, 3212, 1 }, { 3214, 3216, 1 }, { 3218, 3240, 1 }, { 3242, 3251, 1 }, { 3253, 3257, 1 }, { 3260, 3268, 1 }, { 3270, 3272, 1 }, { 3274, 3277, 1 }, { 3285, 3286, 1 }, { 3294, 3294, 1 }, { 3296, 3299, 1 }, { 3302, 3311, 1 }, { 3313, 3314, 1 } };
    }
    
    private static int[][] make_Old_Turkic() {
        return new int[][] { { 68608, 68680, 1 } };
    }
    
    private static int[][] make_Tamil() {
        return new int[][] { { 2946, 2947, 1 }, { 2949, 2954, 1 }, { 2958, 2960, 1 }, { 2962, 2965, 1 }, { 2969, 2970, 1 }, { 2972, 2972, 1 }, { 2974, 2975, 1 }, { 2979, 2980, 1 }, { 2984, 2986, 1 }, { 2990, 3001, 1 }, { 3006, 3010, 1 }, { 3014, 3016, 1 }, { 3018, 3021, 1 }, { 3024, 3024, 1 }, { 3031, 3031, 1 }, { 3046, 3066, 1 } };
    }
    
    private static int[][] make_Tagalog() {
        return new int[][] { { 5888, 5900, 1 }, { 5902, 5908, 1 } };
    }
    
    private static int[][] make_Brahmi() {
        return new int[][] { { 69632, 69709, 1 }, { 69714, 69743, 1 } };
    }
    
    private static int[][] make_Arabic() {
        return new int[][] { { 1536, 1539, 1 }, { 1542, 1547, 1 }, { 1549, 1562, 1 }, { 1566, 1566, 1 }, { 1568, 1599, 1 }, { 1601, 1610, 1 }, { 1622, 1630, 1 }, { 1642, 1647, 1 }, { 1649, 1756, 1 }, { 1758, 1791, 1 }, { 1872, 1919, 1 }, { 64336, 64449, 1 }, { 64467, 64829, 1 }, { 64848, 64911, 1 }, { 64914, 64967, 1 }, { 65008, 65020, 1 }, { 65136, 65140, 1 }, { 65142, 65276, 1 }, { 69216, 69246, 1 } };
    }
    
    private static int[][] make_Tagbanwa() {
        return new int[][] { { 5984, 5996, 1 }, { 5998, 6000, 1 }, { 6002, 6003, 1 } };
    }
    
    private static int[][] make_Canadian_Aboriginal() {
        return new int[][] { { 5120, 5759, 1 }, { 6320, 6389, 1 } };
    }
    
    private static int[][] make_Tibetan() {
        return new int[][] { { 3840, 3911, 1 }, { 3913, 3948, 1 }, { 3953, 3991, 1 }, { 3993, 4028, 1 }, { 4030, 4044, 1 }, { 4046, 4052, 1 }, { 4057, 4058, 1 } };
    }
    
    private static int[][] make_Coptic() {
        return new int[][] { { 994, 1007, 1 }, { 11392, 11505, 1 }, { 11513, 11519, 1 } };
    }
    
    private static int[][] make_Hiragana() {
        return new int[][] { { 12353, 12438, 1 }, { 12445, 12447, 1 }, { 110593, 110593, 1 }, { 127488, 127488, 1 } };
    }
    
    private static int[][] make_Limbu() {
        return new int[][] { { 6400, 6428, 1 }, { 6432, 6443, 1 }, { 6448, 6459, 1 }, { 6464, 6464, 1 }, { 6468, 6479, 1 } };
    }
    
    private static int[][] make_Egyptian_Hieroglyphs() {
        return new int[][] { { 77824, 78894, 1 } };
    }
    
    private static int[][] make_Avestan() {
        return new int[][] { { 68352, 68405, 1 }, { 68409, 68415, 1 } };
    }
    
    private static int[][] make_Myanmar() {
        return new int[][] { { 4096, 4255, 1 }, { 43616, 43643, 1 } };
    }
    
    private static int[][] make_Armenian() {
        return new int[][] { { 1329, 1366, 1 }, { 1369, 1375, 1 }, { 1377, 1415, 1 }, { 1418, 1418, 1 }, { 64275, 64279, 1 } };
    }
    
    private static int[][] make_Sinhala() {
        return new int[][] { { 3458, 3459, 1 }, { 3461, 3478, 1 }, { 3482, 3505, 1 }, { 3507, 3515, 1 }, { 3517, 3517, 1 }, { 3520, 3526, 1 }, { 3530, 3530, 1 }, { 3535, 3540, 1 }, { 3542, 3542, 1 }, { 3544, 3551, 1 }, { 3570, 3572, 1 } };
    }
    
    private static int[][] make_Bengali() {
        return new int[][] { { 2433, 2435, 1 }, { 2437, 2444, 1 }, { 2447, 2448, 1 }, { 2451, 2472, 1 }, { 2474, 2480, 1 }, { 2482, 2482, 1 }, { 2486, 2489, 1 }, { 2492, 2500, 1 }, { 2503, 2504, 1 }, { 2507, 2510, 1 }, { 2519, 2519, 1 }, { 2524, 2525, 1 }, { 2527, 2531, 1 }, { 2534, 2555, 1 } };
    }
    
    private static int[][] make_Greek() {
        return new int[][] { { 880, 883, 1 }, { 885, 887, 1 }, { 890, 893, 1 }, { 900, 900, 1 }, { 902, 902, 1 }, { 904, 906, 1 }, { 908, 908, 1 }, { 910, 929, 1 }, { 931, 993, 1 }, { 1008, 1023, 1 }, { 7462, 7466, 1 }, { 7517, 7521, 1 }, { 7526, 7530, 1 }, { 7615, 7615, 1 }, { 7936, 7957, 1 }, { 7960, 7965, 1 }, { 7968, 8005, 1 }, { 8008, 8013, 1 }, { 8016, 8023, 1 }, { 8025, 8025, 1 }, { 8027, 8027, 1 }, { 8029, 8029, 1 }, { 8031, 8061, 1 }, { 8064, 8116, 1 }, { 8118, 8132, 1 }, { 8134, 8147, 1 }, { 8150, 8155, 1 }, { 8157, 8175, 1 }, { 8178, 8180, 1 }, { 8182, 8190, 1 }, { 8486, 8486, 1 }, { 65856, 65930, 1 }, { 119296, 119365, 1 } };
    }
    
    private static int[][] make_Cham() {
        return new int[][] { { 43520, 43574, 1 }, { 43584, 43597, 1 }, { 43600, 43609, 1 }, { 43612, 43615, 1 } };
    }
    
    private static int[][] make_Hebrew() {
        return new int[][] { { 1425, 1479, 1 }, { 1488, 1514, 1 }, { 1520, 1524, 1 }, { 64285, 64310, 1 }, { 64312, 64316, 1 }, { 64318, 64318, 1 }, { 64320, 64321, 1 }, { 64323, 64324, 1 }, { 64326, 64335, 1 } };
    }
    
    private static int[][] make_Meetei_Mayek() {
        return new int[][] { { 43968, 44013, 1 }, { 44016, 44025, 1 } };
    }
    
    private static int[][] make_Saurashtra() {
        return new int[][] { { 43136, 43204, 1 }, { 43214, 43225, 1 } };
    }
    
    private static int[][] make_Hangul() {
        return new int[][] { { 4352, 4607, 1 }, { 12334, 12335, 1 }, { 12593, 12686, 1 }, { 12800, 12830, 1 }, { 12896, 12926, 1 }, { 43360, 43388, 1 }, { 44032, 55203, 1 }, { 55216, 55238, 1 }, { 55243, 55291, 1 }, { 65440, 65470, 1 }, { 65474, 65479, 1 }, { 65482, 65487, 1 }, { 65490, 65495, 1 }, { 65498, 65500, 1 } };
    }
    
    private static int[][] make_Runic() {
        return new int[][] { { 5792, 5866, 1 }, { 5870, 5872, 1 } };
    }
    
    private static int[][] make_Deseret() {
        return new int[][] { { 66560, 66639, 1 } };
    }
    
    private static int[][] make_Lisu() {
        return new int[][] { { 42192, 42239, 1 } };
    }
    
    private static int[][] make_Sundanese() {
        return new int[][] { { 7040, 7082, 1 }, { 7086, 7097, 1 } };
    }
    
    private static int[][] make_Glagolitic() {
        return new int[][] { { 11264, 11310, 1 }, { 11312, 11358, 1 } };
    }
    
    private static int[][] make_Oriya() {
        return new int[][] { { 2817, 2819, 1 }, { 2821, 2828, 1 }, { 2831, 2832, 1 }, { 2835, 2856, 1 }, { 2858, 2864, 1 }, { 2866, 2867, 1 }, { 2869, 2873, 1 }, { 2876, 2884, 1 }, { 2887, 2888, 1 }, { 2891, 2893, 1 }, { 2902, 2903, 1 }, { 2908, 2909, 1 }, { 2911, 2915, 1 }, { 2918, 2935, 1 } };
    }
    
    private static int[][] make_Buhid() {
        return new int[][] { { 5952, 5971, 1 } };
    }
    
    private static int[][] make_Ethiopic() {
        return new int[][] { { 4608, 4680, 1 }, { 4682, 4685, 1 }, { 4688, 4694, 1 }, { 4696, 4696, 1 }, { 4698, 4701, 1 }, { 4704, 4744, 1 }, { 4746, 4749, 1 }, { 4752, 4784, 1 }, { 4786, 4789, 1 }, { 4792, 4798, 1 }, { 4800, 4800, 1 }, { 4802, 4805, 1 }, { 4808, 4822, 1 }, { 4824, 4880, 1 }, { 4882, 4885, 1 }, { 4888, 4954, 1 }, { 4957, 4988, 1 }, { 4992, 5017, 1 }, { 11648, 11670, 1 }, { 11680, 11686, 1 }, { 11688, 11694, 1 }, { 11696, 11702, 1 }, { 11704, 11710, 1 }, { 11712, 11718, 1 }, { 11720, 11726, 1 }, { 11728, 11734, 1 }, { 11736, 11742, 1 }, { 43777, 43782, 1 }, { 43785, 43790, 1 }, { 43793, 43798, 1 }, { 43808, 43814, 1 }, { 43816, 43822, 1 } };
    }
    
    private static int[][] make_Javanese() {
        return new int[][] { { 43392, 43469, 1 }, { 43471, 43481, 1 }, { 43486, 43487, 1 } };
    }
    
    private static int[][] make_Syloti_Nagri() {
        return new int[][] { { 43008, 43051, 1 } };
    }
    
    private static int[][] make_Vai() {
        return new int[][] { { 42240, 42539, 1 } };
    }
    
    private static int[][] make_Cherokee() {
        return new int[][] { { 5024, 5108, 1 } };
    }
    
    private static int[][] make_Ogham() {
        return new int[][] { { 5760, 5788, 1 } };
    }
    
    private static int[][] make_Batak() {
        return new int[][] { { 7104, 7155, 1 }, { 7164, 7167, 1 } };
    }
    
    private static int[][] make_Syriac() {
        return new int[][] { { 1792, 1805, 1 }, { 1807, 1866, 1 }, { 1869, 1871, 1 } };
    }
    
    private static int[][] make_Gurmukhi() {
        return new int[][] { { 2561, 2563, 1 }, { 2565, 2570, 1 }, { 2575, 2576, 1 }, { 2579, 2600, 1 }, { 2602, 2608, 1 }, { 2610, 2611, 1 }, { 2613, 2614, 1 }, { 2616, 2617, 1 }, { 2620, 2620, 1 }, { 2622, 2626, 1 }, { 2631, 2632, 1 }, { 2635, 2637, 1 }, { 2641, 2641, 1 }, { 2649, 2652, 1 }, { 2654, 2654, 1 }, { 2662, 2677, 1 } };
    }
    
    private static int[][] make_Tai_Tham() {
        return new int[][] { { 6688, 6750, 1 }, { 6752, 6780, 1 }, { 6783, 6793, 1 }, { 6800, 6809, 1 }, { 6816, 6829, 1 } };
    }
    
    private static int[][] make_Ol_Chiki() {
        return new int[][] { { 7248, 7295, 1 } };
    }
    
    private static int[][] make_Mongolian() {
        return new int[][] { { 6144, 6145, 1 }, { 6148, 6148, 1 }, { 6150, 6158, 1 }, { 6160, 6169, 1 }, { 6176, 6263, 1 }, { 6272, 6314, 1 } };
    }
    
    private static int[][] make_Hanunoo() {
        return new int[][] { { 5920, 5940, 1 } };
    }
    
    private static int[][] make_Cypriot() {
        return new int[][] { { 67584, 67589, 1 }, { 67592, 67592, 1 }, { 67594, 67637, 1 }, { 67639, 67640, 1 }, { 67644, 67644, 1 }, { 67647, 67647, 1 } };
    }
    
    private static int[][] make_Buginese() {
        return new int[][] { { 6656, 6683, 1 }, { 6686, 6687, 1 } };
    }
    
    private static int[][] make_Bamum() {
        return new int[][] { { 42656, 42743, 1 }, { 92160, 92728, 1 } };
    }
    
    private static int[][] make_Lepcha() {
        return new int[][] { { 7168, 7223, 1 }, { 7227, 7241, 1 }, { 7245, 7247, 1 } };
    }
    
    private static int[][] make_Thaana() {
        return new int[][] { { 1920, 1969, 1 } };
    }
    
    private static int[][] make_Old_Persian() {
        return new int[][] { { 66464, 66499, 1 }, { 66504, 66517, 1 } };
    }
    
    private static int[][] make_Cuneiform() {
        return new int[][] { { 73728, 74606, 1 }, { 74752, 74850, 1 }, { 74864, 74867, 1 } };
    }
    
    private static int[][] make_Rejang() {
        return new int[][] { { 43312, 43347, 1 }, { 43359, 43359, 1 } };
    }
    
    private static int[][] make_Georgian() {
        return new int[][] { { 4256, 4293, 1 }, { 4304, 4346, 1 }, { 4348, 4348, 1 }, { 11520, 11557, 1 } };
    }
    
    private static int[][] make_Shavian() {
        return new int[][] { { 66640, 66687, 1 } };
    }
    
    private static int[][] make_Lycian() {
        return new int[][] { { 66176, 66204, 1 } };
    }
    
    private static int[][] make_Nko() {
        return new int[][] { { 1984, 2042, 1 } };
    }
    
    private static int[][] make_Yi() {
        return new int[][] { { 40960, 42124, 1 }, { 42128, 42182, 1 } };
    }
    
    private static int[][] make_Lao() {
        return new int[][] { { 3713, 3714, 1 }, { 3716, 3716, 1 }, { 3719, 3720, 1 }, { 3722, 3722, 1 }, { 3725, 3725, 1 }, { 3732, 3735, 1 }, { 3737, 3743, 1 }, { 3745, 3747, 1 }, { 3749, 3749, 1 }, { 3751, 3751, 1 }, { 3754, 3755, 1 }, { 3757, 3769, 1 }, { 3771, 3773, 1 }, { 3776, 3780, 1 }, { 3782, 3782, 1 }, { 3784, 3789, 1 }, { 3792, 3801, 1 }, { 3804, 3805, 1 } };
    }
    
    private static int[][] make_Linear_B() {
        return new int[][] { { 65536, 65547, 1 }, { 65549, 65574, 1 }, { 65576, 65594, 1 }, { 65596, 65597, 1 }, { 65599, 65613, 1 }, { 65616, 65629, 1 }, { 65664, 65786, 1 } };
    }
    
    private static int[][] make_Old_Italic() {
        return new int[][] { { 66304, 66334, 1 }, { 66336, 66339, 1 } };
    }
    
    private static int[][] make_Tai_Viet() {
        return new int[][] { { 43648, 43714, 1 }, { 43739, 43743, 1 } };
    }
    
    private static int[][] make_Devanagari() {
        return new int[][] { { 2304, 2384, 1 }, { 2387, 2403, 1 }, { 2406, 2415, 1 }, { 2417, 2423, 1 }, { 2425, 2431, 1 }, { 43232, 43259, 1 } };
    }
    
    private static int[][] make_Lydian() {
        return new int[][] { { 67872, 67897, 1 }, { 67903, 67903, 1 } };
    }
    
    private static int[][] make_Tifinagh() {
        return new int[][] { { 11568, 11621, 1 }, { 11631, 11632, 1 }, { 11647, 11647, 1 } };
    }
    
    private static int[][] make_Ugaritic() {
        return new int[][] { { 66432, 66461, 1 }, { 66463, 66463, 1 } };
    }
    
    private static int[][] make_Thai() {
        return new int[][] { { 3585, 3642, 1 }, { 3648, 3675, 1 } };
    }
    
    private static int[][] make_Cyrillic() {
        return new int[][] { { 1024, 1156, 1 }, { 1159, 1319, 1 }, { 7467, 7467, 1 }, { 7544, 7544, 1 }, { 11744, 11775, 1 }, { 42560, 42611, 1 }, { 42620, 42647, 1 } };
    }
    
    private static int[][] make_Gujarati() {
        return new int[][] { { 2689, 2691, 1 }, { 2693, 2701, 1 }, { 2703, 2705, 1 }, { 2707, 2728, 1 }, { 2730, 2736, 1 }, { 2738, 2739, 1 }, { 2741, 2745, 1 }, { 2748, 2757, 1 }, { 2759, 2761, 1 }, { 2763, 2765, 1 }, { 2768, 2768, 1 }, { 2784, 2787, 1 }, { 2790, 2799, 1 }, { 2801, 2801, 1 } };
    }
    
    private static int[][] make_Carian() {
        return new int[][] { { 66208, 66256, 1 } };
    }
    
    private static int[][] make_Phoenician() {
        return new int[][] { { 67840, 67867, 1 }, { 67871, 67871, 1 } };
    }
    
    private static int[][] make_Balinese() {
        return new int[][] { { 6912, 6987, 1 }, { 6992, 7036, 1 } };
    }
    
    private static int[][] make_Braille() {
        return new int[][] { { 10240, 10495, 1 } };
    }
    
    private static int[][] make_Han() {
        return new int[][] { { 11904, 11929, 1 }, { 11931, 12019, 1 }, { 12032, 12245, 1 }, { 12293, 12293, 1 }, { 12295, 12295, 1 }, { 12321, 12329, 1 }, { 12344, 12347, 1 }, { 13312, 19893, 1 }, { 19968, 40907, 1 }, { 63744, 64045, 1 }, { 64048, 64109, 1 }, { 64112, 64217, 1 }, { 131072, 173782, 1 }, { 173824, 177972, 1 }, { 177984, 178205, 1 }, { 194560, 195101, 1 } };
    }
    
    private static int[][] make_Gothic() {
        return new int[][] { { 66352, 66378, 1 } };
    }
    
    private static Map<String, int[][]> Properties() {
        final Map<String, int[][]> map = new HashMap<String, int[][]>();
        map.put("Pattern_Syntax", UnicodeTables.Pattern_Syntax);
        map.put("Other_ID_Start", UnicodeTables.Other_ID_Start);
        map.put("Pattern_White_Space", UnicodeTables.Pattern_White_Space);
        map.put("Other_Lowercase", UnicodeTables.Other_Lowercase);
        map.put("Soft_Dotted", UnicodeTables.Soft_Dotted);
        map.put("Hex_Digit", UnicodeTables.Hex_Digit);
        map.put("ASCII_Hex_Digit", UnicodeTables.ASCII_Hex_Digit);
        map.put("Deprecated", UnicodeTables.Deprecated);
        map.put("Terminal_Punctuation", UnicodeTables.Terminal_Punctuation);
        map.put("Quotation_Mark", UnicodeTables.Quotation_Mark);
        map.put("Other_ID_Continue", UnicodeTables.Other_ID_Continue);
        map.put("Bidi_Control", UnicodeTables.Bidi_Control);
        map.put("Variation_Selector", UnicodeTables.Variation_Selector);
        map.put("Noncharacter_Code_Point", UnicodeTables.Noncharacter_Code_Point);
        map.put("Other_Math", UnicodeTables.Other_Math);
        map.put("Unified_Ideograph", UnicodeTables.Unified_Ideograph);
        map.put("Hyphen", UnicodeTables.Hyphen);
        map.put("IDS_Binary_Operator", UnicodeTables.IDS_Binary_Operator);
        map.put("Logical_Order_Exception", UnicodeTables.Logical_Order_Exception);
        map.put("Radical", UnicodeTables.Radical);
        map.put("Other_Uppercase", UnicodeTables.Other_Uppercase);
        map.put("STerm", UnicodeTables.STerm);
        map.put("Other_Alphabetic", UnicodeTables.Other_Alphabetic);
        map.put("Diacritic", UnicodeTables.Diacritic);
        map.put("Extender", UnicodeTables.Extender);
        map.put("Join_Control", UnicodeTables.Join_Control);
        map.put("Ideographic", UnicodeTables.Ideographic);
        map.put("Dash", UnicodeTables.Dash);
        map.put("IDS_Trinary_Operator", UnicodeTables.IDS_Trinary_Operator);
        map.put("Other_Grapheme_Extend", UnicodeTables.Other_Grapheme_Extend);
        map.put("Other_Default_Ignorable_Code_Point", UnicodeTables.Other_Default_Ignorable_Code_Point);
        map.put("White_Space", UnicodeTables.White_Space);
        return map;
    }
    
    private static int[][] make_Pattern_Syntax() {
        return new int[][] { { 33, 47, 1 }, { 58, 64, 1 }, { 91, 94, 1 }, { 96, 96, 1 }, { 123, 126, 1 }, { 161, 167, 1 }, { 169, 169, 1 }, { 171, 172, 1 }, { 174, 174, 1 }, { 176, 177, 1 }, { 182, 182, 1 }, { 187, 187, 1 }, { 191, 191, 1 }, { 215, 215, 1 }, { 247, 247, 1 }, { 8208, 8231, 1 }, { 8240, 8254, 1 }, { 8257, 8275, 1 }, { 8277, 8286, 1 }, { 8592, 9311, 1 }, { 9472, 10101, 1 }, { 10132, 11263, 1 }, { 11776, 11903, 1 }, { 12289, 12291, 1 }, { 12296, 12320, 1 }, { 12336, 12336, 1 }, { 64830, 64831, 1 }, { 65093, 65094, 1 } };
    }
    
    private static int[][] make_Other_ID_Start() {
        return new int[][] { { 8472, 8472, 1 }, { 8494, 8494, 1 }, { 12443, 12444, 1 } };
    }
    
    private static int[][] make_Pattern_White_Space() {
        return new int[][] { { 9, 13, 1 }, { 32, 32, 1 }, { 133, 133, 1 }, { 8206, 8207, 1 }, { 8232, 8233, 1 } };
    }
    
    private static int[][] make_Other_Lowercase() {
        return new int[][] { { 688, 696, 1 }, { 704, 705, 1 }, { 736, 740, 1 }, { 837, 837, 1 }, { 890, 890, 1 }, { 7468, 7521, 1 }, { 7544, 7544, 1 }, { 7579, 7615, 1 }, { 8336, 8340, 1 }, { 8560, 8575, 1 }, { 9424, 9449, 1 }, { 11389, 11389, 1 }, { 42864, 42864, 1 } };
    }
    
    private static int[][] make_Soft_Dotted() {
        return new int[][] { { 105, 106, 1 }, { 303, 303, 1 }, { 585, 585, 1 }, { 616, 616, 1 }, { 669, 669, 1 }, { 690, 690, 1 }, { 1011, 1011, 1 }, { 1110, 1110, 1 }, { 1112, 1112, 1 }, { 7522, 7522, 1 }, { 7574, 7574, 1 }, { 7588, 7588, 1 }, { 7592, 7592, 1 }, { 7725, 7725, 1 }, { 7883, 7883, 1 }, { 8305, 8305, 1 }, { 8520, 8521, 1 }, { 11388, 11388, 1 }, { 119842, 119843, 1 }, { 119894, 119895, 1 }, { 119946, 119947, 1 }, { 119998, 119999, 1 }, { 120050, 120051, 1 }, { 120102, 120103, 1 }, { 120154, 120155, 1 }, { 120206, 120207, 1 }, { 120258, 120259, 1 }, { 120310, 120311, 1 }, { 120362, 120363, 1 }, { 120414, 120415, 1 }, { 120466, 120467, 1 } };
    }
    
    private static int[][] make_Hex_Digit() {
        return new int[][] { { 48, 57, 1 }, { 65, 70, 1 }, { 97, 102, 1 }, { 65296, 65305, 1 }, { 65313, 65318, 1 }, { 65345, 65350, 1 } };
    }
    
    private static int[][] make_ASCII_Hex_Digit() {
        return new int[][] { { 48, 57, 1 }, { 65, 70, 1 }, { 97, 102, 1 } };
    }
    
    private static int[][] make_Deprecated() {
        return new int[][] { { 329, 329, 1 }, { 1651, 1651, 1 }, { 3959, 3959, 1 }, { 3961, 3961, 1 }, { 6051, 6052, 1 }, { 8298, 8303, 1 }, { 9001, 9002, 1 }, { 917505, 917505, 1 }, { 917536, 917631, 1 } };
    }
    
    private static int[][] make_Terminal_Punctuation() {
        return new int[][] { { 33, 33, 1 }, { 44, 44, 1 }, { 46, 46, 1 }, { 58, 59, 1 }, { 63, 63, 1 }, { 894, 894, 1 }, { 903, 903, 1 }, { 1417, 1417, 1 }, { 1475, 1475, 1 }, { 1548, 1548, 1 }, { 1563, 1563, 1 }, { 1567, 1567, 1 }, { 1748, 1748, 1 }, { 1792, 1802, 1 }, { 1804, 1804, 1 }, { 2040, 2041, 1 }, { 2096, 2110, 1 }, { 2142, 2142, 1 }, { 2404, 2405, 1 }, { 3674, 3675, 1 }, { 3848, 3848, 1 }, { 3853, 3858, 1 }, { 4170, 4171, 1 }, { 4961, 4968, 1 }, { 5741, 5742, 1 }, { 5867, 5869, 1 }, { 6100, 6102, 1 }, { 6106, 6106, 1 }, { 6146, 6149, 1 }, { 6152, 6153, 1 }, { 6468, 6469, 1 }, { 6824, 6827, 1 }, { 7002, 7003, 1 }, { 7005, 7007, 1 }, { 7227, 7231, 1 }, { 7294, 7295, 1 }, { 8252, 8253, 1 }, { 8263, 8265, 1 }, { 11822, 11822, 1 }, { 12289, 12290, 1 }, { 42238, 42239, 1 }, { 42509, 42511, 1 }, { 42739, 42743, 1 }, { 43126, 43127, 1 }, { 43214, 43215, 1 }, { 43311, 43311, 1 }, { 43463, 43465, 1 }, { 43613, 43615, 1 }, { 43743, 43743, 1 }, { 44011, 44011, 1 }, { 65104, 65106, 1 }, { 65108, 65111, 1 }, { 65281, 65281, 1 }, { 65292, 65292, 1 }, { 65294, 65294, 1 }, { 65306, 65307, 1 }, { 65311, 65311, 1 }, { 65377, 65377, 1 }, { 65380, 65380, 1 }, { 66463, 66463, 1 }, { 66512, 66512, 1 }, { 67671, 67671, 1 }, { 67871, 67871, 1 }, { 68410, 68415, 1 }, { 69703, 69709, 1 }, { 69822, 69825, 1 }, { 74864, 74867, 1 } };
    }
    
    private static int[][] make_Quotation_Mark() {
        return new int[][] { { 34, 34, 1 }, { 39, 39, 1 }, { 171, 171, 1 }, { 187, 187, 1 }, { 8216, 8223, 1 }, { 8249, 8250, 1 }, { 12300, 12303, 1 }, { 12317, 12319, 1 }, { 65089, 65092, 1 }, { 65282, 65282, 1 }, { 65287, 65287, 1 }, { 65378, 65379, 1 } };
    }
    
    private static int[][] make_Other_ID_Continue() {
        return new int[][] { { 183, 183, 1 }, { 903, 903, 1 }, { 4969, 4977, 1 }, { 6618, 6618, 1 } };
    }
    
    private static int[][] make_Bidi_Control() {
        return new int[][] { { 8206, 8207, 1 }, { 8234, 8238, 1 } };
    }
    
    private static int[][] make_Variation_Selector() {
        return new int[][] { { 6155, 6157, 1 }, { 65024, 65039, 1 }, { 917760, 917999, 1 } };
    }
    
    private static int[][] make_Noncharacter_Code_Point() {
        return new int[][] { { 64976, 65007, 1 }, { 65534, 65535, 1 }, { 131070, 131071, 1 }, { 196606, 196607, 1 }, { 262142, 262143, 1 }, { 327678, 327679, 1 }, { 393214, 393215, 1 }, { 458750, 458751, 1 }, { 524286, 524287, 1 }, { 589822, 589823, 1 }, { 655358, 655359, 1 }, { 720894, 720895, 1 }, { 786430, 786431, 1 }, { 851966, 851967, 1 }, { 917502, 917503, 1 }, { 983038, 983039, 1 }, { 1048574, 1048575, 1 }, { 1114110, 1114111, 1 } };
    }
    
    private static int[][] make_Other_Math() {
        return new int[][] { { 94, 94, 1 }, { 976, 978, 1 }, { 981, 981, 1 }, { 1008, 1009, 1 }, { 1012, 1013, 1 }, { 8214, 8214, 1 }, { 8242, 8244, 1 }, { 8256, 8256, 1 }, { 8289, 8292, 1 }, { 8317, 8318, 1 }, { 8333, 8334, 1 }, { 8400, 8412, 1 }, { 8417, 8417, 1 }, { 8421, 8422, 1 }, { 8427, 8431, 1 }, { 8450, 8450, 1 }, { 8455, 8455, 1 }, { 8458, 8467, 1 }, { 8469, 8469, 1 }, { 8473, 8477, 1 }, { 8484, 8484, 1 }, { 8488, 8489, 1 }, { 8492, 8493, 1 }, { 8495, 8497, 1 }, { 8499, 8504, 1 }, { 8508, 8511, 1 }, { 8517, 8521, 1 }, { 8597, 8601, 1 }, { 8604, 8607, 1 }, { 8609, 8610, 1 }, { 8612, 8613, 1 }, { 8615, 8615, 1 }, { 8617, 8621, 1 }, { 8624, 8625, 1 }, { 8630, 8631, 1 }, { 8636, 8653, 1 }, { 8656, 8657, 1 }, { 8659, 8659, 1 }, { 8661, 8667, 1 }, { 8669, 8669, 1 }, { 8676, 8677, 1 }, { 9140, 9141, 1 }, { 9143, 9143, 1 }, { 9168, 9168, 1 }, { 9186, 9186, 1 }, { 9632, 9633, 1 }, { 9646, 9654, 1 }, { 9660, 9664, 1 }, { 9670, 9671, 1 }, { 9674, 9675, 1 }, { 9679, 9683, 1 }, { 9698, 9698, 1 }, { 9700, 9700, 1 }, { 9703, 9708, 1 }, { 9733, 9734, 1 }, { 9792, 9792, 1 }, { 9794, 9794, 1 }, { 9824, 9827, 1 }, { 9837, 9838, 1 }, { 10181, 10182, 1 }, { 10214, 10223, 1 }, { 10627, 10648, 1 }, { 10712, 10715, 1 }, { 10748, 10749, 1 }, { 65121, 65121, 1 }, { 65123, 65123, 1 }, { 65128, 65128, 1 }, { 65340, 65340, 1 }, { 65342, 65342, 1 }, { 119808, 119892, 1 }, { 119894, 119964, 1 }, { 119966, 119967, 1 }, { 119970, 119970, 1 }, { 119973, 119974, 1 }, { 119977, 119980, 1 }, { 119982, 119993, 1 }, { 119995, 119995, 1 }, { 119997, 120003, 1 }, { 120005, 120069, 1 }, { 120071, 120074, 1 }, { 120077, 120084, 1 }, { 120086, 120092, 1 }, { 120094, 120121, 1 }, { 120123, 120126, 1 }, { 120128, 120132, 1 }, { 120134, 120134, 1 }, { 120138, 120144, 1 }, { 120146, 120485, 1 }, { 120488, 120512, 1 }, { 120514, 120538, 1 }, { 120540, 120570, 1 }, { 120572, 120596, 1 }, { 120598, 120628, 1 }, { 120630, 120654, 1 }, { 120656, 120686, 1 }, { 120688, 120712, 1 }, { 120714, 120744, 1 }, { 120746, 120770, 1 }, { 120772, 120779, 1 }, { 120782, 120831, 1 } };
    }
    
    private static int[][] make_Unified_Ideograph() {
        return new int[][] { { 13312, 19893, 1 }, { 19968, 40907, 1 }, { 64014, 64015, 1 }, { 64017, 64017, 1 }, { 64019, 64020, 1 }, { 64031, 64031, 1 }, { 64033, 64033, 1 }, { 64035, 64036, 1 }, { 64039, 64041, 1 }, { 131072, 173782, 1 }, { 173824, 177972, 1 }, { 177984, 178205, 1 } };
    }
    
    private static int[][] make_Hyphen() {
        return new int[][] { { 45, 45, 1 }, { 173, 173, 1 }, { 1418, 1418, 1 }, { 6150, 6150, 1 }, { 8208, 8209, 1 }, { 11799, 11799, 1 }, { 12539, 12539, 1 }, { 65123, 65123, 1 }, { 65293, 65293, 1 }, { 65381, 65381, 1 } };
    }
    
    private static int[][] make_IDS_Binary_Operator() {
        return new int[][] { { 12272, 12273, 1 }, { 12276, 12283, 1 } };
    }
    
    private static int[][] make_Logical_Order_Exception() {
        return new int[][] { { 3648, 3652, 1 }, { 3776, 3780, 1 }, { 43701, 43702, 1 }, { 43705, 43705, 1 }, { 43707, 43708, 1 } };
    }
    
    private static int[][] make_Radical() {
        return new int[][] { { 11904, 11929, 1 }, { 11931, 12019, 1 }, { 12032, 12245, 1 } };
    }
    
    private static int[][] make_Other_Uppercase() {
        return new int[][] { { 8544, 8559, 1 }, { 9398, 9423, 1 } };
    }
    
    private static int[][] make_STerm() {
        return new int[][] { { 33, 33, 1 }, { 46, 46, 1 }, { 63, 63, 1 }, { 1372, 1372, 1 }, { 1374, 1374, 1 }, { 1417, 1417, 1 }, { 1567, 1567, 1 }, { 1748, 1748, 1 }, { 1792, 1794, 1 }, { 2041, 2041, 1 }, { 2404, 2405, 1 }, { 4170, 4171, 1 }, { 4962, 4962, 1 }, { 4967, 4968, 1 }, { 5742, 5742, 1 }, { 5941, 5942, 1 }, { 6147, 6147, 1 }, { 6153, 6153, 1 }, { 6468, 6469, 1 }, { 6824, 6827, 1 }, { 7002, 7003, 1 }, { 7006, 7007, 1 }, { 7227, 7228, 1 }, { 7294, 7295, 1 }, { 8252, 8253, 1 }, { 8263, 8265, 1 }, { 11822, 11822, 1 }, { 12290, 12290, 1 }, { 42239, 42239, 1 }, { 42510, 42511, 1 }, { 42739, 42739, 1 }, { 42743, 42743, 1 }, { 43126, 43127, 1 }, { 43214, 43215, 1 }, { 43311, 43311, 1 }, { 43464, 43465, 1 }, { 43613, 43615, 1 }, { 44011, 44011, 1 }, { 65106, 65106, 1 }, { 65110, 65111, 1 }, { 65281, 65281, 1 }, { 65294, 65294, 1 }, { 65311, 65311, 1 }, { 65377, 65377, 1 }, { 68182, 68183, 1 }, { 69703, 69704, 1 }, { 69822, 69825, 1 } };
    }
    
    private static int[][] make_Other_Alphabetic() {
        return new int[][] { { 837, 837, 1 }, { 1456, 1469, 1 }, { 1471, 1471, 1 }, { 1473, 1474, 1 }, { 1476, 1477, 1 }, { 1479, 1479, 1 }, { 1552, 1562, 1 }, { 1611, 1623, 1 }, { 1625, 1631, 1 }, { 1648, 1648, 1 }, { 1750, 1756, 1 }, { 1761, 1764, 1 }, { 1767, 1768, 1 }, { 1773, 1773, 1 }, { 1809, 1809, 1 }, { 1840, 1855, 1 }, { 1958, 1968, 1 }, { 2070, 2071, 1 }, { 2075, 2083, 1 }, { 2085, 2087, 1 }, { 2089, 2092, 1 }, { 2304, 2307, 1 }, { 2362, 2363, 1 }, { 2366, 2380, 1 }, { 2382, 2383, 1 }, { 2389, 2391, 1 }, { 2402, 2403, 1 }, { 2433, 2435, 1 }, { 2494, 2500, 1 }, { 2503, 2504, 1 }, { 2507, 2508, 1 }, { 2519, 2519, 1 }, { 2530, 2531, 1 }, { 2561, 2563, 1 }, { 2622, 2626, 1 }, { 2631, 2632, 1 }, { 2635, 2636, 1 }, { 2641, 2641, 1 }, { 2672, 2673, 1 }, { 2677, 2677, 1 }, { 2689, 2691, 1 }, { 2750, 2757, 1 }, { 2759, 2761, 1 }, { 2763, 2764, 1 }, { 2786, 2787, 1 }, { 2817, 2819, 1 }, { 2878, 2884, 1 }, { 2887, 2888, 1 }, { 2891, 2892, 1 }, { 2902, 2903, 1 }, { 2914, 2915, 1 }, { 2946, 2946, 1 }, { 3006, 3010, 1 }, { 3014, 3016, 1 }, { 3018, 3020, 1 }, { 3031, 3031, 1 }, { 3073, 3075, 1 }, { 3134, 3140, 1 }, { 3142, 3144, 1 }, { 3146, 3148, 1 }, { 3157, 3158, 1 }, { 3170, 3171, 1 }, { 3202, 3203, 1 }, { 3262, 3268, 1 }, { 3270, 3272, 1 }, { 3274, 3276, 1 }, { 3285, 3286, 1 }, { 3298, 3299, 1 }, { 3330, 3331, 1 }, { 3390, 3396, 1 }, { 3398, 3400, 1 }, { 3402, 3404, 1 }, { 3415, 3415, 1 }, { 3426, 3427, 1 }, { 3458, 3459, 1 }, { 3535, 3540, 1 }, { 3542, 3542, 1 }, { 3544, 3551, 1 }, { 3570, 3571, 1 }, { 3633, 3633, 1 }, { 3636, 3642, 1 }, { 3661, 3661, 1 }, { 3761, 3761, 1 }, { 3764, 3769, 1 }, { 3771, 3772, 1 }, { 3789, 3789, 1 }, { 3953, 3969, 1 }, { 3981, 3991, 1 }, { 3993, 4028, 1 }, { 4139, 4150, 1 }, { 4152, 4152, 1 }, { 4155, 4158, 1 }, { 4182, 4185, 1 }, { 4190, 4192, 1 }, { 4194, 4194, 1 }, { 4199, 4200, 1 }, { 4209, 4212, 1 }, { 4226, 4230, 1 }, { 4252, 4253, 1 }, { 4959, 4959, 1 }, { 5906, 5907, 1 }, { 5938, 5939, 1 }, { 5970, 5971, 1 }, { 6002, 6003, 1 }, { 6070, 6088, 1 }, { 6313, 6313, 1 }, { 6432, 6443, 1 }, { 6448, 6456, 1 }, { 6576, 6592, 1 }, { 6600, 6601, 1 }, { 6679, 6683, 1 }, { 6741, 6750, 1 }, { 6753, 6772, 1 }, { 6912, 6916, 1 }, { 6965, 6979, 1 }, { 7040, 7042, 1 }, { 7073, 7081, 1 }, { 7143, 7153, 1 }, { 7204, 7221, 1 }, { 7410, 7410, 1 }, { 9398, 9449, 1 }, { 11744, 11775, 1 }, { 43043, 43047, 1 }, { 43136, 43137, 1 }, { 43188, 43203, 1 }, { 43302, 43306, 1 }, { 43335, 43346, 1 }, { 43392, 43395, 1 }, { 43444, 43455, 1 }, { 43561, 43574, 1 }, { 43587, 43587, 1 }, { 43596, 43597, 1 }, { 43696, 43696, 1 }, { 43698, 43700, 1 }, { 43703, 43704, 1 }, { 43710, 43710, 1 }, { 44003, 44010, 1 }, { 64286, 64286, 1 }, { 68097, 68099, 1 }, { 68101, 68102, 1 }, { 68108, 68111, 1 }, { 69632, 69634, 1 }, { 69688, 69701, 1 }, { 69762, 69762, 1 }, { 69808, 69816, 1 } };
    }
    
    private static int[][] make_Diacritic() {
        return new int[][] { { 94, 94, 1 }, { 96, 96, 1 }, { 168, 168, 1 }, { 175, 175, 1 }, { 180, 180, 1 }, { 183, 184, 1 }, { 688, 846, 1 }, { 848, 855, 1 }, { 861, 866, 1 }, { 884, 885, 1 }, { 890, 890, 1 }, { 900, 901, 1 }, { 1155, 1159, 1 }, { 1369, 1369, 1 }, { 1425, 1441, 1 }, { 1443, 1469, 1 }, { 1471, 1471, 1 }, { 1473, 1474, 1 }, { 1476, 1476, 1 }, { 1611, 1618, 1 }, { 1623, 1624, 1 }, { 1759, 1760, 1 }, { 1765, 1766, 1 }, { 1770, 1772, 1 }, { 1840, 1866, 1 }, { 1958, 1968, 1 }, { 2027, 2037, 1 }, { 2072, 2073, 1 }, { 2364, 2364, 1 }, { 2381, 2381, 1 }, { 2385, 2388, 1 }, { 2417, 2417, 1 }, { 2492, 2492, 1 }, { 2509, 2509, 1 }, { 2620, 2620, 1 }, { 2637, 2637, 1 }, { 2748, 2748, 1 }, { 2765, 2765, 1 }, { 2876, 2876, 1 }, { 2893, 2893, 1 }, { 3021, 3021, 1 }, { 3149, 3149, 1 }, { 3260, 3260, 1 }, { 3277, 3277, 1 }, { 3405, 3405, 1 }, { 3530, 3530, 1 }, { 3655, 3660, 1 }, { 3662, 3662, 1 }, { 3784, 3788, 1 }, { 3864, 3865, 1 }, { 3893, 3893, 1 }, { 3895, 3895, 1 }, { 3897, 3897, 1 }, { 3902, 3903, 1 }, { 3970, 3972, 1 }, { 3974, 3975, 1 }, { 4038, 4038, 1 }, { 4151, 4151, 1 }, { 4153, 4154, 1 }, { 4231, 4237, 1 }, { 4239, 4239, 1 }, { 4250, 4251, 1 }, { 6089, 6099, 1 }, { 6109, 6109, 1 }, { 6457, 6459, 1 }, { 6773, 6780, 1 }, { 6783, 6783, 1 }, { 6964, 6964, 1 }, { 6980, 6980, 1 }, { 7019, 7027, 1 }, { 7082, 7082, 1 }, { 7222, 7223, 1 }, { 7288, 7293, 1 }, { 7376, 7400, 1 }, { 7405, 7405, 1 }, { 7468, 7530, 1 }, { 7620, 7631, 1 }, { 7677, 7679, 1 }, { 8125, 8125, 1 }, { 8127, 8129, 1 }, { 8141, 8143, 1 }, { 8157, 8159, 1 }, { 8173, 8175, 1 }, { 8189, 8190, 1 }, { 11503, 11505, 1 }, { 11823, 11823, 1 }, { 12330, 12335, 1 }, { 12441, 12444, 1 }, { 12540, 12540, 1 }, { 42607, 42607, 1 }, { 42620, 42621, 1 }, { 42623, 42623, 1 }, { 42736, 42737, 1 }, { 42775, 42785, 1 }, { 42888, 42888, 1 }, { 43204, 43204, 1 }, { 43232, 43249, 1 }, { 43307, 43310, 1 }, { 43347, 43347, 1 }, { 43443, 43443, 1 }, { 43456, 43456, 1 }, { 43643, 43643, 1 }, { 43711, 43714, 1 }, { 44012, 44013, 1 }, { 64286, 64286, 1 }, { 65056, 65062, 1 }, { 65342, 65342, 1 }, { 65344, 65344, 1 }, { 65392, 65392, 1 }, { 65438, 65439, 1 }, { 65507, 65507, 1 }, { 69817, 69818, 1 }, { 119143, 119145, 1 }, { 119149, 119154, 1 }, { 119163, 119170, 1 }, { 119173, 119179, 1 }, { 119210, 119213, 1 } };
    }
    
    private static int[][] make_Extender() {
        return new int[][] { { 183, 183, 1 }, { 720, 721, 1 }, { 1600, 1600, 1 }, { 2042, 2042, 1 }, { 3654, 3654, 1 }, { 3782, 3782, 1 }, { 6211, 6211, 1 }, { 6823, 6823, 1 }, { 7222, 7222, 1 }, { 7291, 7291, 1 }, { 12293, 12293, 1 }, { 12337, 12341, 1 }, { 12445, 12446, 1 }, { 12540, 12542, 1 }, { 40981, 40981, 1 }, { 42508, 42508, 1 }, { 43471, 43471, 1 }, { 43632, 43632, 1 }, { 43741, 43741, 1 }, { 65392, 65392, 1 } };
    }
    
    private static int[][] make_Join_Control() {
        return new int[][] { { 8204, 8205, 1 } };
    }
    
    private static int[][] make_Ideographic() {
        return new int[][] { { 12294, 12295, 1 }, { 12321, 12329, 1 }, { 12344, 12346, 1 }, { 13312, 19893, 1 }, { 19968, 40907, 1 }, { 63744, 64045, 1 }, { 64048, 64109, 1 }, { 64112, 64217, 1 }, { 131072, 173782, 1 }, { 173824, 177972, 1 }, { 177984, 178205, 1 }, { 194560, 195101, 1 } };
    }
    
    private static int[][] make_Dash() {
        return new int[][] { { 45, 45, 1 }, { 1418, 1418, 1 }, { 1470, 1470, 1 }, { 5120, 5120, 1 }, { 6150, 6150, 1 }, { 8208, 8213, 1 }, { 8275, 8275, 1 }, { 8315, 8315, 1 }, { 8331, 8331, 1 }, { 8722, 8722, 1 }, { 11799, 11799, 1 }, { 11802, 11802, 1 }, { 12316, 12316, 1 }, { 12336, 12336, 1 }, { 12448, 12448, 1 }, { 65073, 65074, 1 }, { 65112, 65112, 1 }, { 65123, 65123, 1 }, { 65293, 65293, 1 } };
    }
    
    private static int[][] make_IDS_Trinary_Operator() {
        return new int[][] { { 12274, 12275, 1 } };
    }
    
    private static int[][] make_Other_Grapheme_Extend() {
        return new int[][] { { 2494, 2494, 1 }, { 2519, 2519, 1 }, { 2878, 2878, 1 }, { 2903, 2903, 1 }, { 3006, 3006, 1 }, { 3031, 3031, 1 }, { 3266, 3266, 1 }, { 3285, 3286, 1 }, { 3390, 3390, 1 }, { 3415, 3415, 1 }, { 3535, 3535, 1 }, { 3551, 3551, 1 }, { 8204, 8205, 1 }, { 65438, 65439, 1 }, { 119141, 119141, 1 }, { 119150, 119154, 1 } };
    }
    
    private static int[][] make_Other_Default_Ignorable_Code_Point() {
        return new int[][] { { 847, 847, 1 }, { 4447, 4448, 1 }, { 8293, 8297, 1 }, { 12644, 12644, 1 }, { 65440, 65440, 1 }, { 65520, 65528, 1 }, { 917504, 917504, 1 }, { 917506, 917535, 1 }, { 917632, 917759, 1 }, { 918000, 921599, 1 } };
    }
    
    private static int[][] make_White_Space() {
        return new int[][] { { 9, 13, 1 }, { 32, 32, 1 }, { 133, 133, 1 }, { 160, 160, 1 }, { 5760, 5760, 1 }, { 6158, 6158, 1 }, { 8192, 8202, 1 }, { 8232, 8233, 1 }, { 8239, 8239, 1 }, { 8287, 8287, 1 }, { 12288, 12288, 1 } };
    }
    
    private static Map<String, int[][]> FoldCategory() {
        final Map<String, int[][]> map = new HashMap<String, int[][]>();
        map.put("Ll", UnicodeTables.foldLl);
        map.put("Inherited", UnicodeTables.foldInherited);
        map.put("M", UnicodeTables.foldM);
        map.put("L", UnicodeTables.foldL);
        map.put("Mn", UnicodeTables.foldMn);
        map.put("Common", UnicodeTables.foldCommon);
        map.put("Greek", UnicodeTables.foldGreek);
        map.put("Lu", UnicodeTables.foldLu);
        map.put("Lt", UnicodeTables.foldLt);
        return map;
    }
    
    private static int[][] makefoldLl() {
        return new int[][] { { 65, 90, 1 }, { 192, 214, 1 }, { 216, 222, 1 }, { 256, 302, 2 }, { 306, 310, 2 }, { 313, 327, 2 }, { 330, 376, 2 }, { 377, 381, 2 }, { 385, 386, 1 }, { 388, 390, 2 }, { 391, 393, 2 }, { 394, 395, 1 }, { 398, 401, 1 }, { 403, 404, 1 }, { 406, 408, 1 }, { 412, 413, 1 }, { 415, 416, 1 }, { 418, 422, 2 }, { 423, 425, 2 }, { 428, 430, 2 }, { 431, 433, 2 }, { 434, 435, 1 }, { 437, 439, 2 }, { 440, 444, 4 }, { 452, 453, 1 }, { 455, 456, 1 }, { 458, 459, 1 }, { 461, 475, 2 }, { 478, 494, 2 }, { 497, 498, 1 }, { 500, 502, 2 }, { 503, 504, 1 }, { 506, 562, 2 }, { 570, 571, 1 }, { 573, 574, 1 }, { 577, 579, 2 }, { 580, 582, 1 }, { 584, 590, 2 }, { 837, 880, 43 }, { 882, 886, 4 }, { 902, 904, 2 }, { 905, 906, 1 }, { 908, 910, 2 }, { 911, 913, 2 }, { 914, 929, 1 }, { 931, 939, 1 }, { 975, 984, 9 }, { 986, 1006, 2 }, { 1012, 1015, 3 }, { 1017, 1018, 1 }, { 1021, 1071, 1 }, { 1120, 1152, 2 }, { 1162, 1216, 2 }, { 1217, 1229, 2 }, { 1232, 1318, 2 }, { 1329, 1366, 1 }, { 4256, 4293, 1 }, { 7680, 7828, 2 }, { 7838, 7934, 2 }, { 7944, 7951, 1 }, { 7960, 7965, 1 }, { 7976, 7983, 1 }, { 7992, 7999, 1 }, { 8008, 8013, 1 }, { 8025, 8031, 2 }, { 8040, 8047, 1 }, { 8072, 8079, 1 }, { 8088, 8095, 1 }, { 8104, 8111, 1 }, { 8120, 8124, 1 }, { 8136, 8140, 1 }, { 8152, 8155, 1 }, { 8168, 8172, 1 }, { 8184, 8188, 1 }, { 8486, 8490, 4 }, { 8491, 8498, 7 }, { 8579, 11264, 2685 }, { 11265, 11310, 1 }, { 11360, 11362, 2 }, { 11363, 11364, 1 }, { 11367, 11373, 2 }, { 11374, 11376, 1 }, { 11378, 11381, 3 }, { 11390, 11392, 1 }, { 11394, 11490, 2 }, { 11499, 11501, 2 }, { 42560, 42604, 2 }, { 42624, 42646, 2 }, { 42786, 42798, 2 }, { 42802, 42862, 2 }, { 42873, 42877, 2 }, { 42878, 42886, 2 }, { 42891, 42893, 2 }, { 42896, 42912, 16 }, { 42914, 42920, 2 }, { 65313, 65338, 1 }, { 66560, 66599, 1 } };
    }
    
    private static int[][] makefoldInherited() {
        return new int[][] { { 921, 953, 32 }, { 8126, 8126, 1 } };
    }
    
    private static int[][] makefoldM() {
        return new int[][] { { 921, 953, 32 }, { 8126, 8126, 1 } };
    }
    
    private static int[][] makefoldL() {
        return new int[][] { { 837, 837, 1 } };
    }
    
    private static int[][] makefoldMn() {
        return new int[][] { { 921, 953, 32 }, { 8126, 8126, 1 } };
    }
    
    private static int[][] makefoldCommon() {
        return new int[][] { { 924, 956, 32 } };
    }
    
    private static int[][] makefoldGreek() {
        return new int[][] { { 181, 837, 656 } };
    }
    
    private static int[][] makefoldLu() {
        return new int[][] { { 97, 122, 1 }, { 181, 223, 42 }, { 224, 246, 1 }, { 248, 255, 1 }, { 257, 303, 2 }, { 307, 311, 2 }, { 314, 328, 2 }, { 331, 375, 2 }, { 378, 382, 2 }, { 383, 384, 1 }, { 387, 389, 2 }, { 392, 396, 4 }, { 402, 405, 3 }, { 409, 410, 1 }, { 414, 417, 3 }, { 419, 421, 2 }, { 424, 429, 5 }, { 432, 436, 4 }, { 438, 441, 3 }, { 445, 447, 2 }, { 453, 454, 1 }, { 456, 457, 1 }, { 459, 460, 1 }, { 462, 476, 2 }, { 477, 495, 2 }, { 498, 499, 1 }, { 501, 505, 4 }, { 507, 543, 2 }, { 547, 563, 2 }, { 572, 575, 3 }, { 576, 578, 2 }, { 583, 591, 2 }, { 592, 596, 1 }, { 598, 599, 1 }, { 601, 603, 2 }, { 608, 611, 3 }, { 613, 616, 3 }, { 617, 619, 2 }, { 623, 625, 2 }, { 626, 629, 3 }, { 637, 643, 3 }, { 648, 652, 1 }, { 658, 837, 179 }, { 881, 883, 2 }, { 887, 891, 4 }, { 892, 893, 1 }, { 940, 943, 1 }, { 945, 974, 1 }, { 976, 977, 1 }, { 981, 983, 1 }, { 985, 1007, 2 }, { 1008, 1010, 1 }, { 1013, 1019, 3 }, { 1072, 1119, 1 }, { 1121, 1153, 2 }, { 1163, 1215, 2 }, { 1218, 1230, 2 }, { 1231, 1319, 2 }, { 1377, 1414, 1 }, { 7545, 7549, 4 }, { 7681, 7829, 2 }, { 7835, 7841, 6 }, { 7843, 7935, 2 }, { 7936, 7943, 1 }, { 7952, 7957, 1 }, { 7968, 7975, 1 }, { 7984, 7991, 1 }, { 8000, 8005, 1 }, { 8017, 8023, 2 }, { 8032, 8039, 1 }, { 8048, 8061, 1 }, { 8112, 8113, 1 }, { 8126, 8144, 18 }, { 8145, 8160, 15 }, { 8161, 8165, 4 }, { 8526, 8580, 54 }, { 11312, 11358, 1 }, { 11361, 11365, 4 }, { 11366, 11372, 2 }, { 11379, 11382, 3 }, { 11393, 11491, 2 }, { 11500, 11502, 2 }, { 11520, 11557, 1 }, { 42561, 42605, 2 }, { 42625, 42647, 2 }, { 42787, 42799, 2 }, { 42803, 42863, 2 }, { 42874, 42876, 2 }, { 42879, 42887, 2 }, { 42892, 42897, 5 }, { 42913, 42921, 2 }, { 65345, 65370, 1 }, { 66600, 66639, 1 } };
    }
    
    private static int[][] makefoldLt() {
        return new int[][] { { 452, 454, 2 }, { 455, 457, 2 }, { 458, 460, 2 }, { 497, 499, 2 }, { 8064, 8071, 1 }, { 8080, 8087, 1 }, { 8096, 8103, 1 }, { 8115, 8131, 16 }, { 8179, 8179, 1 } };
    }
    
    private static Map<String, int[][]> FoldScript() {
        return new HashMap<String, int[][]>();
    }
    
    private UnicodeTables() {
    }
    
    static {
        _Lm = make_Lm();
        _Ll = make_Ll();
        _C = make_C();
        _M = make_M();
        _L = make_L();
        _N = make_N();
        _P = make_P();
        _S = make_S();
        _Z = make_Z();
        _Me = make_Me();
        _Mc = make_Mc();
        _Mn = make_Mn();
        _Zl = make_Zl();
        _Zp = make_Zp();
        _Zs = make_Zs();
        _Cs = make_Cs();
        _Co = make_Co();
        _Cf = make_Cf();
        _Cc = make_Cc();
        _Po = make_Po();
        _Pi = make_Pi();
        _Pf = make_Pf();
        _Pe = make_Pe();
        _Pd = make_Pd();
        _Pc = make_Pc();
        _Ps = make_Ps();
        _Nd = make_Nd();
        _Nl = make_Nl();
        _No = make_No();
        _So = make_So();
        _Sm = make_Sm();
        _Sk = make_Sk();
        _Sc = make_Sc();
        _Lu = make_Lu();
        _Lt = make_Lt();
        _Lo = make_Lo();
        Cc = UnicodeTables._Cc;
        Cf = UnicodeTables._Cf;
        Co = UnicodeTables._Co;
        Cs = UnicodeTables._Cs;
        Digit = UnicodeTables._Nd;
        Nd = UnicodeTables._Nd;
        Letter = UnicodeTables._L;
        L = UnicodeTables._L;
        Lm = UnicodeTables._Lm;
        Lo = UnicodeTables._Lo;
        Lower = UnicodeTables._Ll;
        Ll = UnicodeTables._Ll;
        Mark = UnicodeTables._M;
        M = UnicodeTables._M;
        Mc = UnicodeTables._Mc;
        Me = UnicodeTables._Me;
        Mn = UnicodeTables._Mn;
        Nl = UnicodeTables._Nl;
        No = UnicodeTables._No;
        Number = UnicodeTables._N;
        N = UnicodeTables._N;
        Other = UnicodeTables._C;
        C = UnicodeTables._C;
        Pc = UnicodeTables._Pc;
        Pd = UnicodeTables._Pd;
        Pe = UnicodeTables._Pe;
        Pf = UnicodeTables._Pf;
        Pi = UnicodeTables._Pi;
        Po = UnicodeTables._Po;
        Ps = UnicodeTables._Ps;
        Punct = UnicodeTables._P;
        P = UnicodeTables._P;
        Sc = UnicodeTables._Sc;
        Sk = UnicodeTables._Sk;
        Sm = UnicodeTables._Sm;
        So = UnicodeTables._So;
        Space = UnicodeTables._Z;
        Z = UnicodeTables._Z;
        Symbol = UnicodeTables._S;
        S = UnicodeTables._S;
        Title = UnicodeTables._Lt;
        Lt = UnicodeTables._Lt;
        Upper = UnicodeTables._Lu;
        Lu = UnicodeTables._Lu;
        Zl = UnicodeTables._Zl;
        Zp = UnicodeTables._Zp;
        Zs = UnicodeTables._Zs;
        _Katakana = make_Katakana();
        _Malayalam = make_Malayalam();
        _Phags_Pa = make_Phags_Pa();
        _Inscriptional_Parthian = make_Inscriptional_Parthian();
        _Latin = make_Latin();
        _Inscriptional_Pahlavi = make_Inscriptional_Pahlavi();
        _Osmanya = make_Osmanya();
        _Khmer = make_Khmer();
        _Inherited = make_Inherited();
        _Telugu = make_Telugu();
        _Samaritan = make_Samaritan();
        _Bopomofo = make_Bopomofo();
        _Imperial_Aramaic = make_Imperial_Aramaic();
        _Kaithi = make_Kaithi();
        _Mandaic = make_Mandaic();
        _Old_South_Arabian = make_Old_South_Arabian();
        _Kayah_Li = make_Kayah_Li();
        _New_Tai_Lue = make_New_Tai_Lue();
        _Tai_Le = make_Tai_Le();
        _Kharoshthi = make_Kharoshthi();
        _Common = make_Common();
        _Kannada = make_Kannada();
        _Old_Turkic = make_Old_Turkic();
        _Tamil = make_Tamil();
        _Tagalog = make_Tagalog();
        _Brahmi = make_Brahmi();
        _Arabic = make_Arabic();
        _Tagbanwa = make_Tagbanwa();
        _Canadian_Aboriginal = make_Canadian_Aboriginal();
        _Tibetan = make_Tibetan();
        _Coptic = make_Coptic();
        _Hiragana = make_Hiragana();
        _Limbu = make_Limbu();
        _Egyptian_Hieroglyphs = make_Egyptian_Hieroglyphs();
        _Avestan = make_Avestan();
        _Myanmar = make_Myanmar();
        _Armenian = make_Armenian();
        _Sinhala = make_Sinhala();
        _Bengali = make_Bengali();
        _Greek = make_Greek();
        _Cham = make_Cham();
        _Hebrew = make_Hebrew();
        _Meetei_Mayek = make_Meetei_Mayek();
        _Saurashtra = make_Saurashtra();
        _Hangul = make_Hangul();
        _Runic = make_Runic();
        _Deseret = make_Deseret();
        _Lisu = make_Lisu();
        _Sundanese = make_Sundanese();
        _Glagolitic = make_Glagolitic();
        _Oriya = make_Oriya();
        _Buhid = make_Buhid();
        _Ethiopic = make_Ethiopic();
        _Javanese = make_Javanese();
        _Syloti_Nagri = make_Syloti_Nagri();
        _Vai = make_Vai();
        _Cherokee = make_Cherokee();
        _Ogham = make_Ogham();
        _Batak = make_Batak();
        _Syriac = make_Syriac();
        _Gurmukhi = make_Gurmukhi();
        _Tai_Tham = make_Tai_Tham();
        _Ol_Chiki = make_Ol_Chiki();
        _Mongolian = make_Mongolian();
        _Hanunoo = make_Hanunoo();
        _Cypriot = make_Cypriot();
        _Buginese = make_Buginese();
        _Bamum = make_Bamum();
        _Lepcha = make_Lepcha();
        _Thaana = make_Thaana();
        _Old_Persian = make_Old_Persian();
        _Cuneiform = make_Cuneiform();
        _Rejang = make_Rejang();
        _Georgian = make_Georgian();
        _Shavian = make_Shavian();
        _Lycian = make_Lycian();
        _Nko = make_Nko();
        _Yi = make_Yi();
        _Lao = make_Lao();
        _Linear_B = make_Linear_B();
        _Old_Italic = make_Old_Italic();
        _Tai_Viet = make_Tai_Viet();
        _Devanagari = make_Devanagari();
        _Lydian = make_Lydian();
        _Tifinagh = make_Tifinagh();
        _Ugaritic = make_Ugaritic();
        _Thai = make_Thai();
        _Cyrillic = make_Cyrillic();
        _Gujarati = make_Gujarati();
        _Carian = make_Carian();
        _Phoenician = make_Phoenician();
        _Balinese = make_Balinese();
        _Braille = make_Braille();
        _Han = make_Han();
        _Gothic = make_Gothic();
        Arabic = UnicodeTables._Arabic;
        Armenian = UnicodeTables._Armenian;
        Avestan = UnicodeTables._Avestan;
        Balinese = UnicodeTables._Balinese;
        Bamum = UnicodeTables._Bamum;
        Batak = UnicodeTables._Batak;
        Bengali = UnicodeTables._Bengali;
        Bopomofo = UnicodeTables._Bopomofo;
        Brahmi = UnicodeTables._Brahmi;
        Braille = UnicodeTables._Braille;
        Buginese = UnicodeTables._Buginese;
        Buhid = UnicodeTables._Buhid;
        Canadian_Aboriginal = UnicodeTables._Canadian_Aboriginal;
        Carian = UnicodeTables._Carian;
        Cham = UnicodeTables._Cham;
        Cherokee = UnicodeTables._Cherokee;
        Common = UnicodeTables._Common;
        Coptic = UnicodeTables._Coptic;
        Cuneiform = UnicodeTables._Cuneiform;
        Cypriot = UnicodeTables._Cypriot;
        Cyrillic = UnicodeTables._Cyrillic;
        Deseret = UnicodeTables._Deseret;
        Devanagari = UnicodeTables._Devanagari;
        Egyptian_Hieroglyphs = UnicodeTables._Egyptian_Hieroglyphs;
        Ethiopic = UnicodeTables._Ethiopic;
        Georgian = UnicodeTables._Georgian;
        Glagolitic = UnicodeTables._Glagolitic;
        Gothic = UnicodeTables._Gothic;
        Greek = UnicodeTables._Greek;
        Gujarati = UnicodeTables._Gujarati;
        Gurmukhi = UnicodeTables._Gurmukhi;
        Han = UnicodeTables._Han;
        Hangul = UnicodeTables._Hangul;
        Hanunoo = UnicodeTables._Hanunoo;
        Hebrew = UnicodeTables._Hebrew;
        Hiragana = UnicodeTables._Hiragana;
        Imperial_Aramaic = UnicodeTables._Imperial_Aramaic;
        Inherited = UnicodeTables._Inherited;
        Inscriptional_Pahlavi = UnicodeTables._Inscriptional_Pahlavi;
        Inscriptional_Parthian = UnicodeTables._Inscriptional_Parthian;
        Javanese = UnicodeTables._Javanese;
        Kaithi = UnicodeTables._Kaithi;
        Kannada = UnicodeTables._Kannada;
        Katakana = UnicodeTables._Katakana;
        Kayah_Li = UnicodeTables._Kayah_Li;
        Kharoshthi = UnicodeTables._Kharoshthi;
        Khmer = UnicodeTables._Khmer;
        Lao = UnicodeTables._Lao;
        Latin = UnicodeTables._Latin;
        Lepcha = UnicodeTables._Lepcha;
        Limbu = UnicodeTables._Limbu;
        Linear_B = UnicodeTables._Linear_B;
        Lisu = UnicodeTables._Lisu;
        Lycian = UnicodeTables._Lycian;
        Lydian = UnicodeTables._Lydian;
        Malayalam = UnicodeTables._Malayalam;
        Mandaic = UnicodeTables._Mandaic;
        Meetei_Mayek = UnicodeTables._Meetei_Mayek;
        Mongolian = UnicodeTables._Mongolian;
        Myanmar = UnicodeTables._Myanmar;
        New_Tai_Lue = UnicodeTables._New_Tai_Lue;
        Nko = UnicodeTables._Nko;
        Ogham = UnicodeTables._Ogham;
        Ol_Chiki = UnicodeTables._Ol_Chiki;
        Old_Italic = UnicodeTables._Old_Italic;
        Old_Persian = UnicodeTables._Old_Persian;
        Old_South_Arabian = UnicodeTables._Old_South_Arabian;
        Old_Turkic = UnicodeTables._Old_Turkic;
        Oriya = UnicodeTables._Oriya;
        Osmanya = UnicodeTables._Osmanya;
        Phags_Pa = UnicodeTables._Phags_Pa;
        Phoenician = UnicodeTables._Phoenician;
        Rejang = UnicodeTables._Rejang;
        Runic = UnicodeTables._Runic;
        Samaritan = UnicodeTables._Samaritan;
        Saurashtra = UnicodeTables._Saurashtra;
        Shavian = UnicodeTables._Shavian;
        Sinhala = UnicodeTables._Sinhala;
        Sundanese = UnicodeTables._Sundanese;
        Syloti_Nagri = UnicodeTables._Syloti_Nagri;
        Syriac = UnicodeTables._Syriac;
        Tagalog = UnicodeTables._Tagalog;
        Tagbanwa = UnicodeTables._Tagbanwa;
        Tai_Le = UnicodeTables._Tai_Le;
        Tai_Tham = UnicodeTables._Tai_Tham;
        Tai_Viet = UnicodeTables._Tai_Viet;
        Tamil = UnicodeTables._Tamil;
        Telugu = UnicodeTables._Telugu;
        Thaana = UnicodeTables._Thaana;
        Thai = UnicodeTables._Thai;
        Tibetan = UnicodeTables._Tibetan;
        Tifinagh = UnicodeTables._Tifinagh;
        Ugaritic = UnicodeTables._Ugaritic;
        Vai = UnicodeTables._Vai;
        Yi = UnicodeTables._Yi;
        _Pattern_Syntax = make_Pattern_Syntax();
        _Other_ID_Start = make_Other_ID_Start();
        _Pattern_White_Space = make_Pattern_White_Space();
        _Other_Lowercase = make_Other_Lowercase();
        _Soft_Dotted = make_Soft_Dotted();
        _Hex_Digit = make_Hex_Digit();
        _ASCII_Hex_Digit = make_ASCII_Hex_Digit();
        _Deprecated = make_Deprecated();
        _Terminal_Punctuation = make_Terminal_Punctuation();
        _Quotation_Mark = make_Quotation_Mark();
        _Other_ID_Continue = make_Other_ID_Continue();
        _Bidi_Control = make_Bidi_Control();
        _Variation_Selector = make_Variation_Selector();
        _Noncharacter_Code_Point = make_Noncharacter_Code_Point();
        _Other_Math = make_Other_Math();
        _Unified_Ideograph = make_Unified_Ideograph();
        _Hyphen = make_Hyphen();
        _IDS_Binary_Operator = make_IDS_Binary_Operator();
        _Logical_Order_Exception = make_Logical_Order_Exception();
        _Radical = make_Radical();
        _Other_Uppercase = make_Other_Uppercase();
        _STerm = make_STerm();
        _Other_Alphabetic = make_Other_Alphabetic();
        _Diacritic = make_Diacritic();
        _Extender = make_Extender();
        _Join_Control = make_Join_Control();
        _Ideographic = make_Ideographic();
        _Dash = make_Dash();
        _IDS_Trinary_Operator = make_IDS_Trinary_Operator();
        _Other_Grapheme_Extend = make_Other_Grapheme_Extend();
        _Other_Default_Ignorable_Code_Point = make_Other_Default_Ignorable_Code_Point();
        _White_Space = make_White_Space();
        ASCII_Hex_Digit = UnicodeTables._ASCII_Hex_Digit;
        Bidi_Control = UnicodeTables._Bidi_Control;
        Dash = UnicodeTables._Dash;
        Deprecated = UnicodeTables._Deprecated;
        Diacritic = UnicodeTables._Diacritic;
        Extender = UnicodeTables._Extender;
        Hex_Digit = UnicodeTables._Hex_Digit;
        Hyphen = UnicodeTables._Hyphen;
        IDS_Binary_Operator = UnicodeTables._IDS_Binary_Operator;
        IDS_Trinary_Operator = UnicodeTables._IDS_Trinary_Operator;
        Ideographic = UnicodeTables._Ideographic;
        Join_Control = UnicodeTables._Join_Control;
        Logical_Order_Exception = UnicodeTables._Logical_Order_Exception;
        Noncharacter_Code_Point = UnicodeTables._Noncharacter_Code_Point;
        Other_Alphabetic = UnicodeTables._Other_Alphabetic;
        Other_Default_Ignorable_Code_Point = UnicodeTables._Other_Default_Ignorable_Code_Point;
        Other_Grapheme_Extend = UnicodeTables._Other_Grapheme_Extend;
        Other_ID_Continue = UnicodeTables._Other_ID_Continue;
        Other_ID_Start = UnicodeTables._Other_ID_Start;
        Other_Lowercase = UnicodeTables._Other_Lowercase;
        Other_Math = UnicodeTables._Other_Math;
        Other_Uppercase = UnicodeTables._Other_Uppercase;
        Pattern_Syntax = UnicodeTables._Pattern_Syntax;
        Pattern_White_Space = UnicodeTables._Pattern_White_Space;
        Quotation_Mark = UnicodeTables._Quotation_Mark;
        Radical = UnicodeTables._Radical;
        STerm = UnicodeTables._STerm;
        Soft_Dotted = UnicodeTables._Soft_Dotted;
        Terminal_Punctuation = UnicodeTables._Terminal_Punctuation;
        Unified_Ideograph = UnicodeTables._Unified_Ideograph;
        Variation_Selector = UnicodeTables._Variation_Selector;
        White_Space = UnicodeTables._White_Space;
        CASE_RANGES = new int[][] { { 65, 90, 0, 32, 0 }, { 97, 122, -32, 0, -32 }, { 181, 181, 743, 0, 743 }, { 192, 214, 0, 32, 0 }, { 216, 222, 0, 32, 0 }, { 224, 246, -32, 0, -32 }, { 248, 254, -32, 0, -32 }, { 255, 255, 121, 0, 121 }, { 256, 303, 1114112, 1114112, 1114112 }, { 304, 304, 0, -199, 0 }, { 305, 305, -232, 0, -232 }, { 306, 311, 1114112, 1114112, 1114112 }, { 313, 328, 1114112, 1114112, 1114112 }, { 330, 375, 1114112, 1114112, 1114112 }, { 376, 376, 0, -121, 0 }, { 377, 382, 1114112, 1114112, 1114112 }, { 383, 383, -300, 0, -300 }, { 384, 384, 195, 0, 195 }, { 385, 385, 0, 210, 0 }, { 386, 389, 1114112, 1114112, 1114112 }, { 390, 390, 0, 206, 0 }, { 391, 392, 1114112, 1114112, 1114112 }, { 393, 394, 0, 205, 0 }, { 395, 396, 1114112, 1114112, 1114112 }, { 398, 398, 0, 79, 0 }, { 399, 399, 0, 202, 0 }, { 400, 400, 0, 203, 0 }, { 401, 402, 1114112, 1114112, 1114112 }, { 403, 403, 0, 205, 0 }, { 404, 404, 0, 207, 0 }, { 405, 405, 97, 0, 97 }, { 406, 406, 0, 211, 0 }, { 407, 407, 0, 209, 0 }, { 408, 409, 1114112, 1114112, 1114112 }, { 410, 410, 163, 0, 163 }, { 412, 412, 0, 211, 0 }, { 413, 413, 0, 213, 0 }, { 414, 414, 130, 0, 130 }, { 415, 415, 0, 214, 0 }, { 416, 421, 1114112, 1114112, 1114112 }, { 422, 422, 0, 218, 0 }, { 423, 424, 1114112, 1114112, 1114112 }, { 425, 425, 0, 218, 0 }, { 428, 429, 1114112, 1114112, 1114112 }, { 430, 430, 0, 218, 0 }, { 431, 432, 1114112, 1114112, 1114112 }, { 433, 434, 0, 217, 0 }, { 435, 438, 1114112, 1114112, 1114112 }, { 439, 439, 0, 219, 0 }, { 440, 441, 1114112, 1114112, 1114112 }, { 444, 445, 1114112, 1114112, 1114112 }, { 447, 447, 56, 0, 56 }, { 452, 452, 0, 2, 1 }, { 453, 453, -1, 1, 0 }, { 454, 454, -2, 0, -1 }, { 455, 455, 0, 2, 1 }, { 456, 456, -1, 1, 0 }, { 457, 457, -2, 0, -1 }, { 458, 458, 0, 2, 1 }, { 459, 459, -1, 1, 0 }, { 460, 460, -2, 0, -1 }, { 461, 476, 1114112, 1114112, 1114112 }, { 477, 477, -79, 0, -79 }, { 478, 495, 1114112, 1114112, 1114112 }, { 497, 497, 0, 2, 1 }, { 498, 498, -1, 1, 0 }, { 499, 499, -2, 0, -1 }, { 500, 501, 1114112, 1114112, 1114112 }, { 502, 502, 0, -97, 0 }, { 503, 503, 0, -56, 0 }, { 504, 543, 1114112, 1114112, 1114112 }, { 544, 544, 0, -130, 0 }, { 546, 563, 1114112, 1114112, 1114112 }, { 570, 570, 0, 10795, 0 }, { 571, 572, 1114112, 1114112, 1114112 }, { 573, 573, 0, -163, 0 }, { 574, 574, 0, 10792, 0 }, { 575, 576, 10815, 0, 10815 }, { 577, 578, 1114112, 1114112, 1114112 }, { 579, 579, 0, -195, 0 }, { 580, 580, 0, 69, 0 }, { 581, 581, 0, 71, 0 }, { 582, 591, 1114112, 1114112, 1114112 }, { 592, 592, 10783, 0, 10783 }, { 593, 593, 10780, 0, 10780 }, { 594, 594, 10782, 0, 10782 }, { 595, 595, -210, 0, -210 }, { 596, 596, -206, 0, -206 }, { 598, 599, -205, 0, -205 }, { 601, 601, -202, 0, -202 }, { 603, 603, -203, 0, -203 }, { 608, 608, -205, 0, -205 }, { 611, 611, -207, 0, -207 }, { 613, 613, 42280, 0, 42280 }, { 616, 616, -209, 0, -209 }, { 617, 617, -211, 0, -211 }, { 619, 619, 10743, 0, 10743 }, { 623, 623, -211, 0, -211 }, { 625, 625, 10749, 0, 10749 }, { 626, 626, -213, 0, -213 }, { 629, 629, -214, 0, -214 }, { 637, 637, 10727, 0, 10727 }, { 640, 640, -218, 0, -218 }, { 643, 643, -218, 0, -218 }, { 648, 648, -218, 0, -218 }, { 649, 649, -69, 0, -69 }, { 650, 651, -217, 0, -217 }, { 652, 652, -71, 0, -71 }, { 658, 658, -219, 0, -219 }, { 837, 837, 84, 0, 84 }, { 880, 883, 1114112, 1114112, 1114112 }, { 886, 887, 1114112, 1114112, 1114112 }, { 891, 893, 130, 0, 130 }, { 902, 902, 0, 38, 0 }, { 904, 906, 0, 37, 0 }, { 908, 908, 0, 64, 0 }, { 910, 911, 0, 63, 0 }, { 913, 929, 0, 32, 0 }, { 931, 939, 0, 32, 0 }, { 940, 940, -38, 0, -38 }, { 941, 943, -37, 0, -37 }, { 945, 961, -32, 0, -32 }, { 962, 962, -31, 0, -31 }, { 963, 971, -32, 0, -32 }, { 972, 972, -64, 0, -64 }, { 973, 974, -63, 0, -63 }, { 975, 975, 0, 8, 0 }, { 976, 976, -62, 0, -62 }, { 977, 977, -57, 0, -57 }, { 981, 981, -47, 0, -47 }, { 982, 982, -54, 0, -54 }, { 983, 983, -8, 0, -8 }, { 984, 1007, 1114112, 1114112, 1114112 }, { 1008, 1008, -86, 0, -86 }, { 1009, 1009, -80, 0, -80 }, { 1010, 1010, 7, 0, 7 }, { 1012, 1012, 0, -60, 0 }, { 1013, 1013, -96, 0, -96 }, { 1015, 1016, 1114112, 1114112, 1114112 }, { 1017, 1017, 0, -7, 0 }, { 1018, 1019, 1114112, 1114112, 1114112 }, { 1021, 1023, 0, -130, 0 }, { 1024, 1039, 0, 80, 0 }, { 1040, 1071, 0, 32, 0 }, { 1072, 1103, -32, 0, -32 }, { 1104, 1119, -80, 0, -80 }, { 1120, 1153, 1114112, 1114112, 1114112 }, { 1162, 1215, 1114112, 1114112, 1114112 }, { 1216, 1216, 0, 15, 0 }, { 1217, 1230, 1114112, 1114112, 1114112 }, { 1231, 1231, -15, 0, -15 }, { 1232, 1319, 1114112, 1114112, 1114112 }, { 1329, 1366, 0, 48, 0 }, { 1377, 1414, -48, 0, -48 }, { 4256, 4293, 0, 7264, 0 }, { 7545, 7545, 35332, 0, 35332 }, { 7549, 7549, 3814, 0, 3814 }, { 7680, 7829, 1114112, 1114112, 1114112 }, { 7835, 7835, -59, 0, -59 }, { 7838, 7838, 0, -7615, 0 }, { 7840, 7935, 1114112, 1114112, 1114112 }, { 7936, 7943, 8, 0, 8 }, { 7944, 7951, 0, -8, 0 }, { 7952, 7957, 8, 0, 8 }, { 7960, 7965, 0, -8, 0 }, { 7968, 7975, 8, 0, 8 }, { 7976, 7983, 0, -8, 0 }, { 7984, 7991, 8, 0, 8 }, { 7992, 7999, 0, -8, 0 }, { 8000, 8005, 8, 0, 8 }, { 8008, 8013, 0, -8, 0 }, { 8017, 8017, 8, 0, 8 }, { 8019, 8019, 8, 0, 8 }, { 8021, 8021, 8, 0, 8 }, { 8023, 8023, 8, 0, 8 }, { 8025, 8025, 0, -8, 0 }, { 8027, 8027, 0, -8, 0 }, { 8029, 8029, 0, -8, 0 }, { 8031, 8031, 0, -8, 0 }, { 8032, 8039, 8, 0, 8 }, { 8040, 8047, 0, -8, 0 }, { 8048, 8049, 74, 0, 74 }, { 8050, 8053, 86, 0, 86 }, { 8054, 8055, 100, 0, 100 }, { 8056, 8057, 128, 0, 128 }, { 8058, 8059, 112, 0, 112 }, { 8060, 8061, 126, 0, 126 }, { 8064, 8071, 8, 0, 8 }, { 8072, 8079, 0, -8, 0 }, { 8080, 8087, 8, 0, 8 }, { 8088, 8095, 0, -8, 0 }, { 8096, 8103, 8, 0, 8 }, { 8104, 8111, 0, -8, 0 }, { 8112, 8113, 8, 0, 8 }, { 8115, 8115, 9, 0, 9 }, { 8120, 8121, 0, -8, 0 }, { 8122, 8123, 0, -74, 0 }, { 8124, 8124, 0, -9, 0 }, { 8126, 8126, -7205, 0, -7205 }, { 8131, 8131, 9, 0, 9 }, { 8136, 8139, 0, -86, 0 }, { 8140, 8140, 0, -9, 0 }, { 8144, 8145, 8, 0, 8 }, { 8152, 8153, 0, -8, 0 }, { 8154, 8155, 0, -100, 0 }, { 8160, 8161, 8, 0, 8 }, { 8165, 8165, 7, 0, 7 }, { 8168, 8169, 0, -8, 0 }, { 8170, 8171, 0, -112, 0 }, { 8172, 8172, 0, -7, 0 }, { 8179, 8179, 9, 0, 9 }, { 8184, 8185, 0, -128, 0 }, { 8186, 8187, 0, -126, 0 }, { 8188, 8188, 0, -9, 0 }, { 8486, 8486, 0, -7517, 0 }, { 8490, 8490, 0, -8383, 0 }, { 8491, 8491, 0, -8262, 0 }, { 8498, 8498, 0, 28, 0 }, { 8526, 8526, -28, 0, -28 }, { 8544, 8559, 0, 16, 0 }, { 8560, 8575, -16, 0, -16 }, { 8579, 8580, 1114112, 1114112, 1114112 }, { 9398, 9423, 0, 26, 0 }, { 9424, 9449, -26, 0, -26 }, { 11264, 11310, 0, 48, 0 }, { 11312, 11358, -48, 0, -48 }, { 11360, 11361, 1114112, 1114112, 1114112 }, { 11362, 11362, 0, -10743, 0 }, { 11363, 11363, 0, -3814, 0 }, { 11364, 11364, 0, -10727, 0 }, { 11365, 11365, -10795, 0, -10795 }, { 11366, 11366, -10792, 0, -10792 }, { 11367, 11372, 1114112, 1114112, 1114112 }, { 11373, 11373, 0, -10780, 0 }, { 11374, 11374, 0, -10749, 0 }, { 11375, 11375, 0, -10783, 0 }, { 11376, 11376, 0, -10782, 0 }, { 11378, 11379, 1114112, 1114112, 1114112 }, { 11381, 11382, 1114112, 1114112, 1114112 }, { 11390, 11391, 0, -10815, 0 }, { 11392, 11491, 1114112, 1114112, 1114112 }, { 11499, 11502, 1114112, 1114112, 1114112 }, { 11520, 11557, -7264, 0, -7264 }, { 42560, 42605, 1114112, 1114112, 1114112 }, { 42624, 42647, 1114112, 1114112, 1114112 }, { 42786, 42799, 1114112, 1114112, 1114112 }, { 42802, 42863, 1114112, 1114112, 1114112 }, { 42873, 42876, 1114112, 1114112, 1114112 }, { 42877, 42877, 0, -35332, 0 }, { 42878, 42887, 1114112, 1114112, 1114112 }, { 42891, 42892, 1114112, 1114112, 1114112 }, { 42893, 42893, 0, -42280, 0 }, { 42896, 42897, 1114112, 1114112, 1114112 }, { 42912, 42921, 1114112, 1114112, 1114112 }, { 65313, 65338, 0, 32, 0 }, { 65345, 65370, -32, 0, -32 }, { 66560, 66599, 0, 40, 0 }, { 66600, 66639, -40, 0, -40 } };
        CASE_ORBIT = new int[][] { { 75, 107 }, { 83, 115 }, { 107, 8490 }, { 115, 383 }, { 181, 924 }, { 197, 229 }, { 223, 7838 }, { 229, 8491 }, { 304, 304 }, { 305, 305 }, { 383, 83 }, { 452, 453 }, { 453, 454 }, { 454, 452 }, { 455, 456 }, { 456, 457 }, { 457, 455 }, { 458, 459 }, { 459, 460 }, { 460, 458 }, { 497, 498 }, { 498, 499 }, { 499, 497 }, { 837, 921 }, { 914, 946 }, { 917, 949 }, { 920, 952 }, { 921, 953 }, { 922, 954 }, { 924, 956 }, { 928, 960 }, { 929, 961 }, { 931, 962 }, { 934, 966 }, { 937, 969 }, { 946, 976 }, { 949, 1013 }, { 952, 977 }, { 953, 8126 }, { 954, 1008 }, { 956, 181 }, { 960, 982 }, { 961, 1009 }, { 962, 963 }, { 963, 931 }, { 966, 981 }, { 969, 8486 }, { 976, 914 }, { 977, 1012 }, { 981, 934 }, { 982, 928 }, { 1008, 922 }, { 1009, 929 }, { 1012, 920 }, { 1013, 917 }, { 7776, 7777 }, { 7777, 7835 }, { 7835, 7776 }, { 7838, 223 }, { 8126, 837 }, { 8486, 937 }, { 8490, 75 }, { 8491, 197 } };
        foldLl = makefoldLl();
        foldInherited = makefoldInherited();
        foldM = makefoldM();
        foldL = makefoldL();
        foldMn = makefoldMn();
        foldCommon = makefoldCommon();
        foldGreek = makefoldGreek();
        foldLu = makefoldLu();
        foldLt = makefoldLt();
        CATEGORIES = Categories();
        SCRIPTS = Scripts();
        PROPERTIES = Properties();
        FOLD_CATEGORIES = FoldCategory();
        FOLD_SCRIPT = FoldScript();
    }
}
