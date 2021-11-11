# Integrate the Content Provider

The previous chapters in the Lab helped you to set the context of the overall goal of the workshop, where AMQ-Streams and Camel-K are key building blocks for the proposed solution.

The Lab has been prepared with a pre-deployed Content Provider for you to integrate with the Content Server. This backend instance is a simulator (built with Fuse) for you to test with and pretends to be an Exchange Rate system serving Currency prices for the pair GBP/EUR.

The system has been deployed for convenience in the OpenShift cluster but could well be located externally, this should not make any difference to the exercise.

To get a feel of the system, hit the _Content Provider_ with your browser.

The system URL should be similar to:

-     http://currency-lab-resources.apps.{CLUSTER-ID}/camel/currency/gbp-eur

where `CLUSTER-ID` would designate your cluster.

You should see your browser printing prices like below:

![](https://gitlab.com/BrunoNetId/lab-camelk/raw/master/docs/images/lab03/currency-stream.gif)

If you pay attention, you will notice your browser is keeping the HTTP connection open, and the backend server is streaming back a GBP/EUR price every second.

You already know some of the building blocks forming part of the platform, the following diagram shows what information flow the integration should enable:

![](https://gitlab.com/BrunoNetId/lab-camelk/raw/master/docs/images/lab03/solution-view-01.png)

Our Exchange Rate system is represented in the above diagram as the Content Provider.

Our mission is to create a Camel-K definition that is able to open an HTTP stream from the Content Server and place a Kafka event per price in a Kafka topic.


## Create the Camel-K definition

In previous chapters you got familiar with Camel-K and Kafka. You should now be fully skilled to deliver this implementation.

First create an empty new Camel-K XML definition in your local development environment, and name it:

 - `gbp-eur.xml`

As seen in your browser from opening the HTTP stream, a single HTTP call is enough to trigger the data flow from the server to Camel. You then need to ensure Camel starts a single execution.

Define your Camel-K skeleton as follows:

~~~xml
<routes xmlns="http://camel.apache.org/schema/spring">
    <route>
        <from uri="timer:trigger?repeatCount=1"/>
        <log message="display log message only once"/>
    </route>
</routes>
~~~

If not already there, switch to your OCP Camel-K namespace:

    oc project lab-{user}

where `{user}` is your chosen username.

Run in DEV mode your Camel-K integration to ensure all is in order and your Route only triggers once. your command should be:

    kamel run --dev gbp-eur.xml

If something is not quite right, review your actions and retry. Once happy with the results, stop the integration with <kbd>ctrl</kbd>+<kbd>c</kbd>.

<br />

--- 

## Invoke the Exchange Rate system

Now comes the part where we need to open the HTTP stream against the backend, but this is actually not a straightforward step. Everyone is familiar with short HTTP request/reply interactions (typical of REST API calls for example), but this HTTP call is different.

Thankfully Camel is well equipped to deal with this sort of scenario. However you need to be aware of the different HTTP Camel components that are available to pick the right one for the occasion:

- `camel-http` (our pick)
- camel-undertow
- camel-netty-http
- camel-ahc

When using most of the above Camel HTTP components, Camel will wait for a full Request/Response interaction before the route resumes execution.

The Camel component `camel-http` however does not block and leaves the route to resume processing so that the input stream can be handled as needed.

The raw HTTP request line and host header would look like:

~~~
GET /camel/currency/gbp-eur HTTP/1.1
Host: currency.lab-resources.svc:8080
~~~

> **Please Note:** the host address points to the internal OCP service URI since the call stays inside OCP.

From the raw HTTP request details above you can now define the Camel instructions to resolve the HTTP call. Please include the following snippet to your Camel-K skeleton:

~~~xml
        <setHeader name="CamelHttpMethod">
            <constant>GET</constant>
        </setHeader>

        <setHeader name="CamelHttpPath">
            <constant>/camel/currency/gbp-eur</constant>
        </setHeader>

        <to uri="http:currency.lab-resources.svc:8080?disableStreamCache=true"/>

        <log message="HTTP invocation was successful."/>
~~~

> **Please Note:** how the HTTP invocation includes the parameter `disableStreamCache=true` to ensure the Camel does not try to cache the full response.

Run your Camel-K integration to validate all works as expected:

    kamel run --dev gbp-eur.xml

You should see in your terminal the output:

    ...
    ... timer://trigger] route1 - HTTP invocation was successful.

It would indicate you successfully connected to the Exchange Rate system.


<br />

--- 

## Split the HTTP stream

You could say now the telephone call is open, and we just need to digest the conversation. The line will feed Camel with a continuous stream of GBP/EUR prices, one every second.

Camel includes the EIP Splitter:

![](https://gitlab.com/BrunoNetId/lab-camelk/raw/master/docs/images/lab03/camel-splitter-eip.png)



Use the keyword `<split>` to chunk the content in pieces, and process each one individually.

Include the following snippet in your Camel-K code:

~~~xml
        <split streaming="true">
            <tokenize token="\n"/>
            <log message="rate: ${body}"/>
        </split>
~~~

> **Please Note:** the `streaming` flag is enabled, otherwise the splitter would try to load the full request first.

> **Please Note:** how the splitter is configured to cut the stream using the token '\n'.

Run your Camel-K integration to validate all works as expected:

    kamel run --dev gbp-eur.xml

You should see in your terminal an output similar to:

    ... timer://trigger] route1 - rate: 1.1101
    ... timer://trigger] route1 - rate: 1.1114
    ... timer://trigger] route1 - rate: 1.1055
    ... timer://trigger] route1 - rate: 1.1099
                                        ...


It would indicate the splitter is doing its job.

<br />

--- 

## Send the stream data to Kafka

You're almost done with all difficult bits. Now that you have the chunks, you just need to send each one to Kafka to hand over processing responsibility.

- Reset the Kafka topic

    For testing purposes, you'll be using the same Kafka topic as in Lab 2, but before you proceed, let's delete the topic to ensure you work with a clean sheet.

    Execute the following command:

      oc delete kt lab-test -n lab-{user}-kafka

    where `{user}` is your chosen username.

    Double check the Kafka topic does not exist:

      oc get kt -n lab-{user}-kafka

    <br />

You're now in position to resume your Camel-K implementation.

You should be in very familiar ground by now since you've already created previously Camel-K definitions to produce Kafka traffic.

In case you already forgot how it's done, as a reminder here is the code necessary in your Camel-K integration:

~~~xml
            <to uri="kafka:lab-test?brokers=YOUR_BROKER_SERVICE_URI"/>
~~~
where `YOUR_BROKER_SERVICE_URI` is the service URI corresponding to your Kafka service.

> **Reminder:** even though you deleted the Kafka topic, it will be automatically recreated when Camel connects to Kafka.

Run your Camel-K integration for a few seconds to generate some traffic to send prices to Kafka:

    kamel run --dev gbp-eur.xml

Then stop the process with <kbd>ctrl</kbd>+<kbd>c</kbd>.

To validate Kafka's broker received the messages from Camel, let's run the Camel-K consumer you implemented in Lab 02:

    kamel run --dev consumer.xml

If everything went according to plan, you should see GBP/EUR prices showing up in your terminal output similar to:

    ... KafkaConsumer[lab-test]] route1 - 1.1056
    ... KafkaConsumer[lab-test]] route1 - 1.1003
    ... KafkaConsumer[lab-test]] route1 - 1.1147
    ...                 ...                  ...
    ... KafkaConsumer[lab-test]] route1 - 1.1183


you can stop the consumer with <kbd>ctrl</kbd>+<kbd>c</kbd>.


</br>

---


Click the link to the [Next](./lab04.md) chapter when ready.