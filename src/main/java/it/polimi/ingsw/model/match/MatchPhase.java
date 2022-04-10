package it.polimi.ingsw.model.match;

public enum MatchPhase {
    PlanPhaseStepOne,
    PlanPhaseStepTwo,
    ActionPhaseStepOne,
    ActionPhaseStepTwo,
    ActionPhaseStepThree;

    public MatchPhase nextPhase() {
        switch (this) {
            case PlanPhaseStepOne, ActionPhaseStepThree -> {
                return PlanPhaseStepTwo;
            }
            case PlanPhaseStepTwo -> {
                return ActionPhaseStepOne;
            }
            case ActionPhaseStepOne -> {
                return ActionPhaseStepTwo;
            }
            case ActionPhaseStepTwo -> {
                return ActionPhaseStepThree;
            }
            default -> {
                //Will never be executed
                return null;
            }
        }
    }
}
