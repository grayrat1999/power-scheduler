package tech.powerscheduler.server.domain.appgroup

data class AppGroupKey(
    val namespaceCode: String,
    val appCode: String,
) {
    constructor(appGroup: AppGroup) : this(
        namespaceCode = appGroup.namespace!!.code!!,
        appCode = appGroup.code!!,
    )
}