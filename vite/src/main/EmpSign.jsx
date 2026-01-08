import { useState } from "react";
import { useNavigate } from "react-router-dom";

const EmpSign = () => {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleSignUp = async () => {
        if (!email || !password) {
            alert("이메일과 비밀번호를 입력하세요");
            return;
        }

        try {
            const response = await fetch("/back/signup/emp", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email: email,
                    password: password,
                }),
            });

            if (!response.ok) {
                throw new Error("회원가입 실패");
            }

            alert("사원 회원가입 성공");
            navigate("/"); // 로그인 페이지로 이동

        } catch (error) {
            alert(error.message);
        }
    };

    return (
        <>
            <div style={{ display: "flex" }}>
                <h1 style={{ cursor: "pointer" }} onClick={() => navigate("/")}>
                    로그인
                </h1>
                &nbsp;
                <h1 style={{ cursor: "pointer" }} onClick={() => navigate("/sign")}>
                    관리자가입
                </h1>
                &nbsp;
                <h1 style={{ cursor: "pointer" }} onClick={() => navigate("/empsign")}>
                    사원가입
                </h1>
            </div>

            <h2>사원 회원가입</h2>

            <div>
                email:
                <input
                    type="text"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
            </div>

            <div>
                pwd:
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>

            <button onClick={handleSignUp}>회원가입</button>
        </>
    );
};

export default EmpSign;
