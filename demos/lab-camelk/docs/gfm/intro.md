# Introduction 

The goal of this lab is to teach you about the new and upcoming technologies like:

 - **Camel K**: A lightweight integration platform, born on Kubernetes, which brings serverless superpowers to Apache Camel.
 - **AMQ Streams**: Red Hat productised Kafka, covering new use cases in our Messaging Portfolio.
 - **Operators**: A method of packaging, deploying and managing a Kubernetes application oriented towards automation of operational tasks.  

You will learn about these new and exciting technologies by implementing the solution outlined bellow. 

## Introduction to the Lab Solution

The Lab exercises you are about to follow are proposing a use case where users can request content on demand. Users can browse over a list of different content feeds and subscribe to it. 

The content could be of very different nature, i.e. financial, news, media. This implies our organisation having close partnership with different content providers. Our solution sits in the middle, integrating content providers with final users consuming data.

As usual, there are many ways to build such a solution, but the goal on this Lab is to experiment with new techniques that can bring added value when compared to the traditional approaches.

Some of the benefits explored are:

- Minimise resource usage when content is not consumed.

- Minimise impact on the platform when a new content provider is integrated.

- Minimise effort of integrating new providers.

- Clean decoupling between the platform's systems.

The overall picture would look as follows:

![](https://gitlab.com/BrunoNetId/lab-camelk/raw/master/docs/images/intro/platform-view-01.png)

You will be closely involved in the implementation of the block marked with a big question mark. The rest is either already installed in the Openshift cluster you will use for this lab or we will provide instructions on how to deploy it in you own projects. 

**_We hope you can bring great new ideas!_**

---

## Lab index:

The group of lab exercises below drive the student to build and deploy the architecture (based on the proposed use case). The exercises are divided in different chapters to help the student getting familiar with the different technologies and components playing a part in the solution.

> **`Attention`**: ensure your environment has been prepared to run this lab. \
Find **[here](./base.md)** instructions to ensure the pre-requisites are met.

The Lab is split in the following chapters: 

1. [Hands-on with Camel-K](./lab01.md)
1. [Hands-on with AMQ-Streams](./lab02.md)
1. [Platform Implementation (part 1): ](./lab03.md)Integrate the Content Provider
1. [Platform Implementation (part 2): ](./lab04.md)Register the Camel-K integration
1. [Complete End-to-End Flow](./lab05.md)

</br>

---

Start from:  [Chapter 1)](./lab01.md)