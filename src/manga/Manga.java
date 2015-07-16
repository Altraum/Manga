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
    public static String[] chapterArray;
    public static String[] pageArray;
    public static String[] chapterInfo;
    public static String[] pageInfo;
    public static JComboBox chapterList;
    public static JComboBox pageList;
    public static DefaultListModel listmodel = new DefaultListModel();
    public static JList searchResults = new JList(listmodel);
    public static boolean actionListenerState;
    public Manga(){
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(1920, 1040);
        
        JPanel control = new JPanel();
        JButton prev = new JButton("Previous");
        JButton next = new JButton ("Next");
        chapterList = new JComboBox(chapterInfo);
        pageList = new JComboBox(pageInfo);
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
                    int index = pageList.getSelectedIndex();
                    pageList.setSelectedIndex(index-1);
                }
            }
        );
        
        next.addActionListener(  
            new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    
                    int count = 0;
                    Element el = doc.select("div.moderation_bar.rounded.clear>ul>li>a>img").get(count);
                    while(!el.attr("title").equals("Next Page") ){
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
                    int index = pageList.getSelectedIndex();
                    pageList.setSelectedIndex(index+1);
                }
            }
                
        );
        chapterList.addActionListener(  
            new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    try{
                        System.out.println("Web page selected :" + chapterArray[chapterList.getSelectedIndex()]);
                        doc = Jsoup.connect(chapterArray[chapterList.getSelectedIndex()]).get();
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
                        actionListenerState=false;
                        pageList.removeAllItems();
                        actionListenerState=true;
                        fillPages();
                        for (int count = 0; count < pageArray.length; count++){
                            System.out.println("Page " + (count+1));
                            pageList.addItem(pageInfo[count]);
                        }
                        System.out.println("First pageList: " + pageList.getItemAt(0));
                        Manga.this.add(pane);
                        Manga.this.revalidate();
                    }
                    catch(IOException i){
                        System.out.println("IO exception");
                    }
                    int index = pageList.getSelectedIndex();
                    pageList.setSelectedIndex(index);
                }
            }
        );
        pageList.addActionListener(  
            new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    if(actionListenerState){
                        try{
                            System.out.println("Web page selected :" + pageArray[pageList.getSelectedIndex()]);
                            doc = Jsoup.connect(pageArray[pageList.getSelectedIndex()]).get();
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
            }
        );
    }
    
    public static int ratioWidth(int w, int h){
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
        chapterInfo = new String[chapters.size()];
        while(index < chapters.size()){
            System.out.print("Method start " + (index +1) + ": ");
            chapterArray[index] = (chapters.get(index).attr("value"));
            chapterInfo[index] = (chapters.get(index).text());
            System.out.println(chapterArray[index]);
            index++;
        }
        System.out.println("...fillChapters finished");
        fillPages();
    }
    
    public static void fillPages(){
        System.out.print("fillPages start...");
        Element cssParse = doc.select("div.moderation_bar.rounded.clear>ul>li>select[name=page_select]").first();
        Elements pages = cssParse.select("option");
        int index = 0;
        pageArray = new String[pages.size()];
        pageInfo = new String[pages.size()];
        while(index < pages.size()){
            System.out.print("Method start " + (index +1) + ": ");
            pageArray[index] = (pages.get(index).attr("value"));
            pageInfo[index] = (pages.get(index).text());
            System.out.println(pageArray[index]);
            index++;
        }
        System.out.println("...fillPages finished");
    }
    
    public void actionPerformed(ActionEvent e) {
        
    }
    
    

    public static void main(String[] args)throws IOException {
        String[][] mangaInfo = new String[2][30];
        JScrollPane scrollPane = new JScrollPane(searchResults, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVisible(false);
        JFrame main = new JFrame();
        main.setLayout(new BorderLayout());
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        main.setSize(640, 480);
        JPanel search = new JPanel();
        JTextField searchInput = new JTextField();
        searchInput.setColumns(35);
        JButton selectBtn = new JButton("Select");
        selectBtn.setEnabled(false);
        main.add(scrollPane, BorderLayout.CENTER);
        main.add(selectBtn, BorderLayout.SOUTH);
        JButton searchBtn = new JButton("Search");
        search.add(searchInput);
        search.add(searchBtn);
        selectBtn.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    try{
                        if(!doc.select("table.chapters_list>tbody>tr:nth-child(" + ((searchResults.getSelectedIndex()*2)+2) + ")>td:nth-child(6)").text().equals("--"))
                        {
                            String selectedManga = mangaInfo[1][searchResults.getSelectedIndex()];
                            doc = Jsoup.connect(selectedManga).get();
                            String title = doc.title();
                            System.out.println("Title is: " + title);
                            Element ele = doc.select("tr.row.lang_English.chapter_row").last();
                            Elements row = ele.select("td>a");
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
                            int r = ratioWidth(w, h);
                            pane = new JPanel() {
                                @Override
                                protected void paintComponent(Graphics g) {
                                    super.paintComponent(g);
                                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                                }
                            };
                            Dimension d = new Dimension((1920 - r)/2,h);
                            padLeft.setBackground(Color.black);
                            padRight.setBackground(Color.black);
                            padLeft.setPreferredSize(d);
                            padRight.setPreferredSize(d);
                            window.add(padLeft, BorderLayout.WEST);
                            window.add(padRight, BorderLayout.EAST);
                            window.add(pane, BorderLayout.CENTER);
                            window.setVisible(true);
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "No chapters available", "No Chapters", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    
                    catch(IOException i){
                        System.out.println("IO Exception");
                    }
                }
            }
        );
        searchBtn.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    try{
                        String mangaToFind = searchInput.getText();
                        doc = Jsoup.connect("http://bato.to/search?name_cond=c&name=" + mangaToFind).get();
                        Elements mangaResults = doc.select("div#comic_search_results>table.ipb_table.chapters_list>tbody>tr>td>strong>a");
                        listmodel.removeAllElements();
                        for(int run = 0; run<=29; run++){
                            if(run<mangaResults.size()){
                                mangaInfo[0][run] = mangaResults.get(run).text();
                                mangaInfo[1][run] = mangaResults.get(run).attr("href");
                                listmodel.addElement(mangaInfo[0][run]);
                                System.out.println("Found  manga " + (run+1) + " at " + mangaInfo[1][run]);
                            }
                        }
                        System.out.println("listmodel size: " + listmodel.getSize());
                        System.out.println("searchResults size = " + searchResults.getModel().getSize());
                        if(!scrollPane.isVisible()){
                            scrollPane.setVisible(true);
                        }
                        if(!selectBtn.isEnabled()){
                            selectBtn.setEnabled(true);
                        }
                        main.revalidate();
                    }
                    
                    catch(IOException i){
                        System.out.println("IO Exception");
                    }
                }
            }
        );
        main.add(search, BorderLayout.NORTH);
        main.setVisible(true);
    }
}