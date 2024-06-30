package com.example.service.implement;

import com.example.Entity.Role;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.repository.UserRepository;
import com.example.request.OtpRequest;
import com.example.request.PasswordRequest;
import com.example.request.ResetPasswordRequest;
import com.example.request.UserRequest;
import com.example.response.*;
import com.example.service.UserService;
import com.example.util.EmailUtil;
import com.example.util.MethodUtils;
import com.example.util.OTPUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private OTPUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MethodUtils methodUtils;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=<>?";
    private static final int PASSWORD_LENGTH = 12;

    private String generateUniqueCode() {
        String code;
        User existingUser;
        do {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            code = LocalDate.now().toString().replaceAll("-", "") + "_" + uuid;
            existingUser = userRepository.findByCode(code);
        } while (existingUser != null);

        return code.toUpperCase();
    }

    private String generatePassword() {
        Random RANDOM = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    @Override
    public UserResponse findUserById(Long id) throws ResponseError {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                                "User not found with id: " + id,
                                HttpStatus.NOT_FOUND.value()));
        return userMapper.userToUserResponse(user);
    }

    @Override
    public UserResponse findUserProfileByJwt(String token) throws ResponseError {
        String email = (String) jwtProvider.getClaimsFormToken(token).get("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseError(
                                "User not found with email: " + email,
                                HttpStatus.NOT_FOUND.value()));
        return userMapper.userToUserResponse(user);
    }

    @Override
    public UserResponse findUserByEmail(String email) throws ResponseError {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseError(
                                "User not found with email: " + email,
                                HttpStatus.NOT_FOUND.value()
                        ));
        return userMapper.userToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRequest userRequest) throws ResponseError {
        // check user is already exist
        Optional<User> userExist = userRepository.findByEmail(userRequest.getEmail());

        if(userExist.isPresent()){
            throw new ResponseError(
                    "User is already exist with email: " + userRequest.getEmail(),
                    HttpStatus.CONFLICT.value()
            );
        }
        User user = new User();
        // Map from DTO to Entity, updating only fields available in DTO
        userMapper.userRequestToUser(userRequest, user);
        Role role = roleService.findByName(RoleConstant.USER);
        user.getRoles().add(role);
        user.setCode(generateUniqueCode());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setGender(userRequest.getGender().toUpperCase());
        user.setCreatedBy(userRequest.getEmail());
        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    public ListUsersResponse filterUserByAdmin(String code, String email, String province, String district, String ward,
                                               int pageIndex, int pageSize) throws ResponseError {
        String emailPresent = methodUtils.getEmailFromTokenOfAdmin();

        List<User> users = userRepository.filterUserByAdmin(code, email, province, district, ward, emailPresent);

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min((startIndex + pageable.getPageSize()), users.size());

        List<UserResponse> userResponseList = new ArrayList<>();

        List<User> usersSubList = users.subList(startIndex, endIndex);
        usersSubList.forEach(user -> {
            UserResponse userResponse = userMapper.userToUserResponse(user);
            // convert Set to String
            StringBuilder sb = new StringBuilder();
            for (Role item : user.getRoles()) {
                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(item.getName());
            }
            userResponse.setRoles(sb.toString());
            userResponseList.add(userResponse);
        });
        ListUsersResponse usersResponse = new ListUsersResponse();
        usersResponse.setUsers(userResponseList);
        usersResponse.setTotal(users.size());

        return usersResponse;
    }

    @Override
    public UserResponse updateInformation(UserRequest userRequest, String email) throws ResponseError {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseError(
                        "User not found with email: " + email,
                        HttpStatus.NOT_FOUND.value()
                ));

        // Map from DTO to Entity, updating only fields available in DTO
        userMapper.userRequestToUser(userRequest, user);
        user.setGender(userRequest.getGender().toUpperCase());
        user.setUpdateBy(email);

        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateInformationUser(UserRequest userRequest) throws ResponseError {
        String email = methodUtils.getEmailFromTokenOfUser();

        return updateInformation(userRequest, email);
    }

    @Override
    @Transactional
    public UserResponse updateInformationAdmin(UserRequest adminRequest) throws ResponseError {
        String email = methodUtils.getEmailFromTokenOfAdmin();

        return updateInformation(adminRequest, email);
    }

    @Override
    public Response changePassword(PasswordRequest passwordRequest, String email) throws ResponseError {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseError(
                        "User not found with email: " + email,
                        HttpStatus.NOT_FOUND.value()
                ));

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            throw new ResponseError("Old password does not match !!!", HttpStatus.BAD_REQUEST.value());
        }
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        user.setCreatedBy(email);

        userRepository.save(user);

        Response response = new Response();
        response.setMessage("Change password success");
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @Override
    @Transactional
    public Response changePasswordUser(PasswordRequest passwordRequest) throws ResponseError {
        String email = methodUtils.getEmailFromTokenOfUser();

        return changePassword(passwordRequest, email);
    }

    @Override
    @Transactional
    public Response changePasswordAdmin(PasswordRequest passwordRequest) throws ResponseError {
        String email = methodUtils.getEmailFromTokenOfAdmin();

        return changePassword(passwordRequest, email);
    }

    @Override
    public String sendOTPCode(String email) throws ResponseError {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseError(
                        "User not found with email: " + email,
                        HttpStatus.NOT_FOUND.value()
                ));
        return String.valueOf(otpUtil.generateOTP());
    }

    @Override
    public Response validateOtp(OtpRequest otpRequest) throws ResponseError {
        String otpCookie = otpUtil.getOtpFromCookie(request);

        if (otpCookie == null) {
            throw new ResponseError("OTP on cookie is empty !!!", HttpStatus.PRECONDITION_FAILED.value());
        }
        if (!otpCookie.equals(otpRequest.getOtp())) {
            throw new ResponseError("The OTP code incorrectly !!!", HttpStatus.CONFLICT.value());
        }
        Response response = new Response();
        response.setMessage("Validate OTP success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public Response resetPassword(ResetPasswordRequest resetPasswordRequest) throws ResponseError {
        String emailCookie = emailUtil.getEmailCookie(request);

        if (emailCookie == null) {
            throw new ResponseError("Email on cookie is empty !!!", HttpStatus.PRECONDITION_FAILED.value());
        }

        User user = userRepository.findByEmail(emailCookie)
                .orElseThrow(() -> new ResponseError(
                        "Email not found in database !!!",
                        HttpStatus.NOT_FOUND.value()
                ));

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userRepository.save(user);

        Response response = new Response();
        response.setMessage("Reset password success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public Long totalUsers() {
        return userRepository.count();
    }

    @Override
    public List<TopFiveUsersBoughtTheMostResponse> getTopFiveUsersBoughtTheMost() {
        return userRepository.getTopFiveUsersBoughtTheMost();
    }

    @Override
    @Transactional
    public UserResponse createUserByAdmin(UserRequest userRequest) throws ResponseError, MessagingException {
        Optional<User> userExist = userRepository.findByEmail(userRequest.getEmail());

        if(userExist.isPresent()){
            throw new ResponseError(
                    "User is already exist with email: " + userRequest.getEmail(),
                    HttpStatus.CONFLICT.value());
        }

        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();
        User user = new User();
        // Map from DTO to Entity, updating only fields available in DTO
        userMapper.userRequestToUser(userRequest, user);
        for (String roleName : userRequest.getRoles()) {
            Role role = roleService.findByName(roleName);
            user.getRoles().add(role);
        }
        String password = generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setCode(generateUniqueCode());
        user.setGender(userRequest.getGender().toUpperCase());
        user.setCreatedBy(emailAdmin);
        user = userRepository.save(user);

        emailUtil.sendPassWordEmail(userRequest.getEmail(), password, user.getLastName() + " " + user.getFirstName());

        return userMapper.userToUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponse updateUserByAdmin(long id, UserRequest userRequest) throws ResponseError, MessagingException {
        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "User not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        // Map from DTO to Entity, updating only fields available in DTO
        userMapper.userRequestToUser(userRequest, user);
        user.getRoles().clear();
        for (String roleName : userRequest.getRoles()) {
            Role role = roleService.findByName(roleName);
            user.getRoles().add(role);
        }
        user.setGender(userRequest.getGender().toUpperCase());
        user.setUpdateBy(emailAdmin);

        user = userRepository.save(user);
        emailUtil.sendNotificationEmail(user.getEmail(), user.getLastName() + " " + user.getFirstName());

        return userMapper.userToUserResponse(user);
    }

    @Transactional
    @Override
    public Response deleteUserByAdmin(long id) throws ResponseError, MessagingException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "User not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        emailUtil.sendNotificationEmailDeleteUser(user.getEmail(), user.getLastName() + " " + user.getFirstName());
        userRepository.delete(user);
        Response response = new Response();
        response.setMessage("Delete user success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Transactional
    @Override
    public Response deleteSomeUsersByAdmin(List<Long> ids) throws MessagingException {
        List<Long> idsMiss = new ArrayList<>();
        for (long id : ids) {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                emailUtil.sendNotificationEmailDeleteUser(user.get().getEmail(), user.get().getLastName() + " " + user.get().getFirstName());
                userRepository.delete(user.get());
            } else {
                idsMiss.add(id);
            }
        }
        Response response = new Response();
        if (idsMiss.isEmpty()) {
            response.setMessage("Delete some users success !!!");
        } else {
            response.setMessage("Delete some users success, but not found some ids: " + idsMiss.toString() + " in list !!!");
        }
        response.setStatus(HttpStatus.OK.value());
        return response;
    }
}
