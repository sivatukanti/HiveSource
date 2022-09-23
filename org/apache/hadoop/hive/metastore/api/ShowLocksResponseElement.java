// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.api;

import java.util.BitSet;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import java.util.Iterator;
import java.util.EnumSet;
import java.util.Collections;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import java.util.EnumMap;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.StandardScheme;
import java.util.HashMap;
import org.apache.thrift.TFieldIdEnum;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TCompactProtocol;
import java.io.OutputStream;
import org.apache.thrift.transport.TIOStreamTransport;
import java.io.ObjectOutputStream;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TBaseHelper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.IScheme;
import java.util.Map;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import java.io.Serializable;
import org.apache.thrift.TBase;

public class ShowLocksResponseElement implements TBase<ShowLocksResponseElement, _Fields>, Serializable, Cloneable
{
    private static final TStruct STRUCT_DESC;
    private static final TField LOCKID_FIELD_DESC;
    private static final TField DBNAME_FIELD_DESC;
    private static final TField TABLENAME_FIELD_DESC;
    private static final TField PARTNAME_FIELD_DESC;
    private static final TField STATE_FIELD_DESC;
    private static final TField TYPE_FIELD_DESC;
    private static final TField TXNID_FIELD_DESC;
    private static final TField LASTHEARTBEAT_FIELD_DESC;
    private static final TField ACQUIREDAT_FIELD_DESC;
    private static final TField USER_FIELD_DESC;
    private static final TField HOSTNAME_FIELD_DESC;
    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes;
    private long lockid;
    private String dbname;
    private String tablename;
    private String partname;
    private LockState state;
    private LockType type;
    private long txnid;
    private long lastheartbeat;
    private long acquiredat;
    private String user;
    private String hostname;
    private static final int __LOCKID_ISSET_ID = 0;
    private static final int __TXNID_ISSET_ID = 1;
    private static final int __LASTHEARTBEAT_ISSET_ID = 2;
    private static final int __ACQUIREDAT_ISSET_ID = 3;
    private byte __isset_bitfield;
    private _Fields[] optionals;
    public static final Map<_Fields, FieldMetaData> metaDataMap;
    
