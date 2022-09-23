// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.Collection;
import com.nimbusds.jose.util.ArrayUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class JWSAlgorithm extends Algorithm
{
    private static final long serialVersionUID = 1L;
    public static final JWSAlgorithm HS256;
    public static final JWSAlgorithm HS384;
    public static final JWSAlgorithm HS512;
    public static final JWSAlgorithm RS256;
    public static final JWSAlgorithm RS384;
    public static final JWSAlgorithm RS512;
    public static final JWSAlgorithm ES256;
    public static final JWSAlgorithm ES384;
    public static final JWSAlgorithm ES512;
    public static final JWSAlgorithm PS256;
    public static final JWSAlgorithm PS384;
    public static final JWSAlgorithm PS512;
    
    static {
        HS256 = new JWSAlgorithm("HS256", Requirement.REQUIRED);
        HS384 = new JWSAlgorithm("HS384", Requirement.OPTIONAL);
        HS512 = new JWSAlgorithm("HS512", Requirement.OPTIONAL);
        RS256 = new JWSAlgorithm("RS256", Requirement.RECOMMENDED);
        RS384 = new JWSAlgorithm("RS384", Requirement.OPTIONAL);
        RS512 = new JWSAlgorithm("RS512", Requirement.OPTIONAL);
        ES256 = new JWSAlgorithm("ES256", Requirement.RECOMMENDED);
        ES384 = new JWSAlgorithm("ES384", Requirement.OPTIONAL);
        ES512 = new JWSAlgorithm("ES512", Requirement.OPTIONAL);
        PS256 = new JWSAlgorithm("PS256", Requirement.OPTIONAL);
        PS384 = new JWSAlgorithm("PS384", Requirement.OPTIONAL);
        PS512 = new JWSAlgorithm("PS512", Requirement.OPTIONAL);
    }
    
    public JWSAlgorithm(final String name, final Requirement req) {
        super(name, req);
    }
    
    public JWSAlgorithm(final String name) {
        super(name, null);
    }
    
    public static JWSAlgorithm parse(final String s) {
        if (s.equals(JWSAlgorithm.HS256.getName())) {
            return JWSAlgorithm.HS256;
        }
        if (s.equals(JWSAlgorithm.HS384.getName())) {
            return JWSAlgorithm.HS384;
        }
        if (s.equals(JWSAlgorithm.HS512.getName())) {
            return JWSAlgorithm.HS512;
        }
        if (s.equals(JWSAlgorithm.RS256.getName())) {
            return JWSAlgorithm.RS256;
        }
        if (s.equals(JWSAlgorithm.RS384.getName())) {
            return JWSAlgorithm.RS384;
        }
        if (s.equals(JWSAlgorithm.RS512.getName())) {
            return JWSAlgorithm.RS512;
        }
        if (s.equals(JWSAlgorithm.ES256.getName())) {
            return JWSAlgorithm.ES256;
        }
        if (s.equals(JWSAlgorithm.ES384.getName())) {
            return JWSAlgorithm.ES384;
        }
        if (s.equals(JWSAlgorithm.ES512.getName())) {
            return JWSAlgorithm.ES512;
        }
        if (s.equals(JWSAlgorithm.PS256.getName())) {
            return JWSAlgorithm.PS256;
        }
        if (s.equals(JWSAlgorithm.PS384.getName())) {
            return JWSAlgorithm.PS384;
        }
        if (s.equals(JWSAlgorithm.PS512.getName())) {
            return JWSAlgorithm.PS512;
        }
        return new JWSAlgorithm(s);
    }
    
    public static final class Family extends AlgorithmFamily<JWSAlgorithm>
    {
        private static final long serialVersionUID = 1L;
        public static final Family HMAC_SHA;
        public static final Family RSA;
        public static final Family EC;
        public static final Family SIGNATURE;
        
        static {
            HMAC_SHA = new Family(new JWSAlgorithm[] { JWSAlgorithm.HS256, JWSAlgorithm.HS384, JWSAlgorithm.HS512 });
            RSA = new Family(new JWSAlgorithm[] { JWSAlgorithm.RS256, JWSAlgorithm.RS384, JWSAlgorithm.RS512, JWSAlgorithm.PS256, JWSAlgorithm.PS384, JWSAlgorithm.PS512 });
            EC = new Family(new JWSAlgorithm[] { JWSAlgorithm.ES256, JWSAlgorithm.ES384, JWSAlgorithm.ES512 });
            SIGNATURE = new Family((JWSAlgorithm[])ArrayUtils.concat(Family.RSA.toArray(new JWSAlgorithm[0]), new JWSAlgorithm[][] { Family.EC.toArray(new JWSAlgorithm[0]) }));
        }
        
        public Family(final JWSAlgorithm... algs) {
            super(algs);
        }
    }
}
