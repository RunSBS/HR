package boot.team.hr.gyu.service;

import boot.team.hr.gyu.dto.EvaluationCriteriaDTO;
import boot.team.hr.gyu.entity.EvaluationCriteria;
import boot.team.hr.gyu.repository.EvaluationCriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationCriteriaService {

    private final EvaluationCriteriaRepository criteriaRepository;

    @Transactional(readOnly = true)
    public List<EvaluationCriteriaDTO> getAllCriteria() {
        return criteriaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EvaluationCriteriaDTO getCriteriaById(Long id) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("평가 항목을 찾을 수 없습니다."));
        return convertToDTO(criteria);
    }

    @Transactional
    public Long createCriteria(EvaluationCriteriaDTO dto) {
        EvaluationCriteria criteria = EvaluationCriteria.builder()
                .criteriaName(dto.getCriteriaName())
                .weight(dto.getWeight())
                .description(dto.getDescription())
                .build();

        return criteriaRepository.save(criteria).getCriteriaId();
    }

    @Transactional
    public void updateCriteria(Long id, EvaluationCriteriaDTO dto) {
        EvaluationCriteria criteria = criteriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("평가 항목을 찾을 수 없습니다."));

        criteria.setCriteriaName(dto.getCriteriaName());
        criteria.setWeight(dto.getWeight());
        criteria.setDescription(dto.getDescription());

        criteriaRepository.save(criteria);
    }

    @Transactional
    public void deleteCriteria(Long id) {
        if (!criteriaRepository.existsById(id)) {
            throw new IllegalArgumentException("평가 항목을 찾을 수 없습니다.");
        }
        criteriaRepository.deleteById(id);
    }

    private EvaluationCriteriaDTO convertToDTO(EvaluationCriteria criteria) {
        return EvaluationCriteriaDTO.builder()
                .criteriaId(criteria.getCriteriaId())
                .criteriaName(criteria.getCriteriaName())
                .weight(criteria.getWeight())
                .description(criteria.getDescription())
                .createdAt(criteria.getCreatedAt())
                .build();
    }
}