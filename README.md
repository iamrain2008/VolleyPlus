VolleyPlus
==========

A clone of Volley. It works with OkHttp library.

It use a Map as http request headers and <a href="https://github.com/square/okhttp/wiki/Recipes" target="_blank">RequestBody</a> as http request body.

It also has a method onFinish, it will call when a http request finish.



## How to use

```
repositories {
    maven { url 'https://raw.github.com/remex2008/VolleyPlus/master/release' }
}
```

then

```
dependencies {
    compile 'info.feelyou.volleyplus:library:0.1.0'
}
```