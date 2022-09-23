// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.io;

import java.io.OutputStream;
import com.google.common.collect.MinMaxPriorityQueue;
import java.util.Comparator;

public class SortPrintStream extends FetchConverter
{
    private static final Comparator<String> STR_COMP;
    protected final MinMaxPriorityQueue<String> outputs;
    
    public SortPrintStream(final OutputStream out, final String encoding) throws Exception {
        super(out, false, encoding);
        this.outputs = MinMaxPriorityQueue.orderedBy(SortPrintStream.STR_COMP).create();
    }
    
    public void process(final String out) {
        assert out != null;
        this.outputs.add(out);
    }
    
    public void processFinal() {
        while (!this.outputs.isEmpty()) {
            this.printDirect(this.outputs.removeFirst());
        }
    }
    
    static {
        STR_COMP = new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return o1.compareTo(o2);
            }
        };
    }
}
