// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.services.io.Formatable;

public class AggregatorInfo implements Formatable
{
    String aggregateName;
    int inputColumn;
    int outputColumn;
    int aggregatorColumn;
    String aggregatorClassName;
    boolean isDistinct;
    ResultDescription rd;
    
    public AggregatorInfo() {
    }
    
    public AggregatorInfo(final String aggregateName, final String aggregatorClassName, final int inputColumn, final int outputColumn, final int aggregatorColumn, final boolean isDistinct, final ResultDescription rd) {
        this.aggregateName = aggregateName;
        this.aggregatorClassName = aggregatorClassName;
        this.inputColumn = inputColumn;
        this.outputColumn = outputColumn;
        this.aggregatorColumn = aggregatorColumn;
        this.isDistinct = isDistinct;
        this.rd = rd;
    }
    
    public String getAggregateName() {
        return this.aggregateName;
    }
    
    public String getAggregatorClassName() {
        return this.aggregatorClassName;
    }
    
    public int getAggregatorColNum() {
        return this.aggregatorColumn;
    }
    
    public int getInputColNum() {
        return this.inputColumn;
    }
    
    public int getOutputColNum() {
        return this.outputColumn;
    }
    
    public boolean isDistinct() {
        return this.isDistinct;
    }
    
    public ResultDescription getResultDescription() {
        return this.rd;
    }
    
    public String toString() {
        return "";
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.aggregateName);
        objectOutput.writeInt(this.inputColumn);
        objectOutput.writeInt(this.outputColumn);
        objectOutput.writeInt(this.aggregatorColumn);
        objectOutput.writeObject(this.aggregatorClassName);
        objectOutput.writeBoolean(this.isDistinct);
        objectOutput.writeObject(this.rd);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.aggregateName = (String)objectInput.readObject();
        this.inputColumn = objectInput.readInt();
        this.outputColumn = objectInput.readInt();
        this.aggregatorColumn = objectInput.readInt();
        this.aggregatorClassName = (String)objectInput.readObject();
        this.isDistinct = objectInput.readBoolean();
        this.rd = (ResultDescription)objectInput.readObject();
    }
    
    public int getTypeFormatId() {
        return 223;
    }
}
