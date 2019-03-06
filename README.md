# Reactive streams interoperability
Project Reactor, Akka Stream, RxJava 2, reactive streams interoperability with JDK 9+ Flow types

_make sure you have jdk installed_

```bash
jenv local 11.0
```

```bash
./mvnw clean package exec:java
# output:
0
-1
-2
# ...
```
