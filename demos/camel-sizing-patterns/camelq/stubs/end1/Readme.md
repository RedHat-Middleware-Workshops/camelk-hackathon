
## Running the stub service

Run it locally executing the command below:

```
mvn clean compile quarkus:dev -Ddebug=6006
```
**Note:** the `-Ddebug=6006` is to prevent a debug port collision with the main service pointing to this stub (defaults to 5005)

You can discover the *OpenApi* service specification with the following `curl` command:

```
curl http://localhost:9000/camel/openapi.json
```

You can send a `POST` request with the following `curl` command:

>**Note**: it's a dummy stub and the payload to send can be empty

```
curl \
-H "content-type: application/xml" \
-d '' \
http://localhost:9000/camel/subscriber/details
```

## Deploying to Openshift

Ensure you create/switch-to the namespace where you want to deploy the stub.

Run the following command to trigger the deployment:
```
mvn clean package -DskipTests -Dquarkus.kubernetes.deploy=true
```

To test the stub once deployed, open a tunnel with the following command:
```
oc port-forward service/end1 8080
```
>**Note**: the stub will run on port 8080 when deployed in OCP

You can discover the *OpenApi* service specification with the following `curl` command:

```
curl http://localhost:8080/camel/openapi.json
```

You can send a `POST` request with the following `curl` command:

>**Note**: it's a dummy stub and the payload to send can be empty

```
curl \
-H "content-type: application/xml" \
-d '' \
http://localhost:8080/camel/subscriber/details
`


