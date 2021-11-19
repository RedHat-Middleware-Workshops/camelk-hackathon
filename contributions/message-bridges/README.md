# AMQ 7 Message Bridge

This examples creates a message bridge between two AMQ 7 Brokers using Kamelet Binding.

## Prerequisites
- AMQ Broker Operator
- Camel K Operator
- Kamel binary on PATH
- Access to OCP 4 cluster

## Setup

### AMQ Brokers
We need to start by deploying 2 (source and targe(or sink)) brokers
   - You can use `source-broker.yaml` and `sink-broker.yaml` which are custom resources to be used with the AMQ Broker operator
   - You need to adjust `namespace`, `adminPassword`, `adminUser` properties
   - Once done, simply execute `oc apply -f [source|sink]-broker.yaml`

### Sensitive data
We will be using Secret to store sensitive data - namely AMQ brokers admin credentials. There is a `create-secrets.sh` script ready which you need to configure with the admin user and admin password values you have used in the previous step
Once done, simply execute the script via `./create-secrets.sh`

### Producer and Consumer
To successfully test message bridge, we need to deploy producer and consumers so we can generate and consume some traffic.
The producer is located in `AMQSourceProducer.java` and producer in `AMQTargetConsumer.java`
No changes should be needed in these two files. If you did some customization in the brokers deployment (such as different broker name) you might need to adjust `source.properties` or `target.properties` accordingly.

You can deploy these two simple integrations via:
```bash
kamel run AMQSourceProducer.java --config secret:message-bridge-binding --config file:source.properties
kamel run AMQTargetConsumer.java --config secret:message-bridge-binding --config file:target.properties
```

When you drill down to the amq-source-producer pods, you should already see some traffic being generated:
```
2021-11-19 13:24:49,851 INFO  [info] (Camel (camel-1) thread #0 - timer://foo) Exchange[ExchangePattern: InOnly, BodyType: String, Body: Sammy Sporer lives on 64409 Arnoldo Lights]
2021-11-19 13:24:49,862 INFO  [org.apa.qpi.jms.JmsConnection] (AmqpProvider :(3754):[amqp://source-broker-amqp-acceptor-0-svc:5672]) Connection ID:0d251f1e-7147-4bc4-b957-00d8767a8984:3754 connected to server: amqp://source-broker-amqp-acceptor-0-svc:5672
2021-11-19 13:24:54,852 INFO  [info] (Camel (camel-1) thread #0 - timer://foo) Exchange[ExchangePattern: InOnly, BodyType: String, Body: Damien Senger lives on 1542 Tremblay Spur]
```

There is no traffic flowing into amq-target-consumer (because we didn't deploy the bridge yet)

### Source/Sink custom kamelets
We decided to deploy custom source/sink amqp kamelets. The only reason was that the original kamelets (i.e. `jms-amqp-10-sink`) didn't expose username/password property. It is of course still possible to pass username/password even using the default kamelet via query parameters (i.e. `jms.userName` and `jms.password`) in your `remoteUri` but wanted to externalize username/password.

Open `[source|sink]-jms-kamelet.yaml` and adjust `namespace` accordingly. Once done you can deploy these custom kamelets simply via:
```bash
oc apply -f source-jms-kamelet.yaml 
oc apply -f sink-jms-kamelet.yaml     
```

### Kamelet binding
Deploying kamelet binding is as simple as `oc apply -f message-bridge-binding.yaml`

You might have to change the `remoteUri` to fit accordingly your configuration.

Broker username and passwords are configured via this section in the binding:

```yaml
spec:
  integration:
    configuration:
     - type: "secret"
       value: "message-bridge-binding"  
```
This will inject the secret into the integration pods and make all the secret properties available. The final trick which complements this part of the setup is the way we have named those secret keys in our `Secret.yaml`:
```yaml
camel.kamelet.jms-amqp-10-source-custom.username: ${SOURCE_USERNAME}
camel.kamelet.jms-amqp-10-source-custom.password: ${SOURCE_PASSWORD}
camel.kamelet.jms-amqp-10-sink-custom.username: ${SINK_USERNAME}
camel.kamelet.jms-amqp-10-sink-custom.password: ${SINK_PASSWORD}
```       
We follow this convention:
`camel.kamelet.<KAMELET_NAME>.<KAMELET_PROPERTY>`

This will substitute the propertie to appropriate kamelet used in the binding.

If all is set properly, you should finally see some traffic in the `amq-target-consumer` integration pod as well:
```
2021-11-19 13:33:54,872 INFO  [info] (Camel (camel-1) thread #0 - JmsConsumer[target-queue]) Exchange[ExchangePattern: InOnly, BodyType: String, Body: Tatiana Rempel lives on 688 Langworth Track]
2021-11-19 13:33:59,867 INFO  [info] (Camel (camel-1) thread #0 - JmsConsumer[target-queue]) Exchange[ExchangePattern: InOnly, BodyType: String, Body: Donny Parisian lives on 011 Howell Fields]
2021-11-19 13:34:04,878 INFO  [info] (Camel (camel-1) thread #0 - JmsConsumer[target-queue]) Exchange[ExchangePattern: InOnly, BodyType: String, Body: Alda Glover lives on 100 Classie Island]
```

This concludes the demo.


