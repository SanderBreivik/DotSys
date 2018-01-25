package dotsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;


public class DotSystemController {

	String username;
	
	@FXML
	ListView<Person> listviewPersons;


	@FXML
	static
	ObservableList<Person> list = FXCollections.observableArrayList();

	@FXML
	private Button candy;

	@FXML
	private Button cake;

	@FXML
	private Button missed;

	@FXML
	private Button unprepared;

	@FXML
	private TextField late;

	@FXML
	private Button lateButton;

	@FXML
	private Button buttonAdd;
	
	@FXML
	private Button buttonRemove;

	@FXML
	private TextField user;
	
	@FXML
	private Label labelFilename;

	@FXML
	private Label dots;

	@FXML
	private Label error;


	public void initialize() {
		listviewPersons.setItems(list.sorted());
		
		buttonAdd.setDisable(true);
		buttonRemove.setDisable(true);
		lateButton.setDisable(true);
		
		// Add listener to listview
		listviewPersons.getSelectionModel().selectedItemProperty().addListener((observable, oldval, newval) -> {
			if (newval != null)
				updateLabel();
			
			buttonRemove.setDisable(newval == null);
		});
		
		user.textProperty().addListener((observable, oldval, newval) -> {
			buttonAdd.setDisable("".equals(newval));
		});
		
		late.textProperty().addListener((observable, oldval, newval) -> {
			lateButton.setDisable("".equals(newval));
		});
	}


	@FXML
	void addPerson() {
		username = user.getText().substring(0, 1).toUpperCase() + user.getText().substring(1).toLowerCase();
		Person p = new Person(username);
		System.out.println("Person:" + p);
		System.out.println();
		list.add(p);
		updateLabel();
		user.clear();
	}
	
	@FXML
	void removePerson() {
		Person p = listviewPersons.getSelectionModel().getSelectedItem();
		if (p == null)
			return;
		
		if (p.getDots() != 0) {
			Alert sureBoutThat = new Alert(AlertType.CONFIRMATION);
			sureBoutThat.setTitle("Sikker?");
			sureBoutThat.setHeaderText(null);
			sureBoutThat.setContentText(p.getName() + " har " + p.getDots() + " prikker, er du sikker på"
					+ " at du vil fjerne denne personen?");
			Optional<ButtonType> result = sureBoutThat.showAndWait();
			if (result.get() != ButtonType.OK) {
				// User cancelled
				return;
			}
		}
		
		list.remove(p);
		updateLabel();
		user.clear();
	}

	public void missedLecture(){
		Person p = this.listviewPersons.getSelectionModel().getSelectedItem();
		if (p == null) {
			setError("Velg en person først");
		} else {
			p.addDots(2);
			updateLabel();
		}
	}

	public void broughtCandy() {
		Person p = this.listviewPersons.getSelectionModel().getSelectedItem();
		if (p == null) {
			setError("Velg en person først");
		} else {
			p.addDots(-1);
			updateLabel();
		}
	}

	public void broughtCake() {
		Person p = this.listviewPersons.getSelectionModel().getSelectedItem();
		if (p == null) {
			setError("Velg en person først");
		} else {
			p.addDots(-4);
			updateLabel();
		}
	}

	public void unprepared() {
		Person p = this.listviewPersons.getSelectionModel().getSelectedItem();
		if (p == null) {
			setError("Velg en person først");
		} else {
			p.addDots(1);
			updateLabel();
		}
	} 

	public void late() {
		Person p = this.listviewPersons.getSelectionModel().getSelectedItem();
		if (p == null) {
			setError("Velg en person først");
		} else {
			if (late.getText().isEmpty()) {
				setError("Skriv inn antall minutter forsein");
			} else {
				int minutes = Integer.parseInt(late.getText());
				if (minutes >= 30) {
					p.addDots(6);
					updateLabel();
					late.clear();
				} else if (minutes < 30){
					p.addDots(Math.floorDiv(minutes,5));
					updateLabel();
					late.clear();
				} 
			}

		}
	}

	public void updateLabel() {
		error.setText("");
		Person p = this.listviewPersons.getSelectionModel().getSelectedItem();
		if (p == null) {
			dots.setText("");
		} else {
			dots.setText(dotsReadable(p));
		}
	}

	public String dotsReadable(Person p) {
		return p.getName() + " har " + p.getDots() + " prikker.";
	}

	public void setError(String description) throws IllegalArgumentException{
		error.setText(description);
		// TODO: Shouldn't throw RuntimeException, or uncaught exception
		throw new IllegalArgumentException(description);
	}

	public void load() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Dot files", "*.dots"));

		File file = chooser.showOpenDialog(null);
		if (file != null) {
			try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
				List<Person> importedPersons = new ArrayList<>();
				Object readObj = in.readObject();
				
				// Validate instance type of read object
				if (!(readObj instanceof List<?>)) {
					throw new Exception("Unrecognizeable object in savefile, expected List<?> but was "
							+ readObj.getClass().getSimpleName());
				}
				
				// Add imported persons, validate types
				List<?> readList = (List<?>) readObj;
				for (Object o : readList) {
					if (!(o instanceof Person)) {
						System.err.println("Unknown object in imported list, expected Person but was "
								+ o.getClass().getSimpleName());
						continue;
					}
					Person p = (Person) o;
					importedPersons.add(p);
				}
				
				list.setAll(importedPersons);
				listviewPersons.getSelectionModel().select(0);
				System.out.println("Loaded: "+list);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}

	public void save() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Dot files", "*.dots"));
		File file = chooser.showSaveDialog(listviewPersons.getScene().getWindow());
		if (file == null) return;
		String filePath = file.getAbsolutePath();
		if(!filePath.endsWith(".dots")) {
			file = new File(filePath + ".dots");
		}
		if (file != null) {
			try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
				System.out.println("Saved: "+list); 
				out.writeObject(new ArrayList<>(list));
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}

}
