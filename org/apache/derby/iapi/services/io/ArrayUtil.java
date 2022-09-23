// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public abstract class ArrayUtil
{
    public static void writeArrayLength(final ObjectOutput objectOutput, final Object[] array) throws IOException {
        objectOutput.writeInt(array.length);
    }
    
    public static void writeArrayItems(final ObjectOutput objectOutput, final Object[] array) throws IOException {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            objectOutput.writeObject(array[i]);
        }
    }
    
    public static void writeArray(final ObjectOutput objectOutput, final Object[] array) throws IOException {
        if (array == null) {
            objectOutput.writeInt(0);
            return;
        }
        objectOutput.writeInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            objectOutput.writeObject(array[i]);
        }
    }
    
    public static void readArrayItems(final ObjectInput objectInput, final Object[] array) throws IOException, ClassNotFoundException {
        for (int i = 0; i < array.length; ++i) {
            array[i] = objectInput.readObject();
        }
    }
    
    public static int readArrayLength(final ObjectInput objectInput) throws IOException {
        return objectInput.readInt();
    }
    
    public static Object[] readObjectArray(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final int int1 = objectInput.readInt();
        if (int1 == 0) {
            return null;
        }
        final Object[] array = new Object[int1];
        readArrayItems(objectInput, array);
        return array;
    }
    
    public static void writeIntArray(final ObjectOutput objectOutput, final int[] array) throws IOException {
        if (array == null) {
            objectOutput.writeInt(0);
        }
        else {
            objectOutput.writeInt(array.length);
            for (int i = 0; i < array.length; ++i) {
                objectOutput.writeInt(array[i]);
            }
        }
    }
    
    public static int[] readIntArray(final ObjectInput objectInput) throws IOException {
        final int int1 = objectInput.readInt();
        if (int1 == 0) {
            return null;
        }
        final int[] array = new int[int1];
        for (int i = 0; i < int1; ++i) {
            array[i] = objectInput.readInt();
        }
        return array;
    }
    
    public static void writeInts(final ObjectOutput objectOutput, final int[][] array) throws IOException {
        if (array == null) {
            objectOutput.writeBoolean(false);
        }
        else {
            objectOutput.writeBoolean(true);
            final int length = array.length;
            objectOutput.writeInt(length);
            for (int i = 0; i < length; ++i) {
                writeIntArray(objectOutput, array[i]);
            }
        }
    }
    
    public static int[][] readInts(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        int[][] array = null;
        if (objectInput.readBoolean()) {
            final int int1 = objectInput.readInt();
            array = new int[int1][];
            for (int i = 0; i < int1; ++i) {
                array[i] = readIntArray(objectInput);
            }
        }
        return array;
    }
    
    public static String toString(final int[] array) {
        if (array == null || array.length == 0) {
            return "null";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append("[").append(array[i]).append("],");
        }
        return sb.toString();
    }
    
    public static void writeLongArray(final ObjectOutput objectOutput, final long[] array) throws IOException {
        if (array == null) {
            objectOutput.writeInt(0);
        }
        else {
            objectOutput.writeInt(array.length);
            for (int i = 0; i < array.length; ++i) {
                objectOutput.writeLong(array[i]);
            }
        }
    }
    
    public static long[] readLongArray(final ObjectInput objectInput) throws IOException {
        final int int1 = objectInput.readInt();
        final long[] array = new long[int1];
        for (int i = 0; i < int1; ++i) {
            array[i] = objectInput.readLong();
        }
        return array;
    }
    
    public static String[] readStringArray(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        final Object[] objectArray = readObjectArray(objectInput);
        if (objectArray == null) {
            return null;
        }
        final int length;
        final String[] array = new String[length = objectArray.length];
        for (int i = 0; i < length; ++i) {
            array[i] = (String)objectArray[i];
        }
        return array;
    }
    
    public static void writeBooleanArray(final ObjectOutput objectOutput, final boolean[] array) throws IOException {
        if (array == null) {
            objectOutput.writeInt(0);
        }
        else {
            objectOutput.writeInt(array.length);
            for (int i = 0; i < array.length; ++i) {
                objectOutput.writeBoolean(array[i]);
            }
        }
    }
    
    public static boolean[] readBooleanArray(final ObjectInput objectInput) throws IOException {
        final int int1 = objectInput.readInt();
        final boolean[] array = new boolean[int1];
        for (int i = 0; i < int1; ++i) {
            array[i] = objectInput.readBoolean();
        }
        return array;
    }
}
