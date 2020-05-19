package learn.thread;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;


/**
 * 自定义独占同步锁
 *
 * @author: liyuzhi
 * @date: 2020/4/28 9:14
 * @version: 1
 */
public class CustomeLock implements Lock {

    /**
     * 线程同步标志位，如果AtomicReference里边有对象的引用，则说明已经有线程拿到了锁，其他线程则进入等待状态
     */
    AtomicReference<Thread> owner = new AtomicReference<>();

    /**
     * 线程等待队列，如果AtomicReference里已经有了对象的引用，则说明有线程拿到了锁，再有其他线程来获取锁，则将该线程
     * 放入这队列中，等拿到锁的线程调用unlock方法，唤醒队列里的所有线程。
     */
    LinkedBlockingQueue<Thread> linkedBlockingQueue = new LinkedBlockingQueue<>();

    /**
     * 调用lock方法是记录线程状态，
     */
    @Override
    public void lock() {

        boolean addQ = true;

        //利用while语法来防止LockSupport.park()伪唤醒
        while (!tryLock()) {
            if (addQ) {
                //如果所已经让其他线程获取到了，则将当前线程放入等到队列中
                linkedBlockingQueue.offer(Thread.currentThread());
                addQ = false;
            } else {
                //没有获取到所得线程进行程阻塞，进入等到状态
                LockSupport.park();
            }
        }
        //如果当前线程获取到了锁，则从等待队列中移除
        linkedBlockingQueue.remove(Thread.currentThread());
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    /**
     * 尝试获取锁
     *
     * @return
     */
    @Override
    public boolean tryLock() {
        /**
         * 判断AtomicReference内部引用是否为null，
         * 如果为null说明可以获取到锁,并把当前线程的引用放入AtomicReference内部，返回true
         * 如果不为null说明锁已经被其他线程占用了，返回false
         */
        return owner.compareAndSet(null, Thread.currentThread());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        /**
         * 首先线程调用了unlock方法以后，应该先把自己拿到的锁释放掉，然后再去唤醒其他等待的线程，如果自己当前的锁没有被释放掉
         * 就算唤醒了其他线程，其他线程也拿不到锁，所以只有把自己拿到的锁释放成功，再去唤醒其他线程。
         */
        if (owner.compareAndSet(Thread.currentThread(), null)) {
            /**
             * 唤醒其他正在等待的线程
             */
            final Iterator<Thread> iterator = linkedBlockingQueue.iterator();
            while (iterator.hasNext()) {
                final Thread next = iterator.next();
                LockSupport.unpark(next);
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
