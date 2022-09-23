// 
// Decompiled by Procyon v0.5.36
// 

package javax.activation;

import java.io.OutputStream;
import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.DataFlavor;

class ObjectDataContentHandler implements DataContentHandler
{
    private DataFlavor[] transferFlavors;
    private Object obj;
    private String mimeType;
    private DataContentHandler dch;
    
    public ObjectDataContentHandler(final DataContentHandler dch, final Object obj, final String mimeType) {
        this.transferFlavors = null;
        this.dch = null;
        this.obj = obj;
        this.mimeType = mimeType;
        this.dch = dch;
    }
    
    public DataContentHandler getDCH() {
        return this.dch;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        if (this.transferFlavors == null) {
            if (this.dch != null) {
                this.transferFlavors = this.dch.getTransferDataFlavors();
            }
            else {
                (this.transferFlavors = new DataFlavor[1])[0] = new ActivationDataFlavor(this.obj.getClass(), this.mimeType, this.mimeType);
            }
        }
        return this.transferFlavors;
    }
    
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws UnsupportedFlavorException, IOException {
        if (this.dch != null) {
            return this.dch.getTransferData(df, ds);
        }
        if (df.equals(this.transferFlavors[0])) {
            return this.obj;
        }
        throw new UnsupportedFlavorException(df);
    }
    
    public Object getContent(final DataSource ds) {
        return this.obj;
    }
    
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (this.dch != null) {
            this.dch.writeTo(obj, mimeType, os);
            return;
        }
        throw new UnsupportedDataTypeException("no object DCH for MIME type " + this.mimeType);
    }
}
