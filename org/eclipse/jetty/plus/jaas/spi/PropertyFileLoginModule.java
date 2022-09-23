// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.spi;

import java.util.HashMap;
import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Credential;
import java.security.Principal;
import java.util.ArrayList;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import org.eclipse.jetty.security.PropertyUserStore;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;

public class PropertyFileLoginModule extends AbstractLoginModule
{
    public static final String DEFAULT_FILENAME = "realm.properties";
    private static final Logger LOG;
    private static Map<String, PropertyUserStore> _propertyUserStores;
    private int _refreshInterval;
    private String _filename;
    
    public PropertyFileLoginModule() {
        this._refreshInterval = 0;
        this._filename = "realm.properties";
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        this.setupPropertyUserStore(options);
    }
    
    private void setupPropertyUserStore(final Map<String, ?> options) {
        if (PropertyFileLoginModule._propertyUserStores.get(this._filename) == null) {
            this.parseConfig(options);
            final PropertyUserStore _propertyUserStore = new PropertyUserStore();
            _propertyUserStore.setConfig(this._filename);
            _propertyUserStore.setRefreshInterval(this._refreshInterval);
            PropertyFileLoginModule.LOG.debug("setupPropertyUserStore: Starting new PropertyUserStore. PropertiesFile: " + this._filename + " refreshInterval: " + this._refreshInterval, new Object[0]);
            try {
                _propertyUserStore.start();
            }
            catch (Exception e) {
                PropertyFileLoginModule.LOG.warn("Exception while starting propertyUserStore: ", e);
            }
            PropertyFileLoginModule._propertyUserStores.put(this._filename, _propertyUserStore);
        }
    }
    
    private void parseConfig(final Map<String, ?> options) {
        this._filename = (String)((options.get("file") != null) ? options.get("file") : "realm.properties");
        final String refreshIntervalString = (String)options.get("refreshInterval");
        this._refreshInterval = ((refreshIntervalString == null) ? this._refreshInterval : Integer.parseInt(refreshIntervalString));
    }
    
    @Override
    public UserInfo getUserInfo(final String userName) throws Exception {
        final PropertyUserStore propertyUserStore = PropertyFileLoginModule._propertyUserStores.get(this._filename);
        if (propertyUserStore == null) {
            throw new IllegalStateException("PropertyUserStore should never be null here!");
        }
        final UserIdentity userIdentity = propertyUserStore.getUserIdentity(userName);
        if (userIdentity == null) {
            return null;
        }
        final Set<Principal> principals = userIdentity.getSubject().getPrincipals();
        final List<String> roles = new ArrayList<String>();
        for (final Principal principal : principals) {
            roles.add(principal.getName());
        }
        final Credential credential = userIdentity.getSubject().getPrivateCredentials().iterator().next();
        PropertyFileLoginModule.LOG.debug("Found: " + userName + " in PropertyUserStore", new Object[0]);
        return new UserInfo(userName, credential, roles);
    }
    
    static {
        LOG = Log.getLogger(PropertyFileLoginModule.class);
        PropertyFileLoginModule._propertyUserStores = new HashMap<String, PropertyUserStore>();
    }
}
