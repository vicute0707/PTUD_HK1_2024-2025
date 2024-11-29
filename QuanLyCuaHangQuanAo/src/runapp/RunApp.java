package runapp;

import java.awt.EventQueue;
import java.sql.Connection;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import connection.MyConnection;
import gui.Form_DangNhap;

public class RunApp {
	   public static void main(String[] args) {  
	        EventQueue.invokeLater(new Runnable() {  
	            public void run() {  
	                try { 
	                	MyConnection dbConnection = new MyConnection();
	                	Connection conn = dbConnection.connect();
	                    Form_DangNhap frame = new Form_DangNhap();  
	                    frame.setVisible(true);
	                    FlatRobotoFont.install();
	                    FlatLaf.setPreferredFontFamily(FlatRobotoFont.FAMILY);
	                    FlatLaf.setPreferredLightFontFamily(FlatRobotoFont.FAMILY_LIGHT);
	                    FlatLaf.setPreferredSemiboldFontFamily(FlatRobotoFont.FAMILY_SEMIBOLD);
	                    FlatIntelliJLaf.registerCustomDefaultsSource("style");
	                    FlatIntelliJLaf.setup();
	                } catch (Exception e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        });  
	    }  

}
