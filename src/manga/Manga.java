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
import java.awt.image.BufferedImage;
import java.awt.GraphicsEnvironment;
import java.io.File;
import static manga.Profile.*;

public class Manga extends JFrame implements ActionListener {
    
    public static Document doc = null;
    public static Document buffer = null;
    public static PaintImage pane = new PaintImage();
    public static JPanel control = new JPanel();
    public static String[] chapterURLArray;
    public static String[] pageURLArray;
    public static String[] chapterNameArray;
    public static String[] pageNameArray;
    public static JComboBox chapterComboBox;
    public static JComboBox pageComboBox;
    public static JLabel loadStatus = new JLabel("Unset");
    public static DefaultListModel listmodel = new DefaultListModel();
    public static JList searchResults = new JList(listmodel);
    public static DefaultListModel favoritesModel = new DefaultListModel();
    public static boolean actionListenerState = true;
    public static boolean threadFlag = true;
    public static BufferedImage image;
    public static Thread imageThread;
    public static JButton favBtn = new JButton("Add to Favorites");
    public static JButton currentBtn = new JButton("Set Current Chapter");
    public Manga(){
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try{
                    threadFlag=false;
                    imageThread.join();
                    threadFlag=true;
                    e.getWindow().dispose();
                }
                catch(Exception x){
                    x.printStackTrace();
                }
            }
        });
        this.setSize((int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth(),(int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight());
        
        JButton prev = new JButton("Previous");
        JButton next = new JButton ("Next");
        
        chapterComboBox = new JComboBox(chapterNameArray);
        chapterComboBox.setSelectedIndex(chapterComboBox.getItemCount()-1);
        pageComboBox = new JComboBox(pageNameArray);
        control.add(prev);
        control.add(chapterComboBox);
        control.add(pageComboBox);
        control.add(next);
        control.add(loadStatus);
        this.add(control, BorderLayout.SOUTH);
        prev.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    int index = pageComboBox.getSelectedIndex();
                    if(index-1<0){
                        if(chapterComboBox.getSelectedIndex()==chapterComboBox.getItemCount()-1){
                            JOptionPane.showMessageDialog(null, "This is the first chapter.", "First Chapter", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else{
                            chapterComboBox.setSelectedIndex(chapterComboBox.getSelectedIndex()+1);
                        }
                    }
                    else{
                        if(pane.isImage(index-1)){
                            pane.callImage(index-1);
                            pageComboBox.setSelectedIndex(index-1);
                        }
                    }
                    
                }
            }
        );
        
        next.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    int index = pageComboBox.getSelectedIndex();
                    System.out.println("Pages: " + pageURLArray.length + " Requested Index: " + (index+1));
                    if(index+1>=pageURLArray.length){
                        if(chapterComboBox.getSelectedIndex()==0){
                            JOptionPane.showMessageDialog(null, "This is the latest chapter.", "Last Chapter", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else{
                            chapterComboBox.setSelectedIndex(chapterComboBox.getSelectedIndex()-1);
                            if(!hasReadBefore(Manga.this.getTitle(), (String)chapterURLArray[chapterComboBox.getSelectedIndex()])){
                                updateMangaFromDB(Manga.this.getTitle(), (String)chapterComboBox.getSelectedItem(), (String)chapterURLArray[chapterComboBox.getSelectedIndex()]);
                                addChapterToDB(Manga.this.getTitle(), (String)chapterURLArray[chapterComboBox.getSelectedIndex()]);
                            }
                            
                        }
                    }
                    else{
                        if(pane.isImage(index+1)){
                            pane.callImage(index+1);
                            pageComboBox.setSelectedIndex(index+1);
                        }
                    }  
                }
            }      
        );
        
        favBtn.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    if(hasFavoritedBefore(Manga.this.getTitle())){
                        JOptionPane.showMessageDialog(null, "This manga is already in your favorites.", "Already Favorited", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else{
                        addMangaToDB(Manga.this.getTitle(), (String)chapterComboBox.getSelectedItem(), chapterURLArray[chapterComboBox.getSelectedIndex()]);
                        for(int i =chapterComboBox.getSelectedIndex();i<chapterURLArray.length;i++){
                            addChapterToDB(Manga.this.getTitle(), chapterURLArray[i]);
                        }
                        favoritesModel.removeAllElements();
                        fillModel(favoritesModel);
                        control.remove(favBtn);
                        control.add(currentBtn);
                    }
                    
                }
            }
        );
        
        currentBtn.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    if(getCurrentChapterURLFromDB(Manga.this.getTitle()).equals(chapterURLArray[chapterComboBox.getSelectedIndex()])){
                        JOptionPane.showMessageDialog(null, "This is set as your current chapter.", "Already Current", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else{
                        updateMangaFromDB(Manga.this.getTitle(), (String)chapterComboBox.getSelectedItem(), chapterURLArray[chapterComboBox.getSelectedIndex()]);
                        flushReadChapters(Manga.this.getTitle());
                        for(int i =chapterComboBox.getItemCount()-1;!getCurrentChapterURLFromDB(Manga.this.getTitle()).equals(chapterURLArray[i]);i--){
                            addChapterToDB(Manga.this.getTitle(), chapterURLArray[i]);
                        }
                        addChapterToDB(Manga.this.getTitle(),chapterURLArray[chapterComboBox.getSelectedIndex()]);
                        favoritesModel.removeAllElements();
                        fillModel(favoritesModel);
                    }
                    
                }
            }
        );
        
        chapterComboBox.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    try{
                        if(actionListenerState){
                            threadFlag=false;
                            imageThread.join();
                            threadFlag=true;
                            System.out.println("Web page selected :" + chapterURLArray[chapterComboBox.getSelectedIndex()]);
                            doc = Jsoup.connect(chapterURLArray[chapterComboBox.getSelectedIndex()]).get();
                            Element e1 = doc.select("img#comic_page").first();
                            String attr = e1.attr("src");
                            System.out.println(attr);
                            pane.removeAll();
                            //pane.setImage(attr);
                            actionListenerState=false;
                            pageComboBox.removeAllItems();
                            fillPages(doc);
                            for (int count = 0; count < pageURLArray.length; count++){
                                System.out.println("Page " + (count+1));
                                pageComboBox.addItem(pageNameArray[count]);
                            }
                            actionListenerState=true;
                            System.out.println("First pageList: " + pageComboBox.getItemAt(0));
                            pane.waitForImage();
                            pane.callImage(0);
                            Manga.this.revalidate();
                            Manga.this.repaint();
                        }
                    }
                    catch(IOException i){
                        System.out.println("IO exception");
                    }
                    catch(InterruptedException x){
                        System.out.println("Interrupted exception");
                    }
                    int index = pageComboBox.getSelectedIndex();
                    pageComboBox.setSelectedIndex(index);
                }
            }
        );
        
        pageComboBox.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    if(actionListenerState){
                        int index = pageComboBox.getSelectedIndex();
                        System.out.println("Pages: " + pageURLArray.length + " Requested Index: " + (index+1));
                        if(pane.isImage(index)){
                            pane.callImage(index);
                        }
                        Manga.this.revalidate();
                        Manga.this.repaint();
                    }
                }
            }
        );
    }
    
    public static void fillChapters(Document doc){
        System.out.print("fillChapter start...");
        Element cssParse = doc.select("div.moderation_bar.rounded.clear>ul>li>select[name=chapter_select]").first();
        Elements chapters = cssParse.select("option");
        int index = 0;
        chapterURLArray = new String[chapters.size()];
        chapterNameArray = new String[chapters.size()];
        while(index < chapters.size()){
            System.out.print("Method start " + (index +1) + ": ");
            chapterURLArray[index] = (chapters.get(index).attr("value"));
            chapterNameArray[index] = (chapters.get(index).text());
            System.out.println(chapterURLArray[index]);
            index++;
        }
        System.out.println("...fillChapters finished");
        fillPages(doc);
    }
    
    public static void fillPages(Document doc){
        System.out.print("fillPages start...");
        Element cssParse = doc.select("div.moderation_bar.rounded.clear>ul>li>select[name=page_select]").first();
        Elements pages = cssParse.select("option");
        pageURLArray = new String[pages.size()];
        pageNameArray = new String[pages.size()];
        pane.setImageCount(pages.size());
        for(int index=0;index < pages.size();index++){
            pageURLArray[index] = (pages.get(index).attr("value"));
            pageNameArray[index] = (pages.get(index).text());
        }
        imageThread = (new Thread() {
            public void run() {
                for(int index=0;index < pages.size();index++){
                    pane.setImage(pages.get(index).attr("value"), index);
                    pane.waitForImage();
                    loadStatus.setText((index+1)+"/"+pages.size() + " Pages Loaded");
                    if(!threadFlag){
                        break;
                    }
                }
                loadStatus.setText("Loading Complete!");
                System.out.println("...fillPages finished");
            }
        });
        imageThread.start();
    }
    
    public void actionPerformed(ActionEvent e) {
        
    }
    
    

    public static void main(String[] args)throws IOException {
        File dbFile = new File("MangaProfile.mdb");
        if(!dbFile.isFile()){
            createDB();
        }
        String[][] mangaInfo = new String[2][30];
        JScrollPane scrollPane = new JScrollPane(searchResults, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVisible(false);
        JFrame start = new JFrame();
        start.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        start.setSize(640, 480);
        start.setLocationRelativeTo(null);
        start.setTitle("Manga Viewer");
        JPanel main = new JPanel();
        main.setLayout(new BorderLayout());
        JPanel profile = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Search", main);
        tabbedPane.addTab("Favorites", profile);
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
                        if(searchResults.getSelectedIndex() != -1){
                            if(!doc.select("table.chapters_list>tbody>tr:nth-child(" + ((searchResults.getSelectedIndex()*2)+2) + ")>td:nth-child(6)").text().equals("--"))
                            {
                                String selectedManga = mangaInfo[1][searchResults.getSelectedIndex()];
                                doc = Jsoup.connect(selectedManga).maxBodySize(0).get();
                                String title = doc.title();
                                System.out.println("Title is: " + title);
                                if(!doc.select("tr.row.lang_English.chapter_row").html().equals("")){
                                    System.out.println(doc.select("tr.row.lang_English.chapter_row").size());
                                    Element ele = doc.select("tr.row.lang_English.chapter_row").last();
                                    Elements row = ele.select("td>a");
                                    String attr = row.attr("href");
                                    System.out.println("Attribute incoming");
                                    System.out.println(attr);
                                    ele = doc.select("tr.row.lang_English.chapter_row").last();
                                    row = ele.select("td>a");
                                    attr = row.attr("href");
                                    doc = Jsoup.connect(attr).get();
                                    fillChapters(doc);
                                    Manga window = new Manga();
                                    pane.waitForImage();
                                    pane.callImage(0);
                                    window.setTitle(mangaInfo[0][searchResults.getSelectedIndex()]);
                                    if(hasFavoritedBefore(window.getTitle())){
                                        control.add(currentBtn);
                                    }
                                    else{
                                        control.add(favBtn);
                                    }
                                    window.add(pane, BorderLayout.CENTER);
                                    window.setVisible(true);
                                }
                                else{
                                    JOptionPane.showMessageDialog(null, "No English Chapters Available", "No English", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "No chapters available", "No Chapters", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(null, "Please choose a manga from the results of your search.", "No Manga Selected", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    
                    catch(IOException i){
                        JOptionPane.showMessageDialog(null, "Connection Lost. Please try again.", "No Connection", JOptionPane.INFORMATION_MESSAGE);
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
        
        profile.setLayout(new BorderLayout());
        
        JList favorites = new JList(favoritesModel);
        fillModel(favoritesModel);
        profile.add(favorites, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        profile.add(buttonPanel, BorderLayout.SOUTH);
        JButton continueBtn = new JButton("Select");
        continueBtn.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    try{
                        if(favorites.getSelectedIndex() != -1){
                            String url = selectMangaFromDB((String)favorites.getSelectedValue());
                            Document doc = Jsoup.connect(url).get();
                            fillChapters(doc);
                            Manga window = new Manga();
                            
                            window.setTitle((String)favorites.getSelectedValue());
                            control.add(currentBtn);
                            actionListenerState=false;
                            chapterComboBox.setSelectedItem(selectChapterFromDB(window.getTitle()));
                            actionListenerState=true;
                            pane.waitForImage();
                            pane.callImage(0);
                            window.add(pane, BorderLayout.CENTER);
                            window.setVisible(true);
                        }
                        
                        else{
                            JOptionPane.showMessageDialog(null, "Please choose a manga from your Favorites.", "No Manga Selected", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    
                    catch(IOException i){
                        JOptionPane.showMessageDialog(null, "Connection Lost. Please try again.", "No Connection", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        );
        buttonPanel.add(continueBtn);
        JButton deleteBtn = new JButton("Remove");
        deleteBtn.addActionListener(new ActionListener()  {
                public void actionPerformed(ActionEvent e) {
                    if(favorites.getSelectedIndex() != -1){
                        deleteMangaFromDB((String)favorites.getSelectedValue());
                        favoritesModel.removeAllElements();
                        fillModel(favoritesModel);
                    }

                    else{
                        JOptionPane.showMessageDialog(null, "Please choose a manga from your Favorites.", "No Manga Selected", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        );
        buttonPanel.add(deleteBtn);
        start.add(tabbedPane);
        start.setVisible(true);
    }
}