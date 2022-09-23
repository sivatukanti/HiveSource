// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.slf4j.LoggerFactory;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginException;
import java.util.Date;
import javax.security.auth.login.Configuration;
import javax.security.auth.kerberos.KerberosTicket;
import org.apache.zookeeper.common.Time;
import javax.security.auth.login.LoginContext;
import java.util.Random;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import org.slf4j.Logger;

public class Login
{
    private static final Logger LOG;
    public CallbackHandler callbackHandler;
    private static final float TICKET_RENEW_WINDOW = 0.8f;
    private static final float TICKET_RENEW_JITTER = 0.05f;
    private static final long MIN_TIME_BEFORE_RELOGIN = 60000L;
    private Subject subject;
    private Thread t;
    private boolean isKrbTicket;
    private boolean isUsingTicketCache;
    private boolean isUsingKeytab;
    private static Random rng;
    private LoginContext login;
    private String loginContextName;
    private String keytabFile;
    private String principal;
    private long lastLogin;
    
    public Login(final String loginContextName, final CallbackHandler callbackHandler) throws LoginException {
        this.subject = null;
        this.t = null;
        this.isKrbTicket = false;
        this.isUsingTicketCache = false;
        this.isUsingKeytab = false;
        this.login = null;
        this.loginContextName = null;
        this.keytabFile = null;
        this.principal = null;
        this.lastLogin = Time.currentElapsedTime() - 60000L;
        this.callbackHandler = callbackHandler;
        this.login = this.login(loginContextName);
        this.loginContextName = loginContextName;
        this.subject = this.login.getSubject();
        this.isKrbTicket = !this.subject.getPrivateCredentials(KerberosTicket.class).isEmpty();
        final AppConfigurationEntry[] appConfigurationEntry;
        final AppConfigurationEntry[] entries = appConfigurationEntry = Configuration.getConfiguration().getAppConfigurationEntry(loginContextName);
        final int length = appConfigurationEntry.length;
        final int n = 0;
        if (n < length) {
            final AppConfigurationEntry entry = appConfigurationEntry[n];
            if (entry.getOptions().get("useTicketCache") != null) {
                final String val = (String)entry.getOptions().get("useTicketCache");
                if (val.equals("true")) {
                    this.isUsingTicketCache = true;
                }
            }
            if (entry.getOptions().get("keyTab") != null) {
                this.keytabFile = (String)entry.getOptions().get("keyTab");
                this.isUsingKeytab = true;
            }
            if (entry.getOptions().get("principal") != null) {
                this.principal = (String)entry.getOptions().get("principal");
            }
        }
        if (!this.isKrbTicket) {
            return;
        }
        (this.t = new Thread(new Runnable() {
            @Override
            public void run() {
                Login.LOG.info("TGT refresh thread started.");
                while (true) {
                    final KerberosTicket tgt = Login.this.getTGT();
                    final long now = Time.currentWallTime();
                    long nextRefresh;
                    Date nextRefreshDate;
                    if (tgt == null) {
                        nextRefresh = now + 60000L;
                        nextRefreshDate = new Date(nextRefresh);
                        Login.LOG.warn("No TGT found: will try again at " + nextRefreshDate);
                    }
                    else {
                        nextRefresh = Login.this.getRefreshTime(tgt);
                        final long expiry = tgt.getEndTime().getTime();
                        final Date expiryDate = new Date(expiry);
                        if (Login.this.isUsingTicketCache && tgt.getEndTime().equals(tgt.getRenewTill())) {
                            Login.LOG.error("The TGT cannot be renewed beyond the next expiry date: " + expiryDate + ".This process will not be able to authenticate new SASL connections after that time (for example, it will not be authenticate a new connection with a Zookeeper Quorum member).  Ask your system administrator to either increase the 'renew until' time by doing : 'modprinc -maxrenewlife " + Login.this.principal + "' within kadmin, or instead, to generate a keytab for " + Login.this.principal + ". Because the TGT's expiry cannot be further extended by refreshing, exiting refresh thread now.");
                            return;
                        }
                        if (nextRefresh > expiry || now + 60000L > expiry) {
                            nextRefresh = now;
                        }
                        else {
                            if (nextRefresh < now + 60000L) {
                                final Date until = new Date(nextRefresh);
                                final Date newuntil = new Date(now + 60000L);
                                Login.LOG.warn("TGT refresh thread time adjusted from : " + until + " to : " + newuntil + " since the former is sooner than the minimum refresh interval (" + 60L + " seconds) from now.");
                            }
                            nextRefresh = Math.max(nextRefresh, now + 60000L);
                        }
                        nextRefreshDate = new Date(nextRefresh);
                        if (nextRefresh > expiry) {
                            Login.LOG.error("next refresh: " + nextRefreshDate + " is later than expiry " + expiryDate + ". This may indicate a clock skew problem. Check that this host and the KDC's hosts' clocks are in sync. Exiting refresh thread.");
                            return;
                        }
                    }
                    if (now == nextRefresh) {
                        Login.LOG.info("refreshing now because expiry is before next scheduled refresh time.");
                    }
                    else {
                        if (now >= nextRefresh) {
                            Login.LOG.error("nextRefresh:" + nextRefreshDate + " is in the past: exiting refresh thread. Check clock sync between this host and KDC - (KDC's clock is likely ahead of this host). Manual intervention will be required for this client to successfully authenticate. Exiting refresh thread.");
                            break;
                        }
                        final Date until2 = new Date(nextRefresh);
                        Login.LOG.info("TGT refresh sleeping until: " + until2.toString());
                        try {
                            Thread.sleep(nextRefresh - now);
                        }
                        catch (InterruptedException ie) {
                            Login.LOG.warn("TGT renewal thread has been interrupted and will exit.");
                            break;
                        }
                    }
                    if (Login.this.isUsingTicketCache) {
                        String cmd = "/usr/bin/kinit";
                        if (System.getProperty("zookeeper.kinit") != null) {
                            cmd = System.getProperty("zookeeper.kinit");
                        }
                        final String kinitArgs = "-R";
                        int retry = 1;
                        while (retry >= 0) {
                            try {
                                Login.LOG.debug("running ticket cache refresh command: " + cmd + " " + kinitArgs);
                                Shell.execCommand(cmd, kinitArgs);
                            }
                            catch (Exception e) {
                                Label_0648: {
                                    if (retry > 0) {
                                        --retry;
                                        try {
                                            Thread.sleep(10000L);
                                            continue;
                                        }
                                        catch (InterruptedException ie2) {
                                            Login.LOG.error("Interrupted while renewing TGT, exiting Login thread");
                                            return;
                                        }
                                        break Label_0648;
                                        continue;
                                    }
                                }
                                Login.LOG.warn("Could not renew TGT due to problem running shell command: '" + cmd + " " + kinitArgs + "'; exception was:" + e + ". Exiting refresh thread.", e);
                                return;
                            }
                            break;
                        }
                    }
                    try {
                        int retry2 = 1;
                        while (retry2 >= 0) {
                            try {
                                Login.this.reLogin();
                            }
                            catch (LoginException le) {
                                if (retry2 > 0) {
                                    --retry2;
                                    try {
                                        Thread.sleep(10000L);
                                        continue;
                                    }
                                    catch (InterruptedException e2) {
                                        Login.LOG.error("Interrupted during login retry after LoginException:", le);
                                        throw le;
                                    }
                                }
                                Login.LOG.error("Could not refresh TGT for principal: " + Login.this.principal + ".", le);
                                continue;
                            }
                            break;
                        }
                    }
                    catch (LoginException le2) {
                        Login.LOG.error("Failed to refresh TGT: refresh thread exiting now.", le2);
                        break;
                    }
                }
            }
        })).setDaemon(true);
    }
    
