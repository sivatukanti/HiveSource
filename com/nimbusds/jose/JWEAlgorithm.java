// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.Collection;
import com.nimbusds.jose.util.ArrayUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class JWEAlgorithm extends Algorithm
{
    private static final long serialVersionUID = 1L;
    @Deprecated
    public static final JWEAlgorithm RSA1_5;
    @Deprecated
    public static final JWEAlgorithm RSA_OAEP;
    public static final JWEAlgorithm RSA_OAEP_256;
    public static final JWEAlgorithm A128KW;
    public static final JWEAlgorithm A192KW;
    public static final JWEAlgorithm A256KW;
    public static final JWEAlgorithm DIR;
    public static final JWEAlgorithm ECDH_ES;
    public static final JWEAlgorithm ECDH_ES_A128KW;
    public static final JWEAlgorithm ECDH_ES_A192KW;
    public static final JWEAlgorithm ECDH_ES_A256KW;
    public static final JWEAlgorithm A128GCMKW;
    public static final JWEAlgorithm A192GCMKW;
    public static final JWEAlgorithm A256GCMKW;
    public static final JWEAlgorithm PBES2_HS256_A128KW;
    public static final JWEAlgorithm PBES2_HS384_A192KW;
    public static final JWEAlgorithm PBES2_HS512_A256KW;
    
    static {
        RSA1_5 = new JWEAlgorithm("RSA1_5", Requirement.REQUIRED);
        RSA_OAEP = new JWEAlgorithm("RSA-OAEP", Requirement.OPTIONAL);
        RSA_OAEP_256 = new JWEAlgorithm("RSA-OAEP-256", Requirement.OPTIONAL);
        A128KW = new JWEAlgorithm("A128KW", Requirement.RECOMMENDED);
        A192KW = new JWEAlgorithm("A192KW", Requirement.OPTIONAL);
        A256KW = new JWEAlgorithm("A256KW", Requirement.RECOMMENDED);
        DIR = new JWEAlgorithm("dir", Requirement.RECOMMENDED);
        ECDH_ES = new JWEAlgorithm("ECDH-ES", Requirement.RECOMMENDED);
        ECDH_ES_A128KW = new JWEAlgorithm("ECDH-ES+A128KW", Requirement.RECOMMENDED);
        ECDH_ES_A192KW = new JWEAlgorithm("ECDH-ES+A192KW", Requirement.OPTIONAL);
        ECDH_ES_A256KW = new JWEAlgorithm("ECDH-ES+A256KW", Requirement.RECOMMENDED);
        A128GCMKW = new JWEAlgorithm("A128GCMKW", Requirement.OPTIONAL);
        A192GCMKW = new JWEAlgorithm("A192GCMKW", Requirement.OPTIONAL);
        A256GCMKW = new JWEAlgorithm("A256GCMKW", Requirement.OPTIONAL);
        PBES2_HS256_A128KW = new JWEAlgorithm("PBES2-HS256+A128KW", Requirement.OPTIONAL);
        PBES2_HS384_A192KW = new JWEAlgorithm("PBES2-HS384+A192KW", Requirement.OPTIONAL);
        PBES2_HS512_A256KW = new JWEAlgorithm("PBES2-HS512+A256KW", Requirement.OPTIONAL);
    }
    
    public JWEAlgorithm(final String name, final Requirement req) {
        super(name, req);
    }
    
    public JWEAlgorithm(final String name) {
        super(name, null);
    }
    
    public static JWEAlgorithm parse(final String s) {
        if (s.equals(JWEAlgorithm.RSA1_5.getName())) {
            return JWEAlgorithm.RSA1_5;
        }
        if (s.equals(JWEAlgorithm.RSA_OAEP.getName())) {
            return JWEAlgorithm.RSA_OAEP;
        }
        if (s.equals(JWEAlgorithm.RSA_OAEP_256.getName())) {
            return JWEAlgorithm.RSA_OAEP_256;
        }
        if (s.equals(JWEAlgorithm.A128KW.getName())) {
            return JWEAlgorithm.A128KW;
        }
        if (s.equals(JWEAlgorithm.A192KW.getName())) {
            return JWEAlgorithm.A192KW;
        }
        if (s.equals(JWEAlgorithm.A256KW.getName())) {
            return JWEAlgorithm.A256KW;
        }
        if (s.equals(JWEAlgorithm.DIR.getName())) {
            return JWEAlgorithm.DIR;
        }
        if (s.equals(JWEAlgorithm.ECDH_ES.getName())) {
            return JWEAlgorithm.ECDH_ES;
        }
        if (s.equals(JWEAlgorithm.ECDH_ES_A128KW.getName())) {
            return JWEAlgorithm.ECDH_ES_A128KW;
        }
        if (s.equals(JWEAlgorithm.ECDH_ES_A192KW.getName())) {
            return JWEAlgorithm.ECDH_ES_A192KW;
        }
        if (s.equals(JWEAlgorithm.ECDH_ES_A256KW.getName())) {
            return JWEAlgorithm.ECDH_ES_A256KW;
        }
        if (s.equals(JWEAlgorithm.A128GCMKW.getName())) {
            return JWEAlgorithm.A128GCMKW;
        }
        if (s.equals(JWEAlgorithm.A192GCMKW.getName())) {
            return JWEAlgorithm.A192GCMKW;
        }
        if (s.equals(JWEAlgorithm.A256GCMKW.getName())) {
            return JWEAlgorithm.A256GCMKW;
        }
        if (s.equals(JWEAlgorithm.PBES2_HS256_A128KW.getName())) {
            return JWEAlgorithm.PBES2_HS256_A128KW;
        }
        if (s.equals(JWEAlgorithm.PBES2_HS384_A192KW.getName())) {
            return JWEAlgorithm.PBES2_HS384_A192KW;
        }
        if (s.equals(JWEAlgorithm.PBES2_HS512_A256KW.getName())) {
            return JWEAlgorithm.PBES2_HS512_A256KW;
        }
        return new JWEAlgorithm(s);
    }
    
    public static final class Family extends AlgorithmFamily<JWEAlgorithm>
    {
        private static final long serialVersionUID = 1L;
        public static final Family RSA;
        public static final Family AES_KW;
        public static final Family ECDH_ES;
        public static final Family AES_GCM_KW;
        public static final Family PBES2;
        public static final Family ASYMMETRIC;
        public static final Family SYMMETRIC;
        
        static {
            RSA = new Family(new JWEAlgorithm[] { JWEAlgorithm.RSA1_5, JWEAlgorithm.RSA_OAEP, JWEAlgorithm.RSA_OAEP_256 });
            AES_KW = new Family(new JWEAlgorithm[] { JWEAlgorithm.A128KW, JWEAlgorithm.A192KW, JWEAlgorithm.A256KW });
            ECDH_ES = new Family(new JWEAlgorithm[] { JWEAlgorithm.ECDH_ES, JWEAlgorithm.ECDH_ES_A128KW, JWEAlgorithm.ECDH_ES_A192KW, JWEAlgorithm.ECDH_ES_A256KW });
            AES_GCM_KW = new Family(new JWEAlgorithm[] { JWEAlgorithm.A128GCMKW, JWEAlgorithm.A192GCMKW, JWEAlgorithm.A256GCMKW });
            PBES2 = new Family(new JWEAlgorithm[] { JWEAlgorithm.PBES2_HS256_A128KW, JWEAlgorithm.PBES2_HS384_A192KW, JWEAlgorithm.PBES2_HS512_A256KW });
            ASYMMETRIC = new Family((JWEAlgorithm[])ArrayUtils.concat(Family.RSA.toArray(new JWEAlgorithm[0]), new JWEAlgorithm[][] { Family.ECDH_ES.toArray(new JWEAlgorithm[0]) }));
            SYMMETRIC = new Family((JWEAlgorithm[])ArrayUtils.concat(Family.AES_KW.toArray(new JWEAlgorithm[0]), new JWEAlgorithm[][] { Family.AES_GCM_KW.toArray(new JWEAlgorithm[0]), { JWEAlgorithm.DIR } }));
        }
        
        public Family(final JWEAlgorithm... algs) {
            super(algs);
        }
    }
}
