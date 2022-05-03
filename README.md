# JFokus 2022 demo: "User-friendly, phishing-proof 2FA with WebAuthn"

This project was used as a demo for the presentation
"User-friendly, phishing-proof 2FA with WebAuthn" at [Jfokus 2022][jfokus].

This is a simple application illustrating how to use the [WebAuthn API][webauthn]
in a [React][react] application
with a Java backend using Yubico's [`java-webauthn-server`][jws] library.

This repository is organized into a series of commits illustrating a sequence of steps
adding individual WebAuthn features one at a time.


## Dependencies

- [Java][java] version 11 or later
- [docker-compose][docker-compose]
- [npm][npm]


## Usage

Start out by checking out the `step0` branch:

```
$ git checkout step0
```

Start the database container using `docker-compose`:

```
$ docker-compose up --detach
```

Then to start the application, run the [Gradle wrapper][gradlew] in the repository root:

```
$ ./gradlew run
```

Now you can work through the app stages by checking out the various "step" branches, for example:

```
$ git checkout step1-webauthn
```

Some steps include database migrations,
which may cause conflicts when going from later steps to earlier ones.
To resolve this, use Flyway to clear and reset the database:

```
$ ./gradlew flywayClean flywayMigrate
```


## Application stages

As described above,
you can use the Git history to explore the development effort to implement WebAuthn features in stages:

 1. `step1-webauthn`: Basic WebAuthn support.

    To introduce support for WebAuthn we first add two libraries to our dependencies:
    `com.yubico:webauthn-server-core` to help with the server side
    and `@github/webauthn-json` to help with the client side.
    We then need to add some front-end views,
    some database access logic,
    wire up the HTTP API for the WebAuthn challenge-response flow
    and tie the WebAuthn registration and authentication flows into our account lifecycle.

 2. `step2-delete-credentials`: Ability to delete WebAuthn credentials.

    Our users should be able to register more than one credential,
    and delete credentials they no longer want to use,
    so here we add the ability to delete credentials.
    This is all our own application logic, and does not tie into the WebAuthn API at all.

 3. `step3-nicknames`: Credential nicknames.

    It's helpful if our users can set nicknames for their credentials,
    to make credential management easier.
    This again is just our own application logic.
    We need another database column and some new UI widgets for the nickname input,
    but this is orthogonal to the WebAuthn API.

 4. `step4-record-passwordless`: Track passwordless capability of credentials.

    The "passwordless" user experience builds upon two WebAuthn features:
    User Verification (UV) for multi-factor authentication,
    and Discoverable Keys (a.k.a. Resident Keys) to skip the username input step.
    Here we add two Boolean properties to our credentials table
    to track which credentials are capable of this.
    This will help when we introduce passwordless login in the future.

    We use the `java-webauthn-server` library to help detect discoverable credentials,
    because some authenticators create discoverable credentials
    even if the server doesn't actively ask for it.
    We check the UV authenticator data flag to see if the credential is used with UV,
    and if we ever see a credential use UV,
    we record that the credential is capable of it.

    We can then say that a credential is passwordless-capable
    if it is both discoverable and UV capable.

 5. `step5-register-passwordless`: Enable registering passwordless credentials.

    Here we introduce the option to make new credentials passwordless-capable.
    Although UV can be enabled at any time if the authenticator supports it,
    an existing credential cannot be made a Discoverable Key
    if it wasn't made so when created.
    Even though we haven't implemented passwordless login yet,
    we can allow our users to register passwordless-capable credential
    in preparation for when we do implement it.

    We need another option in our backend API to enable the passwordless features.
    During credential registration,
    we set the `userVerification` and `residentKey` both to `"required"`,
    which will direct and authenticator to enable these features
    and direct the client to help the user choose a compatible authenticator.

 6. `step6-login-passwordless`: Enable passwordless login.

    Finally, we'll actually implement passwordless login.
    With the previous two preparatory steps already in place,
    there's not much more we need to do.
    We need another backend API to initiate a login operation
    without first specifying a username,
    but after that we can use mostly the same existing backend
    to finish the login operation.
    The `java-webauthn-server` library helps map the user handle in the response
    to a user ID in our application.
    In our `startAuthenticationPasswordless()` method,
    we specify the parameter `.userVerification(UserVerificationRequirement.REQUIRED)`,
    which will make the library verify that the UV flag
    is set in the response from the authenticator.

 7. `step7-attestation`: Collect authenticator attestation.

    Finally, we'll request and store authenticator attestation for new credentials.
    We won't require it and we won't really do much with it for now,
    but it gives us the option in the future to, for example,
    warn affected users if vulnerabilities are discovered in their authenticators.

    If our application had particular security requirements -
    for example, we might have legal requirements on our security -
    we could use attestation to ensure that our users only use approved hardware.
    This would require much additional work in curating
    a set of approved attestation certificates,
    possibly linking them to certification levels, etc.
    To learn more, see [Yubico's developer guide on attestation][devyco].


[devyco]: https://developers.yubico.com/WebAuthn/WebAuthn_Developer_Guide/Attestation.html
[docker-compose]: https://docs.docker.com/compose/
[gradlew]: https://docs.gradle.org/7.4.2/userguide/gradle_wrapper.html
[java]: https://www.java.com/
[jfokus]: https://jfokus.se
[jws]: https://github.com/Yubico/java-webauthn-server
[npm]: https://www.npmjs.com/
[react]: https://reactjs.org/
[webauthn]: https://www.w3.org/TR/2021/REC-webauthn-2-20210408/
