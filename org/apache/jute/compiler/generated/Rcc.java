// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler.generated;

import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import org.apache.jute.compiler.JVector;
import org.apache.jute.compiler.JMap;
import org.apache.jute.compiler.JBuffer;
import org.apache.jute.compiler.JString;
import org.apache.jute.compiler.JDouble;
import org.apache.jute.compiler.JFloat;
import org.apache.jute.compiler.JLong;
import org.apache.jute.compiler.JInt;
import org.apache.jute.compiler.JBoolean;
import org.apache.jute.compiler.JByte;
import org.apache.jute.compiler.JType;
import org.apache.jute.compiler.JField;
import org.apache.jute.compiler.JRecord;
import java.util.Collection;
import java.io.Reader;
import java.io.FileReader;
import org.apache.jute.compiler.JFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Hashtable;

public class Rcc implements RccConstants
{
    private static Hashtable recTab;
    private static String curDir;
    private static String curFileName;
    private static String curModuleName;
    public RccTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private Vector jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    
    public static void main(final String[] args) {
        String language = "java";
        final ArrayList recFiles = new ArrayList();
        JFile curFile = null;
        for (int i = 0; i < args.length; ++i) {
            if ("-l".equalsIgnoreCase(args[i]) || "--language".equalsIgnoreCase(args[i])) {
                language = args[i + 1].toLowerCase();
                ++i;
            }
            else {
                recFiles.add(args[i]);
            }
        }
        if (!"c++".equals(language) && !"java".equals(language) && !"c".equals(language)) {
            System.out.println("Cannot recognize language:" + language);
            System.exit(1);
        }
        if (recFiles.size() == 0) {
            System.out.println("No record files specified. Exiting.");
            System.exit(1);
        }
        for (int i = 0; i < recFiles.size(); ++i) {
            Rcc.curFileName = recFiles.get(i);
            final File file = new File(Rcc.curFileName);
            try {
                curFile = parseFile(file);
            }
            catch (FileNotFoundException e3) {
                System.out.println("File " + recFiles.get(i) + " Not found.");
                System.exit(1);
            }
            catch (ParseException e) {
                System.out.println(e.toString());
                System.exit(1);
            }
            System.out.println(recFiles.get(i) + " Parsed Successfully");
            try {
                curFile.genCode(language, new File("."));
            }
            catch (IOException e2) {
                System.out.println(e2.toString());
                System.exit(1);
            }
        }
    }
    
