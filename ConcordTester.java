

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.*;    // Gives access to persistent user Preferences (for 'set ...' commands)

/**
 * Class used to test the King James Concordance
 * 
 * @author Jason Rhinelander and Liam Keliher
 */
public class ConcordTester extends JFrame implements ActionListener
{
	private JButton openFile, searchButton;
	private JTextField searchText;
	private JTextArea outputTextArea;
	private JFileChooser fc;
	private static Preferences prefs = Preferences.userRoot().node("ConcordTester");
	private boolean fileLoaded = false;
	private PrintStream textAreaPS;
	private Concordance concord;

	//-------------------------------------------------------------------------
	/**
	 * Constructor.
	 */
	public ConcordTester()
	{
		// Set the window title (U+2014 is an em dash)
		super("Lab 3 \u2014 Strong's Concordance");

		// BorderLayout lets us put one thing on one side (i.e. the control
		// panel), then use "CENTER" positioning to take up all the rest of the
		// space (i.e. for the output text field).
		setLayout(new BorderLayout());
		setSize(600, 500);

		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
		add(controls, BorderLayout.NORTH);

		openFile = new JButton("Open file...");
		// The action command string is used in actionPerformed to identify
		// which button was clicked        
		openFile.setActionCommand("open file");
		// The current object (i.e. "this") handles button clicks by
		// implementing ActionListener and providing an actionPerformed method.
		openFile.addActionListener(this);

		searchText = new JTextField();
		searchText.setActionCommand("search field");
		searchText.addActionListener(this);

		searchButton = new JButton("Search");
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);

		controls.add(openFile);
		controls.add(Box.createRigidArea(new Dimension(50, 1)));
		controls.add(searchText);
		controls.add(searchButton);

		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

		// If the persistent preferences contain a last directory,
		// use it instead of the system default:
		String defaultPath = prefs.get("lastDir", null);
		if (defaultPath != null)
			fc.setCurrentDirectory(new File(defaultPath));

		outputTextArea = new JTextArea();
		// Create a new PrintStream object that sends print calls both to the
		// text area and System.out
		ByteArrayOutputStream baos = new ByteArrayOutputStream() {
			public void flush () {
				String s = toString();
				outputTextArea.append(s);
				System.out.print(s);
				reset();
			}
		};
		textAreaPS = new PrintStream(baos, true);
		JScrollPane textscroll = new JScrollPane(outputTextArea);
		add(textscroll, BorderLayout.CENTER);

		setVisible(true);
	} // constructor ConcordTester()
	//-------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if (command.equals("open file")) {
			int status = fc.showOpenDialog(this);
			// Store the currently selected path in the user's permanent preferences:
			prefs.put("lastDir", fc.getCurrentDirectory().getPath());

			if (status == JFileChooser.APPROVE_OPTION) {
				int tableSize = 0;
				String msg = "Enter hash table size.";
				while (tableSize <= 0) {
					String tableStr = JOptionPane.showInputDialog(
							this, msg, prefs.get("lastTableSize", "")
					);
					tableSize = Integer.parseInt(tableStr);
					msg = "Invalid hash table size.  Please enter a valid hash table size.";
				} // while
				prefs.put("lastTableSize", ""+tableSize);
				concord = null;
				try {
					concord = new Concordance(fc.getSelectedFile(), tableSize, textAreaPS);
				} // try
				catch (FileNotFoundException fnfe) {
					textAreaPS.print("Unable to load file: " + fnfe.getMessage());
				} // catch
				catch (ConcordanceException cbe) {
					textAreaPS.print("An exception occured while building concordance: " + cbe.getMessage());
				} // catch
			} // if

		} // if
		else if (command.equals("search field") || command.equals("search")) {
			// Get the string we're searching for:
			String searchFor = searchText.getText();
			if (concord == null) {
				// If concord is null, we haven't loaded a file (or the loading failed),
				// so show an error message dialog:
				JOptionPane.showMessageDialog(
						null,
						"No file loaded!  Please open a file before searching.",
						"No file loaded!",
						JOptionPane.ERROR_MESSAGE);
			} // if
			else if (searchFor == null || searchFor.length() < 1) {
				// They hit search but haven't typed anything; show an error dialog
				JOptionPane.showMessageDialog(
						null,
						"No search string entered!  Please type the word to search for.",
						"No search string!",
						JOptionPane.ERROR_MESSAGE);
			} // else if
			else {
				searchText.selectAll(); // Select the text in the box, so that typing again will enter a new word
				int index = concord.search(searchFor);
				String[] results = concord.formatIDs(index);
				textAreaPS.println("Result of search for \"" + searchFor + "\"");
				textAreaPS.print("-----------------------");
				for (int i = 0; i < searchFor.length(); i++) textAreaPS.print("-");
				textAreaPS.println();
				if (results != null && results.length > 0) {
					textAreaPS.println("  [" + results.length + " results found]\n");
					for (String r: results) {
						textAreaPS.println("  " + r);
					} // for r
				} // if
				else {
					textAreaPS.println("*** No matches for \"" + searchFor + "\" ***");
				} // else
				textAreaPS.println("\n");
			} // else
		} // else if
	} // actionPerformed(ActionEvent)

	//-------------------------------------------------------------------------
	public static void main (String[] args)
	{
		// Just get a ConcordTester object; the constructor does the real work
		ConcordTester c = new ConcordTester();
		c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	} // main(String[])
	//-------------------------------------------------------------------------
} // class ConcordTester