// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.TEnum;

public enum TGetInfoType implements TEnum
{
    CLI_MAX_DRIVER_CONNECTIONS(0), 
    CLI_MAX_CONCURRENT_ACTIVITIES(1), 
    CLI_DATA_SOURCE_NAME(2), 
    CLI_FETCH_DIRECTION(8), 
    CLI_SERVER_NAME(13), 
    CLI_SEARCH_PATTERN_ESCAPE(14), 
    CLI_DBMS_NAME(17), 
    CLI_DBMS_VER(18), 
    CLI_ACCESSIBLE_TABLES(19), 
    CLI_ACCESSIBLE_PROCEDURES(20), 
    CLI_CURSOR_COMMIT_BEHAVIOR(23), 
    CLI_DATA_SOURCE_READ_ONLY(25), 
    CLI_DEFAULT_TXN_ISOLATION(26), 
    CLI_IDENTIFIER_CASE(28), 
    CLI_IDENTIFIER_QUOTE_CHAR(29), 
    CLI_MAX_COLUMN_NAME_LEN(30), 
    CLI_MAX_CURSOR_NAME_LEN(31), 
    CLI_MAX_SCHEMA_NAME_LEN(32), 
    CLI_MAX_CATALOG_NAME_LEN(34), 
    CLI_MAX_TABLE_NAME_LEN(35), 
    CLI_SCROLL_CONCURRENCY(43), 
    CLI_TXN_CAPABLE(46), 
    CLI_USER_NAME(47), 
    CLI_TXN_ISOLATION_OPTION(72), 
    CLI_INTEGRITY(73), 
    CLI_GETDATA_EXTENSIONS(81), 
    CLI_NULL_COLLATION(85), 
    CLI_ALTER_TABLE(86), 
    CLI_ORDER_BY_COLUMNS_IN_SELECT(90), 
    CLI_SPECIAL_CHARACTERS(94), 
    CLI_MAX_COLUMNS_IN_GROUP_BY(97), 
    CLI_MAX_COLUMNS_IN_INDEX(98), 
    CLI_MAX_COLUMNS_IN_ORDER_BY(99), 
    CLI_MAX_COLUMNS_IN_SELECT(100), 
    CLI_MAX_COLUMNS_IN_TABLE(101), 
    CLI_MAX_INDEX_SIZE(102), 
    CLI_MAX_ROW_SIZE(104), 
    CLI_MAX_STATEMENT_LEN(105), 
    CLI_MAX_TABLES_IN_SELECT(106), 
    CLI_MAX_USER_NAME_LEN(107), 
    CLI_OJ_CAPABILITIES(115), 
    CLI_XOPEN_CLI_YEAR(10000), 
    CLI_CURSOR_SENSITIVITY(10001), 
    CLI_DESCRIBE_PARAMETER(10002), 
    CLI_CATALOG_NAME(10003), 
    CLI_COLLATION_SEQ(10004), 
    CLI_MAX_IDENTIFIER_LEN(10005);
    
    private final int value;
    
