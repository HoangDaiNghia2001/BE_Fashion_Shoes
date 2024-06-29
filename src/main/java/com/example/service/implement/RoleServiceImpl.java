package com.example.service.implement;

import com.example.Entity.Role;
import com.example.constant.RoleConstant;
import com.example.mapper.RoleMapper;
import com.example.repository.RoleRepository;
import com.example.request.RoleRequest;
import com.example.response.ListRoleResponse;
import com.example.response.Response;
import com.example.response.ResponseError;
import com.example.service.RoleService;
import com.example.util.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MethodUtils methodUtils;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional
    public Role createRole(RoleRequest roleRequest) throws ResponseError {

        roleRequest.setName(roleRequest.getName().toUpperCase());

        Optional<Role> roleExist = roleRepository.findByName(roleRequest.getName());

        if (roleExist.isPresent()) {
            throw new ResponseError(
                    "Role is already exist with name: " + roleRequest.getName(),
                    HttpStatus.CONFLICT.value()
            );
        }
        String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

        Role role = new Role();
        roleMapper.roleRequestToRole(roleRequest, role);
        role.setCreatedBy(emailAdmin);

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Response deleteRole(Long id) throws ResponseError {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseError(
                        "Role not found with id: " + id,
                        HttpStatus.NOT_FOUND.value()
                ));
        if (role.getName().equals(RoleConstant.ADMIN)) {
            throw new ResponseError("The ADMIN role cannot be deleted !!!", HttpStatus.FORBIDDEN.value());
        }
        roleRepository.deleteById(id);
        Response response = new Response();
        response.setMessage("Delete success !!!");
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @Override
    @Transactional
    public Response deleteSomeRoles(List<Long> ids) {
        List<Long> idsMiss = new ArrayList<>();

        for(long id : ids){
            Optional<Role> role = roleRepository.findById(id);
            if(role.isPresent()){
                roleRepository.delete(role.get());
            }else{
                idsMiss.add(id);
            }
        }

        Response response = new Response();
        response.setStatus(HttpStatus.OK.value());

        if(idsMiss.isEmpty()){
            response.setMessage("Delete list roles success !!!");
        }else{
            response.setMessage("Delete list roles success, but not found ids: " + idsMiss.toString());
        }
        return response;
    }

    @Override
    @Transactional
    public Role updateRole(Long id, RoleRequest roleRequest) throws ResponseError {
        Role oldRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseError("There is no role with id: " + id, HttpStatus.NOT_FOUND.value()));

        roleRequest.setName(roleRequest.getName().toUpperCase());

        Optional<Role> checkExist = roleRepository.findByName(roleRequest.getName());

        if (!checkExist.isPresent() || checkExist.get().getName().equals(oldRole.getName())) {
            String emailAdmin = methodUtils.getEmailFromTokenOfAdmin();

            roleMapper.roleRequestToRole(roleRequest, oldRole);
            oldRole.setUpdateBy(emailAdmin);

            return roleRepository.save(oldRole);
        } else {
            throw new ResponseError("The " + checkExist.get().getName() + " role is already exist !!!", HttpStatus.CONTINUE.value());
        }
    }

    @Override
    public ListRoleResponse getAllRoles(int pageIndex, int pageSize) {
        List<Role> roles = roleRepository.findAll();

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), roles.size());

        ListRoleResponse listRoleResponse = new ListRoleResponse();
        listRoleResponse.setRoles(roles.subList(startIndex, endIndex));
        listRoleResponse.setTotal(roles.size());

        return listRoleResponse;
    }

    @Override
    public Role findByName(String name) throws ResponseError {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResponseError("There is no role with name: " + name, HttpStatus.NOT_FOUND.value()));
        return role;
    }
}
