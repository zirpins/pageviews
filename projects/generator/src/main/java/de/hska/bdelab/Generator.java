package de.hska.bdelab;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Generator {
	private static final Logger LOGGER = Logger.getLogger(Generator.class.getName());

	final String[] urls = { "index", "start", "stream", "batch", "assets", "live", "timings", "requests", "resources",
			"exit", "readme", "modules", "machines", "overview", "help" };

	public void Run() {
		LOGGER.info("generator startup.");

		Properties props = new Properties();
		props.put("bootstrap.servers", "10.10.33.22:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<String, String>(props);

		try {
			for (;;) {
				// send(topic, key, value)
				producer.send(new ProducerRecord<String, String>("output", "pageviews", NewMessage()));
				try {
					Thread.sleep(ThreadLocalRandom.current().nextInt(10, 2000));
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			LOGGER.warning(ex.getMessage());
		} finally {
			producer.close();
		}
		LOGGER.info("generator shutdown.");
	}

	private String NewMessage() {
		return (GenerateTimestamp() + "," + GenerateUri() + "," + GenerateIp() + "," + GenerateUid());
	}

	// rnd element generators
	private String GenerateTimestamp() {
		return (new Date()).toString();
	}

	private String GenerateIp() {
		return (rndDecByte() + "." + rndDecByte() + "." + rndDecByte() + "." + rndDecByte());
	}

	private String GenerateUri() {
		return ("http://bdelab.hska.de/" + urls[ThreadLocalRandom.current().nextInt(0, urls.length)]);
	}

	private String GenerateUid() {
		return (UUID.randomUUID().toString());
	}

	// helpers
	private String rndDecByte() {
		return String.valueOf(ThreadLocalRandom.current().nextInt(0, 256));
	}
	
	
	
	/*
	private HashMap<String, Integer> _counts = new HashMap<String, Integer>();
	
	private void debugCounter(String url) {
		Integer count = _counts.get(url);
		if (count == null) { count = 0; }
		count++;
		_counts.put(url, count);
		System.out.println("out: " + url + "  -  "+ count);
	}
	*/

}
