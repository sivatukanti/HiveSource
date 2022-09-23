// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util.internal.jzlib;

final class Deflate
{
    private static final int STORED = 0;
    private static final int FAST = 1;
    private static final int SLOW = 2;
    private static final Config[] config_table;
    private static final String[] z_errmsg;
    private static final int NeedMore = 0;
    private static final int BlockDone = 1;
    private static final int FinishStarted = 2;
    private static final int FinishDone = 3;
    private static final int INIT_STATE = 42;
    private static final int BUSY_STATE = 113;
    private static final int FINISH_STATE = 666;
    private static final int STORED_BLOCK = 0;
    private static final int STATIC_TREES = 1;
    private static final int DYN_TREES = 2;
    private static final int Z_BINARY = 0;
    private static final int Z_ASCII = 1;
    private static final int Z_UNKNOWN = 2;
    private static final int Buf_size = 16;
    private static final int REP_3_6 = 16;
    private static final int REPZ_3_10 = 17;
    private static final int REPZ_11_138 = 18;
    private static final int MIN_MATCH = 3;
    private static final int MAX_MATCH = 258;
    private static final int MIN_LOOKAHEAD = 262;
    private static final int END_BLOCK = 256;
    ZStream strm;
    int status;
    byte[] pending_buf;
    int pending_buf_size;
    int pending_out;
    int pending;
    JZlib.WrapperType wrapperType;
    private boolean wroteTrailer;
    byte data_type;
    int last_flush;
    int w_size;
    int w_bits;
    int w_mask;
    byte[] window;
    int window_size;
    short[] prev;
    short[] head;
    int ins_h;
    int hash_size;
    int hash_bits;
    int hash_mask;
    int hash_shift;
    int block_start;
    int match_length;
    int prev_match;
    int match_available;
    int strstart;
    int match_start;
    int lookahead;
    int prev_length;
    int max_chain_length;
    int max_lazy_match;
    int level;
    int strategy;
    int good_match;
    int nice_match;
    final short[] dyn_ltree;
    final short[] dyn_dtree;
    final short[] bl_tree;
    final Tree l_desc;
    final Tree d_desc;
    final Tree bl_desc;
    final short[] bl_count;
    final int[] heap;
    int heap_len;
    int heap_max;
    final byte[] depth;
    int l_buf;
    int lit_bufsize;
    int last_lit;
    int d_buf;
    int opt_len;
    int static_len;
    int matches;
    int last_eob_len;
    short bi_buf;
    int bi_valid;
    private int gzipUncompressedBytes;
    
    Deflate() {
        this.l_desc = new Tree();
        this.d_desc = new Tree();
        this.bl_desc = new Tree();
        this.bl_count = new short[16];
        this.heap = new int[573];
        this.depth = new byte[573];
        this.dyn_ltree = new short[1146];
        this.dyn_dtree = new short[122];
        this.bl_tree = new short[78];
    }
    
    private void lm_init() {
        this.window_size = 2 * this.w_size;
        this.max_lazy_match = Deflate.config_table[this.level].max_lazy;
        this.good_match = Deflate.config_table[this.level].good_length;
        this.nice_match = Deflate.config_table[this.level].nice_length;
        this.max_chain_length = Deflate.config_table[this.level].max_chain;
        this.strstart = 0;
        this.block_start = 0;
        this.lookahead = 0;
        final int n = 2;
        this.prev_length = n;
        this.match_length = n;
        this.match_available = 0;
        this.ins_h = 0;
    }
    
    private void tr_init() {
        this.l_desc.dyn_tree = this.dyn_ltree;
        this.l_desc.stat_desc = StaticTree.static_l_desc;
        this.d_desc.dyn_tree = this.dyn_dtree;
        this.d_desc.stat_desc = StaticTree.static_d_desc;
        this.bl_desc.dyn_tree = this.bl_tree;
        this.bl_desc.stat_desc = StaticTree.static_bl_desc;
        this.bi_buf = 0;
        this.bi_valid = 0;
        this.last_eob_len = 8;
        this.init_block();
    }
    
    private void init_block() {
        for (int i = 0; i < 286; ++i) {
            this.dyn_ltree[i * 2] = 0;
        }
        for (int i = 0; i < 30; ++i) {
            this.dyn_dtree[i * 2] = 0;
        }
        for (int i = 0; i < 19; ++i) {
            this.bl_tree[i * 2] = 0;
        }
        this.dyn_ltree[512] = 1;
        final int n = 0;
        this.static_len = n;
        this.opt_len = n;
        final int n2 = 0;
        this.matches = n2;
        this.last_lit = n2;
    }
    
    void pqdownheap(final short[] tree, int k) {
        final int v = this.heap[k];
        for (int j = k << 1; j <= this.heap_len; j <<= 1) {
            if (j < this.heap_len && smaller(tree, this.heap[j + 1], this.heap[j], this.depth)) {
                ++j;
            }
            if (smaller(tree, v, this.heap[j], this.depth)) {
                break;
            }
            this.heap[k] = this.heap[j];
            k = j;
        }
        this.heap[k] = v;
    }
    
