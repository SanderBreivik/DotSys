package dotsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DotSystemController {
	
	String username; 
	Person person = null;
	
	
	@FXML
	ComboBox<Person> comboBox;
		
	
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
    private Button add;

    @FXML
    private TextField user;

    @FXML
    private Label dots;
    
    @FXML
    private Label error;
    
   
    @FXML
    void addPerson() {
    		username = user.getText().substring(0, 1).toUpperCase() + user.getText().substring(1).toLowerCase();
    		Person p = new Person(username);
    		setPerson(p);
    		System.out.println("Person:" + getPerson());
    		System.out.println();
    		list.addAll(getPerson());
    		comboBox.setItems(list.sorted());
    		updateLabel();
    		user.clear();
    }
    
    
    public Person getPerson() {
		return person;
	}
    
    void setPerson(Person person) {
    		this.person = person;
    }
    
    public String dots(Person p) {
		return p.getName() + " har " + p.getDots() + " prikker.";
	}
   
    public void checkPerson() {
    		setPerson(comboBox.getValue());
    		updateLabel();
    }
    
    public Boolean isValidPerson(Person p) {
    		if (p == null) {
    			return false;
    		} else {
    			return true;
    		}
    }
    public void missedLecture(){
    		if (!isValidPerson(person)) {
    			setError("Velg en person først");
        } else {
        		person.addDots(2);
    			updateLabel();
        }
    		
		
	}
	
	public void broughtCandy() {
		if (!isValidPerson(person)) {
			setError("Velg en person først");
    } else {
		person.addDots(-1);
		updateLabel();
    }
	}
	
	public void broughtCake() {
		if (!isValidPerson(person)) {
			setError("Velg en person først");
    } else {
		person.addDots(-4);
		updateLabel();
    		}
	}
	
	public void unprepared() {
		if (!isValidPerson(person)) {
			setError("Velg en person først");
    } else {
		person.addDots(1);
		updateLabel();
		}
	} 
	
	public void late() {
		if (!isValidPerson(person)) {
			setError("Velg en person først");
    } else {
    		if (late.getText().isEmpty()) {
    			setError("Skriv inn antall minutter forsein");
    		} else {
    			int minutes = Integer.parseInt(late.getText());
    			if (minutes >= 30) {
    				person.addDots(6);
    				updateLabel();
    				late.clear();
    			} else if (minutes < 30){
    				person.addDots(Math.floorDiv(minutes,5));
    				updateLabel();
    				late.clear();
    			} 
    		}
		
	}
	}
	
	public void updateLabel() {
		error.setText("");
		dots.setText(dots(getPerson()));
	}
	
	public void setError(String description) throws IllegalArgumentException{
		error.setText(description);
		throw new IllegalArgumentException(description);
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		FileChooser chooser = new FileChooser();
	    chooser.getExtensionFilters().add(new ExtensionFilter("Dot files", "*.dots"));
	    
		File file = chooser.showOpenDialog(null);
        if (file != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                List<Person> persons = (List<Person>) in.readObject();
                list.setAll(persons);
                comboBox.setItems(list.sorted());
                System.out.println("Loaded: "+list);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
	}
	
	public void save() {
		FileChooser chooser = new FileChooser();
		File file = chooser.showSaveDialog(null);
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