package gr.upatras.ergasia3;

import java.util.List;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.*;

@RestController
@SpringBootApplication
public class SimpleMqttClient implements MqttCallback {
    MqttClient myClient;
    MqttConnectOptions connOpt;
    // IMqttClient publisher = new MqttClient("tcp://iot.eclipse.org:1883",publisherId);
    static final String M2MIO_THING = UUID.randomUUID().toString();
    static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    // static final String M2MIO_DOMAIN = "<Insert m2m.io domain here>";
    // static final String M2MIO_STUFF = "things";
    // static final String M2MIO_USERNAME = "<m2m.io username>";
    // static final String M2MIO_PASSWORD_MD5 = "<m2m.io password (MD5 sum of password)>";
    // the following two flags control whether this example is a publisher, a
    // subscriber or both
    static final Boolean subscriber = true;
    static final Boolean publisher = true;
    private static final Logger log = LoggerFactory.getLogger(SimpleMqttClient.class);
    public static final String TOPIC = "grupatras/ergasia3";
    /**
    *
    * connectionLost This callback is invoked upon losing the MQTT connection.
    *
    */
    public void connectionLost(Throwable t) {
        log.info("Connection lost!");
        // code to reconnect to the broker would go here if desired
    }
    /**
    *
    * deliveryComplete This callback is invoked when a message published by this
    * client is successfully received by the broker.
    *
    */
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    /**
    *
    * messageArrived This callback is invoked when a message is received on a
    * subscribed topic.
    *
    */
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.info("\n");
        log.info("-------------------------------------------------");
        log.info("| Topic:" + topic);
        log.info("| Message: " + new String(message.getPayload()));
        log.info("-------------------------------------------------");
        log.info("\n");
    }
    /**
    *
    * MAIN
    *
    */
    public static void main(String[] args) {
    	SpringApplication.run(SimpleMqttClient.class, args);
    	
    }
    /**
    *
    * runClient The main functionality of this simple example. Create a MQTT
    * client, connect to broker, pub/sub, disconnect.
    *
    */
    
    @Autowired
    private ITextService TextService;
    @ApiOperation(value = "Retrieves all Texts", notes = "This operation retrieves all Text entities. ", response = Text.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = Text.class),
    @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
    @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
    @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
    @ApiResponse(code = 404, message = "Not Found", response = Error.class),
    @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
    @ApiResponse(code = 409, message = "Conflict", response = Error.class),
    @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/Text/" , produces = { "application/json;charset=utf-8" }, method = RequestMethod.GET)


    public List<Text> getText() {
        // finds all the Texts
        List<Text> Texts = TextService.findAll();
        // returns the Text list
        return Texts;
    }
    
    @ApiOperation(value = "Creates a Text", notes = "This operation creates a Text entity.", response = Text.class)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Created", response = Text.class),
    @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
    @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
    @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
    @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
    @ApiResponse(code = 409, message = "Conflict", response = Error.class),
    @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/Text", produces = { "application/json;charset=utf-8" }, consumes = { "application/json;charset=utf-8" }, method = RequestMethod.POST)

    public ResponseEntity<Text> createText(@ApiParam(value = "The Text to be created", required = true) @RequestBody Text p) {
        log.info( "Will add a new Text" );
        Text Text = TextService.addText(p);
        // setup MQTT Client
        String clientID = M2MIO_THING;
        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
        // connOpt.setUserName(M2MIO_USERNAME);
        // connOpt.setPassword(M2MIO_PASSWORD_MD5.toCharArray());
        // Connect to Broker
        try {
            myClient = new MqttClient(BROKER_URL, clientID);
            myClient.setCallback(this);
            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        log.info("Connected to " + BROKER_URL);
        String myTopic = TOPIC;
        MqttTopic topic = myClient.getTopic(myTopic);
        // subscribe to topic if subscriber

        if (subscriber) {
            try {
                int subQoS = 0;
                myClient.subscribe(myTopic, subQoS);
                if (!publisher) {
                    while (true) {
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // publish messages if publisher
        if (publisher) {
                String val = p.getText();
                String pubMsg = "{\"text\":" + val + "}";
                int pubQoS = 0;
                MqttMessage message = new MqttMessage(pubMsg.getBytes());
                message.setQos(pubQoS);
                message.setRetained(false);
                // Publish the message
                log.info("Publishing to topic \"" + topic + "\" qos " + pubQoS + "\" text " + val);
                MqttDeliveryToken token = null;

                try {
                    // publish message to broker
                    token = topic.publish(message);
                    // Wait until the message has been delivered to the broker
                    token.waitForCompletion();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            
        }
        // disconnect
        try {
            // wait to ensure subscribed messages are delivered
            if (subscriber) {
                Thread.sleep(5000);
            }
            myClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Text>( Text, HttpStatus.OK);
    }
}