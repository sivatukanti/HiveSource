// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.provider.token;

import java.util.Map;
import java.util.Date;
import java.util.List;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jose.PlainHeader;
import java.util.UUID;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;

public class JwtAuthToken implements AuthToken
{
    private JWTClaimsSet jwtClaims;
    private Boolean isIdToken;
    private Boolean isAcToken;
    
    public JwtAuthToken() {
        this((JWTClaimsSet)new Object());
    }
    
    public JwtAuthToken(final JWTClaimsSet jwtClaims) {
        this.isIdToken = true;
        this.isAcToken = false;
        this.jwtClaims = jwtClaims;
    }
    
    public JwtAuthToken(final ReadOnlyJWTClaimsSet jwtClaims) {
        this.isIdToken = true;
        this.isAcToken = false;
        this.jwtClaims = JwtUtil.from(jwtClaims);
    }
    
    protected JWT getJwt() {
        String jti = this.jwtClaims.getJWTID();
        if (jti == null || jti.isEmpty()) {
            jti = UUID.randomUUID().toString();
            this.jwtClaims.setJWTID(jti);
        }
        final PlainHeader header = new PlainHeader();
        final PlainJWT jwt = new PlainJWT(header, (ReadOnlyJWTClaimsSet)this.jwtClaims);
        return jwt;
    }
    
    @Override
    public String getSubject() {
        return this.jwtClaims.getSubject();
    }
    
    @Override
    public void setSubject(final String sub) {
        this.jwtClaims.setSubject(sub);
    }
    
    @Override
    public String getIssuer() {
        return this.jwtClaims.getIssuer();
    }
    
    @Override
    public void setIssuer(final String issuer) {
        this.jwtClaims.setIssuer(issuer);
    }
    
    @Override
    public List<String> getAudiences() {
        return this.jwtClaims.getAudience();
    }
    
    @Override
    public void setAudiences(final List<String> audiences) {
        this.jwtClaims.setAudience((List)audiences);
    }
    
    @Override
    public boolean isIdToken() {
        return this.isIdToken;
    }
    
    @Override
    public void isIdToken(final boolean isIdToken) {
        this.isIdToken = isIdToken;
    }
    
    @Override
    public boolean isAcToken() {
        return this.isAcToken;
    }
    
    @Override
    public void isAcToken(final boolean isAcToken) {
        this.isAcToken = isAcToken;
    }
    
    @Override
    public boolean isBearerToken() {
        return true;
    }
    
    @Override
    public boolean isHolderOfKeyToken() {
        return false;
    }
    
    @Override
    public Date getExpiredTime() {
        return this.jwtClaims.getExpirationTime();
    }
    
    @Override
    public void setExpirationTime(final Date exp) {
        this.jwtClaims.setExpirationTime(exp);
    }
    
    @Override
    public Date getNotBeforeTime() {
        return this.jwtClaims.getNotBeforeTime();
    }
    
    @Override
    public void setNotBeforeTime(final Date nbt) {
        this.jwtClaims.setNotBeforeTime(nbt);
    }
    
    @Override
    public Date getIssueTime() {
        return this.jwtClaims.getIssueTime();
    }
    
    @Override
    public void setIssueTime(final Date iat) {
        this.jwtClaims.setIssueTime(iat);
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return (Map<String, Object>)this.jwtClaims.getAllClaims();
    }
    
    @Override
    public void addAttribute(final String name, final Object value) {
        this.jwtClaims.setCustomClaim(name, value);
    }
}