    private static boolean smaller(final short[] tree, final int n, final int m, final byte[] depth) {
        final short tn2 = tree[n * 2];
        final short tm2 = tree[m * 2];
        return tn2 < tm2 || (tn2 == tm2 && depth[n] <= depth[m]);
    }
    
    private void scan_tree(final short[] tree, final int max_code) {
        int prevlen = -1;
        int nextlen = tree[1];
        int count = 0;
        int max_count = 7;
        int min_count = 4;
        if (nextlen == 0) {
            max_count = 138;
            min_count = 3;
        }
        tree[(max_code + 1) * 2 + 1] = -1;
        for (int n = 0; n <= max_code; ++n) {
            final int curlen = nextlen;
            nextlen = tree[(n + 1) * 2 + 1];
            if (++count >= max_count || curlen != nextlen) {
                if (count < min_count) {
                    final short[] bl_tree = this.bl_tree;
                    final int n2 = curlen * 2;
                    bl_tree[n2] += (short)count;
                }
                else if (curlen != 0) {
                    if (curlen != prevlen) {
                        final short[] bl_tree2 = this.bl_tree;
                        final int n3 = curlen * 2;
                        ++bl_tree2[n3];
                    }
                    final short[] bl_tree3 = this.bl_tree;
                    final int n4 = 32;
                    ++bl_tree3[n4];
                }
                else if (count <= 10) {
                    final short[] bl_tree4 = this.bl_tree;
                    final int n5 = 34;
                    ++bl_tree4[n5];
                }
                else {
                    final short[] bl_tree5 = this.bl_tree;
                    final int n6 = 36;
                    ++bl_tree5[n6];
                }
                count = 0;
                prevlen = curlen;
                if (nextlen == 0) {
                    max_count = 138;
                    min_count = 3;
                }
                else if (curlen == nextlen) {
                    max_count = 6;
                    min_count = 3;
                }
                else {
                    max_count = 7;
                    min_count = 4;
                }
            }
        }
    }
    
    private int build_bl_tree() {
        this.scan_tree(this.dyn_ltree, this.l_desc.max_code);
        this.scan_tree(this.dyn_dtree, this.d_desc.max_code);
        this.bl_desc.build_tree(this);
        int max_blindex;
        for (max_blindex = 18; max_blindex >= 3 && this.bl_tree[Tree.bl_order[max_blindex] * 2 + 1] == 0; --max_blindex) {}
        this.opt_len += 3 * (max_blindex + 1) + 5 + 5 + 4;
        return max_blindex;
    }
    
    private void send_all_trees(final int lcodes, final int dcodes, final int blcodes) {
        this.send_bits(lcodes - 257, 5);
        this.send_bits(dcodes - 1, 5);
        this.send_bits(blcodes - 4, 4);
        for (int rank = 0; rank < blcodes; ++rank) {
            this.send_bits(this.bl_tree[Tree.bl_order[rank] * 2 + 1], 3);
        }
        this.send_tree(this.dyn_ltree, lcodes - 1);
        this.send_tree(this.dyn_dtree, dcodes - 1);
    }
    
    private void send_tree(final short[] tree, final int max_code) {
        int prevlen = -1;
        int nextlen = tree[1];
        int count = 0;
        int max_count = 7;
        int min_count = 4;
        if (nextlen == 0) {
            max_count = 138;
            min_count = 3;
        }
        for (int n = 0; n <= max_code; ++n) {
            final int curlen = nextlen;
            nextlen = tree[(n + 1) * 2 + 1];
            if (++count >= max_count || curlen != nextlen) {
                if (count < min_count) {
                    do {
                        this.send_code(curlen, this.bl_tree);
                    } while (--count != 0);
                }
                else if (curlen != 0) {
                    if (curlen != prevlen) {
                        this.send_code(curlen, this.bl_tree);
                        --count;
                    }
                    this.send_code(16, this.bl_tree);
                    this.send_bits(count - 3, 2);
                }
                else if (count <= 10) {
                    this.send_code(17, this.bl_tree);
                    this.send_bits(count - 3, 3);
                }
                else {
                    this.send_code(18, this.bl_tree);
                    this.send_bits(count - 11, 7);
                }
                count = 0;
                prevlen = curlen;
                if (nextlen == 0) {
                    max_count = 138;
                    min_count = 3;
                }
                else if (curlen == nextlen) {
                    max_count = 6;
                    min_count = 3;
                }
                else {
                    max_count = 7;
                    min_count = 4;
                }
            }
        }
    }
    
    private void put_byte(final byte[] p, final int start, final int len) {
        System.arraycopy(p, start, this.pending_buf, this.pending, len);
        this.pending += len;
    }
    
