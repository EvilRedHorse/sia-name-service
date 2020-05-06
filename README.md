# Sia Name Service

A way to register, update, and lookup `[name].sia` hosts on the Sia blockchain.

## Dependencies

Must have the Sia wallet running, synced, and unlocked.

## Register a host

Register a host by posting the `[name].sia` host that you wish to register with the skylink that it should point at and a registrant address and a fee to the register endpoint. The fee is how many siacoins to the power of ten that will be needed to register or update the linked skylink. The registrant address should be an address that you control.

Example Request:
```
http://localhost:8080/register
?host=test.sia
&skylink=_ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ
&registrant=8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308
&fee=5
```

Example Response:
```
{
  "skylink": "_ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ",
  "fee": "5",
  "host": "test.sia",
  "registrant": "8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308"
}
```

## Update a host

Update a host by posting the `[name].sia` host that you wish to register with the skylink that it should point at and the original registrant address that was used and a fee at least equal to the original fee.

Note: You can update someone elses fee but since they should own the registrant address they will get the fee and they can then just update the host again for next to nothing because they will be sending the fee to themselves. Also note that if you supply a larger fee than last time this becomes the new minimum fee to update in the future.

Example Request:

```
http://localhost:8080/register
?host=test.sia
&skylink=CAA2XstfpNmrh5WrI3ThcBQKtsTFTEtmrGq--7eeKAlO9Q
&registrant=8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308
&fee=6
```

Example Response:
```
{
  "skylink": "CAA2XstfpNmrh5WrI3ThcBQKtsTFTEtmrGq--7eeKAlO9Q",
  "fee": "6",
  "host": "test.sia",
  "registrant": "8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308"
}
```

## Look up a host

You look up a host by posting the `[name].sia` host that you wish to look up to the hosts endpoint.

Example Request:
```
http://localhost:8080/hosts?host=test.sia
```

Example Response:
```
{
  "skylink": "_ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ",
  "fee": 5,
  "host": "test.sia",
  "registrant": "8d2e801ffcec48cd7276652c5871332592975942af1c7e4964c77be8b01f80a4dcae15d1a308"
}
```
