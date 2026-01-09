package boot.team.hr.gyu.service;

import boot.team.hr.gyu.dto.*;
import boot.team.hr.gyu.entity.EvaluationCriteria;
import boot.team.hr.gyu.entity.EvaluationResult;
import boot.team.hr.gyu.entity.EvaluationScore;
import boot.team.hr.gyu.repository.EvaluationCriteriaRepository;
import boot.team.hr.gyu.repository.EvaluationResultRepository;
import boot.team.hr.gyu.repository.EvaluationScoreRepository;
import boot.team.hr.hyun.emp.entity.Emp;
import boot.team.hr.hyun.emp.repo.EmpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationInputService {

    private final EmpRepository empRepository;
    private final EvaluationResultRepository resultRepository;
    private final EvaluationScoreRepository scoreRepository;
    private final EvaluationCriteriaRepository criteriaRepository;

    /**
     * 현재 로그인 사용자 정보 조회
     */
    public CurrentUserDTO getCurrentUser(String email) {
        Emp emp = empRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return CurrentUserDTO.builder()
                .id(emp.getId())
                .empId(emp.getEmpId())
                .empName(emp.getEmpName())
                .deptId(emp.getDeptId())
                .email(emp.getEmail())
                .position(emp.getPosition())
                .build();
    }

    /**
     * 평가 대상자 목록 조회 (권한별)
     */
    public List<EvaluationTargetDTO> getEvaluationTargets(String email) {
        Emp currentUser = empRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Emp> targets = new ArrayList<>();

        if ("CEO".equals(currentUser.getPosition())) {
            // CEO는 LEADER 목록 조회
            targets = empRepository.findAll().stream()
                    .filter(emp -> "LEADER".equals(emp.getPosition()))
                    .collect(Collectors.toList());
        } else if ("LEADER".equals(currentUser.getPosition())) {
            // LEADER는 같은 부서의 EMP 목록 조회
            targets = empRepository.findAll().stream()
                    .filter(emp -> emp.getDeptId() != null
                            && emp.getDeptId().equals(currentUser.getDeptId())
                            && "EMP".equals(emp.getPosition()))
                    .collect(Collectors.toList());
        }

        return targets.stream()
                .map(emp -> EvaluationTargetDTO.builder()
                        .id(emp.getId())
                        .empId(emp.getEmpId())
                        .empName(emp.getEmpName())
                        .deptId(emp.getDeptId())
                        .position(emp.getPosition())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 평가 권한 체크
     */
    public boolean hasEvaluationPermission(String evaluatorEmail, String targetEmpId) {
        Emp evaluator = empRepository.findByEmail(evaluatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("평가자를 찾을 수 없습니다."));

        Emp target = empRepository.findByEmpId(targetEmpId)
                .orElseThrow(() -> new IllegalArgumentException("평가 대상자를 찾을 수 없습니다."));

        // CEO는 LEADER 평가 가능
        if ("CEO".equals(evaluator.getPosition()) && "LEADER".equals(target.getPosition())) {
            return true;
        }

        // LEADER는 같은 부서의 EMP 평가 가능
        if ("LEADER".equals(evaluator.getPosition())
                && "EMP".equals(target.getPosition())
                && evaluator.getDeptId() != null
                && evaluator.getDeptId().equals(target.getDeptId())) {
            return true;
        }

        return false;
    }

    /**
     * 평가 입력
     */
    @Transactional
    public Long createEvaluation(String evaluatorEmail, EvaluationInputDTO inputDTO) {
        Emp evaluator = empRepository.findByEmail(evaluatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("평가자를 찾을 수 없습니다."));

        // 권한 체크
        if (!hasEvaluationPermission(evaluatorEmail, inputDTO.getEmpId())) {
            throw new IllegalArgumentException("평가 권한이 없습니다.");
        }

        // 평가 항목 조회
        Map<Long, EvaluationCriteria> criteriaMap = criteriaRepository.findAll().stream()
                .collect(Collectors.toMap(EvaluationCriteria::getCriteriaId, c -> c));

        // 총점 계산
        int totalScore = 0;
        for (EvaluationScoreInputDTO scoreInput : inputDTO.getScores()) {
            EvaluationCriteria criteria = criteriaMap.get(scoreInput.getCriteriaId());
            if (criteria != null) {
                totalScore += scoreInput.getScore() * criteria.getWeight() / 100;
            }
        }

        // 평가 결과 저장
        EvaluationResult result = EvaluationResult.builder()
                .empId(inputDTO.getEmpId())
                .evaluatorId(evaluator.getEmpId())
                .totalScore(totalScore)
                .evaluationPeriod(inputDTO.getEvaluationPeriod())
                .comment(inputDTO.getComment())
                .build();

        result = resultRepository.save(result);

        // 평가 항목별 점수 저장
        for (EvaluationScoreInputDTO scoreInput : inputDTO.getScores()) {
            EvaluationScore score = EvaluationScore.builder()
                    .evaluationId(result.getEvaluationId())
                    .criteriaId(scoreInput.getCriteriaId())
                    .score(scoreInput.getScore())
                    .build();
            scoreRepository.save(score);
        }

        return result.getEvaluationId();
    }

    /**
     * 평가 수정
     */
    @Transactional
    public void updateEvaluation(String evaluatorEmail, Long evaluationId, EvaluationInputDTO inputDTO) {
        Emp evaluator = empRepository.findByEmail(evaluatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("평가자를 찾을 수 없습니다."));

        EvaluationResult result = resultRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("평가 결과를 찾을 수 없습니다."));

        // 본인이 작성한 평가인지 확인
        if (!result.getEvaluatorId().equals(evaluator.getEmpId())) {
            throw new IllegalArgumentException("본인이 작성한 평가만 수정할 수 있습니다.");
        }

        // 권한 체크
        if (!hasEvaluationPermission(evaluatorEmail, result.getEmpId())) {
            throw new IllegalArgumentException("평가 권한이 없습니다.");
        }

        // 평가 항목 조회
        Map<Long, EvaluationCriteria> criteriaMap = criteriaRepository.findAll().stream()
                .collect(Collectors.toMap(EvaluationCriteria::getCriteriaId, c -> c));

        // 총점 재계산
        int totalScore = 0;
        for (EvaluationScoreInputDTO scoreInput : inputDTO.getScores()) {
            EvaluationCriteria criteria = criteriaMap.get(scoreInput.getCriteriaId());
            if (criteria != null) {
                totalScore += scoreInput.getScore() * criteria.getWeight() / 100;
            }
        }

        // 평가 결과 수정
        result.setTotalScore(totalScore);
        result.setEvaluationPeriod(inputDTO.getEvaluationPeriod());
        result.setComment(inputDTO.getComment());
        resultRepository.save(result);

        // 기존 평가 항목 점수 삭제 후 재저장
        List<EvaluationScore> existingScores = scoreRepository.findByEvaluationId(evaluationId);
        scoreRepository.deleteAll(existingScores);

        for (EvaluationScoreInputDTO scoreInput : inputDTO.getScores()) {
            EvaluationScore score = EvaluationScore.builder()
                    .evaluationId(evaluationId)
                    .criteriaId(scoreInput.getCriteriaId())
                    .score(scoreInput.getScore())
                    .build();
            scoreRepository.save(score);
        }
    }
}