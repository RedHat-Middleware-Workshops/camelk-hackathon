
## Running the stub service

Run it executing the command below:

```
mvn -Dspring-boot.run.profiles=dev
```

You can discover the *OpenApi* service specification with the following `curl` command:

```
curl http://localhost:9000/camel/api-docs
```

You can send a `POST` request with the following `curl` command:

>**Note**: it's a dummy stub and the payload to send can be empty

```
curl \
-H "content-type: application/xml" \
-d '' \
http://localhost:9000/camel/subscriber/details
```

## Deploying on Openshift

Ensure you create/switch-to the namespace where you want to deploy the stub.

> **Note:** instructions are based on a Fuse 7.9 JDK 11 version.

If the corresponding Fuse image is not loaded in your environment, run the following command:
```
oc import-image fuse-java-openshift-jdk11-rhel8:1.9 \
--from=registry.redhat.io/fuse7/fuse-java-openshift-jdk11-rhel8:1.9 \
--reference-policy='local' \
-n openshift
```

Run the following command to trigger the deployment:
```
mvn oc:deploy -Popenshift
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
```