    private TGetInfoType(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static TGetInfoType findByValue(final int value) {
        switch (value) {
            case 0: {
                return TGetInfoType.CLI_MAX_DRIVER_CONNECTIONS;
            }
            case 1: {
                return TGetInfoType.CLI_MAX_CONCURRENT_ACTIVITIES;
            }
            case 2: {
                return TGetInfoType.CLI_DATA_SOURCE_NAME;
            }
            case 8: {
                return TGetInfoType.CLI_FETCH_DIRECTION;
            }
            case 13: {
                return TGetInfoType.CLI_SERVER_NAME;
            }
            case 14: {
                return TGetInfoType.CLI_SEARCH_PATTERN_ESCAPE;
            }
            case 17: {
                return TGetInfoType.CLI_DBMS_NAME;
            }
            case 18: {
                return TGetInfoType.CLI_DBMS_VER;
            }
            case 19: {
                return TGetInfoType.CLI_ACCESSIBLE_TABLES;
            }
            case 20: {
                return TGetInfoType.CLI_ACCESSIBLE_PROCEDURES;
            }
            case 23: {
                return TGetInfoType.CLI_CURSOR_COMMIT_BEHAVIOR;
            }
            case 25: {
                return TGetInfoType.CLI_DATA_SOURCE_READ_ONLY;
            }
            case 26: {
                return TGetInfoType.CLI_DEFAULT_TXN_ISOLATION;
            }
            case 28: {
                return TGetInfoType.CLI_IDENTIFIER_CASE;
            }
            case 29: {
                return TGetInfoType.CLI_IDENTIFIER_QUOTE_CHAR;
            }
            case 30: {
                return TGetInfoType.CLI_MAX_COLUMN_NAME_LEN;
            }
            case 31: {
                return TGetInfoType.CLI_MAX_CURSOR_NAME_LEN;
            }
            case 32: {
                return TGetInfoType.CLI_MAX_SCHEMA_NAME_LEN;
            }
            case 34: {
                return TGetInfoType.CLI_MAX_CATALOG_NAME_LEN;
            }
            case 35: {
                return TGetInfoType.CLI_MAX_TABLE_NAME_LEN;
            }
            case 43: {
                return TGetInfoType.CLI_SCROLL_CONCURRENCY;
            }
            case 46: {
                return TGetInfoType.CLI_TXN_CAPABLE;
            }
            case 47: {
                return TGetInfoType.CLI_USER_NAME;
            }
            case 72: {
                return TGetInfoType.CLI_TXN_ISOLATION_OPTION;
            }
            case 73: {
                return TGetInfoType.CLI_INTEGRITY;
            }
            case 81: {
                return TGetInfoType.CLI_GETDATA_EXTENSIONS;
            }
            case 85: {
                return TGetInfoType.CLI_NULL_COLLATION;
            }
            case 86: {
                return TGetInfoType.CLI_ALTER_TABLE;
            }
            case 90: {
                return TGetInfoType.CLI_ORDER_BY_COLUMNS_IN_SELECT;
            }
            case 94: {
                return TGetInfoType.CLI_SPECIAL_CHARACTERS;
            }
            case 97: {
                return TGetInfoType.CLI_MAX_COLUMNS_IN_GROUP_BY;
            }
            case 98: {
                return TGetInfoType.CLI_MAX_COLUMNS_IN_INDEX;
            }
            case 99: {
                return TGetInfoType.CLI_MAX_COLUMNS_IN_ORDER_BY;
            }
            case 100: {
                return TGetInfoType.CLI_MAX_COLUMNS_IN_SELECT;
            }
            case 101: {
                return TGetInfoType.CLI_MAX_COLUMNS_IN_TABLE;
            }
            case 102: {
                return TGetInfoType.CLI_MAX_INDEX_SIZE;
            }
            case 104: {
                return TGetInfoType.CLI_MAX_ROW_SIZE;
            }
            case 105: {
                return TGetInfoType.CLI_MAX_STATEMENT_LEN;
            }
            case 106: {
                return TGetInfoType.CLI_MAX_TABLES_IN_SELECT;
            }
            case 107: {
                return TGetInfoType.CLI_MAX_USER_NAME_LEN;
            }
            case 115: {
                return TGetInfoType.CLI_OJ_CAPABILITIES;
            }
            case 10000: {
                return TGetInfoType.CLI_XOPEN_CLI_YEAR;
            }
            case 10001: {
                return TGetInfoType.CLI_CURSOR_SENSITIVITY;
            }
            case 10002: {
                return TGetInfoType.CLI_DESCRIBE_PARAMETER;
            }
            case 10003: {
                return TGetInfoType.CLI_CATALOG_NAME;
            }
            case 10004: {
                return TGetInfoType.CLI_COLLATION_SEQ;
            }
            case 10005: {
                return TGetInfoType.CLI_MAX_IDENTIFIER_LEN;
            }
            default: {
                return null;
            }
        }
    }
}
