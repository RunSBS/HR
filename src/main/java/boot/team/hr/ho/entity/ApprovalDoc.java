package boot.team.hr.ho.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "APPROVAL_DOC")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "approval_doc_seq")
    @SequenceGenerator(
            name = "approval_doc_seq",
            sequenceName = "SEQ_APPROVAL_DOC",
            allocationSize = 1
    )
    @Column(name = "APPROVAL_ID")
    private Long approvalId;

    @Column(name = "EMP_ID", nullable = false)
    private Long empId;

    @Column(name = "TYPE_ID", nullable = false)
    private Long typeId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private ApprovalStatus status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    public static ApprovalDoc create(
            Long empId,
            Long typeId,
            String title,
            String content
    ){
        ApprovalDoc doc = new ApprovalDoc();
        doc.empId = empId;
        doc.typeId = typeId;
        doc.title = title;
        doc.content = content;
        doc.status = ApprovalStatus.WAIT;
        doc.createdAt = LocalDateTime.now();
        return doc;
    }

    /* ==============================
       도메인 행위
    ============================== */
    public void approve() {
        validateWaitingStatus();
        this.status = ApprovalStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        validateWaitingStatus();
        this.status = ApprovalStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        validateWaitingStatus();
        this.status = ApprovalStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }


    private void validateWaitingStatus() {
        if (this.status != ApprovalStatus.WAIT) {
            throw new IllegalStateException(
                    "결재는 대기(WAIT) 상태에서만 처리할 수 있습니다."
            );
        }
    }
}