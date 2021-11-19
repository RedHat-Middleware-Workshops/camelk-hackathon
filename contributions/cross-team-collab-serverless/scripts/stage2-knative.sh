
#if knative serving is installed, the knative *should* be the default deployment controler. --trait knative.enabled=true can also be included
kamel run --name stage2-knative  camelk/stage-2-kafka2mail-knative.xml -d camel-jackson


#apply Channel (optional)

#apply kafkaSouce

#add subscription if channel was used (can also make the association via the dev console - by moving the "sink" )


#test via rest
curl -X POST -H "content-type: application/json" -d '[ "5", "After our recent aq?", "Architecture" ]'  http://stage2-knative-demo-camelk.apps.cluster-0f5d.0f5d.sandbox1327.opentlc.com/