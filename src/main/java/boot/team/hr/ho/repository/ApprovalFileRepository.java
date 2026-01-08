package boot.team.hr.ho.repository;

import boot.team.hr.ho.entity.ApprovalFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalFileRepository extends JpaRepository<ApprovalFile, Long> {

    // 특정 문서에 첨부된 파일 조회
    List<ApprovalFile> findByApprovalId(Long approvalId);
}
