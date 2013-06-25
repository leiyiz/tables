package org.opendatakit.tables.sync;

import org.opendatakit.tables.data.SyncState;

/**
 * The mapping of a table to the status of its synchronization.
 * @author sudar.sam@gmail.com
 *
 */
public class TableResult {
  
  private String mDisplayName;
  private String mTableId;
  private Status mStatus;
  private String mMessage;
  /** Flag if properties were pulled from the server. */
  private boolean mPulledServerProps;
  /** Flag if data was pulled from the server. */
  private boolean mPulledServerData;
  /** Flag if properties were pushed to the server. */
  private boolean mPushedLocalProps;
  /** Flag if data was pushed to the server. */
  private boolean mPushedLocalData;
  /** Flag if properties had to be pushed to the server. */
  private boolean mHadLocalPropChanges;
  /** Flag if local data had to be pushed to the server. */
  private boolean mHadLocalDataChanges;
  /** Flag if properties had to be pulled from the server. */
  private boolean mHadServerPropChanges;
  /** Flat if data had to be pulled from the server. */
  private boolean mHadServerDataChanges;
  /** 
   * The state which the table was in when we synched. This matters for 
   * situations like deleting a table. In this case we might have had data or
   * properties to pull from the server, but it wouldn't matter because we're
   * deleting it. None of our regular flags apply.
   */
  private SyncState mSyncState;
  
  /**
   * Create a table result with a status of {@link Status#FAILURE}. This should
   * then only be updated in the case of success or exceptions. The boolean 
   * flags are initialized to false;
   * @param dbTableName
   * @param status
   */
  public TableResult(String tableDisplayName, String tableId) {
    this.mDisplayName = tableDisplayName;
    this.mStatus = Status.FAILURE;
    this.mMessage = Status.FAILURE.name();
    this.mPulledServerData = false;
    this.mPulledServerProps = false;
    this.mPushedLocalData = false;
    this.mPushedLocalData = false;
    this.mHadLocalDataChanges = false;
    this.mHadLocalPropChanges = false;
    this.mHadServerDataChanges = false;
    this.mHadServerDataChanges = false;
    this.mSyncState = null;
  }
  
  public String getTableDisplayName() {
    return this.mDisplayName;
  }
  
  public Status getStatus() {
    return this.mStatus;
  }
  
  /**
   * Get the action at the time of sync. This matters for 
   * situations like deleting a table. In this case we might have had data or
   * properties to pull from the server, but it wouldn't matter because we're
   * deleting it. None of our regular flags apply.
   * @return
   */
  public SyncState getTableAction() {
    return this.mSyncState;
  }
  
  /**
   * Set the sync state at the time of the sync.
   * @param state
   */
  public void setTableAction(SyncState state) {
    this.mSyncState = state;
  }
  
  public boolean pulledServerData() {
    return this.mPulledServerData;
  }
  
  public boolean pulledServerProperties() {
    return this.mPulledServerProps;
  }
  
  public boolean pushedLocalProperties() {
    return this.mPushedLocalProps;
  }
  
  public boolean pushedLocalData() {
    return this.mPushedLocalData;
  }
  
  public boolean hadLocalDataChanges() {
    return this.mHadLocalDataChanges;
  }
  
  public boolean hadLocalPropertiesChanges() {
    return this.mHadLocalPropChanges;
  }
  
  public boolean serverHadDataChanges() {
    return this.mHadServerDataChanges;
  }
  
  public boolean serverHadPropertiesChanges() {
    return this.mHadServerPropChanges;
  }
  
  public void setPulledServerData(boolean pulledData) {
    this.mPulledServerData = pulledData;
  }
  
  public void setPulledServerProperties(boolean pulledProperties) {
    this.mPulledServerProps = pulledProperties;
  }
  
  public void setPushedLocalProperties(boolean pushedProperties) {
    this.mPushedLocalProps = pushedProperties;
  }
  
  public void setPushedLocalData(boolean pushedData) {
    this.mPushedLocalData = pushedData;
  }
  
  public void setHadLocalPropertiesChanges(boolean hadChanges) {
    this.mHadLocalPropChanges = hadChanges;
  }
  
  public void setHadLocalDataChanges(boolean hadChanges) {
    this.mHadLocalDataChanges = hadChanges;
  }
  
  public void setServerHadPropertiesChanges(boolean serverHadChanges) {
    this.mHadServerPropChanges = serverHadChanges;
  }
  
  public void setServerHadDataChanges(boolean serverHadChanges) {
    this.mHadServerDataChanges = serverHadChanges;
  }
  
  /**
   * Set a message that might be passed back to the user. Likely a place
   * to pass the error message back to the user in case of exceptions.
   * @param message
   */
  public void setMessage(String message) {
    this.mMessage = message;
  }
  
  public String getMessage() {
    return this.mMessage;
  }
  
  /**
   * Update the status of this result. 
   * @param newStatus
   * @throws UnsupportedOperationException if the satus has been set to 
   * {@link Status#EXCEPTION} and the newStatus is something other than
   * {@link Status#EXCEPTION}.
   */
  public void setStatus(Status newStatus) {
    if (this.mStatus == Status.EXCEPTION && newStatus != Status.EXCEPTION) {
      throw new UnsupportedOperationException("Tried to set TableResult " +
      		"status" +
      		" to something other than exception when it had alread been set" +
      		" to exception.");
    }
    this.mStatus = newStatus;
  }
  
  /**
   * The result of an individual table. 
   * @author sudar.sam@gmail.com
   *
   */
  public enum Status {
    SUCCESS, FAILURE, EXCEPTION;
  }
  
}