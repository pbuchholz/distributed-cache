package de.bu.eda.kafka;

import java.io.StringWriter;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import de.bu.eda.kafka.config.Config;
import de.bu.eda.kafka.config.KafkaProperties;
import de.bu.eda.kafka.model.PersonEvent;

@Dependent
public class PersonEventEmitter {

	@Inject
	@KafkaProperties
	private Map<String, Object> kafkaProperties;

	@Config(key = "")
	private String personEventsTopicName;

	@Inject
	private JAXBContext jaxbContext;

	/**
	 * Sends an update to the person update topic in Kafka.
	 * 
	 * @param personEvent
	 * @throws JAXBException
	 */
	public void emitPersonEvent(PersonEvent<?> personEvent) throws JAXBException {
		try (Producer<String, String> kafkaProducer = new KafkaProducer<>(this.kafkaProperties)) {
			ProducerRecord<String, String> producerRecord = new ProducerRecord<>(personEventsTopicName,
					personEvent.getPersonIdentifier().toString(), this.marshallPersonEvent(personEvent));
			kafkaProducer.send(producerRecord);
		}
	}

	private String marshallPersonEvent(PersonEvent<?> personEvent) throws JAXBException {
		Marshaller marshaller = this.jaxbContext.createMarshaller();
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(personEvent, stringWriter);
		return stringWriter.getBuffer().toString();
	}

}