    private void put_byte(final byte c) {
        this.pending_buf[this.pending++] = c;
    }
    
    private void put_short(final int w) {
        this.put_byte((byte)w);
        this.put_byte((byte)(w >>> 8));
    }
    
    private void putShortMSB(final int b) {
        this.put_byte((byte)(b >> 8));
        this.put_byte((byte)b);
    }
    
    private void send_code(final int c, final short[] tree) {
        final int c2 = c * 2;
        this.send_bits(tree[c2] & 0xFFFF, tree[c2 + 1] & 0xFFFF);
    }
    
    private void send_bits(final int value, final int length) {
        final int len = length;
        if (this.bi_valid > 16 - len) {
            final int val = value;
            this.put_short(this.bi_buf |= (short)(val << this.bi_valid & 0xFFFF));
            this.bi_buf = (short)(val >>> 16 - this.bi_valid);
            this.bi_valid += len - 16;
        }
        else {
            this.bi_buf |= (short)(value << this.bi_valid & 0xFFFF);
            this.bi_valid += len;
        }
    }
    
    private void _tr_align() {
        this.send_bits(2, 3);
        this.send_code(256, StaticTree.static_ltree);
        this.bi_flush();
        if (1 + this.last_eob_len + 10 - this.bi_valid < 9) {
            this.send_bits(2, 3);
            this.send_code(256, StaticTree.static_ltree);
            this.bi_flush();
        }
        this.last_eob_len = 7;
    }
    
    private boolean _tr_tally(int dist, final int lc) {
        this.pending_buf[this.d_buf + this.last_lit * 2] = (byte)(dist >>> 8);
        this.pending_buf[this.d_buf + this.last_lit * 2 + 1] = (byte)dist;
        this.pending_buf[this.l_buf + this.last_lit] = (byte)lc;
        ++this.last_lit;
        if (dist == 0) {
            final short[] dyn_ltree = this.dyn_ltree;
            final int n = lc * 2;
            ++dyn_ltree[n];
        }
        else {
            ++this.matches;
            --dist;
            final short[] dyn_ltree2 = this.dyn_ltree;
            final int n2 = (Tree._length_code[lc] + 256 + 1) * 2;
            ++dyn_ltree2[n2];
            final short[] dyn_dtree = this.dyn_dtree;
            final int n3 = Tree.d_code(dist) * 2;
            ++dyn_dtree[n3];
        }
        if ((this.last_lit & 0x1FFF) == 0x0 && this.level > 2) {
            int out_length = this.last_lit * 8;
            final int in_length = this.strstart - this.block_start;
            for (int dcode = 0; dcode < 30; ++dcode) {
                out_length += (int)(this.dyn_dtree[dcode * 2] * (5L + Tree.extra_dbits[dcode]));
            }
            out_length >>>= 3;
            if (this.matches < this.last_lit / 2 && out_length < in_length / 2) {
                return true;
            }
        }
        return this.last_lit == this.lit_bufsize - 1;
    }
    
    private void compress_block(final short[] ltree, final short[] dtree) {
        int lx = 0;
        if (this.last_lit != 0) {
            do {
                int dist = (this.pending_buf[this.d_buf + lx * 2] << 8 & 0xFF00) | (this.pending_buf[this.d_buf + lx * 2 + 1] & 0xFF);
                int lc = this.pending_buf[this.l_buf + lx] & 0xFF;
                ++lx;
                if (dist == 0) {
                    this.send_code(lc, ltree);
                }
                else {
                    int code = Tree._length_code[lc];
                    this.send_code(code + 256 + 1, ltree);
                    int extra = Tree.extra_lbits[code];
                    if (extra != 0) {
                        lc -= Tree.base_length[code];
                        this.send_bits(lc, extra);
                    }
                    code = Tree.d_code(--dist);
                    this.send_code(code, dtree);
                    extra = Tree.extra_dbits[code];
                    if (extra == 0) {
                        continue;
                    }
                    dist -= Tree.base_dist[code];
                    this.send_bits(dist, extra);
                }
            } while (lx < this.last_lit);
        }
        this.send_code(256, ltree);
        this.last_eob_len = ltree[513];
    }
    
    private void set_data_type() {
        int n = 0;
        int ascii_freq = 0;
        int bin_freq = 0;
        while (n < 7) {
            bin_freq += this.dyn_ltree[n * 2];
            ++n;
        }
        while (n < 128) {
            ascii_freq += this.dyn_ltree[n * 2];
            ++n;
        }
        while (n < 256) {
            bin_freq += this.dyn_ltree[n * 2];
            ++n;
        }
        this.data_type = (byte)((bin_freq <= ascii_freq >>> 2) ? 1 : 0);
    }
    
