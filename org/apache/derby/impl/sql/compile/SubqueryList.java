// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.error.StandardException;

public class SubqueryList extends QueryTreeNodeVector
{
    public void addSubqueryNode(final SubqueryNode subqueryNode) throws StandardException {
        this.addElement(subqueryNode);
    }
    
    public void optimize(final DataDictionary dataDictionary, final double n) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((SubqueryNode)this.elementAt(i)).optimize(dataDictionary, n);
        }
    }
    
    public void modifyAccessPaths() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((SubqueryNode)this.elementAt(i)).modifyAccessPaths();
        }
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final SubqueryNode subqueryNode = (SubqueryNode)this.elementAt(i);
            if (!subqueryNode.isMaterializable()) {
                if (subqueryNode.getResultSet().referencesTarget(s, b)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean referencesSessionSchema() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((SubqueryNode)this.elementAt(i)).referencesSessionSchema()) {
                return true;
            }
        }
        return false;
    }
    
    public void setPointOfAttachment(final int pointOfAttachment) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((SubqueryNode)this.elementAt(i)).setPointOfAttachment(pointOfAttachment);
        }
    }
    
    void decrementLevel(final int n) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((SubqueryNode)this.elementAt(i)).getResultSet().decrementLevel(n);
        }
    }
    
    public void markHavingSubqueries() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((SubqueryNode)this.elementAt(i)).setHavingSubquery(true);
        }
    }
    
    public void markWhereSubqueries() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((SubqueryNode)this.elementAt(i)).setWhereSubquery(true);
        }
    }
}