    public static JFile parseFile(final File file) throws FileNotFoundException, ParseException {
        Rcc.curDir = file.getParent();
        Rcc.curFileName = file.getName();
        final FileReader reader = new FileReader(file);
        try {
            final Rcc parser = new Rcc(reader);
            Rcc.recTab = new Hashtable();
            return parser.Input();
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException ex) {}
        }
    }
    
    public final JFile Input() throws ParseException {
        final ArrayList ilist = new ArrayList();
        final ArrayList rlist = new ArrayList();
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 13: {
                    final JFile i = this.Include();
                    ilist.add(i);
                    break;
                }
                case 11: {
                    final ArrayList l = this.Module();
                    rlist.addAll(l);
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 11:
                case 13: {
                    continue;
                }
                default: {
                    this.jj_la1[1] = this.jj_gen;
                    this.jj_consume_token(0);
                    return new JFile(Rcc.curFileName, ilist, rlist);
                }
            }
        }
    }
    
    public final JFile Include() throws ParseException {
        this.jj_consume_token(13);
        final Token t = this.jj_consume_token(31);
        JFile ret = null;
        final String fname = t.image.replaceAll("^\"", "").replaceAll("\"$", "");
        final File file = new File(Rcc.curDir, fname);
        final String tmpDir = Rcc.curDir;
        final String tmpFile = Rcc.curFileName;
        Rcc.curDir = file.getParent();
        Rcc.curFileName = file.getName();
        try {
            final FileReader reader = new FileReader(file);
            final Rcc parser = new Rcc(reader);
            try {
                ret = parser.Input();
                System.out.println(fname + " Parsed Successfully");
            }
            catch (ParseException e) {
                System.out.println(e.toString());
                System.exit(1);
            }
            try {
                reader.close();
            }
            catch (IOException ex) {}
        }
        catch (FileNotFoundException e2) {
            System.out.println("File " + fname + " Not found.");
            System.exit(1);
        }
        Rcc.curDir = tmpDir;
        Rcc.curFileName = tmpFile;
        return ret;
    }
    
    public final ArrayList Module() throws ParseException {
        this.jj_consume_token(11);
        final String mName = Rcc.curModuleName = this.ModuleName();
        this.jj_consume_token(24);
        final ArrayList rlist = this.RecordList();
        this.jj_consume_token(25);
        return rlist;
    }
    
    public final String ModuleName() throws ParseException {
        String name = "";
        Token t = this.jj_consume_token(32);
        name += t.image;
        while (true) {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 30: {
                    this.jj_consume_token(30);
                    t = this.jj_consume_token(32);
                    name = name + "." + t.image;
                    continue;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    return name;
                }
            }
        }
    }
    
    public final ArrayList RecordList() throws ParseException {
        final ArrayList rlist = new ArrayList();
        while (true) {
            final JRecord r = this.Record();
            rlist.add(r);
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 12: {
                    continue;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    return rlist;
                }
            }
        }
    }
    
    public final JRecord Record() throws ParseException {
        final ArrayList flist = new ArrayList();
        this.jj_consume_token(12);
        final Token t = this.jj_consume_token(32);
        final String rname = t.image;
        this.jj_consume_token(24);
        while (true) {
            final JField f = this.Field();
            flist.add(f);
            this.jj_consume_token(28);
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 32: {
                    continue;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    this.jj_consume_token(25);
                    final String fqn = Rcc.curModuleName + "." + rname;
                    final JRecord r = new JRecord(fqn, flist);
                    Rcc.recTab.put(fqn, r);
                    return r;
                }
            }
        }
    }
    
    public final JField Field() throws ParseException {
        final JType jt = this.Type();
        final Token t = this.jj_consume_token(32);
        return new JField(jt, t.image);
    }
    
    public final JType Type() throws ParseException {
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
            case 23: {
                final JType jt = this.Map();
                return jt;
            }
            case 22: {
                final JType jt = this.Vector();
                return jt;
            }
            case 14: {
                this.jj_consume_token(14);
                return new JByte();
            }
            case 15: {
                this.jj_consume_token(15);
                return new JBoolean();
            }
            case 16: {
                this.jj_consume_token(16);
                return new JInt();
            }
            case 17: {
                this.jj_consume_token(17);
                return new JLong();
            }
            case 18: {
                this.jj_consume_token(18);
                return new JFloat();
            }
            case 19: {
                this.jj_consume_token(19);
                return new JDouble();
            }
            case 20: {
                this.jj_consume_token(20);
                return new JString();
            }
            case 21: {
                this.jj_consume_token(21);
                return new JBuffer();
            }
            case 32: {
                String rname = this.ModuleName();
                if (rname.indexOf(46, 0) < 0) {
                    rname = Rcc.curModuleName + "." + rname;
                }
                final JRecord r = Rcc.recTab.get(rname);
                if (r == null) {
                    System.out.println("Type " + rname + " not known. Exiting.");
                    System.exit(1);
                }
                return r;
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
    }
    
    public final JMap Map() throws ParseException {
        this.jj_consume_token(23);
        this.jj_consume_token(26);
        final JType jt1 = this.Type();
        this.jj_consume_token(29);
        final JType jt2 = this.Type();
        this.jj_consume_token(27);
        return new JMap(jt1, jt2);
    }
    
    public final JVector Vector() throws ParseException {
        this.jj_consume_token(22);
        this.jj_consume_token(26);
        final JType jt = this.Type();
        this.jj_consume_token(27);
        return new JVector(jt);
    }
    
    private static void jj_la1_0() {
        Rcc.jj_la1_0 = new int[] { 10240, 10240, 1073741824, 4096, 16760832, 16760832 };
    }
    
    private static void jj_la1_1() {
        Rcc.jj_la1_1 = new int[] { 0, 0, 0, 0, 1, 1 };
    }
    
    public Rcc(final InputStream stream) {
        this(stream, null);
    }
    
    public Rcc(final InputStream stream, final String encoding) {
        this.jj_la1 = new int[6];
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new RccTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
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
    }
    
    public Rcc(final Reader stream) {
        this.jj_la1 = new int[6];
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new RccTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
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
    }
    
    public Rcc(final RccTokenManager tm) {
        this.jj_la1 = new int[6];
        this.jj_expentries = new Vector();
        this.jj_kind = -1;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
    }
    
    public void ReInit(final RccTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 6; ++i) {
            this.jj_la1[i] = -1;
        }
    }
    
    private final Token jj_consume_token(final int kind) throws ParseException {
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
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
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
    
    private final int jj_ntk() {
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
    
    public ParseException generateParseException() {
        this.jj_expentries.removeAllElements();
        final boolean[] la1tokens = new boolean[33];
        for (int i = 0; i < 33; ++i) {
            la1tokens[i] = false;
        }
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 6; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((Rcc.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((Rcc.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 33; ++i) {
            if (la1tokens[i]) {
                (this.jj_expentry = new int[1])[0] = i;
                this.jj_expentries.addElement(this.jj_expentry);
            }
        }
        final int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int k = 0; k < this.jj_expentries.size(); ++k) {
            exptokseq[k] = this.jj_expentries.elementAt(k);
        }
        return new ParseException(this.token, exptokseq, Rcc.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    static {
        Rcc.recTab = new Hashtable();
        Rcc.curDir = System.getProperty("user.dir");
        jj_la1_0();
        jj_la1_1();
    }
}
