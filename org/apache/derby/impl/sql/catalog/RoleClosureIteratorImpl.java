// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.RoleGrantDescriptor;
import java.util.ArrayList;
import org.apache.derby.iapi.store.access.TransactionController;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import org.apache.derby.iapi.sql.dictionary.RoleClosureIterator;

public class RoleClosureIteratorImpl implements RoleClosureIterator
{
    private final boolean inverse;
    private HashMap seenSoFar;
    private HashMap graph;
    private List lifo;
    private Iterator currNodeIter;
    private DataDictionaryImpl dd;
    private TransactionController tc;
    private String root;
    private boolean initial;
    
    RoleClosureIteratorImpl(final String root, final boolean inverse, final DataDictionaryImpl dd, final TransactionController tc) {
        this.inverse = inverse;
        this.graph = null;
        this.root = root;
        this.dd = dd;
        this.tc = tc;
        this.seenSoFar = new HashMap();
        this.lifo = new ArrayList();
        final RoleGrantDescriptor roleGrantDescriptor = new RoleGrantDescriptor(null, null, inverse ? root : null, inverse ? null : root, null, false, false);
        final ArrayList<RoleGrantDescriptor> list = new ArrayList<RoleGrantDescriptor>();
        list.add(roleGrantDescriptor);
        this.currNodeIter = list.iterator();
        this.initial = true;
    }
    
    public String next() throws StandardException {
        if (this.initial) {
            this.initial = false;
            this.seenSoFar.put(this.root, null);
            return this.root;
        }
        if (this.graph == null) {
            this.graph = this.dd.getRoleGrantGraph(this.tc, this.inverse);
            final List list = this.graph.get(this.root);
            if (list != null) {
                this.currNodeIter = list.iterator();
            }
        }
        RoleGrantDescriptor roleGrantDescriptor = null;
        while (roleGrantDescriptor == null) {
            while (this.currNodeIter.hasNext()) {
                final RoleGrantDescriptor roleGrantDescriptor2 = this.currNodeIter.next();
                if (this.seenSoFar.containsKey(this.inverse ? roleGrantDescriptor2.getRoleName() : roleGrantDescriptor2.getGrantee())) {
                    continue;
                }
                this.lifo.add(roleGrantDescriptor2);
                roleGrantDescriptor = roleGrantDescriptor2;
                break;
            }
            if (roleGrantDescriptor == null) {
                this.currNodeIter = null;
                while (this.lifo.size() > 0 && this.currNodeIter == null) {
                    final RoleGrantDescriptor roleGrantDescriptor3 = this.lifo.remove(this.lifo.size() - 1);
                    final List list2 = this.graph.get(this.inverse ? roleGrantDescriptor3.getRoleName() : roleGrantDescriptor3.getGrantee());
                    if (list2 != null) {
                        this.currNodeIter = list2.iterator();
                    }
                }
                if (this.currNodeIter == null) {
                    this.currNodeIter = null;
                    break;
                }
                continue;
            }
        }
        if (roleGrantDescriptor != null) {
            final String key = this.inverse ? roleGrantDescriptor.getRoleName() : roleGrantDescriptor.getGrantee();
            this.seenSoFar.put(key, null);
            return key;
        }
        return null;
    }
}
