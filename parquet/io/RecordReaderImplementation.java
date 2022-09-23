// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import parquet.schema.PrimitiveType;
import parquet.io.api.Converter;
import parquet.schema.MessageType;
import parquet.io.api.RecordConsumer;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import parquet.io.api.PrimitiveConverter;
import parquet.column.impl.ColumnReadStoreImpl;
import parquet.column.ColumnReader;
import parquet.io.api.RecordMaterializer;
import parquet.io.api.GroupConverter;
import parquet.Log;

class RecordReaderImplementation<T> extends RecordReader<T>
{
    private static final Log LOG;
    private final GroupConverter recordRootConverter;
    private final RecordMaterializer<T> recordMaterializer;
    private State[] states;
    private ColumnReader[] columnReaders;
    private boolean shouldSkipCurrentRecord;
    
    public RecordReaderImplementation(final MessageColumnIO root, final RecordMaterializer<T> recordMaterializer, final boolean validating, final ColumnReadStoreImpl columnStore) {
        this.shouldSkipCurrentRecord = false;
        this.recordMaterializer = recordMaterializer;
        this.recordRootConverter = recordMaterializer.getRootConverter();
        final PrimitiveColumnIO[] leaves = root.getLeaves().toArray(new PrimitiveColumnIO[root.getLeaves().size()]);
        this.columnReaders = new ColumnReader[leaves.length];
        final int[][] nextColumnIdxForRepLevel = new int[leaves.length][];
        final int[][] levelToClose = new int[leaves.length][];
        final GroupConverter[][] groupConverterPaths = new GroupConverter[leaves.length][];
        final PrimitiveConverter[] leafConverters = new PrimitiveConverter[leaves.length];
        final int[] firstIndexForLevel = new int[256];
        for (int i = 0; i < leaves.length; ++i) {
            final PrimitiveColumnIO leafColumnIO = leaves[i];
            final int[] indexFieldPath = leafColumnIO.getIndexFieldPath();
            groupConverterPaths[i] = new GroupConverter[indexFieldPath.length - 1];
            GroupConverter current = this.recordRootConverter;
            for (int j = 0; j < indexFieldPath.length - 1; ++j) {
                current = current.getConverter(indexFieldPath[j]).asGroupConverter();
                groupConverterPaths[i][j] = current;
            }
            leafConverters[i] = current.getConverter(indexFieldPath[indexFieldPath.length - 1]).asPrimitiveConverter();
            this.columnReaders[i] = columnStore.getColumnReader(leafColumnIO.getColumnDescriptor());
            final int maxRepetitionLevel = leafColumnIO.getRepetitionLevel();
            nextColumnIdxForRepLevel[i] = new int[maxRepetitionLevel + 1];
            levelToClose[i] = new int[maxRepetitionLevel + 1];
            for (int nextRepLevel = 0; nextRepLevel <= maxRepetitionLevel; ++nextRepLevel) {
                if (leafColumnIO.isFirst(nextRepLevel)) {
                    firstIndexForLevel[nextRepLevel] = i;
                }
                int nextColIdx;
                if (nextRepLevel == 0) {
                    nextColIdx = i + 1;
                }
                else if (leafColumnIO.isLast(nextRepLevel)) {
                    nextColIdx = firstIndexForLevel[nextRepLevel];
                }
                else {
                    nextColIdx = i + 1;
                }
                if (nextColIdx == leaves.length) {
                    levelToClose[i][nextRepLevel] = 0;
                }
                else if (leafColumnIO.isLast(nextRepLevel)) {
                    final ColumnIO parent = leafColumnIO.getParent(nextRepLevel);
                    levelToClose[i][nextRepLevel] = parent.getFieldPath().length - 1;
                }
                else {
                    levelToClose[i][nextRepLevel] = this.getCommonParentLevel(leafColumnIO.getFieldPath(), leaves[nextColIdx].getFieldPath());
                }
                if (levelToClose[i][nextRepLevel] > leaves[i].getFieldPath().length - 1) {
                    throw new ParquetEncodingException(Arrays.toString(leaves[i].getFieldPath()) + " -(" + nextRepLevel + ")-> " + levelToClose[i][nextRepLevel]);
                }
                nextColumnIdxForRepLevel[i][nextRepLevel] = nextColIdx;
            }
        }
        this.states = new State[leaves.length];
        for (int i = 0; i < leaves.length; ++i) {
            this.states[i] = new State(i, leaves[i], this.columnReaders[i], levelToClose[i], groupConverterPaths[i], leafConverters[i]);
            final int[] definitionLevelToDepth = new int[this.states[i].primitiveColumnIO.getDefinitionLevel() + 1];
            final ColumnIO[] path = this.states[i].primitiveColumnIO.getPath();
            int depth = 0;
            for (int d = 0; d < definitionLevelToDepth.length; ++d) {
                while (depth < this.states[i].fieldPath.length - 1 && d >= path[depth + 1].getDefinitionLevel()) {
                    ++depth;
                }
                definitionLevelToDepth[d] = depth - 1;
            }
            this.states[i].definitionLevelToDepth = definitionLevelToDepth;
        }
        for (int i = 0; i < leaves.length; ++i) {
            final State state = this.states[i];
            final int[] nextStateIds = nextColumnIdxForRepLevel[i];
            state.nextState = new State[nextStateIds.length];
            for (int k = 0; k < nextStateIds.length; ++k) {
                state.nextState[k] = ((nextStateIds[k] == this.states.length) ? null : this.states[nextStateIds[k]]);
            }
        }
        for (int i = 0; i < this.states.length; ++i) {
            final State state = this.states[i];
            final Map<Case, Case> definedCases = new HashMap<Case, Case>();
            final Map<Case, Case> undefinedCases = new HashMap<Case, Case>();
            final Case[][][] caseLookup = new Case[state.fieldPath.length][][];
            for (int currentLevel = 0; currentLevel < state.fieldPath.length; ++currentLevel) {
                caseLookup[currentLevel] = new Case[state.maxDefinitionLevel + 1][];
                for (int d2 = 0; d2 <= state.maxDefinitionLevel; ++d2) {
                    caseLookup[currentLevel][d2] = new Case[state.maxRepetitionLevel + 1];
                    for (int nextR = 0; nextR <= state.maxRepetitionLevel; ++nextR) {
                        final int caseStartLevel = currentLevel;
                        final int caseDepth = Math.max(state.getDepth(d2), caseStartLevel - 1);
                        final int caseNextLevel = Math.min(state.nextLevel[nextR], caseDepth + 1);
                        Case currentCase = new Case(caseStartLevel, caseDepth, caseNextLevel, this.getNextReader(state.id, nextR), d2 == state.maxDefinitionLevel);
                        final Map<Case, Case> cases = currentCase.isDefined() ? definedCases : undefinedCases;
                        if (!cases.containsKey(currentCase)) {
                            currentCase.setID(cases.size());
                            cases.put(currentCase, currentCase);
                        }
                        else {
                            currentCase = cases.get(currentCase);
                        }
                        caseLookup[currentLevel][d2][nextR] = currentCase;
                    }
                }
            }
            state.caseLookup = caseLookup;
            state.definedCases = (List<Case>)new ArrayList(definedCases.values());
            state.undefinedCases = (List<Case>)new ArrayList(undefinedCases.values());
            final Comparator<Case> caseComparator = new Comparator<Case>() {
                @Override
                public int compare(final Case o1, final Case o2) {
                    return o1.id - o2.id;
                }
            };
            Collections.sort((List<Object>)state.definedCases, (Comparator<? super Object>)caseComparator);
            Collections.sort((List<Object>)state.undefinedCases, (Comparator<? super Object>)caseComparator);
        }
    }
    
