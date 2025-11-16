package smarttime.model;

import java.time.LocalDate;

public class Task {
    private final int id;
    private String title;
    private String course;
    private LocalDate dueDate;
    private int estimatedMinutes;
    private int difficulty; // 1–5
    private TaskStatus status;

    public Task(int id, String title, String course,
                LocalDate dueDate, int estimatedMinutes, int difficulty) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.dueDate = dueDate;
        this.estimatedMinutes = estimatedMinutes;
        this.difficulty = difficulty;
        this.status = TaskStatus.PLANNED;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCourse() { return course; }
    public LocalDate getDueDate() { return dueDate; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public int getDifficulty() { return difficulty; }
    public TaskStatus getStatus() { return status; }

    public void setTitle(String title) { this.title = title; }
    public void setCourse(String course) { this.course = course; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setStatus(TaskStatus status) { this.status = status; }

    @Override
    public String toString() {
        return title + " (" + course + ") – due " + dueDate;
    }
}