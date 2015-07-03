/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manga;

import java.io.IOException;  
import org.jsoup.Jsoup;  
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.math.BigDecimal;

public class Manga {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws IOException {
        
        Document doc = Jsoup.connect("http://bato.to/comic/_/comics/prison-school-r1011").get();
        String title = doc.title();
        System.out.println("Title is: " + title);
        Elements e = doc.select("tr.row.lang_English.chapter_row");
        Elements row = e.select("td>a");
        String attr = row.attr("href");
        System.out.println(attr);
        doc = Jsoup.connect(attr).get();
        title = doc.title();
        System.out.println("Title is: " + title);
        Element e1 = doc.select("img#comic_page").first();
        System.out.println(e1.attr("src"));
        attr = e1.attr("src");
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        
        URL url = new URL(attr);
        BufferedImage image = ImageIO.read(url);
        int h = image.getHeight();
        int w = image.getWidth();
        frame.setSize(1920, 1040);
        JPanel pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        frame.add(pane, BorderLayout.CENTER);
        JPanel control = new JPanel();
        JPanel padLeft = new JPanel();
        JPanel padRight = new JPanel();
        System.out.println("Width: " + (frame.getWidth() - w)/2);
        int ratio = 0;
        double dratio = (double)968/(double)851;
        System.out.println("Actual Ratio: " + dratio);
        if (image.getHeight() > 968){
            System.out.println("Image shrunk: " + ratio);
            ratio = 968/h;
            padLeft.setPreferredSize(new Dimension((frame.getWidth() - (w * ratio))/2, frame.getHeight()));
            padRight.setPreferredSize(new Dimension((frame.getWidth() - (w * ratio))/2, frame.getHeight()));
        }
        else if (image.getHeight() < 968){
            ratio = 968/h;
            System.out.println("Image enlarged: " + h + ", " + ratio);
            padLeft.setPreferredSize(new Dimension((frame.getWidth() - (w * ratio))/2, frame.getHeight()));
            padRight.setPreferredSize(new Dimension((frame.getWidth() - (w * ratio))/2, frame.getHeight()));
        }
        else {
            padLeft.setPreferredSize(new Dimension((frame.getWidth() - w)/2, frame.getHeight()));
            padRight.setPreferredSize(new Dimension((frame.getWidth() - w)/2, frame.getHeight())); 
        }
        control.add(new Button("Previous"));
        control.add(new Button("Next"));
        frame.add(control, BorderLayout.SOUTH);
        frame.add(padLeft, BorderLayout.WEST);
        frame.add(padRight, BorderLayout.EAST);
        frame.setVisible(true);
        System.out.println("Height: " + (pane.getHeight()));
        //String img = (attr.substring( attr.indexOf("http://"), attr.indexOf(")") ) );
        /*Element column = row.select("td>a").first();
        String chapters = column.attr("href");
        System.out.println(chapters);
        /*Document doc = Jsoup.connect("http://bato.to/read/_/11478/kingdom_v1_ch1_by_exertus-scans/3").get();  
        String title = doc.title();  
        System.out.println("Title is: " + title);
        Element e = doc.select("img#comic_page").first();
        System.out.println(e.attr("src"));
        String attr = e.attr("src");
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        
        URL url = new URL(attr);
        BufferedImage image = ImageIO.read(url);
        int h = image.getHeight();
        int w = image.getWidth();
        frame.setSize(w, h);
        JPanel pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        frame.add(pane);
        frame.setVisible(true);*/
    }
    
}
