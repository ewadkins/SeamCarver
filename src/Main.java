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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
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
	
	private static final Dimension MAX_DIMENSION = new Dimension(1000, 600);
	private static final Dimension MIN_DIMENSION = new Dimension(40, 40);
	private static final double INITIAL_SIZE_RATIO = 0.75;
	
	private static BufferedImage bufferedImage = null;

	private static int originalWidth;
	private static int originalHeight;
	private static int scaledWidth;
	private static int scaledHeight;

	private static int lastWidth;
	private static int lastHeight;

	private static boolean continuousUpdates = false;
	private static boolean dragged = false;
	
	final static JLabel imageLabel = new JLabel();
	final static JPanel imagePanel = new JPanel();

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
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 15, 15, 15);
		imageLabel.setPreferredSize(new Dimension(MAX_DIMENSION.width, (int) (MAX_DIMENSION.getHeight())));
		imageLabel.setBackground(new Color(220, 220, 220));
		imageLabel.setOpaque(true);
		imageLabel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
		imageLabel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (dragged && bufferedImage != null) {
					update();
				}
				dragged = false;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		imageLabel.addMouseMotionListener(new MouseMotionListener(){
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (continuousUpdates) {
					update();
				}
				else {
					dragged = true;
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				//System.out.println(e.getX() + ", " + e.getY());
			}
        	
        });
		ComponentResizer cr = new ComponentResizer();
		cr.registerComponent(imageLabel);
		cr.setMinimumSize(MIN_DIMENSION);
		cr.setMaximumSize(MAX_DIMENSION);
		cr.setSnapSize(new Dimension(1, 1));
		cr.setDragInsets(new Insets(20, 20, 20, 20));
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
					
					try {
						bufferedImage = ImageIO.read(file);
						int[] displayData = displayImage(SobelFilter.filter(bufferedImage), true);
						filenameLabel.setText(file.getName());

						originalWidth = bufferedImage.getWidth();
						originalHeight = bufferedImage.getHeight();
						scaledWidth = displayData[0];
						scaledHeight = displayData[1];
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
	
	private static void update() {
		int newWidth = imageLabel.getWidth();
		int newHeight = imageLabel.getHeight();
		if (lastWidth != newWidth || lastHeight != newHeight) {
			System.out.println(imageLabel.getWidth() + " x " + imageLabel.getHeight());
			System.out.println("Old image (Original: " + originalWidth + " x " + originalHeight + ", Scaled: "
					+ scaledWidth + " x " + scaledHeight + ")");
			double widthRatio = (double) newWidth / scaledWidth;
			double heightRatio = (double) newHeight / scaledHeight;
			System.out.println("New image (Original: " + (int) (originalWidth * widthRatio) + " x "
					+ (int) (originalHeight * heightRatio) + ", Scaled: " + newWidth + " x " + newHeight + ")");
			lastWidth = newWidth;
			lastHeight = newHeight;
			displayImage(SobelFilter.filter(bufferedImage), false);
		}
	}
	
	private static int[] displayImage(BufferedImage bufferedImage, boolean resize) {				
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int size = Math.max(width, height);
		double scaleRatio = Math.min(MAX_DIMENSION.width, MAX_DIMENSION.height) * INITIAL_SIZE_RATIO / size;
		int newWidth = (int) (width * scaleRatio);
		int newHeight = (int) (height * scaleRatio);
		Image image = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		imageLabel.setIcon(new ImageIcon(image));
		if (resize) {
			imageLabel.setPreferredSize(new Dimension(newWidth, newHeight));
			//imagePanel.setPreferredSize(new Dimension((int) (newWidth / INITIAL_SIZE_RATIO), (int) (newHeight / INITIAL_SIZE_RATIO)));
			imagePanel.setPreferredSize(MAX_DIMENSION);	
		}
		
		return new int[]{newWidth, newHeight};
	}
	
}
