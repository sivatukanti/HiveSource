// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.check;

public class CRC64 extends Check
{
    private static final long poly = -3932672073523589310L;
    private static final long[] crcTable;
    private long crc;
    
    public CRC64() {
        this.crc = -1L;
        this.size = 8;
        this.name = "CRC64";
    }
    
    public void update(final byte[] array, int i, final int n) {
        while (i < i + n) {
            this.crc = (CRC64.crcTable[(array[i++] ^ (int)this.crc) & 0xFF] ^ this.crc >>> 8);
        }
    }
    
    public byte[] finish() {
        final long n = ~this.crc;
        this.crc = -1L;
        final byte[] array = new byte[8];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (byte)(n >> i * 8);
        }
        return array;
    }
    
    static {
        crcTable = new long[256];
        for (int i = 0; i < CRC64.crcTable.length; ++i) {
            long n = i;
            for (int j = 0; j < 8; ++j) {
                if ((n & 0x1L) == 0x1L) {
                    n = (n >>> 1 ^ 0xC96C5795D7870F42L);
                }
                else {
                    n >>>= 1;
                }
            }
            CRC64.crcTable[i] = n;
        }
    }
}
