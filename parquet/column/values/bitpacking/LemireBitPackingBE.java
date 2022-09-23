// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

abstract class LemireBitPackingBE
{
    private static final IntPacker[] packers;
    public static final IntPackerFactory factory;
    
    static {
        (packers = new IntPacker[32])[0] = new Packer0();
        LemireBitPackingBE.packers[1] = new Packer1();
        LemireBitPackingBE.packers[2] = new Packer2();
        LemireBitPackingBE.packers[3] = new Packer3();
        LemireBitPackingBE.packers[4] = new Packer4();
        LemireBitPackingBE.packers[5] = new Packer5();
        LemireBitPackingBE.packers[6] = new Packer6();
        LemireBitPackingBE.packers[7] = new Packer7();
        LemireBitPackingBE.packers[8] = new Packer8();
        LemireBitPackingBE.packers[9] = new Packer9();
        LemireBitPackingBE.packers[10] = new Packer10();
        LemireBitPackingBE.packers[11] = new Packer11();
        LemireBitPackingBE.packers[12] = new Packer12();
        LemireBitPackingBE.packers[13] = new Packer13();
        LemireBitPackingBE.packers[14] = new Packer14();
        LemireBitPackingBE.packers[15] = new Packer15();
        LemireBitPackingBE.packers[16] = new Packer16();
        LemireBitPackingBE.packers[17] = new Packer17();
        LemireBitPackingBE.packers[18] = new Packer18();
        LemireBitPackingBE.packers[19] = new Packer19();
        LemireBitPackingBE.packers[20] = new Packer20();
        LemireBitPackingBE.packers[21] = new Packer21();
        LemireBitPackingBE.packers[22] = new Packer22();
        LemireBitPackingBE.packers[23] = new Packer23();
        LemireBitPackingBE.packers[24] = new Packer24();
        LemireBitPackingBE.packers[25] = new Packer25();
        LemireBitPackingBE.packers[26] = new Packer26();
        LemireBitPackingBE.packers[27] = new Packer27();
        LemireBitPackingBE.packers[28] = new Packer28();
        LemireBitPackingBE.packers[29] = new Packer29();
        LemireBitPackingBE.packers[30] = new Packer30();
        LemireBitPackingBE.packers[31] = new Packer31();
        factory = new IntPackerFactory() {
            @Override
            public IntPacker newIntPacker(final int bitWidth) {
                return LemireBitPackingBE.packers[bitWidth];
            }
        };
    }
    
    private static final class Packer0 extends IntPacker
    {
        private Packer0() {
            super(0);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
        }
    }
    
