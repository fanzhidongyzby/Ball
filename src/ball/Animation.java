/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ball;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.text.DecimalFormat;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.JPanel;

/**
 *
 * @author fanzhidong
 */
public class Animation extends Canvas implements ActionListener
{
    private int w;//画布的宽度
    private int h;//画布的高度
    private Image buffer=null;//临时图像
    private Timer time;//上一个时间秒，用于控制时间

    private Ball ball;//程序实例
    private JPanel thePanel;


    private int x=0;//原点位置
    private int y=0;
    private int formY;//上一个Y位置，用以判断落地
    private double v;////速度
    private double alpha;//倾角

    private double delay=0.1;//刷新延迟
    private int count=0;//间隔次数

    private double vt;//顺时速度
    private double alphat;//瞬时alpha

    private boolean showPath=true;//显示路径

    public static Graphics gg=null;
    Animation(int w,int h)
    {
        this.w =w; //画布的宽度
        this.h =h; //画布的高度
        this.setSize(w, h);
        x=30;
        y=h/2;
        v=15;
        alpha=60;
        alphat=alpha;
        vt=v;
        buffer = null;//临时图像
        time=new Timer((int)(getDelay()*1000),this);//创建计时器

        ball=Ball.getInstance();//获取实例
        thePanel=ball.getPnlAnimation();//获取面板

        this.setBackground(Color.lightGray);
        this.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                //validate();
                repaint();
                
                //thePanel.validate();
                //thePanel.repaint();                
            }

        });
    }


    @Override

    public void paint(Graphics g)
    {
        Animation.gg=g;
        //绘制落地效果
        boolean drop=false;
        if(alpha>0&&v>0)//上抛才会落地
        {
            if(y>h/2&&formY<=h/2)
                drop=true;
            else
                drop=false;
        }
        if (drop)//有内部类的多线程实现特效？？？
        {
            g.setColor(Color.green);
            double x2 = 30 + v * v * Math.sin(2 * alpha * Math.PI / 180) / 9.8 * 20;//另一个根，即一次项系数
            g.drawOval((int) x2 - 15, h / 2 - 15, 2*15, 2*15);
//            g.setColor(Color.yellow);
//            new Thread(new Runnable()
//            {
//                public void run()
//                {
//                    Graphics ig=Animation.gg;
//                    double x2 = 30 + v * v * Math.sin(2 * alpha * Math.PI / 180) / 9.8 * 20;//另一个根，即一次项系数
//
//                    //ig.fillOval((int) x2-20 , h / 2-20 , 40, 40);
//                    for(int i=1;i<=30;i++)
//                    {
//                        ig.drawOval((int) x2 - i, h / 2 - i, 2*i, 2*i);
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException ex) {
//
//                        }
//                    }
//
//                }
//            }).start();
            
            
        }

        //g.clearRect(0, 0, w, h);
        g.setColor(Color.darkGray);
        Integer n;
        g.drawLine(0, h/2, w, h/2);//x-ray
        g.fillPolygon(new int[]{w-10,w,w-10}, new int[]{h/2-3,h/2,h/2+3} , 3);
        int i;
        for(i=50,n=new Integer(1);i<w-20;i+=20,n++)
        {
            String ns=n.toString();
            g.drawLine(i, h/2-2, i, h/2+2);
            g.drawString(ns, i-4, h/2+15);
        }
        g.drawString("X/m", w-20+2, h/2+15);
        g.drawLine(30, h, 30, 0);//y-ray
        int a[]=new int[]{27,30,33};
        g.fillPolygon(new int[]{27,30,33}, new int[]{10,0,10} , 3);
        for(i=h/2+20,n=new Integer(-1);i<h;i+=20,n--)
        {
            String ns=n.toString();
            g.drawLine(28, i, 32, i);
            g.drawString(ns, 10,i+4);
        }
        for(i=h/2-20,n=new Integer(1);i>20;i-=20,n++)
        {
            String ns=n.toString();
            g.drawLine(28, i, 32, i);
            g.drawString(ns, 10,i+4);
        }
        g.drawString("Y/m", 5, 20);

        //绘制路径
        this.drawPath(isShowPath(),g);

        //绘制球
        g.setColor(Color.red);
        g.fillOval(x-5, y-5, 10, 10);//ball
        //绘制起始速度符号
        g.setColor(Color.blue);
        this.drawArow(30, h/2, alpha, v*10, 10, 50, g);

        //绘制瞬时速度
        if(!time.isRunning())//时间停止,重新计算
        {
            x = (int) (30 + getV() * Math.cos(getAlpha() * Math.PI / 180) * getDelay() * count * 20);
            y = (int) (h / 2 - (getV() * Math.sin(getAlpha() * Math.PI / 180) * getDelay() * count - 0.5 * 9.8 * Math.pow(getDelay() * count, 2)) * 20);
            double vx = getV() * Math.cos(getAlpha() * Math.PI / 180);//x分速度v0*cos(a)
            double vy = getV() * Math.sin(getAlpha() * Math.PI / 180) - 9.8 * getDelay() * count;//y分速度v0*sin(a)-gt
            vt = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));//合速度
            alphat = Math.atan(vy / vx) * 180 / Math.PI;//速度方向角
        }
        g.setColor(Color.red);
        this.drawArow(x, y, alphat, vt*10, 8, 30, g);



        
        

    }
    private void drawArow(int x1,int y1,double alp,double l ,int width,int length,Graphics g)//绘制箭头
    {
        double alph=alp*Math.PI/180;
        int x2=(int)(x1+l*Math.cos(alph));
        int y2=(int)(y1-l*Math.sin(alph));
        g.drawLine( x1, y1, x2, y2);//绘制线
        
        double beta=Math.atan(width/2.0/length);//箭头半分角(注意double类型！！！)
        double al=Math.sqrt(length*length+0.25*width*width);//箭头的侧长
        
        //箭头的锚点定位
        int [] xx=new int[]
        {
            (int)(x2-al*Math.cos(alph-beta))
                    ,(int)(x2-al*Math.cos(alph+beta))
                    ,x2
        };
        int [] yy=new int[]
        {
            (int)(y2+al*Math.sin(alph-beta))
                    ,(int)(y2+al*Math.sin(alph+beta))
                    ,y2
        };
        g.fillPolygon(xx, yy , 3);
    }
    @Override
    public void update(Graphics g) //实现双缓冲！！！
    {
        buffer = createImage(getWidth(), getHeight());
        Graphics GraImage = buffer.getGraphics();
        paint(GraImage);
        GraImage.dispose();
        g.drawImage(buffer,0,0,null);
    }

    public void startAnimation()
    {
        time.restart();
        count=0;
        alphat=alpha;
        vt=v;        
    }
    public void pauseAnimation()
    {
        time.stop();
    }
    public void continueAnimation()
    {
        time.start();
    }

    public void stopAnimation()
    {
        count=0;
        alphat=alpha;
        vt=v;
        time.stop();
        x=30;
        y=h/2;
        repaint();
    }
    public void drawPath(boolean draw,Graphics g)
    {
        if(!draw)
            return ;
        int xx=30;
        int yy=h/2;
        g.setColor(Color.orange);
        for(int ct=0;xx<=w&&yy<=h&&y>=0;ct++)
        {
            xx=(int)(30+v*Math.cos(alpha*Math.PI/180)*getDelay()*ct*20);
            yy=(int)(h/2-(v*Math.sin(alpha*Math.PI/180)*getDelay()*ct-0.5*9.8*Math.pow(getDelay()*ct, 2))*20);
            g.fillOval(xx-5, yy-5, 10, 10);//ball
        }

    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==time)
        {
            if(x>w||y>h||y<0)
            {
                x=30;
                y=h/2;
                //time.stop();
                //System.out.println(count);
                count=0;
                repaint();
                //return;
            }
            count++;
            formY=y;//记录上一个y
            x=(int)(30+getV()*Math.cos(getAlpha()*Math.PI/180)*getDelay()*count*20);
            y=(int)(h/2-(getV()*Math.sin(getAlpha()*Math.PI/180)*getDelay()*count-0.5*9.8*Math.pow(getDelay()*count, 2))*20);
            //修改坐标值
            double posX=(x-30)/20;
            double posY=(h/2-y)/20;
            String s="("+String.valueOf(posX)+" m,"+String.valueOf(posY)+" m)";
            ball.getLbPos().setText(s);
            //修改瞬时速度，以及顺时alpha
            DecimalFormat df=new DecimalFormat("##0.00000");//格式化小数位数

            double vx=getV()*Math.cos(getAlpha()*Math.PI/180);//x分速度v0*cos(a)
            double vy=getV()*Math.sin(getAlpha()*Math.PI/180)-9.8*getDelay()*count;//y分速度v0*sin(a)-gt
            vt=Math.sqrt(Math.pow(vx, 2)+Math.pow(vy, 2));//合速度
            alphat=Math.atan(vy/vx)*180/Math.PI;//速度方向角
            
            ball.getLbVt().setText(df.format(vt)+" m/s");
            ball.getLbAt().setText(df.format(alphat)+"°");
            //this.invalidate();
            
            this.repaint();
            //System.out.println(getDelay()*count);

        }
    }

    //计算轨迹方程
    public void calTraitFun()
    {

        //计算抛物线方程
        DecimalFormat df = new DecimalFormat("##0.00000");//格式化小数位数
        double x2 = v * v * Math.sin(2 * alpha * Math.PI / 180) / 9.8;//另一个根，即一次项系数
        double h0 = Math.pow(v * Math.sin(alpha * Math.PI / 180), 2) / 2 / 9.8;//最高点
        double a = h0 / Math.pow(0.5 * x2, 2);//二次项系数,绝对值

        String fun ;
        if(alpha==90||alpha==-90||v==0)
        {
            fun=new String("x = 0");
        }
        else
        {
            if(x2<0)
                fun= new String("y = " + df.format(-a) + "x^2 - " + df.format(-a * x2) + "x");
            else
                fun= new String("y = " + df.format(-a) + "x^2 + " + df.format(a * x2) + "x");
        }
        
        ball.getLbFun().setText(fun);

        if (alpha > 0)//斜上抛
        {            
            ball.getLbMaxH().setText(df.format(h0)+" m");
            ball.getLbGrdPos().setText(df.format(x2)+" m");
        }
        else//下、平抛
        {
            ball.getLbMaxH().setText("0.00000 m");
            ball.getLbGrdPos().setText("XXX");
        }
    }
    /**
     * @return the v
     */
    public double getV() {
        return v;
    }

    /**
     * @param v the v to set
     */
    public void setV(double v) {
        this.v = v;
    }

    /**
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha to set
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * @return the delay
     */
    public double getDelay() {
        return delay;
    }

    /**
     * @param delay the delay to set
     */
    public void setDelay(double delay) {
        this.delay = delay;
    }

    /**
     * @return the showPath
     */
    public boolean isShowPath() {
        return showPath;
    }

    /**
     * @param showPath the showPath to set
     */
    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }

}
