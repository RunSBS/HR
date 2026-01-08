package boot.team.hr.gyu.controller;

import boot.team.hr.gyu.dto.EvaluationCriteriaDTO;
import boot.team.hr.gyu.service.EvaluationCriteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/evaluation/criteria")
public class EvaluationCriteriaController {

    private final EvaluationCriteriaService criteriaService;

    @GetMapping
    public ResponseEntity<List<EvaluationCriteriaDTO>> getAllCriteria() {
        List<EvaluationCriteriaDTO> criteria = criteriaService.getAllCriteria();
        return ResponseEntity.ok(criteria);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationCriteriaDTO> getCriteriaById(@PathVariable Long id) {
        EvaluationCriteriaDTO criteria = criteriaService.getCriteriaById(id);
        return ResponseEntity.ok(criteria);
    }

    @PostMapping
    public ResponseEntity<Long> createCriteria(@RequestBody EvaluationCriteriaDTO dto) {
        Long id = criteriaService.createCriteria(dto);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCriteria(@PathVariable Long id, @RequestBody EvaluationCriteriaDTO dto) {
        criteriaService.updateCriteria(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCriteria(@PathVariable Long id) {
        criteriaService.deleteCriteria(id);
        return ResponseEntity.ok().build();
    }
}