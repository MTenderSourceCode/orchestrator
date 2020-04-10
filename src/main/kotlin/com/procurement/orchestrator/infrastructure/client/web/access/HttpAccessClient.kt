package com.procurement.orchestrator.infrastructure.client.web.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.WebClient
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckAccessToTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckPersonesStructureAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.FindLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction
import com.procurement.orchestrator.infrastructure.configuration.property.ComponentProperties
import java.net.URL

class HttpAccessClient(private val webClient: WebClient, properties: ComponentProperties.Component) :
    AccessClient {

    private val url: URL = URL(properties.url + "/command2")

    override suspend fun getLotIds(
        id: CommandId,
        params: FindLotIdsAction.Params
    ): Result<Reply<FindLotIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.FindLotByIds.build(id = id, params = params),
        target = AccessCommands.FindLotByIds.target
    )

    override suspend fun getLotStateByIds(
        id: CommandId,
        params: GetLotStateByIdsAction.Params
    ): Result<Reply<GetLotStateByIdsAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.GetLotStateByIds.build(id = id, params = params),
        target = AccessCommands.GetLotStateByIds.target
    )

    override suspend fun checkAccessToTender(
        id: CommandId,
        params: CheckAccessToTenderAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckAccessToTender.build(id = id, params = params)
    )

    override suspend fun checkPersonsStructure(
        id: CommandId,
        params: CheckPersonesStructureAction.Params
    ): Result<Reply<Unit>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.CheckPersonesStructure.build(id = id, params = params)
    )

    override suspend fun responderProcessing(
        id: CommandId,
        params: ResponderProcessingAction.Params
    ): Result<Reply<ResponderProcessingAction.Result>, Fail.Incident> = webClient.call(
        url = url,
        command = AccessCommands.ResponderProcessing.build(id = id, params = params),
        target = AccessCommands.ResponderProcessing.target
    )
}
