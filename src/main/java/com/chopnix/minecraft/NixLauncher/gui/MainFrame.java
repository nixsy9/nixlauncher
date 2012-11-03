package com.chopnix.minecraft.NixLauncher.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import com.chopnix.minecraft.NixLauncher.utils.Utils;


public class MainFrame extends JFrame {

	private static final long serialVersionUID = 8717494428368790716L;
	private static Component PaneInfo = null;
	// declarations
	private GuiPerformer performer;

	// GUI elements
	private static JPanel contentPane;
	private static JPanel newsPane;
	private static JLabel backgroundLabel;
	private static JLabel titleLabel;
	private static JLabel descriptionLabel;
	private static JLabel userLabel;
	private static JLabel passLabel;
	private static JTextField userField;
	private static JPasswordField passField;
	private static JButton playButton;
	private static JButton optionsButton;
	private static JCheckBox rememberCheckBox;
	private static JLabel statusLabel;
	private static JProgressBar statusPbar;
	private static JLabel titleNewsLabel;
	private static JTextPane JTPane;
	
	

	

	

	private static boolean isLogged = false;
	private static URL url;
	public MainFrame(GuiPerformer performer) {
		this.performer = performer;

		setTitle("ChopNix Launcher");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - 854) / 2, (dim.height - 480) / 2, 854, 480);

		try {
			InputStream in = MainFrame.class.getResourceAsStream("/icon.png");
			if (in != null) {
				setIconImage(ImageIO.read(in));
			}
		} catch (IOException e) {
		}

		if (init()) {
			setVisible(true);
		} else {

		}
	}

	private boolean init() {

		try {
			
			


			contentPane = new JPanel();
			contentPane.setBorder(null);
			contentPane.setLayout(null);

			newsPane = new JPanel();
			newsPane.setBounds(0, 0, 350, 480);
			newsPane.setBackground(new Color(255, 255, 255, 80));
			 try {
			        url = new URL("http://chopnix.info/cc1/cc7.php");
			    }
			    catch(MalformedURLException mue) {
			        JOptionPane.showMessageDialog(null,mue);
			    }
			  try {
			        PaneInfo = new JEditorPane(url);
			        PaneInfo.setBounds(4, 50, 350, 480);
					PaneInfo.setBackground(new Color(255, 255, 255, 0));
					PaneInfo.setPreferredSize(new Dimension(300,500));
			        PaneInfo.setFocusable(false);
			        
			    }
			    catch(IOException ioe) {
			        JOptionPane.showMessageDialog(null,ioe);
			    }
			  SwingUtilities.invokeLater(new Runnable() {
			        public void run() {
			            new JTextField().setText(url.toString());
			        }
			     });
			
			contentPane.add(newsPane);
			getContentPane().add(contentPane);

			// contentPane elements
			titleLabel = new JLabel("ChopNix");
			titleLabel.setForeground(Color.WHITE);
			titleLabel.setBounds(600, 168, 316, 38);
			titleLabel.setFont(new java.awt.Font("Bitstream Charter", 0, 32));
			contentPane.add(titleLabel);

			descriptionLabel = new JLabel("A launcher for the players.");
			descriptionLabel.setForeground(Color.WHITE);
			descriptionLabel.setBounds(600, 195, 316, 38);
			descriptionLabel.setFont(new java.awt.Font("Bitstream Charter", 0, 16));
			contentPane.add(descriptionLabel);

			userLabel = new JLabel("Username");
			userLabel.setForeground(Color.WHITE);
			userLabel.setToolTipText("Your username");
			userLabel.setBounds(642, 342, 88, 15);
			contentPane.add(userLabel);

			userField = new JTextField();
			userField.setToolTipText("Your username");
			userField.setBounds(730, 340, 110, 20);
			userField.setColumns(10);
			contentPane.add(userField);

			passLabel = new JLabel("Password");
			passLabel.setForeground(Color.WHITE);
			passLabel.setToolTipText("Password");
			passLabel.setBounds(645, 368, 85, 15);
			contentPane.add(passLabel);

			passField = new JPasswordField();
			passField.setToolTipText("Password");
			passField.setBounds(730, 366, 110, 20);
			contentPane.add(passField);

			playButton = new JButton("Login");
			playButton.setToolTipText("Connect to minecraft.net to validate your account.");
			playButton.setBounds(630, 418, 211, 30);

			playButton.addActionListener(new ActionListener() {
				@SuppressWarnings("deprecation")
				public void actionPerformed(ActionEvent arg0) {
					if (isLogged == false) {
						if (passField.getText().isEmpty() || userField.getText().isEmpty()) {
							// set status "retry", removed ( useless )
						} else {
							performer.doLogin();
						}
					} else {
						performer.doLaunchMinecraft();
					}

				}
			});

			contentPane.add(playButton);

			optionsButton = new JButton("");
			optionsButton.setIcon(new ImageIcon(MainFrame.class.getResource("/gui/cog.png")));
			optionsButton.setToolTipText("Connect to minecraft.net to validate your account.");
			optionsButton.setBounds(596, 418, 30, 30);

			optionsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					ConfigurationFrame.main();
				}
			});

			contentPane.add(optionsButton);

			rememberCheckBox = new JCheckBox();
			rememberCheckBox.setText("Remember");
			rememberCheckBox.setToolTipText("Remember your username and password for next login.");
			rememberCheckBox.setBounds(730, 390, 111, 19);
			rememberCheckBox.setOpaque(false);
			rememberCheckBox.setForeground(Color.WHITE);
			contentPane.add(rememberCheckBox);

			statusLabel = new JLabel();
			statusLabel = new JLabel("Enjoy your time on chopnix");
			statusLabel.setForeground(Color.WHITE);
			statusLabel.setToolTipText("Indicates the status of the launcher");
			statusLabel.setBounds(368, 12, 472, 15);
			contentPane.add(statusLabel);

			statusPbar = new JProgressBar();
			statusPbar.setVisible(false);
			statusPbar.setMaximum(100);
			statusPbar.setMinimum(0);
			statusPbar.setValue(50);
			statusPbar.setBounds(368, 30, 472, 20);
			contentPane.add(statusPbar);

			backgroundLabel = new JLabel("");
			backgroundLabel.setForeground(Color.WHITE);
			backgroundLabel.setIcon(new ImageIcon(MainFrame.class.getResource("/gui/bg.png")));
			backgroundLabel.setBounds(0, 0, 854, 480);
			contentPane.add(backgroundLabel);

			// newsPane elements
			titleNewsLabel = new JLabel();
			titleNewsLabel.setText("");
		    titleNewsLabel.setBounds(20, 220, 50, 20);
			titleNewsLabel.setForeground(Color.WHITE);
			titleNewsLabel.setFont(new java.awt.Font("Bitstream Charter", 0, 22));

			newsPane.add(titleNewsLabel);
			newsPane.add(PaneInfo);
			
			

			readRemember();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}
	
	/**
	 * Simple way to "print" to a JTextArea; just say
	 * PrintWriter out = new PrintWriter(new TextAreaWriter(myTextArea));
	 * Then out.println() it all will all appear in the TextArea.
	 */
	public final class TextAreaWriter extends Writer {

		private final JTextArea textArea;

		public TextAreaWriter(final JTextArea textArea) {
			this.textArea = textArea;
		}

	    @Override
	    public void flush(){ }
	    
	    @Override
	    public void close(){ }

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			textArea.append(new String(cbuf, off, len));
		}
	}
	

	    public static void htmlp(String[] args) throws Exception {
	        URL chopnix = new URL("http://chopnix.info/query/cc7.php");
	        URLConnection yc = chopnix.openConnection();
	        BufferedReader in = new BufferedReader(
	                                new InputStreamReader(
	                                yc.getInputStream()));
	        String inputLine;

	        while ((inputLine = in.readLine()) != null) 
	            System.out.println(inputLine);
	        in.close();
	    }
	
	
	public void setStatus(String status) {
		statusLabel.setText(status);
	}

	public void setProgression(Integer progression) {
		if (progression >= statusPbar.getMinimum() && progression <= statusPbar.getMaximum()) {
			statusPbar.setValue(progression);
		}
	}

	public void setProgressBarView(Boolean view) {
		statusPbar.setVisible(view);
	}

	public String getUsername() {
		return userField.getText();
	}

	@SuppressWarnings("deprecation")
	public String getPassword() {
		return passField.getText();
	}

	public void setButtonText(String text) {
		playButton.setText(text);
	}

	public void setButtonEnabled(boolean enabled) {
		playButton.setEnabled(enabled);
	}

	public void setLogged(boolean logged) {
		isLogged = logged;
	}

	private void readRemember() {
		try {
			File lastLogin = new File(Utils.getWorkingDir(), "lastlogin");
			if (!lastLogin.exists()) {
				return;
			}

			Cipher cipher = getCipher(2, "passwordfile");

			DataInputStream dis;
			if (cipher != null) {
				dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
			} else {
				dis = new DataInputStream(new FileInputStream(lastLogin));
			}

			userField.setText(dis.readUTF());
			passField.setText(dis.readUTF());
			rememberCheckBox.setSelected(passField.getPassword().length > 0);
			dis.close();
		} catch (Exception e) {
		}
	}

	public void writeRemember() {
		try {
			File lastLogin = new File(Utils.getWorkingDir(), "lastlogin");

			Cipher cipher = getCipher(1, "passwordfile");

			DataOutputStream dos;
			if (cipher != null) {
                        dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
                    }
			else {
				dos = new DataOutputStream(new FileOutputStream(lastLogin));
			}

			dos.writeUTF(userField.getText());
			dos.writeUTF(rememberCheckBox.isSelected() ? new String(passField.getPassword()) : "");
			dos.close();
		} catch (Exception e) {
		}
	}

	private static Cipher getCipher(int mode, String password) throws Exception {
		Random random = new Random(43287234L);
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

		SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(mode, pbeKey, pbeParamSpec);
		return cipher;
	}

	public static JTextPane getJTPane() {
		return JTPane;
	}

	public static void setJTPane(JTextPane jTPane) {
		JTPane = jTPane;
	}
	
	public static URL getUrl() {
		return url;
	}

	public static void setUrl(URL url) {
		MainFrame.url = url;
	}
	}
	

