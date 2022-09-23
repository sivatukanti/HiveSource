// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.depend;

import java.util.Collection;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.Enumeration;
import org.apache.derby.iapi.sql.depend.ProviderList;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import java.util.List;
import org.apache.derby.impl.sql.compile.CreateViewNode;
import org.apache.derby.iapi.sql.dictionary.ViewDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.dictionary.TupleDescriptor;
import org.apache.derby.iapi.sql.dictionary.DependencyDescriptor;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.sql.depend.Dependency;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.ProviderInfo;
import java.util.Map;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.DependencyManager;

public class BasicDependencyManager implements DependencyManager
{
    private final DataDictionary dd;
    private final Map dependents;
    private final Map providers;
    private static final ProviderInfo[] EMPTY_PROVIDER_INFO;
    
    public void addDependency(final Dependent dependent, final Provider provider, final ContextManager contextManager) throws StandardException {
        this.addDependency(dependent, provider, contextManager, null);
    }
    
    private void addDependency(final Dependent dependent, final Provider provider, final ContextManager contextManager, final TransactionController transactionController) throws StandardException {
        if (!dependent.isPersistent() || !provider.isPersistent()) {
            this.addInMemoryDependency(dependent, provider, contextManager);
        }
        else {
            this.addStoredDependency(dependent, provider, contextManager, transactionController);
        }
    }
    
    private synchronized void addInMemoryDependency(final Dependent dependent, final Provider provider, final ContextManager contextManager) throws StandardException {
        final BasicDependency basicDependency = new BasicDependency(dependent, provider);
        if (this.addDependencyToTable(this.dependents, dependent.getObjectID(), basicDependency)) {
            this.addDependencyToTable(this.providers, provider.getObjectID(), basicDependency);
        }
        ((StatementContext)contextManager.getContext("StatementContext")).addDependency(basicDependency);
    }
    
    private void addStoredDependency(final Dependent dependent, final Provider provider, final ContextManager contextManager, final TransactionController transactionController) throws StandardException {
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext(contextManager);
        this.dd.addDescriptor(new DependencyDescriptor(dependent, provider), null, 6, true, (transactionController == null) ? languageConnectionContext.getTransactionExecute() : transactionController);
    }
    
    private void dropDependency(final LanguageConnectionContext languageConnectionContext, final Dependent dependent, final Provider provider) throws StandardException {
        this.dd.dropStoredDependency(new DependencyDescriptor(dependent, provider), languageConnectionContext.getTransactionExecute());
    }
    
    public void invalidateFor(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        if (provider.isPersistent()) {
            this.coreInvalidateFor(provider, n, languageConnectionContext);
        }
        else {
            synchronized (this) {
                this.coreInvalidateFor(provider, n, languageConnectionContext);
            }
        }
    }
    
