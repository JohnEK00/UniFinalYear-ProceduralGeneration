package com.PGD;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ArrowTest {

    @Test
    void testArrowInitialization() {
        Arrow arr = new Arrow(1, 0, 0, null);
        assertNotNull(arr);
        assertNotNull(arr.getRectangle());
    }

    /**
     * If arrow is moving right x position should be more than 0
     * if moving left x pos should be less than 0
     */
    @Test
    void testArrowMove() {
        Arrow arr = new Arrow(1, 0, 0, null);
        Collider collider = new Collider(null);
        arr.update(null, collider, 1f);
        assert(arr.getRectangle().getX() > 0);

        Arrow arr2 = new Arrow(-1, 0, 0, null);
        arr2.update(null, collider, 1f);
        assert(arr2.getRectangle().getX() < 0);
    }

    /**
     * When arrow collides with fixed collider it should stop moving
     */
    @Test
    void testArrowCollide() {
        Arrow arr = new Arrow(1, 0, 0, null);
        Collider collider = new Collider(null);
        arr.update(null, collider, 1f);
        Rectangle rectangle = new Rectangle(arr.getRectangle());

        collider.addFixedCollider(rectangle);
        arr.update(null, collider, 1f);

        assert(!arr.isMoving());
    }

    @Test
    void testArrowCollideItem(){
        ItemFactory.getItem("testBox", null, 10, 10, 0, 0, true, false, null, null);
        ItemType itemType = ItemFactory.getItem("testBox");
        Item item = new Item(0, 0, itemType);

        Arrow arr = new Arrow(1, 0, 0, null);
        Collider collider = new Collider(null);
        collider.addInteractableItem(item);

        arr.update(null, collider, 1f);

        assert(!arr.isMoving());
    }
}
