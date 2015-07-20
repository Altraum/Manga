/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manga;

import javax.swing.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;

public class PaintImage extends JPanel
{
    public static BufferedImage image;
    public static Thread imgLoader;
    public static int h;
    public static int w;
    public static int windowH;
    public int imgResize(){
        int rWidth;
        int imageH = image.getHeight();
        int imageW = image.getWidth();
        windowH = super.getHeight();
        float r;
        if ( imageH > windowH){
            r = (float)windowH/imageH;
        }
        else if ( imageH < windowH){
            r = imageH/(float)windowH;
        }
        else{
            r = 1;
        }
        rWidth = Math.round(imageW * r);
        System.out.println("Image Height: " + imageH);
        System.out.println("Window Height: " + windowH);
        System.out.println("Computer's ratio: " + r);
        System.out.println("Image Width: " + imageW);
        System.out.println("Image Width with ratio applied: " + rWidth);
        return rWidth;
    }

    public void paintComponent(Graphics g)
    {
        g.drawImage(image, (getWidth()/2)-(imgResize()/2), 0, imgResize(), windowH, this);
        repaint();
    }

    public void setImage (String mangaUrl){
        imgLoader = (new Thread()
        {
            @Override public void run()
            {
                try
                {
                    URL url = new URL(mangaUrl);
                    image = ImageIO.read(url);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        imgLoader.start();
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
    
    
    public BufferedImage getImage (){
        return image;
    }
}