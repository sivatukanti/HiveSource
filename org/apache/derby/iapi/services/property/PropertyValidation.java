// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.property;

import java.util.Enumeration;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import java.util.Dictionary;
import java.io.Serializable;
import org.apache.derby.iapi.store.access.TransactionController;
import java.util.Vector;

public class PropertyValidation implements PropertyFactory
{
    private Vector notifyOnSet;
    
    public Serializable doValidateApplyAndMap(final TransactionController transactionController, final String s, final Serializable s2, final Dictionary dictionary, final boolean b) throws StandardException {
        Serializable map = null;
        if (this.notifyOnSet != null) {
            synchronized (this) {
                for (int i = 0; i < this.notifyOnSet.size(); ++i) {
                    final PropertySetCallback propertySetCallback = this.notifyOnSet.get(i);
                    if (propertySetCallback.validate(s, s2, dictionary)) {
                        if (map == null) {
                            map = propertySetCallback.map(s, s2, dictionary);
                        }
                        if (b || !s.startsWith("derby.") || PropertyUtil.whereSet(s, dictionary) != 0) {
                            final Serviceable apply;
                            if ((apply = propertySetCallback.apply(s, s2, dictionary)) != null) {
                                ((TransactionManager)transactionController).addPostCommitWork(apply);
                            }
                        }
                    }
                }
            }
        }
        return map;
    }
    
    public Serializable doMap(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        Serializable map = null;
        if (this.notifyOnSet != null) {
            for (int index = 0; index < this.notifyOnSet.size() && map == null; map = ((PropertySetCallback)this.notifyOnSet.get(index)).map(s, s2, dictionary), ++index) {}
        }
        if (map == null) {
            return s2;
        }
        return map;
    }
    
    public void validateSingleProperty(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        if (s.equals("logDevice")) {
            throw StandardException.newException("XSRS8.S");
        }
        if (this.notifyOnSet != null) {
            for (int i = 0; i < this.notifyOnSet.size(); ++i) {
                ((PropertySetCallback)this.notifyOnSet.get(i)).validate(s, s2, dictionary);
            }
        }
    }
    
    public synchronized void addPropertySetNotification(final PropertySetCallback e) {
        if (this.notifyOnSet == null) {
            this.notifyOnSet = new Vector(1, 1);
        }
        this.notifyOnSet.add(e);
    }
    
    public synchronized void verifyPropertySet(final Properties properties, final Properties properties2) throws StandardException {
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String s = (String)propertyNames.nextElement();
            if (properties2.getProperty(s) != null) {
                continue;
            }
            this.validateSingleProperty(s, properties.getProperty(s), properties);
        }
    }
}
