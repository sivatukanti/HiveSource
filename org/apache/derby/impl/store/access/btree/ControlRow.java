// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree;

import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.impl.store.access.StorableFormatId;
import org.apache.derby.iapi.services.io.TypedFormat;
import org.apache.derby.iapi.store.raw.AuxObject;

public abstract class ControlRow implements AuxObject, TypedFormat
{
    private StorableFormatId version;
    private SQLLongint leftSiblingPageNumber;
    private SQLLongint rightSiblingPageNumber;
    private SQLLongint parentPageNumber;
    private SQLLongint level;
    private SQLLongint isRoot;
    private BTree btree;
    protected Page page;
    protected DataValueDescriptor[] row;
    protected DataValueDescriptor[] scratch_row;
    protected FetchDescriptor fetchDesc;
    protected transient boolean use_last_search_result_hint;
    protected transient int last_search_result;
    protected static final int CR_COLID_FIRST = 0;
    protected static final int CR_VERSION_COLID = 0;
    protected static final int CR_LEFTSIB_COLID = 1;
    protected static final int CR_RIGHTSIB_COLID = 2;
    protected static final int CR_PARENT_COLID = 3;
    protected static final int CR_LEVEL_COLID = 4;
    protected static final int CR_ISROOT_COLID = 5;
    protected static final int CR_CONGLOM_COLID = 6;
    protected static final int CR_COLID_LAST = 6;
    protected static final int CR_NCOLUMNS = 7;
    protected static final FormatableBitSet CR_VERSION_BITSET;
    protected static final FormatableBitSet CR_LEFTSIB_BITSET;
    protected static final FormatableBitSet CR_RIGHTSIB_BITSET;
    protected static final FormatableBitSet CR_PARENT_BITSET;
    protected static final FormatableBitSet CR_LEVEL_BITSET;
    protected static final FormatableBitSet CR_ISROOT_BITSET;
    protected static final FormatableBitSet CR_CONGLOM_BITSET;
    public static final int SPLIT_FLAG_LAST_ON_PAGE = 1;
    public static final int SPLIT_FLAG_LAST_IN_TABLE = 2;
    public static final int SPLIT_FLAG_FIRST_ON_PAGE = 4;
    public static final int SPLIT_FLAG_FIRST_IN_TABLE = 8;
    protected static final int CR_SLOT = 0;
    
    protected ControlRow() {
        this.version = null;
        this.isRoot = null;
        this.btree = null;
        this.use_last_search_result_hint = false;
        this.last_search_result = 0;
        this.scratch_row = new DataValueDescriptor[this.getNumberOfControlRowColumns()];
        this.fetchDesc = new FetchDescriptor(this.scratch_row.length, null, null);
    }
    
    protected ControlRow(final OpenBTree openBTree, final Page page, final int n, final ControlRow controlRow, final boolean b) throws StandardException {
        this.version = null;
        this.isRoot = null;
        this.btree = null;
        this.use_last_search_result_hint = false;
        this.last_search_result = 0;
        this.page = page;
        this.leftSiblingPageNumber = new SQLLongint(-1L);
        this.rightSiblingPageNumber = new SQLLongint(-1L);
        this.parentPageNumber = new SQLLongint((controlRow == null) ? -1L : controlRow.page.getPageNumber());
        this.isRoot = new SQLLongint(b ? 1 : 0);
        this.level = new SQLLongint(n);
        this.version = new StorableFormatId(this.getTypeFormatId());
        this.btree = (BTree)(b ? openBTree.getConglomerate() : Monitor.newInstanceFromIdentifier(openBTree.getConglomerate().getTypeFormatId()));
        (this.row = new DataValueDescriptor[this.getNumberOfControlRowColumns()])[0] = this.version;
        this.row[1] = this.leftSiblingPageNumber;
        this.row[2] = this.rightSiblingPageNumber;
        this.row[3] = this.parentPageNumber;
        this.row[4] = this.level;
        this.row[5] = this.isRoot;
        this.row[6] = this.btree;
        page.setAuxObject(this);
    }
    
