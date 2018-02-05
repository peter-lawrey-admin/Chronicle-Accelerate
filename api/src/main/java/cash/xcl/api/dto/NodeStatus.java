package cash.xcl.api.dto;

// FIXME needs reviewing/completing
public enum NodeStatus {
    WAITING_FOR_APPROVAL,
    APPROVED_AND_NEVER_RUN,
    RUNNING,
    RUNNING_AND_DOING_ROUND_PROCESSING,
    NOT_RUNNING,
    DISABLED;
}
