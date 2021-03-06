/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manga;

import java.awt.Color;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PaintImage extends JPanel
{
    public static BufferedImage[] images;
    public static BufferedImage image;
    public static Thread imgLoader;
    public static Thread imgBuffer;
    public static int h;
    public static int w;
    public static int windowH;
    public static boolean threadFlag=true;
    public int imgResize(){
        int rWidth;
        int imageH = image.getHeight();
        int imageW = image.getWidth();
        windowH = super.getHeight();
        float r;
        r = (float)windowH/imageH;
        rWidth = Math.round(imageW * r);
        /*System.out.println("Image Height: " + imageH);
        System.out.println("Window Height: " + windowH);
        System.out.println("Computer's ratio: " + r);
        System.out.println("Image Width: " + imageW);
        System.out.println("Image Width with ratio applied: " + rWidth);*/
        return rWidth;
    }

    public void paintComponent(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());
        g.drawImage(image, (getWidth()/2)-(imgResize()/2), 0, imgResize(), windowH, this);
    }

    public void setImage (String imgUrl, int index){
        imgLoader = (new Thread()
        {
            @Override public void run()
            {
                System.out.println("setImage Thread #" + (index+1) + " Start");
                try
                {
                    if(threadFlag){
                        Document doc = Jsoup.connect(imgUrl).get();
                        Element e1 = doc.select("img#comic_page").first();
                        String attr = e1.attr("src");
                        System.out.println(attr);
                        URL url = new URL(attr);
                        images[index] = ImageIO.read(url);
                        System.out.println("setImage Thread #" + (index+1) + " Finished"); 
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        imgLoader.start();
    }
    
    public void setImageCount (int count){
        images = new BufferedImage[count];
    }
    
    public void callImage (int index){
        image = images[index];
    }
    
    public boolean isImage (int index){
        if (images[index]==null){
            JOptionPane.showMessageDialog(null, "Page " + (index+1) + " has not been loaded yet.", "Not Loaded", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        else{
            return true;
        }
    }
    
    public void waitForImage(){
        try{
            imgLoader.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void stopImageThread(){
        threadFlag = false;
    }
    
    
    public BufferedImage getImage (){
        return images[0];
    }
}