// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.cli.thrift.TBinaryColumn;
import org.apache.hive.service.cli.thrift.TStringColumn;
import org.apache.hive.service.cli.thrift.TDoubleColumn;
import org.apache.hive.service.cli.thrift.TI64Column;
import org.apache.hive.service.cli.thrift.TI32Column;
import org.apache.hive.service.cli.thrift.TI16Column;
import org.apache.hive.service.cli.thrift.TByteColumn;
import org.apache.hive.service.cli.thrift.TBoolColumn;
import java.util.Arrays;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.Bytes;
import java.util.Collection;
import com.google.common.primitives.Booleans;
import org.apache.hive.service.cli.thrift.TColumn;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.BitSet;
import java.util.AbstractList;

public class Column extends AbstractList
{
    private static final int DEFAULT_SIZE = 100;
    private final Type type;
    private BitSet nulls;
    private int size;
    private boolean[] boolVars;
    private byte[] byteVars;
    private short[] shortVars;
    private int[] intVars;
    private long[] longVars;
    private double[] doubleVars;
    private List<String> stringVars;
    private List<ByteBuffer> binaryVars;
    private static final byte[] MASKS;
    private static final ByteBuffer EMPTY_BINARY;
    private static final String EMPTY_STRING = "";
    
    public Column(final Type type, final BitSet nulls, final Object values) {
        this.type = type;
        this.nulls = nulls;
        if (type == Type.BOOLEAN_TYPE) {
            this.boolVars = (boolean[])values;
            this.size = this.boolVars.length;
        }
        else if (type == Type.TINYINT_TYPE) {
            this.byteVars = (byte[])values;
            this.size = this.byteVars.length;
        }
        else if (type == Type.SMALLINT_TYPE) {
            this.shortVars = (short[])values;
            this.size = this.shortVars.length;
        }
        else if (type == Type.INT_TYPE) {
            this.intVars = (int[])values;
            this.size = this.intVars.length;
        }
        else if (type == Type.BIGINT_TYPE) {
            this.longVars = (long[])values;
            this.size = this.longVars.length;
        }
        else if (type == Type.DOUBLE_TYPE) {
            this.doubleVars = (double[])values;
            this.size = this.doubleVars.length;
        }
        else if (type == Type.BINARY_TYPE) {
            this.binaryVars = (List<ByteBuffer>)values;
            this.size = this.binaryVars.size();
        }
        else {
            if (type != Type.STRING_TYPE) {
                throw new IllegalStateException("invalid union object");
            }
            this.stringVars = (List<String>)values;
            this.size = this.stringVars.size();
        }
    }
    
    public Column(Type type) {
        this.nulls = new BitSet();
        switch (type) {
            case BOOLEAN_TYPE: {
                this.boolVars = new boolean[100];
                break;
            }
            case TINYINT_TYPE: {
                this.byteVars = new byte[100];
                break;
            }
            case SMALLINT_TYPE: {
                this.shortVars = new short[100];
                break;
            }
            case INT_TYPE: {
                this.intVars = new int[100];
                break;
            }
            case BIGINT_TYPE: {
                this.longVars = new long[100];
                break;
            }
            case FLOAT_TYPE:
            case DOUBLE_TYPE: {
                type = Type.DOUBLE_TYPE;
                this.doubleVars = new double[100];
                break;
            }
            case BINARY_TYPE: {
                this.binaryVars = new ArrayList<ByteBuffer>();
                break;
            }
            default: {
                type = Type.STRING_TYPE;
                this.stringVars = new ArrayList<String>();
                break;
            }
        }
        this.type = type;
    }
    
