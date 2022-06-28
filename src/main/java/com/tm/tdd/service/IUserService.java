package com.tm.tdd.service;

import com.tm.tdd.dto.UserDTO;

import java.util.List;

public interface IUserService {

    public List<UserDTO> getAllUsers();

    public UserDTO getUserById(int userId);

    public UserDTO saveUser(UserDTO user);

    public UserDTO updateUser(int userId, UserDTO user);

    public boolean deleteUserById(int userId);

    public boolean existsByEmail(String email);
}
