// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import java.util.Map;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.List;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import java.util.Stack;

class Deadlock
{
    private Deadlock() {
    }
    
    static Object[] look(final AbstractPool abstractPool, final LockTable lockTable, final LockControl lockControl, final ActiveLock activeLock, final byte b) {
        final Hashtable waiters = getWaiters(lockTable);
        final Stack stack = new Stack<CompatibilitySpace>();
        stack.push(activeLock.getCompatabilitySpace());
        stack.push((CompatibilitySpace)lockControl.getGrants());
    Label_0035:
        while (!stack.isEmpty()) {
            final List o = (List)stack.peek();
            if (o.isEmpty()) {
                rollback(stack);
            }
            else {
                final int n = o.size() - 1;
                CompatibilitySpace compatibilitySpace = o.get(n).getCompatabilitySpace();
                for (int i = 0; i < n; ++i) {
                    if (compatibilitySpace.equals(o.get(i).getCompatabilitySpace())) {
                        stack.push(compatibilitySpace);
                        rollback(stack);
                        continue Label_0035;
                    }
                }
            Label_0157:
                while (true) {
                    final int index = stack.indexOf(compatibilitySpace);
                    if (index != -1) {
                        if ((index == stack.size() - 1 || (index == stack.size() - 2 && index == stack.indexOf(o) - 1)) && ((ActiveLock)waiters.get(compatibilitySpace)).canSkip) {
                            stack.push(compatibilitySpace);
                            rollback(stack);
                            break;
                        }
                        return handle(abstractPool, stack, index, waiters, b);
                    }
                    else {
                        stack.push(compatibilitySpace);
                        Object value = null;
                        Block_10: {
                            while (true) {
                                final ActiveLock activeLock2 = (ActiveLock)waiters.get(compatibilitySpace);
                                if (activeLock2 == null) {
                                    break;
                                }
                                value = waiters.get(activeLock2);
                                if (value instanceof LockControl) {
                                    break Block_10;
                                }
                                final ActiveLock activeLock3 = (ActiveLock)value;
                                compatibilitySpace = activeLock3.getCompatabilitySpace();
                                if (activeLock2.getLockable().requestCompatible(activeLock2.getQualifier(), activeLock3.getQualifier())) {
                                    continue;
                                }
                                continue Label_0157;
                            }
                            rollback(stack);
                            break;
                        }
                        final LockControl lockControl2 = (LockControl)value;
                        if (lockControl2.isUnlocked()) {
                            rollback(stack);
                            break;
                        }
                        stack.push((CompatibilitySpace)lockControl2.getGrants());
                        break;
                    }
                }
            }
        }
        return null;
    }
    
    private static void rollback(final Stack stack) {
        do {
            stack.pop();
            if (stack.isEmpty()) {
                return;
            }
        } while (!(stack.peek() instanceof List));
        final List list = stack.peek();
        list.remove(list.size() - 1);
    }
    
    private static Hashtable getWaiters(final LockTable lockTable) {
        final Hashtable hashtable = new Hashtable();
        lockTable.addWaiters(hashtable);
        return hashtable;
    }
    
    private static Object[] handle(final AbstractPool abstractPool, final Stack stack, final int n, final Dictionary dictionary, final byte b) {
        final Object element = stack.elementAt(0);
        int n2 = Integer.MAX_VALUE;
        Object obj = null;
        for (int i = n; i < stack.size(); ++i) {
            final Object element2 = stack.elementAt(i);
            if (!(element2 instanceof List)) {
                if (element.equals(element2) && b == 2) {
                    obj = element;
                    break;
                }
                final int deadlockCount = ((LockSpace)element2).deadlockCount(n2);
                if (deadlockCount <= n2) {
                    obj = element2;
                    n2 = deadlockCount;
                }
            }
        }
        if (element.equals(obj)) {
            return new Object[] { stack, dictionary };
        }
        dictionary.get(obj).wakeUp((byte)2);
        return null;
    }
    
    static StandardException buildException(final AbstractPool abstractPool, final Object[] array) {
        final Stack stack = (Stack)array[0];
        final Dictionary dictionary = (Dictionary)array[1];
        final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext");
        TableNameInfo tableNameInfo = null;
        TransactionInfo[] transactionInfo = null;
        TransactionController transactionExecute = null;
        if (languageConnectionContext != null) {
            try {
                transactionExecute = languageConnectionContext.getTransactionExecute();
                tableNameInfo = new TableNameInfo(languageConnectionContext, false);
                transactionInfo = transactionExecute.getAccessManager().getTransactionInfo();
            }
            catch (StandardException ex) {}
        }
        final StringBuffer sb = new StringBuffer(200);
        final Hashtable<Object, Long> hashtable = new Hashtable<Object, Long>(17);
        Object o = null;
        for (int i = 0; i < stack.size(); ++i) {
            final Object element = stack.elementAt(i);
            if (element instanceof List) {
                final List<Lock> list = (List<Lock>)element;
                if (list.size() != 0) {
                    sb.append("  Granted XID : ");
                    for (int j = 0; j < list.size(); ++j) {
                        if (j != 0) {
                            sb.append(", ");
                        }
                        final Lock lock = list.get(j);
                        sb.append("{");
                        sb.append(lock.getCompatabilitySpace().getOwner());
                        sb.append(", ");
                        sb.append(lock.getQualifier());
                        sb.append("} ");
                    }
                    sb.append('\n');
                }
            }
            else {
                final Lock lock2 = dictionary.get(element);
                lock2.getLockable().lockAttributes(-1, hashtable);
                addInfo(sb, "Lock : ", hashtable.get("TYPE"));
                if (tableNameInfo != null) {
                    Long n = hashtable.get("CONGLOMID");
                    if (n == null) {
                        final Long n2 = hashtable.get("CONTAINERID");
                        try {
                            n = new Long(transactionExecute.findConglomid(n2));
                        }
                        catch (StandardException ex2) {}
                    }
                    addInfo(sb, ", ", tableNameInfo.getTableName(n));
                }
                addInfo(sb, ", ", hashtable.get("LOCKNAME"));
                sb.append('\n');
                final String value = String.valueOf(lock2.getCompatabilitySpace().getOwner());
                if (i == 0) {
                    o = value;
                }
                addInfo(sb, "  Waiting XID : {", value);
                addInfo(sb, ", ", lock2.getQualifier());
                sb.append("} ");
                if (transactionInfo != null) {
                    for (int k = transactionInfo.length - 1; k >= 0; --k) {
                        final TransactionInfo transactionInfo2 = transactionInfo[k];
                        if (transactionInfo2 != null) {
                            final String transactionIdString = transactionInfo2.getTransactionIdString();
                            if (transactionIdString != null && transactionIdString.equals(value)) {
                                addInfo(sb, ", ", transactionInfo2.getUsernameString());
                                addInfo(sb, ", ", transactionInfo2.getStatementTextString());
                                break;
                            }
                        }
                    }
                }
                sb.append('\n');
                hashtable.clear();
            }
        }
        final StandardException exception = StandardException.newException("40001", sb.toString(), o);
        exception.setReport(abstractPool.deadlockMonitor);
        return exception;
    }
    
    private static void addInfo(final StringBuffer sb, final String str, Object obj) {
        sb.append(str);
        if (obj == null) {
            obj = "?";
        }
        sb.append(obj);
    }
}
