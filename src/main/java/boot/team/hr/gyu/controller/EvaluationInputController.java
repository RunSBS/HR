package boot.team.hr.gyu.controller;

import boot.team.hr.gyu.dto.CurrentUserDTO;
import boot.team.hr.gyu.dto.EvaluationInputDTO;
import boot.team.hr.gyu.dto.EvaluationTargetDTO;
import boot.team.hr.gyu.service.EvaluationInputService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/evaluation/input")
public class EvaluationInputController {

    private final EvaluationInputService inputService;

    /**
     * 현재 로그인 사용자 정보 조회
     */
    @GetMapping("/current-user")
    public ResponseEntity<CurrentUserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        CurrentUserDTO currentUser = inputService.getCurrentUser(email);
        return ResponseEntity.ok(currentUser);
    }

    /**
     * 평가 대상자 목록 조회 (권한별)
     */
    @GetMapping("/targets")
    public ResponseEntity<List<EvaluationTargetDTO>> getEvaluationTargets(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        List<EvaluationTargetDTO> targets = inputService.getEvaluationTargets(email);
        return ResponseEntity.ok(targets);
    }

    /**
     * 평가 입력
     */
    @PostMapping
    public ResponseEntity<Long> createEvaluation(
            Authentication authentication,
            @RequestBody EvaluationInputDTO inputDTO) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            String email = authentication.getName();
            Long evaluationId = inputService.createEvaluation(email, inputDTO);
            return ResponseEntity.ok(evaluationId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 평가 수정
     */
    @PutMapping("/{evaluationId}")
    public ResponseEntity<Void> updateEvaluation(
            Authentication authentication,
            @PathVariable Long evaluationId,
            @RequestBody EvaluationInputDTO inputDTO) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            String email = authentication.getName();
            inputService.updateEvaluation(email, evaluationId, inputDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}