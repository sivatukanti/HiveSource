// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.Iterator;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.JWSObject;
import com.google.common.annotations.VisibleForTesting;
import javax.servlet.http.Cookie;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import java.text.ParseException;
import com.nimbusds.jwt.SignedJWT;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import org.apache.hadoop.security.authentication.util.CertificateUtil;
import javax.servlet.ServletException;
import java.util.Properties;
import java.util.List;
import java.security.interfaces.RSAPublicKey;
import org.slf4j.Logger;

public class JWTRedirectAuthenticationHandler extends AltKerberosAuthenticationHandler
{
    private static Logger LOG;
    public static final String AUTHENTICATION_PROVIDER_URL = "authentication.provider.url";
    public static final String PUBLIC_KEY_PEM = "public.key.pem";
    public static final String EXPECTED_JWT_AUDIENCES = "expected.jwt.audiences";
    public static final String JWT_COOKIE_NAME = "jwt.cookie.name";
    private static final String ORIGINAL_URL_QUERY_PARAM = "originalUrl=";
    private String authenticationProviderUrl;
    private RSAPublicKey publicKey;
    private List<String> audiences;
    private String cookieName;
    
    public JWTRedirectAuthenticationHandler() {
        this.authenticationProviderUrl = null;
        this.publicKey = null;
        this.audiences = null;
        this.cookieName = "hadoop-jwt";
    }
    
    public void setPublicKey(final RSAPublicKey pk) {
        this.publicKey = pk;
    }
    
    @Override
    public void init(final Properties config) throws ServletException {
        super.init(config);
        this.authenticationProviderUrl = config.getProperty("authentication.provider.url");
        if (this.authenticationProviderUrl == null) {
            throw new ServletException("Authentication provider URL must not be null - configure: authentication.provider.url");
        }
        if (this.publicKey == null) {
            final String pemPublicKey = config.getProperty("public.key.pem");
            if (pemPublicKey == null) {
                throw new ServletException("Public key for signature validation must be provisioned.");
            }
            this.publicKey = CertificateUtil.parseRSAPublicKey(pemPublicKey);
        }
        final String auds = config.getProperty("expected.jwt.audiences");
        if (auds != null) {
            final String[] audArray = auds.split(",");
            this.audiences = new ArrayList<String>();
            for (final String a : audArray) {
                this.audiences.add(a);
            }
        }
        final String customCookieName = config.getProperty("jwt.cookie.name");
        if (customCookieName != null) {
            this.cookieName = customCookieName;
        }
    }
    
