package com.niuniukeaiyouhaochi.os.UI;


import com.niuniukeaiyouhaochi.os.CPU.CPU;
import com.niuniukeaiyouhaochi.os.Listener.DragUtil;
import com.niuniukeaiyouhaochi.os.ProcessManager.Process;
import com.niuniukeaiyouhaochi.os.ProcessManager.ProcessController;
import com.niuniukeaiyouhaochi.os.device.Device;
import com.niuniukeaiyouhaochi.os.device.DeviceController;
import com.niuniukeaiyouhaochi.os.memory.Address;
import com.niuniukeaiyouhaochi.os.memory.MemoryBlock;
import com.niuniukeaiyouhaochi.os.memory.MemoryController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.ResourceBundle;

public class ProcessXMLController implements Initializable {

	@FXML
	private NumberAxis x;

	@FXML
	private NumberAxis y;

	@FXML
	private Button startButton;

	@FXML
	private Button stopStart;

	@FXML
	private LineChart<?, ?> lineChart;

	@FXML
	private Label priority;

	@FXML
	private Label equipment;

	@FXML
	private Label completion;

	@FXML
	private ProgressBar timeClip;

	@FXML
	private Label processPercentage;

	@FXML
	private AnchorPane ProcessBlock;

	@FXML
	private ProgressIndicator ProcessIndicator;

	@FXML
	private Pane Interrupter1;

	@FXML
	private Label InterrupterLabel1;

	@FXML
	private Pane Interrupter2;

	@FXML
	private Label InterrupterLabel2;

	@FXML
	private Pane Interrupter3;

	@FXML
	private Label InterrupterLabel3;

	@FXML
	private Pane Interrupter4;

	@FXML
	private Label InterrupterLabel4;

	@FXML
	private Pane Interrupter5;

	@FXML
	private Label InterrupterLabel5;

	@FXML
	private Pane Ready1;

	@FXML
	private Label ReadyLabel1;

	@FXML
	private Pane Ready2;

	@FXML
	private Label ReadyLabel2;

	@FXML
	private Pane Ready3;

	@FXML
	private Label ReadyLabel3;

	@FXML
	private Pane Ready4;

	@FXML
	private Label ReadyLabel4;

	@FXML
	private Pane Ready5;

	@FXML
	private Label ReadyLabel5;

	@FXML
	private Pane Blocked1;

	@FXML
	private Label BlockedLabel1;

	@FXML
	private Pane Blocked2;

	@FXML
	private Label BlockedLabel2;

	@FXML
	private Pane Blocked3;

	@FXML
	private Label BlockedLabel3;

	@FXML
	private Pane Blocked4;

	@FXML
	private Label BlockedLabel4;

	@FXML
	private Pane Blocked5;

	@FXML
	private Label BlockedLabel5;

	@FXML
	private AnchorPane DA1;

	@FXML
	private AnchorPane DB2;

	@FXML
	private AnchorPane DB1;

	@FXML
	private AnchorPane DA2;

	@FXML
	private AnchorPane DeviceCUsed;

	@FXML
	private Label DeviceCProcess;

	@FXML
	private AnchorPane DeviceBUsed2;

	@FXML
	private Label DeviceBProcess2;

	@FXML
	private AnchorPane DeviceBUsed1;

	@FXML
	private Label DeviceBProcess1;

	@FXML
	private AnchorPane DeviceAUsed2;

	@FXML
	private Label DeviceAProcess2;

	@FXML
	private AnchorPane DeviceAUsed1;

	@FXML
	private Label DeviceAProcess1;

	@FXML
	private AnchorPane DC;

	@FXML
	private AnchorPane top;

	@FXML
	private ImageView backIcon;

	@FXML
	private ImageView outIcon;

	@FXML
	private StackPane mainPane;

	@FXML
	private BorderPane root;


