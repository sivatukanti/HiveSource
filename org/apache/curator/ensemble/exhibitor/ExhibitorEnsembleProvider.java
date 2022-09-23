// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.ensemble.exhibitor;

import org.apache.curator.RetryLoop;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.utils.ThreadUtils;
import org.slf4j.LoggerFactory;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.curator.RetryPolicy;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.apache.curator.ensemble.EnsembleProvider;

public class ExhibitorEnsembleProvider implements EnsembleProvider
{
    private final Logger log;
    private final AtomicReference<Exhibitors> exhibitors;
    private final AtomicReference<Exhibitors> masterExhibitors;
    private final ExhibitorRestClient restClient;
    private final String restUriPath;
    private final int pollingMs;
    private final RetryPolicy retryPolicy;
    private final ScheduledExecutorService service;
    private final Random random;
    private final AtomicReference<String> connectionString;
    private final AtomicReference<State> state;
    private static final String MIME_TYPE = "application/x-www-form-urlencoded";
    private static final String VALUE_PORT = "port";
    private static final String VALUE_COUNT = "count";
    private static final String VALUE_SERVER_PREFIX = "server";
    
    public ExhibitorEnsembleProvider(final Exhibitors exhibitors, final ExhibitorRestClient restClient, final String restUriPath, final int pollingMs, final RetryPolicy retryPolicy) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.exhibitors = new AtomicReference<Exhibitors>();
        this.masterExhibitors = new AtomicReference<Exhibitors>();
        this.service = ThreadUtils.newSingleThreadScheduledExecutor("ExhibitorEnsembleProvider");
        this.random = new Random();
        this.connectionString = new AtomicReference<String>("");
        this.state = new AtomicReference<State>(State.LATENT);
        this.exhibitors.set(exhibitors);
        this.masterExhibitors.set(exhibitors);
        this.restClient = restClient;
        this.restUriPath = restUriPath;
        this.pollingMs = pollingMs;
        this.retryPolicy = retryPolicy;
    }
    
    public void setExhibitors(final Exhibitors newExhibitors) {
        this.exhibitors.set(newExhibitors);
        this.masterExhibitors.set(newExhibitors);
    }
    
    public void pollForInitialEnsemble() throws Exception {
        Preconditions.checkState(this.state.get() == State.LATENT, (Object)"Cannot be called after start()");
        this.poll();
    }
    
    @Override
    public void start() throws Exception {
        Preconditions.checkState(this.state.compareAndSet(State.LATENT, State.STARTED), (Object)"Cannot be started more than once");
        this.service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                ExhibitorEnsembleProvider.this.poll();
            }
        }, this.pollingMs, this.pollingMs, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void close() throws IOException {
        Preconditions.checkState(this.state.compareAndSet(State.STARTED, State.CLOSED), (Object)"Already closed or has not been started");
        this.service.shutdownNow();
    }
    
    @Override
    public String getConnectionString() {
        return this.connectionString.get();
    }
    
    @VisibleForTesting
    protected void poll() {
        final Exhibitors localExhibitors = this.exhibitors.get();
        Map<String, String> values = this.queryExhibitors(localExhibitors);
        int count = this.getCountFromValues(values);
        if (count == 0) {
            this.log.warn("0 count returned from Exhibitors. Using backup connection values.");
            values = this.useBackup(localExhibitors);
            count = this.getCountFromValues(values);
        }
        if (count > 0) {
            final int port = Integer.parseInt(values.get("port"));
            final StringBuilder newConnectionString = new StringBuilder();
            final List<String> newHostnames = (List<String>)Lists.newArrayList();
            for (int i = 0; i < count; ++i) {
                if (newConnectionString.length() > 0) {
                    newConnectionString.append(",");
                }
                final String server = values.get("server" + i);
                newConnectionString.append(server).append(":").append(port);
                newHostnames.add(server);
            }
            final String newConnectionStringValue = newConnectionString.toString();
            if (!newConnectionStringValue.equals(this.connectionString.get())) {
                this.log.info(String.format("Connection string has changed. Old value (%s), new value (%s)", this.connectionString.get(), newConnectionStringValue));
            }
            final Exhibitors newExhibitors = new Exhibitors(newHostnames, localExhibitors.getRestPort(), new Exhibitors.BackupConnectionStringProvider() {
                @Override
                public String getBackupConnectionString() throws Exception {
                    return ExhibitorEnsembleProvider.this.masterExhibitors.get().getBackupConnectionString();
                }
            });
            this.connectionString.set(newConnectionStringValue);
            this.exhibitors.set(newExhibitors);
        }
    }
    
    private int getCountFromValues(final Map<String, String> values) {
        try {
            return Integer.parseInt(values.get("count"));
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }
    
    private Map<String, String> useBackup(final Exhibitors localExhibitors) {
        final Map<String, String> values = this.newValues();
        try {
            final String backupConnectionString = localExhibitors.getBackupConnectionString();
            int thePort = -1;
            int count = 0;
            for (String spec : backupConnectionString.split(",")) {
                spec = spec.trim();
                final String[] parts = spec.split(":");
                if (parts.length == 2) {
                    final String hostname = parts[0];
                    final int port = Integer.parseInt(parts[1]);
                    if (thePort < 0) {
                        thePort = port;
                    }
                    else if (port != thePort) {
                        this.log.warn("Inconsistent port in connection component: " + spec);
                    }
                    values.put("server" + count, hostname);
                    ++count;
                }
                else {
                    this.log.warn("Bad backup connection component: " + spec);
                }
            }
            values.put("count", Integer.toString(count));
            values.put("port", Integer.toString(thePort));
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.log.error("Couldn't get backup connection string", e);
        }
        return values;
    }
    
    private Map<String, String> newValues() {
        final Map<String, String> values = (Map<String, String>)Maps.newHashMap();
        values.put("count", "0");
        return values;
    }
    
    private static Map<String, String> decodeExhibitorList(final String str) throws UnsupportedEncodingException {
        final Map<String, String> values = (Map<String, String>)Maps.newHashMap();
        for (final String spec : str.split("&")) {
            final String[] parts = spec.split("=");
            if (parts.length == 2) {
                values.put(parts[0], URLDecoder.decode(parts[1], "UTF-8"));
            }
        }
        return values;
    }
    
    private Map<String, String> queryExhibitors(final Exhibitors localExhibitors) {
        final Map<String, String> values = this.newValues();
        final long start = System.currentTimeMillis();
        int retries = 0;
        boolean done = false;
        while (!done) {
            final List<String> hostnames = (List<String>)Lists.newArrayList((Iterable<?>)localExhibitors.getHostnames());
            if (hostnames.size() == 0) {
                done = true;
            }
            else {
                final String hostname = hostnames.get(this.random.nextInt(hostnames.size()));
                try {
                    final String encoded = this.restClient.getRaw(hostname, localExhibitors.getRestPort(), this.restUriPath, "application/x-www-form-urlencoded");
                    values.putAll(decodeExhibitorList(encoded));
                    done = true;
                }
                catch (Throwable e) {
                    ThreadUtils.checkInterrupted(e);
                    if (this.retryPolicy.allowRetry(retries++, System.currentTimeMillis() - start, RetryLoop.getDefaultRetrySleeper())) {
                        this.log.warn("Couldn't get servers from Exhibitor. Retrying.", e);
                    }
                    else {
                        this.log.error("Couldn't get servers from Exhibitor. Giving up.", e);
                        done = true;
                    }
                }
            }
        }
        return values;
    }
    
    private enum State
    {
        LATENT, 
        STARTED, 
        CLOSED;
    }
}
