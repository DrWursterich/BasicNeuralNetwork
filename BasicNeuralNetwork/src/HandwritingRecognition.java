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
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/***
 * A demonstration program that recognizes handwritten digits using the BNN
 * package
 * @author Mario Schaeper
 */
public class HandwritingRecognition extends Application {
	private final static double CANVAS_X = 35;
	private final static double CANVAS_Y = 40;
	private final static int CANVAS_SIZE = 28;
	private final static int CANVAS_SCALE = 6;
	private final static double CANVAS_BORDER_WIDTH = 4;
	private final static double LINE_WIDTH = 10;
	private final static double LINEGRAPH_WIDTH = 1000;
	private final static double LINEGRAPH_HEIGHT = 500;
	private final static int TRAINING_EPOCHS = 15;
	private static LineGraph graph;
	private static NeuralNet nn;
	private double dragStartY = 0;

	public static void main(String[] args) {
		double[][][] trainingData = mnistLoader.loadArrayZip(
				"mnistData.zip", "TrainingData.ini");
		double[][][] testData = mnistLoader.loadArrayZip(
				"mnistData.zip", "TestData.ini");
		if (trainingData == null || testData == null) {
			System.exit(0);
		}
		graph = new LineGraph(
				CANVAS_X * 2 + CANVAS_SIZE * CANVAS_SCALE,
				LINEGRAPH_HEIGHT + CANVAS_Y,
				LINEGRAPH_WIDTH,
				LINEGRAPH_HEIGHT,
				0,
				TRAINING_EPOCHS,
				0,
				100);
		int graphEvaluationAccuracy = graph.addGraph(Color.BLUE);
		int graphTrainingAccuracy = graph.addGraph(Color.RED);
		Thread nnThread = new Thread() {
			@Override
			public void run() {
				nn = new NeuralNet(784, 50, 10);
				nn.setMonitoring(false, true, false, true);
				double tdAcc = 0;
				for (int i = trainingData.length - 1; i >= 0; i--) {
					double[] output = nn.feedForward(trainingData[i][0]);
					int maxO = 0;
					int maxRO = 0;
					for (int j = output.length - 1; j >= 0; j--) {
						maxO = output[j] > output[maxO] ? j : maxO;
						maxRO = trainingData[i][1][j]
									> trainingData[i][1][maxRO]
								? j
								: maxRO;
					}
					tdAcc += maxO == maxRO ? 1 : 0;
				}
				graph.extendGraph(
						graphTrainingAccuracy,
						0,
						100 * tdAcc / trainingData.length);
				double edAcc = 0;
				for (int i = testData.length - 1; i >= 0; i--) {
					double[] output = nn.feedForward(testData[i][0]);
					int maxO = 0;
					int maxRO = 0;
					for (int j = output.length - 1; j >= 0; j--) {
						maxO = output[j] > output[maxO] ? j : maxO;
						maxRO = testData[i][1][j] > testData[i][1][maxRO]
								? j
								: maxRO;
					}
					edAcc += maxO == maxRO ? 1 : 0;
				}
				graph.extendGraph(
						graphEvaluationAccuracy,
						0,
						100 * edAcc / testData.length);
				nn.monitor.addObserver(new Observer() {
					private int iteration = 0;

					@Override
					public void update(Observable obj, Object arg) {
						NeuralNet.Monitor.Change change =
								(NeuralNet.Monitor.Change)arg;
						try {
							switch (change.getIndex()) {
								case NeuralNet.Monitor.TRAINING_ACCURACY:
									graph.extendGraph(
											graphTrainingAccuracy,
											1 + this.iteration / 2,
											change.getValue()
												/ trainingData.length * 100);
									break;
								case NeuralNet.Monitor.EVALUATION_ACCURACY:
									graph.extendGraph(
											graphEvaluationAccuracy,
											1 + this.iteration / 2,
											change.getValue()
												/ testData.length * 100);
									break;
								default:
									break;
							}
							this.iteration++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				nn.stochasticGradientDescent(
						trainingData,
						TRAINING_EPOCHS,
						10,
						0.1,
						5.0,
						nn.new CrossEntropy(),
						testData);
			}
		};
		nnThread.setPriority(Thread.MAX_PRIORITY);
		nnThread.start();

		Application.launch(args);

		try {
			nnThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Canvas canvas = new Canvas(
				CANVAS_SIZE * CANVAS_SCALE,
				CANVAS_SIZE * CANVAS_SCALE);
		canvas.setManaged(false);
		canvas.relocate(
				CANVAS_X + CANVAS_BORDER_WIDTH,
				CANVAS_Y + CANVAS_BORDER_WIDTH);

		GraphicsContext gc;
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(LINE_WIDTH);
		gc.setLineCap(StrokeLineCap.ROUND);
		gc.setLineJoin(StrokeLineJoin.ROUND);

		GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.setVgap(10);

		Scene scene = new Scene(
				pane,
				3 * CANVAS_X
					+ CANVAS_SCALE * CANVAS_SIZE
					+ LINEGRAPH_WIDTH
					+ graph.getMarkingSizeX(),
				LINEGRAPH_HEIGHT
					+ 2 * CANVAS_Y
					+ graph.getMarkingSizeY());
		scene.setOnMousePressed(e -> {
			gc.beginPath();
			gc.lineTo(
					e.getSceneX() - CANVAS_X - CANVAS_BORDER_WIDTH,
					e.getSceneY() - CANVAS_Y - CANVAS_BORDER_WIDTH);
			gc.stroke();
		});
		scene.setOnMouseDragged(e -> {
			gc.lineTo(
					e.getSceneX() - CANVAS_X - CANVAS_BORDER_WIDTH,
					e.getSceneY() - CANVAS_Y - CANVAS_BORDER_WIDTH);
			gc.stroke();
		});

		Rectangle canvasOuter = new Rectangle(
				CANVAS_X,
				CANVAS_Y,
				CANVAS_SIZE * CANVAS_SCALE + 2 * CANVAS_BORDER_WIDTH,
				CANVAS_SIZE * CANVAS_SCALE + 2 * CANVAS_BORDER_WIDTH);
		canvasOuter.setManaged(false);
		canvasOuter.setFill(Color.BROWN);

		Rectangle canvasInner = new Rectangle(
				CANVAS_X + CANVAS_BORDER_WIDTH,
				CANVAS_Y + CANVAS_BORDER_WIDTH,
				CANVAS_SIZE * CANVAS_SCALE,
				CANVAS_SIZE * CANVAS_SCALE);
		canvasInner.setManaged(false);
		canvasInner.setFill(Color.WHITE);

		Group canvasGroup = new Group();
		canvasGroup.getChildren().addAll(canvasOuter, canvasInner, canvas);
		canvasGroup.setManaged(false);

		Button buttonReset = new Button("Reset");
		buttonReset.setCancelButton(true);
		buttonReset.setOnAction(e -> {
			gc.clearRect(
					0,
					0,
					CANVAS_SIZE * CANVAS_SCALE,
					CANVAS_SIZE * CANVAS_SCALE);
		});

		WritableImage wim = new WritableImage(
				CANVAS_SIZE * CANVAS_SCALE,
				CANVAS_SIZE * CANVAS_SCALE);

		Button buttonSubmit = new Button("Submit");
		buttonSubmit.setOnAction(e -> {
			canvas.snapshot(null, wim);
			PixelReader px = wim.getPixelReader();
			byte[] buffer = new byte[
					(int)wim.getWidth() * (int)wim.getHeight() * 4];
			px.getPixels(
					0,
					0,
					(int)wim.getWidth(),
					(int)wim.getHeight(),
					PixelFormat.getByteBgraInstance(),
					buffer,
					0,
					(int)wim.getWidth() * 4);
			double[][] inputImage = new double[CANVAS_SIZE][];
			for (int i = inputImage.length - 1; i >= 0; i--) {
				inputImage[i] = new double[CANVAS_SIZE];
				for (int j = inputImage[i].length - 1; j >= 0; j--) {
					inputImage[i][j] = 0;
					for (int k = CANVAS_SCALE - 1; k >= 0; k--) {
						for (int l = CANVAS_SCALE - 1; l >= 0; l--) {
							for (int m = 2; m >= 0; m--) {
								final int pixelIndex = 4
										* (CANVAS_SCALE
											* (i
											* CANVAS_SIZE
											* CANVAS_SCALE
											+ j)
										+ k
											* CANVAS_SIZE
											* CANVAS_SCALE
											+ l)
										+ m;
								inputImage[i][j] += buffer[pixelIndex] < 0
												? (buffer[pixelIndex] >= -1
													? buffer[pixelIndex]
													: -1)
												: 0;
							}
						}
					}
					inputImage[i][j] = 1
							+ inputImage[i][j]
								/ (3 * Math.pow(CANVAS_SCALE, 2));
				}
			}
			System.out.println();
			ArrayDebug.setDoubleFormat(" 5.0");
			ArrayDebug.printArray(new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});
			// The inputImage array has to be merged if a fcnn is used
			ArrayDebug.setDoubleFormat(" 1.2");
			ArrayDebug.printArray(nn.feedForward(VecMath.merge(inputImage)));
		});

		graph.setMarking(LineGraph.marking(
				TRAINING_EPOCHS + 1,	//amountX
				6,						//amountY
				2,						//digitsX
				0,						//commaDigitsX
				3,						//digitsY
				0,						//commaDigitsY
				6,						//length
				Font.font(
					"verdana",
					FontWeight.LIGHT,
					FontPosture.REGULAR,
					10)));
		graph.setX(graph.getX() + graph.getMarkingSizeX());
		graph.setScaleStrokeWidth(2);

		graph.setOnScroll(e -> {
			this.zoomGraph(
					e.getDeltaY() > 0 ? -1 : 1,
					(e.getSceneY()
							- (graph.getY() + graph.getHeight()))
						/ graph.getHeight());
		});

		graph.setOnMousePressed(e -> {
			this.dragStartY = e.getSceneY();
		});

		graph.setOnMouseDragged(e -> {
			double distance = Math.min(Math.max(
					(e.getSceneY() - this.dragStartY)
						/ (graph.getHeight()
						/ (graph.getYEnd() - graph.getYStart())),
					graph.getYEnd() - 100), graph.getYStart());
			graph.setYStart(graph.getYStart() - distance);
			graph.setYEnd(graph.getYEnd() - distance);
			this.dragStartY = e.getSceneY();
		});

		pane.add(buttonReset, 4, 23);
		pane.add(buttonSubmit, 10, 23);
		pane.getChildren().addAll(canvasGroup, graph);

		primaryStage.setTitle("Handwriting Recognition");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void zoomGraph(double amount, double to) {
		to = Math.max(Math.min(-to, 1), 0);
		amount = graph.getYEnd() - graph.getYStart() + amount < 1
				? 1 - graph.getYEnd() + graph.getYStart()
				: amount;
		double overZoom = graph.getYStart() - amount * (1 - to) < 0
				? graph.getYStart() - amount * (1 - to)
				: (graph.getYEnd() + amount * to > 100
					? -(100 - graph.getYEnd() + amount * to)
					: 0);
		graph.setYScale(
				graph.getYStart() - amount * (1 - to) + overZoom,
				graph.getYEnd() + amount * to - overZoom);
		graph.setYScale(
				Math.max(graph.getYStart(), 0),
				Math.min(graph.getYEnd(), 100));
	}
}
