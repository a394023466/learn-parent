package learn.guava;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * @version 多值Map测试,
 * @author: Lenovo
 * @date:Create：in 2020/4/30 17:22
 */
public class MultimapTest {
    /**
     * 把map的value封装成list集合，相同key对应的value会放到一个list集合中
     */
    final static ArrayListMultimap<String, String> multimap = ArrayListMultimap.create();

    static {
        multimap.put("a", "a");
        multimap.put("a", "b");
        multimap.put("a", "c");
        multimap.put("a", "d");
    }

    /**
     * 替换指定key下的所有value
     */
    private static void replaceValuesTest() {
        multimap.replaceValues("a", Lists.newArrayList("1", "2", "3"));
        System.out.println(multimap.get("a"));//[1, 2, 3]
    }

    /**
     * 判断key下班是否存在指定的value
     */
    private static void containsEntryTest() {
        System.out.println(multimap.containsEntry("a", "1"));//false
        System.out.println(multimap.containsEntry("a", "d"));//true
    }

    /**
     * 判断Map是否包含指定的value
     */
    private static void containsValueTest() {
        final ArrayList<String> objects = Lists.newArrayList("a");
        final boolean b1 = multimap.containsValue(objects);
        final boolean b2 = multimap.containsValue("a");
        System.out.println(b1);//false
        System.out.println(b2);//true
    }
}