    protected ControlRow(final ContainerHandle containerHandle, final Page page) throws StandardException {
        this.version = null;
        this.isRoot = null;
        this.btree = null;
        this.use_last_search_result_hint = false;
        this.last_search_result = 0;
        System.out.println("ControlRow construct 2.");
        this.page = page;
    }
    
    protected int getVersion() throws StandardException {
        if (this.version == null) {
            this.version = new StorableFormatId();
            this.scratch_row[0] = this.version;
            this.fetchDesc.setValidColumns(ControlRow.CR_VERSION_BITSET);
            this.page.fetchFromSlot(null, 0, this.scratch_row, this.fetchDesc, false);
        }
        return this.version.getValue();
    }
    
    protected void setVersion(final int value) throws StandardException {
        if (this.version == null) {
            this.version = new StorableFormatId();
        }
        this.version.setValue(value);
        this.page.updateFieldAtSlot(0, 0, this.version, null);
    }
    
    public ControlRow getLeftSibling(final OpenBTree openBTree) throws StandardException, WaitError {
        final long getleftSiblingPageNumber = this.getleftSiblingPageNumber();
        if (getleftSiblingPageNumber == -1L) {
            return null;
        }
        final ControlRow noWait = getNoWait(openBTree, getleftSiblingPageNumber);
        if (noWait == null) {
            throw new WaitError();
        }
        return noWait;
    }
    
    protected void setLeftSibling(final ControlRow controlRow) throws StandardException {
        final long value = (controlRow == null) ? -1L : controlRow.page.getPageNumber();
        if (this.leftSiblingPageNumber == null) {
            this.leftSiblingPageNumber = new SQLLongint(value);
        }
        else {
            this.leftSiblingPageNumber.setValue(value);
        }
        try {
            this.page.updateFieldAtSlot(0, 1, this.leftSiblingPageNumber, null);
        }
        catch (StandardException ex) {
            throw ex;
        }
    }
    
    protected ControlRow getRightSibling(final OpenBTree openBTree) throws StandardException {
        final long getrightSiblingPageNumber = this.getrightSiblingPageNumber();
        if (getrightSiblingPageNumber == -1L) {
            return null;
        }
        return get(openBTree, getrightSiblingPageNumber);
    }
    
    protected void setRightSibling(final ControlRow controlRow) throws StandardException {
        final long value = (controlRow == null) ? -1L : controlRow.page.getPageNumber();
        if (this.rightSiblingPageNumber == null) {
            this.rightSiblingPageNumber = new SQLLongint(value);
        }
        else {
            this.rightSiblingPageNumber.setValue(value);
        }
        try {
            this.page.updateFieldAtSlot(0, 2, this.rightSiblingPageNumber, null);
        }
        catch (StandardException ex) {
            throw ex;
        }
    }
    
    public long getleftSiblingPageNumber() throws StandardException {
        if (this.leftSiblingPageNumber == null) {
            this.leftSiblingPageNumber = new SQLLongint();
            this.scratch_row[1] = this.leftSiblingPageNumber;
            this.fetchDesc.setValidColumns(ControlRow.CR_LEFTSIB_BITSET);
            this.page.fetchFromSlot(null, 0, this.scratch_row, this.fetchDesc, false);
        }
        return this.leftSiblingPageNumber.getLong();
    }
    
    protected long getrightSiblingPageNumber() throws StandardException {
        if (this.rightSiblingPageNumber == null) {
            this.rightSiblingPageNumber = new SQLLongint();
            this.scratch_row[2] = this.rightSiblingPageNumber;
            this.fetchDesc.setValidColumns(ControlRow.CR_RIGHTSIB_BITSET);
            this.page.fetchFromSlot(null, 0, this.scratch_row, this.fetchDesc, false);
        }
        return this.rightSiblingPageNumber.getLong();
    }
    
    protected long getParentPageNumber() throws StandardException {
        if (this.parentPageNumber == null) {
            this.parentPageNumber = new SQLLongint();
            this.scratch_row[3] = this.parentPageNumber;
            this.fetchDesc.setValidColumns(ControlRow.CR_PARENT_BITSET);
            this.page.fetchFromSlot(null, 0, this.scratch_row, this.fetchDesc, false);
        }
        return this.parentPageNumber.getLong();
    }
    
