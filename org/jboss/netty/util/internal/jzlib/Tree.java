// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal.jzlib;

final class Tree
{
    static final int[] extra_lbits;
    static final int[] extra_dbits;
    static final int[] extra_blbits;
    static final byte[] bl_order;
    static final byte[] _dist_code;
    static final byte[] _length_code;
    static final int[] base_length;
    static final int[] base_dist;
    short[] dyn_tree;
    int max_code;
    StaticTree stat_desc;
    
    static int d_code(final int dist) {
        return (dist < 256) ? Tree._dist_code[dist] : Tree._dist_code[256 + (dist >>> 7)];
    }
    
    private void gen_bitlen(final Deflate s) {
        final short[] tree = this.dyn_tree;
        final short[] stree = this.stat_desc.static_tree;
        final int[] extra = this.stat_desc.extra_bits;
        final int base = this.stat_desc.extra_base;
        final int max_length = this.stat_desc.max_length;
        int overflow = 0;
        for (int bits = 0; bits <= 15; ++bits) {
            s.bl_count[bits] = 0;
        }
        tree[s.heap[s.heap_max] * 2 + 1] = 0;
        int h;
        for (h = s.heap_max + 1; h < 573; ++h) {
            final int n = s.heap[h];
            int bits = tree[tree[n * 2 + 1] * 2 + 1] + 1;
            if (bits > max_length) {
                bits = max_length;
                ++overflow;
            }
            tree[n * 2 + 1] = (short)bits;
            if (n <= this.max_code) {
                final short[] bl_count = s.bl_count;
                final int n2 = bits;
                ++bl_count[n2];
                int xbits = 0;
                if (n >= base) {
                    xbits = extra[n - base];
                }
                final short f = tree[n * 2];
                s.opt_len += f * (bits + xbits);
                if (stree != null) {
                    s.static_len += f * (stree[n * 2 + 1] + xbits);
                }
            }
        }
        if (overflow == 0) {
            return;
        }
        do {
            int bits;
            for (bits = max_length - 1; s.bl_count[bits] == 0; --bits) {}
            final short[] bl_count2 = s.bl_count;
            final int n3 = bits;
            --bl_count2[n3];
            final short[] bl_count3 = s.bl_count;
            final int n4 = bits + 1;
            bl_count3[n4] += 2;
            final short[] bl_count4 = s.bl_count;
            final int n5 = max_length;
            --bl_count4[n5];
            overflow -= 2;
        } while (overflow > 0);
        for (int bits = max_length; bits != 0; --bits) {
            int n = s.bl_count[bits];
            while (n != 0) {
                final int m = s.heap[--h];
                if (m > this.max_code) {
                    continue;
                }
                if (tree[m * 2 + 1] != bits) {
                    s.opt_len += (int)((bits - (long)tree[m * 2 + 1]) * tree[m * 2]);
                    tree[m * 2 + 1] = (short)bits;
                }
                --n;
            }
        }
    }
    
    void build_tree(final Deflate s) {
        final short[] tree = this.dyn_tree;
        final short[] stree = this.stat_desc.static_tree;
        final int elems = this.stat_desc.elems;
        int max_code = -1;
        s.heap_len = 0;
        s.heap_max = 573;
        for (int n = 0; n < elems; ++n) {
            if (tree[n * 2] != 0) {
                max_code = (s.heap[++s.heap_len] = n);
                s.depth[n] = 0;
            }
            else {
                tree[n * 2 + 1] = 0;
            }
        }
        while (s.heap_len < 2) {
            final int[] heap = s.heap;
            final int n2 = ++s.heap_len;
            final int n3 = (max_code < 2) ? (++max_code) : 0;
            heap[n2] = n3;
            final int node = n3;
            tree[node * 2] = 1;
            s.depth[node] = 0;
            --s.opt_len;
            if (stree != null) {
                s.static_len -= stree[node * 2 + 1];
            }
        }
        this.max_code = max_code;
        for (int n = s.heap_len / 2; n >= 1; --n) {
            s.pqdownheap(tree, n);
        }
        int node = elems;
        do {
            final int n = s.heap[1];
            s.heap[1] = s.heap[s.heap_len--];
            s.pqdownheap(tree, 1);
            final int m = s.heap[1];
            s.heap[--s.heap_max] = n;
            s.heap[--s.heap_max] = m;
            tree[node * 2] = (short)(tree[n * 2] + tree[m * 2]);
            s.depth[node] = (byte)(Math.max(s.depth[n], s.depth[m]) + 1);
            tree[n * 2 + 1] = (tree[m * 2 + 1] = (short)node);
            s.heap[1] = node++;
            s.pqdownheap(tree, 1);
        } while (s.heap_len >= 2);
        s.heap[--s.heap_max] = s.heap[1];
        this.gen_bitlen(s);
        gen_codes(tree, max_code, s.bl_count);
    }
    
    private static void gen_codes(final short[] tree, final int max_code, final short[] bl_count) {
        final short[] next_code = new short[16];
        short code = 0;
        for (int bits = 1; bits <= 15; ++bits) {
            code = (next_code[bits] = (short)(code + bl_count[bits - 1] << 1));
        }
        for (int n = 0; n <= max_code; ++n) {
            final int len = tree[n * 2 + 1];
            if (len != 0) {
                final int n2 = n * 2;
                final short[] array = next_code;
                final int n3 = len;
                final short code2 = array[n3];
                array[n3] = (short)(code2 + 1);
                tree[n2] = (short)bi_reverse(code2, len);
            }
        }
    }
    
    private static int bi_reverse(int code, int len) {
        int res = 0;
        do {
            res |= (code & 0x1);
            code >>>= 1;
            res <<= 1;
        } while (--len > 0);
        return res >>> 1;
    }
    
    static {
        extra_lbits = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0 };
        extra_dbits = new int[] { 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 };
        extra_blbits = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 7 };
        bl_order = new byte[] { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };
        _dist_code = new byte[] { 0, 1, 2, 3, 4, 4, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 0, 0, 16, 17, 18, 18, 19, 19, 20, 20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29 };
        _length_code = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 12, 12, 13, 13, 13, 13, 14, 14, 14, 14, 15, 15, 15, 15, 16, 16, 16, 16, 16, 16, 16, 16, 17, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 28 };
        base_length = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 20, 24, 28, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 0 };
        base_dist = new int[] { 0, 1, 2, 3, 4, 6, 8, 12, 16, 24, 32, 48, 64, 96, 128, 192, 256, 384, 512, 768, 1024, 1536, 2048, 3072, 4096, 6144, 8192, 12288, 16384, 24576 };
    }
}
