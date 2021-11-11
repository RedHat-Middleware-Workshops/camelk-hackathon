curl \
-H "content-type: application/json" \
-d '{"id":"123"}' \
http://`oc get route api-layer -o jsonpath='{..spec.host}'`/camel/subscriber/details
