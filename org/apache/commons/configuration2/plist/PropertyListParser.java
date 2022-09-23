// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.plist;

import java.util.Iterator;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import java.util.Date;
import org.apache.commons.codec.binary.Hex;
import java.util.List;

class PropertyListParser implements PropertyListParserConstants
{
    public PropertyListParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns;
    private boolean jj_rescan;
    private int jj_gc;
    private final LookaheadSuccess jj_ls;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int[] jj_lasttokens;
    private int jj_endpos;
    
    protected String removeQuotes(String s) {
        if (s == null) {
            return null;
        }
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }
    
    protected String unescapeQuotes(final String s) {
        return s.replaceAll("\\\\\"", "\"");
    }
    
    protected byte[] filterData(String s) throws ParseException {
        if (s == null) {
            return null;
        }
        if (s.startsWith("<") && s.endsWith(">") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        s = s.replaceAll("\\s", "");
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        try {
            return Hex.decodeHex(s.toCharArray());
        }
        catch (Exception e) {
            throw new ParseException("Unable to parse the byte[] : " + e.getMessage());
        }
    }
    
    protected Date parseDate(final String s) throws ParseException {
        return PropertyListConfiguration.parseDate(s);
    }
    
    public final PropertyListConfiguration parse() throws ParseException {
        PropertyListConfiguration configuration = null;
        configuration = this.Dictionary();
        this.jj_consume_token(0);
        return configuration;
    }
    
    public final PropertyListConfiguration Dictionary() throws ParseException {
        final ImmutableNode.Builder builder = new ImmutableNode.Builder();
        ImmutableNode child = null;
        this.jj_consume_token(14);
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 27:
                case 28: {
                    child = this.Property();
                    if (child.getValue() instanceof HierarchicalConfiguration) {
                        final HierarchicalConfiguration<ImmutableNode> conf = (HierarchicalConfiguration<ImmutableNode>)child.getValue();
                        final ImmutableNode root = conf.getNodeModel().getNodeHandler().getRootNode();
                        final ImmutableNode.Builder childBuilder = new ImmutableNode.Builder();
                        childBuilder.name(child.getNodeName()).value(root.getValue()).addChildren(root.getChildren());
                        builder.addChild(childBuilder.create());
                        continue;
                    }
                    builder.addChild(child);
                    continue;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    this.jj_consume_token(15);
                    return new PropertyListConfiguration(builder.create());
                }
            }
        }
    }
    
    public final ImmutableNode Property() throws ParseException {
        String key = null;
        Object value = null;
        final ImmutableNode.Builder node = new ImmutableNode.Builder();
        key = this.String();
        node.name(key);
        this.jj_consume_token(17);
        value = this.Element();
        node.value(value);
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 16: {
                this.jj_consume_token(16);
                break;
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
                break;
            }
        }
        return node.create();
    }
    
    public final Object Element() throws ParseException {
        Object value = null;
        if (this.jj_2_1(2)) {
            value = this.Array();
            return value;
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 14: {
                value = this.Dictionary();
                return value;
            }
            case 27:
            case 28: {
                value = this.String();
                return value;
            }
            case 25: {
                value = this.Data();
                return value;
            }
            case 26: {
                value = this.Date();
                return value;
            }
            default: {
                this.jj_la1[2] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final List Array() throws ParseException {
        final List<Object> list = new ArrayList<Object>();
        Object element = null;
        this.jj_consume_token(11);
        Label_0225: {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 11:
                case 14:
                case 25:
                case 26:
                case 27:
                case 28: {
                    element = this.Element();
                    list.add(element);
                    while (true) {
                        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                            case 13: {
                                this.jj_consume_token(13);
                                element = this.Element();
                                list.add(element);
                                continue;
                            }
                            default: {
                                this.jj_la1[3] = this.jj_gen;
                                break Label_0225;
                            }
                        }
                    }
                    break;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    break;
                }
            }
        }
        this.jj_consume_token(12);
        return list;
    }
    
    public final String String() throws ParseException {
        Token token = null;
        final String value = null;
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 28: {
                token = this.jj_consume_token(28);
                return this.unescapeQuotes(this.removeQuotes(token.image));
            }
            case 27: {
                token = this.jj_consume_token(27);
                return token.image;
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final byte[] Data() throws ParseException {
        final Token token = this.jj_consume_token(25);
        return this.filterData(token.image);
    }
    
    public final Date Date() throws ParseException {
        final Token token = this.jj_consume_token(26);
        return this.parseDate(token.image);
    }
    
    private boolean jj_2_1(final int xla) {
        this.jj_la = xla;
        final Token token = this.token;
        this.jj_scanpos = token;
        this.jj_lastpos = token;
        try {
            return !this.jj_3_1();
        }
        catch (LookaheadSuccess ls) {
            return true;
        }
        finally {
            this.jj_save(0, xla);
        }
    }
    
    private boolean jj_3R_15() {
        return this.jj_scan_token(27);
    }
    
    private boolean jj_3R_3() {
        if (this.jj_scan_token(11)) {
            return true;
        }
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_4()) {
            this.jj_scanpos = xsp;
        }
        return this.jj_scan_token(12);
    }
    
    private boolean jj_3_1() {
        return this.jj_3R_3();
    }
    
    private boolean jj_3R_5() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3_1()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_6()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_7()) {
                    this.jj_scanpos = xsp;
                    if (this.jj_3R_8()) {
                        this.jj_scanpos = xsp;
                        if (this.jj_3R_9()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean jj_3R_14() {
        return this.jj_scan_token(28);
    }
    
    private boolean jj_3R_11() {
        final Token xsp = this.jj_scanpos;
        if (this.jj_3R_14()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_15()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean jj_3R_10() {
        return this.jj_scan_token(14);
    }
    
    private boolean jj_3R_13() {
        return this.jj_scan_token(26);
    }
    
    private boolean jj_3R_9() {
        return this.jj_3R_13();
    }
    
    private boolean jj_3R_8() {
        return this.jj_3R_12();
    }
    
    private boolean jj_3R_12() {
        return this.jj_scan_token(25);
    }
    
    private boolean jj_3R_7() {
        return this.jj_3R_11();
    }
    
    private boolean jj_3R_4() {
        return this.jj_3R_5();
    }
    
    private boolean jj_3R_6() {
        return this.jj_3R_10();
    }
    
    private static void jj_la1_init_0() {
        PropertyListParser.jj_la1_0 = new int[] { 402653184, 65536, 503332864, 8192, 503334912, 402653184 };
    }
    
    public PropertyListParser(final InputStream stream) {
        this(stream, null);
    }
    
    public PropertyListParser(final InputStream stream, final String encoding) {
        this.jj_la1 = new int[6];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new PropertyListParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final InputStream stream) {
        this.ReInit(stream, null);
    }
    
    public void ReInit(final InputStream stream, final String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public PropertyListParser(final Reader stream) {
        this.jj_la1 = new int[6];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new PropertyListParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public PropertyListParser(final PropertyListParserTokenManager tm) {
        this.jj_la1 = new int[6];
        this.jj_2_rtns = new JJCalls[1];
        this.jj_rescan = false;
        this.jj_gc = 0;
        this.jj_ls = new LookaheadSuccess();
        this.jj_expentries = new ArrayList<int[]>();
        this.jj_kind = -1;
        this.jj_lasttokens = new int[100];
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    public void ReInit(final PropertyListParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
        for (int i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }
    
    private Token jj_consume_token(final int kind) throws ParseException {
        final Token oldToken;
        if ((oldToken = this.token).next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    for (JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }
    
    private boolean jj_scan_token(final int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                final Token jj_scanpos = this.jj_scanpos;
                final Token nextToken = this.token_source.getNextToken();
                jj_scanpos.next = nextToken;
                this.jj_scanpos = nextToken;
                this.jj_lastpos = nextToken;
            }
            else {
                final Token next = this.jj_scanpos.next;
                this.jj_scanpos = next;
                this.jj_lastpos = next;
            }
        }
        else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok;
            for (tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
                ++i;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }
    
    public final Token getNextToken() {
        if (this.token.next != null) {
            this.token = this.token.next;
        }
        else {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            this.token = nextToken;
        }
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }
    
    public final Token getToken(final int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            if (t.next != null) {
                t = t.next;
            }
            else {
                final Token token = t;
                final Token nextToken = this.token_source.getNextToken();
                token.next = nextToken;
                t = nextToken;
            }
        }
        return t;
    }
    
    private int jj_ntk() {
        final Token next = this.token.next;
        this.jj_nt = next;
        if (next == null) {
            final Token token = this.token;
            final Token nextToken = this.token_source.getNextToken();
            token.next = nextToken;
            return this.jj_ntk = nextToken.kind;
        }
        return this.jj_ntk = this.jj_nt.kind;
    }
    
    private void jj_add_error_token(final int kind, final int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        }
        else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
        Label_0092:
            for (final int[] oldentry : this.jj_expentries) {
                if (oldentry.length == this.jj_expentry.length) {
                    for (int j = 0; j < this.jj_expentry.length; ++j) {
                        if (oldentry[j] != this.jj_expentry[j]) {
                            continue Label_0092;
                        }
                    }
                    this.jj_expentries.add(this.jj_expentry);
                    break;
                }
            }
            if (pos != 0) {
                this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
        }
    }
    
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        final boolean[] la1tokens = new boolean[30];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 6; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((PropertyListParser.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 30; ++i) {
            if (la1tokens[i]) {
                (this.jj_expentry = new int[1])[0] = i;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        final int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int k = 0; k < this.jj_expentries.size(); ++k) {
            exptokseq[k] = this.jj_expentries.get(k);
        }
        return new ParseException(this.token, exptokseq, PropertyListParser.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 1; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen > this.jj_gen) {
                        this.jj_la = p.arg;
                        final Token first = p.first;
                        this.jj_scanpos = first;
                        this.jj_lastpos = first;
                        switch (i) {
                            case 0: {
                                this.jj_3_1();
                                break;
                            }
                        }
                    }
                    p = p.next;
                } while (p != null);
            }
            catch (LookaheadSuccess lookaheadSuccess) {}
        }
        this.jj_rescan = false;
    }
    
    private void jj_save(final int index, final int xla) {
        JJCalls p;
        for (p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
            if (p.next == null) {
                final JJCalls jjCalls = p;
                final JJCalls next = new JJCalls();
                jjCalls.next = next;
                p = next;
                break;
            }
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }
    
    static {
        jj_la1_init_0();
    }
    
    private static final class LookaheadSuccess extends Error
    {
    }
    
    static final class JJCalls
    {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }
}