    public Column(final TColumn colValues) {
        if (colValues.isSetBoolVal()) {
            this.type = Type.BOOLEAN_TYPE;
            this.nulls = toBitset(colValues.getBoolVal().getNulls());
            this.boolVars = Booleans.toArray(colValues.getBoolVal().getValues());
            this.size = this.boolVars.length;
        }
        else if (colValues.isSetByteVal()) {
            this.type = Type.TINYINT_TYPE;
            this.nulls = toBitset(colValues.getByteVal().getNulls());
            this.byteVars = Bytes.toArray(colValues.getByteVal().getValues());
            this.size = this.byteVars.length;
        }
        else if (colValues.isSetI16Val()) {
            this.type = Type.SMALLINT_TYPE;
            this.nulls = toBitset(colValues.getI16Val().getNulls());
            this.shortVars = Shorts.toArray(colValues.getI16Val().getValues());
            this.size = this.shortVars.length;
        }
        else if (colValues.isSetI32Val()) {
            this.type = Type.INT_TYPE;
            this.nulls = toBitset(colValues.getI32Val().getNulls());
            this.intVars = Ints.toArray(colValues.getI32Val().getValues());
            this.size = this.intVars.length;
        }
        else if (colValues.isSetI64Val()) {
            this.type = Type.BIGINT_TYPE;
            this.nulls = toBitset(colValues.getI64Val().getNulls());
            this.longVars = Longs.toArray(colValues.getI64Val().getValues());
            this.size = this.longVars.length;
        }
        else if (colValues.isSetDoubleVal()) {
            this.type = Type.DOUBLE_TYPE;
            this.nulls = toBitset(colValues.getDoubleVal().getNulls());
            this.doubleVars = Doubles.toArray(colValues.getDoubleVal().getValues());
            this.size = this.doubleVars.length;
        }
        else if (colValues.isSetBinaryVal()) {
            this.type = Type.BINARY_TYPE;
            this.nulls = toBitset(colValues.getBinaryVal().getNulls());
            this.binaryVars = colValues.getBinaryVal().getValues();
            this.size = this.binaryVars.size();
        }
        else {
            if (!colValues.isSetStringVal()) {
                throw new IllegalStateException("invalid union object");
            }
            this.type = Type.STRING_TYPE;
            this.nulls = toBitset(colValues.getStringVal().getNulls());
            this.stringVars = colValues.getStringVal().getValues();
            this.size = this.stringVars.size();
        }
    }
    
    public Column extractSubset(final int start, final int end) {
        final BitSet subNulls = this.nulls.get(start, end);
        if (this.type == Type.BOOLEAN_TYPE) {
            final Column subset = new Column(this.type, subNulls, Arrays.copyOfRange(this.boolVars, start, end));
            this.boolVars = Arrays.copyOfRange(this.boolVars, end, this.size);
            this.nulls = this.nulls.get(start, this.size);
            this.size = this.boolVars.length;
            return subset;
        }
        if (this.type == Type.TINYINT_TYPE) {
            final Column subset = new Column(this.type, subNulls, Arrays.copyOfRange(this.byteVars, start, end));
            this.byteVars = Arrays.copyOfRange(this.byteVars, end, this.size);
            this.nulls = this.nulls.get(start, this.size);
            this.size = this.byteVars.length;
            return subset;
        }
        if (this.type == Type.SMALLINT_TYPE) {
            final Column subset = new Column(this.type, subNulls, Arrays.copyOfRange(this.shortVars, start, end));
            this.shortVars = Arrays.copyOfRange(this.shortVars, end, this.size);
            this.nulls = this.nulls.get(start, this.size);
            this.size = this.shortVars.length;
            return subset;
        }
        if (this.type == Type.INT_TYPE) {
            final Column subset = new Column(this.type, subNulls, Arrays.copyOfRange(this.intVars, start, end));
            this.intVars = Arrays.copyOfRange(this.intVars, end, this.size);
            this.nulls = this.nulls.get(start, this.size);
            this.size = this.intVars.length;
            return subset;
        }
        if (this.type == Type.BIGINT_TYPE) {
            final Column subset = new Column(this.type, subNulls, Arrays.copyOfRange(this.longVars, start, end));
            this.longVars = Arrays.copyOfRange(this.longVars, end, this.size);
            this.nulls = this.nulls.get(start, this.size);
            this.size = this.longVars.length;
            return subset;
        }
        if (this.type == Type.DOUBLE_TYPE) {
            final Column subset = new Column(this.type, subNulls, Arrays.copyOfRange(this.doubleVars, start, end));
            this.doubleVars = Arrays.copyOfRange(this.doubleVars, end, this.size);
            this.nulls = this.nulls.get(start, this.size);
            this.size = this.doubleVars.length;
            return subset;
        }
        if (this.type == Type.BINARY_TYPE) {
            final Column subset = new Column(this.type, subNulls, this.binaryVars.subList(start, end));
            this.binaryVars = this.binaryVars.subList(end, this.binaryVars.size());
            this.nulls = this.nulls.get(start, this.size);
            this.size = this.binaryVars.size();
            return subset;
        }
        if (this.type == Type.STRING_TYPE) {
            final Column subset = new Column(this.type, subNulls, this.stringVars.subList(start, end));
            this.stringVars = this.stringVars.subList(end, this.stringVars.size());
            this.nulls = this.nulls.get(start, this.size);
            this.size = this.stringVars.size();
            return subset;
        }
        throw new IllegalStateException("invalid union object");
    }
    
