
## Dependency
This service requires a backend up and running. You can find it under the folder:
 - camelk/stubs/end1

Follow the instructions to run the stub service.

</br>

## Running the service

Run it executing the command below:

```
./run.sh
```

The Camel K operator will create a route to access the service.

You can discover the *OpenApi* service specification with the following `curl` command:

```
curl http://`oc get route api-layer -o jsonpath='{..spec.host}'`/camel/openapi.json
```

You can send a `POST` request with the following `curl` command:

```
curl \
-H "content-type: application/json" \
-d '{"id":"123"}' \
http://`oc get route api-layer -o jsonpath='{..spec.host}'`/camel/subscriber/details
```

Alternatively, execute the provided script that includes the `curl` command above:

```
./call.sh
```
