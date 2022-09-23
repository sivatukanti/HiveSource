// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.Stack;
import java.io.InputStream;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;

class RopeByteString extends ByteString
{
    private static final int[] minLengthByDepth;
    private final int totalLength;
    private final ByteString left;
    private final ByteString right;
    private final int leftLength;
    private final int treeDepth;
    private int hash;
    
    private RopeByteString(final ByteString left, final ByteString right) {
        this.hash = 0;
        this.left = left;
        this.right = right;
        this.leftLength = left.size();
        this.totalLength = this.leftLength + right.size();
        this.treeDepth = Math.max(left.getTreeDepth(), right.getTreeDepth()) + 1;
    }
    
    static ByteString concatenate(final ByteString left, final ByteString right) {
        final RopeByteString leftRope = (left instanceof RopeByteString) ? ((RopeByteString)left) : null;
        ByteString result;
        if (right.size() == 0) {
            result = left;
        }
        else if (left.size() == 0) {
            result = right;
        }
        else {
            final int newLength = left.size() + right.size();
            if (newLength < 128) {
                result = concatenateBytes(left, right);
            }
            else if (leftRope != null && leftRope.right.size() + right.size() < 128) {
                final ByteString newRight = concatenateBytes(leftRope.right, right);
                result = new RopeByteString(leftRope.left, newRight);
            }
            else if (leftRope != null && leftRope.left.getTreeDepth() > leftRope.right.getTreeDepth() && leftRope.getTreeDepth() > right.getTreeDepth()) {
                final ByteString newRight = new RopeByteString(leftRope.right, right);
                result = new RopeByteString(leftRope.left, newRight);
            }
            else {
                final int newDepth = Math.max(left.getTreeDepth(), right.getTreeDepth()) + 1;
                if (newLength >= RopeByteString.minLengthByDepth[newDepth]) {
                    result = new RopeByteString(left, right);
                }
                else {
                    result = new Balancer().balance(left, right);
                }
            }
        }
        return result;
    }
    
    private static LiteralByteString concatenateBytes(final ByteString left, final ByteString right) {
        final int leftSize = left.size();
        final int rightSize = right.size();
        final byte[] bytes = new byte[leftSize + rightSize];
        left.copyTo(bytes, 0, 0, leftSize);
        right.copyTo(bytes, 0, leftSize, rightSize);
        return new LiteralByteString(bytes);
    }
    
    static RopeByteString newInstanceForTest(final ByteString left, final ByteString right) {
        return new RopeByteString(left, right);
    }
    
