package learn.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

/**
 * 自定义锁
 *
 * @author: liyuzhi
 * @date: 2020/4/28 14:12
 * @version: 1
 */
public class CustomeLockTest {

    Lock lock = new CustomeLock();
    int state;

    public static void main(String[] args) throws InterruptedException {
        CustomeLockTest customeLockTest = new CustomeLockTest();

        //等待所有线程执行完毕后在唤醒主线程
        CountDownLatch countDownLatch = new CountDownLatch(1000);

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                customeLockTest.testCustemLockTest();
                countDownLatch.countDown();
            }).start();
        }
        //主线程进入等待状态
        countDownLatch.await();
    }

    public void testCustemLockTest() {
        lock.lock();
        try {
            state++;
            System.out.println(state);
        } finally {
            lock.unlock();
        }
    }
}