    private void coreInvalidateFor(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final List dependents = this.getDependents(provider);
        if (dependents.isEmpty()) {
            return;
        }
        FormatableBitSet referencedColumnMap = null;
        FormatableBitSet set = null;
        if (provider instanceof TableDescriptor) {
            referencedColumnMap = ((TableDescriptor)provider).getReferencedColumnMap();
            if (referencedColumnMap != null) {
                set = new FormatableBitSet(referencedColumnMap.getLength());
            }
        }
        Throwable cause = null;
        for (int i = dependents.size() - 1; i >= 0; --i) {
            if (i < dependents.size()) {
                final Dependency dependency = dependents.get(i);
                final Dependent dependent = dependency.getDependent();
                if (referencedColumnMap != null) {
                    final TableDescriptor tableDescriptor = (TableDescriptor)dependency.getProvider();
                    final FormatableBitSet referencedColumnMap2 = tableDescriptor.getReferencedColumnMap();
                    if (referencedColumnMap2 == null) {
                        if (dependent instanceof ViewDescriptor) {
                            final ViewDescriptor viewDescriptor = (ViewDescriptor)dependent;
                            final CompilerContext pushCompilerContext = languageConnectionContext.pushCompilerContext(this.dd.getSchemaDescriptor(viewDescriptor.getCompSchemaId(), null));
                            final CreateViewNode createViewNode = (CreateViewNode)pushCompilerContext.getParser().parseStatement(viewDescriptor.getViewText());
                            pushCompilerContext.setCurrentDependent(dependent);
                            createViewNode.bindStatement();
                            final ProviderInfo[] providerInfo = createViewNode.getProviderInfo();
                            languageConnectionContext.popCompilerContext(pushCompilerContext);
                            boolean b = false;
                            for (int j = 0; j < providerInfo.length; ++j) {
                                final Provider provider2 = (Provider)providerInfo[j].getDependableFinder().getDependable(this.dd, providerInfo[j].getObjectId());
                                if (provider2 instanceof TableDescriptor) {
                                    final TableDescriptor tableDescriptor2 = (TableDescriptor)provider2;
                                    final FormatableBitSet referencedColumnMap3 = tableDescriptor2.getReferencedColumnMap();
                                    if (referencedColumnMap3 != null) {
                                        tableDescriptor2.setReferencedColumnMap(null);
                                        this.dropDependency(languageConnectionContext, viewDescriptor, tableDescriptor2);
                                        tableDescriptor2.setReferencedColumnMap(referencedColumnMap3);
                                        this.addDependency(viewDescriptor, tableDescriptor2, languageConnectionContext.getContextManager());
                                        if (tableDescriptor2.getObjectID().equals(tableDescriptor.getObjectID())) {
                                            System.arraycopy(referencedColumnMap.getByteArray(), 0, set.getByteArray(), 0, referencedColumnMap.getLengthInBytes());
                                            set.and(referencedColumnMap3);
                                            if (set.anySetBit() != -1) {
                                                b = true;
                                                ((TableDescriptor)provider).setReferencedColumnMap(set);
                                            }
                                        }
                                    }
                                }
                            }
                            if (!b) {
                                continue;
                            }
                        }
                        else {
                            ((TableDescriptor)provider).setReferencedColumnMap(null);
                        }
                    }
                    else {
                        System.arraycopy(referencedColumnMap.getByteArray(), 0, set.getByteArray(), 0, referencedColumnMap.getLengthInBytes());
                        set.and(referencedColumnMap2);
                        if (set.anySetBit() == -1) {
                            continue;
                        }
                        ((TableDescriptor)provider).setReferencedColumnMap(set);
                    }
                }
                try {
                    dependent.prepareToInvalidate(provider, n, languageConnectionContext);
                }
                catch (StandardException ex) {
                    if (cause == null) {
                        cause = ex;
                    }
                    else {
                        try {
                            ex.initCause(cause);
                            cause = ex;
                        }
                        catch (IllegalStateException ex2) {}
                    }
                }
                if (cause == null) {
                    if (referencedColumnMap != null) {
                        ((TableDescriptor)provider).setReferencedColumnMap(referencedColumnMap);
                    }
                    dependent.makeInvalid(n, languageConnectionContext);
                }
            }
        }
        if (cause != null) {
            throw cause;
        }
    }
    
    public void clearDependencies(final LanguageConnectionContext languageConnectionContext, final Dependent dependent) throws StandardException {
        this.clearDependencies(languageConnectionContext, dependent, null);
    }
    
    public void clearDependencies(final LanguageConnectionContext languageConnectionContext, final Dependent dependent, final TransactionController transactionController) throws StandardException {
        final UUID objectID = dependent.getObjectID();
        if (dependent.isPersistent()) {
            final boolean b = transactionController == null;
            this.dd.dropDependentsStoredDependencies(objectID, b ? languageConnectionContext.getTransactionExecute() : transactionController, b);
        }
        synchronized (this) {
            final List<Dependency> list = this.dependents.get(objectID);
            if (list != null) {
                for (final Dependency dependency : list) {
                    this.clearProviderDependency(dependency.getProviderKey(), dependency);
                }
                this.dependents.remove(objectID);
            }
        }
    }
    
    public synchronized void clearInMemoryDependency(final Dependency dependency) {
        final UUID objectID = dependency.getDependent().getObjectID();
        final UUID providerKey = dependency.getProviderKey();
        final List list = this.dependents.get(objectID);
        if (list == null) {
            return;
        }
        final List list2 = this.providers.get(providerKey);
        if (list2 == null) {
            return;
        }
        list.remove(dependency);
        if (list.isEmpty()) {
            this.dependents.remove(objectID);
        }
        list2.remove(dependency);
        if (list2.isEmpty()) {
            this.providers.remove(providerKey);
        }
    }
    
