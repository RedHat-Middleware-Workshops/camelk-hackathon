
## Dependency
This service requires a backend up and running. You can find it under the folder:
 - camelk/stubs/end1

Follow the instructions to run the stub service.

</br>

## Running the service

Run in `dev` mode with:

```
./dev.sh
```

The Camel K operator will create a route to access the service.

You can discover the *OpenApi* service specification with the following `curl` command:

```
curl http://ENVIRONMENT_URL/camel/openapi.json
```

You can send a `POST` request with the following `curl` command:

```
curl \
-H "content-type: application/json" \
-d '{"id":"123"}' \
http://ENVIRONMENT_URL/camel/subscriber/details
```

