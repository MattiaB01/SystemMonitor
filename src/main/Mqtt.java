package main;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Mqtt {
	private static final Logger log = LoggerFactory.getLogger(Mqtt.class);

	private final static String broker = "tcp://127.0.0.1:1883";
	private final static String clientId = "JavaApp";
	private static MqttClient client;
	private static MqttConnectOptions options;

	public static void config() throws MqttException {
		log.info("Start Mqtt configuration...");
		try {
		client = new MqttClient(broker, clientId);
		options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setConnectionTimeout(5);
		options.setKeepAliveInterval(10);
		client.connect(options);
		log.info("Connected");}
		catch (Exception e ) {
			log.info("Not connected");
		}
	}

	public static void stop() throws MqttException {
		client.disconnect();
		client.close();
		log.info("Connection closed");

	}

	public static void sendMqtt(Cpu_Model model) {
		try {

			// String clientId = "JavaTestClient";
			String topicCpu = "system/cpu";
			String topicRam = "system/ram";

			String topicDisk = "system/disk";
			String payloadCpu = String.valueOf(model.getCpu());
			String payloadRam = String.valueOf(model.getRam());
			String payloadDisk = String.valueOf(model.getSpace());

			MqttMessage messageCpu = new MqttMessage(payloadCpu.getBytes());
			messageCpu.setQos(0);
			messageCpu.setRetained(false);

			MqttMessage messageRam = new MqttMessage(payloadRam.getBytes());
			messageRam.setQos(0);
			messageRam.setRetained(false);

			MqttMessage messageDisk = new MqttMessage(payloadDisk.getBytes());
			messageDisk.setQos(0);
			messageDisk.setRetained(false);

			client.publish(topicDisk, messageDisk);
			client.publish(topicRam, messageRam);
			client.publish(topicCpu, messageCpu);

			//System.out.println("Messaggio inviato correttamente");
			log.info("Messaggio inviato correttamente");
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
