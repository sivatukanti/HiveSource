// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class SnappyCodec
{
    public static final byte[] MAGIC_HEADER;
    public static final int MAGIC_LEN = 8;
    public static final int DEFAULT_VERSION = 1;
    public static final int MINIMUM_COMPATIBLE_VERSION = 1;
    public final byte[] magic;
    public final int version;
    public final int compatibleVersion;
    
    private SnappyCodec(final byte[] magic, final int version, final int compatibleVersion) {
        this.magic = magic;
        this.version = version;
        this.compatibleVersion = compatibleVersion;
    }
    
    @Override
    public String toString() {
        return String.format("version:%d, compatible version:%d", this.version, this.compatibleVersion);
    }
    
    public static int headerSize() {
        return 16;
    }
    
    public void writeHeader(final OutputStream out) throws IOException {
        final ByteArrayOutputStream header = new ByteArrayOutputStream();
        final DataOutputStream d = new DataOutputStream(header);
        d.write(this.magic, 0, 8);
        d.writeInt(this.version);
        d.writeInt(this.compatibleVersion);
        d.close();
        out.write(header.toByteArray(), 0, header.size());
    }
    
    public boolean isValidMagicHeader() {
        return Arrays.equals(SnappyCodec.MAGIC_HEADER, this.magic);
    }
    
    public static SnappyCodec readHeader(final InputStream in) throws IOException {
        final DataInputStream d = new DataInputStream(in);
        final byte[] magic = new byte[8];
        d.read(magic, 0, 8);
        final int version = d.readInt();
        final int compatibleVersion = d.readInt();
        return new SnappyCodec(magic, version, compatibleVersion);
    }
    
    public static SnappyCodec currentHeader() {
        return new SnappyCodec(SnappyCodec.MAGIC_HEADER, 1, 1);
    }
    
    static {
        MAGIC_HEADER = new byte[] { -126, 83, 78, 65, 80, 80, 89, 0 };
    }
}
