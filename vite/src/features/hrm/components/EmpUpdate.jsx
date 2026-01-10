import {useState} from "react";
import axios from "axios";

const EmpUpdate = () => {

    const [form, setForm] = useState({
        empId: "",    // 카멜케이스로 통일
        empName: "",
        deptId: "",
        email: "",
        empRole: ""
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value // input의 name 속성값이 여기의 key가 됩니다.
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            // Spring Boot의 @RequestBody가 이 JSON 객체를 EmpDto로 변환합니다.
            const res = await axios.put("/back/hyun/emp/update",
                form,
                {withCredentials: true}
            );
            console.log("수정 성공 :", res.data);
            alert("사원 수정이 완료되었습니다.");
        } catch(e) {
            console.log("수정 실패 :", e);
            alert("수정 실패");
        }
    };

    return (
        <div>
            <h2>사원 수정</h2>
            <form onSubmit={handleSubmit}>
                <table>
                    <tbody>
                        <tr>
                            <th>
                                <label htmlFor="empId">사원번호</label>
                            </th>
                            <td>
                                <input
                                    id="empId"
                                    name="empId" // DTO 필드명과 일치
                                    type="text"
                                    value={form.empId} // state 키값과 일치
                                    onChange={handleChange}
                                />
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <label htmlFor="empName">사원명</label>
                            </th>
                            <td>
                                <input
                                    id="empName"
                                    name="empName" // DTO 필드명과 일치
                                    value={form.empName}
                                    onChange={handleChange}
                                />
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <label htmlFor="deptId">부서번호</label>
                            </th>
                            <td>
                                <input
                                    id="deptId"
                                    name="deptId" // DTO 필드명과 일치
                                    type="number"
                                    value={form.deptId}
                                    onChange={handleChange}
                                />
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <label htmlFor="email">이메일</label>
                            </th>
                            <td>
                                <input
                                    id="email"
                                    name="email"
                                    type="email"
                                    value={form.email}
                                    onChange={handleChange}
                                />
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <label htmlFor="empRole">직급</label>
                            </th>
                            <td>
                                <select
                                    id="empRole"
                                    name="empRole"
                                    value={form.empRole}
                                    onChange={handleChange}
                                >
                                    <option value="">선택하세요</option>

                                    {/* CEO */}
                                    <option value="CEO">CEO</option>

                                    {/* 담당관 */}
                                    <option value="HR">담당관 - 인사</option>
                                    <option value="ATTENDANCE">담당관 - 근태</option>
                                    <option value="SCHEDULE">담당관 - 일정</option>
                                    <option value="EVAL">담당관 - 평가</option>
                                    <option value="REWARD">담당관 - 포상</option>

                                    {/* 팀장 */}
                                    <option value="LEADER">팀장</option>

                                    {/* 사원 */}
                                    <option value="EMP">사원</option>
                                </select>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <button type="submit">사원 수정</button>
            </form>
        </div>
    );
};

export default EmpUpdate;