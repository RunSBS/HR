import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const Login = () => {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = async () => {
        try {
            const params = new URLSearchParams();
            params.append("email", email);
            params.append("password", password);

             await axios.post("/back/login", params, {
                withCredentials: true, // 세션 쿠키 포함
                headers: { "Content-Type": "application/x-www-form-urlencoded" }
            });

            alert("로그인 성공");
            navigate("/main");
        } catch (err) {
            console.error(err);
            alert("로그인 실패: " + (err.response?.status || err.message));
        }
    };

    return (
        <div>
            {/* 상단 메뉴 */}
            <div style={{ display: "flex" }}>
                <h1 onClick={() => navigate("/")}>로그인</h1>
                &nbsp;
                <h1 onClick={() => navigate("/sign")}>관리자가입</h1>
                &nbsp;
                <h1 onClick={() => navigate("/empsign")}>사원가입</h1>
            </div>

            {/* 로그인 폼 */}
            <div>
                <div>
                    Email: <input type="text" value={email} onChange={(e) => setEmail(e.target.value)} />
                </div>
                <div>
                    Password: <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                </div>
                <button onClick={handleLogin}>로그인</button>
            </div>
        </div>
    );
};

export default Login;