    private RecordConsumer validator(final RecordConsumer recordConsumer, final boolean validating, final MessageType schema) {
        return validating ? new ValidatingRecordConsumer(recordConsumer, schema) : recordConsumer;
    }
    
    private RecordConsumer wrap(final RecordConsumer recordConsumer) {
        if (Log.DEBUG) {
            return new RecordConsumerLoggingWrapper(recordConsumer);
        }
        return recordConsumer;
    }
    
    @Override
    public T read() {
        int currentLevel = 0;
        this.recordRootConverter.start();
        State currentState = this.states[0];
        do {
            final ColumnReader columnReader = currentState.column;
            final int d = columnReader.getCurrentDefinitionLevel();
            for (int depth = currentState.definitionLevelToDepth[d]; currentLevel <= depth; ++currentLevel) {
                currentState.groupConverterPath[currentLevel].start();
            }
            if (d >= currentState.maxDefinitionLevel) {
                columnReader.writeCurrentValueToConverter();
            }
            columnReader.consume();
            final int nextR = (currentState.maxRepetitionLevel == 0) ? 0 : columnReader.getCurrentRepetitionLevel();
            for (int next = currentState.nextLevel[nextR]; currentLevel > next; --currentLevel) {
                currentState.groupConverterPath[currentLevel - 1].end();
            }
            currentState = currentState.nextState[nextR];
        } while (currentState != null);
        this.recordRootConverter.end();
        final T record = this.recordMaterializer.getCurrentRecord();
        this.shouldSkipCurrentRecord = (record == null);
        if (this.shouldSkipCurrentRecord) {
            this.recordMaterializer.skipCurrentRecord();
        }
        return record;
    }
    
    @Override
    public boolean shouldSkipCurrentRecord() {
        return this.shouldSkipCurrentRecord;
    }
    
    private static void log(final String string) {
        RecordReaderImplementation.LOG.debug(string);
    }
    
    int getNextReader(final int current, final int nextRepetitionLevel) {
        final State nextState = this.states[current].nextState[nextRepetitionLevel];
        return (nextState == null) ? this.states.length : nextState.id;
    }
    
    int getNextLevel(final int current, final int nextRepetitionLevel) {
        return this.states[current].nextLevel[nextRepetitionLevel];
    }
    
