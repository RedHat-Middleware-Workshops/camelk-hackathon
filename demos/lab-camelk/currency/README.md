# Spring-Boot Document Service Demo

This Fuse application simulates (for demo purposes) a document server.
The goal is to use it as an HTTP backend capable of streaming back very large files.
The client application can invoke this service indicating the document size to download.


### Running the application on your machine

1) Start the application using maven:

		mvn

2) Test the service using the curl command:

		curl -k https://localhost:8443/camel/file/test

   This should return:

		{
		    "status":"ack"
		}

3) Invoke the service using curl to download a small file (1 MB):

		curl -k https://localhost:8443/camel/file/download/1 -o document.txt
	
   This results in a file being downloaded of 1 MB size.


4) Invoke the service using curl to download a very large file (4 GB):

		curl -k https://localhost:8443/camel/file/download/4092 -o document.txt
	
   This results in a file being downloaded of 4 GB size.



### Running the application on OpenShift Cluster

1) Deploy the application in Openshift

	a) create new project

		oc new-project uc-file

	b) deploy the app

		mvn fabric8:deploy -Popenshift


2) expose the service to external traffic (HTTPS)

		oc create route passthrough --service=docserver

   run 'oc get routes' to obtain the URL to the service


3) Invoke the service using curl to download a small file (1 MB):

		curl -k https://docserver-uc-file.192.168.99.117.nip.io/camel/file/download/1 -o document.txt
	
   This results in a file being downloaded of 1 MB size.


4) Invoke the service using curl to download a very large file (4 GB):

		curl -k https://docserver-uc-file.192.168.99.117.nip.io/camel/file/download/4092 -o document.txt
	
   This results in a file being downloaded of 4 GB size.

