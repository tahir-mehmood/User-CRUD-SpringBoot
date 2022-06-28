package com.tm.tdd.controller;

import com.tm.tdd.dto.UserDTO;
import com.tm.tdd.service.IUserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UsersController {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    IUserService userService;


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.info("UsersController.getAllUsers() START");
        List<UserDTO> users = userService.getAllUsers();
        ResponseEntity responseEntity = new ResponseEntity(users, HttpStatus.OK);
        logger.info("UsersController.getAllUsers() END");
        return responseEntity;
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int userId) {
        logger.info("UsersController.getUserById() START");

        UserDTO users = userService.getUserById(userId);
        ResponseEntity responseEntity = new ResponseEntity(users, HttpStatus.OK);
        logger.info("UsersController.getUserById() END");

        return responseEntity;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createNewUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("UsersController.createNewUser() START");
        UserDTO user = userService.saveUser(userDTO);
        logger.info("UsersController.createNewUser() User Created: " + user.toString());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        ResponseEntity responseEntity = ResponseEntity.created(location).build();
        logger.info("UsersController.createNewUser() END");
        return responseEntity;
    }

    @PutMapping(value = "/{userId}")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable int userId) {
        logger.info("UsersController.updateUser() START");

        UserDTO user = userService.updateUser(userId, userDTO);
        ResponseEntity responseEntity = new ResponseEntity(user, HttpStatus.OK);
        logger.info("UsersController.updateUser() END");
        return responseEntity;
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        logger.info("UsersController.deleteUser() START");
        userService.deleteUserById(userId);
        logger.info("UsersController.deleteUser() END");
        return ResponseEntity.noContent().build();
    }

}
