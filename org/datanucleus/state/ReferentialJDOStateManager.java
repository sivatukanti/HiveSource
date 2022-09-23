// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.util.ClassUtils;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;
import java.util.Map;
import java.util.List;

public class ReferentialJDOStateManager extends JDOStateManager
{
    private List<ReferentialJDOStateManager> insertionNotifyList;
    private Map<ReferentialJDOStateManager, FieldContainer> fieldsToBeUpdatedAfterObjectInsertion;
    
    public ReferentialJDOStateManager(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        super(ec, cmd);
        this.insertionNotifyList = null;
        this.fieldsToBeUpdatedAfterObjectInsertion = null;
    }
    
    @Override
    public void connect(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        super.connect(ec, cmd);
        this.fieldsToBeUpdatedAfterObjectInsertion = null;
        this.insertionNotifyList = null;
    }
    
    @Override
    public void disconnect() {
        this.fieldsToBeUpdatedAfterObjectInsertion = null;
        this.insertionNotifyList = null;
        super.disconnect();
    }
    
    @Override
    public void changeActivityState(final ActivityState activityState) {
        this.activity = activityState;
        if (activityState == ActivityState.INSERTING_CALLBACKS && this.insertionNotifyList != null) {
            synchronized (this.insertionNotifyList) {
                for (final ReferentialJDOStateManager notifySM : this.insertionNotifyList) {
                    notifySM.insertionCompleted(this);
                }
            }
            this.insertionNotifyList.clear();
            this.insertionNotifyList = null;
        }
    }
    
    @Override
    public void updateFieldAfterInsert(final Object pc, final int fieldNumber) {
        final ReferentialJDOStateManager otherSM = (ReferentialJDOStateManager)this.myEC.findObjectProvider(pc);
        if (otherSM.insertionNotifyList == null) {
            otherSM.insertionNotifyList = Collections.synchronizedList(new ArrayList<ReferentialJDOStateManager>(1));
        }
        otherSM.insertionNotifyList.add(this);
        if (this.fieldsToBeUpdatedAfterObjectInsertion == null) {
            this.fieldsToBeUpdatedAfterObjectInsertion = new HashMap<ReferentialJDOStateManager, FieldContainer>(1);
        }
        FieldContainer cont = this.fieldsToBeUpdatedAfterObjectInsertion.get(otherSM);
        if (cont == null) {
            cont = new FieldContainer(fieldNumber);
        }
        else {
            cont.set(fieldNumber);
        }
        this.fieldsToBeUpdatedAfterObjectInsertion.put(otherSM, cont);
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ReferentialJDOStateManager.LOCALISER.msg("026021", this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber).getFullFieldName(), StringUtils.toJVMIDString(this.myPC), StringUtils.toJVMIDString(pc)));
        }
    }
    
    void insertionCompleted(final ReferentialJDOStateManager op) {
        if (this.fieldsToBeUpdatedAfterObjectInsertion == null) {
            return;
        }
        final FieldContainer fldCont = this.fieldsToBeUpdatedAfterObjectInsertion.get(op);
        if (fldCont != null) {
            this.dirty = true;
            final int[] fieldsToUpdate = fldCont.getFields();
            for (int i = 0; i < fieldsToUpdate.length; ++i) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ReferentialJDOStateManager.LOCALISER.msg("026022", this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldsToUpdate[i]).getFullFieldName(), IdentityUtils.getIdentityAsString(this.myEC.getApiAdapter(), this.myID), StringUtils.toJVMIDString(op.getObject())));
                }
                this.dirtyFields[fieldsToUpdate[i]] = true;
            }
            this.fieldsToBeUpdatedAfterObjectInsertion.remove(op);
            if (this.fieldsToBeUpdatedAfterObjectInsertion.isEmpty()) {
                this.fieldsToBeUpdatedAfterObjectInsertion = null;
            }
            try {
                this.flags |= 0x4000;
                this.flush();
            }
            finally {
                this.flags &= 0xFFFFBFFF;
            }
        }
    }
    
    private class FieldContainer
    {
        boolean[] fieldsToUpdate;
        
        public FieldContainer(final int fieldNumber) {
            (this.fieldsToUpdate = new boolean[ReferentialJDOStateManager.this.cmd.getAllMemberPositions().length])[fieldNumber] = true;
        }
        
        public void set(final int fieldNumber) {
            this.fieldsToUpdate[fieldNumber] = true;
        }
        
        public int[] getFields() {
            return ClassUtils.getFlagsSetTo(this.fieldsToUpdate, true);
        }
    }
}
