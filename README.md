greenInject
===========
greenInject is a injection library for Android optimized for minimal size.

Read more about greenInject: https://github.com/greenrobot/greenInject/wiki

Release History
---------------
### V0.6 (2011-09-08)
* Workaround for http://code.google.com/p/android/issues/detail?id=5964 which crashes @OnClick annotations on Android 2.1 and below. Should be fine with Android 1.6 now.
* Value binding for ImageView (one-way)
* Annotation @Target allows compile time checks for annotation's location
* Separated ValueBinder from Injector
* Improved ValueBinder performance by static caching

### V0.5 (2011-08-29)
Initial open source version with basic features:

* Inject views
* Inject most important resource types
* Inject Intent extras
* Value binding for TextView descendants and Strings
* Listener Binding with @OnClick
