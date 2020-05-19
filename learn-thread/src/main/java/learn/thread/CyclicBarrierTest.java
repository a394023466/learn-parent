package learn.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @version 栅栏测试
 * @author: Lenovo
 * @date:Create：in 2020/4/30 16:54
 */
public class CyclicBarrierTest {


    public static void main(String[] args) throws InterruptedException {
        /**
         * 满足五个线程，栅栏打开，唤醒被栅栏拦截的所有阻塞线程。
         */
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println("乘客" + Thread.currentThread().getName() + "进入等待区");
                    /**
                     *  开启栅栏的条件，当五个线程执行了 cyclicBarrier.await()方法，那么这五个线程才会继续往下执行，
                     *  如果不满足五个，其他线程会一直阻塞在这里，直到满足五个线程调用了cyclicBarrier.await()方法为止
                     */
                    cyclicBarrier.await();
                    System.out.println("等待区已经满足五个乘客了，乘客" + Thread.currentThread().getName() + "从等待区放行");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
            Thread.sleep(100);
        }
    }
}
