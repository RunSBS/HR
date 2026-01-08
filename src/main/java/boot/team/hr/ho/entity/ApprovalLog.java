package boot.team.hr.ho.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "APPROVAL_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalLog {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "approval_log_seq"
    )
    @SequenceGenerator(
            name = "approval_log_seq",
            sequenceName = "SEQ_APPROVAL_LOG",
            allocationSize = 1
    )
    @Column(name = "LOG_ID")
    private Long logId;

    @Column(name = "APPROVAL_ID", nullable = false)
    private Long approvalId;

    @Column(name = "EMP_ID", nullable = false)
    private Long empId;

    @Column(name = "ACTION", nullable = false)
    private String action;

    @Column(name = "COMMENT")
    private String comment;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public static ApprovalLog create(
            Long approvalId,
            Long empId,
            String action,
            String comment
    ) {
        ApprovalLog log = new ApprovalLog();
        log.approvalId = approvalId;
        log.empId = empId;
        log.action = action;
        log.comment = comment;
        log.createdAt = LocalDateTime.now();
        return log;
    }
}
