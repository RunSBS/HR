package boot.team.hr.hyun.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmpDto {
    private String emp_id;
    private String emp_name;
    private String dept_id;
    private String email;
    private String role;
    private String created_at;
    private String updated_at;
}
