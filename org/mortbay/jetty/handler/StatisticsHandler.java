// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Response;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class StatisticsHandler extends AbstractStatisticsHandler
{
    private transient long _statsStartedAt;
    private transient int _requests;
    private transient long _minRequestTime;
    private transient long _maxRequestTime;
    private transient long _totalRequestTime;
    private transient int _requestsActive;
    private transient int _requestsActiveMax;
    private transient int _responses1xx;
    private transient int _responses2xx;
    private transient int _responses3xx;
    private transient int _responses4xx;
    private transient int _responses5xx;
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        synchronized (this) {
            ++this._requests;
            ++this._requestsActive;
            if (this._requestsActive > this._requestsActiveMax) {
                this._requestsActiveMax = this._requestsActive;
            }
        }
        final long requestStartTime = System.currentTimeMillis();
        try {
            super.handle(target, request, response, dispatch);
        }
        finally {
            final long requestTime = System.currentTimeMillis() - requestStartTime;
            synchronized (this) {
                --this._requestsActive;
                if (this._requestsActive < 0) {
                    this._requestsActive = 0;
                }
                this._totalRequestTime += requestTime;
                if (requestTime < this._minRequestTime || this._minRequestTime == 0L) {
                    this._minRequestTime = requestTime;
                }
                if (requestTime > this._maxRequestTime) {
                    this._maxRequestTime = requestTime;
                }
                final Response jettyResponse = (Response)((response instanceof Response) ? response : HttpConnection.getCurrentConnection().getResponse());
                switch (jettyResponse.getStatus() / 100) {
                    case 0: {
                        ++this._responses1xx;
                        break;
                    }
                    case 1: {
                        ++this._responses2xx;
                        break;
                    }
                    case 2: {
                        ++this._responses3xx;
                        break;
                    }
                    case 3: {
                        ++this._responses4xx;
                        break;
                    }
                    case 4: {
                        ++this._responses5xx;
                        break;
                    }
                }
            }
        }
    }
    
    public void statsReset() {
        synchronized (this) {
            this._statsStartedAt = System.currentTimeMillis();
            this._requests = 0;
            this._minRequestTime = 0L;
            this._maxRequestTime = 0L;
            this._totalRequestTime = 0L;
            this._requestsActiveMax = this._requestsActive;
            this._requestsActive = 0;
            this._responses1xx = 0;
            this._responses2xx = 0;
            this._responses3xx = 0;
            this._responses4xx = 0;
            this._responses5xx = 0;
        }
    }
    
    public int getRequests() {
        synchronized (this) {
            return this._requests;
        }
    }
    
    public int getRequestsActive() {
        synchronized (this) {
            return this._requestsActive;
        }
    }
    
    public int getRequestsActiveMax() {
        synchronized (this) {
            return this._requestsActiveMax;
        }
    }
    
    public int getResponses1xx() {
        synchronized (this) {
            return this._responses1xx;
        }
    }
    
    public int getResponses2xx() {
        synchronized (this) {
            return this._responses2xx;
        }
    }
    
    public int getResponses3xx() {
        synchronized (this) {
            return this._responses3xx;
        }
    }
    
    public int getResponses4xx() {
        synchronized (this) {
            return this._responses4xx;
        }
    }
    
    public int getResponses5xx() {
        synchronized (this) {
            return this._responses5xx;
        }
    }
    
    public long getStatsOnMs() {
        synchronized (this) {
            return System.currentTimeMillis() - this._statsStartedAt;
        }
    }
    
    public long getRequestTimeMin() {
        synchronized (this) {
            return this._minRequestTime;
        }
    }
    
    public long getRequestTimeMax() {
        synchronized (this) {
            return this._maxRequestTime;
        }
    }
    
    public long getRequestTimeTotal() {
        synchronized (this) {
            return this._totalRequestTime;
        }
    }
    
    public long getRequestTimeAverage() {
        synchronized (this) {
            return (this._requests == 0) ? 0L : (this._totalRequestTime / this._requests);
        }
    }
}
