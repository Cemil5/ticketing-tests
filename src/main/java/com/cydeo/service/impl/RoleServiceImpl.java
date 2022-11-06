package com.cydeo.service.impl;

import com.cydeo.dto.RoleDTO;
import com.cydeo.entity.Role;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.mapper.RoleMapper;
import com.cydeo.repository.RoleRepository;
import com.cydeo.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
//    RoleMapper roleMapper;
    private final MapperUtil mapperUtil;

    @Override
    public List<RoleDTO> listAllRoles() {
        List<Role> roles = roleRepository.findAll();
//        return roles.stream()
//                .map(role -> roleMapper.convertToDto(role))
//                .collect(Collectors.toList());
//        return roles.stream()
//                .map(roleMapper::convertToDto)
//                .collect(Collectors.toList());

        return roles.stream()
                .map(role -> mapperUtil.convert(role, new RoleDTO()))
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("role not found"));
//        return roleMapper.convertToDto(role);
        return mapperUtil.convert(role, new RoleDTO());
    }
}
