package com.example.psp;

import java.io.Serializable;
import java.util.Date;

public class LearningUnit implements Serializable{
    private final int learningUnitId;
    private final Date creationDate;
    private String learningUnitTitle;

    // Der geplante Leraufwand wird in Minuten gespeichert. Sekunden sind zu fein für die Anforderung, und dadurch reicht auch int aus.
    private int plannedLearningEffort;

    public static int calculateLearningEffort(int hours, int minutes) {
        return (hours * 60) + minutes;
    }

    public static int calculateLearningEffortHours(long learningEffortTime) {
        return (int) learningEffortTime / 60;
    }

    public static int calculateLearningEffortMinutes(long learningEffortTime) {
        return (int) learningEffortTime % 60;
    }

    public LearningUnit(int learningUnitId, Date creationDate, String learningUnitTitle, int plannedLearningEffort) {
        this.learningUnitId = learningUnitId;
        this.creationDate = creationDate;
        this.learningUnitTitle = learningUnitTitle;
        this.plannedLearningEffort = plannedLearningEffort;
    }

    public int getLearningUnitId() {
        return learningUnitId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getLearningUnitTitle() {
        return learningUnitTitle;
    }

    public void setLearningUnitTitle(String learningUnitTitle) {
        this.learningUnitTitle = learningUnitTitle;
    }
    public int getPlannedLearningEffort() {
        return plannedLearningEffort;
    }
    public int getPlannedLearningEffortHours() {
        return calculateLearningEffortHours(plannedLearningEffort);
    }

    public int getPlannedLearningEffortMinutes() {
        return calculateLearningEffortMinutes(plannedLearningEffort);
    }

    public void setPlannedLearningEffort(int plannedLearningEffortHours, int plannedLearningEffortMinutes) {
        this.plannedLearningEffort = calculateLearningEffort(plannedLearningEffortHours, plannedLearningEffortMinutes);
    }

}
