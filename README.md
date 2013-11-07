ihe-iti
=======

![Bender](http://i.imgur.com/M6TjMim.jpg)

A better description
--------------------

IHE has a bunch of profiles as part of the IT Infrastructure (ITI) Framework.
Like anything related to healthcare, it comes with its own quirks. Also given
that these profiles use SOAP Web Services and use MTOM and JAXB doesn't like
a lot of things that are being done here, any developer is likely to have a
tough time dealing with these profiles.

This project aims to make it easier for anyone who wants to work with these
profiles.

Usage
-----

If you want to download the library from Sonatype, add this to your dependencies
section:

    <dependency>
      <groupId>com.github.rahulsom</groupId>
      <artifactId>ihe-iti</artifactId>
      <version>0.3</version>
    </dependency>

To browse the latest builds, you can see [MavenRepository](http://mvnrepository.com/artifact/com.github.rahulsom/ihe-iti). It also has instructions for Gradle, Ivy, sbt, etc.

And add this to your repositories section:

    <repository>
      <id>sonatype-oss</id>
      <url>https://oss.sonatype.org/content/groups/public</url>
    </repository>

Alternately you can download and build this locally. This project is built using
Maven. Just checkout the code and run

    mvn install

That should make it possible for you to use these classes.

Contributing
------------

A lot of profiles are not being built because they haven't been tested. If you
feel you need to use one of these, please feel free to modify the pom and send
a pull request.

Builds
------

This project is being built on travis. See
[https://travis-ci.org/rahulsom/ihe-iti](https://travis-ci.org/rahulsom/ihe-iti)
It gets automatically deployed to [Sonatype OSS](https://oss.sonatype.org/).

[![Build Status](https://travis-ci.org/rahulsom/ihe-iti.png)](https://travis-ci.org/rahulsom/ihe-iti)
