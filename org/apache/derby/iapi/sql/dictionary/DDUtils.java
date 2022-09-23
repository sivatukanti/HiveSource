// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.derby.iapi.error.StandardException;

public class DDUtils
{
    public static ReferencedKeyConstraintDescriptor locateReferencedConstraint(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final String s, final String[] array, final ConsInfo consInfo) throws StandardException {
        final TableDescriptor referencedTableDescriptor = consInfo.getReferencedTableDescriptor(dataDictionary);
        if (referencedTableDescriptor == null) {
            throw StandardException.newException("X0Y46.S", s, consInfo.getReferencedTableName());
        }
        final String[] referencedColumnNames = consInfo.getReferencedColumnNames();
        if (referencedColumnNames != null && referencedColumnNames.length != 0) {
            final ColumnDescriptorList columnDescriptors = getColumnDescriptors(dataDictionary, tableDescriptor, array);
            final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(referencedTableDescriptor);
            for (int size = constraintDescriptors.size(), i = 0; i < size; ++i) {
                final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
                if (element instanceof ReferencedKeyConstraintDescriptor && element.areColumnsComparable(columnDescriptors) && columnNamesMatch(referencedColumnNames, element.getColumnDescriptors())) {
                    return (ReferencedKeyConstraintDescriptor)element;
                }
            }
            throw StandardException.newException("X0Y44.S", s, referencedTableDescriptor.getQualifiedName());
        }
        final ReferencedKeyConstraintDescriptor primaryKey = referencedTableDescriptor.getPrimaryKey();
        if (primaryKey == null) {
            throw StandardException.newException("X0Y41.S", s, referencedTableDescriptor.getQualifiedName());
        }
        final ColumnDescriptorList columnDescriptors2 = getColumnDescriptors(dataDictionary, tableDescriptor, array);
        if (columnDescriptors2.size() != primaryKey.getColumnDescriptors().size()) {
            throw StandardException.newException("X0Y43.S", s, String.valueOf(columnDescriptors2.size()), String.valueOf(primaryKey.getColumnDescriptors().size()));
        }
        if (!primaryKey.areColumnsComparable(columnDescriptors2)) {
            throw StandardException.newException("X0Y42.S", s);
        }
        return primaryKey;
    }
    
    public static ColumnDescriptorList getColumnDescriptors(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final String[] array) throws StandardException {
        final ColumnDescriptorList list = new ColumnDescriptorList();
        for (int i = 0; i < array.length; ++i) {
            list.add(tableDescriptor.getUUID(), tableDescriptor.getColumnDescriptor(array[i]));
        }
        return list;
    }
    
