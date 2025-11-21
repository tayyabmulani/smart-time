package smarttime.model;

/**
 * Represents a single undoable action in the SmartTime app.
 *
 * Supports:
 *  - ADD_TASK: undo by removing the added task
 *  - UPDATE_STATUS: undo by restoring the previous TaskStatus
 *  - UPDATE_TASK_DETAILS: undo by restoring a full Task snapshot
 *  - DELETE_TASK: undo by restoring the deleted task
 */
public class UndoAction {

    public enum ActionType {
        ADD_TASK,
        UPDATE_STATUS,
        UPDATE_TASK_DETAILS,
        DELETE_TASK
    }

    private final ActionType type;
    private final Task task;                 // the affected task
    private final TaskStatus previousStatus; // only for UPDATE_STATUS
    private final Task snapshot;             // only for UPDATE_TASK_DETAILS

    // Constructor for ADD_TASK and DELETE_TASK
    public UndoAction(ActionType type, Task task) {
        this.type = type;
        this.task = task;
        this.previousStatus = null;
        this.snapshot = null;
    }

    // Constructor for UPDATE_STATUS
    public UndoAction(ActionType type, Task task, TaskStatus previousStatus) {
        this.type = type;
        this.task = task;
        this.previousStatus = previousStatus;
        this.snapshot = null;
    }

    // Constructor for UPDATE_TASK_DETAILS with snapshot
    public UndoAction(ActionType type, Task task, Task snapshotTask) {
        this.type = type;
        this.task = task;
        this.snapshot = snapshotTask;
        this.previousStatus = null;
    }

    public ActionType getType() {
        return type;
    }

    public Task getTask() {
        return task;
    }

    public TaskStatus getPreviousStatus() {
        return previousStatus;
    }

    public Task getSnapshot() {
        return snapshot;
    }
}