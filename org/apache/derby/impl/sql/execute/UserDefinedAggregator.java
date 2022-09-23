// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.agg.Aggregator;
import org.apache.derby.iapi.sql.execute.ExecAggregator;

public final class UserDefinedAggregator implements ExecAggregator
{
    private static final int FIRST_VERSION = 0;
    private Aggregator _aggregator;
    private DataTypeDescriptor _resultType;
    private boolean _eliminatedNulls;
    
    public void setup(final ClassFactory classFactory, final String s, final DataTypeDescriptor dataTypeDescriptor) {
        try {
            this.setup(classFactory.loadApplicationClass(s), dataTypeDescriptor);
        }
        catch (ClassNotFoundException ex) {
            this.logAggregatorInstantiationError(s, ex);
        }
    }
    
    private void setup(final Class clazz, final DataTypeDescriptor resultType) {
        final String name = clazz.getName();
        try {
            (this._aggregator = clazz.newInstance()).init();
        }
        catch (InstantiationException ex) {
            this.logAggregatorInstantiationError(name, ex);
        }
        catch (IllegalAccessException ex2) {
            this.logAggregatorInstantiationError(name, ex2);
        }
        this._resultType = resultType;
    }
    
    public boolean didEliminateNulls() {
        return this._eliminatedNulls;
    }
    
    public void accumulate(final DataValueDescriptor dataValueDescriptor, final Object o) throws StandardException {
        if (dataValueDescriptor == null || dataValueDescriptor.isNull()) {
            this._eliminatedNulls = true;
            return;
        }
        this._aggregator.accumulate(dataValueDescriptor.getObject());
    }
    
    public void merge(final ExecAggregator execAggregator) throws StandardException {
        this._aggregator.merge(((UserDefinedAggregator)execAggregator)._aggregator);
    }
    
    public DataValueDescriptor getResult() throws StandardException {
        final Object terminate = this._aggregator.terminate();
        if (terminate == null) {
            return null;
        }
        final DataValueDescriptor null = this._resultType.getNull();
        null.setObjectForCast(terminate, true, terminate.getClass().getName());
        return null;
    }
    
    public ExecAggregator newAggregator() {
        final UserDefinedAggregator userDefinedAggregator = new UserDefinedAggregator();
        userDefinedAggregator.setup(this._aggregator.getClass(), this._resultType);
        return userDefinedAggregator;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(0);
        objectOutput.writeObject(this._aggregator);
        objectOutput.writeObject(this._resultType);
        objectOutput.writeBoolean(this._eliminatedNulls);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        objectInput.readInt();
        this._aggregator = (Aggregator)objectInput.readObject();
        this._resultType = (DataTypeDescriptor)objectInput.readObject();
        this._eliminatedNulls = objectInput.readBoolean();
    }
    
    public int getTypeFormatId() {
        return 323;
    }
    
    private void logAggregatorInstantiationError(final String s, final Throwable cause) {
        final String textMessage = MessageService.getTextMessage("C008", s, cause.getMessage());
        Monitor.getStream().println(textMessage);
        new Exception(textMessage, cause).printStackTrace(Monitor.getStream().getPrintWriter());
    }
}
