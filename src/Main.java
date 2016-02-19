import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class Main {
	
	private static final int MAX_DIMENSION = 600;

	public static void main(String[] args) {
		
		final JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Map<Integer, Integer> keyStatus = new HashMap<>();
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher(){

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					keyStatus.put(e.getKeyCode(), KeyEvent.KEY_PRESSED);
					System.out.println("Key pressed: " + e.getKeyCode() + " (" + e.getKeyChar() + ")");
				}
				else if (e.getID() == KeyEvent.KEY_RELEASED) {
					keyStatus.put(e.getKeyCode(), KeyEvent.KEY_RELEASED);
					System.out.println("Key released: " + e.getKeyCode() + " (" + e.getKeyChar() + ")");
				}
				else if (e.getID() == KeyEvent.KEY_TYPED) {
					//System.out.println("Key typed: " + e.getKeyCode() + " (" + e.getKeyChar() + ")");
				}
				return false;
			}
        	
        });
				
		// Create master panel
		final JPanel masterPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		frame.add(masterPanel);
		
		/*final JSplitPane imagePaneOuter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final JSplitPane imagePaneInner = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 15, 15, 15);
		final JLabel imageLabel = new JLabel();
		imageLabel.setPreferredSize(new Dimension(600, 400));
		imageLabel.setBackground(new Color(200, 200, 200));
		imageLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
		imagePaneInner.setTopComponent(imageLabel);
		final JLabel emptyLabel = new JLabel();
		imagePaneInner.setRightComponent(emptyLabel);
		
		imagePaneOuter.setTopComponent(imagePaneInner);
		imagePaneOuter.setBottomComponent(emptyLabel);
		masterPanel.add(imagePaneOuter, c);*/
		
		final JPanel imagePanel = new JPanel();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 15, 15, 15);
		final JLabel imageLabel = new JLabel();
		imageLabel.setPreferredSize(new Dimension(600, 400));
		imageLabel.setBackground(new Color(200, 200, 200));
		imageLabel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));

		ComponentResizer cr = new ComponentResizer();
		cr.registerComponent(imageLabel);
		cr.setMaximumSize(new Dimension(MAX_DIMENSION, MAX_DIMENSION));
		imagePanel.add(imageLabel);
		masterPanel.add(imagePanel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 5, 5, 5);
		final JLabel filenameLabel = new JLabel("Please select an image");
        masterPanel.add(filenameLabel, c);
		
		final JFileChooser fileChooser = new JFileChooser();
		
		// Create open image button
		final JButton openButton = new JButton("Open Image");
		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				int returnVal = fileChooser.showOpenDialog(frame);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					BufferedImage bufferedImage = null;
					try {
						bufferedImage = ImageIO.read(file);
						int width = bufferedImage.getWidth();
						int height = bufferedImage.getHeight();
						int size = Math.max(width, height);
						double scaleRatio = (double) MAX_DIMENSION / size;
						int newWidth = (int) (width * scaleRatio);
						int newHeight = (int) (height * scaleRatio);
						Image image = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
						imageLabel.setIcon(new ImageIcon(image));
						imageLabel.setPreferredSize(new Dimension(newWidth, newHeight));
						filenameLabel.setText(file.getName());
						frame.pack();
					}
					catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frame,
								"An error occured when opening: " + file.getName(),
								"Couldn't open file",
							    JOptionPane.WARNING_MESSAGE);
					}
					
		            System.out.println("Opening: " + file.getName());
		        }
				else {
		            System.out.println("Open command cancelled by user");
		        }
			}
		});
		c.gridx = 0;
		c.gridy = 2;
		masterPanel.add(openButton, c);
		
		frame.pack();
		frame.requestFocus();
		frame.setVisible(true);
		
	}
	
}
