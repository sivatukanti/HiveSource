// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.http.HttpHeaders;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;

public class RedirectListener extends HttpEventListenerWrapper
{
    private final HttpExchange _exchange;
    private HttpDestination _destination;
    private String _location;
    private int _attempts;
    private boolean _requestComplete;
    private boolean _responseComplete;
    private boolean _redirected;
    
    public RedirectListener(final HttpDestination destination, final HttpExchange ex) {
        super(ex.getEventListener(), true);
        this._destination = destination;
        this._exchange = ex;
    }
    
    @Override
    public void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        this._redirected = ((status == 301 || status == 302) && this._attempts < this._destination.getHttpClient().maxRedirects());
        if (this._redirected) {
            this.setDelegatingRequests(false);
            this.setDelegatingResponses(false);
        }
        super.onResponseStatus(version, status, reason);
    }
    
    @Override
    public void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
        if (this._redirected) {
            final int header = HttpHeaders.CACHE.getOrdinal(name);
            switch (header) {
                case 45: {
                    this._location = value.toString();
                    break;
                }
            }
        }
        super.onResponseHeader(name, value);
    }
    
    @Override
    public void onRequestComplete() throws IOException {
        this._requestComplete = true;
        if (this.checkExchangeComplete()) {
            super.onRequestComplete();
        }
    }
    
    @Override
    public void onResponseComplete() throws IOException {
        this._responseComplete = true;
        if (this.checkExchangeComplete()) {
            super.onResponseComplete();
        }
    }
    
    public boolean checkExchangeComplete() throws IOException {
        if (this._redirected && this._requestComplete && this._responseComplete) {
            if (this._location != null) {
                if (this._location.indexOf("://") > 0) {
                    this._exchange.setURL(this._location);
                }
                else {
                    this._exchange.setRequestURI(this._location);
                }
                final boolean isHttps = "https".equals(String.valueOf(this._exchange.getScheme()));
                final HttpDestination destination = this._destination.getHttpClient().getDestination(this._exchange.getAddress(), isHttps);
                if (this._destination == destination) {
                    this._destination.resend(this._exchange);
                }
                else {
                    HttpEventListener listener;
                    for (listener = this; listener instanceof HttpEventListenerWrapper; listener = ((HttpEventListenerWrapper)listener).getEventListener()) {}
                    this._exchange.getEventListener().onRetry();
                    this._exchange.reset();
                    this._exchange.setEventListener(listener);
                    final Address address = this._exchange.getAddress();
                    final int port = address.getPort();
                    final StringBuilder hostHeader = new StringBuilder(64);
                    hostHeader.append(address.getHost());
                    if ((port != 80 || isHttps) && (port != 443 || !isHttps)) {
                        hostHeader.append(':');
                        hostHeader.append(port);
                    }
                    this._exchange.setRequestHeader("Host", hostHeader.toString());
                    destination.send(this._exchange);
                }
                return false;
            }
            this.setDelegationResult(false);
        }
        return true;
    }
    
    @Override
    public void onRetry() {
        this._redirected = false;
        ++this._attempts;
        this.setDelegatingRequests(true);
        this.setDelegatingResponses(true);
        this._requestComplete = false;
        this._responseComplete = false;
        super.onRetry();
    }
    
    @Override
    public void onConnectionFailed(final Throwable ex) {
        this.setDelegatingRequests(true);
        this.setDelegatingResponses(true);
        super.onConnectionFailed(ex);
    }
    
    @Override
    public void onException(final Throwable ex) {
        this.setDelegatingRequests(true);
        this.setDelegatingResponses(true);
        super.onException(ex);
    }
}
