// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.Collection;
import net.jcip.annotations.Immutable;

@Immutable
public final class EncryptionMethod extends Algorithm
{
    private static final long serialVersionUID = 1L;
    private final int cekBitLength;
    public static final EncryptionMethod A128CBC_HS256;
    public static final EncryptionMethod A192CBC_HS384;
    public static final EncryptionMethod A256CBC_HS512;
    public static final EncryptionMethod A128CBC_HS256_DEPRECATED;
    public static final EncryptionMethod A256CBC_HS512_DEPRECATED;
    public static final EncryptionMethod A128GCM;
    public static final EncryptionMethod A192GCM;
    public static final EncryptionMethod A256GCM;
    
    static {
        A128CBC_HS256 = new EncryptionMethod("A128CBC-HS256", Requirement.REQUIRED, 256);
        A192CBC_HS384 = new EncryptionMethod("A192CBC-HS384", Requirement.OPTIONAL, 384);
        A256CBC_HS512 = new EncryptionMethod("A256CBC-HS512", Requirement.REQUIRED, 512);
        A128CBC_HS256_DEPRECATED = new EncryptionMethod("A128CBC+HS256", Requirement.OPTIONAL, 256);
        A256CBC_HS512_DEPRECATED = new EncryptionMethod("A256CBC+HS512", Requirement.OPTIONAL, 512);
        A128GCM = new EncryptionMethod("A128GCM", Requirement.RECOMMENDED, 128);
        A192GCM = new EncryptionMethod("A192GCM", Requirement.OPTIONAL, 192);
        A256GCM = new EncryptionMethod("A256GCM", Requirement.RECOMMENDED, 256);
    }
    
    public EncryptionMethod(final String name, final Requirement req, final int cekBitLength) {
        super(name, req);
        this.cekBitLength = cekBitLength;
    }
    
    public EncryptionMethod(final String name, final Requirement req) {
        this(name, req, 0);
    }
    
    public EncryptionMethod(final String name) {
        this(name, null, 0);
    }
    
    public int cekBitLength() {
        return this.cekBitLength;
    }
    
    public static EncryptionMethod parse(final String s) {
        if (s.equals(EncryptionMethod.A128CBC_HS256.getName())) {
            return EncryptionMethod.A128CBC_HS256;
        }
        if (s.equals(EncryptionMethod.A192CBC_HS384.getName())) {
            return EncryptionMethod.A192CBC_HS384;
        }
        if (s.equals(EncryptionMethod.A256CBC_HS512.getName())) {
            return EncryptionMethod.A256CBC_HS512;
        }
        if (s.equals(EncryptionMethod.A128GCM.getName())) {
            return EncryptionMethod.A128GCM;
        }
        if (s.equals(EncryptionMethod.A192GCM.getName())) {
            return EncryptionMethod.A192GCM;
        }
        if (s.equals(EncryptionMethod.A256GCM.getName())) {
            return EncryptionMethod.A256GCM;
        }
        if (s.equals(EncryptionMethod.A128CBC_HS256_DEPRECATED.getName())) {
            return EncryptionMethod.A128CBC_HS256_DEPRECATED;
        }
        if (s.equals(EncryptionMethod.A256CBC_HS512_DEPRECATED.getName())) {
            return EncryptionMethod.A256CBC_HS512_DEPRECATED;
        }
        return new EncryptionMethod(s);
    }
    
    public static final class Family extends AlgorithmFamily<EncryptionMethod>
    {
        private static final long serialVersionUID = 1L;
        public static final Family AES_CBC_HMAC_SHA;
        public static final Family AES_GCM;
        
        static {
            AES_CBC_HMAC_SHA = new Family(new EncryptionMethod[] { EncryptionMethod.A128CBC_HS256, EncryptionMethod.A192CBC_HS384, EncryptionMethod.A256CBC_HS512 });
            AES_GCM = new Family(new EncryptionMethod[] { EncryptionMethod.A128GCM, EncryptionMethod.A192GCM, EncryptionMethod.A256GCM });
        }
        
        public Family(final EncryptionMethod... encs) {
            super(encs);
        }
    }
}