	private TimerChart tc;
	private static List<Label> labels = new ArrayList<>();
	private static Color DefaultColor = Color.rgb(215, 215,215);
	private static List<Pane> InterrupterPane = new ArrayList<>();
	private static List<Pane> ReadyPane = new ArrayList<>();
	private static List<Pane> BlockedPane = new ArrayList<>();
	private static List<Label> InterrupterLabels = new ArrayList<>();
	private static List<Label> ReadyLabels = new ArrayList<>();
	private static List<Label> BlockedLabels = new ArrayList<>();
	private static Thread LabelThread; 														// ??????CPU????????????????????????
	private static Thread LabelThread1;														// ????????????????????????
	private static Thread LabelThread2;														// ?????????????????????????????????
	private static DeviceController deviceController = DeviceController.getInstance();		// ???????????????
	private static ProcessController processController = ProcessController.getInstance();	// ???????????????

	Process lastProcess = null;
	Process process = null;
	Address lastAddress=null;
	String  pid;


	// ??????????????????
	@FXML
	void start(ActionEvent event) {
		if (!CPU.getInstance().isRunning()) {
			System.out.println("?????????????????? ???");
			CPU.getInstance().play();
			tc.start();
			try {
				LabelThread.notify();
				LabelThread1.notify();
				LabelThread2.notify();
			} catch (Exception e) {
				System.out.println("???????????????????????????");
			}
		}
	}

	@FXML
	void stop(ActionEvent event) {
		System.out.println("??????");
		CPU.getInstance().cpuStop();
		tc.cancel();
		tc.reset();
		LabelThread.interrupt();
		LabelThread1.interrupt();
		LabelThread2.interrupt();
	}


	/**
	 * description ???????????????????????????????????????
	 * ??????	mergeMemory() (dongye)
	 * ?????? MemoryRefresh() (pc)
	 * param void
	 * return void
	 * author pc
	 * createTime  2021/10/22
	 **/
	@FXML
	void compress(ActionEvent event) {
		if (!CPU.getInstance().isRunning()) {
			MemoryController.getInstance().mergeMemory();
			MemoryRefresh();
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			                 alert.setTitle("??????");
			                 alert.setHeaderText("?????????CPU???????????????\n" +
									 "???????????????????????????????????????CPU??????");
			                 alert.showAndWait();
		}
	}


	@FXML
	void back(MouseEvent event) {
		ProcessMain.stage.hide();
		LoginMain.LoginStage.show();
	}


	/**
	 * description ????????????
	 * param void
	 * return void
	 * author pc
	 * createTime 2021/10/23
	 **/
	@FXML
	void out(MouseEvent event) {
		Stage stage = (Stage) top.getScene().getWindow();
		stage.close();
	}

