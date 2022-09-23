// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.db.DatabaseContext;
import java.io.IOException;
import java.io.ObjectOutput;

public final class ClobStreamHeaderGenerator implements StreamHeaderGenerator
{
    private static final byte MAGIC_BYTE = -16;
    private static final byte[] UNKNOWN_LENGTH;
    private static final CharStreamHeaderGenerator CHARHDRGEN;
    private final StringDataValue callbackDVD;
    private Boolean isPreDerbyTenFive;
    
    public ClobStreamHeaderGenerator(final StringDataValue callbackDVD) {
        if (callbackDVD == null) {
            throw new IllegalStateException("dvd cannot be null");
        }
        this.callbackDVD = callbackDVD;
    }
    
    public ClobStreamHeaderGenerator(final boolean b) {
        this.callbackDVD = null;
        this.isPreDerbyTenFive = b;
    }
    
    public boolean expectsCharCount() {
        if (this.callbackDVD != null && this.isPreDerbyTenFive == null) {
            this.determineHeaderFormat();
        }
        return !this.isPreDerbyTenFive;
    }
    
    public int generateInto(final byte[] array, final int n, final long n2) {
        if (this.callbackDVD != null && this.isPreDerbyTenFive == null) {
            this.determineHeaderFormat();
        }
        int n3 = 0;
        if (this.isPreDerbyTenFive == Boolean.FALSE) {
            if (n2 >= 0L) {
                array[n + n3++] = (byte)(n2 >>> 24);
                array[n + n3++] = (byte)(n2 >>> 16);
                array[n + n3++] = -16;
                array[n + n3++] = (byte)(n2 >>> 8);
                array[n + n3++] = (byte)(n2 >>> 0);
            }
            else {
                n3 = ClobStreamHeaderGenerator.UNKNOWN_LENGTH.length;
                System.arraycopy(ClobStreamHeaderGenerator.UNKNOWN_LENGTH, 0, array, n, n3);
            }
        }
        else {
            n3 = ClobStreamHeaderGenerator.CHARHDRGEN.generateInto(array, n, n2);
        }
        return n3;
    }
    
    public int generateInto(final ObjectOutput objectOutput, final long n) throws IOException {
        if (this.callbackDVD != null && this.isPreDerbyTenFive == null) {
            this.determineHeaderFormat();
        }
        int generateInto;
        if (this.isPreDerbyTenFive == Boolean.FALSE) {
            generateInto = 5;
            if (n > 0L) {
                objectOutput.writeByte((byte)(n >>> 24));
                objectOutput.writeByte((byte)(n >>> 16));
                objectOutput.writeByte(-16);
                objectOutput.writeByte((byte)(n >>> 8));
                objectOutput.writeByte((byte)(n >>> 0));
            }
            else {
                objectOutput.write(ClobStreamHeaderGenerator.UNKNOWN_LENGTH);
            }
        }
        else {
            generateInto = ClobStreamHeaderGenerator.CHARHDRGEN.generateInto(objectOutput, n);
        }
        return generateInto;
    }
    
    public int writeEOF(final byte[] array, final int n, final long n2) {
        if (this.callbackDVD != null && this.isPreDerbyTenFive == null) {
            this.determineHeaderFormat();
        }
        if (this.isPreDerbyTenFive) {
            return ClobStreamHeaderGenerator.CHARHDRGEN.writeEOF(array, n, n2);
        }
        if (n2 < 0L) {
            System.arraycopy(ClobStreamHeaderGenerator.DERBY_EOF_MARKER, 0, array, n, ClobStreamHeaderGenerator.DERBY_EOF_MARKER.length);
            return ClobStreamHeaderGenerator.DERBY_EOF_MARKER.length;
        }
        return 0;
    }
    
    public int writeEOF(final ObjectOutput objectOutput, final long n) throws IOException {
        if (this.callbackDVD != null && this.isPreDerbyTenFive == null) {
            this.determineHeaderFormat();
        }
        if (this.isPreDerbyTenFive) {
            return ClobStreamHeaderGenerator.CHARHDRGEN.writeEOF(objectOutput, n);
        }
        if (n < 0L) {
            objectOutput.write(ClobStreamHeaderGenerator.DERBY_EOF_MARKER);
            return ClobStreamHeaderGenerator.DERBY_EOF_MARKER.length;
        }
        return 0;
    }
    
    public int getMaxHeaderLength() {
        return 5;
    }
    
    private void determineHeaderFormat() {
        final DatabaseContext databaseContext = (DatabaseContext)ContextService.getContext("Database");
        if (databaseContext == null) {
            throw new IllegalStateException("No context, unable to determine which stream header format to generate");
        }
        final DataDictionary dataDictionary = databaseContext.getDatabase().getDataDictionary();
        try {
            this.isPreDerbyTenFive = !dataDictionary.checkVersion(170, null);
        }
        catch (StandardException cause) {
            final IllegalStateException ex = new IllegalStateException(cause.getMessage());
            ex.initCause(cause);
            throw ex;
        }
        this.callbackDVD.setStreamHeaderFormat(this.isPreDerbyTenFive);
    }
    
    static {
        UNKNOWN_LENGTH = new byte[] { 0, 0, -16, 0, 0 };
        CHARHDRGEN = new CharStreamHeaderGenerator();
    }
}
