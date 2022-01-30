import java.util.Objects;

public class Task {
    private final String title;
    private final String description;
    private final Status status;
    private final int id;

    public Task(String title, String description, Status status, int id) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String title, String description, int id) {
        this(title, description, Status.NEW, id);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getId() == task.getId()
                && Objects.equals(getTitle(), task.getTitle())
                && Objects.equals(getDescription(), task.getDescription())
                && getStatus() == task.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getStatus(), getId());
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}
