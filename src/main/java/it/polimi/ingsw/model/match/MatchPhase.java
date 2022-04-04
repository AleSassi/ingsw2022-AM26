package it.polimi.ingsw.model.match;

public enum MatchPhase {
    PlanPhaseStepOne,
    PlanPhaseStepTwo,
    ActionPhaseStepOne,
    ActionPhaseStepTwo,
    ActionPhaseStepThree;

    MatchPhase matchPhase;

    public MatchPhase nextPhase() {
        switch (matchPhase) {
            case PlanPhaseStepOne -> matchPhase = PlanPhaseStepTwo;
            case PlanPhaseStepTwo -> matchPhase = ActionPhaseStepOne;
            case ActionPhaseStepOne -> matchPhase = ActionPhaseStepTwo;
            case ActionPhaseStepTwo -> matchPhase = ActionPhaseStepThree;
            case ActionPhaseStepThree -> matchPhase = PlanPhaseStepOne;

        }
        return matchPhase;
    }
}
