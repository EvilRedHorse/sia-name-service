# Sia Name Service

Sia Name Service is a Java Gradle project that provides the webportal software necessary to register, update, and lookup `[name].sns` host entries on the Sia blockchain.

The best way to describe Sia Name Service is as a decentralized URL shortening service and a link management platform for Skynet's Skylinks.

What happens is that when you assign an `.sns` domain to a Skylink you automatically have a shortened URL in the form of `http[s]://[sia-name-service portal]/[domain-name].sns` (for example: http://sns.hopto.me/bigbuckbunny.sns). Additionally, if you install the browser extension you can access `.sns` domains through the browser's address bar (for example: http://bigbuckbunny.sns/ would resolve). Thirdly, the service also lets you supply a `Host: [domain].sns` header in a request to the service. This should allow someone to point their router at a personal DNS resolver that rewrites all `.sns` domains to a defined Sia Name Service portal that will then resolve the request natively (for example: http://bigbuckbunny.sns/ would resolve without needing a browser extension). Lastly, the Sia Name Service portal provides a "GO" input field that will redirect you to the Skylink associated with the input field's `.sns` domain. 

One of Sia Name Service's secondary missions is to provide an out-of-the-box Skynet portal. The idea here is to provide a Jetty server alternative to Nebulous's official Skynet webportal that you can just double click on to start up. This will allow you to run a Skynet portal without needing to set up an nginx webserver on your computer or fiddling with router configurations. 

A latent purpose is to provide an alternative to Dapp Dapp Go's excellent Skylink search engine. This is achieved by providing the /list and the /whois/bigbuckbunny.sns API endpoints. The long term goal here is to someday add a /search endpoint.

## Download
[Precompiled Sia Name Service releases](https://github.com/geo-gs/sia-name-service/releases)

## Sia Name Service Webportals
Sia name service webportals can be found at:
* [sns.hopto.me](http://sns.hopto.me)
* [sns.redirectme.net](http://sns.redirectme.net)

Please contact me if you have a sia name service webportal that you would like referenced.

## Dependencies

Must have a Sia wallet that is running to be able to locate new host entries. Must have a Sia wallet that is synced and unlocked to be able to register or update host entries.

## How to configure

Sia Name Service uses a settings.json file for its configurations. In this file you can set the web server's port, the registration fee, the wallets port, the wallets API password, the TLDs to scan for, and the skynet portals to use.

##  How to run

You can run the precompiled jar or a self-compiled jar on your own or you can open the project in a Gradle-enabled Java IDE (I use IntelliJ IDEA) and run `com.georgemcarlson.sianameservice.Main.java`.

## How to build

Have gradle installed on your computer. Then, in your terminal, navigate to the projects root and run `gradle clean jar`. The compiled jar will be in the projects `./build/libs/`. Note: You may also need to run chmod +x sia-name-service-[x.x.x].jar to make the compiled jar executable.

## Register a host

Register a host by posting the `[name].sns` host that you wish to register with the skylink that it should point at and a registrant address and a fee to the register endpoint and is configured in the `settings.json` file. The fee is how many siacoins to the power of ten that will be needed to register or update the linked skylink. Registration is turned off if the fee is set to less than one. The registrant address should be an address that you control.

Example Request:
```
http://localhost:8080/register
?host=test.sns
&skylink=_ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ
&registrant=8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308
```

Example Response:
```
{
  "skylink": "_ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ",
  "fee": "5",
  "host": "test.sns",
  "registrant": "8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308",
  "block": 250550,
  "seconds": 143
}
```

## Update a host

Update a host by posting the `[name].sns` host that you wish to update with the skylink that it should point at.

Note: You can update someone else's fee but since they should own the registrant address they will get the fee and they can then just update the host again for next to nothing because they will be sending the fee to themselves. Also note that if the server is configures to use a larger fee than last time this larger becomes the new minimum fee to update in the future.

Example Request:

```
http://localhost:8080/update
?host=test.sns
&skylink=CAA2XstfpNmrh5WrI3ThcBQKtsTFTEtmrGq--7eeKAlO9Q
```

Example Response:
```
{
  "skylink": "CAA2XstfpNmrh5WrI3ThcBQKtsTFTEtmrGq--7eeKAlO9Q",
  "fee": "6",
  "host": "test.sns",
  "registrant": "8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308",
  "block": 250550,
  "seconds": 143
}
```

## Look up a host

You look up the meta-data registered to a `[name].sns` host with the whois endpoint.

Example Request:
```
http://localhost:8080/whois/test.sns
```

Example Response:
```
{
  "skylink": "_ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ",
  "fee": 5,
  "host": "test.sns",
  "registrant": "8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308",
  "block": 250550,
  "seconds": 143
}
```

## Redirect to skylink

You can automatically redirect to the skylink registered to a `[name].sns` host by:

### Supplying the `[name].sns` host as the context:
Example Request:
```
http://localhost:8080/test.sns
```

Example Response:
```
302 Found; Location https://[portal]/[skylink]
```

### Using the redirect endpoint:
Example Request:
```
http://localhost:8080/redirect?host=test.sns
```

Example Response:
```
302 Found; Location https://[portal]/[skylink]
```

### Configuring your DNS resolver to map `*.sns` TLDs to a Sia-Name_Service server IP:
Example Request:
```
http://test.sns
```

Example Response:
```
302 Found; Location https://[portal]/[skylink]
```

### Install and configure a DNS redirector browser extension:
There are many browser extensions that can be installed to add native `*.sns` TLD support to a browser's address bar.

One that I have tested is called Redirector and exists for both [Firefox](https://addons.mozilla.org/en-US/firefox/addon/redirector/)  and [Chrome](https://chrome.google.com/webstore/detail/redirector/ocgpenflpmgnfapjedencafcfakcekcd).

Additionally, Sia-Name-Service includes an endpoint to automatically generate the necessary config file for this plugin. See the **Generate Redirector config** documentation section.

Example:
```
http://test.sns
```

Example Response:
```
302 Found; Location https://[portal]/[skylink]
```

## Generate Redirector configuration file

You can generate the configuration needed to set up the Redirector browser extension to natively support the `.sns` TLD through the redirector endpoint.

Example Request:
```
http://localhost:8080/redirector
```

Example Response:

[sia-name-service-redirector-config.json](/sia-name-service-redirector-config.json)

## List registered hosts

You can list 50 known registered hosts using the list endpoint and the offset parameter.

Example Request:
```
http://localhost:8080/list?offset=50
```

Example Response:
```
["test.sns"]
```

## Scanner status

You can view the latest block that the scanner has scanned by using the scanner endpoint.

Example Request:
```
http://localhost:8080/scanner
```

Example Response:
```
{
  "block": 260543,
  "id": 0
}
```

## Upload Skyfile to Skynet

Upload to skynet by making a multipart post to `/skynet/skyfile` as documented 
<a href='https://sia.tech/docs/#skynet-skyfile-siapath-post'>here</a>.

## Retrieve Skylink from Skynet

You can retrieve data from skynet by making a get request to either `/[skylink]` or 
`/skymet/skylink/[skylink]` as documented 
<a href='https://sia.tech/docs/#skynet-skylink-skylink-get'>here</a>.

## Retrieve Skylink HEAD from Skynet

You can retrieve HEAD metadata from skynet by making a head request to either `/[skylink]` or 
`/skymet/skylink/[skylink]` as documented 
<a href='https://sia.tech/docs/#skynet-skylink-skylink-head'>here</a>.