import { useState, useEffect } from 'react';
import { inputApi } from '../api/inputApi';
import { criteriaApi } from '../api/criteriaApi';
import '../styles/Input.css';

const Input = () => {
  const [currentUser, setCurrentUser] = useState(null);
  const [targets, setTargets] = useState([]);
  const [criteriaList, setCriteriaList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [hasPermission, setHasPermission] = useState(false);

  const [formData, setFormData] = useState({
    empId: '',
    evaluationPeriod: '',
    comment: '',
    scores: [],
  });

  useEffect(() => {
    checkPermissionAndLoadData();
  }, []);

  const checkPermissionAndLoadData = async () => {
    try {
      setLoading(true);

      // 현재 사용자 정보 조회
      const user = await inputApi.getCurrentUser();
      setCurrentUser(user);
      console.log('현재 로그인 사용자:', user);
      console.log('사용자 포지션:', user.position);

      // 권한 체크: CEO 또는 LEADER만 접근 가능
      if (user.position === 'CEO' || user.position === 'LEADER') {
        setHasPermission(true);
        console.log('평가 입력 권한 있음');

        // 평가 대상자 목록 조회
        const targetList = await inputApi.getEvaluationTargets();
        setTargets(targetList);
        console.log('평가 대상자 목록:', targetList);

        // 평가 항목 조회
        const criteria = await criteriaApi.getAllCriteria();
        setCriteriaList(criteria);
        console.log('평가 항목 목록:', criteria);

        // 점수 초기화
        const initialScores = criteria.map((c) => ({
          criteriaId: c.criteriaId,
          score: 0,
        }));
        setFormData((prev) => ({ ...prev, scores: initialScores }));
      } else {
        setHasPermission(false);
        console.log('평가 입력 권한 없음');
      }
    } catch (error) {
      console.error('데이터 로딩 실패:', error);
      setHasPermission(false);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleScoreChange = (criteriaId, score) => {
    setFormData((prev) => ({
      ...prev,
      scores: prev.scores.map((s) =>
        s.criteriaId === criteriaId ? { ...s, score: parseInt(score) || 0 } : s
      ),
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.empId) {
      alert('평가 대상자를 선택해주세요.');
      return;
    }

    if (!formData.evaluationPeriod) {
      alert('평가 기간을 입력해주세요.');
      return;
    }

    try {
      const submitData = {
        empId: formData.empId,
        evaluatorId: currentUser.empId,
        evaluationPeriod: formData.evaluationPeriod,
        comment: formData.comment,
        scores: formData.scores,
      };

      await inputApi.createEvaluation(submitData);
      alert('평가가 성공적으로 등록되었습니다.');

      // 폼 초기화
      setFormData({
        empId: '',
        evaluationPeriod: '',
        comment: '',
        scores: criteriaList.map((c) => ({
          criteriaId: c.criteriaId,
          score: 0,
        })),
      });
    } catch (error) {
      console.error('평가 등록 실패:', error);
      alert('평가 등록에 실패했습니다.');
    }
  };

  if (loading) {
    return <div className="input-container">로딩 중...</div>;
  }

  if (!hasPermission) {
    return (
      <div className="input-container">
        <div className="no-permission">
          <h2>접근 권한이 없습니다</h2>
          <p>평가 입력은 CEO 또는 LEADER만 가능합니다.</p>
          {currentUser && (
            <div className="user-info">
              <p>현재 사용자: {currentUser.empName}</p>
              <p>직급: {currentUser.position}</p>
            </div>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="input-container">
      <h1>평가 입력</h1>

      <div className="user-info-box">
        <p>평가자: {currentUser.empName} ({currentUser.position})</p>
      </div>

      <form onSubmit={handleSubmit} className="evaluation-form">
        {/* 평가 대상자 선택 */}
        <div className="form-group">
          <label htmlFor="empId">평가 대상자 *</label>
          <select
            id="empId"
            name="empId"
            value={formData.empId}
            onChange={handleInputChange}
            required
          >
            <option value="">선택해주세요</option>
            {targets.map((target) => (
              <option key={target.id} value={target.empId}>
                {target.empName} ({target.position}) - 사번: {target.empId}
              </option>
            ))}
          </select>
        </div>

        {/* 평가 기간 */}
        <div className="form-group">
          <label htmlFor="evaluationPeriod">평가 기간 *</label>
          <input
            type="text"
            id="evaluationPeriod"
            name="evaluationPeriod"
            value={formData.evaluationPeriod}
            onChange={handleInputChange}
            placeholder="예: 2025년 1분기"
            required
          />
        </div>

        {/* 평가 항목별 점수 */}
        <div className="form-group">
          <label>평가 항목별 점수 (0-100점)</label>
          <div className="criteria-scores">
            {criteriaList.map((criteria) => {
              const currentScore =
                formData.scores.find((s) => s.criteriaId === criteria.criteriaId)?.score || 0;
              return (
                <div key={criteria.criteriaId} className="criteria-item">
                  <div className="criteria-header">
                    <span className="criteria-name">{criteria.criteriaName}</span>
                    <span className="criteria-weight">가중치: {criteria.weight}%</span>
                  </div>
                  <div className="criteria-description">{criteria.description}</div>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={currentScore}
                    onChange={(e) =>
                      handleScoreChange(criteria.criteriaId, e.target.value)
                    }
                    className="score-input"
                  />
                </div>
              );
            })}
          </div>
        </div>

        {/* 평가 의견 */}
        <div className="form-group">
          <label htmlFor="comment">평가 의견</label>
          <textarea
            id="comment"
            name="comment"
            value={formData.comment}
            onChange={handleInputChange}
            rows="5"
            placeholder="평가 의견을 입력해주세요"
          />
        </div>

        <button type="submit" className="submit-button">
          평가 등록
        </button>
      </form>
    </div>
  );
};

export default Input;