    public ProviderInfo[] getPersistentProviderInfos(final Dependent dependent) throws StandardException {
        final List providers = this.getProviders(dependent);
        if (providers.isEmpty()) {
            return BasicDependencyManager.EMPTY_PROVIDER_INFO;
        }
        final Iterator<Provider> iterator = providers.iterator();
        final ArrayList list = new ArrayList<BasicProviderInfo>();
        while (iterator.hasNext()) {
            final Provider provider = iterator.next();
            if (provider.isPersistent()) {
                list.add(new BasicProviderInfo(provider.getObjectID(), provider.getDependableFinder(), provider.getObjectName()));
            }
        }
        return (ProviderInfo[])list.toArray(BasicDependencyManager.EMPTY_PROVIDER_INFO);
    }
    
    public ProviderInfo[] getPersistentProviderInfos(final ProviderList list) throws StandardException {
        final Enumeration<Object> elements = list.elements();
        int n = 0;
        while (elements != null && elements.hasMoreElements()) {
            if (elements.nextElement().isPersistent()) {
                ++n;
            }
        }
        final Enumeration<Object> elements2 = list.elements();
        final ProviderInfo[] array = new ProviderInfo[n];
        int n2 = 0;
        while (elements2 != null && elements2.hasMoreElements()) {
            final Provider provider = elements2.nextElement();
            if (provider.isPersistent()) {
                array[n2++] = new BasicProviderInfo(provider.getObjectID(), provider.getDependableFinder(), provider.getObjectName());
            }
        }
        return array;
    }
    
    public void clearColumnInfoInProviders(final ProviderList list) throws StandardException {
        final Enumeration<Provider> elements = list.elements();
        while (elements.hasMoreElements()) {
            final Provider provider = elements.nextElement();
            if (provider instanceof TableDescriptor) {
                ((TableDescriptor)provider).setReferencedColumnMap(null);
            }
        }
    }
    
    public void copyDependencies(final Dependent dependent, final Dependent dependent2, final boolean b, final ContextManager contextManager) throws StandardException {
        this.copyDependencies(dependent, dependent2, b, contextManager, null);
    }
    
    public void copyDependencies(final Dependent dependent, final Dependent dependent2, final boolean b, final ContextManager contextManager, final TransactionController transactionController) throws StandardException {
        for (final Provider provider : this.getProviders(dependent)) {
            if (!b || provider.isPersistent()) {
                this.addDependency(dependent2, provider, contextManager, transactionController);
            }
        }
    }
    
