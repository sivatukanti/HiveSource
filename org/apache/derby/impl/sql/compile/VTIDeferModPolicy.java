// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.Visitable;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import java.util.HashSet;
import org.apache.derby.vti.DeferModification;
import org.apache.derby.iapi.sql.compile.Visitor;

class VTIDeferModPolicy implements Visitor
{
    private boolean deferred;
    private DeferModification deferralControl;
    private int statementType;
    private int tableNumber;
    private final HashSet columns;
    
    public static boolean deferIt(final int n, final FromVTI fromVTI, final String[] array, final QueryTreeNode queryTreeNode) throws StandardException {
        try {
            final int resultSetType = fromVTI.getResultSetType();
            if ((n == 2 || n == 3) && resultSetType == 1003) {
                return false;
            }
            DeferModification deferralControl = fromVTI.getDeferralControl();
            if (deferralControl == null) {
                deferralControl = new DefaultVTIModDeferPolicy(fromVTI.getMethodCall().getJavaClassName(), 1005 == resultSetType);
            }
            if (deferralControl.alwaysDefer(n)) {
                return true;
            }
            if (queryTreeNode == null && n != 2) {
                return false;
            }
            final VTIDeferModPolicy vtiDeferModPolicy = new VTIDeferModPolicy(fromVTI, array, deferralControl, n);
            if (queryTreeNode != null) {
                queryTreeNode.accept(vtiDeferModPolicy);
            }
            if (n == 2) {
                final Iterator iterator = vtiDeferModPolicy.columns.iterator();
                while (iterator.hasNext()) {
                    if (deferralControl.columnRequiresDefer(n, iterator.next(), false)) {
                        return true;
                    }
                }
            }
            return vtiDeferModPolicy.deferred;
        }
        catch (SQLException ex) {
            throw StandardException.unexpectedUserException(ex);
        }
    }
    
    private VTIDeferModPolicy(final FromVTI fromVTI, final String[] a, final DeferModification deferralControl, final int statementType) {
        this.deferred = false;
        this.columns = new HashSet();
        this.deferralControl = deferralControl;
        this.statementType = statementType;
        this.tableNumber = fromVTI.getTableNumber();
        if (statementType == 2 && a != null) {
            this.columns.addAll(Arrays.asList(a));
        }
    }
    
    public Visitable visit(final Visitable visitable) throws StandardException {
        try {
            if (visitable instanceof ColumnReference && this.statementType != 1) {
                final ColumnReference columnReference = (ColumnReference)visitable;
                if (columnReference.getTableNumber() == this.tableNumber) {
                    final String columnName = columnReference.getColumnName();
                    if (this.statementType == 3) {
                        if (this.columns.add(columnName) && this.deferralControl.columnRequiresDefer(this.statementType, columnName, true)) {
                            this.deferred = true;
                        }
                    }
                    else if (this.statementType == 2 && this.columns.remove(columnName) && this.deferralControl.columnRequiresDefer(this.statementType, columnName, true)) {
                        this.deferred = true;
                    }
                }
            }
            else if (visitable instanceof SelectNode) {
                final FromList fromList = ((SelectNode)visitable).getFromList();
                for (int i = 0; i < fromList.size(); ++i) {
                    final FromTable fromTable = (FromTable)fromList.elementAt(i);
                    if (fromTable instanceof FromBaseTable) {
                        final TableDescriptor tableDescriptor = fromTable.getTableDescriptor();
                        if (this.deferralControl.subselectRequiresDefer(this.statementType, tableDescriptor.getSchemaName(), tableDescriptor.getName())) {
                            this.deferred = true;
                        }
                    }
                    else if (fromTable instanceof FromVTI && this.deferralControl.subselectRequiresDefer(this.statementType, ((FromVTI)fromTable).getMethodCall().getJavaClassName())) {
                        this.deferred = true;
                    }
                }
            }
        }
        catch (SQLException ex) {
            throw StandardException.unexpectedUserException(ex);
        }
        return visitable;
    }
    
    public boolean stopTraversal() {
        return this.deferred;
    }
    
    public boolean skipChildren(final Visitable visitable) {
        return false;
    }
    
    public boolean visitChildrenFirst(final Visitable visitable) {
        return false;
    }
}
