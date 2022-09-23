// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal.jzlib;

final class InfBlocks
{
    private static final int[] inflate_mask;
    private static final int[] border;
    private static final int TYPE = 0;
    private static final int LENS = 1;
    private static final int STORED = 2;
    private static final int TABLE = 3;
    private static final int BTREE = 4;
    private static final int DTREE = 5;
    private static final int CODES = 6;
    private static final int DRY = 7;
    private static final int DONE = 8;
    private static final int BAD = 9;
    private int mode;
    private int left;
    private int table;
    private int index;
    private int[] blens;
    private final int[] bb;
    private final int[] tb;
    private final InfCodes codes;
    private int last;
    int bitk;
    int bitb;
    private int[] hufts;
    byte[] window;
    final int end;
    int read;
    int write;
    private final Object checkfn;
    private long check;
    private final InfTree inftree;
    
    InfBlocks(final ZStream z, final Object checkfn, final int w) {
        this.bb = new int[1];
        this.tb = new int[1];
        this.codes = new InfCodes();
        this.inftree = new InfTree();
        this.hufts = new int[4320];
        this.window = new byte[w];
        this.end = w;
        this.checkfn = checkfn;
        this.mode = 0;
        this.reset(z, null);
    }
    
    void reset(final ZStream z, final long[] c) {
        if (c != null) {
            c[0] = this.check;
        }
        this.mode = 0;
        this.bitk = 0;
        this.bitb = 0;
        final int n = 0;
        this.write = n;
        this.read = n;
        if (this.checkfn != null) {
            final long adler32 = Adler32.adler32(0L, null, 0, 0);
            this.check = adler32;
            z.adler = adler32;
        }
    }
    
