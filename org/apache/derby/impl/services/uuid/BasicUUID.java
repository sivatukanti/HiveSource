// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.uuid;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.StringReader;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.UUID;

public class BasicUUID implements UUID, Formatable
{
    private long majorId;
    private long timemillis;
    private int sequence;
    
    public BasicUUID(final long majorId, final long timemillis, final int sequence) {
        this.majorId = majorId;
        this.timemillis = timemillis;
        this.sequence = sequence;
    }
    
    public BasicUUID(final String s) {
        final StringReader stringReader = new StringReader(s);
        this.sequence = (int)readMSB(stringReader);
        this.timemillis = (readMSB(stringReader) << 32) + (readMSB(stringReader) << 16) + readMSB(stringReader);
        this.majorId = readMSB(stringReader);
    }
    
    public BasicUUID() {
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(this.majorId);
        objectOutput.writeLong(this.timemillis);
        objectOutput.writeInt(this.sequence);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.majorId = objectInput.readLong();
        this.timemillis = objectInput.readLong();
        this.sequence = objectInput.readInt();
    }
    
    public int getTypeFormatId() {
        return 131;
    }
    
    private static void writeMSB(final char[] array, int n, final long n2, final int n3) {
        for (int i = n3 - 1; i >= 0; --i) {
            final long n4 = (n2 & 255L << 8 * i) >>> 8 * i;
            final int n5 = (int)((n4 & 0xF0L) >> 4);
            array[n++] = (char)((n5 < 10) ? (n5 + 48) : (n5 - 10 + 97));
            final int n6 = (int)(n4 & 0xFL);
            array[n++] = (char)((n6 < 10) ? (n6 + 48) : (n6 - 10 + 97));
        }
    }
    
    private static long readMSB(final StringReader stringReader) {
        long n = 0L;
        try {
            int read;
            while ((read = stringReader.read()) != -1 && read != 45) {
                n <<= 4;
                int n2;
                if (read <= 57) {
                    n2 = read - 48;
                }
                else if (read <= 70) {
                    n2 = read - 65 + 10;
                }
                else {
                    n2 = read - 97 + 10;
                }
                n += n2;
            }
        }
        catch (Exception ex) {}
        return n;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof BasicUUID)) {
            return false;
        }
        final BasicUUID basicUUID = (BasicUUID)o;
        return this.sequence == basicUUID.sequence && this.timemillis == basicUUID.timemillis && this.majorId == basicUUID.majorId;
    }
    
    public int hashCode() {
        return this.sequence ^ (int)((this.majorId ^ this.timemillis) >> 4);
    }
    
    public String toString() {
        return this.stringWorkhorse('-');
    }
    
    public String toANSIidentifier() {
        return "U" + this.stringWorkhorse('X');
    }
    
    public String stringWorkhorse(final char c) {
        final char[] value = new char[36];
        writeMSB(value, 0, this.sequence, 4);
        int count = 8;
        if (c != '\0') {
            value[count++] = c;
        }
        final long timemillis = this.timemillis;
        writeMSB(value, count, (timemillis & 0xFFFF00000000L) >>> 32, 2);
        count += 4;
        if (c != '\0') {
            value[count++] = c;
        }
        writeMSB(value, count, (timemillis & 0xFFFF0000L) >>> 16, 2);
        count += 4;
        if (c != '\0') {
            value[count++] = c;
        }
        writeMSB(value, count, timemillis & 0xFFFFL, 2);
        count += 4;
        if (c != '\0') {
            value[count++] = c;
        }
        writeMSB(value, count, this.majorId, 6);
        count += 12;
        return new String(value, 0, count);
    }
    
    public UUID cloneMe() {
        return new BasicUUID(this.majorId, this.timemillis, this.sequence);
    }
}
