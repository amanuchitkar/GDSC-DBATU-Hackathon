package com.example.psp;

import java.io.Serializable;
import java.util.Date;

public class LearningEffort implements Serializable {

    private final int learningEffortId;
    private final Date creationDate;
    private Date learningEffortDate;
    // Der geplante Leraufwand wird in Minuten gespeichert. Sekunden sind zu fein für die Anforderung, und dadurch reicht auch int aus.
    private int actualLearningEffort;

    public LearningEffort(int learningEffortId, Date creationDate, Date learningEffortDate, int actualLearningEffort) {
        this.learningEffortId = learningEffortId;
        this.creationDate = creationDate;
        this.learningEffortDate = learningEffortDate;
        this.actualLearningEffort = actualLearningEffort;
    }

    public int getLearningEffortId() {
        return learningEffortId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getActualLearningEffort() {
        return actualLearningEffort;
    }

    public Date getLearningEffortDate() {
        return learningEffortDate;
    }

    public void setLearningEffortDate(Date learningEffortDate) {
        this.learningEffortDate = learningEffortDate;
    }

    public int getActualLearningEffortHours() {
        return com.example.psp.LearningUnit.calculateLearningEffortHours(actualLearningEffort);
    }

    public int getActualLearningEffortMinutes() {
        return com.example.psp.LearningUnit.calculateLearningEffortMinutes(actualLearningEffort);
    }

    public void setActualLearningEffort(int actualLearningEffortHours, int actualLearningEffortMinutes) {
        this.actualLearningEffort = com.example.psp.LearningUnit.calculateLearningEffort(actualLearningEffortHours, actualLearningEffortMinutes);
    }
}
