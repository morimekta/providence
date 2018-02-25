Providence License
==================

The providence project is under the Apache 2.0 license, except
where specifically noted otherwise. See contributor list for details.

## Main Constributors

- [@morimekta](http://github.com/morimekta) Stein Eldar Johnsen

## Copyright Notice

All files in the project that does not have a copyright notice
implicitly has this notice. This includes config files, code
files, scripts etc.

```text
Copyright (c) ${YEAR}, Providence Authors

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
```

### Other licenses

Libraries used by the release artifacts of providence with
licenses are as follows. These will be dragged into shade
builds if the dependency is present, among others:

* `providence-core`:
    - `com.google.guava`: [Apache 2.0](https://github.com/google/guava/blob/master/COPYING)
    - `net.morimekta.util`: [Apache 2.0](https://github.com/morimekta/utils/blob/master/LICENSE)
    - `org.slf4j`: [MIT License](https://www.slf4j.org/license.html)
* `providence-core-client`:
    - `google-http-client`: [Apache 2.0](https://github.com/google/google-http-java-client/blob/dev/LICENSE)
    - `org.apache.httpcomponents`: [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
* `providence-core-server`:
    - `org.eclipse.jetty`: [Apache 2.0](https://www.eclipse.org/jetty/licenses.html)
* `providence-core-jackson`:
    - `com.fasterxml.jackson`: [Apache 2.0](https://github.com/FasterXML/jackson-core/blob/master/src/main/resources/META-INF/LICENSE)
* `providence-jax-rs`:
    - `javax.ws.rs`: [CDDL 1.1](https://github.com/jax-rs/api/blob/master/LICENSE.txt)
* `providence-thrift-protocols`:
    - `org.apache.thrift`: [Apache 2.0](https://github.com/apache/thrift/blob/master/LICENSE)
* `providence-storage-hazelcast`:
    - `com.hazelcast`: [Apache 2.0](http://docs.hazelcast.org/docs/latest-development/manual/html/License_Questions.html)