    private void bi_flush() {
        if (this.bi_valid == 16) {
            this.put_short(this.bi_buf);
            this.bi_buf = 0;
            this.bi_valid = 0;
        }
        else if (this.bi_valid >= 8) {
            this.put_byte((byte)this.bi_buf);
            this.bi_buf >>>= 8;
            this.bi_valid -= 8;
        }
    }
    
    private void bi_windup() {
        if (this.bi_valid > 8) {
            this.put_short(this.bi_buf);
        }
        else if (this.bi_valid > 0) {
            this.put_byte((byte)this.bi_buf);
        }
        this.bi_buf = 0;
        this.bi_valid = 0;
    }
    
    private void copy_block(final int buf, final int len, final boolean header) {
        this.bi_windup();
        this.last_eob_len = 8;
        if (header) {
            this.put_short((short)len);
            this.put_short((short)~len);
        }
        this.put_byte(this.window, buf, len);
    }
    
    private void flush_block_only(final boolean eof) {
        this._tr_flush_block((this.block_start >= 0) ? this.block_start : -1, this.strstart - this.block_start, eof);
        this.block_start = this.strstart;
        this.strm.flush_pending();
    }
    
    private int deflate_stored(final int flush) {
        int max_block_size = 65535;
        if (max_block_size > this.pending_buf_size - 5) {
            max_block_size = this.pending_buf_size - 5;
        }
        while (true) {
            if (this.lookahead <= 1) {
                this.fill_window();
                if (this.lookahead == 0 && flush == 0) {
                    return 0;
                }
                if (this.lookahead == 0) {
                    this.flush_block_only(flush == 4);
                    if (this.strm.avail_out == 0) {
                        return (flush == 4) ? 2 : 0;
                    }
                    return (flush == 4) ? 3 : 1;
                }
            }
            this.strstart += this.lookahead;
            this.lookahead = 0;
            final int max_start = this.block_start + max_block_size;
            if (this.strstart == 0 || this.strstart >= max_start) {
                this.lookahead = this.strstart - max_start;
                this.strstart = max_start;
                this.flush_block_only(false);
                if (this.strm.avail_out == 0) {
                    return 0;
                }
            }
            if (this.strstart - this.block_start >= this.w_size - 262) {
                this.flush_block_only(false);
                if (this.strm.avail_out == 0) {
                    return 0;
                }
                continue;
            }
        }
    }
    
    private void _tr_stored_block(final int buf, final int stored_len, final boolean eof) {
        this.send_bits(0 + (eof ? 1 : 0), 3);
        this.copy_block(buf, stored_len, true);
    }
    
    private void _tr_flush_block(final int buf, final int stored_len, final boolean eof) {
        int max_blindex = 0;
        int opt_lenb;
        int static_lenb;
        if (this.level > 0) {
            if (this.data_type == 2) {
                this.set_data_type();
            }
            this.l_desc.build_tree(this);
            this.d_desc.build_tree(this);
            max_blindex = this.build_bl_tree();
            opt_lenb = this.opt_len + 3 + 7 >>> 3;
            static_lenb = this.static_len + 3 + 7 >>> 3;
            if (static_lenb <= opt_lenb) {
                opt_lenb = static_lenb;
            }
        }
        else {
            static_lenb = (opt_lenb = stored_len + 5);
        }
        if (stored_len + 4 <= opt_lenb && buf != -1) {
            this._tr_stored_block(buf, stored_len, eof);
        }
        else if (static_lenb == opt_lenb) {
            this.send_bits(2 + (eof ? 1 : 0), 3);
            this.compress_block(StaticTree.static_ltree, StaticTree.static_dtree);
        }
        else {
            this.send_bits(4 + (eof ? 1 : 0), 3);
            this.send_all_trees(this.l_desc.max_code + 1, this.d_desc.max_code + 1, max_blindex + 1);
            this.compress_block(this.dyn_ltree, this.dyn_dtree);
        }
        this.init_block();
        if (eof) {
            this.bi_windup();
        }
    }
    
    private void fill_window() {
        do {
            int more = this.window_size - this.lookahead - this.strstart;
            if (more == 0 && this.strstart == 0 && this.lookahead == 0) {
                more = this.w_size;
            }
            else if (more == -1) {
                --more;
            }
            else if (this.strstart >= this.w_size + this.w_size - 262) {
                System.arraycopy(this.window, this.w_size, this.window, 0, this.w_size);
                this.match_start -= this.w_size;
                this.strstart -= this.w_size;
                this.block_start -= this.w_size;
                int p;
                int n = p = this.hash_size;
                do {
                    final int m = this.head[--p] & 0xFFFF;
                    this.head[p] = (short)((m >= this.w_size) ? ((short)(m - this.w_size)) : 0);
                } while (--n != 0);
                n = (p = this.w_size);
                do {
                    final int m = this.prev[--p] & 0xFFFF;
                    this.prev[p] = (short)((m >= this.w_size) ? ((short)(m - this.w_size)) : 0);
                } while (--n != 0);
                more += this.w_size;
            }
            if (this.strm.avail_in == 0) {
                return;
            }
            int n = this.strm.read_buf(this.window, this.strstart + this.lookahead, more);
            this.lookahead += n;
            if (this.lookahead < 3) {
                continue;
            }
            this.ins_h = (this.window[this.strstart] & 0xFF);
            this.ins_h = ((this.ins_h << this.hash_shift ^ (this.window[this.strstart + 1] & 0xFF)) & this.hash_mask);
        } while (this.lookahead < 262 && this.strm.avail_in != 0);
    }
    
