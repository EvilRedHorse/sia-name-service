# Sia Name Service

Sia Name Service is a Java Gradle project that provides a process to register, update, and lookup `[name].sns` hosts on the Sia blockchain.

## Dependencies

Must have the Sia wallet running, synced, and unlocked.

##  How to run

You can compile the gradle project and then run it on your own or you can open the project in a Gradle-enabled Java IDE (I use IntelliJ IDEA) and run `com.georgemcarlson.sianameservice.Main.java`.

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

Update a host by posting the `[name].sns` host that you wish to register with the skylink that it should point at and the original registrant address that was used and a fee at least equal to the original fee.

Note: You can update someone else's fee but since they should own the registrant address they will get the fee and they can then just update the host again for next to nothing because they will be sending the fee to themselves. Also note that if you supply a larger fee than last time this becomes the new minimum fee to update in the future.

Example Request:

```
http://localhost:8080/register
?host=test.sns
&skylink=CAA2XstfpNmrh5WrI3ThcBQKtsTFTEtmrGq--7eeKAlO9Q
&registrant=8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308
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
