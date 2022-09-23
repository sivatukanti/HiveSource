// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

public final class XmlChars
{
    static final int SIZE = 394;
    static final int[] sXml10StartChars;
    static final int[] sXml10Chars;
    
    private XmlChars() {
    }
    
    public static final boolean is10NameStartChar(final char c) {
        if (c <= '\u312c') {
            final int ix = c;
            return (XmlChars.sXml10StartChars[ix >> 5] & 1 << (ix & 0x1F)) != 0x0;
        }
        if (c < '\uac00') {
            return c >= '\u4e00' && c <= '\u9fa5';
        }
        return c <= '\ud7a3' || (c <= '\udbff' && c >= '\ud800');
    }
    
    public static final boolean is10NameChar(final char c) {
        if (c <= '\u312c') {
            final int ix = c;
            return (XmlChars.sXml10Chars[ix >> 5] & 1 << (ix & 0x1F)) != 0x0;
        }
        if (c < '\uac00') {
            return c >= '\u4e00' && c <= '\u9fa5';
        }
        return c <= '\ud7a3' || (c >= '\ud800' && c <= '\udfff');
    }
    
    public static final boolean is11NameStartChar(final char c) {
        if (c > '\u2fef') {
            if (c >= '\u3001') {
                if (c <= '\udbff') {
                    return true;
                }
                if (c >= '\uf900' && c <= '\ufffd') {
                    return c <= '\ufdcf' || c >= '\ufdf0';
                }
            }
            return false;
        }
        if (c < '\u0300') {
            return c >= '\u00c0' && c != '\u00d7' && c != '\u00f7';
        }
        if (c >= '\u2c00') {
            return true;
        }
        if (c < '\u0370' || c > '\u218f') {
            return false;
        }
        if (c < '\u2000') {
            return c != '\u037e';
        }
        if (c >= '\u2070') {
            return c <= '\u218f';
        }
        return c == '\u200c' || c == '\u200d';
    }
    
    public static final boolean is11NameChar(final char c) {
        if (c > '\u2fef') {
            if (c >= '\u3001') {
                if (c <= '\udfff') {
                    return true;
                }
                if (c >= '\uf900' && c <= '\ufffd') {
                    return c <= '\ufdcf' || c >= '\ufdf0';
                }
            }
            return false;
        }
        if (c < '\u2000') {
            return (c >= '\u00c0' && c != '\u037e') || c == '·';
        }
        return c >= '\u2c00' || (c >= '\u200c' && c <= '\u218f' && (c >= '\u2070' || c == '\u200c' || c == '\u200d' || c == '\u203f' || c == '\u2040'));
    }
    
    private static void SETBITS(final int[] array, int start, int end) {
        int bit1 = start & 0x1F;
        final int bit2 = end & 0x1F;
        start >>= 5;
        end >>= 5;
        if (start == end) {
            while (bit1 <= bit2) {
                final int n = start;
                array[n] |= 1 << bit1;
                ++bit1;
            }
        }
        else {
            for (int bit3 = bit1; bit3 <= 31; ++bit3) {
                final int n2 = start;
                array[n2] |= 1 << bit3;
            }
            while (++start < end) {
                array[start] = -1;
            }
            for (int bit3 = 0; bit3 <= bit2; ++bit3) {
                final int n3 = end;
                array[n3] |= 1 << bit3;
            }
        }
    }
    
    private static void SETBITS(final int[] array, final int point) {
        final int ix = point >> 5;
        final int bit = point & 0x1F;
        final int n = ix;
        array[n] |= 1 << bit;
    }
    
