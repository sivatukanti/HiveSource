// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.UUID;
import java.util.ArrayList;

public class ConstraintDescriptorList extends ArrayList
{
    private boolean scanned;
    
    public void setScanned(final boolean scanned) {
        this.scanned = scanned;
    }
    
    public boolean getScanned() {
        return this.scanned;
    }
    
    public ConstraintDescriptor getConstraintDescriptor(final UUID obj) {
        ConstraintDescriptor constraintDescriptor = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = this.elementAt(i);
            if (element instanceof KeyConstraintDescriptor) {
                if (((KeyConstraintDescriptor)element).getIndexId().equals(obj)) {
                    constraintDescriptor = element;
                    break;
                }
            }
        }
        return constraintDescriptor;
    }
    
    public ConstraintDescriptor getConstraintDescriptorById(final UUID obj) {
        ConstraintDescriptor constraintDescriptor = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = this.elementAt(i);
            if (element.getUUID().equals(obj)) {
                constraintDescriptor = element;
                break;
            }
        }
        return constraintDescriptor;
    }
    
    public ConstraintDescriptor dropConstraintDescriptorById(final UUID obj) {
        ConstraintDescriptor element = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            element = this.elementAt(i);
            if (element.getUUID().equals(obj)) {
                this.remove(element);
                break;
            }
        }
        return element;
    }
    
    public ConstraintDescriptor getConstraintDescriptorByName(final SchemaDescriptor schemaDescriptor, final String anObject) {
        ConstraintDescriptor constraintDescriptor = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = this.elementAt(i);
            if (element.getConstraintName().equals(anObject) && (schemaDescriptor == null || schemaDescriptor.equals(element.getSchemaDescriptor()))) {
                constraintDescriptor = element;
                break;
            }
        }
        return constraintDescriptor;
    }
    
    public ReferencedKeyConstraintDescriptor getPrimaryKey() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = this.elementAt(i);
            if (element.getConstraintType() == 2) {
                return (ReferencedKeyConstraintDescriptor)element;
            }
        }
        return null;
    }
    
    public ConstraintDescriptorList getConstraintDescriptorList(final boolean b) {
        final ConstraintDescriptorList list = new ConstraintDescriptorList();
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = this.elementAt(i);
            if (element.isEnabled() == b) {
                list.add(element);
            }
        }
        return list;
    }
    
    public ConstraintDescriptor elementAt(final int index) {
        return this.get(index);
    }
    
    public ConstraintDescriptorList getSubList(final int n) {
        final ConstraintDescriptorList list = new ConstraintDescriptorList();
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = this.elementAt(i);
            if (element.getConstraintType() == n) {
                list.add(element);
            }
        }
        return list;
    }
}
