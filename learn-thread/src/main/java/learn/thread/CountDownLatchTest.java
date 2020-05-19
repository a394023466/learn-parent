package learn.thread;

import java.util.concurrent.CountDownLatch;

/**
 * 生成一个CountDownLatch实例。计数数量为10，这表示需要有10个线程来完成任务，
 * 当10个线程全部执行完毕之后在唤醒调用{@link java.util.concurrent.CountDownLatch#wait()}方法的线程
 *
 * @author: liyuzhi
 * @date: 2020/4/30 15:27
 * @version: 1
 */

public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println("线程" + Thread.currentThread().getName() + "执行完毕");
                //计数器减一
                countDownLatch.countDown();
            }).start();
        }

        //上边十个线程全部执行完毕才会唤醒主线程继续执行
        countDownLatch.await();
        System.out.println("10个线程全部执行完毕,主线程被唤醒，继续执行");
    }
}
