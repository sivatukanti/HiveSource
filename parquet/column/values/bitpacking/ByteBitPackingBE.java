// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

public abstract class ByteBitPackingBE
{
    private static final BytePacker[] packers;
    public static final BytePackerFactory factory;
    
    static {
        (packers = new BytePacker[33])[0] = new Packer0();
        ByteBitPackingBE.packers[1] = new Packer1();
        ByteBitPackingBE.packers[2] = new Packer2();
        ByteBitPackingBE.packers[3] = new Packer3();
        ByteBitPackingBE.packers[4] = new Packer4();
        ByteBitPackingBE.packers[5] = new Packer5();
        ByteBitPackingBE.packers[6] = new Packer6();
        ByteBitPackingBE.packers[7] = new Packer7();
        ByteBitPackingBE.packers[8] = new Packer8();
        ByteBitPackingBE.packers[9] = new Packer9();
        ByteBitPackingBE.packers[10] = new Packer10();
        ByteBitPackingBE.packers[11] = new Packer11();
        ByteBitPackingBE.packers[12] = new Packer12();
        ByteBitPackingBE.packers[13] = new Packer13();
        ByteBitPackingBE.packers[14] = new Packer14();
        ByteBitPackingBE.packers[15] = new Packer15();
        ByteBitPackingBE.packers[16] = new Packer16();
        ByteBitPackingBE.packers[17] = new Packer17();
        ByteBitPackingBE.packers[18] = new Packer18();
        ByteBitPackingBE.packers[19] = new Packer19();
        ByteBitPackingBE.packers[20] = new Packer20();
        ByteBitPackingBE.packers[21] = new Packer21();
        ByteBitPackingBE.packers[22] = new Packer22();
        ByteBitPackingBE.packers[23] = new Packer23();
        ByteBitPackingBE.packers[24] = new Packer24();
        ByteBitPackingBE.packers[25] = new Packer25();
        ByteBitPackingBE.packers[26] = new Packer26();
        ByteBitPackingBE.packers[27] = new Packer27();
        ByteBitPackingBE.packers[28] = new Packer28();
        ByteBitPackingBE.packers[29] = new Packer29();
        ByteBitPackingBE.packers[30] = new Packer30();
        ByteBitPackingBE.packers[31] = new Packer31();
        ByteBitPackingBE.packers[32] = new Packer32();
        factory = new BytePackerFactory() {
            @Override
            public BytePacker newBytePacker(final int bitWidth) {
                return ByteBitPackingBE.packers[bitWidth];
            }
        };
    }
    
    private static final class Packer0 extends BytePacker
    {
        private Packer0() {
            super(0);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
        }
    }
    
    private static final class Packer1 extends BytePacker
    {
        private Packer1() {
            super(1);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x1) << 7 | (in[1 + inPos] & 0x1) << 6 | (in[2 + inPos] & 0x1) << 5 | (in[3 + inPos] & 0x1) << 4 | (in[4 + inPos] & 0x1) << 3 | (in[5 + inPos] & 0x1) << 2 | (in[6 + inPos] & 0x1) << 1 | (in[7 + inPos] & 0x1)) & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x1) << 7 | (in[1 + inPos] & 0x1) << 6 | (in[2 + inPos] & 0x1) << 5 | (in[3 + inPos] & 0x1) << 4 | (in[4 + inPos] & 0x1) << 3 | (in[5 + inPos] & 0x1) << 2 | (in[6 + inPos] & 0x1) << 1 | (in[7 + inPos] & 0x1)) & 0xFF);
            out[1 + outPos] = (byte)(((in[8 + inPos] & 0x1) << 7 | (in[9 + inPos] & 0x1) << 6 | (in[10 + inPos] & 0x1) << 5 | (in[11 + inPos] & 0x1) << 4 | (in[12 + inPos] & 0x1) << 3 | (in[13 + inPos] & 0x1) << 2 | (in[14 + inPos] & 0x1) << 1 | (in[15 + inPos] & 0x1)) & 0xFF);
            out[2 + outPos] = (byte)(((in[16 + inPos] & 0x1) << 7 | (in[17 + inPos] & 0x1) << 6 | (in[18 + inPos] & 0x1) << 5 | (in[19 + inPos] & 0x1) << 4 | (in[20 + inPos] & 0x1) << 3 | (in[21 + inPos] & 0x1) << 2 | (in[22 + inPos] & 0x1) << 1 | (in[23 + inPos] & 0x1)) & 0xFF);
            out[3 + outPos] = (byte)(((in[24 + inPos] & 0x1) << 7 | (in[25 + inPos] & 0x1) << 6 | (in[26 + inPos] & 0x1) << 5 | (in[27 + inPos] & 0x1) << 4 | (in[28 + inPos] & 0x1) << 3 | (in[29 + inPos] & 0x1) << 2 | (in[30 + inPos] & 0x1) << 1 | (in[31 + inPos] & 0x1)) & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 7 & 0x1);
            out[1 + outPos] = ((in[0 + inPos] & 0xFF) >>> 6 & 0x1);
            out[2 + outPos] = ((in[0 + inPos] & 0xFF) >>> 5 & 0x1);
            out[3 + outPos] = ((in[0 + inPos] & 0xFF) >>> 4 & 0x1);
            out[4 + outPos] = ((in[0 + inPos] & 0xFF) >>> 3 & 0x1);
            out[5 + outPos] = ((in[0 + inPos] & 0xFF) >>> 2 & 0x1);
            out[6 + outPos] = ((in[0 + inPos] & 0xFF) >>> 1 & 0x1);
            out[7 + outPos] = (in[0 + inPos] & 0xFF & 0x1);
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 7 & 0x1);
            out[1 + outPos] = ((in[0 + inPos] & 0xFF) >>> 6 & 0x1);
            out[2 + outPos] = ((in[0 + inPos] & 0xFF) >>> 5 & 0x1);
            out[3 + outPos] = ((in[0 + inPos] & 0xFF) >>> 4 & 0x1);
            out[4 + outPos] = ((in[0 + inPos] & 0xFF) >>> 3 & 0x1);
            out[5 + outPos] = ((in[0 + inPos] & 0xFF) >>> 2 & 0x1);
            out[6 + outPos] = ((in[0 + inPos] & 0xFF) >>> 1 & 0x1);
            out[7 + outPos] = (in[0 + inPos] & 0xFF & 0x1);
            out[8 + outPos] = ((in[1 + inPos] & 0xFF) >>> 7 & 0x1);
            out[9 + outPos] = ((in[1 + inPos] & 0xFF) >>> 6 & 0x1);
            out[10 + outPos] = ((in[1 + inPos] & 0xFF) >>> 5 & 0x1);
            out[11 + outPos] = ((in[1 + inPos] & 0xFF) >>> 4 & 0x1);
            out[12 + outPos] = ((in[1 + inPos] & 0xFF) >>> 3 & 0x1);
            out[13 + outPos] = ((in[1 + inPos] & 0xFF) >>> 2 & 0x1);
            out[14 + outPos] = ((in[1 + inPos] & 0xFF) >>> 1 & 0x1);
            out[15 + outPos] = (in[1 + inPos] & 0xFF & 0x1);
            out[16 + outPos] = ((in[2 + inPos] & 0xFF) >>> 7 & 0x1);
            out[17 + outPos] = ((in[2 + inPos] & 0xFF) >>> 6 & 0x1);
            out[18 + outPos] = ((in[2 + inPos] & 0xFF) >>> 5 & 0x1);
            out[19 + outPos] = ((in[2 + inPos] & 0xFF) >>> 4 & 0x1);
            out[20 + outPos] = ((in[2 + inPos] & 0xFF) >>> 3 & 0x1);
            out[21 + outPos] = ((in[2 + inPos] & 0xFF) >>> 2 & 0x1);
            out[22 + outPos] = ((in[2 + inPos] & 0xFF) >>> 1 & 0x1);
            out[23 + outPos] = (in[2 + inPos] & 0xFF & 0x1);
            out[24 + outPos] = ((in[3 + inPos] & 0xFF) >>> 7 & 0x1);
            out[25 + outPos] = ((in[3 + inPos] & 0xFF) >>> 6 & 0x1);
            out[26 + outPos] = ((in[3 + inPos] & 0xFF) >>> 5 & 0x1);
            out[27 + outPos] = ((in[3 + inPos] & 0xFF) >>> 4 & 0x1);
            out[28 + outPos] = ((in[3 + inPos] & 0xFF) >>> 3 & 0x1);
            out[29 + outPos] = ((in[3 + inPos] & 0xFF) >>> 2 & 0x1);
            out[30 + outPos] = ((in[3 + inPos] & 0xFF) >>> 1 & 0x1);
            out[31 + outPos] = (in[3 + inPos] & 0xFF & 0x1);
        }
    }
    
    private static final class Packer2 extends BytePacker
    {
        private Packer2() {
            super(2);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x3) << 6 | (in[1 + inPos] & 0x3) << 4 | (in[2 + inPos] & 0x3) << 2 | (in[3 + inPos] & 0x3)) & 0xFF);
            out[1 + outPos] = (byte)(((in[4 + inPos] & 0x3) << 6 | (in[5 + inPos] & 0x3) << 4 | (in[6 + inPos] & 0x3) << 2 | (in[7 + inPos] & 0x3)) & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x3) << 6 | (in[1 + inPos] & 0x3) << 4 | (in[2 + inPos] & 0x3) << 2 | (in[3 + inPos] & 0x3)) & 0xFF);
            out[1 + outPos] = (byte)(((in[4 + inPos] & 0x3) << 6 | (in[5 + inPos] & 0x3) << 4 | (in[6 + inPos] & 0x3) << 2 | (in[7 + inPos] & 0x3)) & 0xFF);
            out[2 + outPos] = (byte)(((in[8 + inPos] & 0x3) << 6 | (in[9 + inPos] & 0x3) << 4 | (in[10 + inPos] & 0x3) << 2 | (in[11 + inPos] & 0x3)) & 0xFF);
            out[3 + outPos] = (byte)(((in[12 + inPos] & 0x3) << 6 | (in[13 + inPos] & 0x3) << 4 | (in[14 + inPos] & 0x3) << 2 | (in[15 + inPos] & 0x3)) & 0xFF);
            out[4 + outPos] = (byte)(((in[16 + inPos] & 0x3) << 6 | (in[17 + inPos] & 0x3) << 4 | (in[18 + inPos] & 0x3) << 2 | (in[19 + inPos] & 0x3)) & 0xFF);
            out[5 + outPos] = (byte)(((in[20 + inPos] & 0x3) << 6 | (in[21 + inPos] & 0x3) << 4 | (in[22 + inPos] & 0x3) << 2 | (in[23 + inPos] & 0x3)) & 0xFF);
            out[6 + outPos] = (byte)(((in[24 + inPos] & 0x3) << 6 | (in[25 + inPos] & 0x3) << 4 | (in[26 + inPos] & 0x3) << 2 | (in[27 + inPos] & 0x3)) & 0xFF);
            out[7 + outPos] = (byte)(((in[28 + inPos] & 0x3) << 6 | (in[29 + inPos] & 0x3) << 4 | (in[30 + inPos] & 0x3) << 2 | (in[31 + inPos] & 0x3)) & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 6 & 0x3);
            out[1 + outPos] = ((in[0 + inPos] & 0xFF) >>> 4 & 0x3);
            out[2 + outPos] = ((in[0 + inPos] & 0xFF) >>> 2 & 0x3);
            out[3 + outPos] = (in[0 + inPos] & 0xFF & 0x3);
            out[4 + outPos] = ((in[1 + inPos] & 0xFF) >>> 6 & 0x3);
            out[5 + outPos] = ((in[1 + inPos] & 0xFF) >>> 4 & 0x3);
            out[6 + outPos] = ((in[1 + inPos] & 0xFF) >>> 2 & 0x3);
            out[7 + outPos] = (in[1 + inPos] & 0xFF & 0x3);
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 6 & 0x3);
            out[1 + outPos] = ((in[0 + inPos] & 0xFF) >>> 4 & 0x3);
            out[2 + outPos] = ((in[0 + inPos] & 0xFF) >>> 2 & 0x3);
            out[3 + outPos] = (in[0 + inPos] & 0xFF & 0x3);
            out[4 + outPos] = ((in[1 + inPos] & 0xFF) >>> 6 & 0x3);
            out[5 + outPos] = ((in[1 + inPos] & 0xFF) >>> 4 & 0x3);
            out[6 + outPos] = ((in[1 + inPos] & 0xFF) >>> 2 & 0x3);
            out[7 + outPos] = (in[1 + inPos] & 0xFF & 0x3);
            out[8 + outPos] = ((in[2 + inPos] & 0xFF) >>> 6 & 0x3);
            out[9 + outPos] = ((in[2 + inPos] & 0xFF) >>> 4 & 0x3);
            out[10 + outPos] = ((in[2 + inPos] & 0xFF) >>> 2 & 0x3);
            out[11 + outPos] = (in[2 + inPos] & 0xFF & 0x3);
            out[12 + outPos] = ((in[3 + inPos] & 0xFF) >>> 6 & 0x3);
            out[13 + outPos] = ((in[3 + inPos] & 0xFF) >>> 4 & 0x3);
            out[14 + outPos] = ((in[3 + inPos] & 0xFF) >>> 2 & 0x3);
            out[15 + outPos] = (in[3 + inPos] & 0xFF & 0x3);
            out[16 + outPos] = ((in[4 + inPos] & 0xFF) >>> 6 & 0x3);
            out[17 + outPos] = ((in[4 + inPos] & 0xFF) >>> 4 & 0x3);
            out[18 + outPos] = ((in[4 + inPos] & 0xFF) >>> 2 & 0x3);
            out[19 + outPos] = (in[4 + inPos] & 0xFF & 0x3);
            out[20 + outPos] = ((in[5 + inPos] & 0xFF) >>> 6 & 0x3);
            out[21 + outPos] = ((in[5 + inPos] & 0xFF) >>> 4 & 0x3);
            out[22 + outPos] = ((in[5 + inPos] & 0xFF) >>> 2 & 0x3);
            out[23 + outPos] = (in[5 + inPos] & 0xFF & 0x3);
            out[24 + outPos] = ((in[6 + inPos] & 0xFF) >>> 6 & 0x3);
            out[25 + outPos] = ((in[6 + inPos] & 0xFF) >>> 4 & 0x3);
            out[26 + outPos] = ((in[6 + inPos] & 0xFF) >>> 2 & 0x3);
            out[27 + outPos] = (in[6 + inPos] & 0xFF & 0x3);
            out[28 + outPos] = ((in[7 + inPos] & 0xFF) >>> 6 & 0x3);
            out[29 + outPos] = ((in[7 + inPos] & 0xFF) >>> 4 & 0x3);
            out[30 + outPos] = ((in[7 + inPos] & 0xFF) >>> 2 & 0x3);
            out[31 + outPos] = (in[7 + inPos] & 0xFF & 0x3);
        }
    }
    
    private static final class Packer3 extends BytePacker
    {
        private Packer3() {
            super(3);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x7) << 5 | (in[1 + inPos] & 0x7) << 2 | (in[2 + inPos] & 0x7) >>> 1) & 0xFF);
            out[1 + outPos] = (byte)(((in[2 + inPos] & 0x7) << 7 | (in[3 + inPos] & 0x7) << 4 | (in[4 + inPos] & 0x7) << 1 | (in[5 + inPos] & 0x7) >>> 2) & 0xFF);
            out[2 + outPos] = (byte)(((in[5 + inPos] & 0x7) << 6 | (in[6 + inPos] & 0x7) << 3 | (in[7 + inPos] & 0x7)) & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x7) << 5 | (in[1 + inPos] & 0x7) << 2 | (in[2 + inPos] & 0x7) >>> 1) & 0xFF);
            out[1 + outPos] = (byte)(((in[2 + inPos] & 0x7) << 7 | (in[3 + inPos] & 0x7) << 4 | (in[4 + inPos] & 0x7) << 1 | (in[5 + inPos] & 0x7) >>> 2) & 0xFF);
            out[2 + outPos] = (byte)(((in[5 + inPos] & 0x7) << 6 | (in[6 + inPos] & 0x7) << 3 | (in[7 + inPos] & 0x7)) & 0xFF);
            out[3 + outPos] = (byte)(((in[8 + inPos] & 0x7) << 5 | (in[9 + inPos] & 0x7) << 2 | (in[10 + inPos] & 0x7) >>> 1) & 0xFF);
            out[4 + outPos] = (byte)(((in[10 + inPos] & 0x7) << 7 | (in[11 + inPos] & 0x7) << 4 | (in[12 + inPos] & 0x7) << 1 | (in[13 + inPos] & 0x7) >>> 2) & 0xFF);
            out[5 + outPos] = (byte)(((in[13 + inPos] & 0x7) << 6 | (in[14 + inPos] & 0x7) << 3 | (in[15 + inPos] & 0x7)) & 0xFF);
            out[6 + outPos] = (byte)(((in[16 + inPos] & 0x7) << 5 | (in[17 + inPos] & 0x7) << 2 | (in[18 + inPos] & 0x7) >>> 1) & 0xFF);
            out[7 + outPos] = (byte)(((in[18 + inPos] & 0x7) << 7 | (in[19 + inPos] & 0x7) << 4 | (in[20 + inPos] & 0x7) << 1 | (in[21 + inPos] & 0x7) >>> 2) & 0xFF);
            out[8 + outPos] = (byte)(((in[21 + inPos] & 0x7) << 6 | (in[22 + inPos] & 0x7) << 3 | (in[23 + inPos] & 0x7)) & 0xFF);
            out[9 + outPos] = (byte)(((in[24 + inPos] & 0x7) << 5 | (in[25 + inPos] & 0x7) << 2 | (in[26 + inPos] & 0x7) >>> 1) & 0xFF);
            out[10 + outPos] = (byte)(((in[26 + inPos] & 0x7) << 7 | (in[27 + inPos] & 0x7) << 4 | (in[28 + inPos] & 0x7) << 1 | (in[29 + inPos] & 0x7) >>> 2) & 0xFF);
            out[11 + outPos] = (byte)(((in[29 + inPos] & 0x7) << 6 | (in[30 + inPos] & 0x7) << 3 | (in[31 + inPos] & 0x7)) & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 5 & 0x7);
            out[1 + outPos] = ((in[0 + inPos] & 0xFF) >>> 2 & 0x7);
            out[2 + outPos] = (((in[0 + inPos] & 0xFF) << 1 & 0x7) | ((in[1 + inPos] & 0xFF) >>> 7 & 0x7));
            out[3 + outPos] = ((in[1 + inPos] & 0xFF) >>> 4 & 0x7);
            out[4 + outPos] = ((in[1 + inPos] & 0xFF) >>> 1 & 0x7);
            out[5 + outPos] = (((in[1 + inPos] & 0xFF) << 2 & 0x7) | ((in[2 + inPos] & 0xFF) >>> 6 & 0x7));
            out[6 + outPos] = ((in[2 + inPos] & 0xFF) >>> 3 & 0x7);
            out[7 + outPos] = (in[2 + inPos] & 0xFF & 0x7);
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 5 & 0x7);
            out[1 + outPos] = ((in[0 + inPos] & 0xFF) >>> 2 & 0x7);
            out[2 + outPos] = (((in[0 + inPos] & 0xFF) << 1 & 0x7) | ((in[1 + inPos] & 0xFF) >>> 7 & 0x7));
            out[3 + outPos] = ((in[1 + inPos] & 0xFF) >>> 4 & 0x7);
            out[4 + outPos] = ((in[1 + inPos] & 0xFF) >>> 1 & 0x7);
            out[5 + outPos] = (((in[1 + inPos] & 0xFF) << 2 & 0x7) | ((in[2 + inPos] & 0xFF) >>> 6 & 0x7));
            out[6 + outPos] = ((in[2 + inPos] & 0xFF) >>> 3 & 0x7);
            out[7 + outPos] = (in[2 + inPos] & 0xFF & 0x7);
            out[8 + outPos] = ((in[3 + inPos] & 0xFF) >>> 5 & 0x7);
            out[9 + outPos] = ((in[3 + inPos] & 0xFF) >>> 2 & 0x7);
            out[10 + outPos] = (((in[3 + inPos] & 0xFF) << 1 & 0x7) | ((in[4 + inPos] & 0xFF) >>> 7 & 0x7));
            out[11 + outPos] = ((in[4 + inPos] & 0xFF) >>> 4 & 0x7);
            out[12 + outPos] = ((in[4 + inPos] & 0xFF) >>> 1 & 0x7);
            out[13 + outPos] = (((in[4 + inPos] & 0xFF) << 2 & 0x7) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x7));
            out[14 + outPos] = ((in[5 + inPos] & 0xFF) >>> 3 & 0x7);
            out[15 + outPos] = (in[5 + inPos] & 0xFF & 0x7);
            out[16 + outPos] = ((in[6 + inPos] & 0xFF) >>> 5 & 0x7);
            out[17 + outPos] = ((in[6 + inPos] & 0xFF) >>> 2 & 0x7);
            out[18 + outPos] = (((in[6 + inPos] & 0xFF) << 1 & 0x7) | ((in[7 + inPos] & 0xFF) >>> 7 & 0x7));
            out[19 + outPos] = ((in[7 + inPos] & 0xFF) >>> 4 & 0x7);
            out[20 + outPos] = ((in[7 + inPos] & 0xFF) >>> 1 & 0x7);
            out[21 + outPos] = (((in[7 + inPos] & 0xFF) << 2 & 0x7) | ((in[8 + inPos] & 0xFF) >>> 6 & 0x7));
            out[22 + outPos] = ((in[8 + inPos] & 0xFF) >>> 3 & 0x7);
            out[23 + outPos] = (in[8 + inPos] & 0xFF & 0x7);
            out[24 + outPos] = ((in[9 + inPos] & 0xFF) >>> 5 & 0x7);
            out[25 + outPos] = ((in[9 + inPos] & 0xFF) >>> 2 & 0x7);
            out[26 + outPos] = (((in[9 + inPos] & 0xFF) << 1 & 0x7) | ((in[10 + inPos] & 0xFF) >>> 7 & 0x7));
            out[27 + outPos] = ((in[10 + inPos] & 0xFF) >>> 4 & 0x7);
            out[28 + outPos] = ((in[10 + inPos] & 0xFF) >>> 1 & 0x7);
            out[29 + outPos] = (((in[10 + inPos] & 0xFF) << 2 & 0x7) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x7));
            out[30 + outPos] = ((in[11 + inPos] & 0xFF) >>> 3 & 0x7);
            out[31 + outPos] = (in[11 + inPos] & 0xFF & 0x7);
        }
    }
    
    private static final class Packer4 extends BytePacker
    {
        private Packer4() {
            super(4);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0xF) << 4 | (in[1 + inPos] & 0xF)) & 0xFF);
            out[1 + outPos] = (byte)(((in[2 + inPos] & 0xF) << 4 | (in[3 + inPos] & 0xF)) & 0xFF);
            out[2 + outPos] = (byte)(((in[4 + inPos] & 0xF) << 4 | (in[5 + inPos] & 0xF)) & 0xFF);
            out[3 + outPos] = (byte)(((in[6 + inPos] & 0xF) << 4 | (in[7 + inPos] & 0xF)) & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0xF) << 4 | (in[1 + inPos] & 0xF)) & 0xFF);
            out[1 + outPos] = (byte)(((in[2 + inPos] & 0xF) << 4 | (in[3 + inPos] & 0xF)) & 0xFF);
            out[2 + outPos] = (byte)(((in[4 + inPos] & 0xF) << 4 | (in[5 + inPos] & 0xF)) & 0xFF);
            out[3 + outPos] = (byte)(((in[6 + inPos] & 0xF) << 4 | (in[7 + inPos] & 0xF)) & 0xFF);
            out[4 + outPos] = (byte)(((in[8 + inPos] & 0xF) << 4 | (in[9 + inPos] & 0xF)) & 0xFF);
            out[5 + outPos] = (byte)(((in[10 + inPos] & 0xF) << 4 | (in[11 + inPos] & 0xF)) & 0xFF);
            out[6 + outPos] = (byte)(((in[12 + inPos] & 0xF) << 4 | (in[13 + inPos] & 0xF)) & 0xFF);
            out[7 + outPos] = (byte)(((in[14 + inPos] & 0xF) << 4 | (in[15 + inPos] & 0xF)) & 0xFF);
            out[8 + outPos] = (byte)(((in[16 + inPos] & 0xF) << 4 | (in[17 + inPos] & 0xF)) & 0xFF);
            out[9 + outPos] = (byte)(((in[18 + inPos] & 0xF) << 4 | (in[19 + inPos] & 0xF)) & 0xFF);
            out[10 + outPos] = (byte)(((in[20 + inPos] & 0xF) << 4 | (in[21 + inPos] & 0xF)) & 0xFF);
            out[11 + outPos] = (byte)(((in[22 + inPos] & 0xF) << 4 | (in[23 + inPos] & 0xF)) & 0xFF);
            out[12 + outPos] = (byte)(((in[24 + inPos] & 0xF) << 4 | (in[25 + inPos] & 0xF)) & 0xFF);
            out[13 + outPos] = (byte)(((in[26 + inPos] & 0xF) << 4 | (in[27 + inPos] & 0xF)) & 0xFF);
            out[14 + outPos] = (byte)(((in[28 + inPos] & 0xF) << 4 | (in[29 + inPos] & 0xF)) & 0xFF);
            out[15 + outPos] = (byte)(((in[30 + inPos] & 0xF) << 4 | (in[31 + inPos] & 0xF)) & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 4 & 0xF);
            out[1 + outPos] = (in[0 + inPos] & 0xFF & 0xF);
            out[2 + outPos] = ((in[1 + inPos] & 0xFF) >>> 4 & 0xF);
            out[3 + outPos] = (in[1 + inPos] & 0xFF & 0xF);
            out[4 + outPos] = ((in[2 + inPos] & 0xFF) >>> 4 & 0xF);
            out[5 + outPos] = (in[2 + inPos] & 0xFF & 0xF);
            out[6 + outPos] = ((in[3 + inPos] & 0xFF) >>> 4 & 0xF);
            out[7 + outPos] = (in[3 + inPos] & 0xFF & 0xF);
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 4 & 0xF);
            out[1 + outPos] = (in[0 + inPos] & 0xFF & 0xF);
            out[2 + outPos] = ((in[1 + inPos] & 0xFF) >>> 4 & 0xF);
            out[3 + outPos] = (in[1 + inPos] & 0xFF & 0xF);
            out[4 + outPos] = ((in[2 + inPos] & 0xFF) >>> 4 & 0xF);
            out[5 + outPos] = (in[2 + inPos] & 0xFF & 0xF);
            out[6 + outPos] = ((in[3 + inPos] & 0xFF) >>> 4 & 0xF);
            out[7 + outPos] = (in[3 + inPos] & 0xFF & 0xF);
            out[8 + outPos] = ((in[4 + inPos] & 0xFF) >>> 4 & 0xF);
            out[9 + outPos] = (in[4 + inPos] & 0xFF & 0xF);
            out[10 + outPos] = ((in[5 + inPos] & 0xFF) >>> 4 & 0xF);
            out[11 + outPos] = (in[5 + inPos] & 0xFF & 0xF);
            out[12 + outPos] = ((in[6 + inPos] & 0xFF) >>> 4 & 0xF);
            out[13 + outPos] = (in[6 + inPos] & 0xFF & 0xF);
            out[14 + outPos] = ((in[7 + inPos] & 0xFF) >>> 4 & 0xF);
            out[15 + outPos] = (in[7 + inPos] & 0xFF & 0xF);
            out[16 + outPos] = ((in[8 + inPos] & 0xFF) >>> 4 & 0xF);
            out[17 + outPos] = (in[8 + inPos] & 0xFF & 0xF);
            out[18 + outPos] = ((in[9 + inPos] & 0xFF) >>> 4 & 0xF);
            out[19 + outPos] = (in[9 + inPos] & 0xFF & 0xF);
            out[20 + outPos] = ((in[10 + inPos] & 0xFF) >>> 4 & 0xF);
            out[21 + outPos] = (in[10 + inPos] & 0xFF & 0xF);
            out[22 + outPos] = ((in[11 + inPos] & 0xFF) >>> 4 & 0xF);
            out[23 + outPos] = (in[11 + inPos] & 0xFF & 0xF);
            out[24 + outPos] = ((in[12 + inPos] & 0xFF) >>> 4 & 0xF);
            out[25 + outPos] = (in[12 + inPos] & 0xFF & 0xF);
            out[26 + outPos] = ((in[13 + inPos] & 0xFF) >>> 4 & 0xF);
            out[27 + outPos] = (in[13 + inPos] & 0xFF & 0xF);
            out[28 + outPos] = ((in[14 + inPos] & 0xFF) >>> 4 & 0xF);
            out[29 + outPos] = (in[14 + inPos] & 0xFF & 0xF);
            out[30 + outPos] = ((in[15 + inPos] & 0xFF) >>> 4 & 0xF);
            out[31 + outPos] = (in[15 + inPos] & 0xFF & 0xF);
        }
    }
    
    private static final class Packer5 extends BytePacker
    {
        private Packer5() {
            super(5);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x1F) << 3 | (in[1 + inPos] & 0x1F) >>> 2) & 0xFF);
            out[1 + outPos] = (byte)(((in[1 + inPos] & 0x1F) << 6 | (in[2 + inPos] & 0x1F) << 1 | (in[3 + inPos] & 0x1F) >>> 4) & 0xFF);
            out[2 + outPos] = (byte)(((in[3 + inPos] & 0x1F) << 4 | (in[4 + inPos] & 0x1F) >>> 1) & 0xFF);
            out[3 + outPos] = (byte)(((in[4 + inPos] & 0x1F) << 7 | (in[5 + inPos] & 0x1F) << 2 | (in[6 + inPos] & 0x1F) >>> 3) & 0xFF);
            out[4 + outPos] = (byte)(((in[6 + inPos] & 0x1F) << 5 | (in[7 + inPos] & 0x1F)) & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x1F) << 3 | (in[1 + inPos] & 0x1F) >>> 2) & 0xFF);
            out[1 + outPos] = (byte)(((in[1 + inPos] & 0x1F) << 6 | (in[2 + inPos] & 0x1F) << 1 | (in[3 + inPos] & 0x1F) >>> 4) & 0xFF);
            out[2 + outPos] = (byte)(((in[3 + inPos] & 0x1F) << 4 | (in[4 + inPos] & 0x1F) >>> 1) & 0xFF);
            out[3 + outPos] = (byte)(((in[4 + inPos] & 0x1F) << 7 | (in[5 + inPos] & 0x1F) << 2 | (in[6 + inPos] & 0x1F) >>> 3) & 0xFF);
            out[4 + outPos] = (byte)(((in[6 + inPos] & 0x1F) << 5 | (in[7 + inPos] & 0x1F)) & 0xFF);
            out[5 + outPos] = (byte)(((in[8 + inPos] & 0x1F) << 3 | (in[9 + inPos] & 0x1F) >>> 2) & 0xFF);
            out[6 + outPos] = (byte)(((in[9 + inPos] & 0x1F) << 6 | (in[10 + inPos] & 0x1F) << 1 | (in[11 + inPos] & 0x1F) >>> 4) & 0xFF);
            out[7 + outPos] = (byte)(((in[11 + inPos] & 0x1F) << 4 | (in[12 + inPos] & 0x1F) >>> 1) & 0xFF);
            out[8 + outPos] = (byte)(((in[12 + inPos] & 0x1F) << 7 | (in[13 + inPos] & 0x1F) << 2 | (in[14 + inPos] & 0x1F) >>> 3) & 0xFF);
            out[9 + outPos] = (byte)(((in[14 + inPos] & 0x1F) << 5 | (in[15 + inPos] & 0x1F)) & 0xFF);
            out[10 + outPos] = (byte)(((in[16 + inPos] & 0x1F) << 3 | (in[17 + inPos] & 0x1F) >>> 2) & 0xFF);
            out[11 + outPos] = (byte)(((in[17 + inPos] & 0x1F) << 6 | (in[18 + inPos] & 0x1F) << 1 | (in[19 + inPos] & 0x1F) >>> 4) & 0xFF);
            out[12 + outPos] = (byte)(((in[19 + inPos] & 0x1F) << 4 | (in[20 + inPos] & 0x1F) >>> 1) & 0xFF);
            out[13 + outPos] = (byte)(((in[20 + inPos] & 0x1F) << 7 | (in[21 + inPos] & 0x1F) << 2 | (in[22 + inPos] & 0x1F) >>> 3) & 0xFF);
            out[14 + outPos] = (byte)(((in[22 + inPos] & 0x1F) << 5 | (in[23 + inPos] & 0x1F)) & 0xFF);
            out[15 + outPos] = (byte)(((in[24 + inPos] & 0x1F) << 3 | (in[25 + inPos] & 0x1F) >>> 2) & 0xFF);
            out[16 + outPos] = (byte)(((in[25 + inPos] & 0x1F) << 6 | (in[26 + inPos] & 0x1F) << 1 | (in[27 + inPos] & 0x1F) >>> 4) & 0xFF);
            out[17 + outPos] = (byte)(((in[27 + inPos] & 0x1F) << 4 | (in[28 + inPos] & 0x1F) >>> 1) & 0xFF);
            out[18 + outPos] = (byte)(((in[28 + inPos] & 0x1F) << 7 | (in[29 + inPos] & 0x1F) << 2 | (in[30 + inPos] & 0x1F) >>> 3) & 0xFF);
            out[19 + outPos] = (byte)(((in[30 + inPos] & 0x1F) << 5 | (in[31 + inPos] & 0x1F)) & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 3 & 0x1F);
            out[1 + outPos] = (((in[0 + inPos] & 0xFF) << 2 & 0x1F) | ((in[1 + inPos] & 0xFF) >>> 6 & 0x1F));
            out[2 + outPos] = ((in[1 + inPos] & 0xFF) >>> 1 & 0x1F);
            out[3 + outPos] = (((in[1 + inPos] & 0xFF) << 4 & 0x1F) | ((in[2 + inPos] & 0xFF) >>> 4 & 0x1F));
            out[4 + outPos] = (((in[2 + inPos] & 0xFF) << 1 & 0x1F) | ((in[3 + inPos] & 0xFF) >>> 7 & 0x1F));
            out[5 + outPos] = ((in[3 + inPos] & 0xFF) >>> 2 & 0x1F);
            out[6 + outPos] = (((in[3 + inPos] & 0xFF) << 3 & 0x1F) | ((in[4 + inPos] & 0xFF) >>> 5 & 0x1F));
            out[7 + outPos] = (in[4 + inPos] & 0xFF & 0x1F);
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 3 & 0x1F);
            out[1 + outPos] = (((in[0 + inPos] & 0xFF) << 2 & 0x1F) | ((in[1 + inPos] & 0xFF) >>> 6 & 0x1F));
            out[2 + outPos] = ((in[1 + inPos] & 0xFF) >>> 1 & 0x1F);
            out[3 + outPos] = (((in[1 + inPos] & 0xFF) << 4 & 0x1F) | ((in[2 + inPos] & 0xFF) >>> 4 & 0x1F));
            out[4 + outPos] = (((in[2 + inPos] & 0xFF) << 1 & 0x1F) | ((in[3 + inPos] & 0xFF) >>> 7 & 0x1F));
            out[5 + outPos] = ((in[3 + inPos] & 0xFF) >>> 2 & 0x1F);
            out[6 + outPos] = (((in[3 + inPos] & 0xFF) << 3 & 0x1F) | ((in[4 + inPos] & 0xFF) >>> 5 & 0x1F));
            out[7 + outPos] = (in[4 + inPos] & 0xFF & 0x1F);
            out[8 + outPos] = ((in[5 + inPos] & 0xFF) >>> 3 & 0x1F);
            out[9 + outPos] = (((in[5 + inPos] & 0xFF) << 2 & 0x1F) | ((in[6 + inPos] & 0xFF) >>> 6 & 0x1F));
            out[10 + outPos] = ((in[6 + inPos] & 0xFF) >>> 1 & 0x1F);
            out[11 + outPos] = (((in[6 + inPos] & 0xFF) << 4 & 0x1F) | ((in[7 + inPos] & 0xFF) >>> 4 & 0x1F));
            out[12 + outPos] = (((in[7 + inPos] & 0xFF) << 1 & 0x1F) | ((in[8 + inPos] & 0xFF) >>> 7 & 0x1F));
            out[13 + outPos] = ((in[8 + inPos] & 0xFF) >>> 2 & 0x1F);
            out[14 + outPos] = (((in[8 + inPos] & 0xFF) << 3 & 0x1F) | ((in[9 + inPos] & 0xFF) >>> 5 & 0x1F));
            out[15 + outPos] = (in[9 + inPos] & 0xFF & 0x1F);
            out[16 + outPos] = ((in[10 + inPos] & 0xFF) >>> 3 & 0x1F);
            out[17 + outPos] = (((in[10 + inPos] & 0xFF) << 2 & 0x1F) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x1F));
            out[18 + outPos] = ((in[11 + inPos] & 0xFF) >>> 1 & 0x1F);
            out[19 + outPos] = (((in[11 + inPos] & 0xFF) << 4 & 0x1F) | ((in[12 + inPos] & 0xFF) >>> 4 & 0x1F));
            out[20 + outPos] = (((in[12 + inPos] & 0xFF) << 1 & 0x1F) | ((in[13 + inPos] & 0xFF) >>> 7 & 0x1F));
            out[21 + outPos] = ((in[13 + inPos] & 0xFF) >>> 2 & 0x1F);
            out[22 + outPos] = (((in[13 + inPos] & 0xFF) << 3 & 0x1F) | ((in[14 + inPos] & 0xFF) >>> 5 & 0x1F));
            out[23 + outPos] = (in[14 + inPos] & 0xFF & 0x1F);
            out[24 + outPos] = ((in[15 + inPos] & 0xFF) >>> 3 & 0x1F);
            out[25 + outPos] = (((in[15 + inPos] & 0xFF) << 2 & 0x1F) | ((in[16 + inPos] & 0xFF) >>> 6 & 0x1F));
            out[26 + outPos] = ((in[16 + inPos] & 0xFF) >>> 1 & 0x1F);
            out[27 + outPos] = (((in[16 + inPos] & 0xFF) << 4 & 0x1F) | ((in[17 + inPos] & 0xFF) >>> 4 & 0x1F));
            out[28 + outPos] = (((in[17 + inPos] & 0xFF) << 1 & 0x1F) | ((in[18 + inPos] & 0xFF) >>> 7 & 0x1F));
            out[29 + outPos] = ((in[18 + inPos] & 0xFF) >>> 2 & 0x1F);
            out[30 + outPos] = (((in[18 + inPos] & 0xFF) << 3 & 0x1F) | ((in[19 + inPos] & 0xFF) >>> 5 & 0x1F));
            out[31 + outPos] = (in[19 + inPos] & 0xFF & 0x1F);
        }
    }
    
    private static final class Packer6 extends BytePacker
    {
        private Packer6() {
            super(6);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x3F) << 2 | (in[1 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[1 + outPos] = (byte)(((in[1 + inPos] & 0x3F) << 4 | (in[2 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[2 + outPos] = (byte)(((in[2 + inPos] & 0x3F) << 6 | (in[3 + inPos] & 0x3F)) & 0xFF);
            out[3 + outPos] = (byte)(((in[4 + inPos] & 0x3F) << 2 | (in[5 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[4 + outPos] = (byte)(((in[5 + inPos] & 0x3F) << 4 | (in[6 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[5 + outPos] = (byte)(((in[6 + inPos] & 0x3F) << 6 | (in[7 + inPos] & 0x3F)) & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x3F) << 2 | (in[1 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[1 + outPos] = (byte)(((in[1 + inPos] & 0x3F) << 4 | (in[2 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[2 + outPos] = (byte)(((in[2 + inPos] & 0x3F) << 6 | (in[3 + inPos] & 0x3F)) & 0xFF);
            out[3 + outPos] = (byte)(((in[4 + inPos] & 0x3F) << 2 | (in[5 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[4 + outPos] = (byte)(((in[5 + inPos] & 0x3F) << 4 | (in[6 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[5 + outPos] = (byte)(((in[6 + inPos] & 0x3F) << 6 | (in[7 + inPos] & 0x3F)) & 0xFF);
            out[6 + outPos] = (byte)(((in[8 + inPos] & 0x3F) << 2 | (in[9 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[7 + outPos] = (byte)(((in[9 + inPos] & 0x3F) << 4 | (in[10 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[8 + outPos] = (byte)(((in[10 + inPos] & 0x3F) << 6 | (in[11 + inPos] & 0x3F)) & 0xFF);
            out[9 + outPos] = (byte)(((in[12 + inPos] & 0x3F) << 2 | (in[13 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[10 + outPos] = (byte)(((in[13 + inPos] & 0x3F) << 4 | (in[14 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[11 + outPos] = (byte)(((in[14 + inPos] & 0x3F) << 6 | (in[15 + inPos] & 0x3F)) & 0xFF);
            out[12 + outPos] = (byte)(((in[16 + inPos] & 0x3F) << 2 | (in[17 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[13 + outPos] = (byte)(((in[17 + inPos] & 0x3F) << 4 | (in[18 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[14 + outPos] = (byte)(((in[18 + inPos] & 0x3F) << 6 | (in[19 + inPos] & 0x3F)) & 0xFF);
            out[15 + outPos] = (byte)(((in[20 + inPos] & 0x3F) << 2 | (in[21 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[16 + outPos] = (byte)(((in[21 + inPos] & 0x3F) << 4 | (in[22 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[17 + outPos] = (byte)(((in[22 + inPos] & 0x3F) << 6 | (in[23 + inPos] & 0x3F)) & 0xFF);
            out[18 + outPos] = (byte)(((in[24 + inPos] & 0x3F) << 2 | (in[25 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[19 + outPos] = (byte)(((in[25 + inPos] & 0x3F) << 4 | (in[26 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[20 + outPos] = (byte)(((in[26 + inPos] & 0x3F) << 6 | (in[27 + inPos] & 0x3F)) & 0xFF);
            out[21 + outPos] = (byte)(((in[28 + inPos] & 0x3F) << 2 | (in[29 + inPos] & 0x3F) >>> 4) & 0xFF);
            out[22 + outPos] = (byte)(((in[29 + inPos] & 0x3F) << 4 | (in[30 + inPos] & 0x3F) >>> 2) & 0xFF);
            out[23 + outPos] = (byte)(((in[30 + inPos] & 0x3F) << 6 | (in[31 + inPos] & 0x3F)) & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[1 + outPos] = (((in[0 + inPos] & 0xFF) << 4 & 0x3F) | ((in[1 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[2 + outPos] = (((in[1 + inPos] & 0xFF) << 2 & 0x3F) | ((in[2 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[3 + outPos] = (in[2 + inPos] & 0xFF & 0x3F);
            out[4 + outPos] = ((in[3 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[5 + outPos] = (((in[3 + inPos] & 0xFF) << 4 & 0x3F) | ((in[4 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[6 + outPos] = (((in[4 + inPos] & 0xFF) << 2 & 0x3F) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[7 + outPos] = (in[5 + inPos] & 0xFF & 0x3F);
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[1 + outPos] = (((in[0 + inPos] & 0xFF) << 4 & 0x3F) | ((in[1 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[2 + outPos] = (((in[1 + inPos] & 0xFF) << 2 & 0x3F) | ((in[2 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[3 + outPos] = (in[2 + inPos] & 0xFF & 0x3F);
            out[4 + outPos] = ((in[3 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[5 + outPos] = (((in[3 + inPos] & 0xFF) << 4 & 0x3F) | ((in[4 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[6 + outPos] = (((in[4 + inPos] & 0xFF) << 2 & 0x3F) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[7 + outPos] = (in[5 + inPos] & 0xFF & 0x3F);
            out[8 + outPos] = ((in[6 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[9 + outPos] = (((in[6 + inPos] & 0xFF) << 4 & 0x3F) | ((in[7 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[10 + outPos] = (((in[7 + inPos] & 0xFF) << 2 & 0x3F) | ((in[8 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[11 + outPos] = (in[8 + inPos] & 0xFF & 0x3F);
            out[12 + outPos] = ((in[9 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[13 + outPos] = (((in[9 + inPos] & 0xFF) << 4 & 0x3F) | ((in[10 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[14 + outPos] = (((in[10 + inPos] & 0xFF) << 2 & 0x3F) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[15 + outPos] = (in[11 + inPos] & 0xFF & 0x3F);
            out[16 + outPos] = ((in[12 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[17 + outPos] = (((in[12 + inPos] & 0xFF) << 4 & 0x3F) | ((in[13 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[18 + outPos] = (((in[13 + inPos] & 0xFF) << 2 & 0x3F) | ((in[14 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[19 + outPos] = (in[14 + inPos] & 0xFF & 0x3F);
            out[20 + outPos] = ((in[15 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[21 + outPos] = (((in[15 + inPos] & 0xFF) << 4 & 0x3F) | ((in[16 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[22 + outPos] = (((in[16 + inPos] & 0xFF) << 2 & 0x3F) | ((in[17 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[23 + outPos] = (in[17 + inPos] & 0xFF & 0x3F);
            out[24 + outPos] = ((in[18 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[25 + outPos] = (((in[18 + inPos] & 0xFF) << 4 & 0x3F) | ((in[19 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[26 + outPos] = (((in[19 + inPos] & 0xFF) << 2 & 0x3F) | ((in[20 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[27 + outPos] = (in[20 + inPos] & 0xFF & 0x3F);
            out[28 + outPos] = ((in[21 + inPos] & 0xFF) >>> 2 & 0x3F);
            out[29 + outPos] = (((in[21 + inPos] & 0xFF) << 4 & 0x3F) | ((in[22 + inPos] & 0xFF) >>> 4 & 0x3F));
            out[30 + outPos] = (((in[22 + inPos] & 0xFF) << 2 & 0x3F) | ((in[23 + inPos] & 0xFF) >>> 6 & 0x3F));
            out[31 + outPos] = (in[23 + inPos] & 0xFF & 0x3F);
        }
    }
    
    private static final class Packer7 extends BytePacker
    {
        private Packer7() {
            super(7);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x7F) << 1 | (in[1 + inPos] & 0x7F) >>> 6) & 0xFF);
            out[1 + outPos] = (byte)(((in[1 + inPos] & 0x7F) << 2 | (in[2 + inPos] & 0x7F) >>> 5) & 0xFF);
            out[2 + outPos] = (byte)(((in[2 + inPos] & 0x7F) << 3 | (in[3 + inPos] & 0x7F) >>> 4) & 0xFF);
            out[3 + outPos] = (byte)(((in[3 + inPos] & 0x7F) << 4 | (in[4 + inPos] & 0x7F) >>> 3) & 0xFF);
            out[4 + outPos] = (byte)(((in[4 + inPos] & 0x7F) << 5 | (in[5 + inPos] & 0x7F) >>> 2) & 0xFF);
            out[5 + outPos] = (byte)(((in[5 + inPos] & 0x7F) << 6 | (in[6 + inPos] & 0x7F) >>> 1) & 0xFF);
            out[6 + outPos] = (byte)(((in[6 + inPos] & 0x7F) << 7 | (in[7 + inPos] & 0x7F)) & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(((in[0 + inPos] & 0x7F) << 1 | (in[1 + inPos] & 0x7F) >>> 6) & 0xFF);
            out[1 + outPos] = (byte)(((in[1 + inPos] & 0x7F) << 2 | (in[2 + inPos] & 0x7F) >>> 5) & 0xFF);
            out[2 + outPos] = (byte)(((in[2 + inPos] & 0x7F) << 3 | (in[3 + inPos] & 0x7F) >>> 4) & 0xFF);
            out[3 + outPos] = (byte)(((in[3 + inPos] & 0x7F) << 4 | (in[4 + inPos] & 0x7F) >>> 3) & 0xFF);
            out[4 + outPos] = (byte)(((in[4 + inPos] & 0x7F) << 5 | (in[5 + inPos] & 0x7F) >>> 2) & 0xFF);
            out[5 + outPos] = (byte)(((in[5 + inPos] & 0x7F) << 6 | (in[6 + inPos] & 0x7F) >>> 1) & 0xFF);
            out[6 + outPos] = (byte)(((in[6 + inPos] & 0x7F) << 7 | (in[7 + inPos] & 0x7F)) & 0xFF);
            out[7 + outPos] = (byte)(((in[8 + inPos] & 0x7F) << 1 | (in[9 + inPos] & 0x7F) >>> 6) & 0xFF);
            out[8 + outPos] = (byte)(((in[9 + inPos] & 0x7F) << 2 | (in[10 + inPos] & 0x7F) >>> 5) & 0xFF);
            out[9 + outPos] = (byte)(((in[10 + inPos] & 0x7F) << 3 | (in[11 + inPos] & 0x7F) >>> 4) & 0xFF);
            out[10 + outPos] = (byte)(((in[11 + inPos] & 0x7F) << 4 | (in[12 + inPos] & 0x7F) >>> 3) & 0xFF);
            out[11 + outPos] = (byte)(((in[12 + inPos] & 0x7F) << 5 | (in[13 + inPos] & 0x7F) >>> 2) & 0xFF);
            out[12 + outPos] = (byte)(((in[13 + inPos] & 0x7F) << 6 | (in[14 + inPos] & 0x7F) >>> 1) & 0xFF);
            out[13 + outPos] = (byte)(((in[14 + inPos] & 0x7F) << 7 | (in[15 + inPos] & 0x7F)) & 0xFF);
            out[14 + outPos] = (byte)(((in[16 + inPos] & 0x7F) << 1 | (in[17 + inPos] & 0x7F) >>> 6) & 0xFF);
            out[15 + outPos] = (byte)(((in[17 + inPos] & 0x7F) << 2 | (in[18 + inPos] & 0x7F) >>> 5) & 0xFF);
            out[16 + outPos] = (byte)(((in[18 + inPos] & 0x7F) << 3 | (in[19 + inPos] & 0x7F) >>> 4) & 0xFF);
            out[17 + outPos] = (byte)(((in[19 + inPos] & 0x7F) << 4 | (in[20 + inPos] & 0x7F) >>> 3) & 0xFF);
            out[18 + outPos] = (byte)(((in[20 + inPos] & 0x7F) << 5 | (in[21 + inPos] & 0x7F) >>> 2) & 0xFF);
            out[19 + outPos] = (byte)(((in[21 + inPos] & 0x7F) << 6 | (in[22 + inPos] & 0x7F) >>> 1) & 0xFF);
            out[20 + outPos] = (byte)(((in[22 + inPos] & 0x7F) << 7 | (in[23 + inPos] & 0x7F)) & 0xFF);
            out[21 + outPos] = (byte)(((in[24 + inPos] & 0x7F) << 1 | (in[25 + inPos] & 0x7F) >>> 6) & 0xFF);
            out[22 + outPos] = (byte)(((in[25 + inPos] & 0x7F) << 2 | (in[26 + inPos] & 0x7F) >>> 5) & 0xFF);
            out[23 + outPos] = (byte)(((in[26 + inPos] & 0x7F) << 3 | (in[27 + inPos] & 0x7F) >>> 4) & 0xFF);
            out[24 + outPos] = (byte)(((in[27 + inPos] & 0x7F) << 4 | (in[28 + inPos] & 0x7F) >>> 3) & 0xFF);
            out[25 + outPos] = (byte)(((in[28 + inPos] & 0x7F) << 5 | (in[29 + inPos] & 0x7F) >>> 2) & 0xFF);
            out[26 + outPos] = (byte)(((in[29 + inPos] & 0x7F) << 6 | (in[30 + inPos] & 0x7F) >>> 1) & 0xFF);
            out[27 + outPos] = (byte)(((in[30 + inPos] & 0x7F) << 7 | (in[31 + inPos] & 0x7F)) & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 1 & 0x7F);
            out[1 + outPos] = (((in[0 + inPos] & 0xFF) << 6 & 0x7F) | ((in[1 + inPos] & 0xFF) >>> 2 & 0x7F));
            out[2 + outPos] = (((in[1 + inPos] & 0xFF) << 5 & 0x7F) | ((in[2 + inPos] & 0xFF) >>> 3 & 0x7F));
            out[3 + outPos] = (((in[2 + inPos] & 0xFF) << 4 & 0x7F) | ((in[3 + inPos] & 0xFF) >>> 4 & 0x7F));
            out[4 + outPos] = (((in[3 + inPos] & 0xFF) << 3 & 0x7F) | ((in[4 + inPos] & 0xFF) >>> 5 & 0x7F));
            out[5 + outPos] = (((in[4 + inPos] & 0xFF) << 2 & 0x7F) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x7F));
            out[6 + outPos] = (((in[5 + inPos] & 0xFF) << 1 & 0x7F) | ((in[6 + inPos] & 0xFF) >>> 7 & 0x7F));
            out[7 + outPos] = (in[6 + inPos] & 0xFF & 0x7F);
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = ((in[0 + inPos] & 0xFF) >>> 1 & 0x7F);
            out[1 + outPos] = (((in[0 + inPos] & 0xFF) << 6 & 0x7F) | ((in[1 + inPos] & 0xFF) >>> 2 & 0x7F));
            out[2 + outPos] = (((in[1 + inPos] & 0xFF) << 5 & 0x7F) | ((in[2 + inPos] & 0xFF) >>> 3 & 0x7F));
            out[3 + outPos] = (((in[2 + inPos] & 0xFF) << 4 & 0x7F) | ((in[3 + inPos] & 0xFF) >>> 4 & 0x7F));
            out[4 + outPos] = (((in[3 + inPos] & 0xFF) << 3 & 0x7F) | ((in[4 + inPos] & 0xFF) >>> 5 & 0x7F));
            out[5 + outPos] = (((in[4 + inPos] & 0xFF) << 2 & 0x7F) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x7F));
            out[6 + outPos] = (((in[5 + inPos] & 0xFF) << 1 & 0x7F) | ((in[6 + inPos] & 0xFF) >>> 7 & 0x7F));
            out[7 + outPos] = (in[6 + inPos] & 0xFF & 0x7F);
            out[8 + outPos] = ((in[7 + inPos] & 0xFF) >>> 1 & 0x7F);
            out[9 + outPos] = (((in[7 + inPos] & 0xFF) << 6 & 0x7F) | ((in[8 + inPos] & 0xFF) >>> 2 & 0x7F));
            out[10 + outPos] = (((in[8 + inPos] & 0xFF) << 5 & 0x7F) | ((in[9 + inPos] & 0xFF) >>> 3 & 0x7F));
            out[11 + outPos] = (((in[9 + inPos] & 0xFF) << 4 & 0x7F) | ((in[10 + inPos] & 0xFF) >>> 4 & 0x7F));
            out[12 + outPos] = (((in[10 + inPos] & 0xFF) << 3 & 0x7F) | ((in[11 + inPos] & 0xFF) >>> 5 & 0x7F));
            out[13 + outPos] = (((in[11 + inPos] & 0xFF) << 2 & 0x7F) | ((in[12 + inPos] & 0xFF) >>> 6 & 0x7F));
            out[14 + outPos] = (((in[12 + inPos] & 0xFF) << 1 & 0x7F) | ((in[13 + inPos] & 0xFF) >>> 7 & 0x7F));
            out[15 + outPos] = (in[13 + inPos] & 0xFF & 0x7F);
            out[16 + outPos] = ((in[14 + inPos] & 0xFF) >>> 1 & 0x7F);
            out[17 + outPos] = (((in[14 + inPos] & 0xFF) << 6 & 0x7F) | ((in[15 + inPos] & 0xFF) >>> 2 & 0x7F));
            out[18 + outPos] = (((in[15 + inPos] & 0xFF) << 5 & 0x7F) | ((in[16 + inPos] & 0xFF) >>> 3 & 0x7F));
            out[19 + outPos] = (((in[16 + inPos] & 0xFF) << 4 & 0x7F) | ((in[17 + inPos] & 0xFF) >>> 4 & 0x7F));
            out[20 + outPos] = (((in[17 + inPos] & 0xFF) << 3 & 0x7F) | ((in[18 + inPos] & 0xFF) >>> 5 & 0x7F));
            out[21 + outPos] = (((in[18 + inPos] & 0xFF) << 2 & 0x7F) | ((in[19 + inPos] & 0xFF) >>> 6 & 0x7F));
            out[22 + outPos] = (((in[19 + inPos] & 0xFF) << 1 & 0x7F) | ((in[20 + inPos] & 0xFF) >>> 7 & 0x7F));
            out[23 + outPos] = (in[20 + inPos] & 0xFF & 0x7F);
            out[24 + outPos] = ((in[21 + inPos] & 0xFF) >>> 1 & 0x7F);
            out[25 + outPos] = (((in[21 + inPos] & 0xFF) << 6 & 0x7F) | ((in[22 + inPos] & 0xFF) >>> 2 & 0x7F));
            out[26 + outPos] = (((in[22 + inPos] & 0xFF) << 5 & 0x7F) | ((in[23 + inPos] & 0xFF) >>> 3 & 0x7F));
            out[27 + outPos] = (((in[23 + inPos] & 0xFF) << 4 & 0x7F) | ((in[24 + inPos] & 0xFF) >>> 4 & 0x7F));
            out[28 + outPos] = (((in[24 + inPos] & 0xFF) << 3 & 0x7F) | ((in[25 + inPos] & 0xFF) >>> 5 & 0x7F));
            out[29 + outPos] = (((in[25 + inPos] & 0xFF) << 2 & 0x7F) | ((in[26 + inPos] & 0xFF) >>> 6 & 0x7F));
            out[30 + outPos] = (((in[26 + inPos] & 0xFF) << 1 & 0x7F) | ((in[27 + inPos] & 0xFF) >>> 7 & 0x7F));
            out[31 + outPos] = (in[27 + inPos] & 0xFF & 0x7F);
        }
    }
    
    private static final class Packer8 extends BytePacker
    {
        private Packer8() {
            super(8);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(in[0 + inPos] & 0xFF & 0xFF);
            out[1 + outPos] = (byte)(in[1 + inPos] & 0xFF & 0xFF);
            out[2 + outPos] = (byte)(in[2 + inPos] & 0xFF & 0xFF);
            out[3 + outPos] = (byte)(in[3 + inPos] & 0xFF & 0xFF);
            out[4 + outPos] = (byte)(in[4 + inPos] & 0xFF & 0xFF);
            out[5 + outPos] = (byte)(in[5 + inPos] & 0xFF & 0xFF);
            out[6 + outPos] = (byte)(in[6 + inPos] & 0xFF & 0xFF);
            out[7 + outPos] = (byte)(in[7 + inPos] & 0xFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)(in[0 + inPos] & 0xFF & 0xFF);
            out[1 + outPos] = (byte)(in[1 + inPos] & 0xFF & 0xFF);
            out[2 + outPos] = (byte)(in[2 + inPos] & 0xFF & 0xFF);
            out[3 + outPos] = (byte)(in[3 + inPos] & 0xFF & 0xFF);
            out[4 + outPos] = (byte)(in[4 + inPos] & 0xFF & 0xFF);
            out[5 + outPos] = (byte)(in[5 + inPos] & 0xFF & 0xFF);
            out[6 + outPos] = (byte)(in[6 + inPos] & 0xFF & 0xFF);
            out[7 + outPos] = (byte)(in[7 + inPos] & 0xFF & 0xFF);
            out[8 + outPos] = (byte)(in[8 + inPos] & 0xFF & 0xFF);
            out[9 + outPos] = (byte)(in[9 + inPos] & 0xFF & 0xFF);
            out[10 + outPos] = (byte)(in[10 + inPos] & 0xFF & 0xFF);
            out[11 + outPos] = (byte)(in[11 + inPos] & 0xFF & 0xFF);
            out[12 + outPos] = (byte)(in[12 + inPos] & 0xFF & 0xFF);
            out[13 + outPos] = (byte)(in[13 + inPos] & 0xFF & 0xFF);
            out[14 + outPos] = (byte)(in[14 + inPos] & 0xFF & 0xFF);
            out[15 + outPos] = (byte)(in[15 + inPos] & 0xFF & 0xFF);
            out[16 + outPos] = (byte)(in[16 + inPos] & 0xFF & 0xFF);
            out[17 + outPos] = (byte)(in[17 + inPos] & 0xFF & 0xFF);
            out[18 + outPos] = (byte)(in[18 + inPos] & 0xFF & 0xFF);
            out[19 + outPos] = (byte)(in[19 + inPos] & 0xFF & 0xFF);
            out[20 + outPos] = (byte)(in[20 + inPos] & 0xFF & 0xFF);
            out[21 + outPos] = (byte)(in[21 + inPos] & 0xFF & 0xFF);
            out[22 + outPos] = (byte)(in[22 + inPos] & 0xFF & 0xFF);
            out[23 + outPos] = (byte)(in[23 + inPos] & 0xFF & 0xFF);
            out[24 + outPos] = (byte)(in[24 + inPos] & 0xFF & 0xFF);
            out[25 + outPos] = (byte)(in[25 + inPos] & 0xFF & 0xFF);
            out[26 + outPos] = (byte)(in[26 + inPos] & 0xFF & 0xFF);
            out[27 + outPos] = (byte)(in[27 + inPos] & 0xFF & 0xFF);
            out[28 + outPos] = (byte)(in[28 + inPos] & 0xFF & 0xFF);
            out[29 + outPos] = (byte)(in[29 + inPos] & 0xFF & 0xFF);
            out[30 + outPos] = (byte)(in[30 + inPos] & 0xFF & 0xFF);
            out[31 + outPos] = (byte)(in[31 + inPos] & 0xFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] & 0xFF & 0xFF);
            out[1 + outPos] = (in[1 + inPos] & 0xFF & 0xFF);
            out[2 + outPos] = (in[2 + inPos] & 0xFF & 0xFF);
            out[3 + outPos] = (in[3 + inPos] & 0xFF & 0xFF);
            out[4 + outPos] = (in[4 + inPos] & 0xFF & 0xFF);
            out[5 + outPos] = (in[5 + inPos] & 0xFF & 0xFF);
            out[6 + outPos] = (in[6 + inPos] & 0xFF & 0xFF);
            out[7 + outPos] = (in[7 + inPos] & 0xFF & 0xFF);
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (in[0 + inPos] & 0xFF & 0xFF);
            out[1 + outPos] = (in[1 + inPos] & 0xFF & 0xFF);
            out[2 + outPos] = (in[2 + inPos] & 0xFF & 0xFF);
            out[3 + outPos] = (in[3 + inPos] & 0xFF & 0xFF);
            out[4 + outPos] = (in[4 + inPos] & 0xFF & 0xFF);
            out[5 + outPos] = (in[5 + inPos] & 0xFF & 0xFF);
            out[6 + outPos] = (in[6 + inPos] & 0xFF & 0xFF);
            out[7 + outPos] = (in[7 + inPos] & 0xFF & 0xFF);
            out[8 + outPos] = (in[8 + inPos] & 0xFF & 0xFF);
            out[9 + outPos] = (in[9 + inPos] & 0xFF & 0xFF);
            out[10 + outPos] = (in[10 + inPos] & 0xFF & 0xFF);
            out[11 + outPos] = (in[11 + inPos] & 0xFF & 0xFF);
            out[12 + outPos] = (in[12 + inPos] & 0xFF & 0xFF);
            out[13 + outPos] = (in[13 + inPos] & 0xFF & 0xFF);
            out[14 + outPos] = (in[14 + inPos] & 0xFF & 0xFF);
            out[15 + outPos] = (in[15 + inPos] & 0xFF & 0xFF);
            out[16 + outPos] = (in[16 + inPos] & 0xFF & 0xFF);
            out[17 + outPos] = (in[17 + inPos] & 0xFF & 0xFF);
            out[18 + outPos] = (in[18 + inPos] & 0xFF & 0xFF);
            out[19 + outPos] = (in[19 + inPos] & 0xFF & 0xFF);
            out[20 + outPos] = (in[20 + inPos] & 0xFF & 0xFF);
            out[21 + outPos] = (in[21 + inPos] & 0xFF & 0xFF);
            out[22 + outPos] = (in[22 + inPos] & 0xFF & 0xFF);
            out[23 + outPos] = (in[23 + inPos] & 0xFF & 0xFF);
            out[24 + outPos] = (in[24 + inPos] & 0xFF & 0xFF);
            out[25 + outPos] = (in[25 + inPos] & 0xFF & 0xFF);
            out[26 + outPos] = (in[26 + inPos] & 0xFF & 0xFF);
            out[27 + outPos] = (in[27 + inPos] & 0xFF & 0xFF);
            out[28 + outPos] = (in[28 + inPos] & 0xFF & 0xFF);
            out[29 + outPos] = (in[29 + inPos] & 0xFF & 0xFF);
            out[30 + outPos] = (in[30 + inPos] & 0xFF & 0xFF);
            out[31 + outPos] = (in[31 + inPos] & 0xFF & 0xFF);
        }
    }
    
    private static final class Packer9 extends BytePacker
    {
        private Packer9() {
            super(9);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FF) >>> 1 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x1FF) << 7 | (in[1 + inPos] & 0x1FF) >>> 2) & 0xFF);
            out[2 + outPos] = (byte)(((in[1 + inPos] & 0x1FF) << 6 | (in[2 + inPos] & 0x1FF) >>> 3) & 0xFF);
            out[3 + outPos] = (byte)(((in[2 + inPos] & 0x1FF) << 5 | (in[3 + inPos] & 0x1FF) >>> 4) & 0xFF);
            out[4 + outPos] = (byte)(((in[3 + inPos] & 0x1FF) << 4 | (in[4 + inPos] & 0x1FF) >>> 5) & 0xFF);
            out[5 + outPos] = (byte)(((in[4 + inPos] & 0x1FF) << 3 | (in[5 + inPos] & 0x1FF) >>> 6) & 0xFF);
            out[6 + outPos] = (byte)(((in[5 + inPos] & 0x1FF) << 2 | (in[6 + inPos] & 0x1FF) >>> 7) & 0xFF);
            out[7 + outPos] = (byte)(((in[6 + inPos] & 0x1FF) << 1 | (in[7 + inPos] & 0x1FF) >>> 8) & 0xFF);
            out[8 + outPos] = (byte)(in[7 + inPos] & 0x1FF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FF) >>> 1 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x1FF) << 7 | (in[1 + inPos] & 0x1FF) >>> 2) & 0xFF);
            out[2 + outPos] = (byte)(((in[1 + inPos] & 0x1FF) << 6 | (in[2 + inPos] & 0x1FF) >>> 3) & 0xFF);
            out[3 + outPos] = (byte)(((in[2 + inPos] & 0x1FF) << 5 | (in[3 + inPos] & 0x1FF) >>> 4) & 0xFF);
            out[4 + outPos] = (byte)(((in[3 + inPos] & 0x1FF) << 4 | (in[4 + inPos] & 0x1FF) >>> 5) & 0xFF);
            out[5 + outPos] = (byte)(((in[4 + inPos] & 0x1FF) << 3 | (in[5 + inPos] & 0x1FF) >>> 6) & 0xFF);
            out[6 + outPos] = (byte)(((in[5 + inPos] & 0x1FF) << 2 | (in[6 + inPos] & 0x1FF) >>> 7) & 0xFF);
            out[7 + outPos] = (byte)(((in[6 + inPos] & 0x1FF) << 1 | (in[7 + inPos] & 0x1FF) >>> 8) & 0xFF);
            out[8 + outPos] = (byte)(in[7 + inPos] & 0x1FF & 0xFF);
            out[9 + outPos] = (byte)((in[8 + inPos] & 0x1FF) >>> 1 & 0xFF);
            out[10 + outPos] = (byte)(((in[8 + inPos] & 0x1FF) << 7 | (in[9 + inPos] & 0x1FF) >>> 2) & 0xFF);
            out[11 + outPos] = (byte)(((in[9 + inPos] & 0x1FF) << 6 | (in[10 + inPos] & 0x1FF) >>> 3) & 0xFF);
            out[12 + outPos] = (byte)(((in[10 + inPos] & 0x1FF) << 5 | (in[11 + inPos] & 0x1FF) >>> 4) & 0xFF);
            out[13 + outPos] = (byte)(((in[11 + inPos] & 0x1FF) << 4 | (in[12 + inPos] & 0x1FF) >>> 5) & 0xFF);
            out[14 + outPos] = (byte)(((in[12 + inPos] & 0x1FF) << 3 | (in[13 + inPos] & 0x1FF) >>> 6) & 0xFF);
            out[15 + outPos] = (byte)(((in[13 + inPos] & 0x1FF) << 2 | (in[14 + inPos] & 0x1FF) >>> 7) & 0xFF);
            out[16 + outPos] = (byte)(((in[14 + inPos] & 0x1FF) << 1 | (in[15 + inPos] & 0x1FF) >>> 8) & 0xFF);
            out[17 + outPos] = (byte)(in[15 + inPos] & 0x1FF & 0xFF);
            out[18 + outPos] = (byte)((in[16 + inPos] & 0x1FF) >>> 1 & 0xFF);
            out[19 + outPos] = (byte)(((in[16 + inPos] & 0x1FF) << 7 | (in[17 + inPos] & 0x1FF) >>> 2) & 0xFF);
            out[20 + outPos] = (byte)(((in[17 + inPos] & 0x1FF) << 6 | (in[18 + inPos] & 0x1FF) >>> 3) & 0xFF);
            out[21 + outPos] = (byte)(((in[18 + inPos] & 0x1FF) << 5 | (in[19 + inPos] & 0x1FF) >>> 4) & 0xFF);
            out[22 + outPos] = (byte)(((in[19 + inPos] & 0x1FF) << 4 | (in[20 + inPos] & 0x1FF) >>> 5) & 0xFF);
            out[23 + outPos] = (byte)(((in[20 + inPos] & 0x1FF) << 3 | (in[21 + inPos] & 0x1FF) >>> 6) & 0xFF);
            out[24 + outPos] = (byte)(((in[21 + inPos] & 0x1FF) << 2 | (in[22 + inPos] & 0x1FF) >>> 7) & 0xFF);
            out[25 + outPos] = (byte)(((in[22 + inPos] & 0x1FF) << 1 | (in[23 + inPos] & 0x1FF) >>> 8) & 0xFF);
            out[26 + outPos] = (byte)(in[23 + inPos] & 0x1FF & 0xFF);
            out[27 + outPos] = (byte)((in[24 + inPos] & 0x1FF) >>> 1 & 0xFF);
            out[28 + outPos] = (byte)(((in[24 + inPos] & 0x1FF) << 7 | (in[25 + inPos] & 0x1FF) >>> 2) & 0xFF);
            out[29 + outPos] = (byte)(((in[25 + inPos] & 0x1FF) << 6 | (in[26 + inPos] & 0x1FF) >>> 3) & 0xFF);
            out[30 + outPos] = (byte)(((in[26 + inPos] & 0x1FF) << 5 | (in[27 + inPos] & 0x1FF) >>> 4) & 0xFF);
            out[31 + outPos] = (byte)(((in[27 + inPos] & 0x1FF) << 4 | (in[28 + inPos] & 0x1FF) >>> 5) & 0xFF);
            out[32 + outPos] = (byte)(((in[28 + inPos] & 0x1FF) << 3 | (in[29 + inPos] & 0x1FF) >>> 6) & 0xFF);
            out[33 + outPos] = (byte)(((in[29 + inPos] & 0x1FF) << 2 | (in[30 + inPos] & 0x1FF) >>> 7) & 0xFF);
            out[34 + outPos] = (byte)(((in[30 + inPos] & 0x1FF) << 1 | (in[31 + inPos] & 0x1FF) >>> 8) & 0xFF);
            out[35 + outPos] = (byte)(in[31 + inPos] & 0x1FF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 1 & 0x1FF) | ((in[1 + inPos] & 0xFF) >>> 7 & 0x1FF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 2 & 0x1FF) | ((in[2 + inPos] & 0xFF) >>> 6 & 0x1FF));
            out[2 + outPos] = (((in[2 + inPos] & 0xFF) << 3 & 0x1FF) | ((in[3 + inPos] & 0xFF) >>> 5 & 0x1FF));
            out[3 + outPos] = (((in[3 + inPos] & 0xFF) << 4 & 0x1FF) | ((in[4 + inPos] & 0xFF) >>> 4 & 0x1FF));
            out[4 + outPos] = (((in[4 + inPos] & 0xFF) << 5 & 0x1FF) | ((in[5 + inPos] & 0xFF) >>> 3 & 0x1FF));
            out[5 + outPos] = (((in[5 + inPos] & 0xFF) << 6 & 0x1FF) | ((in[6 + inPos] & 0xFF) >>> 2 & 0x1FF));
            out[6 + outPos] = (((in[6 + inPos] & 0xFF) << 7 & 0x1FF) | ((in[7 + inPos] & 0xFF) >>> 1 & 0x1FF));
            out[7 + outPos] = (((in[7 + inPos] & 0xFF) << 8 & 0x1FF) | (in[8 + inPos] & 0xFF & 0x1FF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 1 & 0x1FF) | ((in[1 + inPos] & 0xFF) >>> 7 & 0x1FF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 2 & 0x1FF) | ((in[2 + inPos] & 0xFF) >>> 6 & 0x1FF));
            out[2 + outPos] = (((in[2 + inPos] & 0xFF) << 3 & 0x1FF) | ((in[3 + inPos] & 0xFF) >>> 5 & 0x1FF));
            out[3 + outPos] = (((in[3 + inPos] & 0xFF) << 4 & 0x1FF) | ((in[4 + inPos] & 0xFF) >>> 4 & 0x1FF));
            out[4 + outPos] = (((in[4 + inPos] & 0xFF) << 5 & 0x1FF) | ((in[5 + inPos] & 0xFF) >>> 3 & 0x1FF));
            out[5 + outPos] = (((in[5 + inPos] & 0xFF) << 6 & 0x1FF) | ((in[6 + inPos] & 0xFF) >>> 2 & 0x1FF));
            out[6 + outPos] = (((in[6 + inPos] & 0xFF) << 7 & 0x1FF) | ((in[7 + inPos] & 0xFF) >>> 1 & 0x1FF));
            out[7 + outPos] = (((in[7 + inPos] & 0xFF) << 8 & 0x1FF) | (in[8 + inPos] & 0xFF & 0x1FF));
            out[8 + outPos] = (((in[9 + inPos] & 0xFF) << 1 & 0x1FF) | ((in[10 + inPos] & 0xFF) >>> 7 & 0x1FF));
            out[9 + outPos] = (((in[10 + inPos] & 0xFF) << 2 & 0x1FF) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x1FF));
            out[10 + outPos] = (((in[11 + inPos] & 0xFF) << 3 & 0x1FF) | ((in[12 + inPos] & 0xFF) >>> 5 & 0x1FF));
            out[11 + outPos] = (((in[12 + inPos] & 0xFF) << 4 & 0x1FF) | ((in[13 + inPos] & 0xFF) >>> 4 & 0x1FF));
            out[12 + outPos] = (((in[13 + inPos] & 0xFF) << 5 & 0x1FF) | ((in[14 + inPos] & 0xFF) >>> 3 & 0x1FF));
            out[13 + outPos] = (((in[14 + inPos] & 0xFF) << 6 & 0x1FF) | ((in[15 + inPos] & 0xFF) >>> 2 & 0x1FF));
            out[14 + outPos] = (((in[15 + inPos] & 0xFF) << 7 & 0x1FF) | ((in[16 + inPos] & 0xFF) >>> 1 & 0x1FF));
            out[15 + outPos] = (((in[16 + inPos] & 0xFF) << 8 & 0x1FF) | (in[17 + inPos] & 0xFF & 0x1FF));
            out[16 + outPos] = (((in[18 + inPos] & 0xFF) << 1 & 0x1FF) | ((in[19 + inPos] & 0xFF) >>> 7 & 0x1FF));
            out[17 + outPos] = (((in[19 + inPos] & 0xFF) << 2 & 0x1FF) | ((in[20 + inPos] & 0xFF) >>> 6 & 0x1FF));
            out[18 + outPos] = (((in[20 + inPos] & 0xFF) << 3 & 0x1FF) | ((in[21 + inPos] & 0xFF) >>> 5 & 0x1FF));
            out[19 + outPos] = (((in[21 + inPos] & 0xFF) << 4 & 0x1FF) | ((in[22 + inPos] & 0xFF) >>> 4 & 0x1FF));
            out[20 + outPos] = (((in[22 + inPos] & 0xFF) << 5 & 0x1FF) | ((in[23 + inPos] & 0xFF) >>> 3 & 0x1FF));
            out[21 + outPos] = (((in[23 + inPos] & 0xFF) << 6 & 0x1FF) | ((in[24 + inPos] & 0xFF) >>> 2 & 0x1FF));
            out[22 + outPos] = (((in[24 + inPos] & 0xFF) << 7 & 0x1FF) | ((in[25 + inPos] & 0xFF) >>> 1 & 0x1FF));
            out[23 + outPos] = (((in[25 + inPos] & 0xFF) << 8 & 0x1FF) | (in[26 + inPos] & 0xFF & 0x1FF));
            out[24 + outPos] = (((in[27 + inPos] & 0xFF) << 1 & 0x1FF) | ((in[28 + inPos] & 0xFF) >>> 7 & 0x1FF));
            out[25 + outPos] = (((in[28 + inPos] & 0xFF) << 2 & 0x1FF) | ((in[29 + inPos] & 0xFF) >>> 6 & 0x1FF));
            out[26 + outPos] = (((in[29 + inPos] & 0xFF) << 3 & 0x1FF) | ((in[30 + inPos] & 0xFF) >>> 5 & 0x1FF));
            out[27 + outPos] = (((in[30 + inPos] & 0xFF) << 4 & 0x1FF) | ((in[31 + inPos] & 0xFF) >>> 4 & 0x1FF));
            out[28 + outPos] = (((in[31 + inPos] & 0xFF) << 5 & 0x1FF) | ((in[32 + inPos] & 0xFF) >>> 3 & 0x1FF));
            out[29 + outPos] = (((in[32 + inPos] & 0xFF) << 6 & 0x1FF) | ((in[33 + inPos] & 0xFF) >>> 2 & 0x1FF));
            out[30 + outPos] = (((in[33 + inPos] & 0xFF) << 7 & 0x1FF) | ((in[34 + inPos] & 0xFF) >>> 1 & 0x1FF));
            out[31 + outPos] = (((in[34 + inPos] & 0xFF) << 8 & 0x1FF) | (in[35 + inPos] & 0xFF & 0x1FF));
        }
    }
    
    private static final class Packer10 extends BytePacker
    {
        private Packer10() {
            super(10);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x3FF) << 6 | (in[1 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[2 + outPos] = (byte)(((in[1 + inPos] & 0x3FF) << 4 | (in[2 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[3 + outPos] = (byte)(((in[2 + inPos] & 0x3FF) << 2 | (in[3 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[4 + outPos] = (byte)(in[3 + inPos] & 0x3FF & 0xFF);
            out[5 + outPos] = (byte)((in[4 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[6 + outPos] = (byte)(((in[4 + inPos] & 0x3FF) << 6 | (in[5 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[7 + outPos] = (byte)(((in[5 + inPos] & 0x3FF) << 4 | (in[6 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[8 + outPos] = (byte)(((in[6 + inPos] & 0x3FF) << 2 | (in[7 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[9 + outPos] = (byte)(in[7 + inPos] & 0x3FF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x3FF) << 6 | (in[1 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[2 + outPos] = (byte)(((in[1 + inPos] & 0x3FF) << 4 | (in[2 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[3 + outPos] = (byte)(((in[2 + inPos] & 0x3FF) << 2 | (in[3 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[4 + outPos] = (byte)(in[3 + inPos] & 0x3FF & 0xFF);
            out[5 + outPos] = (byte)((in[4 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[6 + outPos] = (byte)(((in[4 + inPos] & 0x3FF) << 6 | (in[5 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[7 + outPos] = (byte)(((in[5 + inPos] & 0x3FF) << 4 | (in[6 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[8 + outPos] = (byte)(((in[6 + inPos] & 0x3FF) << 2 | (in[7 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[9 + outPos] = (byte)(in[7 + inPos] & 0x3FF & 0xFF);
            out[10 + outPos] = (byte)((in[8 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[11 + outPos] = (byte)(((in[8 + inPos] & 0x3FF) << 6 | (in[9 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[12 + outPos] = (byte)(((in[9 + inPos] & 0x3FF) << 4 | (in[10 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[13 + outPos] = (byte)(((in[10 + inPos] & 0x3FF) << 2 | (in[11 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[14 + outPos] = (byte)(in[11 + inPos] & 0x3FF & 0xFF);
            out[15 + outPos] = (byte)((in[12 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[16 + outPos] = (byte)(((in[12 + inPos] & 0x3FF) << 6 | (in[13 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[17 + outPos] = (byte)(((in[13 + inPos] & 0x3FF) << 4 | (in[14 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[18 + outPos] = (byte)(((in[14 + inPos] & 0x3FF) << 2 | (in[15 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[19 + outPos] = (byte)(in[15 + inPos] & 0x3FF & 0xFF);
            out[20 + outPos] = (byte)((in[16 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[21 + outPos] = (byte)(((in[16 + inPos] & 0x3FF) << 6 | (in[17 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[22 + outPos] = (byte)(((in[17 + inPos] & 0x3FF) << 4 | (in[18 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[23 + outPos] = (byte)(((in[18 + inPos] & 0x3FF) << 2 | (in[19 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[24 + outPos] = (byte)(in[19 + inPos] & 0x3FF & 0xFF);
            out[25 + outPos] = (byte)((in[20 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[26 + outPos] = (byte)(((in[20 + inPos] & 0x3FF) << 6 | (in[21 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[27 + outPos] = (byte)(((in[21 + inPos] & 0x3FF) << 4 | (in[22 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[28 + outPos] = (byte)(((in[22 + inPos] & 0x3FF) << 2 | (in[23 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[29 + outPos] = (byte)(in[23 + inPos] & 0x3FF & 0xFF);
            out[30 + outPos] = (byte)((in[24 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[31 + outPos] = (byte)(((in[24 + inPos] & 0x3FF) << 6 | (in[25 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[32 + outPos] = (byte)(((in[25 + inPos] & 0x3FF) << 4 | (in[26 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[33 + outPos] = (byte)(((in[26 + inPos] & 0x3FF) << 2 | (in[27 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[34 + outPos] = (byte)(in[27 + inPos] & 0x3FF & 0xFF);
            out[35 + outPos] = (byte)((in[28 + inPos] & 0x3FF) >>> 2 & 0xFF);
            out[36 + outPos] = (byte)(((in[28 + inPos] & 0x3FF) << 6 | (in[29 + inPos] & 0x3FF) >>> 4) & 0xFF);
            out[37 + outPos] = (byte)(((in[29 + inPos] & 0x3FF) << 4 | (in[30 + inPos] & 0x3FF) >>> 6) & 0xFF);
            out[38 + outPos] = (byte)(((in[30 + inPos] & 0x3FF) << 2 | (in[31 + inPos] & 0x3FF) >>> 8) & 0xFF);
            out[39 + outPos] = (byte)(in[31 + inPos] & 0x3FF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[1 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[2 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[2 + outPos] = (((in[2 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[3 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[3 + outPos] = (((in[3 + inPos] & 0xFF) << 8 & 0x3FF) | (in[4 + inPos] & 0xFF & 0x3FF));
            out[4 + outPos] = (((in[5 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[6 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[5 + outPos] = (((in[6 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[6 + outPos] = (((in[7 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[8 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[7 + outPos] = (((in[8 + inPos] & 0xFF) << 8 & 0x3FF) | (in[9 + inPos] & 0xFF & 0x3FF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[1 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[2 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[2 + outPos] = (((in[2 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[3 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[3 + outPos] = (((in[3 + inPos] & 0xFF) << 8 & 0x3FF) | (in[4 + inPos] & 0xFF & 0x3FF));
            out[4 + outPos] = (((in[5 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[6 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[5 + outPos] = (((in[6 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[6 + outPos] = (((in[7 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[8 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[7 + outPos] = (((in[8 + inPos] & 0xFF) << 8 & 0x3FF) | (in[9 + inPos] & 0xFF & 0x3FF));
            out[8 + outPos] = (((in[10 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[9 + outPos] = (((in[11 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[12 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[10 + outPos] = (((in[12 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[13 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[11 + outPos] = (((in[13 + inPos] & 0xFF) << 8 & 0x3FF) | (in[14 + inPos] & 0xFF & 0x3FF));
            out[12 + outPos] = (((in[15 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[16 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[13 + outPos] = (((in[16 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[17 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[14 + outPos] = (((in[17 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[18 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[15 + outPos] = (((in[18 + inPos] & 0xFF) << 8 & 0x3FF) | (in[19 + inPos] & 0xFF & 0x3FF));
            out[16 + outPos] = (((in[20 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[21 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[17 + outPos] = (((in[21 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[22 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[18 + outPos] = (((in[22 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[23 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[19 + outPos] = (((in[23 + inPos] & 0xFF) << 8 & 0x3FF) | (in[24 + inPos] & 0xFF & 0x3FF));
            out[20 + outPos] = (((in[25 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[26 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[21 + outPos] = (((in[26 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[27 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[22 + outPos] = (((in[27 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[28 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[23 + outPos] = (((in[28 + inPos] & 0xFF) << 8 & 0x3FF) | (in[29 + inPos] & 0xFF & 0x3FF));
            out[24 + outPos] = (((in[30 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[31 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[25 + outPos] = (((in[31 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[32 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[26 + outPos] = (((in[32 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[33 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[27 + outPos] = (((in[33 + inPos] & 0xFF) << 8 & 0x3FF) | (in[34 + inPos] & 0xFF & 0x3FF));
            out[28 + outPos] = (((in[35 + inPos] & 0xFF) << 2 & 0x3FF) | ((in[36 + inPos] & 0xFF) >>> 6 & 0x3FF));
            out[29 + outPos] = (((in[36 + inPos] & 0xFF) << 4 & 0x3FF) | ((in[37 + inPos] & 0xFF) >>> 4 & 0x3FF));
            out[30 + outPos] = (((in[37 + inPos] & 0xFF) << 6 & 0x3FF) | ((in[38 + inPos] & 0xFF) >>> 2 & 0x3FF));
            out[31 + outPos] = (((in[38 + inPos] & 0xFF) << 8 & 0x3FF) | (in[39 + inPos] & 0xFF & 0x3FF));
        }
    }
    
    private static final class Packer11 extends BytePacker
    {
        private Packer11() {
            super(11);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FF) >>> 3 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x7FF) << 5 | (in[1 + inPos] & 0x7FF) >>> 6) & 0xFF);
            out[2 + outPos] = (byte)(((in[1 + inPos] & 0x7FF) << 2 | (in[2 + inPos] & 0x7FF) >>> 9) & 0xFF);
            out[3 + outPos] = (byte)((in[2 + inPos] & 0x7FF) >>> 1 & 0xFF);
            out[4 + outPos] = (byte)(((in[2 + inPos] & 0x7FF) << 7 | (in[3 + inPos] & 0x7FF) >>> 4) & 0xFF);
            out[5 + outPos] = (byte)(((in[3 + inPos] & 0x7FF) << 4 | (in[4 + inPos] & 0x7FF) >>> 7) & 0xFF);
            out[6 + outPos] = (byte)(((in[4 + inPos] & 0x7FF) << 1 | (in[5 + inPos] & 0x7FF) >>> 10) & 0xFF);
            out[7 + outPos] = (byte)((in[5 + inPos] & 0x7FF) >>> 2 & 0xFF);
            out[8 + outPos] = (byte)(((in[5 + inPos] & 0x7FF) << 6 | (in[6 + inPos] & 0x7FF) >>> 5) & 0xFF);
            out[9 + outPos] = (byte)(((in[6 + inPos] & 0x7FF) << 3 | (in[7 + inPos] & 0x7FF) >>> 8) & 0xFF);
            out[10 + outPos] = (byte)(in[7 + inPos] & 0x7FF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FF) >>> 3 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x7FF) << 5 | (in[1 + inPos] & 0x7FF) >>> 6) & 0xFF);
            out[2 + outPos] = (byte)(((in[1 + inPos] & 0x7FF) << 2 | (in[2 + inPos] & 0x7FF) >>> 9) & 0xFF);
            out[3 + outPos] = (byte)((in[2 + inPos] & 0x7FF) >>> 1 & 0xFF);
            out[4 + outPos] = (byte)(((in[2 + inPos] & 0x7FF) << 7 | (in[3 + inPos] & 0x7FF) >>> 4) & 0xFF);
            out[5 + outPos] = (byte)(((in[3 + inPos] & 0x7FF) << 4 | (in[4 + inPos] & 0x7FF) >>> 7) & 0xFF);
            out[6 + outPos] = (byte)(((in[4 + inPos] & 0x7FF) << 1 | (in[5 + inPos] & 0x7FF) >>> 10) & 0xFF);
            out[7 + outPos] = (byte)((in[5 + inPos] & 0x7FF) >>> 2 & 0xFF);
            out[8 + outPos] = (byte)(((in[5 + inPos] & 0x7FF) << 6 | (in[6 + inPos] & 0x7FF) >>> 5) & 0xFF);
            out[9 + outPos] = (byte)(((in[6 + inPos] & 0x7FF) << 3 | (in[7 + inPos] & 0x7FF) >>> 8) & 0xFF);
            out[10 + outPos] = (byte)(in[7 + inPos] & 0x7FF & 0xFF);
            out[11 + outPos] = (byte)((in[8 + inPos] & 0x7FF) >>> 3 & 0xFF);
            out[12 + outPos] = (byte)(((in[8 + inPos] & 0x7FF) << 5 | (in[9 + inPos] & 0x7FF) >>> 6) & 0xFF);
            out[13 + outPos] = (byte)(((in[9 + inPos] & 0x7FF) << 2 | (in[10 + inPos] & 0x7FF) >>> 9) & 0xFF);
            out[14 + outPos] = (byte)((in[10 + inPos] & 0x7FF) >>> 1 & 0xFF);
            out[15 + outPos] = (byte)(((in[10 + inPos] & 0x7FF) << 7 | (in[11 + inPos] & 0x7FF) >>> 4) & 0xFF);
            out[16 + outPos] = (byte)(((in[11 + inPos] & 0x7FF) << 4 | (in[12 + inPos] & 0x7FF) >>> 7) & 0xFF);
            out[17 + outPos] = (byte)(((in[12 + inPos] & 0x7FF) << 1 | (in[13 + inPos] & 0x7FF) >>> 10) & 0xFF);
            out[18 + outPos] = (byte)((in[13 + inPos] & 0x7FF) >>> 2 & 0xFF);
            out[19 + outPos] = (byte)(((in[13 + inPos] & 0x7FF) << 6 | (in[14 + inPos] & 0x7FF) >>> 5) & 0xFF);
            out[20 + outPos] = (byte)(((in[14 + inPos] & 0x7FF) << 3 | (in[15 + inPos] & 0x7FF) >>> 8) & 0xFF);
            out[21 + outPos] = (byte)(in[15 + inPos] & 0x7FF & 0xFF);
            out[22 + outPos] = (byte)((in[16 + inPos] & 0x7FF) >>> 3 & 0xFF);
            out[23 + outPos] = (byte)(((in[16 + inPos] & 0x7FF) << 5 | (in[17 + inPos] & 0x7FF) >>> 6) & 0xFF);
            out[24 + outPos] = (byte)(((in[17 + inPos] & 0x7FF) << 2 | (in[18 + inPos] & 0x7FF) >>> 9) & 0xFF);
            out[25 + outPos] = (byte)((in[18 + inPos] & 0x7FF) >>> 1 & 0xFF);
            out[26 + outPos] = (byte)(((in[18 + inPos] & 0x7FF) << 7 | (in[19 + inPos] & 0x7FF) >>> 4) & 0xFF);
            out[27 + outPos] = (byte)(((in[19 + inPos] & 0x7FF) << 4 | (in[20 + inPos] & 0x7FF) >>> 7) & 0xFF);
            out[28 + outPos] = (byte)(((in[20 + inPos] & 0x7FF) << 1 | (in[21 + inPos] & 0x7FF) >>> 10) & 0xFF);
            out[29 + outPos] = (byte)((in[21 + inPos] & 0x7FF) >>> 2 & 0xFF);
            out[30 + outPos] = (byte)(((in[21 + inPos] & 0x7FF) << 6 | (in[22 + inPos] & 0x7FF) >>> 5) & 0xFF);
            out[31 + outPos] = (byte)(((in[22 + inPos] & 0x7FF) << 3 | (in[23 + inPos] & 0x7FF) >>> 8) & 0xFF);
            out[32 + outPos] = (byte)(in[23 + inPos] & 0x7FF & 0xFF);
            out[33 + outPos] = (byte)((in[24 + inPos] & 0x7FF) >>> 3 & 0xFF);
            out[34 + outPos] = (byte)(((in[24 + inPos] & 0x7FF) << 5 | (in[25 + inPos] & 0x7FF) >>> 6) & 0xFF);
            out[35 + outPos] = (byte)(((in[25 + inPos] & 0x7FF) << 2 | (in[26 + inPos] & 0x7FF) >>> 9) & 0xFF);
            out[36 + outPos] = (byte)((in[26 + inPos] & 0x7FF) >>> 1 & 0xFF);
            out[37 + outPos] = (byte)(((in[26 + inPos] & 0x7FF) << 7 | (in[27 + inPos] & 0x7FF) >>> 4) & 0xFF);
            out[38 + outPos] = (byte)(((in[27 + inPos] & 0x7FF) << 4 | (in[28 + inPos] & 0x7FF) >>> 7) & 0xFF);
            out[39 + outPos] = (byte)(((in[28 + inPos] & 0x7FF) << 1 | (in[29 + inPos] & 0x7FF) >>> 10) & 0xFF);
            out[40 + outPos] = (byte)((in[29 + inPos] & 0x7FF) >>> 2 & 0xFF);
            out[41 + outPos] = (byte)(((in[29 + inPos] & 0x7FF) << 6 | (in[30 + inPos] & 0x7FF) >>> 5) & 0xFF);
            out[42 + outPos] = (byte)(((in[30 + inPos] & 0x7FF) << 3 | (in[31 + inPos] & 0x7FF) >>> 8) & 0xFF);
            out[43 + outPos] = (byte)(in[31 + inPos] & 0x7FF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 3 & 0x7FF) | ((in[1 + inPos] & 0xFF) >>> 5 & 0x7FF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 6 & 0x7FF) | ((in[2 + inPos] & 0xFF) >>> 2 & 0x7FF));
            out[2 + outPos] = (((in[2 + inPos] & 0xFF) << 9 & 0x7FF) | ((in[3 + inPos] & 0xFF) << 1 & 0x7FF) | ((in[4 + inPos] & 0xFF) >>> 7 & 0x7FF));
            out[3 + outPos] = (((in[4 + inPos] & 0xFF) << 4 & 0x7FF) | ((in[5 + inPos] & 0xFF) >>> 4 & 0x7FF));
            out[4 + outPos] = (((in[5 + inPos] & 0xFF) << 7 & 0x7FF) | ((in[6 + inPos] & 0xFF) >>> 1 & 0x7FF));
            out[5 + outPos] = (((in[6 + inPos] & 0xFF) << 10 & 0x7FF) | ((in[7 + inPos] & 0xFF) << 2 & 0x7FF) | ((in[8 + inPos] & 0xFF) >>> 6 & 0x7FF));
            out[6 + outPos] = (((in[8 + inPos] & 0xFF) << 5 & 0x7FF) | ((in[9 + inPos] & 0xFF) >>> 3 & 0x7FF));
            out[7 + outPos] = (((in[9 + inPos] & 0xFF) << 8 & 0x7FF) | (in[10 + inPos] & 0xFF & 0x7FF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 3 & 0x7FF) | ((in[1 + inPos] & 0xFF) >>> 5 & 0x7FF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 6 & 0x7FF) | ((in[2 + inPos] & 0xFF) >>> 2 & 0x7FF));
            out[2 + outPos] = (((in[2 + inPos] & 0xFF) << 9 & 0x7FF) | ((in[3 + inPos] & 0xFF) << 1 & 0x7FF) | ((in[4 + inPos] & 0xFF) >>> 7 & 0x7FF));
            out[3 + outPos] = (((in[4 + inPos] & 0xFF) << 4 & 0x7FF) | ((in[5 + inPos] & 0xFF) >>> 4 & 0x7FF));
            out[4 + outPos] = (((in[5 + inPos] & 0xFF) << 7 & 0x7FF) | ((in[6 + inPos] & 0xFF) >>> 1 & 0x7FF));
            out[5 + outPos] = (((in[6 + inPos] & 0xFF) << 10 & 0x7FF) | ((in[7 + inPos] & 0xFF) << 2 & 0x7FF) | ((in[8 + inPos] & 0xFF) >>> 6 & 0x7FF));
            out[6 + outPos] = (((in[8 + inPos] & 0xFF) << 5 & 0x7FF) | ((in[9 + inPos] & 0xFF) >>> 3 & 0x7FF));
            out[7 + outPos] = (((in[9 + inPos] & 0xFF) << 8 & 0x7FF) | (in[10 + inPos] & 0xFF & 0x7FF));
            out[8 + outPos] = (((in[11 + inPos] & 0xFF) << 3 & 0x7FF) | ((in[12 + inPos] & 0xFF) >>> 5 & 0x7FF));
            out[9 + outPos] = (((in[12 + inPos] & 0xFF) << 6 & 0x7FF) | ((in[13 + inPos] & 0xFF) >>> 2 & 0x7FF));
            out[10 + outPos] = (((in[13 + inPos] & 0xFF) << 9 & 0x7FF) | ((in[14 + inPos] & 0xFF) << 1 & 0x7FF) | ((in[15 + inPos] & 0xFF) >>> 7 & 0x7FF));
            out[11 + outPos] = (((in[15 + inPos] & 0xFF) << 4 & 0x7FF) | ((in[16 + inPos] & 0xFF) >>> 4 & 0x7FF));
            out[12 + outPos] = (((in[16 + inPos] & 0xFF) << 7 & 0x7FF) | ((in[17 + inPos] & 0xFF) >>> 1 & 0x7FF));
            out[13 + outPos] = (((in[17 + inPos] & 0xFF) << 10 & 0x7FF) | ((in[18 + inPos] & 0xFF) << 2 & 0x7FF) | ((in[19 + inPos] & 0xFF) >>> 6 & 0x7FF));
            out[14 + outPos] = (((in[19 + inPos] & 0xFF) << 5 & 0x7FF) | ((in[20 + inPos] & 0xFF) >>> 3 & 0x7FF));
            out[15 + outPos] = (((in[20 + inPos] & 0xFF) << 8 & 0x7FF) | (in[21 + inPos] & 0xFF & 0x7FF));
            out[16 + outPos] = (((in[22 + inPos] & 0xFF) << 3 & 0x7FF) | ((in[23 + inPos] & 0xFF) >>> 5 & 0x7FF));
            out[17 + outPos] = (((in[23 + inPos] & 0xFF) << 6 & 0x7FF) | ((in[24 + inPos] & 0xFF) >>> 2 & 0x7FF));
            out[18 + outPos] = (((in[24 + inPos] & 0xFF) << 9 & 0x7FF) | ((in[25 + inPos] & 0xFF) << 1 & 0x7FF) | ((in[26 + inPos] & 0xFF) >>> 7 & 0x7FF));
            out[19 + outPos] = (((in[26 + inPos] & 0xFF) << 4 & 0x7FF) | ((in[27 + inPos] & 0xFF) >>> 4 & 0x7FF));
            out[20 + outPos] = (((in[27 + inPos] & 0xFF) << 7 & 0x7FF) | ((in[28 + inPos] & 0xFF) >>> 1 & 0x7FF));
            out[21 + outPos] = (((in[28 + inPos] & 0xFF) << 10 & 0x7FF) | ((in[29 + inPos] & 0xFF) << 2 & 0x7FF) | ((in[30 + inPos] & 0xFF) >>> 6 & 0x7FF));
            out[22 + outPos] = (((in[30 + inPos] & 0xFF) << 5 & 0x7FF) | ((in[31 + inPos] & 0xFF) >>> 3 & 0x7FF));
            out[23 + outPos] = (((in[31 + inPos] & 0xFF) << 8 & 0x7FF) | (in[32 + inPos] & 0xFF & 0x7FF));
            out[24 + outPos] = (((in[33 + inPos] & 0xFF) << 3 & 0x7FF) | ((in[34 + inPos] & 0xFF) >>> 5 & 0x7FF));
            out[25 + outPos] = (((in[34 + inPos] & 0xFF) << 6 & 0x7FF) | ((in[35 + inPos] & 0xFF) >>> 2 & 0x7FF));
            out[26 + outPos] = (((in[35 + inPos] & 0xFF) << 9 & 0x7FF) | ((in[36 + inPos] & 0xFF) << 1 & 0x7FF) | ((in[37 + inPos] & 0xFF) >>> 7 & 0x7FF));
            out[27 + outPos] = (((in[37 + inPos] & 0xFF) << 4 & 0x7FF) | ((in[38 + inPos] & 0xFF) >>> 4 & 0x7FF));
            out[28 + outPos] = (((in[38 + inPos] & 0xFF) << 7 & 0x7FF) | ((in[39 + inPos] & 0xFF) >>> 1 & 0x7FF));
            out[29 + outPos] = (((in[39 + inPos] & 0xFF) << 10 & 0x7FF) | ((in[40 + inPos] & 0xFF) << 2 & 0x7FF) | ((in[41 + inPos] & 0xFF) >>> 6 & 0x7FF));
            out[30 + outPos] = (((in[41 + inPos] & 0xFF) << 5 & 0x7FF) | ((in[42 + inPos] & 0xFF) >>> 3 & 0x7FF));
            out[31 + outPos] = (((in[42 + inPos] & 0xFF) << 8 & 0x7FF) | (in[43 + inPos] & 0xFF & 0x7FF));
        }
    }
    
    private static final class Packer12 extends BytePacker
    {
        private Packer12() {
            super(12);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0xFFF) << 4 | (in[1 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[2 + outPos] = (byte)(in[1 + inPos] & 0xFFF & 0xFF);
            out[3 + outPos] = (byte)((in[2 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[4 + outPos] = (byte)(((in[2 + inPos] & 0xFFF) << 4 | (in[3 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[5 + outPos] = (byte)(in[3 + inPos] & 0xFFF & 0xFF);
            out[6 + outPos] = (byte)((in[4 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[7 + outPos] = (byte)(((in[4 + inPos] & 0xFFF) << 4 | (in[5 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[8 + outPos] = (byte)(in[5 + inPos] & 0xFFF & 0xFF);
            out[9 + outPos] = (byte)((in[6 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[10 + outPos] = (byte)(((in[6 + inPos] & 0xFFF) << 4 | (in[7 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[11 + outPos] = (byte)(in[7 + inPos] & 0xFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0xFFF) << 4 | (in[1 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[2 + outPos] = (byte)(in[1 + inPos] & 0xFFF & 0xFF);
            out[3 + outPos] = (byte)((in[2 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[4 + outPos] = (byte)(((in[2 + inPos] & 0xFFF) << 4 | (in[3 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[5 + outPos] = (byte)(in[3 + inPos] & 0xFFF & 0xFF);
            out[6 + outPos] = (byte)((in[4 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[7 + outPos] = (byte)(((in[4 + inPos] & 0xFFF) << 4 | (in[5 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[8 + outPos] = (byte)(in[5 + inPos] & 0xFFF & 0xFF);
            out[9 + outPos] = (byte)((in[6 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[10 + outPos] = (byte)(((in[6 + inPos] & 0xFFF) << 4 | (in[7 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[11 + outPos] = (byte)(in[7 + inPos] & 0xFFF & 0xFF);
            out[12 + outPos] = (byte)((in[8 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[13 + outPos] = (byte)(((in[8 + inPos] & 0xFFF) << 4 | (in[9 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[14 + outPos] = (byte)(in[9 + inPos] & 0xFFF & 0xFF);
            out[15 + outPos] = (byte)((in[10 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[16 + outPos] = (byte)(((in[10 + inPos] & 0xFFF) << 4 | (in[11 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[17 + outPos] = (byte)(in[11 + inPos] & 0xFFF & 0xFF);
            out[18 + outPos] = (byte)((in[12 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[19 + outPos] = (byte)(((in[12 + inPos] & 0xFFF) << 4 | (in[13 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[20 + outPos] = (byte)(in[13 + inPos] & 0xFFF & 0xFF);
            out[21 + outPos] = (byte)((in[14 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[22 + outPos] = (byte)(((in[14 + inPos] & 0xFFF) << 4 | (in[15 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[23 + outPos] = (byte)(in[15 + inPos] & 0xFFF & 0xFF);
            out[24 + outPos] = (byte)((in[16 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[25 + outPos] = (byte)(((in[16 + inPos] & 0xFFF) << 4 | (in[17 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[26 + outPos] = (byte)(in[17 + inPos] & 0xFFF & 0xFF);
            out[27 + outPos] = (byte)((in[18 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[28 + outPos] = (byte)(((in[18 + inPos] & 0xFFF) << 4 | (in[19 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[29 + outPos] = (byte)(in[19 + inPos] & 0xFFF & 0xFF);
            out[30 + outPos] = (byte)((in[20 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[31 + outPos] = (byte)(((in[20 + inPos] & 0xFFF) << 4 | (in[21 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[32 + outPos] = (byte)(in[21 + inPos] & 0xFFF & 0xFF);
            out[33 + outPos] = (byte)((in[22 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[34 + outPos] = (byte)(((in[22 + inPos] & 0xFFF) << 4 | (in[23 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[35 + outPos] = (byte)(in[23 + inPos] & 0xFFF & 0xFF);
            out[36 + outPos] = (byte)((in[24 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[37 + outPos] = (byte)(((in[24 + inPos] & 0xFFF) << 4 | (in[25 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[38 + outPos] = (byte)(in[25 + inPos] & 0xFFF & 0xFF);
            out[39 + outPos] = (byte)((in[26 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[40 + outPos] = (byte)(((in[26 + inPos] & 0xFFF) << 4 | (in[27 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[41 + outPos] = (byte)(in[27 + inPos] & 0xFFF & 0xFF);
            out[42 + outPos] = (byte)((in[28 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[43 + outPos] = (byte)(((in[28 + inPos] & 0xFFF) << 4 | (in[29 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[44 + outPos] = (byte)(in[29 + inPos] & 0xFFF & 0xFF);
            out[45 + outPos] = (byte)((in[30 + inPos] & 0xFFF) >>> 4 & 0xFF);
            out[46 + outPos] = (byte)(((in[30 + inPos] & 0xFFF) << 4 | (in[31 + inPos] & 0xFFF) >>> 8) & 0xFF);
            out[47 + outPos] = (byte)(in[31 + inPos] & 0xFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[1 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 8 & 0xFFF) | (in[2 + inPos] & 0xFF & 0xFFF));
            out[2 + outPos] = (((in[3 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[4 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[3 + outPos] = (((in[4 + inPos] & 0xFF) << 8 & 0xFFF) | (in[5 + inPos] & 0xFF & 0xFFF));
            out[4 + outPos] = (((in[6 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[5 + outPos] = (((in[7 + inPos] & 0xFF) << 8 & 0xFFF) | (in[8 + inPos] & 0xFF & 0xFFF));
            out[6 + outPos] = (((in[9 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[10 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[7 + outPos] = (((in[10 + inPos] & 0xFF) << 8 & 0xFFF) | (in[11 + inPos] & 0xFF & 0xFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[1 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 8 & 0xFFF) | (in[2 + inPos] & 0xFF & 0xFFF));
            out[2 + outPos] = (((in[3 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[4 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[3 + outPos] = (((in[4 + inPos] & 0xFF) << 8 & 0xFFF) | (in[5 + inPos] & 0xFF & 0xFFF));
            out[4 + outPos] = (((in[6 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[5 + outPos] = (((in[7 + inPos] & 0xFF) << 8 & 0xFFF) | (in[8 + inPos] & 0xFF & 0xFFF));
            out[6 + outPos] = (((in[9 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[10 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[7 + outPos] = (((in[10 + inPos] & 0xFF) << 8 & 0xFFF) | (in[11 + inPos] & 0xFF & 0xFFF));
            out[8 + outPos] = (((in[12 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[13 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[9 + outPos] = (((in[13 + inPos] & 0xFF) << 8 & 0xFFF) | (in[14 + inPos] & 0xFF & 0xFFF));
            out[10 + outPos] = (((in[15 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[16 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[11 + outPos] = (((in[16 + inPos] & 0xFF) << 8 & 0xFFF) | (in[17 + inPos] & 0xFF & 0xFFF));
            out[12 + outPos] = (((in[18 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[19 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[13 + outPos] = (((in[19 + inPos] & 0xFF) << 8 & 0xFFF) | (in[20 + inPos] & 0xFF & 0xFFF));
            out[14 + outPos] = (((in[21 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[22 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[15 + outPos] = (((in[22 + inPos] & 0xFF) << 8 & 0xFFF) | (in[23 + inPos] & 0xFF & 0xFFF));
            out[16 + outPos] = (((in[24 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[25 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[17 + outPos] = (((in[25 + inPos] & 0xFF) << 8 & 0xFFF) | (in[26 + inPos] & 0xFF & 0xFFF));
            out[18 + outPos] = (((in[27 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[28 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[19 + outPos] = (((in[28 + inPos] & 0xFF) << 8 & 0xFFF) | (in[29 + inPos] & 0xFF & 0xFFF));
            out[20 + outPos] = (((in[30 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[31 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[21 + outPos] = (((in[31 + inPos] & 0xFF) << 8 & 0xFFF) | (in[32 + inPos] & 0xFF & 0xFFF));
            out[22 + outPos] = (((in[33 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[34 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[23 + outPos] = (((in[34 + inPos] & 0xFF) << 8 & 0xFFF) | (in[35 + inPos] & 0xFF & 0xFFF));
            out[24 + outPos] = (((in[36 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[37 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[25 + outPos] = (((in[37 + inPos] & 0xFF) << 8 & 0xFFF) | (in[38 + inPos] & 0xFF & 0xFFF));
            out[26 + outPos] = (((in[39 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[40 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[27 + outPos] = (((in[40 + inPos] & 0xFF) << 8 & 0xFFF) | (in[41 + inPos] & 0xFF & 0xFFF));
            out[28 + outPos] = (((in[42 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[43 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[29 + outPos] = (((in[43 + inPos] & 0xFF) << 8 & 0xFFF) | (in[44 + inPos] & 0xFF & 0xFFF));
            out[30 + outPos] = (((in[45 + inPos] & 0xFF) << 4 & 0xFFF) | ((in[46 + inPos] & 0xFF) >>> 4 & 0xFFF));
            out[31 + outPos] = (((in[46 + inPos] & 0xFF) << 8 & 0xFFF) | (in[47 + inPos] & 0xFF & 0xFFF));
        }
    }
    
    private static final class Packer13 extends BytePacker
    {
        private Packer13() {
            super(13);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFF) >>> 5 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x1FFF) << 3 | (in[1 + inPos] & 0x1FFF) >>> 10) & 0xFF);
            out[2 + outPos] = (byte)((in[1 + inPos] & 0x1FFF) >>> 2 & 0xFF);
            out[3 + outPos] = (byte)(((in[1 + inPos] & 0x1FFF) << 6 | (in[2 + inPos] & 0x1FFF) >>> 7) & 0xFF);
            out[4 + outPos] = (byte)(((in[2 + inPos] & 0x1FFF) << 1 | (in[3 + inPos] & 0x1FFF) >>> 12) & 0xFF);
            out[5 + outPos] = (byte)((in[3 + inPos] & 0x1FFF) >>> 4 & 0xFF);
            out[6 + outPos] = (byte)(((in[3 + inPos] & 0x1FFF) << 4 | (in[4 + inPos] & 0x1FFF) >>> 9) & 0xFF);
            out[7 + outPos] = (byte)((in[4 + inPos] & 0x1FFF) >>> 1 & 0xFF);
            out[8 + outPos] = (byte)(((in[4 + inPos] & 0x1FFF) << 7 | (in[5 + inPos] & 0x1FFF) >>> 6) & 0xFF);
            out[9 + outPos] = (byte)(((in[5 + inPos] & 0x1FFF) << 2 | (in[6 + inPos] & 0x1FFF) >>> 11) & 0xFF);
            out[10 + outPos] = (byte)((in[6 + inPos] & 0x1FFF) >>> 3 & 0xFF);
            out[11 + outPos] = (byte)(((in[6 + inPos] & 0x1FFF) << 5 | (in[7 + inPos] & 0x1FFF) >>> 8) & 0xFF);
            out[12 + outPos] = (byte)(in[7 + inPos] & 0x1FFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFF) >>> 5 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x1FFF) << 3 | (in[1 + inPos] & 0x1FFF) >>> 10) & 0xFF);
            out[2 + outPos] = (byte)((in[1 + inPos] & 0x1FFF) >>> 2 & 0xFF);
            out[3 + outPos] = (byte)(((in[1 + inPos] & 0x1FFF) << 6 | (in[2 + inPos] & 0x1FFF) >>> 7) & 0xFF);
            out[4 + outPos] = (byte)(((in[2 + inPos] & 0x1FFF) << 1 | (in[3 + inPos] & 0x1FFF) >>> 12) & 0xFF);
            out[5 + outPos] = (byte)((in[3 + inPos] & 0x1FFF) >>> 4 & 0xFF);
            out[6 + outPos] = (byte)(((in[3 + inPos] & 0x1FFF) << 4 | (in[4 + inPos] & 0x1FFF) >>> 9) & 0xFF);
            out[7 + outPos] = (byte)((in[4 + inPos] & 0x1FFF) >>> 1 & 0xFF);
            out[8 + outPos] = (byte)(((in[4 + inPos] & 0x1FFF) << 7 | (in[5 + inPos] & 0x1FFF) >>> 6) & 0xFF);
            out[9 + outPos] = (byte)(((in[5 + inPos] & 0x1FFF) << 2 | (in[6 + inPos] & 0x1FFF) >>> 11) & 0xFF);
            out[10 + outPos] = (byte)((in[6 + inPos] & 0x1FFF) >>> 3 & 0xFF);
            out[11 + outPos] = (byte)(((in[6 + inPos] & 0x1FFF) << 5 | (in[7 + inPos] & 0x1FFF) >>> 8) & 0xFF);
            out[12 + outPos] = (byte)(in[7 + inPos] & 0x1FFF & 0xFF);
            out[13 + outPos] = (byte)((in[8 + inPos] & 0x1FFF) >>> 5 & 0xFF);
            out[14 + outPos] = (byte)(((in[8 + inPos] & 0x1FFF) << 3 | (in[9 + inPos] & 0x1FFF) >>> 10) & 0xFF);
            out[15 + outPos] = (byte)((in[9 + inPos] & 0x1FFF) >>> 2 & 0xFF);
            out[16 + outPos] = (byte)(((in[9 + inPos] & 0x1FFF) << 6 | (in[10 + inPos] & 0x1FFF) >>> 7) & 0xFF);
            out[17 + outPos] = (byte)(((in[10 + inPos] & 0x1FFF) << 1 | (in[11 + inPos] & 0x1FFF) >>> 12) & 0xFF);
            out[18 + outPos] = (byte)((in[11 + inPos] & 0x1FFF) >>> 4 & 0xFF);
            out[19 + outPos] = (byte)(((in[11 + inPos] & 0x1FFF) << 4 | (in[12 + inPos] & 0x1FFF) >>> 9) & 0xFF);
            out[20 + outPos] = (byte)((in[12 + inPos] & 0x1FFF) >>> 1 & 0xFF);
            out[21 + outPos] = (byte)(((in[12 + inPos] & 0x1FFF) << 7 | (in[13 + inPos] & 0x1FFF) >>> 6) & 0xFF);
            out[22 + outPos] = (byte)(((in[13 + inPos] & 0x1FFF) << 2 | (in[14 + inPos] & 0x1FFF) >>> 11) & 0xFF);
            out[23 + outPos] = (byte)((in[14 + inPos] & 0x1FFF) >>> 3 & 0xFF);
            out[24 + outPos] = (byte)(((in[14 + inPos] & 0x1FFF) << 5 | (in[15 + inPos] & 0x1FFF) >>> 8) & 0xFF);
            out[25 + outPos] = (byte)(in[15 + inPos] & 0x1FFF & 0xFF);
            out[26 + outPos] = (byte)((in[16 + inPos] & 0x1FFF) >>> 5 & 0xFF);
            out[27 + outPos] = (byte)(((in[16 + inPos] & 0x1FFF) << 3 | (in[17 + inPos] & 0x1FFF) >>> 10) & 0xFF);
            out[28 + outPos] = (byte)((in[17 + inPos] & 0x1FFF) >>> 2 & 0xFF);
            out[29 + outPos] = (byte)(((in[17 + inPos] & 0x1FFF) << 6 | (in[18 + inPos] & 0x1FFF) >>> 7) & 0xFF);
            out[30 + outPos] = (byte)(((in[18 + inPos] & 0x1FFF) << 1 | (in[19 + inPos] & 0x1FFF) >>> 12) & 0xFF);
            out[31 + outPos] = (byte)((in[19 + inPos] & 0x1FFF) >>> 4 & 0xFF);
            out[32 + outPos] = (byte)(((in[19 + inPos] & 0x1FFF) << 4 | (in[20 + inPos] & 0x1FFF) >>> 9) & 0xFF);
            out[33 + outPos] = (byte)((in[20 + inPos] & 0x1FFF) >>> 1 & 0xFF);
            out[34 + outPos] = (byte)(((in[20 + inPos] & 0x1FFF) << 7 | (in[21 + inPos] & 0x1FFF) >>> 6) & 0xFF);
            out[35 + outPos] = (byte)(((in[21 + inPos] & 0x1FFF) << 2 | (in[22 + inPos] & 0x1FFF) >>> 11) & 0xFF);
            out[36 + outPos] = (byte)((in[22 + inPos] & 0x1FFF) >>> 3 & 0xFF);
            out[37 + outPos] = (byte)(((in[22 + inPos] & 0x1FFF) << 5 | (in[23 + inPos] & 0x1FFF) >>> 8) & 0xFF);
            out[38 + outPos] = (byte)(in[23 + inPos] & 0x1FFF & 0xFF);
            out[39 + outPos] = (byte)((in[24 + inPos] & 0x1FFF) >>> 5 & 0xFF);
            out[40 + outPos] = (byte)(((in[24 + inPos] & 0x1FFF) << 3 | (in[25 + inPos] & 0x1FFF) >>> 10) & 0xFF);
            out[41 + outPos] = (byte)((in[25 + inPos] & 0x1FFF) >>> 2 & 0xFF);
            out[42 + outPos] = (byte)(((in[25 + inPos] & 0x1FFF) << 6 | (in[26 + inPos] & 0x1FFF) >>> 7) & 0xFF);
            out[43 + outPos] = (byte)(((in[26 + inPos] & 0x1FFF) << 1 | (in[27 + inPos] & 0x1FFF) >>> 12) & 0xFF);
            out[44 + outPos] = (byte)((in[27 + inPos] & 0x1FFF) >>> 4 & 0xFF);
            out[45 + outPos] = (byte)(((in[27 + inPos] & 0x1FFF) << 4 | (in[28 + inPos] & 0x1FFF) >>> 9) & 0xFF);
            out[46 + outPos] = (byte)((in[28 + inPos] & 0x1FFF) >>> 1 & 0xFF);
            out[47 + outPos] = (byte)(((in[28 + inPos] & 0x1FFF) << 7 | (in[29 + inPos] & 0x1FFF) >>> 6) & 0xFF);
            out[48 + outPos] = (byte)(((in[29 + inPos] & 0x1FFF) << 2 | (in[30 + inPos] & 0x1FFF) >>> 11) & 0xFF);
            out[49 + outPos] = (byte)((in[30 + inPos] & 0x1FFF) >>> 3 & 0xFF);
            out[50 + outPos] = (byte)(((in[30 + inPos] & 0x1FFF) << 5 | (in[31 + inPos] & 0x1FFF) >>> 8) & 0xFF);
            out[51 + outPos] = (byte)(in[31 + inPos] & 0x1FFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 5 & 0x1FFF) | ((in[1 + inPos] & 0xFF) >>> 3 & 0x1FFF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 10 & 0x1FFF) | ((in[2 + inPos] & 0xFF) << 2 & 0x1FFF) | ((in[3 + inPos] & 0xFF) >>> 6 & 0x1FFF));
            out[2 + outPos] = (((in[3 + inPos] & 0xFF) << 7 & 0x1FFF) | ((in[4 + inPos] & 0xFF) >>> 1 & 0x1FFF));
            out[3 + outPos] = (((in[4 + inPos] & 0xFF) << 12 & 0x1FFF) | ((in[5 + inPos] & 0xFF) << 4 & 0x1FFF) | ((in[6 + inPos] & 0xFF) >>> 4 & 0x1FFF));
            out[4 + outPos] = (((in[6 + inPos] & 0xFF) << 9 & 0x1FFF) | ((in[7 + inPos] & 0xFF) << 1 & 0x1FFF) | ((in[8 + inPos] & 0xFF) >>> 7 & 0x1FFF));
            out[5 + outPos] = (((in[8 + inPos] & 0xFF) << 6 & 0x1FFF) | ((in[9 + inPos] & 0xFF) >>> 2 & 0x1FFF));
            out[6 + outPos] = (((in[9 + inPos] & 0xFF) << 11 & 0x1FFF) | ((in[10 + inPos] & 0xFF) << 3 & 0x1FFF) | ((in[11 + inPos] & 0xFF) >>> 5 & 0x1FFF));
            out[7 + outPos] = (((in[11 + inPos] & 0xFF) << 8 & 0x1FFF) | (in[12 + inPos] & 0xFF & 0x1FFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 5 & 0x1FFF) | ((in[1 + inPos] & 0xFF) >>> 3 & 0x1FFF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 10 & 0x1FFF) | ((in[2 + inPos] & 0xFF) << 2 & 0x1FFF) | ((in[3 + inPos] & 0xFF) >>> 6 & 0x1FFF));
            out[2 + outPos] = (((in[3 + inPos] & 0xFF) << 7 & 0x1FFF) | ((in[4 + inPos] & 0xFF) >>> 1 & 0x1FFF));
            out[3 + outPos] = (((in[4 + inPos] & 0xFF) << 12 & 0x1FFF) | ((in[5 + inPos] & 0xFF) << 4 & 0x1FFF) | ((in[6 + inPos] & 0xFF) >>> 4 & 0x1FFF));
            out[4 + outPos] = (((in[6 + inPos] & 0xFF) << 9 & 0x1FFF) | ((in[7 + inPos] & 0xFF) << 1 & 0x1FFF) | ((in[8 + inPos] & 0xFF) >>> 7 & 0x1FFF));
            out[5 + outPos] = (((in[8 + inPos] & 0xFF) << 6 & 0x1FFF) | ((in[9 + inPos] & 0xFF) >>> 2 & 0x1FFF));
            out[6 + outPos] = (((in[9 + inPos] & 0xFF) << 11 & 0x1FFF) | ((in[10 + inPos] & 0xFF) << 3 & 0x1FFF) | ((in[11 + inPos] & 0xFF) >>> 5 & 0x1FFF));
            out[7 + outPos] = (((in[11 + inPos] & 0xFF) << 8 & 0x1FFF) | (in[12 + inPos] & 0xFF & 0x1FFF));
            out[8 + outPos] = (((in[13 + inPos] & 0xFF) << 5 & 0x1FFF) | ((in[14 + inPos] & 0xFF) >>> 3 & 0x1FFF));
            out[9 + outPos] = (((in[14 + inPos] & 0xFF) << 10 & 0x1FFF) | ((in[15 + inPos] & 0xFF) << 2 & 0x1FFF) | ((in[16 + inPos] & 0xFF) >>> 6 & 0x1FFF));
            out[10 + outPos] = (((in[16 + inPos] & 0xFF) << 7 & 0x1FFF) | ((in[17 + inPos] & 0xFF) >>> 1 & 0x1FFF));
            out[11 + outPos] = (((in[17 + inPos] & 0xFF) << 12 & 0x1FFF) | ((in[18 + inPos] & 0xFF) << 4 & 0x1FFF) | ((in[19 + inPos] & 0xFF) >>> 4 & 0x1FFF));
            out[12 + outPos] = (((in[19 + inPos] & 0xFF) << 9 & 0x1FFF) | ((in[20 + inPos] & 0xFF) << 1 & 0x1FFF) | ((in[21 + inPos] & 0xFF) >>> 7 & 0x1FFF));
            out[13 + outPos] = (((in[21 + inPos] & 0xFF) << 6 & 0x1FFF) | ((in[22 + inPos] & 0xFF) >>> 2 & 0x1FFF));
            out[14 + outPos] = (((in[22 + inPos] & 0xFF) << 11 & 0x1FFF) | ((in[23 + inPos] & 0xFF) << 3 & 0x1FFF) | ((in[24 + inPos] & 0xFF) >>> 5 & 0x1FFF));
            out[15 + outPos] = (((in[24 + inPos] & 0xFF) << 8 & 0x1FFF) | (in[25 + inPos] & 0xFF & 0x1FFF));
            out[16 + outPos] = (((in[26 + inPos] & 0xFF) << 5 & 0x1FFF) | ((in[27 + inPos] & 0xFF) >>> 3 & 0x1FFF));
            out[17 + outPos] = (((in[27 + inPos] & 0xFF) << 10 & 0x1FFF) | ((in[28 + inPos] & 0xFF) << 2 & 0x1FFF) | ((in[29 + inPos] & 0xFF) >>> 6 & 0x1FFF));
            out[18 + outPos] = (((in[29 + inPos] & 0xFF) << 7 & 0x1FFF) | ((in[30 + inPos] & 0xFF) >>> 1 & 0x1FFF));
            out[19 + outPos] = (((in[30 + inPos] & 0xFF) << 12 & 0x1FFF) | ((in[31 + inPos] & 0xFF) << 4 & 0x1FFF) | ((in[32 + inPos] & 0xFF) >>> 4 & 0x1FFF));
            out[20 + outPos] = (((in[32 + inPos] & 0xFF) << 9 & 0x1FFF) | ((in[33 + inPos] & 0xFF) << 1 & 0x1FFF) | ((in[34 + inPos] & 0xFF) >>> 7 & 0x1FFF));
            out[21 + outPos] = (((in[34 + inPos] & 0xFF) << 6 & 0x1FFF) | ((in[35 + inPos] & 0xFF) >>> 2 & 0x1FFF));
            out[22 + outPos] = (((in[35 + inPos] & 0xFF) << 11 & 0x1FFF) | ((in[36 + inPos] & 0xFF) << 3 & 0x1FFF) | ((in[37 + inPos] & 0xFF) >>> 5 & 0x1FFF));
            out[23 + outPos] = (((in[37 + inPos] & 0xFF) << 8 & 0x1FFF) | (in[38 + inPos] & 0xFF & 0x1FFF));
            out[24 + outPos] = (((in[39 + inPos] & 0xFF) << 5 & 0x1FFF) | ((in[40 + inPos] & 0xFF) >>> 3 & 0x1FFF));
            out[25 + outPos] = (((in[40 + inPos] & 0xFF) << 10 & 0x1FFF) | ((in[41 + inPos] & 0xFF) << 2 & 0x1FFF) | ((in[42 + inPos] & 0xFF) >>> 6 & 0x1FFF));
            out[26 + outPos] = (((in[42 + inPos] & 0xFF) << 7 & 0x1FFF) | ((in[43 + inPos] & 0xFF) >>> 1 & 0x1FFF));
            out[27 + outPos] = (((in[43 + inPos] & 0xFF) << 12 & 0x1FFF) | ((in[44 + inPos] & 0xFF) << 4 & 0x1FFF) | ((in[45 + inPos] & 0xFF) >>> 4 & 0x1FFF));
            out[28 + outPos] = (((in[45 + inPos] & 0xFF) << 9 & 0x1FFF) | ((in[46 + inPos] & 0xFF) << 1 & 0x1FFF) | ((in[47 + inPos] & 0xFF) >>> 7 & 0x1FFF));
            out[29 + outPos] = (((in[47 + inPos] & 0xFF) << 6 & 0x1FFF) | ((in[48 + inPos] & 0xFF) >>> 2 & 0x1FFF));
            out[30 + outPos] = (((in[48 + inPos] & 0xFF) << 11 & 0x1FFF) | ((in[49 + inPos] & 0xFF) << 3 & 0x1FFF) | ((in[50 + inPos] & 0xFF) >>> 5 & 0x1FFF));
            out[31 + outPos] = (((in[50 + inPos] & 0xFF) << 8 & 0x1FFF) | (in[51 + inPos] & 0xFF & 0x1FFF));
        }
    }
    
    private static final class Packer14 extends BytePacker
    {
        private Packer14() {
            super(14);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x3FFF) << 2 | (in[1 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[2 + outPos] = (byte)((in[1 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[3 + outPos] = (byte)(((in[1 + inPos] & 0x3FFF) << 4 | (in[2 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[4 + outPos] = (byte)((in[2 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[5 + outPos] = (byte)(((in[2 + inPos] & 0x3FFF) << 6 | (in[3 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[6 + outPos] = (byte)(in[3 + inPos] & 0x3FFF & 0xFF);
            out[7 + outPos] = (byte)((in[4 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[8 + outPos] = (byte)(((in[4 + inPos] & 0x3FFF) << 2 | (in[5 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[9 + outPos] = (byte)((in[5 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[10 + outPos] = (byte)(((in[5 + inPos] & 0x3FFF) << 4 | (in[6 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[11 + outPos] = (byte)((in[6 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[12 + outPos] = (byte)(((in[6 + inPos] & 0x3FFF) << 6 | (in[7 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[13 + outPos] = (byte)(in[7 + inPos] & 0x3FFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x3FFF) << 2 | (in[1 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[2 + outPos] = (byte)((in[1 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[3 + outPos] = (byte)(((in[1 + inPos] & 0x3FFF) << 4 | (in[2 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[4 + outPos] = (byte)((in[2 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[5 + outPos] = (byte)(((in[2 + inPos] & 0x3FFF) << 6 | (in[3 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[6 + outPos] = (byte)(in[3 + inPos] & 0x3FFF & 0xFF);
            out[7 + outPos] = (byte)((in[4 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[8 + outPos] = (byte)(((in[4 + inPos] & 0x3FFF) << 2 | (in[5 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[9 + outPos] = (byte)((in[5 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[10 + outPos] = (byte)(((in[5 + inPos] & 0x3FFF) << 4 | (in[6 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[11 + outPos] = (byte)((in[6 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[12 + outPos] = (byte)(((in[6 + inPos] & 0x3FFF) << 6 | (in[7 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[13 + outPos] = (byte)(in[7 + inPos] & 0x3FFF & 0xFF);
            out[14 + outPos] = (byte)((in[8 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[15 + outPos] = (byte)(((in[8 + inPos] & 0x3FFF) << 2 | (in[9 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[16 + outPos] = (byte)((in[9 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[17 + outPos] = (byte)(((in[9 + inPos] & 0x3FFF) << 4 | (in[10 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[18 + outPos] = (byte)((in[10 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[19 + outPos] = (byte)(((in[10 + inPos] & 0x3FFF) << 6 | (in[11 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[20 + outPos] = (byte)(in[11 + inPos] & 0x3FFF & 0xFF);
            out[21 + outPos] = (byte)((in[12 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[22 + outPos] = (byte)(((in[12 + inPos] & 0x3FFF) << 2 | (in[13 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[23 + outPos] = (byte)((in[13 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[24 + outPos] = (byte)(((in[13 + inPos] & 0x3FFF) << 4 | (in[14 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[25 + outPos] = (byte)((in[14 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[26 + outPos] = (byte)(((in[14 + inPos] & 0x3FFF) << 6 | (in[15 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[27 + outPos] = (byte)(in[15 + inPos] & 0x3FFF & 0xFF);
            out[28 + outPos] = (byte)((in[16 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[29 + outPos] = (byte)(((in[16 + inPos] & 0x3FFF) << 2 | (in[17 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[30 + outPos] = (byte)((in[17 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[31 + outPos] = (byte)(((in[17 + inPos] & 0x3FFF) << 4 | (in[18 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[32 + outPos] = (byte)((in[18 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[33 + outPos] = (byte)(((in[18 + inPos] & 0x3FFF) << 6 | (in[19 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[34 + outPos] = (byte)(in[19 + inPos] & 0x3FFF & 0xFF);
            out[35 + outPos] = (byte)((in[20 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[36 + outPos] = (byte)(((in[20 + inPos] & 0x3FFF) << 2 | (in[21 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[37 + outPos] = (byte)((in[21 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[38 + outPos] = (byte)(((in[21 + inPos] & 0x3FFF) << 4 | (in[22 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[39 + outPos] = (byte)((in[22 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[40 + outPos] = (byte)(((in[22 + inPos] & 0x3FFF) << 6 | (in[23 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[41 + outPos] = (byte)(in[23 + inPos] & 0x3FFF & 0xFF);
            out[42 + outPos] = (byte)((in[24 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[43 + outPos] = (byte)(((in[24 + inPos] & 0x3FFF) << 2 | (in[25 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[44 + outPos] = (byte)((in[25 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[45 + outPos] = (byte)(((in[25 + inPos] & 0x3FFF) << 4 | (in[26 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[46 + outPos] = (byte)((in[26 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[47 + outPos] = (byte)(((in[26 + inPos] & 0x3FFF) << 6 | (in[27 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[48 + outPos] = (byte)(in[27 + inPos] & 0x3FFF & 0xFF);
            out[49 + outPos] = (byte)((in[28 + inPos] & 0x3FFF) >>> 6 & 0xFF);
            out[50 + outPos] = (byte)(((in[28 + inPos] & 0x3FFF) << 2 | (in[29 + inPos] & 0x3FFF) >>> 12) & 0xFF);
            out[51 + outPos] = (byte)((in[29 + inPos] & 0x3FFF) >>> 4 & 0xFF);
            out[52 + outPos] = (byte)(((in[29 + inPos] & 0x3FFF) << 4 | (in[30 + inPos] & 0x3FFF) >>> 10) & 0xFF);
            out[53 + outPos] = (byte)((in[30 + inPos] & 0x3FFF) >>> 2 & 0xFF);
            out[54 + outPos] = (byte)(((in[30 + inPos] & 0x3FFF) << 6 | (in[31 + inPos] & 0x3FFF) >>> 8) & 0xFF);
            out[55 + outPos] = (byte)(in[31 + inPos] & 0x3FFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[1 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[2 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[3 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[2 + outPos] = (((in[3 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[4 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[3 + outPos] = (((in[5 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[6 + inPos] & 0xFF & 0x3FFF));
            out[4 + outPos] = (((in[7 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[8 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[5 + outPos] = (((in[8 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[9 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[10 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[6 + outPos] = (((in[10 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[11 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[12 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[7 + outPos] = (((in[12 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[13 + inPos] & 0xFF & 0x3FFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[1 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[2 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[3 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[2 + outPos] = (((in[3 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[4 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[3 + outPos] = (((in[5 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[6 + inPos] & 0xFF & 0x3FFF));
            out[4 + outPos] = (((in[7 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[8 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[5 + outPos] = (((in[8 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[9 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[10 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[6 + outPos] = (((in[10 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[11 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[12 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[7 + outPos] = (((in[12 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[13 + inPos] & 0xFF & 0x3FFF));
            out[8 + outPos] = (((in[14 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[15 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[9 + outPos] = (((in[15 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[16 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[17 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[10 + outPos] = (((in[17 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[18 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[19 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[11 + outPos] = (((in[19 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[20 + inPos] & 0xFF & 0x3FFF));
            out[12 + outPos] = (((in[21 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[22 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[13 + outPos] = (((in[22 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[23 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[24 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[14 + outPos] = (((in[24 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[25 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[26 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[15 + outPos] = (((in[26 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[27 + inPos] & 0xFF & 0x3FFF));
            out[16 + outPos] = (((in[28 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[29 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[17 + outPos] = (((in[29 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[30 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[31 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[18 + outPos] = (((in[31 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[32 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[33 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[19 + outPos] = (((in[33 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[34 + inPos] & 0xFF & 0x3FFF));
            out[20 + outPos] = (((in[35 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[36 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[21 + outPos] = (((in[36 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[37 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[38 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[22 + outPos] = (((in[38 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[39 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[40 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[23 + outPos] = (((in[40 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[41 + inPos] & 0xFF & 0x3FFF));
            out[24 + outPos] = (((in[42 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[43 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[25 + outPos] = (((in[43 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[44 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[45 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[26 + outPos] = (((in[45 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[46 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[47 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[27 + outPos] = (((in[47 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[48 + inPos] & 0xFF & 0x3FFF));
            out[28 + outPos] = (((in[49 + inPos] & 0xFF) << 6 & 0x3FFF) | ((in[50 + inPos] & 0xFF) >>> 2 & 0x3FFF));
            out[29 + outPos] = (((in[50 + inPos] & 0xFF) << 12 & 0x3FFF) | ((in[51 + inPos] & 0xFF) << 4 & 0x3FFF) | ((in[52 + inPos] & 0xFF) >>> 4 & 0x3FFF));
            out[30 + outPos] = (((in[52 + inPos] & 0xFF) << 10 & 0x3FFF) | ((in[53 + inPos] & 0xFF) << 2 & 0x3FFF) | ((in[54 + inPos] & 0xFF) >>> 6 & 0x3FFF));
            out[31 + outPos] = (((in[54 + inPos] & 0xFF) << 8 & 0x3FFF) | (in[55 + inPos] & 0xFF & 0x3FFF));
        }
    }
    
    private static final class Packer15 extends BytePacker
    {
        private Packer15() {
            super(15);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FFF) >>> 7 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x7FFF) << 1 | (in[1 + inPos] & 0x7FFF) >>> 14) & 0xFF);
            out[2 + outPos] = (byte)((in[1 + inPos] & 0x7FFF) >>> 6 & 0xFF);
            out[3 + outPos] = (byte)(((in[1 + inPos] & 0x7FFF) << 2 | (in[2 + inPos] & 0x7FFF) >>> 13) & 0xFF);
            out[4 + outPos] = (byte)((in[2 + inPos] & 0x7FFF) >>> 5 & 0xFF);
            out[5 + outPos] = (byte)(((in[2 + inPos] & 0x7FFF) << 3 | (in[3 + inPos] & 0x7FFF) >>> 12) & 0xFF);
            out[6 + outPos] = (byte)((in[3 + inPos] & 0x7FFF) >>> 4 & 0xFF);
            out[7 + outPos] = (byte)(((in[3 + inPos] & 0x7FFF) << 4 | (in[4 + inPos] & 0x7FFF) >>> 11) & 0xFF);
            out[8 + outPos] = (byte)((in[4 + inPos] & 0x7FFF) >>> 3 & 0xFF);
            out[9 + outPos] = (byte)(((in[4 + inPos] & 0x7FFF) << 5 | (in[5 + inPos] & 0x7FFF) >>> 10) & 0xFF);
            out[10 + outPos] = (byte)((in[5 + inPos] & 0x7FFF) >>> 2 & 0xFF);
            out[11 + outPos] = (byte)(((in[5 + inPos] & 0x7FFF) << 6 | (in[6 + inPos] & 0x7FFF) >>> 9) & 0xFF);
            out[12 + outPos] = (byte)((in[6 + inPos] & 0x7FFF) >>> 1 & 0xFF);
            out[13 + outPos] = (byte)(((in[6 + inPos] & 0x7FFF) << 7 | (in[7 + inPos] & 0x7FFF) >>> 8) & 0xFF);
            out[14 + outPos] = (byte)(in[7 + inPos] & 0x7FFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FFF) >>> 7 & 0xFF);
            out[1 + outPos] = (byte)(((in[0 + inPos] & 0x7FFF) << 1 | (in[1 + inPos] & 0x7FFF) >>> 14) & 0xFF);
            out[2 + outPos] = (byte)((in[1 + inPos] & 0x7FFF) >>> 6 & 0xFF);
            out[3 + outPos] = (byte)(((in[1 + inPos] & 0x7FFF) << 2 | (in[2 + inPos] & 0x7FFF) >>> 13) & 0xFF);
            out[4 + outPos] = (byte)((in[2 + inPos] & 0x7FFF) >>> 5 & 0xFF);
            out[5 + outPos] = (byte)(((in[2 + inPos] & 0x7FFF) << 3 | (in[3 + inPos] & 0x7FFF) >>> 12) & 0xFF);
            out[6 + outPos] = (byte)((in[3 + inPos] & 0x7FFF) >>> 4 & 0xFF);
            out[7 + outPos] = (byte)(((in[3 + inPos] & 0x7FFF) << 4 | (in[4 + inPos] & 0x7FFF) >>> 11) & 0xFF);
            out[8 + outPos] = (byte)((in[4 + inPos] & 0x7FFF) >>> 3 & 0xFF);
            out[9 + outPos] = (byte)(((in[4 + inPos] & 0x7FFF) << 5 | (in[5 + inPos] & 0x7FFF) >>> 10) & 0xFF);
            out[10 + outPos] = (byte)((in[5 + inPos] & 0x7FFF) >>> 2 & 0xFF);
            out[11 + outPos] = (byte)(((in[5 + inPos] & 0x7FFF) << 6 | (in[6 + inPos] & 0x7FFF) >>> 9) & 0xFF);
            out[12 + outPos] = (byte)((in[6 + inPos] & 0x7FFF) >>> 1 & 0xFF);
            out[13 + outPos] = (byte)(((in[6 + inPos] & 0x7FFF) << 7 | (in[7 + inPos] & 0x7FFF) >>> 8) & 0xFF);
            out[14 + outPos] = (byte)(in[7 + inPos] & 0x7FFF & 0xFF);
            out[15 + outPos] = (byte)((in[8 + inPos] & 0x7FFF) >>> 7 & 0xFF);
            out[16 + outPos] = (byte)(((in[8 + inPos] & 0x7FFF) << 1 | (in[9 + inPos] & 0x7FFF) >>> 14) & 0xFF);
            out[17 + outPos] = (byte)((in[9 + inPos] & 0x7FFF) >>> 6 & 0xFF);
            out[18 + outPos] = (byte)(((in[9 + inPos] & 0x7FFF) << 2 | (in[10 + inPos] & 0x7FFF) >>> 13) & 0xFF);
            out[19 + outPos] = (byte)((in[10 + inPos] & 0x7FFF) >>> 5 & 0xFF);
            out[20 + outPos] = (byte)(((in[10 + inPos] & 0x7FFF) << 3 | (in[11 + inPos] & 0x7FFF) >>> 12) & 0xFF);
            out[21 + outPos] = (byte)((in[11 + inPos] & 0x7FFF) >>> 4 & 0xFF);
            out[22 + outPos] = (byte)(((in[11 + inPos] & 0x7FFF) << 4 | (in[12 + inPos] & 0x7FFF) >>> 11) & 0xFF);
            out[23 + outPos] = (byte)((in[12 + inPos] & 0x7FFF) >>> 3 & 0xFF);
            out[24 + outPos] = (byte)(((in[12 + inPos] & 0x7FFF) << 5 | (in[13 + inPos] & 0x7FFF) >>> 10) & 0xFF);
            out[25 + outPos] = (byte)((in[13 + inPos] & 0x7FFF) >>> 2 & 0xFF);
            out[26 + outPos] = (byte)(((in[13 + inPos] & 0x7FFF) << 6 | (in[14 + inPos] & 0x7FFF) >>> 9) & 0xFF);
            out[27 + outPos] = (byte)((in[14 + inPos] & 0x7FFF) >>> 1 & 0xFF);
            out[28 + outPos] = (byte)(((in[14 + inPos] & 0x7FFF) << 7 | (in[15 + inPos] & 0x7FFF) >>> 8) & 0xFF);
            out[29 + outPos] = (byte)(in[15 + inPos] & 0x7FFF & 0xFF);
            out[30 + outPos] = (byte)((in[16 + inPos] & 0x7FFF) >>> 7 & 0xFF);
            out[31 + outPos] = (byte)(((in[16 + inPos] & 0x7FFF) << 1 | (in[17 + inPos] & 0x7FFF) >>> 14) & 0xFF);
            out[32 + outPos] = (byte)((in[17 + inPos] & 0x7FFF) >>> 6 & 0xFF);
            out[33 + outPos] = (byte)(((in[17 + inPos] & 0x7FFF) << 2 | (in[18 + inPos] & 0x7FFF) >>> 13) & 0xFF);
            out[34 + outPos] = (byte)((in[18 + inPos] & 0x7FFF) >>> 5 & 0xFF);
            out[35 + outPos] = (byte)(((in[18 + inPos] & 0x7FFF) << 3 | (in[19 + inPos] & 0x7FFF) >>> 12) & 0xFF);
            out[36 + outPos] = (byte)((in[19 + inPos] & 0x7FFF) >>> 4 & 0xFF);
            out[37 + outPos] = (byte)(((in[19 + inPos] & 0x7FFF) << 4 | (in[20 + inPos] & 0x7FFF) >>> 11) & 0xFF);
            out[38 + outPos] = (byte)((in[20 + inPos] & 0x7FFF) >>> 3 & 0xFF);
            out[39 + outPos] = (byte)(((in[20 + inPos] & 0x7FFF) << 5 | (in[21 + inPos] & 0x7FFF) >>> 10) & 0xFF);
            out[40 + outPos] = (byte)((in[21 + inPos] & 0x7FFF) >>> 2 & 0xFF);
            out[41 + outPos] = (byte)(((in[21 + inPos] & 0x7FFF) << 6 | (in[22 + inPos] & 0x7FFF) >>> 9) & 0xFF);
            out[42 + outPos] = (byte)((in[22 + inPos] & 0x7FFF) >>> 1 & 0xFF);
            out[43 + outPos] = (byte)(((in[22 + inPos] & 0x7FFF) << 7 | (in[23 + inPos] & 0x7FFF) >>> 8) & 0xFF);
            out[44 + outPos] = (byte)(in[23 + inPos] & 0x7FFF & 0xFF);
            out[45 + outPos] = (byte)((in[24 + inPos] & 0x7FFF) >>> 7 & 0xFF);
            out[46 + outPos] = (byte)(((in[24 + inPos] & 0x7FFF) << 1 | (in[25 + inPos] & 0x7FFF) >>> 14) & 0xFF);
            out[47 + outPos] = (byte)((in[25 + inPos] & 0x7FFF) >>> 6 & 0xFF);
            out[48 + outPos] = (byte)(((in[25 + inPos] & 0x7FFF) << 2 | (in[26 + inPos] & 0x7FFF) >>> 13) & 0xFF);
            out[49 + outPos] = (byte)((in[26 + inPos] & 0x7FFF) >>> 5 & 0xFF);
            out[50 + outPos] = (byte)(((in[26 + inPos] & 0x7FFF) << 3 | (in[27 + inPos] & 0x7FFF) >>> 12) & 0xFF);
            out[51 + outPos] = (byte)((in[27 + inPos] & 0x7FFF) >>> 4 & 0xFF);
            out[52 + outPos] = (byte)(((in[27 + inPos] & 0x7FFF) << 4 | (in[28 + inPos] & 0x7FFF) >>> 11) & 0xFF);
            out[53 + outPos] = (byte)((in[28 + inPos] & 0x7FFF) >>> 3 & 0xFF);
            out[54 + outPos] = (byte)(((in[28 + inPos] & 0x7FFF) << 5 | (in[29 + inPos] & 0x7FFF) >>> 10) & 0xFF);
            out[55 + outPos] = (byte)((in[29 + inPos] & 0x7FFF) >>> 2 & 0xFF);
            out[56 + outPos] = (byte)(((in[29 + inPos] & 0x7FFF) << 6 | (in[30 + inPos] & 0x7FFF) >>> 9) & 0xFF);
            out[57 + outPos] = (byte)((in[30 + inPos] & 0x7FFF) >>> 1 & 0xFF);
            out[58 + outPos] = (byte)(((in[30 + inPos] & 0x7FFF) << 7 | (in[31 + inPos] & 0x7FFF) >>> 8) & 0xFF);
            out[59 + outPos] = (byte)(in[31 + inPos] & 0x7FFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 7 & 0x7FFF) | ((in[1 + inPos] & 0xFF) >>> 1 & 0x7FFF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 14 & 0x7FFF) | ((in[2 + inPos] & 0xFF) << 6 & 0x7FFF) | ((in[3 + inPos] & 0xFF) >>> 2 & 0x7FFF));
            out[2 + outPos] = (((in[3 + inPos] & 0xFF) << 13 & 0x7FFF) | ((in[4 + inPos] & 0xFF) << 5 & 0x7FFF) | ((in[5 + inPos] & 0xFF) >>> 3 & 0x7FFF));
            out[3 + outPos] = (((in[5 + inPos] & 0xFF) << 12 & 0x7FFF) | ((in[6 + inPos] & 0xFF) << 4 & 0x7FFF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0x7FFF));
            out[4 + outPos] = (((in[7 + inPos] & 0xFF) << 11 & 0x7FFF) | ((in[8 + inPos] & 0xFF) << 3 & 0x7FFF) | ((in[9 + inPos] & 0xFF) >>> 5 & 0x7FFF));
            out[5 + outPos] = (((in[9 + inPos] & 0xFF) << 10 & 0x7FFF) | ((in[10 + inPos] & 0xFF) << 2 & 0x7FFF) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x7FFF));
            out[6 + outPos] = (((in[11 + inPos] & 0xFF) << 9 & 0x7FFF) | ((in[12 + inPos] & 0xFF) << 1 & 0x7FFF) | ((in[13 + inPos] & 0xFF) >>> 7 & 0x7FFF));
            out[7 + outPos] = (((in[13 + inPos] & 0xFF) << 8 & 0x7FFF) | (in[14 + inPos] & 0xFF & 0x7FFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 7 & 0x7FFF) | ((in[1 + inPos] & 0xFF) >>> 1 & 0x7FFF));
            out[1 + outPos] = (((in[1 + inPos] & 0xFF) << 14 & 0x7FFF) | ((in[2 + inPos] & 0xFF) << 6 & 0x7FFF) | ((in[3 + inPos] & 0xFF) >>> 2 & 0x7FFF));
            out[2 + outPos] = (((in[3 + inPos] & 0xFF) << 13 & 0x7FFF) | ((in[4 + inPos] & 0xFF) << 5 & 0x7FFF) | ((in[5 + inPos] & 0xFF) >>> 3 & 0x7FFF));
            out[3 + outPos] = (((in[5 + inPos] & 0xFF) << 12 & 0x7FFF) | ((in[6 + inPos] & 0xFF) << 4 & 0x7FFF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0x7FFF));
            out[4 + outPos] = (((in[7 + inPos] & 0xFF) << 11 & 0x7FFF) | ((in[8 + inPos] & 0xFF) << 3 & 0x7FFF) | ((in[9 + inPos] & 0xFF) >>> 5 & 0x7FFF));
            out[5 + outPos] = (((in[9 + inPos] & 0xFF) << 10 & 0x7FFF) | ((in[10 + inPos] & 0xFF) << 2 & 0x7FFF) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x7FFF));
            out[6 + outPos] = (((in[11 + inPos] & 0xFF) << 9 & 0x7FFF) | ((in[12 + inPos] & 0xFF) << 1 & 0x7FFF) | ((in[13 + inPos] & 0xFF) >>> 7 & 0x7FFF));
            out[7 + outPos] = (((in[13 + inPos] & 0xFF) << 8 & 0x7FFF) | (in[14 + inPos] & 0xFF & 0x7FFF));
            out[8 + outPos] = (((in[15 + inPos] & 0xFF) << 7 & 0x7FFF) | ((in[16 + inPos] & 0xFF) >>> 1 & 0x7FFF));
            out[9 + outPos] = (((in[16 + inPos] & 0xFF) << 14 & 0x7FFF) | ((in[17 + inPos] & 0xFF) << 6 & 0x7FFF) | ((in[18 + inPos] & 0xFF) >>> 2 & 0x7FFF));
            out[10 + outPos] = (((in[18 + inPos] & 0xFF) << 13 & 0x7FFF) | ((in[19 + inPos] & 0xFF) << 5 & 0x7FFF) | ((in[20 + inPos] & 0xFF) >>> 3 & 0x7FFF));
            out[11 + outPos] = (((in[20 + inPos] & 0xFF) << 12 & 0x7FFF) | ((in[21 + inPos] & 0xFF) << 4 & 0x7FFF) | ((in[22 + inPos] & 0xFF) >>> 4 & 0x7FFF));
            out[12 + outPos] = (((in[22 + inPos] & 0xFF) << 11 & 0x7FFF) | ((in[23 + inPos] & 0xFF) << 3 & 0x7FFF) | ((in[24 + inPos] & 0xFF) >>> 5 & 0x7FFF));
            out[13 + outPos] = (((in[24 + inPos] & 0xFF) << 10 & 0x7FFF) | ((in[25 + inPos] & 0xFF) << 2 & 0x7FFF) | ((in[26 + inPos] & 0xFF) >>> 6 & 0x7FFF));
            out[14 + outPos] = (((in[26 + inPos] & 0xFF) << 9 & 0x7FFF) | ((in[27 + inPos] & 0xFF) << 1 & 0x7FFF) | ((in[28 + inPos] & 0xFF) >>> 7 & 0x7FFF));
            out[15 + outPos] = (((in[28 + inPos] & 0xFF) << 8 & 0x7FFF) | (in[29 + inPos] & 0xFF & 0x7FFF));
            out[16 + outPos] = (((in[30 + inPos] & 0xFF) << 7 & 0x7FFF) | ((in[31 + inPos] & 0xFF) >>> 1 & 0x7FFF));
            out[17 + outPos] = (((in[31 + inPos] & 0xFF) << 14 & 0x7FFF) | ((in[32 + inPos] & 0xFF) << 6 & 0x7FFF) | ((in[33 + inPos] & 0xFF) >>> 2 & 0x7FFF));
            out[18 + outPos] = (((in[33 + inPos] & 0xFF) << 13 & 0x7FFF) | ((in[34 + inPos] & 0xFF) << 5 & 0x7FFF) | ((in[35 + inPos] & 0xFF) >>> 3 & 0x7FFF));
            out[19 + outPos] = (((in[35 + inPos] & 0xFF) << 12 & 0x7FFF) | ((in[36 + inPos] & 0xFF) << 4 & 0x7FFF) | ((in[37 + inPos] & 0xFF) >>> 4 & 0x7FFF));
            out[20 + outPos] = (((in[37 + inPos] & 0xFF) << 11 & 0x7FFF) | ((in[38 + inPos] & 0xFF) << 3 & 0x7FFF) | ((in[39 + inPos] & 0xFF) >>> 5 & 0x7FFF));
            out[21 + outPos] = (((in[39 + inPos] & 0xFF) << 10 & 0x7FFF) | ((in[40 + inPos] & 0xFF) << 2 & 0x7FFF) | ((in[41 + inPos] & 0xFF) >>> 6 & 0x7FFF));
            out[22 + outPos] = (((in[41 + inPos] & 0xFF) << 9 & 0x7FFF) | ((in[42 + inPos] & 0xFF) << 1 & 0x7FFF) | ((in[43 + inPos] & 0xFF) >>> 7 & 0x7FFF));
            out[23 + outPos] = (((in[43 + inPos] & 0xFF) << 8 & 0x7FFF) | (in[44 + inPos] & 0xFF & 0x7FFF));
            out[24 + outPos] = (((in[45 + inPos] & 0xFF) << 7 & 0x7FFF) | ((in[46 + inPos] & 0xFF) >>> 1 & 0x7FFF));
            out[25 + outPos] = (((in[46 + inPos] & 0xFF) << 14 & 0x7FFF) | ((in[47 + inPos] & 0xFF) << 6 & 0x7FFF) | ((in[48 + inPos] & 0xFF) >>> 2 & 0x7FFF));
            out[26 + outPos] = (((in[48 + inPos] & 0xFF) << 13 & 0x7FFF) | ((in[49 + inPos] & 0xFF) << 5 & 0x7FFF) | ((in[50 + inPos] & 0xFF) >>> 3 & 0x7FFF));
            out[27 + outPos] = (((in[50 + inPos] & 0xFF) << 12 & 0x7FFF) | ((in[51 + inPos] & 0xFF) << 4 & 0x7FFF) | ((in[52 + inPos] & 0xFF) >>> 4 & 0x7FFF));
            out[28 + outPos] = (((in[52 + inPos] & 0xFF) << 11 & 0x7FFF) | ((in[53 + inPos] & 0xFF) << 3 & 0x7FFF) | ((in[54 + inPos] & 0xFF) >>> 5 & 0x7FFF));
            out[29 + outPos] = (((in[54 + inPos] & 0xFF) << 10 & 0x7FFF) | ((in[55 + inPos] & 0xFF) << 2 & 0x7FFF) | ((in[56 + inPos] & 0xFF) >>> 6 & 0x7FFF));
            out[30 + outPos] = (((in[56 + inPos] & 0xFF) << 9 & 0x7FFF) | ((in[57 + inPos] & 0xFF) << 1 & 0x7FFF) | ((in[58 + inPos] & 0xFF) >>> 7 & 0x7FFF));
            out[31 + outPos] = (((in[58 + inPos] & 0xFF) << 8 & 0x7FFF) | (in[59 + inPos] & 0xFF & 0x7FFF));
        }
    }
    
    private static final class Packer16 extends BytePacker
    {
        private Packer16() {
            super(16);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[1 + outPos] = (byte)(in[0 + inPos] & 0xFFFF & 0xFF);
            out[2 + outPos] = (byte)((in[1 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[3 + outPos] = (byte)(in[1 + inPos] & 0xFFFF & 0xFF);
            out[4 + outPos] = (byte)((in[2 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[5 + outPos] = (byte)(in[2 + inPos] & 0xFFFF & 0xFF);
            out[6 + outPos] = (byte)((in[3 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[7 + outPos] = (byte)(in[3 + inPos] & 0xFFFF & 0xFF);
            out[8 + outPos] = (byte)((in[4 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[9 + outPos] = (byte)(in[4 + inPos] & 0xFFFF & 0xFF);
            out[10 + outPos] = (byte)((in[5 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[11 + outPos] = (byte)(in[5 + inPos] & 0xFFFF & 0xFF);
            out[12 + outPos] = (byte)((in[6 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[13 + outPos] = (byte)(in[6 + inPos] & 0xFFFF & 0xFF);
            out[14 + outPos] = (byte)((in[7 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[15 + outPos] = (byte)(in[7 + inPos] & 0xFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[1 + outPos] = (byte)(in[0 + inPos] & 0xFFFF & 0xFF);
            out[2 + outPos] = (byte)((in[1 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[3 + outPos] = (byte)(in[1 + inPos] & 0xFFFF & 0xFF);
            out[4 + outPos] = (byte)((in[2 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[5 + outPos] = (byte)(in[2 + inPos] & 0xFFFF & 0xFF);
            out[6 + outPos] = (byte)((in[3 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[7 + outPos] = (byte)(in[3 + inPos] & 0xFFFF & 0xFF);
            out[8 + outPos] = (byte)((in[4 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[9 + outPos] = (byte)(in[4 + inPos] & 0xFFFF & 0xFF);
            out[10 + outPos] = (byte)((in[5 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[11 + outPos] = (byte)(in[5 + inPos] & 0xFFFF & 0xFF);
            out[12 + outPos] = (byte)((in[6 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[13 + outPos] = (byte)(in[6 + inPos] & 0xFFFF & 0xFF);
            out[14 + outPos] = (byte)((in[7 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[15 + outPos] = (byte)(in[7 + inPos] & 0xFFFF & 0xFF);
            out[16 + outPos] = (byte)((in[8 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[17 + outPos] = (byte)(in[8 + inPos] & 0xFFFF & 0xFF);
            out[18 + outPos] = (byte)((in[9 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[19 + outPos] = (byte)(in[9 + inPos] & 0xFFFF & 0xFF);
            out[20 + outPos] = (byte)((in[10 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[21 + outPos] = (byte)(in[10 + inPos] & 0xFFFF & 0xFF);
            out[22 + outPos] = (byte)((in[11 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[23 + outPos] = (byte)(in[11 + inPos] & 0xFFFF & 0xFF);
            out[24 + outPos] = (byte)((in[12 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[25 + outPos] = (byte)(in[12 + inPos] & 0xFFFF & 0xFF);
            out[26 + outPos] = (byte)((in[13 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[27 + outPos] = (byte)(in[13 + inPos] & 0xFFFF & 0xFF);
            out[28 + outPos] = (byte)((in[14 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[29 + outPos] = (byte)(in[14 + inPos] & 0xFFFF & 0xFF);
            out[30 + outPos] = (byte)((in[15 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[31 + outPos] = (byte)(in[15 + inPos] & 0xFFFF & 0xFF);
            out[32 + outPos] = (byte)((in[16 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[33 + outPos] = (byte)(in[16 + inPos] & 0xFFFF & 0xFF);
            out[34 + outPos] = (byte)((in[17 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[35 + outPos] = (byte)(in[17 + inPos] & 0xFFFF & 0xFF);
            out[36 + outPos] = (byte)((in[18 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[37 + outPos] = (byte)(in[18 + inPos] & 0xFFFF & 0xFF);
            out[38 + outPos] = (byte)((in[19 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[39 + outPos] = (byte)(in[19 + inPos] & 0xFFFF & 0xFF);
            out[40 + outPos] = (byte)((in[20 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[41 + outPos] = (byte)(in[20 + inPos] & 0xFFFF & 0xFF);
            out[42 + outPos] = (byte)((in[21 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[43 + outPos] = (byte)(in[21 + inPos] & 0xFFFF & 0xFF);
            out[44 + outPos] = (byte)((in[22 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[45 + outPos] = (byte)(in[22 + inPos] & 0xFFFF & 0xFF);
            out[46 + outPos] = (byte)((in[23 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[47 + outPos] = (byte)(in[23 + inPos] & 0xFFFF & 0xFF);
            out[48 + outPos] = (byte)((in[24 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[49 + outPos] = (byte)(in[24 + inPos] & 0xFFFF & 0xFF);
            out[50 + outPos] = (byte)((in[25 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[51 + outPos] = (byte)(in[25 + inPos] & 0xFFFF & 0xFF);
            out[52 + outPos] = (byte)((in[26 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[53 + outPos] = (byte)(in[26 + inPos] & 0xFFFF & 0xFF);
            out[54 + outPos] = (byte)((in[27 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[55 + outPos] = (byte)(in[27 + inPos] & 0xFFFF & 0xFF);
            out[56 + outPos] = (byte)((in[28 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[57 + outPos] = (byte)(in[28 + inPos] & 0xFFFF & 0xFF);
            out[58 + outPos] = (byte)((in[29 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[59 + outPos] = (byte)(in[29 + inPos] & 0xFFFF & 0xFF);
            out[60 + outPos] = (byte)((in[30 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[61 + outPos] = (byte)(in[30 + inPos] & 0xFFFF & 0xFF);
            out[62 + outPos] = (byte)((in[31 + inPos] & 0xFFFF) >>> 8 & 0xFF);
            out[63 + outPos] = (byte)(in[31 + inPos] & 0xFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[1 + inPos] & 0xFF & 0xFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[3 + inPos] & 0xFF & 0xFFFF));
            out[2 + outPos] = (((in[4 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[5 + inPos] & 0xFF & 0xFFFF));
            out[3 + outPos] = (((in[6 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[7 + inPos] & 0xFF & 0xFFFF));
            out[4 + outPos] = (((in[8 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[9 + inPos] & 0xFF & 0xFFFF));
            out[5 + outPos] = (((in[10 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[11 + inPos] & 0xFF & 0xFFFF));
            out[6 + outPos] = (((in[12 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[13 + inPos] & 0xFF & 0xFFFF));
            out[7 + outPos] = (((in[14 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[15 + inPos] & 0xFF & 0xFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[1 + inPos] & 0xFF & 0xFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[3 + inPos] & 0xFF & 0xFFFF));
            out[2 + outPos] = (((in[4 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[5 + inPos] & 0xFF & 0xFFFF));
            out[3 + outPos] = (((in[6 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[7 + inPos] & 0xFF & 0xFFFF));
            out[4 + outPos] = (((in[8 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[9 + inPos] & 0xFF & 0xFFFF));
            out[5 + outPos] = (((in[10 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[11 + inPos] & 0xFF & 0xFFFF));
            out[6 + outPos] = (((in[12 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[13 + inPos] & 0xFF & 0xFFFF));
            out[7 + outPos] = (((in[14 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[15 + inPos] & 0xFF & 0xFFFF));
            out[8 + outPos] = (((in[16 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[17 + inPos] & 0xFF & 0xFFFF));
            out[9 + outPos] = (((in[18 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[19 + inPos] & 0xFF & 0xFFFF));
            out[10 + outPos] = (((in[20 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[21 + inPos] & 0xFF & 0xFFFF));
            out[11 + outPos] = (((in[22 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[23 + inPos] & 0xFF & 0xFFFF));
            out[12 + outPos] = (((in[24 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[25 + inPos] & 0xFF & 0xFFFF));
            out[13 + outPos] = (((in[26 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[27 + inPos] & 0xFF & 0xFFFF));
            out[14 + outPos] = (((in[28 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[29 + inPos] & 0xFF & 0xFFFF));
            out[15 + outPos] = (((in[30 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[31 + inPos] & 0xFF & 0xFFFF));
            out[16 + outPos] = (((in[32 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[33 + inPos] & 0xFF & 0xFFFF));
            out[17 + outPos] = (((in[34 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[35 + inPos] & 0xFF & 0xFFFF));
            out[18 + outPos] = (((in[36 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[37 + inPos] & 0xFF & 0xFFFF));
            out[19 + outPos] = (((in[38 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[39 + inPos] & 0xFF & 0xFFFF));
            out[20 + outPos] = (((in[40 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[41 + inPos] & 0xFF & 0xFFFF));
            out[21 + outPos] = (((in[42 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[43 + inPos] & 0xFF & 0xFFFF));
            out[22 + outPos] = (((in[44 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[45 + inPos] & 0xFF & 0xFFFF));
            out[23 + outPos] = (((in[46 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[47 + inPos] & 0xFF & 0xFFFF));
            out[24 + outPos] = (((in[48 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[49 + inPos] & 0xFF & 0xFFFF));
            out[25 + outPos] = (((in[50 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[51 + inPos] & 0xFF & 0xFFFF));
            out[26 + outPos] = (((in[52 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[53 + inPos] & 0xFF & 0xFFFF));
            out[27 + outPos] = (((in[54 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[55 + inPos] & 0xFF & 0xFFFF));
            out[28 + outPos] = (((in[56 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[57 + inPos] & 0xFF & 0xFFFF));
            out[29 + outPos] = (((in[58 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[59 + inPos] & 0xFF & 0xFFFF));
            out[30 + outPos] = (((in[60 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[61 + inPos] & 0xFF & 0xFFFF));
            out[31 + outPos] = (((in[62 + inPos] & 0xFF) << 8 & 0xFFFF) | (in[63 + inPos] & 0xFF & 0xFFFF));
        }
    }
    
    private static final class Packer17 extends BytePacker
    {
        private Packer17() {
            super(17);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFFF) >>> 9 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x1FFFF) >>> 1 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x1FFFF) << 7 | (in[1 + inPos] & 0x1FFFF) >>> 10) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x1FFFF) >>> 2 & 0xFF);
            out[4 + outPos] = (byte)(((in[1 + inPos] & 0x1FFFF) << 6 | (in[2 + inPos] & 0x1FFFF) >>> 11) & 0xFF);
            out[5 + outPos] = (byte)((in[2 + inPos] & 0x1FFFF) >>> 3 & 0xFF);
            out[6 + outPos] = (byte)(((in[2 + inPos] & 0x1FFFF) << 5 | (in[3 + inPos] & 0x1FFFF) >>> 12) & 0xFF);
            out[7 + outPos] = (byte)((in[3 + inPos] & 0x1FFFF) >>> 4 & 0xFF);
            out[8 + outPos] = (byte)(((in[3 + inPos] & 0x1FFFF) << 4 | (in[4 + inPos] & 0x1FFFF) >>> 13) & 0xFF);
            out[9 + outPos] = (byte)((in[4 + inPos] & 0x1FFFF) >>> 5 & 0xFF);
            out[10 + outPos] = (byte)(((in[4 + inPos] & 0x1FFFF) << 3 | (in[5 + inPos] & 0x1FFFF) >>> 14) & 0xFF);
            out[11 + outPos] = (byte)((in[5 + inPos] & 0x1FFFF) >>> 6 & 0xFF);
            out[12 + outPos] = (byte)(((in[5 + inPos] & 0x1FFFF) << 2 | (in[6 + inPos] & 0x1FFFF) >>> 15) & 0xFF);
            out[13 + outPos] = (byte)((in[6 + inPos] & 0x1FFFF) >>> 7 & 0xFF);
            out[14 + outPos] = (byte)(((in[6 + inPos] & 0x1FFFF) << 1 | (in[7 + inPos] & 0x1FFFF) >>> 16) & 0xFF);
            out[15 + outPos] = (byte)((in[7 + inPos] & 0x1FFFF) >>> 8 & 0xFF);
            out[16 + outPos] = (byte)(in[7 + inPos] & 0x1FFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFFF) >>> 9 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x1FFFF) >>> 1 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x1FFFF) << 7 | (in[1 + inPos] & 0x1FFFF) >>> 10) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x1FFFF) >>> 2 & 0xFF);
            out[4 + outPos] = (byte)(((in[1 + inPos] & 0x1FFFF) << 6 | (in[2 + inPos] & 0x1FFFF) >>> 11) & 0xFF);
            out[5 + outPos] = (byte)((in[2 + inPos] & 0x1FFFF) >>> 3 & 0xFF);
            out[6 + outPos] = (byte)(((in[2 + inPos] & 0x1FFFF) << 5 | (in[3 + inPos] & 0x1FFFF) >>> 12) & 0xFF);
            out[7 + outPos] = (byte)((in[3 + inPos] & 0x1FFFF) >>> 4 & 0xFF);
            out[8 + outPos] = (byte)(((in[3 + inPos] & 0x1FFFF) << 4 | (in[4 + inPos] & 0x1FFFF) >>> 13) & 0xFF);
            out[9 + outPos] = (byte)((in[4 + inPos] & 0x1FFFF) >>> 5 & 0xFF);
            out[10 + outPos] = (byte)(((in[4 + inPos] & 0x1FFFF) << 3 | (in[5 + inPos] & 0x1FFFF) >>> 14) & 0xFF);
            out[11 + outPos] = (byte)((in[5 + inPos] & 0x1FFFF) >>> 6 & 0xFF);
            out[12 + outPos] = (byte)(((in[5 + inPos] & 0x1FFFF) << 2 | (in[6 + inPos] & 0x1FFFF) >>> 15) & 0xFF);
            out[13 + outPos] = (byte)((in[6 + inPos] & 0x1FFFF) >>> 7 & 0xFF);
            out[14 + outPos] = (byte)(((in[6 + inPos] & 0x1FFFF) << 1 | (in[7 + inPos] & 0x1FFFF) >>> 16) & 0xFF);
            out[15 + outPos] = (byte)((in[7 + inPos] & 0x1FFFF) >>> 8 & 0xFF);
            out[16 + outPos] = (byte)(in[7 + inPos] & 0x1FFFF & 0xFF);
            out[17 + outPos] = (byte)((in[8 + inPos] & 0x1FFFF) >>> 9 & 0xFF);
            out[18 + outPos] = (byte)((in[8 + inPos] & 0x1FFFF) >>> 1 & 0xFF);
            out[19 + outPos] = (byte)(((in[8 + inPos] & 0x1FFFF) << 7 | (in[9 + inPos] & 0x1FFFF) >>> 10) & 0xFF);
            out[20 + outPos] = (byte)((in[9 + inPos] & 0x1FFFF) >>> 2 & 0xFF);
            out[21 + outPos] = (byte)(((in[9 + inPos] & 0x1FFFF) << 6 | (in[10 + inPos] & 0x1FFFF) >>> 11) & 0xFF);
            out[22 + outPos] = (byte)((in[10 + inPos] & 0x1FFFF) >>> 3 & 0xFF);
            out[23 + outPos] = (byte)(((in[10 + inPos] & 0x1FFFF) << 5 | (in[11 + inPos] & 0x1FFFF) >>> 12) & 0xFF);
            out[24 + outPos] = (byte)((in[11 + inPos] & 0x1FFFF) >>> 4 & 0xFF);
            out[25 + outPos] = (byte)(((in[11 + inPos] & 0x1FFFF) << 4 | (in[12 + inPos] & 0x1FFFF) >>> 13) & 0xFF);
            out[26 + outPos] = (byte)((in[12 + inPos] & 0x1FFFF) >>> 5 & 0xFF);
            out[27 + outPos] = (byte)(((in[12 + inPos] & 0x1FFFF) << 3 | (in[13 + inPos] & 0x1FFFF) >>> 14) & 0xFF);
            out[28 + outPos] = (byte)((in[13 + inPos] & 0x1FFFF) >>> 6 & 0xFF);
            out[29 + outPos] = (byte)(((in[13 + inPos] & 0x1FFFF) << 2 | (in[14 + inPos] & 0x1FFFF) >>> 15) & 0xFF);
            out[30 + outPos] = (byte)((in[14 + inPos] & 0x1FFFF) >>> 7 & 0xFF);
            out[31 + outPos] = (byte)(((in[14 + inPos] & 0x1FFFF) << 1 | (in[15 + inPos] & 0x1FFFF) >>> 16) & 0xFF);
            out[32 + outPos] = (byte)((in[15 + inPos] & 0x1FFFF) >>> 8 & 0xFF);
            out[33 + outPos] = (byte)(in[15 + inPos] & 0x1FFFF & 0xFF);
            out[34 + outPos] = (byte)((in[16 + inPos] & 0x1FFFF) >>> 9 & 0xFF);
            out[35 + outPos] = (byte)((in[16 + inPos] & 0x1FFFF) >>> 1 & 0xFF);
            out[36 + outPos] = (byte)(((in[16 + inPos] & 0x1FFFF) << 7 | (in[17 + inPos] & 0x1FFFF) >>> 10) & 0xFF);
            out[37 + outPos] = (byte)((in[17 + inPos] & 0x1FFFF) >>> 2 & 0xFF);
            out[38 + outPos] = (byte)(((in[17 + inPos] & 0x1FFFF) << 6 | (in[18 + inPos] & 0x1FFFF) >>> 11) & 0xFF);
            out[39 + outPos] = (byte)((in[18 + inPos] & 0x1FFFF) >>> 3 & 0xFF);
            out[40 + outPos] = (byte)(((in[18 + inPos] & 0x1FFFF) << 5 | (in[19 + inPos] & 0x1FFFF) >>> 12) & 0xFF);
            out[41 + outPos] = (byte)((in[19 + inPos] & 0x1FFFF) >>> 4 & 0xFF);
            out[42 + outPos] = (byte)(((in[19 + inPos] & 0x1FFFF) << 4 | (in[20 + inPos] & 0x1FFFF) >>> 13) & 0xFF);
            out[43 + outPos] = (byte)((in[20 + inPos] & 0x1FFFF) >>> 5 & 0xFF);
            out[44 + outPos] = (byte)(((in[20 + inPos] & 0x1FFFF) << 3 | (in[21 + inPos] & 0x1FFFF) >>> 14) & 0xFF);
            out[45 + outPos] = (byte)((in[21 + inPos] & 0x1FFFF) >>> 6 & 0xFF);
            out[46 + outPos] = (byte)(((in[21 + inPos] & 0x1FFFF) << 2 | (in[22 + inPos] & 0x1FFFF) >>> 15) & 0xFF);
            out[47 + outPos] = (byte)((in[22 + inPos] & 0x1FFFF) >>> 7 & 0xFF);
            out[48 + outPos] = (byte)(((in[22 + inPos] & 0x1FFFF) << 1 | (in[23 + inPos] & 0x1FFFF) >>> 16) & 0xFF);
            out[49 + outPos] = (byte)((in[23 + inPos] & 0x1FFFF) >>> 8 & 0xFF);
            out[50 + outPos] = (byte)(in[23 + inPos] & 0x1FFFF & 0xFF);
            out[51 + outPos] = (byte)((in[24 + inPos] & 0x1FFFF) >>> 9 & 0xFF);
            out[52 + outPos] = (byte)((in[24 + inPos] & 0x1FFFF) >>> 1 & 0xFF);
            out[53 + outPos] = (byte)(((in[24 + inPos] & 0x1FFFF) << 7 | (in[25 + inPos] & 0x1FFFF) >>> 10) & 0xFF);
            out[54 + outPos] = (byte)((in[25 + inPos] & 0x1FFFF) >>> 2 & 0xFF);
            out[55 + outPos] = (byte)(((in[25 + inPos] & 0x1FFFF) << 6 | (in[26 + inPos] & 0x1FFFF) >>> 11) & 0xFF);
            out[56 + outPos] = (byte)((in[26 + inPos] & 0x1FFFF) >>> 3 & 0xFF);
            out[57 + outPos] = (byte)(((in[26 + inPos] & 0x1FFFF) << 5 | (in[27 + inPos] & 0x1FFFF) >>> 12) & 0xFF);
            out[58 + outPos] = (byte)((in[27 + inPos] & 0x1FFFF) >>> 4 & 0xFF);
            out[59 + outPos] = (byte)(((in[27 + inPos] & 0x1FFFF) << 4 | (in[28 + inPos] & 0x1FFFF) >>> 13) & 0xFF);
            out[60 + outPos] = (byte)((in[28 + inPos] & 0x1FFFF) >>> 5 & 0xFF);
            out[61 + outPos] = (byte)(((in[28 + inPos] & 0x1FFFF) << 3 | (in[29 + inPos] & 0x1FFFF) >>> 14) & 0xFF);
            out[62 + outPos] = (byte)((in[29 + inPos] & 0x1FFFF) >>> 6 & 0xFF);
            out[63 + outPos] = (byte)(((in[29 + inPos] & 0x1FFFF) << 2 | (in[30 + inPos] & 0x1FFFF) >>> 15) & 0xFF);
            out[64 + outPos] = (byte)((in[30 + inPos] & 0x1FFFF) >>> 7 & 0xFF);
            out[65 + outPos] = (byte)(((in[30 + inPos] & 0x1FFFF) << 1 | (in[31 + inPos] & 0x1FFFF) >>> 16) & 0xFF);
            out[66 + outPos] = (byte)((in[31 + inPos] & 0x1FFFF) >>> 8 & 0xFF);
            out[67 + outPos] = (byte)(in[31 + inPos] & 0x1FFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 9 & 0x1FFFF) | ((in[1 + inPos] & 0xFF) << 1 & 0x1FFFF) | ((in[2 + inPos] & 0xFF) >>> 7 & 0x1FFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 10 & 0x1FFFF) | ((in[3 + inPos] & 0xFF) << 2 & 0x1FFFF) | ((in[4 + inPos] & 0xFF) >>> 6 & 0x1FFFF));
            out[2 + outPos] = (((in[4 + inPos] & 0xFF) << 11 & 0x1FFFF) | ((in[5 + inPos] & 0xFF) << 3 & 0x1FFFF) | ((in[6 + inPos] & 0xFF) >>> 5 & 0x1FFFF));
            out[3 + outPos] = (((in[6 + inPos] & 0xFF) << 12 & 0x1FFFF) | ((in[7 + inPos] & 0xFF) << 4 & 0x1FFFF) | ((in[8 + inPos] & 0xFF) >>> 4 & 0x1FFFF));
            out[4 + outPos] = (((in[8 + inPos] & 0xFF) << 13 & 0x1FFFF) | ((in[9 + inPos] & 0xFF) << 5 & 0x1FFFF) | ((in[10 + inPos] & 0xFF) >>> 3 & 0x1FFFF));
            out[5 + outPos] = (((in[10 + inPos] & 0xFF) << 14 & 0x1FFFF) | ((in[11 + inPos] & 0xFF) << 6 & 0x1FFFF) | ((in[12 + inPos] & 0xFF) >>> 2 & 0x1FFFF));
            out[6 + outPos] = (((in[12 + inPos] & 0xFF) << 15 & 0x1FFFF) | ((in[13 + inPos] & 0xFF) << 7 & 0x1FFFF) | ((in[14 + inPos] & 0xFF) >>> 1 & 0x1FFFF));
            out[7 + outPos] = (((in[14 + inPos] & 0xFF) << 16 & 0x1FFFF) | ((in[15 + inPos] & 0xFF) << 8 & 0x1FFFF) | (in[16 + inPos] & 0xFF & 0x1FFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 9 & 0x1FFFF) | ((in[1 + inPos] & 0xFF) << 1 & 0x1FFFF) | ((in[2 + inPos] & 0xFF) >>> 7 & 0x1FFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 10 & 0x1FFFF) | ((in[3 + inPos] & 0xFF) << 2 & 0x1FFFF) | ((in[4 + inPos] & 0xFF) >>> 6 & 0x1FFFF));
            out[2 + outPos] = (((in[4 + inPos] & 0xFF) << 11 & 0x1FFFF) | ((in[5 + inPos] & 0xFF) << 3 & 0x1FFFF) | ((in[6 + inPos] & 0xFF) >>> 5 & 0x1FFFF));
            out[3 + outPos] = (((in[6 + inPos] & 0xFF) << 12 & 0x1FFFF) | ((in[7 + inPos] & 0xFF) << 4 & 0x1FFFF) | ((in[8 + inPos] & 0xFF) >>> 4 & 0x1FFFF));
            out[4 + outPos] = (((in[8 + inPos] & 0xFF) << 13 & 0x1FFFF) | ((in[9 + inPos] & 0xFF) << 5 & 0x1FFFF) | ((in[10 + inPos] & 0xFF) >>> 3 & 0x1FFFF));
            out[5 + outPos] = (((in[10 + inPos] & 0xFF) << 14 & 0x1FFFF) | ((in[11 + inPos] & 0xFF) << 6 & 0x1FFFF) | ((in[12 + inPos] & 0xFF) >>> 2 & 0x1FFFF));
            out[6 + outPos] = (((in[12 + inPos] & 0xFF) << 15 & 0x1FFFF) | ((in[13 + inPos] & 0xFF) << 7 & 0x1FFFF) | ((in[14 + inPos] & 0xFF) >>> 1 & 0x1FFFF));
            out[7 + outPos] = (((in[14 + inPos] & 0xFF) << 16 & 0x1FFFF) | ((in[15 + inPos] & 0xFF) << 8 & 0x1FFFF) | (in[16 + inPos] & 0xFF & 0x1FFFF));
            out[8 + outPos] = (((in[17 + inPos] & 0xFF) << 9 & 0x1FFFF) | ((in[18 + inPos] & 0xFF) << 1 & 0x1FFFF) | ((in[19 + inPos] & 0xFF) >>> 7 & 0x1FFFF));
            out[9 + outPos] = (((in[19 + inPos] & 0xFF) << 10 & 0x1FFFF) | ((in[20 + inPos] & 0xFF) << 2 & 0x1FFFF) | ((in[21 + inPos] & 0xFF) >>> 6 & 0x1FFFF));
            out[10 + outPos] = (((in[21 + inPos] & 0xFF) << 11 & 0x1FFFF) | ((in[22 + inPos] & 0xFF) << 3 & 0x1FFFF) | ((in[23 + inPos] & 0xFF) >>> 5 & 0x1FFFF));
            out[11 + outPos] = (((in[23 + inPos] & 0xFF) << 12 & 0x1FFFF) | ((in[24 + inPos] & 0xFF) << 4 & 0x1FFFF) | ((in[25 + inPos] & 0xFF) >>> 4 & 0x1FFFF));
            out[12 + outPos] = (((in[25 + inPos] & 0xFF) << 13 & 0x1FFFF) | ((in[26 + inPos] & 0xFF) << 5 & 0x1FFFF) | ((in[27 + inPos] & 0xFF) >>> 3 & 0x1FFFF));
            out[13 + outPos] = (((in[27 + inPos] & 0xFF) << 14 & 0x1FFFF) | ((in[28 + inPos] & 0xFF) << 6 & 0x1FFFF) | ((in[29 + inPos] & 0xFF) >>> 2 & 0x1FFFF));
            out[14 + outPos] = (((in[29 + inPos] & 0xFF) << 15 & 0x1FFFF) | ((in[30 + inPos] & 0xFF) << 7 & 0x1FFFF) | ((in[31 + inPos] & 0xFF) >>> 1 & 0x1FFFF));
            out[15 + outPos] = (((in[31 + inPos] & 0xFF) << 16 & 0x1FFFF) | ((in[32 + inPos] & 0xFF) << 8 & 0x1FFFF) | (in[33 + inPos] & 0xFF & 0x1FFFF));
            out[16 + outPos] = (((in[34 + inPos] & 0xFF) << 9 & 0x1FFFF) | ((in[35 + inPos] & 0xFF) << 1 & 0x1FFFF) | ((in[36 + inPos] & 0xFF) >>> 7 & 0x1FFFF));
            out[17 + outPos] = (((in[36 + inPos] & 0xFF) << 10 & 0x1FFFF) | ((in[37 + inPos] & 0xFF) << 2 & 0x1FFFF) | ((in[38 + inPos] & 0xFF) >>> 6 & 0x1FFFF));
            out[18 + outPos] = (((in[38 + inPos] & 0xFF) << 11 & 0x1FFFF) | ((in[39 + inPos] & 0xFF) << 3 & 0x1FFFF) | ((in[40 + inPos] & 0xFF) >>> 5 & 0x1FFFF));
            out[19 + outPos] = (((in[40 + inPos] & 0xFF) << 12 & 0x1FFFF) | ((in[41 + inPos] & 0xFF) << 4 & 0x1FFFF) | ((in[42 + inPos] & 0xFF) >>> 4 & 0x1FFFF));
            out[20 + outPos] = (((in[42 + inPos] & 0xFF) << 13 & 0x1FFFF) | ((in[43 + inPos] & 0xFF) << 5 & 0x1FFFF) | ((in[44 + inPos] & 0xFF) >>> 3 & 0x1FFFF));
            out[21 + outPos] = (((in[44 + inPos] & 0xFF) << 14 & 0x1FFFF) | ((in[45 + inPos] & 0xFF) << 6 & 0x1FFFF) | ((in[46 + inPos] & 0xFF) >>> 2 & 0x1FFFF));
            out[22 + outPos] = (((in[46 + inPos] & 0xFF) << 15 & 0x1FFFF) | ((in[47 + inPos] & 0xFF) << 7 & 0x1FFFF) | ((in[48 + inPos] & 0xFF) >>> 1 & 0x1FFFF));
            out[23 + outPos] = (((in[48 + inPos] & 0xFF) << 16 & 0x1FFFF) | ((in[49 + inPos] & 0xFF) << 8 & 0x1FFFF) | (in[50 + inPos] & 0xFF & 0x1FFFF));
            out[24 + outPos] = (((in[51 + inPos] & 0xFF) << 9 & 0x1FFFF) | ((in[52 + inPos] & 0xFF) << 1 & 0x1FFFF) | ((in[53 + inPos] & 0xFF) >>> 7 & 0x1FFFF));
            out[25 + outPos] = (((in[53 + inPos] & 0xFF) << 10 & 0x1FFFF) | ((in[54 + inPos] & 0xFF) << 2 & 0x1FFFF) | ((in[55 + inPos] & 0xFF) >>> 6 & 0x1FFFF));
            out[26 + outPos] = (((in[55 + inPos] & 0xFF) << 11 & 0x1FFFF) | ((in[56 + inPos] & 0xFF) << 3 & 0x1FFFF) | ((in[57 + inPos] & 0xFF) >>> 5 & 0x1FFFF));
            out[27 + outPos] = (((in[57 + inPos] & 0xFF) << 12 & 0x1FFFF) | ((in[58 + inPos] & 0xFF) << 4 & 0x1FFFF) | ((in[59 + inPos] & 0xFF) >>> 4 & 0x1FFFF));
            out[28 + outPos] = (((in[59 + inPos] & 0xFF) << 13 & 0x1FFFF) | ((in[60 + inPos] & 0xFF) << 5 & 0x1FFFF) | ((in[61 + inPos] & 0xFF) >>> 3 & 0x1FFFF));
            out[29 + outPos] = (((in[61 + inPos] & 0xFF) << 14 & 0x1FFFF) | ((in[62 + inPos] & 0xFF) << 6 & 0x1FFFF) | ((in[63 + inPos] & 0xFF) >>> 2 & 0x1FFFF));
            out[30 + outPos] = (((in[63 + inPos] & 0xFF) << 15 & 0x1FFFF) | ((in[64 + inPos] & 0xFF) << 7 & 0x1FFFF) | ((in[65 + inPos] & 0xFF) >>> 1 & 0x1FFFF));
            out[31 + outPos] = (((in[65 + inPos] & 0xFF) << 16 & 0x1FFFF) | ((in[66 + inPos] & 0xFF) << 8 & 0x1FFFF) | (in[67 + inPos] & 0xFF & 0x1FFFF));
        }
    }
    
    private static final class Packer18 extends BytePacker
    {
        private Packer18() {
            super(18);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x3FFFF) << 6 | (in[1 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[4 + outPos] = (byte)(((in[1 + inPos] & 0x3FFFF) << 4 | (in[2 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[5 + outPos] = (byte)((in[2 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[6 + outPos] = (byte)(((in[2 + inPos] & 0x3FFFF) << 2 | (in[3 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[7 + outPos] = (byte)((in[3 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[8 + outPos] = (byte)(in[3 + inPos] & 0x3FFFF & 0xFF);
            out[9 + outPos] = (byte)((in[4 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[10 + outPos] = (byte)((in[4 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[11 + outPos] = (byte)(((in[4 + inPos] & 0x3FFFF) << 6 | (in[5 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[12 + outPos] = (byte)((in[5 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[13 + outPos] = (byte)(((in[5 + inPos] & 0x3FFFF) << 4 | (in[6 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[14 + outPos] = (byte)((in[6 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[15 + outPos] = (byte)(((in[6 + inPos] & 0x3FFFF) << 2 | (in[7 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[16 + outPos] = (byte)((in[7 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[17 + outPos] = (byte)(in[7 + inPos] & 0x3FFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x3FFFF) << 6 | (in[1 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[4 + outPos] = (byte)(((in[1 + inPos] & 0x3FFFF) << 4 | (in[2 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[5 + outPos] = (byte)((in[2 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[6 + outPos] = (byte)(((in[2 + inPos] & 0x3FFFF) << 2 | (in[3 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[7 + outPos] = (byte)((in[3 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[8 + outPos] = (byte)(in[3 + inPos] & 0x3FFFF & 0xFF);
            out[9 + outPos] = (byte)((in[4 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[10 + outPos] = (byte)((in[4 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[11 + outPos] = (byte)(((in[4 + inPos] & 0x3FFFF) << 6 | (in[5 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[12 + outPos] = (byte)((in[5 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[13 + outPos] = (byte)(((in[5 + inPos] & 0x3FFFF) << 4 | (in[6 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[14 + outPos] = (byte)((in[6 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[15 + outPos] = (byte)(((in[6 + inPos] & 0x3FFFF) << 2 | (in[7 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[16 + outPos] = (byte)((in[7 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[17 + outPos] = (byte)(in[7 + inPos] & 0x3FFFF & 0xFF);
            out[18 + outPos] = (byte)((in[8 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[19 + outPos] = (byte)((in[8 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[20 + outPos] = (byte)(((in[8 + inPos] & 0x3FFFF) << 6 | (in[9 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[21 + outPos] = (byte)((in[9 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[22 + outPos] = (byte)(((in[9 + inPos] & 0x3FFFF) << 4 | (in[10 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[23 + outPos] = (byte)((in[10 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[24 + outPos] = (byte)(((in[10 + inPos] & 0x3FFFF) << 2 | (in[11 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[25 + outPos] = (byte)((in[11 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[26 + outPos] = (byte)(in[11 + inPos] & 0x3FFFF & 0xFF);
            out[27 + outPos] = (byte)((in[12 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[28 + outPos] = (byte)((in[12 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[29 + outPos] = (byte)(((in[12 + inPos] & 0x3FFFF) << 6 | (in[13 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[30 + outPos] = (byte)((in[13 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[31 + outPos] = (byte)(((in[13 + inPos] & 0x3FFFF) << 4 | (in[14 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[32 + outPos] = (byte)((in[14 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[33 + outPos] = (byte)(((in[14 + inPos] & 0x3FFFF) << 2 | (in[15 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[34 + outPos] = (byte)((in[15 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[35 + outPos] = (byte)(in[15 + inPos] & 0x3FFFF & 0xFF);
            out[36 + outPos] = (byte)((in[16 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[37 + outPos] = (byte)((in[16 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[38 + outPos] = (byte)(((in[16 + inPos] & 0x3FFFF) << 6 | (in[17 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[39 + outPos] = (byte)((in[17 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[40 + outPos] = (byte)(((in[17 + inPos] & 0x3FFFF) << 4 | (in[18 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[41 + outPos] = (byte)((in[18 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[42 + outPos] = (byte)(((in[18 + inPos] & 0x3FFFF) << 2 | (in[19 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[43 + outPos] = (byte)((in[19 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[44 + outPos] = (byte)(in[19 + inPos] & 0x3FFFF & 0xFF);
            out[45 + outPos] = (byte)((in[20 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[46 + outPos] = (byte)((in[20 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[47 + outPos] = (byte)(((in[20 + inPos] & 0x3FFFF) << 6 | (in[21 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[48 + outPos] = (byte)((in[21 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[49 + outPos] = (byte)(((in[21 + inPos] & 0x3FFFF) << 4 | (in[22 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[50 + outPos] = (byte)((in[22 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[51 + outPos] = (byte)(((in[22 + inPos] & 0x3FFFF) << 2 | (in[23 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[52 + outPos] = (byte)((in[23 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[53 + outPos] = (byte)(in[23 + inPos] & 0x3FFFF & 0xFF);
            out[54 + outPos] = (byte)((in[24 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[55 + outPos] = (byte)((in[24 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[56 + outPos] = (byte)(((in[24 + inPos] & 0x3FFFF) << 6 | (in[25 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[57 + outPos] = (byte)((in[25 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[58 + outPos] = (byte)(((in[25 + inPos] & 0x3FFFF) << 4 | (in[26 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[59 + outPos] = (byte)((in[26 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[60 + outPos] = (byte)(((in[26 + inPos] & 0x3FFFF) << 2 | (in[27 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[61 + outPos] = (byte)((in[27 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[62 + outPos] = (byte)(in[27 + inPos] & 0x3FFFF & 0xFF);
            out[63 + outPos] = (byte)((in[28 + inPos] & 0x3FFFF) >>> 10 & 0xFF);
            out[64 + outPos] = (byte)((in[28 + inPos] & 0x3FFFF) >>> 2 & 0xFF);
            out[65 + outPos] = (byte)(((in[28 + inPos] & 0x3FFFF) << 6 | (in[29 + inPos] & 0x3FFFF) >>> 12) & 0xFF);
            out[66 + outPos] = (byte)((in[29 + inPos] & 0x3FFFF) >>> 4 & 0xFF);
            out[67 + outPos] = (byte)(((in[29 + inPos] & 0x3FFFF) << 4 | (in[30 + inPos] & 0x3FFFF) >>> 14) & 0xFF);
            out[68 + outPos] = (byte)((in[30 + inPos] & 0x3FFFF) >>> 6 & 0xFF);
            out[69 + outPos] = (byte)(((in[30 + inPos] & 0x3FFFF) << 2 | (in[31 + inPos] & 0x3FFFF) >>> 16) & 0xFF);
            out[70 + outPos] = (byte)((in[31 + inPos] & 0x3FFFF) >>> 8 & 0xFF);
            out[71 + outPos] = (byte)(in[31 + inPos] & 0x3FFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[1 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[2 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[3 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[4 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[2 + outPos] = (((in[4 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[5 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[6 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[3 + outPos] = (((in[6 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[7 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[8 + inPos] & 0xFF & 0x3FFFF));
            out[4 + outPos] = (((in[9 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[10 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[5 + outPos] = (((in[11 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[12 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[13 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[6 + outPos] = (((in[13 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[14 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[15 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[7 + outPos] = (((in[15 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[16 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[17 + inPos] & 0xFF & 0x3FFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[1 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[2 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[3 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[4 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[2 + outPos] = (((in[4 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[5 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[6 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[3 + outPos] = (((in[6 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[7 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[8 + inPos] & 0xFF & 0x3FFFF));
            out[4 + outPos] = (((in[9 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[10 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[5 + outPos] = (((in[11 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[12 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[13 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[6 + outPos] = (((in[13 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[14 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[15 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[7 + outPos] = (((in[15 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[16 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[17 + inPos] & 0xFF & 0x3FFFF));
            out[8 + outPos] = (((in[18 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[19 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[20 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[9 + outPos] = (((in[20 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[21 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[22 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[10 + outPos] = (((in[22 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[23 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[24 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[11 + outPos] = (((in[24 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[25 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[26 + inPos] & 0xFF & 0x3FFFF));
            out[12 + outPos] = (((in[27 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[28 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[29 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[13 + outPos] = (((in[29 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[30 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[31 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[14 + outPos] = (((in[31 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[32 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[33 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[15 + outPos] = (((in[33 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[34 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[35 + inPos] & 0xFF & 0x3FFFF));
            out[16 + outPos] = (((in[36 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[37 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[38 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[17 + outPos] = (((in[38 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[39 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[40 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[18 + outPos] = (((in[40 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[41 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[42 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[19 + outPos] = (((in[42 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[43 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[44 + inPos] & 0xFF & 0x3FFFF));
            out[20 + outPos] = (((in[45 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[46 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[47 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[21 + outPos] = (((in[47 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[48 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[49 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[22 + outPos] = (((in[49 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[50 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[51 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[23 + outPos] = (((in[51 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[52 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[53 + inPos] & 0xFF & 0x3FFFF));
            out[24 + outPos] = (((in[54 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[55 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[56 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[25 + outPos] = (((in[56 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[57 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[58 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[26 + outPos] = (((in[58 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[59 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[60 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[27 + outPos] = (((in[60 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[61 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[62 + inPos] & 0xFF & 0x3FFFF));
            out[28 + outPos] = (((in[63 + inPos] & 0xFF) << 10 & 0x3FFFF) | ((in[64 + inPos] & 0xFF) << 2 & 0x3FFFF) | ((in[65 + inPos] & 0xFF) >>> 6 & 0x3FFFF));
            out[29 + outPos] = (((in[65 + inPos] & 0xFF) << 12 & 0x3FFFF) | ((in[66 + inPos] & 0xFF) << 4 & 0x3FFFF) | ((in[67 + inPos] & 0xFF) >>> 4 & 0x3FFFF));
            out[30 + outPos] = (((in[67 + inPos] & 0xFF) << 14 & 0x3FFFF) | ((in[68 + inPos] & 0xFF) << 6 & 0x3FFFF) | ((in[69 + inPos] & 0xFF) >>> 2 & 0x3FFFF));
            out[31 + outPos] = (((in[69 + inPos] & 0xFF) << 16 & 0x3FFFF) | ((in[70 + inPos] & 0xFF) << 8 & 0x3FFFF) | (in[71 + inPos] & 0xFF & 0x3FFFF));
        }
    }
    
    private static final class Packer19 extends BytePacker
    {
        private Packer19() {
            super(19);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FFFF) >>> 11 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x7FFFF) >>> 3 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x7FFFF) << 5 | (in[1 + inPos] & 0x7FFFF) >>> 14) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x7FFFF) >>> 6 & 0xFF);
            out[4 + outPos] = (byte)(((in[1 + inPos] & 0x7FFFF) << 2 | (in[2 + inPos] & 0x7FFFF) >>> 17) & 0xFF);
            out[5 + outPos] = (byte)((in[2 + inPos] & 0x7FFFF) >>> 9 & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0x7FFFF) >>> 1 & 0xFF);
            out[7 + outPos] = (byte)(((in[2 + inPos] & 0x7FFFF) << 7 | (in[3 + inPos] & 0x7FFFF) >>> 12) & 0xFF);
            out[8 + outPos] = (byte)((in[3 + inPos] & 0x7FFFF) >>> 4 & 0xFF);
            out[9 + outPos] = (byte)(((in[3 + inPos] & 0x7FFFF) << 4 | (in[4 + inPos] & 0x7FFFF) >>> 15) & 0xFF);
            out[10 + outPos] = (byte)((in[4 + inPos] & 0x7FFFF) >>> 7 & 0xFF);
            out[11 + outPos] = (byte)(((in[4 + inPos] & 0x7FFFF) << 1 | (in[5 + inPos] & 0x7FFFF) >>> 18) & 0xFF);
            out[12 + outPos] = (byte)((in[5 + inPos] & 0x7FFFF) >>> 10 & 0xFF);
            out[13 + outPos] = (byte)((in[5 + inPos] & 0x7FFFF) >>> 2 & 0xFF);
            out[14 + outPos] = (byte)(((in[5 + inPos] & 0x7FFFF) << 6 | (in[6 + inPos] & 0x7FFFF) >>> 13) & 0xFF);
            out[15 + outPos] = (byte)((in[6 + inPos] & 0x7FFFF) >>> 5 & 0xFF);
            out[16 + outPos] = (byte)(((in[6 + inPos] & 0x7FFFF) << 3 | (in[7 + inPos] & 0x7FFFF) >>> 16) & 0xFF);
            out[17 + outPos] = (byte)((in[7 + inPos] & 0x7FFFF) >>> 8 & 0xFF);
            out[18 + outPos] = (byte)(in[7 + inPos] & 0x7FFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FFFF) >>> 11 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x7FFFF) >>> 3 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x7FFFF) << 5 | (in[1 + inPos] & 0x7FFFF) >>> 14) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x7FFFF) >>> 6 & 0xFF);
            out[4 + outPos] = (byte)(((in[1 + inPos] & 0x7FFFF) << 2 | (in[2 + inPos] & 0x7FFFF) >>> 17) & 0xFF);
            out[5 + outPos] = (byte)((in[2 + inPos] & 0x7FFFF) >>> 9 & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0x7FFFF) >>> 1 & 0xFF);
            out[7 + outPos] = (byte)(((in[2 + inPos] & 0x7FFFF) << 7 | (in[3 + inPos] & 0x7FFFF) >>> 12) & 0xFF);
            out[8 + outPos] = (byte)((in[3 + inPos] & 0x7FFFF) >>> 4 & 0xFF);
            out[9 + outPos] = (byte)(((in[3 + inPos] & 0x7FFFF) << 4 | (in[4 + inPos] & 0x7FFFF) >>> 15) & 0xFF);
            out[10 + outPos] = (byte)((in[4 + inPos] & 0x7FFFF) >>> 7 & 0xFF);
            out[11 + outPos] = (byte)(((in[4 + inPos] & 0x7FFFF) << 1 | (in[5 + inPos] & 0x7FFFF) >>> 18) & 0xFF);
            out[12 + outPos] = (byte)((in[5 + inPos] & 0x7FFFF) >>> 10 & 0xFF);
            out[13 + outPos] = (byte)((in[5 + inPos] & 0x7FFFF) >>> 2 & 0xFF);
            out[14 + outPos] = (byte)(((in[5 + inPos] & 0x7FFFF) << 6 | (in[6 + inPos] & 0x7FFFF) >>> 13) & 0xFF);
            out[15 + outPos] = (byte)((in[6 + inPos] & 0x7FFFF) >>> 5 & 0xFF);
            out[16 + outPos] = (byte)(((in[6 + inPos] & 0x7FFFF) << 3 | (in[7 + inPos] & 0x7FFFF) >>> 16) & 0xFF);
            out[17 + outPos] = (byte)((in[7 + inPos] & 0x7FFFF) >>> 8 & 0xFF);
            out[18 + outPos] = (byte)(in[7 + inPos] & 0x7FFFF & 0xFF);
            out[19 + outPos] = (byte)((in[8 + inPos] & 0x7FFFF) >>> 11 & 0xFF);
            out[20 + outPos] = (byte)((in[8 + inPos] & 0x7FFFF) >>> 3 & 0xFF);
            out[21 + outPos] = (byte)(((in[8 + inPos] & 0x7FFFF) << 5 | (in[9 + inPos] & 0x7FFFF) >>> 14) & 0xFF);
            out[22 + outPos] = (byte)((in[9 + inPos] & 0x7FFFF) >>> 6 & 0xFF);
            out[23 + outPos] = (byte)(((in[9 + inPos] & 0x7FFFF) << 2 | (in[10 + inPos] & 0x7FFFF) >>> 17) & 0xFF);
            out[24 + outPos] = (byte)((in[10 + inPos] & 0x7FFFF) >>> 9 & 0xFF);
            out[25 + outPos] = (byte)((in[10 + inPos] & 0x7FFFF) >>> 1 & 0xFF);
            out[26 + outPos] = (byte)(((in[10 + inPos] & 0x7FFFF) << 7 | (in[11 + inPos] & 0x7FFFF) >>> 12) & 0xFF);
            out[27 + outPos] = (byte)((in[11 + inPos] & 0x7FFFF) >>> 4 & 0xFF);
            out[28 + outPos] = (byte)(((in[11 + inPos] & 0x7FFFF) << 4 | (in[12 + inPos] & 0x7FFFF) >>> 15) & 0xFF);
            out[29 + outPos] = (byte)((in[12 + inPos] & 0x7FFFF) >>> 7 & 0xFF);
            out[30 + outPos] = (byte)(((in[12 + inPos] & 0x7FFFF) << 1 | (in[13 + inPos] & 0x7FFFF) >>> 18) & 0xFF);
            out[31 + outPos] = (byte)((in[13 + inPos] & 0x7FFFF) >>> 10 & 0xFF);
            out[32 + outPos] = (byte)((in[13 + inPos] & 0x7FFFF) >>> 2 & 0xFF);
            out[33 + outPos] = (byte)(((in[13 + inPos] & 0x7FFFF) << 6 | (in[14 + inPos] & 0x7FFFF) >>> 13) & 0xFF);
            out[34 + outPos] = (byte)((in[14 + inPos] & 0x7FFFF) >>> 5 & 0xFF);
            out[35 + outPos] = (byte)(((in[14 + inPos] & 0x7FFFF) << 3 | (in[15 + inPos] & 0x7FFFF) >>> 16) & 0xFF);
            out[36 + outPos] = (byte)((in[15 + inPos] & 0x7FFFF) >>> 8 & 0xFF);
            out[37 + outPos] = (byte)(in[15 + inPos] & 0x7FFFF & 0xFF);
            out[38 + outPos] = (byte)((in[16 + inPos] & 0x7FFFF) >>> 11 & 0xFF);
            out[39 + outPos] = (byte)((in[16 + inPos] & 0x7FFFF) >>> 3 & 0xFF);
            out[40 + outPos] = (byte)(((in[16 + inPos] & 0x7FFFF) << 5 | (in[17 + inPos] & 0x7FFFF) >>> 14) & 0xFF);
            out[41 + outPos] = (byte)((in[17 + inPos] & 0x7FFFF) >>> 6 & 0xFF);
            out[42 + outPos] = (byte)(((in[17 + inPos] & 0x7FFFF) << 2 | (in[18 + inPos] & 0x7FFFF) >>> 17) & 0xFF);
            out[43 + outPos] = (byte)((in[18 + inPos] & 0x7FFFF) >>> 9 & 0xFF);
            out[44 + outPos] = (byte)((in[18 + inPos] & 0x7FFFF) >>> 1 & 0xFF);
            out[45 + outPos] = (byte)(((in[18 + inPos] & 0x7FFFF) << 7 | (in[19 + inPos] & 0x7FFFF) >>> 12) & 0xFF);
            out[46 + outPos] = (byte)((in[19 + inPos] & 0x7FFFF) >>> 4 & 0xFF);
            out[47 + outPos] = (byte)(((in[19 + inPos] & 0x7FFFF) << 4 | (in[20 + inPos] & 0x7FFFF) >>> 15) & 0xFF);
            out[48 + outPos] = (byte)((in[20 + inPos] & 0x7FFFF) >>> 7 & 0xFF);
            out[49 + outPos] = (byte)(((in[20 + inPos] & 0x7FFFF) << 1 | (in[21 + inPos] & 0x7FFFF) >>> 18) & 0xFF);
            out[50 + outPos] = (byte)((in[21 + inPos] & 0x7FFFF) >>> 10 & 0xFF);
            out[51 + outPos] = (byte)((in[21 + inPos] & 0x7FFFF) >>> 2 & 0xFF);
            out[52 + outPos] = (byte)(((in[21 + inPos] & 0x7FFFF) << 6 | (in[22 + inPos] & 0x7FFFF) >>> 13) & 0xFF);
            out[53 + outPos] = (byte)((in[22 + inPos] & 0x7FFFF) >>> 5 & 0xFF);
            out[54 + outPos] = (byte)(((in[22 + inPos] & 0x7FFFF) << 3 | (in[23 + inPos] & 0x7FFFF) >>> 16) & 0xFF);
            out[55 + outPos] = (byte)((in[23 + inPos] & 0x7FFFF) >>> 8 & 0xFF);
            out[56 + outPos] = (byte)(in[23 + inPos] & 0x7FFFF & 0xFF);
            out[57 + outPos] = (byte)((in[24 + inPos] & 0x7FFFF) >>> 11 & 0xFF);
            out[58 + outPos] = (byte)((in[24 + inPos] & 0x7FFFF) >>> 3 & 0xFF);
            out[59 + outPos] = (byte)(((in[24 + inPos] & 0x7FFFF) << 5 | (in[25 + inPos] & 0x7FFFF) >>> 14) & 0xFF);
            out[60 + outPos] = (byte)((in[25 + inPos] & 0x7FFFF) >>> 6 & 0xFF);
            out[61 + outPos] = (byte)(((in[25 + inPos] & 0x7FFFF) << 2 | (in[26 + inPos] & 0x7FFFF) >>> 17) & 0xFF);
            out[62 + outPos] = (byte)((in[26 + inPos] & 0x7FFFF) >>> 9 & 0xFF);
            out[63 + outPos] = (byte)((in[26 + inPos] & 0x7FFFF) >>> 1 & 0xFF);
            out[64 + outPos] = (byte)(((in[26 + inPos] & 0x7FFFF) << 7 | (in[27 + inPos] & 0x7FFFF) >>> 12) & 0xFF);
            out[65 + outPos] = (byte)((in[27 + inPos] & 0x7FFFF) >>> 4 & 0xFF);
            out[66 + outPos] = (byte)(((in[27 + inPos] & 0x7FFFF) << 4 | (in[28 + inPos] & 0x7FFFF) >>> 15) & 0xFF);
            out[67 + outPos] = (byte)((in[28 + inPos] & 0x7FFFF) >>> 7 & 0xFF);
            out[68 + outPos] = (byte)(((in[28 + inPos] & 0x7FFFF) << 1 | (in[29 + inPos] & 0x7FFFF) >>> 18) & 0xFF);
            out[69 + outPos] = (byte)((in[29 + inPos] & 0x7FFFF) >>> 10 & 0xFF);
            out[70 + outPos] = (byte)((in[29 + inPos] & 0x7FFFF) >>> 2 & 0xFF);
            out[71 + outPos] = (byte)(((in[29 + inPos] & 0x7FFFF) << 6 | (in[30 + inPos] & 0x7FFFF) >>> 13) & 0xFF);
            out[72 + outPos] = (byte)((in[30 + inPos] & 0x7FFFF) >>> 5 & 0xFF);
            out[73 + outPos] = (byte)(((in[30 + inPos] & 0x7FFFF) << 3 | (in[31 + inPos] & 0x7FFFF) >>> 16) & 0xFF);
            out[74 + outPos] = (byte)((in[31 + inPos] & 0x7FFFF) >>> 8 & 0xFF);
            out[75 + outPos] = (byte)(in[31 + inPos] & 0x7FFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 11 & 0x7FFFF) | ((in[1 + inPos] & 0xFF) << 3 & 0x7FFFF) | ((in[2 + inPos] & 0xFF) >>> 5 & 0x7FFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 14 & 0x7FFFF) | ((in[3 + inPos] & 0xFF) << 6 & 0x7FFFF) | ((in[4 + inPos] & 0xFF) >>> 2 & 0x7FFFF));
            out[2 + outPos] = (((in[4 + inPos] & 0xFF) << 17 & 0x7FFFF) | ((in[5 + inPos] & 0xFF) << 9 & 0x7FFFF) | ((in[6 + inPos] & 0xFF) << 1 & 0x7FFFF) | ((in[7 + inPos] & 0xFF) >>> 7 & 0x7FFFF));
            out[3 + outPos] = (((in[7 + inPos] & 0xFF) << 12 & 0x7FFFF) | ((in[8 + inPos] & 0xFF) << 4 & 0x7FFFF) | ((in[9 + inPos] & 0xFF) >>> 4 & 0x7FFFF));
            out[4 + outPos] = (((in[9 + inPos] & 0xFF) << 15 & 0x7FFFF) | ((in[10 + inPos] & 0xFF) << 7 & 0x7FFFF) | ((in[11 + inPos] & 0xFF) >>> 1 & 0x7FFFF));
            out[5 + outPos] = (((in[11 + inPos] & 0xFF) << 18 & 0x7FFFF) | ((in[12 + inPos] & 0xFF) << 10 & 0x7FFFF) | ((in[13 + inPos] & 0xFF) << 2 & 0x7FFFF) | ((in[14 + inPos] & 0xFF) >>> 6 & 0x7FFFF));
            out[6 + outPos] = (((in[14 + inPos] & 0xFF) << 13 & 0x7FFFF) | ((in[15 + inPos] & 0xFF) << 5 & 0x7FFFF) | ((in[16 + inPos] & 0xFF) >>> 3 & 0x7FFFF));
            out[7 + outPos] = (((in[16 + inPos] & 0xFF) << 16 & 0x7FFFF) | ((in[17 + inPos] & 0xFF) << 8 & 0x7FFFF) | (in[18 + inPos] & 0xFF & 0x7FFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 11 & 0x7FFFF) | ((in[1 + inPos] & 0xFF) << 3 & 0x7FFFF) | ((in[2 + inPos] & 0xFF) >>> 5 & 0x7FFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 14 & 0x7FFFF) | ((in[3 + inPos] & 0xFF) << 6 & 0x7FFFF) | ((in[4 + inPos] & 0xFF) >>> 2 & 0x7FFFF));
            out[2 + outPos] = (((in[4 + inPos] & 0xFF) << 17 & 0x7FFFF) | ((in[5 + inPos] & 0xFF) << 9 & 0x7FFFF) | ((in[6 + inPos] & 0xFF) << 1 & 0x7FFFF) | ((in[7 + inPos] & 0xFF) >>> 7 & 0x7FFFF));
            out[3 + outPos] = (((in[7 + inPos] & 0xFF) << 12 & 0x7FFFF) | ((in[8 + inPos] & 0xFF) << 4 & 0x7FFFF) | ((in[9 + inPos] & 0xFF) >>> 4 & 0x7FFFF));
            out[4 + outPos] = (((in[9 + inPos] & 0xFF) << 15 & 0x7FFFF) | ((in[10 + inPos] & 0xFF) << 7 & 0x7FFFF) | ((in[11 + inPos] & 0xFF) >>> 1 & 0x7FFFF));
            out[5 + outPos] = (((in[11 + inPos] & 0xFF) << 18 & 0x7FFFF) | ((in[12 + inPos] & 0xFF) << 10 & 0x7FFFF) | ((in[13 + inPos] & 0xFF) << 2 & 0x7FFFF) | ((in[14 + inPos] & 0xFF) >>> 6 & 0x7FFFF));
            out[6 + outPos] = (((in[14 + inPos] & 0xFF) << 13 & 0x7FFFF) | ((in[15 + inPos] & 0xFF) << 5 & 0x7FFFF) | ((in[16 + inPos] & 0xFF) >>> 3 & 0x7FFFF));
            out[7 + outPos] = (((in[16 + inPos] & 0xFF) << 16 & 0x7FFFF) | ((in[17 + inPos] & 0xFF) << 8 & 0x7FFFF) | (in[18 + inPos] & 0xFF & 0x7FFFF));
            out[8 + outPos] = (((in[19 + inPos] & 0xFF) << 11 & 0x7FFFF) | ((in[20 + inPos] & 0xFF) << 3 & 0x7FFFF) | ((in[21 + inPos] & 0xFF) >>> 5 & 0x7FFFF));
            out[9 + outPos] = (((in[21 + inPos] & 0xFF) << 14 & 0x7FFFF) | ((in[22 + inPos] & 0xFF) << 6 & 0x7FFFF) | ((in[23 + inPos] & 0xFF) >>> 2 & 0x7FFFF));
            out[10 + outPos] = (((in[23 + inPos] & 0xFF) << 17 & 0x7FFFF) | ((in[24 + inPos] & 0xFF) << 9 & 0x7FFFF) | ((in[25 + inPos] & 0xFF) << 1 & 0x7FFFF) | ((in[26 + inPos] & 0xFF) >>> 7 & 0x7FFFF));
            out[11 + outPos] = (((in[26 + inPos] & 0xFF) << 12 & 0x7FFFF) | ((in[27 + inPos] & 0xFF) << 4 & 0x7FFFF) | ((in[28 + inPos] & 0xFF) >>> 4 & 0x7FFFF));
            out[12 + outPos] = (((in[28 + inPos] & 0xFF) << 15 & 0x7FFFF) | ((in[29 + inPos] & 0xFF) << 7 & 0x7FFFF) | ((in[30 + inPos] & 0xFF) >>> 1 & 0x7FFFF));
            out[13 + outPos] = (((in[30 + inPos] & 0xFF) << 18 & 0x7FFFF) | ((in[31 + inPos] & 0xFF) << 10 & 0x7FFFF) | ((in[32 + inPos] & 0xFF) << 2 & 0x7FFFF) | ((in[33 + inPos] & 0xFF) >>> 6 & 0x7FFFF));
            out[14 + outPos] = (((in[33 + inPos] & 0xFF) << 13 & 0x7FFFF) | ((in[34 + inPos] & 0xFF) << 5 & 0x7FFFF) | ((in[35 + inPos] & 0xFF) >>> 3 & 0x7FFFF));
            out[15 + outPos] = (((in[35 + inPos] & 0xFF) << 16 & 0x7FFFF) | ((in[36 + inPos] & 0xFF) << 8 & 0x7FFFF) | (in[37 + inPos] & 0xFF & 0x7FFFF));
            out[16 + outPos] = (((in[38 + inPos] & 0xFF) << 11 & 0x7FFFF) | ((in[39 + inPos] & 0xFF) << 3 & 0x7FFFF) | ((in[40 + inPos] & 0xFF) >>> 5 & 0x7FFFF));
            out[17 + outPos] = (((in[40 + inPos] & 0xFF) << 14 & 0x7FFFF) | ((in[41 + inPos] & 0xFF) << 6 & 0x7FFFF) | ((in[42 + inPos] & 0xFF) >>> 2 & 0x7FFFF));
            out[18 + outPos] = (((in[42 + inPos] & 0xFF) << 17 & 0x7FFFF) | ((in[43 + inPos] & 0xFF) << 9 & 0x7FFFF) | ((in[44 + inPos] & 0xFF) << 1 & 0x7FFFF) | ((in[45 + inPos] & 0xFF) >>> 7 & 0x7FFFF));
            out[19 + outPos] = (((in[45 + inPos] & 0xFF) << 12 & 0x7FFFF) | ((in[46 + inPos] & 0xFF) << 4 & 0x7FFFF) | ((in[47 + inPos] & 0xFF) >>> 4 & 0x7FFFF));
            out[20 + outPos] = (((in[47 + inPos] & 0xFF) << 15 & 0x7FFFF) | ((in[48 + inPos] & 0xFF) << 7 & 0x7FFFF) | ((in[49 + inPos] & 0xFF) >>> 1 & 0x7FFFF));
            out[21 + outPos] = (((in[49 + inPos] & 0xFF) << 18 & 0x7FFFF) | ((in[50 + inPos] & 0xFF) << 10 & 0x7FFFF) | ((in[51 + inPos] & 0xFF) << 2 & 0x7FFFF) | ((in[52 + inPos] & 0xFF) >>> 6 & 0x7FFFF));
            out[22 + outPos] = (((in[52 + inPos] & 0xFF) << 13 & 0x7FFFF) | ((in[53 + inPos] & 0xFF) << 5 & 0x7FFFF) | ((in[54 + inPos] & 0xFF) >>> 3 & 0x7FFFF));
            out[23 + outPos] = (((in[54 + inPos] & 0xFF) << 16 & 0x7FFFF) | ((in[55 + inPos] & 0xFF) << 8 & 0x7FFFF) | (in[56 + inPos] & 0xFF & 0x7FFFF));
            out[24 + outPos] = (((in[57 + inPos] & 0xFF) << 11 & 0x7FFFF) | ((in[58 + inPos] & 0xFF) << 3 & 0x7FFFF) | ((in[59 + inPos] & 0xFF) >>> 5 & 0x7FFFF));
            out[25 + outPos] = (((in[59 + inPos] & 0xFF) << 14 & 0x7FFFF) | ((in[60 + inPos] & 0xFF) << 6 & 0x7FFFF) | ((in[61 + inPos] & 0xFF) >>> 2 & 0x7FFFF));
            out[26 + outPos] = (((in[61 + inPos] & 0xFF) << 17 & 0x7FFFF) | ((in[62 + inPos] & 0xFF) << 9 & 0x7FFFF) | ((in[63 + inPos] & 0xFF) << 1 & 0x7FFFF) | ((in[64 + inPos] & 0xFF) >>> 7 & 0x7FFFF));
            out[27 + outPos] = (((in[64 + inPos] & 0xFF) << 12 & 0x7FFFF) | ((in[65 + inPos] & 0xFF) << 4 & 0x7FFFF) | ((in[66 + inPos] & 0xFF) >>> 4 & 0x7FFFF));
            out[28 + outPos] = (((in[66 + inPos] & 0xFF) << 15 & 0x7FFFF) | ((in[67 + inPos] & 0xFF) << 7 & 0x7FFFF) | ((in[68 + inPos] & 0xFF) >>> 1 & 0x7FFFF));
            out[29 + outPos] = (((in[68 + inPos] & 0xFF) << 18 & 0x7FFFF) | ((in[69 + inPos] & 0xFF) << 10 & 0x7FFFF) | ((in[70 + inPos] & 0xFF) << 2 & 0x7FFFF) | ((in[71 + inPos] & 0xFF) >>> 6 & 0x7FFFF));
            out[30 + outPos] = (((in[71 + inPos] & 0xFF) << 13 & 0x7FFFF) | ((in[72 + inPos] & 0xFF) << 5 & 0x7FFFF) | ((in[73 + inPos] & 0xFF) >>> 3 & 0x7FFFF));
            out[31 + outPos] = (((in[73 + inPos] & 0xFF) << 16 & 0x7FFFF) | ((in[74 + inPos] & 0xFF) << 8 & 0x7FFFF) | (in[75 + inPos] & 0xFF & 0x7FFFF));
        }
    }
    
    private static final class Packer20 extends BytePacker
    {
        private Packer20() {
            super(20);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0xFFFFF) << 4 | (in[1 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[4 + outPos] = (byte)(in[1 + inPos] & 0xFFFFF & 0xFF);
            out[5 + outPos] = (byte)((in[2 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[7 + outPos] = (byte)(((in[2 + inPos] & 0xFFFFF) << 4 | (in[3 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[8 + outPos] = (byte)((in[3 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[9 + outPos] = (byte)(in[3 + inPos] & 0xFFFFF & 0xFF);
            out[10 + outPos] = (byte)((in[4 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[11 + outPos] = (byte)((in[4 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[12 + outPos] = (byte)(((in[4 + inPos] & 0xFFFFF) << 4 | (in[5 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[13 + outPos] = (byte)((in[5 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[14 + outPos] = (byte)(in[5 + inPos] & 0xFFFFF & 0xFF);
            out[15 + outPos] = (byte)((in[6 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[16 + outPos] = (byte)((in[6 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[17 + outPos] = (byte)(((in[6 + inPos] & 0xFFFFF) << 4 | (in[7 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[18 + outPos] = (byte)((in[7 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[19 + outPos] = (byte)(in[7 + inPos] & 0xFFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0xFFFFF) << 4 | (in[1 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[4 + outPos] = (byte)(in[1 + inPos] & 0xFFFFF & 0xFF);
            out[5 + outPos] = (byte)((in[2 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[7 + outPos] = (byte)(((in[2 + inPos] & 0xFFFFF) << 4 | (in[3 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[8 + outPos] = (byte)((in[3 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[9 + outPos] = (byte)(in[3 + inPos] & 0xFFFFF & 0xFF);
            out[10 + outPos] = (byte)((in[4 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[11 + outPos] = (byte)((in[4 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[12 + outPos] = (byte)(((in[4 + inPos] & 0xFFFFF) << 4 | (in[5 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[13 + outPos] = (byte)((in[5 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[14 + outPos] = (byte)(in[5 + inPos] & 0xFFFFF & 0xFF);
            out[15 + outPos] = (byte)((in[6 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[16 + outPos] = (byte)((in[6 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[17 + outPos] = (byte)(((in[6 + inPos] & 0xFFFFF) << 4 | (in[7 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[18 + outPos] = (byte)((in[7 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[19 + outPos] = (byte)(in[7 + inPos] & 0xFFFFF & 0xFF);
            out[20 + outPos] = (byte)((in[8 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[21 + outPos] = (byte)((in[8 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[22 + outPos] = (byte)(((in[8 + inPos] & 0xFFFFF) << 4 | (in[9 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[23 + outPos] = (byte)((in[9 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[24 + outPos] = (byte)(in[9 + inPos] & 0xFFFFF & 0xFF);
            out[25 + outPos] = (byte)((in[10 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[26 + outPos] = (byte)((in[10 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[27 + outPos] = (byte)(((in[10 + inPos] & 0xFFFFF) << 4 | (in[11 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[28 + outPos] = (byte)((in[11 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[29 + outPos] = (byte)(in[11 + inPos] & 0xFFFFF & 0xFF);
            out[30 + outPos] = (byte)((in[12 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[31 + outPos] = (byte)((in[12 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[32 + outPos] = (byte)(((in[12 + inPos] & 0xFFFFF) << 4 | (in[13 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[33 + outPos] = (byte)((in[13 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[34 + outPos] = (byte)(in[13 + inPos] & 0xFFFFF & 0xFF);
            out[35 + outPos] = (byte)((in[14 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[36 + outPos] = (byte)((in[14 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[37 + outPos] = (byte)(((in[14 + inPos] & 0xFFFFF) << 4 | (in[15 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[38 + outPos] = (byte)((in[15 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[39 + outPos] = (byte)(in[15 + inPos] & 0xFFFFF & 0xFF);
            out[40 + outPos] = (byte)((in[16 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[41 + outPos] = (byte)((in[16 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[42 + outPos] = (byte)(((in[16 + inPos] & 0xFFFFF) << 4 | (in[17 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[43 + outPos] = (byte)((in[17 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[44 + outPos] = (byte)(in[17 + inPos] & 0xFFFFF & 0xFF);
            out[45 + outPos] = (byte)((in[18 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[46 + outPos] = (byte)((in[18 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[47 + outPos] = (byte)(((in[18 + inPos] & 0xFFFFF) << 4 | (in[19 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[48 + outPos] = (byte)((in[19 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[49 + outPos] = (byte)(in[19 + inPos] & 0xFFFFF & 0xFF);
            out[50 + outPos] = (byte)((in[20 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[51 + outPos] = (byte)((in[20 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[52 + outPos] = (byte)(((in[20 + inPos] & 0xFFFFF) << 4 | (in[21 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[53 + outPos] = (byte)((in[21 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[54 + outPos] = (byte)(in[21 + inPos] & 0xFFFFF & 0xFF);
            out[55 + outPos] = (byte)((in[22 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[56 + outPos] = (byte)((in[22 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[57 + outPos] = (byte)(((in[22 + inPos] & 0xFFFFF) << 4 | (in[23 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[58 + outPos] = (byte)((in[23 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[59 + outPos] = (byte)(in[23 + inPos] & 0xFFFFF & 0xFF);
            out[60 + outPos] = (byte)((in[24 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[61 + outPos] = (byte)((in[24 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[62 + outPos] = (byte)(((in[24 + inPos] & 0xFFFFF) << 4 | (in[25 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[63 + outPos] = (byte)((in[25 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[64 + outPos] = (byte)(in[25 + inPos] & 0xFFFFF & 0xFF);
            out[65 + outPos] = (byte)((in[26 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[66 + outPos] = (byte)((in[26 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[67 + outPos] = (byte)(((in[26 + inPos] & 0xFFFFF) << 4 | (in[27 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[68 + outPos] = (byte)((in[27 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[69 + outPos] = (byte)(in[27 + inPos] & 0xFFFFF & 0xFF);
            out[70 + outPos] = (byte)((in[28 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[71 + outPos] = (byte)((in[28 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[72 + outPos] = (byte)(((in[28 + inPos] & 0xFFFFF) << 4 | (in[29 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[73 + outPos] = (byte)((in[29 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[74 + outPos] = (byte)(in[29 + inPos] & 0xFFFFF & 0xFF);
            out[75 + outPos] = (byte)((in[30 + inPos] & 0xFFFFF) >>> 12 & 0xFF);
            out[76 + outPos] = (byte)((in[30 + inPos] & 0xFFFFF) >>> 4 & 0xFF);
            out[77 + outPos] = (byte)(((in[30 + inPos] & 0xFFFFF) << 4 | (in[31 + inPos] & 0xFFFFF) >>> 16) & 0xFF);
            out[78 + outPos] = (byte)((in[31 + inPos] & 0xFFFFF) >>> 8 & 0xFF);
            out[79 + outPos] = (byte)(in[31 + inPos] & 0xFFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[1 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[2 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[3 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[4 + inPos] & 0xFF & 0xFFFFF));
            out[2 + outPos] = (((in[5 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[6 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[3 + outPos] = (((in[7 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[8 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[9 + inPos] & 0xFF & 0xFFFFF));
            out[4 + outPos] = (((in[10 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[11 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[12 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[5 + outPos] = (((in[12 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[13 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[14 + inPos] & 0xFF & 0xFFFFF));
            out[6 + outPos] = (((in[15 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[16 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[17 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[7 + outPos] = (((in[17 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[18 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[19 + inPos] & 0xFF & 0xFFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[1 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[2 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[3 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[4 + inPos] & 0xFF & 0xFFFFF));
            out[2 + outPos] = (((in[5 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[6 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[3 + outPos] = (((in[7 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[8 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[9 + inPos] & 0xFF & 0xFFFFF));
            out[4 + outPos] = (((in[10 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[11 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[12 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[5 + outPos] = (((in[12 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[13 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[14 + inPos] & 0xFF & 0xFFFFF));
            out[6 + outPos] = (((in[15 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[16 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[17 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[7 + outPos] = (((in[17 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[18 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[19 + inPos] & 0xFF & 0xFFFFF));
            out[8 + outPos] = (((in[20 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[21 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[22 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[9 + outPos] = (((in[22 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[23 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[24 + inPos] & 0xFF & 0xFFFFF));
            out[10 + outPos] = (((in[25 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[26 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[27 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[11 + outPos] = (((in[27 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[28 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[29 + inPos] & 0xFF & 0xFFFFF));
            out[12 + outPos] = (((in[30 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[31 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[32 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[13 + outPos] = (((in[32 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[33 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[34 + inPos] & 0xFF & 0xFFFFF));
            out[14 + outPos] = (((in[35 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[36 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[37 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[15 + outPos] = (((in[37 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[38 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[39 + inPos] & 0xFF & 0xFFFFF));
            out[16 + outPos] = (((in[40 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[41 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[42 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[17 + outPos] = (((in[42 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[43 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[44 + inPos] & 0xFF & 0xFFFFF));
            out[18 + outPos] = (((in[45 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[46 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[47 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[19 + outPos] = (((in[47 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[48 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[49 + inPos] & 0xFF & 0xFFFFF));
            out[20 + outPos] = (((in[50 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[51 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[52 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[21 + outPos] = (((in[52 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[53 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[54 + inPos] & 0xFF & 0xFFFFF));
            out[22 + outPos] = (((in[55 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[56 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[57 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[23 + outPos] = (((in[57 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[58 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[59 + inPos] & 0xFF & 0xFFFFF));
            out[24 + outPos] = (((in[60 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[61 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[62 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[25 + outPos] = (((in[62 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[63 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[64 + inPos] & 0xFF & 0xFFFFF));
            out[26 + outPos] = (((in[65 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[66 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[67 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[27 + outPos] = (((in[67 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[68 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[69 + inPos] & 0xFF & 0xFFFFF));
            out[28 + outPos] = (((in[70 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[71 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[72 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[29 + outPos] = (((in[72 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[73 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[74 + inPos] & 0xFF & 0xFFFFF));
            out[30 + outPos] = (((in[75 + inPos] & 0xFF) << 12 & 0xFFFFF) | ((in[76 + inPos] & 0xFF) << 4 & 0xFFFFF) | ((in[77 + inPos] & 0xFF) >>> 4 & 0xFFFFF));
            out[31 + outPos] = (((in[77 + inPos] & 0xFF) << 16 & 0xFFFFF) | ((in[78 + inPos] & 0xFF) << 8 & 0xFFFFF) | (in[79 + inPos] & 0xFF & 0xFFFFF));
        }
    }
    
    private static final class Packer21 extends BytePacker
    {
        private Packer21() {
            super(21);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFF) >>> 13 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFF) >>> 5 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x1FFFFF) << 3 | (in[1 + inPos] & 0x1FFFFF) >>> 18) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFF) >>> 10 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFF) >>> 2 & 0xFF);
            out[5 + outPos] = (byte)(((in[1 + inPos] & 0x1FFFFF) << 6 | (in[2 + inPos] & 0x1FFFFF) >>> 15) & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFF) >>> 7 & 0xFF);
            out[7 + outPos] = (byte)(((in[2 + inPos] & 0x1FFFFF) << 1 | (in[3 + inPos] & 0x1FFFFF) >>> 20) & 0xFF);
            out[8 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFF) >>> 12 & 0xFF);
            out[9 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFF) >>> 4 & 0xFF);
            out[10 + outPos] = (byte)(((in[3 + inPos] & 0x1FFFFF) << 4 | (in[4 + inPos] & 0x1FFFFF) >>> 17) & 0xFF);
            out[11 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFF) >>> 9 & 0xFF);
            out[12 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFF) >>> 1 & 0xFF);
            out[13 + outPos] = (byte)(((in[4 + inPos] & 0x1FFFFF) << 7 | (in[5 + inPos] & 0x1FFFFF) >>> 14) & 0xFF);
            out[14 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFF) >>> 6 & 0xFF);
            out[15 + outPos] = (byte)(((in[5 + inPos] & 0x1FFFFF) << 2 | (in[6 + inPos] & 0x1FFFFF) >>> 19) & 0xFF);
            out[16 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFF) >>> 11 & 0xFF);
            out[17 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFF) >>> 3 & 0xFF);
            out[18 + outPos] = (byte)(((in[6 + inPos] & 0x1FFFFF) << 5 | (in[7 + inPos] & 0x1FFFFF) >>> 16) & 0xFF);
            out[19 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFF) >>> 8 & 0xFF);
            out[20 + outPos] = (byte)(in[7 + inPos] & 0x1FFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFF) >>> 13 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFF) >>> 5 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x1FFFFF) << 3 | (in[1 + inPos] & 0x1FFFFF) >>> 18) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFF) >>> 10 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFF) >>> 2 & 0xFF);
            out[5 + outPos] = (byte)(((in[1 + inPos] & 0x1FFFFF) << 6 | (in[2 + inPos] & 0x1FFFFF) >>> 15) & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFF) >>> 7 & 0xFF);
            out[7 + outPos] = (byte)(((in[2 + inPos] & 0x1FFFFF) << 1 | (in[3 + inPos] & 0x1FFFFF) >>> 20) & 0xFF);
            out[8 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFF) >>> 12 & 0xFF);
            out[9 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFF) >>> 4 & 0xFF);
            out[10 + outPos] = (byte)(((in[3 + inPos] & 0x1FFFFF) << 4 | (in[4 + inPos] & 0x1FFFFF) >>> 17) & 0xFF);
            out[11 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFF) >>> 9 & 0xFF);
            out[12 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFF) >>> 1 & 0xFF);
            out[13 + outPos] = (byte)(((in[4 + inPos] & 0x1FFFFF) << 7 | (in[5 + inPos] & 0x1FFFFF) >>> 14) & 0xFF);
            out[14 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFF) >>> 6 & 0xFF);
            out[15 + outPos] = (byte)(((in[5 + inPos] & 0x1FFFFF) << 2 | (in[6 + inPos] & 0x1FFFFF) >>> 19) & 0xFF);
            out[16 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFF) >>> 11 & 0xFF);
            out[17 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFF) >>> 3 & 0xFF);
            out[18 + outPos] = (byte)(((in[6 + inPos] & 0x1FFFFF) << 5 | (in[7 + inPos] & 0x1FFFFF) >>> 16) & 0xFF);
            out[19 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFF) >>> 8 & 0xFF);
            out[20 + outPos] = (byte)(in[7 + inPos] & 0x1FFFFF & 0xFF);
            out[21 + outPos] = (byte)((in[8 + inPos] & 0x1FFFFF) >>> 13 & 0xFF);
            out[22 + outPos] = (byte)((in[8 + inPos] & 0x1FFFFF) >>> 5 & 0xFF);
            out[23 + outPos] = (byte)(((in[8 + inPos] & 0x1FFFFF) << 3 | (in[9 + inPos] & 0x1FFFFF) >>> 18) & 0xFF);
            out[24 + outPos] = (byte)((in[9 + inPos] & 0x1FFFFF) >>> 10 & 0xFF);
            out[25 + outPos] = (byte)((in[9 + inPos] & 0x1FFFFF) >>> 2 & 0xFF);
            out[26 + outPos] = (byte)(((in[9 + inPos] & 0x1FFFFF) << 6 | (in[10 + inPos] & 0x1FFFFF) >>> 15) & 0xFF);
            out[27 + outPos] = (byte)((in[10 + inPos] & 0x1FFFFF) >>> 7 & 0xFF);
            out[28 + outPos] = (byte)(((in[10 + inPos] & 0x1FFFFF) << 1 | (in[11 + inPos] & 0x1FFFFF) >>> 20) & 0xFF);
            out[29 + outPos] = (byte)((in[11 + inPos] & 0x1FFFFF) >>> 12 & 0xFF);
            out[30 + outPos] = (byte)((in[11 + inPos] & 0x1FFFFF) >>> 4 & 0xFF);
            out[31 + outPos] = (byte)(((in[11 + inPos] & 0x1FFFFF) << 4 | (in[12 + inPos] & 0x1FFFFF) >>> 17) & 0xFF);
            out[32 + outPos] = (byte)((in[12 + inPos] & 0x1FFFFF) >>> 9 & 0xFF);
            out[33 + outPos] = (byte)((in[12 + inPos] & 0x1FFFFF) >>> 1 & 0xFF);
            out[34 + outPos] = (byte)(((in[12 + inPos] & 0x1FFFFF) << 7 | (in[13 + inPos] & 0x1FFFFF) >>> 14) & 0xFF);
            out[35 + outPos] = (byte)((in[13 + inPos] & 0x1FFFFF) >>> 6 & 0xFF);
            out[36 + outPos] = (byte)(((in[13 + inPos] & 0x1FFFFF) << 2 | (in[14 + inPos] & 0x1FFFFF) >>> 19) & 0xFF);
            out[37 + outPos] = (byte)((in[14 + inPos] & 0x1FFFFF) >>> 11 & 0xFF);
            out[38 + outPos] = (byte)((in[14 + inPos] & 0x1FFFFF) >>> 3 & 0xFF);
            out[39 + outPos] = (byte)(((in[14 + inPos] & 0x1FFFFF) << 5 | (in[15 + inPos] & 0x1FFFFF) >>> 16) & 0xFF);
            out[40 + outPos] = (byte)((in[15 + inPos] & 0x1FFFFF) >>> 8 & 0xFF);
            out[41 + outPos] = (byte)(in[15 + inPos] & 0x1FFFFF & 0xFF);
            out[42 + outPos] = (byte)((in[16 + inPos] & 0x1FFFFF) >>> 13 & 0xFF);
            out[43 + outPos] = (byte)((in[16 + inPos] & 0x1FFFFF) >>> 5 & 0xFF);
            out[44 + outPos] = (byte)(((in[16 + inPos] & 0x1FFFFF) << 3 | (in[17 + inPos] & 0x1FFFFF) >>> 18) & 0xFF);
            out[45 + outPos] = (byte)((in[17 + inPos] & 0x1FFFFF) >>> 10 & 0xFF);
            out[46 + outPos] = (byte)((in[17 + inPos] & 0x1FFFFF) >>> 2 & 0xFF);
            out[47 + outPos] = (byte)(((in[17 + inPos] & 0x1FFFFF) << 6 | (in[18 + inPos] & 0x1FFFFF) >>> 15) & 0xFF);
            out[48 + outPos] = (byte)((in[18 + inPos] & 0x1FFFFF) >>> 7 & 0xFF);
            out[49 + outPos] = (byte)(((in[18 + inPos] & 0x1FFFFF) << 1 | (in[19 + inPos] & 0x1FFFFF) >>> 20) & 0xFF);
            out[50 + outPos] = (byte)((in[19 + inPos] & 0x1FFFFF) >>> 12 & 0xFF);
            out[51 + outPos] = (byte)((in[19 + inPos] & 0x1FFFFF) >>> 4 & 0xFF);
            out[52 + outPos] = (byte)(((in[19 + inPos] & 0x1FFFFF) << 4 | (in[20 + inPos] & 0x1FFFFF) >>> 17) & 0xFF);
            out[53 + outPos] = (byte)((in[20 + inPos] & 0x1FFFFF) >>> 9 & 0xFF);
            out[54 + outPos] = (byte)((in[20 + inPos] & 0x1FFFFF) >>> 1 & 0xFF);
            out[55 + outPos] = (byte)(((in[20 + inPos] & 0x1FFFFF) << 7 | (in[21 + inPos] & 0x1FFFFF) >>> 14) & 0xFF);
            out[56 + outPos] = (byte)((in[21 + inPos] & 0x1FFFFF) >>> 6 & 0xFF);
            out[57 + outPos] = (byte)(((in[21 + inPos] & 0x1FFFFF) << 2 | (in[22 + inPos] & 0x1FFFFF) >>> 19) & 0xFF);
            out[58 + outPos] = (byte)((in[22 + inPos] & 0x1FFFFF) >>> 11 & 0xFF);
            out[59 + outPos] = (byte)((in[22 + inPos] & 0x1FFFFF) >>> 3 & 0xFF);
            out[60 + outPos] = (byte)(((in[22 + inPos] & 0x1FFFFF) << 5 | (in[23 + inPos] & 0x1FFFFF) >>> 16) & 0xFF);
            out[61 + outPos] = (byte)((in[23 + inPos] & 0x1FFFFF) >>> 8 & 0xFF);
            out[62 + outPos] = (byte)(in[23 + inPos] & 0x1FFFFF & 0xFF);
            out[63 + outPos] = (byte)((in[24 + inPos] & 0x1FFFFF) >>> 13 & 0xFF);
            out[64 + outPos] = (byte)((in[24 + inPos] & 0x1FFFFF) >>> 5 & 0xFF);
            out[65 + outPos] = (byte)(((in[24 + inPos] & 0x1FFFFF) << 3 | (in[25 + inPos] & 0x1FFFFF) >>> 18) & 0xFF);
            out[66 + outPos] = (byte)((in[25 + inPos] & 0x1FFFFF) >>> 10 & 0xFF);
            out[67 + outPos] = (byte)((in[25 + inPos] & 0x1FFFFF) >>> 2 & 0xFF);
            out[68 + outPos] = (byte)(((in[25 + inPos] & 0x1FFFFF) << 6 | (in[26 + inPos] & 0x1FFFFF) >>> 15) & 0xFF);
            out[69 + outPos] = (byte)((in[26 + inPos] & 0x1FFFFF) >>> 7 & 0xFF);
            out[70 + outPos] = (byte)(((in[26 + inPos] & 0x1FFFFF) << 1 | (in[27 + inPos] & 0x1FFFFF) >>> 20) & 0xFF);
            out[71 + outPos] = (byte)((in[27 + inPos] & 0x1FFFFF) >>> 12 & 0xFF);
            out[72 + outPos] = (byte)((in[27 + inPos] & 0x1FFFFF) >>> 4 & 0xFF);
            out[73 + outPos] = (byte)(((in[27 + inPos] & 0x1FFFFF) << 4 | (in[28 + inPos] & 0x1FFFFF) >>> 17) & 0xFF);
            out[74 + outPos] = (byte)((in[28 + inPos] & 0x1FFFFF) >>> 9 & 0xFF);
            out[75 + outPos] = (byte)((in[28 + inPos] & 0x1FFFFF) >>> 1 & 0xFF);
            out[76 + outPos] = (byte)(((in[28 + inPos] & 0x1FFFFF) << 7 | (in[29 + inPos] & 0x1FFFFF) >>> 14) & 0xFF);
            out[77 + outPos] = (byte)((in[29 + inPos] & 0x1FFFFF) >>> 6 & 0xFF);
            out[78 + outPos] = (byte)(((in[29 + inPos] & 0x1FFFFF) << 2 | (in[30 + inPos] & 0x1FFFFF) >>> 19) & 0xFF);
            out[79 + outPos] = (byte)((in[30 + inPos] & 0x1FFFFF) >>> 11 & 0xFF);
            out[80 + outPos] = (byte)((in[30 + inPos] & 0x1FFFFF) >>> 3 & 0xFF);
            out[81 + outPos] = (byte)(((in[30 + inPos] & 0x1FFFFF) << 5 | (in[31 + inPos] & 0x1FFFFF) >>> 16) & 0xFF);
            out[82 + outPos] = (byte)((in[31 + inPos] & 0x1FFFFF) >>> 8 & 0xFF);
            out[83 + outPos] = (byte)(in[31 + inPos] & 0x1FFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 13 & 0x1FFFFF) | ((in[1 + inPos] & 0xFF) << 5 & 0x1FFFFF) | ((in[2 + inPos] & 0xFF) >>> 3 & 0x1FFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 18 & 0x1FFFFF) | ((in[3 + inPos] & 0xFF) << 10 & 0x1FFFFF) | ((in[4 + inPos] & 0xFF) << 2 & 0x1FFFFF) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x1FFFFF));
            out[2 + outPos] = (((in[5 + inPos] & 0xFF) << 15 & 0x1FFFFF) | ((in[6 + inPos] & 0xFF) << 7 & 0x1FFFFF) | ((in[7 + inPos] & 0xFF) >>> 1 & 0x1FFFFF));
            out[3 + outPos] = (((in[7 + inPos] & 0xFF) << 20 & 0x1FFFFF) | ((in[8 + inPos] & 0xFF) << 12 & 0x1FFFFF) | ((in[9 + inPos] & 0xFF) << 4 & 0x1FFFFF) | ((in[10 + inPos] & 0xFF) >>> 4 & 0x1FFFFF));
            out[4 + outPos] = (((in[10 + inPos] & 0xFF) << 17 & 0x1FFFFF) | ((in[11 + inPos] & 0xFF) << 9 & 0x1FFFFF) | ((in[12 + inPos] & 0xFF) << 1 & 0x1FFFFF) | ((in[13 + inPos] & 0xFF) >>> 7 & 0x1FFFFF));
            out[5 + outPos] = (((in[13 + inPos] & 0xFF) << 14 & 0x1FFFFF) | ((in[14 + inPos] & 0xFF) << 6 & 0x1FFFFF) | ((in[15 + inPos] & 0xFF) >>> 2 & 0x1FFFFF));
            out[6 + outPos] = (((in[15 + inPos] & 0xFF) << 19 & 0x1FFFFF) | ((in[16 + inPos] & 0xFF) << 11 & 0x1FFFFF) | ((in[17 + inPos] & 0xFF) << 3 & 0x1FFFFF) | ((in[18 + inPos] & 0xFF) >>> 5 & 0x1FFFFF));
            out[7 + outPos] = (((in[18 + inPos] & 0xFF) << 16 & 0x1FFFFF) | ((in[19 + inPos] & 0xFF) << 8 & 0x1FFFFF) | (in[20 + inPos] & 0xFF & 0x1FFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 13 & 0x1FFFFF) | ((in[1 + inPos] & 0xFF) << 5 & 0x1FFFFF) | ((in[2 + inPos] & 0xFF) >>> 3 & 0x1FFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 18 & 0x1FFFFF) | ((in[3 + inPos] & 0xFF) << 10 & 0x1FFFFF) | ((in[4 + inPos] & 0xFF) << 2 & 0x1FFFFF) | ((in[5 + inPos] & 0xFF) >>> 6 & 0x1FFFFF));
            out[2 + outPos] = (((in[5 + inPos] & 0xFF) << 15 & 0x1FFFFF) | ((in[6 + inPos] & 0xFF) << 7 & 0x1FFFFF) | ((in[7 + inPos] & 0xFF) >>> 1 & 0x1FFFFF));
            out[3 + outPos] = (((in[7 + inPos] & 0xFF) << 20 & 0x1FFFFF) | ((in[8 + inPos] & 0xFF) << 12 & 0x1FFFFF) | ((in[9 + inPos] & 0xFF) << 4 & 0x1FFFFF) | ((in[10 + inPos] & 0xFF) >>> 4 & 0x1FFFFF));
            out[4 + outPos] = (((in[10 + inPos] & 0xFF) << 17 & 0x1FFFFF) | ((in[11 + inPos] & 0xFF) << 9 & 0x1FFFFF) | ((in[12 + inPos] & 0xFF) << 1 & 0x1FFFFF) | ((in[13 + inPos] & 0xFF) >>> 7 & 0x1FFFFF));
            out[5 + outPos] = (((in[13 + inPos] & 0xFF) << 14 & 0x1FFFFF) | ((in[14 + inPos] & 0xFF) << 6 & 0x1FFFFF) | ((in[15 + inPos] & 0xFF) >>> 2 & 0x1FFFFF));
            out[6 + outPos] = (((in[15 + inPos] & 0xFF) << 19 & 0x1FFFFF) | ((in[16 + inPos] & 0xFF) << 11 & 0x1FFFFF) | ((in[17 + inPos] & 0xFF) << 3 & 0x1FFFFF) | ((in[18 + inPos] & 0xFF) >>> 5 & 0x1FFFFF));
            out[7 + outPos] = (((in[18 + inPos] & 0xFF) << 16 & 0x1FFFFF) | ((in[19 + inPos] & 0xFF) << 8 & 0x1FFFFF) | (in[20 + inPos] & 0xFF & 0x1FFFFF));
            out[8 + outPos] = (((in[21 + inPos] & 0xFF) << 13 & 0x1FFFFF) | ((in[22 + inPos] & 0xFF) << 5 & 0x1FFFFF) | ((in[23 + inPos] & 0xFF) >>> 3 & 0x1FFFFF));
            out[9 + outPos] = (((in[23 + inPos] & 0xFF) << 18 & 0x1FFFFF) | ((in[24 + inPos] & 0xFF) << 10 & 0x1FFFFF) | ((in[25 + inPos] & 0xFF) << 2 & 0x1FFFFF) | ((in[26 + inPos] & 0xFF) >>> 6 & 0x1FFFFF));
            out[10 + outPos] = (((in[26 + inPos] & 0xFF) << 15 & 0x1FFFFF) | ((in[27 + inPos] & 0xFF) << 7 & 0x1FFFFF) | ((in[28 + inPos] & 0xFF) >>> 1 & 0x1FFFFF));
            out[11 + outPos] = (((in[28 + inPos] & 0xFF) << 20 & 0x1FFFFF) | ((in[29 + inPos] & 0xFF) << 12 & 0x1FFFFF) | ((in[30 + inPos] & 0xFF) << 4 & 0x1FFFFF) | ((in[31 + inPos] & 0xFF) >>> 4 & 0x1FFFFF));
            out[12 + outPos] = (((in[31 + inPos] & 0xFF) << 17 & 0x1FFFFF) | ((in[32 + inPos] & 0xFF) << 9 & 0x1FFFFF) | ((in[33 + inPos] & 0xFF) << 1 & 0x1FFFFF) | ((in[34 + inPos] & 0xFF) >>> 7 & 0x1FFFFF));
            out[13 + outPos] = (((in[34 + inPos] & 0xFF) << 14 & 0x1FFFFF) | ((in[35 + inPos] & 0xFF) << 6 & 0x1FFFFF) | ((in[36 + inPos] & 0xFF) >>> 2 & 0x1FFFFF));
            out[14 + outPos] = (((in[36 + inPos] & 0xFF) << 19 & 0x1FFFFF) | ((in[37 + inPos] & 0xFF) << 11 & 0x1FFFFF) | ((in[38 + inPos] & 0xFF) << 3 & 0x1FFFFF) | ((in[39 + inPos] & 0xFF) >>> 5 & 0x1FFFFF));
            out[15 + outPos] = (((in[39 + inPos] & 0xFF) << 16 & 0x1FFFFF) | ((in[40 + inPos] & 0xFF) << 8 & 0x1FFFFF) | (in[41 + inPos] & 0xFF & 0x1FFFFF));
            out[16 + outPos] = (((in[42 + inPos] & 0xFF) << 13 & 0x1FFFFF) | ((in[43 + inPos] & 0xFF) << 5 & 0x1FFFFF) | ((in[44 + inPos] & 0xFF) >>> 3 & 0x1FFFFF));
            out[17 + outPos] = (((in[44 + inPos] & 0xFF) << 18 & 0x1FFFFF) | ((in[45 + inPos] & 0xFF) << 10 & 0x1FFFFF) | ((in[46 + inPos] & 0xFF) << 2 & 0x1FFFFF) | ((in[47 + inPos] & 0xFF) >>> 6 & 0x1FFFFF));
            out[18 + outPos] = (((in[47 + inPos] & 0xFF) << 15 & 0x1FFFFF) | ((in[48 + inPos] & 0xFF) << 7 & 0x1FFFFF) | ((in[49 + inPos] & 0xFF) >>> 1 & 0x1FFFFF));
            out[19 + outPos] = (((in[49 + inPos] & 0xFF) << 20 & 0x1FFFFF) | ((in[50 + inPos] & 0xFF) << 12 & 0x1FFFFF) | ((in[51 + inPos] & 0xFF) << 4 & 0x1FFFFF) | ((in[52 + inPos] & 0xFF) >>> 4 & 0x1FFFFF));
            out[20 + outPos] = (((in[52 + inPos] & 0xFF) << 17 & 0x1FFFFF) | ((in[53 + inPos] & 0xFF) << 9 & 0x1FFFFF) | ((in[54 + inPos] & 0xFF) << 1 & 0x1FFFFF) | ((in[55 + inPos] & 0xFF) >>> 7 & 0x1FFFFF));
            out[21 + outPos] = (((in[55 + inPos] & 0xFF) << 14 & 0x1FFFFF) | ((in[56 + inPos] & 0xFF) << 6 & 0x1FFFFF) | ((in[57 + inPos] & 0xFF) >>> 2 & 0x1FFFFF));
            out[22 + outPos] = (((in[57 + inPos] & 0xFF) << 19 & 0x1FFFFF) | ((in[58 + inPos] & 0xFF) << 11 & 0x1FFFFF) | ((in[59 + inPos] & 0xFF) << 3 & 0x1FFFFF) | ((in[60 + inPos] & 0xFF) >>> 5 & 0x1FFFFF));
            out[23 + outPos] = (((in[60 + inPos] & 0xFF) << 16 & 0x1FFFFF) | ((in[61 + inPos] & 0xFF) << 8 & 0x1FFFFF) | (in[62 + inPos] & 0xFF & 0x1FFFFF));
            out[24 + outPos] = (((in[63 + inPos] & 0xFF) << 13 & 0x1FFFFF) | ((in[64 + inPos] & 0xFF) << 5 & 0x1FFFFF) | ((in[65 + inPos] & 0xFF) >>> 3 & 0x1FFFFF));
            out[25 + outPos] = (((in[65 + inPos] & 0xFF) << 18 & 0x1FFFFF) | ((in[66 + inPos] & 0xFF) << 10 & 0x1FFFFF) | ((in[67 + inPos] & 0xFF) << 2 & 0x1FFFFF) | ((in[68 + inPos] & 0xFF) >>> 6 & 0x1FFFFF));
            out[26 + outPos] = (((in[68 + inPos] & 0xFF) << 15 & 0x1FFFFF) | ((in[69 + inPos] & 0xFF) << 7 & 0x1FFFFF) | ((in[70 + inPos] & 0xFF) >>> 1 & 0x1FFFFF));
            out[27 + outPos] = (((in[70 + inPos] & 0xFF) << 20 & 0x1FFFFF) | ((in[71 + inPos] & 0xFF) << 12 & 0x1FFFFF) | ((in[72 + inPos] & 0xFF) << 4 & 0x1FFFFF) | ((in[73 + inPos] & 0xFF) >>> 4 & 0x1FFFFF));
            out[28 + outPos] = (((in[73 + inPos] & 0xFF) << 17 & 0x1FFFFF) | ((in[74 + inPos] & 0xFF) << 9 & 0x1FFFFF) | ((in[75 + inPos] & 0xFF) << 1 & 0x1FFFFF) | ((in[76 + inPos] & 0xFF) >>> 7 & 0x1FFFFF));
            out[29 + outPos] = (((in[76 + inPos] & 0xFF) << 14 & 0x1FFFFF) | ((in[77 + inPos] & 0xFF) << 6 & 0x1FFFFF) | ((in[78 + inPos] & 0xFF) >>> 2 & 0x1FFFFF));
            out[30 + outPos] = (((in[78 + inPos] & 0xFF) << 19 & 0x1FFFFF) | ((in[79 + inPos] & 0xFF) << 11 & 0x1FFFFF) | ((in[80 + inPos] & 0xFF) << 3 & 0x1FFFFF) | ((in[81 + inPos] & 0xFF) >>> 5 & 0x1FFFFF));
            out[31 + outPos] = (((in[81 + inPos] & 0xFF) << 16 & 0x1FFFFF) | ((in[82 + inPos] & 0xFF) << 8 & 0x1FFFFF) | (in[83 + inPos] & 0xFF & 0x1FFFFF));
        }
    }
    
    private static final class Packer22 extends BytePacker
    {
        private Packer22() {
            super(22);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x3FFFFF) << 2 | (in[1 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[5 + outPos] = (byte)(((in[1 + inPos] & 0x3FFFFF) << 4 | (in[2 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[8 + outPos] = (byte)(((in[2 + inPos] & 0x3FFFFF) << 6 | (in[3 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[9 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[10 + outPos] = (byte)(in[3 + inPos] & 0x3FFFFF & 0xFF);
            out[11 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[12 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[13 + outPos] = (byte)(((in[4 + inPos] & 0x3FFFFF) << 2 | (in[5 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[14 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[15 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[16 + outPos] = (byte)(((in[5 + inPos] & 0x3FFFFF) << 4 | (in[6 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[17 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[18 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[19 + outPos] = (byte)(((in[6 + inPos] & 0x3FFFFF) << 6 | (in[7 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[20 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[21 + outPos] = (byte)(in[7 + inPos] & 0x3FFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x3FFFFF) << 2 | (in[1 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[5 + outPos] = (byte)(((in[1 + inPos] & 0x3FFFFF) << 4 | (in[2 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[8 + outPos] = (byte)(((in[2 + inPos] & 0x3FFFFF) << 6 | (in[3 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[9 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[10 + outPos] = (byte)(in[3 + inPos] & 0x3FFFFF & 0xFF);
            out[11 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[12 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[13 + outPos] = (byte)(((in[4 + inPos] & 0x3FFFFF) << 2 | (in[5 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[14 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[15 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[16 + outPos] = (byte)(((in[5 + inPos] & 0x3FFFFF) << 4 | (in[6 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[17 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[18 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[19 + outPos] = (byte)(((in[6 + inPos] & 0x3FFFFF) << 6 | (in[7 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[20 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[21 + outPos] = (byte)(in[7 + inPos] & 0x3FFFFF & 0xFF);
            out[22 + outPos] = (byte)((in[8 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[23 + outPos] = (byte)((in[8 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[24 + outPos] = (byte)(((in[8 + inPos] & 0x3FFFFF) << 2 | (in[9 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[25 + outPos] = (byte)((in[9 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[26 + outPos] = (byte)((in[9 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[27 + outPos] = (byte)(((in[9 + inPos] & 0x3FFFFF) << 4 | (in[10 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[28 + outPos] = (byte)((in[10 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[29 + outPos] = (byte)((in[10 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[30 + outPos] = (byte)(((in[10 + inPos] & 0x3FFFFF) << 6 | (in[11 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[31 + outPos] = (byte)((in[11 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[32 + outPos] = (byte)(in[11 + inPos] & 0x3FFFFF & 0xFF);
            out[33 + outPos] = (byte)((in[12 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[34 + outPos] = (byte)((in[12 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[35 + outPos] = (byte)(((in[12 + inPos] & 0x3FFFFF) << 2 | (in[13 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[36 + outPos] = (byte)((in[13 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[37 + outPos] = (byte)((in[13 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[38 + outPos] = (byte)(((in[13 + inPos] & 0x3FFFFF) << 4 | (in[14 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[39 + outPos] = (byte)((in[14 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[40 + outPos] = (byte)((in[14 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[41 + outPos] = (byte)(((in[14 + inPos] & 0x3FFFFF) << 6 | (in[15 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[42 + outPos] = (byte)((in[15 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[43 + outPos] = (byte)(in[15 + inPos] & 0x3FFFFF & 0xFF);
            out[44 + outPos] = (byte)((in[16 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[45 + outPos] = (byte)((in[16 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[46 + outPos] = (byte)(((in[16 + inPos] & 0x3FFFFF) << 2 | (in[17 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[47 + outPos] = (byte)((in[17 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[48 + outPos] = (byte)((in[17 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[49 + outPos] = (byte)(((in[17 + inPos] & 0x3FFFFF) << 4 | (in[18 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[50 + outPos] = (byte)((in[18 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[51 + outPos] = (byte)((in[18 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[52 + outPos] = (byte)(((in[18 + inPos] & 0x3FFFFF) << 6 | (in[19 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[53 + outPos] = (byte)((in[19 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[54 + outPos] = (byte)(in[19 + inPos] & 0x3FFFFF & 0xFF);
            out[55 + outPos] = (byte)((in[20 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[56 + outPos] = (byte)((in[20 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[57 + outPos] = (byte)(((in[20 + inPos] & 0x3FFFFF) << 2 | (in[21 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[58 + outPos] = (byte)((in[21 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[59 + outPos] = (byte)((in[21 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[60 + outPos] = (byte)(((in[21 + inPos] & 0x3FFFFF) << 4 | (in[22 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[61 + outPos] = (byte)((in[22 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[62 + outPos] = (byte)((in[22 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[63 + outPos] = (byte)(((in[22 + inPos] & 0x3FFFFF) << 6 | (in[23 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[64 + outPos] = (byte)((in[23 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[65 + outPos] = (byte)(in[23 + inPos] & 0x3FFFFF & 0xFF);
            out[66 + outPos] = (byte)((in[24 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[67 + outPos] = (byte)((in[24 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[68 + outPos] = (byte)(((in[24 + inPos] & 0x3FFFFF) << 2 | (in[25 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[69 + outPos] = (byte)((in[25 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[70 + outPos] = (byte)((in[25 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[71 + outPos] = (byte)(((in[25 + inPos] & 0x3FFFFF) << 4 | (in[26 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[72 + outPos] = (byte)((in[26 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[73 + outPos] = (byte)((in[26 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[74 + outPos] = (byte)(((in[26 + inPos] & 0x3FFFFF) << 6 | (in[27 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[75 + outPos] = (byte)((in[27 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[76 + outPos] = (byte)(in[27 + inPos] & 0x3FFFFF & 0xFF);
            out[77 + outPos] = (byte)((in[28 + inPos] & 0x3FFFFF) >>> 14 & 0xFF);
            out[78 + outPos] = (byte)((in[28 + inPos] & 0x3FFFFF) >>> 6 & 0xFF);
            out[79 + outPos] = (byte)(((in[28 + inPos] & 0x3FFFFF) << 2 | (in[29 + inPos] & 0x3FFFFF) >>> 20) & 0xFF);
            out[80 + outPos] = (byte)((in[29 + inPos] & 0x3FFFFF) >>> 12 & 0xFF);
            out[81 + outPos] = (byte)((in[29 + inPos] & 0x3FFFFF) >>> 4 & 0xFF);
            out[82 + outPos] = (byte)(((in[29 + inPos] & 0x3FFFFF) << 4 | (in[30 + inPos] & 0x3FFFFF) >>> 18) & 0xFF);
            out[83 + outPos] = (byte)((in[30 + inPos] & 0x3FFFFF) >>> 10 & 0xFF);
            out[84 + outPos] = (byte)((in[30 + inPos] & 0x3FFFFF) >>> 2 & 0xFF);
            out[85 + outPos] = (byte)(((in[30 + inPos] & 0x3FFFFF) << 6 | (in[31 + inPos] & 0x3FFFFF) >>> 16) & 0xFF);
            out[86 + outPos] = (byte)((in[31 + inPos] & 0x3FFFFF) >>> 8 & 0xFF);
            out[87 + outPos] = (byte)(in[31 + inPos] & 0x3FFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[1 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[2 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[3 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[4 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[5 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[2 + outPos] = (((in[5 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[6 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[7 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[8 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[3 + outPos] = (((in[8 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[9 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[10 + inPos] & 0xFF & 0x3FFFFF));
            out[4 + outPos] = (((in[11 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[12 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[13 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[5 + outPos] = (((in[13 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[14 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[15 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[16 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[6 + outPos] = (((in[16 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[17 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[18 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[19 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[7 + outPos] = (((in[19 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[20 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[21 + inPos] & 0xFF & 0x3FFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[1 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[2 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[3 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[4 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[5 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[2 + outPos] = (((in[5 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[6 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[7 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[8 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[3 + outPos] = (((in[8 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[9 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[10 + inPos] & 0xFF & 0x3FFFFF));
            out[4 + outPos] = (((in[11 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[12 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[13 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[5 + outPos] = (((in[13 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[14 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[15 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[16 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[6 + outPos] = (((in[16 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[17 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[18 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[19 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[7 + outPos] = (((in[19 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[20 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[21 + inPos] & 0xFF & 0x3FFFFF));
            out[8 + outPos] = (((in[22 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[23 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[24 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[9 + outPos] = (((in[24 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[25 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[26 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[27 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[10 + outPos] = (((in[27 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[28 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[29 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[30 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[11 + outPos] = (((in[30 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[31 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[32 + inPos] & 0xFF & 0x3FFFFF));
            out[12 + outPos] = (((in[33 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[34 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[35 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[13 + outPos] = (((in[35 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[36 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[37 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[38 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[14 + outPos] = (((in[38 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[39 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[40 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[41 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[15 + outPos] = (((in[41 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[42 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[43 + inPos] & 0xFF & 0x3FFFFF));
            out[16 + outPos] = (((in[44 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[45 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[46 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[17 + outPos] = (((in[46 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[47 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[48 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[49 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[18 + outPos] = (((in[49 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[50 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[51 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[52 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[19 + outPos] = (((in[52 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[53 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[54 + inPos] & 0xFF & 0x3FFFFF));
            out[20 + outPos] = (((in[55 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[56 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[57 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[21 + outPos] = (((in[57 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[58 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[59 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[60 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[22 + outPos] = (((in[60 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[61 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[62 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[63 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[23 + outPos] = (((in[63 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[64 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[65 + inPos] & 0xFF & 0x3FFFFF));
            out[24 + outPos] = (((in[66 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[67 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[68 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[25 + outPos] = (((in[68 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[69 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[70 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[71 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[26 + outPos] = (((in[71 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[72 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[73 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[74 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[27 + outPos] = (((in[74 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[75 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[76 + inPos] & 0xFF & 0x3FFFFF));
            out[28 + outPos] = (((in[77 + inPos] & 0xFF) << 14 & 0x3FFFFF) | ((in[78 + inPos] & 0xFF) << 6 & 0x3FFFFF) | ((in[79 + inPos] & 0xFF) >>> 2 & 0x3FFFFF));
            out[29 + outPos] = (((in[79 + inPos] & 0xFF) << 20 & 0x3FFFFF) | ((in[80 + inPos] & 0xFF) << 12 & 0x3FFFFF) | ((in[81 + inPos] & 0xFF) << 4 & 0x3FFFFF) | ((in[82 + inPos] & 0xFF) >>> 4 & 0x3FFFFF));
            out[30 + outPos] = (((in[82 + inPos] & 0xFF) << 18 & 0x3FFFFF) | ((in[83 + inPos] & 0xFF) << 10 & 0x3FFFFF) | ((in[84 + inPos] & 0xFF) << 2 & 0x3FFFFF) | ((in[85 + inPos] & 0xFF) >>> 6 & 0x3FFFFF));
            out[31 + outPos] = (((in[85 + inPos] & 0xFF) << 16 & 0x3FFFFF) | ((in[86 + inPos] & 0xFF) << 8 & 0x3FFFFF) | (in[87 + inPos] & 0xFF & 0x3FFFFF));
        }
    }
    
    private static final class Packer23 extends BytePacker
    {
        private Packer23() {
            super(23);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFF) >>> 15 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFF) >>> 7 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x7FFFFF) << 1 | (in[1 + inPos] & 0x7FFFFF) >>> 22) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x7FFFFF) >>> 14 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x7FFFFF) >>> 6 & 0xFF);
            out[5 + outPos] = (byte)(((in[1 + inPos] & 0x7FFFFF) << 2 | (in[2 + inPos] & 0x7FFFFF) >>> 21) & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFF) >>> 13 & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFF) >>> 5 & 0xFF);
            out[8 + outPos] = (byte)(((in[2 + inPos] & 0x7FFFFF) << 3 | (in[3 + inPos] & 0x7FFFFF) >>> 20) & 0xFF);
            out[9 + outPos] = (byte)((in[3 + inPos] & 0x7FFFFF) >>> 12 & 0xFF);
            out[10 + outPos] = (byte)((in[3 + inPos] & 0x7FFFFF) >>> 4 & 0xFF);
            out[11 + outPos] = (byte)(((in[3 + inPos] & 0x7FFFFF) << 4 | (in[4 + inPos] & 0x7FFFFF) >>> 19) & 0xFF);
            out[12 + outPos] = (byte)((in[4 + inPos] & 0x7FFFFF) >>> 11 & 0xFF);
            out[13 + outPos] = (byte)((in[4 + inPos] & 0x7FFFFF) >>> 3 & 0xFF);
            out[14 + outPos] = (byte)(((in[4 + inPos] & 0x7FFFFF) << 5 | (in[5 + inPos] & 0x7FFFFF) >>> 18) & 0xFF);
            out[15 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFF) >>> 10 & 0xFF);
            out[16 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFF) >>> 2 & 0xFF);
            out[17 + outPos] = (byte)(((in[5 + inPos] & 0x7FFFFF) << 6 | (in[6 + inPos] & 0x7FFFFF) >>> 17) & 0xFF);
            out[18 + outPos] = (byte)((in[6 + inPos] & 0x7FFFFF) >>> 9 & 0xFF);
            out[19 + outPos] = (byte)((in[6 + inPos] & 0x7FFFFF) >>> 1 & 0xFF);
            out[20 + outPos] = (byte)(((in[6 + inPos] & 0x7FFFFF) << 7 | (in[7 + inPos] & 0x7FFFFF) >>> 16) & 0xFF);
            out[21 + outPos] = (byte)((in[7 + inPos] & 0x7FFFFF) >>> 8 & 0xFF);
            out[22 + outPos] = (byte)(in[7 + inPos] & 0x7FFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFF) >>> 15 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFF) >>> 7 & 0xFF);
            out[2 + outPos] = (byte)(((in[0 + inPos] & 0x7FFFFF) << 1 | (in[1 + inPos] & 0x7FFFFF) >>> 22) & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0x7FFFFF) >>> 14 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x7FFFFF) >>> 6 & 0xFF);
            out[5 + outPos] = (byte)(((in[1 + inPos] & 0x7FFFFF) << 2 | (in[2 + inPos] & 0x7FFFFF) >>> 21) & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFF) >>> 13 & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFF) >>> 5 & 0xFF);
            out[8 + outPos] = (byte)(((in[2 + inPos] & 0x7FFFFF) << 3 | (in[3 + inPos] & 0x7FFFFF) >>> 20) & 0xFF);
            out[9 + outPos] = (byte)((in[3 + inPos] & 0x7FFFFF) >>> 12 & 0xFF);
            out[10 + outPos] = (byte)((in[3 + inPos] & 0x7FFFFF) >>> 4 & 0xFF);
            out[11 + outPos] = (byte)(((in[3 + inPos] & 0x7FFFFF) << 4 | (in[4 + inPos] & 0x7FFFFF) >>> 19) & 0xFF);
            out[12 + outPos] = (byte)((in[4 + inPos] & 0x7FFFFF) >>> 11 & 0xFF);
            out[13 + outPos] = (byte)((in[4 + inPos] & 0x7FFFFF) >>> 3 & 0xFF);
            out[14 + outPos] = (byte)(((in[4 + inPos] & 0x7FFFFF) << 5 | (in[5 + inPos] & 0x7FFFFF) >>> 18) & 0xFF);
            out[15 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFF) >>> 10 & 0xFF);
            out[16 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFF) >>> 2 & 0xFF);
            out[17 + outPos] = (byte)(((in[5 + inPos] & 0x7FFFFF) << 6 | (in[6 + inPos] & 0x7FFFFF) >>> 17) & 0xFF);
            out[18 + outPos] = (byte)((in[6 + inPos] & 0x7FFFFF) >>> 9 & 0xFF);
            out[19 + outPos] = (byte)((in[6 + inPos] & 0x7FFFFF) >>> 1 & 0xFF);
            out[20 + outPos] = (byte)(((in[6 + inPos] & 0x7FFFFF) << 7 | (in[7 + inPos] & 0x7FFFFF) >>> 16) & 0xFF);
            out[21 + outPos] = (byte)((in[7 + inPos] & 0x7FFFFF) >>> 8 & 0xFF);
            out[22 + outPos] = (byte)(in[7 + inPos] & 0x7FFFFF & 0xFF);
            out[23 + outPos] = (byte)((in[8 + inPos] & 0x7FFFFF) >>> 15 & 0xFF);
            out[24 + outPos] = (byte)((in[8 + inPos] & 0x7FFFFF) >>> 7 & 0xFF);
            out[25 + outPos] = (byte)(((in[8 + inPos] & 0x7FFFFF) << 1 | (in[9 + inPos] & 0x7FFFFF) >>> 22) & 0xFF);
            out[26 + outPos] = (byte)((in[9 + inPos] & 0x7FFFFF) >>> 14 & 0xFF);
            out[27 + outPos] = (byte)((in[9 + inPos] & 0x7FFFFF) >>> 6 & 0xFF);
            out[28 + outPos] = (byte)(((in[9 + inPos] & 0x7FFFFF) << 2 | (in[10 + inPos] & 0x7FFFFF) >>> 21) & 0xFF);
            out[29 + outPos] = (byte)((in[10 + inPos] & 0x7FFFFF) >>> 13 & 0xFF);
            out[30 + outPos] = (byte)((in[10 + inPos] & 0x7FFFFF) >>> 5 & 0xFF);
            out[31 + outPos] = (byte)(((in[10 + inPos] & 0x7FFFFF) << 3 | (in[11 + inPos] & 0x7FFFFF) >>> 20) & 0xFF);
            out[32 + outPos] = (byte)((in[11 + inPos] & 0x7FFFFF) >>> 12 & 0xFF);
            out[33 + outPos] = (byte)((in[11 + inPos] & 0x7FFFFF) >>> 4 & 0xFF);
            out[34 + outPos] = (byte)(((in[11 + inPos] & 0x7FFFFF) << 4 | (in[12 + inPos] & 0x7FFFFF) >>> 19) & 0xFF);
            out[35 + outPos] = (byte)((in[12 + inPos] & 0x7FFFFF) >>> 11 & 0xFF);
            out[36 + outPos] = (byte)((in[12 + inPos] & 0x7FFFFF) >>> 3 & 0xFF);
            out[37 + outPos] = (byte)(((in[12 + inPos] & 0x7FFFFF) << 5 | (in[13 + inPos] & 0x7FFFFF) >>> 18) & 0xFF);
            out[38 + outPos] = (byte)((in[13 + inPos] & 0x7FFFFF) >>> 10 & 0xFF);
            out[39 + outPos] = (byte)((in[13 + inPos] & 0x7FFFFF) >>> 2 & 0xFF);
            out[40 + outPos] = (byte)(((in[13 + inPos] & 0x7FFFFF) << 6 | (in[14 + inPos] & 0x7FFFFF) >>> 17) & 0xFF);
            out[41 + outPos] = (byte)((in[14 + inPos] & 0x7FFFFF) >>> 9 & 0xFF);
            out[42 + outPos] = (byte)((in[14 + inPos] & 0x7FFFFF) >>> 1 & 0xFF);
            out[43 + outPos] = (byte)(((in[14 + inPos] & 0x7FFFFF) << 7 | (in[15 + inPos] & 0x7FFFFF) >>> 16) & 0xFF);
            out[44 + outPos] = (byte)((in[15 + inPos] & 0x7FFFFF) >>> 8 & 0xFF);
            out[45 + outPos] = (byte)(in[15 + inPos] & 0x7FFFFF & 0xFF);
            out[46 + outPos] = (byte)((in[16 + inPos] & 0x7FFFFF) >>> 15 & 0xFF);
            out[47 + outPos] = (byte)((in[16 + inPos] & 0x7FFFFF) >>> 7 & 0xFF);
            out[48 + outPos] = (byte)(((in[16 + inPos] & 0x7FFFFF) << 1 | (in[17 + inPos] & 0x7FFFFF) >>> 22) & 0xFF);
            out[49 + outPos] = (byte)((in[17 + inPos] & 0x7FFFFF) >>> 14 & 0xFF);
            out[50 + outPos] = (byte)((in[17 + inPos] & 0x7FFFFF) >>> 6 & 0xFF);
            out[51 + outPos] = (byte)(((in[17 + inPos] & 0x7FFFFF) << 2 | (in[18 + inPos] & 0x7FFFFF) >>> 21) & 0xFF);
            out[52 + outPos] = (byte)((in[18 + inPos] & 0x7FFFFF) >>> 13 & 0xFF);
            out[53 + outPos] = (byte)((in[18 + inPos] & 0x7FFFFF) >>> 5 & 0xFF);
            out[54 + outPos] = (byte)(((in[18 + inPos] & 0x7FFFFF) << 3 | (in[19 + inPos] & 0x7FFFFF) >>> 20) & 0xFF);
            out[55 + outPos] = (byte)((in[19 + inPos] & 0x7FFFFF) >>> 12 & 0xFF);
            out[56 + outPos] = (byte)((in[19 + inPos] & 0x7FFFFF) >>> 4 & 0xFF);
            out[57 + outPos] = (byte)(((in[19 + inPos] & 0x7FFFFF) << 4 | (in[20 + inPos] & 0x7FFFFF) >>> 19) & 0xFF);
            out[58 + outPos] = (byte)((in[20 + inPos] & 0x7FFFFF) >>> 11 & 0xFF);
            out[59 + outPos] = (byte)((in[20 + inPos] & 0x7FFFFF) >>> 3 & 0xFF);
            out[60 + outPos] = (byte)(((in[20 + inPos] & 0x7FFFFF) << 5 | (in[21 + inPos] & 0x7FFFFF) >>> 18) & 0xFF);
            out[61 + outPos] = (byte)((in[21 + inPos] & 0x7FFFFF) >>> 10 & 0xFF);
            out[62 + outPos] = (byte)((in[21 + inPos] & 0x7FFFFF) >>> 2 & 0xFF);
            out[63 + outPos] = (byte)(((in[21 + inPos] & 0x7FFFFF) << 6 | (in[22 + inPos] & 0x7FFFFF) >>> 17) & 0xFF);
            out[64 + outPos] = (byte)((in[22 + inPos] & 0x7FFFFF) >>> 9 & 0xFF);
            out[65 + outPos] = (byte)((in[22 + inPos] & 0x7FFFFF) >>> 1 & 0xFF);
            out[66 + outPos] = (byte)(((in[22 + inPos] & 0x7FFFFF) << 7 | (in[23 + inPos] & 0x7FFFFF) >>> 16) & 0xFF);
            out[67 + outPos] = (byte)((in[23 + inPos] & 0x7FFFFF) >>> 8 & 0xFF);
            out[68 + outPos] = (byte)(in[23 + inPos] & 0x7FFFFF & 0xFF);
            out[69 + outPos] = (byte)((in[24 + inPos] & 0x7FFFFF) >>> 15 & 0xFF);
            out[70 + outPos] = (byte)((in[24 + inPos] & 0x7FFFFF) >>> 7 & 0xFF);
            out[71 + outPos] = (byte)(((in[24 + inPos] & 0x7FFFFF) << 1 | (in[25 + inPos] & 0x7FFFFF) >>> 22) & 0xFF);
            out[72 + outPos] = (byte)((in[25 + inPos] & 0x7FFFFF) >>> 14 & 0xFF);
            out[73 + outPos] = (byte)((in[25 + inPos] & 0x7FFFFF) >>> 6 & 0xFF);
            out[74 + outPos] = (byte)(((in[25 + inPos] & 0x7FFFFF) << 2 | (in[26 + inPos] & 0x7FFFFF) >>> 21) & 0xFF);
            out[75 + outPos] = (byte)((in[26 + inPos] & 0x7FFFFF) >>> 13 & 0xFF);
            out[76 + outPos] = (byte)((in[26 + inPos] & 0x7FFFFF) >>> 5 & 0xFF);
            out[77 + outPos] = (byte)(((in[26 + inPos] & 0x7FFFFF) << 3 | (in[27 + inPos] & 0x7FFFFF) >>> 20) & 0xFF);
            out[78 + outPos] = (byte)((in[27 + inPos] & 0x7FFFFF) >>> 12 & 0xFF);
            out[79 + outPos] = (byte)((in[27 + inPos] & 0x7FFFFF) >>> 4 & 0xFF);
            out[80 + outPos] = (byte)(((in[27 + inPos] & 0x7FFFFF) << 4 | (in[28 + inPos] & 0x7FFFFF) >>> 19) & 0xFF);
            out[81 + outPos] = (byte)((in[28 + inPos] & 0x7FFFFF) >>> 11 & 0xFF);
            out[82 + outPos] = (byte)((in[28 + inPos] & 0x7FFFFF) >>> 3 & 0xFF);
            out[83 + outPos] = (byte)(((in[28 + inPos] & 0x7FFFFF) << 5 | (in[29 + inPos] & 0x7FFFFF) >>> 18) & 0xFF);
            out[84 + outPos] = (byte)((in[29 + inPos] & 0x7FFFFF) >>> 10 & 0xFF);
            out[85 + outPos] = (byte)((in[29 + inPos] & 0x7FFFFF) >>> 2 & 0xFF);
            out[86 + outPos] = (byte)(((in[29 + inPos] & 0x7FFFFF) << 6 | (in[30 + inPos] & 0x7FFFFF) >>> 17) & 0xFF);
            out[87 + outPos] = (byte)((in[30 + inPos] & 0x7FFFFF) >>> 9 & 0xFF);
            out[88 + outPos] = (byte)((in[30 + inPos] & 0x7FFFFF) >>> 1 & 0xFF);
            out[89 + outPos] = (byte)(((in[30 + inPos] & 0x7FFFFF) << 7 | (in[31 + inPos] & 0x7FFFFF) >>> 16) & 0xFF);
            out[90 + outPos] = (byte)((in[31 + inPos] & 0x7FFFFF) >>> 8 & 0xFF);
            out[91 + outPos] = (byte)(in[31 + inPos] & 0x7FFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 15 & 0x7FFFFF) | ((in[1 + inPos] & 0xFF) << 7 & 0x7FFFFF) | ((in[2 + inPos] & 0xFF) >>> 1 & 0x7FFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 22 & 0x7FFFFF) | ((in[3 + inPos] & 0xFF) << 14 & 0x7FFFFF) | ((in[4 + inPos] & 0xFF) << 6 & 0x7FFFFF) | ((in[5 + inPos] & 0xFF) >>> 2 & 0x7FFFFF));
            out[2 + outPos] = (((in[5 + inPos] & 0xFF) << 21 & 0x7FFFFF) | ((in[6 + inPos] & 0xFF) << 13 & 0x7FFFFF) | ((in[7 + inPos] & 0xFF) << 5 & 0x7FFFFF) | ((in[8 + inPos] & 0xFF) >>> 3 & 0x7FFFFF));
            out[3 + outPos] = (((in[8 + inPos] & 0xFF) << 20 & 0x7FFFFF) | ((in[9 + inPos] & 0xFF) << 12 & 0x7FFFFF) | ((in[10 + inPos] & 0xFF) << 4 & 0x7FFFFF) | ((in[11 + inPos] & 0xFF) >>> 4 & 0x7FFFFF));
            out[4 + outPos] = (((in[11 + inPos] & 0xFF) << 19 & 0x7FFFFF) | ((in[12 + inPos] & 0xFF) << 11 & 0x7FFFFF) | ((in[13 + inPos] & 0xFF) << 3 & 0x7FFFFF) | ((in[14 + inPos] & 0xFF) >>> 5 & 0x7FFFFF));
            out[5 + outPos] = (((in[14 + inPos] & 0xFF) << 18 & 0x7FFFFF) | ((in[15 + inPos] & 0xFF) << 10 & 0x7FFFFF) | ((in[16 + inPos] & 0xFF) << 2 & 0x7FFFFF) | ((in[17 + inPos] & 0xFF) >>> 6 & 0x7FFFFF));
            out[6 + outPos] = (((in[17 + inPos] & 0xFF) << 17 & 0x7FFFFF) | ((in[18 + inPos] & 0xFF) << 9 & 0x7FFFFF) | ((in[19 + inPos] & 0xFF) << 1 & 0x7FFFFF) | ((in[20 + inPos] & 0xFF) >>> 7 & 0x7FFFFF));
            out[7 + outPos] = (((in[20 + inPos] & 0xFF) << 16 & 0x7FFFFF) | ((in[21 + inPos] & 0xFF) << 8 & 0x7FFFFF) | (in[22 + inPos] & 0xFF & 0x7FFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 15 & 0x7FFFFF) | ((in[1 + inPos] & 0xFF) << 7 & 0x7FFFFF) | ((in[2 + inPos] & 0xFF) >>> 1 & 0x7FFFFF));
            out[1 + outPos] = (((in[2 + inPos] & 0xFF) << 22 & 0x7FFFFF) | ((in[3 + inPos] & 0xFF) << 14 & 0x7FFFFF) | ((in[4 + inPos] & 0xFF) << 6 & 0x7FFFFF) | ((in[5 + inPos] & 0xFF) >>> 2 & 0x7FFFFF));
            out[2 + outPos] = (((in[5 + inPos] & 0xFF) << 21 & 0x7FFFFF) | ((in[6 + inPos] & 0xFF) << 13 & 0x7FFFFF) | ((in[7 + inPos] & 0xFF) << 5 & 0x7FFFFF) | ((in[8 + inPos] & 0xFF) >>> 3 & 0x7FFFFF));
            out[3 + outPos] = (((in[8 + inPos] & 0xFF) << 20 & 0x7FFFFF) | ((in[9 + inPos] & 0xFF) << 12 & 0x7FFFFF) | ((in[10 + inPos] & 0xFF) << 4 & 0x7FFFFF) | ((in[11 + inPos] & 0xFF) >>> 4 & 0x7FFFFF));
            out[4 + outPos] = (((in[11 + inPos] & 0xFF) << 19 & 0x7FFFFF) | ((in[12 + inPos] & 0xFF) << 11 & 0x7FFFFF) | ((in[13 + inPos] & 0xFF) << 3 & 0x7FFFFF) | ((in[14 + inPos] & 0xFF) >>> 5 & 0x7FFFFF));
            out[5 + outPos] = (((in[14 + inPos] & 0xFF) << 18 & 0x7FFFFF) | ((in[15 + inPos] & 0xFF) << 10 & 0x7FFFFF) | ((in[16 + inPos] & 0xFF) << 2 & 0x7FFFFF) | ((in[17 + inPos] & 0xFF) >>> 6 & 0x7FFFFF));
            out[6 + outPos] = (((in[17 + inPos] & 0xFF) << 17 & 0x7FFFFF) | ((in[18 + inPos] & 0xFF) << 9 & 0x7FFFFF) | ((in[19 + inPos] & 0xFF) << 1 & 0x7FFFFF) | ((in[20 + inPos] & 0xFF) >>> 7 & 0x7FFFFF));
            out[7 + outPos] = (((in[20 + inPos] & 0xFF) << 16 & 0x7FFFFF) | ((in[21 + inPos] & 0xFF) << 8 & 0x7FFFFF) | (in[22 + inPos] & 0xFF & 0x7FFFFF));
            out[8 + outPos] = (((in[23 + inPos] & 0xFF) << 15 & 0x7FFFFF) | ((in[24 + inPos] & 0xFF) << 7 & 0x7FFFFF) | ((in[25 + inPos] & 0xFF) >>> 1 & 0x7FFFFF));
            out[9 + outPos] = (((in[25 + inPos] & 0xFF) << 22 & 0x7FFFFF) | ((in[26 + inPos] & 0xFF) << 14 & 0x7FFFFF) | ((in[27 + inPos] & 0xFF) << 6 & 0x7FFFFF) | ((in[28 + inPos] & 0xFF) >>> 2 & 0x7FFFFF));
            out[10 + outPos] = (((in[28 + inPos] & 0xFF) << 21 & 0x7FFFFF) | ((in[29 + inPos] & 0xFF) << 13 & 0x7FFFFF) | ((in[30 + inPos] & 0xFF) << 5 & 0x7FFFFF) | ((in[31 + inPos] & 0xFF) >>> 3 & 0x7FFFFF));
            out[11 + outPos] = (((in[31 + inPos] & 0xFF) << 20 & 0x7FFFFF) | ((in[32 + inPos] & 0xFF) << 12 & 0x7FFFFF) | ((in[33 + inPos] & 0xFF) << 4 & 0x7FFFFF) | ((in[34 + inPos] & 0xFF) >>> 4 & 0x7FFFFF));
            out[12 + outPos] = (((in[34 + inPos] & 0xFF) << 19 & 0x7FFFFF) | ((in[35 + inPos] & 0xFF) << 11 & 0x7FFFFF) | ((in[36 + inPos] & 0xFF) << 3 & 0x7FFFFF) | ((in[37 + inPos] & 0xFF) >>> 5 & 0x7FFFFF));
            out[13 + outPos] = (((in[37 + inPos] & 0xFF) << 18 & 0x7FFFFF) | ((in[38 + inPos] & 0xFF) << 10 & 0x7FFFFF) | ((in[39 + inPos] & 0xFF) << 2 & 0x7FFFFF) | ((in[40 + inPos] & 0xFF) >>> 6 & 0x7FFFFF));
            out[14 + outPos] = (((in[40 + inPos] & 0xFF) << 17 & 0x7FFFFF) | ((in[41 + inPos] & 0xFF) << 9 & 0x7FFFFF) | ((in[42 + inPos] & 0xFF) << 1 & 0x7FFFFF) | ((in[43 + inPos] & 0xFF) >>> 7 & 0x7FFFFF));
            out[15 + outPos] = (((in[43 + inPos] & 0xFF) << 16 & 0x7FFFFF) | ((in[44 + inPos] & 0xFF) << 8 & 0x7FFFFF) | (in[45 + inPos] & 0xFF & 0x7FFFFF));
            out[16 + outPos] = (((in[46 + inPos] & 0xFF) << 15 & 0x7FFFFF) | ((in[47 + inPos] & 0xFF) << 7 & 0x7FFFFF) | ((in[48 + inPos] & 0xFF) >>> 1 & 0x7FFFFF));
            out[17 + outPos] = (((in[48 + inPos] & 0xFF) << 22 & 0x7FFFFF) | ((in[49 + inPos] & 0xFF) << 14 & 0x7FFFFF) | ((in[50 + inPos] & 0xFF) << 6 & 0x7FFFFF) | ((in[51 + inPos] & 0xFF) >>> 2 & 0x7FFFFF));
            out[18 + outPos] = (((in[51 + inPos] & 0xFF) << 21 & 0x7FFFFF) | ((in[52 + inPos] & 0xFF) << 13 & 0x7FFFFF) | ((in[53 + inPos] & 0xFF) << 5 & 0x7FFFFF) | ((in[54 + inPos] & 0xFF) >>> 3 & 0x7FFFFF));
            out[19 + outPos] = (((in[54 + inPos] & 0xFF) << 20 & 0x7FFFFF) | ((in[55 + inPos] & 0xFF) << 12 & 0x7FFFFF) | ((in[56 + inPos] & 0xFF) << 4 & 0x7FFFFF) | ((in[57 + inPos] & 0xFF) >>> 4 & 0x7FFFFF));
            out[20 + outPos] = (((in[57 + inPos] & 0xFF) << 19 & 0x7FFFFF) | ((in[58 + inPos] & 0xFF) << 11 & 0x7FFFFF) | ((in[59 + inPos] & 0xFF) << 3 & 0x7FFFFF) | ((in[60 + inPos] & 0xFF) >>> 5 & 0x7FFFFF));
            out[21 + outPos] = (((in[60 + inPos] & 0xFF) << 18 & 0x7FFFFF) | ((in[61 + inPos] & 0xFF) << 10 & 0x7FFFFF) | ((in[62 + inPos] & 0xFF) << 2 & 0x7FFFFF) | ((in[63 + inPos] & 0xFF) >>> 6 & 0x7FFFFF));
            out[22 + outPos] = (((in[63 + inPos] & 0xFF) << 17 & 0x7FFFFF) | ((in[64 + inPos] & 0xFF) << 9 & 0x7FFFFF) | ((in[65 + inPos] & 0xFF) << 1 & 0x7FFFFF) | ((in[66 + inPos] & 0xFF) >>> 7 & 0x7FFFFF));
            out[23 + outPos] = (((in[66 + inPos] & 0xFF) << 16 & 0x7FFFFF) | ((in[67 + inPos] & 0xFF) << 8 & 0x7FFFFF) | (in[68 + inPos] & 0xFF & 0x7FFFFF));
            out[24 + outPos] = (((in[69 + inPos] & 0xFF) << 15 & 0x7FFFFF) | ((in[70 + inPos] & 0xFF) << 7 & 0x7FFFFF) | ((in[71 + inPos] & 0xFF) >>> 1 & 0x7FFFFF));
            out[25 + outPos] = (((in[71 + inPos] & 0xFF) << 22 & 0x7FFFFF) | ((in[72 + inPos] & 0xFF) << 14 & 0x7FFFFF) | ((in[73 + inPos] & 0xFF) << 6 & 0x7FFFFF) | ((in[74 + inPos] & 0xFF) >>> 2 & 0x7FFFFF));
            out[26 + outPos] = (((in[74 + inPos] & 0xFF) << 21 & 0x7FFFFF) | ((in[75 + inPos] & 0xFF) << 13 & 0x7FFFFF) | ((in[76 + inPos] & 0xFF) << 5 & 0x7FFFFF) | ((in[77 + inPos] & 0xFF) >>> 3 & 0x7FFFFF));
            out[27 + outPos] = (((in[77 + inPos] & 0xFF) << 20 & 0x7FFFFF) | ((in[78 + inPos] & 0xFF) << 12 & 0x7FFFFF) | ((in[79 + inPos] & 0xFF) << 4 & 0x7FFFFF) | ((in[80 + inPos] & 0xFF) >>> 4 & 0x7FFFFF));
            out[28 + outPos] = (((in[80 + inPos] & 0xFF) << 19 & 0x7FFFFF) | ((in[81 + inPos] & 0xFF) << 11 & 0x7FFFFF) | ((in[82 + inPos] & 0xFF) << 3 & 0x7FFFFF) | ((in[83 + inPos] & 0xFF) >>> 5 & 0x7FFFFF));
            out[29 + outPos] = (((in[83 + inPos] & 0xFF) << 18 & 0x7FFFFF) | ((in[84 + inPos] & 0xFF) << 10 & 0x7FFFFF) | ((in[85 + inPos] & 0xFF) << 2 & 0x7FFFFF) | ((in[86 + inPos] & 0xFF) >>> 6 & 0x7FFFFF));
            out[30 + outPos] = (((in[86 + inPos] & 0xFF) << 17 & 0x7FFFFF) | ((in[87 + inPos] & 0xFF) << 9 & 0x7FFFFF) | ((in[88 + inPos] & 0xFF) << 1 & 0x7FFFFF) | ((in[89 + inPos] & 0xFF) >>> 7 & 0x7FFFFF));
            out[31 + outPos] = (((in[89 + inPos] & 0xFF) << 16 & 0x7FFFFF) | ((in[90 + inPos] & 0xFF) << 8 & 0x7FFFFF) | (in[91 + inPos] & 0xFF & 0x7FFFFF));
        }
    }
    
    private static final class Packer24 extends BytePacker
    {
        private Packer24() {
            super(24);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[2 + outPos] = (byte)(in[0 + inPos] & 0xFFFFFF & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[5 + outPos] = (byte)(in[1 + inPos] & 0xFFFFFF & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[8 + outPos] = (byte)(in[2 + inPos] & 0xFFFFFF & 0xFF);
            out[9 + outPos] = (byte)((in[3 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[10 + outPos] = (byte)((in[3 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[11 + outPos] = (byte)(in[3 + inPos] & 0xFFFFFF & 0xFF);
            out[12 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[13 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[14 + outPos] = (byte)(in[4 + inPos] & 0xFFFFFF & 0xFF);
            out[15 + outPos] = (byte)((in[5 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[16 + outPos] = (byte)((in[5 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[17 + outPos] = (byte)(in[5 + inPos] & 0xFFFFFF & 0xFF);
            out[18 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[19 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[20 + outPos] = (byte)(in[6 + inPos] & 0xFFFFFF & 0xFF);
            out[21 + outPos] = (byte)((in[7 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[22 + outPos] = (byte)((in[7 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[23 + outPos] = (byte)(in[7 + inPos] & 0xFFFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[2 + outPos] = (byte)(in[0 + inPos] & 0xFFFFFF & 0xFF);
            out[3 + outPos] = (byte)((in[1 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[5 + outPos] = (byte)(in[1 + inPos] & 0xFFFFFF & 0xFF);
            out[6 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[8 + outPos] = (byte)(in[2 + inPos] & 0xFFFFFF & 0xFF);
            out[9 + outPos] = (byte)((in[3 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[10 + outPos] = (byte)((in[3 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[11 + outPos] = (byte)(in[3 + inPos] & 0xFFFFFF & 0xFF);
            out[12 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[13 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[14 + outPos] = (byte)(in[4 + inPos] & 0xFFFFFF & 0xFF);
            out[15 + outPos] = (byte)((in[5 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[16 + outPos] = (byte)((in[5 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[17 + outPos] = (byte)(in[5 + inPos] & 0xFFFFFF & 0xFF);
            out[18 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[19 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[20 + outPos] = (byte)(in[6 + inPos] & 0xFFFFFF & 0xFF);
            out[21 + outPos] = (byte)((in[7 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[22 + outPos] = (byte)((in[7 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[23 + outPos] = (byte)(in[7 + inPos] & 0xFFFFFF & 0xFF);
            out[24 + outPos] = (byte)((in[8 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[25 + outPos] = (byte)((in[8 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[26 + outPos] = (byte)(in[8 + inPos] & 0xFFFFFF & 0xFF);
            out[27 + outPos] = (byte)((in[9 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[28 + outPos] = (byte)((in[9 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[29 + outPos] = (byte)(in[9 + inPos] & 0xFFFFFF & 0xFF);
            out[30 + outPos] = (byte)((in[10 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[31 + outPos] = (byte)((in[10 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[32 + outPos] = (byte)(in[10 + inPos] & 0xFFFFFF & 0xFF);
            out[33 + outPos] = (byte)((in[11 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[34 + outPos] = (byte)((in[11 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[35 + outPos] = (byte)(in[11 + inPos] & 0xFFFFFF & 0xFF);
            out[36 + outPos] = (byte)((in[12 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[37 + outPos] = (byte)((in[12 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[38 + outPos] = (byte)(in[12 + inPos] & 0xFFFFFF & 0xFF);
            out[39 + outPos] = (byte)((in[13 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[40 + outPos] = (byte)((in[13 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[41 + outPos] = (byte)(in[13 + inPos] & 0xFFFFFF & 0xFF);
            out[42 + outPos] = (byte)((in[14 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[43 + outPos] = (byte)((in[14 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[44 + outPos] = (byte)(in[14 + inPos] & 0xFFFFFF & 0xFF);
            out[45 + outPos] = (byte)((in[15 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[46 + outPos] = (byte)((in[15 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[47 + outPos] = (byte)(in[15 + inPos] & 0xFFFFFF & 0xFF);
            out[48 + outPos] = (byte)((in[16 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[49 + outPos] = (byte)((in[16 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[50 + outPos] = (byte)(in[16 + inPos] & 0xFFFFFF & 0xFF);
            out[51 + outPos] = (byte)((in[17 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[52 + outPos] = (byte)((in[17 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[53 + outPos] = (byte)(in[17 + inPos] & 0xFFFFFF & 0xFF);
            out[54 + outPos] = (byte)((in[18 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[55 + outPos] = (byte)((in[18 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[56 + outPos] = (byte)(in[18 + inPos] & 0xFFFFFF & 0xFF);
            out[57 + outPos] = (byte)((in[19 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[58 + outPos] = (byte)((in[19 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[59 + outPos] = (byte)(in[19 + inPos] & 0xFFFFFF & 0xFF);
            out[60 + outPos] = (byte)((in[20 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[61 + outPos] = (byte)((in[20 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[62 + outPos] = (byte)(in[20 + inPos] & 0xFFFFFF & 0xFF);
            out[63 + outPos] = (byte)((in[21 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[64 + outPos] = (byte)((in[21 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[65 + outPos] = (byte)(in[21 + inPos] & 0xFFFFFF & 0xFF);
            out[66 + outPos] = (byte)((in[22 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[67 + outPos] = (byte)((in[22 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[68 + outPos] = (byte)(in[22 + inPos] & 0xFFFFFF & 0xFF);
            out[69 + outPos] = (byte)((in[23 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[70 + outPos] = (byte)((in[23 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[71 + outPos] = (byte)(in[23 + inPos] & 0xFFFFFF & 0xFF);
            out[72 + outPos] = (byte)((in[24 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[73 + outPos] = (byte)((in[24 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[74 + outPos] = (byte)(in[24 + inPos] & 0xFFFFFF & 0xFF);
            out[75 + outPos] = (byte)((in[25 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[76 + outPos] = (byte)((in[25 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[77 + outPos] = (byte)(in[25 + inPos] & 0xFFFFFF & 0xFF);
            out[78 + outPos] = (byte)((in[26 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[79 + outPos] = (byte)((in[26 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[80 + outPos] = (byte)(in[26 + inPos] & 0xFFFFFF & 0xFF);
            out[81 + outPos] = (byte)((in[27 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[82 + outPos] = (byte)((in[27 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[83 + outPos] = (byte)(in[27 + inPos] & 0xFFFFFF & 0xFF);
            out[84 + outPos] = (byte)((in[28 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[85 + outPos] = (byte)((in[28 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[86 + outPos] = (byte)(in[28 + inPos] & 0xFFFFFF & 0xFF);
            out[87 + outPos] = (byte)((in[29 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[88 + outPos] = (byte)((in[29 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[89 + outPos] = (byte)(in[29 + inPos] & 0xFFFFFF & 0xFF);
            out[90 + outPos] = (byte)((in[30 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[91 + outPos] = (byte)((in[30 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[92 + outPos] = (byte)(in[30 + inPos] & 0xFFFFFF & 0xFF);
            out[93 + outPos] = (byte)((in[31 + inPos] & 0xFFFFFF) >>> 16 & 0xFF);
            out[94 + outPos] = (byte)((in[31 + inPos] & 0xFFFFFF) >>> 8 & 0xFF);
            out[95 + outPos] = (byte)(in[31 + inPos] & 0xFFFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[1 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[2 + inPos] & 0xFF & 0xFFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[4 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[5 + inPos] & 0xFF & 0xFFFFFF));
            out[2 + outPos] = (((in[6 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[7 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[8 + inPos] & 0xFF & 0xFFFFFF));
            out[3 + outPos] = (((in[9 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[10 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[11 + inPos] & 0xFF & 0xFFFFFF));
            out[4 + outPos] = (((in[12 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[13 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[14 + inPos] & 0xFF & 0xFFFFFF));
            out[5 + outPos] = (((in[15 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[16 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[17 + inPos] & 0xFF & 0xFFFFFF));
            out[6 + outPos] = (((in[18 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[19 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[20 + inPos] & 0xFF & 0xFFFFFF));
            out[7 + outPos] = (((in[21 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[22 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[23 + inPos] & 0xFF & 0xFFFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[1 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[2 + inPos] & 0xFF & 0xFFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[4 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[5 + inPos] & 0xFF & 0xFFFFFF));
            out[2 + outPos] = (((in[6 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[7 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[8 + inPos] & 0xFF & 0xFFFFFF));
            out[3 + outPos] = (((in[9 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[10 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[11 + inPos] & 0xFF & 0xFFFFFF));
            out[4 + outPos] = (((in[12 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[13 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[14 + inPos] & 0xFF & 0xFFFFFF));
            out[5 + outPos] = (((in[15 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[16 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[17 + inPos] & 0xFF & 0xFFFFFF));
            out[6 + outPos] = (((in[18 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[19 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[20 + inPos] & 0xFF & 0xFFFFFF));
            out[7 + outPos] = (((in[21 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[22 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[23 + inPos] & 0xFF & 0xFFFFFF));
            out[8 + outPos] = (((in[24 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[25 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[26 + inPos] & 0xFF & 0xFFFFFF));
            out[9 + outPos] = (((in[27 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[28 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[29 + inPos] & 0xFF & 0xFFFFFF));
            out[10 + outPos] = (((in[30 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[31 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[32 + inPos] & 0xFF & 0xFFFFFF));
            out[11 + outPos] = (((in[33 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[34 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[35 + inPos] & 0xFF & 0xFFFFFF));
            out[12 + outPos] = (((in[36 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[37 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[38 + inPos] & 0xFF & 0xFFFFFF));
            out[13 + outPos] = (((in[39 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[40 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[41 + inPos] & 0xFF & 0xFFFFFF));
            out[14 + outPos] = (((in[42 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[43 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[44 + inPos] & 0xFF & 0xFFFFFF));
            out[15 + outPos] = (((in[45 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[46 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[47 + inPos] & 0xFF & 0xFFFFFF));
            out[16 + outPos] = (((in[48 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[49 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[50 + inPos] & 0xFF & 0xFFFFFF));
            out[17 + outPos] = (((in[51 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[52 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[53 + inPos] & 0xFF & 0xFFFFFF));
            out[18 + outPos] = (((in[54 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[55 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[56 + inPos] & 0xFF & 0xFFFFFF));
            out[19 + outPos] = (((in[57 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[58 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[59 + inPos] & 0xFF & 0xFFFFFF));
            out[20 + outPos] = (((in[60 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[61 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[62 + inPos] & 0xFF & 0xFFFFFF));
            out[21 + outPos] = (((in[63 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[64 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[65 + inPos] & 0xFF & 0xFFFFFF));
            out[22 + outPos] = (((in[66 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[67 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[68 + inPos] & 0xFF & 0xFFFFFF));
            out[23 + outPos] = (((in[69 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[70 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[71 + inPos] & 0xFF & 0xFFFFFF));
            out[24 + outPos] = (((in[72 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[73 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[74 + inPos] & 0xFF & 0xFFFFFF));
            out[25 + outPos] = (((in[75 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[76 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[77 + inPos] & 0xFF & 0xFFFFFF));
            out[26 + outPos] = (((in[78 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[79 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[80 + inPos] & 0xFF & 0xFFFFFF));
            out[27 + outPos] = (((in[81 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[82 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[83 + inPos] & 0xFF & 0xFFFFFF));
            out[28 + outPos] = (((in[84 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[85 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[86 + inPos] & 0xFF & 0xFFFFFF));
            out[29 + outPos] = (((in[87 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[88 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[89 + inPos] & 0xFF & 0xFFFFFF));
            out[30 + outPos] = (((in[90 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[91 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[92 + inPos] & 0xFF & 0xFFFFFF));
            out[31 + outPos] = (((in[93 + inPos] & 0xFF) << 16 & 0xFFFFFF) | ((in[94 + inPos] & 0xFF) << 8 & 0xFFFFFF) | (in[95 + inPos] & 0xFF & 0xFFFFFF));
        }
    }
    
    private static final class Packer25 extends BytePacker
    {
        private Packer25() {
            super(25);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFF) >>> 17 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFF) >>> 9 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFF) >>> 1 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x1FFFFFF) << 7 | (in[1 + inPos] & 0x1FFFFFF) >>> 18) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFF) >>> 10 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFF) >>> 2 & 0xFF);
            out[6 + outPos] = (byte)(((in[1 + inPos] & 0x1FFFFFF) << 6 | (in[2 + inPos] & 0x1FFFFFF) >>> 19) & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFFF) >>> 11 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFFF) >>> 3 & 0xFF);
            out[9 + outPos] = (byte)(((in[2 + inPos] & 0x1FFFFFF) << 5 | (in[3 + inPos] & 0x1FFFFFF) >>> 20) & 0xFF);
            out[10 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFF) >>> 12 & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFF) >>> 4 & 0xFF);
            out[12 + outPos] = (byte)(((in[3 + inPos] & 0x1FFFFFF) << 4 | (in[4 + inPos] & 0x1FFFFFF) >>> 21) & 0xFF);
            out[13 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFF) >>> 13 & 0xFF);
            out[14 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFF) >>> 5 & 0xFF);
            out[15 + outPos] = (byte)(((in[4 + inPos] & 0x1FFFFFF) << 3 | (in[5 + inPos] & 0x1FFFFFF) >>> 22) & 0xFF);
            out[16 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFFF) >>> 14 & 0xFF);
            out[17 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFFF) >>> 6 & 0xFF);
            out[18 + outPos] = (byte)(((in[5 + inPos] & 0x1FFFFFF) << 2 | (in[6 + inPos] & 0x1FFFFFF) >>> 23) & 0xFF);
            out[19 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFF) >>> 15 & 0xFF);
            out[20 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFF) >>> 7 & 0xFF);
            out[21 + outPos] = (byte)(((in[6 + inPos] & 0x1FFFFFF) << 1 | (in[7 + inPos] & 0x1FFFFFF) >>> 24) & 0xFF);
            out[22 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFFF) >>> 16 & 0xFF);
            out[23 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFFF) >>> 8 & 0xFF);
            out[24 + outPos] = (byte)(in[7 + inPos] & 0x1FFFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFF) >>> 17 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFF) >>> 9 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFF) >>> 1 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x1FFFFFF) << 7 | (in[1 + inPos] & 0x1FFFFFF) >>> 18) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFF) >>> 10 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFF) >>> 2 & 0xFF);
            out[6 + outPos] = (byte)(((in[1 + inPos] & 0x1FFFFFF) << 6 | (in[2 + inPos] & 0x1FFFFFF) >>> 19) & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFFF) >>> 11 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFFF) >>> 3 & 0xFF);
            out[9 + outPos] = (byte)(((in[2 + inPos] & 0x1FFFFFF) << 5 | (in[3 + inPos] & 0x1FFFFFF) >>> 20) & 0xFF);
            out[10 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFF) >>> 12 & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFF) >>> 4 & 0xFF);
            out[12 + outPos] = (byte)(((in[3 + inPos] & 0x1FFFFFF) << 4 | (in[4 + inPos] & 0x1FFFFFF) >>> 21) & 0xFF);
            out[13 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFF) >>> 13 & 0xFF);
            out[14 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFF) >>> 5 & 0xFF);
            out[15 + outPos] = (byte)(((in[4 + inPos] & 0x1FFFFFF) << 3 | (in[5 + inPos] & 0x1FFFFFF) >>> 22) & 0xFF);
            out[16 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFFF) >>> 14 & 0xFF);
            out[17 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFFF) >>> 6 & 0xFF);
            out[18 + outPos] = (byte)(((in[5 + inPos] & 0x1FFFFFF) << 2 | (in[6 + inPos] & 0x1FFFFFF) >>> 23) & 0xFF);
            out[19 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFF) >>> 15 & 0xFF);
            out[20 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFF) >>> 7 & 0xFF);
            out[21 + outPos] = (byte)(((in[6 + inPos] & 0x1FFFFFF) << 1 | (in[7 + inPos] & 0x1FFFFFF) >>> 24) & 0xFF);
            out[22 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFFF) >>> 16 & 0xFF);
            out[23 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFFF) >>> 8 & 0xFF);
            out[24 + outPos] = (byte)(in[7 + inPos] & 0x1FFFFFF & 0xFF);
            out[25 + outPos] = (byte)((in[8 + inPos] & 0x1FFFFFF) >>> 17 & 0xFF);
            out[26 + outPos] = (byte)((in[8 + inPos] & 0x1FFFFFF) >>> 9 & 0xFF);
            out[27 + outPos] = (byte)((in[8 + inPos] & 0x1FFFFFF) >>> 1 & 0xFF);
            out[28 + outPos] = (byte)(((in[8 + inPos] & 0x1FFFFFF) << 7 | (in[9 + inPos] & 0x1FFFFFF) >>> 18) & 0xFF);
            out[29 + outPos] = (byte)((in[9 + inPos] & 0x1FFFFFF) >>> 10 & 0xFF);
            out[30 + outPos] = (byte)((in[9 + inPos] & 0x1FFFFFF) >>> 2 & 0xFF);
            out[31 + outPos] = (byte)(((in[9 + inPos] & 0x1FFFFFF) << 6 | (in[10 + inPos] & 0x1FFFFFF) >>> 19) & 0xFF);
            out[32 + outPos] = (byte)((in[10 + inPos] & 0x1FFFFFF) >>> 11 & 0xFF);
            out[33 + outPos] = (byte)((in[10 + inPos] & 0x1FFFFFF) >>> 3 & 0xFF);
            out[34 + outPos] = (byte)(((in[10 + inPos] & 0x1FFFFFF) << 5 | (in[11 + inPos] & 0x1FFFFFF) >>> 20) & 0xFF);
            out[35 + outPos] = (byte)((in[11 + inPos] & 0x1FFFFFF) >>> 12 & 0xFF);
            out[36 + outPos] = (byte)((in[11 + inPos] & 0x1FFFFFF) >>> 4 & 0xFF);
            out[37 + outPos] = (byte)(((in[11 + inPos] & 0x1FFFFFF) << 4 | (in[12 + inPos] & 0x1FFFFFF) >>> 21) & 0xFF);
            out[38 + outPos] = (byte)((in[12 + inPos] & 0x1FFFFFF) >>> 13 & 0xFF);
            out[39 + outPos] = (byte)((in[12 + inPos] & 0x1FFFFFF) >>> 5 & 0xFF);
            out[40 + outPos] = (byte)(((in[12 + inPos] & 0x1FFFFFF) << 3 | (in[13 + inPos] & 0x1FFFFFF) >>> 22) & 0xFF);
            out[41 + outPos] = (byte)((in[13 + inPos] & 0x1FFFFFF) >>> 14 & 0xFF);
            out[42 + outPos] = (byte)((in[13 + inPos] & 0x1FFFFFF) >>> 6 & 0xFF);
            out[43 + outPos] = (byte)(((in[13 + inPos] & 0x1FFFFFF) << 2 | (in[14 + inPos] & 0x1FFFFFF) >>> 23) & 0xFF);
            out[44 + outPos] = (byte)((in[14 + inPos] & 0x1FFFFFF) >>> 15 & 0xFF);
            out[45 + outPos] = (byte)((in[14 + inPos] & 0x1FFFFFF) >>> 7 & 0xFF);
            out[46 + outPos] = (byte)(((in[14 + inPos] & 0x1FFFFFF) << 1 | (in[15 + inPos] & 0x1FFFFFF) >>> 24) & 0xFF);
            out[47 + outPos] = (byte)((in[15 + inPos] & 0x1FFFFFF) >>> 16 & 0xFF);
            out[48 + outPos] = (byte)((in[15 + inPos] & 0x1FFFFFF) >>> 8 & 0xFF);
            out[49 + outPos] = (byte)(in[15 + inPos] & 0x1FFFFFF & 0xFF);
            out[50 + outPos] = (byte)((in[16 + inPos] & 0x1FFFFFF) >>> 17 & 0xFF);
            out[51 + outPos] = (byte)((in[16 + inPos] & 0x1FFFFFF) >>> 9 & 0xFF);
            out[52 + outPos] = (byte)((in[16 + inPos] & 0x1FFFFFF) >>> 1 & 0xFF);
            out[53 + outPos] = (byte)(((in[16 + inPos] & 0x1FFFFFF) << 7 | (in[17 + inPos] & 0x1FFFFFF) >>> 18) & 0xFF);
            out[54 + outPos] = (byte)((in[17 + inPos] & 0x1FFFFFF) >>> 10 & 0xFF);
            out[55 + outPos] = (byte)((in[17 + inPos] & 0x1FFFFFF) >>> 2 & 0xFF);
            out[56 + outPos] = (byte)(((in[17 + inPos] & 0x1FFFFFF) << 6 | (in[18 + inPos] & 0x1FFFFFF) >>> 19) & 0xFF);
            out[57 + outPos] = (byte)((in[18 + inPos] & 0x1FFFFFF) >>> 11 & 0xFF);
            out[58 + outPos] = (byte)((in[18 + inPos] & 0x1FFFFFF) >>> 3 & 0xFF);
            out[59 + outPos] = (byte)(((in[18 + inPos] & 0x1FFFFFF) << 5 | (in[19 + inPos] & 0x1FFFFFF) >>> 20) & 0xFF);
            out[60 + outPos] = (byte)((in[19 + inPos] & 0x1FFFFFF) >>> 12 & 0xFF);
            out[61 + outPos] = (byte)((in[19 + inPos] & 0x1FFFFFF) >>> 4 & 0xFF);
            out[62 + outPos] = (byte)(((in[19 + inPos] & 0x1FFFFFF) << 4 | (in[20 + inPos] & 0x1FFFFFF) >>> 21) & 0xFF);
            out[63 + outPos] = (byte)((in[20 + inPos] & 0x1FFFFFF) >>> 13 & 0xFF);
            out[64 + outPos] = (byte)((in[20 + inPos] & 0x1FFFFFF) >>> 5 & 0xFF);
            out[65 + outPos] = (byte)(((in[20 + inPos] & 0x1FFFFFF) << 3 | (in[21 + inPos] & 0x1FFFFFF) >>> 22) & 0xFF);
            out[66 + outPos] = (byte)((in[21 + inPos] & 0x1FFFFFF) >>> 14 & 0xFF);
            out[67 + outPos] = (byte)((in[21 + inPos] & 0x1FFFFFF) >>> 6 & 0xFF);
            out[68 + outPos] = (byte)(((in[21 + inPos] & 0x1FFFFFF) << 2 | (in[22 + inPos] & 0x1FFFFFF) >>> 23) & 0xFF);
            out[69 + outPos] = (byte)((in[22 + inPos] & 0x1FFFFFF) >>> 15 & 0xFF);
            out[70 + outPos] = (byte)((in[22 + inPos] & 0x1FFFFFF) >>> 7 & 0xFF);
            out[71 + outPos] = (byte)(((in[22 + inPos] & 0x1FFFFFF) << 1 | (in[23 + inPos] & 0x1FFFFFF) >>> 24) & 0xFF);
            out[72 + outPos] = (byte)((in[23 + inPos] & 0x1FFFFFF) >>> 16 & 0xFF);
            out[73 + outPos] = (byte)((in[23 + inPos] & 0x1FFFFFF) >>> 8 & 0xFF);
            out[74 + outPos] = (byte)(in[23 + inPos] & 0x1FFFFFF & 0xFF);
            out[75 + outPos] = (byte)((in[24 + inPos] & 0x1FFFFFF) >>> 17 & 0xFF);
            out[76 + outPos] = (byte)((in[24 + inPos] & 0x1FFFFFF) >>> 9 & 0xFF);
            out[77 + outPos] = (byte)((in[24 + inPos] & 0x1FFFFFF) >>> 1 & 0xFF);
            out[78 + outPos] = (byte)(((in[24 + inPos] & 0x1FFFFFF) << 7 | (in[25 + inPos] & 0x1FFFFFF) >>> 18) & 0xFF);
            out[79 + outPos] = (byte)((in[25 + inPos] & 0x1FFFFFF) >>> 10 & 0xFF);
            out[80 + outPos] = (byte)((in[25 + inPos] & 0x1FFFFFF) >>> 2 & 0xFF);
            out[81 + outPos] = (byte)(((in[25 + inPos] & 0x1FFFFFF) << 6 | (in[26 + inPos] & 0x1FFFFFF) >>> 19) & 0xFF);
            out[82 + outPos] = (byte)((in[26 + inPos] & 0x1FFFFFF) >>> 11 & 0xFF);
            out[83 + outPos] = (byte)((in[26 + inPos] & 0x1FFFFFF) >>> 3 & 0xFF);
            out[84 + outPos] = (byte)(((in[26 + inPos] & 0x1FFFFFF) << 5 | (in[27 + inPos] & 0x1FFFFFF) >>> 20) & 0xFF);
            out[85 + outPos] = (byte)((in[27 + inPos] & 0x1FFFFFF) >>> 12 & 0xFF);
            out[86 + outPos] = (byte)((in[27 + inPos] & 0x1FFFFFF) >>> 4 & 0xFF);
            out[87 + outPos] = (byte)(((in[27 + inPos] & 0x1FFFFFF) << 4 | (in[28 + inPos] & 0x1FFFFFF) >>> 21) & 0xFF);
            out[88 + outPos] = (byte)((in[28 + inPos] & 0x1FFFFFF) >>> 13 & 0xFF);
            out[89 + outPos] = (byte)((in[28 + inPos] & 0x1FFFFFF) >>> 5 & 0xFF);
            out[90 + outPos] = (byte)(((in[28 + inPos] & 0x1FFFFFF) << 3 | (in[29 + inPos] & 0x1FFFFFF) >>> 22) & 0xFF);
            out[91 + outPos] = (byte)((in[29 + inPos] & 0x1FFFFFF) >>> 14 & 0xFF);
            out[92 + outPos] = (byte)((in[29 + inPos] & 0x1FFFFFF) >>> 6 & 0xFF);
            out[93 + outPos] = (byte)(((in[29 + inPos] & 0x1FFFFFF) << 2 | (in[30 + inPos] & 0x1FFFFFF) >>> 23) & 0xFF);
            out[94 + outPos] = (byte)((in[30 + inPos] & 0x1FFFFFF) >>> 15 & 0xFF);
            out[95 + outPos] = (byte)((in[30 + inPos] & 0x1FFFFFF) >>> 7 & 0xFF);
            out[96 + outPos] = (byte)(((in[30 + inPos] & 0x1FFFFFF) << 1 | (in[31 + inPos] & 0x1FFFFFF) >>> 24) & 0xFF);
            out[97 + outPos] = (byte)((in[31 + inPos] & 0x1FFFFFF) >>> 16 & 0xFF);
            out[98 + outPos] = (byte)((in[31 + inPos] & 0x1FFFFFF) >>> 8 & 0xFF);
            out[99 + outPos] = (byte)(in[31 + inPos] & 0x1FFFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 17 & 0x1FFFFFF) | ((in[1 + inPos] & 0xFF) << 9 & 0x1FFFFFF) | ((in[2 + inPos] & 0xFF) << 1 & 0x1FFFFFF) | ((in[3 + inPos] & 0xFF) >>> 7 & 0x1FFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 18 & 0x1FFFFFF) | ((in[4 + inPos] & 0xFF) << 10 & 0x1FFFFFF) | ((in[5 + inPos] & 0xFF) << 2 & 0x1FFFFFF) | ((in[6 + inPos] & 0xFF) >>> 6 & 0x1FFFFFF));
            out[2 + outPos] = (((in[6 + inPos] & 0xFF) << 19 & 0x1FFFFFF) | ((in[7 + inPos] & 0xFF) << 11 & 0x1FFFFFF) | ((in[8 + inPos] & 0xFF) << 3 & 0x1FFFFFF) | ((in[9 + inPos] & 0xFF) >>> 5 & 0x1FFFFFF));
            out[3 + outPos] = (((in[9 + inPos] & 0xFF) << 20 & 0x1FFFFFF) | ((in[10 + inPos] & 0xFF) << 12 & 0x1FFFFFF) | ((in[11 + inPos] & 0xFF) << 4 & 0x1FFFFFF) | ((in[12 + inPos] & 0xFF) >>> 4 & 0x1FFFFFF));
            out[4 + outPos] = (((in[12 + inPos] & 0xFF) << 21 & 0x1FFFFFF) | ((in[13 + inPos] & 0xFF) << 13 & 0x1FFFFFF) | ((in[14 + inPos] & 0xFF) << 5 & 0x1FFFFFF) | ((in[15 + inPos] & 0xFF) >>> 3 & 0x1FFFFFF));
            out[5 + outPos] = (((in[15 + inPos] & 0xFF) << 22 & 0x1FFFFFF) | ((in[16 + inPos] & 0xFF) << 14 & 0x1FFFFFF) | ((in[17 + inPos] & 0xFF) << 6 & 0x1FFFFFF) | ((in[18 + inPos] & 0xFF) >>> 2 & 0x1FFFFFF));
            out[6 + outPos] = (((in[18 + inPos] & 0xFF) << 23 & 0x1FFFFFF) | ((in[19 + inPos] & 0xFF) << 15 & 0x1FFFFFF) | ((in[20 + inPos] & 0xFF) << 7 & 0x1FFFFFF) | ((in[21 + inPos] & 0xFF) >>> 1 & 0x1FFFFFF));
            out[7 + outPos] = (((in[21 + inPos] & 0xFF) << 24 & 0x1FFFFFF) | ((in[22 + inPos] & 0xFF) << 16 & 0x1FFFFFF) | ((in[23 + inPos] & 0xFF) << 8 & 0x1FFFFFF) | (in[24 + inPos] & 0xFF & 0x1FFFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 17 & 0x1FFFFFF) | ((in[1 + inPos] & 0xFF) << 9 & 0x1FFFFFF) | ((in[2 + inPos] & 0xFF) << 1 & 0x1FFFFFF) | ((in[3 + inPos] & 0xFF) >>> 7 & 0x1FFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 18 & 0x1FFFFFF) | ((in[4 + inPos] & 0xFF) << 10 & 0x1FFFFFF) | ((in[5 + inPos] & 0xFF) << 2 & 0x1FFFFFF) | ((in[6 + inPos] & 0xFF) >>> 6 & 0x1FFFFFF));
            out[2 + outPos] = (((in[6 + inPos] & 0xFF) << 19 & 0x1FFFFFF) | ((in[7 + inPos] & 0xFF) << 11 & 0x1FFFFFF) | ((in[8 + inPos] & 0xFF) << 3 & 0x1FFFFFF) | ((in[9 + inPos] & 0xFF) >>> 5 & 0x1FFFFFF));
            out[3 + outPos] = (((in[9 + inPos] & 0xFF) << 20 & 0x1FFFFFF) | ((in[10 + inPos] & 0xFF) << 12 & 0x1FFFFFF) | ((in[11 + inPos] & 0xFF) << 4 & 0x1FFFFFF) | ((in[12 + inPos] & 0xFF) >>> 4 & 0x1FFFFFF));
            out[4 + outPos] = (((in[12 + inPos] & 0xFF) << 21 & 0x1FFFFFF) | ((in[13 + inPos] & 0xFF) << 13 & 0x1FFFFFF) | ((in[14 + inPos] & 0xFF) << 5 & 0x1FFFFFF) | ((in[15 + inPos] & 0xFF) >>> 3 & 0x1FFFFFF));
            out[5 + outPos] = (((in[15 + inPos] & 0xFF) << 22 & 0x1FFFFFF) | ((in[16 + inPos] & 0xFF) << 14 & 0x1FFFFFF) | ((in[17 + inPos] & 0xFF) << 6 & 0x1FFFFFF) | ((in[18 + inPos] & 0xFF) >>> 2 & 0x1FFFFFF));
            out[6 + outPos] = (((in[18 + inPos] & 0xFF) << 23 & 0x1FFFFFF) | ((in[19 + inPos] & 0xFF) << 15 & 0x1FFFFFF) | ((in[20 + inPos] & 0xFF) << 7 & 0x1FFFFFF) | ((in[21 + inPos] & 0xFF) >>> 1 & 0x1FFFFFF));
            out[7 + outPos] = (((in[21 + inPos] & 0xFF) << 24 & 0x1FFFFFF) | ((in[22 + inPos] & 0xFF) << 16 & 0x1FFFFFF) | ((in[23 + inPos] & 0xFF) << 8 & 0x1FFFFFF) | (in[24 + inPos] & 0xFF & 0x1FFFFFF));
            out[8 + outPos] = (((in[25 + inPos] & 0xFF) << 17 & 0x1FFFFFF) | ((in[26 + inPos] & 0xFF) << 9 & 0x1FFFFFF) | ((in[27 + inPos] & 0xFF) << 1 & 0x1FFFFFF) | ((in[28 + inPos] & 0xFF) >>> 7 & 0x1FFFFFF));
            out[9 + outPos] = (((in[28 + inPos] & 0xFF) << 18 & 0x1FFFFFF) | ((in[29 + inPos] & 0xFF) << 10 & 0x1FFFFFF) | ((in[30 + inPos] & 0xFF) << 2 & 0x1FFFFFF) | ((in[31 + inPos] & 0xFF) >>> 6 & 0x1FFFFFF));
            out[10 + outPos] = (((in[31 + inPos] & 0xFF) << 19 & 0x1FFFFFF) | ((in[32 + inPos] & 0xFF) << 11 & 0x1FFFFFF) | ((in[33 + inPos] & 0xFF) << 3 & 0x1FFFFFF) | ((in[34 + inPos] & 0xFF) >>> 5 & 0x1FFFFFF));
            out[11 + outPos] = (((in[34 + inPos] & 0xFF) << 20 & 0x1FFFFFF) | ((in[35 + inPos] & 0xFF) << 12 & 0x1FFFFFF) | ((in[36 + inPos] & 0xFF) << 4 & 0x1FFFFFF) | ((in[37 + inPos] & 0xFF) >>> 4 & 0x1FFFFFF));
            out[12 + outPos] = (((in[37 + inPos] & 0xFF) << 21 & 0x1FFFFFF) | ((in[38 + inPos] & 0xFF) << 13 & 0x1FFFFFF) | ((in[39 + inPos] & 0xFF) << 5 & 0x1FFFFFF) | ((in[40 + inPos] & 0xFF) >>> 3 & 0x1FFFFFF));
            out[13 + outPos] = (((in[40 + inPos] & 0xFF) << 22 & 0x1FFFFFF) | ((in[41 + inPos] & 0xFF) << 14 & 0x1FFFFFF) | ((in[42 + inPos] & 0xFF) << 6 & 0x1FFFFFF) | ((in[43 + inPos] & 0xFF) >>> 2 & 0x1FFFFFF));
            out[14 + outPos] = (((in[43 + inPos] & 0xFF) << 23 & 0x1FFFFFF) | ((in[44 + inPos] & 0xFF) << 15 & 0x1FFFFFF) | ((in[45 + inPos] & 0xFF) << 7 & 0x1FFFFFF) | ((in[46 + inPos] & 0xFF) >>> 1 & 0x1FFFFFF));
            out[15 + outPos] = (((in[46 + inPos] & 0xFF) << 24 & 0x1FFFFFF) | ((in[47 + inPos] & 0xFF) << 16 & 0x1FFFFFF) | ((in[48 + inPos] & 0xFF) << 8 & 0x1FFFFFF) | (in[49 + inPos] & 0xFF & 0x1FFFFFF));
            out[16 + outPos] = (((in[50 + inPos] & 0xFF) << 17 & 0x1FFFFFF) | ((in[51 + inPos] & 0xFF) << 9 & 0x1FFFFFF) | ((in[52 + inPos] & 0xFF) << 1 & 0x1FFFFFF) | ((in[53 + inPos] & 0xFF) >>> 7 & 0x1FFFFFF));
            out[17 + outPos] = (((in[53 + inPos] & 0xFF) << 18 & 0x1FFFFFF) | ((in[54 + inPos] & 0xFF) << 10 & 0x1FFFFFF) | ((in[55 + inPos] & 0xFF) << 2 & 0x1FFFFFF) | ((in[56 + inPos] & 0xFF) >>> 6 & 0x1FFFFFF));
            out[18 + outPos] = (((in[56 + inPos] & 0xFF) << 19 & 0x1FFFFFF) | ((in[57 + inPos] & 0xFF) << 11 & 0x1FFFFFF) | ((in[58 + inPos] & 0xFF) << 3 & 0x1FFFFFF) | ((in[59 + inPos] & 0xFF) >>> 5 & 0x1FFFFFF));
            out[19 + outPos] = (((in[59 + inPos] & 0xFF) << 20 & 0x1FFFFFF) | ((in[60 + inPos] & 0xFF) << 12 & 0x1FFFFFF) | ((in[61 + inPos] & 0xFF) << 4 & 0x1FFFFFF) | ((in[62 + inPos] & 0xFF) >>> 4 & 0x1FFFFFF));
            out[20 + outPos] = (((in[62 + inPos] & 0xFF) << 21 & 0x1FFFFFF) | ((in[63 + inPos] & 0xFF) << 13 & 0x1FFFFFF) | ((in[64 + inPos] & 0xFF) << 5 & 0x1FFFFFF) | ((in[65 + inPos] & 0xFF) >>> 3 & 0x1FFFFFF));
            out[21 + outPos] = (((in[65 + inPos] & 0xFF) << 22 & 0x1FFFFFF) | ((in[66 + inPos] & 0xFF) << 14 & 0x1FFFFFF) | ((in[67 + inPos] & 0xFF) << 6 & 0x1FFFFFF) | ((in[68 + inPos] & 0xFF) >>> 2 & 0x1FFFFFF));
            out[22 + outPos] = (((in[68 + inPos] & 0xFF) << 23 & 0x1FFFFFF) | ((in[69 + inPos] & 0xFF) << 15 & 0x1FFFFFF) | ((in[70 + inPos] & 0xFF) << 7 & 0x1FFFFFF) | ((in[71 + inPos] & 0xFF) >>> 1 & 0x1FFFFFF));
            out[23 + outPos] = (((in[71 + inPos] & 0xFF) << 24 & 0x1FFFFFF) | ((in[72 + inPos] & 0xFF) << 16 & 0x1FFFFFF) | ((in[73 + inPos] & 0xFF) << 8 & 0x1FFFFFF) | (in[74 + inPos] & 0xFF & 0x1FFFFFF));
            out[24 + outPos] = (((in[75 + inPos] & 0xFF) << 17 & 0x1FFFFFF) | ((in[76 + inPos] & 0xFF) << 9 & 0x1FFFFFF) | ((in[77 + inPos] & 0xFF) << 1 & 0x1FFFFFF) | ((in[78 + inPos] & 0xFF) >>> 7 & 0x1FFFFFF));
            out[25 + outPos] = (((in[78 + inPos] & 0xFF) << 18 & 0x1FFFFFF) | ((in[79 + inPos] & 0xFF) << 10 & 0x1FFFFFF) | ((in[80 + inPos] & 0xFF) << 2 & 0x1FFFFFF) | ((in[81 + inPos] & 0xFF) >>> 6 & 0x1FFFFFF));
            out[26 + outPos] = (((in[81 + inPos] & 0xFF) << 19 & 0x1FFFFFF) | ((in[82 + inPos] & 0xFF) << 11 & 0x1FFFFFF) | ((in[83 + inPos] & 0xFF) << 3 & 0x1FFFFFF) | ((in[84 + inPos] & 0xFF) >>> 5 & 0x1FFFFFF));
            out[27 + outPos] = (((in[84 + inPos] & 0xFF) << 20 & 0x1FFFFFF) | ((in[85 + inPos] & 0xFF) << 12 & 0x1FFFFFF) | ((in[86 + inPos] & 0xFF) << 4 & 0x1FFFFFF) | ((in[87 + inPos] & 0xFF) >>> 4 & 0x1FFFFFF));
            out[28 + outPos] = (((in[87 + inPos] & 0xFF) << 21 & 0x1FFFFFF) | ((in[88 + inPos] & 0xFF) << 13 & 0x1FFFFFF) | ((in[89 + inPos] & 0xFF) << 5 & 0x1FFFFFF) | ((in[90 + inPos] & 0xFF) >>> 3 & 0x1FFFFFF));
            out[29 + outPos] = (((in[90 + inPos] & 0xFF) << 22 & 0x1FFFFFF) | ((in[91 + inPos] & 0xFF) << 14 & 0x1FFFFFF) | ((in[92 + inPos] & 0xFF) << 6 & 0x1FFFFFF) | ((in[93 + inPos] & 0xFF) >>> 2 & 0x1FFFFFF));
            out[30 + outPos] = (((in[93 + inPos] & 0xFF) << 23 & 0x1FFFFFF) | ((in[94 + inPos] & 0xFF) << 15 & 0x1FFFFFF) | ((in[95 + inPos] & 0xFF) << 7 & 0x1FFFFFF) | ((in[96 + inPos] & 0xFF) >>> 1 & 0x1FFFFFF));
            out[31 + outPos] = (((in[96 + inPos] & 0xFF) << 24 & 0x1FFFFFF) | ((in[97 + inPos] & 0xFF) << 16 & 0x1FFFFFF) | ((in[98 + inPos] & 0xFF) << 8 & 0x1FFFFFF) | (in[99 + inPos] & 0xFF & 0x1FFFFFF));
        }
    }
    
    private static final class Packer26 extends BytePacker
    {
        private Packer26() {
            super(26);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x3FFFFFF) << 6 | (in[1 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[6 + outPos] = (byte)(((in[1 + inPos] & 0x3FFFFFF) << 4 | (in[2 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[9 + outPos] = (byte)(((in[2 + inPos] & 0x3FFFFFF) << 2 | (in[3 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[10 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[12 + outPos] = (byte)(in[3 + inPos] & 0x3FFFFFF & 0xFF);
            out[13 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[14 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[16 + outPos] = (byte)(((in[4 + inPos] & 0x3FFFFFF) << 6 | (in[5 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[17 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[18 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[19 + outPos] = (byte)(((in[5 + inPos] & 0x3FFFFFF) << 4 | (in[6 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[20 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[21 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[22 + outPos] = (byte)(((in[6 + inPos] & 0x3FFFFFF) << 2 | (in[7 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[23 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[24 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[25 + outPos] = (byte)(in[7 + inPos] & 0x3FFFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x3FFFFFF) << 6 | (in[1 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[6 + outPos] = (byte)(((in[1 + inPos] & 0x3FFFFFF) << 4 | (in[2 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[9 + outPos] = (byte)(((in[2 + inPos] & 0x3FFFFFF) << 2 | (in[3 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[10 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[12 + outPos] = (byte)(in[3 + inPos] & 0x3FFFFFF & 0xFF);
            out[13 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[14 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[16 + outPos] = (byte)(((in[4 + inPos] & 0x3FFFFFF) << 6 | (in[5 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[17 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[18 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[19 + outPos] = (byte)(((in[5 + inPos] & 0x3FFFFFF) << 4 | (in[6 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[20 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[21 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[22 + outPos] = (byte)(((in[6 + inPos] & 0x3FFFFFF) << 2 | (in[7 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[23 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[24 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[25 + outPos] = (byte)(in[7 + inPos] & 0x3FFFFFF & 0xFF);
            out[26 + outPos] = (byte)((in[8 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[27 + outPos] = (byte)((in[8 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[28 + outPos] = (byte)((in[8 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[29 + outPos] = (byte)(((in[8 + inPos] & 0x3FFFFFF) << 6 | (in[9 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[30 + outPos] = (byte)((in[9 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[31 + outPos] = (byte)((in[9 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[32 + outPos] = (byte)(((in[9 + inPos] & 0x3FFFFFF) << 4 | (in[10 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[33 + outPos] = (byte)((in[10 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[34 + outPos] = (byte)((in[10 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[35 + outPos] = (byte)(((in[10 + inPos] & 0x3FFFFFF) << 2 | (in[11 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[36 + outPos] = (byte)((in[11 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[37 + outPos] = (byte)((in[11 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[38 + outPos] = (byte)(in[11 + inPos] & 0x3FFFFFF & 0xFF);
            out[39 + outPos] = (byte)((in[12 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[40 + outPos] = (byte)((in[12 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[41 + outPos] = (byte)((in[12 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[42 + outPos] = (byte)(((in[12 + inPos] & 0x3FFFFFF) << 6 | (in[13 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[43 + outPos] = (byte)((in[13 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[44 + outPos] = (byte)((in[13 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[45 + outPos] = (byte)(((in[13 + inPos] & 0x3FFFFFF) << 4 | (in[14 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[46 + outPos] = (byte)((in[14 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[47 + outPos] = (byte)((in[14 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[48 + outPos] = (byte)(((in[14 + inPos] & 0x3FFFFFF) << 2 | (in[15 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[49 + outPos] = (byte)((in[15 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[50 + outPos] = (byte)((in[15 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[51 + outPos] = (byte)(in[15 + inPos] & 0x3FFFFFF & 0xFF);
            out[52 + outPos] = (byte)((in[16 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[53 + outPos] = (byte)((in[16 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[54 + outPos] = (byte)((in[16 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[55 + outPos] = (byte)(((in[16 + inPos] & 0x3FFFFFF) << 6 | (in[17 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[56 + outPos] = (byte)((in[17 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[57 + outPos] = (byte)((in[17 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[58 + outPos] = (byte)(((in[17 + inPos] & 0x3FFFFFF) << 4 | (in[18 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[59 + outPos] = (byte)((in[18 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[60 + outPos] = (byte)((in[18 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[61 + outPos] = (byte)(((in[18 + inPos] & 0x3FFFFFF) << 2 | (in[19 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[62 + outPos] = (byte)((in[19 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[63 + outPos] = (byte)((in[19 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[64 + outPos] = (byte)(in[19 + inPos] & 0x3FFFFFF & 0xFF);
            out[65 + outPos] = (byte)((in[20 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[66 + outPos] = (byte)((in[20 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[67 + outPos] = (byte)((in[20 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[68 + outPos] = (byte)(((in[20 + inPos] & 0x3FFFFFF) << 6 | (in[21 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[69 + outPos] = (byte)((in[21 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[70 + outPos] = (byte)((in[21 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[71 + outPos] = (byte)(((in[21 + inPos] & 0x3FFFFFF) << 4 | (in[22 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[72 + outPos] = (byte)((in[22 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[73 + outPos] = (byte)((in[22 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[74 + outPos] = (byte)(((in[22 + inPos] & 0x3FFFFFF) << 2 | (in[23 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[75 + outPos] = (byte)((in[23 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[76 + outPos] = (byte)((in[23 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[77 + outPos] = (byte)(in[23 + inPos] & 0x3FFFFFF & 0xFF);
            out[78 + outPos] = (byte)((in[24 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[79 + outPos] = (byte)((in[24 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[80 + outPos] = (byte)((in[24 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[81 + outPos] = (byte)(((in[24 + inPos] & 0x3FFFFFF) << 6 | (in[25 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[82 + outPos] = (byte)((in[25 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[83 + outPos] = (byte)((in[25 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[84 + outPos] = (byte)(((in[25 + inPos] & 0x3FFFFFF) << 4 | (in[26 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[85 + outPos] = (byte)((in[26 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[86 + outPos] = (byte)((in[26 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[87 + outPos] = (byte)(((in[26 + inPos] & 0x3FFFFFF) << 2 | (in[27 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[88 + outPos] = (byte)((in[27 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[89 + outPos] = (byte)((in[27 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[90 + outPos] = (byte)(in[27 + inPos] & 0x3FFFFFF & 0xFF);
            out[91 + outPos] = (byte)((in[28 + inPos] & 0x3FFFFFF) >>> 18 & 0xFF);
            out[92 + outPos] = (byte)((in[28 + inPos] & 0x3FFFFFF) >>> 10 & 0xFF);
            out[93 + outPos] = (byte)((in[28 + inPos] & 0x3FFFFFF) >>> 2 & 0xFF);
            out[94 + outPos] = (byte)(((in[28 + inPos] & 0x3FFFFFF) << 6 | (in[29 + inPos] & 0x3FFFFFF) >>> 20) & 0xFF);
            out[95 + outPos] = (byte)((in[29 + inPos] & 0x3FFFFFF) >>> 12 & 0xFF);
            out[96 + outPos] = (byte)((in[29 + inPos] & 0x3FFFFFF) >>> 4 & 0xFF);
            out[97 + outPos] = (byte)(((in[29 + inPos] & 0x3FFFFFF) << 4 | (in[30 + inPos] & 0x3FFFFFF) >>> 22) & 0xFF);
            out[98 + outPos] = (byte)((in[30 + inPos] & 0x3FFFFFF) >>> 14 & 0xFF);
            out[99 + outPos] = (byte)((in[30 + inPos] & 0x3FFFFFF) >>> 6 & 0xFF);
            out[100 + outPos] = (byte)(((in[30 + inPos] & 0x3FFFFFF) << 2 | (in[31 + inPos] & 0x3FFFFFF) >>> 24) & 0xFF);
            out[101 + outPos] = (byte)((in[31 + inPos] & 0x3FFFFFF) >>> 16 & 0xFF);
            out[102 + outPos] = (byte)((in[31 + inPos] & 0x3FFFFFF) >>> 8 & 0xFF);
            out[103 + outPos] = (byte)(in[31 + inPos] & 0x3FFFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[1 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[2 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[3 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[4 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[5 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[6 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[2 + outPos] = (((in[6 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[7 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[8 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[9 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[3 + outPos] = (((in[9 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[10 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[11 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[12 + inPos] & 0xFF & 0x3FFFFFF));
            out[4 + outPos] = (((in[13 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[14 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[15 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[16 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[5 + outPos] = (((in[16 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[17 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[18 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[19 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[6 + outPos] = (((in[19 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[20 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[21 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[22 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[7 + outPos] = (((in[22 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[23 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[24 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[25 + inPos] & 0xFF & 0x3FFFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[1 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[2 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[3 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[4 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[5 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[6 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[2 + outPos] = (((in[6 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[7 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[8 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[9 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[3 + outPos] = (((in[9 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[10 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[11 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[12 + inPos] & 0xFF & 0x3FFFFFF));
            out[4 + outPos] = (((in[13 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[14 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[15 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[16 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[5 + outPos] = (((in[16 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[17 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[18 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[19 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[6 + outPos] = (((in[19 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[20 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[21 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[22 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[7 + outPos] = (((in[22 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[23 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[24 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[25 + inPos] & 0xFF & 0x3FFFFFF));
            out[8 + outPos] = (((in[26 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[27 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[28 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[29 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[9 + outPos] = (((in[29 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[30 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[31 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[32 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[10 + outPos] = (((in[32 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[33 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[34 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[35 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[11 + outPos] = (((in[35 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[36 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[37 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[38 + inPos] & 0xFF & 0x3FFFFFF));
            out[12 + outPos] = (((in[39 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[40 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[41 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[42 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[13 + outPos] = (((in[42 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[43 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[44 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[45 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[14 + outPos] = (((in[45 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[46 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[47 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[48 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[15 + outPos] = (((in[48 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[49 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[50 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[51 + inPos] & 0xFF & 0x3FFFFFF));
            out[16 + outPos] = (((in[52 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[53 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[54 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[55 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[17 + outPos] = (((in[55 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[56 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[57 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[58 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[18 + outPos] = (((in[58 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[59 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[60 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[61 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[19 + outPos] = (((in[61 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[62 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[63 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[64 + inPos] & 0xFF & 0x3FFFFFF));
            out[20 + outPos] = (((in[65 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[66 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[67 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[68 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[21 + outPos] = (((in[68 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[69 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[70 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[71 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[22 + outPos] = (((in[71 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[72 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[73 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[74 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[23 + outPos] = (((in[74 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[75 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[76 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[77 + inPos] & 0xFF & 0x3FFFFFF));
            out[24 + outPos] = (((in[78 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[79 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[80 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[81 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[25 + outPos] = (((in[81 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[82 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[83 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[84 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[26 + outPos] = (((in[84 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[85 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[86 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[87 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[27 + outPos] = (((in[87 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[88 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[89 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[90 + inPos] & 0xFF & 0x3FFFFFF));
            out[28 + outPos] = (((in[91 + inPos] & 0xFF) << 18 & 0x3FFFFFF) | ((in[92 + inPos] & 0xFF) << 10 & 0x3FFFFFF) | ((in[93 + inPos] & 0xFF) << 2 & 0x3FFFFFF) | ((in[94 + inPos] & 0xFF) >>> 6 & 0x3FFFFFF));
            out[29 + outPos] = (((in[94 + inPos] & 0xFF) << 20 & 0x3FFFFFF) | ((in[95 + inPos] & 0xFF) << 12 & 0x3FFFFFF) | ((in[96 + inPos] & 0xFF) << 4 & 0x3FFFFFF) | ((in[97 + inPos] & 0xFF) >>> 4 & 0x3FFFFFF));
            out[30 + outPos] = (((in[97 + inPos] & 0xFF) << 22 & 0x3FFFFFF) | ((in[98 + inPos] & 0xFF) << 14 & 0x3FFFFFF) | ((in[99 + inPos] & 0xFF) << 6 & 0x3FFFFFF) | ((in[100 + inPos] & 0xFF) >>> 2 & 0x3FFFFFF));
            out[31 + outPos] = (((in[100 + inPos] & 0xFF) << 24 & 0x3FFFFFF) | ((in[101 + inPos] & 0xFF) << 16 & 0x3FFFFFF) | ((in[102 + inPos] & 0xFF) << 8 & 0x3FFFFFF) | (in[103 + inPos] & 0xFF & 0x3FFFFFF));
        }
    }
    
    private static final class Packer27 extends BytePacker
    {
        private Packer27() {
            super(27);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFFF) >>> 19 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFFF) >>> 11 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFFF) >>> 3 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x7FFFFFF) << 5 | (in[1 + inPos] & 0x7FFFFFF) >>> 22) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x7FFFFFF) >>> 14 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x7FFFFFF) >>> 6 & 0xFF);
            out[6 + outPos] = (byte)(((in[1 + inPos] & 0x7FFFFFF) << 2 | (in[2 + inPos] & 0x7FFFFFF) >>> 25) & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFFF) >>> 17 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFFF) >>> 9 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFFF) >>> 1 & 0xFF);
            out[10 + outPos] = (byte)(((in[2 + inPos] & 0x7FFFFFF) << 7 | (in[3 + inPos] & 0x7FFFFFF) >>> 20) & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0x7FFFFFF) >>> 12 & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & 0x7FFFFFF) >>> 4 & 0xFF);
            out[13 + outPos] = (byte)(((in[3 + inPos] & 0x7FFFFFF) << 4 | (in[4 + inPos] & 0x7FFFFFF) >>> 23) & 0xFF);
            out[14 + outPos] = (byte)((in[4 + inPos] & 0x7FFFFFF) >>> 15 & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0x7FFFFFF) >>> 7 & 0xFF);
            out[16 + outPos] = (byte)(((in[4 + inPos] & 0x7FFFFFF) << 1 | (in[5 + inPos] & 0x7FFFFFF) >>> 26) & 0xFF);
            out[17 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFFF) >>> 18 & 0xFF);
            out[18 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFFF) >>> 10 & 0xFF);
            out[19 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFFF) >>> 2 & 0xFF);
            out[20 + outPos] = (byte)(((in[5 + inPos] & 0x7FFFFFF) << 6 | (in[6 + inPos] & 0x7FFFFFF) >>> 21) & 0xFF);
            out[21 + outPos] = (byte)((in[6 + inPos] & 0x7FFFFFF) >>> 13 & 0xFF);
            out[22 + outPos] = (byte)((in[6 + inPos] & 0x7FFFFFF) >>> 5 & 0xFF);
            out[23 + outPos] = (byte)(((in[6 + inPos] & 0x7FFFFFF) << 3 | (in[7 + inPos] & 0x7FFFFFF) >>> 24) & 0xFF);
            out[24 + outPos] = (byte)((in[7 + inPos] & 0x7FFFFFF) >>> 16 & 0xFF);
            out[25 + outPos] = (byte)((in[7 + inPos] & 0x7FFFFFF) >>> 8 & 0xFF);
            out[26 + outPos] = (byte)(in[7 + inPos] & 0x7FFFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFFF) >>> 19 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFFF) >>> 11 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x7FFFFFF) >>> 3 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x7FFFFFF) << 5 | (in[1 + inPos] & 0x7FFFFFF) >>> 22) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x7FFFFFF) >>> 14 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x7FFFFFF) >>> 6 & 0xFF);
            out[6 + outPos] = (byte)(((in[1 + inPos] & 0x7FFFFFF) << 2 | (in[2 + inPos] & 0x7FFFFFF) >>> 25) & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFFF) >>> 17 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFFF) >>> 9 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & 0x7FFFFFF) >>> 1 & 0xFF);
            out[10 + outPos] = (byte)(((in[2 + inPos] & 0x7FFFFFF) << 7 | (in[3 + inPos] & 0x7FFFFFF) >>> 20) & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0x7FFFFFF) >>> 12 & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & 0x7FFFFFF) >>> 4 & 0xFF);
            out[13 + outPos] = (byte)(((in[3 + inPos] & 0x7FFFFFF) << 4 | (in[4 + inPos] & 0x7FFFFFF) >>> 23) & 0xFF);
            out[14 + outPos] = (byte)((in[4 + inPos] & 0x7FFFFFF) >>> 15 & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0x7FFFFFF) >>> 7 & 0xFF);
            out[16 + outPos] = (byte)(((in[4 + inPos] & 0x7FFFFFF) << 1 | (in[5 + inPos] & 0x7FFFFFF) >>> 26) & 0xFF);
            out[17 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFFF) >>> 18 & 0xFF);
            out[18 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFFF) >>> 10 & 0xFF);
            out[19 + outPos] = (byte)((in[5 + inPos] & 0x7FFFFFF) >>> 2 & 0xFF);
            out[20 + outPos] = (byte)(((in[5 + inPos] & 0x7FFFFFF) << 6 | (in[6 + inPos] & 0x7FFFFFF) >>> 21) & 0xFF);
            out[21 + outPos] = (byte)((in[6 + inPos] & 0x7FFFFFF) >>> 13 & 0xFF);
            out[22 + outPos] = (byte)((in[6 + inPos] & 0x7FFFFFF) >>> 5 & 0xFF);
            out[23 + outPos] = (byte)(((in[6 + inPos] & 0x7FFFFFF) << 3 | (in[7 + inPos] & 0x7FFFFFF) >>> 24) & 0xFF);
            out[24 + outPos] = (byte)((in[7 + inPos] & 0x7FFFFFF) >>> 16 & 0xFF);
            out[25 + outPos] = (byte)((in[7 + inPos] & 0x7FFFFFF) >>> 8 & 0xFF);
            out[26 + outPos] = (byte)(in[7 + inPos] & 0x7FFFFFF & 0xFF);
            out[27 + outPos] = (byte)((in[8 + inPos] & 0x7FFFFFF) >>> 19 & 0xFF);
            out[28 + outPos] = (byte)((in[8 + inPos] & 0x7FFFFFF) >>> 11 & 0xFF);
            out[29 + outPos] = (byte)((in[8 + inPos] & 0x7FFFFFF) >>> 3 & 0xFF);
            out[30 + outPos] = (byte)(((in[8 + inPos] & 0x7FFFFFF) << 5 | (in[9 + inPos] & 0x7FFFFFF) >>> 22) & 0xFF);
            out[31 + outPos] = (byte)((in[9 + inPos] & 0x7FFFFFF) >>> 14 & 0xFF);
            out[32 + outPos] = (byte)((in[9 + inPos] & 0x7FFFFFF) >>> 6 & 0xFF);
            out[33 + outPos] = (byte)(((in[9 + inPos] & 0x7FFFFFF) << 2 | (in[10 + inPos] & 0x7FFFFFF) >>> 25) & 0xFF);
            out[34 + outPos] = (byte)((in[10 + inPos] & 0x7FFFFFF) >>> 17 & 0xFF);
            out[35 + outPos] = (byte)((in[10 + inPos] & 0x7FFFFFF) >>> 9 & 0xFF);
            out[36 + outPos] = (byte)((in[10 + inPos] & 0x7FFFFFF) >>> 1 & 0xFF);
            out[37 + outPos] = (byte)(((in[10 + inPos] & 0x7FFFFFF) << 7 | (in[11 + inPos] & 0x7FFFFFF) >>> 20) & 0xFF);
            out[38 + outPos] = (byte)((in[11 + inPos] & 0x7FFFFFF) >>> 12 & 0xFF);
            out[39 + outPos] = (byte)((in[11 + inPos] & 0x7FFFFFF) >>> 4 & 0xFF);
            out[40 + outPos] = (byte)(((in[11 + inPos] & 0x7FFFFFF) << 4 | (in[12 + inPos] & 0x7FFFFFF) >>> 23) & 0xFF);
            out[41 + outPos] = (byte)((in[12 + inPos] & 0x7FFFFFF) >>> 15 & 0xFF);
            out[42 + outPos] = (byte)((in[12 + inPos] & 0x7FFFFFF) >>> 7 & 0xFF);
            out[43 + outPos] = (byte)(((in[12 + inPos] & 0x7FFFFFF) << 1 | (in[13 + inPos] & 0x7FFFFFF) >>> 26) & 0xFF);
            out[44 + outPos] = (byte)((in[13 + inPos] & 0x7FFFFFF) >>> 18 & 0xFF);
            out[45 + outPos] = (byte)((in[13 + inPos] & 0x7FFFFFF) >>> 10 & 0xFF);
            out[46 + outPos] = (byte)((in[13 + inPos] & 0x7FFFFFF) >>> 2 & 0xFF);
            out[47 + outPos] = (byte)(((in[13 + inPos] & 0x7FFFFFF) << 6 | (in[14 + inPos] & 0x7FFFFFF) >>> 21) & 0xFF);
            out[48 + outPos] = (byte)((in[14 + inPos] & 0x7FFFFFF) >>> 13 & 0xFF);
            out[49 + outPos] = (byte)((in[14 + inPos] & 0x7FFFFFF) >>> 5 & 0xFF);
            out[50 + outPos] = (byte)(((in[14 + inPos] & 0x7FFFFFF) << 3 | (in[15 + inPos] & 0x7FFFFFF) >>> 24) & 0xFF);
            out[51 + outPos] = (byte)((in[15 + inPos] & 0x7FFFFFF) >>> 16 & 0xFF);
            out[52 + outPos] = (byte)((in[15 + inPos] & 0x7FFFFFF) >>> 8 & 0xFF);
            out[53 + outPos] = (byte)(in[15 + inPos] & 0x7FFFFFF & 0xFF);
            out[54 + outPos] = (byte)((in[16 + inPos] & 0x7FFFFFF) >>> 19 & 0xFF);
            out[55 + outPos] = (byte)((in[16 + inPos] & 0x7FFFFFF) >>> 11 & 0xFF);
            out[56 + outPos] = (byte)((in[16 + inPos] & 0x7FFFFFF) >>> 3 & 0xFF);
            out[57 + outPos] = (byte)(((in[16 + inPos] & 0x7FFFFFF) << 5 | (in[17 + inPos] & 0x7FFFFFF) >>> 22) & 0xFF);
            out[58 + outPos] = (byte)((in[17 + inPos] & 0x7FFFFFF) >>> 14 & 0xFF);
            out[59 + outPos] = (byte)((in[17 + inPos] & 0x7FFFFFF) >>> 6 & 0xFF);
            out[60 + outPos] = (byte)(((in[17 + inPos] & 0x7FFFFFF) << 2 | (in[18 + inPos] & 0x7FFFFFF) >>> 25) & 0xFF);
            out[61 + outPos] = (byte)((in[18 + inPos] & 0x7FFFFFF) >>> 17 & 0xFF);
            out[62 + outPos] = (byte)((in[18 + inPos] & 0x7FFFFFF) >>> 9 & 0xFF);
            out[63 + outPos] = (byte)((in[18 + inPos] & 0x7FFFFFF) >>> 1 & 0xFF);
            out[64 + outPos] = (byte)(((in[18 + inPos] & 0x7FFFFFF) << 7 | (in[19 + inPos] & 0x7FFFFFF) >>> 20) & 0xFF);
            out[65 + outPos] = (byte)((in[19 + inPos] & 0x7FFFFFF) >>> 12 & 0xFF);
            out[66 + outPos] = (byte)((in[19 + inPos] & 0x7FFFFFF) >>> 4 & 0xFF);
            out[67 + outPos] = (byte)(((in[19 + inPos] & 0x7FFFFFF) << 4 | (in[20 + inPos] & 0x7FFFFFF) >>> 23) & 0xFF);
            out[68 + outPos] = (byte)((in[20 + inPos] & 0x7FFFFFF) >>> 15 & 0xFF);
            out[69 + outPos] = (byte)((in[20 + inPos] & 0x7FFFFFF) >>> 7 & 0xFF);
            out[70 + outPos] = (byte)(((in[20 + inPos] & 0x7FFFFFF) << 1 | (in[21 + inPos] & 0x7FFFFFF) >>> 26) & 0xFF);
            out[71 + outPos] = (byte)((in[21 + inPos] & 0x7FFFFFF) >>> 18 & 0xFF);
            out[72 + outPos] = (byte)((in[21 + inPos] & 0x7FFFFFF) >>> 10 & 0xFF);
            out[73 + outPos] = (byte)((in[21 + inPos] & 0x7FFFFFF) >>> 2 & 0xFF);
            out[74 + outPos] = (byte)(((in[21 + inPos] & 0x7FFFFFF) << 6 | (in[22 + inPos] & 0x7FFFFFF) >>> 21) & 0xFF);
            out[75 + outPos] = (byte)((in[22 + inPos] & 0x7FFFFFF) >>> 13 & 0xFF);
            out[76 + outPos] = (byte)((in[22 + inPos] & 0x7FFFFFF) >>> 5 & 0xFF);
            out[77 + outPos] = (byte)(((in[22 + inPos] & 0x7FFFFFF) << 3 | (in[23 + inPos] & 0x7FFFFFF) >>> 24) & 0xFF);
            out[78 + outPos] = (byte)((in[23 + inPos] & 0x7FFFFFF) >>> 16 & 0xFF);
            out[79 + outPos] = (byte)((in[23 + inPos] & 0x7FFFFFF) >>> 8 & 0xFF);
            out[80 + outPos] = (byte)(in[23 + inPos] & 0x7FFFFFF & 0xFF);
            out[81 + outPos] = (byte)((in[24 + inPos] & 0x7FFFFFF) >>> 19 & 0xFF);
            out[82 + outPos] = (byte)((in[24 + inPos] & 0x7FFFFFF) >>> 11 & 0xFF);
            out[83 + outPos] = (byte)((in[24 + inPos] & 0x7FFFFFF) >>> 3 & 0xFF);
            out[84 + outPos] = (byte)(((in[24 + inPos] & 0x7FFFFFF) << 5 | (in[25 + inPos] & 0x7FFFFFF) >>> 22) & 0xFF);
            out[85 + outPos] = (byte)((in[25 + inPos] & 0x7FFFFFF) >>> 14 & 0xFF);
            out[86 + outPos] = (byte)((in[25 + inPos] & 0x7FFFFFF) >>> 6 & 0xFF);
            out[87 + outPos] = (byte)(((in[25 + inPos] & 0x7FFFFFF) << 2 | (in[26 + inPos] & 0x7FFFFFF) >>> 25) & 0xFF);
            out[88 + outPos] = (byte)((in[26 + inPos] & 0x7FFFFFF) >>> 17 & 0xFF);
            out[89 + outPos] = (byte)((in[26 + inPos] & 0x7FFFFFF) >>> 9 & 0xFF);
            out[90 + outPos] = (byte)((in[26 + inPos] & 0x7FFFFFF) >>> 1 & 0xFF);
            out[91 + outPos] = (byte)(((in[26 + inPos] & 0x7FFFFFF) << 7 | (in[27 + inPos] & 0x7FFFFFF) >>> 20) & 0xFF);
            out[92 + outPos] = (byte)((in[27 + inPos] & 0x7FFFFFF) >>> 12 & 0xFF);
            out[93 + outPos] = (byte)((in[27 + inPos] & 0x7FFFFFF) >>> 4 & 0xFF);
            out[94 + outPos] = (byte)(((in[27 + inPos] & 0x7FFFFFF) << 4 | (in[28 + inPos] & 0x7FFFFFF) >>> 23) & 0xFF);
            out[95 + outPos] = (byte)((in[28 + inPos] & 0x7FFFFFF) >>> 15 & 0xFF);
            out[96 + outPos] = (byte)((in[28 + inPos] & 0x7FFFFFF) >>> 7 & 0xFF);
            out[97 + outPos] = (byte)(((in[28 + inPos] & 0x7FFFFFF) << 1 | (in[29 + inPos] & 0x7FFFFFF) >>> 26) & 0xFF);
            out[98 + outPos] = (byte)((in[29 + inPos] & 0x7FFFFFF) >>> 18 & 0xFF);
            out[99 + outPos] = (byte)((in[29 + inPos] & 0x7FFFFFF) >>> 10 & 0xFF);
            out[100 + outPos] = (byte)((in[29 + inPos] & 0x7FFFFFF) >>> 2 & 0xFF);
            out[101 + outPos] = (byte)(((in[29 + inPos] & 0x7FFFFFF) << 6 | (in[30 + inPos] & 0x7FFFFFF) >>> 21) & 0xFF);
            out[102 + outPos] = (byte)((in[30 + inPos] & 0x7FFFFFF) >>> 13 & 0xFF);
            out[103 + outPos] = (byte)((in[30 + inPos] & 0x7FFFFFF) >>> 5 & 0xFF);
            out[104 + outPos] = (byte)(((in[30 + inPos] & 0x7FFFFFF) << 3 | (in[31 + inPos] & 0x7FFFFFF) >>> 24) & 0xFF);
            out[105 + outPos] = (byte)((in[31 + inPos] & 0x7FFFFFF) >>> 16 & 0xFF);
            out[106 + outPos] = (byte)((in[31 + inPos] & 0x7FFFFFF) >>> 8 & 0xFF);
            out[107 + outPos] = (byte)(in[31 + inPos] & 0x7FFFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 19 & 0x7FFFFFF) | ((in[1 + inPos] & 0xFF) << 11 & 0x7FFFFFF) | ((in[2 + inPos] & 0xFF) << 3 & 0x7FFFFFF) | ((in[3 + inPos] & 0xFF) >>> 5 & 0x7FFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 22 & 0x7FFFFFF) | ((in[4 + inPos] & 0xFF) << 14 & 0x7FFFFFF) | ((in[5 + inPos] & 0xFF) << 6 & 0x7FFFFFF) | ((in[6 + inPos] & 0xFF) >>> 2 & 0x7FFFFFF));
            out[2 + outPos] = (((in[6 + inPos] & 0xFF) << 25 & 0x7FFFFFF) | ((in[7 + inPos] & 0xFF) << 17 & 0x7FFFFFF) | ((in[8 + inPos] & 0xFF) << 9 & 0x7FFFFFF) | ((in[9 + inPos] & 0xFF) << 1 & 0x7FFFFFF) | ((in[10 + inPos] & 0xFF) >>> 7 & 0x7FFFFFF));
            out[3 + outPos] = (((in[10 + inPos] & 0xFF) << 20 & 0x7FFFFFF) | ((in[11 + inPos] & 0xFF) << 12 & 0x7FFFFFF) | ((in[12 + inPos] & 0xFF) << 4 & 0x7FFFFFF) | ((in[13 + inPos] & 0xFF) >>> 4 & 0x7FFFFFF));
            out[4 + outPos] = (((in[13 + inPos] & 0xFF) << 23 & 0x7FFFFFF) | ((in[14 + inPos] & 0xFF) << 15 & 0x7FFFFFF) | ((in[15 + inPos] & 0xFF) << 7 & 0x7FFFFFF) | ((in[16 + inPos] & 0xFF) >>> 1 & 0x7FFFFFF));
            out[5 + outPos] = (((in[16 + inPos] & 0xFF) << 26 & 0x7FFFFFF) | ((in[17 + inPos] & 0xFF) << 18 & 0x7FFFFFF) | ((in[18 + inPos] & 0xFF) << 10 & 0x7FFFFFF) | ((in[19 + inPos] & 0xFF) << 2 & 0x7FFFFFF) | ((in[20 + inPos] & 0xFF) >>> 6 & 0x7FFFFFF));
            out[6 + outPos] = (((in[20 + inPos] & 0xFF) << 21 & 0x7FFFFFF) | ((in[21 + inPos] & 0xFF) << 13 & 0x7FFFFFF) | ((in[22 + inPos] & 0xFF) << 5 & 0x7FFFFFF) | ((in[23 + inPos] & 0xFF) >>> 3 & 0x7FFFFFF));
            out[7 + outPos] = (((in[23 + inPos] & 0xFF) << 24 & 0x7FFFFFF) | ((in[24 + inPos] & 0xFF) << 16 & 0x7FFFFFF) | ((in[25 + inPos] & 0xFF) << 8 & 0x7FFFFFF) | (in[26 + inPos] & 0xFF & 0x7FFFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 19 & 0x7FFFFFF) | ((in[1 + inPos] & 0xFF) << 11 & 0x7FFFFFF) | ((in[2 + inPos] & 0xFF) << 3 & 0x7FFFFFF) | ((in[3 + inPos] & 0xFF) >>> 5 & 0x7FFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 22 & 0x7FFFFFF) | ((in[4 + inPos] & 0xFF) << 14 & 0x7FFFFFF) | ((in[5 + inPos] & 0xFF) << 6 & 0x7FFFFFF) | ((in[6 + inPos] & 0xFF) >>> 2 & 0x7FFFFFF));
            out[2 + outPos] = (((in[6 + inPos] & 0xFF) << 25 & 0x7FFFFFF) | ((in[7 + inPos] & 0xFF) << 17 & 0x7FFFFFF) | ((in[8 + inPos] & 0xFF) << 9 & 0x7FFFFFF) | ((in[9 + inPos] & 0xFF) << 1 & 0x7FFFFFF) | ((in[10 + inPos] & 0xFF) >>> 7 & 0x7FFFFFF));
            out[3 + outPos] = (((in[10 + inPos] & 0xFF) << 20 & 0x7FFFFFF) | ((in[11 + inPos] & 0xFF) << 12 & 0x7FFFFFF) | ((in[12 + inPos] & 0xFF) << 4 & 0x7FFFFFF) | ((in[13 + inPos] & 0xFF) >>> 4 & 0x7FFFFFF));
            out[4 + outPos] = (((in[13 + inPos] & 0xFF) << 23 & 0x7FFFFFF) | ((in[14 + inPos] & 0xFF) << 15 & 0x7FFFFFF) | ((in[15 + inPos] & 0xFF) << 7 & 0x7FFFFFF) | ((in[16 + inPos] & 0xFF) >>> 1 & 0x7FFFFFF));
            out[5 + outPos] = (((in[16 + inPos] & 0xFF) << 26 & 0x7FFFFFF) | ((in[17 + inPos] & 0xFF) << 18 & 0x7FFFFFF) | ((in[18 + inPos] & 0xFF) << 10 & 0x7FFFFFF) | ((in[19 + inPos] & 0xFF) << 2 & 0x7FFFFFF) | ((in[20 + inPos] & 0xFF) >>> 6 & 0x7FFFFFF));
            out[6 + outPos] = (((in[20 + inPos] & 0xFF) << 21 & 0x7FFFFFF) | ((in[21 + inPos] & 0xFF) << 13 & 0x7FFFFFF) | ((in[22 + inPos] & 0xFF) << 5 & 0x7FFFFFF) | ((in[23 + inPos] & 0xFF) >>> 3 & 0x7FFFFFF));
            out[7 + outPos] = (((in[23 + inPos] & 0xFF) << 24 & 0x7FFFFFF) | ((in[24 + inPos] & 0xFF) << 16 & 0x7FFFFFF) | ((in[25 + inPos] & 0xFF) << 8 & 0x7FFFFFF) | (in[26 + inPos] & 0xFF & 0x7FFFFFF));
            out[8 + outPos] = (((in[27 + inPos] & 0xFF) << 19 & 0x7FFFFFF) | ((in[28 + inPos] & 0xFF) << 11 & 0x7FFFFFF) | ((in[29 + inPos] & 0xFF) << 3 & 0x7FFFFFF) | ((in[30 + inPos] & 0xFF) >>> 5 & 0x7FFFFFF));
            out[9 + outPos] = (((in[30 + inPos] & 0xFF) << 22 & 0x7FFFFFF) | ((in[31 + inPos] & 0xFF) << 14 & 0x7FFFFFF) | ((in[32 + inPos] & 0xFF) << 6 & 0x7FFFFFF) | ((in[33 + inPos] & 0xFF) >>> 2 & 0x7FFFFFF));
            out[10 + outPos] = (((in[33 + inPos] & 0xFF) << 25 & 0x7FFFFFF) | ((in[34 + inPos] & 0xFF) << 17 & 0x7FFFFFF) | ((in[35 + inPos] & 0xFF) << 9 & 0x7FFFFFF) | ((in[36 + inPos] & 0xFF) << 1 & 0x7FFFFFF) | ((in[37 + inPos] & 0xFF) >>> 7 & 0x7FFFFFF));
            out[11 + outPos] = (((in[37 + inPos] & 0xFF) << 20 & 0x7FFFFFF) | ((in[38 + inPos] & 0xFF) << 12 & 0x7FFFFFF) | ((in[39 + inPos] & 0xFF) << 4 & 0x7FFFFFF) | ((in[40 + inPos] & 0xFF) >>> 4 & 0x7FFFFFF));
            out[12 + outPos] = (((in[40 + inPos] & 0xFF) << 23 & 0x7FFFFFF) | ((in[41 + inPos] & 0xFF) << 15 & 0x7FFFFFF) | ((in[42 + inPos] & 0xFF) << 7 & 0x7FFFFFF) | ((in[43 + inPos] & 0xFF) >>> 1 & 0x7FFFFFF));
            out[13 + outPos] = (((in[43 + inPos] & 0xFF) << 26 & 0x7FFFFFF) | ((in[44 + inPos] & 0xFF) << 18 & 0x7FFFFFF) | ((in[45 + inPos] & 0xFF) << 10 & 0x7FFFFFF) | ((in[46 + inPos] & 0xFF) << 2 & 0x7FFFFFF) | ((in[47 + inPos] & 0xFF) >>> 6 & 0x7FFFFFF));
            out[14 + outPos] = (((in[47 + inPos] & 0xFF) << 21 & 0x7FFFFFF) | ((in[48 + inPos] & 0xFF) << 13 & 0x7FFFFFF) | ((in[49 + inPos] & 0xFF) << 5 & 0x7FFFFFF) | ((in[50 + inPos] & 0xFF) >>> 3 & 0x7FFFFFF));
            out[15 + outPos] = (((in[50 + inPos] & 0xFF) << 24 & 0x7FFFFFF) | ((in[51 + inPos] & 0xFF) << 16 & 0x7FFFFFF) | ((in[52 + inPos] & 0xFF) << 8 & 0x7FFFFFF) | (in[53 + inPos] & 0xFF & 0x7FFFFFF));
            out[16 + outPos] = (((in[54 + inPos] & 0xFF) << 19 & 0x7FFFFFF) | ((in[55 + inPos] & 0xFF) << 11 & 0x7FFFFFF) | ((in[56 + inPos] & 0xFF) << 3 & 0x7FFFFFF) | ((in[57 + inPos] & 0xFF) >>> 5 & 0x7FFFFFF));
            out[17 + outPos] = (((in[57 + inPos] & 0xFF) << 22 & 0x7FFFFFF) | ((in[58 + inPos] & 0xFF) << 14 & 0x7FFFFFF) | ((in[59 + inPos] & 0xFF) << 6 & 0x7FFFFFF) | ((in[60 + inPos] & 0xFF) >>> 2 & 0x7FFFFFF));
            out[18 + outPos] = (((in[60 + inPos] & 0xFF) << 25 & 0x7FFFFFF) | ((in[61 + inPos] & 0xFF) << 17 & 0x7FFFFFF) | ((in[62 + inPos] & 0xFF) << 9 & 0x7FFFFFF) | ((in[63 + inPos] & 0xFF) << 1 & 0x7FFFFFF) | ((in[64 + inPos] & 0xFF) >>> 7 & 0x7FFFFFF));
            out[19 + outPos] = (((in[64 + inPos] & 0xFF) << 20 & 0x7FFFFFF) | ((in[65 + inPos] & 0xFF) << 12 & 0x7FFFFFF) | ((in[66 + inPos] & 0xFF) << 4 & 0x7FFFFFF) | ((in[67 + inPos] & 0xFF) >>> 4 & 0x7FFFFFF));
            out[20 + outPos] = (((in[67 + inPos] & 0xFF) << 23 & 0x7FFFFFF) | ((in[68 + inPos] & 0xFF) << 15 & 0x7FFFFFF) | ((in[69 + inPos] & 0xFF) << 7 & 0x7FFFFFF) | ((in[70 + inPos] & 0xFF) >>> 1 & 0x7FFFFFF));
            out[21 + outPos] = (((in[70 + inPos] & 0xFF) << 26 & 0x7FFFFFF) | ((in[71 + inPos] & 0xFF) << 18 & 0x7FFFFFF) | ((in[72 + inPos] & 0xFF) << 10 & 0x7FFFFFF) | ((in[73 + inPos] & 0xFF) << 2 & 0x7FFFFFF) | ((in[74 + inPos] & 0xFF) >>> 6 & 0x7FFFFFF));
            out[22 + outPos] = (((in[74 + inPos] & 0xFF) << 21 & 0x7FFFFFF) | ((in[75 + inPos] & 0xFF) << 13 & 0x7FFFFFF) | ((in[76 + inPos] & 0xFF) << 5 & 0x7FFFFFF) | ((in[77 + inPos] & 0xFF) >>> 3 & 0x7FFFFFF));
            out[23 + outPos] = (((in[77 + inPos] & 0xFF) << 24 & 0x7FFFFFF) | ((in[78 + inPos] & 0xFF) << 16 & 0x7FFFFFF) | ((in[79 + inPos] & 0xFF) << 8 & 0x7FFFFFF) | (in[80 + inPos] & 0xFF & 0x7FFFFFF));
            out[24 + outPos] = (((in[81 + inPos] & 0xFF) << 19 & 0x7FFFFFF) | ((in[82 + inPos] & 0xFF) << 11 & 0x7FFFFFF) | ((in[83 + inPos] & 0xFF) << 3 & 0x7FFFFFF) | ((in[84 + inPos] & 0xFF) >>> 5 & 0x7FFFFFF));
            out[25 + outPos] = (((in[84 + inPos] & 0xFF) << 22 & 0x7FFFFFF) | ((in[85 + inPos] & 0xFF) << 14 & 0x7FFFFFF) | ((in[86 + inPos] & 0xFF) << 6 & 0x7FFFFFF) | ((in[87 + inPos] & 0xFF) >>> 2 & 0x7FFFFFF));
            out[26 + outPos] = (((in[87 + inPos] & 0xFF) << 25 & 0x7FFFFFF) | ((in[88 + inPos] & 0xFF) << 17 & 0x7FFFFFF) | ((in[89 + inPos] & 0xFF) << 9 & 0x7FFFFFF) | ((in[90 + inPos] & 0xFF) << 1 & 0x7FFFFFF) | ((in[91 + inPos] & 0xFF) >>> 7 & 0x7FFFFFF));
            out[27 + outPos] = (((in[91 + inPos] & 0xFF) << 20 & 0x7FFFFFF) | ((in[92 + inPos] & 0xFF) << 12 & 0x7FFFFFF) | ((in[93 + inPos] & 0xFF) << 4 & 0x7FFFFFF) | ((in[94 + inPos] & 0xFF) >>> 4 & 0x7FFFFFF));
            out[28 + outPos] = (((in[94 + inPos] & 0xFF) << 23 & 0x7FFFFFF) | ((in[95 + inPos] & 0xFF) << 15 & 0x7FFFFFF) | ((in[96 + inPos] & 0xFF) << 7 & 0x7FFFFFF) | ((in[97 + inPos] & 0xFF) >>> 1 & 0x7FFFFFF));
            out[29 + outPos] = (((in[97 + inPos] & 0xFF) << 26 & 0x7FFFFFF) | ((in[98 + inPos] & 0xFF) << 18 & 0x7FFFFFF) | ((in[99 + inPos] & 0xFF) << 10 & 0x7FFFFFF) | ((in[100 + inPos] & 0xFF) << 2 & 0x7FFFFFF) | ((in[101 + inPos] & 0xFF) >>> 6 & 0x7FFFFFF));
            out[30 + outPos] = (((in[101 + inPos] & 0xFF) << 21 & 0x7FFFFFF) | ((in[102 + inPos] & 0xFF) << 13 & 0x7FFFFFF) | ((in[103 + inPos] & 0xFF) << 5 & 0x7FFFFFF) | ((in[104 + inPos] & 0xFF) >>> 3 & 0x7FFFFFF));
            out[31 + outPos] = (((in[104 + inPos] & 0xFF) << 24 & 0x7FFFFFF) | ((in[105 + inPos] & 0xFF) << 16 & 0x7FFFFFF) | ((in[106 + inPos] & 0xFF) << 8 & 0x7FFFFFF) | (in[107 + inPos] & 0xFF & 0x7FFFFFF));
        }
    }
    
    private static final class Packer28 extends BytePacker
    {
        private Packer28() {
            super(28);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0xFFFFFFF) << 4 | (in[1 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[6 + outPos] = (byte)(in[1 + inPos] & 0xFFFFFFF & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[10 + outPos] = (byte)(((in[2 + inPos] & 0xFFFFFFF) << 4 | (in[3 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[13 + outPos] = (byte)(in[3 + inPos] & 0xFFFFFFF & 0xFF);
            out[14 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[17 + outPos] = (byte)(((in[4 + inPos] & 0xFFFFFFF) << 4 | (in[5 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[18 + outPos] = (byte)((in[5 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[19 + outPos] = (byte)((in[5 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[20 + outPos] = (byte)(in[5 + inPos] & 0xFFFFFFF & 0xFF);
            out[21 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[22 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[23 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[24 + outPos] = (byte)(((in[6 + inPos] & 0xFFFFFFF) << 4 | (in[7 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[25 + outPos] = (byte)((in[7 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[26 + outPos] = (byte)((in[7 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[27 + outPos] = (byte)(in[7 + inPos] & 0xFFFFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0xFFFFFFF) << 4 | (in[1 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[6 + outPos] = (byte)(in[1 + inPos] & 0xFFFFFFF & 0xFF);
            out[7 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[10 + outPos] = (byte)(((in[2 + inPos] & 0xFFFFFFF) << 4 | (in[3 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[13 + outPos] = (byte)(in[3 + inPos] & 0xFFFFFFF & 0xFF);
            out[14 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[17 + outPos] = (byte)(((in[4 + inPos] & 0xFFFFFFF) << 4 | (in[5 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[18 + outPos] = (byte)((in[5 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[19 + outPos] = (byte)((in[5 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[20 + outPos] = (byte)(in[5 + inPos] & 0xFFFFFFF & 0xFF);
            out[21 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[22 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[23 + outPos] = (byte)((in[6 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[24 + outPos] = (byte)(((in[6 + inPos] & 0xFFFFFFF) << 4 | (in[7 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[25 + outPos] = (byte)((in[7 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[26 + outPos] = (byte)((in[7 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[27 + outPos] = (byte)(in[7 + inPos] & 0xFFFFFFF & 0xFF);
            out[28 + outPos] = (byte)((in[8 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[29 + outPos] = (byte)((in[8 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[30 + outPos] = (byte)((in[8 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[31 + outPos] = (byte)(((in[8 + inPos] & 0xFFFFFFF) << 4 | (in[9 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[32 + outPos] = (byte)((in[9 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[33 + outPos] = (byte)((in[9 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[34 + outPos] = (byte)(in[9 + inPos] & 0xFFFFFFF & 0xFF);
            out[35 + outPos] = (byte)((in[10 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[36 + outPos] = (byte)((in[10 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[37 + outPos] = (byte)((in[10 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[38 + outPos] = (byte)(((in[10 + inPos] & 0xFFFFFFF) << 4 | (in[11 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[39 + outPos] = (byte)((in[11 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[40 + outPos] = (byte)((in[11 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[41 + outPos] = (byte)(in[11 + inPos] & 0xFFFFFFF & 0xFF);
            out[42 + outPos] = (byte)((in[12 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[43 + outPos] = (byte)((in[12 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[44 + outPos] = (byte)((in[12 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[45 + outPos] = (byte)(((in[12 + inPos] & 0xFFFFFFF) << 4 | (in[13 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[46 + outPos] = (byte)((in[13 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[47 + outPos] = (byte)((in[13 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[48 + outPos] = (byte)(in[13 + inPos] & 0xFFFFFFF & 0xFF);
            out[49 + outPos] = (byte)((in[14 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[50 + outPos] = (byte)((in[14 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[51 + outPos] = (byte)((in[14 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[52 + outPos] = (byte)(((in[14 + inPos] & 0xFFFFFFF) << 4 | (in[15 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[53 + outPos] = (byte)((in[15 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[54 + outPos] = (byte)((in[15 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[55 + outPos] = (byte)(in[15 + inPos] & 0xFFFFFFF & 0xFF);
            out[56 + outPos] = (byte)((in[16 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[57 + outPos] = (byte)((in[16 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[58 + outPos] = (byte)((in[16 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[59 + outPos] = (byte)(((in[16 + inPos] & 0xFFFFFFF) << 4 | (in[17 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[60 + outPos] = (byte)((in[17 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[61 + outPos] = (byte)((in[17 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[62 + outPos] = (byte)(in[17 + inPos] & 0xFFFFFFF & 0xFF);
            out[63 + outPos] = (byte)((in[18 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[64 + outPos] = (byte)((in[18 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[65 + outPos] = (byte)((in[18 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[66 + outPos] = (byte)(((in[18 + inPos] & 0xFFFFFFF) << 4 | (in[19 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[67 + outPos] = (byte)((in[19 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[68 + outPos] = (byte)((in[19 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[69 + outPos] = (byte)(in[19 + inPos] & 0xFFFFFFF & 0xFF);
            out[70 + outPos] = (byte)((in[20 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[71 + outPos] = (byte)((in[20 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[72 + outPos] = (byte)((in[20 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[73 + outPos] = (byte)(((in[20 + inPos] & 0xFFFFFFF) << 4 | (in[21 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[74 + outPos] = (byte)((in[21 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[75 + outPos] = (byte)((in[21 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[76 + outPos] = (byte)(in[21 + inPos] & 0xFFFFFFF & 0xFF);
            out[77 + outPos] = (byte)((in[22 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[78 + outPos] = (byte)((in[22 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[79 + outPos] = (byte)((in[22 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[80 + outPos] = (byte)(((in[22 + inPos] & 0xFFFFFFF) << 4 | (in[23 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[81 + outPos] = (byte)((in[23 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[82 + outPos] = (byte)((in[23 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[83 + outPos] = (byte)(in[23 + inPos] & 0xFFFFFFF & 0xFF);
            out[84 + outPos] = (byte)((in[24 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[85 + outPos] = (byte)((in[24 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[86 + outPos] = (byte)((in[24 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[87 + outPos] = (byte)(((in[24 + inPos] & 0xFFFFFFF) << 4 | (in[25 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[88 + outPos] = (byte)((in[25 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[89 + outPos] = (byte)((in[25 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[90 + outPos] = (byte)(in[25 + inPos] & 0xFFFFFFF & 0xFF);
            out[91 + outPos] = (byte)((in[26 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[92 + outPos] = (byte)((in[26 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[93 + outPos] = (byte)((in[26 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[94 + outPos] = (byte)(((in[26 + inPos] & 0xFFFFFFF) << 4 | (in[27 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[95 + outPos] = (byte)((in[27 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[96 + outPos] = (byte)((in[27 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[97 + outPos] = (byte)(in[27 + inPos] & 0xFFFFFFF & 0xFF);
            out[98 + outPos] = (byte)((in[28 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[99 + outPos] = (byte)((in[28 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[100 + outPos] = (byte)((in[28 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[101 + outPos] = (byte)(((in[28 + inPos] & 0xFFFFFFF) << 4 | (in[29 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[102 + outPos] = (byte)((in[29 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[103 + outPos] = (byte)((in[29 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[104 + outPos] = (byte)(in[29 + inPos] & 0xFFFFFFF & 0xFF);
            out[105 + outPos] = (byte)((in[30 + inPos] & 0xFFFFFFF) >>> 20 & 0xFF);
            out[106 + outPos] = (byte)((in[30 + inPos] & 0xFFFFFFF) >>> 12 & 0xFF);
            out[107 + outPos] = (byte)((in[30 + inPos] & 0xFFFFFFF) >>> 4 & 0xFF);
            out[108 + outPos] = (byte)(((in[30 + inPos] & 0xFFFFFFF) << 4 | (in[31 + inPos] & 0xFFFFFFF) >>> 24) & 0xFF);
            out[109 + outPos] = (byte)((in[31 + inPos] & 0xFFFFFFF) >>> 16 & 0xFF);
            out[110 + outPos] = (byte)((in[31 + inPos] & 0xFFFFFFF) >>> 8 & 0xFF);
            out[111 + outPos] = (byte)(in[31 + inPos] & 0xFFFFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[1 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[2 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[3 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[4 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[5 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[6 + inPos] & 0xFF & 0xFFFFFFF));
            out[2 + outPos] = (((in[7 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[8 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[9 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[10 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[3 + outPos] = (((in[10 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[11 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[12 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[13 + inPos] & 0xFF & 0xFFFFFFF));
            out[4 + outPos] = (((in[14 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[15 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[16 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[17 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[5 + outPos] = (((in[17 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[18 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[19 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[20 + inPos] & 0xFF & 0xFFFFFFF));
            out[6 + outPos] = (((in[21 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[22 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[23 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[24 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[7 + outPos] = (((in[24 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[25 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[26 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[27 + inPos] & 0xFF & 0xFFFFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[1 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[2 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[3 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[4 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[5 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[6 + inPos] & 0xFF & 0xFFFFFFF));
            out[2 + outPos] = (((in[7 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[8 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[9 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[10 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[3 + outPos] = (((in[10 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[11 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[12 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[13 + inPos] & 0xFF & 0xFFFFFFF));
            out[4 + outPos] = (((in[14 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[15 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[16 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[17 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[5 + outPos] = (((in[17 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[18 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[19 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[20 + inPos] & 0xFF & 0xFFFFFFF));
            out[6 + outPos] = (((in[21 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[22 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[23 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[24 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[7 + outPos] = (((in[24 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[25 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[26 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[27 + inPos] & 0xFF & 0xFFFFFFF));
            out[8 + outPos] = (((in[28 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[29 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[30 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[31 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[9 + outPos] = (((in[31 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[32 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[33 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[34 + inPos] & 0xFF & 0xFFFFFFF));
            out[10 + outPos] = (((in[35 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[36 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[37 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[38 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[11 + outPos] = (((in[38 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[39 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[40 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[41 + inPos] & 0xFF & 0xFFFFFFF));
            out[12 + outPos] = (((in[42 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[43 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[44 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[45 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[13 + outPos] = (((in[45 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[46 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[47 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[48 + inPos] & 0xFF & 0xFFFFFFF));
            out[14 + outPos] = (((in[49 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[50 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[51 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[52 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[15 + outPos] = (((in[52 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[53 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[54 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[55 + inPos] & 0xFF & 0xFFFFFFF));
            out[16 + outPos] = (((in[56 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[57 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[58 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[59 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[17 + outPos] = (((in[59 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[60 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[61 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[62 + inPos] & 0xFF & 0xFFFFFFF));
            out[18 + outPos] = (((in[63 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[64 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[65 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[66 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[19 + outPos] = (((in[66 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[67 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[68 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[69 + inPos] & 0xFF & 0xFFFFFFF));
            out[20 + outPos] = (((in[70 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[71 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[72 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[73 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[21 + outPos] = (((in[73 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[74 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[75 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[76 + inPos] & 0xFF & 0xFFFFFFF));
            out[22 + outPos] = (((in[77 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[78 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[79 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[80 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[23 + outPos] = (((in[80 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[81 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[82 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[83 + inPos] & 0xFF & 0xFFFFFFF));
            out[24 + outPos] = (((in[84 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[85 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[86 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[87 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[25 + outPos] = (((in[87 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[88 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[89 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[90 + inPos] & 0xFF & 0xFFFFFFF));
            out[26 + outPos] = (((in[91 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[92 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[93 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[94 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[27 + outPos] = (((in[94 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[95 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[96 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[97 + inPos] & 0xFF & 0xFFFFFFF));
            out[28 + outPos] = (((in[98 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[99 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[100 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[101 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[29 + outPos] = (((in[101 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[102 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[103 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[104 + inPos] & 0xFF & 0xFFFFFFF));
            out[30 + outPos] = (((in[105 + inPos] & 0xFF) << 20 & 0xFFFFFFF) | ((in[106 + inPos] & 0xFF) << 12 & 0xFFFFFFF) | ((in[107 + inPos] & 0xFF) << 4 & 0xFFFFFFF) | ((in[108 + inPos] & 0xFF) >>> 4 & 0xFFFFFFF));
            out[31 + outPos] = (((in[108 + inPos] & 0xFF) << 24 & 0xFFFFFFF) | ((in[109 + inPos] & 0xFF) << 16 & 0xFFFFFFF) | ((in[110 + inPos] & 0xFF) << 8 & 0xFFFFFFF) | (in[111 + inPos] & 0xFF & 0xFFFFFFF));
        }
    }
    
    private static final class Packer29 extends BytePacker
    {
        private Packer29() {
            super(29);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFFF) >>> 21 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFFF) >>> 13 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFFF) >>> 5 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x1FFFFFFF) << 3 | (in[1 + inPos] & 0x1FFFFFFF) >>> 26) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFFF) >>> 18 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFFF) >>> 10 & 0xFF);
            out[6 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFFF) >>> 2 & 0xFF);
            out[7 + outPos] = (byte)(((in[1 + inPos] & 0x1FFFFFFF) << 6 | (in[2 + inPos] & 0x1FFFFFFF) >>> 23) & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFFFF) >>> 15 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFFFF) >>> 7 & 0xFF);
            out[10 + outPos] = (byte)(((in[2 + inPos] & 0x1FFFFFFF) << 1 | (in[3 + inPos] & 0x1FFFFFFF) >>> 28) & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFFF) >>> 20 & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFFF) >>> 12 & 0xFF);
            out[13 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFFF) >>> 4 & 0xFF);
            out[14 + outPos] = (byte)(((in[3 + inPos] & 0x1FFFFFFF) << 4 | (in[4 + inPos] & 0x1FFFFFFF) >>> 25) & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFFF) >>> 17 & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFFF) >>> 9 & 0xFF);
            out[17 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFFF) >>> 1 & 0xFF);
            out[18 + outPos] = (byte)(((in[4 + inPos] & 0x1FFFFFFF) << 7 | (in[5 + inPos] & 0x1FFFFFFF) >>> 22) & 0xFF);
            out[19 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFFFF) >>> 14 & 0xFF);
            out[20 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFFFF) >>> 6 & 0xFF);
            out[21 + outPos] = (byte)(((in[5 + inPos] & 0x1FFFFFFF) << 2 | (in[6 + inPos] & 0x1FFFFFFF) >>> 27) & 0xFF);
            out[22 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFFF) >>> 19 & 0xFF);
            out[23 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFFF) >>> 11 & 0xFF);
            out[24 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFFF) >>> 3 & 0xFF);
            out[25 + outPos] = (byte)(((in[6 + inPos] & 0x1FFFFFFF) << 5 | (in[7 + inPos] & 0x1FFFFFFF) >>> 24) & 0xFF);
            out[26 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFFFF) >>> 16 & 0xFF);
            out[27 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFFFF) >>> 8 & 0xFF);
            out[28 + outPos] = (byte)(in[7 + inPos] & 0x1FFFFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFFF) >>> 21 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFFF) >>> 13 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x1FFFFFFF) >>> 5 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x1FFFFFFF) << 3 | (in[1 + inPos] & 0x1FFFFFFF) >>> 26) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFFF) >>> 18 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFFF) >>> 10 & 0xFF);
            out[6 + outPos] = (byte)((in[1 + inPos] & 0x1FFFFFFF) >>> 2 & 0xFF);
            out[7 + outPos] = (byte)(((in[1 + inPos] & 0x1FFFFFFF) << 6 | (in[2 + inPos] & 0x1FFFFFFF) >>> 23) & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFFFF) >>> 15 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & 0x1FFFFFFF) >>> 7 & 0xFF);
            out[10 + outPos] = (byte)(((in[2 + inPos] & 0x1FFFFFFF) << 1 | (in[3 + inPos] & 0x1FFFFFFF) >>> 28) & 0xFF);
            out[11 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFFF) >>> 20 & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFFF) >>> 12 & 0xFF);
            out[13 + outPos] = (byte)((in[3 + inPos] & 0x1FFFFFFF) >>> 4 & 0xFF);
            out[14 + outPos] = (byte)(((in[3 + inPos] & 0x1FFFFFFF) << 4 | (in[4 + inPos] & 0x1FFFFFFF) >>> 25) & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFFF) >>> 17 & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFFF) >>> 9 & 0xFF);
            out[17 + outPos] = (byte)((in[4 + inPos] & 0x1FFFFFFF) >>> 1 & 0xFF);
            out[18 + outPos] = (byte)(((in[4 + inPos] & 0x1FFFFFFF) << 7 | (in[5 + inPos] & 0x1FFFFFFF) >>> 22) & 0xFF);
            out[19 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFFFF) >>> 14 & 0xFF);
            out[20 + outPos] = (byte)((in[5 + inPos] & 0x1FFFFFFF) >>> 6 & 0xFF);
            out[21 + outPos] = (byte)(((in[5 + inPos] & 0x1FFFFFFF) << 2 | (in[6 + inPos] & 0x1FFFFFFF) >>> 27) & 0xFF);
            out[22 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFFF) >>> 19 & 0xFF);
            out[23 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFFF) >>> 11 & 0xFF);
            out[24 + outPos] = (byte)((in[6 + inPos] & 0x1FFFFFFF) >>> 3 & 0xFF);
            out[25 + outPos] = (byte)(((in[6 + inPos] & 0x1FFFFFFF) << 5 | (in[7 + inPos] & 0x1FFFFFFF) >>> 24) & 0xFF);
            out[26 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFFFF) >>> 16 & 0xFF);
            out[27 + outPos] = (byte)((in[7 + inPos] & 0x1FFFFFFF) >>> 8 & 0xFF);
            out[28 + outPos] = (byte)(in[7 + inPos] & 0x1FFFFFFF & 0xFF);
            out[29 + outPos] = (byte)((in[8 + inPos] & 0x1FFFFFFF) >>> 21 & 0xFF);
            out[30 + outPos] = (byte)((in[8 + inPos] & 0x1FFFFFFF) >>> 13 & 0xFF);
            out[31 + outPos] = (byte)((in[8 + inPos] & 0x1FFFFFFF) >>> 5 & 0xFF);
            out[32 + outPos] = (byte)(((in[8 + inPos] & 0x1FFFFFFF) << 3 | (in[9 + inPos] & 0x1FFFFFFF) >>> 26) & 0xFF);
            out[33 + outPos] = (byte)((in[9 + inPos] & 0x1FFFFFFF) >>> 18 & 0xFF);
            out[34 + outPos] = (byte)((in[9 + inPos] & 0x1FFFFFFF) >>> 10 & 0xFF);
            out[35 + outPos] = (byte)((in[9 + inPos] & 0x1FFFFFFF) >>> 2 & 0xFF);
            out[36 + outPos] = (byte)(((in[9 + inPos] & 0x1FFFFFFF) << 6 | (in[10 + inPos] & 0x1FFFFFFF) >>> 23) & 0xFF);
            out[37 + outPos] = (byte)((in[10 + inPos] & 0x1FFFFFFF) >>> 15 & 0xFF);
            out[38 + outPos] = (byte)((in[10 + inPos] & 0x1FFFFFFF) >>> 7 & 0xFF);
            out[39 + outPos] = (byte)(((in[10 + inPos] & 0x1FFFFFFF) << 1 | (in[11 + inPos] & 0x1FFFFFFF) >>> 28) & 0xFF);
            out[40 + outPos] = (byte)((in[11 + inPos] & 0x1FFFFFFF) >>> 20 & 0xFF);
            out[41 + outPos] = (byte)((in[11 + inPos] & 0x1FFFFFFF) >>> 12 & 0xFF);
            out[42 + outPos] = (byte)((in[11 + inPos] & 0x1FFFFFFF) >>> 4 & 0xFF);
            out[43 + outPos] = (byte)(((in[11 + inPos] & 0x1FFFFFFF) << 4 | (in[12 + inPos] & 0x1FFFFFFF) >>> 25) & 0xFF);
            out[44 + outPos] = (byte)((in[12 + inPos] & 0x1FFFFFFF) >>> 17 & 0xFF);
            out[45 + outPos] = (byte)((in[12 + inPos] & 0x1FFFFFFF) >>> 9 & 0xFF);
            out[46 + outPos] = (byte)((in[12 + inPos] & 0x1FFFFFFF) >>> 1 & 0xFF);
            out[47 + outPos] = (byte)(((in[12 + inPos] & 0x1FFFFFFF) << 7 | (in[13 + inPos] & 0x1FFFFFFF) >>> 22) & 0xFF);
            out[48 + outPos] = (byte)((in[13 + inPos] & 0x1FFFFFFF) >>> 14 & 0xFF);
            out[49 + outPos] = (byte)((in[13 + inPos] & 0x1FFFFFFF) >>> 6 & 0xFF);
            out[50 + outPos] = (byte)(((in[13 + inPos] & 0x1FFFFFFF) << 2 | (in[14 + inPos] & 0x1FFFFFFF) >>> 27) & 0xFF);
            out[51 + outPos] = (byte)((in[14 + inPos] & 0x1FFFFFFF) >>> 19 & 0xFF);
            out[52 + outPos] = (byte)((in[14 + inPos] & 0x1FFFFFFF) >>> 11 & 0xFF);
            out[53 + outPos] = (byte)((in[14 + inPos] & 0x1FFFFFFF) >>> 3 & 0xFF);
            out[54 + outPos] = (byte)(((in[14 + inPos] & 0x1FFFFFFF) << 5 | (in[15 + inPos] & 0x1FFFFFFF) >>> 24) & 0xFF);
            out[55 + outPos] = (byte)((in[15 + inPos] & 0x1FFFFFFF) >>> 16 & 0xFF);
            out[56 + outPos] = (byte)((in[15 + inPos] & 0x1FFFFFFF) >>> 8 & 0xFF);
            out[57 + outPos] = (byte)(in[15 + inPos] & 0x1FFFFFFF & 0xFF);
            out[58 + outPos] = (byte)((in[16 + inPos] & 0x1FFFFFFF) >>> 21 & 0xFF);
            out[59 + outPos] = (byte)((in[16 + inPos] & 0x1FFFFFFF) >>> 13 & 0xFF);
            out[60 + outPos] = (byte)((in[16 + inPos] & 0x1FFFFFFF) >>> 5 & 0xFF);
            out[61 + outPos] = (byte)(((in[16 + inPos] & 0x1FFFFFFF) << 3 | (in[17 + inPos] & 0x1FFFFFFF) >>> 26) & 0xFF);
            out[62 + outPos] = (byte)((in[17 + inPos] & 0x1FFFFFFF) >>> 18 & 0xFF);
            out[63 + outPos] = (byte)((in[17 + inPos] & 0x1FFFFFFF) >>> 10 & 0xFF);
            out[64 + outPos] = (byte)((in[17 + inPos] & 0x1FFFFFFF) >>> 2 & 0xFF);
            out[65 + outPos] = (byte)(((in[17 + inPos] & 0x1FFFFFFF) << 6 | (in[18 + inPos] & 0x1FFFFFFF) >>> 23) & 0xFF);
            out[66 + outPos] = (byte)((in[18 + inPos] & 0x1FFFFFFF) >>> 15 & 0xFF);
            out[67 + outPos] = (byte)((in[18 + inPos] & 0x1FFFFFFF) >>> 7 & 0xFF);
            out[68 + outPos] = (byte)(((in[18 + inPos] & 0x1FFFFFFF) << 1 | (in[19 + inPos] & 0x1FFFFFFF) >>> 28) & 0xFF);
            out[69 + outPos] = (byte)((in[19 + inPos] & 0x1FFFFFFF) >>> 20 & 0xFF);
            out[70 + outPos] = (byte)((in[19 + inPos] & 0x1FFFFFFF) >>> 12 & 0xFF);
            out[71 + outPos] = (byte)((in[19 + inPos] & 0x1FFFFFFF) >>> 4 & 0xFF);
            out[72 + outPos] = (byte)(((in[19 + inPos] & 0x1FFFFFFF) << 4 | (in[20 + inPos] & 0x1FFFFFFF) >>> 25) & 0xFF);
            out[73 + outPos] = (byte)((in[20 + inPos] & 0x1FFFFFFF) >>> 17 & 0xFF);
            out[74 + outPos] = (byte)((in[20 + inPos] & 0x1FFFFFFF) >>> 9 & 0xFF);
            out[75 + outPos] = (byte)((in[20 + inPos] & 0x1FFFFFFF) >>> 1 & 0xFF);
            out[76 + outPos] = (byte)(((in[20 + inPos] & 0x1FFFFFFF) << 7 | (in[21 + inPos] & 0x1FFFFFFF) >>> 22) & 0xFF);
            out[77 + outPos] = (byte)((in[21 + inPos] & 0x1FFFFFFF) >>> 14 & 0xFF);
            out[78 + outPos] = (byte)((in[21 + inPos] & 0x1FFFFFFF) >>> 6 & 0xFF);
            out[79 + outPos] = (byte)(((in[21 + inPos] & 0x1FFFFFFF) << 2 | (in[22 + inPos] & 0x1FFFFFFF) >>> 27) & 0xFF);
            out[80 + outPos] = (byte)((in[22 + inPos] & 0x1FFFFFFF) >>> 19 & 0xFF);
            out[81 + outPos] = (byte)((in[22 + inPos] & 0x1FFFFFFF) >>> 11 & 0xFF);
            out[82 + outPos] = (byte)((in[22 + inPos] & 0x1FFFFFFF) >>> 3 & 0xFF);
            out[83 + outPos] = (byte)(((in[22 + inPos] & 0x1FFFFFFF) << 5 | (in[23 + inPos] & 0x1FFFFFFF) >>> 24) & 0xFF);
            out[84 + outPos] = (byte)((in[23 + inPos] & 0x1FFFFFFF) >>> 16 & 0xFF);
            out[85 + outPos] = (byte)((in[23 + inPos] & 0x1FFFFFFF) >>> 8 & 0xFF);
            out[86 + outPos] = (byte)(in[23 + inPos] & 0x1FFFFFFF & 0xFF);
            out[87 + outPos] = (byte)((in[24 + inPos] & 0x1FFFFFFF) >>> 21 & 0xFF);
            out[88 + outPos] = (byte)((in[24 + inPos] & 0x1FFFFFFF) >>> 13 & 0xFF);
            out[89 + outPos] = (byte)((in[24 + inPos] & 0x1FFFFFFF) >>> 5 & 0xFF);
            out[90 + outPos] = (byte)(((in[24 + inPos] & 0x1FFFFFFF) << 3 | (in[25 + inPos] & 0x1FFFFFFF) >>> 26) & 0xFF);
            out[91 + outPos] = (byte)((in[25 + inPos] & 0x1FFFFFFF) >>> 18 & 0xFF);
            out[92 + outPos] = (byte)((in[25 + inPos] & 0x1FFFFFFF) >>> 10 & 0xFF);
            out[93 + outPos] = (byte)((in[25 + inPos] & 0x1FFFFFFF) >>> 2 & 0xFF);
            out[94 + outPos] = (byte)(((in[25 + inPos] & 0x1FFFFFFF) << 6 | (in[26 + inPos] & 0x1FFFFFFF) >>> 23) & 0xFF);
            out[95 + outPos] = (byte)((in[26 + inPos] & 0x1FFFFFFF) >>> 15 & 0xFF);
            out[96 + outPos] = (byte)((in[26 + inPos] & 0x1FFFFFFF) >>> 7 & 0xFF);
            out[97 + outPos] = (byte)(((in[26 + inPos] & 0x1FFFFFFF) << 1 | (in[27 + inPos] & 0x1FFFFFFF) >>> 28) & 0xFF);
            out[98 + outPos] = (byte)((in[27 + inPos] & 0x1FFFFFFF) >>> 20 & 0xFF);
            out[99 + outPos] = (byte)((in[27 + inPos] & 0x1FFFFFFF) >>> 12 & 0xFF);
            out[100 + outPos] = (byte)((in[27 + inPos] & 0x1FFFFFFF) >>> 4 & 0xFF);
            out[101 + outPos] = (byte)(((in[27 + inPos] & 0x1FFFFFFF) << 4 | (in[28 + inPos] & 0x1FFFFFFF) >>> 25) & 0xFF);
            out[102 + outPos] = (byte)((in[28 + inPos] & 0x1FFFFFFF) >>> 17 & 0xFF);
            out[103 + outPos] = (byte)((in[28 + inPos] & 0x1FFFFFFF) >>> 9 & 0xFF);
            out[104 + outPos] = (byte)((in[28 + inPos] & 0x1FFFFFFF) >>> 1 & 0xFF);
            out[105 + outPos] = (byte)(((in[28 + inPos] & 0x1FFFFFFF) << 7 | (in[29 + inPos] & 0x1FFFFFFF) >>> 22) & 0xFF);
            out[106 + outPos] = (byte)((in[29 + inPos] & 0x1FFFFFFF) >>> 14 & 0xFF);
            out[107 + outPos] = (byte)((in[29 + inPos] & 0x1FFFFFFF) >>> 6 & 0xFF);
            out[108 + outPos] = (byte)(((in[29 + inPos] & 0x1FFFFFFF) << 2 | (in[30 + inPos] & 0x1FFFFFFF) >>> 27) & 0xFF);
            out[109 + outPos] = (byte)((in[30 + inPos] & 0x1FFFFFFF) >>> 19 & 0xFF);
            out[110 + outPos] = (byte)((in[30 + inPos] & 0x1FFFFFFF) >>> 11 & 0xFF);
            out[111 + outPos] = (byte)((in[30 + inPos] & 0x1FFFFFFF) >>> 3 & 0xFF);
            out[112 + outPos] = (byte)(((in[30 + inPos] & 0x1FFFFFFF) << 5 | (in[31 + inPos] & 0x1FFFFFFF) >>> 24) & 0xFF);
            out[113 + outPos] = (byte)((in[31 + inPos] & 0x1FFFFFFF) >>> 16 & 0xFF);
            out[114 + outPos] = (byte)((in[31 + inPos] & 0x1FFFFFFF) >>> 8 & 0xFF);
            out[115 + outPos] = (byte)(in[31 + inPos] & 0x1FFFFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 21 & 0x1FFFFFFF) | ((in[1 + inPos] & 0xFF) << 13 & 0x1FFFFFFF) | ((in[2 + inPos] & 0xFF) << 5 & 0x1FFFFFFF) | ((in[3 + inPos] & 0xFF) >>> 3 & 0x1FFFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 26 & 0x1FFFFFFF) | ((in[4 + inPos] & 0xFF) << 18 & 0x1FFFFFFF) | ((in[5 + inPos] & 0xFF) << 10 & 0x1FFFFFFF) | ((in[6 + inPos] & 0xFF) << 2 & 0x1FFFFFFF) | ((in[7 + inPos] & 0xFF) >>> 6 & 0x1FFFFFFF));
            out[2 + outPos] = (((in[7 + inPos] & 0xFF) << 23 & 0x1FFFFFFF) | ((in[8 + inPos] & 0xFF) << 15 & 0x1FFFFFFF) | ((in[9 + inPos] & 0xFF) << 7 & 0x1FFFFFFF) | ((in[10 + inPos] & 0xFF) >>> 1 & 0x1FFFFFFF));
            out[3 + outPos] = (((in[10 + inPos] & 0xFF) << 28 & 0x1FFFFFFF) | ((in[11 + inPos] & 0xFF) << 20 & 0x1FFFFFFF) | ((in[12 + inPos] & 0xFF) << 12 & 0x1FFFFFFF) | ((in[13 + inPos] & 0xFF) << 4 & 0x1FFFFFFF) | ((in[14 + inPos] & 0xFF) >>> 4 & 0x1FFFFFFF));
            out[4 + outPos] = (((in[14 + inPos] & 0xFF) << 25 & 0x1FFFFFFF) | ((in[15 + inPos] & 0xFF) << 17 & 0x1FFFFFFF) | ((in[16 + inPos] & 0xFF) << 9 & 0x1FFFFFFF) | ((in[17 + inPos] & 0xFF) << 1 & 0x1FFFFFFF) | ((in[18 + inPos] & 0xFF) >>> 7 & 0x1FFFFFFF));
            out[5 + outPos] = (((in[18 + inPos] & 0xFF) << 22 & 0x1FFFFFFF) | ((in[19 + inPos] & 0xFF) << 14 & 0x1FFFFFFF) | ((in[20 + inPos] & 0xFF) << 6 & 0x1FFFFFFF) | ((in[21 + inPos] & 0xFF) >>> 2 & 0x1FFFFFFF));
            out[6 + outPos] = (((in[21 + inPos] & 0xFF) << 27 & 0x1FFFFFFF) | ((in[22 + inPos] & 0xFF) << 19 & 0x1FFFFFFF) | ((in[23 + inPos] & 0xFF) << 11 & 0x1FFFFFFF) | ((in[24 + inPos] & 0xFF) << 3 & 0x1FFFFFFF) | ((in[25 + inPos] & 0xFF) >>> 5 & 0x1FFFFFFF));
            out[7 + outPos] = (((in[25 + inPos] & 0xFF) << 24 & 0x1FFFFFFF) | ((in[26 + inPos] & 0xFF) << 16 & 0x1FFFFFFF) | ((in[27 + inPos] & 0xFF) << 8 & 0x1FFFFFFF) | (in[28 + inPos] & 0xFF & 0x1FFFFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 21 & 0x1FFFFFFF) | ((in[1 + inPos] & 0xFF) << 13 & 0x1FFFFFFF) | ((in[2 + inPos] & 0xFF) << 5 & 0x1FFFFFFF) | ((in[3 + inPos] & 0xFF) >>> 3 & 0x1FFFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 26 & 0x1FFFFFFF) | ((in[4 + inPos] & 0xFF) << 18 & 0x1FFFFFFF) | ((in[5 + inPos] & 0xFF) << 10 & 0x1FFFFFFF) | ((in[6 + inPos] & 0xFF) << 2 & 0x1FFFFFFF) | ((in[7 + inPos] & 0xFF) >>> 6 & 0x1FFFFFFF));
            out[2 + outPos] = (((in[7 + inPos] & 0xFF) << 23 & 0x1FFFFFFF) | ((in[8 + inPos] & 0xFF) << 15 & 0x1FFFFFFF) | ((in[9 + inPos] & 0xFF) << 7 & 0x1FFFFFFF) | ((in[10 + inPos] & 0xFF) >>> 1 & 0x1FFFFFFF));
            out[3 + outPos] = (((in[10 + inPos] & 0xFF) << 28 & 0x1FFFFFFF) | ((in[11 + inPos] & 0xFF) << 20 & 0x1FFFFFFF) | ((in[12 + inPos] & 0xFF) << 12 & 0x1FFFFFFF) | ((in[13 + inPos] & 0xFF) << 4 & 0x1FFFFFFF) | ((in[14 + inPos] & 0xFF) >>> 4 & 0x1FFFFFFF));
            out[4 + outPos] = (((in[14 + inPos] & 0xFF) << 25 & 0x1FFFFFFF) | ((in[15 + inPos] & 0xFF) << 17 & 0x1FFFFFFF) | ((in[16 + inPos] & 0xFF) << 9 & 0x1FFFFFFF) | ((in[17 + inPos] & 0xFF) << 1 & 0x1FFFFFFF) | ((in[18 + inPos] & 0xFF) >>> 7 & 0x1FFFFFFF));
            out[5 + outPos] = (((in[18 + inPos] & 0xFF) << 22 & 0x1FFFFFFF) | ((in[19 + inPos] & 0xFF) << 14 & 0x1FFFFFFF) | ((in[20 + inPos] & 0xFF) << 6 & 0x1FFFFFFF) | ((in[21 + inPos] & 0xFF) >>> 2 & 0x1FFFFFFF));
            out[6 + outPos] = (((in[21 + inPos] & 0xFF) << 27 & 0x1FFFFFFF) | ((in[22 + inPos] & 0xFF) << 19 & 0x1FFFFFFF) | ((in[23 + inPos] & 0xFF) << 11 & 0x1FFFFFFF) | ((in[24 + inPos] & 0xFF) << 3 & 0x1FFFFFFF) | ((in[25 + inPos] & 0xFF) >>> 5 & 0x1FFFFFFF));
            out[7 + outPos] = (((in[25 + inPos] & 0xFF) << 24 & 0x1FFFFFFF) | ((in[26 + inPos] & 0xFF) << 16 & 0x1FFFFFFF) | ((in[27 + inPos] & 0xFF) << 8 & 0x1FFFFFFF) | (in[28 + inPos] & 0xFF & 0x1FFFFFFF));
            out[8 + outPos] = (((in[29 + inPos] & 0xFF) << 21 & 0x1FFFFFFF) | ((in[30 + inPos] & 0xFF) << 13 & 0x1FFFFFFF) | ((in[31 + inPos] & 0xFF) << 5 & 0x1FFFFFFF) | ((in[32 + inPos] & 0xFF) >>> 3 & 0x1FFFFFFF));
            out[9 + outPos] = (((in[32 + inPos] & 0xFF) << 26 & 0x1FFFFFFF) | ((in[33 + inPos] & 0xFF) << 18 & 0x1FFFFFFF) | ((in[34 + inPos] & 0xFF) << 10 & 0x1FFFFFFF) | ((in[35 + inPos] & 0xFF) << 2 & 0x1FFFFFFF) | ((in[36 + inPos] & 0xFF) >>> 6 & 0x1FFFFFFF));
            out[10 + outPos] = (((in[36 + inPos] & 0xFF) << 23 & 0x1FFFFFFF) | ((in[37 + inPos] & 0xFF) << 15 & 0x1FFFFFFF) | ((in[38 + inPos] & 0xFF) << 7 & 0x1FFFFFFF) | ((in[39 + inPos] & 0xFF) >>> 1 & 0x1FFFFFFF));
            out[11 + outPos] = (((in[39 + inPos] & 0xFF) << 28 & 0x1FFFFFFF) | ((in[40 + inPos] & 0xFF) << 20 & 0x1FFFFFFF) | ((in[41 + inPos] & 0xFF) << 12 & 0x1FFFFFFF) | ((in[42 + inPos] & 0xFF) << 4 & 0x1FFFFFFF) | ((in[43 + inPos] & 0xFF) >>> 4 & 0x1FFFFFFF));
            out[12 + outPos] = (((in[43 + inPos] & 0xFF) << 25 & 0x1FFFFFFF) | ((in[44 + inPos] & 0xFF) << 17 & 0x1FFFFFFF) | ((in[45 + inPos] & 0xFF) << 9 & 0x1FFFFFFF) | ((in[46 + inPos] & 0xFF) << 1 & 0x1FFFFFFF) | ((in[47 + inPos] & 0xFF) >>> 7 & 0x1FFFFFFF));
            out[13 + outPos] = (((in[47 + inPos] & 0xFF) << 22 & 0x1FFFFFFF) | ((in[48 + inPos] & 0xFF) << 14 & 0x1FFFFFFF) | ((in[49 + inPos] & 0xFF) << 6 & 0x1FFFFFFF) | ((in[50 + inPos] & 0xFF) >>> 2 & 0x1FFFFFFF));
            out[14 + outPos] = (((in[50 + inPos] & 0xFF) << 27 & 0x1FFFFFFF) | ((in[51 + inPos] & 0xFF) << 19 & 0x1FFFFFFF) | ((in[52 + inPos] & 0xFF) << 11 & 0x1FFFFFFF) | ((in[53 + inPos] & 0xFF) << 3 & 0x1FFFFFFF) | ((in[54 + inPos] & 0xFF) >>> 5 & 0x1FFFFFFF));
            out[15 + outPos] = (((in[54 + inPos] & 0xFF) << 24 & 0x1FFFFFFF) | ((in[55 + inPos] & 0xFF) << 16 & 0x1FFFFFFF) | ((in[56 + inPos] & 0xFF) << 8 & 0x1FFFFFFF) | (in[57 + inPos] & 0xFF & 0x1FFFFFFF));
            out[16 + outPos] = (((in[58 + inPos] & 0xFF) << 21 & 0x1FFFFFFF) | ((in[59 + inPos] & 0xFF) << 13 & 0x1FFFFFFF) | ((in[60 + inPos] & 0xFF) << 5 & 0x1FFFFFFF) | ((in[61 + inPos] & 0xFF) >>> 3 & 0x1FFFFFFF));
            out[17 + outPos] = (((in[61 + inPos] & 0xFF) << 26 & 0x1FFFFFFF) | ((in[62 + inPos] & 0xFF) << 18 & 0x1FFFFFFF) | ((in[63 + inPos] & 0xFF) << 10 & 0x1FFFFFFF) | ((in[64 + inPos] & 0xFF) << 2 & 0x1FFFFFFF) | ((in[65 + inPos] & 0xFF) >>> 6 & 0x1FFFFFFF));
            out[18 + outPos] = (((in[65 + inPos] & 0xFF) << 23 & 0x1FFFFFFF) | ((in[66 + inPos] & 0xFF) << 15 & 0x1FFFFFFF) | ((in[67 + inPos] & 0xFF) << 7 & 0x1FFFFFFF) | ((in[68 + inPos] & 0xFF) >>> 1 & 0x1FFFFFFF));
            out[19 + outPos] = (((in[68 + inPos] & 0xFF) << 28 & 0x1FFFFFFF) | ((in[69 + inPos] & 0xFF) << 20 & 0x1FFFFFFF) | ((in[70 + inPos] & 0xFF) << 12 & 0x1FFFFFFF) | ((in[71 + inPos] & 0xFF) << 4 & 0x1FFFFFFF) | ((in[72 + inPos] & 0xFF) >>> 4 & 0x1FFFFFFF));
            out[20 + outPos] = (((in[72 + inPos] & 0xFF) << 25 & 0x1FFFFFFF) | ((in[73 + inPos] & 0xFF) << 17 & 0x1FFFFFFF) | ((in[74 + inPos] & 0xFF) << 9 & 0x1FFFFFFF) | ((in[75 + inPos] & 0xFF) << 1 & 0x1FFFFFFF) | ((in[76 + inPos] & 0xFF) >>> 7 & 0x1FFFFFFF));
            out[21 + outPos] = (((in[76 + inPos] & 0xFF) << 22 & 0x1FFFFFFF) | ((in[77 + inPos] & 0xFF) << 14 & 0x1FFFFFFF) | ((in[78 + inPos] & 0xFF) << 6 & 0x1FFFFFFF) | ((in[79 + inPos] & 0xFF) >>> 2 & 0x1FFFFFFF));
            out[22 + outPos] = (((in[79 + inPos] & 0xFF) << 27 & 0x1FFFFFFF) | ((in[80 + inPos] & 0xFF) << 19 & 0x1FFFFFFF) | ((in[81 + inPos] & 0xFF) << 11 & 0x1FFFFFFF) | ((in[82 + inPos] & 0xFF) << 3 & 0x1FFFFFFF) | ((in[83 + inPos] & 0xFF) >>> 5 & 0x1FFFFFFF));
            out[23 + outPos] = (((in[83 + inPos] & 0xFF) << 24 & 0x1FFFFFFF) | ((in[84 + inPos] & 0xFF) << 16 & 0x1FFFFFFF) | ((in[85 + inPos] & 0xFF) << 8 & 0x1FFFFFFF) | (in[86 + inPos] & 0xFF & 0x1FFFFFFF));
            out[24 + outPos] = (((in[87 + inPos] & 0xFF) << 21 & 0x1FFFFFFF) | ((in[88 + inPos] & 0xFF) << 13 & 0x1FFFFFFF) | ((in[89 + inPos] & 0xFF) << 5 & 0x1FFFFFFF) | ((in[90 + inPos] & 0xFF) >>> 3 & 0x1FFFFFFF));
            out[25 + outPos] = (((in[90 + inPos] & 0xFF) << 26 & 0x1FFFFFFF) | ((in[91 + inPos] & 0xFF) << 18 & 0x1FFFFFFF) | ((in[92 + inPos] & 0xFF) << 10 & 0x1FFFFFFF) | ((in[93 + inPos] & 0xFF) << 2 & 0x1FFFFFFF) | ((in[94 + inPos] & 0xFF) >>> 6 & 0x1FFFFFFF));
            out[26 + outPos] = (((in[94 + inPos] & 0xFF) << 23 & 0x1FFFFFFF) | ((in[95 + inPos] & 0xFF) << 15 & 0x1FFFFFFF) | ((in[96 + inPos] & 0xFF) << 7 & 0x1FFFFFFF) | ((in[97 + inPos] & 0xFF) >>> 1 & 0x1FFFFFFF));
            out[27 + outPos] = (((in[97 + inPos] & 0xFF) << 28 & 0x1FFFFFFF) | ((in[98 + inPos] & 0xFF) << 20 & 0x1FFFFFFF) | ((in[99 + inPos] & 0xFF) << 12 & 0x1FFFFFFF) | ((in[100 + inPos] & 0xFF) << 4 & 0x1FFFFFFF) | ((in[101 + inPos] & 0xFF) >>> 4 & 0x1FFFFFFF));
            out[28 + outPos] = (((in[101 + inPos] & 0xFF) << 25 & 0x1FFFFFFF) | ((in[102 + inPos] & 0xFF) << 17 & 0x1FFFFFFF) | ((in[103 + inPos] & 0xFF) << 9 & 0x1FFFFFFF) | ((in[104 + inPos] & 0xFF) << 1 & 0x1FFFFFFF) | ((in[105 + inPos] & 0xFF) >>> 7 & 0x1FFFFFFF));
            out[29 + outPos] = (((in[105 + inPos] & 0xFF) << 22 & 0x1FFFFFFF) | ((in[106 + inPos] & 0xFF) << 14 & 0x1FFFFFFF) | ((in[107 + inPos] & 0xFF) << 6 & 0x1FFFFFFF) | ((in[108 + inPos] & 0xFF) >>> 2 & 0x1FFFFFFF));
            out[30 + outPos] = (((in[108 + inPos] & 0xFF) << 27 & 0x1FFFFFFF) | ((in[109 + inPos] & 0xFF) << 19 & 0x1FFFFFFF) | ((in[110 + inPos] & 0xFF) << 11 & 0x1FFFFFFF) | ((in[111 + inPos] & 0xFF) << 3 & 0x1FFFFFFF) | ((in[112 + inPos] & 0xFF) >>> 5 & 0x1FFFFFFF));
            out[31 + outPos] = (((in[112 + inPos] & 0xFF) << 24 & 0x1FFFFFFF) | ((in[113 + inPos] & 0xFF) << 16 & 0x1FFFFFFF) | ((in[114 + inPos] & 0xFF) << 8 & 0x1FFFFFFF) | (in[115 + inPos] & 0xFF & 0x1FFFFFFF));
        }
    }
    
    private static final class Packer30 extends BytePacker
    {
        private Packer30() {
            super(30);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x3FFFFFFF) << 2 | (in[1 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[6 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[7 + outPos] = (byte)(((in[1 + inPos] & 0x3FFFFFFF) << 4 | (in[2 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[10 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[11 + outPos] = (byte)(((in[2 + inPos] & 0x3FFFFFFF) << 6 | (in[3 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[13 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[14 + outPos] = (byte)(in[3 + inPos] & 0x3FFFFFFF & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[17 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[18 + outPos] = (byte)(((in[4 + inPos] & 0x3FFFFFFF) << 2 | (in[5 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[19 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[20 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[21 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[22 + outPos] = (byte)(((in[5 + inPos] & 0x3FFFFFFF) << 4 | (in[6 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[23 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[24 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[25 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[26 + outPos] = (byte)(((in[6 + inPos] & 0x3FFFFFFF) << 6 | (in[7 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[27 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[28 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[29 + outPos] = (byte)(in[7 + inPos] & 0x3FFFFFFF & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & 0x3FFFFFFF) << 2 | (in[1 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[6 + outPos] = (byte)((in[1 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[7 + outPos] = (byte)(((in[1 + inPos] & 0x3FFFFFFF) << 4 | (in[2 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[10 + outPos] = (byte)((in[2 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[11 + outPos] = (byte)(((in[2 + inPos] & 0x3FFFFFFF) << 6 | (in[3 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[13 + outPos] = (byte)((in[3 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[14 + outPos] = (byte)(in[3 + inPos] & 0x3FFFFFFF & 0xFF);
            out[15 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[17 + outPos] = (byte)((in[4 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[18 + outPos] = (byte)(((in[4 + inPos] & 0x3FFFFFFF) << 2 | (in[5 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[19 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[20 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[21 + outPos] = (byte)((in[5 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[22 + outPos] = (byte)(((in[5 + inPos] & 0x3FFFFFFF) << 4 | (in[6 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[23 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[24 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[25 + outPos] = (byte)((in[6 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[26 + outPos] = (byte)(((in[6 + inPos] & 0x3FFFFFFF) << 6 | (in[7 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[27 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[28 + outPos] = (byte)((in[7 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[29 + outPos] = (byte)(in[7 + inPos] & 0x3FFFFFFF & 0xFF);
            out[30 + outPos] = (byte)((in[8 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[31 + outPos] = (byte)((in[8 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[32 + outPos] = (byte)((in[8 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[33 + outPos] = (byte)(((in[8 + inPos] & 0x3FFFFFFF) << 2 | (in[9 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[34 + outPos] = (byte)((in[9 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[35 + outPos] = (byte)((in[9 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[36 + outPos] = (byte)((in[9 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[37 + outPos] = (byte)(((in[9 + inPos] & 0x3FFFFFFF) << 4 | (in[10 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[38 + outPos] = (byte)((in[10 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[39 + outPos] = (byte)((in[10 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[40 + outPos] = (byte)((in[10 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[41 + outPos] = (byte)(((in[10 + inPos] & 0x3FFFFFFF) << 6 | (in[11 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[42 + outPos] = (byte)((in[11 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[43 + outPos] = (byte)((in[11 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[44 + outPos] = (byte)(in[11 + inPos] & 0x3FFFFFFF & 0xFF);
            out[45 + outPos] = (byte)((in[12 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[46 + outPos] = (byte)((in[12 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[47 + outPos] = (byte)((in[12 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[48 + outPos] = (byte)(((in[12 + inPos] & 0x3FFFFFFF) << 2 | (in[13 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[49 + outPos] = (byte)((in[13 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[50 + outPos] = (byte)((in[13 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[51 + outPos] = (byte)((in[13 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[52 + outPos] = (byte)(((in[13 + inPos] & 0x3FFFFFFF) << 4 | (in[14 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[53 + outPos] = (byte)((in[14 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[54 + outPos] = (byte)((in[14 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[55 + outPos] = (byte)((in[14 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[56 + outPos] = (byte)(((in[14 + inPos] & 0x3FFFFFFF) << 6 | (in[15 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[57 + outPos] = (byte)((in[15 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[58 + outPos] = (byte)((in[15 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[59 + outPos] = (byte)(in[15 + inPos] & 0x3FFFFFFF & 0xFF);
            out[60 + outPos] = (byte)((in[16 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[61 + outPos] = (byte)((in[16 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[62 + outPos] = (byte)((in[16 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[63 + outPos] = (byte)(((in[16 + inPos] & 0x3FFFFFFF) << 2 | (in[17 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[64 + outPos] = (byte)((in[17 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[65 + outPos] = (byte)((in[17 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[66 + outPos] = (byte)((in[17 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[67 + outPos] = (byte)(((in[17 + inPos] & 0x3FFFFFFF) << 4 | (in[18 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[68 + outPos] = (byte)((in[18 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[69 + outPos] = (byte)((in[18 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[70 + outPos] = (byte)((in[18 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[71 + outPos] = (byte)(((in[18 + inPos] & 0x3FFFFFFF) << 6 | (in[19 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[72 + outPos] = (byte)((in[19 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[73 + outPos] = (byte)((in[19 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[74 + outPos] = (byte)(in[19 + inPos] & 0x3FFFFFFF & 0xFF);
            out[75 + outPos] = (byte)((in[20 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[76 + outPos] = (byte)((in[20 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[77 + outPos] = (byte)((in[20 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[78 + outPos] = (byte)(((in[20 + inPos] & 0x3FFFFFFF) << 2 | (in[21 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[79 + outPos] = (byte)((in[21 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[80 + outPos] = (byte)((in[21 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[81 + outPos] = (byte)((in[21 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[82 + outPos] = (byte)(((in[21 + inPos] & 0x3FFFFFFF) << 4 | (in[22 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[83 + outPos] = (byte)((in[22 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[84 + outPos] = (byte)((in[22 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[85 + outPos] = (byte)((in[22 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[86 + outPos] = (byte)(((in[22 + inPos] & 0x3FFFFFFF) << 6 | (in[23 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[87 + outPos] = (byte)((in[23 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[88 + outPos] = (byte)((in[23 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[89 + outPos] = (byte)(in[23 + inPos] & 0x3FFFFFFF & 0xFF);
            out[90 + outPos] = (byte)((in[24 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[91 + outPos] = (byte)((in[24 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[92 + outPos] = (byte)((in[24 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[93 + outPos] = (byte)(((in[24 + inPos] & 0x3FFFFFFF) << 2 | (in[25 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[94 + outPos] = (byte)((in[25 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[95 + outPos] = (byte)((in[25 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[96 + outPos] = (byte)((in[25 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[97 + outPos] = (byte)(((in[25 + inPos] & 0x3FFFFFFF) << 4 | (in[26 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[98 + outPos] = (byte)((in[26 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[99 + outPos] = (byte)((in[26 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[100 + outPos] = (byte)((in[26 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[101 + outPos] = (byte)(((in[26 + inPos] & 0x3FFFFFFF) << 6 | (in[27 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[102 + outPos] = (byte)((in[27 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[103 + outPos] = (byte)((in[27 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[104 + outPos] = (byte)(in[27 + inPos] & 0x3FFFFFFF & 0xFF);
            out[105 + outPos] = (byte)((in[28 + inPos] & 0x3FFFFFFF) >>> 22 & 0xFF);
            out[106 + outPos] = (byte)((in[28 + inPos] & 0x3FFFFFFF) >>> 14 & 0xFF);
            out[107 + outPos] = (byte)((in[28 + inPos] & 0x3FFFFFFF) >>> 6 & 0xFF);
            out[108 + outPos] = (byte)(((in[28 + inPos] & 0x3FFFFFFF) << 2 | (in[29 + inPos] & 0x3FFFFFFF) >>> 28) & 0xFF);
            out[109 + outPos] = (byte)((in[29 + inPos] & 0x3FFFFFFF) >>> 20 & 0xFF);
            out[110 + outPos] = (byte)((in[29 + inPos] & 0x3FFFFFFF) >>> 12 & 0xFF);
            out[111 + outPos] = (byte)((in[29 + inPos] & 0x3FFFFFFF) >>> 4 & 0xFF);
            out[112 + outPos] = (byte)(((in[29 + inPos] & 0x3FFFFFFF) << 4 | (in[30 + inPos] & 0x3FFFFFFF) >>> 26) & 0xFF);
            out[113 + outPos] = (byte)((in[30 + inPos] & 0x3FFFFFFF) >>> 18 & 0xFF);
            out[114 + outPos] = (byte)((in[30 + inPos] & 0x3FFFFFFF) >>> 10 & 0xFF);
            out[115 + outPos] = (byte)((in[30 + inPos] & 0x3FFFFFFF) >>> 2 & 0xFF);
            out[116 + outPos] = (byte)(((in[30 + inPos] & 0x3FFFFFFF) << 6 | (in[31 + inPos] & 0x3FFFFFFF) >>> 24) & 0xFF);
            out[117 + outPos] = (byte)((in[31 + inPos] & 0x3FFFFFFF) >>> 16 & 0xFF);
            out[118 + outPos] = (byte)((in[31 + inPos] & 0x3FFFFFFF) >>> 8 & 0xFF);
            out[119 + outPos] = (byte)(in[31 + inPos] & 0x3FFFFFFF & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[1 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[2 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[3 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[4 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[5 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[6 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[2 + outPos] = (((in[7 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[8 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[9 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[10 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[3 + outPos] = (((in[11 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[12 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[13 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[14 + inPos] & 0xFF & 0x3FFFFFFF));
            out[4 + outPos] = (((in[15 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[16 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[17 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[18 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[5 + outPos] = (((in[18 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[19 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[20 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[21 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[22 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[6 + outPos] = (((in[22 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[23 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[24 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[25 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[26 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[7 + outPos] = (((in[26 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[27 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[28 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[29 + inPos] & 0xFF & 0x3FFFFFFF));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[1 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[2 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[3 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[4 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[5 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[6 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[7 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[2 + outPos] = (((in[7 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[8 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[9 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[10 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[11 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[3 + outPos] = (((in[11 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[12 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[13 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[14 + inPos] & 0xFF & 0x3FFFFFFF));
            out[4 + outPos] = (((in[15 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[16 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[17 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[18 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[5 + outPos] = (((in[18 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[19 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[20 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[21 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[22 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[6 + outPos] = (((in[22 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[23 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[24 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[25 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[26 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[7 + outPos] = (((in[26 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[27 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[28 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[29 + inPos] & 0xFF & 0x3FFFFFFF));
            out[8 + outPos] = (((in[30 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[31 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[32 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[33 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[9 + outPos] = (((in[33 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[34 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[35 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[36 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[37 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[10 + outPos] = (((in[37 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[38 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[39 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[40 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[41 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[11 + outPos] = (((in[41 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[42 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[43 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[44 + inPos] & 0xFF & 0x3FFFFFFF));
            out[12 + outPos] = (((in[45 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[46 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[47 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[48 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[13 + outPos] = (((in[48 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[49 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[50 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[51 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[52 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[14 + outPos] = (((in[52 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[53 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[54 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[55 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[56 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[15 + outPos] = (((in[56 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[57 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[58 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[59 + inPos] & 0xFF & 0x3FFFFFFF));
            out[16 + outPos] = (((in[60 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[61 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[62 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[63 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[17 + outPos] = (((in[63 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[64 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[65 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[66 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[67 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[18 + outPos] = (((in[67 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[68 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[69 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[70 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[71 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[19 + outPos] = (((in[71 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[72 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[73 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[74 + inPos] & 0xFF & 0x3FFFFFFF));
            out[20 + outPos] = (((in[75 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[76 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[77 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[78 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[21 + outPos] = (((in[78 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[79 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[80 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[81 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[82 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[22 + outPos] = (((in[82 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[83 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[84 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[85 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[86 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[23 + outPos] = (((in[86 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[87 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[88 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[89 + inPos] & 0xFF & 0x3FFFFFFF));
            out[24 + outPos] = (((in[90 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[91 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[92 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[93 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[25 + outPos] = (((in[93 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[94 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[95 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[96 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[97 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[26 + outPos] = (((in[97 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[98 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[99 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[100 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[101 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[27 + outPos] = (((in[101 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[102 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[103 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[104 + inPos] & 0xFF & 0x3FFFFFFF));
            out[28 + outPos] = (((in[105 + inPos] & 0xFF) << 22 & 0x3FFFFFFF) | ((in[106 + inPos] & 0xFF) << 14 & 0x3FFFFFFF) | ((in[107 + inPos] & 0xFF) << 6 & 0x3FFFFFFF) | ((in[108 + inPos] & 0xFF) >>> 2 & 0x3FFFFFFF));
            out[29 + outPos] = (((in[108 + inPos] & 0xFF) << 28 & 0x3FFFFFFF) | ((in[109 + inPos] & 0xFF) << 20 & 0x3FFFFFFF) | ((in[110 + inPos] & 0xFF) << 12 & 0x3FFFFFFF) | ((in[111 + inPos] & 0xFF) << 4 & 0x3FFFFFFF) | ((in[112 + inPos] & 0xFF) >>> 4 & 0x3FFFFFFF));
            out[30 + outPos] = (((in[112 + inPos] & 0xFF) << 26 & 0x3FFFFFFF) | ((in[113 + inPos] & 0xFF) << 18 & 0x3FFFFFFF) | ((in[114 + inPos] & 0xFF) << 10 & 0x3FFFFFFF) | ((in[115 + inPos] & 0xFF) << 2 & 0x3FFFFFFF) | ((in[116 + inPos] & 0xFF) >>> 6 & 0x3FFFFFFF));
            out[31 + outPos] = (((in[116 + inPos] & 0xFF) << 24 & 0x3FFFFFFF) | ((in[117 + inPos] & 0xFF) << 16 & 0x3FFFFFFF) | ((in[118 + inPos] & 0xFF) << 8 & 0x3FFFFFFF) | (in[119 + inPos] & 0xFF & 0x3FFFFFFF));
        }
    }
    
    private static final class Packer31 extends BytePacker
    {
        private Packer31() {
            super(31);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & Integer.MAX_VALUE) >>> 23 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & Integer.MAX_VALUE) >>> 15 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & Integer.MAX_VALUE) >>> 7 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & Integer.MAX_VALUE) << 1 | (in[1 + inPos] & Integer.MAX_VALUE) >>> 30) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & Integer.MAX_VALUE) >>> 22 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & Integer.MAX_VALUE) >>> 14 & 0xFF);
            out[6 + outPos] = (byte)((in[1 + inPos] & Integer.MAX_VALUE) >>> 6 & 0xFF);
            out[7 + outPos] = (byte)(((in[1 + inPos] & Integer.MAX_VALUE) << 2 | (in[2 + inPos] & Integer.MAX_VALUE) >>> 29) & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & Integer.MAX_VALUE) >>> 21 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & Integer.MAX_VALUE) >>> 13 & 0xFF);
            out[10 + outPos] = (byte)((in[2 + inPos] & Integer.MAX_VALUE) >>> 5 & 0xFF);
            out[11 + outPos] = (byte)(((in[2 + inPos] & Integer.MAX_VALUE) << 3 | (in[3 + inPos] & Integer.MAX_VALUE) >>> 28) & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & Integer.MAX_VALUE) >>> 20 & 0xFF);
            out[13 + outPos] = (byte)((in[3 + inPos] & Integer.MAX_VALUE) >>> 12 & 0xFF);
            out[14 + outPos] = (byte)((in[3 + inPos] & Integer.MAX_VALUE) >>> 4 & 0xFF);
            out[15 + outPos] = (byte)(((in[3 + inPos] & Integer.MAX_VALUE) << 4 | (in[4 + inPos] & Integer.MAX_VALUE) >>> 27) & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & Integer.MAX_VALUE) >>> 19 & 0xFF);
            out[17 + outPos] = (byte)((in[4 + inPos] & Integer.MAX_VALUE) >>> 11 & 0xFF);
            out[18 + outPos] = (byte)((in[4 + inPos] & Integer.MAX_VALUE) >>> 3 & 0xFF);
            out[19 + outPos] = (byte)(((in[4 + inPos] & Integer.MAX_VALUE) << 5 | (in[5 + inPos] & Integer.MAX_VALUE) >>> 26) & 0xFF);
            out[20 + outPos] = (byte)((in[5 + inPos] & Integer.MAX_VALUE) >>> 18 & 0xFF);
            out[21 + outPos] = (byte)((in[5 + inPos] & Integer.MAX_VALUE) >>> 10 & 0xFF);
            out[22 + outPos] = (byte)((in[5 + inPos] & Integer.MAX_VALUE) >>> 2 & 0xFF);
            out[23 + outPos] = (byte)(((in[5 + inPos] & Integer.MAX_VALUE) << 6 | (in[6 + inPos] & Integer.MAX_VALUE) >>> 25) & 0xFF);
            out[24 + outPos] = (byte)((in[6 + inPos] & Integer.MAX_VALUE) >>> 17 & 0xFF);
            out[25 + outPos] = (byte)((in[6 + inPos] & Integer.MAX_VALUE) >>> 9 & 0xFF);
            out[26 + outPos] = (byte)((in[6 + inPos] & Integer.MAX_VALUE) >>> 1 & 0xFF);
            out[27 + outPos] = (byte)(((in[6 + inPos] & Integer.MAX_VALUE) << 7 | (in[7 + inPos] & Integer.MAX_VALUE) >>> 24) & 0xFF);
            out[28 + outPos] = (byte)((in[7 + inPos] & Integer.MAX_VALUE) >>> 16 & 0xFF);
            out[29 + outPos] = (byte)((in[7 + inPos] & Integer.MAX_VALUE) >>> 8 & 0xFF);
            out[30 + outPos] = (byte)(in[7 + inPos] & Integer.MAX_VALUE & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & Integer.MAX_VALUE) >>> 23 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & Integer.MAX_VALUE) >>> 15 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & Integer.MAX_VALUE) >>> 7 & 0xFF);
            out[3 + outPos] = (byte)(((in[0 + inPos] & Integer.MAX_VALUE) << 1 | (in[1 + inPos] & Integer.MAX_VALUE) >>> 30) & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & Integer.MAX_VALUE) >>> 22 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & Integer.MAX_VALUE) >>> 14 & 0xFF);
            out[6 + outPos] = (byte)((in[1 + inPos] & Integer.MAX_VALUE) >>> 6 & 0xFF);
            out[7 + outPos] = (byte)(((in[1 + inPos] & Integer.MAX_VALUE) << 2 | (in[2 + inPos] & Integer.MAX_VALUE) >>> 29) & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & Integer.MAX_VALUE) >>> 21 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & Integer.MAX_VALUE) >>> 13 & 0xFF);
            out[10 + outPos] = (byte)((in[2 + inPos] & Integer.MAX_VALUE) >>> 5 & 0xFF);
            out[11 + outPos] = (byte)(((in[2 + inPos] & Integer.MAX_VALUE) << 3 | (in[3 + inPos] & Integer.MAX_VALUE) >>> 28) & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & Integer.MAX_VALUE) >>> 20 & 0xFF);
            out[13 + outPos] = (byte)((in[3 + inPos] & Integer.MAX_VALUE) >>> 12 & 0xFF);
            out[14 + outPos] = (byte)((in[3 + inPos] & Integer.MAX_VALUE) >>> 4 & 0xFF);
            out[15 + outPos] = (byte)(((in[3 + inPos] & Integer.MAX_VALUE) << 4 | (in[4 + inPos] & Integer.MAX_VALUE) >>> 27) & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & Integer.MAX_VALUE) >>> 19 & 0xFF);
            out[17 + outPos] = (byte)((in[4 + inPos] & Integer.MAX_VALUE) >>> 11 & 0xFF);
            out[18 + outPos] = (byte)((in[4 + inPos] & Integer.MAX_VALUE) >>> 3 & 0xFF);
            out[19 + outPos] = (byte)(((in[4 + inPos] & Integer.MAX_VALUE) << 5 | (in[5 + inPos] & Integer.MAX_VALUE) >>> 26) & 0xFF);
            out[20 + outPos] = (byte)((in[5 + inPos] & Integer.MAX_VALUE) >>> 18 & 0xFF);
            out[21 + outPos] = (byte)((in[5 + inPos] & Integer.MAX_VALUE) >>> 10 & 0xFF);
            out[22 + outPos] = (byte)((in[5 + inPos] & Integer.MAX_VALUE) >>> 2 & 0xFF);
            out[23 + outPos] = (byte)(((in[5 + inPos] & Integer.MAX_VALUE) << 6 | (in[6 + inPos] & Integer.MAX_VALUE) >>> 25) & 0xFF);
            out[24 + outPos] = (byte)((in[6 + inPos] & Integer.MAX_VALUE) >>> 17 & 0xFF);
            out[25 + outPos] = (byte)((in[6 + inPos] & Integer.MAX_VALUE) >>> 9 & 0xFF);
            out[26 + outPos] = (byte)((in[6 + inPos] & Integer.MAX_VALUE) >>> 1 & 0xFF);
            out[27 + outPos] = (byte)(((in[6 + inPos] & Integer.MAX_VALUE) << 7 | (in[7 + inPos] & Integer.MAX_VALUE) >>> 24) & 0xFF);
            out[28 + outPos] = (byte)((in[7 + inPos] & Integer.MAX_VALUE) >>> 16 & 0xFF);
            out[29 + outPos] = (byte)((in[7 + inPos] & Integer.MAX_VALUE) >>> 8 & 0xFF);
            out[30 + outPos] = (byte)(in[7 + inPos] & Integer.MAX_VALUE & 0xFF);
            out[31 + outPos] = (byte)((in[8 + inPos] & Integer.MAX_VALUE) >>> 23 & 0xFF);
            out[32 + outPos] = (byte)((in[8 + inPos] & Integer.MAX_VALUE) >>> 15 & 0xFF);
            out[33 + outPos] = (byte)((in[8 + inPos] & Integer.MAX_VALUE) >>> 7 & 0xFF);
            out[34 + outPos] = (byte)(((in[8 + inPos] & Integer.MAX_VALUE) << 1 | (in[9 + inPos] & Integer.MAX_VALUE) >>> 30) & 0xFF);
            out[35 + outPos] = (byte)((in[9 + inPos] & Integer.MAX_VALUE) >>> 22 & 0xFF);
            out[36 + outPos] = (byte)((in[9 + inPos] & Integer.MAX_VALUE) >>> 14 & 0xFF);
            out[37 + outPos] = (byte)((in[9 + inPos] & Integer.MAX_VALUE) >>> 6 & 0xFF);
            out[38 + outPos] = (byte)(((in[9 + inPos] & Integer.MAX_VALUE) << 2 | (in[10 + inPos] & Integer.MAX_VALUE) >>> 29) & 0xFF);
            out[39 + outPos] = (byte)((in[10 + inPos] & Integer.MAX_VALUE) >>> 21 & 0xFF);
            out[40 + outPos] = (byte)((in[10 + inPos] & Integer.MAX_VALUE) >>> 13 & 0xFF);
            out[41 + outPos] = (byte)((in[10 + inPos] & Integer.MAX_VALUE) >>> 5 & 0xFF);
            out[42 + outPos] = (byte)(((in[10 + inPos] & Integer.MAX_VALUE) << 3 | (in[11 + inPos] & Integer.MAX_VALUE) >>> 28) & 0xFF);
            out[43 + outPos] = (byte)((in[11 + inPos] & Integer.MAX_VALUE) >>> 20 & 0xFF);
            out[44 + outPos] = (byte)((in[11 + inPos] & Integer.MAX_VALUE) >>> 12 & 0xFF);
            out[45 + outPos] = (byte)((in[11 + inPos] & Integer.MAX_VALUE) >>> 4 & 0xFF);
            out[46 + outPos] = (byte)(((in[11 + inPos] & Integer.MAX_VALUE) << 4 | (in[12 + inPos] & Integer.MAX_VALUE) >>> 27) & 0xFF);
            out[47 + outPos] = (byte)((in[12 + inPos] & Integer.MAX_VALUE) >>> 19 & 0xFF);
            out[48 + outPos] = (byte)((in[12 + inPos] & Integer.MAX_VALUE) >>> 11 & 0xFF);
            out[49 + outPos] = (byte)((in[12 + inPos] & Integer.MAX_VALUE) >>> 3 & 0xFF);
            out[50 + outPos] = (byte)(((in[12 + inPos] & Integer.MAX_VALUE) << 5 | (in[13 + inPos] & Integer.MAX_VALUE) >>> 26) & 0xFF);
            out[51 + outPos] = (byte)((in[13 + inPos] & Integer.MAX_VALUE) >>> 18 & 0xFF);
            out[52 + outPos] = (byte)((in[13 + inPos] & Integer.MAX_VALUE) >>> 10 & 0xFF);
            out[53 + outPos] = (byte)((in[13 + inPos] & Integer.MAX_VALUE) >>> 2 & 0xFF);
            out[54 + outPos] = (byte)(((in[13 + inPos] & Integer.MAX_VALUE) << 6 | (in[14 + inPos] & Integer.MAX_VALUE) >>> 25) & 0xFF);
            out[55 + outPos] = (byte)((in[14 + inPos] & Integer.MAX_VALUE) >>> 17 & 0xFF);
            out[56 + outPos] = (byte)((in[14 + inPos] & Integer.MAX_VALUE) >>> 9 & 0xFF);
            out[57 + outPos] = (byte)((in[14 + inPos] & Integer.MAX_VALUE) >>> 1 & 0xFF);
            out[58 + outPos] = (byte)(((in[14 + inPos] & Integer.MAX_VALUE) << 7 | (in[15 + inPos] & Integer.MAX_VALUE) >>> 24) & 0xFF);
            out[59 + outPos] = (byte)((in[15 + inPos] & Integer.MAX_VALUE) >>> 16 & 0xFF);
            out[60 + outPos] = (byte)((in[15 + inPos] & Integer.MAX_VALUE) >>> 8 & 0xFF);
            out[61 + outPos] = (byte)(in[15 + inPos] & Integer.MAX_VALUE & 0xFF);
            out[62 + outPos] = (byte)((in[16 + inPos] & Integer.MAX_VALUE) >>> 23 & 0xFF);
            out[63 + outPos] = (byte)((in[16 + inPos] & Integer.MAX_VALUE) >>> 15 & 0xFF);
            out[64 + outPos] = (byte)((in[16 + inPos] & Integer.MAX_VALUE) >>> 7 & 0xFF);
            out[65 + outPos] = (byte)(((in[16 + inPos] & Integer.MAX_VALUE) << 1 | (in[17 + inPos] & Integer.MAX_VALUE) >>> 30) & 0xFF);
            out[66 + outPos] = (byte)((in[17 + inPos] & Integer.MAX_VALUE) >>> 22 & 0xFF);
            out[67 + outPos] = (byte)((in[17 + inPos] & Integer.MAX_VALUE) >>> 14 & 0xFF);
            out[68 + outPos] = (byte)((in[17 + inPos] & Integer.MAX_VALUE) >>> 6 & 0xFF);
            out[69 + outPos] = (byte)(((in[17 + inPos] & Integer.MAX_VALUE) << 2 | (in[18 + inPos] & Integer.MAX_VALUE) >>> 29) & 0xFF);
            out[70 + outPos] = (byte)((in[18 + inPos] & Integer.MAX_VALUE) >>> 21 & 0xFF);
            out[71 + outPos] = (byte)((in[18 + inPos] & Integer.MAX_VALUE) >>> 13 & 0xFF);
            out[72 + outPos] = (byte)((in[18 + inPos] & Integer.MAX_VALUE) >>> 5 & 0xFF);
            out[73 + outPos] = (byte)(((in[18 + inPos] & Integer.MAX_VALUE) << 3 | (in[19 + inPos] & Integer.MAX_VALUE) >>> 28) & 0xFF);
            out[74 + outPos] = (byte)((in[19 + inPos] & Integer.MAX_VALUE) >>> 20 & 0xFF);
            out[75 + outPos] = (byte)((in[19 + inPos] & Integer.MAX_VALUE) >>> 12 & 0xFF);
            out[76 + outPos] = (byte)((in[19 + inPos] & Integer.MAX_VALUE) >>> 4 & 0xFF);
            out[77 + outPos] = (byte)(((in[19 + inPos] & Integer.MAX_VALUE) << 4 | (in[20 + inPos] & Integer.MAX_VALUE) >>> 27) & 0xFF);
            out[78 + outPos] = (byte)((in[20 + inPos] & Integer.MAX_VALUE) >>> 19 & 0xFF);
            out[79 + outPos] = (byte)((in[20 + inPos] & Integer.MAX_VALUE) >>> 11 & 0xFF);
            out[80 + outPos] = (byte)((in[20 + inPos] & Integer.MAX_VALUE) >>> 3 & 0xFF);
            out[81 + outPos] = (byte)(((in[20 + inPos] & Integer.MAX_VALUE) << 5 | (in[21 + inPos] & Integer.MAX_VALUE) >>> 26) & 0xFF);
            out[82 + outPos] = (byte)((in[21 + inPos] & Integer.MAX_VALUE) >>> 18 & 0xFF);
            out[83 + outPos] = (byte)((in[21 + inPos] & Integer.MAX_VALUE) >>> 10 & 0xFF);
            out[84 + outPos] = (byte)((in[21 + inPos] & Integer.MAX_VALUE) >>> 2 & 0xFF);
            out[85 + outPos] = (byte)(((in[21 + inPos] & Integer.MAX_VALUE) << 6 | (in[22 + inPos] & Integer.MAX_VALUE) >>> 25) & 0xFF);
            out[86 + outPos] = (byte)((in[22 + inPos] & Integer.MAX_VALUE) >>> 17 & 0xFF);
            out[87 + outPos] = (byte)((in[22 + inPos] & Integer.MAX_VALUE) >>> 9 & 0xFF);
            out[88 + outPos] = (byte)((in[22 + inPos] & Integer.MAX_VALUE) >>> 1 & 0xFF);
            out[89 + outPos] = (byte)(((in[22 + inPos] & Integer.MAX_VALUE) << 7 | (in[23 + inPos] & Integer.MAX_VALUE) >>> 24) & 0xFF);
            out[90 + outPos] = (byte)((in[23 + inPos] & Integer.MAX_VALUE) >>> 16 & 0xFF);
            out[91 + outPos] = (byte)((in[23 + inPos] & Integer.MAX_VALUE) >>> 8 & 0xFF);
            out[92 + outPos] = (byte)(in[23 + inPos] & Integer.MAX_VALUE & 0xFF);
            out[93 + outPos] = (byte)((in[24 + inPos] & Integer.MAX_VALUE) >>> 23 & 0xFF);
            out[94 + outPos] = (byte)((in[24 + inPos] & Integer.MAX_VALUE) >>> 15 & 0xFF);
            out[95 + outPos] = (byte)((in[24 + inPos] & Integer.MAX_VALUE) >>> 7 & 0xFF);
            out[96 + outPos] = (byte)(((in[24 + inPos] & Integer.MAX_VALUE) << 1 | (in[25 + inPos] & Integer.MAX_VALUE) >>> 30) & 0xFF);
            out[97 + outPos] = (byte)((in[25 + inPos] & Integer.MAX_VALUE) >>> 22 & 0xFF);
            out[98 + outPos] = (byte)((in[25 + inPos] & Integer.MAX_VALUE) >>> 14 & 0xFF);
            out[99 + outPos] = (byte)((in[25 + inPos] & Integer.MAX_VALUE) >>> 6 & 0xFF);
            out[100 + outPos] = (byte)(((in[25 + inPos] & Integer.MAX_VALUE) << 2 | (in[26 + inPos] & Integer.MAX_VALUE) >>> 29) & 0xFF);
            out[101 + outPos] = (byte)((in[26 + inPos] & Integer.MAX_VALUE) >>> 21 & 0xFF);
            out[102 + outPos] = (byte)((in[26 + inPos] & Integer.MAX_VALUE) >>> 13 & 0xFF);
            out[103 + outPos] = (byte)((in[26 + inPos] & Integer.MAX_VALUE) >>> 5 & 0xFF);
            out[104 + outPos] = (byte)(((in[26 + inPos] & Integer.MAX_VALUE) << 3 | (in[27 + inPos] & Integer.MAX_VALUE) >>> 28) & 0xFF);
            out[105 + outPos] = (byte)((in[27 + inPos] & Integer.MAX_VALUE) >>> 20 & 0xFF);
            out[106 + outPos] = (byte)((in[27 + inPos] & Integer.MAX_VALUE) >>> 12 & 0xFF);
            out[107 + outPos] = (byte)((in[27 + inPos] & Integer.MAX_VALUE) >>> 4 & 0xFF);
            out[108 + outPos] = (byte)(((in[27 + inPos] & Integer.MAX_VALUE) << 4 | (in[28 + inPos] & Integer.MAX_VALUE) >>> 27) & 0xFF);
            out[109 + outPos] = (byte)((in[28 + inPos] & Integer.MAX_VALUE) >>> 19 & 0xFF);
            out[110 + outPos] = (byte)((in[28 + inPos] & Integer.MAX_VALUE) >>> 11 & 0xFF);
            out[111 + outPos] = (byte)((in[28 + inPos] & Integer.MAX_VALUE) >>> 3 & 0xFF);
            out[112 + outPos] = (byte)(((in[28 + inPos] & Integer.MAX_VALUE) << 5 | (in[29 + inPos] & Integer.MAX_VALUE) >>> 26) & 0xFF);
            out[113 + outPos] = (byte)((in[29 + inPos] & Integer.MAX_VALUE) >>> 18 & 0xFF);
            out[114 + outPos] = (byte)((in[29 + inPos] & Integer.MAX_VALUE) >>> 10 & 0xFF);
            out[115 + outPos] = (byte)((in[29 + inPos] & Integer.MAX_VALUE) >>> 2 & 0xFF);
            out[116 + outPos] = (byte)(((in[29 + inPos] & Integer.MAX_VALUE) << 6 | (in[30 + inPos] & Integer.MAX_VALUE) >>> 25) & 0xFF);
            out[117 + outPos] = (byte)((in[30 + inPos] & Integer.MAX_VALUE) >>> 17 & 0xFF);
            out[118 + outPos] = (byte)((in[30 + inPos] & Integer.MAX_VALUE) >>> 9 & 0xFF);
            out[119 + outPos] = (byte)((in[30 + inPos] & Integer.MAX_VALUE) >>> 1 & 0xFF);
            out[120 + outPos] = (byte)(((in[30 + inPos] & Integer.MAX_VALUE) << 7 | (in[31 + inPos] & Integer.MAX_VALUE) >>> 24) & 0xFF);
            out[121 + outPos] = (byte)((in[31 + inPos] & Integer.MAX_VALUE) >>> 16 & 0xFF);
            out[122 + outPos] = (byte)((in[31 + inPos] & Integer.MAX_VALUE) >>> 8 & 0xFF);
            out[123 + outPos] = (byte)(in[31 + inPos] & Integer.MAX_VALUE & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 23 & Integer.MAX_VALUE) | ((in[1 + inPos] & 0xFF) << 15 & Integer.MAX_VALUE) | ((in[2 + inPos] & 0xFF) << 7 & Integer.MAX_VALUE) | ((in[3 + inPos] & 0xFF) >>> 1 & Integer.MAX_VALUE));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 30 & Integer.MAX_VALUE) | ((in[4 + inPos] & 0xFF) << 22 & Integer.MAX_VALUE) | ((in[5 + inPos] & 0xFF) << 14 & Integer.MAX_VALUE) | ((in[6 + inPos] & 0xFF) << 6 & Integer.MAX_VALUE) | ((in[7 + inPos] & 0xFF) >>> 2 & Integer.MAX_VALUE));
            out[2 + outPos] = (((in[7 + inPos] & 0xFF) << 29 & Integer.MAX_VALUE) | ((in[8 + inPos] & 0xFF) << 21 & Integer.MAX_VALUE) | ((in[9 + inPos] & 0xFF) << 13 & Integer.MAX_VALUE) | ((in[10 + inPos] & 0xFF) << 5 & Integer.MAX_VALUE) | ((in[11 + inPos] & 0xFF) >>> 3 & Integer.MAX_VALUE));
            out[3 + outPos] = (((in[11 + inPos] & 0xFF) << 28 & Integer.MAX_VALUE) | ((in[12 + inPos] & 0xFF) << 20 & Integer.MAX_VALUE) | ((in[13 + inPos] & 0xFF) << 12 & Integer.MAX_VALUE) | ((in[14 + inPos] & 0xFF) << 4 & Integer.MAX_VALUE) | ((in[15 + inPos] & 0xFF) >>> 4 & Integer.MAX_VALUE));
            out[4 + outPos] = (((in[15 + inPos] & 0xFF) << 27 & Integer.MAX_VALUE) | ((in[16 + inPos] & 0xFF) << 19 & Integer.MAX_VALUE) | ((in[17 + inPos] & 0xFF) << 11 & Integer.MAX_VALUE) | ((in[18 + inPos] & 0xFF) << 3 & Integer.MAX_VALUE) | ((in[19 + inPos] & 0xFF) >>> 5 & Integer.MAX_VALUE));
            out[5 + outPos] = (((in[19 + inPos] & 0xFF) << 26 & Integer.MAX_VALUE) | ((in[20 + inPos] & 0xFF) << 18 & Integer.MAX_VALUE) | ((in[21 + inPos] & 0xFF) << 10 & Integer.MAX_VALUE) | ((in[22 + inPos] & 0xFF) << 2 & Integer.MAX_VALUE) | ((in[23 + inPos] & 0xFF) >>> 6 & Integer.MAX_VALUE));
            out[6 + outPos] = (((in[23 + inPos] & 0xFF) << 25 & Integer.MAX_VALUE) | ((in[24 + inPos] & 0xFF) << 17 & Integer.MAX_VALUE) | ((in[25 + inPos] & 0xFF) << 9 & Integer.MAX_VALUE) | ((in[26 + inPos] & 0xFF) << 1 & Integer.MAX_VALUE) | ((in[27 + inPos] & 0xFF) >>> 7 & Integer.MAX_VALUE));
            out[7 + outPos] = (((in[27 + inPos] & 0xFF) << 24 & Integer.MAX_VALUE) | ((in[28 + inPos] & 0xFF) << 16 & Integer.MAX_VALUE) | ((in[29 + inPos] & 0xFF) << 8 & Integer.MAX_VALUE) | (in[30 + inPos] & 0xFF & Integer.MAX_VALUE));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 23 & Integer.MAX_VALUE) | ((in[1 + inPos] & 0xFF) << 15 & Integer.MAX_VALUE) | ((in[2 + inPos] & 0xFF) << 7 & Integer.MAX_VALUE) | ((in[3 + inPos] & 0xFF) >>> 1 & Integer.MAX_VALUE));
            out[1 + outPos] = (((in[3 + inPos] & 0xFF) << 30 & Integer.MAX_VALUE) | ((in[4 + inPos] & 0xFF) << 22 & Integer.MAX_VALUE) | ((in[5 + inPos] & 0xFF) << 14 & Integer.MAX_VALUE) | ((in[6 + inPos] & 0xFF) << 6 & Integer.MAX_VALUE) | ((in[7 + inPos] & 0xFF) >>> 2 & Integer.MAX_VALUE));
            out[2 + outPos] = (((in[7 + inPos] & 0xFF) << 29 & Integer.MAX_VALUE) | ((in[8 + inPos] & 0xFF) << 21 & Integer.MAX_VALUE) | ((in[9 + inPos] & 0xFF) << 13 & Integer.MAX_VALUE) | ((in[10 + inPos] & 0xFF) << 5 & Integer.MAX_VALUE) | ((in[11 + inPos] & 0xFF) >>> 3 & Integer.MAX_VALUE));
            out[3 + outPos] = (((in[11 + inPos] & 0xFF) << 28 & Integer.MAX_VALUE) | ((in[12 + inPos] & 0xFF) << 20 & Integer.MAX_VALUE) | ((in[13 + inPos] & 0xFF) << 12 & Integer.MAX_VALUE) | ((in[14 + inPos] & 0xFF) << 4 & Integer.MAX_VALUE) | ((in[15 + inPos] & 0xFF) >>> 4 & Integer.MAX_VALUE));
            out[4 + outPos] = (((in[15 + inPos] & 0xFF) << 27 & Integer.MAX_VALUE) | ((in[16 + inPos] & 0xFF) << 19 & Integer.MAX_VALUE) | ((in[17 + inPos] & 0xFF) << 11 & Integer.MAX_VALUE) | ((in[18 + inPos] & 0xFF) << 3 & Integer.MAX_VALUE) | ((in[19 + inPos] & 0xFF) >>> 5 & Integer.MAX_VALUE));
            out[5 + outPos] = (((in[19 + inPos] & 0xFF) << 26 & Integer.MAX_VALUE) | ((in[20 + inPos] & 0xFF) << 18 & Integer.MAX_VALUE) | ((in[21 + inPos] & 0xFF) << 10 & Integer.MAX_VALUE) | ((in[22 + inPos] & 0xFF) << 2 & Integer.MAX_VALUE) | ((in[23 + inPos] & 0xFF) >>> 6 & Integer.MAX_VALUE));
            out[6 + outPos] = (((in[23 + inPos] & 0xFF) << 25 & Integer.MAX_VALUE) | ((in[24 + inPos] & 0xFF) << 17 & Integer.MAX_VALUE) | ((in[25 + inPos] & 0xFF) << 9 & Integer.MAX_VALUE) | ((in[26 + inPos] & 0xFF) << 1 & Integer.MAX_VALUE) | ((in[27 + inPos] & 0xFF) >>> 7 & Integer.MAX_VALUE));
            out[7 + outPos] = (((in[27 + inPos] & 0xFF) << 24 & Integer.MAX_VALUE) | ((in[28 + inPos] & 0xFF) << 16 & Integer.MAX_VALUE) | ((in[29 + inPos] & 0xFF) << 8 & Integer.MAX_VALUE) | (in[30 + inPos] & 0xFF & Integer.MAX_VALUE));
            out[8 + outPos] = (((in[31 + inPos] & 0xFF) << 23 & Integer.MAX_VALUE) | ((in[32 + inPos] & 0xFF) << 15 & Integer.MAX_VALUE) | ((in[33 + inPos] & 0xFF) << 7 & Integer.MAX_VALUE) | ((in[34 + inPos] & 0xFF) >>> 1 & Integer.MAX_VALUE));
            out[9 + outPos] = (((in[34 + inPos] & 0xFF) << 30 & Integer.MAX_VALUE) | ((in[35 + inPos] & 0xFF) << 22 & Integer.MAX_VALUE) | ((in[36 + inPos] & 0xFF) << 14 & Integer.MAX_VALUE) | ((in[37 + inPos] & 0xFF) << 6 & Integer.MAX_VALUE) | ((in[38 + inPos] & 0xFF) >>> 2 & Integer.MAX_VALUE));
            out[10 + outPos] = (((in[38 + inPos] & 0xFF) << 29 & Integer.MAX_VALUE) | ((in[39 + inPos] & 0xFF) << 21 & Integer.MAX_VALUE) | ((in[40 + inPos] & 0xFF) << 13 & Integer.MAX_VALUE) | ((in[41 + inPos] & 0xFF) << 5 & Integer.MAX_VALUE) | ((in[42 + inPos] & 0xFF) >>> 3 & Integer.MAX_VALUE));
            out[11 + outPos] = (((in[42 + inPos] & 0xFF) << 28 & Integer.MAX_VALUE) | ((in[43 + inPos] & 0xFF) << 20 & Integer.MAX_VALUE) | ((in[44 + inPos] & 0xFF) << 12 & Integer.MAX_VALUE) | ((in[45 + inPos] & 0xFF) << 4 & Integer.MAX_VALUE) | ((in[46 + inPos] & 0xFF) >>> 4 & Integer.MAX_VALUE));
            out[12 + outPos] = (((in[46 + inPos] & 0xFF) << 27 & Integer.MAX_VALUE) | ((in[47 + inPos] & 0xFF) << 19 & Integer.MAX_VALUE) | ((in[48 + inPos] & 0xFF) << 11 & Integer.MAX_VALUE) | ((in[49 + inPos] & 0xFF) << 3 & Integer.MAX_VALUE) | ((in[50 + inPos] & 0xFF) >>> 5 & Integer.MAX_VALUE));
            out[13 + outPos] = (((in[50 + inPos] & 0xFF) << 26 & Integer.MAX_VALUE) | ((in[51 + inPos] & 0xFF) << 18 & Integer.MAX_VALUE) | ((in[52 + inPos] & 0xFF) << 10 & Integer.MAX_VALUE) | ((in[53 + inPos] & 0xFF) << 2 & Integer.MAX_VALUE) | ((in[54 + inPos] & 0xFF) >>> 6 & Integer.MAX_VALUE));
            out[14 + outPos] = (((in[54 + inPos] & 0xFF) << 25 & Integer.MAX_VALUE) | ((in[55 + inPos] & 0xFF) << 17 & Integer.MAX_VALUE) | ((in[56 + inPos] & 0xFF) << 9 & Integer.MAX_VALUE) | ((in[57 + inPos] & 0xFF) << 1 & Integer.MAX_VALUE) | ((in[58 + inPos] & 0xFF) >>> 7 & Integer.MAX_VALUE));
            out[15 + outPos] = (((in[58 + inPos] & 0xFF) << 24 & Integer.MAX_VALUE) | ((in[59 + inPos] & 0xFF) << 16 & Integer.MAX_VALUE) | ((in[60 + inPos] & 0xFF) << 8 & Integer.MAX_VALUE) | (in[61 + inPos] & 0xFF & Integer.MAX_VALUE));
            out[16 + outPos] = (((in[62 + inPos] & 0xFF) << 23 & Integer.MAX_VALUE) | ((in[63 + inPos] & 0xFF) << 15 & Integer.MAX_VALUE) | ((in[64 + inPos] & 0xFF) << 7 & Integer.MAX_VALUE) | ((in[65 + inPos] & 0xFF) >>> 1 & Integer.MAX_VALUE));
            out[17 + outPos] = (((in[65 + inPos] & 0xFF) << 30 & Integer.MAX_VALUE) | ((in[66 + inPos] & 0xFF) << 22 & Integer.MAX_VALUE) | ((in[67 + inPos] & 0xFF) << 14 & Integer.MAX_VALUE) | ((in[68 + inPos] & 0xFF) << 6 & Integer.MAX_VALUE) | ((in[69 + inPos] & 0xFF) >>> 2 & Integer.MAX_VALUE));
            out[18 + outPos] = (((in[69 + inPos] & 0xFF) << 29 & Integer.MAX_VALUE) | ((in[70 + inPos] & 0xFF) << 21 & Integer.MAX_VALUE) | ((in[71 + inPos] & 0xFF) << 13 & Integer.MAX_VALUE) | ((in[72 + inPos] & 0xFF) << 5 & Integer.MAX_VALUE) | ((in[73 + inPos] & 0xFF) >>> 3 & Integer.MAX_VALUE));
            out[19 + outPos] = (((in[73 + inPos] & 0xFF) << 28 & Integer.MAX_VALUE) | ((in[74 + inPos] & 0xFF) << 20 & Integer.MAX_VALUE) | ((in[75 + inPos] & 0xFF) << 12 & Integer.MAX_VALUE) | ((in[76 + inPos] & 0xFF) << 4 & Integer.MAX_VALUE) | ((in[77 + inPos] & 0xFF) >>> 4 & Integer.MAX_VALUE));
            out[20 + outPos] = (((in[77 + inPos] & 0xFF) << 27 & Integer.MAX_VALUE) | ((in[78 + inPos] & 0xFF) << 19 & Integer.MAX_VALUE) | ((in[79 + inPos] & 0xFF) << 11 & Integer.MAX_VALUE) | ((in[80 + inPos] & 0xFF) << 3 & Integer.MAX_VALUE) | ((in[81 + inPos] & 0xFF) >>> 5 & Integer.MAX_VALUE));
            out[21 + outPos] = (((in[81 + inPos] & 0xFF) << 26 & Integer.MAX_VALUE) | ((in[82 + inPos] & 0xFF) << 18 & Integer.MAX_VALUE) | ((in[83 + inPos] & 0xFF) << 10 & Integer.MAX_VALUE) | ((in[84 + inPos] & 0xFF) << 2 & Integer.MAX_VALUE) | ((in[85 + inPos] & 0xFF) >>> 6 & Integer.MAX_VALUE));
            out[22 + outPos] = (((in[85 + inPos] & 0xFF) << 25 & Integer.MAX_VALUE) | ((in[86 + inPos] & 0xFF) << 17 & Integer.MAX_VALUE) | ((in[87 + inPos] & 0xFF) << 9 & Integer.MAX_VALUE) | ((in[88 + inPos] & 0xFF) << 1 & Integer.MAX_VALUE) | ((in[89 + inPos] & 0xFF) >>> 7 & Integer.MAX_VALUE));
            out[23 + outPos] = (((in[89 + inPos] & 0xFF) << 24 & Integer.MAX_VALUE) | ((in[90 + inPos] & 0xFF) << 16 & Integer.MAX_VALUE) | ((in[91 + inPos] & 0xFF) << 8 & Integer.MAX_VALUE) | (in[92 + inPos] & 0xFF & Integer.MAX_VALUE));
            out[24 + outPos] = (((in[93 + inPos] & 0xFF) << 23 & Integer.MAX_VALUE) | ((in[94 + inPos] & 0xFF) << 15 & Integer.MAX_VALUE) | ((in[95 + inPos] & 0xFF) << 7 & Integer.MAX_VALUE) | ((in[96 + inPos] & 0xFF) >>> 1 & Integer.MAX_VALUE));
            out[25 + outPos] = (((in[96 + inPos] & 0xFF) << 30 & Integer.MAX_VALUE) | ((in[97 + inPos] & 0xFF) << 22 & Integer.MAX_VALUE) | ((in[98 + inPos] & 0xFF) << 14 & Integer.MAX_VALUE) | ((in[99 + inPos] & 0xFF) << 6 & Integer.MAX_VALUE) | ((in[100 + inPos] & 0xFF) >>> 2 & Integer.MAX_VALUE));
            out[26 + outPos] = (((in[100 + inPos] & 0xFF) << 29 & Integer.MAX_VALUE) | ((in[101 + inPos] & 0xFF) << 21 & Integer.MAX_VALUE) | ((in[102 + inPos] & 0xFF) << 13 & Integer.MAX_VALUE) | ((in[103 + inPos] & 0xFF) << 5 & Integer.MAX_VALUE) | ((in[104 + inPos] & 0xFF) >>> 3 & Integer.MAX_VALUE));
            out[27 + outPos] = (((in[104 + inPos] & 0xFF) << 28 & Integer.MAX_VALUE) | ((in[105 + inPos] & 0xFF) << 20 & Integer.MAX_VALUE) | ((in[106 + inPos] & 0xFF) << 12 & Integer.MAX_VALUE) | ((in[107 + inPos] & 0xFF) << 4 & Integer.MAX_VALUE) | ((in[108 + inPos] & 0xFF) >>> 4 & Integer.MAX_VALUE));
            out[28 + outPos] = (((in[108 + inPos] & 0xFF) << 27 & Integer.MAX_VALUE) | ((in[109 + inPos] & 0xFF) << 19 & Integer.MAX_VALUE) | ((in[110 + inPos] & 0xFF) << 11 & Integer.MAX_VALUE) | ((in[111 + inPos] & 0xFF) << 3 & Integer.MAX_VALUE) | ((in[112 + inPos] & 0xFF) >>> 5 & Integer.MAX_VALUE));
            out[29 + outPos] = (((in[112 + inPos] & 0xFF) << 26 & Integer.MAX_VALUE) | ((in[113 + inPos] & 0xFF) << 18 & Integer.MAX_VALUE) | ((in[114 + inPos] & 0xFF) << 10 & Integer.MAX_VALUE) | ((in[115 + inPos] & 0xFF) << 2 & Integer.MAX_VALUE) | ((in[116 + inPos] & 0xFF) >>> 6 & Integer.MAX_VALUE));
            out[30 + outPos] = (((in[116 + inPos] & 0xFF) << 25 & Integer.MAX_VALUE) | ((in[117 + inPos] & 0xFF) << 17 & Integer.MAX_VALUE) | ((in[118 + inPos] & 0xFF) << 9 & Integer.MAX_VALUE) | ((in[119 + inPos] & 0xFF) << 1 & Integer.MAX_VALUE) | ((in[120 + inPos] & 0xFF) >>> 7 & Integer.MAX_VALUE));
            out[31 + outPos] = (((in[120 + inPos] & 0xFF) << 24 & Integer.MAX_VALUE) | ((in[121 + inPos] & 0xFF) << 16 & Integer.MAX_VALUE) | ((in[122 + inPos] & 0xFF) << 8 & Integer.MAX_VALUE) | (in[123 + inPos] & 0xFF & Integer.MAX_VALUE));
        }
    }
    
    private static final class Packer32 extends BytePacker
    {
        private Packer32() {
            super(32);
        }
        
        @Override
        public final void pack8Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & -1) >>> 24 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & -1) >>> 16 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & -1) >>> 8 & 0xFF);
            out[3 + outPos] = (byte)(in[0 + inPos] & -1 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & -1) >>> 24 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & -1) >>> 16 & 0xFF);
            out[6 + outPos] = (byte)((in[1 + inPos] & -1) >>> 8 & 0xFF);
            out[7 + outPos] = (byte)(in[1 + inPos] & -1 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & -1) >>> 24 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & -1) >>> 16 & 0xFF);
            out[10 + outPos] = (byte)((in[2 + inPos] & -1) >>> 8 & 0xFF);
            out[11 + outPos] = (byte)(in[2 + inPos] & -1 & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & -1) >>> 24 & 0xFF);
            out[13 + outPos] = (byte)((in[3 + inPos] & -1) >>> 16 & 0xFF);
            out[14 + outPos] = (byte)((in[3 + inPos] & -1) >>> 8 & 0xFF);
            out[15 + outPos] = (byte)(in[3 + inPos] & -1 & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & -1) >>> 24 & 0xFF);
            out[17 + outPos] = (byte)((in[4 + inPos] & -1) >>> 16 & 0xFF);
            out[18 + outPos] = (byte)((in[4 + inPos] & -1) >>> 8 & 0xFF);
            out[19 + outPos] = (byte)(in[4 + inPos] & -1 & 0xFF);
            out[20 + outPos] = (byte)((in[5 + inPos] & -1) >>> 24 & 0xFF);
            out[21 + outPos] = (byte)((in[5 + inPos] & -1) >>> 16 & 0xFF);
            out[22 + outPos] = (byte)((in[5 + inPos] & -1) >>> 8 & 0xFF);
            out[23 + outPos] = (byte)(in[5 + inPos] & -1 & 0xFF);
            out[24 + outPos] = (byte)((in[6 + inPos] & -1) >>> 24 & 0xFF);
            out[25 + outPos] = (byte)((in[6 + inPos] & -1) >>> 16 & 0xFF);
            out[26 + outPos] = (byte)((in[6 + inPos] & -1) >>> 8 & 0xFF);
            out[27 + outPos] = (byte)(in[6 + inPos] & -1 & 0xFF);
            out[28 + outPos] = (byte)((in[7 + inPos] & -1) >>> 24 & 0xFF);
            out[29 + outPos] = (byte)((in[7 + inPos] & -1) >>> 16 & 0xFF);
            out[30 + outPos] = (byte)((in[7 + inPos] & -1) >>> 8 & 0xFF);
            out[31 + outPos] = (byte)(in[7 + inPos] & -1 & 0xFF);
        }
        
        @Override
        public final void pack32Values(final int[] in, final int inPos, final byte[] out, final int outPos) {
            out[0 + outPos] = (byte)((in[0 + inPos] & -1) >>> 24 & 0xFF);
            out[1 + outPos] = (byte)((in[0 + inPos] & -1) >>> 16 & 0xFF);
            out[2 + outPos] = (byte)((in[0 + inPos] & -1) >>> 8 & 0xFF);
            out[3 + outPos] = (byte)(in[0 + inPos] & -1 & 0xFF);
            out[4 + outPos] = (byte)((in[1 + inPos] & -1) >>> 24 & 0xFF);
            out[5 + outPos] = (byte)((in[1 + inPos] & -1) >>> 16 & 0xFF);
            out[6 + outPos] = (byte)((in[1 + inPos] & -1) >>> 8 & 0xFF);
            out[7 + outPos] = (byte)(in[1 + inPos] & -1 & 0xFF);
            out[8 + outPos] = (byte)((in[2 + inPos] & -1) >>> 24 & 0xFF);
            out[9 + outPos] = (byte)((in[2 + inPos] & -1) >>> 16 & 0xFF);
            out[10 + outPos] = (byte)((in[2 + inPos] & -1) >>> 8 & 0xFF);
            out[11 + outPos] = (byte)(in[2 + inPos] & -1 & 0xFF);
            out[12 + outPos] = (byte)((in[3 + inPos] & -1) >>> 24 & 0xFF);
            out[13 + outPos] = (byte)((in[3 + inPos] & -1) >>> 16 & 0xFF);
            out[14 + outPos] = (byte)((in[3 + inPos] & -1) >>> 8 & 0xFF);
            out[15 + outPos] = (byte)(in[3 + inPos] & -1 & 0xFF);
            out[16 + outPos] = (byte)((in[4 + inPos] & -1) >>> 24 & 0xFF);
            out[17 + outPos] = (byte)((in[4 + inPos] & -1) >>> 16 & 0xFF);
            out[18 + outPos] = (byte)((in[4 + inPos] & -1) >>> 8 & 0xFF);
            out[19 + outPos] = (byte)(in[4 + inPos] & -1 & 0xFF);
            out[20 + outPos] = (byte)((in[5 + inPos] & -1) >>> 24 & 0xFF);
            out[21 + outPos] = (byte)((in[5 + inPos] & -1) >>> 16 & 0xFF);
            out[22 + outPos] = (byte)((in[5 + inPos] & -1) >>> 8 & 0xFF);
            out[23 + outPos] = (byte)(in[5 + inPos] & -1 & 0xFF);
            out[24 + outPos] = (byte)((in[6 + inPos] & -1) >>> 24 & 0xFF);
            out[25 + outPos] = (byte)((in[6 + inPos] & -1) >>> 16 & 0xFF);
            out[26 + outPos] = (byte)((in[6 + inPos] & -1) >>> 8 & 0xFF);
            out[27 + outPos] = (byte)(in[6 + inPos] & -1 & 0xFF);
            out[28 + outPos] = (byte)((in[7 + inPos] & -1) >>> 24 & 0xFF);
            out[29 + outPos] = (byte)((in[7 + inPos] & -1) >>> 16 & 0xFF);
            out[30 + outPos] = (byte)((in[7 + inPos] & -1) >>> 8 & 0xFF);
            out[31 + outPos] = (byte)(in[7 + inPos] & -1 & 0xFF);
            out[32 + outPos] = (byte)((in[8 + inPos] & -1) >>> 24 & 0xFF);
            out[33 + outPos] = (byte)((in[8 + inPos] & -1) >>> 16 & 0xFF);
            out[34 + outPos] = (byte)((in[8 + inPos] & -1) >>> 8 & 0xFF);
            out[35 + outPos] = (byte)(in[8 + inPos] & -1 & 0xFF);
            out[36 + outPos] = (byte)((in[9 + inPos] & -1) >>> 24 & 0xFF);
            out[37 + outPos] = (byte)((in[9 + inPos] & -1) >>> 16 & 0xFF);
            out[38 + outPos] = (byte)((in[9 + inPos] & -1) >>> 8 & 0xFF);
            out[39 + outPos] = (byte)(in[9 + inPos] & -1 & 0xFF);
            out[40 + outPos] = (byte)((in[10 + inPos] & -1) >>> 24 & 0xFF);
            out[41 + outPos] = (byte)((in[10 + inPos] & -1) >>> 16 & 0xFF);
            out[42 + outPos] = (byte)((in[10 + inPos] & -1) >>> 8 & 0xFF);
            out[43 + outPos] = (byte)(in[10 + inPos] & -1 & 0xFF);
            out[44 + outPos] = (byte)((in[11 + inPos] & -1) >>> 24 & 0xFF);
            out[45 + outPos] = (byte)((in[11 + inPos] & -1) >>> 16 & 0xFF);
            out[46 + outPos] = (byte)((in[11 + inPos] & -1) >>> 8 & 0xFF);
            out[47 + outPos] = (byte)(in[11 + inPos] & -1 & 0xFF);
            out[48 + outPos] = (byte)((in[12 + inPos] & -1) >>> 24 & 0xFF);
            out[49 + outPos] = (byte)((in[12 + inPos] & -1) >>> 16 & 0xFF);
            out[50 + outPos] = (byte)((in[12 + inPos] & -1) >>> 8 & 0xFF);
            out[51 + outPos] = (byte)(in[12 + inPos] & -1 & 0xFF);
            out[52 + outPos] = (byte)((in[13 + inPos] & -1) >>> 24 & 0xFF);
            out[53 + outPos] = (byte)((in[13 + inPos] & -1) >>> 16 & 0xFF);
            out[54 + outPos] = (byte)((in[13 + inPos] & -1) >>> 8 & 0xFF);
            out[55 + outPos] = (byte)(in[13 + inPos] & -1 & 0xFF);
            out[56 + outPos] = (byte)((in[14 + inPos] & -1) >>> 24 & 0xFF);
            out[57 + outPos] = (byte)((in[14 + inPos] & -1) >>> 16 & 0xFF);
            out[58 + outPos] = (byte)((in[14 + inPos] & -1) >>> 8 & 0xFF);
            out[59 + outPos] = (byte)(in[14 + inPos] & -1 & 0xFF);
            out[60 + outPos] = (byte)((in[15 + inPos] & -1) >>> 24 & 0xFF);
            out[61 + outPos] = (byte)((in[15 + inPos] & -1) >>> 16 & 0xFF);
            out[62 + outPos] = (byte)((in[15 + inPos] & -1) >>> 8 & 0xFF);
            out[63 + outPos] = (byte)(in[15 + inPos] & -1 & 0xFF);
            out[64 + outPos] = (byte)((in[16 + inPos] & -1) >>> 24 & 0xFF);
            out[65 + outPos] = (byte)((in[16 + inPos] & -1) >>> 16 & 0xFF);
            out[66 + outPos] = (byte)((in[16 + inPos] & -1) >>> 8 & 0xFF);
            out[67 + outPos] = (byte)(in[16 + inPos] & -1 & 0xFF);
            out[68 + outPos] = (byte)((in[17 + inPos] & -1) >>> 24 & 0xFF);
            out[69 + outPos] = (byte)((in[17 + inPos] & -1) >>> 16 & 0xFF);
            out[70 + outPos] = (byte)((in[17 + inPos] & -1) >>> 8 & 0xFF);
            out[71 + outPos] = (byte)(in[17 + inPos] & -1 & 0xFF);
            out[72 + outPos] = (byte)((in[18 + inPos] & -1) >>> 24 & 0xFF);
            out[73 + outPos] = (byte)((in[18 + inPos] & -1) >>> 16 & 0xFF);
            out[74 + outPos] = (byte)((in[18 + inPos] & -1) >>> 8 & 0xFF);
            out[75 + outPos] = (byte)(in[18 + inPos] & -1 & 0xFF);
            out[76 + outPos] = (byte)((in[19 + inPos] & -1) >>> 24 & 0xFF);
            out[77 + outPos] = (byte)((in[19 + inPos] & -1) >>> 16 & 0xFF);
            out[78 + outPos] = (byte)((in[19 + inPos] & -1) >>> 8 & 0xFF);
            out[79 + outPos] = (byte)(in[19 + inPos] & -1 & 0xFF);
            out[80 + outPos] = (byte)((in[20 + inPos] & -1) >>> 24 & 0xFF);
            out[81 + outPos] = (byte)((in[20 + inPos] & -1) >>> 16 & 0xFF);
            out[82 + outPos] = (byte)((in[20 + inPos] & -1) >>> 8 & 0xFF);
            out[83 + outPos] = (byte)(in[20 + inPos] & -1 & 0xFF);
            out[84 + outPos] = (byte)((in[21 + inPos] & -1) >>> 24 & 0xFF);
            out[85 + outPos] = (byte)((in[21 + inPos] & -1) >>> 16 & 0xFF);
            out[86 + outPos] = (byte)((in[21 + inPos] & -1) >>> 8 & 0xFF);
            out[87 + outPos] = (byte)(in[21 + inPos] & -1 & 0xFF);
            out[88 + outPos] = (byte)((in[22 + inPos] & -1) >>> 24 & 0xFF);
            out[89 + outPos] = (byte)((in[22 + inPos] & -1) >>> 16 & 0xFF);
            out[90 + outPos] = (byte)((in[22 + inPos] & -1) >>> 8 & 0xFF);
            out[91 + outPos] = (byte)(in[22 + inPos] & -1 & 0xFF);
            out[92 + outPos] = (byte)((in[23 + inPos] & -1) >>> 24 & 0xFF);
            out[93 + outPos] = (byte)((in[23 + inPos] & -1) >>> 16 & 0xFF);
            out[94 + outPos] = (byte)((in[23 + inPos] & -1) >>> 8 & 0xFF);
            out[95 + outPos] = (byte)(in[23 + inPos] & -1 & 0xFF);
            out[96 + outPos] = (byte)((in[24 + inPos] & -1) >>> 24 & 0xFF);
            out[97 + outPos] = (byte)((in[24 + inPos] & -1) >>> 16 & 0xFF);
            out[98 + outPos] = (byte)((in[24 + inPos] & -1) >>> 8 & 0xFF);
            out[99 + outPos] = (byte)(in[24 + inPos] & -1 & 0xFF);
            out[100 + outPos] = (byte)((in[25 + inPos] & -1) >>> 24 & 0xFF);
            out[101 + outPos] = (byte)((in[25 + inPos] & -1) >>> 16 & 0xFF);
            out[102 + outPos] = (byte)((in[25 + inPos] & -1) >>> 8 & 0xFF);
            out[103 + outPos] = (byte)(in[25 + inPos] & -1 & 0xFF);
            out[104 + outPos] = (byte)((in[26 + inPos] & -1) >>> 24 & 0xFF);
            out[105 + outPos] = (byte)((in[26 + inPos] & -1) >>> 16 & 0xFF);
            out[106 + outPos] = (byte)((in[26 + inPos] & -1) >>> 8 & 0xFF);
            out[107 + outPos] = (byte)(in[26 + inPos] & -1 & 0xFF);
            out[108 + outPos] = (byte)((in[27 + inPos] & -1) >>> 24 & 0xFF);
            out[109 + outPos] = (byte)((in[27 + inPos] & -1) >>> 16 & 0xFF);
            out[110 + outPos] = (byte)((in[27 + inPos] & -1) >>> 8 & 0xFF);
            out[111 + outPos] = (byte)(in[27 + inPos] & -1 & 0xFF);
            out[112 + outPos] = (byte)((in[28 + inPos] & -1) >>> 24 & 0xFF);
            out[113 + outPos] = (byte)((in[28 + inPos] & -1) >>> 16 & 0xFF);
            out[114 + outPos] = (byte)((in[28 + inPos] & -1) >>> 8 & 0xFF);
            out[115 + outPos] = (byte)(in[28 + inPos] & -1 & 0xFF);
            out[116 + outPos] = (byte)((in[29 + inPos] & -1) >>> 24 & 0xFF);
            out[117 + outPos] = (byte)((in[29 + inPos] & -1) >>> 16 & 0xFF);
            out[118 + outPos] = (byte)((in[29 + inPos] & -1) >>> 8 & 0xFF);
            out[119 + outPos] = (byte)(in[29 + inPos] & -1 & 0xFF);
            out[120 + outPos] = (byte)((in[30 + inPos] & -1) >>> 24 & 0xFF);
            out[121 + outPos] = (byte)((in[30 + inPos] & -1) >>> 16 & 0xFF);
            out[122 + outPos] = (byte)((in[30 + inPos] & -1) >>> 8 & 0xFF);
            out[123 + outPos] = (byte)(in[30 + inPos] & -1 & 0xFF);
            out[124 + outPos] = (byte)((in[31 + inPos] & -1) >>> 24 & 0xFF);
            out[125 + outPos] = (byte)((in[31 + inPos] & -1) >>> 16 & 0xFF);
            out[126 + outPos] = (byte)((in[31 + inPos] & -1) >>> 8 & 0xFF);
            out[127 + outPos] = (byte)(in[31 + inPos] & -1 & 0xFF);
        }
        
        @Override
        public final void unpack8Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 24 & -1) | ((in[1 + inPos] & 0xFF) << 16 & -1) | ((in[2 + inPos] & 0xFF) << 8 & -1) | (in[3 + inPos] & 0xFF & -1));
            out[1 + outPos] = (((in[4 + inPos] & 0xFF) << 24 & -1) | ((in[5 + inPos] & 0xFF) << 16 & -1) | ((in[6 + inPos] & 0xFF) << 8 & -1) | (in[7 + inPos] & 0xFF & -1));
            out[2 + outPos] = (((in[8 + inPos] & 0xFF) << 24 & -1) | ((in[9 + inPos] & 0xFF) << 16 & -1) | ((in[10 + inPos] & 0xFF) << 8 & -1) | (in[11 + inPos] & 0xFF & -1));
            out[3 + outPos] = (((in[12 + inPos] & 0xFF) << 24 & -1) | ((in[13 + inPos] & 0xFF) << 16 & -1) | ((in[14 + inPos] & 0xFF) << 8 & -1) | (in[15 + inPos] & 0xFF & -1));
            out[4 + outPos] = (((in[16 + inPos] & 0xFF) << 24 & -1) | ((in[17 + inPos] & 0xFF) << 16 & -1) | ((in[18 + inPos] & 0xFF) << 8 & -1) | (in[19 + inPos] & 0xFF & -1));
            out[5 + outPos] = (((in[20 + inPos] & 0xFF) << 24 & -1) | ((in[21 + inPos] & 0xFF) << 16 & -1) | ((in[22 + inPos] & 0xFF) << 8 & -1) | (in[23 + inPos] & 0xFF & -1));
            out[6 + outPos] = (((in[24 + inPos] & 0xFF) << 24 & -1) | ((in[25 + inPos] & 0xFF) << 16 & -1) | ((in[26 + inPos] & 0xFF) << 8 & -1) | (in[27 + inPos] & 0xFF & -1));
            out[7 + outPos] = (((in[28 + inPos] & 0xFF) << 24 & -1) | ((in[29 + inPos] & 0xFF) << 16 & -1) | ((in[30 + inPos] & 0xFF) << 8 & -1) | (in[31 + inPos] & 0xFF & -1));
        }
        
        @Override
        public final void unpack32Values(final byte[] in, final int inPos, final int[] out, final int outPos) {
            out[0 + outPos] = (((in[0 + inPos] & 0xFF) << 24 & -1) | ((in[1 + inPos] & 0xFF) << 16 & -1) | ((in[2 + inPos] & 0xFF) << 8 & -1) | (in[3 + inPos] & 0xFF & -1));
            out[1 + outPos] = (((in[4 + inPos] & 0xFF) << 24 & -1) | ((in[5 + inPos] & 0xFF) << 16 & -1) | ((in[6 + inPos] & 0xFF) << 8 & -1) | (in[7 + inPos] & 0xFF & -1));
            out[2 + outPos] = (((in[8 + inPos] & 0xFF) << 24 & -1) | ((in[9 + inPos] & 0xFF) << 16 & -1) | ((in[10 + inPos] & 0xFF) << 8 & -1) | (in[11 + inPos] & 0xFF & -1));
            out[3 + outPos] = (((in[12 + inPos] & 0xFF) << 24 & -1) | ((in[13 + inPos] & 0xFF) << 16 & -1) | ((in[14 + inPos] & 0xFF) << 8 & -1) | (in[15 + inPos] & 0xFF & -1));
            out[4 + outPos] = (((in[16 + inPos] & 0xFF) << 24 & -1) | ((in[17 + inPos] & 0xFF) << 16 & -1) | ((in[18 + inPos] & 0xFF) << 8 & -1) | (in[19 + inPos] & 0xFF & -1));
            out[5 + outPos] = (((in[20 + inPos] & 0xFF) << 24 & -1) | ((in[21 + inPos] & 0xFF) << 16 & -1) | ((in[22 + inPos] & 0xFF) << 8 & -1) | (in[23 + inPos] & 0xFF & -1));
            out[6 + outPos] = (((in[24 + inPos] & 0xFF) << 24 & -1) | ((in[25 + inPos] & 0xFF) << 16 & -1) | ((in[26 + inPos] & 0xFF) << 8 & -1) | (in[27 + inPos] & 0xFF & -1));
            out[7 + outPos] = (((in[28 + inPos] & 0xFF) << 24 & -1) | ((in[29 + inPos] & 0xFF) << 16 & -1) | ((in[30 + inPos] & 0xFF) << 8 & -1) | (in[31 + inPos] & 0xFF & -1));
            out[8 + outPos] = (((in[32 + inPos] & 0xFF) << 24 & -1) | ((in[33 + inPos] & 0xFF) << 16 & -1) | ((in[34 + inPos] & 0xFF) << 8 & -1) | (in[35 + inPos] & 0xFF & -1));
            out[9 + outPos] = (((in[36 + inPos] & 0xFF) << 24 & -1) | ((in[37 + inPos] & 0xFF) << 16 & -1) | ((in[38 + inPos] & 0xFF) << 8 & -1) | (in[39 + inPos] & 0xFF & -1));
            out[10 + outPos] = (((in[40 + inPos] & 0xFF) << 24 & -1) | ((in[41 + inPos] & 0xFF) << 16 & -1) | ((in[42 + inPos] & 0xFF) << 8 & -1) | (in[43 + inPos] & 0xFF & -1));
            out[11 + outPos] = (((in[44 + inPos] & 0xFF) << 24 & -1) | ((in[45 + inPos] & 0xFF) << 16 & -1) | ((in[46 + inPos] & 0xFF) << 8 & -1) | (in[47 + inPos] & 0xFF & -1));
            out[12 + outPos] = (((in[48 + inPos] & 0xFF) << 24 & -1) | ((in[49 + inPos] & 0xFF) << 16 & -1) | ((in[50 + inPos] & 0xFF) << 8 & -1) | (in[51 + inPos] & 0xFF & -1));
            out[13 + outPos] = (((in[52 + inPos] & 0xFF) << 24 & -1) | ((in[53 + inPos] & 0xFF) << 16 & -1) | ((in[54 + inPos] & 0xFF) << 8 & -1) | (in[55 + inPos] & 0xFF & -1));
            out[14 + outPos] = (((in[56 + inPos] & 0xFF) << 24 & -1) | ((in[57 + inPos] & 0xFF) << 16 & -1) | ((in[58 + inPos] & 0xFF) << 8 & -1) | (in[59 + inPos] & 0xFF & -1));
            out[15 + outPos] = (((in[60 + inPos] & 0xFF) << 24 & -1) | ((in[61 + inPos] & 0xFF) << 16 & -1) | ((in[62 + inPos] & 0xFF) << 8 & -1) | (in[63 + inPos] & 0xFF & -1));
            out[16 + outPos] = (((in[64 + inPos] & 0xFF) << 24 & -1) | ((in[65 + inPos] & 0xFF) << 16 & -1) | ((in[66 + inPos] & 0xFF) << 8 & -1) | (in[67 + inPos] & 0xFF & -1));
            out[17 + outPos] = (((in[68 + inPos] & 0xFF) << 24 & -1) | ((in[69 + inPos] & 0xFF) << 16 & -1) | ((in[70 + inPos] & 0xFF) << 8 & -1) | (in[71 + inPos] & 0xFF & -1));
            out[18 + outPos] = (((in[72 + inPos] & 0xFF) << 24 & -1) | ((in[73 + inPos] & 0xFF) << 16 & -1) | ((in[74 + inPos] & 0xFF) << 8 & -1) | (in[75 + inPos] & 0xFF & -1));
            out[19 + outPos] = (((in[76 + inPos] & 0xFF) << 24 & -1) | ((in[77 + inPos] & 0xFF) << 16 & -1) | ((in[78 + inPos] & 0xFF) << 8 & -1) | (in[79 + inPos] & 0xFF & -1));
            out[20 + outPos] = (((in[80 + inPos] & 0xFF) << 24 & -1) | ((in[81 + inPos] & 0xFF) << 16 & -1) | ((in[82 + inPos] & 0xFF) << 8 & -1) | (in[83 + inPos] & 0xFF & -1));
            out[21 + outPos] = (((in[84 + inPos] & 0xFF) << 24 & -1) | ((in[85 + inPos] & 0xFF) << 16 & -1) | ((in[86 + inPos] & 0xFF) << 8 & -1) | (in[87 + inPos] & 0xFF & -1));
            out[22 + outPos] = (((in[88 + inPos] & 0xFF) << 24 & -1) | ((in[89 + inPos] & 0xFF) << 16 & -1) | ((in[90 + inPos] & 0xFF) << 8 & -1) | (in[91 + inPos] & 0xFF & -1));
            out[23 + outPos] = (((in[92 + inPos] & 0xFF) << 24 & -1) | ((in[93 + inPos] & 0xFF) << 16 & -1) | ((in[94 + inPos] & 0xFF) << 8 & -1) | (in[95 + inPos] & 0xFF & -1));
            out[24 + outPos] = (((in[96 + inPos] & 0xFF) << 24 & -1) | ((in[97 + inPos] & 0xFF) << 16 & -1) | ((in[98 + inPos] & 0xFF) << 8 & -1) | (in[99 + inPos] & 0xFF & -1));
            out[25 + outPos] = (((in[100 + inPos] & 0xFF) << 24 & -1) | ((in[101 + inPos] & 0xFF) << 16 & -1) | ((in[102 + inPos] & 0xFF) << 8 & -1) | (in[103 + inPos] & 0xFF & -1));
            out[26 + outPos] = (((in[104 + inPos] & 0xFF) << 24 & -1) | ((in[105 + inPos] & 0xFF) << 16 & -1) | ((in[106 + inPos] & 0xFF) << 8 & -1) | (in[107 + inPos] & 0xFF & -1));
            out[27 + outPos] = (((in[108 + inPos] & 0xFF) << 24 & -1) | ((in[109 + inPos] & 0xFF) << 16 & -1) | ((in[110 + inPos] & 0xFF) << 8 & -1) | (in[111 + inPos] & 0xFF & -1));
            out[28 + outPos] = (((in[112 + inPos] & 0xFF) << 24 & -1) | ((in[113 + inPos] & 0xFF) << 16 & -1) | ((in[114 + inPos] & 0xFF) << 8 & -1) | (in[115 + inPos] & 0xFF & -1));
            out[29 + outPos] = (((in[116 + inPos] & 0xFF) << 24 & -1) | ((in[117 + inPos] & 0xFF) << 16 & -1) | ((in[118 + inPos] & 0xFF) << 8 & -1) | (in[119 + inPos] & 0xFF & -1));
            out[30 + outPos] = (((in[120 + inPos] & 0xFF) << 24 & -1) | ((in[121 + inPos] & 0xFF) << 16 & -1) | ((in[122 + inPos] & 0xFF) << 8 & -1) | (in[123 + inPos] & 0xFF & -1));
            out[31 + outPos] = (((in[124 + inPos] & 0xFF) << 24 & -1) | ((in[125 + inPos] & 0xFF) << 16 & -1) | ((in[126 + inPos] & 0xFF) << 8 & -1) | (in[127 + inPos] & 0xFF & -1));
        }
    }
}