	/**
	 * description ????????????????????????
	 * param
	 * return
	 * author pc
	 * createTime 2021/10/21
	 **/
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		XYChart.Series way1 = new XYChart.Series();
		XYChart.Series way2 = new XYChart.Series();
		XYChart.Series way3 = new XYChart.Series();
		way1.setName("Running");
		way2.setName("Blocked");
		way3.setName("Ready");
		lineChart.getData().addAll(way1, way2, way3);
		Thread thread = new Thread(ProcessController.getInstance());
		thread.setDaemon(true);
		thread.start();
		while (!QueuePaneInit()) {}
		QueueRefresh();
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 16; j ++) {
				Label label = new Label();
				label.setLayoutX(i * 17 + 2);
				label.setLayoutY(j * 17 + 2);
				ProcessBlock.getChildren().add(label);
				label.setMinSize(15, 15);
				label.setPrefSize(15, 15);
				label.setMaxSize(15, 15);
				label.setId("diskLabel");
				labels.add(label);
			}
		}

		LabelThread = new Thread(() -> {

			while (true) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						timeClip.setProgress(processController.getAllRunTime() / 2.0);

						if ( processController.getRunningProcess() != null
								&& processController.getRunningProcess() !=
								processController.getHangOutProcess()) {

							Process process = processController.getRunningProcess();
							Color color = findColorFromMemoryBlock(process.getPcb().getId());
							ProcessIndicator.setStyle("-fx-progress-color: rgb(" + (int) (color.getRed() * 255) +"," +(int) (color.getGreen() * 255) +"," + (int) (color.getBlue() * 255) +");");
							double value = process.getPcb().getRestRunTime()
									/ process.getPcb().getTotalRunTime() * 100;


							completion.setText((100 - (int)value) + "%");
							ProcessIndicator.setProgress(1.0 - value / 100);
							equipment.setText(process.getDeviceType());
							priority.setText(String.valueOf(process.getPcb().getPriority()));
							processPercentage.setText(process.getPcb().getId());
						}
						QueueRefresh();
					}
				});
				try {
					Thread.sleep(50L);
				} catch (Exception e) {

				}
			}
		});
		LabelThread.setDaemon(true);

		// ?????????????????????
		LabelThread1 = new Thread(() -> {

			while (true) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						List<Device> deviceList = deviceController.deviceList;

						if (deviceList.get(0).isOccupy()) {
							DA1.setVisible(false);
							DeviceAUsed1.setVisible(true);
							DeviceAProcess1.setText(deviceList.get(0).getPID());
						} else {
							DA1.setVisible(true);
							DeviceAUsed1.setVisible(false);
						}

						if (deviceList.get(1).isOccupy()) {
							DA2.setVisible(false);
							DeviceAUsed2.setVisible(true);
							DeviceAProcess2.setText(deviceList.get(1).getPID());
						} else {
							DA2.setVisible(true);
							DeviceAUsed2.setVisible(false);
						}

						if (deviceList.get(2).isOccupy()) {
							DB1.setVisible(false);
							DeviceBUsed1.setVisible(true);
							DeviceBProcess1.setText(deviceList.get(2).getPID());
						} else {
							DB1.setVisible(true);
							DeviceBUsed1.setVisible(false);
						}

						if (deviceList.get(3).isOccupy()) {
							DB2.setVisible(false);
							DeviceBUsed2.setVisible(true);
							DeviceBProcess2.setText(deviceList.get(3).getPID());
						} else {
							DB2.setVisible(true);
							DeviceBUsed2.setVisible(false);
						}

						if (deviceList.get(4).isOccupy()) {
							DC.setVisible(false);
							DeviceCUsed.setVisible(true);
							DeviceCProcess.setText(deviceList.get(4).getPID());
						} else {
							DC.setVisible(true);
							DeviceCUsed.setVisible(false);
						}
					}
				});
				try {
					Thread.sleep(100L);
				} catch (Exception e) {

				}
			}
		});
		LabelThread1.setDaemon(true);


		LabelThread2 = new Thread(() -> {
			while (true) {
				 process = processController.getRunningProcess();
				 Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							if (process == processController.getHangOutProcess()) {
								return;
							}
							else {
								pid=process.getPcb().getId();
							}
							Address address=null;
							for(int i=0;i<MemoryController.getInstance().getMyMemoryBlockList().size();i++)
							{
								if(MemoryController.getInstance().getMyMemoryBlockList().get(i).getPID()
										.equals(pid)){
									address=MemoryController.getInstance().getMyMemoryBlockList().get(i).getAddress();
								}
							}

							if (lastAddress == null) {
								lastAddress = address;
							}

							for (int i = lastAddress.getStartAddress();
								 i <= lastAddress.getEndAddress(); i++) {
								labels.get(i).setBorder(null);
							}
							for (int i = address.getStartAddress();
								 i <= address.getEndAddress(); i++) {
								labels.get(i).setBorder(new Border(new BorderStroke(Paint.valueOf("#FB0D11"),
										BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(2))));
							}
							lastProcess = process;
							lastAddress=address;
						} catch (Exception e) {
							System.out.println(" ????????????????????? ");
						}
					}
				});

				try {
					Thread.sleep(50L);
				} catch (Exception e) {

				}
			}
		});
		LabelThread2.setDaemon(true);


		tc = new TimerChart(this);
		// ??????0.5 s??????
		tc.setPeriod(Duration.seconds(0.5));
		LabelThread.start();
		LabelThread1.start();
		LabelThread2.start();

		x.setLowerBound(0.0D);
		x.setUpperBound(10.0D);
		y.setLowerBound(0.0D);
		y.setUpperBound(5);
		tc.valueProperty().addListener(new ChangeListener<ArrayList<Integer>>() {
			/*
			 * description ????????????lineChart???????????????
			 * param void
			 * return void
			 * author pc
			 * createTime 2021/10/20
			 **/
			public double cur = 0;
			@Override
			public void changed(ObservableValue<? extends ArrayList<Integer>> observableValue, ArrayList<Integer> integers, ArrayList<Integer> t1) {
				if (t1 != null) {
					XYChart.Data<Number,Number> d1 = new XYChart.Data<>(cur,t1.get(0));
					XYChart.Data<Number,Number> d2 = new XYChart.Data<>(cur,t1.get(1));
					XYChart.Data<Number,Number> d3 = new XYChart.Data<>(cur,t1.get(2));
					cur += 0.5;
					way1.getData().add(d1);
					way2.getData().add(d2);
					way3.getData().add(d3);
				}
			}
		});
		MemoryInit();
		addListener();
	}

	void addListener(){

		// ??????????????????
		DragUtil.addDragListener(ProcessMain.LoginStage, top);

		// ????????????????????????
		outIcon.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				outIcon.setCursor(Cursor.HAND);
			}
		});

		// ??????????????????
		backIcon.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				backIcon.setCursor(Cursor.HAND);
			}
		});
	}

	/**
	 * description ????????????????????? , ??????????????????????????????????????????
	 * param void
	 * return boolean
	 * author pc
	 * createTime  2021/10/22
	 **/
	public boolean QueuePaneInit() {
		InterrupterPane.add(Interrupter1);
		InterrupterPane.add(Interrupter2);
		InterrupterPane.add(Interrupter3);
		InterrupterPane.add(Interrupter4);
		InterrupterPane.add(Interrupter5);

		ReadyPane.add(Ready1);
		ReadyPane.add(Ready2);
		ReadyPane.add(Ready3);
		ReadyPane.add(Ready4);
		ReadyPane.add(Ready5);

		BlockedPane.add(Blocked1);
		BlockedPane.add(Blocked2);
		BlockedPane.add(Blocked3);
		BlockedPane.add(Blocked4);
		BlockedPane.add(Blocked5);

		BlockedLabels.add(BlockedLabel1);
		BlockedLabels.add(BlockedLabel2);
		BlockedLabels.add(BlockedLabel3);
		BlockedLabels.add(BlockedLabel4);
		BlockedLabels.add(BlockedLabel5);

		ReadyLabels.add(ReadyLabel1);
		ReadyLabels.add(ReadyLabel2);
		ReadyLabels.add(ReadyLabel3);
		ReadyLabels.add(ReadyLabel4);
		ReadyLabels.add(ReadyLabel5);

		InterrupterLabels.add(InterrupterLabel1);
		InterrupterLabels.add(InterrupterLabel2);
		InterrupterLabels.add(InterrupterLabel3);
		InterrupterLabels.add(InterrupterLabel4);
		InterrupterLabels.add(InterrupterLabel5);
		return true;
	}

	// ????????????????????????
	public static void MemoryInit() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (Label label : labels) {
					label.setBackground(new Background(new BackgroundFill(DefaultColor, null, null)));
				}
			}
		});
	}

	// ????????? ???????????? ??????
	public static void MemoryRefresh() {
		List<MemoryBlock> MemoryBlocks = MemoryController.getInstance().getMyMemoryBlockList();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<=511;i++)
				{
					labels.get(i).setBackground(
							new Background(new BackgroundFill(DefaultColor,null,null)));
					labels.get(i).setBorder(null);
				}

				for (MemoryBlock memoryBlock : MemoryBlocks) {
					for (int i = memoryBlock.getAddress().getStartAddress(); i <= memoryBlock.getAddress().getEndAddress(); i++) {

						labels.get(i).setBackground(new Background(new BackgroundFill(
								memoryBlock.getColor(), null, null
						)));

					}
				}
			}
		});
	}

	// ?????????????????????
	public static void MemoryRelease(MemoryBlock memoryBlock) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = memoryBlock.getAddress().getStartAddress(); i <= memoryBlock.getAddress().getEndAddress(); i++) {
					labels.get(i).setBackground(new Background(new BackgroundFill(
							DefaultColor, null, null
					)));
				}
			}
		});
	}

	// ??????????????? ??????
	public static void MemoryOccupy(MemoryBlock memoryBlock) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = memoryBlock.getStartPosition(); i <= memoryBlock.getEndPosition(); i++) {
					labels.get(i).setBackground(new Background(new BackgroundFill(
							memoryBlock.getColor(), null, null
					)));
				}
			}
		});
	}

	/**
	 * description ????????????????????????
	 * param void
	 * return void
	 * author pc
	 * createTime 2021/10/22
	 **/
	public static void QueueRefresh() {
		List<Process> InterrupterProcess = ProcessController.getInstance().getRunningQueue();
		List<Process> ReadyProcess = ProcessController.getInstance().getReadyQueue();
		List<Process> BlockedProcess = ProcessController.getInstance().getBlockedQueue();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 5; i++) {
					if (i < InterrupterProcess.size()) {
						Process process = InterrupterProcess.get(i);
						InterrupterPane.get(i).setBackground(new Background(new BackgroundFill(
								findColorFromMemoryBlock(process.getPcb().getId()), null, null)));
						InterrupterLabels.get(i).setText(process.getPcb().getId());
					} else {
						InterrupterPane.get(i).setBackground(new Background(new BackgroundFill(
								DefaultColor, null, null)));
						InterrupterLabels.get(i).setText(" ");
					}

					if (i < BlockedProcess.size()) {
						Process process = BlockedProcess.get(i);
						BlockedPane.get(i).setBackground(new Background(new BackgroundFill(
								findColorFromMemoryBlock(process.getPcb().getId()), null, null)));
						BlockedLabels.get(i).setText(process.getPcb().getId());
					} else {
						BlockedPane.get(i).setBackground(new Background(new BackgroundFill(
								DefaultColor, null, null)));
						BlockedLabels.get(i).setText(" ");
					}

					if (i < ReadyProcess.size()) {
						Process process = ReadyProcess.get(i);
						ReadyPane.get(i).setBackground(new Background(new BackgroundFill(
								findColorFromMemoryBlock(process.getPcb().getId()), null, null)));
						ReadyLabels.get(i).setText(process.getPcb().getId());
					} else {
						ReadyPane.get(i).setBackground(new Background(new BackgroundFill(
								DefaultColor, null, null)));
						ReadyLabels.get(i).setText(" ");
					}
				}
			}
		});
	}

	static List<MemoryBlock> memoryBlockList = MemoryController.getInstance().getMyMemoryBlockList();
	/**
	 * description
	 * param String PID ????????????PID
	 * return Color ????????????????????????
	 * author pc
	 * createTime  2021/10/22
	 **/
	public static Color findColorFromMemoryBlock(String PID) {
		for (MemoryBlock memoryBlock : memoryBlockList) {
			if (memoryBlock.getPID().equals(PID)) {
				return memoryBlock.getColor();
			}
		}
		return DefaultColor;
	}
}

