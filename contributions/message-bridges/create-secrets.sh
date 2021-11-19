oc process -f Secret.yaml \
-p RESOURCE_NAME=message-bridge-binding \
-p SOURCE_USERNAME=admin \
-p SOURCE_PASSWORD=password1! \
-p SINK_USERNAME=admin \
-p SINK_PASSWORD=password1! \
| oc apply -f -
