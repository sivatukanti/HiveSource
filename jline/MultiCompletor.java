// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MultiCompletor implements Completor
{
    Completor[] completors;
    
    public MultiCompletor() {
        this(new Completor[0]);
    }
    
    public MultiCompletor(final List completors) {
        this(completors.toArray(new Completor[completors.size()]));
    }
    
    public MultiCompletor(final Completor[] completors) {
        this.completors = new Completor[0];
        this.completors = completors;
    }
    
    public int complete(final String buffer, final int pos, final List cand) {
        final int[] positions = new int[this.completors.length];
        final List[] copies = new List[this.completors.length];
        for (int i = 0; i < this.completors.length; ++i) {
            copies[i] = new LinkedList(cand);
            positions[i] = this.completors[i].complete(buffer, pos, copies[i]);
        }
        int maxposition = -1;
        for (int j = 0; j < positions.length; ++j) {
            maxposition = Math.max(maxposition, positions[j]);
        }
        for (int j = 0; j < copies.length; ++j) {
            if (positions[j] == maxposition) {
                cand.addAll(copies[j]);
            }
        }
        return maxposition;
    }
    
    public void setCompletors(final Completor[] completors) {
        this.completors = completors;
    }
    
    public Completor[] getCompletors() {
        return this.completors;
    }
}
