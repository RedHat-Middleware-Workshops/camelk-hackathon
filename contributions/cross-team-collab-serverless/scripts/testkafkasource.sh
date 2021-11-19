 # generate test payloads to the questions topic
 oc run kafka-producer \
    -ti --image=quay.io/strimzi/kafka:latest-kafka-2.7.0 --rm=true \
    --restart=Never -- bin/kafka-console-producer.sh \
    --broker-list my-cluster-kafka-bootstrap.demo-camelk.svc:9092 --topic questions

    # example payload
    # ["5","After our recent aq?","Architecture"]


#event display - in case your kafka source is sinking to the event display
kn service create event-display --image quay.io/openshift-knative/knative-eventing-sources-event-display

#event display log
oc logs $(oc get pod -o name | grep event-display) -c user-container