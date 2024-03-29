// Copyright 2015-2019 SWIM.AI inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package swim.basic.mqtt;

import swim.basic.mqtt.ssl.SSLUtils;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class DataSourcePopulator {

  public final MqttClient mqtt;

  public DataSourcePopulator(String broker) throws MqttException {
    this(broker, null);
  }

  public DataSourcePopulator(String broker, MqttConnectOptions connOpts) throws MqttException {
    this.mqtt = new MqttClient(broker, "Writer");

    if (connOpts == null) {
      connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);
    }

    System.out.println("Populator connecting to " + broker);
    mqtt.connect(connOpts);
    System.out.println("Populator connected!");
  }

  public void populate(final String topic) {
    final int qos = 1;
    int count = 0;
    while (true) {
      for (int i = 0; i < 10; i++) {
        final String payload = new JSONObject()
                .put("id", i)
                .put("val", String.format("FromPopulator%d", count++))
                .toString();
        try {
          mqtt.publish(topic, payload.getBytes(), qos, false);
          // Don't lower this value unless you use a personal MQTT broker, or
          // you will get rate-limited!!
          Thread.sleep(10000);
        } catch (MqttException | InterruptedException e) {
          System.out.println("Publication error");
          e.printStackTrace();
          return;
        }
      }
    }
  }

  public static void main(String[] args) throws MqttException, GeneralSecurityException, IOException {
    InputStream in = DataSourcePopulator.class.getClassLoader().getResourceAsStream("config.properties");
    Properties prop = new Properties();
    prop.load(in);
    String broker = prop.getProperty("mqtt.broker");
    final DataSourcePopulator pop;
    if (broker.startsWith("ssl://")) {
      String username = prop.getProperty("mqtt.username");
      String password = prop.getProperty("mqtt.password");
      String clientCert = prop.getProperty("mqtt.clientcert");
      String privateKey = prop.getProperty("mqtt.privatekey");
      pop = new DataSourcePopulator(broker, SSLUtils.loadMqttConnectOptions(username, password, clientCert, privateKey));
    } else {
      pop = new DataSourcePopulator(broker);
    }
    pop.populate(prop.getProperty("mqtt.topic"));
  }
}
