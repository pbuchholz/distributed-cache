package de.bu.eda.kafka.model;

import java.util.ArrayList;
import java.util.List;

public class Person {

	private String firstName;
	private String lastName;
	private int age;
	private List<Contract> contracted;

	public Person() {

	}

	public Person(String firstName, String lastName, int age, List<Contract> contracted) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.contracted = contracted;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getAge() {
		return this.age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public List<Contract> getContracted() {
		return this.contracted;
	}

	public void addContract(Contract contract) {
		if (null == this.contracted) {
			this.contracted = new ArrayList<>();
		}

		this.contracted.add(contract);
	}

}