    int proc(final ZStream z, int r) {
        int p = z.next_in_index;
        int n = z.avail_in;
        int b = this.bitb;
        int k = this.bitk;
        int q = this.write;
        int m = (q < this.read) ? (this.read - q - 1) : (this.end - q);
        Label_2715: {
        Label_2625:
            while (true) {
                Label_2472: {
                    switch (this.mode) {
                        case 0: {
                            while (k < 3) {
                                if (n == 0) {
                                    this.bitb = b;
                                    this.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    this.write = q;
                                    return this.inflate_flush(z, r);
                                }
                                r = 0;
                                --n;
                                b |= (z.next_in[p++] & 0xFF) << k;
                                k += 8;
                            }
                            int t = b & 0x7;
                            this.last = (t & 0x1);
                            switch (t >>> 1) {
                                case 0: {
                                    b >>>= 3;
                                    k -= 3;
                                    t = (k & 0x7);
                                    b >>>= t;
                                    k -= t;
                                    this.mode = 1;
                                    continue;
                                }
                                case 1: {
                                    final int[] bl = { 0 };
                                    final int[] bd = { 0 };
                                    final int[][] tl = { null };
                                    final int[][] td = { null };
                                    InfTree.inflate_trees_fixed(bl, bd, tl, td);
                                    this.codes.init(bl[0], bd[0], tl[0], 0, td[0], 0);
                                    b >>>= 3;
                                    k -= 3;
                                    this.mode = 6;
                                    continue;
                                }
                                case 2: {
                                    b >>>= 3;
                                    k -= 3;
                                    this.mode = 3;
                                    continue;
                                }
                                case 3: {
                                    b >>>= 3;
                                    k -= 3;
                                    this.mode = 9;
                                    z.msg = "invalid block type";
                                    r = -3;
                                    this.bitb = b;
                                    this.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    this.write = q;
                                    return this.inflate_flush(z, r);
                                }
                            }
                            continue;
                        }
                        case 1: {
                            while (k < 32) {
                                if (n == 0) {
                                    this.bitb = b;
                                    this.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    this.write = q;
                                    return this.inflate_flush(z, r);
                                }
                                r = 0;
                                --n;
                                b |= (z.next_in[p++] & 0xFF) << k;
                                k += 8;
                            }
                            if ((~b >>> 16 & 0xFFFF) != (b & 0xFFFF)) {
                                this.mode = 9;
                                z.msg = "invalid stored block lengths";
                                r = -3;
                                this.bitb = b;
                                this.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                this.write = q;
                                return this.inflate_flush(z, r);
                            }
                            this.left = (b & 0xFFFF);
                            k = (b = 0);
                            this.mode = ((this.left != 0) ? 2 : ((this.last != 0) ? 7 : 0));
                            continue;
                        }
                        case 2: {
                            if (n == 0) {
                                this.bitb = b;
                                this.bitk = k;
                                z.avail_in = 0;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                this.write = q;
                                return this.inflate_flush(z, r);
                            }
                            if (m == 0) {
                                if (q == this.end && this.read != 0) {
                                    q = 0;
                                    m = ((q < this.read) ? (this.read - q - 1) : (this.end - q));
                                }
                                if (m == 0) {
                                    this.write = q;
                                    r = this.inflate_flush(z, r);
                                    q = this.write;
                                    m = ((q < this.read) ? (this.read - q - 1) : (this.end - q));
                                    if (q == this.end && this.read != 0) {
                                        q = 0;
                                        m = ((q < this.read) ? (this.read - q - 1) : (this.end - q));
                                    }
                                    if (m == 0) {
                                        this.bitb = b;
                                        this.bitk = k;
                                        z.avail_in = n;
                                        z.total_in += p - z.next_in_index;
                                        z.next_in_index = p;
                                        this.write = q;
                                        return this.inflate_flush(z, r);
                                    }
                                }
                            }
                            r = 0;
                            int t = this.left;
                            if (t > n) {
                                t = n;
                            }
                            if (t > m) {
                                t = m;
                            }
                            System.arraycopy(z.next_in, p, this.window, q, t);
                            p += t;
                            n -= t;
                            q += t;
                            m -= t;
                            if ((this.left -= t) != 0) {
                                continue;
                            }
                            this.mode = ((this.last != 0) ? 7 : 0);
                            continue;
                        }
                        case 3: {
                            while (k < 14) {
                                if (n == 0) {
                                    this.bitb = b;
                                    this.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    this.write = q;
                                    return this.inflate_flush(z, r);
                                }
                                r = 0;
                                --n;
                                b |= (z.next_in[p++] & 0xFF) << k;
                                k += 8;
                            }
                            int t = this.table = (b & 0x3FFF);
                            if ((t & 0x1F) > 29 || (t >> 5 & 0x1F) > 29) {
                                this.mode = 9;
                                z.msg = "too many length or distance symbols";
                                r = -3;
                                this.bitb = b;
                                this.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                this.write = q;
                                return this.inflate_flush(z, r);
                            }
                            t = 258 + (t & 0x1F) + (t >> 5 & 0x1F);
                            if (this.blens == null || this.blens.length < t) {
                                this.blens = new int[t];
                            }
                            else {
                                for (int i = 0; i < t; ++i) {
                                    this.blens[i] = 0;
                                }
                            }
                            b >>>= 14;
                            k -= 14;
                            this.index = 0;
                            this.mode = 4;
                        }
                        case 4: {
                            while (this.index < 4 + (this.table >>> 10)) {
                                while (k < 3) {
                                    if (n == 0) {
                                        this.bitb = b;
                                        this.bitk = k;
                                        z.avail_in = n;
                                        z.total_in += p - z.next_in_index;
                                        z.next_in_index = p;
                                        this.write = q;
                                        return this.inflate_flush(z, r);
                                    }
                                    r = 0;
                                    --n;
                                    b |= (z.next_in[p++] & 0xFF) << k;
                                    k += 8;
                                }
                                this.blens[InfBlocks.border[this.index++]] = (b & 0x7);
                                b >>>= 3;
                                k -= 3;
                            }
                            while (this.index < 19) {
                                this.blens[InfBlocks.border[this.index++]] = 0;
                            }
                            this.bb[0] = 7;
                            final int t = this.inftree.inflate_trees_bits(this.blens, this.bb, this.tb, this.hufts, z);
                            if (t != 0) {
                                r = t;
                                if (r == -3) {
                                    this.blens = null;
                                    this.mode = 9;
                                }
                                this.bitb = b;
                                this.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                this.write = q;
                                return this.inflate_flush(z, r);
                            }
                            this.index = 0;
                            this.mode = 5;
                        }
                        case 5: {
                            while (true) {
                                int t = this.table;
                                if (this.index >= 258 + (t & 0x1F) + (t >> 5 & 0x1F)) {
                                    this.tb[0] = -1;
                                    final int[] bl = { 0 };
                                    final int[] bd = { 0 };
                                    final int[] tl2 = { 0 };
                                    final int[] td2 = { 0 };
                                    bl[0] = 9;
                                    bd[0] = 6;
                                    t = this.table;
                                    t = this.inftree.inflate_trees_dynamic(257 + (t & 0x1F), 1 + (t >> 5 & 0x1F), this.blens, bl, bd, tl2, td2, this.hufts, z);
                                    if (t != 0) {
                                        if (t == -3) {
                                            this.blens = null;
                                            this.mode = 9;
                                        }
                                        r = t;
                                        this.bitb = b;
                                        this.bitk = k;
                                        z.avail_in = n;
                                        z.total_in += p - z.next_in_index;
                                        z.next_in_index = p;
                                        this.write = q;
                                        return this.inflate_flush(z, r);
                                    }
                                    this.codes.init(bl[0], bd[0], this.hufts, tl2[0], this.hufts, td2[0]);
                                    this.mode = 6;
                                    break Label_2472;
                                }
                                else {
                                    for (t = this.bb[0]; k < t; k += 8) {
                                        if (n == 0) {
                                            this.bitb = b;
                                            this.bitk = k;
                                            z.avail_in = n;
                                            z.total_in += p - z.next_in_index;
                                            z.next_in_index = p;
                                            this.write = q;
                                            return this.inflate_flush(z, r);
                                        }
                                        r = 0;
                                        --n;
                                        b |= (z.next_in[p++] & 0xFF) << k;
                                    }
                                    if (this.tb[0] == -1) {}
                                    t = this.hufts[(this.tb[0] + (b & InfBlocks.inflate_mask[t])) * 3 + 1];
                                    int c = this.hufts[(this.tb[0] + (b & InfBlocks.inflate_mask[t])) * 3 + 2];
                                    if (c < 16) {
                                        b >>>= t;
                                        k -= t;
                                        this.blens[this.index++] = c;
                                    }
                                    else {
                                        int i = (c == 18) ? 7 : (c - 14);
                                        int j = (c == 18) ? 11 : 3;
                                        while (k < t + i) {
                                            if (n == 0) {
                                                this.bitb = b;
                                                this.bitk = k;
                                                z.avail_in = n;
                                                z.total_in += p - z.next_in_index;
                                                z.next_in_index = p;
                                                this.write = q;
                                                return this.inflate_flush(z, r);
                                            }
                                            r = 0;
                                            --n;
                                            b |= (z.next_in[p++] & 0xFF) << k;
                                            k += 8;
                                        }
                                        b >>>= t;
                                        k -= t;
                                        j += (b & InfBlocks.inflate_mask[i]);
                                        b >>>= i;
                                        k -= i;
                                        i = this.index;
                                        t = this.table;
                                        if (i + j > 258 + (t & 0x1F) + (t >> 5 & 0x1F) || (c == 16 && i < 1)) {
                                            this.blens = null;
                                            this.mode = 9;
                                            z.msg = "invalid bit length repeat";
                                            r = -3;
                                            this.bitb = b;
                                            this.bitk = k;
                                            z.avail_in = n;
                                            z.total_in += p - z.next_in_index;
                                            z.next_in_index = p;
                                            this.write = q;
                                            return this.inflate_flush(z, r);
                                        }
                                        c = ((c == 16) ? this.blens[i - 1] : 0);
                                        do {
                                            this.blens[i++] = c;
                                        } while (--j != 0);
                                        this.index = i;
                                    }
                                }
                            }
                            break;
                        }
                        case 6: {
                            this.bitb = b;
                            this.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            this.write = q;
                            if ((r = this.codes.proc(this, z, r)) != 1) {
                                return this.inflate_flush(z, r);
                            }
                            r = 0;
                            p = z.next_in_index;
                            n = z.avail_in;
                            b = this.bitb;
                            k = this.bitk;
                            q = this.write;
                            m = ((q < this.read) ? (this.read - q - 1) : (this.end - q));
                            if (this.last == 0) {
                                this.mode = 0;
                                continue;
                            }
                            this.mode = 7;
                            break Label_2625;
                        }
                        case 7: {
                            break Label_2625;
                        }
                        case 8: {
                            break Label_2715;
                        }
                        case 9: {
                            r = -3;
                            this.bitb = b;
                            this.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            this.write = q;
                            return this.inflate_flush(z, r);
                        }
                        default: {
                            r = -2;
                            this.bitb = b;
                            this.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            this.write = q;
                            return this.inflate_flush(z, r);
                        }
                    }
                }
            }
            this.write = q;
            r = this.inflate_flush(z, r);
            q = this.write;
            if (this.read != this.write) {
                this.bitb = b;
                this.bitk = k;
                z.avail_in = n;
                z.total_in += p - z.next_in_index;
                z.next_in_index = p;
                this.write = q;
                return this.inflate_flush(z, r);
            }
            this.mode = 8;
        }
        r = 1;
        this.bitb = b;
        this.bitk = k;
        z.avail_in = n;
        z.total_in += p - z.next_in_index;
        z.next_in_index = p;
        this.write = q;
        return this.inflate_flush(z, r);
    }
    
