package plugins.org.craftercms.plugin.docusign.form

interface FormHandler {

    def handle(params, request, pluginConfig, siteItemService)

}