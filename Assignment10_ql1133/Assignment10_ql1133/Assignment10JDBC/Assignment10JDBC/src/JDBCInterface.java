import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class JDBCInterface extends JFrame {

	private JPanel controlPanel;
	private JTextArea textQueryArea;
	private JTextField lastNameQuery;
	private JButton queryButton;
	private JButton insertButton;

	
	private Connection conn;
	private PreparedStatement queryStmtLastName;
	private PreparedStatement queryAll;
	private PreparedStatement insertStmt;
	
	
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 400;
	final int AREA_ROWS = 20;
	final int AREA_COLUMNS = 40;
	private JTextField insertAge;
	private JTextField insertCity;
	private JTextField insertLastName;
	private JTextField insertFirstName;
   
   public JDBCInterface() {

		try {
			conn = DriverManager.getConnection("jdbc:sqlite:assignment.db");
			queryStmtLastName = conn.prepareStatement("Select * from People WHERE Last = ?");
			
			//PreparedStatement for select all
			queryAll = conn.prepareStatement("Select * from People");
			//PreparedStatement for insert
			insertStmt = conn.prepareStatement("Insert into People(First, last, age, city) values(?,?,?,?)");
			
		} catch (SQLException e) {
			System.err.println("Connection error: " + e);
			System.exit(1);
		}
		
	   createControlPanel();
	   queryButton.addActionListener(new QueryButtonListener());
	   insertButton.addActionListener(new InsertButtonListener());

	   textQueryArea = new JTextArea(
	            AREA_ROWS, AREA_COLUMNS);
	   textQueryArea.setEditable(false);
	   
	   /* scrollPanel is optional */
	   JScrollPane scrollPane = new JScrollPane(textQueryArea);
	   JPanel textPanel = new JPanel();
	   textPanel.add(scrollPane);
	   getContentPane().add(textPanel, BorderLayout.CENTER);
	   getContentPane().add(controlPanel, BorderLayout.NORTH);
   }
   
   private JPanel createControlPanel() {
	   
	   /* you are going to have to create a much more fully-featured layout */
	   
	   controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
	   controlPanel.setPreferredSize(new Dimension(10, 170));
	   
	   JPanel inputPanel = new JPanel();
	   JLabel lbl = new JLabel("Last Name:");
	   inputPanel.add(lbl);
	   lastNameQuery = new JTextField(10);
	   inputPanel.add(lastNameQuery);
	   
	   JPanel buttonPanel = new JPanel();
	   queryButton = new JButton("Execute Query");
	   buttonPanel.add(queryButton);
	   
	   JPanel p1 = new JPanel();
	   controlPanel.add(p1);
	   
	   p1.add(new JLabel("Last Name:"));
	   
	   insertLastName = new JTextField(10);
	   p1.add(insertLastName);
	   
	   JPanel p2 = new JPanel();
	   controlPanel.add(p2);
	   
	   p2.add(new JLabel("First Name:"));
	   
	   insertFirstName = new JTextField(10);
	   p2.add(insertFirstName);
	   
	   JPanel p3 = new JPanel();
	   controlPanel.add(p3);
	   
	   p3.add(new JLabel("Age:"));
	   
	   insertAge = new JTextField(10);
	   p3.add(insertAge);
	   
	   JPanel p4 = new JPanel();
	   controlPanel.add(p4);
	   
	   p4.add(new JLabel("City:"));
	   
	   insertCity = new JTextField(10);
	   p4.add(insertCity);
	   
	   JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 150,0));
	   controlPanel.add(p5);
	   
	   insertButton = new JButton("Insert");
	   p5.add(insertButton);
	   
	   controlPanel.add(inputPanel);
	   controlPanel.add(buttonPanel);
	   
	   return controlPanel;
	   
	   
   }
   
   class InsertButtonListener implements ActionListener {
	   public void actionPerformed(ActionEvent event) {
		   String firstName = insertFirstName.getText();
		   String lastName = insertLastName.getText();
		   String age = insertAge.getText();
		   String city = insertCity.getText();
			if (firstName.length() == 0 || lastName.length() == 0 || 
					age.length() == 0 || city.length() == 0) {
				//If any of the fields are blank,show Message Dialog
				JOptionPane.showMessageDialog(controlPanel, "ALl Fields must be filled");
		   }else{
			   try {
				   PreparedStatement stmt =insertStmt;
				   stmt.setString(1, firstName);
				   stmt.setString(2, lastName);
				   stmt.setInt(3, Integer.parseInt(age));
				   stmt.setString(4, city);
				   //insert a new row
				   stmt.execute();
				   
				   // all of the insert fields should be erased after insert
				   insertAge.setText("");
				   insertCity.setText("");
				   insertLastName.setText("");
				   insertFirstName.setText("");
			   } catch (NumberFormatException e) {
				   JOptionPane.showMessageDialog(controlPanel, "Age Field error");
				   e.printStackTrace();
			   } catch (SQLException e) {
				   e.printStackTrace();
			   }
		   }
		   
	   }
   }
   
   class QueryButtonListener implements ActionListener {
	   public void actionPerformed(ActionEvent event)
       {
		   /* as far as the columns, it is totally acceptable to
		    * get all of the column data ahead of time, so you only
		    * have to do it once, and just reprint the string
		    * in the text area.
		    */
		   
		   /* you want to change things here so that if the text of the 
		    * last name query field is empty, it should query for all rows.
		    * 
		    * For now, if the last name query field is blank, it will execute:
		    * SELECT * FROM People WHERE Last=''
		    * which will give no results
		    */
		   try {
			   textQueryArea.setText("");
			   PreparedStatement stmt = queryStmtLastName;
			   String lastNameText = lastNameQuery.getText();
			   if (lastNameText.trim().length() == 0) {
				   //If there is no text in the ¡°Last Name¡± query field, ALL rows should be returned
				   stmt = queryAll;
			   }else{
				   stmt.setString(1, lastNameText);
			   }
				ResultSet rset = stmt.executeQuery();
				ResultSetMetaData rsmd = rset.getMetaData();
				int numColumns = rsmd.getColumnCount();
				System.out.println("numcolumns is "+ numColumns);
	
				
				String rowString = "",labelString = "";
				// return the column names above the rows
				for (int i=1;i<=numColumns;i++) {
					labelString+=rsmd.getColumnLabel(i)+ "\t";
				}
				labelString +="\n";
				while (rset.next()) {
					for (int i=1;i<=numColumns;i++) {
						Object o = rset.getObject(i);
						rowString += o.toString() + "\t";
					}
					rowString += "\n";
				}
				System.out.print("labelString  is  " + labelString);
				System.out.print("rowString  is  " + rowString);
				textQueryArea.setText(labelString+rowString);
		   } catch (SQLException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
       }
   }
    
   public static void main(String[] args)
	{  
	   JFrame frame = new JDBCInterface();
	   frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   frame.setVisible(true);      
	}
}
