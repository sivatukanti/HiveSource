// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.locks.Lockable;
import org.apache.derby.iapi.services.locks.ShExQual;
import java.util.Enumeration;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.store.access.ConglomerateController;
import java.util.Hashtable;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.types.UserType;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.io.Serializable;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.property.PropertyFactory;
import java.util.Dictionary;
import org.apache.derby.iapi.services.locks.LockFactory;
import java.util.Properties;

class PropertyConglomerate
{
    protected long propertiesConglomId;
    protected Properties serviceProperties;
    private LockFactory lf;
    private Dictionary cachedSet;
    private CacheLock cachedLock;
    private PropertyFactory pf;
    
    PropertyConglomerate(final TransactionController transactionController, boolean b, final Properties serviceProperties, final PropertyFactory pf) throws StandardException {
        this.pf = pf;
        if (!b) {
            final String property = serviceProperties.getProperty("derby.storage.propertiesId");
            if (property == null) {
                b = true;
            }
            else {
                try {
                    this.propertiesConglomId = Long.valueOf(property);
                }
                catch (NumberFormatException ex) {
                    throw Monitor.exceptionStartingModule(ex);
                }
            }
        }
        if (b) {
            final DataValueDescriptor[] newTemplate = this.makeNewTemplate();
            final Properties properties = new Properties();
            properties.put("derby.storage.pageSize", "2048");
            properties.put("derby.storage.pageReservedSpace", "0");
            this.propertiesConglomId = transactionController.createConglomerate("heap", newTemplate, null, null, properties, 0);
            serviceProperties.put("derby.storage.propertiesId", Long.toString(this.propertiesConglomId));
        }
        this.serviceProperties = serviceProperties;
        this.lf = ((RAMTransaction)transactionController).getAccessManager().getLockFactory();
        this.cachedLock = new CacheLock(this);
        final PC_XenaVersion pc_XenaVersion = new PC_XenaVersion();
        if (b) {
            this.setProperty(transactionController, "PropertyConglomerateVersion", pc_XenaVersion, true);
        }
        else {
            pc_XenaVersion.upgradeIfNeeded(transactionController, this, serviceProperties);
        }
        this.getCachedDbProperties(transactionController);
    }
    
    private DataValueDescriptor[] makeNewTemplate(final String s, final Serializable s2) {
        return new DataValueDescriptor[] { new UTF(s), new UserType(s2) };
    }
    
    private DataValueDescriptor[] makeNewTemplate() {
        return new DataValueDescriptor[] { new UTF(), new UserType() };
    }
    
    private ScanController openScan(final TransactionController transactionController, final String s, final int n) throws StandardException {
        Qualifier[][] array = null;
        if (s != null) {
            array = new Qualifier[][] { new Qualifier[1] };
            array[0][0] = new UTFQualifier(0, s);
        }
        return transactionController.openScan(this.propertiesConglomId, false, n, 7, 5, null, null, 0, array, null, 0);
    }
    
    void setPropertyDefault(final TransactionController transactionController, final String s, final Serializable s2) throws StandardException {
        this.lockProperties(transactionController);
        Serializable s3 = null;
        if (this.propertyDefaultIsVisible(transactionController, s)) {
            s3 = this.validateApplyAndMap(transactionController, s, s2, false);
        }
        else {
            synchronized (this) {
                final Hashtable hashtable = new Hashtable();
                this.getProperties(transactionController, hashtable, false, true);
                this.validate(s, s2, hashtable);
                s3 = this.map(s, s2, hashtable);
            }
        }
        this.savePropertyDefault(transactionController, s, s3);
    }
    
    boolean propertyDefaultIsVisible(final TransactionController transactionController, final String s) throws StandardException {
        this.lockProperties(transactionController);
        return this.readProperty(transactionController, s) == null;
    }
    
    void saveProperty(final TransactionController transactionController, final String s, final Serializable s2) throws StandardException {
        if (this.saveServiceProperty(s, s2)) {
            return;
        }
        final ScanController openScan = this.openScan(transactionController, s, 4);
        final DataValueDescriptor[] newTemplate = this.makeNewTemplate();
        if (openScan.fetchNext(newTemplate)) {
            if (s2 == null) {
                openScan.delete();
            }
            else {
                newTemplate[1] = new UserType(s2);
                openScan.replace(newTemplate, null);
            }
            openScan.close();
        }
        else {
            openScan.close();
            if (s2 != null) {
                final DataValueDescriptor[] newTemplate2 = this.makeNewTemplate(s, s2);
                final ConglomerateController openConglomerate = transactionController.openConglomerate(this.propertiesConglomId, false, 4, 7, 5);
                openConglomerate.insert(newTemplate2);
                openConglomerate.close();
            }
        }
    }
    
