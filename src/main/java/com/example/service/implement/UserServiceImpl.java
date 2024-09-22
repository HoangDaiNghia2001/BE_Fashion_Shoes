package com.example.service.implement;

import com.example.Entity.CustomUserDetails;
import com.example.Entity.Role;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.repository.UserRepository;
import com.example.request.OtpRequest;
import com.example.request.PasswordRequest;
import com.example.request.ResetPasswordRequest;
import com.example.request.UserRequest;
import com.example.response.ListUsersResponse;
import com.example.response.Response;
import com.example.response.TopFiveUsersBoughtTheMostResponse;
import com.example.response.UserResponse;
import com.example.service.EmailService;
import com.example.service.UserService;
import com.example.util.MethodUtils;
import com.example.util.OTPUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private EmailService emailService;
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
    public CustomUserDetails loadUserByUsername(String email) throws CustomException {
        User user = this.findUserByEmail(email);

        if(!user.isActive()){
            throw new CustomException(
                    "Your account has been disabled !!!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setUser(user);

        return userDetails;
    }

    @Override
    public Authentication authenticate(String email, String password) throws CustomException {
        CustomUserDetails user = this.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(
                    "Password incorrect !!!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public User getById(Long id) throws CustomException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "User not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()));
        return user;
    }

    @Override
    public UserResponse findUserProfileByJwt(String token) throws CustomException {
        String email = (String) jwtProvider.getClaimsFormToken(token).get("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        "User not found with email: " + email,
                        HttpStatus.NOT_FOUND.value()));
        return userMapper.userToUserResponse(user);
    }

    @Override
    public User findUserByEmail(String email) throws CustomException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        "User not found with email: " + email,
                        HttpStatus.NOT_FOUND.value()
                ));
        return user;
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRequest userRequest) throws CustomException {
        // check user is already exist
        Optional<User> userExist = userRepository.findByEmail(userRequest.getEmail());

        if (userExist.isPresent()) {
            throw new CustomException(
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
    public ListUsersResponse filterUserByAdmin(String code, String email, String province, String district, String ward, boolean inactive,
                                               int pageIndex, int pageSize) throws CustomException {
        String emailPresent = methodUtils.getEmailFromTokenOfAdmin();

        List<User> users = userRepository.filterUserByAdmin(code, email, province, district, ward, emailPresent, inactive);

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
    public UserResponse updateInformation(UserRequest userRequest, String email) throws CustomException {

        User user = this.findUserByEmail(email);

        // Map from DTO to Entity, updating only fields available in DTO
        userMapper.userRequestToUser(userRequest, user);
        user.setGender(userRequest.getGender().toUpperCase());
        user.setUpdateBy(email);

        return userMapper.userToUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateInformationUser(UserRequest userRequest) throws CustomException {
        String email = methodUtils.getEmailFromTokenOfUser();

        return updateInformation(userRequest, email);
    }

    @Override
    @Transactional
    public UserResponse updateInformationAdmin(UserRequest adminRequest) throws CustomException {
        String email = methodUtils.getEmailFromTokenOfAdmin();

        return updateInformation(adminRequest, email);
    }

    @Override
    public Response changePassword(PasswordRequest passwordRequest, String email) throws CustomException {
        User user = this.findUserByEmail(email);

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            throw new CustomException("Old password does not match !!!", HttpStatus.BAD_REQUEST.value());
        }
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        user.setUpdateBy(email);

        userRepository.save(user);

        Response response = new Response();
        response.setMessage("Change password success");
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @Override
    @Transactional
    public Response changePasswordUser(PasswordRequest passwordRequest) throws CustomException {
        String email = methodUtils.getEmailFromTokenOfUser();

        return changePassword(passwordRequest, email);
    }

    @Override
    @Transactional
    public Response changePasswordAdmin(PasswordRequest passwordRequest) throws CustomException {
        String email = methodUtils.getEmailFromTokenOfAdmin();

        return changePassword(passwordRequest, email);
    }

    @Override
    public Response validateOtp(OtpRequest otpRequest) throws CustomException {
        String otpCookie = otpUtil.getOtpFromCookie(request);

        if (otpCookie == null) {
            throw new CustomException("OTP on cookie is expired !!!", HttpStatus.PRECONDITION_FAILED.value());
        }
        if (!otpCookie.equals(otpRequest.getOtp())) {
            throw new CustomException("The OTP code incorrectly !!!", HttpStatus.CONFLICT.value());
        }
        Response response = new Response();
        response.setMessage("Validate OTP success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public Response resetPassword(ResetPasswordRequest resetPasswordRequest) throws CustomException {
        String emailCookie = emailService.getEmailCookie(request);

        if (emailCookie == null) {
            throw new CustomException("Email on cookie is empty !!!", HttpStatus.PRECONDITION_FAILED.value());
        }

        User user = this.findUserByEmail(emailCookie);

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
    public UserResponse createUserByAdmin(UserRequest userRequest) throws CustomException, MessagingException {
        Optional<User> userExist = userRepository.findByEmail(userRequest.getEmail());

        if (userExist.isPresent()) {
            throw new CustomException(
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
        String password = this.generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setCode(this.generateUniqueCode());
        user.setGender(userRequest.getGender().toUpperCase());
        user.setCreatedBy(emailAdmin);
        user = userRepository.save(user);

        // send email to the new user
        Context context = new Context();
        context.setVariable("firstName", user.getFirstName());
        context.setVariable("newPassword", password);

        emailService.sendEmail(user.getEmail(), "Welcome! Your Account Has Been Created Successfully", "first_password_email", context);

        return userMapper.userToUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponse updateUserByAdmin(long id, UserRequest userRequest) throws CustomException, MessagingException {
        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        User user = this.getById(id);

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

        Context context = new Context();
        context.setVariable("firstName", user.getFirstName());
        context.setVariable("notification", "Your information has been updated");
        context.setVariable("description", "We have successfully updated your account information. If you did not request this change or if you have any concerns, please reach out to our support team for assistance.");

        emailService.sendEmail(user.getEmail(),"Your Account Information Has Been Successfully Updated","notification_email", context);

        return userMapper.userToUserResponse(user);
    }

    @Transactional
    @Override
    public Response deleteUserByAdmin(long id) throws CustomException, MessagingException {
        User user = this.getById(id);

        if(user.isActive()){
            throw new CustomException(
                    "Users should be disabled before being permanently deleted !!!",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        // send email to the user
        Context context = new Context();
        context.setVariable("firstName", user.getFirstName());
        context.setVariable("notification", "Your account has been Delete");
        context.setVariable("description", "We regret to inform you that your account has been permanently deleted. This action is irreversible, and all your data has been removed from our system. If you have any questions or need further assistance, please contact our support team.");

        emailService.sendEmail(user.getEmail(),"Important: Your Account Has Been Delete","notification_email", context);

        userRepository.delete(user);
        Response response = new Response();
        response.setMessage("Delete user success !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Transactional
    @Override
    public Response deleteSomeUsersByAdmin(List<Long> ids) throws MessagingException, CustomException {
        // lấy ra danh sách user theo danh sách ids
        List<User> usersDelete = userRepository.findAllById(ids);

        // lấy ra danh sách ids theo list user trên
        List<Long> idsUserDelete = usersDelete.stream().map(User::getId).collect(Collectors.toList());

        // lọc ra những id không có user
        List<Long> idsMiss = ids.stream().filter(id -> !idsUserDelete.contains(id)).collect(Collectors.toList());

        // send email to the user
        Context context = new Context();
        context.setVariable("notification", "Your account has been Delete");
        context.setVariable("description", "We regret to inform you that your account has been permanently deleted. This action is irreversible, and all your data has been removed from our system. If you have any questions or need further assistance, please contact our support team.");

        for(User user : usersDelete){
            if (user.isActive()){
                throw new CustomException(
                        "Users " + user.getEmail() + " should be disabled before being permanently deleted !!!",
                        HttpStatus.BAD_REQUEST.value()
                );
            }

            context.setVariable("firstName", user.getFirstName());
            emailService.sendEmail(user.getEmail(),"Important: Your Account Has Been Delete","notification_email", context);

            // delete user
            userRepository.delete(user);
        }

        String message = idsMiss.isEmpty() ? "Delete some users success !!!"
                : "Delete some users success, but not found some ids: " + idsMiss.toString() + " in list !!!";
        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage(message);

        return response;
    }

    @Override
    public Response deactivateUser(Long id) throws CustomException, MessagingException {
        User user = this.getById(id);
        user.setActive(false);
        userRepository.save(user);

        Context context = new Context();
        context.setVariable("firstName", user.getFirstName());
        context.setVariable("notification", "Your account has been deactivated");
        context.setVariable("description", "We regret to inform you that your account has been deactivated due to inactivity or a violation of our terms of service. If you believe this is a mistake or if you would like to discuss this further, please contact our support team.");

        emailService.sendEmail(user.getEmail(),"Important: Your Account Has Been Deactivated","notification_email", context);

        Response response = new Response();
        response.setMessage("Deactivate user successfully !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    @Override
    public Response reactivateUser(Long id) throws CustomException, MessagingException {
        User user = this.getById(id);
        user.setActive(true);
        userRepository.save(user);

        Context context = new Context();
        context.setVariable("firstName", user.getFirstName());
        context.setVariable("notification", "Your account has been reactivated");
        context.setVariable("description", "We are pleased to inform you that your account has been reactivated. You can now access all the features and services. If you encounter any issues or have any questions, please feel free to contact our support team.");

        emailService.sendEmail(user.getEmail(),"Important: Your Account Has Been Reactivated","notification_email", context);

        Response response = new Response();
        response.setMessage("Activate user successfully !!!");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }
}
