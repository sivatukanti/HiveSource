// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.compile.JoinStrategy;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public class Level2OptimizerImpl extends OptimizerImpl
{
    private LanguageConnectionContext lcc;
    
    Level2OptimizerImpl(final OptimizableList list, final OptimizablePredicateList list2, final DataDictionary dataDictionary, final boolean b, final boolean b2, final boolean b3, final int n, final JoinStrategy[] array, final int n2, final RequiredRowOrdering requiredRowOrdering, final int n3, final LanguageConnectionContext lcc) throws StandardException {
        super(list, list2, dataDictionary, b, b2, b3, n, array, n2, requiredRowOrdering, n3);
        this.optimizerTrace = lcc.getOptimizerTrace();
        this.optimizerTraceHtml = lcc.getOptimizerTraceHtml();
        this.lcc = lcc;
        if (this.optimizerTrace) {
            this.trace(1, 0, 0, 0.0, null);
        }
    }
    
    public int getLevel() {
        return 2;
    }
    
    public CostEstimate newCostEstimate() {
        return new Level2CostEstimateImpl();
    }
    
    public CostEstimateImpl getNewCostEstimate(final double n, final double n2, final double n3) {
        return new Level2CostEstimateImpl(n, n2, n3);
    }
    
    public void trace(final int n, final int i, final int n2, final double d, final Object o) {
        String s = null;
        if (!this.optimizerTrace) {
            return;
        }
        switch (n) {
            case 1: {
                s = "Optimization started at time " + this.timeOptimizationStarted + " using optimizer " + this.hashCode();
                break;
            }
            case 2: {
                s = "Optimization time exceeded at time " + this.currentTime + "\n" + this.bestCost();
                break;
            }
            case 3: {
                s = "No tables to optimize.";
                break;
            }
            case 4: {
                s = "We have a complete join order.";
                break;
            }
            case 5: {
                s = "Cost of sorting is " + this.sortCost;
                break;
            }
            case 6: {
                s = "No best plan found.";
                break;
            }
            case 7: {
                s = "Modifying access paths using optimizer " + this.hashCode();
                break;
            }
            case 8: {
                String str = this.timeExceeded ? "time exceeded" : "cost";
                if (this.optimizableList.getOptimizable(this.proposedJoinOrder[this.joinPosition]).getBestAccessPath().getCostEstimate() == null) {
                    str = "no best plan found";
                }
                s = "Short circuiting based on " + str + " at join position " + this.joinPosition;
                break;
            }
            case 9: {
                s = this.buildJoinOrder("\n\nSkipping join order: ", true, i, this.proposedJoinOrder);
                break;
            }
            case 10: {
                s = "User specified join order is not legal.";
                break;
            }
            case 11: {
                s = "User-specified join order has now been optimized.";
                break;
            }
            case 12: {
                s = this.buildJoinOrder("\n\nConsidering join order: ", false, i, this.proposedJoinOrder);
                break;
            }
            case 13: {
                s = "Total cost of non-sort-avoidance plan is " + this.currentCost;
                break;
            }
            case 14: {
                s = "Total cost of sort avoidance plan is " + this.currentSortAvoidanceCost;
                break;
            }
            case 15: {
                s = "Total cost of non-sort-avoidance plan with sort cost added is " + this.currentCost;
                break;
            }
            case 16: {
                s = "Current plan is a sort avoidance plan.\n\tBest cost is : " + this.bestCost + "\n\tThis cost is : " + this.currentSortAvoidanceCost;
                break;
            }
            case 17: {
                s = "This is the cheapest plan so far.";
                break;
            }
            case 18: {
                s = "Plan is a " + ((i == 1) ? "normal" : "sort avoidance") + " plan.";
                break;
            }
            case 19: {
                s = "Cost of cheapest plan is " + this.currentCost;
                break;
            }
            case 20: {
                s = "Sort needed for ordering: " + (i != 2) + "\n\tRow ordering: " + this.requiredRowOrdering;
                break;
            }
            case 21: {
                s = this.buildJoinOrder("\n\nRemembering join order as best: ", false, i, this.bestJoinOrder);
                break;
            }
            case 22: {
                s = "Skipping access path due to excess memory usage, maximum is " + this.maxMemoryPerTable;
                break;
            }
            case 23: {
                s = "Cost of " + d + " scans is: " + o + " for table " + i;
                break;
            }
            case 24: {
                s = "Skipping HASH JOIN because optimizable is not materializable";
                break;
            }
            case 25: {
                s = "Skipping HASH JOIN because there are no hash key columns";
                break;
            }
            case 26: {
                final int[] array = (int[])o;
                s = "# hash key columns = " + array.length;
                for (int j = 0; j < array.length; ++j) {
                    s = "\n" + s + "hashKeyColumns[" + j + "] = " + array[j];
                }
                break;
            }
            case 27: {
                s = "Calling optimizeIt() for join node";
                break;
            }
            case 28: {
                s = "\nConsidering join strategy " + o + " for table " + i;
                break;
            }
            case 29: {
                s = "Remembering access path " + o + " as truly the best for table " + i + " for plan type " + ((n2 == 1) ? " normal " : "sort avoidance") + "\n";
                break;
            }
            case 30: {
                s = "No more conglomerates to consider for table " + i;
                break;
            }
            case 31: {
                s = "\nConsidering conglomerate " + this.dumpConglomerateDescriptor((ConglomerateDescriptor)o) + " for table " + i;
                break;
            }
            case 32: {
                s = "Scanning heap, but we have a full match on a unique key.";
                break;
            }
            case 33: {
                s = "Adding unordered optimizable, # of predicates = " + i;
                break;
            }
            case 34: {
                s = "Changing access path for table " + i;
                break;
            }
            case 35: {
                s = "Lock mode set to MODE_TABLE because no start or stop position";
                break;
            }
            case 36: {
                s = "Index does not cover query - cost including base row fetch is: " + d + " for table " + i;
                break;
            }
            case 37: {
                s = "Lock mode set to MODE_RECORD because all start and stop positions are constant";
                break;
            }
            case 38: {
                s = "Estimating cost of conglomerate: " + this.costForTable(this.dumpConglomerateDescriptor((ConglomerateDescriptor)o), i);
                break;
            }
            case 39: {
                s = "Looking for user-specified index: " + o + " for table " + i;
                break;
            }
            case 40: {
                s = "Guaranteed to match a single row - cost is: " + d + " for table " + i;
                break;
            }
            case 41: {
                this.costIncluding("1st column", o, i);
                s = "Cost including extra first column selectivity is : " + o + " for table " + i;
                break;
            }
            case 42: {
                s = "Calling nextAccessPath() for base table " + o + " with " + i + " predicates.";
                break;
            }
            case 43: {
                s = this.lockModeThreshold("MODE_TABLE", "greater", d, i);
                break;
            }
            case 44: {
                s = this.lockModeThreshold("MODE_RECORD", "less", d, i);
                break;
            }
            case 45: {
                s = this.costIncluding("start/stop", o, i);
                break;
            }
            case 46: {
                s = this.costIncluding("qualifier", o, i);
                break;
            }
            case 47: {
                s = this.costIncluding("non-qualifier", o, i);
                break;
            }
            case 60: {
                s = this.costIncluding("selectivity from statistics", o, i);
                break;
            }
            case 62: {
                s = this.costIncluding("statistics for index being considered", o, i);
                break;
            }
            case 61: {
                s = "Selectivity from statistics found. It is " + d;
                break;
            }
            case 48: {
                s = "Index does not cover query: cost including row fetch is: " + this.costForTable(o, i);
                break;
            }
            case 49: {
                s = "\nRemembering join strategy " + o + " as best for table " + i;
                break;
            }
            case 50: {
                s = "in best access path";
                break;
            }
            case 51: {
                s = "in best sort avoidance access path";
                break;
            }
            case 52: {
                s = "in best unknown access path";
                break;
            }
            case 53: {
                s = "Cost of conglomerate " + this.dumpConglomerateDescriptor((ConglomerateDescriptor)o) + " scan for table number " + i + " is : ";
                break;
            }
            case 54: {
                s = o.toString();
                break;
            }
            case 55: {
                s = "\tNumber of extra first column predicates is : " + i + ", extra first column selectivity is : " + d;
                break;
            }
            case 56: {
                s = "\tNumber of extra start/stop predicates is : " + i + ", extra start/stop selectivity is : " + d;
                break;
            }
            case 57: {
                s = "\tNumber of extra qualifiers is : " + i + ", extra qualifier selectivity is : " + d;
                break;
            }
            case 58: {
                s = "\tNumber of extra non-qualifiers is : " + i + ", extra non-qualifier selectivity is : " + d;
                break;
            }
            case 59: {
                s = "\tNumber of start/stop statistics predicates is : " + i + ", statistics start/stop selectivity is : " + d;
                break;
            }
        }
        this.lcc.appendOptimizerTraceOutput(s + "\n");
    }
    
    private String costForTable(final Object obj, final int i) {
        return obj + " for table " + i;
    }
    
    private String bestCost() {
        return "Best cost = " + this.bestCost + "\n";
    }
    
    private String buildJoinOrder(final String str, final boolean b, final int i, final int[] array) {
        final StringBuffer sb = new StringBuffer();
        sb.append(str);
        for (int j = 0; j <= this.joinPosition; ++j) {
            sb.append(" ").append(array[j]);
        }
        if (b) {
            sb.append(" ").append(i);
        }
        sb.append(" with assignedTableMap = ").append(this.assignedTableMap).append("\n\n");
        return sb.toString();
    }
    
    private String lockModeThreshold(final String str, final String str2, final double d, final int i) {
        return "Lock mode set to " + str + " because estimated row count of " + d + " " + str2 + " than threshold of " + i;
    }
    
    private String costIncluding(final String str, final Object o, final int n) {
        return "Cost including extra " + str + " start/stop selectivity is : " + this.costForTable(o, n);
    }
    
    private String dumpConglomerateDescriptor(final ConglomerateDescriptor conglomerateDescriptor) {
        String string = "";
        final String[] columnNames = conglomerateDescriptor.getColumnNames();
        if (conglomerateDescriptor.isIndex() && columnNames != null) {
            final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
            String s = ", key columns = {" + columnNames[baseColumnPositions[0] - 1];
            for (int i = 1; i < baseColumnPositions.length; ++i) {
                s = s + ", " + columnNames[baseColumnPositions[i] - 1];
            }
            string = s + "}";
        }
        return "CD: conglomerateNumber = " + conglomerateDescriptor.getConglomerateNumber() + " name = " + conglomerateDescriptor.getConglomerateName() + " uuid = " + conglomerateDescriptor.getUUID() + " indexable = " + conglomerateDescriptor.isIndex() + string;
    }
}
