// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.util.StringUtils;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.Serializable;

public class ValueGenerationBlock implements Serializable
{
    private int nextIndex;
    private final List valueList;
    
    public ValueGenerationBlock(final Object[] values) {
        this.nextIndex = 0;
        this.valueList = Arrays.asList(values);
    }
    
    public ValueGenerationBlock(final List oid) {
        this.nextIndex = 0;
        this.valueList = new ArrayList(oid);
    }
    
    public ValueGeneration current() {
        if (this.nextIndex == 0 || this.nextIndex - 1 >= this.valueList.size()) {
            throw new NoSuchElementException();
        }
        return new ValueGeneration(this.valueList.get(this.nextIndex - 1));
    }
    
    public ValueGeneration next() {
        if (this.nextIndex >= this.valueList.size()) {
            throw new NoSuchElementException();
        }
        return new ValueGeneration(this.valueList.get(this.nextIndex++));
    }
    
    public boolean hasNext() {
        return this.nextIndex < this.valueList.size();
    }
    
    public void addBlock(final ValueGenerationBlock block) {
        if (block == null) {
            return;
        }
        while (block.hasNext()) {
            this.valueList.add(block.next());
        }
    }
    
    @Override
    public String toString() {
        return "ValueGenerationBlock : " + StringUtils.collectionToString(this.valueList);
    }
}