    void free(final ZStream z) {
        this.reset(z, null);
        this.window = null;
        this.hufts = null;
    }
    
    void set_dictionary(final byte[] d, final int start, final int n) {
        System.arraycopy(d, start, this.window, 0, n);
        this.write = n;
        this.read = n;
    }
    
    int sync_point() {
        return (this.mode == 1) ? 1 : 0;
    }
    
    int inflate_flush(final ZStream z, int r) {
        int p = z.next_out_index;
        int q = this.read;
        int n = ((q <= this.write) ? this.write : this.end) - q;
        if (n > z.avail_out) {
            n = z.avail_out;
        }
        if (n != 0 && r == -5) {
            r = 0;
        }
        z.avail_out -= n;
        z.total_out += n;
        if (this.checkfn != null) {
            final long adler32 = Adler32.adler32(this.check, this.window, q, n);
            this.check = adler32;
            z.adler = adler32;
        }
        System.arraycopy(this.window, q, z.next_out, p, n);
        p += n;
        q += n;
        if (q == this.end) {
            q = 0;
            if (this.write == this.end) {
                this.write = 0;
            }
            n = this.write - q;
            if (n > z.avail_out) {
                n = z.avail_out;
            }
            if (n != 0 && r == -5) {
                r = 0;
            }
            z.avail_out -= n;
            z.total_out += n;
            if (this.checkfn != null) {
                final long adler33 = Adler32.adler32(this.check, this.window, q, n);
                this.check = adler33;
                z.adler = adler33;
            }
            System.arraycopy(this.window, q, z.next_out, p, n);
            p += n;
            q += n;
        }
        z.next_out_index = p;
        this.read = q;
        return r;
    }
    
    static {
        inflate_mask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535 };
        border = new int[] { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };
    }
}
