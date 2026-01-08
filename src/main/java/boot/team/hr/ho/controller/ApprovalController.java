package boot.team.hr.ho.controller;

import boot.team.hr.ho.dto.*;
import boot.team.hr.ho.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ho/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    // -----------------------------
    // 1. 결재 신청
    @PostMapping
    public ResponseEntity<ApprovalResponseDto> createApproval(@RequestBody ApprovalRequestDto request) {
        ApprovalResponseDto response = approvalService.createApproval(request);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // 2. 결재 상세 조회
    @GetMapping("/{approvalId}")
    public ResponseEntity<ApprovalResponseDto> getApproval(@PathVariable Long approvalId) {
        ApprovalResponseDto response = approvalService.getApproval(approvalId);
        return ResponseEntity.ok(response);
    }

    // -----------------------------
    // 3. 결재 승인
    @PostMapping("/{approvalId}/approve")
    public ResponseEntity<Void> approveApproval(
            @PathVariable Long approvalId,
            @RequestBody ApprovalActionDto request
    ) {
        request.setApprovalId(approvalId);
        approvalService.approveApproval(request);
        return ResponseEntity.ok().build();
    }

    // -----------------------------
    // 4. 결재 반려
    @PostMapping("/{approvalId}/reject")
    public ResponseEntity<Void> rejectApproval(
            @PathVariable Long approvalId,
            @RequestBody ApprovalActionDto request
    ) {
        request.setApprovalId(approvalId);
        approvalService.rejectApproval(request);
        return ResponseEntity.ok().build();
    }

    // -----------------------------
    // 5. 결재 취소
    @PostMapping("/{approvalId}/cancel")
    public ResponseEntity<Void> cancelApproval(
            @PathVariable Long approvalId,
            @RequestBody ApprovalActionDto request
    ) {
        request.setApprovalId(approvalId);
        approvalService.cancelApproval(request);
        return ResponseEntity.ok().build();
    }

    // -----------------------------
    // 6. 사원별 결재 목록 조회
    @GetMapping
    public ResponseEntity<List<ApprovalResponseDto>> getApprovalsByEmp(
            @RequestParam Long empId
    ) {
        List<ApprovalResponseDto> approvals = approvalService.getApprovalsByEmp(empId);
        return ResponseEntity.ok(approvals);
    }
}