    public String getActionString(final int n) {
        switch (n) {
            case 12: {
                return "ALTER TABLE";
            }
            case 34: {
                return "RENAME";
            }
            case 41: {
                return "RENAME INDEX";
            }
            case 0: {
                return "COMPILE FAILED";
            }
            case 1: {
                return "DROP TABLE";
            }
            case 2: {
                return "DROP INDEX";
            }
            case 9: {
                return "DROP VIEW";
            }
            case 3: {
                return "CREATE INDEX";
            }
            case 4: {
                return "ROLLBACK";
            }
            case 5: {
                return "CHANGED CURSOR";
            }
            case 22: {
                return "CREATE CONSTRAINT";
            }
            case 19: {
                return "DROP CONSTRAINT";
            }
            case 6: {
                return "DROP ROUTINE";
            }
            case 11: {
                return "PREPARED STATEMENT RELEASE";
            }
            case 13: {
                return "DROP STORED PREPARED STATEMENT";
            }
            case 14: {
                return "USER REQUESTED INVALIDATION";
            }
            case 15: {
                return "BULK INSERT";
            }
            case 10: {
                return "CREATE_VIEW";
            }
            case 17: {
                return "DROP_JAR";
            }
            case 18: {
                return "REPLACE_JAR";
            }
            case 20: {
                return "SET_CONSTRAINTS_ENABLE";
            }
            case 21: {
                return "SET_CONSTRAINTS_DISABLE";
            }
            case 23: {
                return "INTERNAL RECOMPILE REQUEST";
            }
            case 28: {
                return "CREATE TRIGGER";
            }
            case 27: {
                return "DROP TRIGGER";
            }
            case 29: {
                return "SET TRIGGERS ENABLED";
            }
            case 30: {
                return "SET TRIGGERS DISABLED";
            }
            case 31: {
                return "MODIFY COLUMN DEFAULT";
            }
            case 33: {
                return "COMPRESS TABLE";
            }
            case 37: {
                return "DROP COLUMN";
            }
            case 46: {
                return "DROP COLUMN RESTRICT";
            }
            case 39: {
                return "DROP STATISTICS";
            }
            case 40: {
                return "UPDATE STATISTICS";
            }
            case 42: {
                return "TRUNCATE TABLE";
            }
            case 43: {
                return "DROP SYNONYM";
            }
            case 44: {
                return "REVOKE PRIVILEGE";
            }
            case 45: {
                return "REVOKE PRIVILEGE RESTRICT";
            }
            case 47: {
                return "REVOKE ROLE";
            }
            case 48: {
                return "RECHECK PRIVILEGES";
            }
            case 49: {
                return "DROP SEQUENCE";
            }
            case 50: {
                return "DROP TYPE";
            }
            case 51: {
                return "DROP DERBY AGGREGATE";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    public int countDependencies() throws StandardException {
        int size = this.dd.getAllDependencyDescriptorsList().size();
        synchronized (this) {
            final Iterator<List> iterator = this.dependents.values().iterator();
            final Iterator<List> iterator2 = this.providers.values().iterator();
            while (iterator.hasNext()) {
                size += iterator.next().size();
            }
            while (iterator2.hasNext()) {
                size += iterator2.next().size();
            }
        }
        return size;
    }
    
    public BasicDependencyManager(final DataDictionary dd) {
        this.dependents = new HashMap();
        this.providers = new HashMap();
        this.dd = dd;
    }
    
    private boolean addDependencyToTable(final Map map, final Object o, final Dependency dependency) {
        final ArrayList<Dependency> list = map.get(o);
        if (list == null) {
            final ArrayList<Dependency> list2 = new ArrayList<Dependency>();
            list2.add(dependency);
            map.put(o, list2);
        }
        else {
            final UUID objectID = dependency.getProvider().getObjectID();
            final UUID objectID2 = dependency.getDependent().getObjectID();
            final ListIterator<Object> listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                final Dependency dependency2 = listIterator.next();
                if (dependency2.getProvider().getObjectID().equals(objectID) && dependency2.getDependent().getObjectID().equals(objectID2)) {
                    return false;
                }
            }
            list.add(dependency);
        }
        return true;
    }
    
    private void clearProviderDependency(final UUID uuid, final Dependency dependency) {
        final List list = this.providers.get(uuid);
        if (list == null) {
            return;
        }
        list.remove(dependency);
        if (list.size() == 0) {
            this.providers.remove(uuid);
        }
    }
    
    private List getDependencyDescriptorList(final List list, final Provider provider) throws StandardException {
        if (list.size() != 0) {
            final ListIterator<DependencyDescriptor> listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                final DependencyDescriptor dependencyDescriptor = listIterator.next();
                final Dependent dependent = (Dependent)dependencyDescriptor.getDependentFinder().getDependable(this.dd, dependencyDescriptor.getUUID());
                Provider provider2;
                if (provider != null) {
                    provider2 = provider;
                }
                else {
                    provider2 = (Provider)dependencyDescriptor.getProviderFinder().getDependable(this.dd, dependencyDescriptor.getProviderID());
                }
                listIterator.set((DependencyDescriptor)new BasicDependency(dependent, provider2));
            }
        }
        return list;
    }
    
    private LanguageConnectionContext getLanguageConnectionContext(final ContextManager contextManager) {
        return (LanguageConnectionContext)contextManager.getContext("LanguageConnectionContext");
    }
    
    private List getProviders(final Dependent dependent) throws StandardException {
        final ArrayList<Provider> list = new ArrayList<Provider>();
        synchronized (this) {
            final List<Dependency> list2 = this.dependents.get(dependent.getObjectID());
            if (list2 != null) {
                final Iterator<Dependency> iterator = list2.iterator();
                while (iterator.hasNext()) {
                    list.add(iterator.next().getProvider());
                }
            }
        }
        if (dependent.isPersistent()) {
            final Iterator<Dependency> iterator2 = this.getDependencyDescriptorList(this.dd.getDependentsDescriptorList(dependent.getObjectID().toString()), null).iterator();
            while (iterator2.hasNext()) {
                list.add(iterator2.next().getProvider());
            }
        }
        return list;
    }
    
    private List getDependents(final Provider provider) throws StandardException {
        final ArrayList list = new ArrayList();
        synchronized (this) {
            final List list2 = this.providers.get(provider.getObjectID());
            if (list2 != null) {
                list.addAll(list2);
            }
        }
        if (provider.isPersistent()) {
            list.addAll(this.getDependencyDescriptorList(this.dd.getProvidersDescriptorList(provider.getObjectID().toString()), provider));
        }
        return list;
    }
    
    static {
        EMPTY_PROVIDER_INFO = new ProviderInfo[0];
    }
}
