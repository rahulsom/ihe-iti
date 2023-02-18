[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Maven Central](https://img.shields.io/maven-central/v/com.github.rahulsom/ihe-iti)
[![Maven Snapshot](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Foss.sonatype.org%2Fcontent%2Frepositories%2Fsnapshots%2Fcom%2Fgithub%2Frahulsom%2Fihe-iti%2Fmaven-metadata.xml)](https://oss.sonatype.org/content/repositories/snapshots/com/github/rahulsom/ihe-iti/)


# ihe-iti

![Bender](http://i.imgur.com/M6TjMim.jpg)

## A better description

IHE has a bunch of profiles as part of the IT Infrastructure (ITI) Framework.
Like anything related to healthcare, it comes with its own quirks. Also given
that these profiles use SOAP Web Services and use MTOM and JAXB doesn't like
a lot of things that are being done here, any developer is likely to have a
tough time dealing with these profiles.

This project aims to make it easier for anyone who wants to work with these
profiles.

Currently this supports these services:

* PIX, PDQ, XCPD
* XDS.b (Repository/Registry), XCA (Initiating/Responding)
* SVS Repository
* RAD

In Addition, it supports parsing of CDA Documents. Look at `com.github.rahulsom.cda.POCDMT000040ClinicalDocument`


# Usage

If you want to download the library from Maven Central, add this to your dependencies section:

```kotlin
dependencies {
  implementation("com.github.rahulsom:ihe-iti:<VERSION>")
}
```

Alternately you can download and build this locally. This project is built using
Maven. Just clone the repo & checkout the branch, and run

```shell
./gradlew build
```

That should make it possible for you to use these classes.

Sample code is [here](http://rahulsom.github.io/ihe-iti/).

# Contributing

A lot of profiles are not being built because they haven't been tested. If you
feel you need to use one of these, please feel free to modify the pom and send
a pull request.

Issue tracking is done through GitHub at [https://github.com/rahulsom/ihe-iti/issues](https://github.com/rahulsom/ihe-iti/issues). Feel free to raise issues or fix open issues.

# Questions & Discussion

There's a Google Groups Mailing list at [ihe-iti](https://groups.google.com/d/forum/ihe-iti). That's the best way to get answers.
