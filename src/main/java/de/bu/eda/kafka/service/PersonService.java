package de.bu.eda.kafka.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import de.bu.eda.kafka.PersonEventEmitter;
import de.bu.eda.kafka.model.Contract;
import de.bu.eda.kafka.model.Person;
import de.bu.eda.kafka.model.PersonEvent;

@Path("/persons")
public class PersonService {

	@Inject
	private PersonEventEmitter personEventEmitter;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> readPersons() {
		List<Person> persons = new ArrayList<>();

		persons.add(new Person("Lars", "Lustig", 80,
				Arrays.asList(new Contract(7798L), new Contract(7799L), new Contract(7780L))));
		return persons;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void updatePerson(Person person) throws JAXBException {
		PersonEvent<String> personUpdate = new PersonEvent<String>();
		personUpdate.setFieldName("firstName");
		personUpdate.setOldValue("Lars");
		personUpdate.setNewValue("Helmfried");
		this.personEventEmitter.emitPersonEvent(personUpdate);

	}

}
