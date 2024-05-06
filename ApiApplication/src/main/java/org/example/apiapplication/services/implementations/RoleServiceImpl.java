package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.dto.roles.RoleDto;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.repositories.RoleRepository;
import org.example.apiapplication.services.interfaces.RoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleDto> getRoles() {
        List<Role> roles = new ArrayList<>();
        for (Role role : roleRepository.findAll()) {
            roles.add(role);
        }

        return roles.stream()
                .map(x -> new RoleDto(x.getId(), x.getName().name()))
                .toList();
    }
}
