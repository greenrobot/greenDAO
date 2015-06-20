greenDAO
========
greenDAO is a light & fast ORM solution for Android that maps objects to SQLite databases. Being highly optimized for Android, greenDAO offers great performance and consumes minimal memory.

**<font size="+1">Home page, documentation, and support links: http://greendao-orm.com/</font>**

[![Build Status](https://travis-ci.org/greenrobot/greenDAO.svg?branch=master)](https://travis-ci.org/greenrobot/greenDAO)

Upcoming features (already available in beta quality)
-----------------------------------------------------
New features ahead for the brave: documentation and test coverage may be lacking. Give it a shot if you dare. API may change in the future. 
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

Release History
---------------
### Future release
* Added Flag for AsyncOperation to track the caller's stacktrace (useful for debugging)

### Generator V1.3.1 (2014-05-24): Bugfix
* Fix schema version >= 1000

### V1.3.7 (2013-11-27): Bugfix
* Fixed building defect DeleteQuery for tables ending with character 'T' (bug introduced in 1.3.3)
* Prepared Fast Cursor for API level 19

### V1.3.6 (2013-11-15): Bugfix
* Fixed leaked statement in DeleteQuery

### V1.3.5 (2013-11-14): Bugfix
* Because of an issue with ApplicationTestCase, the base test class DbTest does not extend this class anymore.
Note: This refactoring may require you to adjust some test classes if your tests depend on DbTest or its subclasses.

### V1.3.4 (2013-10-28): Bugfix
* Redeployment of 1.3.3 artifacts without some old class leftovers

### V1.3.3 (2013-10-18): Bugfix
* Fixed a memory leak affecting Query class that was introduced in 1.3.0 (#93)
* Fixed a rare race condition that can lead to "Entity is detached from DAO context" DaoException (#101)

### V1.3.2 (2013-08-28): Bugfix
* Fixed building CountQueries with combined AND/OR conditions
* Some secret inoffical work in progress

### V1.3.1 (2013-03-02): Fixed Gradle dependencies
* Don't use Gradle's "compile" dependency scope

### V1.3.0 (2013-02-24): Multithreading robustness and refactoring (breaking changes!)
* Reworked internal locking of insert/update/delete methods
* Fixed potential deadlocks when transactions are executed concurrently to one of the various insert/update/delete calls
* Reworked queries to be used without locking, query instances are now bound to their owner thread (breaking change!)
* Relations use the new lock-free query API
* Query classes were moved into the new query subpackage (breaking change!)
* Introduced Gradle build scripts for DaoCore and DaoGenerator projects
* Maven artifacts are pushed to Maven Central starting with this version
* Added two packages for classes used internally (identityscope and internal)
* Added new deleteByKeyInTx DAO method to efficiently delete multiple entities using their keys
* Added some checks to throw exceptions with nicer messages telling what's wrong
* Added Travis CI

### V1.2.0 (2012-06-08): Feature release
* Limited support of String PKs (no relations using String FKs yet)
* Fixed index creation (please update your schema)
* Added CountQuery for creating SELECT COUNT (*) queries with QueryBuilder
* Added getDatabase in DaoMaster, DaoSession, and Dao
* Added insertOrReplaceInTx in Dao
* Added deleteInTx in Dao
* Added autoincrement() creating AUTOINCREMENT PKs
* Made DAOs and DaoSessions in active entities transient (allows serialization of entities)
* Minor fixes

### V1.1.2 (2012-03-26): ADT 17 support for demo project
* Demo projects works with ADT 17 (moved greendao.jar into libs)
* CREATE/DROP TABLE may be skipped for entity types: This allows having multiple entity types operate on the same table
* Minor improvements

### V1.1.1 (2012-02-14): Mini bugfix&feature release
* Added NOT IN condition for QueryBuilder
* Fix for Roboelectric (Issue #22)
* Minor fix (Issue #5)

### V1.1.0 (2012-02-13): Feature release
* DeleteQuery for bulk deletes
* Entities may implement Java interfaces
* Entities may extend a Java class
* Added LIMIT and OFFSET support for QueryBuilder and Query
* Convenience methods to add named relationships
* SQL scripts are executed in a transaction by default
* Fixed queries with special column names (SQL keywords)
* Changed default names for to-many relations to end with "List"
* ORDER BY uses LOCALIZED collation for strings by default

### V1.0.1 (2011-10-30): Bugfix release
* Fixed generation of to-many relations
* Fixed generated import statements when entities/DAO are not in the same package

### V1.0.0 (2011-10-24): First open source release
* To-many relations (lazily loaded on the entities)
* To-many relations with custom join properties
* Active entities can be updated, refreshed, and deleted directly
* Significant performance improvements (faster identity scope, faster database result parser)
* "Keep sections" for custom code in entities were added that won't be overwritten during code generation
* Other minor improvements

### Third preview (2011-08-19) 
http://greendao-orm.com/2011/08/19/query-builder-and-lazy-loading-lists/

### Second preview (2011-08-12) 
http://greendao-orm.com/2011/08/12/greendao-2nd-preview/

### First public release (2011-08-04) 
http://greendao-orm.com/2011/08/04/greendao-public-release/