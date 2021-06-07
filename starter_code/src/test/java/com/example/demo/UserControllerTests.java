package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTests {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void testInit() {

        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUser() {

        when(bCryptPasswordEncoder.encode("TEST1234")).thenReturn("thisIsHashed");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("TEST");
        createUserRequest.setPassword("TEST1234");
        createUserRequest.setConfirmPassword("TEST1234");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertEquals(0, user.getId());
        assertEquals("TEST", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void createUserBadPassword() {

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("TEST");
        createUserRequest.setPassword("TEST");
        createUserRequest.setConfirmPassword("TEST");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findById() {

        User user = new User();
        user.setId(0);
        user.setUsername("TEST");
        user.setPassword("TEST123");

        when(userRepository.findById(0L)).thenReturn(Optional.of(user));

        ResponseEntity<User> findByIdResponse = userController.findById(0l);

        assertNotNull(findByIdResponse);
        assertEquals(200, findByIdResponse.getStatusCodeValue());
    }

    @Test
    public void findByUserName() {

        User user = new User();
        user.setId(0);
        user.setUsername("TEST");
        user.setPassword("TEST123");

        when(userRepository.findByUsername("TEST")).thenReturn(user);

        ResponseEntity<User> findByUserNameResponse = userController.findByUserName("TEST");

        assertNotNull(findByUserNameResponse);
        assertEquals(200, findByUserNameResponse.getStatusCodeValue());
    }
}
