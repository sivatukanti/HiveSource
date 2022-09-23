// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal.jzlib;

final class InfCodes
{
    private static final int[] inflate_mask;
    private static final int START = 0;
    private static final int LEN = 1;
    private static final int LENEXT = 2;
    private static final int DIST = 3;
    private static final int DISTEXT = 4;
    private static final int COPY = 5;
    private static final int LIT = 6;
    private static final int WASH = 7;
    private static final int END = 8;
    private static final int BADCODE = 9;
    private int mode;
    private int len;
    private int[] tree;
    private int tree_index;
    private int need;
    private int lit;
    private int get;
    private int dist;
    private byte lbits;
    private byte dbits;
    private int[] ltree;
    private int ltree_index;
    private int[] dtree;
    private int dtree_index;
    
    void init(final int bl, final int bd, final int[] tl, final int tl_index, final int[] td, final int td_index) {
        this.mode = 0;
        this.lbits = (byte)bl;
        this.dbits = (byte)bd;
        this.ltree = tl;
        this.ltree_index = tl_index;
        this.dtree = td;
        this.dtree_index = td_index;
        this.tree = null;
    }
    
    int proc(final InfBlocks s, final ZStream z, int r) {
        int p = z.next_in_index;
        int n = z.avail_in;
        int b = s.bitb;
        int k = s.bitk;
        int q = s.write;
        int m = (q < s.read) ? (s.read - q - 1) : (s.end - q);
        Label_2060: {
            Label_2003: {
            Label_1947:
                while (true) {
                    switch (this.mode) {
                        case 0: {
                            if (m >= 258 && n >= 10) {
                                s.bitb = b;
                                s.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                s.write = q;
                                r = inflate_fast(this.lbits, this.dbits, this.ltree, this.ltree_index, this.dtree, this.dtree_index, s, z);
                                p = z.next_in_index;
                                n = z.avail_in;
                                b = s.bitb;
                                k = s.bitk;
                                q = s.write;
                                m = ((q < s.read) ? (s.read - q - 1) : (s.end - q));
                                if (r != 0) {
                                    this.mode = ((r == 1) ? 7 : 9);
                                    continue;
                                }
                            }
                            this.need = this.lbits;
                            this.tree = this.ltree;
                            this.tree_index = this.ltree_index;
                            this.mode = 1;
                        }
                        case 1: {
                            int j;
                            for (j = this.need; k < j; k += 8) {
                                if (n == 0) {
                                    s.bitb = b;
                                    s.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    s.write = q;
                                    return s.inflate_flush(z, r);
                                }
                                r = 0;
                                --n;
                                b |= (z.next_in[p++] & 0xFF) << k;
                            }
                            final int tindex = (this.tree_index + (b & InfCodes.inflate_mask[j])) * 3;
                            b >>>= this.tree[tindex + 1];
                            k -= this.tree[tindex + 1];
                            final int e = this.tree[tindex];
                            if (e == 0) {
                                this.lit = this.tree[tindex + 2];
                                this.mode = 6;
                                continue;
                            }
                            if ((e & 0x10) != 0x0) {
                                this.get = (e & 0xF);
                                this.len = this.tree[tindex + 2];
                                this.mode = 2;
                                continue;
                            }
                            if ((e & 0x40) == 0x0) {
                                this.need = e;
                                this.tree_index = tindex / 3 + this.tree[tindex + 2];
                                continue;
                            }
                            if ((e & 0x20) != 0x0) {
                                this.mode = 7;
                                continue;
                            }
                            this.mode = 9;
                            z.msg = "invalid literal/length code";
                            r = -3;
                            s.bitb = b;
                            s.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            s.write = q;
                            return s.inflate_flush(z, r);
                        }
                        case 2: {
                            int j;
                            for (j = this.get; k < j; k += 8) {
                                if (n == 0) {
                                    s.bitb = b;
                                    s.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    s.write = q;
                                    return s.inflate_flush(z, r);
                                }
                                r = 0;
                                --n;
                                b |= (z.next_in[p++] & 0xFF) << k;
                            }
                            this.len += (b & InfCodes.inflate_mask[j]);
                            b >>= j;
                            k -= j;
                            this.need = this.dbits;
                            this.tree = this.dtree;
                            this.tree_index = this.dtree_index;
                            this.mode = 3;
                        }
                        case 3: {
                            int j;
                            for (j = this.need; k < j; k += 8) {
                                if (n == 0) {
                                    s.bitb = b;
                                    s.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    s.write = q;
                                    return s.inflate_flush(z, r);
                                }
                                r = 0;
                                --n;
                                b |= (z.next_in[p++] & 0xFF) << k;
                            }
                            final int tindex = (this.tree_index + (b & InfCodes.inflate_mask[j])) * 3;
                            b >>= this.tree[tindex + 1];
                            k -= this.tree[tindex + 1];
                            final int e = this.tree[tindex];
                            if ((e & 0x10) != 0x0) {
                                this.get = (e & 0xF);
                                this.dist = this.tree[tindex + 2];
                                this.mode = 4;
                                continue;
                            }
                            if ((e & 0x40) == 0x0) {
                                this.need = e;
                                this.tree_index = tindex / 3 + this.tree[tindex + 2];
                                continue;
                            }
                            this.mode = 9;
                            z.msg = "invalid distance code";
                            r = -3;
                            s.bitb = b;
                            s.bitk = k;
                            z.avail_in = n;
                            z.total_in += p - z.next_in_index;
                            z.next_in_index = p;
                            s.write = q;
                            return s.inflate_flush(z, r);
                        }
                        case 4: {
                            int j;
                            for (j = this.get; k < j; k += 8) {
                                if (n == 0) {
                                    s.bitb = b;
                                    s.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    s.write = q;
                                    return s.inflate_flush(z, r);
                                }
                                r = 0;
                                --n;
                                b |= (z.next_in[p++] & 0xFF) << k;
                            }
                            this.dist += (b & InfCodes.inflate_mask[j]);
                            b >>= j;
                            k -= j;
                            this.mode = 5;
                        }
                        case 5: {
                            int f;
                            for (f = q - this.dist; f < 0; f += s.end) {}
                            while (this.len != 0) {
                                if (m == 0) {
                                    if (q == s.end && s.read != 0) {
                                        q = 0;
                                        m = ((q < s.read) ? (s.read - q - 1) : (s.end - q));
                                    }
                                    if (m == 0) {
                                        s.write = q;
                                        r = s.inflate_flush(z, r);
                                        q = s.write;
                                        m = ((q < s.read) ? (s.read - q - 1) : (s.end - q));
                                        if (q == s.end && s.read != 0) {
                                            q = 0;
                                            m = ((q < s.read) ? (s.read - q - 1) : (s.end - q));
                                        }
                                        if (m == 0) {
                                            s.bitb = b;
                                            s.bitk = k;
                                            z.avail_in = n;
                                            z.total_in += p - z.next_in_index;
                                            z.next_in_index = p;
                                            s.write = q;
                                            return s.inflate_flush(z, r);
                                        }
                                    }
                                }
                                s.window[q++] = s.window[f++];
                                --m;
                                if (f == s.end) {
                                    f = 0;
                                }
                                --this.len;
                            }
                            this.mode = 0;
                            continue;
                        }
                        case 6: {
                            if (m == 0) {
                                if (q == s.end && s.read != 0) {
                                    q = 0;
                                    m = ((q < s.read) ? (s.read - q - 1) : (s.end - q));
                                }
                                if (m == 0) {
                                    s.write = q;
                                    r = s.inflate_flush(z, r);
                                    q = s.write;
                                    m = ((q < s.read) ? (s.read - q - 1) : (s.end - q));
                                    if (q == s.end && s.read != 0) {
                                        q = 0;
                                        m = ((q < s.read) ? (s.read - q - 1) : (s.end - q));
                                    }
                                    if (m == 0) {
                                        s.bitb = b;
                                        s.bitk = k;
                                        z.avail_in = n;
                                        z.total_in += p - z.next_in_index;
                                        z.next_in_index = p;
                                        s.write = q;
                                        return s.inflate_flush(z, r);
                                    }
                                }
                            }
                            r = 0;
                            s.window[q++] = (byte)this.lit;
                            --m;
                            this.mode = 0;
                            continue;
                        }
                        case 7: {
                            if (k > 7) {
                                k -= 8;
                                ++n;
                                --p;
                            }
                            s.write = q;
                            r = s.inflate_flush(z, r);
                            q = s.write;
                            if (s.read != s.write) {
                                s.bitb = b;
                                s.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                s.write = q;
                                return s.inflate_flush(z, r);
                            }
                            this.mode = 8;
                            break Label_1947;
                        }
                        case 8: {
                            break Label_1947;
                        }
                        case 9: {
                            break Label_2003;
                        }
                        default: {
                            break Label_2060;
                        }
                    }
                }
                r = 1;
                s.bitb = b;
                s.bitk = k;
                z.avail_in = n;
                z.total_in += p - z.next_in_index;
                z.next_in_index = p;
                s.write = q;
                return s.inflate_flush(z, r);
            }
            r = -3;
            s.bitb = b;
            s.bitk = k;
            z.avail_in = n;
            z.total_in += p - z.next_in_index;
            z.next_in_index = p;
            s.write = q;
            return s.inflate_flush(z, r);
        }
        r = -2;
        s.bitb = b;
        s.bitk = k;
        z.avail_in = n;
        z.total_in += p - z.next_in_index;
        z.next_in_index = p;
        s.write = q;
        return s.inflate_flush(z, r);
    }
    
