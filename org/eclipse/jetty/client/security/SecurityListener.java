// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.security;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.http.HttpHeaders;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.StringUtil;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.client.HttpEventListenerWrapper;

public class SecurityListener extends HttpEventListenerWrapper
{
    private static final Logger LOG;
    private HttpDestination _destination;
    private HttpExchange _exchange;
    private boolean _requestComplete;
    private boolean _responseComplete;
    private boolean _needIntercept;
    private int _attempts;
    
    public SecurityListener(final HttpDestination destination, final HttpExchange ex) {
        super(ex.getEventListener(), true);
        this._attempts = 0;
        this._destination = destination;
        this._exchange = ex;
    }
    
    protected String scrapeAuthenticationType(final String authString) {
        String authType;
        if (authString.indexOf(" ") == -1) {
            authType = authString.toString().trim();
        }
        else {
            final String authResponse = authString.toString();
            authType = authResponse.substring(0, authResponse.indexOf(" ")).trim();
        }
        return authType;
    }
    
    protected Map<String, String> scrapeAuthenticationDetails(String authString) {
        final Map<String, String> authenticationDetails = new HashMap<String, String>();
        authString = authString.substring(authString.indexOf(" ") + 1, authString.length());
        final StringTokenizer strtok = new StringTokenizer(authString, ",");
        while (strtok.hasMoreTokens()) {
            final String token = strtok.nextToken();
            final String[] pair = token.split("=");
            if (pair.length == 2) {
                final String itemName = pair[0].trim();
                String itemValue = pair[1].trim();
                itemValue = StringUtil.unquote(itemValue);
                authenticationDetails.put(itemName, itemValue);
            }
            else {
                SecurityListener.LOG.debug("SecurityListener: missed scraping authentication details - " + token, new Object[0]);
            }
        }
        return authenticationDetails;
    }
    
    @Override
    public void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        if (SecurityListener.LOG.isDebugEnabled()) {
            SecurityListener.LOG.debug("SecurityListener:Response Status: " + status, new Object[0]);
        }
        if (status == 401 && this._attempts < this._destination.getHttpClient().maxRetries()) {
            this.setDelegatingResponses(false);
            this._needIntercept = true;
        }
        else {
            this.setDelegatingResponses(true);
            this.setDelegatingRequests(true);
            this._needIntercept = false;
        }
        super.onResponseStatus(version, status, reason);
    }
    
    @Override
    public void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
        if (SecurityListener.LOG.isDebugEnabled()) {
            SecurityListener.LOG.debug("SecurityListener:Header: " + name.toString() + " / " + value.toString(), new Object[0]);
        }
        if (!this.isDelegatingResponses()) {
            final int header = HttpHeaders.CACHE.getOrdinal(name);
            switch (header) {
                case 51: {
                    final String authString = value.toString();
                    final String type = this.scrapeAuthenticationType(authString);
                    final Map<String, String> details = this.scrapeAuthenticationDetails(authString);
                    final String pathSpec = "/";
                    final RealmResolver realmResolver = this._destination.getHttpClient().getRealmResolver();
                    if (realmResolver == null) {
                        break;
                    }
                    final Realm realm = realmResolver.getRealm(details.get("realm"), this._destination, pathSpec);
                    if (realm == null) {
                        SecurityListener.LOG.warn("Unknown Security Realm: " + details.get("realm"), new Object[0]);
                        break;
                    }
                    if ("digest".equalsIgnoreCase(type)) {
                        this._destination.addAuthorization("/", new DigestAuthentication(realm, details));
                        break;
                    }
                    if ("basic".equalsIgnoreCase(type)) {
                        this._destination.addAuthorization(pathSpec, new BasicAuthentication(realm));
                        break;
                    }
                    break;
                }
            }
        }
        super.onResponseHeader(name, value);
    }
    
    @Override
    public void onRequestComplete() throws IOException {
        this._requestComplete = true;
        if (this._needIntercept) {
            if (this._requestComplete && this._responseComplete) {
                if (SecurityListener.LOG.isDebugEnabled()) {
                    SecurityListener.LOG.debug("onRequestComplete, Both complete: Resending from onResponseComplete " + this._exchange, new Object[0]);
                }
                this._responseComplete = false;
                this._requestComplete = false;
                this.setDelegatingRequests(true);
                this.setDelegatingResponses(true);
                this._destination.resend(this._exchange);
            }
            else {
                if (SecurityListener.LOG.isDebugEnabled()) {
                    SecurityListener.LOG.debug("onRequestComplete, Response not yet complete onRequestComplete, calling super for " + this._exchange, new Object[0]);
                }
                super.onRequestComplete();
            }
        }
        else {
            if (SecurityListener.LOG.isDebugEnabled()) {
                SecurityListener.LOG.debug("onRequestComplete, delegating to super with Request complete=" + this._requestComplete + ", response complete=" + this._responseComplete + " " + this._exchange, new Object[0]);
            }
            super.onRequestComplete();
        }
    }
    
    @Override
    public void onResponseComplete() throws IOException {
        this._responseComplete = true;
        if (this._needIntercept) {
            if (this._requestComplete && this._responseComplete) {
                if (SecurityListener.LOG.isDebugEnabled()) {
                    SecurityListener.LOG.debug("onResponseComplete, Both complete: Resending from onResponseComplete" + this._exchange, new Object[0]);
                }
                this._responseComplete = false;
                this._requestComplete = false;
                this.setDelegatingResponses(true);
                this.setDelegatingRequests(true);
                this._destination.resend(this._exchange);
            }
            else {
                if (SecurityListener.LOG.isDebugEnabled()) {
                    SecurityListener.LOG.debug("onResponseComplete, Request not yet complete from onResponseComplete,  calling super " + this._exchange, new Object[0]);
                }
                super.onResponseComplete();
            }
        }
        else {
            if (SecurityListener.LOG.isDebugEnabled()) {
                SecurityListener.LOG.debug("OnResponseComplete, delegating to super with Request complete=" + this._requestComplete + ", response complete=" + this._responseComplete + " " + this._exchange, new Object[0]);
            }
            super.onResponseComplete();
        }
    }
    
    @Override
    public void onRetry() {
        ++this._attempts;
        this.setDelegatingRequests(true);
        this.setDelegatingResponses(true);
        this._requestComplete = false;
        this._responseComplete = false;
        this._needIntercept = false;
        super.onRetry();
    }
    
    static {
        LOG = Log.getLogger(SecurityListener.class);
    }
}
