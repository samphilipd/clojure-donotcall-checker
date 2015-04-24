# clojure-donotcall

A simple REST application to check if a phone number is present in a Do Not Call blacklist e.g. https://telemarketing.donotcall.gov/

You will need leiningen to build/run it.

Designed to run on Apache Tomcat, but can also be run standalone with a Jetty server. Database should be set using the `DATABASE_URL` ENV variable.

# Installation

## To run locally:

1. `lein ring server`

## To build for Tomcat:

1. `lein ring uberwar`
2. Deploy the war file created at target/donotcall-standalone.war

## To run standalone

1. `lein uberjar`
2. `DATABASE_URL="postgresql://user:pass@yourhost/yourdb" target/donotcall-standalone.jar

# Querying

Query single numbers like this:

`GET hostname/donotcall/1234567890`

If the number is in the list, will return

`{"number": "1234567890"}` (status 200)

if the number is not in the list, will return

`{}` (status 404)

Query multiple numbers in batch like this:

`GET hostname/batch?numbers=1000000000,20000000000,3000000000,<...>`

Will return only numbers found in the list. If no numbers are found, will return an empty array.

`{"numbers":["1000000000", "30000000"]}`

## License

Copyright © 2015 Sam Davies

Distributed under the MIT license.