    private int deflate_fast(final int flush) {
        int hash_head = 0;
        while (true) {
            if (this.lookahead < 262) {
                this.fill_window();
                if (this.lookahead < 262 && flush == 0) {
                    return 0;
                }
                if (this.lookahead == 0) {
                    this.flush_block_only(flush == 4);
                    if (this.strm.avail_out != 0) {
                        return (flush == 4) ? 3 : 1;
                    }
                    if (flush == 4) {
                        return 2;
                    }
                    return 0;
                }
            }
            if (this.lookahead >= 3) {
                this.ins_h = ((this.ins_h << this.hash_shift ^ (this.window[this.strstart + 3 - 1] & 0xFF)) & this.hash_mask);
                hash_head = (this.head[this.ins_h] & 0xFFFF);
                this.prev[this.strstart & this.w_mask] = this.head[this.ins_h];
                this.head[this.ins_h] = (short)this.strstart;
            }
            if (hash_head != 0L && (this.strstart - hash_head & 0xFFFF) <= this.w_size - 262 && this.strategy != 2) {
                this.match_length = this.longest_match(hash_head);
            }
            boolean bflush;
            if (this.match_length >= 3) {
                bflush = this._tr_tally(this.strstart - this.match_start, this.match_length - 3);
                this.lookahead -= this.match_length;
                if (this.match_length <= this.max_lazy_match && this.lookahead >= 3) {
                    --this.match_length;
                    do {
                        ++this.strstart;
                        this.ins_h = ((this.ins_h << this.hash_shift ^ (this.window[this.strstart + 3 - 1] & 0xFF)) & this.hash_mask);
                        hash_head = (this.head[this.ins_h] & 0xFFFF);
                        this.prev[this.strstart & this.w_mask] = this.head[this.ins_h];
                        this.head[this.ins_h] = (short)this.strstart;
                    } while (--this.match_length != 0);
                    ++this.strstart;
                }
                else {
                    this.strstart += this.match_length;
                    this.match_length = 0;
                    this.ins_h = (this.window[this.strstart] & 0xFF);
                    this.ins_h = ((this.ins_h << this.hash_shift ^ (this.window[this.strstart + 1] & 0xFF)) & this.hash_mask);
                }
            }
            else {
                bflush = this._tr_tally(0, this.window[this.strstart] & 0xFF);
                --this.lookahead;
                ++this.strstart;
            }
            if (bflush) {
                this.flush_block_only(false);
                if (this.strm.avail_out == 0) {
                    return 0;
                }
                continue;
            }
        }
    }
    
    private int deflate_slow(final int flush) {
        int hash_head = 0;
        while (true) {
            if (this.lookahead < 262) {
                this.fill_window();
                if (this.lookahead < 262 && flush == 0) {
                    return 0;
                }
                if (this.lookahead == 0) {
                    if (this.match_available != 0) {
                        this._tr_tally(0, this.window[this.strstart - 1] & 0xFF);
                        this.match_available = 0;
                    }
                    this.flush_block_only(flush == 4);
                    if (this.strm.avail_out != 0) {
                        return (flush == 4) ? 3 : 1;
                    }
                    if (flush == 4) {
                        return 2;
                    }
                    return 0;
                }
            }
            if (this.lookahead >= 3) {
                this.ins_h = ((this.ins_h << this.hash_shift ^ (this.window[this.strstart + 3 - 1] & 0xFF)) & this.hash_mask);
                hash_head = (this.head[this.ins_h] & 0xFFFF);
                this.prev[this.strstart & this.w_mask] = this.head[this.ins_h];
                this.head[this.ins_h] = (short)this.strstart;
            }
            this.prev_length = this.match_length;
            this.prev_match = this.match_start;
            this.match_length = 2;
            if (hash_head != 0 && this.prev_length < this.max_lazy_match && (this.strstart - hash_head & 0xFFFF) <= this.w_size - 262) {
                if (this.strategy != 2) {
                    this.match_length = this.longest_match(hash_head);
                }
                if (this.match_length <= 5 && (this.strategy == 1 || (this.match_length == 3 && this.strstart - this.match_start > 4096))) {
                    this.match_length = 2;
                }
            }
            if (this.prev_length >= 3 && this.match_length <= this.prev_length) {
                final int max_insert = this.strstart + this.lookahead - 3;
                final boolean bflush = this._tr_tally(this.strstart - 1 - this.prev_match, this.prev_length - 3);
                this.lookahead -= this.prev_length - 1;
                this.prev_length -= 2;
                do {
                    if (++this.strstart <= max_insert) {
                        this.ins_h = ((this.ins_h << this.hash_shift ^ (this.window[this.strstart + 3 - 1] & 0xFF)) & this.hash_mask);
                        hash_head = (this.head[this.ins_h] & 0xFFFF);
                        this.prev[this.strstart & this.w_mask] = this.head[this.ins_h];
                        this.head[this.ins_h] = (short)this.strstart;
                    }
                } while (--this.prev_length != 0);
                this.match_available = 0;
                this.match_length = 2;
                ++this.strstart;
                if (!bflush) {
                    continue;
                }
                this.flush_block_only(false);
                if (this.strm.avail_out == 0) {
                    return 0;
                }
                continue;
            }
            else if (this.match_available != 0) {
                final boolean bflush = this._tr_tally(0, this.window[this.strstart - 1] & 0xFF);
                if (bflush) {
                    this.flush_block_only(false);
                }
                ++this.strstart;
                --this.lookahead;
                if (this.strm.avail_out == 0) {
                    return 0;
                }
                continue;
            }
            else {
                this.match_available = 1;
                ++this.strstart;
                --this.lookahead;
            }
        }
    }
    
