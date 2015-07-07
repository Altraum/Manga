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
import java.math.*;

public class Manga extends JFrame implements ActionListener {

    /**
     * @param args the command line arguments
     */
    
    public static Document doc = null;
    public static JPanel pane = new JPanel();
    public static JPanel padLeft = new JPanel();
    public static JPanel padRight = new JPanel();
    public static String [] chapterArray;
    public static String[] pageArray;
    public Manga(){
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1920, 1040);
        JPanel control = new JPanel();
        JButton prev = new JButton("Previous");
        JButton next = new JButton ("Next");
        JComboBox chapterList = new JComboBox(chapterArray);
        JComboBox pageList = new JComboBox();
        control.add(prev);
        control.add(chapterList);
        control.add(pageList);
        control.add(next);
        this.add(control, BorderLayout.SOUTH);
        prev.addActionListener(  
            new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    int count = 0;
                    Element el = doc.select("div.moderation_bar.rounded.clear>ul>li>a>img").get(count);
                    while(!el.attr("title").equals("Previous Page") ){
                        el = el.parent();
                        count++;
                        el = doc.select("div.moderation_bar.rounded.clear>ul>li>a>img").get(count);
                        System.out.println(el.attr("title"));
                    }
                    el = el.parent();
                    String l = el.attr("href");
                    try{
                        doc = Jsoup.connect(l).get();
                        Element e1 = doc.select("img#comic_page").first();
                        String attr = e1.attr("src");
                        System.out.println(attr);
                        URL url = new URL(attr);
                        BufferedImage image = ImageIO.read(url);
                        int h = image.getHeight();
                        int w = image.getWidth();
                        int r = ratioWidth(w, h);
                        Manga.this.remove(pane);
                        pane.removeAll();
                        pane = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                            }
                        };
                        Dimension d = new Dimension(((1920 - r)/2), 968);
                        padLeft.setPreferredSize(d);
                        padRight.setPreferredSize(d);
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
                    int count = 0;
                    Element el = doc.select("div.moderation_bar.rounded.clear>ul>li>a>img").get(count);
                    while(!el.attr("title").equals("Next Page") ){
                        el = el.parent();
                        count++;
                        el = doc.select("div.moderation_bar.rounded.clear>ul>li>a>img").get(count);
                        System.out.println(el.attr("title"));
                    }
                    el = el.parent();
                    String l = el.attr("href");
                    try{
                        doc = Jsoup.connect(l).get();
                        Element e1 = doc.select("img#comic_page").first();
                        String attr = e1.attr("src");
                        System.out.println(attr);
                        URL url = new URL(attr);
                        BufferedImage image = ImageIO.read(url);
                        int h = image.getHeight();
                        int w = image.getWidth();
                        int r = ratioWidth(w, h);
                        pane.removeAll();
                        Manga.this.remove(pane);
                        pane = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                            }
                        };
                        System.out.println(r);
                        Dimension d = new Dimension(((1920 - r)/2),h);
                        padLeft.setPreferredSize(d);
                        padRight.setPreferredSize(d);
                        Manga.this.add(pane);
                        Manga.this.revalidate();
                    }
                    catch(IOException i){
                        System.out.println("IO exception");
                    }
                }
            }
                
        );
        chapterList.addActionListener(  
            new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    try{
                        System.out.println("Web page selected :" + chapterList.getSelectedItem().toString());
                        doc = Jsoup.connect(chapterList.getSelectedItem().toString()).get();
                        Element e1 = doc.select("img#comic_page").first();
                        String attr = e1.attr("src");
                        System.out.println(attr);
                        URL url = new URL(attr);
                        BufferedImage image = ImageIO.read(url);
                        int h = image.getHeight();
                        int w = image.getWidth();
                        int r = ratioWidth(w, h);
                        pane.removeAll();
                        Manga.this.remove(pane);
                        pane = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                            }
                        };
                        System.out.println(r);
                        Dimension d = new Dimension(((1920 - r)/2),h);
                        padLeft.setPreferredSize(d);
                        padRight.setPreferredSize(d);
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
    
    public int ratioWidth(int w, int h){
        int rWidth;
        float r;
        if ( h > 968){
            r = (float)968/h;
        }
        else if ( h < 968){
            r = h/(float)968;
        }
        else{
            r = 1;
        }
        rWidth = Math.round(w * r);
        System.out.println("Height: " + h);
        System.out.println("Computer's ratio: " + r);
        System.out.println("Width: " + w);
        System.out.println("New Width: " + rWidth);
        return rWidth;
    }
    
    public static void fillChapters(){
        System.out.print("fillChapter start...");
        Element cssParse = doc.select("div.moderation_bar.rounded.clear>ul>li>select[name=chapter_select]").first();
        Elements chapters = cssParse.select("option");
        int index = 0;
        chapterArray = new String[chapters.size()];
        while(index < chapters.size()){
            System.out.print("Method start " + (index +1) + ": ");
            chapterArray[index] = (chapters.get(index).attr("value"));
            System.out.println(chapterArray[index]);
            index++;
        }
        System.out.println("...fillChapters finished");
    }
    
    public void fillPages(){
    
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
        fillChapters();
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
        Dimension d = new Dimension((1920 - w)/2,h);
        padLeft.setBackground(Color.black);
        padRight.setBackground(Color.black);
        padLeft.setPreferredSize(d);
        padRight.setPreferredSize(d);
        window.add(padLeft, BorderLayout.WEST);
        window.add(padRight, BorderLayout.EAST);
        window.add(pane, BorderLayout.CENTER);
        window.setVisible(true);
    }
}
