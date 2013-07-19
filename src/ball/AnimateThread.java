/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ball;


/**
 *
 * @author fanzhidong
 */
public class AnimateThread implements Runnable
{
    private Thread  curThread;
    private Animation animation;
    AnimateThread(Animation animation)
    {
        curThread=new Thread(this);//创建线程
        this.animation=animation;
    }

   public void start()//启动线程
    {
       if(curThread.isAlive())//为何无法控制？？？
           curThread.stop();
       else
       {
           System.out.println("Isn't Alive !");
           curThread.start();
           //curThread.run();
       }

    }

    public void run() {
//        int i=0;
//        while(i<100)
//        {
//            System.out.print("H");
//            try
//            {
//                Thread.sleep(100);
//            } catch (InterruptedException ex) {
//
//            }
//        }
        animation.startAnimation();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//
//        }
        return ;

    }

}
