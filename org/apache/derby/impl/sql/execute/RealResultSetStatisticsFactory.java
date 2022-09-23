// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.impl.sql.execute.rts.RealVTIStatistics;
import org.apache.derby.impl.sql.execute.rts.RealHashTableStatistics;
import org.apache.derby.impl.sql.execute.rts.RealHashScanStatistics;
import org.apache.derby.impl.sql.execute.rts.RealDistinctScanStatistics;
import org.apache.derby.impl.sql.execute.rts.RealCurrentOfStatistics;
import org.apache.derby.impl.sql.execute.rts.RealScrollInsensitiveResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealMaterializedResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealNormalizeResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealOnceResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealAnyResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealUnionResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealSetOpResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealWindowResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealRowResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealIndexRowToBaseRowStatistics;
import org.apache.derby.impl.sql.execute.rts.RealNestedLoopJoinStatistics;
import org.apache.derby.impl.sql.execute.rts.RealHashJoinStatistics;
import org.apache.derby.impl.sql.execute.rts.RealNestedLoopLeftOuterJoinStatistics;
import org.apache.derby.impl.sql.execute.rts.RealHashLeftOuterJoinStatistics;
import org.apache.derby.impl.sql.execute.rts.RealLastIndexKeyScanStatistics;
import org.apache.derby.impl.sql.execute.rts.RealTableScanStatistics;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.impl.sql.execute.rts.RealGroupedAggregateStatistics;
import org.apache.derby.impl.sql.execute.rts.RealScalarAggregateStatistics;
import org.apache.derby.impl.sql.execute.rts.RealDistinctScalarAggregateStatistics;
import org.apache.derby.impl.sql.execute.rts.RealSortStatistics;
import org.apache.derby.impl.sql.execute.rts.RealRowCountStatistics;
import org.apache.derby.impl.sql.execute.rts.RealProjectRestrictStatistics;
import org.apache.derby.impl.sql.execute.rts.RealDeleteVTIResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealDeleteResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealDeleteCascadeResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealUpdateResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealInsertVTIResultSetStatistics;
import org.apache.derby.impl.sql.execute.rts.RealInsertResultSetStatistics;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecPreparedStatement;
import org.apache.derby.impl.sql.execute.rts.RunTimeStatisticsImpl;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;
import org.apache.derby.iapi.sql.execute.RunTimeStatistics;
import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.sql.execute.ResultSetStatisticsFactory;

public class RealResultSetStatisticsFactory implements ResultSetStatisticsFactory
{
    public RunTimeStatistics getRunTimeStatistics(final Activation activation, final ResultSet set, final NoPutResultSet[] array) throws StandardException {
        final ExecPreparedStatement preparedStatement = activation.getPreparedStatement();
        if (preparedStatement == null) {
            return null;
        }
        ResultSetStatistics resultSetStatistics;
        if (set instanceof NoPutResultSet) {
            resultSetStatistics = this.getResultSetStatistics((NoPutResultSet)set);
        }
        else {
            resultSetStatistics = this.getResultSetStatistics(set);
        }
        final int n = (array == null) ? 0 : array.length;
        ResultSetStatistics[] array2 = new ResultSetStatistics[n];
        boolean b = false;
        for (int i = 0; i < n; ++i) {
            if (array[i] != null && array[i].getPointOfAttachment() == -1) {
                array2[i] = this.getResultSetStatistics(array[i]);
                b = true;
            }
        }
        if (!b) {
            array2 = null;
        }
        return new RunTimeStatisticsImpl(preparedStatement.getSPSName(), activation.getCursorName(), preparedStatement.getSource(), preparedStatement.getCompileTimeInMillis(), preparedStatement.getParseTimeInMillis(), preparedStatement.getBindTimeInMillis(), preparedStatement.getOptimizeTimeInMillis(), preparedStatement.getGenerateTimeInMillis(), set.getExecuteTime(), preparedStatement.getBeginCompileTimestamp(), preparedStatement.getEndCompileTimestamp(), set.getBeginExecutionTimestamp(), set.getEndExecutionTimestamp(), array2, resultSetStatistics);
    }
    
    public ResultSetStatistics getResultSetStatistics(final ResultSet set) {
        if (!set.returnsRows()) {
            return this.getNoRowsResultSetStatistics(set);
        }
        if (set instanceof NoPutResultSet) {
            return this.getResultSetStatistics((NoPutResultSet)set);
        }
        return null;
    }
    
