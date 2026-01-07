package boot.team.hr.min.account.service;

import boot.team.hr.min.account.repository.AccountRepository;
import boot.team.hr.min.account.dto.AccountDTO;
import boot.team.hr.min.account.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public Long adminSignUp(AccountDTO request) {

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Account admin = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                //무조건 ADMIN
                .role("ADMIN")
                .status("ACTIVE") // 관리자는 바로 활성화
                .build();

        return accountRepository.save(admin).getId();
    }

    @Transactional
    public Long employeeSignUp(AccountDTO request) {

        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Account emp = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("EMP")         // 사원은 EMP
                .status("ACTIVE")    // 바로 활성화
                .build();

        return accountRepository.save(emp).getId();
    }
}