    static int inflate_fast(final int bl, final int bd, final int[] tl, final int tl_index, final int[] td, final int td_index, final InfBlocks s, final ZStream z) {
        int p = z.next_in_index;
        int n = z.avail_in;
        int b = s.bitb;
        int k = s.bitk;
        int q = s.write;
        int m = (q < s.read) ? (s.read - q - 1) : (s.end - q);
        final int ml = InfCodes.inflate_mask[bl];
        final int md = InfCodes.inflate_mask[bd];
        while (true) {
            if (k < 20) {
                --n;
                b |= (z.next_in[p++] & 0xFF) << k;
                k += 8;
            }
            else {
                int t = b & ml;
                int[] tp = tl;
                int tp_index = tl_index;
                int tp_index_t_3 = (tp_index + t) * 3;
                Label_1301: {
                    int e;
                    if ((e = tp[tp_index_t_3]) == 0) {
                        b >>= tp[tp_index_t_3 + 1];
                        k -= tp[tp_index_t_3 + 1];
                        s.window[q++] = (byte)tp[tp_index_t_3 + 2];
                        --m;
                    }
                    else {
                        do {
                            b >>= tp[tp_index_t_3 + 1];
                            k -= tp[tp_index_t_3 + 1];
                            if ((e & 0x10) != 0x0) {
                                e &= 0xF;
                                int c = tp[tp_index_t_3 + 2] + (b & InfCodes.inflate_mask[e]);
                                b >>= e;
                                for (k -= e; k < 15; k += 8) {
                                    --n;
                                    b |= (z.next_in[p++] & 0xFF) << k;
                                }
                                t = (b & md);
                                tp = td;
                                tp_index = td_index;
                                tp_index_t_3 = (tp_index + t) * 3;
                                e = tp[tp_index_t_3];
                                while (true) {
                                    b >>= tp[tp_index_t_3 + 1];
                                    k -= tp[tp_index_t_3 + 1];
                                    if ((e & 0x10) != 0x0) {
                                        for (e &= 0xF; k < e; k += 8) {
                                            --n;
                                            b |= (z.next_in[p++] & 0xFF) << k;
                                        }
                                        final int d = tp[tp_index_t_3 + 2] + (b & InfCodes.inflate_mask[e]);
                                        b >>= e;
                                        k -= e;
                                        m -= c;
                                        int r;
                                        if (q >= d) {
                                            r = q - d;
                                            if (q - r > 0 && 2 > q - r) {
                                                s.window[q++] = s.window[r++];
                                                s.window[q++] = s.window[r++];
                                                c -= 2;
                                            }
                                            else {
                                                System.arraycopy(s.window, r, s.window, q, 2);
                                                q += 2;
                                                r += 2;
                                                c -= 2;
                                            }
                                        }
                                        else {
                                            r = q - d;
                                            do {
                                                r += s.end;
                                            } while (r < 0);
                                            e = s.end - r;
                                            if (c > e) {
                                                c -= e;
                                                if (q - r > 0 && e > q - r) {
                                                    do {
                                                        s.window[q++] = s.window[r++];
                                                    } while (--e != 0);
                                                }
                                                else {
                                                    System.arraycopy(s.window, r, s.window, q, e);
                                                    q += e;
                                                    r += e;
                                                }
                                                r = 0;
                                            }
                                        }
                                        if (q - r > 0 && c > q - r) {
                                            do {
                                                s.window[q++] = s.window[r++];
                                            } while (--c != 0);
                                            break Label_1301;
                                        }
                                        System.arraycopy(s.window, r, s.window, q, c);
                                        q += c;
                                        r += c;
                                        break Label_1301;
                                    }
                                    else {
                                        if ((e & 0x40) != 0x0) {
                                            z.msg = "invalid distance code";
                                            c = z.avail_in - n;
                                            c = ((k >> 3 < c) ? (k >> 3) : c);
                                            n += c;
                                            p -= c;
                                            k -= c << 3;
                                            s.bitb = b;
                                            s.bitk = k;
                                            z.avail_in = n;
                                            z.total_in += p - z.next_in_index;
                                            z.next_in_index = p;
                                            s.write = q;
                                            return -3;
                                        }
                                        t += tp[tp_index_t_3 + 2];
                                        t += (b & InfCodes.inflate_mask[e]);
                                        tp_index_t_3 = (tp_index + t) * 3;
                                        e = tp[tp_index_t_3];
                                    }
                                }
                            }
                            else if ((e & 0x40) == 0x0) {
                                t += tp[tp_index_t_3 + 2];
                                t += (b & InfCodes.inflate_mask[e]);
                                tp_index_t_3 = (tp_index + t) * 3;
                            }
                            else {
                                if ((e & 0x20) != 0x0) {
                                    int c = z.avail_in - n;
                                    c = ((k >> 3 < c) ? (k >> 3) : c);
                                    n += c;
                                    p -= c;
                                    k -= c << 3;
                                    s.bitb = b;
                                    s.bitk = k;
                                    z.avail_in = n;
                                    z.total_in += p - z.next_in_index;
                                    z.next_in_index = p;
                                    s.write = q;
                                    return 1;
                                }
                                z.msg = "invalid literal/length code";
                                int c = z.avail_in - n;
                                c = ((k >> 3 < c) ? (k >> 3) : c);
                                n += c;
                                p -= c;
                                k -= c << 3;
                                s.bitb = b;
                                s.bitk = k;
                                z.avail_in = n;
                                z.total_in += p - z.next_in_index;
                                z.next_in_index = p;
                                s.write = q;
                                return -3;
                            }
                        } while ((e = tp[tp_index_t_3]) != 0);
                        b >>= tp[tp_index_t_3 + 1];
                        k -= tp[tp_index_t_3 + 1];
                        s.window[q++] = (byte)tp[tp_index_t_3 + 2];
                        --m;
                    }
                }
                if (m < 258 || n < 10) {
                    int c = z.avail_in - n;
                    c = ((k >> 3 < c) ? (k >> 3) : c);
                    n += c;
                    p -= c;
                    k -= c << 3;
                    s.bitb = b;
                    s.bitk = k;
                    z.avail_in = n;
                    z.total_in += p - z.next_in_index;
                    z.next_in_index = p;
                    s.write = q;
                    return 0;
                }
                continue;
            }
        }
    }
    
    static {
        inflate_mask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535 };
    }
}
