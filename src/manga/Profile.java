/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manga;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultListModel;

/**
 *
 * @author Basil
 */
public class Profile{
    
    public static File dbFile = new File("MangaProfile.mdb");
    public static String path = dbFile.getAbsolutePath();
    public static void createDB(){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path +";Newdatabaseversion=V2003";
            conn = DriverManager.getConnection(url);

            //Also Ucanaccess supports create table as () syntax:
            PreparedStatement createStatement = conn.prepareStatement("CREATE TABLE Profiles"
                + "(Manga varchar(255),"
                + "Chapter varchar(255),"
                + "ChapterURL varchar(255));");
            createStatement.executeUpdate();
            createStatement.close();
        }
        
        catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static void addMangaToDB(String MangaName, String ChapterName, String ChapterURL){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            
            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO Profiles (Manga, Chapter, ChapterURL) VALUES (?, ?, ?)");
            insertStatement.setString(1, MangaName);
            insertStatement.setString(2, ChapterName);
            insertStatement.setString(3, ChapterURL);
            insertStatement.executeUpdate();
            insertStatement.close();
            
            Statement createStatement = conn.createStatement();
            createStatement.execute("CREATE TABLE [" + MangaName + "] (ReadChapters varchar(255));");
            createStatement.close();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static void addChapterToDB(String MangaName, String ChapterURL){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            
            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO [" + MangaName + "](ReadChapters) VALUES (?)");
            insertStatement.setString(1, ChapterURL);
            insertStatement.executeUpdate();
            insertStatement.close();
            
            
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static String selectMangaFromDB(String MangaName){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT ChapterURL FROM Profiles WHERE Manga ='" + MangaName + "'");
            rs.next();
            return rs.getString(1);
            
            
        } catch (Exception e) {
          e.printStackTrace();
          String duh = new String("Oops");
          return duh;
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static String selectChapterFromDB(String MangaName){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT Chapter FROM Profiles WHERE Manga ='" + MangaName + "'");
            rs.next();
            return rs.getString(1);
            
            
        } catch (Exception e) {
          e.printStackTrace();
          String duh = new String("Oops");
          return duh;
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static void deleteMangaFromDB(String MangaName){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            statement.executeUpdate("DELETE FROM Profiles WHERE Manga ='" + MangaName + "'");
            statement.executeUpdate("DROP TABLE [" + MangaName + "]");
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static String getCurrentChapterURLFromDB(String MangaName){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT ChapterURL FROM Profiles WHERE Manga ='" + MangaName + "'");
            rs.next();
            return rs.getString(1);
            
            
        } catch (Exception e) {
          e.printStackTrace();
          String duh = new String("Oops");
          return duh;
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static void updateMangaFromDB(String MangaName, String ChapterName, String ChapterURL){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            statement.executeUpdate("UPDATE Profiles SET Chapter='" + ChapterName + "', ChapterURL='" + ChapterURL + "' WHERE Manga ='" + MangaName + "'");
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static void flushReadChapters(String MangaName){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            statement.executeUpdate("DELETE * FROM [" + MangaName + "]");
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static void fillModel(DefaultListModel model){
        Connection conn = null;
        Statement statement = null;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT Manga FROM Profiles");
            while(rs.next()){
                model.addElement(rs.getString(1));
            }
            
            
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
    }
    
    public static boolean hasReadBefore(String MangaName, String chapterURL){
        Connection conn = null;
        Statement statement = null;
        Boolean answer = false;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT ReadChapters FROM [" + MangaName + "]");
            while(rs.next()){
                if(rs.getString(1).equals(chapterURL)){
                    answer = true;
                }
            }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
        return answer;
    }
    
    public static boolean hasFavoritedBefore(String MangaName){
        Connection conn = null;
        Statement statement = null;
        Boolean answer = false;
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();
            String url = "jdbc:ucanaccess://" + path;
            conn = DriverManager.getConnection(url);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT Manga FROM Profiles");
            while(rs.next()){
                if(rs.getString(1).equals(MangaName)){
                    answer = true;
                }
            }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (statement != null) {
            try {
              statement.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
          if (conn != null) {
            try {
              conn.close();
            } catch (SQLException e) {
            } // nothing we can do
          }
        }
        return answer;
    }
    
    public static void main(String[] args){
        createDB();
        /*addMangaToDB("Prison School", "Chapter 1", "http://bato.to/read/_/22130/prison-school_v1_ch1_by_ems-team");
        try{
            String url = selectMangaFromDB("Prison School");
            Document doc = Jsoup.connect(url).get();
            fillChapters(doc);
            Manga window = new Manga();
            pane.waitForImage();
            pane.callImage(0);
            window.setTitle("Prison School");
            window.add(pane, BorderLayout.CENTER);
            window.setVisible(true);
            System.out.println(selectMangaFromDB("Prison School"));
        }
        
        
        catch(IOException i){
            JOptionPane.showMessageDialog(null, "Connection Lost. Please try again.", "No Connection", JOptionPane.INFORMATION_MESSAGE);
        }*/
    }
}