    private int getCommonParentLevel(final String[] previous, final String[] next) {
        int i;
        for (i = 0; i < Math.min(previous.length, next.length) && previous[i].equals(next[i]); ++i) {}
        return i;
    }
    
    protected int getStateCount() {
        return this.states.length;
    }
    
    protected State getState(final int i) {
        return this.states[i];
    }
    
    protected RecordMaterializer<T> getMaterializer() {
        return this.recordMaterializer;
    }
    
    protected Converter getRecordConsumer() {
        return this.recordRootConverter;
    }
    
    protected Iterable<ColumnReader> getColumnReaders() {
        return Arrays.asList(this.columnReaders);
    }
    
    static {
        LOG = Log.getLog(RecordReaderImplementation.class);
    }
    
    public static class Case
    {
        private int id;
        private final int startLevel;
        private final int depth;
        private final int nextLevel;
        private final boolean goingUp;
        private final boolean goingDown;
        private final int nextState;
        private final boolean defined;
        
        public Case(final int startLevel, final int depth, final int nextLevel, final int nextState, final boolean defined) {
            this.startLevel = startLevel;
            this.depth = depth;
            this.nextLevel = nextLevel;
            this.nextState = nextState;
            this.defined = defined;
            this.goingUp = (startLevel <= depth);
            this.goingDown = (depth + 1 > nextLevel);
        }
        
        public void setID(final int id) {
            this.id = id;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 17;
            hashCode += 31 * this.startLevel;
            hashCode += 31 * this.depth;
            hashCode += 31 * this.nextLevel;
            hashCode += 31 * this.nextState;
            hashCode += 31 * (this.defined ? 0 : 1);
            return hashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Case && this.equals((Case)obj);
        }
        
        public boolean equals(final Case other) {
            return this.startLevel == other.startLevel && this.depth == other.depth && this.nextLevel == other.nextLevel && this.nextState == other.nextState && ((this.defined && other.defined) || (!this.defined && !other.defined));
        }
        
        public int getID() {
            return this.id;
        }
        
        public int getStartLevel() {
            return this.startLevel;
        }
        
        public int getDepth() {
            return this.depth;
        }
        
        public int getNextLevel() {
            return this.nextLevel;
        }
        
        public int getNextState() {
            return this.nextState;
        }
        
        public boolean isGoingUp() {
            return this.goingUp;
        }
        
        public boolean isGoingDown() {
            return this.goingDown;
        }
        
        public boolean isDefined() {
            return this.defined;
        }
        
        @Override
        public String toString() {
            return "Case " + this.startLevel + " -> " + this.depth + " -> " + this.nextLevel + "; goto sate_" + this.getNextState();
        }
    }
    
    public static class State
    {
        public final int id;
        public final PrimitiveColumnIO primitiveColumnIO;
        public final int maxDefinitionLevel;
        public final int maxRepetitionLevel;
        public final PrimitiveType.PrimitiveTypeName primitive;
        public final ColumnReader column;
        public final String[] fieldPath;
        public final int[] indexFieldPath;
        public final GroupConverter[] groupConverterPath;
        public final PrimitiveConverter primitiveConverter;
        public final String primitiveField;
        public final int primitiveFieldIndex;
        public final int[] nextLevel;
        private int[] definitionLevelToDepth;
        private State[] nextState;
        private Case[][][] caseLookup;
        private List<Case> definedCases;
        private List<Case> undefinedCases;
        
        private State(final int id, final PrimitiveColumnIO primitiveColumnIO, final ColumnReader column, final int[] nextLevel, final GroupConverter[] groupConverterPath, final PrimitiveConverter primitiveConverter) {
            this.id = id;
            this.primitiveColumnIO = primitiveColumnIO;
            this.maxDefinitionLevel = primitiveColumnIO.getDefinitionLevel();
            this.maxRepetitionLevel = primitiveColumnIO.getRepetitionLevel();
            this.column = column;
            this.nextLevel = nextLevel;
            this.groupConverterPath = groupConverterPath;
            this.primitiveConverter = primitiveConverter;
            this.primitive = primitiveColumnIO.getType().asPrimitiveType().getPrimitiveTypeName();
            this.fieldPath = primitiveColumnIO.getFieldPath();
            this.primitiveField = this.fieldPath[this.fieldPath.length - 1];
            this.indexFieldPath = primitiveColumnIO.getIndexFieldPath();
            this.primitiveFieldIndex = this.indexFieldPath[this.indexFieldPath.length - 1];
        }
        
        public int getDepth(final int definitionLevel) {
            return this.definitionLevelToDepth[definitionLevel];
        }
        
        public List<Case> getDefinedCases() {
            return this.definedCases;
        }
        
        public List<Case> getUndefinedCases() {
            return this.undefinedCases;
        }
        
        public Case getCase(final int currentLevel, final int d, final int nextR) {
            return this.caseLookup[currentLevel][d][nextR];
        }
        
        public State getNextState(final int nextR) {
            return this.nextState[nextR];
        }
    }
}