    public static boolean columnNamesMatch(final String[] array, final ColumnDescriptorList list) throws StandardException {
        if (array.length != list.size()) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!list.elementAt(i).getColumnName().equals(array[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static void validateReferentialActions(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final String s, final ConsInfo consInfo, final String[] array) throws StandardException {
        final int referentialActionDeleteRule = consInfo.getReferentialActionDeleteRule();
        if (referentialActionDeleteRule == 3) {
            boolean b = false;
            for (int i = 0; i < array.length; ++i) {
                if (tableDescriptor.getColumnDescriptor(array[i]).getType().isNullable()) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                throw StandardException.newException("42834", s);
            }
        }
        final TableDescriptor referencedTableDescriptor = consInfo.getReferencedTableDescriptor(dataDictionary);
        final Hashtable hashtable = new Hashtable();
        final boolean equals = referencedTableDescriptor.getUUID().equals(tableDescriptor.getUUID());
        validateDeleteConnection(dataDictionary, tableDescriptor, referencedTableDescriptor, referentialActionDeleteRule, hashtable, (Hashtable)hashtable.clone(), true, s, false, new StringBuffer(0), referencedTableDescriptor.getSchemaName() + "." + referencedTableDescriptor.getName(), equals, getCurrentDeleteConnections(dataDictionary, tableDescriptor, -1, hashtable, false, true));
        if (!equals) {
            checkForAnyExistingDeleteConnectionViolations(dataDictionary, tableDescriptor, referentialActionDeleteRule, hashtable, s);
        }
    }
    
    private static int getCurrentDeleteConnections(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final int n, final Hashtable hashtable, boolean b, boolean b2) throws StandardException {
        int n2 = -1;
        tableDescriptor.emptyConstraintDescriptorList();
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor);
        final int size = constraintDescriptors.size();
        final boolean b3 = b;
        for (int i = 0; i < size; ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element instanceof ForeignKeyConstraintDescriptor) {
                final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)element;
                foreignKeyConstraintDescriptor.getConstraintName();
                final int raDeleteRule = foreignKeyConstraintDescriptor.getRaDeleteRule();
                foreignKeyConstraintDescriptor.getRaUpdateRule();
                if (b2 && foreignKeyConstraintDescriptor.isSelfReferencingFK()) {
                    n2 = raDeleteRule;
                    b2 = false;
                }
                final TableDescriptor tableDescriptor2 = foreignKeyConstraintDescriptor.getReferencedConstraint().getTableDescriptor();
                final int value = (n == -1) ? raDeleteRule : n;
                final String string = tableDescriptor2.getSchemaName() + "." + tableDescriptor2.getName();
                if (hashtable.get(string) != null) {
                    b = b3;
                }
                else if (raDeleteRule != 0 && b) {
                    b = b3;
                }
                else {
                    hashtable.put(string, new Integer(value));
                    if (!foreignKeyConstraintDescriptor.isSelfReferencingFK()) {
                        getCurrentDeleteConnections(dataDictionary, tableDescriptor2, value, hashtable, true, false);
                    }
                    b = b3;
                }
            }
        }
        return n2;
    }
    
