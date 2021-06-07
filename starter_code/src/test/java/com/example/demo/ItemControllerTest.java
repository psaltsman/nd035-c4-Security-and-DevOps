package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void initTest() {

        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    /**
     * Tests returning all items.
     */
    @Test
    public void getItems() {

        Item item1 = new Item();
        item1.setId(0L);
        item1.setName("TEST ITEM 1");
        item1.setDescription("TEST ITEM 1 DESCRIPTION");
        item1.setPrice(new BigDecimal(1.99));

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("TEST ITEM 2");
        item2.setDescription("TEST ITEM 2 DESCRIPTION");
        item2.setPrice(new BigDecimal(2.99));

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> allItemsResponse = itemController.getItems();

        assertNotNull(allItemsResponse);
        assertEquals(200, allItemsResponse.getStatusCodeValue());

        List<Item> returnedItems = allItemsResponse.getBody();
        assertEquals(2, returnedItems.size());
    }

    /**
     * Tests getting an item by id.
     */
    @Test
    public void getItemById() {

        Item item = new Item();
        item.setId(0L);
        item.setName("TEST ITEM 1");
        item.setDescription("TEST ITEM 1 DESCRIPTION");
        item.setPrice(new BigDecimal(1.99));

        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.of(item));

        ResponseEntity<Item> itemResponse = itemController.getItemById(0L);

        assertNotNull(itemResponse);
        assertEquals(200, itemResponse.getStatusCodeValue());

        Item returnedItem = itemResponse.getBody();

        assertEquals(item.toString(), returnedItem.toString());
        assertEquals(item.hashCode(), returnedItem.hashCode());
        assertTrue(item.equals(returnedItem));
    }

    /**
     * Tests getting an item by name.
     */
    @Test
    public void getItemByName() {

        Item item = new Item();
        item.setId(0L);
        item.setName("TEST ITEM 1");
        item.setDescription("TEST ITEM 1 DESCRIPTION");
        item.setPrice(new BigDecimal(1.99));

        List<Item> items = new ArrayList<>();
        items.add(item);

        when(itemRepository.findByName("TEST ITEM 1")).thenReturn(items);

        ResponseEntity<List<Item>> itemResponse = itemController.getItemsByName("TEST ITEM 1");

        assertNotNull(itemResponse);
        assertEquals(200, itemResponse.getStatusCodeValue());

        List<Item> returnedItems = itemResponse.getBody();

        assertEquals(1, returnedItems.size());
    }
}
