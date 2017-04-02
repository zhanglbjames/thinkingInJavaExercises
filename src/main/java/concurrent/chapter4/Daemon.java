package concurrent.chapter4;

/**
 * @author zhanglbjames@163.com
 * @version Created on 2017/4/1.
 */
import java.util.concurrent.TimeUnit;

/**
 * 当 main函数的开始都结束，没有非守护线程时，则虚拟机推出，即使还在运行守护线程也会立即结束
 *
 * */
public class Daemon {
    public static void main(String[] args){
        Thread thread = new Thread(new DaemonRunner(),"DaemonRunner");
        thread.setDaemon(true);
        thread.start();

    }
    static class DaemonRunner implements Runnable{

        public void run() {
            try{
                TimeUnit.SECONDS.sleep(10);
            }catch(InterruptedException ie){

            }finally {
                System.out.println("DaemonThread finally run.");
            }
        }
    }
}