    private int longest_match(int cur_match) {
        int chain_length = this.max_chain_length;
        int scan = this.strstart;
        int best_len = this.prev_length;
        final int limit = (this.strstart > this.w_size - 262) ? (this.strstart - (this.w_size - 262)) : 0;
        int nice_match = this.nice_match;
        final int wmask = this.w_mask;
        final int strend = this.strstart + 258;
        byte scan_end1 = this.window[scan + best_len - 1];
        byte scan_end2 = this.window[scan + best_len];
        if (this.prev_length >= this.good_match) {
            chain_length >>= 2;
        }
        if (nice_match > this.lookahead) {
            nice_match = this.lookahead;
        }
        do {
            int match = cur_match;
            if (this.window[match + best_len] == scan_end2 && this.window[match + best_len - 1] == scan_end1 && this.window[match] == this.window[scan]) {
                if (this.window[++match] != this.window[scan + 1]) {
                    continue;
                }
                scan += 2;
                ++match;
                while (this.window[++scan] == this.window[++match] && this.window[++scan] == this.window[++match] && this.window[++scan] == this.window[++match] && this.window[++scan] == this.window[++match] && this.window[++scan] == this.window[++match] && this.window[++scan] == this.window[++match] && this.window[++scan] == this.window[++match] && this.window[++scan] == this.window[++match] && scan < strend) {}
                final int len = 258 - (strend - scan);
                scan = strend - 258;
                if (len <= best_len) {
                    continue;
                }
                this.match_start = cur_match;
                if ((best_len = len) >= nice_match) {
                    break;
                }
                scan_end1 = this.window[scan + best_len - 1];
                scan_end2 = this.window[scan + best_len];
            }
        } while ((cur_match = (this.prev[cur_match & wmask] & 0xFFFF)) > limit && --chain_length != 0);
        if (best_len <= this.lookahead) {
            return best_len;
        }
        return this.lookahead;
    }
    
    int deflateInit(final ZStream strm, final int level, final int bits, final int memLevel, final JZlib.WrapperType wrapperType) {
        return this.deflateInit2(strm, level, 8, bits, memLevel, 0, wrapperType);
    }
    
    private int deflateInit2(final ZStream strm, int level, final int method, final int windowBits, final int memLevel, final int strategy, final JZlib.WrapperType wrapperType) {
        if (wrapperType == JZlib.WrapperType.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("ZLIB_OR_NONE allowed only for inflate");
        }
        strm.msg = null;
        if (level == -1) {
            level = 6;
        }
        if (windowBits < 0) {
            throw new IllegalArgumentException("windowBits: " + windowBits);
        }
        if (memLevel < 1 || memLevel > 9 || method != 8 || windowBits < 9 || windowBits > 15 || level < 0 || level > 9 || strategy < 0 || strategy > 2) {
            return -2;
        }
        strm.dstate = this;
        this.wrapperType = wrapperType;
        this.w_bits = windowBits;
        this.w_size = 1 << this.w_bits;
        this.w_mask = this.w_size - 1;
        this.hash_bits = memLevel + 7;
        this.hash_size = 1 << this.hash_bits;
        this.hash_mask = this.hash_size - 1;
        this.hash_shift = (this.hash_bits + 3 - 1) / 3;
        this.window = new byte[this.w_size * 2];
        this.prev = new short[this.w_size];
        this.head = new short[this.hash_size];
        this.lit_bufsize = 1 << memLevel + 6;
        this.pending_buf = new byte[this.lit_bufsize * 4];
        this.pending_buf_size = this.lit_bufsize * 4;
        this.d_buf = this.lit_bufsize / 2;
        this.l_buf = 3 * this.lit_bufsize;
        this.level = level;
        this.strategy = strategy;
        return this.deflateReset(strm);
    }
    
