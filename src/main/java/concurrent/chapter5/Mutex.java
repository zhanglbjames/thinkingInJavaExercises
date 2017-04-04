package concurrent.chapter5;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author zhanglbjames@163.com
 * @version Created on 2017/4/3.
 */
public class Mutex implements Lock {

    private static class Sync extends AbstractQueuedSynchronizer {
        //是否处于占用状态
        protected boolean isHeldExclusive() {
            return getState() == 1;
        }

        //当状态为 0 时获取锁
        @SuppressWarnings("Since15")
        public boolean tryAcquired(int acquires) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        //释放锁，将状态设置为 0
        @SuppressWarnings("Since15")
        protected boolean tryRelease(int releases) {
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }
        //返回一个Condition，每个condition都包含了一个condition的对队列
        Condition newCondition(){
            return new ConditionObject();
        }
    }

    //仅仅将操作代理到 Sync 上即可
    private final Sync sync = new Sync();

    public void lock() {
        sync.acquire(1);
    }

    public boolean tryLock() {
        return sync.tryAcquired(1);
    }

    public void unlock() {
        sync.release(1);
    }

    public Condition newCondition() {
        return sync.newCondition();
    }
    public boolean isLocked(){
        return sync.isHeldExclusive();


    }
    public boolean hasQueuedThread(){
        return sync.hasQueuedThreads();
    }
    public void lockInterruptibly() throws InterruptedException{
        sync.acquireInterruptibly(1);
    }
    public boolean tryLock(long timeOut, TimeUnit unit) throws InterruptedException{
        return sync.tryAcquireNanos(1,unit.toNanos(timeOut));
    }
}