    void setParent(final long value) throws StandardException {
        if (this.parentPageNumber == null) {
            this.parentPageNumber = new SQLLongint();
        }
        this.parentPageNumber.setValue(value);
        try {
            this.page.updateFieldAtSlot(0, 3, this.parentPageNumber, null);
        }
        catch (StandardException ex) {
            throw ex;
        }
    }
    
    protected int getLevel() throws StandardException {
        if (this.level == null) {
            this.level = new SQLLongint();
            this.scratch_row[4] = this.level;
            this.fetchDesc.setValidColumns(ControlRow.CR_LEVEL_BITSET);
            this.page.fetchFromSlot(null, 0, this.scratch_row, this.fetchDesc, false);
        }
        return (int)this.level.getLong();
    }
    
    protected void setLevel(final int n) throws StandardException {
        if (this.level == null) {
            this.level = new SQLLongint();
        }
        this.level.setValue((long)n);
        this.page.updateFieldAtSlot(0, 4, this.level, null);
    }
    
    protected boolean getIsRoot() throws StandardException {
        if (this.isRoot == null) {
            this.isRoot = new SQLLongint();
            this.scratch_row[5] = this.isRoot;
            this.fetchDesc.setValidColumns(ControlRow.CR_ISROOT_BITSET);
            this.page.fetchFromSlot(null, 0, this.scratch_row, this.fetchDesc, false);
        }
        return this.isRoot.getLong() == 1L;
    }
    
    protected void setIsRoot(final boolean value) throws StandardException {
        if (this.isRoot == null) {
            this.isRoot = new SQLLongint();
        }
        this.isRoot.setValue(value ? 1 : 0);
        this.page.updateFieldAtSlot(0, 5, this.isRoot, null);
    }
    
    public BTree getConglom(final int n) throws StandardException {
        if (this.btree == null) {
            this.btree = (BTree)Monitor.newInstanceFromIdentifier(n);
            this.scratch_row[6] = this.btree;
            this.fetchDesc.setValidColumns(ControlRow.CR_CONGLOM_BITSET);
            this.page.fetchFromSlot(null, 0, this.scratch_row, this.fetchDesc, false);
        }
        return this.btree;
    }
    
    public static ControlRow get(final OpenBTree openBTree, final long n) throws StandardException {
        return get(openBTree.container, n);
    }
    
    public static ControlRow get(final ContainerHandle containerHandle, final long n) throws StandardException {
        return getControlRowForPage(containerHandle, containerHandle.getPage(n));
    }
    
    public static ControlRow getNoWait(final OpenBTree openBTree, final long n) throws StandardException {
        final Page userPageNoWait = openBTree.container.getUserPageNoWait(n);
        if (userPageNoWait == null) {
            return null;
        }
        return getControlRowForPage(openBTree.container, userPageNoWait);
    }
    
    protected static ControlRow getControlRowForPage(final ContainerHandle containerHandle, final Page page) throws StandardException {
        final AuxObject auxObject = page.getAuxObject();
        if (auxObject != null) {
            return (ControlRow)auxObject;
        }
        final StorableFormatId storableFormatId = new StorableFormatId();
        page.fetchFromSlot(null, 0, new DataValueDescriptor[] { storableFormatId }, new FetchDescriptor(1, ControlRow.CR_VERSION_BITSET, null), false);
        final ControlRow auxObject2 = (ControlRow)Monitor.newInstanceFromIdentifier(storableFormatId.getValue());
        auxObject2.page = page;
        auxObject2.controlRowInit();
        page.setAuxObject(auxObject2);
        return auxObject2;
    }
    
    public void release() {
        if (this.page != null) {
            this.page.unlatch();
        }
    }
    
