package com.example.service.implement;

import com.example.Entity.Role;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.repository.UserRepository;
import com.example.request.OtpRequest;
import com.example.request.PasswordRequest;
import com.example.request.ResetPasswordRequest;
import com.example.request.UserRequest;
import com.example.response.Response;
import com.example.service.UserService;
import com.example.util.EmailUtil;
import com.example.util.OTPUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    @Override
    public User findUserById(Long id) throws CustomException {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new CustomException("User not found with id: " + id));
    }

    @Override
    public User findUserProfileByJwt(String token) throws CustomException {
        String email = (String) jwtProvider.getClaimsFormToken(token).get("email");
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        }
        throw new CustomException("User not found !!!");
    }

    @Override
    public User findUserByEmail(String email) throws CustomException {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return user;
        }
        throw new CustomException("User not found with email: " + email);
    }

    @Override
    @Transactional
    public void registerUser(UserRequest userRequest) throws CustomException {
        User checkExist = userRepository.findByEmail(userRequest.getEmail());

        if (checkExist != null) {
            throw new CustomException("Email is already exist !!!");
        } else {
            User user = new User();

            Role role = roleService.findByName(RoleConstant.USER);

            user.getRoles().add(role);
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            user.setEmail(userRequest.getEmail());
            user.setMobile(userRequest.getMobile());
            user.setGender(userRequest.getGender().toUpperCase());
            user.setAddress(userRequest.getAddress());
            user.setProvince(userRequest.getProvince());
            user.setDistrict(userRequest.getDistrict());
            user.setWard(userRequest.getWard());
            user.setAvatarBase64(userRequest.getAvatarBase64());
            user.setCreatedBy(userRequest.getEmail());
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void registerAdmin(UserRequest adminRequest) throws CustomException {
        User checkExist = userRepository.findByEmail(adminRequest.getEmail());

        if (checkExist != null) {
            throw new CustomException("Admin whose email: " + adminRequest.getEmail() + " already exists !!!");
        } else {
            User admin = new User();

            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);

            String email = (String) jwtProvider.getClaimsFormToken(token).get("email");
            String createdBy = findUserByEmail(email).getEmail();

            Role userRole = roleService.findByName(RoleConstant.USER);
            Role adminRole = roleService.findByName(RoleConstant.ADMIN);

            admin.getRoles().add(userRole);
            admin.getRoles().add(adminRole);
            admin.setCreatedBy(createdBy);
            admin.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
            admin.setFirstName(adminRequest.getFirstName());
            admin.setLastName(adminRequest.getLastName());
            admin.setGender(adminRequest.getGender().toUpperCase());
            admin.setMobile(adminRequest.getMobile());
            admin.setEmail(adminRequest.getEmail());
            admin.setAddress(adminRequest.getAddress());
            admin.setProvince(adminRequest.getProvince());
            admin.setDistrict(adminRequest.getDistrict());
            admin.setWard(adminRequest.getWard());

            userRepository.save(admin);
        }
    }

    @Override
    public List<User> getAllUser(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    @Transactional
    public String deleteUser(Long id) throws CustomException {
        User user = findUserById(id);
        if (user != null) {
            userRepository.delete(user);
            return "Delete success !!!";
        }
        return "User not found with id: " + id;
    }

    @Override
    public User updateInformation(UserRequest userRequest, String token) throws CustomException, IOException {
        User oldUser = findUserProfileByJwt(token);

        oldUser.setAvatarBase64(userRequest.getAvatarBase64());
        oldUser.setFirstName(userRequest.getFirstName());
        oldUser.setLastName(userRequest.getLastName());
        oldUser.setGender(userRequest.getGender().toUpperCase());
        oldUser.setMobile(userRequest.getMobile());
        oldUser.setUpdateBy(oldUser.getEmail());
        oldUser.setAddress(userRequest.getAddress());
        oldUser.setProvince(userRequest.getProvince());
        oldUser.setDistrict(userRequest.getDistrict());
        oldUser.setWard(userRequest.getWard());

        return userRepository.save(oldUser);
    }

    @Override
    @Transactional
    public User updateInformationUser(UserRequest userRequest) throws CustomException, IOException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);

        return updateInformation(userRequest, token);
    }

    @Override
    @Transactional
    public User updateInformationAdmin(UserRequest adminRequest) throws CustomException, IOException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);

        return updateInformation(adminRequest, token);
    }

    @Override
    public Boolean confirmPassword(PasswordRequest password) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);

        User user = findUserProfileByJwt(token);

        return passwordEncoder.matches(password.getOldPassword(), user.getPassword());
    }

    @Override
    public Response changePassword(PasswordRequest passwordRequest, String token) throws CustomException {
        User user = findUserProfileByJwt(token);

        Response response = new Response();

        if (passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
            user.setCreatedBy(user.getEmail());

            user = userRepository.save(user);

            response.setMessage("Change password success !!!");
            response.setSuccess(true);
            return response;
        } else {
            throw new CustomException("Old password does not match !!!");
        }
    }

    @Override
    @Transactional
    public Response changePasswordUser(PasswordRequest passwordRequest) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_USER);

       return changePassword(passwordRequest, token);
    }

    @Override
    @Transactional
    public Response changePasswordAdmin(PasswordRequest passwordRequest) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);

        return changePassword(passwordRequest, token);
    }

    @Override
    public String sendOTPCode(String email) throws CustomException, MessagingException {
        User checkUser = findUserByEmail(email);
        if (checkUser != null) {
            return String.valueOf(otpUtil.generateOTP());
        }
        throw new CustomException("User not found with email: " + email);
    }

    @Override
    public Response validateOtp(OtpRequest otpRequest) throws CustomException {
        String otpCookie = otpUtil.getOtpFromCookie(request);

        if(otpCookie != null){
            if (otpCookie.equals(otpRequest.getOtp())) {
                Response response = new Response();
                response.setSuccess(true);
                response.setMessage("Validate OTP success !!!");

                return response;
            }
            throw new CustomException("The OTP code incorrectly !!!");
        }
        throw new CustomException("OTP on cookie is empty !!!");
    }

    @Override
    public Response resetPassword(ResetPasswordRequest resetPasswordRequest) throws CustomException {
        String emailCookie = emailUtil.getEmailCookie(request);

        if (emailCookie != null) {

            User user = findUserByEmail(emailCookie);

            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
            user = userRepository.save(user);

            Response response = new Response();
            response.setMessage("Reset password success !!!");
            response.setSuccess(true);

            return response;
        } else {
            throw new CustomException("Email on cookie is empty !!!");
        }
    }
}