    private boolean saveServiceProperty(final String s, final Serializable value) {
        if (PropertyUtil.isServiceProperty(s)) {
            if (value != null) {
                this.serviceProperties.put(s, value);
            }
            else {
                this.serviceProperties.remove(s);
            }
            return true;
        }
        return false;
    }
    
    void savePropertyDefault(final TransactionController transactionController, final String s, final Serializable s2) throws StandardException {
        if (this.saveServiceProperty(s, s2)) {
            return;
        }
        Dictionary<String, Serializable> dictionary = (Dictionary<String, Serializable>)this.readProperty(transactionController, "derby.defaultPropertyName");
        if (dictionary == null) {
            dictionary = (Dictionary<String, Serializable>)new FormatableHashtable();
        }
        if (s2 == null) {
            dictionary.remove(s);
        }
        else {
            dictionary.put(s, s2);
        }
        if (dictionary.size() == 0) {
            dictionary = null;
        }
        this.saveProperty(transactionController, "derby.defaultPropertyName", (Serializable)dictionary);
    }
    
    private Serializable validateApplyAndMap(final TransactionController transactionController, final String s, final Serializable s2, final boolean b) throws StandardException {
        final Hashtable hashtable = new Hashtable();
        this.getProperties(transactionController, hashtable, false, false);
        final Serializable doValidateApplyAndMap = this.pf.doValidateApplyAndMap(transactionController, s, s2, hashtable, b);
        if (s.equals("logDevice")) {
            throw StandardException.newException("XSRS8.S");
        }
        if (doValidateApplyAndMap == null) {
            return s2;
        }
        return doValidateApplyAndMap;
    }
    
    private Serializable map(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        return this.pf.doMap(s, s2, dictionary);
    }
    
    private void validate(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        this.pf.validateSingleProperty(s, s2, dictionary);
    }
    
    private boolean bootPasswordChange(final TransactionController transactionController, final String s, Serializable changeBootPassword) throws StandardException {
        if (s.equals("bootPassword")) {
            final RawStoreFactory rawStoreFactory = (RawStoreFactory)Monitor.findServiceModule(((TransactionManager)transactionController).getAccessManager(), "org.apache.derby.iapi.store.raw.RawStoreFactory");
            this.serviceProperties.remove("bootPassword");
            changeBootPassword = rawStoreFactory.changeBootPassword(this.serviceProperties, changeBootPassword);
            this.serviceProperties.put("encryptedBootPassword", changeBootPassword);
            return true;
        }
        return false;
    }
    
    void setProperty(final TransactionController transactionController, final String s, final Serializable s2, final boolean b) throws StandardException {
        this.lockProperties(transactionController);
        Serializable propertyDefault = s2;
        if (s2 == null) {
            propertyDefault = this.getPropertyDefault(transactionController, s);
        }
        final Serializable validateApplyAndMap = this.validateApplyAndMap(transactionController, s, propertyDefault, b);
        if (this.bootPasswordChange(transactionController, s, s2)) {
            return;
        }
        if (s2 == null) {
            this.saveProperty(transactionController, s, null);
        }
        else {
            this.saveProperty(transactionController, s, validateApplyAndMap);
        }
    }
    
    private Serializable readProperty(final TransactionController transactionController, final String s) throws StandardException {
        final ScanController openScan = this.openScan(transactionController, s, 0);
        final DataValueDescriptor[] newTemplate = this.makeNewTemplate();
        final boolean fetchNext = openScan.fetchNext(newTemplate);
        openScan.close();
        if (!fetchNext) {
            return null;
        }
        return (Serializable)((UserType)newTemplate[1]).getObject();
    }
    
    private Serializable getCachedProperty(final TransactionController transactionController, final String s) throws StandardException {
        final Dictionary cachedDbProperties = this.getCachedDbProperties(transactionController);
        if (cachedDbProperties.get(s) != null) {
            return cachedDbProperties.get(s);
        }
        return this.getCachedPropertyDefault(transactionController, s, cachedDbProperties);
    }
    
