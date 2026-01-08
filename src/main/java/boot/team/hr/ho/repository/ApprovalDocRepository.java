package boot.team.hr.ho.repository;

import boot.team.hr.ho.entity.ApprovalDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalDocRepository extends JpaRepository<ApprovalDoc, Long> {

    // 특정 사원이 작성한 결재 문서 조회
    List<ApprovalDoc> findByEmpId(Long empId);

    // 특정 결재 유형의 문서 조회
    List<ApprovalDoc> findByTypeId(Long typeId);
}