    private int deflateReset(final ZStream strm) {
        final long n = 0L;
        strm.total_out = n;
        strm.total_in = n;
        strm.msg = null;
        this.pending = 0;
        this.pending_out = 0;
        this.wroteTrailer = false;
        this.status = ((this.wrapperType == JZlib.WrapperType.NONE) ? 113 : 42);
        strm.adler = Adler32.adler32(0L, null, 0, 0);
        strm.crc32 = 0;
        this.gzipUncompressedBytes = 0;
        this.last_flush = 0;
        this.tr_init();
        this.lm_init();
        return 0;
    }
    
    int deflateEnd() {
        if (this.status != 42 && this.status != 113 && this.status != 666) {
            return -2;
        }
        this.pending_buf = null;
        this.head = null;
        this.prev = null;
        this.window = null;
        return (this.status == 113) ? -3 : 0;
    }
    
    int deflateParams(final ZStream strm, int _level, final int _strategy) {
        int err = 0;
        if (_level == -1) {
            _level = 6;
        }
        if (_level < 0 || _level > 9 || _strategy < 0 || _strategy > 2) {
            return -2;
        }
        if (Deflate.config_table[this.level].func != Deflate.config_table[_level].func && strm.total_in != 0L) {
            err = strm.deflate(1);
        }
        if (this.level != _level) {
            this.level = _level;
            this.max_lazy_match = Deflate.config_table[this.level].max_lazy;
            this.good_match = Deflate.config_table[this.level].good_length;
            this.nice_match = Deflate.config_table[this.level].nice_length;
            this.max_chain_length = Deflate.config_table[this.level].max_chain;
        }
        this.strategy = _strategy;
        return err;
    }
    
    int deflateSetDictionary(final ZStream strm, final byte[] dictionary, final int dictLength) {
        int length = dictLength;
        int index = 0;
        if (dictionary == null || this.status != 42) {
            return -2;
        }
        strm.adler = Adler32.adler32(strm.adler, dictionary, 0, dictLength);
        if (length < 3) {
            return 0;
        }
        if (length > this.w_size - 262) {
            length = this.w_size - 262;
            index = dictLength - length;
        }
        System.arraycopy(dictionary, index, this.window, 0, length);
        this.strstart = length;
        this.block_start = length;
        this.ins_h = (this.window[0] & 0xFF);
        this.ins_h = ((this.ins_h << this.hash_shift ^ (this.window[1] & 0xFF)) & this.hash_mask);
        for (int n = 0; n <= length - 3; ++n) {
            this.ins_h = ((this.ins_h << this.hash_shift ^ (this.window[n + 3 - 1] & 0xFF)) & this.hash_mask);
            this.prev[n & this.w_mask] = this.head[this.ins_h];
            this.head[this.ins_h] = (short)n;
        }
        return 0;
    }
    
