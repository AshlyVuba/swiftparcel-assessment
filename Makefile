.PHONY: compile test package web-test clean

compile:
	mvn compile

test:
	mvn test

# The test suite has already run in its own stage by this point,
# so packaging does not run it again.
package:
	mvn package -DskipTests

web-test:
	node --test web/test/app.test.js

clean:
	mvn clean
