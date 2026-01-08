package boot.team.hr.ho.service;

import boot.team.hr.ho.dto.*;
import java.util.List;

public interface ApprovalService {

    // 1. 결재 신청
    ApprovalResponseDto createApproval(ApprovalRequestDto request);

    // 2. 결재 상세 조회
    ApprovalResponseDto getApproval(Long approvalId);

    // 3. 결재 승인
    void approveApproval(ApprovalActionDto request);

    // 4. 결재 반려
    void rejectApproval(ApprovalActionDto request);

    // 5. 결재 취소
    void cancelApproval(ApprovalActionDto request);

    // 6. 사원별 결재 목록 조회
    List<ApprovalResponseDto> getApprovalsByEmp(Long empId);
}
