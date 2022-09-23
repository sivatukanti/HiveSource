// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.bitpacking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitPacking
{
    private BitPacking() {
    }
    
    public static BitPackingWriter getBitPackingWriter(final int bitLength, final OutputStream out) {
        switch (bitLength) {
            case 0: {
                return new ZeroBitPackingWriter();
            }
            case 1: {
                return new OneBitPackingWriter(out);
            }
            case 2: {
                return new TwoBitPackingWriter(out);
            }
            case 3: {
                return new ThreeBitPackingWriter(out);
            }
            case 4: {
                return new FourBitPackingWriter(out);
            }
            case 5: {
                return new FiveBitPackingWriter(out);
            }
            case 6: {
                return new SixBitPackingWriter(out);
            }
            case 7: {
                return new SevenBitPackingWriter(out);
            }
            case 8: {
                return new EightBitPackingWriter(out);
            }
            default: {
                throw new UnsupportedOperationException("only support up to 8 for now");
            }
        }
    }
    
    public static BitPackingReader createBitPackingReader(final int bitLength, final InputStream in, final long valueCount) {
        switch (bitLength) {
            case 0: {
                return new ZeroBitPackingReader();
            }
            case 1: {
                return new OneBitPackingReader(in);
            }
            case 2: {
                return new TwoBitPackingReader(in);
            }
            case 3: {
                return new ThreeBitPackingReader(in, valueCount);
            }
            case 4: {
                return new FourBitPackingReader(in);
            }
            case 5: {
                return new FiveBitPackingReader(in, valueCount);
            }
            case 6: {
                return new SixBitPackingReader(in, valueCount);
            }
            case 7: {
                return new SevenBitPackingReader(in, valueCount);
            }
            case 8: {
                return new EightBitPackingReader(in);
            }
            default: {
                throw new UnsupportedOperationException("only support up to 8 for now");
            }
        }
    }
    
    public abstract static class BitPackingWriter
    {
        public abstract void write(final int p0) throws IOException;
        
        public abstract void finish() throws IOException;
    }
    
    public abstract static class BitPackingReader
    {
        public abstract int read() throws IOException;
    }
}