    public ShowLocksResponseElement() {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.TABLENAME, _Fields.PARTNAME, _Fields.TXNID, _Fields.ACQUIREDAT };
    }
    
    public ShowLocksResponseElement(final long lockid, final String dbname, final LockState state, final LockType type, final long lastheartbeat, final String user, final String hostname) {
        this();
        this.lockid = lockid;
        this.setLockidIsSet(true);
        this.dbname = dbname;
        this.state = state;
        this.type = type;
        this.lastheartbeat = lastheartbeat;
        this.setLastheartbeatIsSet(true);
        this.user = user;
        this.hostname = hostname;
    }
    
    public ShowLocksResponseElement(final ShowLocksResponseElement other) {
        this.__isset_bitfield = 0;
        this.optionals = new _Fields[] { _Fields.TABLENAME, _Fields.PARTNAME, _Fields.TXNID, _Fields.ACQUIREDAT };
        this.__isset_bitfield = other.__isset_bitfield;
        this.lockid = other.lockid;
        if (other.isSetDbname()) {
            this.dbname = other.dbname;
        }
        if (other.isSetTablename()) {
            this.tablename = other.tablename;
        }
        if (other.isSetPartname()) {
            this.partname = other.partname;
        }
        if (other.isSetState()) {
            this.state = other.state;
        }
        if (other.isSetType()) {
            this.type = other.type;
        }
        this.txnid = other.txnid;
        this.lastheartbeat = other.lastheartbeat;
        this.acquiredat = other.acquiredat;
        if (other.isSetUser()) {
            this.user = other.user;
        }
        if (other.isSetHostname()) {
            this.hostname = other.hostname;
        }
    }
    
    @Override
    public ShowLocksResponseElement deepCopy() {
        return new ShowLocksResponseElement(this);
    }
    
    @Override
    public void clear() {
        this.setLockidIsSet(false);
        this.lockid = 0L;
        this.dbname = null;
        this.tablename = null;
        this.partname = null;
        this.state = null;
        this.type = null;
        this.setTxnidIsSet(false);
        this.txnid = 0L;
        this.setLastheartbeatIsSet(false);
        this.lastheartbeat = 0L;
        this.setAcquiredatIsSet(false);
        this.acquiredat = 0L;
        this.user = null;
        this.hostname = null;
    }
    
    public long getLockid() {
        return this.lockid;
    }
    
    public void setLockid(final long lockid) {
        this.lockid = lockid;
        this.setLockidIsSet(true);
    }
    
    public void unsetLockid() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 0);
    }
    
    public boolean isSetLockid() {
        return EncodingUtils.testBit(this.__isset_bitfield, 0);
    }
    
    public void setLockidIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 0, value);
    }
    
    public String getDbname() {
        return this.dbname;
    }
    
    public void setDbname(final String dbname) {
        this.dbname = dbname;
    }
    
    public void unsetDbname() {
        this.dbname = null;
    }
    
    public boolean isSetDbname() {
        return this.dbname != null;
    }
    
    public void setDbnameIsSet(final boolean value) {
        if (!value) {
            this.dbname = null;
        }
    }
    
    public String getTablename() {
        return this.tablename;
    }
    
    public void setTablename(final String tablename) {
        this.tablename = tablename;
    }
    
    public void unsetTablename() {
        this.tablename = null;
    }
    
    public boolean isSetTablename() {
        return this.tablename != null;
    }
    
    public void setTablenameIsSet(final boolean value) {
        if (!value) {
            this.tablename = null;
        }
    }
    
    public String getPartname() {
        return this.partname;
    }
    
    public void setPartname(final String partname) {
        this.partname = partname;
    }
    
    public void unsetPartname() {
        this.partname = null;
    }
    
    public boolean isSetPartname() {
        return this.partname != null;
    }
    
    public void setPartnameIsSet(final boolean value) {
        if (!value) {
            this.partname = null;
        }
    }
    
    public LockState getState() {
        return this.state;
    }
    
    public void setState(final LockState state) {
        this.state = state;
    }
    
    public void unsetState() {
        this.state = null;
    }
    
    public boolean isSetState() {
        return this.state != null;
    }
    
    public void setStateIsSet(final boolean value) {
        if (!value) {
            this.state = null;
        }
    }
    
    public LockType getType() {
        return this.type;
    }
    
    public void setType(final LockType type) {
        this.type = type;
    }
    
    public void unsetType() {
        this.type = null;
    }
    
    public boolean isSetType() {
        return this.type != null;
    }
    
    public void setTypeIsSet(final boolean value) {
        if (!value) {
            this.type = null;
        }
    }
    
    public long getTxnid() {
        return this.txnid;
    }
    
    public void setTxnid(final long txnid) {
        this.txnid = txnid;
        this.setTxnidIsSet(true);
    }
    
    public void unsetTxnid() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 1);
    }
    
    public boolean isSetTxnid() {
        return EncodingUtils.testBit(this.__isset_bitfield, 1);
    }
    
    public void setTxnidIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 1, value);
    }
    
    public long getLastheartbeat() {
        return this.lastheartbeat;
    }
    
    public void setLastheartbeat(final long lastheartbeat) {
        this.lastheartbeat = lastheartbeat;
        this.setLastheartbeatIsSet(true);
    }
    
    public void unsetLastheartbeat() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 2);
    }
    
    public boolean isSetLastheartbeat() {
        return EncodingUtils.testBit(this.__isset_bitfield, 2);
    }
    
    public void setLastheartbeatIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 2, value);
    }
    
    public long getAcquiredat() {
        return this.acquiredat;
    }
    
    public void setAcquiredat(final long acquiredat) {
        this.acquiredat = acquiredat;
        this.setAcquiredatIsSet(true);
    }
    
    public void unsetAcquiredat() {
        this.__isset_bitfield = EncodingUtils.clearBit(this.__isset_bitfield, 3);
    }
    
    public boolean isSetAcquiredat() {
        return EncodingUtils.testBit(this.__isset_bitfield, 3);
    }
    
    public void setAcquiredatIsSet(final boolean value) {
        this.__isset_bitfield = EncodingUtils.setBit(this.__isset_bitfield, 3, value);
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setUser(final String user) {
        this.user = user;
    }
    
    public void unsetUser() {
        this.user = null;
    }
    
    public boolean isSetUser() {
        return this.user != null;
    }
    
    public void setUserIsSet(final boolean value) {
        if (!value) {
            this.user = null;
        }
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }
    
    public void unsetHostname() {
        this.hostname = null;
    }
    
    public boolean isSetHostname() {
        return this.hostname != null;
    }
    
    public void setHostnameIsSet(final boolean value) {
        if (!value) {
            this.hostname = null;
        }
    }
    
    @Override
    public void setFieldValue(final _Fields field, final Object value) {
        switch (field) {
            case LOCKID: {
                if (value == null) {
                    this.unsetLockid();
                    break;
                }
                this.setLockid((long)value);
                break;
            }
            case DBNAME: {
                if (value == null) {
                    this.unsetDbname();
                    break;
                }
                this.setDbname((String)value);
                break;
            }
            case TABLENAME: {
                if (value == null) {
                    this.unsetTablename();
                    break;
                }
                this.setTablename((String)value);
                break;
            }
            case PARTNAME: {
                if (value == null) {
                    this.unsetPartname();
                    break;
                }
                this.setPartname((String)value);
                break;
            }
            case STATE: {
                if (value == null) {
                    this.unsetState();
                    break;
                }
                this.setState((LockState)value);
                break;
            }
            case TYPE: {
                if (value == null) {
                    this.unsetType();
                    break;
                }
                this.setType((LockType)value);
                break;
            }
            case TXNID: {
                if (value == null) {
                    this.unsetTxnid();
                    break;
                }
                this.setTxnid((long)value);
                break;
            }
            case LASTHEARTBEAT: {
                if (value == null) {
                    this.unsetLastheartbeat();
                    break;
                }
                this.setLastheartbeat((long)value);
                break;
            }
            case ACQUIREDAT: {
                if (value == null) {
                    this.unsetAcquiredat();
                    break;
                }
                this.setAcquiredat((long)value);
                break;
            }
            case USER: {
                if (value == null) {
                    this.unsetUser();
                    break;
                }
                this.setUser((String)value);
                break;
            }
            case HOSTNAME: {
                if (value == null) {
                    this.unsetHostname();
                    break;
                }
                this.setHostname((String)value);
                break;
            }
        }
    }
    
    @Override
    public Object getFieldValue(final _Fields field) {
        switch (field) {
            case LOCKID: {
                return this.getLockid();
            }
            case DBNAME: {
                return this.getDbname();
            }
            case TABLENAME: {
                return this.getTablename();
            }
            case PARTNAME: {
                return this.getPartname();
            }
            case STATE: {
                return this.getState();
            }
            case TYPE: {
                return this.getType();
            }
            case TXNID: {
                return this.getTxnid();
            }
            case LASTHEARTBEAT: {
                return this.getLastheartbeat();
            }
            case ACQUIREDAT: {
                return this.getAcquiredat();
            }
            case USER: {
                return this.getUser();
            }
            case HOSTNAME: {
                return this.getHostname();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean isSet(final _Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }
        switch (field) {
            case LOCKID: {
                return this.isSetLockid();
            }
            case DBNAME: {
                return this.isSetDbname();
            }
            case TABLENAME: {
                return this.isSetTablename();
            }
            case PARTNAME: {
                return this.isSetPartname();
            }
            case STATE: {
                return this.isSetState();
            }
            case TYPE: {
                return this.isSetType();
            }
            case TXNID: {
                return this.isSetTxnid();
            }
            case LASTHEARTBEAT: {
                return this.isSetLastheartbeat();
            }
            case ACQUIREDAT: {
                return this.isSetAcquiredat();
            }
            case USER: {
                return this.isSetUser();
            }
            case HOSTNAME: {
                return this.isSetHostname();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean equals(final Object that) {
        return that != null && that instanceof ShowLocksResponseElement && this.equals((ShowLocksResponseElement)that);
    }
    
    public boolean equals(final ShowLocksResponseElement that) {
        if (that == null) {
            return false;
        }
        final boolean this_present_lockid = true;
        final boolean that_present_lockid = true;
        if (this_present_lockid || that_present_lockid) {
            if (!this_present_lockid || !that_present_lockid) {
                return false;
            }
            if (this.lockid != that.lockid) {
                return false;
            }
        }
        final boolean this_present_dbname = this.isSetDbname();
        final boolean that_present_dbname = that.isSetDbname();
        if (this_present_dbname || that_present_dbname) {
            if (!this_present_dbname || !that_present_dbname) {
                return false;
            }
            if (!this.dbname.equals(that.dbname)) {
                return false;
            }
        }
        final boolean this_present_tablename = this.isSetTablename();
        final boolean that_present_tablename = that.isSetTablename();
        if (this_present_tablename || that_present_tablename) {
            if (!this_present_tablename || !that_present_tablename) {
                return false;
            }
            if (!this.tablename.equals(that.tablename)) {
                return false;
            }
        }
        final boolean this_present_partname = this.isSetPartname();
        final boolean that_present_partname = that.isSetPartname();
        if (this_present_partname || that_present_partname) {
            if (!this_present_partname || !that_present_partname) {
                return false;
            }
            if (!this.partname.equals(that.partname)) {
                return false;
            }
        }
        final boolean this_present_state = this.isSetState();
        final boolean that_present_state = that.isSetState();
        if (this_present_state || that_present_state) {
            if (!this_present_state || !that_present_state) {
                return false;
            }
            if (!this.state.equals(that.state)) {
                return false;
            }
        }
        final boolean this_present_type = this.isSetType();
        final boolean that_present_type = that.isSetType();
        if (this_present_type || that_present_type) {
            if (!this_present_type || !that_present_type) {
                return false;
            }
            if (!this.type.equals(that.type)) {
                return false;
            }
        }
        final boolean this_present_txnid = this.isSetTxnid();
        final boolean that_present_txnid = that.isSetTxnid();
        if (this_present_txnid || that_present_txnid) {
            if (!this_present_txnid || !that_present_txnid) {
                return false;
            }
            if (this.txnid != that.txnid) {
                return false;
            }
        }
        final boolean this_present_lastheartbeat = true;
        final boolean that_present_lastheartbeat = true;
        if (this_present_lastheartbeat || that_present_lastheartbeat) {
            if (!this_present_lastheartbeat || !that_present_lastheartbeat) {
                return false;
            }
            if (this.lastheartbeat != that.lastheartbeat) {
                return false;
            }
        }
        final boolean this_present_acquiredat = this.isSetAcquiredat();
        final boolean that_present_acquiredat = that.isSetAcquiredat();
        if (this_present_acquiredat || that_present_acquiredat) {
            if (!this_present_acquiredat || !that_present_acquiredat) {
                return false;
            }
            if (this.acquiredat != that.acquiredat) {
                return false;
            }
        }
        final boolean this_present_user = this.isSetUser();
        final boolean that_present_user = that.isSetUser();
        if (this_present_user || that_present_user) {
            if (!this_present_user || !that_present_user) {
                return false;
            }
            if (!this.user.equals(that.user)) {
                return false;
            }
        }
        final boolean this_present_hostname = this.isSetHostname();
        final boolean that_present_hostname = that.isSetHostname();
        if (this_present_hostname || that_present_hostname) {
            if (!this_present_hostname || !that_present_hostname) {
                return false;
            }
            if (!this.hostname.equals(that.hostname)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        final boolean present_lockid = true;
        builder.append(present_lockid);
        if (present_lockid) {
            builder.append(this.lockid);
        }
        final boolean present_dbname = this.isSetDbname();
        builder.append(present_dbname);
        if (present_dbname) {
            builder.append(this.dbname);
        }
        final boolean present_tablename = this.isSetTablename();
        builder.append(present_tablename);
        if (present_tablename) {
            builder.append(this.tablename);
        }
        final boolean present_partname = this.isSetPartname();
        builder.append(present_partname);
        if (present_partname) {
            builder.append(this.partname);
        }
        final boolean present_state = this.isSetState();
        builder.append(present_state);
        if (present_state) {
            builder.append(this.state.getValue());
        }
        final boolean present_type = this.isSetType();
        builder.append(present_type);
        if (present_type) {
            builder.append(this.type.getValue());
        }
        final boolean present_txnid = this.isSetTxnid();
        builder.append(present_txnid);
        if (present_txnid) {
            builder.append(this.txnid);
        }
        final boolean present_lastheartbeat = true;
        builder.append(present_lastheartbeat);
        if (present_lastheartbeat) {
            builder.append(this.lastheartbeat);
        }
        final boolean present_acquiredat = this.isSetAcquiredat();
        builder.append(present_acquiredat);
        if (present_acquiredat) {
            builder.append(this.acquiredat);
        }
        final boolean present_user = this.isSetUser();
        builder.append(present_user);
        if (present_user) {
            builder.append(this.user);
        }
        final boolean present_hostname = this.isSetHostname();
        builder.append(present_hostname);
        if (present_hostname) {
            builder.append(this.hostname);
        }
        return builder.toHashCode();
    }
    
    @Override
    public int compareTo(final ShowLocksResponseElement other) {
        if (!this.getClass().equals(other.getClass())) {
            return this.getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        final ShowLocksResponseElement typedOther = other;
        lastComparison = Boolean.valueOf(this.isSetLockid()).compareTo(Boolean.valueOf(typedOther.isSetLockid()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLockid()) {
            lastComparison = TBaseHelper.compareTo(this.lockid, typedOther.lockid);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetDbname()).compareTo(Boolean.valueOf(typedOther.isSetDbname()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetDbname()) {
            lastComparison = TBaseHelper.compareTo(this.dbname, typedOther.dbname);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTablename()).compareTo(Boolean.valueOf(typedOther.isSetTablename()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTablename()) {
            lastComparison = TBaseHelper.compareTo(this.tablename, typedOther.tablename);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetPartname()).compareTo(Boolean.valueOf(typedOther.isSetPartname()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetPartname()) {
            lastComparison = TBaseHelper.compareTo(this.partname, typedOther.partname);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetState()).compareTo(Boolean.valueOf(typedOther.isSetState()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetState()) {
            lastComparison = TBaseHelper.compareTo(this.state, typedOther.state);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetType()).compareTo(Boolean.valueOf(typedOther.isSetType()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetType()) {
            lastComparison = TBaseHelper.compareTo(this.type, typedOther.type);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetTxnid()).compareTo(Boolean.valueOf(typedOther.isSetTxnid()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetTxnid()) {
            lastComparison = TBaseHelper.compareTo(this.txnid, typedOther.txnid);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetLastheartbeat()).compareTo(Boolean.valueOf(typedOther.isSetLastheartbeat()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetLastheartbeat()) {
            lastComparison = TBaseHelper.compareTo(this.lastheartbeat, typedOther.lastheartbeat);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetAcquiredat()).compareTo(Boolean.valueOf(typedOther.isSetAcquiredat()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetAcquiredat()) {
            lastComparison = TBaseHelper.compareTo(this.acquiredat, typedOther.acquiredat);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetUser()).compareTo(Boolean.valueOf(typedOther.isSetUser()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetUser()) {
            lastComparison = TBaseHelper.compareTo(this.user, typedOther.user);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(this.isSetHostname()).compareTo(Boolean.valueOf(typedOther.isSetHostname()));
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (this.isSetHostname()) {
            lastComparison = TBaseHelper.compareTo(this.hostname, typedOther.hostname);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }
    
    @Override
    public _Fields fieldForId(final int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }
    
    @Override
    public void read(final TProtocol iprot) throws TException {
        ShowLocksResponseElement.schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }
    
    @Override
    public void write(final TProtocol oprot) throws TException {
        ShowLocksResponseElement.schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShowLocksResponseElement(");
        boolean first = true;
        sb.append("lockid:");
        sb.append(this.lockid);
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("dbname:");
        if (this.dbname == null) {
            sb.append("null");
        }
        else {
            sb.append(this.dbname);
        }
        first = false;
        if (this.isSetTablename()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("tablename:");
            if (this.tablename == null) {
                sb.append("null");
            }
            else {
                sb.append(this.tablename);
            }
            first = false;
        }
        if (this.isSetPartname()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("partname:");
            if (this.partname == null) {
                sb.append("null");
            }
            else {
                sb.append(this.partname);
            }
            first = false;
        }
        if (!first) {
            sb.append(", ");
        }
        sb.append("state:");
        if (this.state == null) {
            sb.append("null");
        }
        else {
            sb.append(this.state);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("type:");
        if (this.type == null) {
            sb.append("null");
        }
        else {
            sb.append(this.type);
        }
        first = false;
        if (this.isSetTxnid()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("txnid:");
            sb.append(this.txnid);
            first = false;
        }
        if (!first) {
            sb.append(", ");
        }
        sb.append("lastheartbeat:");
        sb.append(this.lastheartbeat);
        first = false;
        if (this.isSetAcquiredat()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("acquiredat:");
            sb.append(this.acquiredat);
            first = false;
        }
        if (!first) {
            sb.append(", ");
        }
        sb.append("user:");
        if (this.user == null) {
            sb.append("null");
        }
        else {
            sb.append(this.user);
        }
        first = false;
        if (!first) {
            sb.append(", ");
        }
        sb.append("hostname:");
        if (this.hostname == null) {
            sb.append("null");
        }
        else {
            sb.append(this.hostname);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }
    
    public void validate() throws TException {
        if (!this.isSetLockid()) {
            throw new TProtocolException("Required field 'lockid' is unset! Struct:" + this.toString());
        }
        if (!this.isSetDbname()) {
            throw new TProtocolException("Required field 'dbname' is unset! Struct:" + this.toString());
        }
        if (!this.isSetState()) {
            throw new TProtocolException("Required field 'state' is unset! Struct:" + this.toString());
        }
        if (!this.isSetType()) {
            throw new TProtocolException("Required field 'type' is unset! Struct:" + this.toString());
        }
        if (!this.isSetLastheartbeat()) {
            throw new TProtocolException("Required field 'lastheartbeat' is unset! Struct:" + this.toString());
        }
        if (!this.isSetUser()) {
            throw new TProtocolException("Required field 'user' is unset! Struct:" + this.toString());
        }
        if (!this.isSetHostname()) {
            throw new TProtocolException("Required field 'hostname' is unset! Struct:" + this.toString());
        }
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        try {
            this.write(new TCompactProtocol(new TIOStreamTransport(out)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            this.__isset_bitfield = 0;
            this.read(new TCompactProtocol(new TIOStreamTransport(in)));
        }
        catch (TException te) {
            throw new IOException(te);
        }
    }
    
    static {
        STRUCT_DESC = new TStruct("ShowLocksResponseElement");
        LOCKID_FIELD_DESC = new TField("lockid", (byte)10, (short)1);
        DBNAME_FIELD_DESC = new TField("dbname", (byte)11, (short)2);
        TABLENAME_FIELD_DESC = new TField("tablename", (byte)11, (short)3);
        PARTNAME_FIELD_DESC = new TField("partname", (byte)11, (short)4);
        STATE_FIELD_DESC = new TField("state", (byte)8, (short)5);
        TYPE_FIELD_DESC = new TField("type", (byte)8, (short)6);
        TXNID_FIELD_DESC = new TField("txnid", (byte)10, (short)7);
        LASTHEARTBEAT_FIELD_DESC = new TField("lastheartbeat", (byte)10, (short)8);
        ACQUIREDAT_FIELD_DESC = new TField("acquiredat", (byte)10, (short)9);
        USER_FIELD_DESC = new TField("user", (byte)11, (short)10);
        HOSTNAME_FIELD_DESC = new TField("hostname", (byte)11, (short)11);
        (schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>()).put(StandardScheme.class, new ShowLocksResponseElementStandardSchemeFactory());
        ShowLocksResponseElement.schemes.put(TupleScheme.class, new ShowLocksResponseElementTupleSchemeFactory());
        final Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.LOCKID, new FieldMetaData("lockid", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.DBNAME, new FieldMetaData("dbname", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.TABLENAME, new FieldMetaData("tablename", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.PARTNAME, new FieldMetaData("partname", (byte)2, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.STATE, new FieldMetaData("state", (byte)1, new EnumMetaData((byte)16, LockState.class)));
        tmpMap.put(_Fields.TYPE, new FieldMetaData("type", (byte)1, new EnumMetaData((byte)16, LockType.class)));
        tmpMap.put(_Fields.TXNID, new FieldMetaData("txnid", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.LASTHEARTBEAT, new FieldMetaData("lastheartbeat", (byte)1, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.ACQUIREDAT, new FieldMetaData("acquiredat", (byte)2, new FieldValueMetaData((byte)10)));
        tmpMap.put(_Fields.USER, new FieldMetaData("user", (byte)1, new FieldValueMetaData((byte)11)));
        tmpMap.put(_Fields.HOSTNAME, new FieldMetaData("hostname", (byte)1, new FieldValueMetaData((byte)11)));
        FieldMetaData.addStructMetaDataMap(ShowLocksResponseElement.class, metaDataMap = Collections.unmodifiableMap((Map<? extends _Fields, ? extends FieldMetaData>)tmpMap));
    }
    
    public enum _Fields implements TFieldIdEnum
    {
        LOCKID((short)1, "lockid"), 
        DBNAME((short)2, "dbname"), 
        TABLENAME((short)3, "tablename"), 
        PARTNAME((short)4, "partname"), 
        STATE((short)5, "state"), 
        TYPE((short)6, "type"), 
        TXNID((short)7, "txnid"), 
        LASTHEARTBEAT((short)8, "lastheartbeat"), 
        ACQUIREDAT((short)9, "acquiredat"), 
        USER((short)10, "user"), 
        HOSTNAME((short)11, "hostname");
        
        private static final Map<String, _Fields> byName;
        private final short _thriftId;
        private final String _fieldName;
        
        public static _Fields findByThriftId(final int fieldId) {
            switch (fieldId) {
                case 1: {
                    return _Fields.LOCKID;
                }
                case 2: {
                    return _Fields.DBNAME;
                }
                case 3: {
                    return _Fields.TABLENAME;
                }
                case 4: {
                    return _Fields.PARTNAME;
                }
                case 5: {
                    return _Fields.STATE;
                }
                case 6: {
                    return _Fields.TYPE;
                }
                case 7: {
                    return _Fields.TXNID;
                }
                case 8: {
                    return _Fields.LASTHEARTBEAT;
                }
                case 9: {
                    return _Fields.ACQUIREDAT;
                }
                case 10: {
                    return _Fields.USER;
                }
                case 11: {
                    return _Fields.HOSTNAME;
                }
                default: {
                    return null;
                }
            }
        }
        
        public static _Fields findByThriftIdOrThrow(final int fieldId) {
            final _Fields fields = findByThriftId(fieldId);
            if (fields == null) {
                throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            }
            return fields;
        }
        
        public static _Fields findByName(final String name) {
            return _Fields.byName.get(name);
        }
        
        private _Fields(final short thriftId, final String fieldName) {
            this._thriftId = thriftId;
            this._fieldName = fieldName;
        }
        
        @Override
        public short getThriftFieldId() {
            return this._thriftId;
        }
        
        @Override
        public String getFieldName() {
            return this._fieldName;
        }
        
        static {
            byName = new HashMap<String, _Fields>();
            for (final _Fields field : EnumSet.allOf(_Fields.class)) {
                _Fields.byName.put(field.getFieldName(), field);
            }
        }
    }
    
    private static class ShowLocksResponseElementStandardSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowLocksResponseElementStandardScheme getScheme() {
            return new ShowLocksResponseElementStandardScheme();
        }
    }
    
    private static class ShowLocksResponseElementStandardScheme extends StandardScheme<ShowLocksResponseElement>
    {
        @Override
        public void read(final TProtocol iprot, final ShowLocksResponseElement struct) throws TException {
            iprot.readStructBegin();
            while (true) {
                final TField schemeField = iprot.readFieldBegin();
                if (schemeField.type == 0) {
                    break;
                }
                switch (schemeField.id) {
                    case 1: {
                        if (schemeField.type == 10) {
                            struct.lockid = iprot.readI64();
                            struct.setLockidIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 2: {
                        if (schemeField.type == 11) {
                            struct.dbname = iprot.readString();
                            struct.setDbnameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 3: {
                        if (schemeField.type == 11) {
                            struct.tablename = iprot.readString();
                            struct.setTablenameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 4: {
                        if (schemeField.type == 11) {
                            struct.partname = iprot.readString();
                            struct.setPartnameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 5: {
                        if (schemeField.type == 8) {
                            struct.state = LockState.findByValue(iprot.readI32());
                            struct.setStateIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 6: {
                        if (schemeField.type == 8) {
                            struct.type = LockType.findByValue(iprot.readI32());
                            struct.setTypeIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 7: {
                        if (schemeField.type == 10) {
                            struct.txnid = iprot.readI64();
                            struct.setTxnidIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 8: {
                        if (schemeField.type == 10) {
                            struct.lastheartbeat = iprot.readI64();
                            struct.setLastheartbeatIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 9: {
                        if (schemeField.type == 10) {
                            struct.acquiredat = iprot.readI64();
                            struct.setAcquiredatIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 10: {
                        if (schemeField.type == 11) {
                            struct.user = iprot.readString();
                            struct.setUserIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    case 11: {
                        if (schemeField.type == 11) {
                            struct.hostname = iprot.readString();
                            struct.setHostnameIsSet(true);
                            break;
                        }
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                    default: {
                        TProtocolUtil.skip(iprot, schemeField.type);
                        break;
                    }
                }
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();
            struct.validate();
        }
        
        @Override
        public void write(final TProtocol oprot, final ShowLocksResponseElement struct) throws TException {
            struct.validate();
            oprot.writeStructBegin(ShowLocksResponseElement.STRUCT_DESC);
            oprot.writeFieldBegin(ShowLocksResponseElement.LOCKID_FIELD_DESC);
            oprot.writeI64(struct.lockid);
            oprot.writeFieldEnd();
            if (struct.dbname != null) {
                oprot.writeFieldBegin(ShowLocksResponseElement.DBNAME_FIELD_DESC);
                oprot.writeString(struct.dbname);
                oprot.writeFieldEnd();
            }
            if (struct.tablename != null && struct.isSetTablename()) {
                oprot.writeFieldBegin(ShowLocksResponseElement.TABLENAME_FIELD_DESC);
                oprot.writeString(struct.tablename);
                oprot.writeFieldEnd();
            }
            if (struct.partname != null && struct.isSetPartname()) {
                oprot.writeFieldBegin(ShowLocksResponseElement.PARTNAME_FIELD_DESC);
                oprot.writeString(struct.partname);
                oprot.writeFieldEnd();
            }
            if (struct.state != null) {
                oprot.writeFieldBegin(ShowLocksResponseElement.STATE_FIELD_DESC);
                oprot.writeI32(struct.state.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.type != null) {
                oprot.writeFieldBegin(ShowLocksResponseElement.TYPE_FIELD_DESC);
                oprot.writeI32(struct.type.getValue());
                oprot.writeFieldEnd();
            }
            if (struct.isSetTxnid()) {
                oprot.writeFieldBegin(ShowLocksResponseElement.TXNID_FIELD_DESC);
                oprot.writeI64(struct.txnid);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldBegin(ShowLocksResponseElement.LASTHEARTBEAT_FIELD_DESC);
            oprot.writeI64(struct.lastheartbeat);
            oprot.writeFieldEnd();
            if (struct.isSetAcquiredat()) {
                oprot.writeFieldBegin(ShowLocksResponseElement.ACQUIREDAT_FIELD_DESC);
                oprot.writeI64(struct.acquiredat);
                oprot.writeFieldEnd();
            }
            if (struct.user != null) {
                oprot.writeFieldBegin(ShowLocksResponseElement.USER_FIELD_DESC);
                oprot.writeString(struct.user);
                oprot.writeFieldEnd();
            }
            if (struct.hostname != null) {
                oprot.writeFieldBegin(ShowLocksResponseElement.HOSTNAME_FIELD_DESC);
                oprot.writeString(struct.hostname);
                oprot.writeFieldEnd();
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }
    
    private static class ShowLocksResponseElementTupleSchemeFactory implements SchemeFactory
    {
        @Override
        public ShowLocksResponseElementTupleScheme getScheme() {
            return new ShowLocksResponseElementTupleScheme();
        }
    }
    
    private static class ShowLocksResponseElementTupleScheme extends TupleScheme<ShowLocksResponseElement>
    {
        @Override
        public void write(final TProtocol prot, final ShowLocksResponseElement struct) throws TException {
            final TTupleProtocol oprot = (TTupleProtocol)prot;
            oprot.writeI64(struct.lockid);
            oprot.writeString(struct.dbname);
            oprot.writeI32(struct.state.getValue());
            oprot.writeI32(struct.type.getValue());
            oprot.writeI64(struct.lastheartbeat);
            oprot.writeString(struct.user);
            oprot.writeString(struct.hostname);
            final BitSet optionals = new BitSet();
            if (struct.isSetTablename()) {
                optionals.set(0);
            }
            if (struct.isSetPartname()) {
                optionals.set(1);
            }
            if (struct.isSetTxnid()) {
                optionals.set(2);
            }
            if (struct.isSetAcquiredat()) {
                optionals.set(3);
            }
            oprot.writeBitSet(optionals, 4);
            if (struct.isSetTablename()) {
                oprot.writeString(struct.tablename);
            }
            if (struct.isSetPartname()) {
                oprot.writeString(struct.partname);
            }
            if (struct.isSetTxnid()) {
                oprot.writeI64(struct.txnid);
            }
            if (struct.isSetAcquiredat()) {
                oprot.writeI64(struct.acquiredat);
            }
        }
        
        @Override
        public void read(final TProtocol prot, final ShowLocksResponseElement struct) throws TException {
            final TTupleProtocol iprot = (TTupleProtocol)prot;
            struct.lockid = iprot.readI64();
            struct.setLockidIsSet(true);
            struct.dbname = iprot.readString();
            struct.setDbnameIsSet(true);
            struct.state = LockState.findByValue(iprot.readI32());
            struct.setStateIsSet(true);
            struct.type = LockType.findByValue(iprot.readI32());
            struct.setTypeIsSet(true);
            struct.lastheartbeat = iprot.readI64();
            struct.setLastheartbeatIsSet(true);
            struct.user = iprot.readString();
            struct.setUserIsSet(true);
            struct.hostname = iprot.readString();
            struct.setHostnameIsSet(true);
            final BitSet incoming = iprot.readBitSet(4);
            if (incoming.get(0)) {
                struct.tablename = iprot.readString();
                struct.setTablenameIsSet(true);
            }
            if (incoming.get(1)) {
                struct.partname = iprot.readString();
                struct.setPartnameIsSet(true);
            }
            if (incoming.get(2)) {
                struct.txnid = iprot.readI64();
                struct.setTxnidIsSet(true);
            }
            if (incoming.get(3)) {
                struct.acquiredat = iprot.readI64();
                struct.setAcquiredatIsSet(true);
            }
        }
    }
}
