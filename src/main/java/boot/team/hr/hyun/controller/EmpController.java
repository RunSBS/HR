package boot.team.hr.hyun.controller;

import boot.team.hr.hyun.service.EmpService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequestMapping("/hyun/emp")
public class EmpController {
    private EmpService empService;
    @PostMapping("/insertEmp")
    public void insertEmp(Integer emp_id, String emp_name, Integer dept_id, String email, String role, Timestamp created_at, Timestamp updated_at){
        empService.insertEmp(emp_id, emp_name, dept_id, email, role, created_at, updated_at);
    }
}
