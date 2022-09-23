// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.logging.Log;

public class DiskRangeList extends DiskRange
{
    private static final Log LOG;
    public DiskRangeList prev;
    public DiskRangeList next;
    
    public DiskRangeList(final long offset, final long end) {
        super(offset, end);
    }
    
    public DiskRangeList replaceSelfWith(final DiskRangeList other) {
        other.prev = this.prev;
        other.next = this.next;
        if (this.prev != null) {
            this.prev.next = other;
        }
        if (this.next != null) {
            this.next.prev = other;
        }
        final DiskRangeList list = null;
        this.prev = list;
        this.next = list;
        return other;
    }
    
    public DiskRangeList insertPartBefore(final DiskRangeList other) {
        assert other.end >= this.offset;
        this.offset = other.end;
        other.prev = this.prev;
        other.next = this;
        if (this.prev != null) {
            this.prev.next = other;
        }
        return this.prev = other;
    }
    
    public DiskRangeList insertAfter(final DiskRangeList other) {
        other.next = this.next;
        other.prev = this;
        if (this.next != null) {
            this.next.prev = other;
        }
        return this.next = other;
    }
    
    public DiskRangeList insertPartAfter(final DiskRangeList other) {
        assert other.offset <= this.end;
        this.end = other.offset;
        return this.insertAfter(other);
    }
    
    public void removeAfter() {
        final DiskRangeList other = this.next;
        this.next = other.next;
        if (this.next != null) {
            this.next.prev = this;
        }
        final DiskRangeList list = other;
        final DiskRangeList list2 = other;
        final DiskRangeList list3 = null;
        list2.prev = list3;
        list.next = list3;
    }
    
    public void removeSelf() {
        if (this.prev != null) {
            this.prev.next = this.next;
        }
        if (this.next != null) {
            this.next.prev = this.prev;
        }
        final DiskRangeList list = null;
        this.prev = list;
        this.next = list;
    }
    
    public DiskRangeList split(final long cOffset) {
        this.insertAfter((DiskRangeList)this.sliceAndShift(cOffset, this.end, 0L));
        return this.replaceSelfWith((DiskRangeList)this.sliceAndShift(this.offset, cOffset, 0L));
    }
    
    public boolean hasContiguousNext() {
        return this.next != null && this.end == this.next.offset;
    }
    
    @VisibleForTesting
    public int listSize() {
        int result = 1;
        for (DiskRangeList current = this.next; current != null; current = current.next) {
            ++result;
        }
        return result;
    }
    
    @VisibleForTesting
    public DiskRangeList[] listToArray() {
        final DiskRangeList[] result = new DiskRangeList[this.listSize()];
        int i = 0;
        for (DiskRangeList current = this.next; current != null; current = current.next) {
            result[i] = current;
            ++i;
        }
        return result;
    }
    
    static {
        LOG = LogFactory.getLog(DiskRangeList.class);
    }
    
    public static class DiskRangeListCreateHelper
    {
        private DiskRangeList tail;
        private DiskRangeList head;
        
        public DiskRangeListCreateHelper() {
            this.tail = null;
        }
        
        public DiskRangeList getTail() {
            return this.tail;
        }
        
        public void addOrMerge(final long offset, final long end, final boolean doMerge, final boolean doLogNew) {
            if (doMerge && this.tail != null && this.tail.merge(offset, end)) {
                return;
            }
            if (doLogNew) {
                DiskRangeList.LOG.info("Creating new range; last range (which can include some previous adds) was " + this.tail);
            }
            final DiskRangeList node = new DiskRangeList(offset, end);
            if (this.tail == null) {
                final DiskRangeList list = node;
                this.tail = list;
                this.head = list;
            }
            else {
                this.tail = this.tail.insertAfter(node);
            }
        }
        
        public DiskRangeList get() {
            return this.head;
        }
        
        public DiskRangeList extract() {
            final DiskRangeList result = this.head;
            this.head = null;
            return result;
        }
    }
    
    public static class DiskRangeListMutateHelper extends DiskRangeList
    {
        public DiskRangeListMutateHelper(final DiskRangeList head) {
            super(-1L, -1L);
            assert head != null;
            assert head.prev == null;
            this.next = head;
            head.prev = this;
        }
        
        public DiskRangeList get() {
            return this.next;
        }
        
        public DiskRangeList extract() {
            final DiskRangeList result = this.next;
            assert result != null;
            final DiskRangeList list = result;
            final DiskRangeList list2 = null;
            list.prev = list2;
            this.next = list2;
            return result;
        }
    }
}
