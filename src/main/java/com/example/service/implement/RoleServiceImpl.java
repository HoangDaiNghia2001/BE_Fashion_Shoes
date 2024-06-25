package com.example.service.implement;

import com.example.Entity.Role;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.constant.CookieConstant;
import com.example.constant.RoleConstant;
import com.example.exception.CustomException;
import com.example.repository.RoleRepository;
import com.example.request.RoleRequest;
import com.example.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserServiceImpl userService;

    @Override
    @Transactional
    public Role createRole(RoleRequest roleRequest) throws CustomException {

        roleRequest.setName(roleRequest.getName().toUpperCase());

        Role check = roleRepository.findByName(roleRequest.getName());

        if(check != null){
            throw new CustomException("Role is already exist with name: " + roleRequest.getName());
        }else{
            String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
            User admin = userService.findUserProfileByJwt(token);

            Role role = new Role();

            role.setName(roleRequest.getName());
            role.setDescription(roleRequest.getDescription());
            role.setCreatedBy(admin.getEmail());

            return roleRepository.save(role);
        }
    }

    @Override
    @Transactional
    public String deleteRole(Long id) throws CustomException{
        Optional<Role> role = roleRepository.findById(id);
        if(role.isPresent()){
            if(role.get().getName().equals(RoleConstant.ADMIN)){
                throw new CustomException("The ADMIN role cannot be deleted !!!");
            }else{
                roleRepository.deleteById(id);
                return "Delete success !!!";
            }
        }else{
            throw new CustomException("Role not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public Role updateRole(Long id, RoleRequest roleRequest) throws CustomException {
        Optional<Role> oldRole = roleRepository.findById(id);

        roleRequest.setName(roleRequest.getName().toUpperCase());

        Role check = roleRepository.findByName(roleRequest.getName());

        if (oldRole.isPresent()) {
            if (check == null || check.getName().equals(oldRole.get().getName())){
                String token = jwtProvider.getTokenFromCookie(request, CookieConstant.JWT_COOKIE_ADMIN);
                User admin = userService.findUserProfileByJwt(token);

                oldRole.get().setName(roleRequest.getName());
                oldRole.get().setDescription(roleRequest.getDescription());
                oldRole.get().setUpdateBy(admin.getEmail());

                return roleRepository.save(oldRole.get());
            }else{
                throw new CustomException("The " + check.getName() + " role is already exist !!!");
            }
        }else{
            throw new CustomException("There is no role with id: " + id);
        }
    }

    @Override
    public Role findByName(String name) throws CustomException {
        Role role = roleRepository.findByName(name);
        if (role != null) {
            return role;
        }
        throw new CustomException("There is no role with name: " + name);
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }
}
