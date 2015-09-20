package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import syntaxChecker.SyntaxChecker;

public class Main {
	static String openedFile = "";
	static boolean textIsModified = false;
	static JFrame frame;
	static SyntaxChecker javaChecker;

	public static int findLastNonWordChar(String text, int index) {
		while (--index >= 0) {
			if (String.valueOf(text.charAt(index)).matches("\\W")) {
				break;
			}
		}
		return index;
	}

	public static int findFirstNonWordChar(String text, int index) {
		while (index < text.length()) {
			if (String.valueOf(text.charAt(index)).matches("\\W")) {
				break;
			}
			index++;
		}
		return index;
	}

	private static void createAndShowGUI() {

		JPanel contentPane = new JPanel(new BorderLayout());
		JTextPane output;
		JScrollPane scrollPane;
		JMenuBar menuBar;
		JMenu fileMenu, editMenu, aboutMenu;
		JMenuItem newItem, openItem, saveItem, closeItem, aboutTheDeveloper, aboutUpdate;
		contentPane.setOpaque(true);
		// Create a scrolled text area.

		final StyleContext cont = StyleContext.getDefaultStyleContext();
		final AttributeSet attr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.blue);
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setBold(sas, true);
		final AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.white);
		DefaultStyledDocument doc = new DefaultStyledDocument() {
			public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
				super.insertString(offset, str, a);

				String text = getText(0, getLength());
				int before = findLastNonWordChar(text, offset);
				if (before < 0)
					before = 0;
				int after = findFirstNonWordChar(text, offset + str.length());
				int wordL = before;
				int wordR = before;
				String[] keywordArray = new String[] { "abstract", "assert", "boolean", "break", "byte", "case",
						"catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum",
						"extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
						"instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected",
						"public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
						"throw", "throws", "transient", "try", "void", "volatile", "while" };
				String keywords = "";
				for (int i = 0; i < keywordArray.length; i++) {
					keywords += keywordArray[i];
					if (i != keywordArray.length - 1) {
						keywords += "|";
					}
				}

				while (wordR <= after) {
					if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
						if (text.substring(wordL, wordR).matches("(\\W)*(" + keywords + ")")) {
							setCharacterAttributes(wordL, wordR - wordL, attr, false);
							setCharacterAttributes(before, after - before, sas, false);
						} else
							setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
						wordL = wordR;
					}
					wordR++;
				}
			}

			public void remove(int offs, int len) throws BadLocationException {
				super.remove(offs, len);

				String text = getText(0, getLength());
				int before = findLastNonWordChar(text, offs);
				if (before < 0)
					before = 0;
				int after = findFirstNonWordChar(text, offs);
				String[] keywordArray = new String[] { "abstract", "assert", "boolean", "break", "byte", "case",
						"catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum",
						"extends", "final", "finally", "float", "for", "goto", "if", "implements", "import",
						"instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected",
						"public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this",
						"throw", "throws", "transient", "try", "void", "volatile", "while" };
				String keywords = "";
				for (int i = 0; i < keywordArray.length; i++) {
					keywords += keywordArray[i];
					if (i != keywordArray.length - 1) {
						keywords += "|";
					}
				}
				if (text.substring(before, after).matches("(\\W)*(" + keywords + ")")) {
					setCharacterAttributes(before, after - before, attr, false);
					setCharacterAttributes(before, after - before, sas, false);
				} else {
					setCharacterAttributes(before, after - before, attrBlack, false);
				}
			}

		};
		output = new JTextPane(doc);
		output.setBackground(Color.BLACK);
		output.setEditable(true);
		scrollPane = new JScrollPane(output);
        	JFrame splash = new JFrame();
		// Add the text area to the content pane.
		contentPane.add(scrollPane, BorderLayout.CENTER);
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		aboutMenu = new JMenu("About");
		// ******** file ******
		newItem = new JMenuItem("New");
		openItem = new JMenuItem("Open");
		saveItem = new JMenuItem("Save");
		closeItem = new JMenuItem("Close");
		// ******** edit ******
		JMenuItem copyItem = new JMenuItem("Copy");
		JMenuItem cutItem = new JMenuItem("Cut");
		JMenuItem pasteItem = new JMenuItem("Paste");
		editMenu.addSeparator();
		JMenuItem findItem = new JMenuItem("Find ...");
		JMenuItem replaceItem = new JMenuItem("Replace ...");
		// ********** about *******
		aboutTheDeveloper = new JMenuItem("About the developer");
		aboutUpdate = new JMenuItem("Check for updates ...");

		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(closeItem);

		editMenu.add(copyItem);
		editMenu.add(cutItem);
		editMenu.add(pasteItem);
		//editMenu.add(findItem);
		//editMenu.add(replaceItem);

		aboutMenu.add(aboutTheDeveloper);
		//aboutMenu.add(aboutUpdate);

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(aboutMenu);

		newItem.setMnemonic(KeyEvent.VK_N);
		KeyStroke ctrlNKeyStroke = KeyStroke.getKeyStroke("control N");
		newItem.setAccelerator(ctrlNKeyStroke);

		openItem.setMnemonic(KeyEvent.VK_O);
		KeyStroke ctrlOKeyStroke = KeyStroke.getKeyStroke("control O");
		openItem.setAccelerator(ctrlOKeyStroke);

		saveItem.setMnemonic(KeyEvent.VK_S);
		KeyStroke ctrlSKeyStroke = KeyStroke.getKeyStroke("control S");
		saveItem.setAccelerator(ctrlSKeyStroke);

		closeItem.setMnemonic(KeyEvent.VK_Q);
		KeyStroke ctrlQKeyStroke = KeyStroke.getKeyStroke("control Q");
		closeItem.setAccelerator(ctrlQKeyStroke);

		copyItem.setMnemonic(KeyEvent.VK_C);
		KeyStroke ctrlCKeyStroke = KeyStroke.getKeyStroke("control C");
		copyItem.setAccelerator(ctrlCKeyStroke);

		pasteItem.setMnemonic(KeyEvent.VK_P);
		KeyStroke ctrlVKeyStroke = KeyStroke.getKeyStroke("control V");
		pasteItem.setAccelerator(ctrlVKeyStroke);

		cutItem.setMnemonic(KeyEvent.VK_X);
		KeyStroke ctrlXKeyStroke = KeyStroke.getKeyStroke("control X");
		cutItem.setAccelerator(ctrlXKeyStroke);

		findItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		pasteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				output.paste();

			}
		});
		cutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				output.cut();

			}
		});
		aboutTheDeveloper.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame me = new JFrame("About the developer");
				JLabel mail = new JLabel("Mohamed Maalej:maalejmedti@gmail.com");
				me.add(mail);
				me.setSize(300, 150);
				me.setDefaultCloseOperation(1);
				me.setResizable(false);
				me.setVisible(true);
				
			}
		} );
		copyItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				output.copy();

			}
		});
		newItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if ((textIsModified) && (!output.getText().equals(""))) {
					JFileChooser saver = new JFileChooser();
					JFrame dFrame = new JFrame();
					int n = JOptionPane.showConfirmDialog(dFrame, "Save current file ?", "File saving ",
							JOptionPane.YES_NO_OPTION);
					if (n == 0) {
						if (openedFile.equals("")) {
							int rVal = saver.showSaveDialog(saveItem);
							if (rVal == JFileChooser.APPROVE_OPTION) {
								File fileToSave = saver.getSelectedFile();
								BufferedWriter toJava = null;
								StringBuilder sb;
								try {
									toJava = new BufferedWriter(new FileWriter(fileToSave));
									sb = new StringBuilder();
									sb.append(output.getText());
									toJava.write(sb.toString());
									openedFile = fileToSave.getAbsolutePath().toString();
									frame.setTitle("SemiColon: Just another Java editor -- " + openedFile);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								try {
									toJava.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						} else {
							BufferedWriter toJava = null;
							StringBuilder sb;
							try {
								toJava = new BufferedWriter(new FileWriter(openedFile));
								sb = new StringBuilder();
								sb.append(output.getText());
								toJava.write(sb.toString());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								toJava.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}

				}
				openedFile = "";
				output.setVisible(true);
				output.setText("");
				frame.setTitle("SemiColon: Just another Java editor -- " + openedFile);
			}
		});
		// open item action : opening FileChooser dialog
		openItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (openedFile.equals("")) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
					fileChooser.addChoosableFileFilter(new FileFilter() {
						
						@Override
						public String getDescription() {
							// TODO Auto-generated method stub
							return "Java code files (*.java)";
						}
						
						@Override
						public boolean accept(File f) {
							if (f.isDirectory()) {
					            return true;
					        } else {
					            return f.getName().toLowerCase().endsWith(".java");
					        }
						}
					});
					int result = fileChooser.showOpenDialog(openItem);
					if (result == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						openedFile = selectedFile.getAbsolutePath().toString();
						StringBuilder code = new StringBuilder();
						BufferedReader fromJava;
						String s;
						try {
							fromJava = new BufferedReader(new FileReader(selectedFile));
							s = fromJava.readLine();
							while (s != null) {
								code.append(s + "\n");
								s = fromJava.readLine();
							}
							frame.setTitle("SemiColon: Just another Java editor -- " + openedFile);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException exIO) {
							exIO.printStackTrace();
						}
						output.setText(code.toString());
					}

				}
				else if(textIsModified == true)
				{
					if ((output.getText().equals(""))) {
						JFileChooser saver = new JFileChooser();
						JFrame dFrame = new JFrame();
						int n = JOptionPane.showConfirmDialog(dFrame, "Save current file ?", "File saving ",
								JOptionPane.YES_NO_OPTION);
						if (n == 0) {
							if (!openedFile.equals("")) {
								int rVal = saver.showSaveDialog(saveItem);
								if (rVal == JFileChooser.APPROVE_OPTION) {
									File fileToSave = saver.getSelectedFile();
									BufferedWriter toJava = null;
									StringBuilder sb;
									try {
										toJava = new BufferedWriter(new FileWriter(fileToSave));
										sb = new StringBuilder();
										sb.append(output.getText());
										toJava.write(sb.toString());
										openedFile = fileToSave.getAbsolutePath().toString();
										frame.setTitle("SemiColon: Just another Java editor -- " + openedFile);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									try {
										toJava.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}

							} else if (output.getText().equals("")) {
								BufferedWriter toJava = null;
								StringBuilder sb;
								try {
									toJava = new BufferedWriter(new FileWriter(openedFile));
									sb = new StringBuilder();
									sb.append(output.getText());
									toJava.write(sb.toString());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								try {
									toJava.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
							}
							
							
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
							int result = fileChooser.showOpenDialog(openItem);
							if (result == JFileChooser.APPROVE_OPTION) {
								File selectedFile = fileChooser.getSelectedFile();
								openedFile = selectedFile.getAbsolutePath().toString();
								StringBuilder code = new StringBuilder();
								BufferedReader fromJava;
								String s;
								try {
									fromJava = new BufferedReader(new FileReader(selectedFile));
									s = fromJava.readLine();
									while (s != null) {
										code.append(s + "\n");
										s = fromJava.readLine();
									}
									frame.setTitle("SemiColon: Just another Java editor -- " + openedFile);
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException exIO) {
									exIO.printStackTrace();
								}
								output.setText(code.toString());
							}     
						}
						
						

					}
					
				}
			
			}
            
		});
		saveItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser saver = new JFileChooser();
				// Demonstrate "Save" dialog:

				if ((textIsModified == true) && (openedFile.equals(""))) {
					int rVal = saver.showSaveDialog(saveItem);
					if (rVal == JFileChooser.APPROVE_OPTION) {
						File fileToSave = saver.getSelectedFile();
						BufferedWriter toJava = null;
						StringBuilder sb;
						try {
							toJava = new BufferedWriter(new FileWriter(fileToSave));
							sb = new StringBuilder();
							sb.append(output.getText());
							toJava.write(sb.toString());
							openedFile = fileToSave.getAbsolutePath().toString();
							frame.setTitle("SemiColon: Just another Java editor -- " + openedFile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							toJava.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} else if ((textIsModified == true) && (!openedFile.equals(""))) {
					BufferedWriter toJava = null;
					StringBuilder sb;
					try {
						toJava = new BufferedWriter(new FileWriter(openedFile));
						sb = new StringBuilder();
						sb.append(output.getText());
						toJava.write(sb.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						toJava.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		});

		closeItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				output.setVisible(false);
				openedFile = "";
				frame.setTitle("SemiColon: Just another Java editor -- " + openedFile);
			}
		});

		output.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				textIsModified = true;

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {

				textIsModified = true;

			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				textIsModified = true;

			}
		});
		frame = new JFrame("SemiColon: Just another Java editor -- ");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		frame.setContentPane(contentPane);
		frame.setSize(1200, 900);
		splash.setSize(500, 350);
		splash.setVisible(true);
		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException ie)
		{
			
		}
		splash.setVisible(false);
		frame.setVisible(true);

	}

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
