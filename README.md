ihe-iti
=======

Java codegen for IHE ITI profiles.

A better description
----

IHE has a bunch of profiles as part of the IT Infrastructure (ITI) Framework.
Like anything related to healthcare, it comes with its own quirks. Also given
that these profiles use SOAP Web Services and use MTOM and JAXB doesn't like
a lot of things that are being done here, any developer is likely to have a
tough time dealing with these profiles.

This project aims to make it easier for anyone who wants to work with these
profiles.

Usage
----

This project is built using Maven. Just checkout the code and run

    mvn install

That should make it possible for you to use these classes.

Contributing
----

A lot of profiles are not being built because they haven't been tested. If you
feel you need to use one of these, please feel free to modify the pom and send
a pull request.

Future
----

This project is being built on travis. See
[https://travis-ci.org/rahulsom/ihe-iti](https://travis-ci.org/rahulsom/ihe-iti)
The goal is to have travis build it and deploy the artifacts to bin tray.

[![Build Status](https://travis-ci.org/rahulsom/ihe-iti.png)](https://travis-ci.org/rahulsom/ihe-iti)
