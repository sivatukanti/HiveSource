// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

public final class EBCDICCodec
{
    static final int[] sCodePage037;
    
    private EBCDICCodec() {
    }
    
    public static int[] getCp037Mapping() {
        return EBCDICCodec.sCodePage037;
    }
    
    static {
        final int[] ch = { 0, 1, 2, 3, 156, 9, 134, 127, 151, 141, 142, 11, 12, 13, 14, 15, 16, 17, 18, 19, 157, 133, 8, 135, 24, 25, 146, 143, 28, 29, 30, 31, 128, 129, 130, 131, 132, 10, 23, 27, 136, 137, 138, 139, 140, 5, 6, 7, 144, 145, 22, 147, 148, 149, 150, 4, 152, 153, 154, 155, 20, 21, 158, 26, 32, 160, 226, 228, 224, 225, 227, 229, 231, 241, 162, 46, 60, 40, 43, 124, 38, 233, 234, 235, 232, 237, 238, 239, 236, 223, 33, 36, 42, 41, 59, 172, 45, 47, 194, 196, 192, 193, 195, 197, 199, 209, 166, 44, 37, 95, 62, 63, 248, 201, 202, 203, 200, 205, 206, 207, 204, 96, 58, 35, 64, 39, 61, 34, 216, 97, 98, 99, 100, 101, 102, 103, 104, 105, 171, 187, 240, 253, 254, 177, 176, 106, 107, 108, 109, 110, 111, 112, 113, 114, 170, 186, 230, 184, 198, 164, 181, 126, 115, 116, 117, 118, 119, 120, 121, 122, 161, 191, 208, 221, 222, 174, 94, 163, 165, 183, 169, 167, 182, 188, 189, 190, 91, 93, 175, 168, 180, 215, 123, 65, 66, 67, 68, 69, 70, 71, 72, 73, 173, 244, 246, 242, 243, 245, 125, 74, 75, 76, 77, 78, 79, 80, 81, 82, 185, 251, 252, 249, 250, 255, 92, 247, 83, 84, 85, 86, 87, 88, 89, 90, 178, 212, 214, 210, 211, 213, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 179, 219, 220, 217, 218, 159 };
        for (int i = 0, len = ch.length; i < len; ++i) {
            final int c = ch[i];
            if (c >= 127 && c <= 159) {
                if (c == 133) {
                    ch[i] = 10;
                }
                else {
                    ch[i] = -c;
                }
            }
        }
        sCodePage037 = ch;
    }
}
