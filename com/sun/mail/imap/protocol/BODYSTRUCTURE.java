// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.imap.protocol;

import com.sun.mail.iap.Response;
import java.util.Vector;
import com.sun.mail.iap.ParsingException;
import javax.mail.internet.ParameterList;

public class BODYSTRUCTURE implements Item
{
    static final char[] name;
    public int msgno;
    public String type;
    public String subtype;
    public String encoding;
    public int lines;
    public int size;
    public String disposition;
    public String id;
    public String description;
    public String md5;
    public String attachment;
    public ParameterList cParams;
    public ParameterList dParams;
    public String[] language;
    public BODYSTRUCTURE[] bodies;
    public ENVELOPE envelope;
    private static int SINGLE;
    private static int MULTI;
    private static int NESTED;
    private int processedType;
    private static boolean parseDebug;
    
    public BODYSTRUCTURE(final FetchResponse r) throws ParsingException {
        this.lines = -1;
        this.size = -1;
        if (BODYSTRUCTURE.parseDebug) {
            System.out.println("DEBUG IMAP: parsing BODYSTRUCTURE");
        }
        this.msgno = r.getNumber();
        if (BODYSTRUCTURE.parseDebug) {
            System.out.println("DEBUG IMAP: msgno " + this.msgno);
        }
        r.skipSpaces();
        if (r.readByte() != 40) {
            throw new ParsingException("BODYSTRUCTURE parse error: missing ``('' at start");
        }
        if (r.peekByte() == 40) {
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: parsing multipart");
            }
            this.type = "multipart";
            this.processedType = BODYSTRUCTURE.MULTI;
            final Vector v = new Vector(1);
            final int i = 1;
            do {
                v.addElement(new BODYSTRUCTURE(r));
                r.skipSpaces();
            } while (r.peekByte() == 40);
            v.copyInto(this.bodies = new BODYSTRUCTURE[v.size()]);
            this.subtype = r.readString();
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: subtype " + this.subtype);
            }
            if (r.readByte() == 41) {
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: parse DONE");
                }
                return;
            }
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: parsing extension data");
            }
            this.cParams = this.parseParameters(r);
            if (r.readByte() == 41) {
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: body parameters DONE");
                }
                return;
            }
            byte b = r.readByte();
            if (b == 40) {
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: parse disposition");
                }
                this.disposition = r.readString();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: disposition " + this.disposition);
                }
                this.dParams = this.parseParameters(r);
                if (r.readByte() != 41) {
                    throw new ParsingException("BODYSTRUCTURE parse error: missing ``)'' at end of disposition in multipart");
                }
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: disposition DONE");
                }
            }
            else {
                if (b != 78 && b != 110) {
                    throw new ParsingException("BODYSTRUCTURE parse error: " + this.type + "/" + this.subtype + ": " + "bad multipart disposition, b " + b);
                }
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: disposition NIL");
                }
                r.skip(2);
            }
            if ((b = r.readByte()) == 41) {
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: no body-fld-lang");
                }
                return;
            }
            if (b != 32) {
                throw new ParsingException("BODYSTRUCTURE parse error: missing space after disposition");
            }
            if (r.peekByte() == 40) {
                this.language = r.readStringList();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: language len " + this.language.length);
                }
            }
            else {
                final String l = r.readString();
                if (l != null) {
                    final String[] la = { l };
                    this.language = la;
                    if (BODYSTRUCTURE.parseDebug) {
                        System.out.println("DEBUG IMAP: language " + l);
                    }
                }
            }
            while (r.readByte() == 32) {
                this.parseBodyExtension(r);
            }
        }
        else {
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: single part");
            }
            this.type = r.readString();
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: type " + this.type);
            }
            this.processedType = BODYSTRUCTURE.SINGLE;
            this.subtype = r.readString();
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: subtype " + this.subtype);
            }
            if (this.type == null) {
                this.type = "application";
                this.subtype = "octet-stream";
            }
            this.cParams = this.parseParameters(r);
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: cParams " + this.cParams);
            }
            this.id = r.readString();
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: id " + this.id);
            }
            this.description = r.readString();
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: description " + this.description);
            }
            this.encoding = r.readString();
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: encoding " + this.encoding);
            }
            this.size = r.readNumber();
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: size " + this.size);
            }
            if (this.size < 0) {
                throw new ParsingException("BODYSTRUCTURE parse error: bad ``size'' element");
            }
            if (this.type.equalsIgnoreCase("text")) {
                this.lines = r.readNumber();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: lines " + this.lines);
                }
                if (this.lines < 0) {
                    throw new ParsingException("BODYSTRUCTURE parse error: bad ``lines'' element");
                }
            }
            else if (this.type.equalsIgnoreCase("message") && this.subtype.equalsIgnoreCase("rfc822")) {
                this.processedType = BODYSTRUCTURE.NESTED;
                this.envelope = new ENVELOPE(r);
                final BODYSTRUCTURE[] bs = { new BODYSTRUCTURE(r) };
                this.bodies = bs;
                this.lines = r.readNumber();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: lines " + this.lines);
                }
                if (this.lines < 0) {
                    throw new ParsingException("BODYSTRUCTURE parse error: bad ``lines'' element");
                }
            }
            else {
                r.skipSpaces();
                final byte bn = r.peekByte();
                if (Character.isDigit((char)bn)) {
                    throw new ParsingException("BODYSTRUCTURE parse error: server erroneously included ``lines'' element with type " + this.type + "/" + this.subtype);
                }
            }
            if (r.peekByte() == 41) {
                r.readByte();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: parse DONE");
                }
                return;
            }
            this.md5 = r.readString();
            if (r.readByte() == 41) {
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: no MD5 DONE");
                }
                return;
            }
            final byte b2 = r.readByte();
            if (b2 == 40) {
                this.disposition = r.readString();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: disposition " + this.disposition);
                }
                this.dParams = this.parseParameters(r);
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: dParams " + this.dParams);
                }
                if (r.readByte() != 41) {
                    throw new ParsingException("BODYSTRUCTURE parse error: missing ``)'' at end of disposition");
                }
            }
            else {
                if (b2 != 78 && b2 != 110) {
                    throw new ParsingException("BODYSTRUCTURE parse error: " + this.type + "/" + this.subtype + ": " + "bad single part disposition, b " + b2);
                }
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: disposition NIL");
                }
                r.skip(2);
            }
            if (r.readByte() == 41) {
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: disposition DONE");
                }
                return;
            }
            if (r.peekByte() == 40) {
                this.language = r.readStringList();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: language len " + this.language.length);
                }
            }
            else {
                final String j = r.readString();
                if (j != null) {
                    final String[] la2 = { j };
                    this.language = la2;
                    if (BODYSTRUCTURE.parseDebug) {
                        System.out.println("DEBUG IMAP: language " + j);
                    }
                }
            }
            while (r.readByte() == 32) {
                this.parseBodyExtension(r);
            }
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: all DONE");
            }
        }
    }
    
    public boolean isMulti() {
        return this.processedType == BODYSTRUCTURE.MULTI;
    }
    
    public boolean isSingle() {
        return this.processedType == BODYSTRUCTURE.SINGLE;
    }
    
    public boolean isNested() {
        return this.processedType == BODYSTRUCTURE.NESTED;
    }
    
    private ParameterList parseParameters(final Response r) throws ParsingException {
        r.skipSpaces();
        ParameterList list = null;
        final byte b = r.readByte();
        if (b == 40) {
            list = new ParameterList();
            do {
                final String name = r.readString();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: parameter name " + name);
                }
                if (name == null) {
                    throw new ParsingException("BODYSTRUCTURE parse error: " + this.type + "/" + this.subtype + ": " + "null name in parameter list");
                }
                final String value = r.readString();
                if (BODYSTRUCTURE.parseDebug) {
                    System.out.println("DEBUG IMAP: parameter value " + value);
                }
                list.set(name, value);
            } while (r.readByte() != 41);
            list.set(null, "DONE");
        }
        else {
            if (b != 78 && b != 110) {
                throw new ParsingException("Parameter list parse error");
            }
            if (BODYSTRUCTURE.parseDebug) {
                System.out.println("DEBUG IMAP: parameter list NIL");
            }
            r.skip(2);
        }
        return list;
    }
    
    private void parseBodyExtension(final Response r) throws ParsingException {
        r.skipSpaces();
        final byte b = r.peekByte();
        if (b == 40) {
            r.skip(1);
            do {
                this.parseBodyExtension(r);
            } while (r.readByte() != 41);
        }
        else if (Character.isDigit((char)b)) {
            r.readNumber();
        }
        else {
            r.readString();
        }
    }
    
    static {
        name = new char[] { 'B', 'O', 'D', 'Y', 'S', 'T', 'R', 'U', 'C', 'T', 'U', 'R', 'E' };
        BODYSTRUCTURE.SINGLE = 1;
        BODYSTRUCTURE.MULTI = 2;
        BODYSTRUCTURE.NESTED = 3;
        BODYSTRUCTURE.parseDebug = false;
        try {
            final String s = System.getProperty("mail.imap.parse.debug");
            BODYSTRUCTURE.parseDebug = (s != null && s.equalsIgnoreCase("true"));
        }
        catch (SecurityException ex) {}
    }
}
