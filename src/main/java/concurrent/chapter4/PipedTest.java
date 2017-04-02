package concurrent.chapter4;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * @author zhanglbjames@163.com
 * @version Created on 2017/4/1.
 */
public class PipedTest {

    public static void main(String[] args) throws IOException{
        PipedReader in = new PipedReader();
        PipedWriter out = new PipedWriter();
        out.connect(in);

        Thread printThread = new Thread(new Print(in),"PrintThread");
        printThread.start();
        int receive = 0;
        try{
            while((receive = System.in.read()) != -1){
                out.write(receive);//write(int char)
            }
        }catch(IOException e){
            e.printStackTrace();

        }finally{
            out.close();
        }

    }

    static class Print implements Runnable{

        private PipedReader in;
        public Print(PipedReader in){
            this.in = in;
        }
        public void run() {
            int receive = 0;
            try{
                while((receive = in.read() )!= -1){
                    System.out.print((char) receive);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