    public void startThreadIfNeeded() {
        if (this.t != null) {
            this.t.start();
        }
    }
    
    public void shutdown() {
        if (this.t != null && this.t.isAlive()) {
            this.t.interrupt();
            try {
                this.t.join();
            }
            catch (InterruptedException e) {
                Login.LOG.warn("error while waiting for Login thread to shutdown: " + e);
            }
        }
    }
    
    public Subject getSubject() {
        return this.subject;
    }
    
    public String getLoginContextName() {
        return this.loginContextName;
    }
    
    private synchronized LoginContext login(final String loginContextName) throws LoginException {
        if (loginContextName == null) {
            throw new LoginException("loginContext name (JAAS file section header) was null. Please check your java.security.login.auth.config (=" + System.getProperty("java.security.login.auth.config") + ") and your " + "zookeeper.sasl.clientconfig" + "(=" + System.getProperty("zookeeper.sasl.clientconfig", "Client") + ")");
        }
        final LoginContext loginContext = new LoginContext(loginContextName, this.callbackHandler);
        loginContext.login();
        Login.LOG.info("{} successfully logged in.", loginContextName);
        return loginContext;
    }
    
    private long getRefreshTime(final KerberosTicket tgt) {
        final long start = tgt.getStartTime().getTime();
        final long expires = tgt.getEndTime().getTime();
        Login.LOG.info("TGT valid starting at:        " + tgt.getStartTime().toString());
        Login.LOG.info("TGT expires:                  " + tgt.getEndTime().toString());
        final long proposedRefresh = start + (long)((expires - start) * (0.800000011920929 + 0.05000000074505806 * Login.rng.nextDouble()));
        if (proposedRefresh > expires) {
            return Time.currentWallTime();
        }
        return proposedRefresh;
    }
    
    private synchronized KerberosTicket getTGT() {
        final Set<KerberosTicket> tickets = this.subject.getPrivateCredentials(KerberosTicket.class);
        for (final KerberosTicket ticket : tickets) {
            final KerberosPrincipal server = ticket.getServer();
            if (server.getName().equals("krbtgt/" + server.getRealm() + "@" + server.getRealm())) {
                Login.LOG.debug("Client principal is \"" + ticket.getClient().getName() + "\".");
                Login.LOG.debug("Server principal is \"" + ticket.getServer().getName() + "\".");
                return ticket;
            }
        }
        return null;
    }
    
    private boolean hasSufficientTimeElapsed() {
        final long now = Time.currentElapsedTime();
        if (now - this.getLastLogin() < 60000L) {
            Login.LOG.warn("Not attempting to re-login since the last re-login was attempted less than 60 seconds before.");
            return false;
        }
        this.setLastLogin(now);
        return true;
    }
    
    private LoginContext getLogin() {
        return this.login;
    }
    
    private void setLogin(final LoginContext login) {
        this.login = login;
    }
    
    private void setLastLogin(final long time) {
        this.lastLogin = time;
    }
    
    private long getLastLogin() {
        return this.lastLogin;
    }
    
    private synchronized void reLogin() throws LoginException {
        if (!this.isKrbTicket) {
            return;
        }
        LoginContext login = this.getLogin();
        if (login == null) {
            throw new LoginException("login must be done first");
        }
        if (!this.hasSufficientTimeElapsed()) {
            return;
        }
        Login.LOG.info("Initiating logout for " + this.principal);
        synchronized (Login.class) {
            login.logout();
            login = new LoginContext(this.loginContextName, this.getSubject());
            Login.LOG.info("Initiating re-login for " + this.principal);
            login.login();
            this.setLogin(login);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(Login.class);
        Login.rng = new Random();
    }
}
