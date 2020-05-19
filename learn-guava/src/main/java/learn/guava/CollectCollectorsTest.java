package learn.guava;

import com.google.common.collect.Comparators;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

/**
 * @author: Lenovo
 * @date:Create：in 2020/4/30 17:58
 */
public class CollectCollectorsTest {

    public static void main(String[] args) {

        final ArrayList<String> strings = Lists.newArrayList("a", "c", "d", "v", "s");


        final Comparator<Optional<String>> first = Comparators.emptiesFirst(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {

                return 0;
            }
        });


    }
}
