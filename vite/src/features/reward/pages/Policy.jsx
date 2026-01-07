import { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Alert, Container, Row, Col, Badge } from 'react-bootstrap';
import { getAllPolicies, createPolicy, updatePolicy, deletePolicy } from '../api/policyApi';

const Policy = () => {
    const [policies, setPolicies] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentPolicy, setCurrentPolicy] = useState({
        policyId: null,
        policyName: '',
        rewardType: '',
        rewardAmount: 0,
        description: '',
        isActive: true
    });
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    // 데이터 로드
    useEffect(() => {
        fetchPolicies();
    }, []);

    const fetchPolicies = async () => {
        try {
            const data = await getAllPolicies();
            setPolicies(data);
            setError('');
        } catch (err) {
            setError('포상 정책을 불러오는데 실패했습니다.');
            console.error(err);
        }
    };

    // 모달 열기 (추가)
    const handleAddClick = () => {
        setIsEditMode(false);
        setCurrentPolicy({
            policyId: null,
            policyName: '',
            rewardType: '',
            rewardAmount: 0,
            description: '',
            isActive: true
        });
        setShowModal(true);
    };

    // 모달 열기 (수정)
    const handleEditClick = (item) => {
        setIsEditMode(true);
        setCurrentPolicy({
            policyId: item.policyId,
            policyName: item.policyName,
            rewardType: item.rewardType,
            rewardAmount: item.rewardAmount,
            description: item.description,
            isActive: item.isActive
        });
        setShowModal(true);
    };

    // 모달 닫기
    const handleCloseModal = () => {
        setShowModal(false);
        setCurrentPolicy({
            policyId: null,
            policyName: '',
            rewardType: '',
            rewardAmount: 0,
            description: '',
            isActive: true
        });
    };

    // 입력값 변경
    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setCurrentPolicy({
            ...currentPolicy,
            [name]: type === 'checkbox' ? checked :
                    name === 'rewardAmount' ? parseInt(value) || 0 : value
        });
    };

    // 저장
    const handleSave = async () => {
        try {
            if (!currentPolicy.policyName.trim()) {
                setError('정책명을 입력해주세요.');
                return;
            }
            if (!currentPolicy.rewardType.trim()) {
                setError('포상 유형을 입력해주세요.');
                return;
            }
            if (currentPolicy.rewardAmount < 0) {
                setError('포상 금액은 0 이상이어야 합니다.');
                return;
            }

            if (isEditMode) {
                await updatePolicy(currentPolicy.policyId, currentPolicy);
                setSuccessMessage('포상 정책이 수정되었습니다.');
            } else {
                await createPolicy(currentPolicy);
                setSuccessMessage('포상 정책이 추가되었습니다.');
            }

            fetchPolicies();
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
                await deletePolicy(currentPolicy.policyId);
                setSuccessMessage('포상 정책이 삭제되었습니다.');
                fetchPolicies();
                handleCloseModal();
                setError('');
                setTimeout(() => setSuccessMessage(''), 3000);
            } catch (err) {
                setError('삭제에 실패했습니다.');
                console.error(err);
            }
        }
    };

    // 금액 포맷
    const formatAmount = (amount) => {
        return new Intl.NumberFormat('ko-KR').format(amount);
    };

    return (
        <Container className="mt-4">
            <Row className="mb-3">
                <Col>
                    <h2>포상 정책 관리</h2>
                </Col>
                <Col className="text-end">
                    <Button variant="primary" onClick={handleAddClick}>
                        정책 추가
                    </Button>
                </Col>
            </Row>

            {error && <Alert variant="danger" onClose={() => setError('')} dismissible>{error}</Alert>}
            {successMessage && <Alert variant="success" onClose={() => setSuccessMessage('')} dismissible>{successMessage}</Alert>}

            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>정책명</th>
                        <th>포상 유형</th>
                        <th>포상 금액</th>
                        <th>설명</th>
                        <th>활성화</th>
                        <th>생성일</th>
                        <th>작업</th>
                    </tr>
                </thead>
                <tbody>
                    {policies.length === 0 ? (
                        <tr>
                            <td colSpan="7" className="text-center">등록된 포상 정책이 없습니다.</td>
                        </tr>
                    ) : (
                        policies.map((item) => (
                            <tr key={item.policyId}>
                                <td>{item.policyName}</td>
                                <td>{item.rewardType}</td>
                                <td>{formatAmount(item.rewardAmount)}원</td>
                                <td>{item.description || '-'}</td>
                                <td>
                                    {item.isActive ?
                                        <Badge bg="success">활성</Badge> :
                                        <Badge bg="secondary">비활성</Badge>
                                    }
                                </td>
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
                    <Modal.Title>{isEditMode ? '포상 정책 수정' : '포상 정책 추가'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>정책명 *</Form.Label>
                            <Form.Control
                                type="text"
                                name="policyName"
                                value={currentPolicy.policyName}
                                onChange={handleInputChange}
                                placeholder="예: 우수사원 포상"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>포상 유형 *</Form.Label>
                            <Form.Control
                                type="text"
                                name="rewardType"
                                value={currentPolicy.rewardType}
                                onChange={handleInputChange}
                                placeholder="예: 금전, 상품권, 휴가"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>포상 금액 (원) *</Form.Label>
                            <Form.Control
                                type="number"
                                name="rewardAmount"
                                value={currentPolicy.rewardAmount}
                                onChange={handleInputChange}
                                min="0"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>설명</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={3}
                                name="description"
                                value={currentPolicy.description}
                                onChange={handleInputChange}
                                placeholder="포상 정책에 대한 설명을 입력하세요"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Check
                                type="checkbox"
                                name="isActive"
                                label="활성화"
                                checked={currentPolicy.isActive}
                                onChange={handleInputChange}
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

export default Policy;