    protected void searchForEntry(final SearchParameters searchParameters) throws StandardException {
        int n = 1;
        int n2 = this.page.recordCount() - 1;
        int i = 0;
        int n3 = n2 + 1;
        int n4;
        if (this.use_last_search_result_hint) {
            n4 = ((this.last_search_result == 0) ? 1 : this.last_search_result);
            if (n4 > n2) {
                n4 = n2;
            }
        }
        else {
            n4 = (n + n2) / 2;
        }
        while (i != n3 - 1) {
            final int compareIndexRowFromPageToKey = compareIndexRowFromPageToKey(this, n4, searchParameters.template, searchParameters.searchKey, searchParameters.btree.getConglomerate().nUniqueColumns, searchParameters.partial_key_match_op, searchParameters.btree.getConglomerate().ascDescInfo);
            if (compareIndexRowFromPageToKey == 0) {
                searchParameters.resultSlot = n4;
                searchParameters.resultExact = true;
                this.use_last_search_result_hint = (n4 == this.last_search_result);
                this.last_search_result = n4;
                return;
            }
            if (compareIndexRowFromPageToKey > 0) {
                n3 = n4;
                n2 = n4 - 1;
            }
            else {
                i = n4;
                n = n4 + 1;
            }
            n4 = (n + n2) / 2;
        }
        this.use_last_search_result_hint = (i == this.last_search_result);
        this.last_search_result = i;
        searchParameters.resultSlot = i;
        searchParameters.resultExact = false;
    }
    
    protected void searchForEntryBackward(final SearchParameters searchParameters) throws StandardException {
        int n = 1;
        int n2 = this.page.recordCount() - 1;
        int i = 0;
        int n3 = n2 + 1;
        int n4;
        if (this.use_last_search_result_hint) {
            n4 = ((this.last_search_result == 0) ? 1 : this.last_search_result);
            if (n4 > n2) {
                n4 = n2;
            }
        }
        else {
            n4 = (n + n2) / 2;
        }
        while (i != n3 - 1) {
            final int compareIndexRowFromPageToKey = compareIndexRowFromPageToKey(this, n4, searchParameters.template, searchParameters.searchKey, searchParameters.btree.getConglomerate().nUniqueColumns, searchParameters.partial_key_match_op, searchParameters.btree.getConglomerate().ascDescInfo);
            if (compareIndexRowFromPageToKey == 0) {
                searchParameters.resultSlot = n4;
                searchParameters.resultExact = true;
                this.use_last_search_result_hint = (n4 == this.last_search_result);
                this.last_search_result = n4;
                return;
            }
            if (compareIndexRowFromPageToKey > 0) {
                n3 = n4;
                n2 = n4 - 1;
            }
            else {
                i = n4;
                n = n4 + 1;
            }
            n4 = (n + n2) / 2;
        }
        this.use_last_search_result_hint = (i == this.last_search_result);
        this.last_search_result = i;
        searchParameters.resultSlot = i;
        searchParameters.resultExact = false;
    }
    
    public static int compareIndexRowFromPageToKey(final ControlRow controlRow, final int n, final DataValueDescriptor[] array, final DataValueDescriptor[] array2, final int n2, final int n3, final boolean[] array3) throws StandardException {
        final int length = array2.length;
        controlRow.page.fetchFromSlot(null, n, array, null, true);
        int i = 0;
        while (i < n2) {
            if (i >= length) {
                return n3;
            }
            final int compare = array[i].compare(array2[i]);
            if (compare != 0) {
                if (array3[i]) {
                    return compare;
                }
                return -compare;
            }
            else {
                ++i;
            }
        }
        return 0;
    }
    
    public static int compareIndexRowToKey(final DataValueDescriptor[] array, final DataValueDescriptor[] array2, final int n, final int n2, final boolean[] array3) throws StandardException {
        final int length = array2.length;
        int i = 0;
        while (i < n) {
            if (i >= length) {
                return n2;
            }
            final int compare = array[i].compare(array2[i]);
            if (compare != 0) {
                if (array3[i]) {
                    return compare;
                }
                return -compare;
            }
            else {
                ++i;
            }
        }
        return 0;
    }
    
    protected void checkGeneric(final OpenBTree openBTree, final ControlRow controlRow, final boolean b) throws StandardException {
    }
    
    protected boolean checkRowOrder(final OpenBTree openBTree, final ControlRow controlRow) throws StandardException {
        return true;
    }
    
    protected boolean compareRowsOnSiblings(final OpenBTree openBTree, final ControlRow controlRow, final ControlRow controlRow2) throws StandardException {
        return true;
    }
    
    protected void checkSiblings(final OpenBTree openBTree) throws StandardException {
    }
    