    public ResultSetStatistics getNoRowsResultSetStatistics(final ResultSet set) {
        ResultSetStatistics resultSetStatistics = null;
        if (set instanceof InsertResultSet) {
            final InsertResultSet set2 = (InsertResultSet)set;
            resultSetStatistics = new RealInsertResultSetStatistics((int)set2.rowCount, set2.constants.deferred, set2.constants.irgs.length, set2.userSpecifiedBulkInsert, set2.bulkInsertPerformed, set2.constants.lockMode == 7, set2.getExecuteTime(), this.getResultSetStatistics(set2.savedSource));
            set2.savedSource = null;
        }
        else if (set instanceof InsertVTIResultSet) {
            final InsertVTIResultSet set3 = (InsertVTIResultSet)set;
            resultSetStatistics = new RealInsertVTIResultSetStatistics((int)set3.rowCount, set3.constants.deferred, set3.getExecuteTime(), this.getResultSetStatistics(set3.savedSource));
            set3.savedSource = null;
        }
        else if (set instanceof UpdateResultSet) {
            final UpdateResultSet set4 = (UpdateResultSet)set;
            resultSetStatistics = new RealUpdateResultSetStatistics((int)set4.rowCount, set4.constants.deferred, set4.constants.irgs.length, set4.constants.lockMode == 7, set4.getExecuteTime(), this.getResultSetStatistics(set4.savedSource));
            set4.savedSource = null;
        }
        else if (set instanceof DeleteCascadeResultSet) {
            final DeleteCascadeResultSet set5 = (DeleteCascadeResultSet)set;
            final int n = (set5.dependentResultSets == null) ? 0 : set5.dependentResultSets.length;
            ResultSetStatistics[] array = new ResultSetStatistics[n];
            boolean b = false;
            for (int i = 0; i < n; ++i) {
                if (set5.dependentResultSets[i] != null) {
                    array[i] = this.getResultSetStatistics(set5.dependentResultSets[i]);
                    b = true;
                }
            }
            if (!b) {
                array = null;
            }
            resultSetStatistics = new RealDeleteCascadeResultSetStatistics((int)set5.rowCount, set5.constants.deferred, set5.constants.irgs.length, set5.constants.lockMode == 7, set5.getExecuteTime(), this.getResultSetStatistics(set5.savedSource), array);
            set5.savedSource = null;
        }
        else if (set instanceof DeleteResultSet) {
            final DeleteResultSet set6 = (DeleteResultSet)set;
            resultSetStatistics = new RealDeleteResultSetStatistics((int)set6.rowCount, set6.constants.deferred, set6.constants.irgs.length, set6.constants.lockMode == 7, set6.getExecuteTime(), this.getResultSetStatistics(set6.savedSource));
            set6.savedSource = null;
        }
        else if (set instanceof DeleteVTIResultSet) {
            final DeleteVTIResultSet set7 = (DeleteVTIResultSet)set;
            resultSetStatistics = new RealDeleteVTIResultSetStatistics((int)set7.rowCount, set7.getExecuteTime(), this.getResultSetStatistics(set7.savedSource));
            set7.savedSource = null;
        }
        return resultSetStatistics;
    }
    
