package com.tm.tdd.service.impl;

import com.tm.tdd.domain.entity.User;
import com.tm.tdd.domain.repository.UserRepository;
import com.tm.tdd.dto.UserDTO;
import com.tm.tdd.service.IUserService;
import com.tm.tdd.utils.exceptions.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    private final Log logger = LogFactory.getLog(getClass());
    @Autowired
    UserRepository userRepository;


    @Override
    public List<UserDTO> getAllUsers() {
        logger.info("UsersServiceImpl.getAllUsers() START");

        List<UserDTO> userDtos = new ArrayList<>();

        Iterable<User> userEntities = userRepository.findAll();
        if (userEntities.iterator().hasNext()) {
            Streamable.of(userEntities).forEach(user -> {
                userDtos.add(new UserDTO(user));
            });
            logger.info("UsersServiceImpl.getAllUsers()- Found users. Returning now. END");
            return userDtos;
        } else {
            logger.info("UsersServiceImpl.getAllUsers() : Users were not found. Returning error response.");
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "There are no users present in the system at the moment.");
        }
    }

    @Override
    public UserDTO getUserById(int userId) {
        logger.info("UsersServiceImpl.getUserById() START ID:" + userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UserDTO userDTO = new UserDTO(user.get());
            logger.info("UsersServiceImpl.getUserById(): User found in DB. Returning . END ID:" + userId);
            return userDTO;
        } else {
            logger.info("UsersServiceImpl.getUserById(): User wasn't found in DB. Returning Error response. END ID:" + userId);
            throw new UserNotFoundException(userId);
        }

    }

    @Override
    public UserDTO saveUser(UserDTO user) {
        logger.info("UsersServiceImpl.saveUser() START\n " + user.toString());
        User userEntity = new User();
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setDateOfbirth(user.getDateOfBirth());
        userEntity.setEmail(user.getEmail());
        userEntity = userRepository.save(userEntity);

        user.setId(userEntity.getId());

        logger.info("UsersServiceImpl.saveUser() END Successfully");
        return user;
    }

    @Override
    public UserDTO updateUser(int userId, UserDTO user) {
        logger.info("UsersServiceImpl.updateUser() START ID: " + userId);
        Optional<User> optionalUserEntity = userRepository.findById(userId);
        if (optionalUserEntity.isPresent()) {
            User userEntity = optionalUserEntity.get();
            userEntity.setFirstName(user.getFirstName());
            userEntity.setLastName(user.getLastName());
            userEntity.setDateOfbirth(user.getDateOfBirth());
            userEntity.setEmail(user.getEmail());
            userRepository.save(userEntity);
            user.setId(userId);
            logger.info("UsersServiceImpl.updateUser() User Updated successfully; END ID: " + userId);
            return user;
        } else {
            logger.info("UsersServiceImpl.updateUser() User wasn't found with given ID in DB. Returning error response. END  ID: " + userId);
            throw new UserNotFoundException(userId);
        }

    }

    @Override
    public boolean deleteUserById(int userId) {
        logger.info("UsersServiceImpl.deleteUserById() START ID: " + userId);
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            logger.info("UsersServiceImpl.deleteUserById() User with ID deleted END ID: " + userId);
            return true;
        } else {
            logger.info("UsersServiceImpl.deleteUserById() User with ID wasn't found in DB. Returning error response. END ID: " + userId);
            throw new UserNotFoundException(userId);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        logger.info("UsersServiceImpl.existsByEmail() Checking if user exists in DB with given email: " + email);
        return userRepository.existsByEmail(email);
    }


}