    private static BitSet toBitset(final byte[] nulls) {
        final BitSet bitset = new BitSet();
        for (int bits = nulls.length * 8, i = 0; i < bits; ++i) {
            bitset.set(i, (nulls[i / 8] & Column.MASKS[i % 8]) != 0x0);
        }
        return bitset;
    }
    
    private static byte[] toBinary(final BitSet bitset) {
        final byte[] nulls = new byte[1 + bitset.length() / 8];
        for (int i = 0; i < bitset.length(); ++i) {
            final byte[] array = nulls;
            final int n = i / 8;
            array[n] |= (byte)(bitset.get(i) ? Column.MASKS[i % 8] : 0);
        }
        return nulls;
    }
    
    public Type getType() {
        return this.type;
    }
    
    @Override
    public Object get(final int index) {
        if (this.nulls.get(index)) {
            return null;
        }
        switch (this.type) {
            case BOOLEAN_TYPE: {
                return this.boolVars[index];
            }
            case TINYINT_TYPE: {
                return this.byteVars[index];
            }
            case SMALLINT_TYPE: {
                return this.shortVars[index];
            }
            case INT_TYPE: {
                return this.intVars[index];
            }
            case BIGINT_TYPE: {
                return this.longVars[index];
            }
            case DOUBLE_TYPE: {
                return this.doubleVars[index];
            }
            case STRING_TYPE: {
                return this.stringVars.get(index);
            }
            case BINARY_TYPE: {
                return this.binaryVars.get(index).array();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    public TColumn toTColumn() {
        final TColumn value = new TColumn();
        final ByteBuffer nullMasks = ByteBuffer.wrap(toBinary(this.nulls));
        switch (this.type) {
            case BOOLEAN_TYPE: {
                value.setBoolVal(new TBoolColumn(Booleans.asList(Arrays.copyOfRange(this.boolVars, 0, this.size)), nullMasks));
                break;
            }
            case TINYINT_TYPE: {
                value.setByteVal(new TByteColumn(Bytes.asList(Arrays.copyOfRange(this.byteVars, 0, this.size)), nullMasks));
                break;
            }
            case SMALLINT_TYPE: {
                value.setI16Val(new TI16Column(Shorts.asList(Arrays.copyOfRange(this.shortVars, 0, this.size)), nullMasks));
                break;
            }
            case INT_TYPE: {
                value.setI32Val(new TI32Column(Ints.asList(Arrays.copyOfRange(this.intVars, 0, this.size)), nullMasks));
                break;
            }
            case BIGINT_TYPE: {
                value.setI64Val(new TI64Column(Longs.asList(Arrays.copyOfRange(this.longVars, 0, this.size)), nullMasks));
                break;
            }
            case DOUBLE_TYPE: {
                value.setDoubleVal(new TDoubleColumn(Doubles.asList(Arrays.copyOfRange(this.doubleVars, 0, this.size)), nullMasks));
                break;
            }
            case STRING_TYPE: {
                value.setStringVal(new TStringColumn(this.stringVars, nullMasks));
                break;
            }
            case BINARY_TYPE: {
                value.setBinaryVal(new TBinaryColumn(this.binaryVars, nullMasks));
                break;
            }
        }
        return value;
    }
    
    public void addValue(final Type type, final Object field) {
        switch (type) {
            case BOOLEAN_TYPE: {
                this.nulls.set(this.size, field == null);
                this.boolVars()[this.size] = (field == null || (boolean)field);
                break;
            }
            case TINYINT_TYPE: {
                this.nulls.set(this.size, field == null);
                this.byteVars()[this.size] = (byte)((field == null) ? 0 : ((byte)field));
                break;
            }
            case SMALLINT_TYPE: {
                this.nulls.set(this.size, field == null);
                this.shortVars()[this.size] = (short)((field == null) ? 0 : ((short)field));
                break;
            }
            case INT_TYPE: {
                this.nulls.set(this.size, field == null);
                this.intVars()[this.size] = (int)((field == null) ? 0 : field);
                break;
            }
            case BIGINT_TYPE: {
                this.nulls.set(this.size, field == null);
                this.longVars()[this.size] = (long)((field == null) ? 0L : field);
                break;
            }
            case FLOAT_TYPE: {
                this.nulls.set(this.size, field == null);
                this.doubleVars()[this.size] = ((field == null) ? 0.0 : new Double(field.toString()));
                break;
            }
            case DOUBLE_TYPE: {
                this.nulls.set(this.size, field == null);
                this.doubleVars()[this.size] = (double)((field == null) ? 0.0 : field);
                break;
            }
            case BINARY_TYPE: {
                this.nulls.set(this.binaryVars.size(), field == null);
                this.binaryVars.add((field == null) ? Column.EMPTY_BINARY : ByteBuffer.wrap((byte[])field));
                break;
            }
            default: {
                this.nulls.set(this.stringVars.size(), field == null);
                this.stringVars.add((field == null) ? "" : String.valueOf(field));
                break;
            }
        }
        ++this.size;
    }
    
    private boolean[] boolVars() {
        if (this.boolVars.length == this.size) {
            final boolean[] newVars = new boolean[this.size << 1];
            System.arraycopy(this.boolVars, 0, newVars, 0, this.size);
            return this.boolVars = newVars;
        }
        return this.boolVars;
    }
    
    private byte[] byteVars() {
        if (this.byteVars.length == this.size) {
            final byte[] newVars = new byte[this.size << 1];
            System.arraycopy(this.byteVars, 0, newVars, 0, this.size);
            return this.byteVars = newVars;
        }
        return this.byteVars;
    }
    
    private short[] shortVars() {
        if (this.shortVars.length == this.size) {
            final short[] newVars = new short[this.size << 1];
            System.arraycopy(this.shortVars, 0, newVars, 0, this.size);
            return this.shortVars = newVars;
        }
        return this.shortVars;
    }
    
    private int[] intVars() {
        if (this.intVars.length == this.size) {
            final int[] newVars = new int[this.size << 1];
            System.arraycopy(this.intVars, 0, newVars, 0, this.size);
            return this.intVars = newVars;
        }
        return this.intVars;
    }
    
    private long[] longVars() {
        if (this.longVars.length == this.size) {
            final long[] newVars = new long[this.size << 1];
            System.arraycopy(this.longVars, 0, newVars, 0, this.size);
            return this.longVars = newVars;
        }
        return this.longVars;
    }
    
    private double[] doubleVars() {
        if (this.doubleVars.length == this.size) {
            final double[] newVars = new double[this.size << 1];
            System.arraycopy(this.doubleVars, 0, newVars, 0, this.size);
            return this.doubleVars = newVars;
        }
        return this.doubleVars;
    }
    
    static {
        MASKS = new byte[] { 1, 2, 4, 8, 16, 32, 64, -128 };
        EMPTY_BINARY = ByteBuffer.allocate(0);
    }
}
