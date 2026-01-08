package boot.team.hr.ho.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "APPROVAL_LINE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalLine {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "approval_line_seq"
    )
    @SequenceGenerator(
            name = "approval_line_seq",
            sequenceName = "SEQ_APPROVAL_LINE",
            allocationSize = 1
    )
    @Column(name = "LINE_ID")
    private Long lineId;

    @Column(name = "APPROVAL_ID", nullable = false)
    private Long approvalId;

    @Column(name = "EMP_ID", nullable = false)
    private Long empId;

    @Column(name = "STEP_ORDER", nullable = false)
    private Integer stepOrder;

    @Column(name = "CURRENT", nullable = false)
    private boolean current;

    @Column(name = "ACTION_AT")
    private LocalDateTime actionAt;

    public boolean isCurrent() {
        return current;
    }

    public void activate() {
        this.current = true;
    }

    public void deactivate() {
        this.current = false;
        this.actionAt = LocalDateTime.now();
    }

    public static ApprovalLine create(Long approvalId, Long empId, Integer stepOrder, boolean isCurrent) {
        ApprovalLine line = new ApprovalLine();
        line.approvalId = approvalId;
        line.empId = empId;
        line.stepOrder = stepOrder;
        line.current = isCurrent;
        line.actionAt = isCurrent ? null : LocalDateTime.now(); // 이미 지나간 경우 actionAt 기록
        return line;
    }

}
