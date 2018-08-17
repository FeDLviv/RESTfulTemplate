# API

* Compile application (example):

```sh
./gradlew build
```

* Compile application without test (example):

```sh
./gradlew build -x test
```

* Deploying applications to Heroku (example):

```sh
./gradlew build deployHeroku
```

* Run application (example):

```sh
java -jar RESTfulTemplate-0.0.1-SNAPSHOT.jar &
```

* Profiles (modes):
    * local (default)
    * test
    * dev
    * prod
    
* Open in browser (example):

    * http://localhost:8080

* Read documentation (example):

    * http://localhost:8080/swagger-ui.html
    
* Generate a self-signed SSL certificate (example):

```sh
keytool -genkey -alias tomcat -keyalg RSA -keysize 2048 -keystore KeyStore.jks -validity 3650
```

* Generate SSL certificate with Let’s Encrypt (example):

```sh
sudo certbot certonly --standalone -d DOMAIN
```

* Generate PKCS12 files from PEM files(Let’s Encrypt) (example):

```sh
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name tomcat -CAfile chain.pem -caname root
```
    
* Run application with redirect HTTP(80) to HTTPS(443) (example):

```sh
java -Dserver.port=443 -Dmanagement.server.port=-1 -Dserver.ssl.key-store=PATH -Dserver.ssl.key-store-password=PASSWORD -Dserver.ssl.key-store-type=TYPE -Dserver.ssl.key-alias=ALIAS -jar RESTfulTemplate-0.0.1-SNAPSHOT.jar &
```
