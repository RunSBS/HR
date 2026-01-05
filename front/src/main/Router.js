import { Routes, Route } from 'react-router-dom';
import Login from './Login';
import Sign from './Sign';
import Main from './Main';
import Calender from "../features/일정/pages/Calender";
import Home from './Home'
function Router() {
    return (
        <Routes>
            <Route path="/" element={<Login />} />
            <Route path="/sign" element={<Sign />} />
            <Route path="/main" element={<Main />}>
                <Route index  element={<Home />}/>   {/* /main */}
                <Route path="schedule/calendar"   element={<Calender />}/>
                <Route path="schedule/my" />
                <Route path="schedule/team" />
            </Route>
        </Routes>
    );
}

export default Router;
