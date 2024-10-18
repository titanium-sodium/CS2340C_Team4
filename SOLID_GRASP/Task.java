import java.util.Date;

interface TaskActions {
    void execute();
    String getTitle();
    String getStatus();
    void setStatus(String newStatus);
}

abstract class Task implements TaskActions{
    private String title;
    private String description;
    private Date dueDate;
    private String status;
    private String priority;

    public Task (String title, String description, Date dueDate, String status, String priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }
    @Override
    public void execute(){
        //do something
    }
    @Override
    public String getTitle() {
        return this.title;
    }
    @Override
    public String getStatus() {
        return this.status;
    }
    @Override
    public void setStatus(String newStatus) {
        this.status = newStatus;
    }
}
class RecurringTask extends Task {
    private String reoccuringSchedule;

    public RecurringTask (String title, String description, Date dueDate, String status, String priority, String reoccuringSchedule) {
        super(title, description, dueDate, status, priority);
        this.reoccuringSchedule = reoccuringSchedule;
    }

    @Override
    public void execute() {
        super.execute();
    }
}
class PriorityTask extends Task {
    public PriorityTask (String title, String description, Date dueDate, String status, String priority) {
        super(title, description, dueDate, status, "HIGH");
    }

    @Override
    public void execute() {
        super.execute();
    }
}