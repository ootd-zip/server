package zip.ootd.ootdzip.admin.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.admin.domain.Admin;
import zip.ootd.ootdzip.admin.repository.AdminRepository;
import zip.ootd.ootdzip.admin.service.request.AdminJoinSvcReq;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public void joinAdmin(AdminJoinSvcReq request, User loginUser) {

        if (!loginUser.isAdmin()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER_ERROR);
        }

        if (adminRepository.existsByLoginId(request.getLoginId())) {
            throw new CustomException(ErrorCode.EXISTED_ADMIN_ID);
        }

        Admin admin = Admin.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        adminRepository.save(admin);
    }
}
