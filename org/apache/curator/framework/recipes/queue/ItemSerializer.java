// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Iterator;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;

class ItemSerializer
{
    private static final int VERSION = 65537;
    private static final byte ITEM_OPCODE = 1;
    private static final byte EOF_OPCODE = 2;
    private static final int INITIAL_BUFFER_SIZE = 4096;
    
    static <T> MultiItem<T> deserialize(final byte[] bytes, final QueueSerializer<T> serializer) throws Exception {
        final DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        final int version = in.readInt();
        if (version != 65537) {
            throw new IOException(String.format("Incorrect version. Expected %d - Found: %d", 65537, version));
        }
        final List<T> items = (List<T>)Lists.newArrayList();
        while (true) {
            final byte opcode = in.readByte();
            if (opcode == 2) {
                final Iterator<T> iterator = items.iterator();
                return new MultiItem<T>() {
                    @Override
                    public T nextItem() {
                        return iterator.hasNext() ? iterator.next() : null;
                    }
                };
            }
            if (opcode != 1) {
                throw new IOException(String.format("Incorrect opcode. Expected %d - Found: %d", 1, opcode));
            }
            final int size = in.readInt();
            if (size < 0) {
                throw new IOException(String.format("Bad size: %d", size));
            }
            final byte[] itemBytes = new byte[size];
            if (size > 0) {
                in.readFully(itemBytes);
            }
            items.add(serializer.deserialize(itemBytes));
        }
    }
    
    static <T> byte[] serialize(final MultiItem<T> items, final QueueSerializer<T> serializer) throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream(4096);
        final DataOutputStream out = new DataOutputStream(bytes);
        out.writeInt(65537);
        while (true) {
            final T item = items.nextItem();
            if (item == null) {
                break;
            }
            final byte[] itemBytes = serializer.serialize(item);
            out.writeByte(1);
            out.writeInt(itemBytes.length);
            if (itemBytes.length <= 0) {
                continue;
            }
            out.write(itemBytes);
        }
        out.writeByte(2);
        out.close();
        return bytes.toByteArray();
    }
    
    private ItemSerializer() {
    }
}
