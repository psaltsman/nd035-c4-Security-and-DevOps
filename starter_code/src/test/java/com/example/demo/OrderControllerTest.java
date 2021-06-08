package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void testInit() {

        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    /**
     * Tests the order submit process.
     */
    @Test
    public void submit() {

        //Create a test user with a cart that has a single item in it for testing
        User user = new User();
        user.setId(0L);
        user.setUsername("TESTUSER");
        user.setPassword("TESTPASSWORD");

        Item item = new Item();
        item.setId(1L);
        item.setName("TEST_ITEM");
        item.setDescription("TEST_DESCRIPTION");
        item.setPrice(new BigDecimal(1.99));

        List<Item> items = new ArrayList<>();
        items.add(item);

        Cart cart = new Cart();
        cart.setId(2L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(new BigDecimal(1.99));

        user.setCart(cart);

        //Test the setter methods on the UserOrder class
        //This really doesn't do anything but ensure code coverage
        UserOrder userOrderTest = new UserOrder();
        userOrderTest.setId(3L);
        userOrderTest.setUser(user);
        userOrderTest.setItems(items);
        userOrderTest.setTotal(new BigDecimal(1.99));

        UserOrder userOrderExpected = UserOrder.createFromCart(cart);

        //Stub the user and the order for the purposes of this test
        when(userRepository.findByUsername("TESTUSER")).thenReturn(user);
        when(orderRepository.save(userOrderExpected)).thenReturn(userOrderExpected);

        //First try creating an order with a bad username which should return a 404
        ResponseEntity<UserOrder> userOrderResponseNotFound = orderController.submit("ABC");
        assertEquals(404, userOrderResponseNotFound.getStatusCodeValue());

        ResponseEntity<UserOrder> userOrderResponse = orderController.submit("TESTUSER");

        assertNotNull(userOrderResponse);
        assertEquals(200, userOrderResponse.getStatusCodeValue());

        UserOrder userOrder = userOrderResponse.getBody();
        assertEquals(userOrderExpected.getUser(), userOrder.getUser());
        assertEquals(userOrderExpected.getTotal(), userOrder.getTotal());
        assertEquals(userOrderExpected.getItems(), userOrder.getItems());
    }

    @Test
    public void getOrdersForUser() {

        User user = new User();
        user.setId(0L);
        user.setUsername("TESTUSER");
        user.setPassword("TESTPASSWORD");

        Item item = new Item();
        item.setId(1L);
        item.setName("TEST_ITEM");
        item.setDescription("TEST_DESCRIPTION");
        item.setPrice(new BigDecimal(1.99));

        List<Item> items = new ArrayList<>();
        items.add(item);

        Cart cart = new Cart();
        cart.setId(2L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(new BigDecimal(1.99));

        user.setCart(cart);

        UserOrder userOrder = UserOrder.createFromCart(cart);

        List<UserOrder> userOrderList = new ArrayList<>();
        userOrderList.add(userOrder);

        when(userRepository.findByUsername("TESTUSER")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrderList);

        //First try getting an order history with a bad username which should return a 404
        ResponseEntity<List<UserOrder>> userOrderResponseNotFound = orderController.getOrdersForUser("ABC");
        assertEquals(404, userOrderResponseNotFound.getStatusCodeValue());

        ResponseEntity<List<UserOrder>> userOrderResponse = orderController.getOrdersForUser("TESTUSER");
        assertEquals(200, userOrderResponse.getStatusCodeValue());
        List<UserOrder> userOrdersReturned = userOrderResponse.getBody();
        assertEquals(1, userOrdersReturned.size());
    }
}
