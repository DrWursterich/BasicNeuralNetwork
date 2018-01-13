import BNN.*;

import java.io.IOException;
import java.util.Arrays;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("unused")
public class HandwritingRecognition {
	private static JFrame frame;
	private static JButton submit;
	private static JButton reset;
	private static boolean painted = false;
	private static boolean pressed = false;
	private static int mouse_x = 0;
	private static int mouse_y = 0;
	private static int[][] image;
	private static final int PIXEL_COUNT = 28;
	private static final int PIXEL_SIZE = 5;
	private static final int IMAGE_X = 12;
	private static final int IMAGE_Y = 35;
	private static final int BORDER_SIZE = 1;
	private static final int BRUSH_RADIUS = 1;
	private static AdvancedNet nn;

	public HandwritingRecognition() {
		frame = new JFrame() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				if (!painted) {
					g.setColor(new Color(255, 0, 0));
					g.fillRect(IMAGE_X-BORDER_SIZE, IMAGE_Y-BORDER_SIZE, PIXEL_COUNT*PIXEL_SIZE+(2*BORDER_SIZE),
							PIXEL_COUNT*PIXEL_SIZE+(2*BORDER_SIZE));
					painted = true;
				}
				for (int i=PIXEL_COUNT-1;i>=0;i--) {
					for (int j=PIXEL_COUNT-1;j>=0;j--) {
						g.setColor(new Color(image[i][j], image[i][j], image[i][j]));
						g.fillRect(IMAGE_X+PIXEL_SIZE*j, IMAGE_Y+PIXEL_SIZE*i, PIXEL_SIZE, PIXEL_SIZE);
					}
				}
			}
		};
		Container panel = frame.getContentPane();
		panel.setLayout(new FlowLayout());
		panel.setSize(new Dimension(800, 500));
		frame.setLayout(new BorderLayout());
		frame.setTitle("Handwriting Recognition");
		frame.setSize(400, 250);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent evt) {
				if (mouse_x >= IMAGE_X && mouse_x <= IMAGE_X+PIXEL_COUNT*PIXEL_SIZE
						&& mouse_y >= IMAGE_Y && mouse_y <= IMAGE_Y+PIXEL_COUNT*PIXEL_SIZE) {
					paintImage();
					pressed = true;
				}
			}
			@Override
			public void mouseReleased(MouseEvent evt) {
				pressed = false;
			}
			@Override
			public void mouseClicked(MouseEvent evt) {
			}
			@Override
			public void mouseEntered(MouseEvent evt) {
			}
			@Override
			public void mouseExited(MouseEvent evt) {
			}
		});
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			System.out.println(e.getMessage());
		}

		submit = new JButton("Submit");
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				double[] imageInput = new double[784];
				int k = 0;
				for (int i=0;i<28;i++) {
					for (int j=0;j<28;j++) {
						imageInput[k++] = 1-image[i][j]/255;
					}
				}
				double[] output = nn.feedForward(imageInput);
				double[] ranking[] = new double[output.length][];
				for (int i=0;i<output.length;i++) {
					ranking[i] = new double[2];
					double[] toInsert = new double[2];
					toInsert[0] = i;
					toInsert[1] = output[i];
					for (int j=0;j<=i;j++) {
						if (toInsert[1] > ranking[j][1]) {
							double[] temp = ranking[j];
							ranking[j] = toInsert;
							toInsert = temp;
						}
					}
				}
//				for (int i=0;i<output.length;i++) {
//					System.out.println(String.format("%2d: %1.8f", (int)ranking[i][0], ranking[i][1]));
//				}
				String[] conclusions = {"this is unlikely a ", "it might be a ", "i guess this is a ", "this is probably a ", "this is a "};
				System.out.println(conclusions[(int)Math.ceil(ranking[0][1]*5)-1] + (int)ranking[0][0]);
				for (int i=1;i<ranking.length;i++) {
					if (ranking[i][1] >= 0.1 && ranking[i-1][1] - ranking[i][1] < 0.35) {
						System.out.println(conclusions[(int)Math.ceil(ranking[i][1]*5)-1] + (int)ranking[i][0]);
					}
				}
				System.out.println();
			}
		});
		panel.add(submit, BorderLayout.EAST);

		reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				resetImage();
				frame.repaint();
			}
		});
		panel.add(reset, BorderLayout.SOUTH);

		resetImage();
	}
	

	public static void main(String[] args) {
		new HandwritingRecognition();
		double[][][] trainingData = mnistLoader.loadArrayZip("mnistData.zip", "TrainingData.ini");
		double[][][] testData = mnistLoader.loadArrayZip("mnistData.zip", "TestData.ini");
		nn = new AdvancedNet(new int[]{784, 50, 10});
		if (trainingData != null && testData != null) { 
			nn.stochasticGradientDescent(trainingData, 20, 10, 3.0, testData);
		}
		frame.setVisible(true);
		int last_mouse_x = mouse_x;
		int last_mouse_y = mouse_y;
		while (true) {
//			if (System.nanoTime()%4000000000.0 <= 1000000) {
//				showDataImage(trainingData, (int)(Math.random()*(trainingData.length-1)));
//			}
			setMousePos();
			if (pressed && (mouse_x != last_mouse_x || mouse_y != last_mouse_y)) {
				if (mouse_x >= IMAGE_X && mouse_x <= IMAGE_X+PIXEL_COUNT*PIXEL_SIZE
						&& mouse_y >= IMAGE_Y && mouse_y <= IMAGE_Y+PIXEL_COUNT*PIXEL_SIZE) {
					paintImage();
				}
			}
			last_mouse_x = mouse_x;
			last_mouse_y = mouse_y;
		}
	}
	

	private static void showDataImage(double[][][] data, int imageNumber) {
		for (int i=0;i<28;i++) {
			for (int j=0;j<28;j++) {
				image[i][j] = 255-(int)(data[imageNumber][0][i*28+j]*255);
			}
		}
		frame.repaint();
	}
	

	private static void resetImage() {
		image = new int[28][];
		for (int i=27;i>=0;i--) {
			image[i] = new int[28];
			Arrays.fill(image[i], 255);
		}
	}
	

	private static void setMousePos() {
		Point pos = frame.getMousePosition();
		if (pos != null) {
			mouse_x = (int)pos.getX();
			mouse_y = (int)pos.getY();
		}
	}
	

	private static void paintImage() {
		int pixel_x = (int)(mouse_x-IMAGE_X)/PIXEL_SIZE;
		int pixel_y = (int)(mouse_y-IMAGE_Y)/PIXEL_SIZE;
		for (int i=pixel_x-BRUSH_RADIUS;i<=pixel_x+BRUSH_RADIUS;i++) {
			for (int j=pixel_y-BRUSH_RADIUS;j<=pixel_y+BRUSH_RADIUS;j++) {
				if (i >= 0 && i < PIXEL_COUNT && j >= 0 && j < PIXEL_COUNT) {
					image[j][i] -= (int)((1-((Math.abs(i-pixel_x)+Math.abs(j-pixel_y))/((double)BRUSH_RADIUS*2.0)))*127);
					if (image[j][i]>255) {
						image[j][i] = 255;
					}
					if (image[j][i]<0) {
						image[j][i] = 0;
					}
				}
			}
		}
		frame.repaint();
	}
}