    @Override
    public AuthenticationToken alternateAuthenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        AuthenticationToken token = null;
        String serializedJWT = null;
        final HttpServletRequest req = request;
        serializedJWT = this.getJWTFromCookie(req);
        if (serializedJWT == null) {
            final String loginURL = this.constructLoginURL(request);
            JWTRedirectAuthenticationHandler.LOG.info("sending redirect to: " + loginURL);
            response.sendRedirect(loginURL);
        }
        else {
            String userName = null;
            SignedJWT jwtToken = null;
            boolean valid = false;
            try {
                jwtToken = SignedJWT.parse(serializedJWT);
                valid = this.validateToken(jwtToken);
                if (valid) {
                    userName = jwtToken.getJWTClaimsSet().getSubject();
                    JWTRedirectAuthenticationHandler.LOG.info("USERNAME: " + userName);
                }
                else {
                    JWTRedirectAuthenticationHandler.LOG.warn("jwtToken failed validation: " + jwtToken.serialize());
                }
            }
            catch (ParseException pe) {
                JWTRedirectAuthenticationHandler.LOG.warn("Unable to parse the JWT token", pe);
            }
            if (valid) {
                JWTRedirectAuthenticationHandler.LOG.debug("Issuing AuthenticationToken for user.");
                token = new AuthenticationToken(userName, userName, this.getType());
            }
            else {
                final String loginURL2 = this.constructLoginURL(request);
                JWTRedirectAuthenticationHandler.LOG.info("token validation failed - sending redirect to: " + loginURL2);
                response.sendRedirect(loginURL2);
            }
        }
        return token;
    }
    
    protected String getJWTFromCookie(final HttpServletRequest req) {
        String serializedJWT = null;
        final Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (this.cookieName.equals(cookie.getName())) {
                    JWTRedirectAuthenticationHandler.LOG.info(this.cookieName + " cookie has been found and is being processed");
                    serializedJWT = cookie.getValue();
                    break;
                }
            }
        }
        return serializedJWT;
    }
    
    @VisibleForTesting
    String constructLoginURL(final HttpServletRequest request) {
        String delimiter = "?";
        if (this.authenticationProviderUrl.contains("?")) {
            delimiter = "&";
        }
        final String loginURL = this.authenticationProviderUrl + delimiter + "originalUrl=" + request.getRequestURL().toString() + this.getOriginalQueryString(request);
        return loginURL;
    }
    
    private String getOriginalQueryString(final HttpServletRequest request) {
        final String originalQueryString = request.getQueryString();
        return (originalQueryString == null) ? "" : ("?" + originalQueryString);
    }
    
    protected boolean validateToken(final SignedJWT jwtToken) {
        final boolean sigValid = this.validateSignature(jwtToken);
        if (!sigValid) {
            JWTRedirectAuthenticationHandler.LOG.warn("Signature could not be verified");
        }
        final boolean audValid = this.validateAudiences(jwtToken);
        if (!audValid) {
            JWTRedirectAuthenticationHandler.LOG.warn("Audience validation failed.");
        }
        final boolean expValid = this.validateExpiration(jwtToken);
        if (!expValid) {
            JWTRedirectAuthenticationHandler.LOG.info("Expiration validation failed.");
        }
        return sigValid && audValid && expValid;
    }
    
    protected boolean validateSignature(final SignedJWT jwtToken) {
        boolean valid = false;
        if (JWSObject.State.SIGNED == jwtToken.getState()) {
            JWTRedirectAuthenticationHandler.LOG.debug("JWT token is in a SIGNED state");
            if (jwtToken.getSignature() != null) {
                JWTRedirectAuthenticationHandler.LOG.debug("JWT token signature is not null");
                try {
                    final JWSVerifier verifier = new RSASSAVerifier(this.publicKey);
                    if (jwtToken.verify(verifier)) {
                        valid = true;
                        JWTRedirectAuthenticationHandler.LOG.debug("JWT token has been successfully verified");
                    }
                    else {
                        JWTRedirectAuthenticationHandler.LOG.warn("JWT signature verification failed.");
                    }
                }
                catch (JOSEException je) {
                    JWTRedirectAuthenticationHandler.LOG.warn("Error while validating signature", je);
                }
            }
        }
        return valid;
    }
    
    protected boolean validateAudiences(final SignedJWT jwtToken) {
        boolean valid = false;
        try {
            final List<String> tokenAudienceList = jwtToken.getJWTClaimsSet().getAudience();
            if (this.audiences == null) {
                valid = true;
            }
            else {
                final boolean found = false;
                for (final String aud : tokenAudienceList) {
                    if (this.audiences.contains(aud)) {
                        JWTRedirectAuthenticationHandler.LOG.debug("JWT token audience has been successfully validated");
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    JWTRedirectAuthenticationHandler.LOG.warn("JWT audience validation failed.");
                }
            }
        }
        catch (ParseException pe) {
            JWTRedirectAuthenticationHandler.LOG.warn("Unable to parse the JWT token.", pe);
        }
        return valid;
    }
    
    protected boolean validateExpiration(final SignedJWT jwtToken) {
        boolean valid = false;
        try {
            final Date expires = jwtToken.getJWTClaimsSet().getExpirationTime();
            if (expires == null || new Date().before(expires)) {
                JWTRedirectAuthenticationHandler.LOG.debug("JWT token expiration date has been successfully validated");
                valid = true;
            }
            else {
                JWTRedirectAuthenticationHandler.LOG.warn("JWT expiration date validation failed.");
            }
        }
        catch (ParseException pe) {
            JWTRedirectAuthenticationHandler.LOG.warn("JWT expiration date validation failed.", pe);
        }
        return valid;
    }
    
    static {
        JWTRedirectAuthenticationHandler.LOG = LoggerFactory.getLogger(JWTRedirectAuthenticationHandler.class);
    }
}
