# savvymoney-v2-load-tests

Goals
=====

The goal of this project is to provide simple way to execute JMeter test scenarios. It is achieved by maven and JMeter java API.

The main idea is:
1) JMeter jmx-files (test scenarios) are stored in project's source code
2) Scenarios have input parameters. Default values are described in conf-files, connected to specific scenario.
3) JUnit is used as runner which fired by Maven Surefire Plugin
4) During JUnit run test-users are created by calling internal API of Credit Service, Presentation API and Credit Session Manager
5) Created users are passed into scenarios as list in CSV-files
6) JMeter starts in each JUnit test by using its native Java API. This API is managed as Maven dependency so there is no need to install 
any additional software on one's computer or CI\CD server.

Results of each scenario execution and its final configured version are stored into `target\jmeter` directory is specific folder per each
test. 


Parameterization
================

Project allows to set
* environment (local, dev, sandbox, etc). See `src/main/resources/environments.conf`
* any test parameter

Every test describes its parameters in its Junit Java class. As instance: `var("RegisteredWidgetTest", "numberOfThreads")`.

This parameter has default value stored in corresponding conf-file which is specific per each test. Important! each parameter should have
prefix. By naming convention prefix should be equal to test class name. 

There are additional common parameters:
* `environment`
* `pid`

In common case any parameter can be specified (in order of priority)
* as java define `-D`  (should have prefix `savvy.`)
* as environment variable  (should have prefix `SAVVY_`)
* in local settings conf-file
* in test specific conf-file
* in `src/main/resources/defaults.conf` file

Examples of java defines `-D`:
* `-Dsavvy.RegisteredWidgetTest.numberOfThreads=1`
* `-Dsavvy.environemnt=sandbox`

Examples of environment variables:
* `SAVVY_RegisteredWidgetTest_numberOfThreads=1`
* `SAVVY_environment=beta`

In order to change path to local settings file one can use (see pom.xml):
* during maven execution: `-Dsavvy.local.settings={PUT_YOUR_PATH_HERE}`
* during IDE executions: via environment variable `SAVVY_LOCAL_SETTING`


Execution
=========

`./mvnw clean test`

Tests can be filtered by using regular Maven Surefire Plugin filtering features. 
* http://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html
* https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html
