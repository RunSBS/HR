package boot.team.hr.hyun.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Emp {
    @Id
    public Integer emp_id;
    public String emp_name;
    public Integer dept_id;
    public String email;
    public String role;
    public Timestamp created_at;
    public Timestamp updated_at;
}
