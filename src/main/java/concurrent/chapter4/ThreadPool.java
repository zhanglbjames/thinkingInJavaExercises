package concurrent.chapter4;

/**
 * @author zhanglbjames@163.com
 * @version Created on 2017/4/2.
 */
public interface ThreadPool<Job extends Runnable> {
    void execute(Job job);
    void shutdown();
    void addWorkers(int num);
    void removeWorkers(int num);
    int getJobSize();

}
