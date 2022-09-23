// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.parser;

import java.io.IOException;

abstract class JSONParserStream extends JSONParserBase
{
    public JSONParserStream(final int permissiveMode) {
        super(permissiveMode);
    }
    
    @Override
    protected void readNQString(final boolean[] stop) throws IOException {
        this.sb.clear();
        this.skipNQString(stop);
        this.xs = this.sb.toString().trim();
    }
    
    @Override
    protected Object readNumber(final boolean[] stop) throws ParseException, IOException {
        this.sb.clear();
        this.sb.append(this.c);
        this.read();
        this.skipDigits();
        if (this.c != '.' && this.c != 'E' && this.c != 'e') {
            this.skipSpace();
            if (this.c < '\0' || this.c >= '~' || stop[this.c] || this.c == '\u001a') {
                this.xs = this.sb.toString().trim();
                return this.parseNumber(this.xs);
            }
            this.skipNQString(stop);
            this.xs = this.sb.toString().trim();
            if (!this.acceptNonQuote) {
                throw new ParseException(this.pos, 1, this.xs);
            }
            return this.xs;
        }
        else {
            if (this.c == '.') {
                this.sb.append(this.c);
                this.read();
                this.skipDigits();
            }
            if (this.c != 'E' && this.c != 'e') {
                this.skipSpace();
                if (this.c < '\0' || this.c >= '~' || stop[this.c] || this.c == '\u001a') {
                    this.xs = this.sb.toString().trim();
                    return this.extractFloat();
                }
                this.skipNQString(stop);
                this.xs = this.sb.toString().trim();
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
                        this.xs = this.sb.toString().trim();
                        return this.extractFloat();
                    }
                    this.skipNQString(stop);
                    this.xs = this.sb.toString().trim();
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                else {
                    this.skipNQString(stop);
                    this.xs = this.sb.toString().trim();
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
        if (this.acceptSimpleQuote || this.c != '\'') {
            this.sb.clear();
            this.readString2();
            return;
        }
        if (this.acceptNonQuote) {
            this.readNQString(JSONParserStream.stopAll);
            return;
        }
        throw new ParseException(this.pos, 0, this.c);
    }
}
