// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.Statistics;
import java.sql.Timestamp;
import org.apache.derby.catalog.UUID;

public class StatisticsDescriptor extends TupleDescriptor
{
    private UUID statID;
    private UUID statRefID;
    private UUID statTableID;
    private Timestamp statUpdateTime;
    private String statType;
    private boolean statValid;
    private Statistics statStat;
    private int statColumnCount;
    
    public StatisticsDescriptor(final DataDictionary dataDictionary, final UUID statID, final UUID statRefID, final UUID statTableID, final String s, final Statistics statStat, final int statColumnCount) {
        super(dataDictionary);
        this.statValid = true;
        this.statID = statID;
        this.statRefID = statRefID;
        this.statTableID = statTableID;
        this.statUpdateTime = new Timestamp(System.currentTimeMillis());
        this.statType = "I";
        this.statStat = statStat;
        this.statColumnCount = statColumnCount;
    }
    
    public UUID getUUID() {
        return this.statID;
    }
    
    public UUID getTableUUID() {
        return this.statTableID;
    }
    
    public UUID getReferenceID() {
        return this.statRefID;
    }
    
    public Timestamp getUpdateTimestamp() {
        return this.statUpdateTime;
    }
    
    public String getStatType() {
        return this.statType;
    }
    
    public boolean isValid() {
        return this.statValid;
    }
    
    public Statistics getStatistic() {
        return this.statStat;
    }
    
    public int getColumnCount() {
        return this.statColumnCount;
    }
    
    public String toString() {
        return "statistics: table=" + this.getTableUUID().toString() + ",conglomerate=" + this.getReferenceID() + ",colCount=" + this.getColumnCount() + ",stat=" + this.getStatistic();
    }
}
