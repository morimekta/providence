java_library(
  name = 'junit',
  exports = [
    '@junit_junit//jar',
    '@org.hamcrest_hamcrest-core//jar'
  ],
  visibility = ['//visibility:public'],
  resources = ['LICENSE-Apache-2.0'],
  licenses = ['notice'],
)

java_library(
  name = 'android',
  exports = [
    '@android-sdk//:sdk-16',
  ],
  visibility = ['//visibility:public'],
  resources = ['LICENSE-Apache-2.0'],
  licenses = ['notice'],
)

java_library(
  name = 'args4j',
  exports = [
    '@args4j_args4j//jar',
  ],
  visibility = ['//visibility:public'],
  resources = ['LICENSE-MIT'],
  licenses = ['notice'],
)

java_library(
  name = 'google-http-client',
  exports = [
    '@com.google.http-client_google-http-client//jar',
    '@org.apache.httpcomponents_httpclient//jar',
    '@org.apache.httpcomponents_httpcore//jar',
    '@com.google.code.findbugs_jsr305//jar',
    '@commons-codec_commons-codec//jar',
    '@commons-logging_commons-logging//jar',
    '@avalon-framework_avalon-framework//jar',
    '@logkit_logkit//jar',
    '@log4j_log4j//jar',
  ],
  visibility = ['//visibility:public'],
  resources = [
    'LICENSE-Apache-2.0',
  ],
  licenses = ['notice'],
)

java_library(
  name = 'libthrift',
  exports = [
    '@libthrift//jar',
    '@org.slf4j_slf4j-api//jar',
    '@org.slf4j_slf4j-simple//jar',
    '@org.apache.httpcomponents_httpcore//jar',
  ],
  visibility = ['//visibility:public'],
  resources = [
    'LICENSE-Apache-2.0',  # thrift, httpclient
    'LICENSE-MIT',         # slf4j
  ],
  licenses = ['notice'],
)

java_library(
  name = 'jax-rs',
  exports = [
    '@javax.ws.rs_javax.ws.rs-api//jar',
  ],
  visibility = ['//visibility:public'],
  resources = [
    'LICENSE-CCDL-GPL-1.1',
  ],
  licenses = ['restricted_if_statically_linked'],
)