    void linkRight(final OpenBTree openBTree, final ControlRow leftSibling) throws StandardException {
        ControlRow rightSibling = null;
        try {
            rightSibling = leftSibling.getRightSibling(openBTree);
            this.setRightSibling(rightSibling);
            this.setLeftSibling(leftSibling);
            if (rightSibling != null) {
                rightSibling.setLeftSibling(this);
            }
            leftSibling.setRightSibling(this);
        }
        finally {
            if (rightSibling != null) {
                rightSibling.release();
            }
        }
    }
    
    boolean unlink(final OpenBTree openBTree) throws StandardException {
        ControlRow leftSibling = null;
        ControlRow rightSibling = null;
        try {
            try {
                leftSibling = this.getLeftSibling(openBTree);
            }
            catch (WaitError waitError) {
                return false;
            }
            rightSibling = this.getRightSibling(openBTree);
            if (leftSibling != null) {
                leftSibling.setRightSibling(rightSibling);
            }
            if (rightSibling != null) {
                rightSibling.setLeftSibling(leftSibling);
            }
            openBTree.container.removePage(this.page);
            return true;
        }
        finally {
            if (leftSibling != null) {
                leftSibling.release();
            }
            if (rightSibling != null) {
                rightSibling.release();
            }
        }
    }
    
    public Page getPage() {
        return this.page;
    }
    
    protected final DataValueDescriptor[] getRow() {
        return this.row;
    }
    
    protected abstract int checkConsistency(final OpenBTree p0, final ControlRow p1, final boolean p2) throws StandardException;
    
    protected abstract ControlRow getLeftChild(final OpenBTree p0) throws StandardException;
    
    protected abstract ControlRow getRightChild(final OpenBTree p0) throws StandardException;
    
    protected abstract void controlRowInit();
    
    public abstract boolean isLeftmostLeaf() throws StandardException;
    
    public abstract boolean isRightmostLeaf() throws StandardException;
    
    public abstract ControlRow search(final SearchParameters p0) throws StandardException;
    
    protected abstract int getNumberOfControlRowColumns();
    
    protected abstract ControlRow searchLeft(final OpenBTree p0) throws StandardException;
    
    protected abstract ControlRow searchRight(final OpenBTree p0) throws StandardException;
    
    protected abstract boolean shrinkFor(final OpenBTree p0, final DataValueDescriptor[] p1) throws StandardException;
    
    protected abstract long splitFor(final OpenBTree p0, final DataValueDescriptor[] p1, final BranchControlRow p2, final DataValueDescriptor[] p3, final int p4) throws StandardException;
    
    public abstract void printTree(final OpenBTree p0) throws StandardException;
    
    public void auxObjectInvalidated() {
        this.version = null;
        this.leftSiblingPageNumber = null;
        this.rightSiblingPageNumber = null;
        this.parentPageNumber = null;
        this.level = null;
        this.isRoot = null;
        this.page = null;
    }
    
    public DataValueDescriptor[] getRowTemplate(final OpenBTree openBTree) throws StandardException {
        return openBTree.getConglomerate().createTemplate(openBTree.getRawTran());
    }
    
    public String debugPage(final OpenBTree openBTree) throws StandardException {
        return null;
    }
    
    public String toString() {
        return null;
    }
    
    static {
        CR_VERSION_BITSET = new FormatableBitSet(1);
        CR_LEFTSIB_BITSET = new FormatableBitSet(2);
        CR_RIGHTSIB_BITSET = new FormatableBitSet(3);
        CR_PARENT_BITSET = new FormatableBitSet(4);
        CR_LEVEL_BITSET = new FormatableBitSet(5);
        CR_ISROOT_BITSET = new FormatableBitSet(6);
        CR_CONGLOM_BITSET = new FormatableBitSet(7);
        ControlRow.CR_VERSION_BITSET.set(0);
        ControlRow.CR_LEFTSIB_BITSET.set(1);
        ControlRow.CR_RIGHTSIB_BITSET.set(2);
        ControlRow.CR_PARENT_BITSET.set(3);
        ControlRow.CR_LEVEL_BITSET.set(4);
        ControlRow.CR_ISROOT_BITSET.set(5);
        ControlRow.CR_CONGLOM_BITSET.set(6);
    }
}
