// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.sql.dictionary.SequenceDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.catalog.SequencePreallocator;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.types.NumberDataValue;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.cache.Cacheable;

public abstract class SequenceUpdater implements Cacheable
{
    protected DataDictionaryImpl _dd;
    protected String _uuidString;
    protected SequenceGenerator _sequenceGenerator;
    
    public SequenceUpdater() {
    }
    
    public SequenceUpdater(final DataDictionaryImpl dd) {
        this();
        this._dd = dd;
    }
    
    protected abstract SequenceGenerator createSequenceGenerator(final TransactionController p0) throws StandardException;
    
    protected abstract boolean updateCurrentValueOnDisk(final TransactionController p0, final Long p1, final Long p2, final boolean p3) throws StandardException;
    
    protected StandardException tooMuchContentionException() {
        return StandardException.newException("X0Y84.T", this._sequenceGenerator.getName());
    }
    
    public void clean(final boolean b) throws StandardException {
        if (this._sequenceGenerator != null && !this.updateCurrentValueOnDisk(null, this.peekAtCurrentValue())) {
            Monitor.getStream().println(MessageService.getTextMessage("X0Y86.S", this._sequenceGenerator.getSchemaName(), this._sequenceGenerator.getName()));
        }
        this._uuidString = null;
        this._sequenceGenerator = null;
    }
    
    public boolean isDirty() {
        return false;
    }
    
    public Object getIdentity() {
        return this._uuidString;
    }
    
    public void clearIdentity() {
        try {
            this.clean(false);
        }
        catch (StandardException ex) {
            final LanguageConnectionContext lcc = getLCC();
            if (lcc != null) {
                final Database database = lcc.getDatabase();
                lcc.getContextManager().cleanupOnError(ex, database != null && database.isActive());
            }
        }
    }
    
    public Cacheable createIdentity(final Object identity, final Object o) throws StandardException {
        return this.setIdentity(identity);
    }
    
    public Cacheable setIdentity(final Object o) throws StandardException {
        this._uuidString = (String)o;
        if (this._sequenceGenerator == null) {
            final TransactionController startNestedUserTransaction = getLCC().getTransactionExecute().startNestedUserTransaction(true, true);
            try {
                this._sequenceGenerator = this.createSequenceGenerator(startNestedUserTransaction);
            }
            finally {
                if (this._sequenceGenerator == null) {
                    this._uuidString = null;
                }
                startNestedUserTransaction.commit();
                startNestedUserTransaction.destroy();
            }
        }
        if (this._sequenceGenerator != null) {
            return this;
        }
        return null;
    }
    
    public synchronized void getCurrentValueAndAdvance(final NumberDataValue numberDataValue) throws StandardException {
        int i = 0;
        while (i < 2) {
            final long[] currentValueAndAdvance = this._sequenceGenerator.getCurrentValueAndAdvance();
            final int n = (int)currentValueAndAdvance[0];
            final long n2 = currentValueAndAdvance[1];
            final long value = currentValueAndAdvance[2];
            final long n3 = currentValueAndAdvance[3];
            switch (n) {
                case 1: {
                    numberDataValue.setValue(n2);
                    return;
                }
                case 2: {
                    this.updateCurrentValueOnDisk(new Long(n2), null);
                    numberDataValue.setValue(n2);
                    return;
                }
                case 3: {
                    if (this.updateCurrentValueOnDisk(new Long(n2), new Long(value))) {
                        this._sequenceGenerator.allocateNewRange(n2, n3);
                    }
                    ++i;
                    continue;
                }
                default: {
                    throw this.unimplementedFeature();
                }
            }
        }
        throw this.tooMuchContentionException();
    }
    
    public Long peekAtCurrentValue() throws StandardException {
        return this._sequenceGenerator.peekAtCurrentValue();
    }
    
