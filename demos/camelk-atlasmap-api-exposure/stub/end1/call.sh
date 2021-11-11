curl \
-H "content-type: application/xml" \
-d '' \
http://`oc get route end1 -o jsonpath='{..spec.host}'`/camel/subscriber/details
