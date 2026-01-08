package boot.team.hr.ho.repository;

import boot.team.hr.ho.entity.ApprovalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalLogRepository extends JpaRepository<ApprovalLog, Long> {

    // 특정 문서의 결재 이력 조회
    List<ApprovalLog> findByApprovalIdOrderByLogId(Long approvalId);

    // 특정 사원의 결재 이력 조회
    List<ApprovalLog> findByEmpId(Long empId);
}
