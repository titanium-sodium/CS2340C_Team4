class TeamMember {
    private String name;
    private String email;
    private String role;

    public TeamMember (String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public void joinProject(Project project) {
        project.addTeamMember(this);
    }

    public void leaveProject(Project project) {
        project.removeTeamMember(this);
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}

class ProjectManager extends TeamMember {
    public ProjectManager(String name, String email) {
        super(name, email, "Project Manager");
    }

    public void overseeProject(Project project) {
        //oversee logic
    }
}