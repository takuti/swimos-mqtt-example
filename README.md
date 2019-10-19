Swim.ai MQTT Example
====================

## Server

```sh
cd server
```

### Broker 

Edit [config.properties](./server/src/main/resources/config.properties) for your broker.

#### Local

```sh
brew install mosquitto
```

```sh
mosquitto
```

`tcp://localhost:1883` by default:

```
1570610668: New connection from 127.0.0.1 on port 1883.
1570610668: New client connected from 127.0.0.1 as Writer (p2, c1, k60).
1570610688: New connection from 127.0.0.1 on port 1883.
1570610688: New client connected from 127.0.0.1 as Listener (p2, c1, k60).
```

#### SSL

`mqtt.broker` config must start with `ssl://`.

Assume you have a client certificate `mqtt.clientcert` and RSA private key file `mqtt.privatekey` having the specific heading and tailing line as follows:

```
-----BEGIN CERTIFICATE-----
-----END CERTIFICATE-----
```

```
-----BEGIN RSA PRIVATE KEY-----
-----END RSA PRIVATE KEY-----
```

Before connecting to the broker, retrieve server certificate and add it to your local keystore e.g., by using [escline/InstallCert](https://github.com/escline/InstallCert):

```sh
javac InstallCert.java

java InstallCert [host]:[port]

# Extract certificate from created jssecacerts keystore
keytool -exportcert -alias [host]-1 -keystore jssecacerts -storepass changeit -file [host].cer

# Import certificate into system keystore
keytool -importcert -alias [host] -keystore [path to system keystore] -storepass changeit -file [host].cer
```

### Swim

```sh
./gradlew run
```

```
Running Basic server...
/unit/2: history update: <1570610688731, FromPopulator2>
/unit/3: history update: <1570610698539, FromPopulator3>
/unit/4: history update: <1570610708542, FromPopulator4>
/unit/5: history update: <1570610718552, FromPopulator5>
/unit/6: history update: <1570610728558, FromPopulator6>
```

### Pub

```sh
./gradlew runMqtt
```

```
Populator connecting to tcp://localhost:1883
Populator connected!
```

### Sub

```sh
./gradlew runBridge
```

```
MQTT Ingress Bridge received '@msg{id:2,val:FromPopulator2}'
MQTT Ingress Bridge received '@msg{id:3,val:FromPopulator3}'
MQTT Ingress Bridge received '@msg{id:4,val:FromPopulator4}'
MQTT Ingress Bridge received '@msg{id:5,val:FromPopulator5}'
MQTT Ingress Bridge received '@msg{id:6,val:FromPopulator6}'
```

## UI

http://localhost:9001/
