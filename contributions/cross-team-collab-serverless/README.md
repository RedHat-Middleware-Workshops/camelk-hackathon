Install OpenShift Serverless, Knative-serving, knative-eventing, knative-kafka
TODO: add details about knative-kafka

Run through the regular demo

Goal is to introduce eventing to the scenario. A new consumer/integration that is event-driven/reactive can be spun up on-demand. In this example, Stage 2, which processes messages from the `questions` topic will be modified. A `KafkaSource` will trigger the integration on-demand

examples
KafkaSource->Stage2 Sink
KafkaSource->Knative Channel->Stage2 Subscriber