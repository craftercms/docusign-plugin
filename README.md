# CrafterCMS DocuSign Plugin

A DocuSign plugin which uses [eSignature REST API](https://developers.docusign.com/docs/esign-rest-api/esign101/) to sign contract via email using [JWT token](https://developers.docusign.com/platform/auth/jwt/jwt-get-token/).

# Installation

1. Install the plugin via Studio's Plugin Management UI under `Site Tools` > `Plugin Management`

    You will need the following information:

    * basePath: DocuSign API base path. Example: `https://demo.docusign.net/restapi`
    * authDomain: DocuSign authentication domain. Example: `account-d.docusign.com`
    * accountId: DocuSign Account ID. Example: `${enc:CCE-V1#heFjVbXwK...}`
    * userId: DocuSign User ID. Example: `${enc:CCE-V1#2FO+0dhgP...}`
    * integrationKey: DocuSign Integration Key. Example: `${enc:CCE-V1#sYOpw...}`
    * scopes: list of scrope to consent. Example: `signature, impersonation`
    * ccEmail: Email address of CC. Example: `email@example.com`
    * ccName: Name of CC. Example: `Foo Bar`
    * status: Request that the envelope be sent by setting |status| to "sent". To request that the envelope be created as a draft, set to "created". Example: `sent`
    * publicKey: The public key of your DocuSign application. Example: `${enc:CCE-V1#tMoh1Qs...}`
    * privateKey: The private key of your DocuSign application. Example: `${enc:CCE-V1#mkt3jYPh...}`

    *Note: Values starting with `${enc:...}` are encrypted text using [CrafterCMS Encryption Tool](https://docs.craftercms.org/en/4.0/system-administrators/activities/authoring/main-menu-encryption-tool.html#encryption-tool)*

    OR You can also install this plugin by cloning this repository and using the Studio API.

    1a. Create a Studio JWT Token.

    1b. Execute the following CURL command a terminal

    ```bash
    curl --location --request POST 'http://SERVER_AND_PORT/studio/api/2/marketplace/copy' \
    --header 'Authorization: Bearer THE_JWT_TOKEN_FOR_STUDIO' \
    --header 'Content-Type: application/json' \
    --data-raw '{
      "siteId": "YOUR-PROJECT-ID",
      "path": "THE_ABSOLUTEL_FILE_SYSTEM_PATH_TO_THIS_REPO",
      "parameters": { }
    }
    ```

    OR You can aslo install this plugin by cloning this repository and using [Crafter CLI Commands](https://docs.craftercms.org/en/4.0/new-ia/reference/devcontentops-toolkit/copy-plugin.html)

    ```bash
    ./crafter-cli copy-plugin -e local -s PROJECT_ID --path /PLUGIN_PATH/docusign-plugin \
        --param basePath='https://demo.docusign.net/restapi' \
        --param authDomain='account-d.docusign.com' \
        --param accountId='${enc:CCE-V1#2IoYj...}' \
        --param userId='${enc:CCE-V1#xMoWy...}' \
        --param integrationKey='${enc:CCE-V1#zNu3e...}' \
        --param scopes='signature, impersonation' \
        --param ccEmail='email@example.com' \
        --param ccName='Foo Bar' \
        --param status=sent \
        --param publicKey='${enc:CCE-V1#ehP7Wf...}' \
        --param privateKey='${enc:CCE-V1#FS0D...}'
    ```

3. Request application consent

Refer on [How to get an access token with JWT Grant](https://developers.docusign.com/platform/auth/jwt/jwt-get-token/) / Step 1. Request application consent

A sample request to get consent for individual application:

```curl
https://account-d.docusign.com/oauth/auth?response_type=code&scope=impersonation%20signature%20&client_id=YOUR_INTEGRATION_KEY&redirect_uri=https://developers.docusign.com/platform/auth/consent
```

4. Using built-in REST API

```curl
curl --location --request POST 'http://localhost:8080/api/plugins/org/craftercms/plugin/docusign/sign.json?crafterSite=YOUR_PROJECT_ID' \
--header 'Content-Type: application/json' \
--data-raw '{
    "signerEmail": "SIGNER_EMAIL_ADDRESS",
    "signerName": "SIGNER_NAME"
}'
```

An email with title *Please sign this document* will be sent to SIGNER_EMAIL_ADDRESS to request digital sign.

5. Alternatively, create a sample form to request the same

* Create a form with `/component/plugins/org/craftercms/plugin/docusign/docusign-form` content type.

![docusign_form](/docusign_form.png)

* Create a page with `/page/plugins/org/craftercms/plugin/docusign/docusign-page` content type to display the DocuSign form in previous step.

![docusign_page](/docusign_page.png)

* Sample page available and you can input signer email and name to sign contract via email.

![sample_page](/sample_page.png)
