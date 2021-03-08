package lessons1;

import java.util.ArrayList;

public class Box<T extends Fruit> {


    ArrayList<T> box = new ArrayList<>();

    public void addFruit(T fruit, int amount) {
        for (int i = 0; i < amount; i++) {
            box.add(fruit);
        }
        System.out.println(fruit.getWeigth());
    }

    public boolean compare(T fruit, T fruit2) {
        return fruit.getWeigth() * box.size() == fruit2.getWeigth() * box.size();
    }


}
