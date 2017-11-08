package de.bu.eda.kafka.model;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an update of a {@link Person}.
 * 
 * @author Philipp Buchholz
 */
@XmlRootElement(name = "person-update")
public class PersonEvent<T> {
	private UUID eventIdentifier;
	private UUID personIdentifier;
	private String fieldName;
	private T oldValue;
	private T newValue;

	public UUID getEventIdentifier() {
		return this.eventIdentifier;
	}

	public void setEventIdentifier(UUID eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}

	public UUID getPersonIdentifier() {
		return this.personIdentifier;
	}

	public void setPersonIdentifier(UUID personIdentifier) {
		this.personIdentifier = personIdentifier;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public T getNewValue() {
		return newValue;
	}

	public void setNewValue(T newValue) {
		this.newValue = newValue;
	}

	public T getOldValue() {
		return oldValue;
	}

	public void setOldValue(T oldValue) {
		this.oldValue = oldValue;
	}

}