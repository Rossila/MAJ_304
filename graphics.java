// We need to import the java.sql package to use JDBC
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

// for reading from the command line
import java.io.*;

// for the login window
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * This class implements a graphical login window and a simple text
 * interface for interacting with the branch table 
 */
public class graphics implements ActionListener {
	// command line reader
	private BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	private Connection con;

	// user is allowed 3 login attempts
	private int loginAttempts = 0;

	// components of the login window
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JFrame loginFrame;
	private JFrame mainMenu;

	/*
	 * constructs login window and loads JDBC driver
	 */
	public graphics() {
		loginFrame = new JFrame("User Login");

		JLabel usernameLabel = new JLabel("Enter library card number: ");
		JLabel passwordLabel = new JLabel("Enter password: ");

		usernameField = new JTextField(10);
		passwordField = new JPasswordField(10);
		passwordField.setEchoChar('*');

		JButton loginButton = new JButton("Log In");

		JPanel contentPane = new JPanel();
		loginFrame.setContentPane(contentPane);

		// layout components using the GridBag layout manager

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		contentPane.setLayout(gb);
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// place the username label
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10, 10, 5, 0);
		gb.setConstraints(usernameLabel, c);
		contentPane.add(usernameLabel);

		// place the text field for the username
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 0, 5, 10);
		gb.setConstraints(usernameField, c);
		contentPane.add(usernameField);

		// place password label
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(0, 10, 10, 0);
		gb.setConstraints(passwordLabel, c);
		contentPane.add(passwordLabel);

		// place the password field
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 10, 10);
		gb.setConstraints(passwordField, c);
		contentPane.add(passwordField);

		// place the login button
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(5, 10, 10, 10);
		c.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(loginButton, c);
		contentPane.add(loginButton);

		// register password field and OK button with action event handler
		passwordField.addActionListener(this);
		loginButton.addActionListener(this);
		
		loginFrame.setResizable(false);
		
		// anonymous inner class for closing the window
		loginFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// size the window to obtain a best fit for the components
		loginFrame.pack();

		// center the frame
		Dimension d = loginFrame.getToolkit().getScreenSize();
		Rectangle r = loginFrame.getBounds();
		loginFrame.setLocation((d.width - r.width) / 2,
				(d.height - r.height) / 2);

		// make the window visible
		 loginFrame.setVisible(true);

		// place the cursor in the text field for the username
		usernameField.requestFocus();

		try {
			// Load the Oracle JDBC driver
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
			System.exit(-1);
		}
	}

	/*
	 * connects to Oracle database named ug and the library system using the
	 * supplied borrower id and password
	 */
	private boolean connect(String username, String password) {
		String connectURL = "jdbc:oracle:thin:@localhost:1522:ug";
		/*
		 * TO DO: Check if the supplied username is in the database and the
		 * password corresponds
		 */
		boolean correct_username = true;
		if (correct_username) {
			try {
				con = DriverManager.getConnection(connectURL, "ora_g7l7",
						"a75171090");

				System.out.println("\nConnected to Oracle!");
				return true; 
			} catch (SQLException ex) {
				System.out.println("Message: " + ex.getMessage());
				return false;
			}
		} 
		return false;
	}

	/*
	 * event handler for login window
	 */
	public void actionPerformed(ActionEvent e) {
		if (connect(usernameField.getText(),
				String.valueOf(passwordField.getPassword()))) {
			// if the username and password are valid,
			// remove the login window and display a text menu

			/*
			 * get the type of the borrower and pass it in
			 */
			String type = "clerck";
			loginFrame.dispose();
			showMainMenu(type);
		} else {
			loginAttempts++;

			if (loginAttempts >= 3) {
				loginFrame.dispose();
				System.exit(-1);
			} else {
				// clear the password
				passwordField.setText("");
			}
		}

	}

	/*
	 * displays simple text interface
	 */
	private void showMainMenu(String type) {
		mainMenu = new JFrame("Home Page");
		mainMenu.setResizable(false);

		JPanel contentPane = new JPanel();
		mainMenu.setContentPane(contentPane);
		GridBagConstraints c = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		JButton logoutButton = new JButton("Log out");
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainMenu.dispose();
				System.exit(-1);
			}
		});

		if (type.equals("student")) {
			JLabel labelSearch = new JLabel("Search: ");
			JButton searchButton = new JButton("Search");
			JTextField searchTextField = new JTextField(30);
			searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					showSearchResults();
				}
			});
			String[] searchOptions = { "author", "title", "keyword" };
			JComboBox searchMenu = new JComboBox(searchOptions);
			JTextArea welcomeString = new JTextArea("Welcome, username");
			welcomeString.setEditable(false);
			JButton accountButton = new JButton("My account");
			accountButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					showAccount();
				}
			});

			JPanel northPanel = new JPanel(new FlowLayout());
			logoutButton.setVisible(true);
			northPanel.add(logoutButton, FlowLayout.LEFT);
			northPanel.add(accountButton, FlowLayout.LEFT);
			northPanel.add(welcomeString, FlowLayout.LEFT);
			c.gridx = 0;
			c.gridy = 0;
			contentPane.add(northPanel, c);
			JPanel centerPanel = new JPanel(new FlowLayout());
			centerPanel.add(searchButton, FlowLayout.LEFT);
			centerPanel.add(searchTextField, FlowLayout.LEFT);
			centerPanel.add(labelSearch, FlowLayout.LEFT);

			c.gridx = 0;
			c.gridy = 20;
			contentPane.add(centerPanel, c);
			FlowLayout southFlowPanel = new FlowLayout();
			JPanel southPanel = new JPanel(southFlowPanel);
			southPanel.add(searchMenu, FlowLayout.LEFT);
			c.gridy = 40;
			contentPane.add(southPanel, c);

		} else if (type.equals("clerck")) {
			c.gridx = 0;
			c.gridy = 0;
			JTextArea welcomeString = new JTextArea("Welcome, clerck");
			welcomeString.setEditable(false);
			contentPane.add(welcomeString, c);
			c.gridx = 1;
			contentPane.add(logoutButton, c);
			c.gridx = 0;
			c.gridy = 1;
			JTextArea options = new JTextArea("Options: ");
			options.setEditable(false);
			contentPane.add(options, c);
			c.gridy = 2;
			JButton addBorrowerButton = new JButton();
			addBorrowerButton
					.setText("<HTML><U>Add borrower to the library system</U></HTML>");
			addBorrowerButton.setHorizontalAlignment(SwingConstants.LEFT);
			addBorrowerButton.setBorderPainted(false);
			addBorrowerButton.setOpaque(false);
			addBorrowerButton.setBackground(Color.WHITE);
			addBorrowerButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					showAddBorrowerMenu();
				}

			});

			contentPane.add(addBorrowerButton, c);

			c.gridy = 3;
			JButton checkOutItemsButton = new JButton();
			checkOutItemsButton
					.setText("<HTML><U>Check out items for a user</U></HTML>");
			checkOutItemsButton.setHorizontalAlignment(SwingConstants.LEFT);
			checkOutItemsButton.setBorderPainted(false);
			checkOutItemsButton.setOpaque(false);
			checkOutItemsButton.setBackground(Color.WHITE);
			checkOutItemsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					showCheckoutItemsMenu();
				}

			});

			contentPane.add(checkOutItemsButton, c);

			c.gridy = 4;
			JButton returnItemsButton = new JButton();
			returnItemsButton
					.setText("<HTML><U>Return items for the user</U></HTML>");
			returnItemsButton.setHorizontalAlignment(SwingConstants.LEFT);
			returnItemsButton.setBorderPainted(false);
			returnItemsButton.setOpaque(false);
			returnItemsButton.setBackground(Color.WHITE);
			returnItemsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					showReturnItemsMenu();
				}

			});

			contentPane.add(returnItemsButton, c);

			c.gridy=5;
			JButton overdueItemsButton = new JButton();
			overdueItemsButton
					.setText("<HTML><U>Check overdue items</U></HTML>");
			overdueItemsButton.setHorizontalAlignment(SwingConstants.LEFT);
			overdueItemsButton.setBorderPainted(false);
			overdueItemsButton.setOpaque(false);
			overdueItemsButton.setBackground(Color.WHITE);
			overdueItemsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					showOverdueItemsMenu();
				}

			});

			contentPane.add(overdueItemsButton, c);

		} else if (type.equals("librarian")) {
			c.gridx = 0;
			c.gridy = 0;
			JTextArea welcomeString = new JTextArea("Welcome, library");
			welcomeString.setEditable(false);
			contentPane.add(welcomeString, c);
			c.gridx = 1;
			contentPane.add(logoutButton, c);
			c.gridx = 0;
			c.gridy = 1;
			JTextArea options = new JTextArea("Options: ");
			options.setEditable(false);
			contentPane.add(options, c);
			c.gridy = 2;
			JButton addBookButton = new JButton();
			addBookButton
					.setText("<HTML><U>Add book or a copy to the library system</U></HTML>");
			addBookButton.setHorizontalAlignment(SwingConstants.LEFT);
			addBookButton.setBorderPainted(false);
			addBookButton.setOpaque(false);
			addBookButton.setBackground(Color.WHITE);
			addBookButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					showAddBookMenu();
				}

			});

			contentPane.add(addBookButton, c);

			c.gridy = 3;
			JButton reportButton = new JButton();
			reportButton
					.setText("<HTML><U>Generate a report with all the books that have been checked out</U></HTML>");
			reportButton.setHorizontalAlignment(SwingConstants.LEFT);
			reportButton.setBorderPainted(false);
			reportButton.setOpaque(false);
			reportButton.setBackground(Color.WHITE);
			reportButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					generateReportMenu();
				}

			});

			contentPane.add(reportButton, c);

			c.gridy = 4;
			JButton popularItemsButton = new JButton();
			popularItemsButton
					.setText("<HTML><U>Generate a report with the most popular items in a given year</U></HTML>");
			popularItemsButton.setHorizontalAlignment(SwingConstants.LEFT);
			popularItemsButton.setBorderPainted(false);
			popularItemsButton.setOpaque(false);
			popularItemsButton.setBackground(Color.WHITE);
			popularItemsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainMenu.dispose();
					generatePopularItemsReportMenu();
				}

			});

			contentPane.add(popularItemsButton, c);

		}
		// anonymous inner class for closing the window
		mainMenu.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		mainMenu.pack();

		Dimension d = mainMenu.getToolkit().getScreenSize();
		Rectangle r = mainMenu.getBounds();
		mainMenu.setLocation((d.width - r.width) / 2, (d.height - r.height) / 2);

		mainMenu.setVisible(true);
	}
	
	private void generatePopularItemsReportMenu() {
		final JFrame popularItemsFrame = new JFrame("Generate a report with the most popular items in a given year");
		JPanel contentPane = new JPanel(new GridBagLayout());
		popularItemsFrame.setContentPane(contentPane);
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx=0;
		c.gridy=0;
		JTextArea yearArea = new JTextArea("Year (in the format YYYY): ");
		yearArea.setEditable(false);
		contentPane.add(yearArea, c);
		final JTextField yearField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(yearField, c);

		c.gridx=0;
		c.gridy=2;
		JTextArea numberArea = new JTextArea("Number of results to display: ");
		numberArea.setEditable(false);
		contentPane.add(numberArea, c);
		final JTextField numberField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(numberField, c);

		
		JButton okButton = new JButton("Generate Report");
		c.gridx = 0;
		c.gridy = 3;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame popularItemsReportFrame = new JFrame("Generate a report with the most popular items in a given year");
				JPanel contentPaneReport = new JPanel(new GridBagLayout());
				popularItemsReportFrame.setContentPane(contentPaneReport);
				GridBagConstraints c = new GridBagConstraints();
				String numberResultsString = numberField.getText();
				String yearString = yearField.getText();
				if(Pattern.matches("^[0-2][0-9][0-9][0-9]", yearString) && Pattern.matches("[0-9]+", numberResultsString) &&
						!numberResultsString.equals("")) {
				int numberResults = Integer.parseInt(numberResultsString);
				c.gridx=0;
				c.gridy = 0;
				JTextArea titleLabel = new JTextArea("Title");
				titleLabel.setEditable(false);
				contentPaneReport.add(titleLabel,c);
				JTextArea callNumberLabel = new JTextArea("Call Number");
				callNumberLabel.setEditable(false);
				c.gridx=1;
				contentPaneReport.add(callNumberLabel, c);
				JTextArea isbnLabel = new JTextArea("ISBN");
				isbnLabel.setEditable(false);
				c.gridx=2;
				contentPaneReport.add(isbnLabel);
				JTextArea yearLabel = new JTextArea("Year");
				yearLabel.setEditable(false);
				c.gridx=3;
				contentPaneReport.add(yearLabel);
				
				for(int i = 0; i<numberResults; i++) {
					c.gridy = i+1;
					c.gridx=0;
					JTextArea title = new JTextArea("  Title of book  ");
					title.setEditable(false);
					contentPaneReport.add(title,c);
					JTextArea callNumber = new JTextArea("  Call Number of book  ");
					callNumber.setEditable(false);
					c.gridx=1;
					contentPaneReport.add(callNumber, c);
					JTextArea isbn = new JTextArea("  ISBN of book  ");
					isbn.setEditable(false);
					c.gridx=2;
					contentPaneReport.add(isbn, c);
					JTextArea year = new JTextArea("  Year of book  ");
					year.setEditable(false);
					c.gridx=3;
					contentPaneReport.add(year,c);
				}
				Dimension d = popularItemsReportFrame.getToolkit().getScreenSize();
				Rectangle r = popularItemsReportFrame.getBounds();
				popularItemsReportFrame.setLocation((d.width - r.width) / 2,
						(d.height - r.height) / 2);		
				
				popularItemsReportFrame.pack();
				popularItemsReportFrame.setVisible(true);
			  } else {
					JOptionPane.showMessageDialog(null, "An incorrect value has been entered", "Error", JOptionPane.WARNING_MESSAGE);		
			  }
			}
		});
		contentPane.add(okButton, c);

		JButton returnButton = new JButton("Return to Main Menu");
		c.gridx = 1;
		c.gridy = 3;
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				popularItemsFrame.dispose();
				showMainMenu("librarian"); 
			}
		});
		contentPane.add(returnButton, c);
		
		Dimension d = popularItemsFrame.getToolkit().getScreenSize();
		Rectangle r = popularItemsFrame.getBounds();
		popularItemsFrame.setLocation((d.width - r.width) / 2,
				(d.height - r.height) / 2);		
		
		popularItemsFrame.pack();
		popularItemsFrame.setVisible(true);
		

		popularItemsFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showMainMenu("librarian");
			}
		});		
	}

	private void generateReportMenu() {
		final JFrame checkOutItemsFrame = new JFrame("Generate a report with all the books that have been checked out");
		JPanel contentPane = new JPanel(new GridBagLayout());
		checkOutItemsFrame.setContentPane(contentPane);
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx=0;
		c.gridy=0;
		JTextArea yearArea = new JTextArea("Generate report by subject: ");
		yearArea.setEditable(false);
		contentPane.add(yearArea, c);
		String[] options = {"none", "subject1", "subject2"};
		final JComboBox optionsBox = new JComboBox(options);
		c.gridx = 1;
		contentPane.add(optionsBox, c);

		JButton okButton = new JButton("Generate Report");
		c.gridx = 0;
		c.gridy = 2;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFrame popularItemsReportFrame = new JFrame("Generate a report with the most popular items in a given year");
				JPanel contentPaneReport = new JPanel(new GridBagLayout());
				popularItemsReportFrame.setContentPane(contentPaneReport);
				GridBagConstraints c = new GridBagConstraints();
				String choice = optionsBox.getSelectedItem().toString();

				int numberResults = 6;
				c.gridx=0;
				c.gridy = 0;
				JTextArea titleLabel = new JTextArea("Title");
				titleLabel.setEditable(false);
				contentPaneReport.add(titleLabel,c);
				JTextArea callNumberLabel = new JTextArea("Call Number");
				callNumberLabel.setEditable(false);
				c.gridx=1;
				contentPaneReport.add(callNumberLabel, c);
				JTextArea checkedOutLabel = new JTextArea("Checked Out");
				checkedOutLabel.setEditable(false);
				c.gridx=2;
				contentPaneReport.add(checkedOutLabel,c);
				JTextArea dueDateLabel = new JTextArea("Due Date");
				dueDateLabel.setEditable(false);
				c.gridx=3;
				contentPaneReport.add(dueDateLabel, c);
				JTextArea overDueLabel = new JTextArea("Overdue");
				overDueLabel.setEditable(false);
				c.gridx=4;
				contentPaneReport.add(overDueLabel, c);
				
				for(int i = 0; i<numberResults; i++) {
					c.gridy = i+1;
					c.gridx=0;
					JTextArea title = new JTextArea("  Title of book  ");
					title.setEditable(false);
					contentPaneReport.add(title,c);
					JTextArea callNumber = new JTextArea("  Call Number of book  ");
					callNumber.setEditable(false);
					c.gridx=1;
					contentPaneReport.add(callNumber, c);
					JTextArea checkedOut = new JTextArea("  Checked out on ...  ");
					checkedOut.setEditable(false);
					c.gridx=2;
					contentPaneReport.add(checkedOut, c);
					JTextArea dueDate = new JTextArea("  due date of book  ");
					dueDate.setEditable(false);
					c.gridx=3;
					contentPaneReport.add(dueDate, c);
					JTextArea overdue = new JTextArea("  overdue  ");
					overdue.setEditable(false);
					c.gridx=4;
					contentPaneReport.add(overdue, c);
				}
				Dimension d = popularItemsReportFrame.getToolkit().getScreenSize();
				Rectangle r = popularItemsReportFrame.getBounds();
				popularItemsReportFrame.setLocation((d.width - r.width) / 2,
						(d.height - r.height) / 2);		
				
				popularItemsReportFrame.pack();
				popularItemsReportFrame.setVisible(true);
			}
		});
		contentPane.add(okButton, c);

		JButton returnButton = new JButton("Return to Main Menu");
		c.gridx = 1;
		c.gridy = 2;
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkOutItemsFrame.dispose();
				showMainMenu("librarian"); 
			}
		});
		contentPane.add(returnButton, c);
		
		Dimension d = checkOutItemsFrame.getToolkit().getScreenSize();
		Rectangle r = checkOutItemsFrame.getBounds();
		checkOutItemsFrame.setLocation((d.width - r.width) / 2,
				(d.height - r.height) / 2);		
		
		checkOutItemsFrame.pack();
		checkOutItemsFrame.setVisible(true);
		

		checkOutItemsFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showMainMenu("librarian");
			}
		});			
	}

	private void showAddBookMenu() {
		final JFrame addBoookFrame = new JFrame("Add Book or Copy");
		JPanel contentPane = new JPanel(new GridBagLayout());
		addBoookFrame.setContentPane(contentPane);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		JTextArea nameArea = new JTextArea("Title: ");
		nameArea.setEditable(false);
		contentPane.add(nameArea, c);
		final JTextField nameField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(nameField, c);

		c.gridx=0;
		c.gridy=1;
		JTextArea typeArea = new JTextArea("New book or copy: ");
		typeArea.setEditable(false);
		contentPane.add(typeArea, c);
		c.gridx=1;
		String[] bookOptions = { "copy", "book" };
		final JComboBox bookMenu = new JComboBox(bookOptions);
		contentPane.add(bookMenu, c);

		c.gridx=0;
		c.gridy=2;
		JTextArea callNumberArea = new JTextArea("Call Number: ");
		callNumberArea.setEditable(false);
		contentPane.add(callNumberArea, c);
		final JTextField callNumberField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(callNumberField, c);
		
		c.gridx=0;
		c.gridy=3;
		JTextArea authorArea = new JTextArea("Main author: ");
		authorArea.setEditable(false);
		contentPane.add(authorArea, c);
		final JTextField authorField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(authorField, c);
		
		c.gridx=0;
		c.gridy=4;
		JTextArea isbnArea = new JTextArea("ISBN: ");
		isbnArea.setEditable(false);
		contentPane.add(isbnArea, c);
		final JTextField isbnField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(isbnField, c);
		
		c.gridx=0;
		c.gridy=5;
		JTextArea publisherArea = new JTextArea("Publisher: ");
		publisherArea.setEditable(false);
		contentPane.add(publisherArea, c);
		final JTextField publisherField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(publisherField, c);
		
		c.gridx=0;
		c.gridy=6;
		JTextArea yearArea = new JTextArea("Year: ");
		yearArea.setEditable(false);
		contentPane.add(yearArea, c);
		final JTextField yearField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(yearField, c);

		
		JButton okButton = new JButton("OK");
		c.gridx = 0;
		c.gridy = 7;
		okButton.addActionListener(new ActionListener() {
			// don't know how to fix the error
			public void actionPerformed(ActionEvent e, String titleField.getText()) {
				//Add checks for non-null values
				PreparedStatement ps;
				if(bookMenu.getSelectedItem().toString() == "book") {
					try{
						ps = con.prepareStatement("INSERT INTO Book VALUES (?,?,?,?,?,?)");
						ps.setString(1, callNumberField.getText());
						
						ps.setString(2, isbnField.getText());
						
						ps.setString(3, nameField.getText());
						
						ps.setString(4, authorField.getText());
						
						ps.setString(5, publisherField.getText());
						
						ps.setString(6, yearField.getText());
						
						ps.executeUpdate();
						con.commit();
						System.out.println("Book successfully added!");
						ps.close();
					}
					catch(SQLException ex){
						System.out.println("Message: " + ex.getMessage());
						try{
							con.rollback();
						}
						catch(SQLException ex2){
							System.out.println("Message: " + ex2.getMessage());
							System.exit(-1);
						}
					}
					catch(ParseException px){
						// TODO Auto-generated catch block
						px.printStackTrace();
					}
					
					try{
						ps = con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?)");
						
						ps.setString(1, callNumberField.getText());
						
						ps.setInt(2, 1);
						
						ps.setString(3, "in");
						
						ps.executeUpdate();
						con.commit();
						System.out.println("BookCopy successfully added!");
						ps.close();
					}
					catch(SQLException ex){
						System.out.println("Message: " + ex.getMessage());
						try{
							con.rollback();
						}
						catch(SQLException ex2){
							System.out.println("Message: " + ex2.getMessage());
							System.exit(-1);
						}
					}
					catch(ParseException px){
						// TODO Auto-generated catch block
						px.printStackTrace();
					}
					
					try{
						ps = con.prepareStatement("INSERT INTO HasAuthor VALUES (?,?)");
						
						ps.setString(1, callNumberField.getText());
						
						ps.setString(2, authorField.getText());
						
						ps.executeUpdate();
						con.commit();
						System.out.println("HasAuthor successfully added!");
						ps.close();
					}
					catch(SQLException ex){
						System.out.println("Message: " + ex.getMessage());
						try{
							con.rollback();
						}
						catch(SQLException ex2){
							System.out.println("Message: " + ex.getMessage());
							System.exit(-1);
						}
					}
					catch(ParseException px){
						// TODO Auto-generated catch block
						px.printStackTrace();
					}
					
					try{
						ps = con.prepareStatement("INSERT INTO HasSubject VALUES (?,?)");
						
						ps.setString(1, callNumberField.getText());
						
						// temporary solution until a field for subject is made
						ps.setString(2, titleField.getText());
						
						ps.executeUpdate();
						con.commit();
						System.out.println("HasSubject successfully added!");
						ps.close();
					}
					catch(SQLException ex){
						System.out.println("Message: " + ex.getMessage());
						try{
							con.rollback();
						}
						catch(SQLException ex2){
							System.out.println("Message: " + ex2.getMessage());
							System.exit(-1);
						}
					}
					catch(ParseException px){
						// TODO Auto-generated catch block
						px.printStackTrace();
					}
				}
				else {
					int copyNumber;
					try{
						ps = con.prepareStatement("SELECT MAX(copyNo) FROM BookCopy WHERE callNumber = ?");
						
						ps.executeQuery();
						copyNumber = Integer.parseInt(ps.toString());
						System.out.println("Max copyNo: " + copyNumber + "\n");
						ps.close();
					}
					catch(SQLException ex){
						System.out.println("Message: " + ex.getMessage());
						System.exit(-1);
					}
					catch(ParseException px){
						// TODO Auto-generated catch block
						px.printStackTrace();
					}
					
					try{
						ps = con.prepareStatement("INSERT INTO BookCopy VALUES(?,?,?)");
						
						ps.setString(1, callNumberField.getText());
						
						ps.setInt(2, copyNumber+1); 
						
						ps.setString(3, "in");
						
						ps.executeUpdate();
						con.commit();
						System.out.println("BookCopy successfully added!");
						ps.close();
					}
					catch(SQLException ex){
						System.out.println("Message: " + ex.getMessage());
						try{
							con.rollback();
						}
						catch(SQLException ex2){
							System.out.println("Message: " + ex2.getMessage());
							System.exit(-1);
						}
					}
					catch(ParseException px){
						// TODO Auto-generated catch block
						px.printStackTrace();
					}
				}
				addBoookFrame.dispose();
				showMainMenu("clerck"); 
			}
		});
		contentPane.add(okButton, c);

		JButton returnButton = new JButton("Return to Main Menu");
		c.gridx = 1;
		c.gridy = 7;
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addBoookFrame.dispose();
				showMainMenu("librarian"); 
			}
		});
		contentPane.add(returnButton, c);
		
		Dimension d = addBoookFrame.getToolkit().getScreenSize();
		Rectangle r = addBoookFrame.getBounds();
		addBoookFrame.setLocation((d.width - r.width) / 2,
				(d.height - r.height) / 2);		
		
		addBoookFrame.pack();
		addBoookFrame.setVisible(true);
		

		addBoookFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showMainMenu("librarian");
			}
		});		
	}

	private void showCheckoutItemsMenu() {
		final JFrame checkOutFrame = new JFrame("Check Out Items");
		JPanel contentPane = new JPanel(new GridBagLayout());
		checkOutFrame.setContentPane(contentPane);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		JTextArea carnNumberArea = new JTextArea("Card Number: ");
		carnNumberArea.setEditable(false);
		contentPane.add(carnNumberArea, c);
		JTextField cardNumberField = new JTextField(20);
		c.gridx = 1;
		contentPane.add(cardNumberField, c);

		c.gridx=0;
		c.gridy=1;
		JTextArea callNumbArea = new JTextArea("Call Number: ");
		callNumbArea.setEditable(false);
		contentPane.add(callNumbArea, c);

		c.gridx=0;
		c.gridy=2;
		JTextField callNumberField = new JTextField(20);
		contentPane.add(callNumberField, c);
		
		c.gridx=0;
		c.gridy=3;
		JTextField callNumberField1 = new JTextField(20);
		contentPane.add(callNumberField1, c);
		
		c.gridx=0;
		c.gridy=4;
		JTextField callNumberField2 = new JTextField(20);
		contentPane.add(callNumberField2, c);
		
		c.gridx=0;
		c.gridy=5;
		JTextField callNumberField3 = new JTextField(20);
		contentPane.add(callNumberField3, c);
		
		c.gridx=0;
		c.gridy=6;
		JTextField callNumberField4 = new JTextField(20);
		contentPane.add(callNumberField4, c);
		
		
		JButton okButton = new JButton("OK");
		c.gridx = 0;
		c.gridy = 7;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Add checks for non-null values
				checkOutFrame.dispose();
				showMainMenu("clerck"); 
			}
		});
		contentPane.add(okButton, c);

		JButton returnButton = new JButton("Return to Main Menu");
		c.gridx = 1;
		c.gridy = 7;
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkOutFrame.dispose();
				showMainMenu("clerck"); 
			}
		});
		contentPane.add(returnButton, c);
		
		Dimension d = checkOutFrame.getToolkit().getScreenSize();
		Rectangle r = checkOutFrame.getBounds();
		checkOutFrame.setLocation((d.width - r.width) / 2,
				(d.height - r.height) / 2);		
		
		checkOutFrame.pack();
		checkOutFrame.setVisible(true);
		

		checkOutFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showMainMenu("clerck");
			}
		});
	}
	
	private void showReturnItemsMenu() {
		final JFrame returnFrame = new JFrame("Return Items");
		JPanel contentPane = new JPanel(new GridBagLayout());
		returnFrame.setContentPane(contentPane);
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx=0;
		c.gridy=1;
		JTextArea callNumbArea = new JTextArea("Call Number: ");
		callNumbArea.setEditable(false);
		contentPane.add(callNumbArea, c);

		c.gridx=0;
		c.gridy=2;
		JTextField callNumberField = new JTextField(20);
		contentPane.add(callNumberField, c);
		
		c.gridx=0;
		c.gridy=3;
		JTextField callNumberField1 = new JTextField(20);
		contentPane.add(callNumberField1, c);
		
		c.gridx=0;
		c.gridy=4;
		JTextField callNumberField2 = new JTextField(20);
		contentPane.add(callNumberField2, c);
		
		c.gridx=0;
		c.gridy=5;
		JTextField callNumberField3 = new JTextField(20);
		contentPane.add(callNumberField3, c);
		
		c.gridx=0;
		c.gridy=6;
		JTextField callNumberField4 = new JTextField(20);
		contentPane.add(callNumberField4, c);
		
		
		JButton okButton = new JButton("OK");
		c.gridx = 0;
		c.gridy = 7;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Add checks for non-null values
				returnFrame.dispose();
				showMainMenu("clerck"); 
			}
		});
		contentPane.add(okButton, c);

		JButton returnButton = new JButton("Return to Main Menu");
		c.gridx = 1;
		c.gridy = 7;
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				returnFrame.dispose();
				showMainMenu("clerck"); 
			}
		});
		contentPane.add(returnButton, c);
		
		Dimension d = returnFrame.getToolkit().getScreenSize();
		Rectangle r = returnFrame.getBounds();
		returnFrame.setLocation((d.width - r.width) / 2,
				(d.height - r.height) / 2);		
		
		returnFrame.pack();
		returnFrame.setVisible(true);
		

		returnFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showMainMenu("clerck");
			}
		});
	}
	
	private void showOverdueItemsMenu() {
		final JFrame checkOutFrame = new JFrame("Overdue Items");
		JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBackground(Color.WHITE);
		checkOutFrame.setContentPane(contentPane);
		int startingY = 0;
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=startingY++;
		JTextArea overdueItemArea = new JTextArea("Overdue Item : ");
		overdueItemArea.setEditable(false);
		contentPane.add(overdueItemArea, c);
		JTextArea cardNumberField = new JTextArea("Card Number :");
		cardNumberField.setEditable(false);
		c.gridx = 1;
		contentPane.add(cardNumberField, c);
        int overdueItems = 5;
		for(int i = 0; i<overdueItems; i++) {
	     	c.gridx=0;
		    c.gridy= startingY;
		   JTextArea TitleArea = new JTextArea("overdue Item Title");
		   TitleArea.setEditable(false);
		   contentPane.add(TitleArea, c);
		   c.gridx=1;
		   c.gridy=startingY++;
		   JTextArea cardNumberArea = new JTextArea("card Number of borrower");
		   cardNumberArea.setEditable(false);
		   contentPane.add(cardNumberArea, c);
		}
		JTextArea emailLabel = new JTextArea("\nEmail the selected borrowers:");
		emailLabel.setEditable(false);
		c.gridx = 0;
		c.gridy = startingY++;
		contentPane.add(emailLabel,c);
		
		int numberBorrowerOverdue = 3;
		for(int j = 0; j<numberBorrowerOverdue; j++) {
			JCheckBox borrower = new JCheckBox("name of borrower (email address)");
			c.gridy=startingY++;
			contentPane.add(borrower, c);
		}
		
		JButton okButton = new JButton("Email the selected users");
		c.gridx = 0;
		c.gridy = startingY;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Emails sent to selected users", "Emails sent", JOptionPane.INFORMATION_MESSAGE);		
			  checkOutFrame.dispose();
			  showMainMenu("clerck");
			}
		});
		contentPane.add(okButton, c);

		JButton returnButton = new JButton("Return to Main Menu");
		c.gridx = 1;
		c.gridy = startingY;
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkOutFrame.dispose();
				showMainMenu("clerck"); 
			}
		});
		contentPane.add(returnButton, c);
		
		Dimension d = checkOutFrame.getToolkit().getScreenSize();
		Rectangle r = checkOutFrame.getBounds();
		checkOutFrame.setLocation((d.width - r.width) / 2,
				(d.height - r.height) / 2);		
		
		checkOutFrame.pack();
		checkOutFrame.setVisible(true);
		

		checkOutFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showMainMenu("clerck");
			}
		});
	}
	
	private void showAddBorrowerMenu() {
		final JFrame addBorrFrame = new JFrame("Add Borrower");
		JPanel contentPane = new JPanel(new GridBagLayout());
		addBorrFrame.setContentPane(contentPane);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		JTextArea nameArea = new JTextArea("Name: ");
		nameArea.setEditable(false);
		contentPane.add(nameArea, c);
		final JTextField nameField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(nameField, c);

		c.gridx=0;
		c.gridy=1;
		JTextArea typeArea = new JTextArea("Type: ");
		typeArea.setEditable(false);
		contentPane.add(typeArea, c);
		c.gridx=1;
		String[] borrowerOptions = { "student", "staff", "faculty" };
		final JComboBox borrowerMenu = new JComboBox(borrowerOptions);
		contentPane.add(borrowerMenu, c);

		c.gridx=0;
		c.gridy=2;
		JTextArea addressArea = new JTextArea("Address: ");
		addressArea.setEditable(false);
		contentPane.add(addressArea, c);
		final JTextField addressField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(addressField, c);
		
		c.gridx=0;
		c.gridy=3;
		JTextArea phoneArea = new JTextArea("Phone Number: ");
		phoneArea.setEditable(false);
		contentPane.add(phoneArea, c);
		final JTextField phoneField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(phoneField, c);
		
		c.gridx=0;
		c.gridy=4;
		JTextArea emailArea = new JTextArea("Email: ");
		emailArea.setEditable(false);
		contentPane.add(emailArea, c);
		final JTextField emailField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(emailField, c);
		
		c.gridx=0;
		c.gridy=5;
		JTextArea sinOrStArea = new JTextArea("Sin or Student Number: ");
		sinOrStArea.setEditable(false);
		contentPane.add(sinOrStArea, c);
		final JTextField sinOrStField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(sinOrStField, c);
		
		c.gridx=0;
		c.gridy=6;
		JTextArea expiryArea = new JTextArea("Expiry date: ");
		expiryArea.setEditable(false);
		contentPane.add(expiryArea, c);
		final JTextField expiryField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(expiryField, c);
		
		c.gridx=0;
		c.gridy=7;
		JTextArea cardArea = new JTextArea("Card Number: ");
		cardArea.setEditable(false);
		contentPane.add(cardArea, c);
		JTextField cardField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(cardField, c);
		
		c.gridx=0;
		c.gridy=8;
		JTextArea passwordArea = new JTextArea("Password: ");
		passwordArea.setEditable(false);
		contentPane.add(passwordArea, c);
		final JTextField passwordField = new JTextField(30);
		c.gridx = 1;
		contentPane.add(passwordField, c);
		
		JButton okButton = new JButton("OK");
		c.gridx = 0;
		c.gridy = 9;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Add checks for non-null values
				PreparedStatement  ps;
				try{
					ps = con.prepareStatement("INSERT INTO Borrower VALUES (bid_counter.nextval,?,?,?,?,?,?,?,?)");
					ps.setString(1, passwordField.getText());
					
					ps.setString(2, nameField.getText());
					
					ps.setString(3, addressField.getText());
					
					ps.setString(4, phoneField.getText());
					
					ps.setString(5, emailField.getText());
					
					ps.setString(6, sinOrStField.getText());
					
					//ps.setString(7, expiryField.getText());
					
					ps.setString(8, borrowerMenu.getSelectedItem().toString());
					
					String stringDate = expiryField.getText();
					SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yy");
					// parse() interprets a string according to the
					// SimpleDateFormat's format pattern and then converts
					// the string to a date object

					java.util.Date utilDate = fm.parse(stringDate);
					
					java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
				
					ps.setDate(7, sqlDate);
					ps.executeUpdate();
					  
					// commit work 
					con.commit();
					System.out.println("Borrower successfully added!");
					ps.close();
				}

				catch (SQLException ex)
				{
				    System.out.println("Message: " + ex.getMessage());
				    
				    try 
				    {
					con.rollback();	
				    }
				    catch (SQLException ex2)
				    {
					System.out.println("Message: " + ex2.getMessage());
					System.exit(-1);
				    }
				} catch (ParseException px) {
					// TODO Auto-generated catch block
					px.printStackTrace();
				}	
				
				addBorrFrame.dispose();
				showMainMenu("clerck"); 
			}
		});
		contentPane.add(okButton, c);

		JButton returnButton = new JButton("Return to Main Menu");
		c.gridx = 1;
		c.gridy = 9;
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addBorrFrame.dispose();
				showMainMenu("clerck"); 
			}
		});
		contentPane.add(returnButton, c);
		
		Dimension d = addBorrFrame.getToolkit().getScreenSize();
		Rectangle r = addBorrFrame.getBounds();
		addBorrFrame.setLocation((d.width - r.width) / 2,
				(d.height - r.height) / 2);		
		
		addBorrFrame.pack();
		addBorrFrame.setVisible(true);
		

		addBorrFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				showMainMenu("clerck");
			}
		});

	}
	
	private void showAccount() {
		final JFrame accountFrame = new JFrame("Welcome, username");
		accountFrame.setResizable(false);
		accountFrame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx= 0;
		int numbItemsCheckedOut = 5;
		JPanel itemsBorr = new JPanel(new GridLayout(numbItemsCheckedOut, 1));
		c.gridy = 0;
		c.gridwidth = 2;
		for(int i=0; i<numbItemsCheckedOut; i++) {
			JTextArea item = new JTextArea("name: name of book"+ "         " + "due date: date the item has to be returned");
			item.setEditable(false);
			itemsBorr.add(item);
		}
		itemsBorr.setBorder(BorderFactory.createTitledBorder("Items checked out"));
		accountFrame.add(itemsBorr, c);
		
		int numbItemsHold = 5;
		JPanel itemsHold = new JPanel(new GridLayout(numbItemsHold, 1));
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy= 1;
		for(int i=0; i<numbItemsHold; i++) {
			JTextArea item = new JTextArea("name: name of book");
			item.setEditable(false);
			itemsHold.add(item);
		}
		itemsHold.setBorder(BorderFactory.createTitledBorder("Items on hold"));
		accountFrame.add(itemsHold, c);
		
		JPanel finePanel = new JPanel();
		c.gridy = 1;
		c.gridx = 1;
		finePanel.setBorder(BorderFactory.createTitledBorder("Fines"));
		JTextArea paymentOptions = new JTextArea("Total fine: amount \n PaymentOptions: \n" +
				"Option #1: Pay by cash. Go to the clerck at the nearest library branch to pay your fine. \n" +
		"Option #2: Pay by credit card.");
		paymentOptions.setEditable(false);
		finePanel.add(paymentOptions);
		
		JButton payFine = new JButton("Pay by credit card");
		payFine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			     showPaymentOptions();
			   }

			private void showPaymentOptions() {
				final JFrame creditCard = new JFrame("Enter your credit card information");
				JPanel creditCardPanel = new JPanel(new GridBagLayout());
				GridBagConstraints cb = new GridBagConstraints();
				cb.gridx = 0;
				cb.gridy = 0;
				JTextArea nameArea = new JTextArea("Name as displayed on the card:");
				nameArea.setEditable(false);
				creditCardPanel.add(nameArea, cb);
				cb.gridx = 1;
				final JTextField nameCard = new JTextField(30);
				creditCardPanel.add(nameCard, cb);
				cb.gridx = 0;
				cb.gridy = 1;
				JTextArea creditArea = new JTextArea("Credit Card Number:");
				creditArea.setEditable(false);
				creditCardPanel.add(creditArea, cb);
				cb.gridx = 1;
				final JTextField cardNumber = new JTextField(30);
				creditCardPanel.add(cardNumber, cb);
				cb.gridx= 0;
				cb.gridy = 2;
				JTextArea expiryArea = new JTextArea("Expiry Date:");
				expiryArea.setEditable(false);
				creditCardPanel.add(expiryArea, cb);
				cb.gridx = 1;
				final JTextField expiryDate = new JTextField(30);
				creditCardPanel.add(expiryDate, cb);
				cb.gridx=0;
				cb.gridy=3;
				JButton pay = new JButton("Ok");

				creditCardPanel.add(pay, cb);
				final JTextArea warning = new JTextArea();
				cb.gridy=4;
				creditCardPanel.add(warning, cb);
				creditCard.add(creditCardPanel);
				pay.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (nameCard.getText().equals("")
								|| cardNumber.getText().equals("")
								|| expiryDate.getText().equals("") || nameCard.getText().equals("The information is not complete.")
								|| cardNumber.getText().equals("The information is not complete.")
								|| expiryDate.getText().equals("The information is not complete.")) {

							if (nameCard.getText().equals("") || nameCard.getText().equals("The information is not complete.") ) {
								nameCard.setText("The information is not complete.");
								nameCard.setForeground(Color.RED);
							}
							if (cardNumber.getText().equals("") || cardNumber.getText().equals("The information is not complete.")) {
								cardNumber.setText("The information is not complete.");
								cardNumber.setForeground(Color.RED);
							}
							if (expiryDate.getText().equals("") || expiryDate.getText().equals("The information is not complete.")) {
								expiryDate.setText("The information is not complete.");
								expiryDate.setForeground(Color.RED);
							}
						} else {
							creditCard.dispose();
						}
					   }
					});
                creditCard.pack();
                creditCard.setVisible(true);
				
			}
		   });
		finePanel.add(payFine);
		accountFrame.add(finePanel, c);
		
		JButton returnButton = new JButton("Return to Main Menu");
		c.gridx = 0;
		c.gridy = 2;
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				accountFrame.dispose();
				showMainMenu("student"); 
			}
		});
		
		accountFrame.getContentPane().add(returnButton, c);
		Dimension d = mainMenu.getToolkit().getScreenSize();
		Rectangle r = mainMenu.getBounds();
		accountFrame.setLocation((d.width - r.width) / 2, (d.height - r.height) / 2);
		accountFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		accountFrame.pack();
		accountFrame.setVisible(true);		
	}

	private void showSearchResults() {
		int searchResults = 6;
		final JFrame searchFrame = new JFrame("Search Results");
		searchFrame.getContentPane().setLayout(new GridLayout(searchResults+1, 1));
		JButton returnButton = new JButton("Return to Main Menu");
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 searchFrame.dispose();
				showMainMenu("student"); 
			}
		});
		for(int i = 0; i<searchResults ; i++) {
			JPanel searchPanel = new JPanel(new GridLayout());
			searchPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			JTextArea title = new JTextArea("title");
			title.setEditable(false);
			JTextArea author = new JTextArea("author");
			author.setEditable(false);
			JTextArea publisherAndYear = new JTextArea("publisher - year");
			publisherAndYear.setEditable(false);
			String statusString;
			if(i/2 == 0) {
				statusString = "out";
			} else {
				statusString = "in";
			}
			JTextArea status = new JTextArea(statusString);
			status.setEditable(false);
			if(statusString.equals("out")) {
			   JButton hold = new JButton("Place a Hold");
			   searchPanel.add(hold, GridBagConstraints.REMAINDER);
			}
			searchPanel.add(status, GridBagConstraints.REMAINDER);
			searchPanel.add(publisherAndYear, GridBagConstraints.REMAINDER);
			searchPanel.add(author, GridBagConstraints.REMAINDER);
			searchPanel.add(title, GridBagConstraints.REMAINDER);
			searchPanel.setVisible(true);
            searchFrame.getContentPane().add(searchPanel);
		}
		searchFrame.getContentPane().add(returnButton);
		Dimension d = mainMenu.getToolkit().getScreenSize();
		Rectangle r = mainMenu.getBounds();
		searchFrame.setLocation((d.width - r.width) / 2, (d.height - r.height) / 2);
	      searchFrame.addWindowListener(new WindowAdapter() 
	      {
		public void windowClosing(WindowEvent e) 
		{ 
		  System.exit(0); 
		}
	      });
		searchFrame.pack();
		searchFrame.setVisible(true);
		
	}

	public static void main(String args[]) {
		graphics b = new graphics();
	}
}