    int deflate(final ZStream strm, final int flush) {
        if (flush > 4 || flush < 0) {
            return -2;
        }
        if (strm.next_out == null || (strm.next_in == null && strm.avail_in != 0) || (this.status == 666 && flush != 4)) {
            strm.msg = Deflate.z_errmsg[4];
            return -2;
        }
        if (strm.avail_out == 0) {
            strm.msg = Deflate.z_errmsg[7];
            return -5;
        }
        this.strm = strm;
        final int old_flush = this.last_flush;
        this.last_flush = flush;
        if (this.status == 42) {
            switch (this.wrapperType) {
                case ZLIB: {
                    int header = 8 + (this.w_bits - 8 << 4) << 8;
                    int level_flags = (this.level - 1 & 0xFF) >> 1;
                    if (level_flags > 3) {
                        level_flags = 3;
                    }
                    header |= level_flags << 6;
                    if (this.strstart != 0) {
                        header |= 0x20;
                    }
                    header += 31 - header % 31;
                    this.putShortMSB(header);
                    if (this.strstart != 0) {
                        this.putShortMSB((int)(strm.adler >>> 16));
                        this.putShortMSB((int)(strm.adler & 0xFFFFL));
                    }
                    strm.adler = Adler32.adler32(0L, null, 0, 0);
                    break;
                }
                case GZIP: {
                    this.put_byte((byte)31);
                    this.put_byte((byte)(-117));
                    this.put_byte((byte)8);
                    this.put_byte((byte)0);
                    this.put_byte((byte)0);
                    this.put_byte((byte)0);
                    this.put_byte((byte)0);
                    this.put_byte((byte)0);
                    switch (Deflate.config_table[this.level].func) {
                        case 1: {
                            this.put_byte((byte)4);
                            break;
                        }
                        case 2: {
                            this.put_byte((byte)2);
                            break;
                        }
                        default: {
                            this.put_byte((byte)0);
                            break;
                        }
                    }
                    this.put_byte((byte)(-1));
                    strm.crc32 = 0;
                    break;
                }
            }
            this.status = 113;
        }
        if (this.pending != 0) {
            strm.flush_pending();
            if (strm.avail_out == 0) {
                this.last_flush = -1;
                return 0;
            }
        }
        else if (strm.avail_in == 0 && flush <= old_flush && flush != 4) {
            strm.msg = Deflate.z_errmsg[7];
            return -5;
        }
        if (this.status == 666 && strm.avail_in != 0) {
            strm.msg = Deflate.z_errmsg[7];
            return -5;
        }
        final int old_next_in_index = strm.next_in_index;
        try {
            if (strm.avail_in != 0 || this.lookahead != 0 || (flush != 0 && this.status != 666)) {
                int bstate = -1;
                switch (Deflate.config_table[this.level].func) {
                    case 0: {
                        bstate = this.deflate_stored(flush);
                        break;
                    }
                    case 1: {
                        bstate = this.deflate_fast(flush);
                        break;
                    }
                    case 2: {
                        bstate = this.deflate_slow(flush);
                        break;
                    }
                }
                if (bstate == 2 || bstate == 3) {
                    this.status = 666;
                }
                if (bstate == 0 || bstate == 2) {
                    if (strm.avail_out == 0) {
                        this.last_flush = -1;
                    }
                    return 0;
                }
                if (bstate == 1) {
                    if (flush == 1) {
                        this._tr_align();
                    }
                    else {
                        this._tr_stored_block(0, 0, false);
                        if (flush == 3) {
                            for (int i = 0; i < this.hash_size; ++i) {
                                this.head[i] = 0;
                            }
                        }
                    }
                    strm.flush_pending();
                    if (strm.avail_out == 0) {
                        this.last_flush = -1;
                        return 0;
                    }
                }
            }
        }
        finally {
            this.gzipUncompressedBytes += strm.next_in_index - old_next_in_index;
        }
        if (flush != 4) {
            return 0;
        }
        if (this.wrapperType == JZlib.WrapperType.NONE || this.wroteTrailer) {
            return 1;
        }
        switch (this.wrapperType) {
            case ZLIB: {
                this.putShortMSB((int)(strm.adler >>> 16));
                this.putShortMSB((int)(strm.adler & 0xFFFFL));
                break;
            }
            case GZIP: {
                this.put_byte((byte)(strm.crc32 & 0xFF));
                this.put_byte((byte)(strm.crc32 >>> 8 & 0xFF));
                this.put_byte((byte)(strm.crc32 >>> 16 & 0xFF));
                this.put_byte((byte)(strm.crc32 >>> 24 & 0xFF));
                this.put_byte((byte)(this.gzipUncompressedBytes & 0xFF));
                this.put_byte((byte)(this.gzipUncompressedBytes >>> 8 & 0xFF));
                this.put_byte((byte)(this.gzipUncompressedBytes >>> 16 & 0xFF));
                this.put_byte((byte)(this.gzipUncompressedBytes >>> 24 & 0xFF));
                break;
            }
        }
        strm.flush_pending();
        this.wroteTrailer = true;
        return (this.pending == 0) ? 1 : 0;
    }
    
    static {
        (config_table = new Config[10])[0] = new Config(0, 0, 0, 0, 0);
        Deflate.config_table[1] = new Config(4, 4, 8, 4, 1);
        Deflate.config_table[2] = new Config(4, 5, 16, 8, 1);
        Deflate.config_table[3] = new Config(4, 6, 32, 32, 1);
        Deflate.config_table[4] = new Config(4, 4, 16, 16, 2);
        Deflate.config_table[5] = new Config(8, 16, 32, 32, 2);
        Deflate.config_table[6] = new Config(8, 16, 128, 128, 2);
        Deflate.config_table[7] = new Config(8, 32, 128, 256, 2);
        Deflate.config_table[8] = new Config(32, 128, 258, 1024, 2);
        Deflate.config_table[9] = new Config(32, 258, 258, 4096, 2);
        z_errmsg = new String[] { "need dictionary", "stream end", "", "file error", "stream error", "data error", "insufficient memory", "buffer error", "incompatible version", "" };
    }
    
    private static final class Config
    {
        final int good_length;
        final int max_lazy;
        final int nice_length;
        final int max_chain;
        final int func;
        
        Config(final int good_length, final int max_lazy, final int nice_length, final int max_chain, final int func) {
            this.good_length = good_length;
            this.max_lazy = max_lazy;
            this.nice_length = nice_length;
            this.max_chain = max_chain;
            this.func = func;
        }
    }
}
