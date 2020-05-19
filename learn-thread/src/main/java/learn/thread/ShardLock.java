package learn.thread;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 共享锁
 *
 * @author: Lenovo
 * @date:Create：in 2020/4/27 11:59
 */
public class ShardLock {
    //多线程操作变量
    int state;
    //创建读写锁
    public final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        ShardLock shardLock = new ShardLock();
        //创建多个线程来操作state变量
        for (int i = 0; i < 10; i++) {
            new Thread(() -> shardLock.write()).start();
        }
    }

    public void read() {
        //加读锁
        readWriteLock.readLock().lock();
        System.out.println("线程：" + Thread.currentThread().getName() + "获取到读锁，当前state值为：" + state);
        try {
            state += 1;
        } finally {
            //解读锁
            readWriteLock.readLock().unlock();
        }
    }

    public void write() {
        //加读锁
        readWriteLock.writeLock().lock();
        System.out.println("线程：" + Thread.currentThread().getName() + "获取到读锁，当前state值为：" + state);
        try {
            state += 1;
        } finally {
            //解读锁
            readWriteLock.writeLock().unlock();
        }
    }
}
