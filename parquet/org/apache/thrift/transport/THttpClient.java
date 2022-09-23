// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import java.net.HttpURLConnection;
import org.apache.http.HttpResponse;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import org.apache.http.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.client.methods.HttpPost;
import java.util.HashMap;
import java.io.IOException;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpHost;
import java.util.Map;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

public class THttpClient extends TTransport
{
    private URL url_;
    private final ByteArrayOutputStream requestBuffer_;
    private InputStream inputStream_;
    private int connectTimeout_;
    private int readTimeout_;
    private Map<String, String> customHeaders_;
    private final HttpHost host;
    private final HttpClient client;
    
    public THttpClient(final String url) throws TTransportException {
        this.url_ = null;
        this.requestBuffer_ = new ByteArrayOutputStream();
        this.inputStream_ = null;
        this.connectTimeout_ = 0;
        this.readTimeout_ = 0;
        this.customHeaders_ = null;
        try {
            this.url_ = new URL(url);
            this.client = null;
            this.host = null;
        }
        catch (IOException iox) {
            throw new TTransportException(iox);
        }
    }
    
    public THttpClient(final String url, final HttpClient client) throws TTransportException {
        this.url_ = null;
        this.requestBuffer_ = new ByteArrayOutputStream();
        this.inputStream_ = null;
        this.connectTimeout_ = 0;
        this.readTimeout_ = 0;
        this.customHeaders_ = null;
        try {
            this.url_ = new URL(url);
            this.client = client;
            this.host = new HttpHost(this.url_.getHost(), (-1 == this.url_.getPort()) ? this.url_.getDefaultPort() : this.url_.getPort(), this.url_.getProtocol());
        }
        catch (IOException iox) {
            throw new TTransportException(iox);
        }
    }
    
    public void setConnectTimeout(final int timeout) {
        this.connectTimeout_ = timeout;
        if (null != this.client) {
            this.client.getParams().setParameter("http.connection.timeout", this.connectTimeout_);
        }
    }
    
    public void setReadTimeout(final int timeout) {
        this.readTimeout_ = timeout;
        if (null != this.client) {
            this.client.getParams().setParameter("http.socket.timeout", this.readTimeout_);
        }
    }
    
    public void setCustomHeaders(final Map<String, String> headers) {
        this.customHeaders_ = headers;
    }
    
    public void setCustomHeader(final String key, final String value) {
        if (this.customHeaders_ == null) {
            this.customHeaders_ = new HashMap<String, String>();
        }
        this.customHeaders_.put(key, value);
    }
    
    @Override
    public void open() {
    }
    
    @Override
    public void close() {
        if (null != this.inputStream_) {
            try {
                this.inputStream_.close();
            }
            catch (IOException ex) {}
            this.inputStream_ = null;
        }
    }
    
    @Override
    public boolean isOpen() {
        return true;
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        if (this.inputStream_ == null) {
            throw new TTransportException("Response buffer is empty, no request.");
        }
        try {
            final int ret = this.inputStream_.read(buf, off, len);
            if (ret == -1) {
                throw new TTransportException("No more data available.");
            }
            return ret;
        }
        catch (IOException iox) {
            throw new TTransportException(iox);
        }
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) {
        this.requestBuffer_.write(buf, off, len);
    }
    
    private void flushUsingHttpClient() throws TTransportException {
        if (null == this.client) {
            throw new TTransportException("Null HttpClient, aborting.");
        }
        final byte[] data = this.requestBuffer_.toByteArray();
        this.requestBuffer_.reset();
        HttpPost post = null;
        InputStream is = null;
        try {
            post = new HttpPost(this.url_.getFile());
            post.setHeader("Content-Type", "application/x-thrift");
            post.setHeader("Accept", "application/x-thrift");
            post.setHeader("User-Agent", "Java/THttpClient/HC");
            if (null != this.customHeaders_) {
                for (final Map.Entry<String, String> header : this.customHeaders_.entrySet()) {
                    post.setHeader(header.getKey(), header.getValue());
                }
            }
            post.setEntity(new ByteArrayEntity(data));
            final HttpResponse response = this.client.execute(this.host, post);
            final int responseCode = response.getStatusLine().getStatusCode();
            is = response.getEntity().getContent();
            if (responseCode != 200) {
                throw new TTransportException("HTTP Response code: " + responseCode);
            }
            final byte[] buf = new byte[1024];
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            do {
                len = is.read(buf);
                if (len > 0) {
                    baos.write(buf, 0, len);
                }
            } while (-1 != len);
            try {
                response.getEntity().consumeContent();
            }
            catch (IOException ex) {}
            this.inputStream_ = new ByteArrayInputStream(baos.toByteArray());
        }
        catch (IOException ioe) {
            if (null != post) {
                post.abort();
            }
            throw new TTransportException(ioe);
        }
        finally {
            if (null != is) {
                try {
                    is.close();
                }
                catch (IOException ioe2) {
                    throw new TTransportException(ioe2);
                }
            }
        }
    }
    
    @Override
    public void flush() throws TTransportException {
        if (null != this.client) {
            this.flushUsingHttpClient();
            return;
        }
        final byte[] data = this.requestBuffer_.toByteArray();
        this.requestBuffer_.reset();
        try {
            final HttpURLConnection connection = (HttpURLConnection)this.url_.openConnection();
            if (this.connectTimeout_ > 0) {
                connection.setConnectTimeout(this.connectTimeout_);
            }
            if (this.readTimeout_ > 0) {
                connection.setReadTimeout(this.readTimeout_);
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-thrift");
            connection.setRequestProperty("Accept", "application/x-thrift");
            connection.setRequestProperty("User-Agent", "Java/THttpClient");
            if (this.customHeaders_ != null) {
                for (final Map.Entry<String, String> header : this.customHeaders_.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            connection.setDoOutput(true);
            connection.connect();
            connection.getOutputStream().write(data);
            final int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new TTransportException("HTTP Response code: " + responseCode);
            }
            this.inputStream_ = connection.getInputStream();
        }
        catch (IOException iox) {
            throw new TTransportException(iox);
        }
    }
    
    public static class Factory extends TTransportFactory
    {
        private final String url;
        private final HttpClient client;
        
        public Factory(final String url) {
            this.url = url;
            this.client = null;
        }
        
        public Factory(final String url, final HttpClient client) {
            this.url = url;
            this.client = client;
        }
        
        @Override
        public TTransport getTransport(final TTransport trans) {
            try {
                if (null != this.client) {
                    return new THttpClient(this.url, this.client);
                }
                return new THttpClient(this.url);
            }
            catch (TTransportException tte) {
                return null;
            }
        }
    }
}
