greenDAO
========
greenDAO is a light & fast ORM for Android that maps objects to SQLite databases. Being highly optimized for Android, greenDAO offers great performance and consumes minimal memory.

**<font size="+1">Home page, documentation, and support links: http://greenrobot.org/greendao/</font>**

[![Build Status](https://travis-ci.org/greenrobot/greenDAO.svg?branch=master)](https://travis-ci.org/greenrobot/greenDAO)

Features
--------
greenDAO's unique set of features:

* Rock solid: greenDAO has been around since 2011 and is used by countless famous apps
* Super simple: concise and straight-forward API, in V3 with annotations
* Small: The library is <150K and it's just plain Java jar (no CPU dependent native parts)
* Fast: Probably the fastest ORM for Android, driven by intelligent code generation
* Safe and expressive query API: QueryBuilder uses property constants to avoid typos
* Powerful joins: query across entities and even chain joins for complex relations
* Flexible property types: use custom classes or enums to represent data in your entity
* Encryption: supports SQLCipher encrypted databases

Add greenDAO to your project
----------------------------
greenDAO is available on Maven Central. Please ensure that you are using the latest versions by [checking here](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.greenrobot%22%20AND%20a%3A%22greendao%22) [and here](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.greenrobot%22%20AND%20a%3A%22greendao-generator%22)

Add the following Gradle configuration to your Android project:
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'org.greenrobot:greendao-gradle-plugin:3.2.1'
    }
}
 
apply plugin: 'org.greenrobot.greendao'
 
dependencies {
    compile 'org.greenrobot:greendao:3.2.0'
}
```

Note that this hooks up the greenDAO Gradle plugin to your build process. When you build your project, it generates classes like DaoMaster, DaoSession and DAOs.

Homepage, Documentation, Links
------------------------------
For more details on greenDAO please check [greenDAO's website](http://greenrobot.org/greendao). Here are some direct links you may find useful:

[Features](http://greenrobot.org/greendao/features/)

[greenDAO 3](http://greenrobot.org/greendao/documentation/updating-to-greendao-3-and-annotations/)

[Documentation](http://greenrobot.org/greendao/documentation/)

[Changelog](http://greenrobot.org/greendao/changelog/)

[Technical FAQ](http://greenrobot.org/greendao/documentation/technical-faq/)

[Non-Technical FAQ](http://greenrobot.org/greendao/documentation/faq/)

More Open Source by greenrobot
==============================
[__EventBus__](https://github.com/greenrobot/EventBus) is a central publish/subscribe bus for Android with optional delivery threads, priorities, and sticky events. A great tool to decouple components (e.g. Activities, Fragments, logic components) from each other.

[__Essentials__](https://github.com/greenrobot/essentials) is a set of utility classes and hash functions for Android & Java projects.

[Follow us on Google+](https://plus.google.com/b/114381455741141514652/+GreenrobotDe/posts) to stay up to date.