    public synchronized boolean updateCurrentValueOnDisk(final Long n, final Long n2) throws StandardException {
        final LanguageConnectionContext lcc = getLCC();
        if (lcc == null) {
            final TransactionController transaction = this._dd.af.getTransaction(ContextService.getFactory().getCurrentContextManager());
            final boolean updateCurrentValueOnDisk = this.updateCurrentValueOnDisk(transaction, n, n2, false);
            transaction.commit();
            transaction.destroy();
            return updateCurrentValueOnDisk;
        }
        final TransactionController startNestedUserTransaction = lcc.getTransactionExecute().startNestedUserTransaction(false, true);
        if (startNestedUserTransaction != null) {
            try {
                return this.updateCurrentValueOnDisk(startNestedUserTransaction, n, n2, false);
            }
            catch (StandardException ex) {
                if (!ex.isLockTimeout()) {
                    throw ex;
                }
            }
            finally {
                startNestedUserTransaction.commit();
                startNestedUserTransaction.destroy();
            }
        }
        throw this.tooMuchContentionException();
    }
    
    protected SequencePreallocator makePreallocator(final TransactionController transactionController) throws StandardException {
        final String s = "derby.language.sequence.preallocator";
        final String serviceProperty = PropertyUtil.getServiceProperty(transactionController, s);
        if (serviceProperty == null) {
            return new SequenceRange();
        }
        try {
            if (this.isNumber(serviceProperty)) {
                return new SequenceRange(Integer.parseInt(serviceProperty));
            }
            return (SequencePreallocator)Class.forName(serviceProperty).newInstance();
        }
        catch (ClassNotFoundException ex) {
            throw this.missingAllocator(s, serviceProperty, ex);
        }
        catch (ClassCastException ex2) {
            throw this.missingAllocator(s, serviceProperty, ex2);
        }
        catch (InstantiationException ex3) {
            throw this.missingAllocator(s, serviceProperty, ex3);
        }
        catch (IllegalAccessException ex4) {
            throw this.missingAllocator(s, serviceProperty, ex4);
        }
        catch (NumberFormatException ex5) {
            throw this.missingAllocator(s, serviceProperty, ex5);
        }
    }
    
    private StandardException missingAllocator(final String s, final String s2, final Exception ex) {
        return StandardException.newException("X0Y85.S", ex, s, s2);
    }
    
    private boolean isNumber(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    private static LanguageConnectionContext getLCC() {
        return (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
    }
    
    private StandardException unimplementedFeature() {
        return StandardException.newException("XSCB3.S");
    }
    
    public static final class SyssequenceUpdater extends SequenceUpdater
    {
        private RowLocation _sequenceRowLocation;
        
        public SyssequenceUpdater() {
        }
        
        public SyssequenceUpdater(final DataDictionaryImpl dataDictionaryImpl) {
            super(dataDictionaryImpl);
        }
        
        protected SequenceGenerator createSequenceGenerator(final TransactionController transactionController) throws StandardException {
            final RowLocation[] array = { null };
            final SequenceDescriptor[] array2 = { null };
            this._dd.computeSequenceRowLocation(transactionController, this._uuidString, array, array2);
            this._sequenceRowLocation = array[0];
            final SequenceDescriptor sequenceDescriptor = array2[0];
            return new SequenceGenerator(sequenceDescriptor.getCurrentValue(), sequenceDescriptor.canCycle(), sequenceDescriptor.getIncrement(), sequenceDescriptor.getMaximumValue(), sequenceDescriptor.getMinimumValue(), sequenceDescriptor.getStartValue(), sequenceDescriptor.getSchemaDescriptor().getSchemaName(), sequenceDescriptor.getSequenceName(), this.makePreallocator(transactionController));
        }
        
        protected boolean updateCurrentValueOnDisk(final TransactionController transactionController, final Long n, final Long n2, final boolean b) throws StandardException {
            return this._dd.updateCurrentSequenceValue(transactionController, this._sequenceRowLocation, b, n, n2);
        }
    }
}
