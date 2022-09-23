// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.webdav;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.client.HttpEventListener;
import org.eclipse.jetty.client.security.SecurityListener;
import org.eclipse.jetty.util.URIUtil;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.client.HttpEventListenerWrapper;

public class WebdavListener extends HttpEventListenerWrapper
{
    private static final Logger LOG;
    private HttpDestination _destination;
    private HttpExchange _exchange;
    private boolean _requestComplete;
    private boolean _responseComplete;
    private boolean _webdavEnabled;
    private boolean _needIntercept;
    
    public WebdavListener(final HttpDestination destination, final HttpExchange ex) {
        super(ex.getEventListener(), true);
        this._destination = destination;
        this._exchange = ex;
        if ("PUT".equalsIgnoreCase(this._exchange.getMethod())) {
            this._webdavEnabled = true;
        }
    }
    
    @Override
    public void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        if (!this._webdavEnabled) {
            this._needIntercept = false;
            super.onResponseStatus(version, status, reason);
            return;
        }
        if (WebdavListener.LOG.isDebugEnabled()) {
            WebdavListener.LOG.debug("WebdavListener:Response Status: " + status, new Object[0]);
        }
        if (status == 403 || status == 409) {
            if (this._webdavEnabled) {
                if (WebdavListener.LOG.isDebugEnabled()) {
                    WebdavListener.LOG.debug("WebdavListener:Response Status: dav enabled, taking a stab at resolving put issue", new Object[0]);
                }
                this.setDelegatingResponses(false);
                this._needIntercept = true;
            }
            else {
                if (WebdavListener.LOG.isDebugEnabled()) {
                    WebdavListener.LOG.debug("WebdavListener:Response Status: Webdav Disabled", new Object[0]);
                }
                this.setDelegatingResponses(true);
                this.setDelegatingRequests(true);
                this._needIntercept = false;
            }
        }
        else {
            this._needIntercept = false;
            this.setDelegatingResponses(true);
            this.setDelegatingRequests(true);
        }
        super.onResponseStatus(version, status, reason);
    }
    
    @Override
    public void onResponseComplete() throws IOException {
        this._responseComplete = true;
        if (this._needIntercept) {
            if (this._requestComplete && this._responseComplete) {
                try {
                    if (this.resolveCollectionIssues()) {
                        this.setDelegatingRequests(true);
                        this.setDelegatingResponses(true);
                        this._requestComplete = false;
                        this._responseComplete = false;
                        this._destination.resend(this._exchange);
                    }
                    else {
                        this.setDelegationResult(false);
                        this.setDelegatingRequests(true);
                        this.setDelegatingResponses(true);
                        super.onResponseComplete();
                    }
                }
                catch (IOException ioe) {
                    WebdavListener.LOG.debug("WebdavListener:Complete:IOException: might not be dealing with dav server, delegate", new Object[0]);
                    super.onResponseComplete();
                }
            }
            else {
                if (WebdavListener.LOG.isDebugEnabled()) {
                    WebdavListener.LOG.debug("WebdavListener:Not ready, calling super", new Object[0]);
                }
                super.onResponseComplete();
            }
        }
        else {
            super.onResponseComplete();
        }
    }
    
    @Override
    public void onRequestComplete() throws IOException {
        this._requestComplete = true;
        if (this._needIntercept) {
            if (this._requestComplete && this._responseComplete) {
                try {
                    if (this.resolveCollectionIssues()) {
                        this.setDelegatingRequests(true);
                        this.setDelegatingResponses(true);
                        this._requestComplete = false;
                        this._responseComplete = false;
                        this._destination.resend(this._exchange);
                    }
                    else {
                        this.setDelegatingRequests(true);
                        this.setDelegatingResponses(true);
                        super.onRequestComplete();
                    }
                }
                catch (IOException ioe) {
                    WebdavListener.LOG.debug("WebdavListener:Complete:IOException: might not be dealing with dav server, delegate", new Object[0]);
                    super.onRequestComplete();
                }
            }
            else {
                if (WebdavListener.LOG.isDebugEnabled()) {
                    WebdavListener.LOG.debug("WebdavListener:Not ready, calling super", new Object[0]);
                }
                super.onRequestComplete();
            }
        }
        else {
            super.onRequestComplete();
        }
    }
    
    private boolean resolveCollectionIssues() throws IOException {
        final String uri = this._exchange.getURI();
        final String[] uriCollection = this._exchange.getURI().split("/");
        final int checkNum = uriCollection.length;
        int rewind = 0;
        String parentUri;
        for (parentUri = URIUtil.parentPath(uri); parentUri != null && !this.checkExists(parentUri); parentUri = URIUtil.parentPath(parentUri)) {
            ++rewind;
        }
        if (this.checkWebdavSupported()) {
            for (int i = 0; i < rewind; --rewind) {
                this.makeCollection(parentUri + "/" + uriCollection[checkNum - rewind - 1]);
                parentUri = parentUri + "/" + uriCollection[checkNum - rewind - 1];
            }
            return true;
        }
        return false;
    }
    
    private boolean checkExists(final String uri) throws IOException {
        if (uri == null) {
            System.out.println("have failed miserably");
            return false;
        }
        final PropfindExchange propfindExchange = new PropfindExchange();
        propfindExchange.setAddress(this._exchange.getAddress());
        propfindExchange.setMethod("GET");
        propfindExchange.setScheme(this._exchange.getScheme());
        propfindExchange.setEventListener(new SecurityListener(this._destination, propfindExchange));
        propfindExchange.setConfigureListeners(false);
        propfindExchange.setRequestURI(uri);
        this._destination.send(propfindExchange);
        try {
            propfindExchange.waitForDone();
            return propfindExchange.exists();
        }
        catch (InterruptedException ie) {
            WebdavListener.LOG.ignore(ie);
            return false;
        }
    }
    
    private boolean makeCollection(final String uri) throws IOException {
        final MkcolExchange mkcolExchange = new MkcolExchange();
        mkcolExchange.setAddress(this._exchange.getAddress());
        mkcolExchange.setMethod("MKCOL " + uri + " HTTP/1.1");
        mkcolExchange.setScheme(this._exchange.getScheme());
        mkcolExchange.setEventListener(new SecurityListener(this._destination, mkcolExchange));
        mkcolExchange.setConfigureListeners(false);
        mkcolExchange.setRequestURI(uri);
        this._destination.send(mkcolExchange);
        try {
            mkcolExchange.waitForDone();
            return mkcolExchange.exists();
        }
        catch (InterruptedException ie) {
            WebdavListener.LOG.ignore(ie);
            return false;
        }
    }
    
    private boolean checkWebdavSupported() throws IOException {
        final WebdavSupportedExchange supportedExchange = new WebdavSupportedExchange();
        supportedExchange.setAddress(this._exchange.getAddress());
        supportedExchange.setMethod("OPTIONS");
        supportedExchange.setScheme(this._exchange.getScheme());
        supportedExchange.setEventListener(new SecurityListener(this._destination, supportedExchange));
        supportedExchange.setConfigureListeners(false);
        supportedExchange.setRequestURI(this._exchange.getURI());
        this._destination.send(supportedExchange);
        try {
            supportedExchange.waitTilCompletion();
            return supportedExchange.isWebdavSupported();
        }
        catch (InterruptedException ie) {
            WebdavListener.LOG.ignore(ie);
            return false;
        }
    }
    
    static {
        LOG = Log.getLogger(WebdavListener.class);
    }
}
