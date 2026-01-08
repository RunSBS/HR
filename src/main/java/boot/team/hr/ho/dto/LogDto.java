package boot.team.hr.ho.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class LogDto {
    private Long logId;
    private Long empId;
    private String action;
    private String comment;
    private LocalDateTime createdAt;
}
