// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.reflect;

import com.sun.xml.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerDouble<BeanT> extends Lister<BeanT, double[], Double, DoubleArrayPack>
{
    private PrimitiveArrayListerDouble() {
    }
    
    static void register() {
        Lister.primitiveArrayListers.put(Double.TYPE, new PrimitiveArrayListerDouble());
    }
    
    @Override
    public ListIterator<Double> iterator(final double[] objects, final XMLSerializer context) {
        return new ListIterator<Double>() {
            int idx = 0;
            
            public boolean hasNext() {
                return this.idx < objects.length;
            }
            
            public Double next() {
                return objects[this.idx++];
            }
        };
    }
    
    @Override
    public DoubleArrayPack startPacking(final BeanT current, final Accessor<BeanT, double[]> acc) {
        return new DoubleArrayPack();
    }
    
    @Override
    public void addToPack(final DoubleArrayPack objects, final Double o) {
        objects.add(o);
    }
    
    @Override
    public void endPacking(final DoubleArrayPack pack, final BeanT bean, final Accessor<BeanT, double[]> acc) throws AccessorException {
        acc.set(bean, pack.build());
    }
    
    @Override
    public void reset(final BeanT o, final Accessor<BeanT, double[]> acc) throws AccessorException {
        acc.set(o, new double[0]);
    }
    
    static final class DoubleArrayPack
    {
        double[] buf;
        int size;
        
        DoubleArrayPack() {
            this.buf = new double[16];
        }
        
        void add(final Double b) {
            if (this.buf.length == this.size) {
                final double[] nb = new double[this.buf.length * 2];
                System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
                this.buf = nb;
            }
            if (b != null) {
                this.buf[this.size++] = b;
            }
        }
        
        double[] build() {
            if (this.buf.length == this.size) {
                return this.buf;
            }
            final double[] r = new double[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
        }
    }
}
