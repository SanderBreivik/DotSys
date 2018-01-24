package dotsystem;

import java.io.Serializable;

public class Person implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dots;
	private String name;

	public Person(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDots() {
		return dots;
	}

	public void addDots(int dots) {
		this.dots += dots;
	}	
	
	public String toString() {
		return name;
	}
}

	