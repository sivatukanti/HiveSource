// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.recordlevel;

import parquet.filter2.predicate.UserDefinedPredicate;
import parquet.hadoop.metadata.ColumnPath;
import parquet.io.api.Binary;
import parquet.filter2.predicate.Operators;

public class IncrementallyUpdatedFilterPredicateBuilder extends IncrementallyUpdatedFilterPredicateBuilderBase
{
    @Override
    public <T extends Comparable<T>> IncrementallyUpdatedFilterPredicate visit(final Operators.Eq<T> pred) {
        final ColumnPath columnPath = pred.getColumn().getColumnPath();
        final Class<T> clazz = pred.getColumn().getColumnType();
        IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector = null;
        if (clazz.equals(Integer.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final int value) {
                        this.setResult(false);
                    }
                };
            }
            else {
                final int target = (int)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final int value) {
                        this.setResult(value == target);
                    }
                };
            }
        }
        if (clazz.equals(Long.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final long value) {
                        this.setResult(false);
                    }
                };
            }
            else {
                final long target2 = (long)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final long value) {
                        this.setResult(value == target2);
                    }
                };
            }
        }
        if (clazz.equals(Boolean.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final boolean value) {
                        this.setResult(false);
                    }
                };
            }
            else {
                final boolean target3 = (boolean)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final boolean value) {
                        this.setResult(value == target3);
                    }
                };
            }
        }
        if (clazz.equals(Float.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final float value) {
                        this.setResult(false);
                    }
                };
            }
            else {
                final float target4 = (float)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final float value) {
                        this.setResult(value == target4);
                    }
                };
            }
        }
        if (clazz.equals(Double.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final double value) {
                        this.setResult(false);
                    }
                };
            }
            else {
                final double target5 = (double)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final double value) {
                        this.setResult(value == target5);
                    }
                };
            }
        }
        if (clazz.equals(Binary.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final Binary value) {
                        this.setResult(false);
                    }
                };
            }
            else {
                final Binary target6 = (Binary)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final Binary value) {
                        this.setResult(value.compareTo(target6) == 0);
                    }
                };
            }
        }
        if (valueInspector == null) {
            throw new IllegalArgumentException("Encountered unknown type " + clazz);
        }
        this.addValueInspector(columnPath, valueInspector);
        return valueInspector;
    }
    
    @Override
    public <T extends Comparable<T>> IncrementallyUpdatedFilterPredicate visit(final Operators.NotEq<T> pred) {
        final ColumnPath columnPath = pred.getColumn().getColumnPath();
        final Class<T> clazz = pred.getColumn().getColumnType();
        IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector = null;
        if (clazz.equals(Integer.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final int value) {
                        this.setResult(true);
                    }
                };
            }
            else {
                final int target = (int)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final int value) {
                        this.setResult(value != target);
                    }
                };
            }
        }
        if (clazz.equals(Long.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final long value) {
                        this.setResult(true);
                    }
                };
            }
            else {
                final long target2 = (long)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final long value) {
                        this.setResult(value != target2);
                    }
                };
            }
        }
        if (clazz.equals(Boolean.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final boolean value) {
                        this.setResult(true);
                    }
                };
            }
            else {
                final boolean target3 = (boolean)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final boolean value) {
                        this.setResult(value != target3);
                    }
                };
            }
        }
        if (clazz.equals(Float.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final float value) {
                        this.setResult(true);
                    }
                };
            }
            else {
                final float target4 = (float)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final float value) {
                        this.setResult(value != target4);
                    }
                };
            }
        }
        if (clazz.equals(Double.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final double value) {
                        this.setResult(true);
                    }
                };
            }
            else {
                final double target5 = (double)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final double value) {
                        this.setResult(value != target5);
                    }
                };
            }
        }
        if (clazz.equals(Binary.class)) {
            if (pred.getValue() == null) {
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(false);
                    }
                    
                    @Override
                    public void update(final Binary value) {
                        this.setResult(true);
                    }
                };
            }
            else {
                final Binary target6 = (Binary)pred.getValue();
                valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                    @Override
                    public void updateNull() {
                        this.setResult(true);
                    }
                    
                    @Override
                    public void update(final Binary value) {
                        this.setResult(value.compareTo(target6) != 0);
                    }
                };
            }
        }
        if (valueInspector == null) {
            throw new IllegalArgumentException("Encountered unknown type " + clazz);
        }
        this.addValueInspector(columnPath, valueInspector);
        return valueInspector;
    }
    
    @Override
    public <T extends Comparable<T>> IncrementallyUpdatedFilterPredicate visit(final Operators.Lt<T> pred) {
        final ColumnPath columnPath = pred.getColumn().getColumnPath();
        final Class<T> clazz = pred.getColumn().getColumnType();
        IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector = null;
        if (clazz.equals(Integer.class)) {
            final int target = (int)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final int value) {
                    this.setResult(value < target);
                }
            };
        }
        if (clazz.equals(Long.class)) {
            final long target2 = (long)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final long value) {
                    this.setResult(value < target2);
                }
            };
        }
        if (clazz.equals(Boolean.class)) {
            throw new IllegalArgumentException("Operator < not supported for Boolean");
        }
        if (clazz.equals(Float.class)) {
            final float target3 = (float)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final float value) {
                    this.setResult(value < target3);
                }
            };
        }
        if (clazz.equals(Double.class)) {
            final double target4 = (double)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final double value) {
                    this.setResult(value < target4);
                }
            };
        }
        if (clazz.equals(Binary.class)) {
            final Binary target5 = (Binary)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final Binary value) {
                    this.setResult(value.compareTo(target5) < 0);
                }
            };
        }
        if (valueInspector == null) {
            throw new IllegalArgumentException("Encountered unknown type " + clazz);
        }
        this.addValueInspector(columnPath, valueInspector);
        return valueInspector;
    }
    
    @Override
    public <T extends Comparable<T>> IncrementallyUpdatedFilterPredicate visit(final Operators.LtEq<T> pred) {
        final ColumnPath columnPath = pred.getColumn().getColumnPath();
        final Class<T> clazz = pred.getColumn().getColumnType();
        IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector = null;
        if (clazz.equals(Integer.class)) {
            final int target = (int)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final int value) {
                    this.setResult(value <= target);
                }
            };
        }
        if (clazz.equals(Long.class)) {
            final long target2 = (long)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final long value) {
                    this.setResult(value <= target2);
                }
            };
        }
        if (clazz.equals(Boolean.class)) {
            throw new IllegalArgumentException("Operator <= not supported for Boolean");
        }
        if (clazz.equals(Float.class)) {
            final float target3 = (float)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final float value) {
                    this.setResult(value <= target3);
                }
            };
        }
        if (clazz.equals(Double.class)) {
            final double target4 = (double)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final double value) {
                    this.setResult(value <= target4);
                }
            };
        }
        if (clazz.equals(Binary.class)) {
            final Binary target5 = (Binary)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final Binary value) {
                    this.setResult(value.compareTo(target5) <= 0);
                }
            };
        }
        if (valueInspector == null) {
            throw new IllegalArgumentException("Encountered unknown type " + clazz);
        }
        this.addValueInspector(columnPath, valueInspector);
        return valueInspector;
    }
    
    @Override
    public <T extends Comparable<T>> IncrementallyUpdatedFilterPredicate visit(final Operators.Gt<T> pred) {
        final ColumnPath columnPath = pred.getColumn().getColumnPath();
        final Class<T> clazz = pred.getColumn().getColumnType();
        IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector = null;
        if (clazz.equals(Integer.class)) {
            final int target = (int)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final int value) {
                    this.setResult(value > target);
                }
            };
        }
        if (clazz.equals(Long.class)) {
            final long target2 = (long)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final long value) {
                    this.setResult(value > target2);
                }
            };
        }
        if (clazz.equals(Boolean.class)) {
            throw new IllegalArgumentException("Operator > not supported for Boolean");
        }
        if (clazz.equals(Float.class)) {
            final float target3 = (float)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final float value) {
                    this.setResult(value > target3);
                }
            };
        }
        if (clazz.equals(Double.class)) {
            final double target4 = (double)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final double value) {
                    this.setResult(value > target4);
                }
            };
        }
        if (clazz.equals(Binary.class)) {
            final Binary target5 = (Binary)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final Binary value) {
                    this.setResult(value.compareTo(target5) > 0);
                }
            };
        }
        if (valueInspector == null) {
            throw new IllegalArgumentException("Encountered unknown type " + clazz);
        }
        this.addValueInspector(columnPath, valueInspector);
        return valueInspector;
    }
    
    @Override
    public <T extends Comparable<T>> IncrementallyUpdatedFilterPredicate visit(final Operators.GtEq<T> pred) {
        final ColumnPath columnPath = pred.getColumn().getColumnPath();
        final Class<T> clazz = pred.getColumn().getColumnType();
        IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector = null;
        if (clazz.equals(Integer.class)) {
            final int target = (int)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final int value) {
                    this.setResult(value >= target);
                }
            };
        }
        if (clazz.equals(Long.class)) {
            final long target2 = (long)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final long value) {
                    this.setResult(value >= target2);
                }
            };
        }
        if (clazz.equals(Boolean.class)) {
            throw new IllegalArgumentException("Operator >= not supported for Boolean");
        }
        if (clazz.equals(Float.class)) {
            final float target3 = (float)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final float value) {
                    this.setResult(value >= target3);
                }
            };
        }
        if (clazz.equals(Double.class)) {
            final double target4 = (double)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final double value) {
                    this.setResult(value >= target4);
                }
            };
        }
        if (clazz.equals(Binary.class)) {
            final Binary target5 = (Binary)pred.getValue();
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(false);
                }
                
                @Override
                public void update(final Binary value) {
                    this.setResult(value.compareTo(target5) >= 0);
                }
            };
        }
        if (valueInspector == null) {
            throw new IllegalArgumentException("Encountered unknown type " + clazz);
        }
        this.addValueInspector(columnPath, valueInspector);
        return valueInspector;
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> IncrementallyUpdatedFilterPredicate visit(final Operators.UserDefined<T, U> pred) {
        final ColumnPath columnPath = pred.getColumn().getColumnPath();
        final Class<T> clazz = pred.getColumn().getColumnType();
        IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector = null;
        final U udp = pred.getUserDefinedPredicate();
        if (clazz.equals(Integer.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final int value) {
                    this.setResult(((UserDefinedPredicate<Integer>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Long.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final long value) {
                    this.setResult(((UserDefinedPredicate<Long>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Boolean.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final boolean value) {
                    this.setResult(((UserDefinedPredicate<Boolean>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Float.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final float value) {
                    this.setResult(((UserDefinedPredicate<Float>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Double.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final double value) {
                    this.setResult(((UserDefinedPredicate<Double>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Binary.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final Binary value) {
                    this.setResult(((UserDefinedPredicate<Binary>)udp).keep(value));
                }
            };
        }
        if (valueInspector == null) {
            throw new IllegalArgumentException("Encountered unknown type " + clazz);
        }
        this.addValueInspector(columnPath, valueInspector);
        return valueInspector;
    }
    
    @Override
    public <T extends Comparable<T>, U extends UserDefinedPredicate<T>> IncrementallyUpdatedFilterPredicate visit(final Operators.LogicalNotUserDefined<T, U> notPred) {
        final Operators.UserDefined<T, U> pred = notPred.getUserDefined();
        final ColumnPath columnPath = pred.getColumn().getColumnPath();
        final Class<T> clazz = pred.getColumn().getColumnType();
        IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector = null;
        final U udp = pred.getUserDefinedPredicate();
        if (clazz.equals(Integer.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(!((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final int value) {
                    this.setResult(!((UserDefinedPredicate<Integer>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Long.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(!((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final long value) {
                    this.setResult(!((UserDefinedPredicate<Long>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Boolean.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(!((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final boolean value) {
                    this.setResult(!((UserDefinedPredicate<Boolean>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Float.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(!((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final float value) {
                    this.setResult(!((UserDefinedPredicate<Float>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Double.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(!((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final double value) {
                    this.setResult(!((UserDefinedPredicate<Double>)udp).keep(value));
                }
            };
        }
        if (clazz.equals(Binary.class)) {
            valueInspector = new IncrementallyUpdatedFilterPredicate.ValueInspector() {
                @Override
                public void updateNull() {
                    this.setResult(!((UserDefinedPredicate<Comparable>)udp).keep(null));
                }
                
                @Override
                public void update(final Binary value) {
                    this.setResult(!((UserDefinedPredicate<Binary>)udp).keep(value));
                }
            };
        }
        if (valueInspector == null) {
            throw new IllegalArgumentException("Encountered unknown type " + clazz);
        }
        this.addValueInspector(columnPath, valueInspector);
        return valueInspector;
    }
}