    private static final class Packer1 extends IntPacker
    {
        private Packer1() {
            super(1);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x1) << 31 | (in[1 + inPos] & 0x1) << 30 | (in[2 + inPos] & 0x1) << 29 | (in[3 + inPos] & 0x1) << 28 | (in[4 + inPos] & 0x1) << 27 | (in[5 + inPos] & 0x1) << 26 | (in[6 + inPos] & 0x1) << 25 | (in[7 + inPos] & 0x1) << 24 | (in[8 + inPos] & 0x1) << 23 | (in[9 + inPos] & 0x1) << 22 | (in[10 + inPos] & 0x1) << 21 | (in[11 + inPos] & 0x1) << 20 | (in[12 + inPos] & 0x1) << 19 | (in[13 + inPos] & 0x1) << 18 | (in[14 + inPos] & 0x1) << 17 | (in[15 + inPos] & 0x1) << 16 | (in[16 + inPos] & 0x1) << 15 | (in[17 + inPos] & 0x1) << 14 | (in[18 + inPos] & 0x1) << 13 | (in[19 + inPos] & 0x1) << 12 | (in[20 + inPos] & 0x1) << 11 | (in[21 + inPos] & 0x1) << 10 | (in[22 + inPos] & 0x1) << 9 | (in[23 + inPos] & 0x1) << 8 | (in[24 + inPos] & 0x1) << 7 | (in[25 + inPos] & 0x1) << 6 | (in[26 + inPos] & 0x1) << 5 | (in[27 + inPos] & 0x1) << 4 | (in[28 + inPos] & 0x1) << 3 | (in[29 + inPos] & 0x1) << 2 | (in[30 + inPos] & 0x1) << 1 | (in[31 + inPos] & 0x1) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 31 & 0x1);
            out[1 + outPos] = (in[0 + inPos] >>> 30 & 0x1);
            out[2 + outPos] = (in[0 + inPos] >>> 29 & 0x1);
            out[3 + outPos] = (in[0 + inPos] >>> 28 & 0x1);
            out[4 + outPos] = (in[0 + inPos] >>> 27 & 0x1);
            out[5 + outPos] = (in[0 + inPos] >>> 26 & 0x1);
            out[6 + outPos] = (in[0 + inPos] >>> 25 & 0x1);
            out[7 + outPos] = (in[0 + inPos] >>> 24 & 0x1);
            out[8 + outPos] = (in[0 + inPos] >>> 23 & 0x1);
            out[9 + outPos] = (in[0 + inPos] >>> 22 & 0x1);
            out[10 + outPos] = (in[0 + inPos] >>> 21 & 0x1);
            out[11 + outPos] = (in[0 + inPos] >>> 20 & 0x1);
            out[12 + outPos] = (in[0 + inPos] >>> 19 & 0x1);
            out[13 + outPos] = (in[0 + inPos] >>> 18 & 0x1);
            out[14 + outPos] = (in[0 + inPos] >>> 17 & 0x1);
            out[15 + outPos] = (in[0 + inPos] >>> 16 & 0x1);
            out[16 + outPos] = (in[0 + inPos] >>> 15 & 0x1);
            out[17 + outPos] = (in[0 + inPos] >>> 14 & 0x1);
            out[18 + outPos] = (in[0 + inPos] >>> 13 & 0x1);
            out[19 + outPos] = (in[0 + inPos] >>> 12 & 0x1);
            out[20 + outPos] = (in[0 + inPos] >>> 11 & 0x1);
            out[21 + outPos] = (in[0 + inPos] >>> 10 & 0x1);
            out[22 + outPos] = (in[0 + inPos] >>> 9 & 0x1);
            out[23 + outPos] = (in[0 + inPos] >>> 8 & 0x1);
            out[24 + outPos] = (in[0 + inPos] >>> 7 & 0x1);
            out[25 + outPos] = (in[0 + inPos] >>> 6 & 0x1);
            out[26 + outPos] = (in[0 + inPos] >>> 5 & 0x1);
            out[27 + outPos] = (in[0 + inPos] >>> 4 & 0x1);
            out[28 + outPos] = (in[0 + inPos] >>> 3 & 0x1);
            out[29 + outPos] = (in[0 + inPos] >>> 2 & 0x1);
            out[30 + outPos] = (in[0 + inPos] >>> 1 & 0x1);
            out[31 + outPos] = (in[0 + inPos] >>> 0 & 0x1);
        }
    }
    
    private static final class Packer2 extends IntPacker
    {
        private Packer2() {
            super(2);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x3) << 30 | (in[1 + inPos] & 0x3) << 28 | (in[2 + inPos] & 0x3) << 26 | (in[3 + inPos] & 0x3) << 24 | (in[4 + inPos] & 0x3) << 22 | (in[5 + inPos] & 0x3) << 20 | (in[6 + inPos] & 0x3) << 18 | (in[7 + inPos] & 0x3) << 16 | (in[8 + inPos] & 0x3) << 14 | (in[9 + inPos] & 0x3) << 12 | (in[10 + inPos] & 0x3) << 10 | (in[11 + inPos] & 0x3) << 8 | (in[12 + inPos] & 0x3) << 6 | (in[13 + inPos] & 0x3) << 4 | (in[14 + inPos] & 0x3) << 2 | (in[15 + inPos] & 0x3) << 0);
            out[1 + outPos] = ((in[16 + inPos] & 0x3) << 30 | (in[17 + inPos] & 0x3) << 28 | (in[18 + inPos] & 0x3) << 26 | (in[19 + inPos] & 0x3) << 24 | (in[20 + inPos] & 0x3) << 22 | (in[21 + inPos] & 0x3) << 20 | (in[22 + inPos] & 0x3) << 18 | (in[23 + inPos] & 0x3) << 16 | (in[24 + inPos] & 0x3) << 14 | (in[25 + inPos] & 0x3) << 12 | (in[26 + inPos] & 0x3) << 10 | (in[27 + inPos] & 0x3) << 8 | (in[28 + inPos] & 0x3) << 6 | (in[29 + inPos] & 0x3) << 4 | (in[30 + inPos] & 0x3) << 2 | (in[31 + inPos] & 0x3) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 30 & 0x3);
            out[1 + outPos] = (in[0 + inPos] >>> 28 & 0x3);
            out[2 + outPos] = (in[0 + inPos] >>> 26 & 0x3);
            out[3 + outPos] = (in[0 + inPos] >>> 24 & 0x3);
            out[4 + outPos] = (in[0 + inPos] >>> 22 & 0x3);
            out[5 + outPos] = (in[0 + inPos] >>> 20 & 0x3);
            out[6 + outPos] = (in[0 + inPos] >>> 18 & 0x3);
            out[7 + outPos] = (in[0 + inPos] >>> 16 & 0x3);
            out[8 + outPos] = (in[0 + inPos] >>> 14 & 0x3);
            out[9 + outPos] = (in[0 + inPos] >>> 12 & 0x3);
            out[10 + outPos] = (in[0 + inPos] >>> 10 & 0x3);
            out[11 + outPos] = (in[0 + inPos] >>> 8 & 0x3);
            out[12 + outPos] = (in[0 + inPos] >>> 6 & 0x3);
            out[13 + outPos] = (in[0 + inPos] >>> 4 & 0x3);
            out[14 + outPos] = (in[0 + inPos] >>> 2 & 0x3);
            out[15 + outPos] = (in[0 + inPos] >>> 0 & 0x3);
            out[16 + outPos] = (in[1 + inPos] >>> 30 & 0x3);
            out[17 + outPos] = (in[1 + inPos] >>> 28 & 0x3);
            out[18 + outPos] = (in[1 + inPos] >>> 26 & 0x3);
            out[19 + outPos] = (in[1 + inPos] >>> 24 & 0x3);
            out[20 + outPos] = (in[1 + inPos] >>> 22 & 0x3);
            out[21 + outPos] = (in[1 + inPos] >>> 20 & 0x3);
            out[22 + outPos] = (in[1 + inPos] >>> 18 & 0x3);
            out[23 + outPos] = (in[1 + inPos] >>> 16 & 0x3);
            out[24 + outPos] = (in[1 + inPos] >>> 14 & 0x3);
            out[25 + outPos] = (in[1 + inPos] >>> 12 & 0x3);
            out[26 + outPos] = (in[1 + inPos] >>> 10 & 0x3);
            out[27 + outPos] = (in[1 + inPos] >>> 8 & 0x3);
            out[28 + outPos] = (in[1 + inPos] >>> 6 & 0x3);
            out[29 + outPos] = (in[1 + inPos] >>> 4 & 0x3);
            out[30 + outPos] = (in[1 + inPos] >>> 2 & 0x3);
            out[31 + outPos] = (in[1 + inPos] >>> 0 & 0x3);
        }
    }
    
    private static final class Packer3 extends IntPacker
    {
        private Packer3() {
            super(3);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x7) << 29 | (in[1 + inPos] & 0x7) << 26 | (in[2 + inPos] & 0x7) << 23 | (in[3 + inPos] & 0x7) << 20 | (in[4 + inPos] & 0x7) << 17 | (in[5 + inPos] & 0x7) << 14 | (in[6 + inPos] & 0x7) << 11 | (in[7 + inPos] & 0x7) << 8 | (in[8 + inPos] & 0x7) << 5 | (in[9 + inPos] & 0x7) << 2 | (in[10 + inPos] & 0x7) >>> 1);
            out[1 + outPos] = ((in[10 + inPos] & 0x7) << 31 | (in[11 + inPos] & 0x7) << 28 | (in[12 + inPos] & 0x7) << 25 | (in[13 + inPos] & 0x7) << 22 | (in[14 + inPos] & 0x7) << 19 | (in[15 + inPos] & 0x7) << 16 | (in[16 + inPos] & 0x7) << 13 | (in[17 + inPos] & 0x7) << 10 | (in[18 + inPos] & 0x7) << 7 | (in[19 + inPos] & 0x7) << 4 | (in[20 + inPos] & 0x7) << 1 | (in[21 + inPos] & 0x7) >>> 2);
            out[2 + outPos] = ((in[21 + inPos] & 0x7) << 30 | (in[22 + inPos] & 0x7) << 27 | (in[23 + inPos] & 0x7) << 24 | (in[24 + inPos] & 0x7) << 21 | (in[25 + inPos] & 0x7) << 18 | (in[26 + inPos] & 0x7) << 15 | (in[27 + inPos] & 0x7) << 12 | (in[28 + inPos] & 0x7) << 9 | (in[29 + inPos] & 0x7) << 6 | (in[30 + inPos] & 0x7) << 3 | (in[31 + inPos] & 0x7) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 29 & 0x7);
            out[1 + outPos] = (in[0 + inPos] >>> 26 & 0x7);
            out[2 + outPos] = (in[0 + inPos] >>> 23 & 0x7);
            out[3 + outPos] = (in[0 + inPos] >>> 20 & 0x7);
            out[4 + outPos] = (in[0 + inPos] >>> 17 & 0x7);
            out[5 + outPos] = (in[0 + inPos] >>> 14 & 0x7);
            out[6 + outPos] = (in[0 + inPos] >>> 11 & 0x7);
            out[7 + outPos] = (in[0 + inPos] >>> 8 & 0x7);
            out[8 + outPos] = (in[0 + inPos] >>> 5 & 0x7);
            out[9 + outPos] = (in[0 + inPos] >>> 2 & 0x7);
            out[10 + outPos] = ((in[0 + inPos] << 1 & 0x7) | in[1 + inPos] >>> 31);
            out[11 + outPos] = (in[1 + inPos] >>> 28 & 0x7);
            out[12 + outPos] = (in[1 + inPos] >>> 25 & 0x7);
            out[13 + outPos] = (in[1 + inPos] >>> 22 & 0x7);
            out[14 + outPos] = (in[1 + inPos] >>> 19 & 0x7);
            out[15 + outPos] = (in[1 + inPos] >>> 16 & 0x7);
            out[16 + outPos] = (in[1 + inPos] >>> 13 & 0x7);
            out[17 + outPos] = (in[1 + inPos] >>> 10 & 0x7);
            out[18 + outPos] = (in[1 + inPos] >>> 7 & 0x7);
            out[19 + outPos] = (in[1 + inPos] >>> 4 & 0x7);
            out[20 + outPos] = (in[1 + inPos] >>> 1 & 0x7);
            out[21 + outPos] = ((in[1 + inPos] << 2 & 0x7) | in[2 + inPos] >>> 30);
            out[22 + outPos] = (in[2 + inPos] >>> 27 & 0x7);
            out[23 + outPos] = (in[2 + inPos] >>> 24 & 0x7);
            out[24 + outPos] = (in[2 + inPos] >>> 21 & 0x7);
            out[25 + outPos] = (in[2 + inPos] >>> 18 & 0x7);
            out[26 + outPos] = (in[2 + inPos] >>> 15 & 0x7);
            out[27 + outPos] = (in[2 + inPos] >>> 12 & 0x7);
            out[28 + outPos] = (in[2 + inPos] >>> 9 & 0x7);
            out[29 + outPos] = (in[2 + inPos] >>> 6 & 0x7);
            out[30 + outPos] = (in[2 + inPos] >>> 3 & 0x7);
            out[31 + outPos] = (in[2 + inPos] >>> 0 & 0x7);
        }
    }
    
    private static final class Packer4 extends IntPacker
    {
        private Packer4() {
            super(4);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xF) << 28 | (in[1 + inPos] & 0xF) << 24 | (in[2 + inPos] & 0xF) << 20 | (in[3 + inPos] & 0xF) << 16 | (in[4 + inPos] & 0xF) << 12 | (in[5 + inPos] & 0xF) << 8 | (in[6 + inPos] & 0xF) << 4 | (in[7 + inPos] & 0xF) << 0);
            out[1 + outPos] = ((in[8 + inPos] & 0xF) << 28 | (in[9 + inPos] & 0xF) << 24 | (in[10 + inPos] & 0xF) << 20 | (in[11 + inPos] & 0xF) << 16 | (in[12 + inPos] & 0xF) << 12 | (in[13 + inPos] & 0xF) << 8 | (in[14 + inPos] & 0xF) << 4 | (in[15 + inPos] & 0xF) << 0);
            out[2 + outPos] = ((in[16 + inPos] & 0xF) << 28 | (in[17 + inPos] & 0xF) << 24 | (in[18 + inPos] & 0xF) << 20 | (in[19 + inPos] & 0xF) << 16 | (in[20 + inPos] & 0xF) << 12 | (in[21 + inPos] & 0xF) << 8 | (in[22 + inPos] & 0xF) << 4 | (in[23 + inPos] & 0xF) << 0);
            out[3 + outPos] = ((in[24 + inPos] & 0xF) << 28 | (in[25 + inPos] & 0xF) << 24 | (in[26 + inPos] & 0xF) << 20 | (in[27 + inPos] & 0xF) << 16 | (in[28 + inPos] & 0xF) << 12 | (in[29 + inPos] & 0xF) << 8 | (in[30 + inPos] & 0xF) << 4 | (in[31 + inPos] & 0xF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 28 & 0xF);
            out[1 + outPos] = (in[0 + inPos] >>> 24 & 0xF);
            out[2 + outPos] = (in[0 + inPos] >>> 20 & 0xF);
            out[3 + outPos] = (in[0 + inPos] >>> 16 & 0xF);
            out[4 + outPos] = (in[0 + inPos] >>> 12 & 0xF);
            out[5 + outPos] = (in[0 + inPos] >>> 8 & 0xF);
            out[6 + outPos] = (in[0 + inPos] >>> 4 & 0xF);
            out[7 + outPos] = (in[0 + inPos] >>> 0 & 0xF);
            out[8 + outPos] = (in[1 + inPos] >>> 28 & 0xF);
            out[9 + outPos] = (in[1 + inPos] >>> 24 & 0xF);
            out[10 + outPos] = (in[1 + inPos] >>> 20 & 0xF);
            out[11 + outPos] = (in[1 + inPos] >>> 16 & 0xF);
            out[12 + outPos] = (in[1 + inPos] >>> 12 & 0xF);
            out[13 + outPos] = (in[1 + inPos] >>> 8 & 0xF);
            out[14 + outPos] = (in[1 + inPos] >>> 4 & 0xF);
            out[15 + outPos] = (in[1 + inPos] >>> 0 & 0xF);
            out[16 + outPos] = (in[2 + inPos] >>> 28 & 0xF);
            out[17 + outPos] = (in[2 + inPos] >>> 24 & 0xF);
            out[18 + outPos] = (in[2 + inPos] >>> 20 & 0xF);
            out[19 + outPos] = (in[2 + inPos] >>> 16 & 0xF);
            out[20 + outPos] = (in[2 + inPos] >>> 12 & 0xF);
            out[21 + outPos] = (in[2 + inPos] >>> 8 & 0xF);
            out[22 + outPos] = (in[2 + inPos] >>> 4 & 0xF);
            out[23 + outPos] = (in[2 + inPos] >>> 0 & 0xF);
            out[24 + outPos] = (in[3 + inPos] >>> 28 & 0xF);
            out[25 + outPos] = (in[3 + inPos] >>> 24 & 0xF);
            out[26 + outPos] = (in[3 + inPos] >>> 20 & 0xF);
            out[27 + outPos] = (in[3 + inPos] >>> 16 & 0xF);
            out[28 + outPos] = (in[3 + inPos] >>> 12 & 0xF);
            out[29 + outPos] = (in[3 + inPos] >>> 8 & 0xF);
            out[30 + outPos] = (in[3 + inPos] >>> 4 & 0xF);
            out[31 + outPos] = (in[3 + inPos] >>> 0 & 0xF);
        }
    }
    
    private static final class Packer5 extends IntPacker
    {
        private Packer5() {
            super(5);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x1F) << 27 | (in[1 + inPos] & 0x1F) << 22 | (in[2 + inPos] & 0x1F) << 17 | (in[3 + inPos] & 0x1F) << 12 | (in[4 + inPos] & 0x1F) << 7 | (in[5 + inPos] & 0x1F) << 2 | (in[6 + inPos] & 0x1F) >>> 3);
            out[1 + outPos] = ((in[6 + inPos] & 0x1F) << 29 | (in[7 + inPos] & 0x1F) << 24 | (in[8 + inPos] & 0x1F) << 19 | (in[9 + inPos] & 0x1F) << 14 | (in[10 + inPos] & 0x1F) << 9 | (in[11 + inPos] & 0x1F) << 4 | (in[12 + inPos] & 0x1F) >>> 1);
            out[2 + outPos] = ((in[12 + inPos] & 0x1F) << 31 | (in[13 + inPos] & 0x1F) << 26 | (in[14 + inPos] & 0x1F) << 21 | (in[15 + inPos] & 0x1F) << 16 | (in[16 + inPos] & 0x1F) << 11 | (in[17 + inPos] & 0x1F) << 6 | (in[18 + inPos] & 0x1F) << 1 | (in[19 + inPos] & 0x1F) >>> 4);
            out[3 + outPos] = ((in[19 + inPos] & 0x1F) << 28 | (in[20 + inPos] & 0x1F) << 23 | (in[21 + inPos] & 0x1F) << 18 | (in[22 + inPos] & 0x1F) << 13 | (in[23 + inPos] & 0x1F) << 8 | (in[24 + inPos] & 0x1F) << 3 | (in[25 + inPos] & 0x1F) >>> 2);
            out[4 + outPos] = ((in[25 + inPos] & 0x1F) << 30 | (in[26 + inPos] & 0x1F) << 25 | (in[27 + inPos] & 0x1F) << 20 | (in[28 + inPos] & 0x1F) << 15 | (in[29 + inPos] & 0x1F) << 10 | (in[30 + inPos] & 0x1F) << 5 | (in[31 + inPos] & 0x1F) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 27 & 0x1F);
            out[1 + outPos] = (in[0 + inPos] >>> 22 & 0x1F);
            out[2 + outPos] = (in[0 + inPos] >>> 17 & 0x1F);
            out[3 + outPos] = (in[0 + inPos] >>> 12 & 0x1F);
            out[4 + outPos] = (in[0 + inPos] >>> 7 & 0x1F);
            out[5 + outPos] = (in[0 + inPos] >>> 2 & 0x1F);
            out[6 + outPos] = ((in[0 + inPos] << 3 & 0x1F) | in[1 + inPos] >>> 29);
            out[7 + outPos] = (in[1 + inPos] >>> 24 & 0x1F);
            out[8 + outPos] = (in[1 + inPos] >>> 19 & 0x1F);
            out[9 + outPos] = (in[1 + inPos] >>> 14 & 0x1F);
            out[10 + outPos] = (in[1 + inPos] >>> 9 & 0x1F);
            out[11 + outPos] = (in[1 + inPos] >>> 4 & 0x1F);
            out[12 + outPos] = ((in[1 + inPos] << 1 & 0x1F) | in[2 + inPos] >>> 31);
            out[13 + outPos] = (in[2 + inPos] >>> 26 & 0x1F);
            out[14 + outPos] = (in[2 + inPos] >>> 21 & 0x1F);
            out[15 + outPos] = (in[2 + inPos] >>> 16 & 0x1F);
            out[16 + outPos] = (in[2 + inPos] >>> 11 & 0x1F);
            out[17 + outPos] = (in[2 + inPos] >>> 6 & 0x1F);
            out[18 + outPos] = (in[2 + inPos] >>> 1 & 0x1F);
            out[19 + outPos] = ((in[2 + inPos] << 4 & 0x1F) | in[3 + inPos] >>> 28);
            out[20 + outPos] = (in[3 + inPos] >>> 23 & 0x1F);
            out[21 + outPos] = (in[3 + inPos] >>> 18 & 0x1F);
            out[22 + outPos] = (in[3 + inPos] >>> 13 & 0x1F);
            out[23 + outPos] = (in[3 + inPos] >>> 8 & 0x1F);
            out[24 + outPos] = (in[3 + inPos] >>> 3 & 0x1F);
            out[25 + outPos] = ((in[3 + inPos] << 2 & 0x1F) | in[4 + inPos] >>> 30);
            out[26 + outPos] = (in[4 + inPos] >>> 25 & 0x1F);
            out[27 + outPos] = (in[4 + inPos] >>> 20 & 0x1F);
            out[28 + outPos] = (in[4 + inPos] >>> 15 & 0x1F);
            out[29 + outPos] = (in[4 + inPos] >>> 10 & 0x1F);
            out[30 + outPos] = (in[4 + inPos] >>> 5 & 0x1F);
            out[31 + outPos] = (in[4 + inPos] >>> 0 & 0x1F);
        }
    }
    
    private static final class Packer6 extends IntPacker
    {
        private Packer6() {
            super(6);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x3F) << 26 | (in[1 + inPos] & 0x3F) << 20 | (in[2 + inPos] & 0x3F) << 14 | (in[3 + inPos] & 0x3F) << 8 | (in[4 + inPos] & 0x3F) << 2 | (in[5 + inPos] & 0x3F) >>> 4);
            out[1 + outPos] = ((in[5 + inPos] & 0x3F) << 28 | (in[6 + inPos] & 0x3F) << 22 | (in[7 + inPos] & 0x3F) << 16 | (in[8 + inPos] & 0x3F) << 10 | (in[9 + inPos] & 0x3F) << 4 | (in[10 + inPos] & 0x3F) >>> 2);
            out[2 + outPos] = ((in[10 + inPos] & 0x3F) << 30 | (in[11 + inPos] & 0x3F) << 24 | (in[12 + inPos] & 0x3F) << 18 | (in[13 + inPos] & 0x3F) << 12 | (in[14 + inPos] & 0x3F) << 6 | (in[15 + inPos] & 0x3F) << 0);
            out[3 + outPos] = ((in[16 + inPos] & 0x3F) << 26 | (in[17 + inPos] & 0x3F) << 20 | (in[18 + inPos] & 0x3F) << 14 | (in[19 + inPos] & 0x3F) << 8 | (in[20 + inPos] & 0x3F) << 2 | (in[21 + inPos] & 0x3F) >>> 4);
            out[4 + outPos] = ((in[21 + inPos] & 0x3F) << 28 | (in[22 + inPos] & 0x3F) << 22 | (in[23 + inPos] & 0x3F) << 16 | (in[24 + inPos] & 0x3F) << 10 | (in[25 + inPos] & 0x3F) << 4 | (in[26 + inPos] & 0x3F) >>> 2);
            out[5 + outPos] = ((in[26 + inPos] & 0x3F) << 30 | (in[27 + inPos] & 0x3F) << 24 | (in[28 + inPos] & 0x3F) << 18 | (in[29 + inPos] & 0x3F) << 12 | (in[30 + inPos] & 0x3F) << 6 | (in[31 + inPos] & 0x3F) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 26 & 0x3F);
            out[1 + outPos] = (in[0 + inPos] >>> 20 & 0x3F);
            out[2 + outPos] = (in[0 + inPos] >>> 14 & 0x3F);
            out[3 + outPos] = (in[0 + inPos] >>> 8 & 0x3F);
            out[4 + outPos] = (in[0 + inPos] >>> 2 & 0x3F);
            out[5 + outPos] = ((in[0 + inPos] << 4 & 0x3F) | in[1 + inPos] >>> 28);
            out[6 + outPos] = (in[1 + inPos] >>> 22 & 0x3F);
            out[7 + outPos] = (in[1 + inPos] >>> 16 & 0x3F);
            out[8 + outPos] = (in[1 + inPos] >>> 10 & 0x3F);
            out[9 + outPos] = (in[1 + inPos] >>> 4 & 0x3F);
            out[10 + outPos] = ((in[1 + inPos] << 2 & 0x3F) | in[2 + inPos] >>> 30);
            out[11 + outPos] = (in[2 + inPos] >>> 24 & 0x3F);
            out[12 + outPos] = (in[2 + inPos] >>> 18 & 0x3F);
            out[13 + outPos] = (in[2 + inPos] >>> 12 & 0x3F);
            out[14 + outPos] = (in[2 + inPos] >>> 6 & 0x3F);
            out[15 + outPos] = (in[2 + inPos] >>> 0 & 0x3F);
            out[16 + outPos] = (in[3 + inPos] >>> 26 & 0x3F);
            out[17 + outPos] = (in[3 + inPos] >>> 20 & 0x3F);
            out[18 + outPos] = (in[3 + inPos] >>> 14 & 0x3F);
            out[19 + outPos] = (in[3 + inPos] >>> 8 & 0x3F);
            out[20 + outPos] = (in[3 + inPos] >>> 2 & 0x3F);
            out[21 + outPos] = ((in[3 + inPos] << 4 & 0x3F) | in[4 + inPos] >>> 28);
            out[22 + outPos] = (in[4 + inPos] >>> 22 & 0x3F);
            out[23 + outPos] = (in[4 + inPos] >>> 16 & 0x3F);
            out[24 + outPos] = (in[4 + inPos] >>> 10 & 0x3F);
            out[25 + outPos] = (in[4 + inPos] >>> 4 & 0x3F);
            out[26 + outPos] = ((in[4 + inPos] << 2 & 0x3F) | in[5 + inPos] >>> 30);
            out[27 + outPos] = (in[5 + inPos] >>> 24 & 0x3F);
            out[28 + outPos] = (in[5 + inPos] >>> 18 & 0x3F);
            out[29 + outPos] = (in[5 + inPos] >>> 12 & 0x3F);
            out[30 + outPos] = (in[5 + inPos] >>> 6 & 0x3F);
            out[31 + outPos] = (in[5 + inPos] >>> 0 & 0x3F);
        }
    }
    
    private static final class Packer7 extends IntPacker
    {
        private Packer7() {
            super(7);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x7F) << 25 | (in[1 + inPos] & 0x7F) << 18 | (in[2 + inPos] & 0x7F) << 11 | (in[3 + inPos] & 0x7F) << 4 | (in[4 + inPos] & 0x7F) >>> 3);
            out[1 + outPos] = ((in[4 + inPos] & 0x7F) << 29 | (in[5 + inPos] & 0x7F) << 22 | (in[6 + inPos] & 0x7F) << 15 | (in[7 + inPos] & 0x7F) << 8 | (in[8 + inPos] & 0x7F) << 1 | (in[9 + inPos] & 0x7F) >>> 6);
            out[2 + outPos] = ((in[9 + inPos] & 0x7F) << 26 | (in[10 + inPos] & 0x7F) << 19 | (in[11 + inPos] & 0x7F) << 12 | (in[12 + inPos] & 0x7F) << 5 | (in[13 + inPos] & 0x7F) >>> 2);
            out[3 + outPos] = ((in[13 + inPos] & 0x7F) << 30 | (in[14 + inPos] & 0x7F) << 23 | (in[15 + inPos] & 0x7F) << 16 | (in[16 + inPos] & 0x7F) << 9 | (in[17 + inPos] & 0x7F) << 2 | (in[18 + inPos] & 0x7F) >>> 5);
            out[4 + outPos] = ((in[18 + inPos] & 0x7F) << 27 | (in[19 + inPos] & 0x7F) << 20 | (in[20 + inPos] & 0x7F) << 13 | (in[21 + inPos] & 0x7F) << 6 | (in[22 + inPos] & 0x7F) >>> 1);
            out[5 + outPos] = ((in[22 + inPos] & 0x7F) << 31 | (in[23 + inPos] & 0x7F) << 24 | (in[24 + inPos] & 0x7F) << 17 | (in[25 + inPos] & 0x7F) << 10 | (in[26 + inPos] & 0x7F) << 3 | (in[27 + inPos] & 0x7F) >>> 4);
            out[6 + outPos] = ((in[27 + inPos] & 0x7F) << 28 | (in[28 + inPos] & 0x7F) << 21 | (in[29 + inPos] & 0x7F) << 14 | (in[30 + inPos] & 0x7F) << 7 | (in[31 + inPos] & 0x7F) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 25 & 0x7F);
            out[1 + outPos] = (in[0 + inPos] >>> 18 & 0x7F);
            out[2 + outPos] = (in[0 + inPos] >>> 11 & 0x7F);
            out[3 + outPos] = (in[0 + inPos] >>> 4 & 0x7F);
            out[4 + outPos] = ((in[0 + inPos] << 3 & 0x7F) | in[1 + inPos] >>> 29);
            out[5 + outPos] = (in[1 + inPos] >>> 22 & 0x7F);
            out[6 + outPos] = (in[1 + inPos] >>> 15 & 0x7F);
            out[7 + outPos] = (in[1 + inPos] >>> 8 & 0x7F);
            out[8 + outPos] = (in[1 + inPos] >>> 1 & 0x7F);
            out[9 + outPos] = ((in[1 + inPos] << 6 & 0x7F) | in[2 + inPos] >>> 26);
            out[10 + outPos] = (in[2 + inPos] >>> 19 & 0x7F);
            out[11 + outPos] = (in[2 + inPos] >>> 12 & 0x7F);
            out[12 + outPos] = (in[2 + inPos] >>> 5 & 0x7F);
            out[13 + outPos] = ((in[2 + inPos] << 2 & 0x7F) | in[3 + inPos] >>> 30);
            out[14 + outPos] = (in[3 + inPos] >>> 23 & 0x7F);
            out[15 + outPos] = (in[3 + inPos] >>> 16 & 0x7F);
            out[16 + outPos] = (in[3 + inPos] >>> 9 & 0x7F);
            out[17 + outPos] = (in[3 + inPos] >>> 2 & 0x7F);
            out[18 + outPos] = ((in[3 + inPos] << 5 & 0x7F) | in[4 + inPos] >>> 27);
            out[19 + outPos] = (in[4 + inPos] >>> 20 & 0x7F);
            out[20 + outPos] = (in[4 + inPos] >>> 13 & 0x7F);
            out[21 + outPos] = (in[4 + inPos] >>> 6 & 0x7F);
            out[22 + outPos] = ((in[4 + inPos] << 1 & 0x7F) | in[5 + inPos] >>> 31);
            out[23 + outPos] = (in[5 + inPos] >>> 24 & 0x7F);
            out[24 + outPos] = (in[5 + inPos] >>> 17 & 0x7F);
            out[25 + outPos] = (in[5 + inPos] >>> 10 & 0x7F);
            out[26 + outPos] = (in[5 + inPos] >>> 3 & 0x7F);
            out[27 + outPos] = ((in[5 + inPos] << 4 & 0x7F) | in[6 + inPos] >>> 28);
            out[28 + outPos] = (in[6 + inPos] >>> 21 & 0x7F);
            out[29 + outPos] = (in[6 + inPos] >>> 14 & 0x7F);
            out[30 + outPos] = (in[6 + inPos] >>> 7 & 0x7F);
            out[31 + outPos] = (in[6 + inPos] >>> 0 & 0x7F);
        }
    }
    
    private static final class Packer8 extends IntPacker
    {
        private Packer8() {
            super(8);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) << 24 | (in[1 + inPos] & 0xFF) << 16 | (in[2 + inPos] & 0xFF) << 8 | (in[3 + inPos] & 0xFF) << 0);
            out[1 + outPos] = ((in[4 + inPos] & 0xFF) << 24 | (in[5 + inPos] & 0xFF) << 16 | (in[6 + inPos] & 0xFF) << 8 | (in[7 + inPos] & 0xFF) << 0);
            out[2 + outPos] = ((in[8 + inPos] & 0xFF) << 24 | (in[9 + inPos] & 0xFF) << 16 | (in[10 + inPos] & 0xFF) << 8 | (in[11 + inPos] & 0xFF) << 0);
            out[3 + outPos] = ((in[12 + inPos] & 0xFF) << 24 | (in[13 + inPos] & 0xFF) << 16 | (in[14 + inPos] & 0xFF) << 8 | (in[15 + inPos] & 0xFF) << 0);
            out[4 + outPos] = ((in[16 + inPos] & 0xFF) << 24 | (in[17 + inPos] & 0xFF) << 16 | (in[18 + inPos] & 0xFF) << 8 | (in[19 + inPos] & 0xFF) << 0);
            out[5 + outPos] = ((in[20 + inPos] & 0xFF) << 24 | (in[21 + inPos] & 0xFF) << 16 | (in[22 + inPos] & 0xFF) << 8 | (in[23 + inPos] & 0xFF) << 0);
            out[6 + outPos] = ((in[24 + inPos] & 0xFF) << 24 | (in[25 + inPos] & 0xFF) << 16 | (in[26 + inPos] & 0xFF) << 8 | (in[27 + inPos] & 0xFF) << 0);
            out[7 + outPos] = ((in[28 + inPos] & 0xFF) << 24 | (in[29 + inPos] & 0xFF) << 16 | (in[30 + inPos] & 0xFF) << 8 | (in[31 + inPos] & 0xFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 24 & 0xFF);
            out[1 + outPos] = (in[0 + inPos] >>> 16 & 0xFF);
            out[2 + outPos] = (in[0 + inPos] >>> 8 & 0xFF);
            out[3 + outPos] = (in[0 + inPos] >>> 0 & 0xFF);
            out[4 + outPos] = (in[1 + inPos] >>> 24 & 0xFF);
            out[5 + outPos] = (in[1 + inPos] >>> 16 & 0xFF);
            out[6 + outPos] = (in[1 + inPos] >>> 8 & 0xFF);
            out[7 + outPos] = (in[1 + inPos] >>> 0 & 0xFF);
            out[8 + outPos] = (in[2 + inPos] >>> 24 & 0xFF);
            out[9 + outPos] = (in[2 + inPos] >>> 16 & 0xFF);
            out[10 + outPos] = (in[2 + inPos] >>> 8 & 0xFF);
            out[11 + outPos] = (in[2 + inPos] >>> 0 & 0xFF);
            out[12 + outPos] = (in[3 + inPos] >>> 24 & 0xFF);
            out[13 + outPos] = (in[3 + inPos] >>> 16 & 0xFF);
            out[14 + outPos] = (in[3 + inPos] >>> 8 & 0xFF);
            out[15 + outPos] = (in[3 + inPos] >>> 0 & 0xFF);
            out[16 + outPos] = (in[4 + inPos] >>> 24 & 0xFF);
            out[17 + outPos] = (in[4 + inPos] >>> 16 & 0xFF);
            out[18 + outPos] = (in[4 + inPos] >>> 8 & 0xFF);
            out[19 + outPos] = (in[4 + inPos] >>> 0 & 0xFF);
            out[20 + outPos] = (in[5 + inPos] >>> 24 & 0xFF);
            out[21 + outPos] = (in[5 + inPos] >>> 16 & 0xFF);
            out[22 + outPos] = (in[5 + inPos] >>> 8 & 0xFF);
            out[23 + outPos] = (in[5 + inPos] >>> 0 & 0xFF);
            out[24 + outPos] = (in[6 + inPos] >>> 24 & 0xFF);
            out[25 + outPos] = (in[6 + inPos] >>> 16 & 0xFF);
            out[26 + outPos] = (in[6 + inPos] >>> 8 & 0xFF);
            out[27 + outPos] = (in[6 + inPos] >>> 0 & 0xFF);
            out[28 + outPos] = (in[7 + inPos] >>> 24 & 0xFF);
            out[29 + outPos] = (in[7 + inPos] >>> 16 & 0xFF);
            out[30 + outPos] = (in[7 + inPos] >>> 8 & 0xFF);
            out[31 + outPos] = (in[7 + inPos] >>> 0 & 0xFF);
        }
    }
    
    private static final class Packer9 extends IntPacker
    {
        private Packer9() {
            super(9);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x1FF) << 23 | (in[1 + inPos] & 0x1FF) << 14 | (in[2 + inPos] & 0x1FF) << 5 | (in[3 + inPos] & 0x1FF) >>> 4);
            out[1 + outPos] = ((in[3 + inPos] & 0x1FF) << 28 | (in[4 + inPos] & 0x1FF) << 19 | (in[5 + inPos] & 0x1FF) << 10 | (in[6 + inPos] & 0x1FF) << 1 | (in[7 + inPos] & 0x1FF) >>> 8);
            out[2 + outPos] = ((in[7 + inPos] & 0x1FF) << 24 | (in[8 + inPos] & 0x1FF) << 15 | (in[9 + inPos] & 0x1FF) << 6 | (in[10 + inPos] & 0x1FF) >>> 3);
            out[3 + outPos] = ((in[10 + inPos] & 0x1FF) << 29 | (in[11 + inPos] & 0x1FF) << 20 | (in[12 + inPos] & 0x1FF) << 11 | (in[13 + inPos] & 0x1FF) << 2 | (in[14 + inPos] & 0x1FF) >>> 7);
            out[4 + outPos] = ((in[14 + inPos] & 0x1FF) << 25 | (in[15 + inPos] & 0x1FF) << 16 | (in[16 + inPos] & 0x1FF) << 7 | (in[17 + inPos] & 0x1FF) >>> 2);
            out[5 + outPos] = ((in[17 + inPos] & 0x1FF) << 30 | (in[18 + inPos] & 0x1FF) << 21 | (in[19 + inPos] & 0x1FF) << 12 | (in[20 + inPos] & 0x1FF) << 3 | (in[21 + inPos] & 0x1FF) >>> 6);
            out[6 + outPos] = ((in[21 + inPos] & 0x1FF) << 26 | (in[22 + inPos] & 0x1FF) << 17 | (in[23 + inPos] & 0x1FF) << 8 | (in[24 + inPos] & 0x1FF) >>> 1);
            out[7 + outPos] = ((in[24 + inPos] & 0x1FF) << 31 | (in[25 + inPos] & 0x1FF) << 22 | (in[26 + inPos] & 0x1FF) << 13 | (in[27 + inPos] & 0x1FF) << 4 | (in[28 + inPos] & 0x1FF) >>> 5);
            out[8 + outPos] = ((in[28 + inPos] & 0x1FF) << 27 | (in[29 + inPos] & 0x1FF) << 18 | (in[30 + inPos] & 0x1FF) << 9 | (in[31 + inPos] & 0x1FF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 23 & 0x1FF);
            out[1 + outPos] = (in[0 + inPos] >>> 14 & 0x1FF);
            out[2 + outPos] = (in[0 + inPos] >>> 5 & 0x1FF);
            out[3 + outPos] = ((in[0 + inPos] << 4 & 0x1FF) | in[1 + inPos] >>> 28);
            out[4 + outPos] = (in[1 + inPos] >>> 19 & 0x1FF);
            out[5 + outPos] = (in[1 + inPos] >>> 10 & 0x1FF);
            out[6 + outPos] = (in[1 + inPos] >>> 1 & 0x1FF);
            out[7 + outPos] = ((in[1 + inPos] << 8 & 0x1FF) | in[2 + inPos] >>> 24);
            out[8 + outPos] = (in[2 + inPos] >>> 15 & 0x1FF);
            out[9 + outPos] = (in[2 + inPos] >>> 6 & 0x1FF);
            out[10 + outPos] = ((in[2 + inPos] << 3 & 0x1FF) | in[3 + inPos] >>> 29);
            out[11 + outPos] = (in[3 + inPos] >>> 20 & 0x1FF);
            out[12 + outPos] = (in[3 + inPos] >>> 11 & 0x1FF);
            out[13 + outPos] = (in[3 + inPos] >>> 2 & 0x1FF);
            out[14 + outPos] = ((in[3 + inPos] << 7 & 0x1FF) | in[4 + inPos] >>> 25);
            out[15 + outPos] = (in[4 + inPos] >>> 16 & 0x1FF);
            out[16 + outPos] = (in[4 + inPos] >>> 7 & 0x1FF);
            out[17 + outPos] = ((in[4 + inPos] << 2 & 0x1FF) | in[5 + inPos] >>> 30);
            out[18 + outPos] = (in[5 + inPos] >>> 21 & 0x1FF);
            out[19 + outPos] = (in[5 + inPos] >>> 12 & 0x1FF);
            out[20 + outPos] = (in[5 + inPos] >>> 3 & 0x1FF);
            out[21 + outPos] = ((in[5 + inPos] << 6 & 0x1FF) | in[6 + inPos] >>> 26);
            out[22 + outPos] = (in[6 + inPos] >>> 17 & 0x1FF);
            out[23 + outPos] = (in[6 + inPos] >>> 8 & 0x1FF);
            out[24 + outPos] = ((in[6 + inPos] << 1 & 0x1FF) | in[7 + inPos] >>> 31);
            out[25 + outPos] = (in[7 + inPos] >>> 22 & 0x1FF);
            out[26 + outPos] = (in[7 + inPos] >>> 13 & 0x1FF);
            out[27 + outPos] = (in[7 + inPos] >>> 4 & 0x1FF);
            out[28 + outPos] = ((in[7 + inPos] << 5 & 0x1FF) | in[8 + inPos] >>> 27);
            out[29 + outPos] = (in[8 + inPos] >>> 18 & 0x1FF);
            out[30 + outPos] = (in[8 + inPos] >>> 9 & 0x1FF);
            out[31 + outPos] = (in[8 + inPos] >>> 0 & 0x1FF);
        }
    }
    
    private static final class Packer10 extends IntPacker
    {
        private Packer10() {
            super(10);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x3FF) << 22 | (in[1 + inPos] & 0x3FF) << 12 | (in[2 + inPos] & 0x3FF) << 2 | (in[3 + inPos] & 0x3FF) >>> 8);
            out[1 + outPos] = ((in[3 + inPos] & 0x3FF) << 24 | (in[4 + inPos] & 0x3FF) << 14 | (in[5 + inPos] & 0x3FF) << 4 | (in[6 + inPos] & 0x3FF) >>> 6);
            out[2 + outPos] = ((in[6 + inPos] & 0x3FF) << 26 | (in[7 + inPos] & 0x3FF) << 16 | (in[8 + inPos] & 0x3FF) << 6 | (in[9 + inPos] & 0x3FF) >>> 4);
            out[3 + outPos] = ((in[9 + inPos] & 0x3FF) << 28 | (in[10 + inPos] & 0x3FF) << 18 | (in[11 + inPos] & 0x3FF) << 8 | (in[12 + inPos] & 0x3FF) >>> 2);
            out[4 + outPos] = ((in[12 + inPos] & 0x3FF) << 30 | (in[13 + inPos] & 0x3FF) << 20 | (in[14 + inPos] & 0x3FF) << 10 | (in[15 + inPos] & 0x3FF) << 0);
            out[5 + outPos] = ((in[16 + inPos] & 0x3FF) << 22 | (in[17 + inPos] & 0x3FF) << 12 | (in[18 + inPos] & 0x3FF) << 2 | (in[19 + inPos] & 0x3FF) >>> 8);
            out[6 + outPos] = ((in[19 + inPos] & 0x3FF) << 24 | (in[20 + inPos] & 0x3FF) << 14 | (in[21 + inPos] & 0x3FF) << 4 | (in[22 + inPos] & 0x3FF) >>> 6);
            out[7 + outPos] = ((in[22 + inPos] & 0x3FF) << 26 | (in[23 + inPos] & 0x3FF) << 16 | (in[24 + inPos] & 0x3FF) << 6 | (in[25 + inPos] & 0x3FF) >>> 4);
            out[8 + outPos] = ((in[25 + inPos] & 0x3FF) << 28 | (in[26 + inPos] & 0x3FF) << 18 | (in[27 + inPos] & 0x3FF) << 8 | (in[28 + inPos] & 0x3FF) >>> 2);
            out[9 + outPos] = ((in[28 + inPos] & 0x3FF) << 30 | (in[29 + inPos] & 0x3FF) << 20 | (in[30 + inPos] & 0x3FF) << 10 | (in[31 + inPos] & 0x3FF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 22 & 0x3FF);
            out[1 + outPos] = (in[0 + inPos] >>> 12 & 0x3FF);
            out[2 + outPos] = (in[0 + inPos] >>> 2 & 0x3FF);
            out[3 + outPos] = ((in[0 + inPos] << 8 & 0x3FF) | in[1 + inPos] >>> 24);
            out[4 + outPos] = (in[1 + inPos] >>> 14 & 0x3FF);
            out[5 + outPos] = (in[1 + inPos] >>> 4 & 0x3FF);
            out[6 + outPos] = ((in[1 + inPos] << 6 & 0x3FF) | in[2 + inPos] >>> 26);
            out[7 + outPos] = (in[2 + inPos] >>> 16 & 0x3FF);
            out[8 + outPos] = (in[2 + inPos] >>> 6 & 0x3FF);
            out[9 + outPos] = ((in[2 + inPos] << 4 & 0x3FF) | in[3 + inPos] >>> 28);
            out[10 + outPos] = (in[3 + inPos] >>> 18 & 0x3FF);
            out[11 + outPos] = (in[3 + inPos] >>> 8 & 0x3FF);
            out[12 + outPos] = ((in[3 + inPos] << 2 & 0x3FF) | in[4 + inPos] >>> 30);
            out[13 + outPos] = (in[4 + inPos] >>> 20 & 0x3FF);
            out[14 + outPos] = (in[4 + inPos] >>> 10 & 0x3FF);
            out[15 + outPos] = (in[4 + inPos] >>> 0 & 0x3FF);
            out[16 + outPos] = (in[5 + inPos] >>> 22 & 0x3FF);
            out[17 + outPos] = (in[5 + inPos] >>> 12 & 0x3FF);
            out[18 + outPos] = (in[5 + inPos] >>> 2 & 0x3FF);
            out[19 + outPos] = ((in[5 + inPos] << 8 & 0x3FF) | in[6 + inPos] >>> 24);
            out[20 + outPos] = (in[6 + inPos] >>> 14 & 0x3FF);
            out[21 + outPos] = (in[6 + inPos] >>> 4 & 0x3FF);
            out[22 + outPos] = ((in[6 + inPos] << 6 & 0x3FF) | in[7 + inPos] >>> 26);
            out[23 + outPos] = (in[7 + inPos] >>> 16 & 0x3FF);
            out[24 + outPos] = (in[7 + inPos] >>> 6 & 0x3FF);
            out[25 + outPos] = ((in[7 + inPos] << 4 & 0x3FF) | in[8 + inPos] >>> 28);
            out[26 + outPos] = (in[8 + inPos] >>> 18 & 0x3FF);
            out[27 + outPos] = (in[8 + inPos] >>> 8 & 0x3FF);
            out[28 + outPos] = ((in[8 + inPos] << 2 & 0x3FF) | in[9 + inPos] >>> 30);
            out[29 + outPos] = (in[9 + inPos] >>> 20 & 0x3FF);
            out[30 + outPos] = (in[9 + inPos] >>> 10 & 0x3FF);
            out[31 + outPos] = (in[9 + inPos] >>> 0 & 0x3FF);
        }
    }
    
    private static final class Packer11 extends IntPacker
    {
        private Packer11() {
            super(11);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x7FF) << 21 | (in[1 + inPos] & 0x7FF) << 10 | (in[2 + inPos] & 0x7FF) >>> 1);
            out[1 + outPos] = ((in[2 + inPos] & 0x7FF) << 31 | (in[3 + inPos] & 0x7FF) << 20 | (in[4 + inPos] & 0x7FF) << 9 | (in[5 + inPos] & 0x7FF) >>> 2);
            out[2 + outPos] = ((in[5 + inPos] & 0x7FF) << 30 | (in[6 + inPos] & 0x7FF) << 19 | (in[7 + inPos] & 0x7FF) << 8 | (in[8 + inPos] & 0x7FF) >>> 3);
            out[3 + outPos] = ((in[8 + inPos] & 0x7FF) << 29 | (in[9 + inPos] & 0x7FF) << 18 | (in[10 + inPos] & 0x7FF) << 7 | (in[11 + inPos] & 0x7FF) >>> 4);
            out[4 + outPos] = ((in[11 + inPos] & 0x7FF) << 28 | (in[12 + inPos] & 0x7FF) << 17 | (in[13 + inPos] & 0x7FF) << 6 | (in[14 + inPos] & 0x7FF) >>> 5);
            out[5 + outPos] = ((in[14 + inPos] & 0x7FF) << 27 | (in[15 + inPos] & 0x7FF) << 16 | (in[16 + inPos] & 0x7FF) << 5 | (in[17 + inPos] & 0x7FF) >>> 6);
            out[6 + outPos] = ((in[17 + inPos] & 0x7FF) << 26 | (in[18 + inPos] & 0x7FF) << 15 | (in[19 + inPos] & 0x7FF) << 4 | (in[20 + inPos] & 0x7FF) >>> 7);
            out[7 + outPos] = ((in[20 + inPos] & 0x7FF) << 25 | (in[21 + inPos] & 0x7FF) << 14 | (in[22 + inPos] & 0x7FF) << 3 | (in[23 + inPos] & 0x7FF) >>> 8);
            out[8 + outPos] = ((in[23 + inPos] & 0x7FF) << 24 | (in[24 + inPos] & 0x7FF) << 13 | (in[25 + inPos] & 0x7FF) << 2 | (in[26 + inPos] & 0x7FF) >>> 9);
            out[9 + outPos] = ((in[26 + inPos] & 0x7FF) << 23 | (in[27 + inPos] & 0x7FF) << 12 | (in[28 + inPos] & 0x7FF) << 1 | (in[29 + inPos] & 0x7FF) >>> 10);
            out[10 + outPos] = ((in[29 + inPos] & 0x7FF) << 22 | (in[30 + inPos] & 0x7FF) << 11 | (in[31 + inPos] & 0x7FF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 21 & 0x7FF);
            out[1 + outPos] = (in[0 + inPos] >>> 10 & 0x7FF);
            out[2 + outPos] = ((in[0 + inPos] << 1 & 0x7FF) | in[1 + inPos] >>> 31);
            out[3 + outPos] = (in[1 + inPos] >>> 20 & 0x7FF);
            out[4 + outPos] = (in[1 + inPos] >>> 9 & 0x7FF);
            out[5 + outPos] = ((in[1 + inPos] << 2 & 0x7FF) | in[2 + inPos] >>> 30);
            out[6 + outPos] = (in[2 + inPos] >>> 19 & 0x7FF);
            out[7 + outPos] = (in[2 + inPos] >>> 8 & 0x7FF);
            out[8 + outPos] = ((in[2 + inPos] << 3 & 0x7FF) | in[3 + inPos] >>> 29);
            out[9 + outPos] = (in[3 + inPos] >>> 18 & 0x7FF);
            out[10 + outPos] = (in[3 + inPos] >>> 7 & 0x7FF);
            out[11 + outPos] = ((in[3 + inPos] << 4 & 0x7FF) | in[4 + inPos] >>> 28);
            out[12 + outPos] = (in[4 + inPos] >>> 17 & 0x7FF);
            out[13 + outPos] = (in[4 + inPos] >>> 6 & 0x7FF);
            out[14 + outPos] = ((in[4 + inPos] << 5 & 0x7FF) | in[5 + inPos] >>> 27);
            out[15 + outPos] = (in[5 + inPos] >>> 16 & 0x7FF);
            out[16 + outPos] = (in[5 + inPos] >>> 5 & 0x7FF);
            out[17 + outPos] = ((in[5 + inPos] << 6 & 0x7FF) | in[6 + inPos] >>> 26);
            out[18 + outPos] = (in[6 + inPos] >>> 15 & 0x7FF);
            out[19 + outPos] = (in[6 + inPos] >>> 4 & 0x7FF);
            out[20 + outPos] = ((in[6 + inPos] << 7 & 0x7FF) | in[7 + inPos] >>> 25);
            out[21 + outPos] = (in[7 + inPos] >>> 14 & 0x7FF);
            out[22 + outPos] = (in[7 + inPos] >>> 3 & 0x7FF);
            out[23 + outPos] = ((in[7 + inPos] << 8 & 0x7FF) | in[8 + inPos] >>> 24);
            out[24 + outPos] = (in[8 + inPos] >>> 13 & 0x7FF);
            out[25 + outPos] = (in[8 + inPos] >>> 2 & 0x7FF);
            out[26 + outPos] = ((in[8 + inPos] << 9 & 0x7FF) | in[9 + inPos] >>> 23);
            out[27 + outPos] = (in[9 + inPos] >>> 12 & 0x7FF);
            out[28 + outPos] = (in[9 + inPos] >>> 1 & 0x7FF);
            out[29 + outPos] = ((in[9 + inPos] << 10 & 0x7FF) | in[10 + inPos] >>> 22);
            out[30 + outPos] = (in[10 + inPos] >>> 11 & 0x7FF);
            out[31 + outPos] = (in[10 + inPos] >>> 0 & 0x7FF);
        }
    }
    
    private static final class Packer12 extends IntPacker
    {
        private Packer12() {
            super(12);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFFF) << 20 | (in[1 + inPos] & 0xFFF) << 8 | (in[2 + inPos] & 0xFFF) >>> 4);
            out[1 + outPos] = ((in[2 + inPos] & 0xFFF) << 28 | (in[3 + inPos] & 0xFFF) << 16 | (in[4 + inPos] & 0xFFF) << 4 | (in[5 + inPos] & 0xFFF) >>> 8);
            out[2 + outPos] = ((in[5 + inPos] & 0xFFF) << 24 | (in[6 + inPos] & 0xFFF) << 12 | (in[7 + inPos] & 0xFFF) << 0);
            out[3 + outPos] = ((in[8 + inPos] & 0xFFF) << 20 | (in[9 + inPos] & 0xFFF) << 8 | (in[10 + inPos] & 0xFFF) >>> 4);
            out[4 + outPos] = ((in[10 + inPos] & 0xFFF) << 28 | (in[11 + inPos] & 0xFFF) << 16 | (in[12 + inPos] & 0xFFF) << 4 | (in[13 + inPos] & 0xFFF) >>> 8);
            out[5 + outPos] = ((in[13 + inPos] & 0xFFF) << 24 | (in[14 + inPos] & 0xFFF) << 12 | (in[15 + inPos] & 0xFFF) << 0);
            out[6 + outPos] = ((in[16 + inPos] & 0xFFF) << 20 | (in[17 + inPos] & 0xFFF) << 8 | (in[18 + inPos] & 0xFFF) >>> 4);
            out[7 + outPos] = ((in[18 + inPos] & 0xFFF) << 28 | (in[19 + inPos] & 0xFFF) << 16 | (in[20 + inPos] & 0xFFF) << 4 | (in[21 + inPos] & 0xFFF) >>> 8);
            out[8 + outPos] = ((in[21 + inPos] & 0xFFF) << 24 | (in[22 + inPos] & 0xFFF) << 12 | (in[23 + inPos] & 0xFFF) << 0);
            out[9 + outPos] = ((in[24 + inPos] & 0xFFF) << 20 | (in[25 + inPos] & 0xFFF) << 8 | (in[26 + inPos] & 0xFFF) >>> 4);
            out[10 + outPos] = ((in[26 + inPos] & 0xFFF) << 28 | (in[27 + inPos] & 0xFFF) << 16 | (in[28 + inPos] & 0xFFF) << 4 | (in[29 + inPos] & 0xFFF) >>> 8);
            out[11 + outPos] = ((in[29 + inPos] & 0xFFF) << 24 | (in[30 + inPos] & 0xFFF) << 12 | (in[31 + inPos] & 0xFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 20 & 0xFFF);
            out[1 + outPos] = (in[0 + inPos] >>> 8 & 0xFFF);
            out[2 + outPos] = ((in[0 + inPos] << 4 & 0xFFF) | in[1 + inPos] >>> 28);
            out[3 + outPos] = (in[1 + inPos] >>> 16 & 0xFFF);
            out[4 + outPos] = (in[1 + inPos] >>> 4 & 0xFFF);
            out[5 + outPos] = ((in[1 + inPos] << 8 & 0xFFF) | in[2 + inPos] >>> 24);
            out[6 + outPos] = (in[2 + inPos] >>> 12 & 0xFFF);
            out[7 + outPos] = (in[2 + inPos] >>> 0 & 0xFFF);
            out[8 + outPos] = (in[3 + inPos] >>> 20 & 0xFFF);
            out[9 + outPos] = (in[3 + inPos] >>> 8 & 0xFFF);
            out[10 + outPos] = ((in[3 + inPos] << 4 & 0xFFF) | in[4 + inPos] >>> 28);
            out[11 + outPos] = (in[4 + inPos] >>> 16 & 0xFFF);
            out[12 + outPos] = (in[4 + inPos] >>> 4 & 0xFFF);
            out[13 + outPos] = ((in[4 + inPos] << 8 & 0xFFF) | in[5 + inPos] >>> 24);
            out[14 + outPos] = (in[5 + inPos] >>> 12 & 0xFFF);
            out[15 + outPos] = (in[5 + inPos] >>> 0 & 0xFFF);
            out[16 + outPos] = (in[6 + inPos] >>> 20 & 0xFFF);
            out[17 + outPos] = (in[6 + inPos] >>> 8 & 0xFFF);
            out[18 + outPos] = ((in[6 + inPos] << 4 & 0xFFF) | in[7 + inPos] >>> 28);
            out[19 + outPos] = (in[7 + inPos] >>> 16 & 0xFFF);
            out[20 + outPos] = (in[7 + inPos] >>> 4 & 0xFFF);
            out[21 + outPos] = ((in[7 + inPos] << 8 & 0xFFF) | in[8 + inPos] >>> 24);
            out[22 + outPos] = (in[8 + inPos] >>> 12 & 0xFFF);
            out[23 + outPos] = (in[8 + inPos] >>> 0 & 0xFFF);
            out[24 + outPos] = (in[9 + inPos] >>> 20 & 0xFFF);
            out[25 + outPos] = (in[9 + inPos] >>> 8 & 0xFFF);
            out[26 + outPos] = ((in[9 + inPos] << 4 & 0xFFF) | in[10 + inPos] >>> 28);
            out[27 + outPos] = (in[10 + inPos] >>> 16 & 0xFFF);
            out[28 + outPos] = (in[10 + inPos] >>> 4 & 0xFFF);
            out[29 + outPos] = ((in[10 + inPos] << 8 & 0xFFF) | in[11 + inPos] >>> 24);
            out[30 + outPos] = (in[11 + inPos] >>> 12 & 0xFFF);
            out[31 + outPos] = (in[11 + inPos] >>> 0 & 0xFFF);
        }
    }
    
    private static final class Packer13 extends IntPacker
    {
        private Packer13() {
            super(13);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x1FFF) << 19 | (in[1 + inPos] & 0x1FFF) << 6 | (in[2 + inPos] & 0x1FFF) >>> 7);
            out[1 + outPos] = ((in[2 + inPos] & 0x1FFF) << 25 | (in[3 + inPos] & 0x1FFF) << 12 | (in[4 + inPos] & 0x1FFF) >>> 1);
            out[2 + outPos] = ((in[4 + inPos] & 0x1FFF) << 31 | (in[5 + inPos] & 0x1FFF) << 18 | (in[6 + inPos] & 0x1FFF) << 5 | (in[7 + inPos] & 0x1FFF) >>> 8);
            out[3 + outPos] = ((in[7 + inPos] & 0x1FFF) << 24 | (in[8 + inPos] & 0x1FFF) << 11 | (in[9 + inPos] & 0x1FFF) >>> 2);
            out[4 + outPos] = ((in[9 + inPos] & 0x1FFF) << 30 | (in[10 + inPos] & 0x1FFF) << 17 | (in[11 + inPos] & 0x1FFF) << 4 | (in[12 + inPos] & 0x1FFF) >>> 9);
            out[5 + outPos] = ((in[12 + inPos] & 0x1FFF) << 23 | (in[13 + inPos] & 0x1FFF) << 10 | (in[14 + inPos] & 0x1FFF) >>> 3);
            out[6 + outPos] = ((in[14 + inPos] & 0x1FFF) << 29 | (in[15 + inPos] & 0x1FFF) << 16 | (in[16 + inPos] & 0x1FFF) << 3 | (in[17 + inPos] & 0x1FFF) >>> 10);
            out[7 + outPos] = ((in[17 + inPos] & 0x1FFF) << 22 | (in[18 + inPos] & 0x1FFF) << 9 | (in[19 + inPos] & 0x1FFF) >>> 4);
            out[8 + outPos] = ((in[19 + inPos] & 0x1FFF) << 28 | (in[20 + inPos] & 0x1FFF) << 15 | (in[21 + inPos] & 0x1FFF) << 2 | (in[22 + inPos] & 0x1FFF) >>> 11);
            out[9 + outPos] = ((in[22 + inPos] & 0x1FFF) << 21 | (in[23 + inPos] & 0x1FFF) << 8 | (in[24 + inPos] & 0x1FFF) >>> 5);
            out[10 + outPos] = ((in[24 + inPos] & 0x1FFF) << 27 | (in[25 + inPos] & 0x1FFF) << 14 | (in[26 + inPos] & 0x1FFF) << 1 | (in[27 + inPos] & 0x1FFF) >>> 12);
            out[11 + outPos] = ((in[27 + inPos] & 0x1FFF) << 20 | (in[28 + inPos] & 0x1FFF) << 7 | (in[29 + inPos] & 0x1FFF) >>> 6);
            out[12 + outPos] = ((in[29 + inPos] & 0x1FFF) << 26 | (in[30 + inPos] & 0x1FFF) << 13 | (in[31 + inPos] & 0x1FFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 19 & 0x1FFF);
            out[1 + outPos] = (in[0 + inPos] >>> 6 & 0x1FFF);
            out[2 + outPos] = ((in[0 + inPos] << 7 & 0x1FFF) | in[1 + inPos] >>> 25);
            out[3 + outPos] = (in[1 + inPos] >>> 12 & 0x1FFF);
            out[4 + outPos] = ((in[1 + inPos] << 1 & 0x1FFF) | in[2 + inPos] >>> 31);
            out[5 + outPos] = (in[2 + inPos] >>> 18 & 0x1FFF);
            out[6 + outPos] = (in[2 + inPos] >>> 5 & 0x1FFF);
            out[7 + outPos] = ((in[2 + inPos] << 8 & 0x1FFF) | in[3 + inPos] >>> 24);
            out[8 + outPos] = (in[3 + inPos] >>> 11 & 0x1FFF);
            out[9 + outPos] = ((in[3 + inPos] << 2 & 0x1FFF) | in[4 + inPos] >>> 30);
            out[10 + outPos] = (in[4 + inPos] >>> 17 & 0x1FFF);
            out[11 + outPos] = (in[4 + inPos] >>> 4 & 0x1FFF);
            out[12 + outPos] = ((in[4 + inPos] << 9 & 0x1FFF) | in[5 + inPos] >>> 23);
            out[13 + outPos] = (in[5 + inPos] >>> 10 & 0x1FFF);
            out[14 + outPos] = ((in[5 + inPos] << 3 & 0x1FFF) | in[6 + inPos] >>> 29);
            out[15 + outPos] = (in[6 + inPos] >>> 16 & 0x1FFF);
            out[16 + outPos] = (in[6 + inPos] >>> 3 & 0x1FFF);
            out[17 + outPos] = ((in[6 + inPos] << 10 & 0x1FFF) | in[7 + inPos] >>> 22);
            out[18 + outPos] = (in[7 + inPos] >>> 9 & 0x1FFF);
            out[19 + outPos] = ((in[7 + inPos] << 4 & 0x1FFF) | in[8 + inPos] >>> 28);
            out[20 + outPos] = (in[8 + inPos] >>> 15 & 0x1FFF);
            out[21 + outPos] = (in[8 + inPos] >>> 2 & 0x1FFF);
            out[22 + outPos] = ((in[8 + inPos] << 11 & 0x1FFF) | in[9 + inPos] >>> 21);
            out[23 + outPos] = (in[9 + inPos] >>> 8 & 0x1FFF);
            out[24 + outPos] = ((in[9 + inPos] << 5 & 0x1FFF) | in[10 + inPos] >>> 27);
            out[25 + outPos] = (in[10 + inPos] >>> 14 & 0x1FFF);
            out[26 + outPos] = (in[10 + inPos] >>> 1 & 0x1FFF);
            out[27 + outPos] = ((in[10 + inPos] << 12 & 0x1FFF) | in[11 + inPos] >>> 20);
            out[28 + outPos] = (in[11 + inPos] >>> 7 & 0x1FFF);
            out[29 + outPos] = ((in[11 + inPos] << 6 & 0x1FFF) | in[12 + inPos] >>> 26);
            out[30 + outPos] = (in[12 + inPos] >>> 13 & 0x1FFF);
            out[31 + outPos] = (in[12 + inPos] >>> 0 & 0x1FFF);
        }
    }
    
    private static final class Packer14 extends IntPacker
    {
        private Packer14() {
            super(14);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x3FFF) << 18 | (in[1 + inPos] & 0x3FFF) << 4 | (in[2 + inPos] & 0x3FFF) >>> 10);
            out[1 + outPos] = ((in[2 + inPos] & 0x3FFF) << 22 | (in[3 + inPos] & 0x3FFF) << 8 | (in[4 + inPos] & 0x3FFF) >>> 6);
            out[2 + outPos] = ((in[4 + inPos] & 0x3FFF) << 26 | (in[5 + inPos] & 0x3FFF) << 12 | (in[6 + inPos] & 0x3FFF) >>> 2);
            out[3 + outPos] = ((in[6 + inPos] & 0x3FFF) << 30 | (in[7 + inPos] & 0x3FFF) << 16 | (in[8 + inPos] & 0x3FFF) << 2 | (in[9 + inPos] & 0x3FFF) >>> 12);
            out[4 + outPos] = ((in[9 + inPos] & 0x3FFF) << 20 | (in[10 + inPos] & 0x3FFF) << 6 | (in[11 + inPos] & 0x3FFF) >>> 8);
            out[5 + outPos] = ((in[11 + inPos] & 0x3FFF) << 24 | (in[12 + inPos] & 0x3FFF) << 10 | (in[13 + inPos] & 0x3FFF) >>> 4);
            out[6 + outPos] = ((in[13 + inPos] & 0x3FFF) << 28 | (in[14 + inPos] & 0x3FFF) << 14 | (in[15 + inPos] & 0x3FFF) << 0);
            out[7 + outPos] = ((in[16 + inPos] & 0x3FFF) << 18 | (in[17 + inPos] & 0x3FFF) << 4 | (in[18 + inPos] & 0x3FFF) >>> 10);
            out[8 + outPos] = ((in[18 + inPos] & 0x3FFF) << 22 | (in[19 + inPos] & 0x3FFF) << 8 | (in[20 + inPos] & 0x3FFF) >>> 6);
            out[9 + outPos] = ((in[20 + inPos] & 0x3FFF) << 26 | (in[21 + inPos] & 0x3FFF) << 12 | (in[22 + inPos] & 0x3FFF) >>> 2);
            out[10 + outPos] = ((in[22 + inPos] & 0x3FFF) << 30 | (in[23 + inPos] & 0x3FFF) << 16 | (in[24 + inPos] & 0x3FFF) << 2 | (in[25 + inPos] & 0x3FFF) >>> 12);
            out[11 + outPos] = ((in[25 + inPos] & 0x3FFF) << 20 | (in[26 + inPos] & 0x3FFF) << 6 | (in[27 + inPos] & 0x3FFF) >>> 8);
            out[12 + outPos] = ((in[27 + inPos] & 0x3FFF) << 24 | (in[28 + inPos] & 0x3FFF) << 10 | (in[29 + inPos] & 0x3FFF) >>> 4);
            out[13 + outPos] = ((in[29 + inPos] & 0x3FFF) << 28 | (in[30 + inPos] & 0x3FFF) << 14 | (in[31 + inPos] & 0x3FFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 18 & 0x3FFF);
            out[1 + outPos] = (in[0 + inPos] >>> 4 & 0x3FFF);
            out[2 + outPos] = ((in[0 + inPos] << 10 & 0x3FFF) | in[1 + inPos] >>> 22);
            out[3 + outPos] = (in[1 + inPos] >>> 8 & 0x3FFF);
            out[4 + outPos] = ((in[1 + inPos] << 6 & 0x3FFF) | in[2 + inPos] >>> 26);
            out[5 + outPos] = (in[2 + inPos] >>> 12 & 0x3FFF);
            out[6 + outPos] = ((in[2 + inPos] << 2 & 0x3FFF) | in[3 + inPos] >>> 30);
            out[7 + outPos] = (in[3 + inPos] >>> 16 & 0x3FFF);
            out[8 + outPos] = (in[3 + inPos] >>> 2 & 0x3FFF);
            out[9 + outPos] = ((in[3 + inPos] << 12 & 0x3FFF) | in[4 + inPos] >>> 20);
            out[10 + outPos] = (in[4 + inPos] >>> 6 & 0x3FFF);
            out[11 + outPos] = ((in[4 + inPos] << 8 & 0x3FFF) | in[5 + inPos] >>> 24);
            out[12 + outPos] = (in[5 + inPos] >>> 10 & 0x3FFF);
            out[13 + outPos] = ((in[5 + inPos] << 4 & 0x3FFF) | in[6 + inPos] >>> 28);
            out[14 + outPos] = (in[6 + inPos] >>> 14 & 0x3FFF);
            out[15 + outPos] = (in[6 + inPos] >>> 0 & 0x3FFF);
            out[16 + outPos] = (in[7 + inPos] >>> 18 & 0x3FFF);
            out[17 + outPos] = (in[7 + inPos] >>> 4 & 0x3FFF);
            out[18 + outPos] = ((in[7 + inPos] << 10 & 0x3FFF) | in[8 + inPos] >>> 22);
            out[19 + outPos] = (in[8 + inPos] >>> 8 & 0x3FFF);
            out[20 + outPos] = ((in[8 + inPos] << 6 & 0x3FFF) | in[9 + inPos] >>> 26);
            out[21 + outPos] = (in[9 + inPos] >>> 12 & 0x3FFF);
            out[22 + outPos] = ((in[9 + inPos] << 2 & 0x3FFF) | in[10 + inPos] >>> 30);
            out[23 + outPos] = (in[10 + inPos] >>> 16 & 0x3FFF);
            out[24 + outPos] = (in[10 + inPos] >>> 2 & 0x3FFF);
            out[25 + outPos] = ((in[10 + inPos] << 12 & 0x3FFF) | in[11 + inPos] >>> 20);
            out[26 + outPos] = (in[11 + inPos] >>> 6 & 0x3FFF);
            out[27 + outPos] = ((in[11 + inPos] << 8 & 0x3FFF) | in[12 + inPos] >>> 24);
            out[28 + outPos] = (in[12 + inPos] >>> 10 & 0x3FFF);
            out[29 + outPos] = ((in[12 + inPos] << 4 & 0x3FFF) | in[13 + inPos] >>> 28);
            out[30 + outPos] = (in[13 + inPos] >>> 14 & 0x3FFF);
            out[31 + outPos] = (in[13 + inPos] >>> 0 & 0x3FFF);
        }
    }
    
    private static final class Packer15 extends IntPacker
    {
        private Packer15() {
            super(15);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x7FFF) << 17 | (in[1 + inPos] & 0x7FFF) << 2 | (in[2 + inPos] & 0x7FFF) >>> 13);
            out[1 + outPos] = ((in[2 + inPos] & 0x7FFF) << 19 | (in[3 + inPos] & 0x7FFF) << 4 | (in[4 + inPos] & 0x7FFF) >>> 11);
            out[2 + outPos] = ((in[4 + inPos] & 0x7FFF) << 21 | (in[5 + inPos] & 0x7FFF) << 6 | (in[6 + inPos] & 0x7FFF) >>> 9);
            out[3 + outPos] = ((in[6 + inPos] & 0x7FFF) << 23 | (in[7 + inPos] & 0x7FFF) << 8 | (in[8 + inPos] & 0x7FFF) >>> 7);
            out[4 + outPos] = ((in[8 + inPos] & 0x7FFF) << 25 | (in[9 + inPos] & 0x7FFF) << 10 | (in[10 + inPos] & 0x7FFF) >>> 5);
            out[5 + outPos] = ((in[10 + inPos] & 0x7FFF) << 27 | (in[11 + inPos] & 0x7FFF) << 12 | (in[12 + inPos] & 0x7FFF) >>> 3);
            out[6 + outPos] = ((in[12 + inPos] & 0x7FFF) << 29 | (in[13 + inPos] & 0x7FFF) << 14 | (in[14 + inPos] & 0x7FFF) >>> 1);
            out[7 + outPos] = ((in[14 + inPos] & 0x7FFF) << 31 | (in[15 + inPos] & 0x7FFF) << 16 | (in[16 + inPos] & 0x7FFF) << 1 | (in[17 + inPos] & 0x7FFF) >>> 14);
            out[8 + outPos] = ((in[17 + inPos] & 0x7FFF) << 18 | (in[18 + inPos] & 0x7FFF) << 3 | (in[19 + inPos] & 0x7FFF) >>> 12);
            out[9 + outPos] = ((in[19 + inPos] & 0x7FFF) << 20 | (in[20 + inPos] & 0x7FFF) << 5 | (in[21 + inPos] & 0x7FFF) >>> 10);
            out[10 + outPos] = ((in[21 + inPos] & 0x7FFF) << 22 | (in[22 + inPos] & 0x7FFF) << 7 | (in[23 + inPos] & 0x7FFF) >>> 8);
            out[11 + outPos] = ((in[23 + inPos] & 0x7FFF) << 24 | (in[24 + inPos] & 0x7FFF) << 9 | (in[25 + inPos] & 0x7FFF) >>> 6);
            out[12 + outPos] = ((in[25 + inPos] & 0x7FFF) << 26 | (in[26 + inPos] & 0x7FFF) << 11 | (in[27 + inPos] & 0x7FFF) >>> 4);
            out[13 + outPos] = ((in[27 + inPos] & 0x7FFF) << 28 | (in[28 + inPos] & 0x7FFF) << 13 | (in[29 + inPos] & 0x7FFF) >>> 2);
            out[14 + outPos] = ((in[29 + inPos] & 0x7FFF) << 30 | (in[30 + inPos] & 0x7FFF) << 15 | (in[31 + inPos] & 0x7FFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 17 & 0x7FFF);
            out[1 + outPos] = (in[0 + inPos] >>> 2 & 0x7FFF);
            out[2 + outPos] = ((in[0 + inPos] << 13 & 0x7FFF) | in[1 + inPos] >>> 19);
            out[3 + outPos] = (in[1 + inPos] >>> 4 & 0x7FFF);
            out[4 + outPos] = ((in[1 + inPos] << 11 & 0x7FFF) | in[2 + inPos] >>> 21);
            out[5 + outPos] = (in[2 + inPos] >>> 6 & 0x7FFF);
            out[6 + outPos] = ((in[2 + inPos] << 9 & 0x7FFF) | in[3 + inPos] >>> 23);
            out[7 + outPos] = (in[3 + inPos] >>> 8 & 0x7FFF);
            out[8 + outPos] = ((in[3 + inPos] << 7 & 0x7FFF) | in[4 + inPos] >>> 25);
            out[9 + outPos] = (in[4 + inPos] >>> 10 & 0x7FFF);
            out[10 + outPos] = ((in[4 + inPos] << 5 & 0x7FFF) | in[5 + inPos] >>> 27);
            out[11 + outPos] = (in[5 + inPos] >>> 12 & 0x7FFF);
            out[12 + outPos] = ((in[5 + inPos] << 3 & 0x7FFF) | in[6 + inPos] >>> 29);
            out[13 + outPos] = (in[6 + inPos] >>> 14 & 0x7FFF);
            out[14 + outPos] = ((in[6 + inPos] << 1 & 0x7FFF) | in[7 + inPos] >>> 31);
            out[15 + outPos] = (in[7 + inPos] >>> 16 & 0x7FFF);
            out[16 + outPos] = (in[7 + inPos] >>> 1 & 0x7FFF);
            out[17 + outPos] = ((in[7 + inPos] << 14 & 0x7FFF) | in[8 + inPos] >>> 18);
            out[18 + outPos] = (in[8 + inPos] >>> 3 & 0x7FFF);
            out[19 + outPos] = ((in[8 + inPos] << 12 & 0x7FFF) | in[9 + inPos] >>> 20);
            out[20 + outPos] = (in[9 + inPos] >>> 5 & 0x7FFF);
            out[21 + outPos] = ((in[9 + inPos] << 10 & 0x7FFF) | in[10 + inPos] >>> 22);
            out[22 + outPos] = (in[10 + inPos] >>> 7 & 0x7FFF);
            out[23 + outPos] = ((in[10 + inPos] << 8 & 0x7FFF) | in[11 + inPos] >>> 24);
            out[24 + outPos] = (in[11 + inPos] >>> 9 & 0x7FFF);
            out[25 + outPos] = ((in[11 + inPos] << 6 & 0x7FFF) | in[12 + inPos] >>> 26);
            out[26 + outPos] = (in[12 + inPos] >>> 11 & 0x7FFF);
            out[27 + outPos] = ((in[12 + inPos] << 4 & 0x7FFF) | in[13 + inPos] >>> 28);
            out[28 + outPos] = (in[13 + inPos] >>> 13 & 0x7FFF);
            out[29 + outPos] = ((in[13 + inPos] << 2 & 0x7FFF) | in[14 + inPos] >>> 30);
            out[30 + outPos] = (in[14 + inPos] >>> 15 & 0x7FFF);
            out[31 + outPos] = (in[14 + inPos] >>> 0 & 0x7FFF);
        }
    }
    
    private static final class Packer16 extends IntPacker
    {
        private Packer16() {
            super(16);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFFFF) << 16 | (in[1 + inPos] & 0xFFFF) << 0);
            out[1 + outPos] = ((in[2 + inPos] & 0xFFFF) << 16 | (in[3 + inPos] & 0xFFFF) << 0);
            out[2 + outPos] = ((in[4 + inPos] & 0xFFFF) << 16 | (in[5 + inPos] & 0xFFFF) << 0);
            out[3 + outPos] = ((in[6 + inPos] & 0xFFFF) << 16 | (in[7 + inPos] & 0xFFFF) << 0);
            out[4 + outPos] = ((in[8 + inPos] & 0xFFFF) << 16 | (in[9 + inPos] & 0xFFFF) << 0);
            out[5 + outPos] = ((in[10 + inPos] & 0xFFFF) << 16 | (in[11 + inPos] & 0xFFFF) << 0);
            out[6 + outPos] = ((in[12 + inPos] & 0xFFFF) << 16 | (in[13 + inPos] & 0xFFFF) << 0);
            out[7 + outPos] = ((in[14 + inPos] & 0xFFFF) << 16 | (in[15 + inPos] & 0xFFFF) << 0);
            out[8 + outPos] = ((in[16 + inPos] & 0xFFFF) << 16 | (in[17 + inPos] & 0xFFFF) << 0);
            out[9 + outPos] = ((in[18 + inPos] & 0xFFFF) << 16 | (in[19 + inPos] & 0xFFFF) << 0);
            out[10 + outPos] = ((in[20 + inPos] & 0xFFFF) << 16 | (in[21 + inPos] & 0xFFFF) << 0);
            out[11 + outPos] = ((in[22 + inPos] & 0xFFFF) << 16 | (in[23 + inPos] & 0xFFFF) << 0);
            out[12 + outPos] = ((in[24 + inPos] & 0xFFFF) << 16 | (in[25 + inPos] & 0xFFFF) << 0);
            out[13 + outPos] = ((in[26 + inPos] & 0xFFFF) << 16 | (in[27 + inPos] & 0xFFFF) << 0);
            out[14 + outPos] = ((in[28 + inPos] & 0xFFFF) << 16 | (in[29 + inPos] & 0xFFFF) << 0);
            out[15 + outPos] = ((in[30 + inPos] & 0xFFFF) << 16 | (in[31 + inPos] & 0xFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 16 & 0xFFFF);
            out[1 + outPos] = (in[0 + inPos] >>> 0 & 0xFFFF);
            out[2 + outPos] = (in[1 + inPos] >>> 16 & 0xFFFF);
            out[3 + outPos] = (in[1 + inPos] >>> 0 & 0xFFFF);
            out[4 + outPos] = (in[2 + inPos] >>> 16 & 0xFFFF);
            out[5 + outPos] = (in[2 + inPos] >>> 0 & 0xFFFF);
            out[6 + outPos] = (in[3 + inPos] >>> 16 & 0xFFFF);
            out[7 + outPos] = (in[3 + inPos] >>> 0 & 0xFFFF);
            out[8 + outPos] = (in[4 + inPos] >>> 16 & 0xFFFF);
            out[9 + outPos] = (in[4 + inPos] >>> 0 & 0xFFFF);
            out[10 + outPos] = (in[5 + inPos] >>> 16 & 0xFFFF);
            out[11 + outPos] = (in[5 + inPos] >>> 0 & 0xFFFF);
            out[12 + outPos] = (in[6 + inPos] >>> 16 & 0xFFFF);
            out[13 + outPos] = (in[6 + inPos] >>> 0 & 0xFFFF);
            out[14 + outPos] = (in[7 + inPos] >>> 16 & 0xFFFF);
            out[15 + outPos] = (in[7 + inPos] >>> 0 & 0xFFFF);
            out[16 + outPos] = (in[8 + inPos] >>> 16 & 0xFFFF);
            out[17 + outPos] = (in[8 + inPos] >>> 0 & 0xFFFF);
            out[18 + outPos] = (in[9 + inPos] >>> 16 & 0xFFFF);
            out[19 + outPos] = (in[9 + inPos] >>> 0 & 0xFFFF);
            out[20 + outPos] = (in[10 + inPos] >>> 16 & 0xFFFF);
            out[21 + outPos] = (in[10 + inPos] >>> 0 & 0xFFFF);
            out[22 + outPos] = (in[11 + inPos] >>> 16 & 0xFFFF);
            out[23 + outPos] = (in[11 + inPos] >>> 0 & 0xFFFF);
            out[24 + outPos] = (in[12 + inPos] >>> 16 & 0xFFFF);
            out[25 + outPos] = (in[12 + inPos] >>> 0 & 0xFFFF);
            out[26 + outPos] = (in[13 + inPos] >>> 16 & 0xFFFF);
            out[27 + outPos] = (in[13 + inPos] >>> 0 & 0xFFFF);
            out[28 + outPos] = (in[14 + inPos] >>> 16 & 0xFFFF);
            out[29 + outPos] = (in[14 + inPos] >>> 0 & 0xFFFF);
            out[30 + outPos] = (in[15 + inPos] >>> 16 & 0xFFFF);
            out[31 + outPos] = (in[15 + inPos] >>> 0 & 0xFFFF);
        }
    }
    
    private static final class Packer17 extends IntPacker
    {
        private Packer17() {
            super(17);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x1FFFF) << 15 | (in[1 + inPos] & 0x1FFFF) >>> 2);
            out[1 + outPos] = ((in[1 + inPos] & 0x1FFFF) << 30 | (in[2 + inPos] & 0x1FFFF) << 13 | (in[3 + inPos] & 0x1FFFF) >>> 4);
            out[2 + outPos] = ((in[3 + inPos] & 0x1FFFF) << 28 | (in[4 + inPos] & 0x1FFFF) << 11 | (in[5 + inPos] & 0x1FFFF) >>> 6);
            out[3 + outPos] = ((in[5 + inPos] & 0x1FFFF) << 26 | (in[6 + inPos] & 0x1FFFF) << 9 | (in[7 + inPos] & 0x1FFFF) >>> 8);
            out[4 + outPos] = ((in[7 + inPos] & 0x1FFFF) << 24 | (in[8 + inPos] & 0x1FFFF) << 7 | (in[9 + inPos] & 0x1FFFF) >>> 10);
            out[5 + outPos] = ((in[9 + inPos] & 0x1FFFF) << 22 | (in[10 + inPos] & 0x1FFFF) << 5 | (in[11 + inPos] & 0x1FFFF) >>> 12);
            out[6 + outPos] = ((in[11 + inPos] & 0x1FFFF) << 20 | (in[12 + inPos] & 0x1FFFF) << 3 | (in[13 + inPos] & 0x1FFFF) >>> 14);
            out[7 + outPos] = ((in[13 + inPos] & 0x1FFFF) << 18 | (in[14 + inPos] & 0x1FFFF) << 1 | (in[15 + inPos] & 0x1FFFF) >>> 16);
            out[8 + outPos] = ((in[15 + inPos] & 0x1FFFF) << 16 | (in[16 + inPos] & 0x1FFFF) >>> 1);
            out[9 + outPos] = ((in[16 + inPos] & 0x1FFFF) << 31 | (in[17 + inPos] & 0x1FFFF) << 14 | (in[18 + inPos] & 0x1FFFF) >>> 3);
            out[10 + outPos] = ((in[18 + inPos] & 0x1FFFF) << 29 | (in[19 + inPos] & 0x1FFFF) << 12 | (in[20 + inPos] & 0x1FFFF) >>> 5);
            out[11 + outPos] = ((in[20 + inPos] & 0x1FFFF) << 27 | (in[21 + inPos] & 0x1FFFF) << 10 | (in[22 + inPos] & 0x1FFFF) >>> 7);
            out[12 + outPos] = ((in[22 + inPos] & 0x1FFFF) << 25 | (in[23 + inPos] & 0x1FFFF) << 8 | (in[24 + inPos] & 0x1FFFF) >>> 9);
            out[13 + outPos] = ((in[24 + inPos] & 0x1FFFF) << 23 | (in[25 + inPos] & 0x1FFFF) << 6 | (in[26 + inPos] & 0x1FFFF) >>> 11);
            out[14 + outPos] = ((in[26 + inPos] & 0x1FFFF) << 21 | (in[27 + inPos] & 0x1FFFF) << 4 | (in[28 + inPos] & 0x1FFFF) >>> 13);
            out[15 + outPos] = ((in[28 + inPos] & 0x1FFFF) << 19 | (in[29 + inPos] & 0x1FFFF) << 2 | (in[30 + inPos] & 0x1FFFF) >>> 15);
            out[16 + outPos] = ((in[30 + inPos] & 0x1FFFF) << 17 | (in[31 + inPos] & 0x1FFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 15 & 0x1FFFF);
            out[1 + outPos] = ((in[0 + inPos] << 2 & 0x1FFFF) | in[1 + inPos] >>> 30);
            out[2 + outPos] = (in[1 + inPos] >>> 13 & 0x1FFFF);
            out[3 + outPos] = ((in[1 + inPos] << 4 & 0x1FFFF) | in[2 + inPos] >>> 28);
            out[4 + outPos] = (in[2 + inPos] >>> 11 & 0x1FFFF);
            out[5 + outPos] = ((in[2 + inPos] << 6 & 0x1FFFF) | in[3 + inPos] >>> 26);
            out[6 + outPos] = (in[3 + inPos] >>> 9 & 0x1FFFF);
            out[7 + outPos] = ((in[3 + inPos] << 8 & 0x1FFFF) | in[4 + inPos] >>> 24);
            out[8 + outPos] = (in[4 + inPos] >>> 7 & 0x1FFFF);
            out[9 + outPos] = ((in[4 + inPos] << 10 & 0x1FFFF) | in[5 + inPos] >>> 22);
            out[10 + outPos] = (in[5 + inPos] >>> 5 & 0x1FFFF);
            out[11 + outPos] = ((in[5 + inPos] << 12 & 0x1FFFF) | in[6 + inPos] >>> 20);
            out[12 + outPos] = (in[6 + inPos] >>> 3 & 0x1FFFF);
            out[13 + outPos] = ((in[6 + inPos] << 14 & 0x1FFFF) | in[7 + inPos] >>> 18);
            out[14 + outPos] = (in[7 + inPos] >>> 1 & 0x1FFFF);
            out[15 + outPos] = ((in[7 + inPos] << 16 & 0x1FFFF) | in[8 + inPos] >>> 16);
            out[16 + outPos] = ((in[8 + inPos] << 1 & 0x1FFFF) | in[9 + inPos] >>> 31);
            out[17 + outPos] = (in[9 + inPos] >>> 14 & 0x1FFFF);
            out[18 + outPos] = ((in[9 + inPos] << 3 & 0x1FFFF) | in[10 + inPos] >>> 29);
            out[19 + outPos] = (in[10 + inPos] >>> 12 & 0x1FFFF);
            out[20 + outPos] = ((in[10 + inPos] << 5 & 0x1FFFF) | in[11 + inPos] >>> 27);
            out[21 + outPos] = (in[11 + inPos] >>> 10 & 0x1FFFF);
            out[22 + outPos] = ((in[11 + inPos] << 7 & 0x1FFFF) | in[12 + inPos] >>> 25);
            out[23 + outPos] = (in[12 + inPos] >>> 8 & 0x1FFFF);
            out[24 + outPos] = ((in[12 + inPos] << 9 & 0x1FFFF) | in[13 + inPos] >>> 23);
            out[25 + outPos] = (in[13 + inPos] >>> 6 & 0x1FFFF);
            out[26 + outPos] = ((in[13 + inPos] << 11 & 0x1FFFF) | in[14 + inPos] >>> 21);
            out[27 + outPos] = (in[14 + inPos] >>> 4 & 0x1FFFF);
            out[28 + outPos] = ((in[14 + inPos] << 13 & 0x1FFFF) | in[15 + inPos] >>> 19);
            out[29 + outPos] = (in[15 + inPos] >>> 2 & 0x1FFFF);
            out[30 + outPos] = ((in[15 + inPos] << 15 & 0x1FFFF) | in[16 + inPos] >>> 17);
            out[31 + outPos] = (in[16 + inPos] >>> 0 & 0x1FFFF);
        }
    }
    
    private static final class Packer18 extends IntPacker
    {
        private Packer18() {
            super(18);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x3FFFF) << 14 | (in[1 + inPos] & 0x3FFFF) >>> 4);
            out[1 + outPos] = ((in[1 + inPos] & 0x3FFFF) << 28 | (in[2 + inPos] & 0x3FFFF) << 10 | (in[3 + inPos] & 0x3FFFF) >>> 8);
            out[2 + outPos] = ((in[3 + inPos] & 0x3FFFF) << 24 | (in[4 + inPos] & 0x3FFFF) << 6 | (in[5 + inPos] & 0x3FFFF) >>> 12);
            out[3 + outPos] = ((in[5 + inPos] & 0x3FFFF) << 20 | (in[6 + inPos] & 0x3FFFF) << 2 | (in[7 + inPos] & 0x3FFFF) >>> 16);
            out[4 + outPos] = ((in[7 + inPos] & 0x3FFFF) << 16 | (in[8 + inPos] & 0x3FFFF) >>> 2);
            out[5 + outPos] = ((in[8 + inPos] & 0x3FFFF) << 30 | (in[9 + inPos] & 0x3FFFF) << 12 | (in[10 + inPos] & 0x3FFFF) >>> 6);
            out[6 + outPos] = ((in[10 + inPos] & 0x3FFFF) << 26 | (in[11 + inPos] & 0x3FFFF) << 8 | (in[12 + inPos] & 0x3FFFF) >>> 10);
            out[7 + outPos] = ((in[12 + inPos] & 0x3FFFF) << 22 | (in[13 + inPos] & 0x3FFFF) << 4 | (in[14 + inPos] & 0x3FFFF) >>> 14);
            out[8 + outPos] = ((in[14 + inPos] & 0x3FFFF) << 18 | (in[15 + inPos] & 0x3FFFF) << 0);
            out[9 + outPos] = ((in[16 + inPos] & 0x3FFFF) << 14 | (in[17 + inPos] & 0x3FFFF) >>> 4);
            out[10 + outPos] = ((in[17 + inPos] & 0x3FFFF) << 28 | (in[18 + inPos] & 0x3FFFF) << 10 | (in[19 + inPos] & 0x3FFFF) >>> 8);
            out[11 + outPos] = ((in[19 + inPos] & 0x3FFFF) << 24 | (in[20 + inPos] & 0x3FFFF) << 6 | (in[21 + inPos] & 0x3FFFF) >>> 12);
            out[12 + outPos] = ((in[21 + inPos] & 0x3FFFF) << 20 | (in[22 + inPos] & 0x3FFFF) << 2 | (in[23 + inPos] & 0x3FFFF) >>> 16);
            out[13 + outPos] = ((in[23 + inPos] & 0x3FFFF) << 16 | (in[24 + inPos] & 0x3FFFF) >>> 2);
            out[14 + outPos] = ((in[24 + inPos] & 0x3FFFF) << 30 | (in[25 + inPos] & 0x3FFFF) << 12 | (in[26 + inPos] & 0x3FFFF) >>> 6);
            out[15 + outPos] = ((in[26 + inPos] & 0x3FFFF) << 26 | (in[27 + inPos] & 0x3FFFF) << 8 | (in[28 + inPos] & 0x3FFFF) >>> 10);
            out[16 + outPos] = ((in[28 + inPos] & 0x3FFFF) << 22 | (in[29 + inPos] & 0x3FFFF) << 4 | (in[30 + inPos] & 0x3FFFF) >>> 14);
            out[17 + outPos] = ((in[30 + inPos] & 0x3FFFF) << 18 | (in[31 + inPos] & 0x3FFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 14 & 0x3FFFF);
            out[1 + outPos] = ((in[0 + inPos] << 4 & 0x3FFFF) | in[1 + inPos] >>> 28);
            out[2 + outPos] = (in[1 + inPos] >>> 10 & 0x3FFFF);
            out[3 + outPos] = ((in[1 + inPos] << 8 & 0x3FFFF) | in[2 + inPos] >>> 24);
            out[4 + outPos] = (in[2 + inPos] >>> 6 & 0x3FFFF);
            out[5 + outPos] = ((in[2 + inPos] << 12 & 0x3FFFF) | in[3 + inPos] >>> 20);
            out[6 + outPos] = (in[3 + inPos] >>> 2 & 0x3FFFF);
            out[7 + outPos] = ((in[3 + inPos] << 16 & 0x3FFFF) | in[4 + inPos] >>> 16);
            out[8 + outPos] = ((in[4 + inPos] << 2 & 0x3FFFF) | in[5 + inPos] >>> 30);
            out[9 + outPos] = (in[5 + inPos] >>> 12 & 0x3FFFF);
            out[10 + outPos] = ((in[5 + inPos] << 6 & 0x3FFFF) | in[6 + inPos] >>> 26);
            out[11 + outPos] = (in[6 + inPos] >>> 8 & 0x3FFFF);
            out[12 + outPos] = ((in[6 + inPos] << 10 & 0x3FFFF) | in[7 + inPos] >>> 22);
            out[13 + outPos] = (in[7 + inPos] >>> 4 & 0x3FFFF);
            out[14 + outPos] = ((in[7 + inPos] << 14 & 0x3FFFF) | in[8 + inPos] >>> 18);
            out[15 + outPos] = (in[8 + inPos] >>> 0 & 0x3FFFF);
            out[16 + outPos] = (in[9 + inPos] >>> 14 & 0x3FFFF);
            out[17 + outPos] = ((in[9 + inPos] << 4 & 0x3FFFF) | in[10 + inPos] >>> 28);
            out[18 + outPos] = (in[10 + inPos] >>> 10 & 0x3FFFF);
            out[19 + outPos] = ((in[10 + inPos] << 8 & 0x3FFFF) | in[11 + inPos] >>> 24);
            out[20 + outPos] = (in[11 + inPos] >>> 6 & 0x3FFFF);
            out[21 + outPos] = ((in[11 + inPos] << 12 & 0x3FFFF) | in[12 + inPos] >>> 20);
            out[22 + outPos] = (in[12 + inPos] >>> 2 & 0x3FFFF);
            out[23 + outPos] = ((in[12 + inPos] << 16 & 0x3FFFF) | in[13 + inPos] >>> 16);
            out[24 + outPos] = ((in[13 + inPos] << 2 & 0x3FFFF) | in[14 + inPos] >>> 30);
            out[25 + outPos] = (in[14 + inPos] >>> 12 & 0x3FFFF);
            out[26 + outPos] = ((in[14 + inPos] << 6 & 0x3FFFF) | in[15 + inPos] >>> 26);
            out[27 + outPos] = (in[15 + inPos] >>> 8 & 0x3FFFF);
            out[28 + outPos] = ((in[15 + inPos] << 10 & 0x3FFFF) | in[16 + inPos] >>> 22);
            out[29 + outPos] = (in[16 + inPos] >>> 4 & 0x3FFFF);
            out[30 + outPos] = ((in[16 + inPos] << 14 & 0x3FFFF) | in[17 + inPos] >>> 18);
            out[31 + outPos] = (in[17 + inPos] >>> 0 & 0x3FFFF);
        }
    }
    
    private static final class Packer19 extends IntPacker
    {
        private Packer19() {
            super(19);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x7FFFF) << 13 | (in[1 + inPos] & 0x7FFFF) >>> 6);
            out[1 + outPos] = ((in[1 + inPos] & 0x7FFFF) << 26 | (in[2 + inPos] & 0x7FFFF) << 7 | (in[3 + inPos] & 0x7FFFF) >>> 12);
            out[2 + outPos] = ((in[3 + inPos] & 0x7FFFF) << 20 | (in[4 + inPos] & 0x7FFFF) << 1 | (in[5 + inPos] & 0x7FFFF) >>> 18);
            out[3 + outPos] = ((in[5 + inPos] & 0x7FFFF) << 14 | (in[6 + inPos] & 0x7FFFF) >>> 5);
            out[4 + outPos] = ((in[6 + inPos] & 0x7FFFF) << 27 | (in[7 + inPos] & 0x7FFFF) << 8 | (in[8 + inPos] & 0x7FFFF) >>> 11);
            out[5 + outPos] = ((in[8 + inPos] & 0x7FFFF) << 21 | (in[9 + inPos] & 0x7FFFF) << 2 | (in[10 + inPos] & 0x7FFFF) >>> 17);
            out[6 + outPos] = ((in[10 + inPos] & 0x7FFFF) << 15 | (in[11 + inPos] & 0x7FFFF) >>> 4);
            out[7 + outPos] = ((in[11 + inPos] & 0x7FFFF) << 28 | (in[12 + inPos] & 0x7FFFF) << 9 | (in[13 + inPos] & 0x7FFFF) >>> 10);
            out[8 + outPos] = ((in[13 + inPos] & 0x7FFFF) << 22 | (in[14 + inPos] & 0x7FFFF) << 3 | (in[15 + inPos] & 0x7FFFF) >>> 16);
            out[9 + outPos] = ((in[15 + inPos] & 0x7FFFF) << 16 | (in[16 + inPos] & 0x7FFFF) >>> 3);
            out[10 + outPos] = ((in[16 + inPos] & 0x7FFFF) << 29 | (in[17 + inPos] & 0x7FFFF) << 10 | (in[18 + inPos] & 0x7FFFF) >>> 9);
            out[11 + outPos] = ((in[18 + inPos] & 0x7FFFF) << 23 | (in[19 + inPos] & 0x7FFFF) << 4 | (in[20 + inPos] & 0x7FFFF) >>> 15);
            out[12 + outPos] = ((in[20 + inPos] & 0x7FFFF) << 17 | (in[21 + inPos] & 0x7FFFF) >>> 2);
            out[13 + outPos] = ((in[21 + inPos] & 0x7FFFF) << 30 | (in[22 + inPos] & 0x7FFFF) << 11 | (in[23 + inPos] & 0x7FFFF) >>> 8);
            out[14 + outPos] = ((in[23 + inPos] & 0x7FFFF) << 24 | (in[24 + inPos] & 0x7FFFF) << 5 | (in[25 + inPos] & 0x7FFFF) >>> 14);
            out[15 + outPos] = ((in[25 + inPos] & 0x7FFFF) << 18 | (in[26 + inPos] & 0x7FFFF) >>> 1);
            out[16 + outPos] = ((in[26 + inPos] & 0x7FFFF) << 31 | (in[27 + inPos] & 0x7FFFF) << 12 | (in[28 + inPos] & 0x7FFFF) >>> 7);
            out[17 + outPos] = ((in[28 + inPos] & 0x7FFFF) << 25 | (in[29 + inPos] & 0x7FFFF) << 6 | (in[30 + inPos] & 0x7FFFF) >>> 13);
            out[18 + outPos] = ((in[30 + inPos] & 0x7FFFF) << 19 | (in[31 + inPos] & 0x7FFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 13 & 0x7FFFF);
            out[1 + outPos] = ((in[0 + inPos] << 6 & 0x7FFFF) | in[1 + inPos] >>> 26);
            out[2 + outPos] = (in[1 + inPos] >>> 7 & 0x7FFFF);
            out[3 + outPos] = ((in[1 + inPos] << 12 & 0x7FFFF) | in[2 + inPos] >>> 20);
            out[4 + outPos] = (in[2 + inPos] >>> 1 & 0x7FFFF);
            out[5 + outPos] = ((in[2 + inPos] << 18 & 0x7FFFF) | in[3 + inPos] >>> 14);
            out[6 + outPos] = ((in[3 + inPos] << 5 & 0x7FFFF) | in[4 + inPos] >>> 27);
            out[7 + outPos] = (in[4 + inPos] >>> 8 & 0x7FFFF);
            out[8 + outPos] = ((in[4 + inPos] << 11 & 0x7FFFF) | in[5 + inPos] >>> 21);
            out[9 + outPos] = (in[5 + inPos] >>> 2 & 0x7FFFF);
            out[10 + outPos] = ((in[5 + inPos] << 17 & 0x7FFFF) | in[6 + inPos] >>> 15);
            out[11 + outPos] = ((in[6 + inPos] << 4 & 0x7FFFF) | in[7 + inPos] >>> 28);
            out[12 + outPos] = (in[7 + inPos] >>> 9 & 0x7FFFF);
            out[13 + outPos] = ((in[7 + inPos] << 10 & 0x7FFFF) | in[8 + inPos] >>> 22);
            out[14 + outPos] = (in[8 + inPos] >>> 3 & 0x7FFFF);
            out[15 + outPos] = ((in[8 + inPos] << 16 & 0x7FFFF) | in[9 + inPos] >>> 16);
            out[16 + outPos] = ((in[9 + inPos] << 3 & 0x7FFFF) | in[10 + inPos] >>> 29);
            out[17 + outPos] = (in[10 + inPos] >>> 10 & 0x7FFFF);
            out[18 + outPos] = ((in[10 + inPos] << 9 & 0x7FFFF) | in[11 + inPos] >>> 23);
            out[19 + outPos] = (in[11 + inPos] >>> 4 & 0x7FFFF);
            out[20 + outPos] = ((in[11 + inPos] << 15 & 0x7FFFF) | in[12 + inPos] >>> 17);
            out[21 + outPos] = ((in[12 + inPos] << 2 & 0x7FFFF) | in[13 + inPos] >>> 30);
            out[22 + outPos] = (in[13 + inPos] >>> 11 & 0x7FFFF);
            out[23 + outPos] = ((in[13 + inPos] << 8 & 0x7FFFF) | in[14 + inPos] >>> 24);
            out[24 + outPos] = (in[14 + inPos] >>> 5 & 0x7FFFF);
            out[25 + outPos] = ((in[14 + inPos] << 14 & 0x7FFFF) | in[15 + inPos] >>> 18);
            out[26 + outPos] = ((in[15 + inPos] << 1 & 0x7FFFF) | in[16 + inPos] >>> 31);
            out[27 + outPos] = (in[16 + inPos] >>> 12 & 0x7FFFF);
            out[28 + outPos] = ((in[16 + inPos] << 7 & 0x7FFFF) | in[17 + inPos] >>> 25);
            out[29 + outPos] = (in[17 + inPos] >>> 6 & 0x7FFFF);
            out[30 + outPos] = ((in[17 + inPos] << 13 & 0x7FFFF) | in[18 + inPos] >>> 19);
            out[31 + outPos] = (in[18 + inPos] >>> 0 & 0x7FFFF);
        }
    }
    
    private static final class Packer20 extends IntPacker
    {
        private Packer20() {
            super(20);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFFFFF) << 12 | (in[1 + inPos] & 0xFFFFF) >>> 8);
            out[1 + outPos] = ((in[1 + inPos] & 0xFFFFF) << 24 | (in[2 + inPos] & 0xFFFFF) << 4 | (in[3 + inPos] & 0xFFFFF) >>> 16);
            out[2 + outPos] = ((in[3 + inPos] & 0xFFFFF) << 16 | (in[4 + inPos] & 0xFFFFF) >>> 4);
            out[3 + outPos] = ((in[4 + inPos] & 0xFFFFF) << 28 | (in[5 + inPos] & 0xFFFFF) << 8 | (in[6 + inPos] & 0xFFFFF) >>> 12);
            out[4 + outPos] = ((in[6 + inPos] & 0xFFFFF) << 20 | (in[7 + inPos] & 0xFFFFF) << 0);
            out[5 + outPos] = ((in[8 + inPos] & 0xFFFFF) << 12 | (in[9 + inPos] & 0xFFFFF) >>> 8);
            out[6 + outPos] = ((in[9 + inPos] & 0xFFFFF) << 24 | (in[10 + inPos] & 0xFFFFF) << 4 | (in[11 + inPos] & 0xFFFFF) >>> 16);
            out[7 + outPos] = ((in[11 + inPos] & 0xFFFFF) << 16 | (in[12 + inPos] & 0xFFFFF) >>> 4);
            out[8 + outPos] = ((in[12 + inPos] & 0xFFFFF) << 28 | (in[13 + inPos] & 0xFFFFF) << 8 | (in[14 + inPos] & 0xFFFFF) >>> 12);
            out[9 + outPos] = ((in[14 + inPos] & 0xFFFFF) << 20 | (in[15 + inPos] & 0xFFFFF) << 0);
            out[10 + outPos] = ((in[16 + inPos] & 0xFFFFF) << 12 | (in[17 + inPos] & 0xFFFFF) >>> 8);
            out[11 + outPos] = ((in[17 + inPos] & 0xFFFFF) << 24 | (in[18 + inPos] & 0xFFFFF) << 4 | (in[19 + inPos] & 0xFFFFF) >>> 16);
            out[12 + outPos] = ((in[19 + inPos] & 0xFFFFF) << 16 | (in[20 + inPos] & 0xFFFFF) >>> 4);
            out[13 + outPos] = ((in[20 + inPos] & 0xFFFFF) << 28 | (in[21 + inPos] & 0xFFFFF) << 8 | (in[22 + inPos] & 0xFFFFF) >>> 12);
            out[14 + outPos] = ((in[22 + inPos] & 0xFFFFF) << 20 | (in[23 + inPos] & 0xFFFFF) << 0);
            out[15 + outPos] = ((in[24 + inPos] & 0xFFFFF) << 12 | (in[25 + inPos] & 0xFFFFF) >>> 8);
            out[16 + outPos] = ((in[25 + inPos] & 0xFFFFF) << 24 | (in[26 + inPos] & 0xFFFFF) << 4 | (in[27 + inPos] & 0xFFFFF) >>> 16);
            out[17 + outPos] = ((in[27 + inPos] & 0xFFFFF) << 16 | (in[28 + inPos] & 0xFFFFF) >>> 4);
            out[18 + outPos] = ((in[28 + inPos] & 0xFFFFF) << 28 | (in[29 + inPos] & 0xFFFFF) << 8 | (in[30 + inPos] & 0xFFFFF) >>> 12);
            out[19 + outPos] = ((in[30 + inPos] & 0xFFFFF) << 20 | (in[31 + inPos] & 0xFFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 12 & 0xFFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 8 & 0xFFFFF) | in[1 + inPos] >>> 24);
            out[2 + outPos] = (in[1 + inPos] >>> 4 & 0xFFFFF);
            out[3 + outPos] = ((in[1 + inPos] << 16 & 0xFFFFF) | in[2 + inPos] >>> 16);
            out[4 + outPos] = ((in[2 + inPos] << 4 & 0xFFFFF) | in[3 + inPos] >>> 28);
            out[5 + outPos] = (in[3 + inPos] >>> 8 & 0xFFFFF);
            out[6 + outPos] = ((in[3 + inPos] << 12 & 0xFFFFF) | in[4 + inPos] >>> 20);
            out[7 + outPos] = (in[4 + inPos] >>> 0 & 0xFFFFF);
            out[8 + outPos] = (in[5 + inPos] >>> 12 & 0xFFFFF);
            out[9 + outPos] = ((in[5 + inPos] << 8 & 0xFFFFF) | in[6 + inPos] >>> 24);
            out[10 + outPos] = (in[6 + inPos] >>> 4 & 0xFFFFF);
            out[11 + outPos] = ((in[6 + inPos] << 16 & 0xFFFFF) | in[7 + inPos] >>> 16);
            out[12 + outPos] = ((in[7 + inPos] << 4 & 0xFFFFF) | in[8 + inPos] >>> 28);
            out[13 + outPos] = (in[8 + inPos] >>> 8 & 0xFFFFF);
            out[14 + outPos] = ((in[8 + inPos] << 12 & 0xFFFFF) | in[9 + inPos] >>> 20);
            out[15 + outPos] = (in[9 + inPos] >>> 0 & 0xFFFFF);
            out[16 + outPos] = (in[10 + inPos] >>> 12 & 0xFFFFF);
            out[17 + outPos] = ((in[10 + inPos] << 8 & 0xFFFFF) | in[11 + inPos] >>> 24);
            out[18 + outPos] = (in[11 + inPos] >>> 4 & 0xFFFFF);
            out[19 + outPos] = ((in[11 + inPos] << 16 & 0xFFFFF) | in[12 + inPos] >>> 16);
            out[20 + outPos] = ((in[12 + inPos] << 4 & 0xFFFFF) | in[13 + inPos] >>> 28);
            out[21 + outPos] = (in[13 + inPos] >>> 8 & 0xFFFFF);
            out[22 + outPos] = ((in[13 + inPos] << 12 & 0xFFFFF) | in[14 + inPos] >>> 20);
            out[23 + outPos] = (in[14 + inPos] >>> 0 & 0xFFFFF);
            out[24 + outPos] = (in[15 + inPos] >>> 12 & 0xFFFFF);
            out[25 + outPos] = ((in[15 + inPos] << 8 & 0xFFFFF) | in[16 + inPos] >>> 24);
            out[26 + outPos] = (in[16 + inPos] >>> 4 & 0xFFFFF);
            out[27 + outPos] = ((in[16 + inPos] << 16 & 0xFFFFF) | in[17 + inPos] >>> 16);
            out[28 + outPos] = ((in[17 + inPos] << 4 & 0xFFFFF) | in[18 + inPos] >>> 28);
            out[29 + outPos] = (in[18 + inPos] >>> 8 & 0xFFFFF);
            out[30 + outPos] = ((in[18 + inPos] << 12 & 0xFFFFF) | in[19 + inPos] >>> 20);
            out[31 + outPos] = (in[19 + inPos] >>> 0 & 0xFFFFF);
        }
    }
    
    private static final class Packer21 extends IntPacker
    {
        private Packer21() {
            super(21);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x1FFFFF) << 11 | (in[1 + inPos] & 0x1FFFFF) >>> 10);
            out[1 + outPos] = ((in[1 + inPos] & 0x1FFFFF) << 22 | (in[2 + inPos] & 0x1FFFFF) << 1 | (in[3 + inPos] & 0x1FFFFF) >>> 20);
            out[2 + outPos] = ((in[3 + inPos] & 0x1FFFFF) << 12 | (in[4 + inPos] & 0x1FFFFF) >>> 9);
            out[3 + outPos] = ((in[4 + inPos] & 0x1FFFFF) << 23 | (in[5 + inPos] & 0x1FFFFF) << 2 | (in[6 + inPos] & 0x1FFFFF) >>> 19);
            out[4 + outPos] = ((in[6 + inPos] & 0x1FFFFF) << 13 | (in[7 + inPos] & 0x1FFFFF) >>> 8);
            out[5 + outPos] = ((in[7 + inPos] & 0x1FFFFF) << 24 | (in[8 + inPos] & 0x1FFFFF) << 3 | (in[9 + inPos] & 0x1FFFFF) >>> 18);
            out[6 + outPos] = ((in[9 + inPos] & 0x1FFFFF) << 14 | (in[10 + inPos] & 0x1FFFFF) >>> 7);
            out[7 + outPos] = ((in[10 + inPos] & 0x1FFFFF) << 25 | (in[11 + inPos] & 0x1FFFFF) << 4 | (in[12 + inPos] & 0x1FFFFF) >>> 17);
            out[8 + outPos] = ((in[12 + inPos] & 0x1FFFFF) << 15 | (in[13 + inPos] & 0x1FFFFF) >>> 6);
            out[9 + outPos] = ((in[13 + inPos] & 0x1FFFFF) << 26 | (in[14 + inPos] & 0x1FFFFF) << 5 | (in[15 + inPos] & 0x1FFFFF) >>> 16);
            out[10 + outPos] = ((in[15 + inPos] & 0x1FFFFF) << 16 | (in[16 + inPos] & 0x1FFFFF) >>> 5);
            out[11 + outPos] = ((in[16 + inPos] & 0x1FFFFF) << 27 | (in[17 + inPos] & 0x1FFFFF) << 6 | (in[18 + inPos] & 0x1FFFFF) >>> 15);
            out[12 + outPos] = ((in[18 + inPos] & 0x1FFFFF) << 17 | (in[19 + inPos] & 0x1FFFFF) >>> 4);
            out[13 + outPos] = ((in[19 + inPos] & 0x1FFFFF) << 28 | (in[20 + inPos] & 0x1FFFFF) << 7 | (in[21 + inPos] & 0x1FFFFF) >>> 14);
            out[14 + outPos] = ((in[21 + inPos] & 0x1FFFFF) << 18 | (in[22 + inPos] & 0x1FFFFF) >>> 3);
            out[15 + outPos] = ((in[22 + inPos] & 0x1FFFFF) << 29 | (in[23 + inPos] & 0x1FFFFF) << 8 | (in[24 + inPos] & 0x1FFFFF) >>> 13);
            out[16 + outPos] = ((in[24 + inPos] & 0x1FFFFF) << 19 | (in[25 + inPos] & 0x1FFFFF) >>> 2);
            out[17 + outPos] = ((in[25 + inPos] & 0x1FFFFF) << 30 | (in[26 + inPos] & 0x1FFFFF) << 9 | (in[27 + inPos] & 0x1FFFFF) >>> 12);
            out[18 + outPos] = ((in[27 + inPos] & 0x1FFFFF) << 20 | (in[28 + inPos] & 0x1FFFFF) >>> 1);
            out[19 + outPos] = ((in[28 + inPos] & 0x1FFFFF) << 31 | (in[29 + inPos] & 0x1FFFFF) << 10 | (in[30 + inPos] & 0x1FFFFF) >>> 11);
            out[20 + outPos] = ((in[30 + inPos] & 0x1FFFFF) << 21 | (in[31 + inPos] & 0x1FFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 11 & 0x1FFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 10 & 0x1FFFFF) | in[1 + inPos] >>> 22);
            out[2 + outPos] = (in[1 + inPos] >>> 1 & 0x1FFFFF);
            out[3 + outPos] = ((in[1 + inPos] << 20 & 0x1FFFFF) | in[2 + inPos] >>> 12);
            out[4 + outPos] = ((in[2 + inPos] << 9 & 0x1FFFFF) | in[3 + inPos] >>> 23);
            out[5 + outPos] = (in[3 + inPos] >>> 2 & 0x1FFFFF);
            out[6 + outPos] = ((in[3 + inPos] << 19 & 0x1FFFFF) | in[4 + inPos] >>> 13);
            out[7 + outPos] = ((in[4 + inPos] << 8 & 0x1FFFFF) | in[5 + inPos] >>> 24);
            out[8 + outPos] = (in[5 + inPos] >>> 3 & 0x1FFFFF);
            out[9 + outPos] = ((in[5 + inPos] << 18 & 0x1FFFFF) | in[6 + inPos] >>> 14);
            out[10 + outPos] = ((in[6 + inPos] << 7 & 0x1FFFFF) | in[7 + inPos] >>> 25);
            out[11 + outPos] = (in[7 + inPos] >>> 4 & 0x1FFFFF);
            out[12 + outPos] = ((in[7 + inPos] << 17 & 0x1FFFFF) | in[8 + inPos] >>> 15);
            out[13 + outPos] = ((in[8 + inPos] << 6 & 0x1FFFFF) | in[9 + inPos] >>> 26);
            out[14 + outPos] = (in[9 + inPos] >>> 5 & 0x1FFFFF);
            out[15 + outPos] = ((in[9 + inPos] << 16 & 0x1FFFFF) | in[10 + inPos] >>> 16);
            out[16 + outPos] = ((in[10 + inPos] << 5 & 0x1FFFFF) | in[11 + inPos] >>> 27);
            out[17 + outPos] = (in[11 + inPos] >>> 6 & 0x1FFFFF);
            out[18 + outPos] = ((in[11 + inPos] << 15 & 0x1FFFFF) | in[12 + inPos] >>> 17);
            out[19 + outPos] = ((in[12 + inPos] << 4 & 0x1FFFFF) | in[13 + inPos] >>> 28);
            out[20 + outPos] = (in[13 + inPos] >>> 7 & 0x1FFFFF);
            out[21 + outPos] = ((in[13 + inPos] << 14 & 0x1FFFFF) | in[14 + inPos] >>> 18);
            out[22 + outPos] = ((in[14 + inPos] << 3 & 0x1FFFFF) | in[15 + inPos] >>> 29);
            out[23 + outPos] = (in[15 + inPos] >>> 8 & 0x1FFFFF);
            out[24 + outPos] = ((in[15 + inPos] << 13 & 0x1FFFFF) | in[16 + inPos] >>> 19);
            out[25 + outPos] = ((in[16 + inPos] << 2 & 0x1FFFFF) | in[17 + inPos] >>> 30);
            out[26 + outPos] = (in[17 + inPos] >>> 9 & 0x1FFFFF);
            out[27 + outPos] = ((in[17 + inPos] << 12 & 0x1FFFFF) | in[18 + inPos] >>> 20);
            out[28 + outPos] = ((in[18 + inPos] << 1 & 0x1FFFFF) | in[19 + inPos] >>> 31);
            out[29 + outPos] = (in[19 + inPos] >>> 10 & 0x1FFFFF);
            out[30 + outPos] = ((in[19 + inPos] << 11 & 0x1FFFFF) | in[20 + inPos] >>> 21);
            out[31 + outPos] = (in[20 + inPos] >>> 0 & 0x1FFFFF);
        }
    }
    
    private static final class Packer22 extends IntPacker
    {
        private Packer22() {
            super(22);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x3FFFFF) << 10 | (in[1 + inPos] & 0x3FFFFF) >>> 12);
            out[1 + outPos] = ((in[1 + inPos] & 0x3FFFFF) << 20 | (in[2 + inPos] & 0x3FFFFF) >>> 2);
            out[2 + outPos] = ((in[2 + inPos] & 0x3FFFFF) << 30 | (in[3 + inPos] & 0x3FFFFF) << 8 | (in[4 + inPos] & 0x3FFFFF) >>> 14);
            out[3 + outPos] = ((in[4 + inPos] & 0x3FFFFF) << 18 | (in[5 + inPos] & 0x3FFFFF) >>> 4);
            out[4 + outPos] = ((in[5 + inPos] & 0x3FFFFF) << 28 | (in[6 + inPos] & 0x3FFFFF) << 6 | (in[7 + inPos] & 0x3FFFFF) >>> 16);
            out[5 + outPos] = ((in[7 + inPos] & 0x3FFFFF) << 16 | (in[8 + inPos] & 0x3FFFFF) >>> 6);
            out[6 + outPos] = ((in[8 + inPos] & 0x3FFFFF) << 26 | (in[9 + inPos] & 0x3FFFFF) << 4 | (in[10 + inPos] & 0x3FFFFF) >>> 18);
            out[7 + outPos] = ((in[10 + inPos] & 0x3FFFFF) << 14 | (in[11 + inPos] & 0x3FFFFF) >>> 8);
            out[8 + outPos] = ((in[11 + inPos] & 0x3FFFFF) << 24 | (in[12 + inPos] & 0x3FFFFF) << 2 | (in[13 + inPos] & 0x3FFFFF) >>> 20);
            out[9 + outPos] = ((in[13 + inPos] & 0x3FFFFF) << 12 | (in[14 + inPos] & 0x3FFFFF) >>> 10);
            out[10 + outPos] = ((in[14 + inPos] & 0x3FFFFF) << 22 | (in[15 + inPos] & 0x3FFFFF) << 0);
            out[11 + outPos] = ((in[16 + inPos] & 0x3FFFFF) << 10 | (in[17 + inPos] & 0x3FFFFF) >>> 12);
            out[12 + outPos] = ((in[17 + inPos] & 0x3FFFFF) << 20 | (in[18 + inPos] & 0x3FFFFF) >>> 2);
            out[13 + outPos] = ((in[18 + inPos] & 0x3FFFFF) << 30 | (in[19 + inPos] & 0x3FFFFF) << 8 | (in[20 + inPos] & 0x3FFFFF) >>> 14);
            out[14 + outPos] = ((in[20 + inPos] & 0x3FFFFF) << 18 | (in[21 + inPos] & 0x3FFFFF) >>> 4);
            out[15 + outPos] = ((in[21 + inPos] & 0x3FFFFF) << 28 | (in[22 + inPos] & 0x3FFFFF) << 6 | (in[23 + inPos] & 0x3FFFFF) >>> 16);
            out[16 + outPos] = ((in[23 + inPos] & 0x3FFFFF) << 16 | (in[24 + inPos] & 0x3FFFFF) >>> 6);
            out[17 + outPos] = ((in[24 + inPos] & 0x3FFFFF) << 26 | (in[25 + inPos] & 0x3FFFFF) << 4 | (in[26 + inPos] & 0x3FFFFF) >>> 18);
            out[18 + outPos] = ((in[26 + inPos] & 0x3FFFFF) << 14 | (in[27 + inPos] & 0x3FFFFF) >>> 8);
            out[19 + outPos] = ((in[27 + inPos] & 0x3FFFFF) << 24 | (in[28 + inPos] & 0x3FFFFF) << 2 | (in[29 + inPos] & 0x3FFFFF) >>> 20);
            out[20 + outPos] = ((in[29 + inPos] & 0x3FFFFF) << 12 | (in[30 + inPos] & 0x3FFFFF) >>> 10);
            out[21 + outPos] = ((in[30 + inPos] & 0x3FFFFF) << 22 | (in[31 + inPos] & 0x3FFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 10 & 0x3FFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 12 & 0x3FFFFF) | in[1 + inPos] >>> 20);
            out[2 + outPos] = ((in[1 + inPos] << 2 & 0x3FFFFF) | in[2 + inPos] >>> 30);
            out[3 + outPos] = (in[2 + inPos] >>> 8 & 0x3FFFFF);
            out[4 + outPos] = ((in[2 + inPos] << 14 & 0x3FFFFF) | in[3 + inPos] >>> 18);
            out[5 + outPos] = ((in[3 + inPos] << 4 & 0x3FFFFF) | in[4 + inPos] >>> 28);
            out[6 + outPos] = (in[4 + inPos] >>> 6 & 0x3FFFFF);
            out[7 + outPos] = ((in[4 + inPos] << 16 & 0x3FFFFF) | in[5 + inPos] >>> 16);
            out[8 + outPos] = ((in[5 + inPos] << 6 & 0x3FFFFF) | in[6 + inPos] >>> 26);
            out[9 + outPos] = (in[6 + inPos] >>> 4 & 0x3FFFFF);
            out[10 + outPos] = ((in[6 + inPos] << 18 & 0x3FFFFF) | in[7 + inPos] >>> 14);
            out[11 + outPos] = ((in[7 + inPos] << 8 & 0x3FFFFF) | in[8 + inPos] >>> 24);
            out[12 + outPos] = (in[8 + inPos] >>> 2 & 0x3FFFFF);
            out[13 + outPos] = ((in[8 + inPos] << 20 & 0x3FFFFF) | in[9 + inPos] >>> 12);
            out[14 + outPos] = ((in[9 + inPos] << 10 & 0x3FFFFF) | in[10 + inPos] >>> 22);
            out[15 + outPos] = (in[10 + inPos] >>> 0 & 0x3FFFFF);
            out[16 + outPos] = (in[11 + inPos] >>> 10 & 0x3FFFFF);
            out[17 + outPos] = ((in[11 + inPos] << 12 & 0x3FFFFF) | in[12 + inPos] >>> 20);
            out[18 + outPos] = ((in[12 + inPos] << 2 & 0x3FFFFF) | in[13 + inPos] >>> 30);
            out[19 + outPos] = (in[13 + inPos] >>> 8 & 0x3FFFFF);
            out[20 + outPos] = ((in[13 + inPos] << 14 & 0x3FFFFF) | in[14 + inPos] >>> 18);
            out[21 + outPos] = ((in[14 + inPos] << 4 & 0x3FFFFF) | in[15 + inPos] >>> 28);
            out[22 + outPos] = (in[15 + inPos] >>> 6 & 0x3FFFFF);
            out[23 + outPos] = ((in[15 + inPos] << 16 & 0x3FFFFF) | in[16 + inPos] >>> 16);
            out[24 + outPos] = ((in[16 + inPos] << 6 & 0x3FFFFF) | in[17 + inPos] >>> 26);
            out[25 + outPos] = (in[17 + inPos] >>> 4 & 0x3FFFFF);
            out[26 + outPos] = ((in[17 + inPos] << 18 & 0x3FFFFF) | in[18 + inPos] >>> 14);
            out[27 + outPos] = ((in[18 + inPos] << 8 & 0x3FFFFF) | in[19 + inPos] >>> 24);
            out[28 + outPos] = (in[19 + inPos] >>> 2 & 0x3FFFFF);
            out[29 + outPos] = ((in[19 + inPos] << 20 & 0x3FFFFF) | in[20 + inPos] >>> 12);
            out[30 + outPos] = ((in[20 + inPos] << 10 & 0x3FFFFF) | in[21 + inPos] >>> 22);
            out[31 + outPos] = (in[21 + inPos] >>> 0 & 0x3FFFFF);
        }
    }
    
    private static final class Packer23 extends IntPacker
    {
        private Packer23() {
            super(23);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x7FFFFF) << 9 | (in[1 + inPos] & 0x7FFFFF) >>> 14);
            out[1 + outPos] = ((in[1 + inPos] & 0x7FFFFF) << 18 | (in[2 + inPos] & 0x7FFFFF) >>> 5);
            out[2 + outPos] = ((in[2 + inPos] & 0x7FFFFF) << 27 | (in[3 + inPos] & 0x7FFFFF) << 4 | (in[4 + inPos] & 0x7FFFFF) >>> 19);
            out[3 + outPos] = ((in[4 + inPos] & 0x7FFFFF) << 13 | (in[5 + inPos] & 0x7FFFFF) >>> 10);
            out[4 + outPos] = ((in[5 + inPos] & 0x7FFFFF) << 22 | (in[6 + inPos] & 0x7FFFFF) >>> 1);
            out[5 + outPos] = ((in[6 + inPos] & 0x7FFFFF) << 31 | (in[7 + inPos] & 0x7FFFFF) << 8 | (in[8 + inPos] & 0x7FFFFF) >>> 15);
            out[6 + outPos] = ((in[8 + inPos] & 0x7FFFFF) << 17 | (in[9 + inPos] & 0x7FFFFF) >>> 6);
            out[7 + outPos] = ((in[9 + inPos] & 0x7FFFFF) << 26 | (in[10 + inPos] & 0x7FFFFF) << 3 | (in[11 + inPos] & 0x7FFFFF) >>> 20);
            out[8 + outPos] = ((in[11 + inPos] & 0x7FFFFF) << 12 | (in[12 + inPos] & 0x7FFFFF) >>> 11);
            out[9 + outPos] = ((in[12 + inPos] & 0x7FFFFF) << 21 | (in[13 + inPos] & 0x7FFFFF) >>> 2);
            out[10 + outPos] = ((in[13 + inPos] & 0x7FFFFF) << 30 | (in[14 + inPos] & 0x7FFFFF) << 7 | (in[15 + inPos] & 0x7FFFFF) >>> 16);
            out[11 + outPos] = ((in[15 + inPos] & 0x7FFFFF) << 16 | (in[16 + inPos] & 0x7FFFFF) >>> 7);
            out[12 + outPos] = ((in[16 + inPos] & 0x7FFFFF) << 25 | (in[17 + inPos] & 0x7FFFFF) << 2 | (in[18 + inPos] & 0x7FFFFF) >>> 21);
            out[13 + outPos] = ((in[18 + inPos] & 0x7FFFFF) << 11 | (in[19 + inPos] & 0x7FFFFF) >>> 12);
            out[14 + outPos] = ((in[19 + inPos] & 0x7FFFFF) << 20 | (in[20 + inPos] & 0x7FFFFF) >>> 3);
            out[15 + outPos] = ((in[20 + inPos] & 0x7FFFFF) << 29 | (in[21 + inPos] & 0x7FFFFF) << 6 | (in[22 + inPos] & 0x7FFFFF) >>> 17);
            out[16 + outPos] = ((in[22 + inPos] & 0x7FFFFF) << 15 | (in[23 + inPos] & 0x7FFFFF) >>> 8);
            out[17 + outPos] = ((in[23 + inPos] & 0x7FFFFF) << 24 | (in[24 + inPos] & 0x7FFFFF) << 1 | (in[25 + inPos] & 0x7FFFFF) >>> 22);
            out[18 + outPos] = ((in[25 + inPos] & 0x7FFFFF) << 10 | (in[26 + inPos] & 0x7FFFFF) >>> 13);
            out[19 + outPos] = ((in[26 + inPos] & 0x7FFFFF) << 19 | (in[27 + inPos] & 0x7FFFFF) >>> 4);
            out[20 + outPos] = ((in[27 + inPos] & 0x7FFFFF) << 28 | (in[28 + inPos] & 0x7FFFFF) << 5 | (in[29 + inPos] & 0x7FFFFF) >>> 18);
            out[21 + outPos] = ((in[29 + inPos] & 0x7FFFFF) << 14 | (in[30 + inPos] & 0x7FFFFF) >>> 9);
            out[22 + outPos] = ((in[30 + inPos] & 0x7FFFFF) << 23 | (in[31 + inPos] & 0x7FFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 9 & 0x7FFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 14 & 0x7FFFFF) | in[1 + inPos] >>> 18);
            out[2 + outPos] = ((in[1 + inPos] << 5 & 0x7FFFFF) | in[2 + inPos] >>> 27);
            out[3 + outPos] = (in[2 + inPos] >>> 4 & 0x7FFFFF);
            out[4 + outPos] = ((in[2 + inPos] << 19 & 0x7FFFFF) | in[3 + inPos] >>> 13);
            out[5 + outPos] = ((in[3 + inPos] << 10 & 0x7FFFFF) | in[4 + inPos] >>> 22);
            out[6 + outPos] = ((in[4 + inPos] << 1 & 0x7FFFFF) | in[5 + inPos] >>> 31);
            out[7 + outPos] = (in[5 + inPos] >>> 8 & 0x7FFFFF);
            out[8 + outPos] = ((in[5 + inPos] << 15 & 0x7FFFFF) | in[6 + inPos] >>> 17);
            out[9 + outPos] = ((in[6 + inPos] << 6 & 0x7FFFFF) | in[7 + inPos] >>> 26);
            out[10 + outPos] = (in[7 + inPos] >>> 3 & 0x7FFFFF);
            out[11 + outPos] = ((in[7 + inPos] << 20 & 0x7FFFFF) | in[8 + inPos] >>> 12);
            out[12 + outPos] = ((in[8 + inPos] << 11 & 0x7FFFFF) | in[9 + inPos] >>> 21);
            out[13 + outPos] = ((in[9 + inPos] << 2 & 0x7FFFFF) | in[10 + inPos] >>> 30);
            out[14 + outPos] = (in[10 + inPos] >>> 7 & 0x7FFFFF);
            out[15 + outPos] = ((in[10 + inPos] << 16 & 0x7FFFFF) | in[11 + inPos] >>> 16);
            out[16 + outPos] = ((in[11 + inPos] << 7 & 0x7FFFFF) | in[12 + inPos] >>> 25);
            out[17 + outPos] = (in[12 + inPos] >>> 2 & 0x7FFFFF);
            out[18 + outPos] = ((in[12 + inPos] << 21 & 0x7FFFFF) | in[13 + inPos] >>> 11);
            out[19 + outPos] = ((in[13 + inPos] << 12 & 0x7FFFFF) | in[14 + inPos] >>> 20);
            out[20 + outPos] = ((in[14 + inPos] << 3 & 0x7FFFFF) | in[15 + inPos] >>> 29);
            out[21 + outPos] = (in[15 + inPos] >>> 6 & 0x7FFFFF);
            out[22 + outPos] = ((in[15 + inPos] << 17 & 0x7FFFFF) | in[16 + inPos] >>> 15);
            out[23 + outPos] = ((in[16 + inPos] << 8 & 0x7FFFFF) | in[17 + inPos] >>> 24);
            out[24 + outPos] = (in[17 + inPos] >>> 1 & 0x7FFFFF);
            out[25 + outPos] = ((in[17 + inPos] << 22 & 0x7FFFFF) | in[18 + inPos] >>> 10);
            out[26 + outPos] = ((in[18 + inPos] << 13 & 0x7FFFFF) | in[19 + inPos] >>> 19);
            out[27 + outPos] = ((in[19 + inPos] << 4 & 0x7FFFFF) | in[20 + inPos] >>> 28);
            out[28 + outPos] = (in[20 + inPos] >>> 5 & 0x7FFFFF);
            out[29 + outPos] = ((in[20 + inPos] << 18 & 0x7FFFFF) | in[21 + inPos] >>> 14);
            out[30 + outPos] = ((in[21 + inPos] << 9 & 0x7FFFFF) | in[22 + inPos] >>> 23);
            out[31 + outPos] = (in[22 + inPos] >>> 0 & 0x7FFFFF);
        }
    }
    
    private static final class Packer24 extends IntPacker
    {
        private Packer24() {
            super(24);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFFFFFF) << 8 | (in[1 + inPos] & 0xFFFFFF) >>> 16);
            out[1 + outPos] = ((in[1 + inPos] & 0xFFFFFF) << 16 | (in[2 + inPos] & 0xFFFFFF) >>> 8);
            out[2 + outPos] = ((in[2 + inPos] & 0xFFFFFF) << 24 | (in[3 + inPos] & 0xFFFFFF) << 0);
            out[3 + outPos] = ((in[4 + inPos] & 0xFFFFFF) << 8 | (in[5 + inPos] & 0xFFFFFF) >>> 16);
            out[4 + outPos] = ((in[5 + inPos] & 0xFFFFFF) << 16 | (in[6 + inPos] & 0xFFFFFF) >>> 8);
            out[5 + outPos] = ((in[6 + inPos] & 0xFFFFFF) << 24 | (in[7 + inPos] & 0xFFFFFF) << 0);
            out[6 + outPos] = ((in[8 + inPos] & 0xFFFFFF) << 8 | (in[9 + inPos] & 0xFFFFFF) >>> 16);
            out[7 + outPos] = ((in[9 + inPos] & 0xFFFFFF) << 16 | (in[10 + inPos] & 0xFFFFFF) >>> 8);
            out[8 + outPos] = ((in[10 + inPos] & 0xFFFFFF) << 24 | (in[11 + inPos] & 0xFFFFFF) << 0);
            out[9 + outPos] = ((in[12 + inPos] & 0xFFFFFF) << 8 | (in[13 + inPos] & 0xFFFFFF) >>> 16);
            out[10 + outPos] = ((in[13 + inPos] & 0xFFFFFF) << 16 | (in[14 + inPos] & 0xFFFFFF) >>> 8);
            out[11 + outPos] = ((in[14 + inPos] & 0xFFFFFF) << 24 | (in[15 + inPos] & 0xFFFFFF) << 0);
            out[12 + outPos] = ((in[16 + inPos] & 0xFFFFFF) << 8 | (in[17 + inPos] & 0xFFFFFF) >>> 16);
            out[13 + outPos] = ((in[17 + inPos] & 0xFFFFFF) << 16 | (in[18 + inPos] & 0xFFFFFF) >>> 8);
            out[14 + outPos] = ((in[18 + inPos] & 0xFFFFFF) << 24 | (in[19 + inPos] & 0xFFFFFF) << 0);
            out[15 + outPos] = ((in[20 + inPos] & 0xFFFFFF) << 8 | (in[21 + inPos] & 0xFFFFFF) >>> 16);
            out[16 + outPos] = ((in[21 + inPos] & 0xFFFFFF) << 16 | (in[22 + inPos] & 0xFFFFFF) >>> 8);
            out[17 + outPos] = ((in[22 + inPos] & 0xFFFFFF) << 24 | (in[23 + inPos] & 0xFFFFFF) << 0);
            out[18 + outPos] = ((in[24 + inPos] & 0xFFFFFF) << 8 | (in[25 + inPos] & 0xFFFFFF) >>> 16);
            out[19 + outPos] = ((in[25 + inPos] & 0xFFFFFF) << 16 | (in[26 + inPos] & 0xFFFFFF) >>> 8);
            out[20 + outPos] = ((in[26 + inPos] & 0xFFFFFF) << 24 | (in[27 + inPos] & 0xFFFFFF) << 0);
            out[21 + outPos] = ((in[28 + inPos] & 0xFFFFFF) << 8 | (in[29 + inPos] & 0xFFFFFF) >>> 16);
            out[22 + outPos] = ((in[29 + inPos] & 0xFFFFFF) << 16 | (in[30 + inPos] & 0xFFFFFF) >>> 8);
            out[23 + outPos] = ((in[30 + inPos] & 0xFFFFFF) << 24 | (in[31 + inPos] & 0xFFFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 8 & 0xFFFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 16 & 0xFFFFFF) | in[1 + inPos] >>> 16);
            out[2 + outPos] = ((in[1 + inPos] << 8 & 0xFFFFFF) | in[2 + inPos] >>> 24);
            out[3 + outPos] = (in[2 + inPos] >>> 0 & 0xFFFFFF);
            out[4 + outPos] = (in[3 + inPos] >>> 8 & 0xFFFFFF);
            out[5 + outPos] = ((in[3 + inPos] << 16 & 0xFFFFFF) | in[4 + inPos] >>> 16);
            out[6 + outPos] = ((in[4 + inPos] << 8 & 0xFFFFFF) | in[5 + inPos] >>> 24);
            out[7 + outPos] = (in[5 + inPos] >>> 0 & 0xFFFFFF);
            out[8 + outPos] = (in[6 + inPos] >>> 8 & 0xFFFFFF);
            out[9 + outPos] = ((in[6 + inPos] << 16 & 0xFFFFFF) | in[7 + inPos] >>> 16);
            out[10 + outPos] = ((in[7 + inPos] << 8 & 0xFFFFFF) | in[8 + inPos] >>> 24);
            out[11 + outPos] = (in[8 + inPos] >>> 0 & 0xFFFFFF);
            out[12 + outPos] = (in[9 + inPos] >>> 8 & 0xFFFFFF);
            out[13 + outPos] = ((in[9 + inPos] << 16 & 0xFFFFFF) | in[10 + inPos] >>> 16);
            out[14 + outPos] = ((in[10 + inPos] << 8 & 0xFFFFFF) | in[11 + inPos] >>> 24);
            out[15 + outPos] = (in[11 + inPos] >>> 0 & 0xFFFFFF);
            out[16 + outPos] = (in[12 + inPos] >>> 8 & 0xFFFFFF);
            out[17 + outPos] = ((in[12 + inPos] << 16 & 0xFFFFFF) | in[13 + inPos] >>> 16);
            out[18 + outPos] = ((in[13 + inPos] << 8 & 0xFFFFFF) | in[14 + inPos] >>> 24);
            out[19 + outPos] = (in[14 + inPos] >>> 0 & 0xFFFFFF);
            out[20 + outPos] = (in[15 + inPos] >>> 8 & 0xFFFFFF);
            out[21 + outPos] = ((in[15 + inPos] << 16 & 0xFFFFFF) | in[16 + inPos] >>> 16);
            out[22 + outPos] = ((in[16 + inPos] << 8 & 0xFFFFFF) | in[17 + inPos] >>> 24);
            out[23 + outPos] = (in[17 + inPos] >>> 0 & 0xFFFFFF);
            out[24 + outPos] = (in[18 + inPos] >>> 8 & 0xFFFFFF);
            out[25 + outPos] = ((in[18 + inPos] << 16 & 0xFFFFFF) | in[19 + inPos] >>> 16);
            out[26 + outPos] = ((in[19 + inPos] << 8 & 0xFFFFFF) | in[20 + inPos] >>> 24);
            out[27 + outPos] = (in[20 + inPos] >>> 0 & 0xFFFFFF);
            out[28 + outPos] = (in[21 + inPos] >>> 8 & 0xFFFFFF);
            out[29 + outPos] = ((in[21 + inPos] << 16 & 0xFFFFFF) | in[22 + inPos] >>> 16);
            out[30 + outPos] = ((in[22 + inPos] << 8 & 0xFFFFFF) | in[23 + inPos] >>> 24);
            out[31 + outPos] = (in[23 + inPos] >>> 0 & 0xFFFFFF);
        }
    }
    
    private static final class Packer25 extends IntPacker
    {
        private Packer25() {
            super(25);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x1FFFFFF) << 7 | (in[1 + inPos] & 0x1FFFFFF) >>> 18);
            out[1 + outPos] = ((in[1 + inPos] & 0x1FFFFFF) << 14 | (in[2 + inPos] & 0x1FFFFFF) >>> 11);
            out[2 + outPos] = ((in[2 + inPos] & 0x1FFFFFF) << 21 | (in[3 + inPos] & 0x1FFFFFF) >>> 4);
            out[3 + outPos] = ((in[3 + inPos] & 0x1FFFFFF) << 28 | (in[4 + inPos] & 0x1FFFFFF) << 3 | (in[5 + inPos] & 0x1FFFFFF) >>> 22);
            out[4 + outPos] = ((in[5 + inPos] & 0x1FFFFFF) << 10 | (in[6 + inPos] & 0x1FFFFFF) >>> 15);
            out[5 + outPos] = ((in[6 + inPos] & 0x1FFFFFF) << 17 | (in[7 + inPos] & 0x1FFFFFF) >>> 8);
            out[6 + outPos] = ((in[7 + inPos] & 0x1FFFFFF) << 24 | (in[8 + inPos] & 0x1FFFFFF) >>> 1);
            out[7 + outPos] = ((in[8 + inPos] & 0x1FFFFFF) << 31 | (in[9 + inPos] & 0x1FFFFFF) << 6 | (in[10 + inPos] & 0x1FFFFFF) >>> 19);
            out[8 + outPos] = ((in[10 + inPos] & 0x1FFFFFF) << 13 | (in[11 + inPos] & 0x1FFFFFF) >>> 12);
            out[9 + outPos] = ((in[11 + inPos] & 0x1FFFFFF) << 20 | (in[12 + inPos] & 0x1FFFFFF) >>> 5);
            out[10 + outPos] = ((in[12 + inPos] & 0x1FFFFFF) << 27 | (in[13 + inPos] & 0x1FFFFFF) << 2 | (in[14 + inPos] & 0x1FFFFFF) >>> 23);
            out[11 + outPos] = ((in[14 + inPos] & 0x1FFFFFF) << 9 | (in[15 + inPos] & 0x1FFFFFF) >>> 16);
            out[12 + outPos] = ((in[15 + inPos] & 0x1FFFFFF) << 16 | (in[16 + inPos] & 0x1FFFFFF) >>> 9);
            out[13 + outPos] = ((in[16 + inPos] & 0x1FFFFFF) << 23 | (in[17 + inPos] & 0x1FFFFFF) >>> 2);
            out[14 + outPos] = ((in[17 + inPos] & 0x1FFFFFF) << 30 | (in[18 + inPos] & 0x1FFFFFF) << 5 | (in[19 + inPos] & 0x1FFFFFF) >>> 20);
            out[15 + outPos] = ((in[19 + inPos] & 0x1FFFFFF) << 12 | (in[20 + inPos] & 0x1FFFFFF) >>> 13);
            out[16 + outPos] = ((in[20 + inPos] & 0x1FFFFFF) << 19 | (in[21 + inPos] & 0x1FFFFFF) >>> 6);
            out[17 + outPos] = ((in[21 + inPos] & 0x1FFFFFF) << 26 | (in[22 + inPos] & 0x1FFFFFF) << 1 | (in[23 + inPos] & 0x1FFFFFF) >>> 24);
            out[18 + outPos] = ((in[23 + inPos] & 0x1FFFFFF) << 8 | (in[24 + inPos] & 0x1FFFFFF) >>> 17);
            out[19 + outPos] = ((in[24 + inPos] & 0x1FFFFFF) << 15 | (in[25 + inPos] & 0x1FFFFFF) >>> 10);
            out[20 + outPos] = ((in[25 + inPos] & 0x1FFFFFF) << 22 | (in[26 + inPos] & 0x1FFFFFF) >>> 3);
            out[21 + outPos] = ((in[26 + inPos] & 0x1FFFFFF) << 29 | (in[27 + inPos] & 0x1FFFFFF) << 4 | (in[28 + inPos] & 0x1FFFFFF) >>> 21);
            out[22 + outPos] = ((in[28 + inPos] & 0x1FFFFFF) << 11 | (in[29 + inPos] & 0x1FFFFFF) >>> 14);
            out[23 + outPos] = ((in[29 + inPos] & 0x1FFFFFF) << 18 | (in[30 + inPos] & 0x1FFFFFF) >>> 7);
            out[24 + outPos] = ((in[30 + inPos] & 0x1FFFFFF) << 25 | (in[31 + inPos] & 0x1FFFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 7 & 0x1FFFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 18 & 0x1FFFFFF) | in[1 + inPos] >>> 14);
            out[2 + outPos] = ((in[1 + inPos] << 11 & 0x1FFFFFF) | in[2 + inPos] >>> 21);
            out[3 + outPos] = ((in[2 + inPos] << 4 & 0x1FFFFFF) | in[3 + inPos] >>> 28);
            out[4 + outPos] = (in[3 + inPos] >>> 3 & 0x1FFFFFF);
            out[5 + outPos] = ((in[3 + inPos] << 22 & 0x1FFFFFF) | in[4 + inPos] >>> 10);
            out[6 + outPos] = ((in[4 + inPos] << 15 & 0x1FFFFFF) | in[5 + inPos] >>> 17);
            out[7 + outPos] = ((in[5 + inPos] << 8 & 0x1FFFFFF) | in[6 + inPos] >>> 24);
            out[8 + outPos] = ((in[6 + inPos] << 1 & 0x1FFFFFF) | in[7 + inPos] >>> 31);
            out[9 + outPos] = (in[7 + inPos] >>> 6 & 0x1FFFFFF);
            out[10 + outPos] = ((in[7 + inPos] << 19 & 0x1FFFFFF) | in[8 + inPos] >>> 13);
            out[11 + outPos] = ((in[8 + inPos] << 12 & 0x1FFFFFF) | in[9 + inPos] >>> 20);
            out[12 + outPos] = ((in[9 + inPos] << 5 & 0x1FFFFFF) | in[10 + inPos] >>> 27);
            out[13 + outPos] = (in[10 + inPos] >>> 2 & 0x1FFFFFF);
            out[14 + outPos] = ((in[10 + inPos] << 23 & 0x1FFFFFF) | in[11 + inPos] >>> 9);
            out[15 + outPos] = ((in[11 + inPos] << 16 & 0x1FFFFFF) | in[12 + inPos] >>> 16);
            out[16 + outPos] = ((in[12 + inPos] << 9 & 0x1FFFFFF) | in[13 + inPos] >>> 23);
            out[17 + outPos] = ((in[13 + inPos] << 2 & 0x1FFFFFF) | in[14 + inPos] >>> 30);
            out[18 + outPos] = (in[14 + inPos] >>> 5 & 0x1FFFFFF);
            out[19 + outPos] = ((in[14 + inPos] << 20 & 0x1FFFFFF) | in[15 + inPos] >>> 12);
            out[20 + outPos] = ((in[15 + inPos] << 13 & 0x1FFFFFF) | in[16 + inPos] >>> 19);
            out[21 + outPos] = ((in[16 + inPos] << 6 & 0x1FFFFFF) | in[17 + inPos] >>> 26);
            out[22 + outPos] = (in[17 + inPos] >>> 1 & 0x1FFFFFF);
            out[23 + outPos] = ((in[17 + inPos] << 24 & 0x1FFFFFF) | in[18 + inPos] >>> 8);
            out[24 + outPos] = ((in[18 + inPos] << 17 & 0x1FFFFFF) | in[19 + inPos] >>> 15);
            out[25 + outPos] = ((in[19 + inPos] << 10 & 0x1FFFFFF) | in[20 + inPos] >>> 22);
            out[26 + outPos] = ((in[20 + inPos] << 3 & 0x1FFFFFF) | in[21 + inPos] >>> 29);
            out[27 + outPos] = (in[21 + inPos] >>> 4 & 0x1FFFFFF);
            out[28 + outPos] = ((in[21 + inPos] << 21 & 0x1FFFFFF) | in[22 + inPos] >>> 11);
            out[29 + outPos] = ((in[22 + inPos] << 14 & 0x1FFFFFF) | in[23 + inPos] >>> 18);
            out[30 + outPos] = ((in[23 + inPos] << 7 & 0x1FFFFFF) | in[24 + inPos] >>> 25);
            out[31 + outPos] = (in[24 + inPos] >>> 0 & 0x1FFFFFF);
        }
    }
    
    private static final class Packer26 extends IntPacker
    {
        private Packer26() {
            super(26);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x3FFFFFF) << 6 | (in[1 + inPos] & 0x3FFFFFF) >>> 20);
            out[1 + outPos] = ((in[1 + inPos] & 0x3FFFFFF) << 12 | (in[2 + inPos] & 0x3FFFFFF) >>> 14);
            out[2 + outPos] = ((in[2 + inPos] & 0x3FFFFFF) << 18 | (in[3 + inPos] & 0x3FFFFFF) >>> 8);
            out[3 + outPos] = ((in[3 + inPos] & 0x3FFFFFF) << 24 | (in[4 + inPos] & 0x3FFFFFF) >>> 2);
            out[4 + outPos] = ((in[4 + inPos] & 0x3FFFFFF) << 30 | (in[5 + inPos] & 0x3FFFFFF) << 4 | (in[6 + inPos] & 0x3FFFFFF) >>> 22);
            out[5 + outPos] = ((in[6 + inPos] & 0x3FFFFFF) << 10 | (in[7 + inPos] & 0x3FFFFFF) >>> 16);
            out[6 + outPos] = ((in[7 + inPos] & 0x3FFFFFF) << 16 | (in[8 + inPos] & 0x3FFFFFF) >>> 10);
            out[7 + outPos] = ((in[8 + inPos] & 0x3FFFFFF) << 22 | (in[9 + inPos] & 0x3FFFFFF) >>> 4);
            out[8 + outPos] = ((in[9 + inPos] & 0x3FFFFFF) << 28 | (in[10 + inPos] & 0x3FFFFFF) << 2 | (in[11 + inPos] & 0x3FFFFFF) >>> 24);
            out[9 + outPos] = ((in[11 + inPos] & 0x3FFFFFF) << 8 | (in[12 + inPos] & 0x3FFFFFF) >>> 18);
            out[10 + outPos] = ((in[12 + inPos] & 0x3FFFFFF) << 14 | (in[13 + inPos] & 0x3FFFFFF) >>> 12);
            out[11 + outPos] = ((in[13 + inPos] & 0x3FFFFFF) << 20 | (in[14 + inPos] & 0x3FFFFFF) >>> 6);
            out[12 + outPos] = ((in[14 + inPos] & 0x3FFFFFF) << 26 | (in[15 + inPos] & 0x3FFFFFF) << 0);
            out[13 + outPos] = ((in[16 + inPos] & 0x3FFFFFF) << 6 | (in[17 + inPos] & 0x3FFFFFF) >>> 20);
            out[14 + outPos] = ((in[17 + inPos] & 0x3FFFFFF) << 12 | (in[18 + inPos] & 0x3FFFFFF) >>> 14);
            out[15 + outPos] = ((in[18 + inPos] & 0x3FFFFFF) << 18 | (in[19 + inPos] & 0x3FFFFFF) >>> 8);
            out[16 + outPos] = ((in[19 + inPos] & 0x3FFFFFF) << 24 | (in[20 + inPos] & 0x3FFFFFF) >>> 2);
            out[17 + outPos] = ((in[20 + inPos] & 0x3FFFFFF) << 30 | (in[21 + inPos] & 0x3FFFFFF) << 4 | (in[22 + inPos] & 0x3FFFFFF) >>> 22);
            out[18 + outPos] = ((in[22 + inPos] & 0x3FFFFFF) << 10 | (in[23 + inPos] & 0x3FFFFFF) >>> 16);
            out[19 + outPos] = ((in[23 + inPos] & 0x3FFFFFF) << 16 | (in[24 + inPos] & 0x3FFFFFF) >>> 10);
            out[20 + outPos] = ((in[24 + inPos] & 0x3FFFFFF) << 22 | (in[25 + inPos] & 0x3FFFFFF) >>> 4);
            out[21 + outPos] = ((in[25 + inPos] & 0x3FFFFFF) << 28 | (in[26 + inPos] & 0x3FFFFFF) << 2 | (in[27 + inPos] & 0x3FFFFFF) >>> 24);
            out[22 + outPos] = ((in[27 + inPos] & 0x3FFFFFF) << 8 | (in[28 + inPos] & 0x3FFFFFF) >>> 18);
            out[23 + outPos] = ((in[28 + inPos] & 0x3FFFFFF) << 14 | (in[29 + inPos] & 0x3FFFFFF) >>> 12);
            out[24 + outPos] = ((in[29 + inPos] & 0x3FFFFFF) << 20 | (in[30 + inPos] & 0x3FFFFFF) >>> 6);
            out[25 + outPos] = ((in[30 + inPos] & 0x3FFFFFF) << 26 | (in[31 + inPos] & 0x3FFFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 6 & 0x3FFFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 20 & 0x3FFFFFF) | in[1 + inPos] >>> 12);
            out[2 + outPos] = ((in[1 + inPos] << 14 & 0x3FFFFFF) | in[2 + inPos] >>> 18);
            out[3 + outPos] = ((in[2 + inPos] << 8 & 0x3FFFFFF) | in[3 + inPos] >>> 24);
            out[4 + outPos] = ((in[3 + inPos] << 2 & 0x3FFFFFF) | in[4 + inPos] >>> 30);
            out[5 + outPos] = (in[4 + inPos] >>> 4 & 0x3FFFFFF);
            out[6 + outPos] = ((in[4 + inPos] << 22 & 0x3FFFFFF) | in[5 + inPos] >>> 10);
            out[7 + outPos] = ((in[5 + inPos] << 16 & 0x3FFFFFF) | in[6 + inPos] >>> 16);
            out[8 + outPos] = ((in[6 + inPos] << 10 & 0x3FFFFFF) | in[7 + inPos] >>> 22);
            out[9 + outPos] = ((in[7 + inPos] << 4 & 0x3FFFFFF) | in[8 + inPos] >>> 28);
            out[10 + outPos] = (in[8 + inPos] >>> 2 & 0x3FFFFFF);
            out[11 + outPos] = ((in[8 + inPos] << 24 & 0x3FFFFFF) | in[9 + inPos] >>> 8);
            out[12 + outPos] = ((in[9 + inPos] << 18 & 0x3FFFFFF) | in[10 + inPos] >>> 14);
            out[13 + outPos] = ((in[10 + inPos] << 12 & 0x3FFFFFF) | in[11 + inPos] >>> 20);
            out[14 + outPos] = ((in[11 + inPos] << 6 & 0x3FFFFFF) | in[12 + inPos] >>> 26);
            out[15 + outPos] = (in[12 + inPos] >>> 0 & 0x3FFFFFF);
            out[16 + outPos] = (in[13 + inPos] >>> 6 & 0x3FFFFFF);
            out[17 + outPos] = ((in[13 + inPos] << 20 & 0x3FFFFFF) | in[14 + inPos] >>> 12);
            out[18 + outPos] = ((in[14 + inPos] << 14 & 0x3FFFFFF) | in[15 + inPos] >>> 18);
            out[19 + outPos] = ((in[15 + inPos] << 8 & 0x3FFFFFF) | in[16 + inPos] >>> 24);
            out[20 + outPos] = ((in[16 + inPos] << 2 & 0x3FFFFFF) | in[17 + inPos] >>> 30);
            out[21 + outPos] = (in[17 + inPos] >>> 4 & 0x3FFFFFF);
            out[22 + outPos] = ((in[17 + inPos] << 22 & 0x3FFFFFF) | in[18 + inPos] >>> 10);
            out[23 + outPos] = ((in[18 + inPos] << 16 & 0x3FFFFFF) | in[19 + inPos] >>> 16);
            out[24 + outPos] = ((in[19 + inPos] << 10 & 0x3FFFFFF) | in[20 + inPos] >>> 22);
            out[25 + outPos] = ((in[20 + inPos] << 4 & 0x3FFFFFF) | in[21 + inPos] >>> 28);
            out[26 + outPos] = (in[21 + inPos] >>> 2 & 0x3FFFFFF);
            out[27 + outPos] = ((in[21 + inPos] << 24 & 0x3FFFFFF) | in[22 + inPos] >>> 8);
            out[28 + outPos] = ((in[22 + inPos] << 18 & 0x3FFFFFF) | in[23 + inPos] >>> 14);
            out[29 + outPos] = ((in[23 + inPos] << 12 & 0x3FFFFFF) | in[24 + inPos] >>> 20);
            out[30 + outPos] = ((in[24 + inPos] << 6 & 0x3FFFFFF) | in[25 + inPos] >>> 26);
            out[31 + outPos] = (in[25 + inPos] >>> 0 & 0x3FFFFFF);
        }
    }
    
    private static final class Packer27 extends IntPacker
    {
        private Packer27() {
            super(27);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x7FFFFFF) << 5 | (in[1 + inPos] & 0x7FFFFFF) >>> 22);
            out[1 + outPos] = ((in[1 + inPos] & 0x7FFFFFF) << 10 | (in[2 + inPos] & 0x7FFFFFF) >>> 17);
            out[2 + outPos] = ((in[2 + inPos] & 0x7FFFFFF) << 15 | (in[3 + inPos] & 0x7FFFFFF) >>> 12);
            out[3 + outPos] = ((in[3 + inPos] & 0x7FFFFFF) << 20 | (in[4 + inPos] & 0x7FFFFFF) >>> 7);
            out[4 + outPos] = ((in[4 + inPos] & 0x7FFFFFF) << 25 | (in[5 + inPos] & 0x7FFFFFF) >>> 2);
            out[5 + outPos] = ((in[5 + inPos] & 0x7FFFFFF) << 30 | (in[6 + inPos] & 0x7FFFFFF) << 3 | (in[7 + inPos] & 0x7FFFFFF) >>> 24);
            out[6 + outPos] = ((in[7 + inPos] & 0x7FFFFFF) << 8 | (in[8 + inPos] & 0x7FFFFFF) >>> 19);
            out[7 + outPos] = ((in[8 + inPos] & 0x7FFFFFF) << 13 | (in[9 + inPos] & 0x7FFFFFF) >>> 14);
            out[8 + outPos] = ((in[9 + inPos] & 0x7FFFFFF) << 18 | (in[10 + inPos] & 0x7FFFFFF) >>> 9);
            out[9 + outPos] = ((in[10 + inPos] & 0x7FFFFFF) << 23 | (in[11 + inPos] & 0x7FFFFFF) >>> 4);
            out[10 + outPos] = ((in[11 + inPos] & 0x7FFFFFF) << 28 | (in[12 + inPos] & 0x7FFFFFF) << 1 | (in[13 + inPos] & 0x7FFFFFF) >>> 26);
            out[11 + outPos] = ((in[13 + inPos] & 0x7FFFFFF) << 6 | (in[14 + inPos] & 0x7FFFFFF) >>> 21);
            out[12 + outPos] = ((in[14 + inPos] & 0x7FFFFFF) << 11 | (in[15 + inPos] & 0x7FFFFFF) >>> 16);
            out[13 + outPos] = ((in[15 + inPos] & 0x7FFFFFF) << 16 | (in[16 + inPos] & 0x7FFFFFF) >>> 11);
            out[14 + outPos] = ((in[16 + inPos] & 0x7FFFFFF) << 21 | (in[17 + inPos] & 0x7FFFFFF) >>> 6);
            out[15 + outPos] = ((in[17 + inPos] & 0x7FFFFFF) << 26 | (in[18 + inPos] & 0x7FFFFFF) >>> 1);
            out[16 + outPos] = ((in[18 + inPos] & 0x7FFFFFF) << 31 | (in[19 + inPos] & 0x7FFFFFF) << 4 | (in[20 + inPos] & 0x7FFFFFF) >>> 23);
            out[17 + outPos] = ((in[20 + inPos] & 0x7FFFFFF) << 9 | (in[21 + inPos] & 0x7FFFFFF) >>> 18);
            out[18 + outPos] = ((in[21 + inPos] & 0x7FFFFFF) << 14 | (in[22 + inPos] & 0x7FFFFFF) >>> 13);
            out[19 + outPos] = ((in[22 + inPos] & 0x7FFFFFF) << 19 | (in[23 + inPos] & 0x7FFFFFF) >>> 8);
            out[20 + outPos] = ((in[23 + inPos] & 0x7FFFFFF) << 24 | (in[24 + inPos] & 0x7FFFFFF) >>> 3);
            out[21 + outPos] = ((in[24 + inPos] & 0x7FFFFFF) << 29 | (in[25 + inPos] & 0x7FFFFFF) << 2 | (in[26 + inPos] & 0x7FFFFFF) >>> 25);
            out[22 + outPos] = ((in[26 + inPos] & 0x7FFFFFF) << 7 | (in[27 + inPos] & 0x7FFFFFF) >>> 20);
            out[23 + outPos] = ((in[27 + inPos] & 0x7FFFFFF) << 12 | (in[28 + inPos] & 0x7FFFFFF) >>> 15);
            out[24 + outPos] = ((in[28 + inPos] & 0x7FFFFFF) << 17 | (in[29 + inPos] & 0x7FFFFFF) >>> 10);
            out[25 + outPos] = ((in[29 + inPos] & 0x7FFFFFF) << 22 | (in[30 + inPos] & 0x7FFFFFF) >>> 5);
            out[26 + outPos] = ((in[30 + inPos] & 0x7FFFFFF) << 27 | (in[31 + inPos] & 0x7FFFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 5 & 0x7FFFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 22 & 0x7FFFFFF) | in[1 + inPos] >>> 10);
            out[2 + outPos] = ((in[1 + inPos] << 17 & 0x7FFFFFF) | in[2 + inPos] >>> 15);
            out[3 + outPos] = ((in[2 + inPos] << 12 & 0x7FFFFFF) | in[3 + inPos] >>> 20);
            out[4 + outPos] = ((in[3 + inPos] << 7 & 0x7FFFFFF) | in[4 + inPos] >>> 25);
            out[5 + outPos] = ((in[4 + inPos] << 2 & 0x7FFFFFF) | in[5 + inPos] >>> 30);
            out[6 + outPos] = (in[5 + inPos] >>> 3 & 0x7FFFFFF);
            out[7 + outPos] = ((in[5 + inPos] << 24 & 0x7FFFFFF) | in[6 + inPos] >>> 8);
            out[8 + outPos] = ((in[6 + inPos] << 19 & 0x7FFFFFF) | in[7 + inPos] >>> 13);
            out[9 + outPos] = ((in[7 + inPos] << 14 & 0x7FFFFFF) | in[8 + inPos] >>> 18);
            out[10 + outPos] = ((in[8 + inPos] << 9 & 0x7FFFFFF) | in[9 + inPos] >>> 23);
            out[11 + outPos] = ((in[9 + inPos] << 4 & 0x7FFFFFF) | in[10 + inPos] >>> 28);
            out[12 + outPos] = (in[10 + inPos] >>> 1 & 0x7FFFFFF);
            out[13 + outPos] = ((in[10 + inPos] << 26 & 0x7FFFFFF) | in[11 + inPos] >>> 6);
            out[14 + outPos] = ((in[11 + inPos] << 21 & 0x7FFFFFF) | in[12 + inPos] >>> 11);
            out[15 + outPos] = ((in[12 + inPos] << 16 & 0x7FFFFFF) | in[13 + inPos] >>> 16);
            out[16 + outPos] = ((in[13 + inPos] << 11 & 0x7FFFFFF) | in[14 + inPos] >>> 21);
            out[17 + outPos] = ((in[14 + inPos] << 6 & 0x7FFFFFF) | in[15 + inPos] >>> 26);
            out[18 + outPos] = ((in[15 + inPos] << 1 & 0x7FFFFFF) | in[16 + inPos] >>> 31);
            out[19 + outPos] = (in[16 + inPos] >>> 4 & 0x7FFFFFF);
            out[20 + outPos] = ((in[16 + inPos] << 23 & 0x7FFFFFF) | in[17 + inPos] >>> 9);
            out[21 + outPos] = ((in[17 + inPos] << 18 & 0x7FFFFFF) | in[18 + inPos] >>> 14);
            out[22 + outPos] = ((in[18 + inPos] << 13 & 0x7FFFFFF) | in[19 + inPos] >>> 19);
            out[23 + outPos] = ((in[19 + inPos] << 8 & 0x7FFFFFF) | in[20 + inPos] >>> 24);
            out[24 + outPos] = ((in[20 + inPos] << 3 & 0x7FFFFFF) | in[21 + inPos] >>> 29);
            out[25 + outPos] = (in[21 + inPos] >>> 2 & 0x7FFFFFF);
            out[26 + outPos] = ((in[21 + inPos] << 25 & 0x7FFFFFF) | in[22 + inPos] >>> 7);
            out[27 + outPos] = ((in[22 + inPos] << 20 & 0x7FFFFFF) | in[23 + inPos] >>> 12);
            out[28 + outPos] = ((in[23 + inPos] << 15 & 0x7FFFFFF) | in[24 + inPos] >>> 17);
            out[29 + outPos] = ((in[24 + inPos] << 10 & 0x7FFFFFF) | in[25 + inPos] >>> 22);
            out[30 + outPos] = ((in[25 + inPos] << 5 & 0x7FFFFFF) | in[26 + inPos] >>> 27);
            out[31 + outPos] = (in[26 + inPos] >>> 0 & 0x7FFFFFF);
        }
    }
    
    private static final class Packer28 extends IntPacker
    {
        private Packer28() {
            super(28);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFFFFFFF) << 4 | (in[1 + inPos] & 0xFFFFFFF) >>> 24);
            out[1 + outPos] = ((in[1 + inPos] & 0xFFFFFFF) << 8 | (in[2 + inPos] & 0xFFFFFFF) >>> 20);
            out[2 + outPos] = ((in[2 + inPos] & 0xFFFFFFF) << 12 | (in[3 + inPos] & 0xFFFFFFF) >>> 16);
            out[3 + outPos] = ((in[3 + inPos] & 0xFFFFFFF) << 16 | (in[4 + inPos] & 0xFFFFFFF) >>> 12);
            out[4 + outPos] = ((in[4 + inPos] & 0xFFFFFFF) << 20 | (in[5 + inPos] & 0xFFFFFFF) >>> 8);
            out[5 + outPos] = ((in[5 + inPos] & 0xFFFFFFF) << 24 | (in[6 + inPos] & 0xFFFFFFF) >>> 4);
            out[6 + outPos] = ((in[6 + inPos] & 0xFFFFFFF) << 28 | (in[7 + inPos] & 0xFFFFFFF) << 0);
            out[7 + outPos] = ((in[8 + inPos] & 0xFFFFFFF) << 4 | (in[9 + inPos] & 0xFFFFFFF) >>> 24);
            out[8 + outPos] = ((in[9 + inPos] & 0xFFFFFFF) << 8 | (in[10 + inPos] & 0xFFFFFFF) >>> 20);
            out[9 + outPos] = ((in[10 + inPos] & 0xFFFFFFF) << 12 | (in[11 + inPos] & 0xFFFFFFF) >>> 16);
            out[10 + outPos] = ((in[11 + inPos] & 0xFFFFFFF) << 16 | (in[12 + inPos] & 0xFFFFFFF) >>> 12);
            out[11 + outPos] = ((in[12 + inPos] & 0xFFFFFFF) << 20 | (in[13 + inPos] & 0xFFFFFFF) >>> 8);
            out[12 + outPos] = ((in[13 + inPos] & 0xFFFFFFF) << 24 | (in[14 + inPos] & 0xFFFFFFF) >>> 4);
            out[13 + outPos] = ((in[14 + inPos] & 0xFFFFFFF) << 28 | (in[15 + inPos] & 0xFFFFFFF) << 0);
            out[14 + outPos] = ((in[16 + inPos] & 0xFFFFFFF) << 4 | (in[17 + inPos] & 0xFFFFFFF) >>> 24);
            out[15 + outPos] = ((in[17 + inPos] & 0xFFFFFFF) << 8 | (in[18 + inPos] & 0xFFFFFFF) >>> 20);
            out[16 + outPos] = ((in[18 + inPos] & 0xFFFFFFF) << 12 | (in[19 + inPos] & 0xFFFFFFF) >>> 16);
            out[17 + outPos] = ((in[19 + inPos] & 0xFFFFFFF) << 16 | (in[20 + inPos] & 0xFFFFFFF) >>> 12);
            out[18 + outPos] = ((in[20 + inPos] & 0xFFFFFFF) << 20 | (in[21 + inPos] & 0xFFFFFFF) >>> 8);
            out[19 + outPos] = ((in[21 + inPos] & 0xFFFFFFF) << 24 | (in[22 + inPos] & 0xFFFFFFF) >>> 4);
            out[20 + outPos] = ((in[22 + inPos] & 0xFFFFFFF) << 28 | (in[23 + inPos] & 0xFFFFFFF) << 0);
            out[21 + outPos] = ((in[24 + inPos] & 0xFFFFFFF) << 4 | (in[25 + inPos] & 0xFFFFFFF) >>> 24);
            out[22 + outPos] = ((in[25 + inPos] & 0xFFFFFFF) << 8 | (in[26 + inPos] & 0xFFFFFFF) >>> 20);
            out[23 + outPos] = ((in[26 + inPos] & 0xFFFFFFF) << 12 | (in[27 + inPos] & 0xFFFFFFF) >>> 16);
            out[24 + outPos] = ((in[27 + inPos] & 0xFFFFFFF) << 16 | (in[28 + inPos] & 0xFFFFFFF) >>> 12);
            out[25 + outPos] = ((in[28 + inPos] & 0xFFFFFFF) << 20 | (in[29 + inPos] & 0xFFFFFFF) >>> 8);
            out[26 + outPos] = ((in[29 + inPos] & 0xFFFFFFF) << 24 | (in[30 + inPos] & 0xFFFFFFF) >>> 4);
            out[27 + outPos] = ((in[30 + inPos] & 0xFFFFFFF) << 28 | (in[31 + inPos] & 0xFFFFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 4 & 0xFFFFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 24 & 0xFFFFFFF) | in[1 + inPos] >>> 8);
            out[2 + outPos] = ((in[1 + inPos] << 20 & 0xFFFFFFF) | in[2 + inPos] >>> 12);
            out[3 + outPos] = ((in[2 + inPos] << 16 & 0xFFFFFFF) | in[3 + inPos] >>> 16);
            out[4 + outPos] = ((in[3 + inPos] << 12 & 0xFFFFFFF) | in[4 + inPos] >>> 20);
            out[5 + outPos] = ((in[4 + inPos] << 8 & 0xFFFFFFF) | in[5 + inPos] >>> 24);
            out[6 + outPos] = ((in[5 + inPos] << 4 & 0xFFFFFFF) | in[6 + inPos] >>> 28);
            out[7 + outPos] = (in[6 + inPos] >>> 0 & 0xFFFFFFF);
            out[8 + outPos] = (in[7 + inPos] >>> 4 & 0xFFFFFFF);
            out[9 + outPos] = ((in[7 + inPos] << 24 & 0xFFFFFFF) | in[8 + inPos] >>> 8);
            out[10 + outPos] = ((in[8 + inPos] << 20 & 0xFFFFFFF) | in[9 + inPos] >>> 12);
            out[11 + outPos] = ((in[9 + inPos] << 16 & 0xFFFFFFF) | in[10 + inPos] >>> 16);
            out[12 + outPos] = ((in[10 + inPos] << 12 & 0xFFFFFFF) | in[11 + inPos] >>> 20);
            out[13 + outPos] = ((in[11 + inPos] << 8 & 0xFFFFFFF) | in[12 + inPos] >>> 24);
            out[14 + outPos] = ((in[12 + inPos] << 4 & 0xFFFFFFF) | in[13 + inPos] >>> 28);
            out[15 + outPos] = (in[13 + inPos] >>> 0 & 0xFFFFFFF);
            out[16 + outPos] = (in[14 + inPos] >>> 4 & 0xFFFFFFF);
            out[17 + outPos] = ((in[14 + inPos] << 24 & 0xFFFFFFF) | in[15 + inPos] >>> 8);
            out[18 + outPos] = ((in[15 + inPos] << 20 & 0xFFFFFFF) | in[16 + inPos] >>> 12);
            out[19 + outPos] = ((in[16 + inPos] << 16 & 0xFFFFFFF) | in[17 + inPos] >>> 16);
            out[20 + outPos] = ((in[17 + inPos] << 12 & 0xFFFFFFF) | in[18 + inPos] >>> 20);
            out[21 + outPos] = ((in[18 + inPos] << 8 & 0xFFFFFFF) | in[19 + inPos] >>> 24);
            out[22 + outPos] = ((in[19 + inPos] << 4 & 0xFFFFFFF) | in[20 + inPos] >>> 28);
            out[23 + outPos] = (in[20 + inPos] >>> 0 & 0xFFFFFFF);
            out[24 + outPos] = (in[21 + inPos] >>> 4 & 0xFFFFFFF);
            out[25 + outPos] = ((in[21 + inPos] << 24 & 0xFFFFFFF) | in[22 + inPos] >>> 8);
            out[26 + outPos] = ((in[22 + inPos] << 20 & 0xFFFFFFF) | in[23 + inPos] >>> 12);
            out[27 + outPos] = ((in[23 + inPos] << 16 & 0xFFFFFFF) | in[24 + inPos] >>> 16);
            out[28 + outPos] = ((in[24 + inPos] << 12 & 0xFFFFFFF) | in[25 + inPos] >>> 20);
            out[29 + outPos] = ((in[25 + inPos] << 8 & 0xFFFFFFF) | in[26 + inPos] >>> 24);
            out[30 + outPos] = ((in[26 + inPos] << 4 & 0xFFFFFFF) | in[27 + inPos] >>> 28);
            out[31 + outPos] = (in[27 + inPos] >>> 0 & 0xFFFFFFF);
        }
    }
    
    private static final class Packer29 extends IntPacker
    {
        private Packer29() {
            super(29);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x1FFFFFFF) << 3 | (in[1 + inPos] & 0x1FFFFFFF) >>> 26);
            out[1 + outPos] = ((in[1 + inPos] & 0x1FFFFFFF) << 6 | (in[2 + inPos] & 0x1FFFFFFF) >>> 23);
            out[2 + outPos] = ((in[2 + inPos] & 0x1FFFFFFF) << 9 | (in[3 + inPos] & 0x1FFFFFFF) >>> 20);
            out[3 + outPos] = ((in[3 + inPos] & 0x1FFFFFFF) << 12 | (in[4 + inPos] & 0x1FFFFFFF) >>> 17);
            out[4 + outPos] = ((in[4 + inPos] & 0x1FFFFFFF) << 15 | (in[5 + inPos] & 0x1FFFFFFF) >>> 14);
            out[5 + outPos] = ((in[5 + inPos] & 0x1FFFFFFF) << 18 | (in[6 + inPos] & 0x1FFFFFFF) >>> 11);
            out[6 + outPos] = ((in[6 + inPos] & 0x1FFFFFFF) << 21 | (in[7 + inPos] & 0x1FFFFFFF) >>> 8);
            out[7 + outPos] = ((in[7 + inPos] & 0x1FFFFFFF) << 24 | (in[8 + inPos] & 0x1FFFFFFF) >>> 5);
            out[8 + outPos] = ((in[8 + inPos] & 0x1FFFFFFF) << 27 | (in[9 + inPos] & 0x1FFFFFFF) >>> 2);
            out[9 + outPos] = ((in[9 + inPos] & 0x1FFFFFFF) << 30 | (in[10 + inPos] & 0x1FFFFFFF) << 1 | (in[11 + inPos] & 0x1FFFFFFF) >>> 28);
            out[10 + outPos] = ((in[11 + inPos] & 0x1FFFFFFF) << 4 | (in[12 + inPos] & 0x1FFFFFFF) >>> 25);
            out[11 + outPos] = ((in[12 + inPos] & 0x1FFFFFFF) << 7 | (in[13 + inPos] & 0x1FFFFFFF) >>> 22);
            out[12 + outPos] = ((in[13 + inPos] & 0x1FFFFFFF) << 10 | (in[14 + inPos] & 0x1FFFFFFF) >>> 19);
            out[13 + outPos] = ((in[14 + inPos] & 0x1FFFFFFF) << 13 | (in[15 + inPos] & 0x1FFFFFFF) >>> 16);
            out[14 + outPos] = ((in[15 + inPos] & 0x1FFFFFFF) << 16 | (in[16 + inPos] & 0x1FFFFFFF) >>> 13);
            out[15 + outPos] = ((in[16 + inPos] & 0x1FFFFFFF) << 19 | (in[17 + inPos] & 0x1FFFFFFF) >>> 10);
            out[16 + outPos] = ((in[17 + inPos] & 0x1FFFFFFF) << 22 | (in[18 + inPos] & 0x1FFFFFFF) >>> 7);
            out[17 + outPos] = ((in[18 + inPos] & 0x1FFFFFFF) << 25 | (in[19 + inPos] & 0x1FFFFFFF) >>> 4);
            out[18 + outPos] = ((in[19 + inPos] & 0x1FFFFFFF) << 28 | (in[20 + inPos] & 0x1FFFFFFF) >>> 1);
            out[19 + outPos] = ((in[20 + inPos] & 0x1FFFFFFF) << 31 | (in[21 + inPos] & 0x1FFFFFFF) << 2 | (in[22 + inPos] & 0x1FFFFFFF) >>> 27);
            out[20 + outPos] = ((in[22 + inPos] & 0x1FFFFFFF) << 5 | (in[23 + inPos] & 0x1FFFFFFF) >>> 24);
            out[21 + outPos] = ((in[23 + inPos] & 0x1FFFFFFF) << 8 | (in[24 + inPos] & 0x1FFFFFFF) >>> 21);
            out[22 + outPos] = ((in[24 + inPos] & 0x1FFFFFFF) << 11 | (in[25 + inPos] & 0x1FFFFFFF) >>> 18);
            out[23 + outPos] = ((in[25 + inPos] & 0x1FFFFFFF) << 14 | (in[26 + inPos] & 0x1FFFFFFF) >>> 15);
            out[24 + outPos] = ((in[26 + inPos] & 0x1FFFFFFF) << 17 | (in[27 + inPos] & 0x1FFFFFFF) >>> 12);
            out[25 + outPos] = ((in[27 + inPos] & 0x1FFFFFFF) << 20 | (in[28 + inPos] & 0x1FFFFFFF) >>> 9);
            out[26 + outPos] = ((in[28 + inPos] & 0x1FFFFFFF) << 23 | (in[29 + inPos] & 0x1FFFFFFF) >>> 6);
            out[27 + outPos] = ((in[29 + inPos] & 0x1FFFFFFF) << 26 | (in[30 + inPos] & 0x1FFFFFFF) >>> 3);
            out[28 + outPos] = ((in[30 + inPos] & 0x1FFFFFFF) << 29 | (in[31 + inPos] & 0x1FFFFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 3 & 0x1FFFFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 26 & 0x1FFFFFFF) | in[1 + inPos] >>> 6);
            out[2 + outPos] = ((in[1 + inPos] << 23 & 0x1FFFFFFF) | in[2 + inPos] >>> 9);
            out[3 + outPos] = ((in[2 + inPos] << 20 & 0x1FFFFFFF) | in[3 + inPos] >>> 12);
            out[4 + outPos] = ((in[3 + inPos] << 17 & 0x1FFFFFFF) | in[4 + inPos] >>> 15);
            out[5 + outPos] = ((in[4 + inPos] << 14 & 0x1FFFFFFF) | in[5 + inPos] >>> 18);
            out[6 + outPos] = ((in[5 + inPos] << 11 & 0x1FFFFFFF) | in[6 + inPos] >>> 21);
            out[7 + outPos] = ((in[6 + inPos] << 8 & 0x1FFFFFFF) | in[7 + inPos] >>> 24);
            out[8 + outPos] = ((in[7 + inPos] << 5 & 0x1FFFFFFF) | in[8 + inPos] >>> 27);
            out[9 + outPos] = ((in[8 + inPos] << 2 & 0x1FFFFFFF) | in[9 + inPos] >>> 30);
            out[10 + outPos] = (in[9 + inPos] >>> 1 & 0x1FFFFFFF);
            out[11 + outPos] = ((in[9 + inPos] << 28 & 0x1FFFFFFF) | in[10 + inPos] >>> 4);
            out[12 + outPos] = ((in[10 + inPos] << 25 & 0x1FFFFFFF) | in[11 + inPos] >>> 7);
            out[13 + outPos] = ((in[11 + inPos] << 22 & 0x1FFFFFFF) | in[12 + inPos] >>> 10);
            out[14 + outPos] = ((in[12 + inPos] << 19 & 0x1FFFFFFF) | in[13 + inPos] >>> 13);
            out[15 + outPos] = ((in[13 + inPos] << 16 & 0x1FFFFFFF) | in[14 + inPos] >>> 16);
            out[16 + outPos] = ((in[14 + inPos] << 13 & 0x1FFFFFFF) | in[15 + inPos] >>> 19);
            out[17 + outPos] = ((in[15 + inPos] << 10 & 0x1FFFFFFF) | in[16 + inPos] >>> 22);
            out[18 + outPos] = ((in[16 + inPos] << 7 & 0x1FFFFFFF) | in[17 + inPos] >>> 25);
            out[19 + outPos] = ((in[17 + inPos] << 4 & 0x1FFFFFFF) | in[18 + inPos] >>> 28);
            out[20 + outPos] = ((in[18 + inPos] << 1 & 0x1FFFFFFF) | in[19 + inPos] >>> 31);
            out[21 + outPos] = (in[19 + inPos] >>> 2 & 0x1FFFFFFF);
            out[22 + outPos] = ((in[19 + inPos] << 27 & 0x1FFFFFFF) | in[20 + inPos] >>> 5);
            out[23 + outPos] = ((in[20 + inPos] << 24 & 0x1FFFFFFF) | in[21 + inPos] >>> 8);
            out[24 + outPos] = ((in[21 + inPos] << 21 & 0x1FFFFFFF) | in[22 + inPos] >>> 11);
            out[25 + outPos] = ((in[22 + inPos] << 18 & 0x1FFFFFFF) | in[23 + inPos] >>> 14);
            out[26 + outPos] = ((in[23 + inPos] << 15 & 0x1FFFFFFF) | in[24 + inPos] >>> 17);
            out[27 + outPos] = ((in[24 + inPos] << 12 & 0x1FFFFFFF) | in[25 + inPos] >>> 20);
            out[28 + outPos] = ((in[25 + inPos] << 9 & 0x1FFFFFFF) | in[26 + inPos] >>> 23);
            out[29 + outPos] = ((in[26 + inPos] << 6 & 0x1FFFFFFF) | in[27 + inPos] >>> 26);
            out[30 + outPos] = ((in[27 + inPos] << 3 & 0x1FFFFFFF) | in[28 + inPos] >>> 29);
            out[31 + outPos] = (in[28 + inPos] >>> 0 & 0x1FFFFFFF);
        }
    }
    
    private static final class Packer30 extends IntPacker
    {
        private Packer30() {
            super(30);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0x3FFFFFFF) << 2 | (in[1 + inPos] & 0x3FFFFFFF) >>> 28);
            out[1 + outPos] = ((in[1 + inPos] & 0x3FFFFFFF) << 4 | (in[2 + inPos] & 0x3FFFFFFF) >>> 26);
            out[2 + outPos] = ((in[2 + inPos] & 0x3FFFFFFF) << 6 | (in[3 + inPos] & 0x3FFFFFFF) >>> 24);
            out[3 + outPos] = ((in[3 + inPos] & 0x3FFFFFFF) << 8 | (in[4 + inPos] & 0x3FFFFFFF) >>> 22);
            out[4 + outPos] = ((in[4 + inPos] & 0x3FFFFFFF) << 10 | (in[5 + inPos] & 0x3FFFFFFF) >>> 20);
            out[5 + outPos] = ((in[5 + inPos] & 0x3FFFFFFF) << 12 | (in[6 + inPos] & 0x3FFFFFFF) >>> 18);
            out[6 + outPos] = ((in[6 + inPos] & 0x3FFFFFFF) << 14 | (in[7 + inPos] & 0x3FFFFFFF) >>> 16);
            out[7 + outPos] = ((in[7 + inPos] & 0x3FFFFFFF) << 16 | (in[8 + inPos] & 0x3FFFFFFF) >>> 14);
            out[8 + outPos] = ((in[8 + inPos] & 0x3FFFFFFF) << 18 | (in[9 + inPos] & 0x3FFFFFFF) >>> 12);
            out[9 + outPos] = ((in[9 + inPos] & 0x3FFFFFFF) << 20 | (in[10 + inPos] & 0x3FFFFFFF) >>> 10);
            out[10 + outPos] = ((in[10 + inPos] & 0x3FFFFFFF) << 22 | (in[11 + inPos] & 0x3FFFFFFF) >>> 8);
            out[11 + outPos] = ((in[11 + inPos] & 0x3FFFFFFF) << 24 | (in[12 + inPos] & 0x3FFFFFFF) >>> 6);
            out[12 + outPos] = ((in[12 + inPos] & 0x3FFFFFFF) << 26 | (in[13 + inPos] & 0x3FFFFFFF) >>> 4);
            out[13 + outPos] = ((in[13 + inPos] & 0x3FFFFFFF) << 28 | (in[14 + inPos] & 0x3FFFFFFF) >>> 2);
            out[14 + outPos] = ((in[14 + inPos] & 0x3FFFFFFF) << 30 | (in[15 + inPos] & 0x3FFFFFFF) << 0);
            out[15 + outPos] = ((in[16 + inPos] & 0x3FFFFFFF) << 2 | (in[17 + inPos] & 0x3FFFFFFF) >>> 28);
            out[16 + outPos] = ((in[17 + inPos] & 0x3FFFFFFF) << 4 | (in[18 + inPos] & 0x3FFFFFFF) >>> 26);
            out[17 + outPos] = ((in[18 + inPos] & 0x3FFFFFFF) << 6 | (in[19 + inPos] & 0x3FFFFFFF) >>> 24);
            out[18 + outPos] = ((in[19 + inPos] & 0x3FFFFFFF) << 8 | (in[20 + inPos] & 0x3FFFFFFF) >>> 22);
            out[19 + outPos] = ((in[20 + inPos] & 0x3FFFFFFF) << 10 | (in[21 + inPos] & 0x3FFFFFFF) >>> 20);
            out[20 + outPos] = ((in[21 + inPos] & 0x3FFFFFFF) << 12 | (in[22 + inPos] & 0x3FFFFFFF) >>> 18);
            out[21 + outPos] = ((in[22 + inPos] & 0x3FFFFFFF) << 14 | (in[23 + inPos] & 0x3FFFFFFF) >>> 16);
            out[22 + outPos] = ((in[23 + inPos] & 0x3FFFFFFF) << 16 | (in[24 + inPos] & 0x3FFFFFFF) >>> 14);
            out[23 + outPos] = ((in[24 + inPos] & 0x3FFFFFFF) << 18 | (in[25 + inPos] & 0x3FFFFFFF) >>> 12);
            out[24 + outPos] = ((in[25 + inPos] & 0x3FFFFFFF) << 20 | (in[26 + inPos] & 0x3FFFFFFF) >>> 10);
            out[25 + outPos] = ((in[26 + inPos] & 0x3FFFFFFF) << 22 | (in[27 + inPos] & 0x3FFFFFFF) >>> 8);
            out[26 + outPos] = ((in[27 + inPos] & 0x3FFFFFFF) << 24 | (in[28 + inPos] & 0x3FFFFFFF) >>> 6);
            out[27 + outPos] = ((in[28 + inPos] & 0x3FFFFFFF) << 26 | (in[29 + inPos] & 0x3FFFFFFF) >>> 4);
            out[28 + outPos] = ((in[29 + inPos] & 0x3FFFFFFF) << 28 | (in[30 + inPos] & 0x3FFFFFFF) >>> 2);
            out[29 + outPos] = ((in[30 + inPos] & 0x3FFFFFFF) << 30 | (in[31 + inPos] & 0x3FFFFFFF) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 2 & 0x3FFFFFFF);
            out[1 + outPos] = ((in[0 + inPos] << 28 & 0x3FFFFFFF) | in[1 + inPos] >>> 4);
            out[2 + outPos] = ((in[1 + inPos] << 26 & 0x3FFFFFFF) | in[2 + inPos] >>> 6);
            out[3 + outPos] = ((in[2 + inPos] << 24 & 0x3FFFFFFF) | in[3 + inPos] >>> 8);
            out[4 + outPos] = ((in[3 + inPos] << 22 & 0x3FFFFFFF) | in[4 + inPos] >>> 10);
            out[5 + outPos] = ((in[4 + inPos] << 20 & 0x3FFFFFFF) | in[5 + inPos] >>> 12);
            out[6 + outPos] = ((in[5 + inPos] << 18 & 0x3FFFFFFF) | in[6 + inPos] >>> 14);
            out[7 + outPos] = ((in[6 + inPos] << 16 & 0x3FFFFFFF) | in[7 + inPos] >>> 16);
            out[8 + outPos] = ((in[7 + inPos] << 14 & 0x3FFFFFFF) | in[8 + inPos] >>> 18);
            out[9 + outPos] = ((in[8 + inPos] << 12 & 0x3FFFFFFF) | in[9 + inPos] >>> 20);
            out[10 + outPos] = ((in[9 + inPos] << 10 & 0x3FFFFFFF) | in[10 + inPos] >>> 22);
            out[11 + outPos] = ((in[10 + inPos] << 8 & 0x3FFFFFFF) | in[11 + inPos] >>> 24);
            out[12 + outPos] = ((in[11 + inPos] << 6 & 0x3FFFFFFF) | in[12 + inPos] >>> 26);
            out[13 + outPos] = ((in[12 + inPos] << 4 & 0x3FFFFFFF) | in[13 + inPos] >>> 28);
            out[14 + outPos] = ((in[13 + inPos] << 2 & 0x3FFFFFFF) | in[14 + inPos] >>> 30);
            out[15 + outPos] = (in[14 + inPos] >>> 0 & 0x3FFFFFFF);
            out[16 + outPos] = (in[15 + inPos] >>> 2 & 0x3FFFFFFF);
            out[17 + outPos] = ((in[15 + inPos] << 28 & 0x3FFFFFFF) | in[16 + inPos] >>> 4);
            out[18 + outPos] = ((in[16 + inPos] << 26 & 0x3FFFFFFF) | in[17 + inPos] >>> 6);
            out[19 + outPos] = ((in[17 + inPos] << 24 & 0x3FFFFFFF) | in[18 + inPos] >>> 8);
            out[20 + outPos] = ((in[18 + inPos] << 22 & 0x3FFFFFFF) | in[19 + inPos] >>> 10);
            out[21 + outPos] = ((in[19 + inPos] << 20 & 0x3FFFFFFF) | in[20 + inPos] >>> 12);
            out[22 + outPos] = ((in[20 + inPos] << 18 & 0x3FFFFFFF) | in[21 + inPos] >>> 14);
            out[23 + outPos] = ((in[21 + inPos] << 16 & 0x3FFFFFFF) | in[22 + inPos] >>> 16);
            out[24 + outPos] = ((in[22 + inPos] << 14 & 0x3FFFFFFF) | in[23 + inPos] >>> 18);
            out[25 + outPos] = ((in[23 + inPos] << 12 & 0x3FFFFFFF) | in[24 + inPos] >>> 20);
            out[26 + outPos] = ((in[24 + inPos] << 10 & 0x3FFFFFFF) | in[25 + inPos] >>> 22);
            out[27 + outPos] = ((in[25 + inPos] << 8 & 0x3FFFFFFF) | in[26 + inPos] >>> 24);
            out[28 + outPos] = ((in[26 + inPos] << 6 & 0x3FFFFFFF) | in[27 + inPos] >>> 26);
            out[29 + outPos] = ((in[27 + inPos] << 4 & 0x3FFFFFFF) | in[28 + inPos] >>> 28);
            out[30 + outPos] = ((in[28 + inPos] << 2 & 0x3FFFFFFF) | in[29 + inPos] >>> 30);
            out[31 + outPos] = (in[29 + inPos] >>> 0 & 0x3FFFFFFF);
        }
    }
    
    private static final class Packer31 extends IntPacker
    {
        private Packer31() {
            super(31);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & Integer.MAX_VALUE) << 1 | (in[1 + inPos] & Integer.MAX_VALUE) >>> 30);
            out[1 + outPos] = ((in[1 + inPos] & Integer.MAX_VALUE) << 2 | (in[2 + inPos] & Integer.MAX_VALUE) >>> 29);
            out[2 + outPos] = ((in[2 + inPos] & Integer.MAX_VALUE) << 3 | (in[3 + inPos] & Integer.MAX_VALUE) >>> 28);
            out[3 + outPos] = ((in[3 + inPos] & Integer.MAX_VALUE) << 4 | (in[4 + inPos] & Integer.MAX_VALUE) >>> 27);
            out[4 + outPos] = ((in[4 + inPos] & Integer.MAX_VALUE) << 5 | (in[5 + inPos] & Integer.MAX_VALUE) >>> 26);
            out[5 + outPos] = ((in[5 + inPos] & Integer.MAX_VALUE) << 6 | (in[6 + inPos] & Integer.MAX_VALUE) >>> 25);
            out[6 + outPos] = ((in[6 + inPos] & Integer.MAX_VALUE) << 7 | (in[7 + inPos] & Integer.MAX_VALUE) >>> 24);
            out[7 + outPos] = ((in[7 + inPos] & Integer.MAX_VALUE) << 8 | (in[8 + inPos] & Integer.MAX_VALUE) >>> 23);
            out[8 + outPos] = ((in[8 + inPos] & Integer.MAX_VALUE) << 9 | (in[9 + inPos] & Integer.MAX_VALUE) >>> 22);
            out[9 + outPos] = ((in[9 + inPos] & Integer.MAX_VALUE) << 10 | (in[10 + inPos] & Integer.MAX_VALUE) >>> 21);
            out[10 + outPos] = ((in[10 + inPos] & Integer.MAX_VALUE) << 11 | (in[11 + inPos] & Integer.MAX_VALUE) >>> 20);
            out[11 + outPos] = ((in[11 + inPos] & Integer.MAX_VALUE) << 12 | (in[12 + inPos] & Integer.MAX_VALUE) >>> 19);
            out[12 + outPos] = ((in[12 + inPos] & Integer.MAX_VALUE) << 13 | (in[13 + inPos] & Integer.MAX_VALUE) >>> 18);
            out[13 + outPos] = ((in[13 + inPos] & Integer.MAX_VALUE) << 14 | (in[14 + inPos] & Integer.MAX_VALUE) >>> 17);
            out[14 + outPos] = ((in[14 + inPos] & Integer.MAX_VALUE) << 15 | (in[15 + inPos] & Integer.MAX_VALUE) >>> 16);
            out[15 + outPos] = ((in[15 + inPos] & Integer.MAX_VALUE) << 16 | (in[16 + inPos] & Integer.MAX_VALUE) >>> 15);
            out[16 + outPos] = ((in[16 + inPos] & Integer.MAX_VALUE) << 17 | (in[17 + inPos] & Integer.MAX_VALUE) >>> 14);
            out[17 + outPos] = ((in[17 + inPos] & Integer.MAX_VALUE) << 18 | (in[18 + inPos] & Integer.MAX_VALUE) >>> 13);
            out[18 + outPos] = ((in[18 + inPos] & Integer.MAX_VALUE) << 19 | (in[19 + inPos] & Integer.MAX_VALUE) >>> 12);
            out[19 + outPos] = ((in[19 + inPos] & Integer.MAX_VALUE) << 20 | (in[20 + inPos] & Integer.MAX_VALUE) >>> 11);
            out[20 + outPos] = ((in[20 + inPos] & Integer.MAX_VALUE) << 21 | (in[21 + inPos] & Integer.MAX_VALUE) >>> 10);
            out[21 + outPos] = ((in[21 + inPos] & Integer.MAX_VALUE) << 22 | (in[22 + inPos] & Integer.MAX_VALUE) >>> 9);
            out[22 + outPos] = ((in[22 + inPos] & Integer.MAX_VALUE) << 23 | (in[23 + inPos] & Integer.MAX_VALUE) >>> 8);
            out[23 + outPos] = ((in[23 + inPos] & Integer.MAX_VALUE) << 24 | (in[24 + inPos] & Integer.MAX_VALUE) >>> 7);
            out[24 + outPos] = ((in[24 + inPos] & Integer.MAX_VALUE) << 25 | (in[25 + inPos] & Integer.MAX_VALUE) >>> 6);
            out[25 + outPos] = ((in[25 + inPos] & Integer.MAX_VALUE) << 26 | (in[26 + inPos] & Integer.MAX_VALUE) >>> 5);
            out[26 + outPos] = ((in[26 + inPos] & Integer.MAX_VALUE) << 27 | (in[27 + inPos] & Integer.MAX_VALUE) >>> 4);
            out[27 + outPos] = ((in[27 + inPos] & Integer.MAX_VALUE) << 28 | (in[28 + inPos] & Integer.MAX_VALUE) >>> 3);
            out[28 + outPos] = ((in[28 + inPos] & Integer.MAX_VALUE) << 29 | (in[29 + inPos] & Integer.MAX_VALUE) >>> 2);
            out[29 + outPos] = ((in[29 + inPos] & Integer.MAX_VALUE) << 30 | (in[30 + inPos] & Integer.MAX_VALUE) >>> 1);
            out[30 + outPos] = ((in[30 + inPos] & Integer.MAX_VALUE) << 31 | (in[31 + inPos] & Integer.MAX_VALUE) << 0);
        }
        
        @Override
        public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] >>> 1 & Integer.MAX_VALUE);
            out[1 + outPos] = ((in[0 + inPos] << 30 & Integer.MAX_VALUE) | in[1 + inPos] >>> 2);
            out[2 + outPos] = ((in[1 + inPos] << 29 & Integer.MAX_VALUE) | in[2 + inPos] >>> 3);
            out[3 + outPos] = ((in[2 + inPos] << 28 & Integer.MAX_VALUE) | in[3 + inPos] >>> 4);
            out[4 + outPos] = ((in[3 + inPos] << 27 & Integer.MAX_VALUE) | in[4 + inPos] >>> 5);
            out[5 + outPos] = ((in[4 + inPos] << 26 & Integer.MAX_VALUE) | in[5 + inPos] >>> 6);
            out[6 + outPos] = ((in[5 + inPos] << 25 & Integer.MAX_VALUE) | in[6 + inPos] >>> 7);
            out[7 + outPos] = ((in[6 + inPos] << 24 & Integer.MAX_VALUE) | in[7 + inPos] >>> 8);
            out[8 + outPos] = ((in[7 + inPos] << 23 & Integer.MAX_VALUE) | in[8 + inPos] >>> 9);
            out[9 + outPos] = ((in[8 + inPos] << 22 & Integer.MAX_VALUE) | in[9 + inPos] >>> 10);
            out[10 + outPos] = ((in[9 + inPos] << 21 & Integer.MAX_VALUE) | in[10 + inPos] >>> 11);
            out[11 + outPos] = ((in[10 + inPos] << 20 & Integer.MAX_VALUE) | in[11 + inPos] >>> 12);
            out[12 + outPos] = ((in[11 + inPos] << 19 & Integer.MAX_VALUE) | in[12 + inPos] >>> 13);
            out[13 + outPos] = ((in[12 + inPos] << 18 & Integer.MAX_VALUE) | in[13 + inPos] >>> 14);
            out[14 + outPos] = ((in[13 + inPos] << 17 & Integer.MAX_VALUE) | in[14 + inPos] >>> 15);
            out[15 + outPos] = ((in[14 + inPos] << 16 & Integer.MAX_VALUE) | in[15 + inPos] >>> 16);
            out[16 + outPos] = ((in[15 + inPos] << 15 & Integer.MAX_VALUE) | in[16 + inPos] >>> 17);
            out[17 + outPos] = ((in[16 + inPos] << 14 & Integer.MAX_VALUE) | in[17 + inPos] >>> 18);
            out[18 + outPos] = ((in[17 + inPos] << 13 & Integer.MAX_VALUE) | in[18 + inPos] >>> 19);
            out[19 + outPos] = ((in[18 + inPos] << 12 & Integer.MAX_VALUE) | in[19 + inPos] >>> 20);
            out[20 + outPos] = ((in[19 + inPos] << 11 & Integer.MAX_VALUE) | in[20 + inPos] >>> 21);
            out[21 + outPos] = ((in[20 + inPos] << 10 & Integer.MAX_VALUE) | in[21 + inPos] >>> 22);
            out[22 + outPos] = ((in[21 + inPos] << 9 & Integer.MAX_VALUE) | in[22 + inPos] >>> 23);
            out[23 + outPos] = ((in[22 + inPos] << 8 & Integer.MAX_VALUE) | in[23 + inPos] >>> 24);
            out[24 + outPos] = ((in[23 + inPos] << 7 & Integer.MAX_VALUE) | in[24 + inPos] >>> 25);
            out[25 + outPos] = ((in[24 + inPos] << 6 & Integer.MAX_VALUE) | in[25 + inPos] >>> 26);
            out[26 + outPos] = ((in[25 + inPos] << 5 & Integer.MAX_VALUE) | in[26 + inPos] >>> 27);
            out[27 + outPos] = ((in[26 + inPos] << 4 & Integer.MAX_VALUE) | in[27 + inPos] >>> 28);
            out[28 + outPos] = ((in[27 + inPos] << 3 & Integer.MAX_VALUE) | in[28 + inPos] >>> 29);
            out[29 + outPos] = ((in[28 + inPos] << 2 & Integer.MAX_VALUE) | in[29 + inPos] >>> 30);
            out[30 + outPos] = ((in[29 + inPos] << 1 & Integer.MAX_VALUE) | in[30 + inPos] >>> 31);
            out[31 + outPos] = (in[30 + inPos] >>> 0 & Integer.MAX_VALUE);
        }
    }
}
