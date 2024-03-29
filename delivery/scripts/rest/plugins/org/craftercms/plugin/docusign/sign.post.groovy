@Grapes([
    @Grab(group='io.swagger', module='swagger-annotations', version='1.5.18', initClass=false),
    @Grab(group='jakarta.ws.rs', module='jakarta.ws.rs-api', version='2.1.6', initClass=false),
    @Grab(group='org.glassfish.jersey.media', module='jersey-media-multipart', version='2.29.1', initClass=false),
    @Grab(group='org.glassfish.jersey.media', module='jersey-media-json-jackson', version='2.29.1', initClass=false),
    @Grab(group='org.glassfish.jersey.core', module='jersey-client', version='2.29.1', initClass=false),
    @Grab(group='org.glassfish.jersey.inject', module='jersey-hk2', version='2.26', initClass=false),
    @Grab(group='com.fasterxml.jackson.core', module='jackson-core', version='2.12.1', initClass=false),
    @Grab(group='com.fasterxml.jackson.core', module='jackson-databind', version='2.12.1', initClass=false),
    @Grab(group='com.fasterxml.jackson.dataformat', module='jackson-dataformat-csv', version='2.12.1', initClass=false),
    @Grab(group='com.fasterxml.jackson.datatype', module='jackson-datatype-jsr310', version='2.12.1', initClass=false),
    @Grab(group='org.apache.oltu.oauth2', module='org.apache.oltu.oauth2.client', version='1.0.2', initClass=false),
    @Grab(group='com.auth0', module='java-jwt', version='3.4.1', initClass=false),
    @Grab(group='org.bouncycastle', module='bcprov-jdk15on', version='1.69', initClass=false),
    @Grab(group='com.docusign', module='docusign-esign-java', version='3.23.0', initClass=false)
])

import com.docusign.esign.api.EnvelopesApi
import com.docusign.esign.model.EnvelopeDefinition
import com.docusign.esign.model.EnvelopeSummary

import javax.servlet.http.HttpServletResponse

import plugins.org.craftercms.plugin.docusign.utils.ApiClientHelpers
import plugins.org.craftercms.plugin.docusign.utils.HttpHelpers
import plugins.org.craftercms.plugin.docusign.utils.EnvelopeHelpers
import plugins.org.craftercms.plugin.docusign.commons.WorkArguments
import plugins.org.craftercms.plugin.docusign.services.SigningViaEmailService

def result = [:]
def invalidParams = false
def paramsList = []

def params = HttpHelpers.getPostParams(request)
if (!params) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
    result.message = HttpHelpers.MSG_BODY_EMPTY
    return result
}

def signerEmail = params.signerEmail
def signerName = params.signerName

if (!signerEmail) {
    invalidParams = true
    paramsList += 'signerEmail'
}

if (!signerName) {
    invalidParams = true
    paramsList += 'signerName'
}

if (invalidParams) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
    result.message = 'Invalid parameter(s): ' + paramsList
    return result
}

String basePath = pluginConfig.getString('basePath')
String privateKey = pluginConfig.getString('privateKey')
String integrationKey = pluginConfig.getString('integrationKey')
String userId = pluginConfig.getString('userId')
List<String> scopes = pluginConfig.getList(String.class, 'scopes')
String accessToken = ApiClientHelpers.getOneTimeAccessToken(basePath, privateKey, integrationKey, userId, scopes)

EnvelopesApi envelopesApi = ApiClientHelpers.createEnvelopesApi(basePath, accessToken)

WorkArguments args = new WorkArguments()
args.signerEmail = signerEmail
args.signerName = signerName
args.ccEmail = pluginConfig.getString('ccEmail')
args.ccName = pluginConfig.getString('ccName')
args.status = pluginConfig.getString('status')

EnvelopeDefinition envelope = SigningViaEmailService.makeEnvelope(
    siteItemService,    // CrafterCMS SiteItemService instance
    args.getSignerEmail(),
    args.getSignerName(),
    args.getCcEmail(),
    args.getCcName(),
    args.getStatus(),
    args
)

def accountId = pluginConfig.getString('accountId')
EnvelopeSummary envelopeSummary = SigningViaEmailService.signingViaEmail(
    envelopesApi,
    accountId,
    envelope
)

return envelopeSummary
