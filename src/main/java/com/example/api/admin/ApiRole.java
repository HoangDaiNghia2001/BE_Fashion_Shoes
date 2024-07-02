package com.example.api.admin;

import com.example.Entity.Role;
import com.example.request.RoleRequest;
import com.example.response.ListRolesResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.response.ResponseError;
import com.example.service.implement.RoleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("roleOfAdmin")
@RequestMapping("/api/admin")
public class ApiRole {
    @Autowired
    private RoleServiceImpl roleService;

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRole(@RequestParam("pageIndex") int pageIndex,
                                        @RequestParam("pageSize") int pageSize) {
        ListRolesResponse listRolesResponse = roleService.getAllRoles(pageIndex, pageSize);

        ResponseData<ListRolesResponse> responseData = new ResponseData<>();
        responseData.setMessage("Get list roles success !!!");
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setResults(listRolesResponse);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/role")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest role) throws ResponseError {
        Role newRole = roleService.createRole(role);

        ResponseData<Role> responseData = new ResponseData<>();
        responseData.setResults(newRole);
        responseData.setMessage("Role added success !!!");
        responseData.setStatus(HttpStatus.CREATED.value());

        return new ResponseEntity<>(responseData, HttpStatus.CREATED);
    }

    @PutMapping("/role")
    public ResponseEntity<?> updateRole(@RequestBody RoleRequest role, @RequestParam("id") Long id) throws ResponseError {
        Role oldRole = roleService.updateRole(id, role);

        ResponseData<Role> responseData = new ResponseData<>();
        responseData.setResults(oldRole);
        responseData.setMessage("Role updated success !!!");
        responseData.setStatus(HttpStatus.OK.value());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @DeleteMapping("/role")
    public ResponseEntity<?> deleteRole(@RequestParam("id") Long id) throws ResponseError {
        Response response = roleService.deleteRole(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/roles/{ids}")
    public ResponseEntity<?> deleteSomeRoles(@PathVariable List<Long> ids) {
        Response response = roleService.deleteSomeRoles(ids);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