    private static void validateDeleteConnection(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final TableDescriptor tableDescriptor2, final int n, final Hashtable hashtable, final Hashtable hashtable2, final boolean b, final String s, boolean b2, StringBuffer sb, final String s2, final boolean b3, final int n2) throws StandardException {
        if (b) {
            final Integer n3 = hashtable.get(tableDescriptor2.getSchemaName() + "." + tableDescriptor2.getName());
            if (b3) {
                if (n2 != -1) {
                    if (n2 != n) {
                        if (n2 == 3) {
                            throw generateError("XCL33.S", s, s2);
                        }
                        throw generateError("XCL36.S", s, n2);
                    }
                    else if (n2 == 3 && n == 3) {
                        throw generateError("XCL33.S", s, s2);
                    }
                }
                if (b3 && hashtable.contains(new Integer(0)) && n != 0) {
                    throw generateError("XCL37.S", s, 0);
                }
                return;
            }
            else {
                if (n2 != -1 && n == 0 && n2 != 0) {
                    throw generateError("XCL39.S", s);
                }
                if (n3 != null) {
                    checkForMultiplePathInvalidCases(n3, n, s, s2);
                }
                if (n != 0) {
                    b2 = true;
                }
                sb = sb.append(n);
            }
        }
        final boolean b4 = b2;
        boolean b5 = true;
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor2);
        for (int size = constraintDescriptors.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element instanceof ForeignKeyConstraintDescriptor) {
                final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)element;
                foreignKeyConstraintDescriptor.getConstraintName();
                final int raDeleteRule = foreignKeyConstraintDescriptor.getRaDeleteRule();
                foreignKeyConstraintDescriptor.getRaUpdateRule();
                final TableDescriptor tableDescriptor3 = foreignKeyConstraintDescriptor.getReferencedConstraint().getTableDescriptor();
                if (raDeleteRule != 0) {
                    if (b2) {
                        b2 = b4;
                        continue;
                    }
                    b2 = true;
                    b5 = false;
                }
                final boolean selfReferencingFK = foreignKeyConstraintDescriptor.isSelfReferencingFK();
                sb = sb.append(raDeleteRule);
                if (tableDescriptor3.getUUID().equals(tableDescriptor.getUUID())) {
                    int j = 0;
                    while (j < sb.length()) {
                        final int numericValue = Character.getNumericValue(sb.charAt(j));
                        if (numericValue != n) {
                            if (numericValue != 0) {
                                throw generateError("XCL40.S", s);
                            }
                            throw generateError("XCL34.S", s, s2);
                        }
                        else {
                            ++j;
                        }
                    }
                }
                final String string = tableDescriptor3.getSchemaName() + "." + tableDescriptor3.getName();
                final Integer n4 = hashtable2.get(string);
                if (n4 != null) {
                    if (!selfReferencingFK && b5) {
                        checkForMultiplePathInvalidCases(n4, n, s, s2);
                    }
                }
                else if (hashtable.get(string) == null) {
                    if (b5) {
                        hashtable.put(string, new Integer(n));
                    }
                    if (!selfReferencingFK) {
                        validateDeleteConnection(dataDictionary, tableDescriptor, tableDescriptor3, n, hashtable, hashtable2, false, s, b2, sb, s2, b3, n2);
                    }
                }
                b2 = b4;
                sb.setLength(sb.length() - 1);
            }
        }
    }
    
    private static void checkForMultiplePathInvalidCases(final int n, final int n2, final String s, final String s2) throws StandardException {
        if (n != n2) {
            if (n == 3) {
                throw generateError("XCL35.S", s, s2);
            }
            throw generateError("XCL38.S", s, n);
        }
        else if (n == 3 && n2 == 3) {
            throw generateError("XCL35.S", s, s2);
        }
    }
    
    private static void checkForAnyExistingDeleteConnectionViolations(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final int n, final Hashtable hashtable, final String s) throws StandardException {
        if (n != 0) {
            return;
        }
        final String string = tableDescriptor.getSchemaName() + "." + tableDescriptor.getName();
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor);
        for (int size = constraintDescriptors.size(), i = 0; i < size; ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element instanceof ReferencedKeyConstraintDescriptor) {
                final ConstraintDescriptorList activeConstraintDescriptors = dataDictionary.getActiveConstraintDescriptors(((ReferencedKeyConstraintDescriptor)element).getForeignKeyConstraints(3));
                final int size2 = activeConstraintDescriptors.size();
                if (size2 != 0) {
                    final Hashtable<String, Integer> hashtable2 = new Hashtable<String, Integer>();
                    for (int j = 0; j < size2; ++j) {
                        final ForeignKeyConstraintDescriptor foreignKeyConstraintDescriptor = (ForeignKeyConstraintDescriptor)activeConstraintDescriptors.elementAt(j);
                        final TableDescriptor tableDescriptor2 = foreignKeyConstraintDescriptor.getTableDescriptor();
                        final int raDeleteRule = foreignKeyConstraintDescriptor.getRaDeleteRule();
                        if (!foreignKeyConstraintDescriptor.isSelfReferencingFK()) {
                            getCurrentDeleteConnections(dataDictionary, tableDescriptor2, -1, hashtable2, false, true);
                            final Enumeration<String> keys = hashtable2.keys();
                            while (keys.hasMoreElements()) {
                                final String s2 = keys.nextElement();
                                if (!s2.equals(string) && hashtable.containsKey(s2)) {
                                    final int intValue = hashtable2.get(s2);
                                    if ((intValue == 3 && raDeleteRule == 3) || intValue != raDeleteRule) {
                                        throw generateError("XCL41.S", s);
                                    }
                                    continue;
                                }
                            }
                        }
                        hashtable2.clear();
                    }
                }
            }
        }
    }
    
    private static StandardException generateError(final String s, final String s2) {
        return StandardException.newException("42915", s2, MessageService.getTextMessage(s));
    }
    
    private static StandardException generateError(final String s, final String s2, final int n) {
        String s3 = null;
        switch (n) {
            case 0: {
                s3 = "CASCADE";
                break;
            }
            case 1: {
                s3 = "RESTRICT";
                break;
            }
            case 3: {
                s3 = "SET NULL";
                break;
            }
            case 4: {
                s3 = "SET DEFAULT";
                break;
            }
            default: {
                s3 = "NO ACTION";
                break;
            }
        }
        return StandardException.newException("42915", s2, MessageService.getTextMessage(s, MessageService.getTextMessage(s3)));
    }
    
    private static StandardException generateError(final String s, final String s2, final String s3) {
        return StandardException.newException("42915", s2, MessageService.getTextMessage(s, s3));
    }
}