    private Serializable getCachedPropertyDefault(final TransactionController transactionController, final String s, Dictionary cachedDbProperties) throws StandardException {
        if (cachedDbProperties == null) {
            cachedDbProperties = this.getCachedDbProperties(transactionController);
        }
        final Dictionary<K, Serializable> dictionary = cachedDbProperties.get("derby.defaultPropertyName");
        if (dictionary == null) {
            return null;
        }
        return dictionary.get(s);
    }
    
    Serializable getProperty(final TransactionController transactionController, final String key) throws StandardException {
        if (PropertyUtil.isServiceProperty(key)) {
            return this.serviceProperties.getProperty(key);
        }
        if (!this.iHoldTheUpdateLock(transactionController)) {
            return this.getCachedProperty(transactionController, key);
        }
        final Serializable property = this.readProperty(transactionController, key);
        if (property != null) {
            return property;
        }
        return this.getPropertyDefault(transactionController, key);
    }
    
    Serializable getPropertyDefault(final TransactionController transactionController, final String s) throws StandardException {
        if (!this.iHoldTheUpdateLock(transactionController)) {
            return this.getCachedPropertyDefault(transactionController, s, null);
        }
        final Dictionary dictionary = (Dictionary)this.readProperty(transactionController, "derby.defaultPropertyName");
        if (dictionary == null) {
            return null;
        }
        return dictionary.get(s);
    }
    
    private Dictionary copyValues(final Dictionary dictionary, final Dictionary dictionary2, final boolean b) {
        if (dictionary2 == null) {
            return dictionary;
        }
        final Enumeration<String> keys = (Enumeration<String>)dictionary2.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            final Object value = dictionary2.get(s);
            if (value instanceof String || !b) {
                dictionary.put(s, value);
            }
        }
        return dictionary;
    }
    
    Properties getProperties(final TransactionController transactionController) throws StandardException {
        final Properties properties = new Properties();
        this.getProperties(transactionController, properties, true, false);
        return properties;
    }
    
    public void getProperties(final TransactionController transactionController, final Dictionary dictionary, final boolean b, final boolean b2) throws StandardException {
        if (this.iHoldTheUpdateLock(transactionController)) {
            final Dictionary dbProperties = this.readDbProperties(transactionController);
            this.copyValues(dictionary, dbProperties.get("derby.defaultPropertyName"), b);
            if (!b2) {
                this.copyValues(dictionary, dbProperties, b);
            }
        }
        else {
            final Dictionary cachedDbProperties = this.getCachedDbProperties(transactionController);
            this.copyValues(dictionary, cachedDbProperties.get("derby.defaultPropertyName"), b);
            if (!b2) {
                this.copyValues(dictionary, cachedDbProperties, b);
            }
        }
    }
    
    void resetCache() {
        this.cachedSet = null;
    }
    
    private Dictionary readDbProperties(final TransactionController transactionController) throws StandardException {
        final Hashtable<String, String> hashtable = new Hashtable<String, String>();
        final ScanController openScan = this.openScan(transactionController, null, 0);
        final DataValueDescriptor[] newTemplate = this.makeNewTemplate();
        while (openScan.fetchNext(newTemplate)) {
            hashtable.put((String)((UserType)newTemplate[0]).getObject(), (String)((UserType)newTemplate[1]).getObject());
        }
        openScan.close();
        for (int i = 0; i < PropertyUtil.servicePropertyList.length; ++i) {
            final String property = this.serviceProperties.getProperty(PropertyUtil.servicePropertyList[i]);
            if (property != null) {
                hashtable.put(PropertyUtil.servicePropertyList[i], property);
            }
        }
        return hashtable;
    }
    
    private Dictionary getCachedDbProperties(final TransactionController transactionController) throws StandardException {
        Dictionary cachedSet = this.cachedSet;
        if (cachedSet == null) {
            cachedSet = this.readDbProperties(transactionController);
            this.cachedSet = cachedSet;
        }
        return cachedSet;
    }
    
    void lockProperties(final TransactionController transactionController) throws StandardException {
        final CompatibilitySpace lockSpace = transactionController.getLockSpace();
        this.lf.lockObject(lockSpace, lockSpace.getOwner(), this.cachedLock, ShExQual.EX, -2);
    }
    
    private boolean iHoldTheUpdateLock(final TransactionController transactionController) throws StandardException {
        final CompatibilitySpace lockSpace = transactionController.getLockSpace();
        return this.lf.isLockHeld(lockSpace, lockSpace.getOwner(), this.cachedLock, ShExQual.EX);
    }
}