    static {
        SETBITS(sXml10StartChars = new int[394], 192, 214);
        SETBITS(XmlChars.sXml10StartChars, 216, 246);
        SETBITS(XmlChars.sXml10StartChars, 248, 255);
        SETBITS(XmlChars.sXml10StartChars, 256, 305);
        SETBITS(XmlChars.sXml10StartChars, 308, 318);
        SETBITS(XmlChars.sXml10StartChars, 321, 328);
        SETBITS(XmlChars.sXml10StartChars, 330, 382);
        SETBITS(XmlChars.sXml10StartChars, 384, 451);
        SETBITS(XmlChars.sXml10StartChars, 461, 496);
        SETBITS(XmlChars.sXml10StartChars, 500, 501);
        SETBITS(XmlChars.sXml10StartChars, 506, 535);
        SETBITS(XmlChars.sXml10StartChars, 592, 680);
        SETBITS(XmlChars.sXml10StartChars, 699, 705);
        SETBITS(XmlChars.sXml10StartChars, 902);
        SETBITS(XmlChars.sXml10StartChars, 904, 906);
        SETBITS(XmlChars.sXml10StartChars, 908);
        SETBITS(XmlChars.sXml10StartChars, 910, 929);
        SETBITS(XmlChars.sXml10StartChars, 931, 974);
        SETBITS(XmlChars.sXml10StartChars, 976, 982);
        SETBITS(XmlChars.sXml10StartChars, 986);
        SETBITS(XmlChars.sXml10StartChars, 988);
        SETBITS(XmlChars.sXml10StartChars, 990);
        SETBITS(XmlChars.sXml10StartChars, 992);
        SETBITS(XmlChars.sXml10StartChars, 994, 1011);
        SETBITS(XmlChars.sXml10StartChars, 1025, 1036);
        SETBITS(XmlChars.sXml10StartChars, 1038, 1103);
        SETBITS(XmlChars.sXml10StartChars, 1105, 1116);
        SETBITS(XmlChars.sXml10StartChars, 1118, 1153);
        SETBITS(XmlChars.sXml10StartChars, 1168, 1220);
        SETBITS(XmlChars.sXml10StartChars, 1223, 1224);
        SETBITS(XmlChars.sXml10StartChars, 1227, 1228);
        SETBITS(XmlChars.sXml10StartChars, 1232, 1259);
        SETBITS(XmlChars.sXml10StartChars, 1262, 1269);
        SETBITS(XmlChars.sXml10StartChars, 1272, 1273);
        SETBITS(XmlChars.sXml10StartChars, 1329, 1366);
        SETBITS(XmlChars.sXml10StartChars, 1369);
        SETBITS(XmlChars.sXml10StartChars, 1377, 1414);
        SETBITS(XmlChars.sXml10StartChars, 1488, 1514);
        SETBITS(XmlChars.sXml10StartChars, 1520, 1522);
        SETBITS(XmlChars.sXml10StartChars, 1569, 1594);
        SETBITS(XmlChars.sXml10StartChars, 1601, 1610);
        SETBITS(XmlChars.sXml10StartChars, 1649, 1719);
        SETBITS(XmlChars.sXml10StartChars, 1722, 1726);
        SETBITS(XmlChars.sXml10StartChars, 1728, 1742);
        SETBITS(XmlChars.sXml10StartChars, 1744, 1747);
        SETBITS(XmlChars.sXml10StartChars, 1749);
        SETBITS(XmlChars.sXml10StartChars, 1765, 1766);
        SETBITS(XmlChars.sXml10StartChars, 2309, 2361);
        SETBITS(XmlChars.sXml10StartChars, 2365);
        SETBITS(XmlChars.sXml10StartChars, 2392, 2401);
        SETBITS(XmlChars.sXml10StartChars, 2437, 2444);
        SETBITS(XmlChars.sXml10StartChars, 2447, 2448);
        SETBITS(XmlChars.sXml10StartChars, 2451, 2472);
        SETBITS(XmlChars.sXml10StartChars, 2474, 2480);
        SETBITS(XmlChars.sXml10StartChars, 2482);
        SETBITS(XmlChars.sXml10StartChars, 2486, 2489);
        SETBITS(XmlChars.sXml10StartChars, 2524);
        SETBITS(XmlChars.sXml10StartChars, 2525);
        SETBITS(XmlChars.sXml10StartChars, 2527, 2529);
        SETBITS(XmlChars.sXml10StartChars, 2544);
        SETBITS(XmlChars.sXml10StartChars, 2545);
        SETBITS(XmlChars.sXml10StartChars, 2565, 2570);
        SETBITS(XmlChars.sXml10StartChars, 2575);
        SETBITS(XmlChars.sXml10StartChars, 2576);
        SETBITS(XmlChars.sXml10StartChars, 2579, 2600);
        SETBITS(XmlChars.sXml10StartChars, 2602, 2608);
        SETBITS(XmlChars.sXml10StartChars, 2610);
        SETBITS(XmlChars.sXml10StartChars, 2611);
        SETBITS(XmlChars.sXml10StartChars, 2613);
        SETBITS(XmlChars.sXml10StartChars, 2614);
        SETBITS(XmlChars.sXml10StartChars, 2616);
        SETBITS(XmlChars.sXml10StartChars, 2617);
        SETBITS(XmlChars.sXml10StartChars, 2649, 2652);
        SETBITS(XmlChars.sXml10StartChars, 2654);
        SETBITS(XmlChars.sXml10StartChars, 2674, 2676);
        SETBITS(XmlChars.sXml10StartChars, 2693, 2699);
        SETBITS(XmlChars.sXml10StartChars, 2701);
        SETBITS(XmlChars.sXml10StartChars, 2703, 2705);
        SETBITS(XmlChars.sXml10StartChars, 2707, 2728);
        SETBITS(XmlChars.sXml10StartChars, 2730, 2736);
        SETBITS(XmlChars.sXml10StartChars, 2738, 2739);
        SETBITS(XmlChars.sXml10StartChars, 2741, 2745);
        SETBITS(XmlChars.sXml10StartChars, 2749);
        SETBITS(XmlChars.sXml10StartChars, 2784);
        SETBITS(XmlChars.sXml10StartChars, 2821, 2828);
        SETBITS(XmlChars.sXml10StartChars, 2831);
        SETBITS(XmlChars.sXml10StartChars, 2832);
        SETBITS(XmlChars.sXml10StartChars, 2835, 2856);
        SETBITS(XmlChars.sXml10StartChars, 2858, 2864);
        SETBITS(XmlChars.sXml10StartChars, 2866);
        SETBITS(XmlChars.sXml10StartChars, 2867);
        SETBITS(XmlChars.sXml10StartChars, 2870, 2873);
        SETBITS(XmlChars.sXml10StartChars, 2877);
        SETBITS(XmlChars.sXml10StartChars, 2908);
        SETBITS(XmlChars.sXml10StartChars, 2909);
        SETBITS(XmlChars.sXml10StartChars, 2911, 2913);
        SETBITS(XmlChars.sXml10StartChars, 2949, 2954);
        SETBITS(XmlChars.sXml10StartChars, 2958, 2960);
        SETBITS(XmlChars.sXml10StartChars, 2962, 2965);
        SETBITS(XmlChars.sXml10StartChars, 2969, 2970);
        SETBITS(XmlChars.sXml10StartChars, 2972);
        SETBITS(XmlChars.sXml10StartChars, 2974);
        SETBITS(XmlChars.sXml10StartChars, 2975);
        SETBITS(XmlChars.sXml10StartChars, 2979);
        SETBITS(XmlChars.sXml10StartChars, 2980);
        SETBITS(XmlChars.sXml10StartChars, 2984, 2986);
        SETBITS(XmlChars.sXml10StartChars, 2990, 2997);
        SETBITS(XmlChars.sXml10StartChars, 2999, 3001);
        SETBITS(XmlChars.sXml10StartChars, 3077, 3084);
        SETBITS(XmlChars.sXml10StartChars, 3086, 3088);
        SETBITS(XmlChars.sXml10StartChars, 3090, 3112);
        SETBITS(XmlChars.sXml10StartChars, 3114, 3123);
        SETBITS(XmlChars.sXml10StartChars, 3125, 3129);
        SETBITS(XmlChars.sXml10StartChars, 3168);
        SETBITS(XmlChars.sXml10StartChars, 3169);
        SETBITS(XmlChars.sXml10StartChars, 3205, 3212);
        SETBITS(XmlChars.sXml10StartChars, 3214, 3216);
        SETBITS(XmlChars.sXml10StartChars, 3218, 3240);
        SETBITS(XmlChars.sXml10StartChars, 3242, 3251);
        SETBITS(XmlChars.sXml10StartChars, 3253, 3257);
        SETBITS(XmlChars.sXml10StartChars, 3294);
        SETBITS(XmlChars.sXml10StartChars, 3296);
        SETBITS(XmlChars.sXml10StartChars, 3297);
        SETBITS(XmlChars.sXml10StartChars, 3333, 3340);
        SETBITS(XmlChars.sXml10StartChars, 3342, 3344);
        SETBITS(XmlChars.sXml10StartChars, 3346, 3368);
        SETBITS(XmlChars.sXml10StartChars, 3370, 3385);
        SETBITS(XmlChars.sXml10StartChars, 3424);
        SETBITS(XmlChars.sXml10StartChars, 3425);
        SETBITS(XmlChars.sXml10StartChars, 3585, 3630);
        SETBITS(XmlChars.sXml10StartChars, 3632);
        SETBITS(XmlChars.sXml10StartChars, 3634);
        SETBITS(XmlChars.sXml10StartChars, 3635);
        SETBITS(XmlChars.sXml10StartChars, 3648, 3653);
        SETBITS(XmlChars.sXml10StartChars, 3713);
        SETBITS(XmlChars.sXml10StartChars, 3714);
        SETBITS(XmlChars.sXml10StartChars, 3716);
        SETBITS(XmlChars.sXml10StartChars, 3719);
        SETBITS(XmlChars.sXml10StartChars, 3720);
        SETBITS(XmlChars.sXml10StartChars, 3722);
        SETBITS(XmlChars.sXml10StartChars, 3725);
        SETBITS(XmlChars.sXml10StartChars, 3732, 3735);
        SETBITS(XmlChars.sXml10StartChars, 3737, 3743);
        SETBITS(XmlChars.sXml10StartChars, 3745, 3747);
        SETBITS(XmlChars.sXml10StartChars, 3749);
        SETBITS(XmlChars.sXml10StartChars, 3751);
        SETBITS(XmlChars.sXml10StartChars, 3754);
        SETBITS(XmlChars.sXml10StartChars, 3755);
        SETBITS(XmlChars.sXml10StartChars, 3757);
        SETBITS(XmlChars.sXml10StartChars, 3758);
        SETBITS(XmlChars.sXml10StartChars, 3760);
        SETBITS(XmlChars.sXml10StartChars, 3762);
        SETBITS(XmlChars.sXml10StartChars, 3763);
        SETBITS(XmlChars.sXml10StartChars, 3773);
        SETBITS(XmlChars.sXml10StartChars, 3776, 3780);
        SETBITS(XmlChars.sXml10StartChars, 3904, 3911);
        SETBITS(XmlChars.sXml10StartChars, 3913, 3945);
        SETBITS(XmlChars.sXml10StartChars, 4256, 4293);
        SETBITS(XmlChars.sXml10StartChars, 4304, 4342);
        SETBITS(XmlChars.sXml10StartChars, 4352);
        SETBITS(XmlChars.sXml10StartChars, 4354, 4355);
        SETBITS(XmlChars.sXml10StartChars, 4357, 4359);
        SETBITS(XmlChars.sXml10StartChars, 4361);
        SETBITS(XmlChars.sXml10StartChars, 4363, 4364);
        SETBITS(XmlChars.sXml10StartChars, 4366, 4370);
        SETBITS(XmlChars.sXml10StartChars, 4412);
        SETBITS(XmlChars.sXml10StartChars, 4414);
        SETBITS(XmlChars.sXml10StartChars, 4416);
        SETBITS(XmlChars.sXml10StartChars, 4428);
        SETBITS(XmlChars.sXml10StartChars, 4430);
        SETBITS(XmlChars.sXml10StartChars, 4432);
        SETBITS(XmlChars.sXml10StartChars, 4436, 4437);
        SETBITS(XmlChars.sXml10StartChars, 4441);
        SETBITS(XmlChars.sXml10StartChars, 4447, 4449);
        SETBITS(XmlChars.sXml10StartChars, 4451);
        SETBITS(XmlChars.sXml10StartChars, 4453);
        SETBITS(XmlChars.sXml10StartChars, 4455);
        SETBITS(XmlChars.sXml10StartChars, 4457);
        SETBITS(XmlChars.sXml10StartChars, 4461, 4462);
        SETBITS(XmlChars.sXml10StartChars, 4466, 4467);
        SETBITS(XmlChars.sXml10StartChars, 4469);
        SETBITS(XmlChars.sXml10StartChars, 4510);
        SETBITS(XmlChars.sXml10StartChars, 4520);
        SETBITS(XmlChars.sXml10StartChars, 4523);
        SETBITS(XmlChars.sXml10StartChars, 4526, 4527);
        SETBITS(XmlChars.sXml10StartChars, 4535, 4536);
        SETBITS(XmlChars.sXml10StartChars, 4538);
        SETBITS(XmlChars.sXml10StartChars, 4540, 4546);
        SETBITS(XmlChars.sXml10StartChars, 4587);
        SETBITS(XmlChars.sXml10StartChars, 4592);
        SETBITS(XmlChars.sXml10StartChars, 4601);
        SETBITS(XmlChars.sXml10StartChars, 7680, 7835);
        SETBITS(XmlChars.sXml10StartChars, 7840, 7929);
        SETBITS(XmlChars.sXml10StartChars, 7936, 7957);
        SETBITS(XmlChars.sXml10StartChars, 7960, 7965);
        SETBITS(XmlChars.sXml10StartChars, 7968, 8005);
        SETBITS(XmlChars.sXml10StartChars, 8008, 8013);
        SETBITS(XmlChars.sXml10StartChars, 8016, 8023);
        SETBITS(XmlChars.sXml10StartChars, 8025);
        SETBITS(XmlChars.sXml10StartChars, 8027);
        SETBITS(XmlChars.sXml10StartChars, 8029);
        SETBITS(XmlChars.sXml10StartChars, 8031, 8061);
        SETBITS(XmlChars.sXml10StartChars, 8064, 8116);
        SETBITS(XmlChars.sXml10StartChars, 8118, 8124);
        SETBITS(XmlChars.sXml10StartChars, 8126);
        SETBITS(XmlChars.sXml10StartChars, 8130, 8132);
        SETBITS(XmlChars.sXml10StartChars, 8134, 8140);
        SETBITS(XmlChars.sXml10StartChars, 8144, 8147);
        SETBITS(XmlChars.sXml10StartChars, 8150, 8155);
        SETBITS(XmlChars.sXml10StartChars, 8160, 8172);
        SETBITS(XmlChars.sXml10StartChars, 8178, 8180);
        SETBITS(XmlChars.sXml10StartChars, 8182, 8188);
        SETBITS(XmlChars.sXml10StartChars, 8486);
        SETBITS(XmlChars.sXml10StartChars, 8490, 8491);
        SETBITS(XmlChars.sXml10StartChars, 8494);
        SETBITS(XmlChars.sXml10StartChars, 8576, 8578);
        SETBITS(XmlChars.sXml10StartChars, 12353, 12436);
        SETBITS(XmlChars.sXml10StartChars, 12449, 12538);
        SETBITS(XmlChars.sXml10StartChars, 12549, 12588);
        SETBITS(XmlChars.sXml10StartChars, 12295);
        SETBITS(XmlChars.sXml10StartChars, 12321, 12329);
        sXml10Chars = new int[394];
        System.arraycopy(XmlChars.sXml10StartChars, 0, XmlChars.sXml10Chars, 0, 394);
        SETBITS(XmlChars.sXml10Chars, 768, 837);
        SETBITS(XmlChars.sXml10Chars, 864, 865);
        SETBITS(XmlChars.sXml10Chars, 1155, 1158);
        SETBITS(XmlChars.sXml10Chars, 1425, 1441);
        SETBITS(XmlChars.sXml10Chars, 1443, 1465);
        SETBITS(XmlChars.sXml10Chars, 1467, 1469);
        SETBITS(XmlChars.sXml10Chars, 1471);
        SETBITS(XmlChars.sXml10Chars, 1473, 1474);
        SETBITS(XmlChars.sXml10Chars, 1476);
        SETBITS(XmlChars.sXml10Chars, 1611, 1618);
        SETBITS(XmlChars.sXml10Chars, 1648);
        SETBITS(XmlChars.sXml10Chars, 1750, 1756);
        SETBITS(XmlChars.sXml10Chars, 1757, 1759);
        SETBITS(XmlChars.sXml10Chars, 1760, 1764);
        SETBITS(XmlChars.sXml10Chars, 1767, 1768);
        SETBITS(XmlChars.sXml10Chars, 1770, 1773);
        SETBITS(XmlChars.sXml10Chars, 2305, 2307);
        SETBITS(XmlChars.sXml10Chars, 2364);
        SETBITS(XmlChars.sXml10Chars, 2366, 2380);
        SETBITS(XmlChars.sXml10Chars, 2381);
        SETBITS(XmlChars.sXml10Chars, 2385, 2388);
        SETBITS(XmlChars.sXml10Chars, 2402);
        SETBITS(XmlChars.sXml10Chars, 2403);
        SETBITS(XmlChars.sXml10Chars, 2433, 2435);
        SETBITS(XmlChars.sXml10Chars, 2492);
        SETBITS(XmlChars.sXml10Chars, 2494);
        SETBITS(XmlChars.sXml10Chars, 2495);
        SETBITS(XmlChars.sXml10Chars, 2496, 2500);
        SETBITS(XmlChars.sXml10Chars, 2503);
        SETBITS(XmlChars.sXml10Chars, 2504);
        SETBITS(XmlChars.sXml10Chars, 2507, 2509);
        SETBITS(XmlChars.sXml10Chars, 2519);
        SETBITS(XmlChars.sXml10Chars, 2530);
        SETBITS(XmlChars.sXml10Chars, 2531);
        SETBITS(XmlChars.sXml10Chars, 2562);
        SETBITS(XmlChars.sXml10Chars, 2620);
        SETBITS(XmlChars.sXml10Chars, 2622);
        SETBITS(XmlChars.sXml10Chars, 2623);
        SETBITS(XmlChars.sXml10Chars, 2624, 2626);
        SETBITS(XmlChars.sXml10Chars, 2631);
        SETBITS(XmlChars.sXml10Chars, 2632);
        SETBITS(XmlChars.sXml10Chars, 2635, 2637);
        SETBITS(XmlChars.sXml10Chars, 2672);
        SETBITS(XmlChars.sXml10Chars, 2673);
        SETBITS(XmlChars.sXml10Chars, 2689, 2691);
        SETBITS(XmlChars.sXml10Chars, 2748);
        SETBITS(XmlChars.sXml10Chars, 2750, 2757);
        SETBITS(XmlChars.sXml10Chars, 2759, 2761);
        SETBITS(XmlChars.sXml10Chars, 2763, 2765);
        SETBITS(XmlChars.sXml10Chars, 2817, 2819);
        SETBITS(XmlChars.sXml10Chars, 2876);
        SETBITS(XmlChars.sXml10Chars, 2878, 2883);
        SETBITS(XmlChars.sXml10Chars, 2887);
        SETBITS(XmlChars.sXml10Chars, 2888);
        SETBITS(XmlChars.sXml10Chars, 2891, 2893);
        SETBITS(XmlChars.sXml10Chars, 2902);
        SETBITS(XmlChars.sXml10Chars, 2903);
        SETBITS(XmlChars.sXml10Chars, 2946);
        SETBITS(XmlChars.sXml10Chars, 2947);
        SETBITS(XmlChars.sXml10Chars, 3006, 3010);
        SETBITS(XmlChars.sXml10Chars, 3014, 3016);
        SETBITS(XmlChars.sXml10Chars, 3018, 3021);
        SETBITS(XmlChars.sXml10Chars, 3031);
        SETBITS(XmlChars.sXml10Chars, 3073, 3075);
        SETBITS(XmlChars.sXml10Chars, 3134, 3140);
        SETBITS(XmlChars.sXml10Chars, 3142, 3144);
        SETBITS(XmlChars.sXml10Chars, 3146, 3149);
        SETBITS(XmlChars.sXml10Chars, 3157, 3158);
        SETBITS(XmlChars.sXml10Chars, 3202, 3203);
        SETBITS(XmlChars.sXml10Chars, 3262, 3268);
        SETBITS(XmlChars.sXml10Chars, 3270, 3272);
        SETBITS(XmlChars.sXml10Chars, 3274, 3277);
        SETBITS(XmlChars.sXml10Chars, 3285, 3286);
        SETBITS(XmlChars.sXml10Chars, 3330, 3331);
        SETBITS(XmlChars.sXml10Chars, 3390, 3395);
        SETBITS(XmlChars.sXml10Chars, 3398, 3400);
        SETBITS(XmlChars.sXml10Chars, 3402, 3405);
        SETBITS(XmlChars.sXml10Chars, 3415);
        SETBITS(XmlChars.sXml10Chars, 3633);
        SETBITS(XmlChars.sXml10Chars, 3636, 3642);
        SETBITS(XmlChars.sXml10Chars, 3655, 3662);
        SETBITS(XmlChars.sXml10Chars, 3761);
        SETBITS(XmlChars.sXml10Chars, 3764, 3769);
        SETBITS(XmlChars.sXml10Chars, 3771, 3772);
        SETBITS(XmlChars.sXml10Chars, 3784, 3789);
        SETBITS(XmlChars.sXml10Chars, 3864, 3865);
        SETBITS(XmlChars.sXml10Chars, 3893);
        SETBITS(XmlChars.sXml10Chars, 3895);
        SETBITS(XmlChars.sXml10Chars, 3897);
        SETBITS(XmlChars.sXml10Chars, 3902);
        SETBITS(XmlChars.sXml10Chars, 3903);
        SETBITS(XmlChars.sXml10Chars, 3953, 3972);
        SETBITS(XmlChars.sXml10Chars, 3974, 3979);
        SETBITS(XmlChars.sXml10Chars, 3984, 3989);
        SETBITS(XmlChars.sXml10Chars, 3991);
        SETBITS(XmlChars.sXml10Chars, 3993, 4013);
        SETBITS(XmlChars.sXml10Chars, 4017, 4023);
        SETBITS(XmlChars.sXml10Chars, 4025);
        SETBITS(XmlChars.sXml10Chars, 8400, 8412);
        SETBITS(XmlChars.sXml10Chars, 8417);
        SETBITS(XmlChars.sXml10Chars, 12330, 12335);
        SETBITS(XmlChars.sXml10Chars, 12441);
        SETBITS(XmlChars.sXml10Chars, 12442);
        SETBITS(XmlChars.sXml10Chars, 1632, 1641);
        SETBITS(XmlChars.sXml10Chars, 1776, 1785);
        SETBITS(XmlChars.sXml10Chars, 2406, 2415);
        SETBITS(XmlChars.sXml10Chars, 2534, 2543);
        SETBITS(XmlChars.sXml10Chars, 2662, 2671);
        SETBITS(XmlChars.sXml10Chars, 2790, 2799);
        SETBITS(XmlChars.sXml10Chars, 2918, 2927);
        SETBITS(XmlChars.sXml10Chars, 3047, 3055);
        SETBITS(XmlChars.sXml10Chars, 3174, 3183);
        SETBITS(XmlChars.sXml10Chars, 3302, 3311);
        SETBITS(XmlChars.sXml10Chars, 3430, 3439);
        SETBITS(XmlChars.sXml10Chars, 3664, 3673);
        SETBITS(XmlChars.sXml10Chars, 3792, 3801);
        SETBITS(XmlChars.sXml10Chars, 3872, 3881);
        SETBITS(XmlChars.sXml10Chars, 183);
        SETBITS(XmlChars.sXml10Chars, 720);
        SETBITS(XmlChars.sXml10Chars, 721);
        SETBITS(XmlChars.sXml10Chars, 903);
        SETBITS(XmlChars.sXml10Chars, 1600);
        SETBITS(XmlChars.sXml10Chars, 3654);
        SETBITS(XmlChars.sXml10Chars, 3782);
        SETBITS(XmlChars.sXml10Chars, 12293);
        SETBITS(XmlChars.sXml10Chars, 12337, 12341);
        SETBITS(XmlChars.sXml10Chars, 12445, 12446);
        SETBITS(XmlChars.sXml10Chars, 12540, 12542);
    }
}
