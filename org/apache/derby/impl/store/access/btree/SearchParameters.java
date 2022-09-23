// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;

public class SearchParameters
{
    public static final int POSITION_LEFT_OF_PARTIAL_KEY_MATCH = 1;
    public static final int POSITION_RIGHT_OF_PARTIAL_KEY_MATCH = -1;
    public DataValueDescriptor[] searchKey;
    int partial_key_match_op;
    public DataValueDescriptor[] template;
    public OpenBTree btree;
    public int resultSlot;
    public boolean resultExact;
    public boolean searchForOptimizer;
    public float left_fraction;
    public float current_fraction;
    
    public SearchParameters(final DataValueDescriptor[] searchKey, final int partial_key_match_op, final DataValueDescriptor[] template, final OpenBTree btree, final boolean searchForOptimizer) throws StandardException {
        this.searchKey = searchKey;
        this.partial_key_match_op = partial_key_match_op;
        this.template = template;
        this.btree = btree;
        this.resultSlot = 0;
        this.resultExact = false;
        this.searchForOptimizer = searchForOptimizer;
        if (this.searchForOptimizer) {
            this.left_fraction = 0.0f;
            this.current_fraction = 1.0f;
        }
    }
    
    public String toString() {
        return null;
    }
}
