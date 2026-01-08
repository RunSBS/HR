package boot.team.hr.ho.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApprovalActionDto {
    private Long approvalId;  // 대상 결재 문서 ID
    private Long empId;       // 결재자 ID
    private String comment;   // 선택적 코멘트
}
