# API

* Compile application (example):

```sh
./gradlew build
```

* Testing application (example):

```sh
./gradlew test
```

* Compile application without test (example):

```sh
./gradlew build -x test
```

* Deploying applications to Heroku (example):

```sh
./gradlew build deployHeroku
```
*Parameters:*
```sh
--app=rest-xxx
-Dserver.port=$PORT 
-Dspring.profiles.active=dev
```

* Run application - executable jar file (example):

```sh
./RESTfulTemplate-0.0.1-SNAPSHOT.jar &
```

* Run application (example):

```sh
java -jar RESTfulTemplate-0.0.1-SNAPSHOT.jar &
```

* Run application from gradle (example):

```sh
./gradlew bootRun
```

* Find the PID of the process using a specific port (example, port - 8080):

```sh
lsof -i:8080
```

* Shutdown application (example):

```sh
kill {PID}
```

* Profiles (modes):
    * local (default)
    * test
    * dev
    * remote
    * prod
    
* Open in browser (example):

    * http://localhost:8080

* Read documentation (example):

    * http://localhost:8080/swagger-ui.html
    
* Generate a self-signed SSL certificate (example, JKS - proprietary format specific for Java):

```sh
keytool -genkey -alias tomcat -keyalg RSA -keystore KeyStore.jks
```

* Generate a self-signed SSL certificate (example, PKCS12 - industry standard format):

```sh
keytool -genkey -alias tomcat -keyalg RSA -storetype PKCS12 -keystore KeyStore.p12
```

* Check the content of the keystore (example):

```sh
keytool -list -v -keystore {PATH}
```

* Generate SSL certificate with Let’s Encrypt (example):

```sh
sudo certbot certonly --standalone -d {DOMAIN}
```

* Generate PKCS12 files from PEM files(Let’s Encrypt) (example):

```sh
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out KeyStore.p12 -name tomcat -CAfile chain.pem -caname root
```
    
* Run application with redirect HTTP(80) to HTTPS(443) (example):

```sh
java -Dserver.port=443 -Dmanagement.server.port=-1 -Dserver.ssl.key-store={PATH} -Dserver.ssl.key-store-password={PASSWORD} -Dserver.ssl.key-store-type={TYPE} -Dserver.ssl.key-alias={ALIAS} -jar RESTfulTemplate-0.0.1-SNAPSHOT.jar &
```