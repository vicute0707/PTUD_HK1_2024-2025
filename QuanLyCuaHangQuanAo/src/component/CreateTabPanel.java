package component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import style.CustomScrollBarUI;
import style.StyleinTab;

public class CreateTabPanel {
	 public JPanel createTabPanel(String title, String[] columns, Object[][] data) {
	        JPanel panel = new JPanel(new BorderLayout());
	        panel.setBackground(Color.WHITE);
	        
	        // Top Controls Panel
	        JPanel topControls = new JPanel(new BorderLayout(20, 0));
	        topControls.setBackground(Color.WHITE);
	        topControls.setBorder(new EmptyBorder(20, 30, 20, 30));
	        
	        // Search panel
	        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
	        searchPanel.setBackground(Color.WHITE);
	        
	        JTextField searchField = new JTextField(20);
	        StyleinTab styleinTab = new StyleinTab();
	        styleinTab.styleTextField(searchField);
	        
	        JButton searchBtn = styleinTab.createIconButton("/icon/search.png");
	        
	        searchPanel.add(searchField);
	        searchPanel.add(searchBtn);
	        
	        // Action buttons panel
	        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
	        actionPanel.setBackground(Color.WHITE);
	        
	        String addButtonText = "Thêm " + (title.contains("quyền") ? "quyền" : 
	                             title.contains("tài khoản") ? "tài khoản" :  title.contains("nhân viên") ? "nhân viên" :"tài khoản");
	        
	        JButton addBtn = styleinTab.createActionButton(addButtonText, "/icon/circle-plus.png", true);
	        JButton editBtn = styleinTab.createActionButton("Sửa", "/icon/pencil.png", false);
	        JButton deleteBtn = styleinTab.createActionButton("Xóa", "/icon/trash.png", false);
	        JButton aboutBtn = styleinTab.createActionButton("About", "/icon/info.png", false);
	        JButton exportBtn = styleinTab.createActionButton("Xuất Excel", "/icon/printer.png", false);
	        
	        actionPanel.add(addBtn);
	        actionPanel.add(editBtn);
	        actionPanel.add(deleteBtn);
	        actionPanel.add(aboutBtn);
	        actionPanel.add(exportBtn);
	        
	        topControls.add(searchPanel, BorderLayout.WEST);
	        topControls.add(actionPanel, BorderLayout.EAST);
	        
	        // Table
	        DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
	            @Override
	            public boolean isCellEditable(int row, int column) {
	                return false;
	            }
	        };
	        
	        JTable table = new JTable(tableModel);
	        styleinTab.styleTable(table);

	        JScrollPane scrollPane = new JScrollPane(table);
	        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
	        // Add components to panel
	        JPanel mainContent = new JPanel(new BorderLayout());
	        mainContent.setBackground(Color.WHITE);
	        mainContent.setBorder(new EmptyBorder(0, 30, 30, 30));
	        mainContent.add(scrollPane, BorderLayout.CENTER);
	        
	        panel.add(topControls, BorderLayout.NORTH);
	        panel.add(mainContent, BorderLayout.CENTER);
	        
	        return panel;
	    }


}
