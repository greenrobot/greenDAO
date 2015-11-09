**Please help us with this short survey: http://bit.ly/greendao-survey Thanks for your support!**

greenDAO
========
greenDAO is a light & fast ORM solution for Android that maps objects to SQLite databases. Being highly optimized for Android, greenDAO offers great performance and consumes minimal memory.

**<font size="+1">Home page, documentation, and support links: http://greenrobot.org/greendao/</font>**

[![Build Status](https://travis-ci.org/greenrobot/greenDAO.svg?branch=master)](https://travis-ci.org/greenrobot/greenDAO)

Features
--------
greenDAO has a unique set of features:

* Rock solid: greenDAO has been around since 2011 and is used by countless famous apps
* Super simple: concise and straight-forward API
* Small: The library is <100K and it's just plain Java jar (no CPU dependent native parts)
* Fast: Probably the fastest ORM for Android, driven by intelligent code generation
* Safe and expressive query API: QueryBuilder uses property constants to avoid typos
* V2.0.0 Powerful joins: query across entities and even chain joins for complex relations
* V2.0.0 Flexible property types: use custom classes or enums to represent data in your entity


Add greenDAO to your project
----------------------------
greenDAO is available on Maven Central. Please ensure that you are using the latest versions by [checking here](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.greenrobot%22%20AND%20a%3A%22greendao%22) [and here](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.greenrobot%22%20AND%20a%3A%22greendao-generator%22)

Gradle dependency for your Android app:
```
    compile 'de.greenrobot:greendao:2.0.0'
```

Gradle dependency for your Java generator project:
```
    compile 'de.greenrobot:greendao-generator:2.0.0'
```


Features in Beta
----------------
Those features are already here for you to try out. Note: Documentation and test coverage may be lacking, and the API may change in the future.
### Asynchronous API
* New AsyncSession (acquired from DaoSession.startAsyncSession()) provides most operations for DAOs, Queries, and transactions in a asynchronously variant
* AsyncOperations are processed in order by a background thread
* waitForCompletion methods for AsyncSession and AsyncOperations
* AsyncOperationListener for asynchronous callback when operations complete
* Asynchronous operations can be merged in single transactions (details follow)
* Added raw SQL queries returning a Query object (LazyList support etc.)

More Open Source by greenrobot
==============================
[__EventBus__](https://github.com/greenrobot/EventBus) is a central publish/subscribe bus for Android with optional delivery threads, priorities, and sticky events. A great tool to decouple components (e.g. Activities, Fragments, logic components) from each other.

[__greenrobot-common__](https://github.com/greenrobot/greenrobot-common) is a set of utility classes and hash functions for Android & Java projects.

[Follow us on Google+](https://plus.google.com/b/114381455741141514652/+GreenrobotDe/posts) to stay up to date.

