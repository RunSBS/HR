import { useState, useEffect } from 'react';
import { criteriaApi } from '../api/criteriaApi';

const Item = () => {
  const [criteriaList, setCriteriaList] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCriteria, setEditingCriteria] = useState(null);
  const [formData, setFormData] = useState({
    criteriaName: '',
    weight: '',
    description: ''
  });

  useEffect(() => {
    fetchCriteria();
  }, []);

  const fetchCriteria = async () => {
    try {
      const data = await criteriaApi.getAllCriteria();
      console.log('API Response:', data);
      setCriteriaList(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('평가 항목 조회 실패:', error);
      setCriteriaList([]);
      alert('평가 항목 조회에 실패했습니다.');
    }
  };

  const handleOpenModal = (criteria = null) => {
    if (criteria) {
      setEditingCriteria(criteria);
      setFormData({
        criteriaName: criteria.criteriaName,
        weight: criteria.weight,
        description: criteria.description || ''
      });
    } else {
      setEditingCriteria(null);
      setFormData({
        criteriaName: '',
        weight: '',
        description: ''
      });
    }
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingCriteria(null);
    setFormData({
      criteriaName: '',
      weight: '',
      description: ''
    });
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.criteriaName.trim()) {
      alert('평가 항목명을 입력해주세요.');
      return;
    }

    if (!formData.weight || formData.weight <= 0 || formData.weight > 999) {
      alert('가중치는 1-999 사이의 값을 입력해주세요.');
      return;
    }

    try {
      if (editingCriteria) {
        await criteriaApi.updateCriteria(editingCriteria.criteriaId, formData);
        alert('평가 항목이 수정되었습니다.');
      } else {
        await criteriaApi.createCriteria(formData);
        alert('평가 항목이 추가되었습니다.');
      }
      handleCloseModal();
      fetchCriteria();
    } catch (error) {
      console.error('평가 항목 저장 실패:', error);
      alert('평가 항목 저장에 실패했습니다.');
    }
  };

  const handleDelete = async (criteriaId) => {
    if (!window.confirm('이 평가 항목을 삭제하시겠습니까?')) {
      return;
    }

    try {
      await criteriaApi.deleteCriteria(criteriaId);
      alert('평가 항목이 삭제되었습니다.');
      handleCloseModal();
      fetchCriteria();
    } catch (error) {
      console.error('평가 항목 삭제 실패:', error);
      alert('평가 항목 삭제에 실패했습니다.');
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h1 style={styles.title}>평가 항목 관리</h1>
        <button style={styles.addButton} onClick={() => handleOpenModal()}>
          + 평가 항목 추가
        </button>
      </div>

      <div style={styles.tableContainer}>
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>평가 항목명</th>
              <th style={styles.th}>가중치</th>
              <th style={styles.th}>설명</th>
              <th style={styles.th}>생성일시</th>
              <th style={styles.th}>관리</th>
            </tr>
          </thead>
          <tbody>
            {criteriaList.length === 0 ? (
              <tr>
                <td colSpan="5" style={styles.emptyMessage}>
                  등록된 평가 항목이 없습니다.
                </td>
              </tr>
            ) : (
              criteriaList.map((criteria) => (
                <tr key={criteria.criteriaId} style={styles.tr}>
                  <td style={styles.td}>{criteria.criteriaName}</td>
                  <td style={styles.td}>{criteria.weight}%</td>
                  <td style={styles.td}>{criteria.description || '-'}</td>
                  <td style={styles.td}>{formatDate(criteria.createdAt)}</td>
                  <td style={styles.td}>
                    <button
                      style={styles.editButton}
                      onClick={() => handleOpenModal(criteria)}
                    >
                      수정
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
        <div style={styles.modalOverlay} onClick={handleCloseModal}>
          <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
            <h2 style={styles.modalTitle}>
              {editingCriteria ? '평가 항목 수정' : '평가 항목 추가'}
            </h2>
            <form onSubmit={handleSubmit} style={styles.form}>
              <div style={styles.formGroup}>
                <label style={styles.label}>평가 항목명 *</label>
                <input
                  type="text"
                  name="criteriaName"
                  value={formData.criteriaName}
                  onChange={handleInputChange}
                  style={styles.input}
                  maxLength="50"
                  required
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>가중치 *</label>
                <input
                  type="number"
                  name="weight"
                  value={formData.weight}
                  onChange={handleInputChange}
                  style={styles.input}
                  min="1"
                  max="999"
                  required
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>설명</label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  style={styles.textarea}
                  maxLength="255"
                  rows="4"
                />
              </div>

              <div style={styles.buttonGroup}>
                {editingCriteria && (
                  <button
                    type="button"
                    style={styles.deleteButtonInModal}
                    onClick={() => handleDelete(editingCriteria.criteriaId)}
                  >
                    삭제
                  </button>
                )}
                <div style={styles.rightButtons}>
                  <button type="submit" style={styles.submitButton}>
                    {editingCriteria ? '수정' : '추가'}
                  </button>
                  <button
                    type="button"
                    style={styles.cancelButton}
                    onClick={handleCloseModal}
                  >
                    취소
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: {
    padding: '20px',
    maxWidth: '1200px',
    margin: '0 auto'
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '20px'
  },
  title: {
    fontSize: '24px',
    fontWeight: 'bold',
    margin: 0
  },
  addButton: {
    padding: '10px 20px',
    backgroundColor: '#4CAF50',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold'
  },
  tableContainer: {
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    overflow: 'hidden'
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse'
  },
  th: {
    backgroundColor: '#f5f5f5',
    padding: '12px',
    textAlign: 'left',
    fontWeight: 'bold',
    borderBottom: '2px solid #ddd'
  },
  tr: {
    borderBottom: '1px solid #eee'
  },
  td: {
    padding: '12px',
    textAlign: 'left'
  },
  emptyMessage: {
    padding: '40px',
    textAlign: 'center',
    color: '#999'
  },
  editButton: {
    padding: '6px 12px',
    backgroundColor: '#2196F3',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginRight: '8px',
    fontSize: '12px'
  },
  deleteButton: {
    padding: '6px 12px',
    backgroundColor: '#f44336',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '12px'
  },
  modalOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000
  },
  modal: {
    backgroundColor: 'white',
    padding: '30px',
    borderRadius: '8px',
    width: '500px',
    maxWidth: '90%',
    maxHeight: '90vh',
    overflow: 'auto'
  },
  modalTitle: {
    fontSize: '20px',
    fontWeight: 'bold',
    marginBottom: '20px',
    marginTop: 0
  },
  form: {
    display: 'flex',
    flexDirection: 'column'
  },
  formGroup: {
    marginBottom: '20px'
  },
  label: {
    display: 'block',
    marginBottom: '8px',
    fontWeight: 'bold',
    fontSize: '14px'
  },
  input: {
    width: '100%',
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '14px',
    boxSizing: 'border-box'
  },
  textarea: {
    width: '100%',
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '14px',
    resize: 'vertical',
    boxSizing: 'border-box'
  },
  buttonGroup: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '20px'
  },
  rightButtons: {
    display: 'flex',
    gap: '10px'
  },
  deleteButtonInModal: {
    padding: '10px 20px',
    backgroundColor: '#f44336',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold'
  },
  submitButton: {
    padding: '10px 20px',
    backgroundColor: '#4CAF50',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold'
  },
  cancelButton: {
    padding: '10px 20px',
    backgroundColor: '#999',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold'
  }
};

export default Item;