// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import java.util.Iterator;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import java.util.ArrayList;

public class ConglomerateDescriptorList extends ArrayList
{
    public ConglomerateDescriptor getConglomerateDescriptor(final long n) {
        ConglomerateDescriptor conglomerateDescriptor = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor2 = this.get(i);
            if (n == conglomerateDescriptor2.getConglomerateNumber()) {
                conglomerateDescriptor = conglomerateDescriptor2;
                break;
            }
        }
        return conglomerateDescriptor;
    }
    
    public ConglomerateDescriptor[] getConglomerateDescriptors(final long n) {
        final int size = this.size();
        int n2 = 0;
        final ConglomerateDescriptor[] array = new ConglomerateDescriptor[size];
        for (int i = 0; i < size; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor = this.get(i);
            if (n == conglomerateDescriptor.getConglomerateNumber()) {
                array[n2++] = conglomerateDescriptor;
            }
        }
        if (n2 == size) {
            return array;
        }
        final ConglomerateDescriptor[] array2 = new ConglomerateDescriptor[n2];
        System.arraycopy(array, 0, array2, 0, n2);
        return array2;
    }
    
    public ConglomerateDescriptor getConglomerateDescriptor(final String s) {
        ConglomerateDescriptor conglomerateDescriptor = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor2 = this.get(i);
            if (s.equals(conglomerateDescriptor2.getConglomerateName())) {
                conglomerateDescriptor = conglomerateDescriptor2;
                break;
            }
        }
        return conglomerateDescriptor;
    }
    
    public ConglomerateDescriptor getConglomerateDescriptor(final UUID uuid) throws StandardException {
        ConglomerateDescriptor conglomerateDescriptor = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor2 = this.get(i);
            if (uuid.equals(conglomerateDescriptor2.getUUID())) {
                conglomerateDescriptor = conglomerateDescriptor2;
                break;
            }
        }
        return conglomerateDescriptor;
    }
    
    public ConglomerateDescriptor[] getConglomerateDescriptors(final UUID uuid) {
        final int size = this.size();
        int n = 0;
        final ConglomerateDescriptor[] array = new ConglomerateDescriptor[size];
        for (int i = 0; i < size; ++i) {
            final ConglomerateDescriptor conglomerateDescriptor = this.get(i);
            if (uuid.equals(conglomerateDescriptor.getUUID())) {
                array[n++] = conglomerateDescriptor;
            }
        }
        if (n == size) {
            return array;
        }
        final ConglomerateDescriptor[] array2 = new ConglomerateDescriptor[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public void dropConglomerateDescriptor(final UUID uuid, final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        final Iterator<ConglomerateDescriptor> iterator = (Iterator<ConglomerateDescriptor>)this.iterator();
        while (iterator.hasNext()) {
            final ConglomerateDescriptor conglomerateDescriptor2 = iterator.next();
            if (conglomerateDescriptor2.getConglomerateNumber() == conglomerateDescriptor.getConglomerateNumber() && conglomerateDescriptor2.getConglomerateName().equals(conglomerateDescriptor.getConglomerateName()) && conglomerateDescriptor2.getSchemaID().equals(conglomerateDescriptor.getSchemaID())) {
                iterator.remove();
                break;
            }
        }
    }
    
    public void dropConglomerateDescriptorByUUID(final UUID uuid) throws StandardException {
        final Iterator<ConglomerateDescriptor> iterator = (Iterator<ConglomerateDescriptor>)this.iterator();
        while (iterator.hasNext()) {
            if (uuid.equals(iterator.next().getUUID())) {
                iterator.remove();
                break;
            }
        }
    }
}
