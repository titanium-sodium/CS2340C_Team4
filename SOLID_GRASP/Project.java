import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.lang.*;

interface ProjectActions {
    void addTeamMember(TeamMember newMember);
    void removeTeamMember(TeamMember removeMember);
    void addTask(Task newTask);
    void removeTask(Task removeTask);
}

public class Project implements  ProjectActions {
    private List<Task> tasks;
    private List<TeamMember> teamMembers;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;

    public Project(String name, String description, Date startDate, Date endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tasks = new ArrayList<>();
        this.teamMembers = new ArrayList<>();
    }

    @Override
    public void addTeamMember(TeamMember newMember) {
        this.teamMembers.add(newMember);
    }

    @Override
    public void removeTeamMember(TeamMember removeMember) {
        this.teamMembers.remove(removeMember);
    }

    @Override
    public void removeTask(Task removeTask) {
        this.tasks.remove(removeTask);
    }

    public void addTask(Task newTask)  {
        this.tasks.add(newTask);
    }
}