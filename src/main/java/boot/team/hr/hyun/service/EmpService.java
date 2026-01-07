package boot.team.hr.hyun.service;

import boot.team.hr.hyun.entity.Emp;
import boot.team.hr.hyun.repo.EmpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class EmpService {
    private EmpRepository empRepository;

    public void insertEmp(Integer emp_id, String emp_name, Integer dept_id, String email, String role, Timestamp created_at, Timestamp updated_at){
        Emp emp = new Emp();
        emp.setEmp_id(emp_id);
        emp.setEmp_name(emp_name);
        emp.setDept_id(dept_id);
        emp.setEmail(email);
        emp.setRole(role);
        emp.setCreated_at(created_at);
        emp.setUpdated_at(updated_at);
        empRepository.save(emp);
    }
}
