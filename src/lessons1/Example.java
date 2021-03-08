package lessons1;
import java.util.ArrayList;
import java.util.Arrays;

public class Example {
    public static <T> void change(T[] obj, int n1, int n2) {
        T n = obj[n1];
        obj[n1] = obj[n2];
        obj[n2] = n;
        System.out.println(Arrays.toString(obj));
    }

    public static <T> void arraylist(T[] obj) {
        ArrayList<T> arrayList = new ArrayList<>(Arrays.asList(obj));
        System.out.println(arrayList);
    }

    public static void main(String[] args) {
        System.out.println("task_1");
        Integer arr1[] = {1, 2, 3, 4, 5, 6, 7};
        String arr2[] = {"A", "B", "C"};
        change(arr1, 1, 4);
        change(arr2, 1, 2);
        System.out.println("");
        System.out.println("task_2");
        arraylist(arr1);
        System.out.println("");
        System.out.println("task_3");
        Box box = new Box();
        Box<Apple> appleBox = new Box<>();
        Box<Orange> orangeBox = new Box();
        appleBox.addFruit(new Apple(), 12);
        orangeBox.addFruit(new Orange(), 8);
        System.out.println(box.compare(new Apple(), new Orange()));


    }
}
