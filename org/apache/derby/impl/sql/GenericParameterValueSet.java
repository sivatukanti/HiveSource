// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.impl.jdbc.Util;
import org.apache.derby.iapi.types.UserDataValue;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.sql.ParameterValueSet;

final class GenericParameterValueSet implements ParameterValueSet
{
    private final GenericParameter[] parms;
    final ClassInspector ci;
    private final boolean hasReturnOutputParam;
    
    GenericParameterValueSet(final ClassInspector ci, final int n, final boolean hasReturnOutputParam) {
        this.ci = ci;
        this.hasReturnOutputParam = hasReturnOutputParam;
        this.parms = new GenericParameter[n];
        for (int i = 0; i < n; ++i) {
            this.parms[i] = new GenericParameter(this, hasReturnOutputParam && i == 0);
        }
    }
    
    private GenericParameterValueSet(final int n, final GenericParameterValueSet set) {
        this.hasReturnOutputParam = set.hasReturnOutputParam;
        this.ci = set.ci;
        this.parms = new GenericParameter[n];
        for (int i = 0; i < n; ++i) {
            this.parms[i] = set.getGenericParameter(i).getClone(this);
        }
    }
    
    public void initialize(final DataTypeDescriptor[] array) throws StandardException {
        for (int i = 0; i < this.parms.length; ++i) {
            final DataTypeDescriptor dataTypeDescriptor = array[i];
            this.parms[i].initialize(dataTypeDescriptor.getNull(), dataTypeDescriptor.getJDBCTypeId(), dataTypeDescriptor.getTypeId().getCorrespondingJavaTypeName());
        }
    }
    
    public void setParameterMode(final int n, final int n2) {
        this.parms[n].parameterMode = (short)n2;
    }
    
    public void clearParameters() {
        for (int i = 0; i < this.parms.length; ++i) {
            this.parms[i].clear();
        }
    }
    
    public int getParameterCount() {
        return this.parms.length;
    }
    
    public DataValueDescriptor getParameter(final int n) throws StandardException {
        try {
            return this.parms[n].getValue();
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            this.checkPosition(n);
            return null;
        }
    }
    
    public DataValueDescriptor getParameterForSet(final int n) throws StandardException {
        try {
            final GenericParameter genericParameter = this.parms[n];
            if (genericParameter.parameterMode == 4) {
                throw StandardException.newException("XCL27.S");
            }
            genericParameter.isSet = true;
            return genericParameter.getValue();
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            this.checkPosition(n);
            return null;
        }
    }
    
    public DataValueDescriptor getParameterForGet(final int n) throws StandardException {
        try {
            final GenericParameter genericParameter = this.parms[n];
            switch (genericParameter.parameterMode) {
                case 0:
                case 1: {
                    throw StandardException.newException("XCL26.S", Integer.toString(n + 1));
                }
                default: {
                    return genericParameter.getValue();
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            this.checkPosition(n);
            return null;
        }
    }
    
    public void setParameterAsObject(final int n, final Object value) throws StandardException {
        final UserDataValue userDataValue = (UserDataValue)this.getParameterForSet(n);
        final GenericParameter genericParameter = this.parms[n];
        if (value != null) {
            Throwable t = null;
            boolean b;
            try {
                b = !this.ci.instanceOf(genericParameter.declaredClassName, value);
            }
            catch (ClassNotFoundException ex) {
                t = ex;
                b = true;
            }
            if (b) {
                throw StandardException.newException("XCL12.S", t, ClassInspector.readableClassName(value.getClass()), genericParameter.declaredClassName);
            }
        }
        userDataValue.setValue(value);
    }
    
    public boolean allAreSet() {
        for (int i = 0; i < this.parms.length; ++i) {
            final GenericParameter genericParameter = this.parms[i];
            if (!genericParameter.isSet) {
                switch (genericParameter.parameterMode) {
                    case 0:
                    case 1:
                    case 2: {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public void transferDataValues(final ParameterValueSet set) throws StandardException {
        for (int i = set.hasReturnOutputParameter() ? 1 : 0; i < this.parms.length; ++i) {
            final GenericParameter genericParameter = this.parms[i];
            if (genericParameter.registerOutType != 0) {
                set.registerOutParameter(i, genericParameter.registerOutType, genericParameter.registerOutScale);
            }
            if (genericParameter.isSet) {
                final DataValueDescriptor value = genericParameter.getValue();
                if (value.hasStream()) {
                    set.getParameterForSet(i).setValue(value.getStream(), -1);
                }
                else {
                    set.getParameterForSet(i).setValue(value);
                }
            }
        }
    }
    
    GenericParameter getGenericParameter(final int n) {
        return this.parms[n];
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.parms.length; ++i) {
            sb.append("begin parameter #" + (i + 1) + ": ");
            sb.append(this.parms[i].toString());
            sb.append(" :end parameter ");
        }
        return sb.toString();
    }
    
    private void checkPosition(final int n) throws StandardException {
        if (n >= 0 && n < this.parms.length) {
            return;
        }
        if (this.parms.length == 0) {
            throw StandardException.newException("07009");
        }
        throw StandardException.newException("XCL13.S", String.valueOf(n + 1), String.valueOf(this.parms.length));
    }
    
    public ParameterValueSet getClone() {
        return new GenericParameterValueSet(this.parms.length, this);
    }
    
    public void registerOutParameter(final int n, final int n2, final int n3) throws StandardException {
        this.checkPosition(n);
        Util.checkSupportedRaiseStandard(n2);
        this.parms[n].setOutParameter(n2, n3);
    }
    
    public void validate() throws StandardException {
        for (int i = 0; i < this.parms.length; ++i) {
            this.parms[i].validate();
        }
    }
    
    public int getParameterNumber(final GenericParameter genericParameter) {
        for (int i = 0; i < this.parms.length; ++i) {
            if (this.parms[i] == genericParameter) {
                return i + 1;
            }
        }
        return 0;
    }
    
    public boolean checkNoDeclaredOutputParameters() {
        boolean b = false;
        for (int i = 0; i < this.parms.length; ++i) {
            final GenericParameter genericParameter = this.parms[i];
            switch (genericParameter.parameterMode) {
                case 2:
                case 4: {
                    b = true;
                    break;
                }
                case 0: {
                    genericParameter.parameterMode = 1;
                    break;
                }
            }
        }
        return b;
    }
    
    public short getParameterMode(final int n) {
        return this.parms[n - 1].parameterMode;
    }
    
    public boolean hasReturnOutputParameter() {
        return this.hasReturnOutputParam;
    }
    
    public DataValueDescriptor getReturnValueForSet() throws StandardException {
        this.checkPosition(0);
        return this.parms[0].getValue();
    }
    
    public int getScale(final int n) {
        return this.parms[n - 1].getScale();
    }
    
    public int getPrecision(final int n) {
        return this.parms[n - 1].getPrecision();
    }
}
