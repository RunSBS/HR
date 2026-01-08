package boot.team.hr.ho.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
public class ApprovalRequestDto {
    private Long empId;          // 작성자 ID
    private Long typeId;         // 결재 유형
    private String title;        // 결재 제목
    private String content;      // 결재 내용
    private Long firstApproverId;   // 1차 결재자
    private Long secondApproverId;  // 2차 결재자
    private Long thirdApproverId;   // 3차 결재자
    private List<FileDto> files; // 첨부파일 리스트
}

