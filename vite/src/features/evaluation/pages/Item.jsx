import { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Alert, Container, Row, Col } from 'react-bootstrap';
import { getAllCriteria, createCriteria, updateCriteria, deleteCriteria } from '../api/criteriaApi';

const Item = () => {
    const [criteria, setCriteria] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentCriteria, setCurrentCriteria] = useState({
        criteriaId: null,
        criteriaName: '',
        weight: 0,
        description: ''
    });
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    // 데이터 로드
    useEffect(() => {
        fetchCriteria();
    }, []);

    const fetchCriteria = async () => {
        try {
            const data = await getAllCriteria();
            setCriteria(data);
            setError('');
        } catch (err) {
            setError('평가 항목을 불러오는데 실패했습니다.');
            console.error(err);
        }
    };

    // 모달 열기 (추가)
    const handleAddClick = () => {
        setIsEditMode(false);
        setCurrentCriteria({
            criteriaId: null,
            criteriaName: '',
            weight: 0,
            description: ''
        });
        setShowModal(true);
    };

    // 모달 열기 (수정)
    const handleEditClick = (item) => {
        setIsEditMode(true);
        setCurrentCriteria({
            criteriaId: item.criteriaId,
            criteriaName: item.criteriaName,
            weight: item.weight,
            description: item.description
        });
        setShowModal(true);
    };

    // 모달 닫기
    const handleCloseModal = () => {
        setShowModal(false);
        setCurrentCriteria({
            criteriaId: null,
            criteriaName: '',
            weight: 0,
            description: ''
        });
    };

    // 입력값 변경
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCurrentCriteria({
            ...currentCriteria,
            [name]: name === 'weight' ? parseInt(value) || 0 : value
        });
    };

    // 저장
    const handleSave = async () => {
        try {
            if (!currentCriteria.criteriaName.trim()) {
                setError('항목명을 입력해주세요.');
                return;
            }
            if (currentCriteria.weight < 0 || currentCriteria.weight > 100) {
                setError('가중치는 0~100 사이의 값이어야 합니다.');
                return;
            }

            if (isEditMode) {
                await updateCriteria(currentCriteria.criteriaId, currentCriteria);
                setSuccessMessage('평가 항목이 수정되었습니다.');
            } else {
                await createCriteria(currentCriteria);
                setSuccessMessage('평가 항목이 추가되었습니다.');
            }

            fetchCriteria();
            handleCloseModal();
            setError('');

            setTimeout(() => setSuccessMessage(''), 3000);
        } catch (err) {
            setError(isEditMode ? '수정에 실패했습니다.' : '추가에 실패했습니다.');
            console.error(err);
        }
    };

    // 삭제
    const handleDelete = async () => {
        if (window.confirm('정말 삭제하시겠습니까?')) {
            try {
                await deleteCriteria(currentCriteria.criteriaId);
                setSuccessMessage('평가 항목이 삭제되었습니다.');
                fetchCriteria();
                handleCloseModal();
                setError('');
                setTimeout(() => setSuccessMessage(''), 3000);
            } catch (err) {
                setError('삭제에 실패했습니다.');
                console.error(err);
            }
        }
    };

    return (
        <Container className="mt-4">
            <Row className="mb-3">
                <Col>
                    <h2>평가 항목 관리</h2>
                </Col>
                <Col className="text-end">
                    <Button variant="primary" onClick={handleAddClick}>
                        항목 추가
                    </Button>
                </Col>
            </Row>

            {error && <Alert variant="danger" onClose={() => setError('')} dismissible>{error}</Alert>}
            {successMessage && <Alert variant="success" onClose={() => setSuccessMessage('')} dismissible>{successMessage}</Alert>}

            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>항목명</th>
                        <th>가중치 (%)</th>
                        <th>설명</th>
                        <th>생성일</th>
                        <th>작업</th>
                    </tr>
                </thead>
                <tbody>
                    {criteria.length === 0 ? (
                        <tr>
                            <td colSpan="5" className="text-center">등록된 평가 항목이 없습니다.</td>
                        </tr>
                    ) : (
                        criteria.map((item) => (
                            <tr key={item.criteriaId}>
                                <td>{item.criteriaName}</td>
                                <td>{item.weight}%</td>
                                <td>{item.description || '-'}</td>
                                <td>{new Date(item.createdAt).toLocaleDateString()}</td>
                                <td>
                                    <Button
                                        variant="warning"
                                        size="sm"
                                        onClick={() => handleEditClick(item)}
                                    >
                                        수정
                                    </Button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </Table>

            {/* 추가/수정 모달 */}
            <Modal show={showModal} onHide={handleCloseModal}>
                <Modal.Header closeButton>
                    <Modal.Title>{isEditMode ? '평가 항목 수정' : '평가 항목 추가'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>항목명 *</Form.Label>
                            <Form.Control
                                type="text"
                                name="criteriaName"
                                value={currentCriteria.criteriaName}
                                onChange={handleInputChange}
                                placeholder="예: 업무능력, 성실도"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>가중치 (%) *</Form.Label>
                            <Form.Control
                                type="number"
                                name="weight"
                                value={currentCriteria.weight}
                                onChange={handleInputChange}
                                min="0"
                                max="100"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>설명</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="description"
                                value={currentCriteria.description}
                                onChange={handleInputChange}
                                placeholder="평가 항목에 대한 설명을 입력하세요"
                            />
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    {isEditMode && (
                        <Button variant="danger" onClick={handleDelete} className="me-auto">
                            삭제
                        </Button>
                    )}
                    <Button variant="secondary" onClick={handleCloseModal}>
                        취소
                    </Button>
                    <Button variant="primary" onClick={handleSave}>
                        {isEditMode ? '수정' : '추가'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default Item;