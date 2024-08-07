package com.maxzamota.spring_sandbox.controllers;

import com.maxzamota.spring_sandbox.model_assemblers.UserModelAssembler;
import com.maxzamota.spring_sandbox.dto.UserDto;
import com.maxzamota.spring_sandbox.exception.BadRequestException;
import com.maxzamota.spring_sandbox.mappers.UserMapper;
import com.maxzamota.spring_sandbox.model.UserEntity;
import com.maxzamota.spring_sandbox.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController implements EntityController<Integer, UserEntity, UserDto> {
    private final UserService userService;
    private final UserMapper mapper;
    private final UserModelAssembler assembler;
    private final PagedResourcesAssembler<UserEntity> pagedAssembler;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(
            UserService userService,
            UserMapper mapper,
            UserModelAssembler assembler,
            PagedResourcesAssembler<UserEntity> pagedAssembler,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.mapper = mapper;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @GetMapping({"/all", "/list"})
    public ResponseEntity<PagedModel<EntityModel<UserEntity>>> getAll(
            @PageableDefault Pageable pageable
    ) {
        Page<UserEntity> users = userService.findAll(pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Page-Number", String.valueOf(users.getNumber()));
        headers.add("X-Page-Size", String.valueOf(users.getSize()));
        PagedModel<EntityModel<UserEntity>> pagedModel = pagedAssembler.toModel(
                users, assembler
        );
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pagedModel);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserEntity>> get(@PathVariable Integer id) {
        UserEntity user = userService.getById(id);
        EntityModel<UserEntity> entityModel = assembler.toModel(user);
        return ResponseEntity.ok(entityModel);
    }

    @Override
    @PostMapping
    public ResponseEntity<EntityModel<UserEntity>> post(@RequestBody UserDto userDto) {
        UserEntity user;
        try {
            user = mapper.fromDto(userDto);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user = userService.save(user);
        EntityModel<UserEntity> userEntityModel = assembler.toModel(user);
        return ResponseEntity
                .created(
                        userEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()
                )
                .body(userEntityModel);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        this.userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserEntity>> update(
            @PathVariable Integer id,
            @RequestBody UserDto userDto
    ) {
        return null;
    }
}
