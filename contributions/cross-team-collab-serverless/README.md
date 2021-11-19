# Pre-req
Install OpenShift Serverless, Knative-serving, knative-eventing, knative-kafka

*Note*: Knative-kafka needs to be backed by a kafka broker. The cluster for the demo can be used as the bootstrap: my-cluster-kafka-bootstrap.demo-camelk.svc:9092

# Demo

Run through the regular demo

Goal is to introduce eventing to the scenario. A new consumer/integration that is event-driven/reactive can be spun up on-demand. In this example, Stage 2, which processes messages from the `questions` topic will be modified. A `KafkaSource` will trigger the integration on-demand

## Scenarios:
see scripts/stage2-knative.sh for instructons

**KafkaSource->Stage2 Sink**
In this example, you have one integration to spin up on demand when a message is posted to the Kafka Topic. The `KafkaSource` sink is configured as the Knative Service, which in this case is the modified version of the Stage2 integration.

**KafkaSource->Knative Channel->Stage2 Subscriber**
There might be a case where multiple serverless integrations are required based on the event on the Kafka Topic. Each KafkaSource has a unique consumer group so to avoid multiple `KafkaSources`, a knative-eventing `Channel` can be utilized. Multiple Knative Services can then **subscribe** to the Channel. If there needs to be any filtering and routing of events knative-eventing `Brokers` and `Triggers` should be used.


## Other considerations
Using Knative channel component to read from the channel

https://www.nicolaferraro.me/2018/12/10/camel-k-on-knative/

https://goois.net/6-serverless-integration-patterns-using-apache-camel-k-knative-cookbook.html 


## Other notes
Couple of concerns
- what if you wanted to read from the earilest message? Can KafkaSource be configured to do that?


Some issues/observations
- when knative is enabled, when kameletbinding is deployed, the corresponding camel k integration isn't rendeed
