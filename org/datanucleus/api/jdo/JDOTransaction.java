// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import javax.jdo.PersistenceManager;
import org.datanucleus.TransactionEventListener;
import org.datanucleus.api.jdo.exceptions.TransactionActiveException;
import org.datanucleus.api.jdo.exceptions.TransactionCommitingException;
import java.util.Map;
import javax.jdo.PersistenceManagerFactory;
import org.datanucleus.transaction.TransactionUtils;
import javax.jdo.JDOUnsupportedOptionException;
import javax.transaction.Synchronization;
import javax.jdo.JDOOptimisticVerificationException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusOptimisticException;
import javax.jdo.Transaction;

public class JDOTransaction implements Transaction
{
    org.datanucleus.Transaction tx;
    JDOPersistenceManager pm;
    
    public JDOTransaction(final JDOPersistenceManager pm, final org.datanucleus.Transaction tx) {
        this.tx = tx;
        this.pm = pm;
    }
    
    public boolean isActive() {
        return this.tx.isActive();
    }
    
    public void begin() {
        if (this.pm.isClosed()) {}
        this.internalBegin();
    }
    
    protected void internalBegin() {
        this.tx.begin();
    }
    
    public void commit() {
        try {
            this.tx.commit();
        }
        catch (NucleusException ne) {
            if (ne.getNestedExceptions() == null) {
                throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
            }
            if (!(ne.getNestedExceptions()[0] instanceof NucleusOptimisticException)) {
                NucleusException ex;
                if (ne.getNestedExceptions()[0] instanceof NucleusException) {
                    ex = (NucleusException)ne.getNestedExceptions()[0];
                }
                else {
                    ex = new NucleusException(ne.getNestedExceptions()[0].getMessage(), ne.getNestedExceptions()[0]);
                }
                throw NucleusJDOHelper.getJDOExceptionForNucleusException(ex);
            }
            if (ne.getNestedExceptions().length > 1) {
                final int numNested = ne.getNestedExceptions().length;
                final JDOOptimisticVerificationException[] jdoNested = new JDOOptimisticVerificationException[numNested];
                for (int i = 0; i < numNested; ++i) {
                    final NucleusException nested = (NucleusOptimisticException)ne.getNestedExceptions()[i];
                    jdoNested[i] = (JDOOptimisticVerificationException)NucleusJDOHelper.getJDOExceptionForNucleusException(nested);
                }
                throw new JDOOptimisticVerificationException(ne.getMessage(), jdoNested);
            }
            NucleusException ex;
            if (ne.getNestedExceptions()[0] instanceof NucleusException) {
                ex = (NucleusException)ne.getNestedExceptions()[0];
            }
            else {
                ex = new NucleusException(ne.getNestedExceptions()[0].getMessage(), ne.getNestedExceptions()[0]);
            }
            final Throwable[] nested2 = ex.getNestedExceptions();
            final JDOOptimisticVerificationException[] jdoNested2 = new JDOOptimisticVerificationException[nested2.length];
            for (int j = 0; j < nested2.length; ++j) {
                NucleusException nestedEx;
                if (nested2[j] instanceof NucleusException) {
                    nestedEx = (NucleusException)nested2[j];
                }
                else {
                    nestedEx = new NucleusException(nested2[j].getMessage(), nested2[j]);
                }
                jdoNested2[j] = (JDOOptimisticVerificationException)NucleusJDOHelper.getJDOExceptionForNucleusException(nestedEx);
            }
            throw new JDOOptimisticVerificationException(ne.getMessage(), jdoNested2);
        }
    }
    
    public void rollback() {
        try {
            this.tx.rollback();
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public boolean getNontransactionalRead() {
        return this.tx.getNontransactionalRead();
    }
    
    public boolean getNontransactionalWrite() {
        return this.tx.getNontransactionalWrite();
    }
    
    public boolean getOptimistic() {
        return this.tx.getOptimistic();
    }
    
    public JDOPersistenceManager getPersistenceManager() {
        return this.pm;
    }
    
    public boolean getRestoreValues() {
        return this.tx.getRestoreValues();
    }
    
    public boolean getRetainValues() {
        return this.tx.getRetainValues();
    }
    
    public boolean getRollbackOnly() {
        return this.tx.getRollbackOnly();
    }
    
    public Synchronization getSynchronization() {
        return this.tx.getSynchronization();
    }
    
    public void setNontransactionalRead(final boolean flag) {
        this.assertNotCommitting();
        this.tx.setNontransactionalRead(flag);
    }
    
    public void setNontransactionalWrite(final boolean flag) {
        this.assertNotCommitting();
        try {
            this.tx.setNontransactionalWrite(flag);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void setOptimistic(final boolean opt) {
        this.assertNotInUse();
        this.assertNotCommitting();
        this.tx.setOptimistic(opt);
    }
    
    public void setRestoreValues(final boolean restore) {
        this.assertNotInUse();
        this.assertNotCommitting();
        this.tx.setRestoreValues(restore);
    }
    
    public void setRetainValues(final boolean retain) {
        this.assertNotCommitting();
        this.tx.setRetainValues(retain);
    }
    
    public void setRollbackOnly() {
        if (this.tx.isActive()) {
            this.tx.setRollbackOnly();
        }
    }
    
    public void setSynchronization(final Synchronization synch) {
        this.tx.setSynchronization(synch);
    }
    
    public void setIsolationLevel(final String level) {
        this.assertNotCommitting();
        if (this.tx.isActive() && !this.tx.getOptimistic()) {
            throw new JDOUnsupportedOptionException("Cannot change the transaction isolation level while a datastore transaction is active");
        }
        final PersistenceManagerFactory pmf = this.pm.getPersistenceManagerFactory();
        if (!pmf.supportedOptions().contains("javax.jdo.option.TransactionIsolationLevel." + level)) {
            throw new JDOUnsupportedOptionException("Isolation level \"" + level + "\" not supported by this datastore");
        }
        final int isolationLevel = TransactionUtils.getTransactionIsolationLevelForName(level);
        this.tx.setOption("transaction.isolation", isolationLevel);
    }
    
    public String getIsolationLevel() {
        final Map<String, Object> txOptions = this.tx.getOptions();
        final Object value = (txOptions != null) ? txOptions.get("transaction.isolation") : null;
        if (value != null) {
            return TransactionUtils.getNameForTransactionIsolationLevel((int)value);
        }
        return null;
    }
    
    protected void assertNotCommitting() {
        if (this.tx.isCommitting()) {
            throw new TransactionCommitingException(this);
        }
    }
    
    protected void assertNotInUse() {
        if (this.tx.isActive()) {
            throw new TransactionActiveException(this);
        }
    }
    
    public Boolean getSerializeRead() {
        return this.tx.getSerializeRead();
    }
    
    public void setSerializeRead(final Boolean serialize) {
        this.assertNotCommitting();
        this.tx.setSerializeRead(serialize);
    }
    
    public void setOption(final String option, final int value) {
        this.tx.setOption(option, value);
    }
    
    public void setOption(final String option, final boolean value) {
        this.tx.setOption(option, value);
    }
    
    public void setOption(final String option, final String value) {
        this.tx.setOption(option, value);
    }
    
    public void registerEventListener(final TransactionEventListener listener) {
        this.tx.bindTransactionEventListener(listener);
    }
    
    public void deregisterEventListener(final TransactionEventListener listener) {
        this.tx.removeTransactionEventListener(listener);
    }
}
