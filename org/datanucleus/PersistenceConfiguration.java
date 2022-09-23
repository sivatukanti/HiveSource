// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import org.datanucleus.util.NucleusLogger;
import java.util.Collections;
import java.util.Properties;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.PersistenceUtils;
import org.datanucleus.properties.IntegerPropertyValidator;
import org.datanucleus.properties.BooleanPropertyValidator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.properties.StringPropertyValidator;
import org.datanucleus.properties.CorePropertyValidator;
import java.util.HashMap;
import org.datanucleus.properties.PersistencePropertyValidator;
import java.util.Map;
import org.datanucleus.util.Localiser;
import java.io.Serializable;
import org.datanucleus.properties.PropertyStore;

public class PersistenceConfiguration extends PropertyStore implements Serializable
{
    protected static final Localiser LOCALISER;
    private NucleusContext nucCtx;
    private Map<String, Object> defaultProperties;
    private Map<String, PropertyMapping> propertyMappings;
    private Map<String, PersistencePropertyValidator> propertyValidators;
    
    public PersistenceConfiguration(final NucleusContext nucCtx) {
        this.defaultProperties = new HashMap<String, Object>();
        this.propertyMappings = new HashMap<String, PropertyMapping>();
        this.propertyValidators = new HashMap<String, PersistencePropertyValidator>();
        this.nucCtx = nucCtx;
        this.addDefaultBooleanProperty("datanucleus.IgnoreCache", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.Optimistic", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.Multithreaded", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.RetainValues", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.RestoreValues", null, false, false, true);
        this.addDefaultProperty("datanucleus.jmxType", null, null, null, false, false);
        this.addDefaultBooleanProperty("datanucleus.enableStatistics", null, false, false, false);
        this.addDefaultProperty("datanucleus.Name", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.PersistenceUnitName", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.persistenceXmlFilename", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.ServerTimeZoneID", null, null, CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.propertiesFile", null, null, null, false, false);
        this.addDefaultBooleanProperty("datanucleus.persistenceUnitLoadClasses", null, false, false, false);
        this.addDefaultBooleanProperty("datanucleus.executionContext.reaperThread", null, false, false, false);
        this.addDefaultIntegerProperty("datanucleus.executionContext.maxIdle", null, 20, false, false);
        this.addDefaultBooleanProperty("datanucleus.objectProvider.reaperThread", null, false, false, false);
        this.addDefaultIntegerProperty("datanucleus.objectProvider.maxIdle", null, 0, false, false);
        this.addDefaultProperty("datanucleus.objectProvider.className", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.datastoreIdentityType", null, "datanucleus", null, false, false);
        this.addDefaultProperty("datanucleus.identityStringTranslatorType", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.identityKeyTranslatorType", null, null, null, false, false);
        this.addDefaultBooleanProperty("datanucleus.useImplementationCreator", null, true, false, false);
        this.addDefaultProperty("datanucleus.classLoaderResolverName", null, "datanucleus", null, false, false);
        this.addDefaultProperty("datanucleus.primaryClassLoader", null, null, null, false, false);
        this.addDefaultBooleanProperty("datanucleus.localisation.messageCodes", null, false, false, false);
        this.addDefaultProperty("datanucleus.localisation.language", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.plugin.pluginRegistryClassName", null, null, null, false, false);
        this.addDefaultBooleanProperty("datanucleus.plugin.allowUserBundles", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.plugin.validatePlugins", null, false, false, false);
        this.addDefaultProperty("datanucleus.plugin.pluginRegistryBundleCheck", null, "EXCEPTION", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.TransactionType", null, null, CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.jtaLocator", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.jtaJndiLocation", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.transactionIsolation", null, "read-committed", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultBooleanProperty("datanucleus.NontransactionalRead", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.NontransactionalWrite", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.nontx.atomic", null, true, false, true);
        this.addDefaultIntegerProperty("datanucleus.datastoreTransactionFlushLimit", null, 1, false, false);
        this.addDefaultProperty("datanucleus.flush.mode", null, null, CorePropertyValidator.class.getName(), false, true);
        this.addDefaultBooleanProperty("datanucleus.metadata.alwaysDetachable", null, false, false, false);
        this.addDefaultBooleanProperty("datanucleus.metadata.xml.validate", null, false, false, false);
        this.addDefaultBooleanProperty("datanucleus.metadata.validate", "datanucleus.metadata.xml.validate", false, false, false);
        this.addDefaultBooleanProperty("datanucleus.metadata.xml.namespaceAware", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.metadata.autoregistration", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.metadata.allowXML", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.metadata.allowAnnotations", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.metadata.allowLoadAtRuntime", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.metadata.supportORM", null, null, false, false);
        this.addDefaultProperty("datanucleus.metadata.jdoFileExtension", null, "jdo", null, false, false);
        this.addDefaultProperty("datanucleus.metadata.ormFileExtension", null, "orm", null, false, false);
        this.addDefaultProperty("datanucleus.metadata.jdoqueryFileExtension", null, "jdoquery", null, false, false);
        this.addDefaultProperty("datanucleus.valuegeneration.transactionIsolation", null, "read-committed", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.valuegeneration.transactionAttribute", null, "New", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultIntegerProperty("datanucleus.valuegeneration.sequence.allocationSize", null, 10, false, false);
        this.addDefaultIntegerProperty("datanucleus.valuegeneration.increment.allocationSize", null, 10, false, false);
        this.addDefaultProperty("datanucleus.validation.mode", null, "auto", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.validation.group.pre-persist", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.validation.group.pre-update", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.validation.group.pre-remove", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.validation.factory", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.autoStartMechanism", null, "None", null, true, false);
        this.addDefaultProperty("datanucleus.autoStartMechanismMode", null, "Quiet", CorePropertyValidator.class.getName(), true, false);
        this.addDefaultProperty("datanucleus.autoStartMechanismXmlFile", null, "datanucleusAutoStart.xml", null, true, false);
        this.addDefaultProperty("datanucleus.autoStartClassNames", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.autoStartMetaDataFiles", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.generateSchema.database.mode", null, "none", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.generateSchema.scripts.mode", null, "none", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.generateSchema.scripts.create.target", null, "datanucleus-schema-create.ddl", null, false, false);
        this.addDefaultProperty("datanucleus.generateSchema.scripts.drop.target", null, "datanucleus-schema-drop.ddl", null, false, false);
        this.addDefaultProperty("datanucleus.generateSchema.scripts.create.source", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.generateSchema.scripts.drop.source", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.generateSchema.scripts.load", null, null, null, false, false);
        this.addDefaultBooleanProperty("datanucleus.autoCreateSchema", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.autoCreateTables", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.autoCreateColumns", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.autoCreateConstraints", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.validateSchema", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.validateTables", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.validateColumns", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.validateConstraints", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.autoCreateWarnOnError", null, false, true, false);
        this.addDefaultProperty("datanucleus.identifier.case", null, null, CorePropertyValidator.class.getName(), true, false);
        this.addDefaultProperty("datanucleus.identifier.tablePrefix", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.identifier.tableSuffix", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.identifier.wordSeparator", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.identifierFactory", null, "datanucleus2", null, true, false);
        this.addDefaultProperty("datanucleus.storeManagerType", null, null, null, false, false);
        this.addDefaultBooleanProperty("datanucleus.store.allowReferencesWithNoImplementations", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.readOnlyDatastore", null, false, true, false);
        this.addDefaultBooleanProperty("datanucleus.fixedDatastore", null, false, true, false);
        this.addDefaultProperty("datanucleus.readOnlyDatastoreAction", null, "EXCEPTION", CorePropertyValidator.class.getName(), true, false);
        this.addDefaultIntegerProperty("datanucleus.datastoreReadTimeout", null, null, true, true);
        this.addDefaultIntegerProperty("datanucleus.datastoreWriteTimeout", null, null, true, true);
        this.addDefaultProperty("datanucleus.mapping", null, null, StringPropertyValidator.class.getName(), true, false);
        this.addDefaultProperty("datanucleus.mapping.Catalog", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.mapping.Schema", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.TenantID", null, null, null, true, false);
        this.addDefaultBooleanProperty("datanucleus.persistenceByReachabilityAtCommit", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.manageRelationships", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.manageRelationshipsChecks", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.SerializeRead", null, false, false, true);
        this.addDefaultProperty("datanucleus.deletionPolicy", null, "JDO2", CorePropertyValidator.class.getName(), false, true);
        this.addDefaultProperty("datanucleus.defaultInheritanceStrategy", null, "JDO2", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultBooleanProperty("datanucleus.findObject.validateWhenCached", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.allowCallbacks", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.DetachAllOnCommit", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.DetachAllOnRollback", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.DetachOnClose", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.CopyOnAttach", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.attachSameDatastore", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.allowAttachOfTransient", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.detachAsWrapped", null, false, false, true);
        this.addDefaultProperty("datanucleus.detachmentFields", null, "load-fields", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.detachedState", null, "fetch-groups", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultIntegerProperty("datanucleus.maxFetchDepth", null, 1, false, true);
        this.addDefaultProperty("datanucleus.ConnectionURL", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.ConnectionDriverName", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.ConnectionUserName", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.ConnectionPassword", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.ConnectionPasswordDecrypter", null, null, null, false, false);
        this.addDefaultProperty("datanucleus.ConnectionFactoryName", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.ConnectionFactory2Name", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.ConnectionFactory", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.ConnectionFactory2", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.connection.resourceType", null, null, CorePropertyValidator.class.getName(), true, false);
        this.addDefaultProperty("datanucleus.connection2.resourceType", null, null, CorePropertyValidator.class.getName(), true, false);
        this.addDefaultProperty("datanucleus.connectionPoolingType", null, null, null, true, false);
        this.addDefaultProperty("datanucleus.connectionPoolingType.nontx", null, null, null, true, false);
        this.addDefaultBooleanProperty("datanucleus.connection.nontx.releaseAfterUse", null, true, true, false);
        this.addDefaultProperty("datanucleus.cache.level1.type", null, "soft", null, false, false);
        this.addDefaultProperty("datanucleus.cache.level2.type", null, "soft", null, false, false);
        this.addDefaultBooleanProperty("datanucleus.cache.collections", null, true, false, true);
        this.addDefaultBooleanProperty("datanucleus.cache.collections.lazy", null, null, false, false);
        this.addDefaultProperty("datanucleus.cache.level2.mode", null, "UNSPECIFIED", CorePropertyValidator.class.getName(), false, false);
        this.addDefaultProperty("datanucleus.cache.level2.cacheName", null, "datanucleus", null, false, false);
        this.addDefaultIntegerProperty("datanucleus.cache.level2.maxSize", null, -1, false, false);
        this.addDefaultBooleanProperty("datanucleus.cache.level2.loadFields", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.cache.level2.clearAtClose", null, true, false, false);
        this.addDefaultIntegerProperty("datanucleus.cache.level2.timeout", null, -1, false, false);
        this.addDefaultIntegerProperty("datanucleus.cache.level2.batchSize", null, 100, false, false);
        this.addDefaultBooleanProperty("datanucleus.cache.level2.cacheEmbedded", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.cache.level2.readThrough", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.cache.level2.writeThrough", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.cache.level2.statisticsEnabled", null, false, false, false);
        this.addDefaultBooleanProperty("datanucleus.cache.level2.storeByValue", null, true, false, false);
        this.addDefaultProperty("datanucleus.cache.level2.retrieveMode", null, "use", CorePropertyValidator.class.getName(), false, true);
        this.addDefaultProperty("datanucleus.cache.level2.storeMode", null, "use", CorePropertyValidator.class.getName(), false, true);
        this.addDefaultProperty("datanucleus.cache.level2.updateMode", null, "commit-and-datastore-read", CorePropertyValidator.class.getName(), false, true);
        this.addDefaultProperty("datanucleus.cache.queryCompilation.type", null, "soft", null, false, false);
        this.addDefaultProperty("datanucleus.cache.queryCompilationDatastore.type", null, "soft", null, false, false);
        this.addDefaultProperty("datanucleus.cache.queryResults.type", null, "soft", null, false, false);
        this.addDefaultProperty("datanucleus.cache.queryResults.cacheName", null, "datanucleus-query", null, false, false);
        this.addDefaultIntegerProperty("datanucleus.cache.queryResults.maxSize", null, -1, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.sql.allowAll", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.query.jdoql.allowAll", null, false, false, true);
        this.addDefaultBooleanProperty("datanucleus.query.flushBeforeExecution", null, false, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.useFetchPlan", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.checkUnusedParameters", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.compileOptimised", null, false, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.loadResultsAtCommit", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.compilation.cached", null, true, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.results.cached", null, false, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.evaluateInMemory", null, false, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.resultCache.validateObjects", null, true, false, false);
        this.addDefaultProperty("datanucleus.query.resultSizeMethod", null, "last", null, false, false);
        this.addDefaultBooleanProperty("datanucleus.query.compileNamedQueriesAtStartup", null, false, false, false);
        final ConfigurationElement[] propElements = nucCtx.getPluginManager().getConfigurationElementsForExtension("org.datanucleus.persistence_properties", null, (String)null);
        if (propElements != null) {
            for (int i = 0; i < propElements.length; ++i) {
                final String name = propElements[i].getAttribute("name");
                final String intName = propElements[i].getAttribute("internal-name");
                final String value = propElements[i].getAttribute("value");
                final String datastoreString = propElements[i].getAttribute("datastore");
                final String validatorName = propElements[i].getAttribute("validator");
                final boolean datastore = datastoreString != null && datastoreString.equalsIgnoreCase("true");
                final String mgrOverrideString = propElements[i].getAttribute("manager-overrideable");
                final boolean mgrOverride = mgrOverrideString != null && mgrOverrideString.equalsIgnoreCase("true");
                this.addDefaultProperty(name, intName, value, validatorName, datastore, mgrOverride);
            }
        }
    }
    
    public Set<String> getSupportedProperties() {
        return this.propertyMappings.keySet();
    }
    
    public Map<String, Object> getDatastoreProperties() {
        final Map<String, Object> props = new HashMap<String, Object>();
        for (final String name : this.properties.keySet()) {
            if (this.isPropertyForDatastore(name)) {
                props.put(name, this.properties.get(name));
            }
        }
        return props;
    }
    
    public void removeDatastoreProperties() {
        final Iterator<String> propKeyIter = this.properties.keySet().iterator();
        while (propKeyIter.hasNext()) {
            final String name = propKeyIter.next();
            if (this.isPropertyForDatastore(name)) {
                propKeyIter.remove();
            }
        }
    }
    
    public boolean isPropertyForDatastore(final String name) {
        final PropertyMapping mapping = this.propertyMappings.get(name.toLowerCase(Locale.ENGLISH));
        return mapping != null && mapping.datastore;
    }
    
    public String getInternalNameForProperty(final String name) {
        final PropertyMapping mapping = this.propertyMappings.get(name.toLowerCase(Locale.ENGLISH));
        return (mapping != null && mapping.internalName != null) ? mapping.internalName : name;
    }
    
    public Map<String, Object> getManagerOverrideableProperties() {
        final Map<String, Object> props = new HashMap<String, Object>();
        for (final Map.Entry<String, PropertyMapping> entry : this.propertyMappings.entrySet()) {
            final PropertyMapping mapping = entry.getValue();
            if (mapping.managerOverride) {
                final String propName = (mapping.internalName != null) ? mapping.internalName.toLowerCase(Locale.ENGLISH) : mapping.name.toLowerCase(Locale.ENGLISH);
                props.put(propName, this.getProperty(propName));
            }
            else {
                if (mapping.internalName == null) {
                    continue;
                }
                final PropertyMapping intMapping = this.propertyMappings.get(mapping.internalName.toLowerCase(Locale.ENGLISH));
                if (intMapping == null || !intMapping.managerOverride) {
                    continue;
                }
                props.put(mapping.name.toLowerCase(Locale.ENGLISH), this.getProperty(mapping.internalName));
            }
        }
        return props;
    }
    
    public Set<String> getManagedOverrideablePropertyNames() {
        final Set<String> propNames = new HashSet<String>();
        for (final PropertyMapping mapping : this.propertyMappings.values()) {
            if (mapping.managerOverride) {
                propNames.add(mapping.name);
            }
        }
        return propNames;
    }
    
    public String getPropertyNameWithInternalPropertyName(final String propName, final String propPrefix) {
        if (propName == null) {
            return null;
        }
        for (final PropertyMapping mapping : this.propertyMappings.values()) {
            if (mapping.internalName != null && mapping.internalName.toLowerCase().equals(propName.toLowerCase()) && mapping.name.startsWith(propPrefix)) {
                return mapping.name;
            }
        }
        return null;
    }
    
    public String getCaseSensitiveNameForPropertyName(final String propName) {
        if (propName == null) {
            return null;
        }
        for (final PropertyMapping mapping : this.propertyMappings.values()) {
            if (mapping.name.toLowerCase().equals(propName.toLowerCase())) {
                return mapping.name;
            }
        }
        return propName;
    }
    
    public void setDefaultProperties(final Map props) {
        if (props != null && props.size() > 0) {
            for (final Map.Entry entry : props.entrySet()) {
                final PropertyMapping mapping = this.propertyMappings.get(entry.getKey().toLowerCase(Locale.ENGLISH));
                Object propValue = entry.getValue();
                if (mapping != null && mapping.validatorName != null && propValue instanceof String) {
                    propValue = this.getValueForPropertyWithValidator((String)propValue, mapping.validatorName);
                }
                this.defaultProperties.put(entry.getKey().toLowerCase(Locale.ENGLISH), propValue);
            }
        }
    }
    
    private void addDefaultBooleanProperty(final String name, final String internalName, final Boolean value, final boolean datastore, final boolean managerOverrideable) {
        this.addDefaultProperty(name, internalName, (value != null) ? ("" + value) : null, BooleanPropertyValidator.class.getName(), datastore, managerOverrideable);
    }
    
    private void addDefaultIntegerProperty(final String name, final String internalName, final Integer value, final boolean datastore, final boolean managerOverrideable) {
        this.addDefaultProperty(name, internalName, (value != null) ? ("" + value) : null, IntegerPropertyValidator.class.getName(), datastore, managerOverrideable);
    }
    
    private void addDefaultProperty(final String name, final String internalName, final String value, final String validatorName, final boolean datastore, final boolean managerOverrideable) {
        this.propertyMappings.put(name.toLowerCase(Locale.ENGLISH), new PropertyMapping(name, internalName, validatorName, datastore, managerOverrideable));
        final String storedName = (internalName != null) ? internalName.toLowerCase(Locale.ENGLISH) : name.toLowerCase(Locale.ENGLISH);
        if (!this.defaultProperties.containsKey(storedName)) {
            Object propValue = System.getProperty(name);
            if (propValue == null) {
                propValue = value;
            }
            if (propValue != null) {
                if (validatorName != null) {
                    propValue = this.getValueForPropertyWithValidator(value, validatorName);
                }
                this.defaultProperties.put(storedName, propValue);
            }
        }
    }
    
    protected Object getValueForPropertyWithValidator(final String value, final String validatorName) {
        if (validatorName.equals(BooleanPropertyValidator.class.getName())) {
            return Boolean.valueOf(value);
        }
        if (validatorName.equals(IntegerPropertyValidator.class.getName())) {
            return Integer.valueOf(value);
        }
        return value;
    }
    
    @Override
    public boolean hasProperty(final String name) {
        return this.properties.containsKey(name.toLowerCase(Locale.ENGLISH)) || this.defaultProperties.containsKey(name.toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public Object getProperty(final String name) {
        if (this.properties.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return super.getProperty(name);
        }
        return this.defaultProperties.get(name.toLowerCase(Locale.ENGLISH));
    }
    
    public synchronized void setPropertiesUsingFile(final String filename) {
        if (filename == null) {
            return;
        }
        Properties props = null;
        try {
            props = PersistenceUtils.setPropertiesUsingFile(filename);
            this.setPropertyInternal("datanucleus.propertiesFile", filename);
        }
        catch (NucleusUserException nue) {
            this.properties.remove("datanucleus.propertiesFile");
            throw nue;
        }
        if (props != null && !props.isEmpty()) {
            this.setPersistenceProperties(props);
        }
    }
    
    public Map<String, Object> getPersistencePropertiesDefaults() {
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.defaultProperties);
    }
    
    public Map<String, Object> getPersistenceProperties() {
        return Collections.unmodifiableMap((Map<? extends String, ?>)this.properties);
    }
    
    public Set<String> getPropertyNamesWithPrefix(final String prefix) {
        Set<String> propNames = null;
        for (final String name : this.properties.keySet()) {
            if (name.startsWith(prefix.toLowerCase(Locale.ENGLISH))) {
                if (propNames == null) {
                    propNames = new HashSet<String>();
                }
                propNames.add(name);
            }
        }
        return propNames;
    }
    
    public void setPersistenceProperties(final Map props) {
        final Set entries = props.entrySet();
        for (final Map.Entry entry : entries) {
            final Object keyObj = entry.getKey();
            if (keyObj instanceof String) {
                final String key = (String)keyObj;
                this.setProperty(key, entry.getValue());
            }
        }
    }
    
    public void setProperty(final String name, Object value) {
        if (name != null) {
            final String propertyName = name.trim();
            final PropertyMapping mapping = this.propertyMappings.get(propertyName.toLowerCase(Locale.ENGLISH));
            if (mapping != null) {
                if (mapping.validatorName != null) {
                    this.validatePropertyValue((mapping.internalName != null) ? mapping.internalName : propertyName, value, mapping.validatorName);
                    if (value != null && value instanceof String) {
                        value = this.getValueForPropertyWithValidator((String)value, mapping.validatorName);
                    }
                }
                if (mapping.internalName != null) {
                    this.setPropertyInternal(mapping.internalName, value);
                }
                else {
                    this.setPropertyInternal(mapping.name, value);
                }
                if (propertyName.equals("datanucleus.propertiesFile")) {
                    this.setPropertiesUsingFile((String)value);
                }
                else if (propertyName.equals("datanucleus.localisation.messageCodes")) {
                    final boolean included = this.getBooleanProperty("datanucleus.localisation.messageCodes");
                    Localiser.setDisplayCodesInMessages(included);
                }
                else if (propertyName.equals("datanucleus.localisation.language")) {
                    final String language = this.getStringProperty("datanucleus.localisation.language");
                    Localiser.setLanguage(language);
                }
            }
            else {
                this.setPropertyInternal(propertyName, value);
                if (this.propertyMappings.size() > 0) {
                    NucleusLogger.PERSISTENCE.info(PersistenceConfiguration.LOCALISER.msg("008015", propertyName));
                }
            }
        }
    }
    
    public void validatePropertyValue(final String name, final Object value) {
        String validatorName = null;
        final PropertyMapping mapping = this.propertyMappings.get(name.toLowerCase(Locale.ENGLISH));
        if (mapping != null) {
            validatorName = mapping.validatorName;
        }
        if (validatorName != null) {
            this.validatePropertyValue(name, value, validatorName);
        }
    }
    
    private void validatePropertyValue(final String name, final Object value, final String validatorName) {
        if (validatorName == null) {
            return;
        }
        PersistencePropertyValidator validator = this.propertyValidators.get(validatorName);
        if (validator == null) {
            try {
                final Class validatorCls = this.nucCtx.getClassLoaderResolver(this.getClass().getClassLoader()).classForName(validatorName);
                validator = validatorCls.newInstance();
                this.propertyValidators.put(validatorName, validator);
            }
            catch (Exception e) {
                NucleusLogger.PERSISTENCE.warn("Error creating validator of type " + validatorName, e);
            }
        }
        if (validator != null) {
            final boolean validated = validator.validate(name, value);
            if (!validated) {
                throw new IllegalArgumentException(PersistenceConfiguration.LOCALISER.msg("008012", name, value));
            }
        }
    }
    
    @Override
    public synchronized boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PersistenceConfiguration)) {
            return false;
        }
        final PersistenceConfiguration config = (PersistenceConfiguration)obj;
        if (this.properties == null) {
            if (config.properties != null) {
                return false;
            }
        }
        else if (!this.properties.equals(config.properties)) {
            return false;
        }
        if (this.defaultProperties == null) {
            if (config.defaultProperties != null) {
                return false;
            }
        }
        else if (!this.defaultProperties.equals(config.defaultProperties)) {
            return false;
        }
        return true;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    static class PropertyMapping implements Serializable
    {
        String name;
        String internalName;
        String validatorName;
        boolean datastore;
        boolean managerOverride;
        
        public PropertyMapping(final String name, final String intName, final String validator, final boolean datastore, final boolean managerOverride) {
            this.name = name;
            this.internalName = intName;
            this.validatorName = validator;
            this.datastore = datastore;
            this.managerOverride = managerOverride;
        }
    }
}
