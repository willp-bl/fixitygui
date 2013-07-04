package spruce.fixitygui;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

	private static File checksumFile = null;
	private static File inputDir = null;
	
	public static void main(String[] args) {
		Application.launch(args);
		
	}

	@Override
	public void start(final Stage pStage) throws Exception {
		pStage.setTitle("Fixity-GUI v0.0.1");
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);

		int count = 0;

		Text title = new Text(pStage.getTitle());
		title.setFont(Font.font(title.getFont().getName(), FontWeight.BOLD, title.getFont().getSize()*2));
		grid.add(title, 0, count++);
		
		Button buttonSetInput = new Button("Set input...");
		final Label labelInput = new Label("No input set");
		grid.add(labelInput, 1, count);
		buttonSetInput.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				DirectoryChooser dc = new DirectoryChooser();
				dc.setTitle("Choose an input directory");				
				inputDir = dc.showDialog(pStage);
				labelInput.setText(inputDir.getAbsolutePath());
			}
			
		});
		grid.add(buttonSetInput, 0, count++);
		
		Button buttonSetChecksumFile = new Button("Set checksum file...");
		final Label labelChecksumFile = new Label("No checksum file set");
		grid.add(labelChecksumFile, 1, count);
		buttonSetChecksumFile.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fc = new FileChooser();
				fc.setTitle("Choose an output file");
				checksumFile = fc.showSaveDialog(pStage);
				labelChecksumFile.setText(checksumFile.getAbsolutePath());
			}
			
		});
		grid.add(buttonSetChecksumFile, 0, count++);

		Button buttonCalc = new Button("Calculate checksums");
		grid.add(buttonCalc, 0, count++);
		final Label labelChecksumStatus = new Label("");
		grid.add(labelChecksumStatus, 1, count-1);
		
		Button buttonVerify = new Button("Verify checksums");
		grid.add(buttonVerify, 0, count++);
		final Label labelVerifyStatus = new Label("");
		grid.add(labelVerifyStatus, 1, count-1);

		final ProgressBar progress = new ProgressBar();
		grid.add(progress, 0, count++);
		
		Button buttonQuit = new Button("Quit");
		buttonQuit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				pStage.close();
			}
			
		});
		grid.add(buttonQuit, 0, count++);		

		buttonCalc.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				labelChecksumStatus.setText("Calculated: "+Calc.calcChecksums(inputDir, checksumFile, progress));
			}
			
		});

		buttonVerify.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				labelVerifyStatus.setText("Failures: "+Calc.verifyChecksums(inputDir, checksumFile, progress).size());
			}
			
		});

		Scene scene = new Scene(grid, 640, 480, Color.ANTIQUEWHITE);
		pStage.setScene(scene);

		pStage.show();

	}
	

}
