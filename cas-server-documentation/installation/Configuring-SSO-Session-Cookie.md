---
layout: default
title: CAS - Configuring SSO Session Cookie
---

# SSO Session Cookie
A ticket-granting cookie is an HTTP cookie set by CAS upon the establishment of a single sign-on session. 
This cookie maintains login state for the client, and while it is valid, the client can present it to CAS in lieu of primary credentials. 
Services can opt out of single sign-on through the `renew` parameter. See the [CAS Protocol](../protocol/CAS-Protocol.html) for more info.

The cookie value is linked to the active ticket-granting ticket, the remote IP address that initiated the request
as well as the user agent that submitted the request. The final cookie value is then encrypted and signed
using `AES_128_CBC_HMAC_SHA_256` and `HMAC_SHA512` respectively.

The secret keys are defined in the `cas.properties` file. These keys **MUST** be regenerated per your specific environment. Each key
is a JSON Web Token with a defined length per the algorithm used for encryption and signing.
You may [use the following tool](https://github.com/mitreid-connect/json-web-key-generator)
to generate your own JSON Web Tokens.

## Configuration

The generation of the ticket-granting cookie is controlled via:

```properties
# The encryption secret key. By default, must be a octet string of size 256.
# tgc.encryption.key=

# The signing secret key. By default, must be a octet string of size 512.
# tgc.signing.key=

# Decides whether SSO cookie should be created only under secure connections.
# tgc.secure=true

# The expiration value of the SSO cookie
# tgc.maxAge=-1

# The name of the SSO cookie
# tgc.name=TGC

# The path to which the SSO cookie will be scoped
# tgc.path=/cas

# Decides whether SSO Warning cookie should be created only under secure connections.
# warn.cookie.secure=true

# The expiration value of the SSO Warning cookie
# warn.cookie.maxAge=-1
```

The cookie has the following properties:

1. It is marked as secure.
2. Depending on container support, the cookie would be marked as http-only automatically.
3. The cookie value is encrypted and signed via secret keys that need to be generated upon deployment.

## Cookie Generation for Renewed Authentications

By default, forced authentication requests that challenge the user for credentials
either via the [`renew` request parameter](../protocol/CAS-Protocol.html)
or via [the service-specific setting](Service-Management.html) of
the CAS service registry will always generate the ticket-granting cookie
nonetheless. What this means is, logging in to a non-SSO-participating application
via CAS nonetheless creates a valid CAS single sign-on session that will be honored on a
subsequent attempt to authenticate to a SSO-participating application.

Plausibly, a CAS adopter may want this behavior to be different, such that logging in to a non-SSO-participating application
via CAS either does not create a CAS SSO session and the SSO session it creates is not honored for authenticating subsequently
to an SSO-participating application. This might better match user expectations.

The controlling of this behavior is done via the `cas.properties` file:

{% highlight properties %}
##
# Single Sign-On Session
#
# Indicates whether an SSO session should be created for renewed authentication requests.
# create.sso.renewed.authn=true

{% endhighlight %}

# SSO Warning Session Cookie
A warning cookie set by CAS upon the establishment of the SSO session at the request of the user on the CAS login page. The cookie is used later to warn and prompt
the user before a service ticket is generated and access to the service application is granted.
The cookie is controlled via:

{% highlight properties %}
# Decides whether SSO Warning cookie should be created only under secure connections.
# warn.cookie.secure=true

# The expiration value of the SSO Warning cookie
# warn.cookie.maxAge=-1

# The name of the SSO Warning cookie
# warn.cookie.name=CASPRIVACY

# The path to which the SSO Warning cookie will be scoped
# warn.cookie.path=/cas

{% endhighlight %}
