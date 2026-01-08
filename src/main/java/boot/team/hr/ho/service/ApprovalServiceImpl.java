package boot.team.hr.ho.service;

import boot.team.hr.ho.dto.*;
import boot.team.hr.ho.entity.*;
import boot.team.hr.ho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalDocRepository approvalDocRepository;
    private final ApprovalLineRepository approvalLineRepository;
    private final ApprovalFileRepository approvalFileRepository;
    private final ApprovalLogRepository approvalLogRepository;

    // -----------------------------
    // 1. 결재 신청
    @Override
    public ApprovalResponseDto createApproval(ApprovalRequestDto request) {

        // 1) ApprovalDoc 생성 및 저장
        ApprovalDoc doc = ApprovalDoc.create(
                request.getEmpId(),
                request.getTypeId(),
                request.getTitle(),
                request.getContent()
        );
        approvalDocRepository.save(doc);

        // 2) 결재선 생성
        List<ApprovalLine> lines = List.of(
                ApprovalLine.create(
                        doc.getApprovalId(),
                        request.getFirstApproverId(),
                        1,
                        true
                ),
                ApprovalLine.create(
                        doc.getApprovalId(),
                        request.getSecondApproverId(),
                        2,
                        false
                ),
                ApprovalLine.create(
                        doc.getApprovalId(),
                        request.getThirdApproverId(),
                        3,
                        false
                )
        );
        approvalLineRepository.saveAll(lines);

        // 3) 첨부파일 저장
        if (request.getFiles() != null) {
            List<ApprovalFile> files = request.getFiles().stream().map(f -> ApprovalFile.create(
                            doc.getApprovalId(),
                            f.getFileName(),
                            f.getFilePaths(),
                            f.getFileSize())).collect(Collectors.toList());
            approvalFileRepository.saveAll(files);
        }

        // 4) 신청 로그 기록
        ApprovalLog log = ApprovalLog.create(
                doc.getApprovalId(),
                request.getEmpId(),
                "REQUEST",
                "결재 신청"
        );
        approvalLogRepository.save(log);

        return mapToResponseDto(doc);
    }

    // -----------------------------
    // 2. 결재 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ApprovalResponseDto getApproval(Long approvalId) {
        ApprovalDoc doc = approvalDocRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("결재 문서를 찾을 수 없습니다."));

        return mapToResponseDto(doc);
    }

    // -----------------------------
    // 3. 결재 승인
    @Override
    public void approveApproval(ApprovalActionDto request) {

        ApprovalDoc doc = approvalDocRepository.findById(request.getApprovalId())
                .orElseThrow(() -> new IllegalArgumentException("결재 문서를 찾을 수 없습니다."));

        // 1) 현재 결재선 찾기
        ApprovalLine current =
                approvalLineRepository.findByApprovalIdAndCurrentTrue(doc.getApprovalId());

        if (!current.getEmpId().equals(request.getEmpId())) {
            throw new IllegalStateException("현재 결재자가 아닙니다.");
        }

        current.deactivate();

        // 2) 다음 결재선
        ApprovalLine next =
                approvalLineRepository.findByApprovalIdAndStepOrder(
                        doc.getApprovalId(),
                        current.getStepOrder() + 1
                );

        if (next != null) {
            next.activate();
        } else {
            // 마지막 승인
            doc.approve();
        }


        // 3) 로그 기록
        ApprovalLog log = ApprovalLog.create(
                doc.getApprovalId(),
                request.getEmpId(),
                "APPROVED",
                request.getComment()
        );
        approvalLogRepository.save(log);

    }


    // -----------------------------
    // 4. 결재 반려
    @Override
    public void rejectApproval(ApprovalActionDto request) {

        ApprovalDoc doc = approvalDocRepository.findById(request.getApprovalId())
                .orElseThrow(() -> new IllegalArgumentException("결재 문서를 찾을 수 없습니다."));

        // 1) 현재 결재선
        ApprovalLine current =
                approvalLineRepository.findByApprovalIdAndCurrentTrue(doc.getApprovalId());

        if (!current.getEmpId().equals(request.getEmpId())) {
            throw new IllegalStateException("현재 결재자가 아닙니다.");
        }

        // 2) 현재 결재선 종료
        current.deactivate();

        // 3) 문서 상태 반려
        doc.reject();

        // 4) 로그 기록
        ApprovalLog log = ApprovalLog.create(
                doc.getApprovalId(),
                request.getEmpId(),
                "REJECTED",
                request.getComment()
        );
        approvalLogRepository.save(log);
    }


    // -----------------------------
    // 5. 결재 취소
    @Override
    public void cancelApproval(ApprovalActionDto request) {

        ApprovalDoc doc = approvalDocRepository.findById(request.getApprovalId())
                .orElseThrow(() -> new IllegalArgumentException("결재 문서를 찾을 수 없습니다."));

        // 1) 신청자 본인인지 검증
        if (!doc.getEmpId().equals(request.getEmpId())) {
            throw new IllegalStateException("신청자만 결재를 취소할 수 있습니다.");
        }

        // 2) 이미 승인된 결재선이 있는지 확인
        boolean approvedExists =
                approvalLineRepository.existsByApprovalIdAndActionAtIsNotNull(doc.getApprovalId());

        if (approvedExists) {
            throw new IllegalStateException("이미 승인된 결재가 있어 취소할 수 없습니다.");
        }

        // 3) 문서 취소
        doc.cancel();

        // 4) 현재 결재선이 있다면 비활성화
        ApprovalLine current =
                approvalLineRepository.findByApprovalIdAndCurrentTrue(doc.getApprovalId());

        if (current != null) {
            current.deactivate();
        }

        // 5) 로그 기록
        ApprovalLog log = ApprovalLog.create(
                doc.getApprovalId(),
                request.getEmpId(),
                "CANCELLED",
                request.getComment()
        );
        approvalLogRepository.save(log);
    }


    // -----------------------------
    // 6. 사원별 결재 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ApprovalResponseDto> getApprovalsByEmp(Long empId) {
        List<ApprovalDoc> docs = approvalDocRepository.findByEmpId(empId);
        return docs.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // -----------------------------
    // DTO 변환 공용 메서드
    private ApprovalResponseDto mapToResponseDto(ApprovalDoc doc) {
        List<LineDto> lines = approvalLineRepository.findByApprovalIdOrderByStepOrder(doc.getApprovalId())
                .stream()
                .map(l -> {
                    LineDto dto = new LineDto();
                    dto.setLineId(l.getLineId());
                    dto.setEmpId(l.getEmpId());
                    dto.setStepOrder(l.getStepOrder());
                    dto.setCurrent(l.isCurrent());
                    dto.setActionAt(l.getActionAt());
                    return dto;
                }).collect(Collectors.toList());

        List<FileDto> files = approvalFileRepository.findByApprovalId(doc.getApprovalId())
                .stream()
                .map(f -> {
                    FileDto dto = new FileDto();
                    dto.setFileName(f.getFileName());
                    dto.setFilePaths(f.getFilePaths());
                    dto.setFileSize(f.getFileSize());
                    return dto;
                }).collect(Collectors.toList());

        List<LogDto> logs = approvalLogRepository.findByApprovalIdOrderByLogId(doc.getApprovalId())
                .stream()
                .map(l -> {
                    LogDto dto = new LogDto();
                    dto.setLogId(l.getLogId());
                    dto.setEmpId(l.getEmpId());
                    dto.setAction(l.getAction());
                    dto.setComment(l.getComment());
                    dto.setCreatedAt(l.getCreatedAt());
                    return dto;
                }).collect(Collectors.toList());

        ApprovalResponseDto response = new ApprovalResponseDto();
        response.setApprovalId(doc.getApprovalId());
        response.setEmpId(doc.getEmpId());
        response.setTypeId(doc.getTypeId());
        response.setTitle(doc.getTitle());
        response.setContent(doc.getContent());
        response.setStatus(doc.getStatus().name());
        response.setCreatedAt(doc.getCreatedAt());
        response.setUpdatedAt(doc.getUpdatedAt());
        response.setLines(lines);
        response.setFiles(files);
        response.setLogs(logs);

        return response;
    }
}
