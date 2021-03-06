
#if knative serving is installed, the knative *should* be the default deployment controler. --trait knative.enabled=true can also be included
kamel run --name stage2-knative  camelk/stage-2-kafka2mail-knative.xml -d camel-jackson


#KafkaSource->Service Sink
#apply kafkaSouce
oc apply -f  knative/question-kafka-source-service-sink.yaml

##OR 

#KafkaSource->Channel->Service 
#apply Channel (optional)
oc apply -f  knative/question-kafka-source-service-sink.yaml

oc apply -f knativ/question-kafka-channel.yaml
oc apply -f knativ/question-stage2-subscription.yaml


#add subscription if channel was used (can also make the association via the dev console - by moving the "sink" )


#test via rest
curl -X POST -H "content-type: application/json" -d '[ "5", "After our recent aq?", "Architecture" ]'  http://stage2-knative-demo-camelk.apps.cluster-0f5d.0f5d.sandbox1327.opentlc.com/