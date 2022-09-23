// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt.proc;

import com.nimbusds.jwt.util.DateUtils;
import java.util.Date;
import com.nimbusds.jwt.JWTClaimsSet;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.proc.SecurityContext;

@ThreadSafe
public class DefaultJWTClaimsVerifier<C extends SecurityContext> implements JWTClaimsSetVerifier<C>, JWTClaimsVerifier, ClockSkewAware
{
    public static final int DEFAULT_MAX_CLOCK_SKEW_SECONDS = 60;
    private static final BadJWTException EXPIRED_JWT_EXCEPTION;
    private static final BadJWTException JWT_BEFORE_USE_EXCEPTION;
    private int maxClockSkew;
    
    static {
        EXPIRED_JWT_EXCEPTION = new BadJWTException("Expired JWT");
        JWT_BEFORE_USE_EXCEPTION = new BadJWTException("JWT before use time");
    }
    
    public DefaultJWTClaimsVerifier() {
        this.maxClockSkew = 60;
    }
    
    @Override
    public int getMaxClockSkew() {
        return this.maxClockSkew;
    }
    
    @Override
    public void setMaxClockSkew(final int maxClockSkewSeconds) {
        this.maxClockSkew = maxClockSkewSeconds;
    }
    
    @Override
    public void verify(final JWTClaimsSet claimsSet) throws BadJWTException {
        this.verify(claimsSet, null);
    }
    
    @Override
    public void verify(final JWTClaimsSet claimsSet, final C context) throws BadJWTException {
        final Date now = new Date();
        final Date exp = claimsSet.getExpirationTime();
        if (exp != null && !DateUtils.isAfter(exp, now, this.maxClockSkew)) {
            throw DefaultJWTClaimsVerifier.EXPIRED_JWT_EXCEPTION;
        }
        final Date nbf = claimsSet.getNotBeforeTime();
        if (nbf != null && !DateUtils.isBefore(nbf, now, this.maxClockSkew)) {
            throw DefaultJWTClaimsVerifier.JWT_BEFORE_USE_EXCEPTION;
        }
    }
}
