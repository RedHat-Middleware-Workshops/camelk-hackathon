
## Running the stub service

Run it executing the command below:

```
./run.sh
```

The Camel K operator will create a route to access the service.

You can discover the *OpenApi* service specification with the following `curl` command:

```
curl http://`oc get route end1 -o jsonpath='{..spec.host}'`/camel/openapi.json
```

You can send a `POST` request with the following `curl` command:

>**Note**: it's a dummy stub and the payload to send can be empty

```
curl \
-H "content-type: application/xml" \
-d '' \
http://`oc get route end1 -o jsonpath='{..spec.host}'`/camel/subscriber/details
```

Alternatively, execute the provided script that includes the `curl` command above:

```
./call.sh
```

