# Camel based sizing patterns

This repository collects a number of Camel based flow patterns very common in large integration platforms. They are useful to represent different levels of complexity and perform sizing measurements.



## Goal

To collect a number of representative Camel based patterns, and standardise their implementation across different Camel flavoured runtimes:

 - Fuse
 - Camel K
 - Camel Quarkus

## Requirements

Each pattern should:

 - expose an OpenApi interface
 - perform AtlasMap based data transformations
 - perform JSON to XML conversions
 - include HTTP backends to interact with
 - be based on Camel's XML DSL

## Patterns

### Simple complexity

This level of complexity is described with the following flow:

```
client ---JSON--> Camel[json2xml] ---XML--> stub 
client <--JSON--- Camel[xml2json] <--XML---  
```

### Medium complexity

To be defined