    @Override
    public byte byteAt(final int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("Index < 0: " + index);
        }
        if (index > this.totalLength) {
            throw new ArrayIndexOutOfBoundsException("Index > length: " + index + ", " + this.totalLength);
        }
        byte result;
        if (index < this.leftLength) {
            result = this.left.byteAt(index);
        }
        else {
            result = this.right.byteAt(index - this.leftLength);
        }
        return result;
    }
    
    @Override
    public int size() {
        return this.totalLength;
    }
    
    @Override
    protected int getTreeDepth() {
        return this.treeDepth;
    }
    
    @Override
    protected boolean isBalanced() {
        return this.totalLength >= RopeByteString.minLengthByDepth[this.treeDepth];
    }
    
    @Override
    public ByteString substring(final int beginIndex, final int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException("Beginning index: " + beginIndex + " < 0");
        }
        if (endIndex > this.totalLength) {
            throw new IndexOutOfBoundsException("End index: " + endIndex + " > " + this.totalLength);
        }
        final int substringLength = endIndex - beginIndex;
        if (substringLength < 0) {
            throw new IndexOutOfBoundsException("Beginning index larger than ending index: " + beginIndex + ", " + endIndex);
        }
        ByteString result;
        if (substringLength == 0) {
            result = ByteString.EMPTY;
        }
        else if (substringLength == this.totalLength) {
            result = this;
        }
        else if (endIndex <= this.leftLength) {
            result = this.left.substring(beginIndex, endIndex);
        }
        else if (beginIndex >= this.leftLength) {
            result = this.right.substring(beginIndex - this.leftLength, endIndex - this.leftLength);
        }
        else {
            final ByteString leftSub = this.left.substring(beginIndex);
            final ByteString rightSub = this.right.substring(0, endIndex - this.leftLength);
            result = new RopeByteString(leftSub, rightSub);
        }
        return result;
    }
    
    @Override
    protected void copyToInternal(final byte[] target, final int sourceOffset, final int targetOffset, final int numberToCopy) {
        if (sourceOffset + numberToCopy <= this.leftLength) {
            this.left.copyToInternal(target, sourceOffset, targetOffset, numberToCopy);
        }
        else if (sourceOffset >= this.leftLength) {
            this.right.copyToInternal(target, sourceOffset - this.leftLength, targetOffset, numberToCopy);
        }
        else {
            final int leftLength = this.leftLength - sourceOffset;
            this.left.copyToInternal(target, sourceOffset, targetOffset, leftLength);
            this.right.copyToInternal(target, 0, targetOffset + leftLength, numberToCopy - leftLength);
        }
    }
    
    @Override
    public void copyTo(final ByteBuffer target) {
        this.left.copyTo(target);
        this.right.copyTo(target);
    }
    
    @Override
    public ByteBuffer asReadOnlyByteBuffer() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(this.toByteArray());
        return byteBuffer.asReadOnlyBuffer();
    }
    
    @Override
    public List<ByteBuffer> asReadOnlyByteBufferList() {
        final List<ByteBuffer> result = new ArrayList<ByteBuffer>();
        final PieceIterator pieces = new PieceIterator((ByteString)this);
        while (pieces.hasNext()) {
            final LiteralByteString byteString = pieces.next();
            result.add(byteString.asReadOnlyByteBuffer());
        }
        return result;
    }
    
    @Override
    public void writeTo(final OutputStream outputStream) throws IOException {
        this.left.writeTo(outputStream);
        this.right.writeTo(outputStream);
    }
    
    @Override
    public String toString(final String charsetName) throws UnsupportedEncodingException {
        return new String(this.toByteArray(), charsetName);
    }
    
    @Override
    public boolean isValidUtf8() {
        final int leftPartial = this.left.partialIsValidUtf8(0, 0, this.leftLength);
        final int state = this.right.partialIsValidUtf8(leftPartial, 0, this.right.size());
        return state == 0;
    }
    
    @Override
    protected int partialIsValidUtf8(final int state, final int offset, final int length) {
        final int toIndex = offset + length;
        if (toIndex <= this.leftLength) {
            return this.left.partialIsValidUtf8(state, offset, length);
        }
        if (offset >= this.leftLength) {
            return this.right.partialIsValidUtf8(state, offset - this.leftLength, length);
        }
        final int leftLength = this.leftLength - offset;
        final int leftPartial = this.left.partialIsValidUtf8(state, offset, leftLength);
        return this.right.partialIsValidUtf8(leftPartial, 0, length - leftLength);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ByteString)) {
            return false;
        }
        final ByteString otherByteString = (ByteString)other;
        if (this.totalLength != otherByteString.size()) {
            return false;
        }
        if (this.totalLength == 0) {
            return true;
        }
        if (this.hash != 0) {
            final int cachedOtherHash = otherByteString.peekCachedHashCode();
            if (cachedOtherHash != 0 && this.hash != cachedOtherHash) {
                return false;
            }
        }
        return this.equalsFragments(otherByteString);
    }
    
    private boolean equalsFragments(final ByteString other) {
        int thisOffset = 0;
        final Iterator<LiteralByteString> thisIter = new PieceIterator((ByteString)this);
        LiteralByteString thisString = thisIter.next();
        int thatOffset = 0;
        final Iterator<LiteralByteString> thatIter = new PieceIterator(other);
        LiteralByteString thatString = thatIter.next();
        int pos = 0;
        while (true) {
            final int thisRemaining = thisString.size() - thisOffset;
            final int thatRemaining = thatString.size() - thatOffset;
            final int bytesToCompare = Math.min(thisRemaining, thatRemaining);
            final boolean stillEqual = (thisOffset == 0) ? thisString.equalsRange(thatString, thatOffset, bytesToCompare) : thatString.equalsRange(thisString, thisOffset, bytesToCompare);
            if (!stillEqual) {
                return false;
            }
            pos += bytesToCompare;
            if (pos >= this.totalLength) {
                if (pos == this.totalLength) {
                    return true;
                }
                throw new IllegalStateException();
            }
            else {
                if (bytesToCompare == thisRemaining) {
                    thisOffset = 0;
                    thisString = thisIter.next();
                }
                else {
                    thisOffset += bytesToCompare;
                }
                if (bytesToCompare == thatRemaining) {
                    thatOffset = 0;
                    thatString = thatIter.next();
                }
                else {
                    thatOffset += bytesToCompare;
                }
            }
        }
    }
    
    @Override
    public int hashCode() {
        int h = this.hash;
        if (h == 0) {
            h = this.totalLength;
            h = this.partialHash(h, 0, this.totalLength);
            if (h == 0) {
                h = 1;
            }
            this.hash = h;
        }
        return h;
    }
    
    @Override
    protected int peekCachedHashCode() {
        return this.hash;
    }
    
    @Override
    protected int partialHash(final int h, final int offset, final int length) {
        final int toIndex = offset + length;
        if (toIndex <= this.leftLength) {
            return this.left.partialHash(h, offset, length);
        }
        if (offset >= this.leftLength) {
            return this.right.partialHash(h, offset - this.leftLength, length);
        }
        final int leftLength = this.leftLength - offset;
        final int leftPartial = this.left.partialHash(h, offset, leftLength);
        return this.right.partialHash(leftPartial, 0, length - leftLength);
    }
    
    @Override
    public CodedInputStream newCodedInput() {
        return CodedInputStream.newInstance(new RopeInputStream());
    }
    
    @Override
    public InputStream newInput() {
        return new RopeInputStream();
    }
    
    @Override
    public ByteIterator iterator() {
        return new RopeByteIterator();
    }
    
    static {
        final List<Integer> numbers = new ArrayList<Integer>();
        int f1 = 1;
        int temp;
        for (int f2 = 1; f2 > 0; f2 = temp) {
            numbers.add(f2);
            temp = f1 + f2;
            f1 = f2;
        }
        numbers.add(Integer.MAX_VALUE);
        minLengthByDepth = new int[numbers.size()];
        for (int i = 0; i < RopeByteString.minLengthByDepth.length; ++i) {
            RopeByteString.minLengthByDepth[i] = numbers.get(i);
        }
    }
    
    private static class Balancer
    {
        private final Stack<ByteString> prefixesStack;
        
        private Balancer() {
            this.prefixesStack = new Stack<ByteString>();
        }
        
        private ByteString balance(final ByteString left, final ByteString right) {
            this.doBalance(left);
            this.doBalance(right);
            ByteString partialString = this.prefixesStack.pop();
            while (!this.prefixesStack.isEmpty()) {
                final ByteString newLeft = this.prefixesStack.pop();
                partialString = new RopeByteString(newLeft, partialString, null);
            }
            return partialString;
        }
        
        private void doBalance(final ByteString root) {
            if (root.isBalanced()) {
                this.insert(root);
            }
            else {
                if (!(root instanceof RopeByteString)) {
                    throw new IllegalArgumentException("Has a new type of ByteString been created? Found " + root.getClass());
                }
                final RopeByteString rbs = (RopeByteString)root;
                this.doBalance(rbs.left);
                this.doBalance(rbs.right);
            }
        }
        
        private void insert(final ByteString byteString) {
            int depthBin = this.getDepthBinForLength(byteString.size());
            int binEnd = RopeByteString.minLengthByDepth[depthBin + 1];
            if (this.prefixesStack.isEmpty() || this.prefixesStack.peek().size() >= binEnd) {
                this.prefixesStack.push(byteString);
            }
            else {
                final int binStart = RopeByteString.minLengthByDepth[depthBin];
                ByteString newTree = this.prefixesStack.pop();
                while (!this.prefixesStack.isEmpty() && this.prefixesStack.peek().size() < binStart) {
                    final ByteString left = this.prefixesStack.pop();
                    newTree = new RopeByteString(left, newTree, null);
                }
                newTree = new RopeByteString(newTree, byteString, null);
                while (!this.prefixesStack.isEmpty()) {
                    depthBin = this.getDepthBinForLength(newTree.size());
                    binEnd = RopeByteString.minLengthByDepth[depthBin + 1];
                    if (this.prefixesStack.peek().size() >= binEnd) {
                        break;
                    }
                    final ByteString left = this.prefixesStack.pop();
                    newTree = new RopeByteString(left, newTree, null);
                }
                this.prefixesStack.push(newTree);
            }
        }
        
        private int getDepthBinForLength(final int length) {
            int depth = Arrays.binarySearch(RopeByteString.minLengthByDepth, length);
            if (depth < 0) {
                final int insertionPoint = -(depth + 1);
                depth = insertionPoint - 1;
            }
            return depth;
        }
    }
    
    private static class PieceIterator implements Iterator<LiteralByteString>
    {
        private final Stack<RopeByteString> breadCrumbs;
        private LiteralByteString next;
        
        private PieceIterator(final ByteString root) {
            this.breadCrumbs = new Stack<RopeByteString>();
            this.next = this.getLeafByLeft(root);
        }
        
        private LiteralByteString getLeafByLeft(final ByteString root) {
            ByteString pos;
            RopeByteString rbs;
            for (pos = root; pos instanceof RopeByteString; pos = rbs.left) {
                rbs = (RopeByteString)pos;
                this.breadCrumbs.push(rbs);
            }
            return (LiteralByteString)pos;
        }
        
        private LiteralByteString getNextNonEmptyLeaf() {
            while (!this.breadCrumbs.isEmpty()) {
                final LiteralByteString result = this.getLeafByLeft(this.breadCrumbs.pop().right);
                if (!result.isEmpty()) {
                    return result;
                }
            }
            return null;
        }
        
        public boolean hasNext() {
            return this.next != null;
        }
        
        public LiteralByteString next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            final LiteralByteString result = this.next;
            this.next = this.getNextNonEmptyLeaf();
            return result;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private class RopeByteIterator implements ByteIterator
    {
        private final PieceIterator pieces;
        private ByteIterator bytes;
        int bytesRemaining;
        
        private RopeByteIterator() {
            this.pieces = new PieceIterator((ByteString)RopeByteString.this);
            this.bytes = this.pieces.next().iterator();
            this.bytesRemaining = RopeByteString.this.size();
        }
        
        public boolean hasNext() {
            return this.bytesRemaining > 0;
        }
        
        public Byte next() {
            return this.nextByte();
        }
        
        public byte nextByte() {
            if (!this.bytes.hasNext()) {
                this.bytes = this.pieces.next().iterator();
            }
            --this.bytesRemaining;
            return this.bytes.nextByte();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private class RopeInputStream extends InputStream
    {
        private PieceIterator pieceIterator;
        private LiteralByteString currentPiece;
        private int currentPieceSize;
        private int currentPieceIndex;
        private int currentPieceOffsetInRope;
        private int mark;
        
        public RopeInputStream() {
            this.initialize();
        }
        
        @Override
        public int read(final byte[] b, final int offset, final int length) {
            if (b == null) {
                throw new NullPointerException();
            }
            if (offset < 0 || length < 0 || length > b.length - offset) {
                throw new IndexOutOfBoundsException();
            }
            return this.readSkipInternal(b, offset, length);
        }
        
        @Override
        public long skip(long length) {
            if (length < 0L) {
                throw new IndexOutOfBoundsException();
            }
            if (length > 2147483647L) {
                length = 2147483647L;
            }
            return this.readSkipInternal(null, 0, (int)length);
        }
        
        private int readSkipInternal(final byte[] b, int offset, final int length) {
            int bytesRemaining = length;
            while (bytesRemaining > 0) {
                this.advanceIfCurrentPieceFullyRead();
                if (this.currentPiece == null) {
                    if (bytesRemaining == length) {
                        return -1;
                    }
                    break;
                }
                else {
                    final int currentPieceRemaining = this.currentPieceSize - this.currentPieceIndex;
                    final int count = Math.min(currentPieceRemaining, bytesRemaining);
                    if (b != null) {
                        this.currentPiece.copyTo(b, this.currentPieceIndex, offset, count);
                        offset += count;
                    }
                    this.currentPieceIndex += count;
                    bytesRemaining -= count;
                }
            }
            return length - bytesRemaining;
        }
        
        @Override
        public int read() throws IOException {
            this.advanceIfCurrentPieceFullyRead();
            if (this.currentPiece == null) {
                return -1;
            }
            return this.currentPiece.byteAt(this.currentPieceIndex++) & 0xFF;
        }
        
        @Override
        public int available() throws IOException {
            final int bytesRead = this.currentPieceOffsetInRope + this.currentPieceIndex;
            return RopeByteString.this.size() - bytesRead;
        }
        
        @Override
        public boolean markSupported() {
            return true;
        }
        
        @Override
        public void mark(final int readAheadLimit) {
            this.mark = this.currentPieceOffsetInRope + this.currentPieceIndex;
        }
        
        @Override
        public synchronized void reset() {
            this.initialize();
            this.readSkipInternal(null, 0, this.mark);
        }
        
        private void initialize() {
            this.pieceIterator = new PieceIterator((ByteString)RopeByteString.this);
            this.currentPiece = this.pieceIterator.next();
            this.currentPieceSize = this.currentPiece.size();
            this.currentPieceIndex = 0;
            this.currentPieceOffsetInRope = 0;
        }
        
        private void advanceIfCurrentPieceFullyRead() {
            if (this.currentPiece != null && this.currentPieceIndex == this.currentPieceSize) {
                this.currentPieceOffsetInRope += this.currentPieceSize;
                this.currentPieceIndex = 0;
                if (this.pieceIterator.hasNext()) {
                    this.currentPiece = this.pieceIterator.next();
                    this.currentPieceSize = this.currentPiece.size();
                }
                else {
                    this.currentPiece = null;
                    this.currentPieceSize = 0;
                }
            }
        }
    }
}
