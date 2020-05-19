package learn.thread;

/**
 * @version Synchrogazed测试
 * @author: Lenovo
 * @date:Create：in 2020/5/2 15:22
 */
public class SynchrogazedTest {

    // 作用于方法上
    public synchronized void test() {
        System.out.println("synchronized test!!!");
    }

    // 作用于代码块内
    public void testBlock() {
        synchronized (this) {
            System.out.println("synchronized test!!!");
        }
    }
}
