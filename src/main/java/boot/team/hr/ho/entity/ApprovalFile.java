package boot.team.hr.ho.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "APPROVAL_FILE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApprovalFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_file_seq")
    @SequenceGenerator(
            name = "approval_file_seq",
            sequenceName = "SEQ_APPROVAL_FILE",
            allocationSize = 1
    )
    @Column(name = "FILE_ID")
    private Long fileId;

    @Column(name = "APPROVAL_ID", nullable = false)
    private Long approvalId;

    @Column(name = "FILE_NAME", length = 255)
    private String fileName;

    @Column(name = "FILE_PATHS", length = 500)
    private String filePaths;

    @Column(name = "FILE_SIZE", nullable = false)
    private Long fileSize;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public static ApprovalFile create(
            Long approvalId,
            String fileName,
            String filePaths,
            Long fileSize
    ) {
        ApprovalFile file = new ApprovalFile();
        file.approvalId = approvalId;
        file.fileName = fileName;
        file.filePaths = filePaths;
        file.fileSize = fileSize;
        file.createdAt = LocalDateTime.now();
        return file;
    }
}