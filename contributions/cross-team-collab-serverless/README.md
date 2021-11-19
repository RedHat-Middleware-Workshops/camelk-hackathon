Install OpenShift Serverless, Knative-serving, knative-eventing, knative-kafka
TODO: add details about knative-kafka

Run through the regular demo

Goal is to introduce eventing to the scenario. A new consumer/integration that is event-driven/reactive can be spun up on-demand. In this example, Stage 2, which processes messages from the `questions` topic will be modified. A `KafkaSource` will trigger the integration on-demand
*Note 

examples

KafkaSource->Stage2 Sink

KafkaSource->Knative Channel->Stage2 Subscriber (with a channel you can have multiple serverless event-driven-integrations)


Couple of concerns
- what if you wanted to read from the earilest message? Can KafkaSource (and maybe channel) be configured to do that?


Some issues/observations
- when knative is enabled, when kameletbinding is deployed, the corresponding camel k integration isn't rendeed
