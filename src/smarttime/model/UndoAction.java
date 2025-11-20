package smarttime.model;

/**
 * Represents a single undoable action in the SmartTime app.
 * 
 * For now we support:
 *  - ADD_TASK: undo by removing the added task
 *  - UPDATE_STATUS: undo by restoring the previous TaskStatus
 *
 * Extend this later (DELETE_TASK, EDIT_TASK, etc.).
 */
public class UndoAction {

    public enum ActionType {
        ADD_TASK,
        UPDATE_STATUS
    }

    private final ActionType type;
    private final Task task;
    private final TaskStatus previousStatus; // only used for UPDATE_STATUS

    public UndoAction(ActionType type, Task task, TaskStatus previousStatus) {
        this.type = type;
        this.task = task;
        this.previousStatus = previousStatus;
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
}