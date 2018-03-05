import BNN.*;
import statistics.*;

import java.util.Observer;
import java.util.Observable;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.shape.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

/***
 * A demonstration program that recognizes handwritten digits using the BNN package
 * @author Mario Schaeper
 */
@SuppressWarnings("restriction")
public class HandwritingRecognition extends Application {
	private static LineGraph graph;
	private static NeuralNet nn;

	public static void main(String[] args) {
		double[][][] trainingData = mnistLoader.loadArrayZip("mnistData.zip", "TrainingData.ini");
		double[][][] testData = mnistLoader.loadArrayZip("mnistData.zip", "TestData.ini");
		if (trainingData == null || testData == null) {
			System.exit(0);
		}
		graph = new LineGraph(100, 550, 1000, 500, 0, 16, 0.9, 1);
		try {
			int graphEvaluationAccuracy = graph.addGraph(new double[][] {}, Color.BLUE);
			int graphTrainingAccuracy = graph.addGraph(new double[][] {}, Color.RED);
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
											1+(int)(iteration/2), change.getValue()/trainingData.length});
									break;
								case NeuralNet.Monitor.EVALUATION_ACCURACY:
									graph.extendGraph(graphEvaluationAccuracy, new double[] {
											1+(int)(iteration/2), change.getValue()/testData.length});
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
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final double canvasX = 35;
		final double canvasY = 40;
		final double canvasSize = 28;
		final double canvasBorderWidth = 4;

		Canvas canvas = new Canvas(canvasSize, canvasSize);
		canvas.setManaged(false);
		canvas.relocate(canvasX+canvasBorderWidth, canvasY+canvasBorderWidth);

		GraphicsContext gc;
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(2);
		gc.setLineCap(StrokeLineCap.ROUND);
		gc.setLineJoin(StrokeLineJoin.ROUND);

		GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.setVgap(10);

		Scene scene = new Scene(pane, 1150, 600);
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
				canvasSize+2*canvasBorderWidth, canvasSize+2*canvasBorderWidth);
		canvasOuter.setManaged(false);
		canvasOuter.setFill(Color.BROWN);

		Rectangle canvasInner = new Rectangle(canvasX+canvasBorderWidth,
				canvasY+canvasBorderWidth, canvasSize, canvasSize);
		canvasInner.setManaged(false);
		canvasInner.setFill(Color.WHITE);

		Group canvasGroup = new Group();
		canvasGroup.getChildren().addAll(canvasOuter, canvasInner, canvas);
		canvasGroup.setManaged(false);

		Button buttonReset = new Button("Reset");
		buttonReset.setCancelButton(true);
		buttonReset.setOnAction(e -> {
			gc.clearRect(0, 0, canvasSize, canvasSize);
		});

		pane.add(buttonReset, 3, 8);
		pane.getChildren().addAll(canvasGroup, graph.getCompleteGroup());
		primaryStage.setTitle("Handwriting Recognition");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
