package concurrent.chapter4;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @author zhanglbjames@163.com
 * @version Created on 2017/4/1.
 */
public class WaitNotifyTest {
    static boolean flag = true;
    static Object lock = new Object();

    public static void main(String[] args) throws Exception {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(5);

        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {

        public void run() {
            synchronized (lock) {
                while (flag) {
                    try {
                        System.out.println(System.currentTimeMillis() +
                                "flag is true. wait @ " +
                                new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }

                }
                System.out.println(Thread.currentThread() +
                        "flag is false. running @ " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date())
                );
            }
        }
    }

    static class Notify implements Runnable {

        public void run() {
            synchronized (lock) {
                System.out.println(Thread.currentThread() + "hold lock. notify @ " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                lock.notifyAll();
                flag = false;
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }

            }
            synchronized (lock) {
                System.out.println(Thread.currentThread() + "hold lock again .sleep @" + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
