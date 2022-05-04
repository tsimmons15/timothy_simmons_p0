package dev.simmons.utilities.lists;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ListTests {
    @Test
    void addItems() {
        List<String> names = new LinkedList();
        names.add("A");
        names.add("B");
        names.add("C");
        Assertions.assertEquals(3, names.length());
    }

    @Test
    void getByIndex() {
        List<String> names = new LinkedList();
        names.add("A");
        names.add("B");
        names.add("C");
        String result = names.get(1);
        Assertions.assertEquals("B", result);
    }

    @Test
    void manyAdds() {
        List<String> names = new LinkedList();

        for (int i = 0; i < 10000; i++) {
            names.add("test");
        }

        Assertions.assertEquals(10000, names.length());
    }

    @Test
    public void removeByIndex() {
        List<String> names = new LinkedList();
        names.add("A");
        names.add("B");
        names.add("C");
        names.add("D");
        String result = names.remove(1);
        Assertions.assertEquals("B", result);
        Assertions.assertEquals(3, names.length());
        result = names.remove(2);
        Assertions.assertEquals("D", result);
        Assertions.assertEquals(2, names.length());
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> names.remove(-1));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> names.remove(55));

        result = names.remove(0);
        Assertions.assertEquals("A", result);
        Assertions.assertEquals(1, names.length());
        result = names.remove(0);
        Assertions.assertEquals("C", result);
        Assertions.assertEquals(0, names.length());

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> names.remove(0));
    }

    @Test
    public void removeByItem() {
        List<String> names = new LinkedList();
        names.add("A");
        names.add("B");
        names.add("C");
        names.add("D");
        Assertions.assertTrue(names.remove("B"));
        Assertions.assertEquals(3, names.length());
        Assertions.assertTrue(names.remove("D"));
        Assertions.assertEquals(2, names.length());

        Assertions.assertFalse(names.remove("D"));
        Assertions.assertFalse(names.remove("E"));

        Assertions.assertTrue(names.remove("A"));
        Assertions.assertEquals(1, names.length());
        Assertions.assertTrue(names.remove("C"));
        Assertions.assertEquals(0, names.length());

        Assertions.assertFalse(names.remove("A"));
    }

    @Test
    public void emptyListIterator() {
        List<String> empty = new LinkedList<>();
        for(String s : empty) {
            System.out.println(s);
        }
    }

    @Test
    public void listIterator() {
        List<String> string = new LinkedList<>();
        string.add("A");
        string.add("A");
        string.add("A");
        string.add("A");
        string.add("A");
        string.add("A");
        string.add("A");
        string.add("A");
        int i = 0;
        for(String s : string) {
            Assertions.assertEquals("A", s);
            i++;
        }
        Assertions.assertEquals(8, i);
    }
}
