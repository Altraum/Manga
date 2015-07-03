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
import javax.swing.event.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class Manga extends JFrame implements ActionListener {

    /**
     * @param args the command line arguments
     */
    
    public static Document doc = null;
    public static JPanel pane = new JPanel();
    public Manga(){
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1920, 1040);
        JPanel control = new JPanel();
        JButton prev = new JButton("Previous");
        JButton next = new JButton ("Next");
        control.add(prev);
        control.add(next);
        this.add(control, BorderLayout.SOUTH);
        prev.addActionListener(  
            new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    String l = doc.title();
                    System.out.println("Title " + l);
                    Element el = doc.select("div.moderation_bar.rounded.clear>ul>li>a").get(2);
                    l = el.attr("href");
                    System.out.println(l);
                    try{
                        doc = Jsoup.connect(l).get();
                        Element e1 = doc.select("img#comic_page").first();
                        String attr = e1.attr("src");
                        System.out.println(attr);
                        URL url = new URL(attr);
                        BufferedImage image = ImageIO.read(url);
                        Manga.this.remove(pane);
                        pane.removeAll();
                        pane = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                            }
                        };
                        Manga.this.add(pane);
                        Manga.this.revalidate();
                    }
                    catch(IOException i){
                        System.out.println("IO exception");
                    }
                }
            }
        );
        
        next.addActionListener(  
            new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    Element el = doc.select("div.moderation_bar.rounded.clear>ul>li>a").get(3);
                    String l = el.attr("href");
                    System.out.println(l);
                    try{
                        doc = Jsoup.connect(l).get();
                        Element e1 = doc.select("img#comic_page").first();
                        String attr = e1.attr("src");
                        System.out.println(attr);
                        URL url = new URL(attr);
                        BufferedImage image = ImageIO.read(url);
                        pane.removeAll();
                        Manga.this.remove(pane);
                        pane = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                            }
                        };
                        Manga.this.add(pane);
                        Manga.this.revalidate();
                    }
                    catch(IOException i){
                        System.out.println("IO exception");
                    }
                }
            }
        );
    }
    
    public void actionPerformed(ActionEvent e) {
        
    }

    public static void main(String[] args)throws IOException {
        
        doc = Jsoup.connect("http://bato.to/comic/_/comics/prison-school-r1011").get();
        String title = doc.title();
        System.out.println("Title is: " + title);
        Elements e = doc.select("tr.row.lang_English.chapter_row");
        Elements row = e.select("td>a");
        String attr = row.attr("href");
        System.out.println(attr);
        doc = Jsoup.connect(attr).get();
        Manga window = new Manga();
        title = doc.title();
        System.out.println("Title is: " + title);
        Element e1 = doc.select("img#comic_page").first();
        System.out.println(e1.attr("src"));
        attr = e1.attr("src");
        
        URL url = new URL(attr);
        
        BufferedImage image = ImageIO.read(url);
        int h = image.getHeight();
        int w = image.getWidth();
        
        pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        window.add(pane, BorderLayout.CENTER);
        window.setVisible(true);
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
