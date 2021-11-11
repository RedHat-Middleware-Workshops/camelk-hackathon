# North API exposure with Camel K and AtlasMap

This repository contains all the sources showcased in the following Red Hat Developers article:

 - https://developers.redhat.com/articles/2021/10/08/north-api-exposure-camel-k-and-atlasmap

## Context

Real scenario and strategy where Camel K enables developers to laser focus on the business logic of an API layer. At its core, AtlasMap’s design-time visual data mapping tool assists and augments productivity. Camel K delivers a thin yet fully functional, maintainable and scalable front-facing Kubernetes-native API layer to normalise access to core capabilities.

## Prerequisites

This demo Camel K code requires the following dependencies:

 - A Kubernetes environment
 - A Camel K platform installed
 - A backend system serving XML over REST (provided)

This demo has been tested using:
 - Red Hat OpenShift 4.7
 - Red Hat Camel K 1.4.1 GA


</br>

## Execution

You can watch how the demo is executed in this video clip:

 - https://youtu.be/UB2z2gPxz5M

The demo is composed of 2 Camel K elements

 - 1 Camel K source implementing the API layer to normalise the backend API
 - 1 Camel K source implementing the mock backend service 

You can decide to have them all running at the same time, or deploying one at a time to allow your audience to better follow and understand the demo. 

1. Run the mock XML backend. You'll find instructions under the following folder:

   - [stub/end1/Readme.md](./stub/end1/Readme.md)

2. Run the front-facing Camel K API. You'll find instructions under the following folder:

   - [camelk/Readme.md](./camelk/Readme.md)

