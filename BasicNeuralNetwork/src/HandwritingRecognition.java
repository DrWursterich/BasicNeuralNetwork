import BNN.*;
import statistics.*;

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
	public static void main(String[] args) {
		launch(args);
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
		
		Scene scene = new Scene(pane, 225, 150);
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
//		buttonReset.setManaged(false);
		buttonReset.relocate(150, 0);
		buttonReset.setCancelButton(true);
		buttonReset.setOnAction(e -> {
			gc.clearRect(0, 0, canvasSize, canvasSize);
		});
		
		LineGraph graph = new LineGraph(100, 100, 100, 50, 0, 25, 0, 40);
//		graph.addGraph(new double[][] {{0, 0}, {1, 1}, {2, 4}}, Color.RED);
//		graph.extendGraph(0, new double[] {3, 8});
//		graph.addGraph(new double[][] {{0, 5}, {2, 10}, {5, 15}}, Color.BLUE);
		
		pane.add(buttonReset, 3, 8);
		pane.getChildren().addAll(canvasGroup, graph.getAllGroup());
		primaryStage.setTitle("Handwriting Recognition");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		NeuralNet nn;
		double[][][] trainingData = mnistLoader.loadArrayZip("mnistData.zip", "TrainingData.ini");
		double[][][] testData = mnistLoader.loadArrayZip("mnistData.zip", "TestData.ini");
		if (trainingData == null || testData == null) {
			System.exit(0);
		}
		nn = new NeuralNet(784, 50, 10);
		nn.setMonitoring(false, false, true, true);
		nn.stochasticGradientDescent(trainingData, 30, 10, 0.1, 5.0, nn.new CrossEntropy(), testData);
	}
}
