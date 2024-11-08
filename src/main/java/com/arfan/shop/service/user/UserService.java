package com.arfan.shop.service.user;

import com.arfan.shop.dto.UserDto;
import com.arfan.shop.model.User;
import com.arfan.shop.request.CreateUserRequest;
import com.arfan.shop.request.UpdateUserRequest;

public interface UserService {

    User getUserById(Long id);

    User createUser(CreateUserRequest request);

    User updateUser(UpdateUserRequest request, Long id);

    void deleteUser(Long id);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();
}
