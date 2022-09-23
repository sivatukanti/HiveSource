// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.client.api.impl;

import java.net.URL;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.cli.HelpFormatter;
import java.util.Iterator;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomains;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import java.security.GeneralSecurityException;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import org.apache.hadoop.security.ssl.SSLFactory;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import com.sun.jersey.api.client.WebResource;
import java.net.ConnectException;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.io.IOException;
import com.sun.jersey.api.client.ClientResponse;
import java.util.Arrays;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import com.sun.jersey.api.client.config.ClientConfig;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import org.apache.hadoop.security.token.delegation.web.PseudoDelegationTokenAuthenticator;
import org.apache.hadoop.security.token.delegation.web.KerberosDelegationTokenAuthenticator;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.webapp.YarnJacksonJaxbJsonProvider;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import java.net.URI;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenAuthenticatedURL;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenAuthenticator;
import org.apache.hadoop.security.authentication.client.ConnectionConfigurator;
import com.sun.jersey.api.client.Client;
import org.apache.commons.cli.Options;
import com.google.common.base.Joiner;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.client.api.TimelineClient;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TimelineClientImpl extends TimelineClient
{
    private static final Log LOG;
    private static final String RESOURCE_URI_STR = "/ws/v1/timeline/";
    private static final Joiner JOINER;
    public static final int DEFAULT_SOCKET_TIMEOUT = 60000;
    private static Options opts;
    private static final String ENTITY_DATA_TYPE = "entity";
    private static final String DOMAIN_DATA_TYPE = "domain";
    private Client client;
    private ConnectionConfigurator connConfigurator;
    private DelegationTokenAuthenticator authenticator;
    private DelegationTokenAuthenticatedURL.Token token;
    private URI resURI;
    private boolean isEnabled;
    @InterfaceAudience.Private
    @VisibleForTesting
    TimelineClientConnectionRetry connectionRetry;
    private static final ConnectionConfigurator DEFAULT_TIMEOUT_CONN_CONFIGURATOR;
    
    public TimelineClientImpl() {
        super(TimelineClientImpl.class.getName());
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        if (!(this.isEnabled = conf.getBoolean("yarn.timeline-service.enabled", false))) {
            TimelineClientImpl.LOG.info("Timeline service is not enabled");
        }
        else {
            final ClientConfig cc = new DefaultClientConfig();
            cc.getClasses().add(YarnJacksonJaxbJsonProvider.class);
            this.connConfigurator = newConnConfigurator(conf);
            if (UserGroupInformation.isSecurityEnabled()) {
                this.authenticator = new KerberosDelegationTokenAuthenticator();
            }
            else {
                this.authenticator = new PseudoDelegationTokenAuthenticator();
            }
            this.authenticator.setConnectionConfigurator(this.connConfigurator);
            this.token = new DelegationTokenAuthenticatedURL.Token();
            this.connectionRetry = new TimelineClientConnectionRetry(conf);
            this.client = new Client(new URLConnectionClientHandler(new TimelineURLConnectionFactory()), cc);
            final TimelineJerseyRetryFilter retryFilter = new TimelineJerseyRetryFilter();
            this.client.addFilter(retryFilter);
            if (YarnConfiguration.useHttps(conf)) {
                this.resURI = URI.create(TimelineClientImpl.JOINER.join("https://", conf.get("yarn.timeline-service.webapp.https.address", "0.0.0.0:8190"), "/ws/v1/timeline/"));
            }
            else {
                this.resURI = URI.create(TimelineClientImpl.JOINER.join("http://", conf.get("yarn.timeline-service.webapp.address", "0.0.0.0:8188"), "/ws/v1/timeline/"));
            }
            TimelineClientImpl.LOG.info("Timeline service address: " + this.resURI);
        }
        super.serviceInit(conf);
    }
    
    @Override
    public TimelinePutResponse putEntities(final TimelineEntity... entities) throws IOException, YarnException {
        if (!this.isEnabled) {
            if (TimelineClientImpl.LOG.isDebugEnabled()) {
                TimelineClientImpl.LOG.debug("Nothing will be put because timeline service is not enabled");
            }
            return new TimelinePutResponse();
        }
        final TimelineEntities entitiesContainer = new TimelineEntities();
        entitiesContainer.addEntities(Arrays.asList(entities));
        final ClientResponse resp = this.doPosting(entitiesContainer, null);
        return resp.getEntity(TimelinePutResponse.class);
    }
    
    @Override
    public void putDomain(final TimelineDomain domain) throws IOException, YarnException {
        if (!this.isEnabled) {
            if (TimelineClientImpl.LOG.isDebugEnabled()) {
                TimelineClientImpl.LOG.debug("Nothing will be put because timeline service is not enabled");
            }
            return;
        }
        this.doPosting(domain, "domain");
    }
    
    private ClientResponse doPosting(final Object obj, final String path) throws IOException, YarnException {
        ClientResponse resp;
        try {
            resp = this.doPostingObject(obj, path);
        }
        catch (RuntimeException re) {
            final String msg = "Failed to get the response from the timeline server.";
            TimelineClientImpl.LOG.error(msg, re);
            throw re;
        }
        if (resp == null || resp.getClientResponseStatus() != ClientResponse.Status.OK) {
            final String msg2 = "Failed to get the response from the timeline server.";
            TimelineClientImpl.LOG.error(msg2);
            if (TimelineClientImpl.LOG.isDebugEnabled() && resp != null) {
                final String output = resp.getEntity(String.class);
                TimelineClientImpl.LOG.debug("HTTP error code: " + resp.getStatus() + " Server response : \n" + output);
            }
            throw new YarnException(msg2);
        }
        return resp;
    }
    
    @Override
    public Token<TimelineDelegationTokenIdentifier> getDelegationToken(final String renewer) throws IOException, YarnException {
        final boolean isProxyAccess = UserGroupInformation.getCurrentUser().getAuthenticationMethod() == UserGroupInformation.AuthenticationMethod.PROXY;
        final String doAsUser = isProxyAccess ? UserGroupInformation.getCurrentUser().getShortUserName() : null;
        final PrivilegedExceptionAction<Token<TimelineDelegationTokenIdentifier>> getDTAction = new PrivilegedExceptionAction<Token<TimelineDelegationTokenIdentifier>>() {
            @Override
            public Token<TimelineDelegationTokenIdentifier> run() throws Exception {
                final DelegationTokenAuthenticatedURL authUrl = new DelegationTokenAuthenticatedURL(TimelineClientImpl.this.authenticator, TimelineClientImpl.this.connConfigurator);
                return (Token<TimelineDelegationTokenIdentifier>)authUrl.getDelegationToken(TimelineClientImpl.this.resURI.toURL(), TimelineClientImpl.this.token, renewer, doAsUser);
            }
        };
        return (Token<TimelineDelegationTokenIdentifier>)this.operateDelegationToken(getDTAction);
    }
    
    @Override
    public long renewDelegationToken(final Token<TimelineDelegationTokenIdentifier> timelineDT) throws IOException, YarnException {
        final boolean isProxyAccess = UserGroupInformation.getCurrentUser().getAuthenticationMethod() == UserGroupInformation.AuthenticationMethod.PROXY;
        final String doAsUser = isProxyAccess ? UserGroupInformation.getCurrentUser().getShortUserName() : null;
        final PrivilegedExceptionAction<Long> renewDTAction = new PrivilegedExceptionAction<Long>() {
            @Override
            public Long run() throws Exception {
                if (!timelineDT.equals(TimelineClientImpl.this.token.getDelegationToken())) {
                    TimelineClientImpl.this.token.setDelegationToken(timelineDT);
                }
                final DelegationTokenAuthenticatedURL authUrl = new DelegationTokenAuthenticatedURL(TimelineClientImpl.this.authenticator, TimelineClientImpl.this.connConfigurator);
                return authUrl.renewDelegationToken(TimelineClientImpl.this.resURI.toURL(), TimelineClientImpl.this.token, doAsUser);
            }
        };
        return (long)this.operateDelegationToken(renewDTAction);
    }
    
    @Override
    public void cancelDelegationToken(final Token<TimelineDelegationTokenIdentifier> timelineDT) throws IOException, YarnException {
        final boolean isProxyAccess = UserGroupInformation.getCurrentUser().getAuthenticationMethod() == UserGroupInformation.AuthenticationMethod.PROXY;
        final String doAsUser = isProxyAccess ? UserGroupInformation.getCurrentUser().getShortUserName() : null;
        final PrivilegedExceptionAction<Void> cancelDTAction = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                if (!timelineDT.equals(TimelineClientImpl.this.token.getDelegationToken())) {
                    TimelineClientImpl.this.token.setDelegationToken(timelineDT);
                }
                final DelegationTokenAuthenticatedURL authUrl = new DelegationTokenAuthenticatedURL(TimelineClientImpl.this.authenticator, TimelineClientImpl.this.connConfigurator);
                authUrl.cancelDelegationToken(TimelineClientImpl.this.resURI.toURL(), TimelineClientImpl.this.token, doAsUser);
                return null;
            }
        };
        this.operateDelegationToken(cancelDTAction);
    }
    
    private Object operateDelegationToken(final PrivilegedExceptionAction<?> action) throws IOException, YarnException {
        final TimelineClientRetryOp tokenRetryOp = new TimelineClientRetryOp() {
            @Override
            public Object run() throws IOException {
                final boolean isProxyAccess = UserGroupInformation.getCurrentUser().getAuthenticationMethod() == UserGroupInformation.AuthenticationMethod.PROXY;
                final UserGroupInformation callerUGI = isProxyAccess ? UserGroupInformation.getCurrentUser().getRealUser() : UserGroupInformation.getCurrentUser();
                try {
                    return callerUGI.doAs(action);
                }
                catch (UndeclaredThrowableException e) {
                    throw new IOException(e.getCause());
                }
                catch (InterruptedException e2) {
                    throw new IOException(e2);
                }
            }
            
            @Override
            public boolean shouldRetryOn(final Exception e) {
                return e instanceof ConnectException;
            }
        };
        return this.connectionRetry.retryOn(tokenRetryOp);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public ClientResponse doPostingObject(final Object object, final String path) {
        final WebResource webResource = this.client.resource(this.resURI);
        if (path == null) {
            return webResource.accept("application/json").type("application/json").post(ClientResponse.class, object);
        }
        if (path.equals("domain")) {
            return webResource.path(path).accept("application/json").type("application/json").put(ClientResponse.class, object);
        }
        throw new YarnRuntimeException("Unknown resource type");
    }
    
    private static ConnectionConfigurator newConnConfigurator(final Configuration conf) {
        try {
            return newSslConnConfigurator(60000, conf);
        }
        catch (Exception e) {
            TimelineClientImpl.LOG.debug("Cannot load customized ssl related configuration. Fallback to system-generic settings.", e);
            return TimelineClientImpl.DEFAULT_TIMEOUT_CONN_CONFIGURATOR;
        }
    }
    
    private static ConnectionConfigurator newSslConnConfigurator(final int timeout, final Configuration conf) throws IOException, GeneralSecurityException {
        final SSLFactory factory = new SSLFactory(SSLFactory.Mode.CLIENT, conf);
        factory.init();
        final SSLSocketFactory sf = factory.createSSLSocketFactory();
        final HostnameVerifier hv = factory.getHostnameVerifier();
        return new ConnectionConfigurator() {
            @Override
            public HttpURLConnection configure(final HttpURLConnection conn) throws IOException {
                if (conn instanceof HttpsURLConnection) {
                    final HttpsURLConnection c = (HttpsURLConnection)conn;
                    c.setSSLSocketFactory(sf);
                    c.setHostnameVerifier(hv);
                }
                setTimeouts(conn, timeout);
                return conn;
            }
        };
    }
    
    private static void setTimeouts(final URLConnection connection, final int socketTimeout) {
        connection.setConnectTimeout(socketTimeout);
        connection.setReadTimeout(socketTimeout);
    }
    
    public static void main(final String[] argv) throws Exception {
        final CommandLine cliParser = new GnuParser().parse(TimelineClientImpl.opts, argv);
        if (cliParser.hasOption("put")) {
            final String path = cliParser.getOptionValue("put");
            if (path != null && path.length() > 0) {
                if (cliParser.hasOption("entity")) {
                    putTimelineDataInJSONFile(path, "entity");
                    return;
                }
                if (cliParser.hasOption("domain")) {
                    putTimelineDataInJSONFile(path, "domain");
                    return;
                }
            }
        }
        printUsage();
    }
    
    private static void putTimelineDataInJSONFile(final String path, final String type) {
        final File jsonFile = new File(path);
        if (!jsonFile.exists()) {
            TimelineClientImpl.LOG.error("File [" + jsonFile.getAbsolutePath() + "] doesn't exist");
            return;
        }
        final ObjectMapper mapper = new ObjectMapper();
        YarnJacksonJaxbJsonProvider.configObjectMapper(mapper);
        TimelineEntities entities = null;
        TimelineDomains domains = null;
        try {
            if (type.equals("entity")) {
                entities = mapper.readValue(jsonFile, TimelineEntities.class);
            }
            else if (type.equals("domain")) {
                domains = mapper.readValue(jsonFile, TimelineDomains.class);
            }
        }
        catch (Exception e) {
            TimelineClientImpl.LOG.error("Error when reading  " + e.getMessage());
            e.printStackTrace(System.err);
            return;
        }
        final Configuration conf = new YarnConfiguration();
        final TimelineClient client = TimelineClient.createTimelineClient();
        client.init(conf);
        client.start();
        try {
            if (UserGroupInformation.isSecurityEnabled() && conf.getBoolean("yarn.timeline-service.enabled", false)) {
                final Token<TimelineDelegationTokenIdentifier> token = client.getDelegationToken(UserGroupInformation.getCurrentUser().getUserName());
                UserGroupInformation.getCurrentUser().addToken(token);
            }
            if (type.equals("entity")) {
                final TimelinePutResponse response = client.putEntities((TimelineEntity[])entities.getEntities().toArray(new TimelineEntity[entities.getEntities().size()]));
                if (response.getErrors().size() == 0) {
                    TimelineClientImpl.LOG.info("Timeline entities are successfully put");
                }
                else {
                    for (final TimelinePutResponse.TimelinePutError error : response.getErrors()) {
                        TimelineClientImpl.LOG.error("TimelineEntity [" + error.getEntityType() + ":" + error.getEntityId() + "] is not successfully put. Error code: " + error.getErrorCode());
                    }
                }
            }
            else if (type.equals("domain")) {
                boolean hasError = false;
                for (final TimelineDomain domain : domains.getDomains()) {
                    try {
                        client.putDomain(domain);
                    }
                    catch (Exception e2) {
                        TimelineClientImpl.LOG.error("Error when putting domain " + domain.getId(), e2);
                        hasError = true;
                    }
                }
                if (!hasError) {
                    TimelineClientImpl.LOG.info("Timeline domains are successfully put");
                }
            }
        }
        catch (RuntimeException e3) {
            TimelineClientImpl.LOG.error("Error when putting the timeline data", e3);
        }
        catch (Exception e4) {
            TimelineClientImpl.LOG.error("Error when putting the timeline data", e4);
        }
        finally {
            client.stop();
        }
    }
    
    private static void printUsage() {
        new HelpFormatter().printHelp("TimelineClient", TimelineClientImpl.opts);
    }
    
    static {
        LOG = LogFactory.getLog(TimelineClientImpl.class);
        JOINER = Joiner.on("");
        (TimelineClientImpl.opts = new Options()).addOption("put", true, "Put the timeline entities/domain in a JSON file");
        TimelineClientImpl.opts.getOption("put").setArgName("Path to the JSON file");
        TimelineClientImpl.opts.addOption("entity", false, "Specify the JSON file contains the entities");
        TimelineClientImpl.opts.addOption("domain", false, "Specify the JSON file contains the domain");
        TimelineClientImpl.opts.addOption("help", false, "Print usage");
        DEFAULT_TIMEOUT_CONN_CONFIGURATOR = new ConnectionConfigurator() {
            @Override
            public HttpURLConnection configure(final HttpURLConnection conn) throws IOException {
                setTimeouts(conn, 60000);
                return conn;
            }
        };
    }
    
    private abstract static class TimelineClientRetryOp
    {
        public abstract Object run() throws IOException;
        
        public abstract boolean shouldRetryOn(final Exception p0);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    static class TimelineClientConnectionRetry
    {
        @InterfaceAudience.Private
        @VisibleForTesting
        public int maxRetries;
        @InterfaceAudience.Private
        @VisibleForTesting
        public long retryInterval;
        @InterfaceAudience.Private
        @VisibleForTesting
        public boolean retried;
        
        public TimelineClientConnectionRetry(final Configuration conf) {
            this.retried = false;
            this.maxRetries = conf.getInt("yarn.timeline-service.client.max-retries", 30);
            this.retryInterval = conf.getLong("yarn.timeline-service.client.retry-interval-ms", 1000L);
        }
        
        public Object retryOn(final TimelineClientRetryOp op) throws RuntimeException, IOException {
            int leftRetries = this.maxRetries;
            this.retried = false;
            while (true) {
                try {
                    return op.run();
                }
                catch (IOException e) {
                    if (leftRetries == 0) {
                        break;
                    }
                    if (!op.shouldRetryOn(e)) {
                        throw e;
                    }
                    this.logException(e, leftRetries);
                }
                catch (RuntimeException e2) {
                    if (leftRetries == 0) {
                        break;
                    }
                    if (!op.shouldRetryOn(e2)) {
                        throw e2;
                    }
                    this.logException(e2, leftRetries);
                }
                if (leftRetries > 0) {
                    --leftRetries;
                }
                this.retried = true;
                try {
                    Thread.sleep(this.retryInterval);
                }
                catch (InterruptedException ie) {
                    TimelineClientImpl.LOG.warn("Client retry sleep interrupted! ");
                }
            }
            throw new RuntimeException("Failed to connect to timeline server. Connection retries limit exceeded. The posted timeline event may be missing");
        }
        
        private void logException(final Exception e, final int leftRetries) {
            if (leftRetries > 0) {
                TimelineClientImpl.LOG.info("Exception caught by TimelineClientConnectionRetry, will try " + leftRetries + " more time(s).\nMessage: " + e.getMessage());
            }
            else {
                TimelineClientImpl.LOG.info("ConnectionException caught by TimelineClientConnectionRetry, will keep retrying.\nMessage: " + e.getMessage());
            }
        }
    }
    
    private class TimelineJerseyRetryFilter extends ClientFilter
    {
        @Override
        public ClientResponse handle(final ClientRequest cr) throws ClientHandlerException {
            final TimelineClientRetryOp jerseyRetryOp = new TimelineClientRetryOp() {
                @Override
                public Object run() {
                    return TimelineJerseyRetryFilter.this.getNext().handle(cr);
                }
                
                @Override
                public boolean shouldRetryOn(final Exception e) {
                    return e instanceof ClientHandlerException && e.getCause() instanceof ConnectException;
                }
            };
            try {
                return (ClientResponse)TimelineClientImpl.this.connectionRetry.retryOn(jerseyRetryOp);
            }
            catch (IOException e) {
                throw new ClientHandlerException("Jersey retry failed!\nMessage: " + e.getMessage());
            }
        }
    }
    
    private class TimelineURLConnectionFactory implements HttpURLConnectionFactory
    {
        @Override
        public HttpURLConnection getHttpURLConnection(final URL url) throws IOException {
            final boolean isProxyAccess = UserGroupInformation.getCurrentUser().getAuthenticationMethod() == UserGroupInformation.AuthenticationMethod.PROXY;
            final UserGroupInformation callerUGI = isProxyAccess ? UserGroupInformation.getCurrentUser().getRealUser() : UserGroupInformation.getCurrentUser();
            final String doAsUser = isProxyAccess ? UserGroupInformation.getCurrentUser().getShortUserName() : null;
            try {
                return callerUGI.doAs((PrivilegedExceptionAction<HttpURLConnection>)new PrivilegedExceptionAction<HttpURLConnection>() {
                    @Override
                    public HttpURLConnection run() throws Exception {
                        return new DelegationTokenAuthenticatedURL(TimelineClientImpl.this.authenticator, TimelineClientImpl.this.connConfigurator).openConnection(url, TimelineClientImpl.this.token, doAsUser);
                    }
                });
            }
            catch (UndeclaredThrowableException e) {
                throw new IOException(e.getCause());
            }
            catch (InterruptedException e2) {
                throw new IOException(e2);
            }
        }
    }
}
