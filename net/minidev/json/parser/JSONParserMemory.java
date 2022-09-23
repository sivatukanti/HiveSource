// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.parser;

import java.io.IOException;

abstract class JSONParserMemory extends JSONParserBase
{
    protected int len;
    
    public JSONParserMemory(final int permissiveMode) {
        super(permissiveMode);
    }
    
    @Override
    protected void readNQString(final boolean[] stop) throws IOException {
        final int start = this.pos;
        this.skipNQString(stop);
        this.extractStringTrim(start, this.pos);
    }
    
    @Override
    protected Object readNumber(final boolean[] stop) throws ParseException, IOException {
        final int start = this.pos;
        this.read();
        this.skipDigits();
        if (this.c != '.' && this.c != 'E' && this.c != 'e') {
            this.skipSpace();
            if (this.c < '\0' || this.c >= '~' || stop[this.c] || this.c == '\u001a') {
                this.extractStringTrim(start, this.pos);
                return this.parseNumber(this.xs);
            }
            this.skipNQString(stop);
            this.extractStringTrim(start, this.pos);
            if (!this.acceptNonQuote) {
                throw new ParseException(this.pos, 1, this.xs);
            }
            return this.xs;
        }
        else {
            if (this.c == '.') {
                this.read();
                this.skipDigits();
            }
            if (this.c != 'E' && this.c != 'e') {
                this.skipSpace();
                if (this.c < '\0' || this.c >= '~' || stop[this.c] || this.c == '\u001a') {
                    this.extractStringTrim(start, this.pos);
                    return this.extractFloat();
                }
                this.skipNQString(stop);
                this.extractStringTrim(start, this.pos);
                if (!this.acceptNonQuote) {
                    throw new ParseException(this.pos, 1, this.xs);
                }
                return this.xs;
            }
            else {
                this.sb.append('E');
                this.read();
                if (this.c == '+' || this.c == '-' || (this.c >= '0' && this.c <= '9')) {
                    this.sb.append(this.c);
                    this.read();
                    this.skipDigits();
                    this.skipSpace();
                    if (this.c < '\0' || this.c >= '~' || stop[this.c] || this.c == '\u001a') {
                        this.extractStringTrim(start, this.pos);
                        return this.extractFloat();
                    }
                    this.skipNQString(stop);
                    this.extractStringTrim(start, this.pos);
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                else {
                    this.skipNQString(stop);
                    this.extractStringTrim(start, this.pos);
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    if (!this.acceptLeadinZero) {
                        this.checkLeadinZero();
                    }
                    return this.xs;
                }
            }
        }
    }
    
    @Override
    protected void readString() throws ParseException, IOException {
        if (!this.acceptSimpleQuote && this.c == '\'') {
            if (this.acceptNonQuote) {
                this.readNQString(JSONParserMemory.stopAll);
                return;
            }
            throw new ParseException(this.pos, 0, this.c);
        }
        else {
            final int tmpP = this.indexOf(this.c, this.pos + 1);
            if (tmpP == -1) {
                throw new ParseException(this.len, 3, null);
            }
            this.extractString(this.pos + 1, tmpP);
            if (this.xs.indexOf(92) == -1) {
                this.checkControleChar();
                this.pos = tmpP;
                this.read();
                return;
            }
            this.sb.clear();
            this.readString2();
        }
    }
    
    protected abstract void extractString(final int p0, final int p1);
    
    protected abstract int indexOf(final char p0, final int p1);
    
    protected abstract void extractStringTrim(final int p0, final int p1);
}
