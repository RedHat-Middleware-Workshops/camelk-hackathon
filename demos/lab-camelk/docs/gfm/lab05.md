# Put the End-to-End Flow together

In this final stint we're aiming to put all the pieces together and test the end-to-end flow of the platform.

First we need to do a final dive into the architecture to understand how the _Content-Server_ operates. One of the benefits we're trying to exploit is the dynamic nature of **Camel-K** and make the platform spin/stop instances as needed according to the demand. Also, we're using **AMQ-Streams** as a strategic decoupling layer so that the _Content-Server_ placed at the front to serve consumers stays abstracted from backend integrations to on-board new providers.

The diagram below describes the flow:

![](https://gitlab.com/BrunoNetId/lab-camelk/raw/master/docs/images/lab05/end-to-end-streams.png)

As pictured above, the _Content Server_ is permanently subscribed to the Kafka Topic listening for new events. At any given moment, the system may have multiple consumers subscribed to a data stream, for which the _Content Server_ needs to perform content based routing to properly direct the stream to the right consumer.

Here we propose few questions to give some consideration to other ideas on how to go forward:

- Can you think on other ways to approach this problem?

- Can you think on how to coordinate the different components to fulfil this technical requirement?

- Can you think on how to implement the content based routing itself?

We would encourage you to spend some minutes thinking on the questions above.


<br />

## Deploy the _Content Server_


1. Switch to the *ContentServer* Fuse project folder:

       cd lab-camelk/contentserver

1. Edit the following configuration file:

    - src/main/resources/application.properties

    Update the following entry:

    ```properties
    kafka.host=my-cluster-kafka-brokers.lab-{user}-kafka.svc
    ```

    where `{user}` is your chosen username.

1. Save the configuration changes.

1. Run the following Maven command to deploy the *ContentServer*:

   From Fuse 7.9

       mvn oc:deploy -Popenshift

<br />

## Stream routing.

To make things easy, the solution chosen in this Lab has been a simple one. A single Kafka Topic is used to collect all the data streams integrated by Camel-K.

However the _Content Server_ still needs to perform content based routing, for which it needs a key piece of information:

 - the user-id

Remember the Camel-K integration we've built is spawned by the _Service Registry_ for a specific user, and for a specific content.

The Content Server therefore needs to identify the `user-id` when collecting events from the same shared Kafka Topic, and at the same time handle the payload data (the stream).

![](https://gitlab.com/BrunoNetId/lab-camelk/raw/master/docs/images/lab05/content-based-router.png)

Ideally, the key metadata would be passed as headers/properties, as you would generally do with messaging technologies, however Kafka did not originally provide an easy interface to work with, you needed to use serialisers/deserialisers. New Kafka versions should allow you to do it, but Camel's Kafka Component will not understand it.

We've then opted to simply wrap the payload with a JSON envelope so that we can include two extra parameters, and it would look as follows:

~~~json
{
    "id":"CLIENT-ID",
    "last":"TRUE/FALSE",
    "payload":"STREAM-DATA"
}
~~~

Where `CLIENT-ID` represents the user consuming the stream, `last` indicates if it's the last event of the data stream, and `payload` carries the stream piece of data.

The `CLIENT-ID` is given to our Camel-K integration by the _Service Registry_ so that's already available to us. We just need to control the stream and be aware when the last Kafka event is sent.

<br />

## Camel-K modifications

To implement the logic above explained we need to adjust our implementation.

Edit your integration:

- `gbp-eur.xml`

And follow the steps below:

1. First and foremost, we're intending to connect all the dots, therefore we need to reconfigure the Kafka producer to point to the Kafka Topic where the _Content Server_ is subscribed to.

    Ensure your replace the Camel Kafka component configuration:

    - from its current test topic:

      ~~~xml
      <to uri="kafka:lab-test?...
      ~~~

    - to the _'production'_ topic :

      ~~~xml
      <to uri="kafka:lab-streams?...
      ~~~

1. Locate the relevant section of the source code and include the portion below that wraps the raw data from the source stream:

 
   ~~~xml
   <setBody>
        <simple>{"id":"{{client.id}}","last":"false","payload":"${body}"}</simple>
   </setBody>
   ~~~

    > **Note:** how the user-id is injected with `{{client.id}}`, this is the external property configured by the _Service Registry_ when spinning the Camel-K integration.

    > **Note:** how the field `last` is always set to `false`, this is because the stream is continuous on this part of the code.

    > **Note:** how the field `payload` carries the raw data of the stream.

2. You need now to ensure you close the stream by sending the last Kafka event flagged with the parameter `last:true`.

   Locate the relevant part on your Camel-K integration where you can fit the following extra code:

    ~~~xml
    <setBody>
        <simple>{"id":"{{client.id}}","last":"true","payload":"end of stream"}</simple>
    </setBody>
            
    <to uri="kafka:lab-streams?brokers=YOUR_BROKER_SERVICE_URI"/>
    ~~~
   
   where `YOUR_BROKER_SERVICE_URI` is the service URI corresponding to your Kafka service.

    > **Note:** how this time the field `last` is set to `true`, indicating end-of-stream.

    > **Note:** also how the configured Kafka Topic is pointing to the _'production'_ one.

<br />

Alright, all the modifications should now be in place. All is needed now is to update the Camel-K registration to upload the new version.

<br />


## Update the Service Registry

Now, with your tunnel to the _Service Registry_ still open, execute the following `cURL` command to register your Camel-K integration:

~~~shell
curl -X POST \
-H 'Content-Type: application/json' \
-H 'service: {"name":"gbp-eur","description": "(updated) exchange rate for currency GBP-EUR"}' \
-d "@gbp-eur.xml" \
'http://localhost:8080/camel/services/register'
~~~

> **Note:** how we've included the tag `(updated)` in its description so that we can verify the update happened.

If the execution was successful, the GBP/EUR service should have been updated. Invoke the REST _List Services_ API to validate it:

    curl http://localhost:8080/camel/services/list

you should obtain the following JSON response back:

~~~json
{
    "services" : [ {
    "name" : "demo",
    "description" : "Demo service to showcase the Registry Service."
    }, {
    "name" : "gbp-eur",
    "description" : "(updated) exchange rate for currency GBP-EUR"
    } ]
}
~~~

where you should be able to validate the integration was `(updated)`.

<br />

---

## Test the end-to-end GBP/EUR integration

This is it, this is the moment where you should see all your efforts come to fruition. If everything was properly done, you should be able to consume the content service as a final user.

Let's get the URL of the content server

    ROUTE_URL=`oc get route contentserver -o jsonpath='{..spec.host}'`
    
set `{USER_ID}`  to any value of your choice, for instance 'Eastwood', or 'Clint' for a shorter one.

    USER_ID=<value>
    
Run the following command and from your browser, open a new tab, and type in the URL generated by this command

    echo http://$ROUTE_URL/camel/stream/currency/gpb-eur/${USER_ID}


When you hit enter to trigger the call, the _Content Server_ will receive the request, and immediately invoke the Service Registry to spin up an integration. Then it will wait to receive Kafka events to be routed to Mr. Clint Eastwood.

You should see on screen the following, and after a minute, the integration should send the self-destroy signal and stop.

~~~console
Activating stream, please wait...
1.1115
1.1184
1.1088
...
1.1012
1.1173
end of stream
End of stream, thank you for using this service.
~~~

The animation below summarises all the components involved working in concert:

![](https://gitlab.com/BrunoNetId/lab-camelk/raw/master/docs/images/lab05/end-to-end-animation.gif)

</br>

---
# Congratulations !!

We're already thinking in which project you could help us next...
