package learn.thread;

import java.util.concurrent.Semaphore;

/**
 * @version 信号量测试
 * @author: Lenovo
 * @date:Create：in 2020/4/30 15:25
 */
public class SemaphoreTest {
    public static void main(String[] args) {

        /**
         * 定义是3个信号量，表示多个线程并发的时候，只能有3个线程并发执行，剩下的线程只能等待
         * 正在执行的线程释放掉信号量，才可以执行。
         */
        Semaphore semaphore = new Semaphore(3);

        /**
         * 初始化10个线程，利用信号量来控制线程的并发执行
         */
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "获取到信号量，正在执行");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(Thread.currentThread().getName() + "释放了信号量");
                    semaphore.release();
                }
            }).start();
        }
    }
}
