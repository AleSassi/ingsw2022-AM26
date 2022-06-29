package it.polimi.ingsw.server.model.match;

/**
 * Enum representing the {@code MatchPhases} of the game
 */
public enum MatchPhase {
    PlanPhaseStepOne,
    PlanPhaseStepTwo,
    ActionPhaseStepOne,
    ActionPhaseStepTwo,
    ActionPhaseStepThree;

    /**
     * Moves to next {@code MatchPhase}
     * @return the next {MatchPhase}
     */
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
