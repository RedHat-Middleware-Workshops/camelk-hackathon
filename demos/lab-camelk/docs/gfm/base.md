# Base Lab preparations

This lab was originally conceived to run on RHPDS and be preloaded with necessary resources to provide the lab attendant a smooth ride through.

Since you may be running the lab in your own environment, some preparations need to be completed before starting the lab exercises.

The following instructions will help you to prepare your environment to make it Lab ready.


## Pre-requisites

Ensure you have an OpenShift 4.x up and running with admin rights.


## Install a cluster-wide AMQ-Streams Operator

Each Lab attendant will deploy its own AMQ-Streams instance so that they can familiarise themselves with the concept of Operators, and play a bit with AMQ-Streams and learn its base concepts.

The Lab attendant will make use of an existing cluster wide AMQ-Streams Operator. You need therefore to install it in your OpenShift environment.

1. Login as administrator
1. Switch to the `openshift-operators` project.
1. Navigate to the *OperatorHub* and select *AMQ-Streams*
1. Click '*Install*'
1. Select the default option: 
   - 'All namespaces on the cluster (default)'
1. Click '*Subscribe*'

Having completed the above should allow Lab attendants to have the Operator visible in their namespace and be able to deploy their own Kafka instance.

</br>

## Prepare Fuse Resources


### Import Fuse image 
Your OCP environment might not have the latest Fuse image loaded:

 - fuse-java-openshift:1.4

Without it, Fabric8's Maven plugin will fail.

To load the image into *Openshift* run the command:

    oc import-image fuse7-java-openshift:1.4 \
    --from=registry.redhat.io/fuse7/fuse-java-openshift:1.4 \
    --reference-policy='local' --comfirm \
    -n openshift

If you're using Fuse 7.9 on JDK 11 run the command:

    oc import-image fuse-java-openshift-jdk11-rhel8:1.9 \
    --from=registry.redhat.io/fuse7/fuse-java-openshift-jdk11-rhel8:1.9 \
    --reference-policy='local' --confirm \
    -n openshift
    
*Note*: you may have to be logged in as cluster-admin to import the fuse image to the `openshift` namespace

</br>

### Deploy Fuse Lab components

The lab requires the *CurrencyProvider*  to be up and running before the student can start. It is a *Fuse* based system that can be deployed with *Maven*.

1. Login to *OpenShift* as administrator
1. Create a new project running the command:

       oc new-project lab-resources

1. Clone the lab's Git repository:

       git clone https://gitlab.com/BrunoNetId/lab-camelk.git

1. Switch to the *Currency* Fuse project folder:

       cd lab-camelk/currency

1. Using *Maven*, build and deploy the *Currency* application:

   Fuse 7.4

       mvn fabric8:deploy -Popenshift 

   Fuse 7.9

       mvn oc:deploy -Popenshift

1. Once deployed, expose its service with the command:

       oc expose service currency

</br>

---

At this point the environment is ready for the hands-on lab.

[Back to Index](./intro.md#lab-index)
