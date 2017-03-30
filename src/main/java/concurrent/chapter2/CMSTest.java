package concurrent.chapter2;

/**
 * @author zhanglbjames@163.com
 * @version Created on 2017/3/30.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CMSTest {

    //共享单变量原子操作
    private AtomicInteger age = new AtomicInteger(0);
    //单变量非原子操作
    private int height = 0;
    //包含多变量的对象原子操作
    private AtomicReference<Person> ari = new AtomicReference<Person>(new Person(0, 0));

    public static void main(String[] args) throws Exception {
        final CMSTest cms = new CMSTest();

        List<Thread> list = new ArrayList<Thread>(200);
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    for (int j = 0; j < 100000; j++) {
                        cms.ageSingleSafeIncrease();
                        cms.ageHeightMultipleSafeIncrease();
                        cms.heightUnsafeIncrease();
                    }
                }
            });
            list.add(thread);
        }
        for(Thread thread : list){
            thread.start();
        }

        for (Thread thread:list) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("CMSTest.age = "+cms.age.get());
        System.out.println("CMSTest.height = "+cms.height);
        System.out.println("Person.height = "+cms.ari.get().getHeight()+" Person.age = "+cms.ari.get().getAge());
        System.out.println("time = "+Long.toString(System.currentTimeMillis()-start));

    }
    public void ageSingleSafeIncrease(){
        for (;;){
            int age = this.age.get();
            if (this.age.compareAndSet(age,++age)){
                break;
            }
        }
    }
    public void heightUnsafeIncrease(){
        this.height ++;
    }
    public void ageHeightMultipleSafeIncrease(){
        for (;;){
            Person person = this.ari.get();
            Person nextPerson = new Person(person.getAge()+1,person.getHeight()+1);
            if(this.ari.compareAndSet(person,nextPerson)){
                break;
            }
            nextPerson = null;
        }

    }

    private class Person {
        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        private int age = 0;
        private int height = 0;

        public Person(int age, int height) {
            this.age = age;
            this.height = height;
        }

        @Override
        public boolean equals(Object obj) {

            //不为 null
            if (obj == null) {
                return false;
            }
            //同一对象
            if (this == obj){
                return true;
            }
            //不是同一类型
            if (this.getClass() != obj.getClass()){
                return false;

            }
            Person  person = (Person)obj;
            if (person.getAge() == this.age && person.getHeight() == this.height){
                return true;
            }else{
                return false;
            }

        }

    }
}


