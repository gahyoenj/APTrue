package aptrue.backend.Admin.Controller;

import aptrue.backend.Admin.Dto.*;
import aptrue.backend.Admin.Entity.Admin;
import aptrue.backend.Admin.Repository.AdminRepository;
import aptrue.backend.Admin.Service.AdminService;
import aptrue.backend.Global.ResultResponse;
import aptrue.backend.Global.Code.SuccessCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminRepository adminRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup (@RequestBody SignupRequestDto signupRequestDto, HttpServletRequest httpServletRequest) {
        SignupResponseDto signupResponseDto = adminService.signup(signupRequestDto, httpServletRequest);
        ResultResponse resultResponse = ResultResponse.of(SuccessCode.SIGN_UP_OK, signupResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        LoginResponseDto loginResponseDto = adminService.login(loginRequestDto, httpServletResponse);
        ResultResponse resultResponse = ResultResponse.of(SuccessCode.LOGIN_OK, loginResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @GetMapping("/admin/list")
    public ResponseEntity<?> adminList(HttpServletRequest httpServletRequest) {
        List<AdminListResponseDto> adminListResponseDtos =adminService.getAdminList(httpServletRequest);
        ResultResponse resultResponse = ResultResponse.of(SuccessCode.GET_ADMIN_LIST, adminListResponseDtos);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }

    @DeleteMapping("/admin/{admin_id}")
    public ResponseEntity<?> deleteAdmin(HttpServletRequest httpServletRequest, @PathVariable int admin_id) {
        adminService.deleteAdmin(httpServletRequest, admin_id);
        ResultResponse resultResponse = ResultResponse.of(SuccessCode.DELETE_ADMIN, admin_id);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }


    @PostMapping("/superAdmin")
    public ResponseEntity<?> superAdmin (@RequestBody SignupRequestDto signupRequestDto) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Admin admin = Admin.builder()
                .name(signupRequestDto.getName())
                .account(signupRequestDto.getAccount())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .phone(signupRequestDto.getPhone())
                .isSuperAdmin(true)
                .createdAt(LocalDateTime.now())
                .build();
        adminRepository.save(admin);
        SignupResponseDto signupResponseDto = SignupResponseDto.builder()
                .adminID(admin.getAdminId())
                .account(admin.getAccount())
                .name(admin.getName())
                .build();
        ResultResponse resultResponse = ResultResponse.of(SuccessCode.SIGN_UP_SUPERUSER, signupResponseDto);
        return ResponseEntity.status(resultResponse.getStatus()).body(resultResponse);
    }
}
