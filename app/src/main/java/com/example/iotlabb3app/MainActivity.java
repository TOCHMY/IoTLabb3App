package com.example.iotlabb3app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private TextView txv_rgb;
    private TextView txv_light;
    private TextView txv_proximity;
    private Button btn_color;
    private MqttAndroidClient client;
    //private static final String SERVER_URI = "test.mosquitto.org";
    private static final String SERVER_URI = "tcp://mqtt.dioty.co:1883";
    private static final String TAG = "MainActivity";

    private void connect(){
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), SERVER_URI,
                        clientId);
        try {
            IMqttToken token = client.connect();

            token.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    txv_light.setText("TOMY");
                    Log.d(TAG, "onSuccess");
                    System.out.println(TAG + " Success. Connected to " + SERVER_URI);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    System.out.println(TAG + " Oh no! Failed to connect to " +
                        SERVER_URI);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(String topicToSubscribe) {
        final String topic = topicToSubscribe;  //topic name, update the connectComplete in onCreate
        //final String topic = "iotlab/tif/sensors";
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Subscription successful to topic: " + topic);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    System.out.println("Failed to subscribe to topic: " + topic);
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("ramin");
        txv_rgb = (TextView) findViewById(R.id.txv_rgbValue);
        txv_light = (TextView) findViewById(R.id.txv_lightValue);
        txv_proximity = (TextView) findViewById(R.id.txv_proximityValue);
        btn_color = (Button) findViewById(R.id.btnColor);

        //btn_color.setOnClickListener();

        connect();
        //txv_proximity.setText("TOMY");

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    System.out.println("Reconnected to : " + serverURI);
                    // Re-subscribe as we lost it due to new session
                    subscribe("iotlab/tommyfelixivan/sensors"); //add name of topic
                } else {

                    System.out.println("Connected to: " + serverURI);
                    subscribe("iotlab/tommyfelixivan/sensors"); //add name of topic
                }
            }
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("The Connection was lost.");
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws
                    Exception {
                String newMessage = new String(message.getPayload());
                System.out.println("Incoming message: " + newMessage);
                System.out.println(client.publish("iotlab/tommyfelixivan/sensors", message));
                client.publish("iotlab/tommyfelixivan/sensors", message);

                /* add code here to interact with elements
                (text views, buttons)
                using data from newMessage
                */
                System.out.println(newMessage);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

    }
}