    public ResultSetStatistics getResultSetStatistics(final NoPutResultSet set) {
        if (set instanceof ProjectRestrictResultSet) {
            final ProjectRestrictResultSet set2 = (ProjectRestrictResultSet)set;
            final int n = (set2.subqueryTrackingArray == null) ? 0 : set2.subqueryTrackingArray.length;
            ResultSetStatistics[] array = new ResultSetStatistics[n];
            boolean b = false;
            for (int i = 0; i < n; ++i) {
                if (set2.subqueryTrackingArray[i] != null && set2.subqueryTrackingArray[i].getPointOfAttachment() == set2.resultSetNumber) {
                    array[i] = this.getResultSetStatistics(set2.subqueryTrackingArray[i]);
                    b = true;
                }
            }
            if (!b) {
                array = null;
            }
            return new RealProjectRestrictStatistics(set2.numOpens, set2.rowsSeen, set2.rowsFiltered, set2.constructorTime, set2.openTime, set2.nextTime, set2.closeTime, set2.resultSetNumber, set2.restrictionTime, set2.projectionTime, array, set2.restriction != null, set2.doesProjection, set2.optimizerEstimatedRowCount, set2.optimizerEstimatedCost, this.getResultSetStatistics(set2.source));
        }
        if (set instanceof RowCountResultSet) {
            final RowCountResultSet set3 = (RowCountResultSet)set;
            return new RealRowCountStatistics(set3.numOpens, set3.rowsSeen, set3.rowsFiltered, set3.constructorTime, set3.openTime, set3.nextTime, set3.closeTime, set3.resultSetNumber, set3.optimizerEstimatedRowCount, set3.optimizerEstimatedCost, this.getResultSetStatistics(set3.source));
        }
        if (set instanceof SortResultSet) {
            final SortResultSet set4 = (SortResultSet)set;
            return new RealSortStatistics(set4.numOpens, set4.rowsSeen, set4.rowsFiltered, set4.constructorTime, set4.openTime, set4.nextTime, set4.closeTime, set4.resultSetNumber, set4.rowsInput, set4.rowsReturned, set4.distinct, set4.isInSortedOrder, set4.sortProperties, set4.optimizerEstimatedRowCount, set4.optimizerEstimatedCost, this.getResultSetStatistics(set4.source));
        }
        if (set instanceof DistinctScalarAggregateResultSet) {
            final DistinctScalarAggregateResultSet set5 = (DistinctScalarAggregateResultSet)set;
            return new RealDistinctScalarAggregateStatistics(set5.numOpens, set5.rowsSeen, set5.rowsFiltered, set5.constructorTime, set5.openTime, set5.nextTime, set5.closeTime, set5.resultSetNumber, set5.rowsInput, set5.optimizerEstimatedRowCount, set5.optimizerEstimatedCost, this.getResultSetStatistics(set5.source));
        }
        if (set instanceof ScalarAggregateResultSet) {
            final ScalarAggregateResultSet set6 = (ScalarAggregateResultSet)set;
            return new RealScalarAggregateStatistics(set6.numOpens, set6.rowsSeen, set6.rowsFiltered, set6.constructorTime, set6.openTime, set6.nextTime, set6.closeTime, set6.resultSetNumber, set6.singleInputRow, set6.rowsInput, set6.optimizerEstimatedRowCount, set6.optimizerEstimatedCost, this.getResultSetStatistics(set6.source));
        }
        if (set instanceof GroupedAggregateResultSet) {
            final GroupedAggregateResultSet set7 = (GroupedAggregateResultSet)set;
            return new RealGroupedAggregateStatistics(set7.numOpens, set7.rowsSeen, set7.rowsFiltered, set7.constructorTime, set7.openTime, set7.nextTime, set7.closeTime, set7.resultSetNumber, set7.rowsInput, set7.hasDistinctAggregate, set7.isInSortedOrder, set7.sortProperties, set7.optimizerEstimatedRowCount, set7.optimizerEstimatedCost, this.getResultSetStatistics(set7.source));
        }
        if (set instanceof TableScanResultSet) {
            boolean b2 = false;
            final TableScanResultSet set8 = (TableScanResultSet)set;
            String s = null;
            String s2 = null;
            String s3 = null;
            String s4 = null;
            switch (set8.isolationLevel) {
                case 5: {
                    s3 = MessageService.getTextMessage("42Z80.U");
                    break;
                }
                case 4: {
                    s3 = MessageService.getTextMessage("42Z92");
                    break;
                }
                case 3: {
                    b2 = true;
                }
                case 2: {
                    s3 = MessageService.getTextMessage("42Z81.U");
                    break;
                }
                case 1: {
                    s3 = MessageService.getTextMessage("42Z9A");
                    break;
                }
            }
            String s5;
            if (set8.forUpdate) {
                s5 = MessageService.getTextMessage("42Z82.U");
            }
            else if (b2) {
                s5 = MessageService.getTextMessage("42Z83.U");
            }
            else {
                s5 = MessageService.getTextMessage("42Z84.U");
            }
            switch (set8.lockMode) {
                case 7: {
                    s4 = s5 + " " + MessageService.getTextMessage("42Z85.U");
                    break;
                }
                case 6: {
                    s4 = s5 + " " + MessageService.getTextMessage("42Z86.U");
                    break;
                }
            }
            if (set8.indexName != null) {
                s = set8.startPositionString;
                if (s == null) {
                    s = set8.printStartPosition();
                }
                s2 = set8.stopPositionString;
                if (s2 == null) {
                    s2 = set8.printStopPosition();
                }
            }
            return new RealTableScanStatistics(set8.numOpens, set8.rowsSeen, set8.rowsFiltered, set8.constructorTime, set8.openTime, set8.nextTime, set8.closeTime, set8.resultSetNumber, set8.tableName, set8.userSuppliedOptimizerOverrides, set8.indexName, set8.isConstraint, NoPutResultSetImpl.printQualifiers(set8.qualifiers), set8.getScanProperties(), s, s2, s3, s4, set8.rowsPerRead, set8.coarserLock, set8.optimizerEstimatedRowCount, set8.optimizerEstimatedCost);
        }
        if (set instanceof LastIndexKeyResultSet) {
            final LastIndexKeyResultSet set9 = (LastIndexKeyResultSet)set;
            String s6 = null;
            String s7 = null;
            switch (set9.isolationLevel) {
                case 5: {
                    s6 = MessageService.getTextMessage("42Z80.U");
                    break;
                }
                case 4: {
                    s6 = MessageService.getTextMessage("42Z92");
                    break;
                }
                case 2:
                case 3: {
                    s6 = MessageService.getTextMessage("42Z81.U");
                    break;
                }
                case 1: {
                    s6 = MessageService.getTextMessage("42Z9A");
                    break;
                }
            }
            switch (set9.lockMode) {
                case 7: {
                    s7 = MessageService.getTextMessage("42Z87.U");
                    break;
                }
                case 6: {
                    s7 = MessageService.getTextMessage("42Z88.U");
                    break;
                }
            }
            return new RealLastIndexKeyScanStatistics(set9.numOpens, set9.constructorTime, set9.openTime, set9.nextTime, set9.closeTime, set9.resultSetNumber, set9.tableName, set9.indexName, s6, s7, set9.optimizerEstimatedRowCount, set9.optimizerEstimatedCost);
        }
        if (set instanceof HashLeftOuterJoinResultSet) {
            final HashLeftOuterJoinResultSet set10 = (HashLeftOuterJoinResultSet)set;
            return new RealHashLeftOuterJoinStatistics(set10.numOpens, set10.rowsSeen, set10.rowsFiltered, set10.constructorTime, set10.openTime, set10.nextTime, set10.closeTime, set10.resultSetNumber, set10.rowsSeenLeft, set10.rowsSeenRight, set10.rowsReturned, set10.restrictionTime, set10.optimizerEstimatedRowCount, set10.optimizerEstimatedCost, set10.userSuppliedOptimizerOverrides, this.getResultSetStatistics(set10.leftResultSet), this.getResultSetStatistics(set10.rightResultSet), set10.emptyRightRowsReturned);
        }
        if (set instanceof NestedLoopLeftOuterJoinResultSet) {
            final NestedLoopLeftOuterJoinResultSet set11 = (NestedLoopLeftOuterJoinResultSet)set;
            return new RealNestedLoopLeftOuterJoinStatistics(set11.numOpens, set11.rowsSeen, set11.rowsFiltered, set11.constructorTime, set11.openTime, set11.nextTime, set11.closeTime, set11.resultSetNumber, set11.rowsSeenLeft, set11.rowsSeenRight, set11.rowsReturned, set11.restrictionTime, set11.optimizerEstimatedRowCount, set11.optimizerEstimatedCost, set11.userSuppliedOptimizerOverrides, this.getResultSetStatistics(set11.leftResultSet), this.getResultSetStatistics(set11.rightResultSet), set11.emptyRightRowsReturned);
        }
        if (set instanceof HashJoinResultSet) {
            final HashJoinResultSet set12 = (HashJoinResultSet)set;
            return new RealHashJoinStatistics(set12.numOpens, set12.rowsSeen, set12.rowsFiltered, set12.constructorTime, set12.openTime, set12.nextTime, set12.closeTime, set12.resultSetNumber, set12.rowsSeenLeft, set12.rowsSeenRight, set12.rowsReturned, set12.restrictionTime, set12.oneRowRightSide, set12.optimizerEstimatedRowCount, set12.optimizerEstimatedCost, set12.userSuppliedOptimizerOverrides, this.getResultSetStatistics(set12.leftResultSet), this.getResultSetStatistics(set12.rightResultSet));
        }
        if (set instanceof NestedLoopJoinResultSet) {
            final NestedLoopJoinResultSet set13 = (NestedLoopJoinResultSet)set;
            return new RealNestedLoopJoinStatistics(set13.numOpens, set13.rowsSeen, set13.rowsFiltered, set13.constructorTime, set13.openTime, set13.nextTime, set13.closeTime, set13.resultSetNumber, set13.rowsSeenLeft, set13.rowsSeenRight, set13.rowsReturned, set13.restrictionTime, set13.oneRowRightSide, set13.optimizerEstimatedRowCount, set13.optimizerEstimatedCost, set13.userSuppliedOptimizerOverrides, this.getResultSetStatistics(set13.leftResultSet), this.getResultSetStatistics(set13.rightResultSet));
        }
        if (set instanceof IndexRowToBaseRowResultSet) {
            final IndexRowToBaseRowResultSet set14 = (IndexRowToBaseRowResultSet)set;
            return new RealIndexRowToBaseRowStatistics(set14.numOpens, set14.rowsSeen, set14.rowsFiltered, set14.constructorTime, set14.openTime, set14.nextTime, set14.closeTime, set14.resultSetNumber, set14.indexName, set14.accessedHeapCols, set14.optimizerEstimatedRowCount, set14.optimizerEstimatedCost, this.getResultSetStatistics(set14.source));
        }
        if (set instanceof RowResultSet) {
            final RowResultSet set15 = (RowResultSet)set;
            return new RealRowResultSetStatistics(set15.numOpens, set15.rowsSeen, set15.rowsFiltered, set15.constructorTime, set15.openTime, set15.nextTime, set15.closeTime, set15.resultSetNumber, set15.rowsReturned, set15.optimizerEstimatedRowCount, set15.optimizerEstimatedCost);
        }
        if (set instanceof WindowResultSet) {
            final WindowResultSet set16 = (WindowResultSet)set;
            return new RealWindowResultSetStatistics(set16.numOpens, set16.rowsSeen, set16.rowsFiltered, set16.constructorTime, set16.openTime, set16.nextTime, set16.closeTime, set16.resultSetNumber, set16.optimizerEstimatedRowCount, set16.optimizerEstimatedCost, this.getResultSetStatistics(set16.source));
        }
        if (set instanceof SetOpResultSet) {
            final SetOpResultSet set17 = (SetOpResultSet)set;
            return new RealSetOpResultSetStatistics(set17.getOpType(), set17.numOpens, set17.rowsSeen, set17.rowsFiltered, set17.constructorTime, set17.openTime, set17.nextTime, set17.closeTime, set17.getResultSetNumber(), set17.getRowsSeenLeft(), set17.getRowsSeenRight(), set17.getRowsReturned(), set17.optimizerEstimatedRowCount, set17.optimizerEstimatedCost, this.getResultSetStatistics(set17.getLeftSourceInput()), this.getResultSetStatistics(set17.getRightSourceInput()));
        }
        if (set instanceof UnionResultSet) {
            final UnionResultSet set18 = (UnionResultSet)set;
            return new RealUnionResultSetStatistics(set18.numOpens, set18.rowsSeen, set18.rowsFiltered, set18.constructorTime, set18.openTime, set18.nextTime, set18.closeTime, set18.resultSetNumber, set18.rowsSeenLeft, set18.rowsSeenRight, set18.rowsReturned, set18.optimizerEstimatedRowCount, set18.optimizerEstimatedCost, this.getResultSetStatistics(set18.source1), this.getResultSetStatistics(set18.source2));
        }
        if (set instanceof AnyResultSet) {
            final AnyResultSet set19 = (AnyResultSet)set;
            return new RealAnyResultSetStatistics(set19.numOpens, set19.rowsSeen, set19.rowsFiltered, set19.constructorTime, set19.openTime, set19.nextTime, set19.closeTime, set19.resultSetNumber, set19.subqueryNumber, set19.pointOfAttachment, set19.optimizerEstimatedRowCount, set19.optimizerEstimatedCost, this.getResultSetStatistics(set19.source));
        }
        if (set instanceof OnceResultSet) {
            final OnceResultSet set20 = (OnceResultSet)set;
            return new RealOnceResultSetStatistics(set20.numOpens, set20.rowsSeen, set20.rowsFiltered, set20.constructorTime, set20.openTime, set20.nextTime, set20.closeTime, set20.resultSetNumber, set20.subqueryNumber, set20.pointOfAttachment, set20.optimizerEstimatedRowCount, set20.optimizerEstimatedCost, this.getResultSetStatistics(set20.source));
        }
        if (set instanceof NormalizeResultSet) {
            final NormalizeResultSet set21 = (NormalizeResultSet)set;
            return new RealNormalizeResultSetStatistics(set21.numOpens, set21.rowsSeen, set21.rowsFiltered, set21.constructorTime, set21.openTime, set21.nextTime, set21.closeTime, set21.resultSetNumber, set21.optimizerEstimatedRowCount, set21.optimizerEstimatedCost, this.getResultSetStatistics(set21.source));
        }
        if (set instanceof MaterializedResultSet) {
            final MaterializedResultSet set22 = (MaterializedResultSet)set;
            return new RealMaterializedResultSetStatistics(set22.numOpens, set22.rowsSeen, set22.rowsFiltered, set22.constructorTime, set22.openTime, set22.nextTime, set22.closeTime, set22.createTCTime, set22.fetchTCTime, set22.resultSetNumber, set22.optimizerEstimatedRowCount, set22.optimizerEstimatedCost, this.getResultSetStatistics(set22.source));
        }
        if (set instanceof ScrollInsensitiveResultSet) {
            final ScrollInsensitiveResultSet set23 = (ScrollInsensitiveResultSet)set;
            return new RealScrollInsensitiveResultSetStatistics(set23.numOpens, set23.rowsSeen, set23.rowsFiltered, set23.constructorTime, set23.openTime, set23.nextTime, set23.closeTime, set23.numFromHashTable, set23.numToHashTable, set23.resultSetNumber, set23.optimizerEstimatedRowCount, set23.optimizerEstimatedCost, this.getResultSetStatistics(set23.source));
        }
        if (set instanceof CurrentOfResultSet) {
            final CurrentOfResultSet set24 = (CurrentOfResultSet)set;
            return new RealCurrentOfStatistics(set24.numOpens, set24.rowsSeen, set24.rowsFiltered, set24.constructorTime, set24.openTime, set24.nextTime, set24.closeTime, set24.resultSetNumber);
        }
        if (set instanceof HashScanResultSet) {
            boolean b3 = false;
            final HashScanResultSet set25 = (HashScanResultSet)set;
            String s8 = null;
            String s9 = null;
            String s10 = null;
            switch (set25.isolationLevel) {
                case 5: {
                    s10 = MessageService.getTextMessage("42Z80.U");
                    break;
                }
                case 4: {
                    s10 = MessageService.getTextMessage("42Z92");
                    break;
                }
                case 3: {
                    b3 = true;
                }
                case 2: {
                    s10 = MessageService.getTextMessage("42Z81.U");
                    break;
                }
            }
            String s11;
            if (set25.forUpdate) {
                s11 = MessageService.getTextMessage("42Z82.U");
            }
            else if (b3) {
                s11 = MessageService.getTextMessage("42Z83.U");
            }
            else {
                s11 = MessageService.getTextMessage("42Z84.U");
            }
            switch (set25.lockMode) {
                case 7: {
                    s11 = s11 + " " + MessageService.getTextMessage("42Z85.U");
                    break;
                }
                case 6: {
                    s11 = s11 + " " + MessageService.getTextMessage("42Z86.U");
                    break;
                }
            }
            if (set25.indexName != null) {
                s8 = set25.startPositionString;
                if (s8 == null) {
                    s8 = set25.printStartPosition();
                }
                s9 = set25.stopPositionString;
                if (s9 == null) {
                    s9 = set25.printStopPosition();
                }
            }
            if (set instanceof DistinctScanResultSet) {
                return new RealDistinctScanStatistics(set25.numOpens, set25.rowsSeen, set25.rowsFiltered, set25.constructorTime, set25.openTime, set25.nextTime, set25.closeTime, set25.resultSetNumber, set25.tableName, set25.indexName, set25.isConstraint, set25.hashtableSize, set25.keyColumns, NoPutResultSetImpl.printQualifiers(set25.scanQualifiers), NoPutResultSetImpl.printQualifiers(set25.nextQualifiers), set25.getScanProperties(), s8, s9, s10, s11, set25.optimizerEstimatedRowCount, set25.optimizerEstimatedCost);
            }
            return new RealHashScanStatistics(set25.numOpens, set25.rowsSeen, set25.rowsFiltered, set25.constructorTime, set25.openTime, set25.nextTime, set25.closeTime, set25.resultSetNumber, set25.tableName, set25.indexName, set25.isConstraint, set25.hashtableSize, set25.keyColumns, NoPutResultSetImpl.printQualifiers(set25.scanQualifiers), NoPutResultSetImpl.printQualifiers(set25.nextQualifiers), set25.getScanProperties(), s8, s9, s10, s11, set25.optimizerEstimatedRowCount, set25.optimizerEstimatedCost);
        }
        else {
            if (set instanceof HashTableResultSet) {
                final HashTableResultSet set26 = (HashTableResultSet)set;
                final int n2 = (set26.subqueryTrackingArray == null) ? 0 : set26.subqueryTrackingArray.length;
                ResultSetStatistics[] array2 = new ResultSetStatistics[n2];
                boolean b4 = false;
                for (int j = 0; j < n2; ++j) {
                    if (set26.subqueryTrackingArray[j] != null && set26.subqueryTrackingArray[j].getPointOfAttachment() == set26.resultSetNumber) {
                        array2[j] = this.getResultSetStatistics(set26.subqueryTrackingArray[j]);
                        b4 = true;
                    }
                }
                if (!b4) {
                    array2 = null;
                }
                return new RealHashTableStatistics(set26.numOpens, set26.rowsSeen, set26.rowsFiltered, set26.constructorTime, set26.openTime, set26.nextTime, set26.closeTime, set26.resultSetNumber, set26.hashtableSize, set26.keyColumns, NoPutResultSetImpl.printQualifiers(set26.nextQualifiers), set26.scanProperties, set26.optimizerEstimatedRowCount, set26.optimizerEstimatedCost, array2, this.getResultSetStatistics(set26.source));
            }
            if (set instanceof VTIResultSet) {
                final VTIResultSet set27 = (VTIResultSet)set;
                return new RealVTIStatistics(set27.numOpens, set27.rowsSeen, set27.rowsFiltered, set27.constructorTime, set27.openTime, set27.nextTime, set27.closeTime, set27.resultSetNumber, set27.javaClassName, set27.optimizerEstimatedRowCount, set27.optimizerEstimatedCost);
            }
            if (set instanceof DependentResultSet) {
                boolean b5 = false;
                final DependentResultSet set28 = (DependentResultSet)set;
                String s12 = null;
                String s13 = null;
                switch (set28.isolationLevel) {
                    case 5: {
                        s12 = MessageService.getTextMessage("42Z80.U");
                        break;
                    }
                    case 4: {
                        s12 = MessageService.getTextMessage("42Z92");
                        break;
                    }
                    case 3: {
                        b5 = true;
                    }
                    case 2: {
                        s12 = MessageService.getTextMessage("42Z81.U");
                        break;
                    }
                    case 1: {
                        s12 = MessageService.getTextMessage("42Z9A");
                        break;
                    }
                }
                String s14;
                if (set28.forUpdate) {
                    s14 = MessageService.getTextMessage("42Z82.U");
                }
                else if (b5) {
                    s14 = MessageService.getTextMessage("42Z83.U");
                }
                else {
                    s14 = MessageService.getTextMessage("42Z84.U");
                }
                switch (set28.lockMode) {
                    case 7: {
                        s13 = s14 + " " + MessageService.getTextMessage("42Z85.U");
                        break;
                    }
                    case 6: {
                        s13 = s14 + " " + MessageService.getTextMessage("42Z86.U");
                        break;
                    }
                }
                String s15 = set28.startPositionString;
                if (s15 == null) {
                    s15 = set28.printStartPosition();
                }
                String s16 = set28.stopPositionString;
                if (s16 == null) {
                    s16 = set28.printStopPosition();
                }
                return new RealTableScanStatistics(set28.numOpens, set28.rowsSeen, set28.rowsFiltered, set28.constructorTime, set28.openTime, set28.nextTime, set28.closeTime, set28.resultSetNumber, set28.tableName, null, set28.indexName, set28.isConstraint, set28.printQualifiers(), set28.getScanProperties(), s15, s16, s12, s13, set28.rowsPerRead, set28.coarserLock, set28.optimizerEstimatedRowCount, set28.optimizerEstimatedCost);
            }
            return null;
        }
    }
}
