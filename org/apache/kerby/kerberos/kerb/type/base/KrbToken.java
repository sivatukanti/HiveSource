// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.base;

import java.util.Map;
import java.util.Date;
import org.apache.kerby.kerberos.kerb.KrbRuntime;
import java.util.List;
import java.io.IOException;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.provider.TokenDecoder;
import org.apache.kerby.kerberos.kerb.provider.TokenEncoder;

public class KrbToken extends KrbTokenBase implements AuthToken
{
    private static TokenEncoder tokenEncoder;
    private static TokenDecoder tokenDecoder;
    private AuthToken innerToken;
    
    public KrbToken() {
        this.innerToken = null;
    }
    
    public KrbToken(final AuthToken authToken, final TokenFormat format) {
        this();
        this.innerToken = authToken;
        this.setTokenType();
        this.setTokenFormat(format);
        try {
            this.setTokenValue(getTokenEncoder().encodeAsBytes(this.innerToken));
        }
        catch (KrbException e) {
            throw new RuntimeException("Failed to encode AuthToken", e);
        }
    }
    
    public AuthToken getAuthToken() {
        return this.innerToken;
    }
    
    @Override
    public void decode(final Asn1ParseResult parseResult) throws IOException {
        super.decode(parseResult);
        if (this.getTokenValue() != null) {
            this.innerToken = getTokenDecoder().decodeFromBytes(this.getTokenValue());
            this.setTokenType();
        }
    }
    
    public void setTokenType() {
        final List<String> audiences = this.innerToken.getAudiences();
        if (audiences != null && audiences.size() == 1 && audiences.get(0).startsWith("krbtgt")) {
            this.isIdToken(true);
        }
        else {
            this.isAcToken(true);
        }
    }
    
    protected static TokenEncoder getTokenEncoder() {
        if (KrbToken.tokenEncoder == null) {
            KrbToken.tokenEncoder = KrbRuntime.getTokenProvider().createTokenEncoder();
        }
        return KrbToken.tokenEncoder;
    }
    
    protected static TokenDecoder getTokenDecoder() {
        if (KrbToken.tokenDecoder == null) {
            KrbToken.tokenDecoder = KrbRuntime.getTokenProvider().createTokenDecoder();
        }
        return KrbToken.tokenDecoder;
    }
    
    @Override
    public String getSubject() {
        return this.innerToken.getSubject();
    }
    
    @Override
    public void setSubject(final String sub) {
        this.innerToken.setSubject(sub);
    }
    
    @Override
    public String getIssuer() {
        return this.innerToken.getIssuer();
    }
    
    @Override
    public void setIssuer(final String issuer) {
        this.innerToken.setIssuer(issuer);
    }
    
    @Override
    public List<String> getAudiences() {
        return this.innerToken.getAudiences();
    }
    
    @Override
    public void setAudiences(final List<String> audiences) {
        this.innerToken.setAudiences(audiences);
    }
    
    @Override
    public boolean isIdToken() {
        return this.innerToken.isIdToken();
    }
    
    @Override
    public void isIdToken(final boolean isIdToken) {
        this.innerToken.isIdToken(isIdToken);
    }
    
    @Override
    public boolean isAcToken() {
        return this.innerToken.isAcToken();
    }
    
    @Override
    public void isAcToken(final boolean isAcToken) {
        this.innerToken.isAcToken(isAcToken);
    }
    
    @Override
    public boolean isBearerToken() {
        return this.innerToken.isBearerToken();
    }
    
    @Override
    public boolean isHolderOfKeyToken() {
        return this.innerToken.isHolderOfKeyToken();
    }
    
    @Override
    public Date getExpiredTime() {
        return this.innerToken.getExpiredTime();
    }
    
    @Override
    public void setExpirationTime(final Date exp) {
        this.innerToken.setExpirationTime(exp);
    }
    
    @Override
    public Date getNotBeforeTime() {
        return this.innerToken.getNotBeforeTime();
    }
    
    @Override
    public void setNotBeforeTime(final Date nbt) {
        this.innerToken.setNotBeforeTime(nbt);
    }
    
    @Override
    public Date getIssueTime() {
        return this.innerToken.getIssueTime();
    }
    
    @Override
    public void setIssueTime(final Date iat) {
        this.innerToken.setIssueTime(iat);
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return this.innerToken.getAttributes();
    }
    
    @Override
    public void addAttribute(final String name, final Object value) {
        this.innerToken.addAttribute(name, value);
    }
    
    public void setInnerToken(final AuthToken authToken) {
        this.innerToken = authToken;
    }
}
