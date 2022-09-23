// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;
import java.util.List;

public class thrift_grammar implements thrift_grammarTreeConstants, thrift_grammarConstants
{
    protected JJTthrift_grammarState jjtree;
    private List<String> include_path;
    private int field_val;
    protected Map<String, DynamicSerDeSimpleNode> types;
    protected Map<String, DynamicSerDeSimpleNode> tables;
    private static final String[] default_include_path;
    public thrift_grammarTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private static int[] jj_la1_2;
    private final List jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    
    protected thrift_grammar(final InputStream is, final List<String> include_path, final boolean junk) {
        this(is, null);
        this.types = new HashMap<String, DynamicSerDeSimpleNode>();
        this.tables = new HashMap<String, DynamicSerDeSimpleNode>();
        this.include_path = include_path;
        this.field_val = -1;
    }
    
    private static File findFile(final String fname, final List<String> include_path) {
        for (final String path : include_path) {
            final String full = path + "/" + fname;
            final File f = new File(full);
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }
    
    public static void main(final String[] args) {
        String filename = null;
        final List<String> include_path = new ArrayList<String>();
        for (final String path : thrift_grammar.default_include_path) {
            include_path.add(path);
        }
        for (int i = 0; i < args.length; ++i) {
            final String arg = args[i];
            if (arg.equals("--include") && i + 1 < args.length) {
                include_path.add(args[++i]);
            }
            if (arg.equals("--file") && i + 1 < args.length) {
                filename = args[++i];
            }
        }
        InputStream is = System.in;
        if (filename != null) {
            try {
                is = new FileInputStream(findFile(filename, include_path));
            }
            catch (IOException ex) {}
        }
        final thrift_grammar t = new thrift_grammar(is, include_path, false);
        try {
            t.Start();
        }
        catch (Exception e) {
            System.out.println("Parse error.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    public final SimpleNode Start() throws ParseException {
        final DynamicSerDeStart jjtn000 = new DynamicSerDeStart(0);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.HeaderList();
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 59:
                    case 60: {
                        this.CommaOrSemicolon();
                        break;
                    }
                    default: {
                        this.jj_la1[0] = this.jj_gen;
                        break;
                    }
                }
                this.Definition();
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 8:
                    case 37:
                    case 42:
                    case 43:
                    case 44:
                    case 47:
                    case 48:
                    case 59:
                    case 60: {
                        continue;
                    }
                    default: {
                        this.jj_la1[1] = this.jj_gen;
                        this.jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                    }
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode HeaderList() throws ParseException {
        final DynamicSerDeHeaderList jjtn000 = new DynamicSerDeHeaderList(1);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 9:
                    case 10:
                    case 11:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 25:
                    case 27: {
                        this.Header();
                        continue;
                    }
                    default: {
                        this.jj_la1[2] = this.jj_gen;
                        this.jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                    }
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Header() throws ParseException {
        final DynamicSerDeHeader jjtn000 = new DynamicSerDeHeader(2);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 27: {
                    this.Include();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 9:
                case 10:
                case 11:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 25: {
                    this.Namespace();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Namespace() throws ParseException {
        final DynamicSerDeNamespace jjtn000 = new DynamicSerDeNamespace(3);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    this.jj_consume_token(9);
                    this.jj_consume_token(54);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 10: {
                    this.jj_consume_token(10);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 11: {
                    this.jj_consume_token(11);
                    this.jj_consume_token(57);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 16: {
                    this.jj_consume_token(16);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 17: {
                    this.jj_consume_token(17);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 18: {
                    this.jj_consume_token(18);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 19: {
                    this.jj_consume_token(19);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 20: {
                    this.jj_consume_token(20);
                    this.jj_consume_token(58);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 21: {
                    this.jj_consume_token(21);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 13: {
                    this.jj_consume_token(13);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 14: {
                    this.jj_consume_token(14);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 25: {
                    this.jj_consume_token(25);
                    this.jj_consume_token(57);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 15: {
                    this.jj_consume_token(15);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Include() throws ParseException {
        final DynamicSerDeInclude jjtn000 = new DynamicSerDeInclude(4);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        boolean found = false;
        try {
            this.jj_consume_token(27);
            String fname = this.jj_consume_token(57).image;
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            fname = fname.substring(1, fname.length() - 1);
            final File f = findFile(fname, this.include_path);
            if (f != null) {
                found = true;
                try {
                    final FileInputStream fis = new FileInputStream(f);
                    final thrift_grammar t = new thrift_grammar(fis, this.include_path, false);
                    t.Start();
                    fis.close();
                    found = true;
                    this.tables.putAll(t.tables);
                    this.types.putAll(t.types);
                }
                catch (Exception e) {
                    System.out.println("File: " + fname + " - Oops.");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
            if (!found) {
                throw new RuntimeException("include file not found: " + fname);
            }
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Definition() throws ParseException {
        final DynamicSerDeDefinition jjtn000 = new DynamicSerDeDefinition(5);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    this.Const();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 47: {
                    this.Service();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 37:
                case 42:
                case 43:
                case 44:
                case 48: {
                    this.TypeDefinition();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode TypeDefinition() throws ParseException {
        final DynamicSerDeTypeDefinition jjtn000 = new DynamicSerDeTypeDefinition(6);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 42: {
                    this.Typedef();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 48: {
                    this.Enum();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 37: {
                    this.Senum();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 43: {
                    this.Struct();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 44: {
                    this.Xception();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[6] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypedef Typedef() throws ParseException {
        final DynamicSerDeTypedef jjtn000 = new DynamicSerDeTypedef(7);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(42);
            this.DefinitionType();
            jjtn000.name = this.jj_consume_token(54).image;
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            this.types.put(jjtn000.name, jjtn000);
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final void CommaOrSemicolon() throws ParseException {
        final DynamicSerDeCommaOrSemicolon jjtn000 = new DynamicSerDeCommaOrSemicolon(8);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 59: {
                    this.jj_consume_token(59);
                    break;
                }
                case 60: {
                    this.jj_consume_token(60);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
                default: {
                    this.jj_la1[7] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Enum() throws ParseException {
        final DynamicSerDeEnum jjtn000 = new DynamicSerDeEnum(9);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(48);
            this.jj_consume_token(54);
            this.jj_consume_token(61);
            this.EnumDefList();
            this.jj_consume_token(62);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode EnumDefList() throws ParseException {
        final DynamicSerDeEnumDefList jjtn000 = new DynamicSerDeEnumDefList(10);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            while (true) {
                this.EnumDef();
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 54: {
                        continue;
                    }
                    default: {
                        this.jj_la1[8] = this.jj_gen;
                        this.jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                    }
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode EnumDef() throws ParseException {
        final DynamicSerDeEnumDef jjtn000 = new DynamicSerDeEnumDef(11);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(54);
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 63: {
                    this.jj_consume_token(63);
                    this.jj_consume_token(52);
                    break;
                }
                default: {
                    this.jj_la1[9] = this.jj_gen;
                    break;
                }
            }
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 59:
                case 60: {
                    this.CommaOrSemicolon();
                    break;
                }
                default: {
                    this.jj_la1[10] = this.jj_gen;
                    break;
                }
            }
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Senum() throws ParseException {
        final DynamicSerDeSenum jjtn000 = new DynamicSerDeSenum(12);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(37);
            this.jj_consume_token(54);
            this.jj_consume_token(61);
            this.SenumDefList();
            this.jj_consume_token(62);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode SenumDefList() throws ParseException {
        final DynamicSerDeSenumDefList jjtn000 = new DynamicSerDeSenumDefList(13);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            while (true) {
                this.SenumDef();
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 57: {
                        continue;
                    }
                    default: {
                        this.jj_la1[11] = this.jj_gen;
                        this.jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                    }
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode SenumDef() throws ParseException {
        final DynamicSerDeSenumDef jjtn000 = new DynamicSerDeSenumDef(14);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(57);
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 59:
                case 60: {
                    this.CommaOrSemicolon();
                    break;
                }
                default: {
                    this.jj_la1[12] = this.jj_gen;
                    break;
                }
            }
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Const() throws ParseException {
        final DynamicSerDeConst jjtn000 = new DynamicSerDeConst(15);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(8);
            this.FieldType();
            this.jj_consume_token(54);
            this.jj_consume_token(63);
            this.ConstValue();
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 59:
                case 60: {
                    this.CommaOrSemicolon();
                    break;
                }
                default: {
                    this.jj_la1[13] = this.jj_gen;
                    break;
                }
            }
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode ConstValue() throws ParseException {
        final DynamicSerDeConstValue jjtn000 = new DynamicSerDeConstValue(16);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 52: {
                    this.jj_consume_token(52);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
                case 53: {
                    this.jj_consume_token(53);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
                case 57: {
                    this.jj_consume_token(57);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
                case 54: {
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
                case 64: {
                    this.ConstList();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
                case 61: {
                    this.ConstMap();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[14] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
        throw new Error("Missing return statement in function");
    }
    
    public final SimpleNode ConstList() throws ParseException {
        final DynamicSerDeConstList jjtn000 = new DynamicSerDeConstList(17);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(64);
            this.ConstListContents();
            this.jj_consume_token(65);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode ConstListContents() throws ParseException {
        final DynamicSerDeConstListContents jjtn000 = new DynamicSerDeConstListContents(18);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            while (true) {
                this.ConstValue();
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 59:
                    case 60: {
                        this.CommaOrSemicolon();
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                        break;
                    }
                }
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 52:
                    case 53:
                    case 54:
                    case 57:
                    case 61:
                    case 64: {
                        continue;
                    }
                    default: {
                        this.jj_la1[16] = this.jj_gen;
                        this.jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                    }
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode ConstMap() throws ParseException {
        final DynamicSerDeConstMap jjtn000 = new DynamicSerDeConstMap(19);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(61);
            this.ConstMapContents();
            this.jj_consume_token(62);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode ConstMapContents() throws ParseException {
        final DynamicSerDeConstMapContents jjtn000 = new DynamicSerDeConstMapContents(20);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        Label_0431: {
            try {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 52:
                    case 53:
                    case 54:
                    case 57:
                    case 61:
                    case 64: {
                        while (true) {
                            this.ConstValue();
                            this.jj_consume_token(66);
                            this.ConstValue();
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 59:
                                case 60: {
                                    this.CommaOrSemicolon();
                                    break;
                                }
                                default: {
                                    this.jj_la1[17] = this.jj_gen;
                                    break;
                                }
                            }
                            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                                case 52:
                                case 53:
                                case 54:
                                case 57:
                                case 61:
                                case 64: {
                                    continue;
                                }
                                default: {
                                    this.jj_la1[18] = this.jj_gen;
                                    this.jjtree.closeNodeScope(jjtn000, true);
                                    jjtc000 = false;
                                    break Label_0431;
                                }
                            }
                        }
                        break;
                    }
                    default: {
                        this.jj_la1[19] = this.jj_gen;
                        this.jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                    }
                }
            }
            catch (Throwable jjte000) {
                if (jjtc000) {
                    this.jjtree.clearNodeScope(jjtn000);
                    jjtc000 = false;
                }
                else {
                    this.jjtree.popNode();
                }
                if (jjte000 instanceof RuntimeException) {
                    throw (RuntimeException)jjte000;
                }
                if (jjte000 instanceof ParseException) {
                    throw (ParseException)jjte000;
                }
                throw (Error)jjte000;
            }
            finally {
                if (jjtc000) {
                    this.jjtree.closeNodeScope(jjtn000, true);
                }
            }
        }
        throw new Error("Missing return statement in function");
    }
    
    public final DynamicSerDeStruct Struct() throws ParseException {
        final DynamicSerDeStruct jjtn000 = new DynamicSerDeStruct(21);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(43);
            jjtn000.name = this.jj_consume_token(54).image;
            this.jj_consume_token(61);
            this.FieldList();
            this.jj_consume_token(62);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            this.types.put(jjtn000.name, jjtn000);
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Xception() throws ParseException {
        final DynamicSerDeXception jjtn000 = new DynamicSerDeXception(22);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(44);
            this.jj_consume_token(54);
            this.jj_consume_token(61);
            this.FieldList();
            this.jj_consume_token(62);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Service() throws ParseException {
        final DynamicSerDeService jjtn000 = new DynamicSerDeService(23);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(47);
            this.jj_consume_token(54);
            this.Extends();
            this.jj_consume_token(61);
            this.FlagArgs();
            while (true) {
                this.Function();
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 28:
                    case 29:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 54: {
                        continue;
                    }
                    default: {
                        this.jj_la1[20] = this.jj_gen;
                        this.UnflagArgs();
                        this.jj_consume_token(62);
                        this.jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                    }
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode FlagArgs() throws ParseException {
        final DynamicSerDeFlagArgs jjtn000 = new DynamicSerDeFlagArgs(24);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode UnflagArgs() throws ParseException {
        final DynamicSerDeUnflagArgs jjtn000 = new DynamicSerDeUnflagArgs(25);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode Extends() throws ParseException {
        final DynamicSerDeExtends jjtn000 = new DynamicSerDeExtends(26);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 45: {
                    this.jj_consume_token(45);
                    this.jj_consume_token(54);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[21] = this.jj_gen;
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeFunction Function() throws ParseException {
        final DynamicSerDeFunction jjtn000 = new DynamicSerDeFunction(27);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.Async();
            this.FunctionType();
            jjtn000.name = this.jj_consume_token(54).image;
            this.jj_consume_token(67);
            this.FieldList();
            this.jj_consume_token(68);
            this.Throws();
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 59:
                case 60: {
                    this.CommaOrSemicolon();
                    break;
                }
                default: {
                    this.jj_la1[22] = this.jj_gen;
                    break;
                }
            }
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            this.tables.put(jjtn000.name, jjtn000);
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final void Async() throws ParseException {
        final DynamicSerDeAsync jjtn000 = new DynamicSerDeAsync(28);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 41: {
                    this.jj_consume_token(41);
                    break;
                }
                default: {
                    this.jj_la1[23] = this.jj_gen;
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final void Throws() throws ParseException {
        final DynamicSerDeThrows jjtn000 = new DynamicSerDeThrows(29);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 46: {
                    this.jj_consume_token(46);
                    this.jj_consume_token(67);
                    this.FieldList();
                    this.jj_consume_token(68);
                    break;
                }
                default: {
                    this.jj_la1[24] = this.jj_gen;
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeFieldList FieldList() throws ParseException {
        final DynamicSerDeFieldList jjtn000 = new DynamicSerDeFieldList(30);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        this.field_val = -1;
        try {
            while (true) {
                switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                    case 29:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 38:
                    case 39:
                    case 40:
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 54: {
                        this.Field();
                        continue;
                    }
                    default: {
                        this.jj_la1[25] = this.jj_gen;
                        this.jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        return jjtn000;
                    }
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeField Field() throws ParseException {
        final DynamicSerDeField jjtn000 = new DynamicSerDeField(31);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        String fidnum = "";
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 52: {
                    fidnum = this.jj_consume_token(52).image;
                    this.jj_consume_token(66);
                    break;
                }
                default: {
                    this.jj_la1[26] = this.jj_gen;
                    break;
                }
            }
            this.FieldRequiredness();
            this.FieldType();
            jjtn000.name = this.jj_consume_token(54).image;
            this.FieldValue();
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 59:
                case 60: {
                    this.CommaOrSemicolon();
                    break;
                }
                default: {
                    this.jj_la1[27] = this.jj_gen;
                    break;
                }
            }
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            if (fidnum.length() > 0) {
                final int fidInt = Integer.valueOf(fidnum);
                jjtn000.fieldid = fidInt;
            }
            else {
                jjtn000.fieldid = this.field_val--;
            }
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeFieldRequiredness FieldRequiredness() throws ParseException {
        final DynamicSerDeFieldRequiredness jjtn000 = new DynamicSerDeFieldRequiredness(32);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 49: {
                    this.jj_consume_token(49);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    jjtn000.requiredness = DynamicSerDeFieldRequiredness.RequirednessTypes.Required;
                    return jjtn000;
                }
                case 50: {
                    this.jj_consume_token(50);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    jjtn000.requiredness = DynamicSerDeFieldRequiredness.RequirednessTypes.Optional;
                    return jjtn000;
                }
                case 51: {
                    this.jj_consume_token(51);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    jjtn000.requiredness = DynamicSerDeFieldRequiredness.RequirednessTypes.Skippable;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[28] = this.jj_gen;
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
            }
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode FieldValue() throws ParseException {
        final DynamicSerDeFieldValue jjtn000 = new DynamicSerDeFieldValue(33);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 63: {
                    this.jj_consume_token(63);
                    this.ConstValue();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[29] = this.jj_gen;
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final SimpleNode DefinitionType() throws ParseException {
        final DynamicSerDeDefinitionType jjtn000 = new DynamicSerDeDefinitionType(34);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 35: {
                    this.TypeString();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 29: {
                    this.TypeBool();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 31: {
                    this.Typei16();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 32: {
                    this.Typei32();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 33: {
                    this.Typei64();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 34: {
                    this.TypeDouble();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 38: {
                    this.TypeMap();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 40: {
                    this.TypeSet();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 39: {
                    this.TypeList();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                default: {
                    this.jj_la1[30] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final void FunctionType() throws ParseException {
        final DynamicSerDeFunctionType jjtn000 = new DynamicSerDeFunctionType(35);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 29:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 38:
                case 39:
                case 40:
                case 54: {
                    this.FieldType();
                    break;
                }
                case 28: {
                    this.jj_consume_token(28);
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    break;
                }
                default: {
                    this.jj_la1[31] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeFieldType FieldType() throws ParseException {
        final DynamicSerDeFieldType jjtn000 = new DynamicSerDeFieldType(36);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
                case 35: {
                    this.TypeString();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 29: {
                    this.TypeBool();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 31: {
                    this.Typei16();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 32: {
                    this.Typei32();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 33: {
                    this.Typei64();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 34: {
                    this.TypeDouble();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 38: {
                    this.TypeMap();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 40: {
                    this.TypeSet();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 39: {
                    this.TypeList();
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    return jjtn000;
                }
                case 54: {
                    jjtn000.name = this.jj_consume_token(54).image;
                    this.jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    if (this.types.get(jjtn000.name) == null) {
                        System.err.println("ERROR: DDL specifying type " + jjtn000.name + " which has not been defined");
                        throw new RuntimeException("specifying type " + jjtn000.name + " which has not been defined");
                    }
                    jjtn000.jjtAddChild(this.types.get(jjtn000.name), 0);
                    return jjtn000;
                }
                default: {
                    this.jj_la1[32] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypeString TypeString() throws ParseException {
        final DynamicSerDeTypeString jjtn000 = new DynamicSerDeTypeString(37);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(35);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypeByte TypeByte() throws ParseException {
        final DynamicSerDeTypeByte jjtn000 = new DynamicSerDeTypeByte(38);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(30);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypei16 Typei16() throws ParseException {
        final DynamicSerDeTypei16 jjtn000 = new DynamicSerDeTypei16(39);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(31);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypei32 Typei32() throws ParseException {
        final DynamicSerDeTypei32 jjtn000 = new DynamicSerDeTypei32(40);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(32);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypei64 Typei64() throws ParseException {
        final DynamicSerDeTypei64 jjtn000 = new DynamicSerDeTypei64(41);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(33);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypeDouble TypeDouble() throws ParseException {
        final DynamicSerDeTypeDouble jjtn000 = new DynamicSerDeTypeDouble(42);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(34);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypeBool TypeBool() throws ParseException {
        final DynamicSerDeTypeBool jjtn000 = new DynamicSerDeTypeBool(43);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(29);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypeMap TypeMap() throws ParseException {
        final DynamicSerDeTypeMap jjtn000 = new DynamicSerDeTypeMap(44);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(38);
            this.jj_consume_token(69);
            this.FieldType();
            this.jj_consume_token(59);
            this.FieldType();
            this.jj_consume_token(70);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypeSet TypeSet() throws ParseException {
        final DynamicSerDeTypeSet jjtn000 = new DynamicSerDeTypeSet(45);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(40);
            this.jj_consume_token(69);
            this.FieldType();
            this.jj_consume_token(70);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    public final DynamicSerDeTypeList TypeList() throws ParseException {
        final DynamicSerDeTypeList jjtn000 = new DynamicSerDeTypeList(46);
        boolean jjtc000 = true;
        this.jjtree.openNodeScope(jjtn000);
        try {
            this.jj_consume_token(39);
            this.jj_consume_token(69);
            this.FieldType();
            this.jj_consume_token(70);
            this.jjtree.closeNodeScope(jjtn000, true);
            jjtc000 = false;
            return jjtn000;
        }
        catch (Throwable jjte000) {
            if (jjtc000) {
                this.jjtree.clearNodeScope(jjtn000);
                jjtc000 = false;
            }
            else {
                this.jjtree.popNode();
            }
            if (jjte000 instanceof RuntimeException) {
                throw (RuntimeException)jjte000;
            }
            if (jjte000 instanceof ParseException) {
                throw (ParseException)jjte000;
            }
            throw (Error)jjte000;
        }
        finally {
            if (jjtc000) {
                this.jjtree.closeNodeScope(jjtn000, true);
            }
        }
    }
    
    private static void jj_la1_init_0() {
        thrift_grammar.jj_la1_0 = new int[] { 0, 256, 171961856, 171961856, 37744128, 256, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1342177280, 0, 0, 0, 0, -1610612736, 0, 0, 0, 0, -1610612736, -1342177280, -1610612736 };
    }
    
    private static void jj_la1_init_1() {
        thrift_grammar.jj_la1_1 = new int[] { 402653184, 402758688, 0, 0, 0, 105504, 72736, 402653184, 4194304, Integer.MIN_VALUE, 402653184, 33554432, 402653184, 402653184, 577765376, 402653184, 577765376, 402653184, 577765376, 577765376, 4195279, 8192, 402653184, 512, 16384, 6160847, 1048576, 402653184, 917504, Integer.MIN_VALUE, 463, 4194767, 4194767 };
    }
    
    private static void jj_la1_init_2() {
        thrift_grammar.jj_la1_2 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
    
    public thrift_grammar(final InputStream stream) {
        this(stream, null);
    }
    
    public thrift_grammar(final InputStream stream, final String encoding) {
        this.jjtree = new JJTthrift_grammarState();
        this.include_path = null;
        this.jj_la1 = new int[33];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        try {
            this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.token_source = new thrift_grammarTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 33; ++i) {
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
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 33; ++i) {
            this.jj_la1[i] = -1;
        }
    }
    
    public thrift_grammar(final Reader stream) {
        this.jjtree = new JJTthrift_grammarState();
        this.include_path = null;
        this.jj_la1 = new int[33];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
        this.token_source = new thrift_grammarTokenManager(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 33; ++i) {
            this.jj_la1[i] = -1;
        }
    }
    
    public void ReInit(final Reader stream) {
        this.jj_input_stream.ReInit(stream, 1, 1);
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 33; ++i) {
            this.jj_la1[i] = -1;
        }
    }
    
    public thrift_grammar(final thrift_grammarTokenManager tm) {
        this.jjtree = new JJTthrift_grammarState();
        this.include_path = null;
        this.jj_la1 = new int[33];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (int i = 0; i < 33; ++i) {
            this.jj_la1[i] = -1;
        }
    }
    
    public void ReInit(final thrift_grammarTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jjtree.reset();
        this.jj_gen = 0;
        for (int i = 0; i < 33; ++i) {
            this.jj_la1[i] = -1;
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
    
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        final boolean[] la1tokens = new boolean[71];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 33; ++i) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; ++j) {
                    if ((thrift_grammar.jj_la1_0[i] & 1 << j) != 0x0) {
                        la1tokens[j] = true;
                    }
                    if ((thrift_grammar.jj_la1_1[i] & 1 << j) != 0x0) {
                        la1tokens[32 + j] = true;
                    }
                    if ((thrift_grammar.jj_la1_2[i] & 1 << j) != 0x0) {
                        la1tokens[64 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 71; ++i) {
            if (la1tokens[i]) {
                (this.jj_expentry = new int[1])[0] = i;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        final int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int k = 0; k < this.jj_expentries.size(); ++k) {
            exptokseq[k] = this.jj_expentries.get(k);
        }
        return new ParseException(this.token, exptokseq, thrift_grammar.tokenImage);
    }
    
    public final void enable_tracing() {
    }
    
    public final void disable_tracing() {
    }
    
    static {
        default_include_path = new String[] { "/usr/local/include", "/usr/include", "/usr/local/include/thrift/if", "/usr/local/include/fb303/if" };
        jj_la1_init_0();
        jj_la1_init_1();
        jj_la1_init_2();
    }
}
