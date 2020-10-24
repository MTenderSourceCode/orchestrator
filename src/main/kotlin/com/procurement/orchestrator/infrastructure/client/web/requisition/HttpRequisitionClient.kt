package com.procurement.orchestrator.infrastructure.client.web.requisition

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.RequisitionClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CreatePcrAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.CheckTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.requisition.action.ValidatePcrDataAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpRequisitionClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    RequisitionClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun validatePcrData(
        id: CommandId,
        params: ValidatePcrDataAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.ValidatePcrData.build(id = id, params = params)
    )

    override suspend fun createPcr(
        id: CommandId,
        params: CreatePcrAction.Params
    ): Result<Reply<CreatePcrAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.CreatePcr.build(id = id, params = params),
        target = RequisitionCommands.CreatePcr.target
    )

    override suspend fun checkTenderState(
        id: CommandId,
        params: CheckTenderStateAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = RequisitionCommands.CheckTenderState.build(id = id, params = params)
    )
}
