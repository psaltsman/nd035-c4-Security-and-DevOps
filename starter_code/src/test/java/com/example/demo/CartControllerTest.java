package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void testInit() {

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    /**
     * Tests the happy path.
     */
    @Test
    public void addToCart() {

        //Create a test user and stub it for use in our test
        User user = new User();
        user.setId(0L);
        user.setUsername("TEST");
        user.setPassword("TEST1234");

        Cart userCart = new Cart();
        userCart.setUser(user);
        userCart.setId(2L);
        user.setCart(userCart);

        //Create a test item and stub it for use in out test
        Item item = new Item();
        item.setId(1L);
        item.setName("TEST ITEM");
        item.setPrice(new BigDecimal(2.99));
        item.setDescription("TEST ITEM DESCRIPTION");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findByUsername("TEST")).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("TEST");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);

        assertNotNull(cartResponse);
        assertEquals(200, cartResponse.getStatusCodeValue());

        Cart cart = cartResponse.getBody();

        assertEquals(Long.valueOf(2), cart.getId());
        assertEquals(new BigDecimal(2.99), cart.getTotal());
        assertEquals(1, cart.getItems().size());
        assertNotNull(cart.getUser());

        User cartUser = cart.getUser();
        assertEquals(user.getId(), cartUser.getId());
        assertEquals(user.getUsername(), cartUser.getUsername());
        assertEquals(user.getPassword(), cartUser.getPassword());

        Item cartItem = cart.getItems().get(0);
        assertEquals(item.getId(), cartItem.getId());
        assertEquals(item.getName(), cartItem.getName());
        assertEquals(item.getDescription(), cartItem.getDescription());
        assertEquals(item.getPrice(), cartItem.getPrice());
    }

    /**
     * Attempts to add an item to the cart with a user that is not in the system which should return a 404 status.
     */
    @Test
    public void addToCartNoUserFound() {

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("NOTFOUND");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cart = cartController.addTocart(modifyCartRequest);

        assertEquals(404, cart.getStatusCodeValue());
    }

    /**
     * Attempts to add an item to the cart that is not in the system which should return a 404 status.
     */
    @Test
    public void addToCartNoItemFound() {

        //Create a test user and stub it for use in our test
        User user = new User();
        user.setId(0L);
        user.setUsername("TEST");
        user.setPassword("TEST1234");

        Cart userCart = new Cart();
        userCart.setUser(user);
        userCart.setId(2L);
        user.setCart(userCart);

        when(userRepository.findByUsername("TEST")).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("TEST");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cart = cartController.addTocart(modifyCartRequest);

        assertEquals(404, cart.getStatusCodeValue());
    }

    /**
     * Tests the happy path of removing an item from the cart
     */
    @Test
    public void removeFromCart() {

        //Create a test user and stub it for use in our test
        User user = new User();
        user.setId(0L);
        user.setUsername("TEST");
        user.setPassword("TEST1234");

        //Create a test item and stub it for use in out test
        Item item = new Item();
        item.setId(1L);
        item.setName("TEST ITEM");
        item.setPrice(new BigDecimal(2.99));
        item.setDescription("TEST ITEM DESCRIPTION");

        Cart userCart = new Cart();
        userCart.addItem(item);
        userCart.setUser(user);
        userCart.setId(2L);
        user.setCart(userCart);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findByUsername("TEST")).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("TEST");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponse = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(cartResponse);
        assertEquals(200, cartResponse.getStatusCodeValue());

        Cart cart = cartResponse.getBody();
        assertEquals(0, cart.getItems().size());

    }

    /**
     * Attempts to removed an item from the cart with a user that is not in the system which should return a 404 status.
     */
    @Test
    public void removeItemFromCartNoUserFound() {

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("NOTFOUND");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponse = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(cartResponse);
        assertEquals(404, cartResponse.getStatusCodeValue());

    }

    /**
     * Attempts to removed an item from the cart that is not in the system which should return a 404 status.
     */
    @Test
    public void removeItemFromCartNoItemFound() {

        //Create a test user and stub it for use in our test
        User user = new User();
        user.setId(0L);
        user.setUsername("TEST");
        user.setPassword("TEST1234");

        when(userRepository.findByUsername("TEST")).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("TEST");
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponse = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(cartResponse);
        assertEquals(404, cartResponse.getStatusCodeValue());
    }
}
