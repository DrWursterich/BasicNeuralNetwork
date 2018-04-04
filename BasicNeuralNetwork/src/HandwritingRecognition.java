import BNN.*;
import statistics.*;

import java.util.Observer;
import java.util.Observable;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.shape.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

/***
 * A demonstration program that recognizes handwritten digits using the BNN package
 * @author Mario Schaeper
 */
@SuppressWarnings("restriction")
public class HandwritingRecognition extends Application {
	private final static double canvasX = 35;
	private final static double canvasY = 40;
	private final static int canvasSize = 28;
	private final static int canvasScale = 6;
	private final static double canvasBorderWidth = 4;
	private final static double lineWidth = 10;
	private final static double lineGraphWidth = 1000;
	private final static double lineGraphHeight = 500;
	private static LineGraph graph;
	private static NeuralNet nn;

	public static void main(String[] args) {
		double[][][] trainingData = mnistLoader.loadArrayZip("mnistData.zip", "TrainingData.ini");
		double[][][] testData = mnistLoader.loadArrayZip("mnistData.zip", "TestData.ini");
		if (trainingData == null || testData == null) {
			System.exit(0);
		}
		graph = new LineGraph(canvasX*2+canvasSize*canvasScale, lineGraphHeight+canvasY, lineGraphWidth, lineGraphHeight, 0, 15, 0.9, 1);
		try {
			int graphEvaluationAccuracy = graph.addGraph(Color.BLUE);
			int graphTrainingAccuracy = graph.addGraph(Color.RED);
			Thread nnThread = new Thread() {
				@Override
				public void run() {
					nn = new NeuralNet(784, 50, 10);
					nn.setMonitoring(false, true, false, true);
					nn.monitor.addObserver(new Observer() {
						private int iteration = 0;
						@Override
						public void update(Observable obj, Object arg) {
							NeuralNet.Monitor.Change change = (NeuralNet.Monitor.Change)arg;
							try {
								switch (change.getIndex()) {
								case NeuralNet.Monitor.TRAINING_ACCURACY:
									graph.extendGraph(graphTrainingAccuracy, new double[] {
											1+iteration/2, change.getValue()/trainingData.length});
									break;
								case NeuralNet.Monitor.EVALUATION_ACCURACY:
									graph.extendGraph(graphEvaluationAccuracy, new double[] {
											1+iteration/2, change.getValue()/testData.length});
									break;
								default:
									break;
								}
								iteration++;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					nn.stochasticGradientDescent(trainingData, 15, 10, 0.1, 5.0, nn.new CrossEntropy(), testData);
				}
			};
			nnThread.start();

			launch(args);

			try {
				nnThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Canvas canvas = new Canvas(canvasSize*canvasScale, canvasSize*canvasScale);
		canvas.setManaged(false);
		canvas.relocate(canvasX+canvasBorderWidth, canvasY+canvasBorderWidth);

		GraphicsContext gc;
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(lineWidth);
		gc.setLineCap(StrokeLineCap.ROUND);
		gc.setLineJoin(StrokeLineJoin.ROUND);

		GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.setVgap(10);

		Scene scene = new Scene(pane, 3*canvasX+canvasScale*canvasSize+lineGraphWidth, lineGraphHeight+2*canvasY);
		scene.setOnMousePressed(e -> {
			gc.beginPath();
			gc.lineTo(e.getSceneX()-canvasX-canvasBorderWidth, e.getSceneY()-canvasY-canvasBorderWidth);
			gc.stroke();
		});
		scene.setOnMouseDragged(e -> {
			gc.lineTo(e.getSceneX()-canvasX-canvasBorderWidth, e.getSceneY()-canvasY-canvasBorderWidth);
			gc.stroke();
		});

		Rectangle canvasOuter = new Rectangle(canvasX, canvasY,
				canvasSize*canvasScale+2*canvasBorderWidth, canvasSize*canvasScale+2*canvasBorderWidth);
		canvasOuter.setManaged(false);
		canvasOuter.setFill(Color.BROWN);

		Rectangle canvasInner = new Rectangle(canvasX+canvasBorderWidth,
				canvasY+canvasBorderWidth, canvasSize*canvasScale, canvasSize*canvasScale);
		canvasInner.setManaged(false);
		canvasInner.setFill(Color.WHITE);

		Group canvasGroup = new Group();
		canvasGroup.getChildren().addAll(canvasOuter, canvasInner, canvas);
		canvasGroup.setManaged(false);

		Button buttonReset = new Button("Reset");
		buttonReset.setCancelButton(true);
		buttonReset.setOnAction(e -> {
			gc.clearRect(0, 0, canvasSize*canvasScale, canvasSize*canvasScale);
		});

		WritableImage wim = new WritableImage(canvasSize*canvasScale, canvasSize*canvasScale);

		Button buttonSubmit = new Button("Submit");
		buttonSubmit.setOnAction(e -> {
			canvas.snapshot(null, wim);
			PixelReader px = wim.getPixelReader();
			byte[] buffer = new byte[(int)wim.getWidth() * (int)wim.getHeight() * 4];
			px.getPixels(0, 0, (int)wim.getWidth(), (int)wim.getHeight(), PixelFormat.getByteBgraInstance(), buffer, 0, (int)wim.getWidth()*4);
			double[][] inputImage = new double[canvasSize][];
			for (int i=inputImage.length-1;i>=0;i--) {
				inputImage[i] = new double[canvasSize];
				for (int j=inputImage[i].length-1;j>=0;j--) {
					inputImage[i][j] = 0;
					for (int k=canvasScale-1;k>=0;k--) {
						for (int l=canvasScale-1;l>=0;l--) {
							for (int m=2;m>=0;m--) {
								inputImage[i][j] += buffer[4*(canvasScale*(i*canvasSize*canvasScale+j)+k*canvasSize*canvasScale+l)+m]<0 ? (
										buffer[4*(canvasScale*(i*canvasSize*canvasScale+j)+k*canvasSize*canvasScale+l)+m]>=-1 ?
												buffer[4*(canvasScale*(i*canvasSize*canvasScale+j)+k*canvasSize*canvasScale+l)+m] : -1) : 0;
							}
						}
					}
					inputImage[i][j] = 1+inputImage[i][j] / (3*Math.pow(canvasScale, 2));
				}
			}
			ArrayDebug.printArray(nn.feedForward(VecMath.merge(inputImage)));
		});

		pane.add(buttonReset, 4, 23);
		pane.add(buttonSubmit, 10, 23);
		pane.getChildren().addAll(canvasGroup, graph.getCompleteGroup());

		primaryStage.setTitle("Handwriting Recognition");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
