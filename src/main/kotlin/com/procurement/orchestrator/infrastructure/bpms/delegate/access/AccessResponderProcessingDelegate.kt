package com.procurement.orchestrator.infrastructure.bpms.delegate.access

import com.procurement.orchestrator.application.CommandId
import com.procurement.orchestrator.application.client.AccessClient
import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAwardIfOnlyOne
import com.procurement.orchestrator.application.model.context.extension.getRequirementResponseIfOnlyOne
import com.procurement.orchestrator.application.model.context.members.Awards
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.failure
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.Cpid
import com.procurement.orchestrator.domain.model.Ocid
import com.procurement.orchestrator.domain.model.identifier.Identifier
import com.procurement.orchestrator.domain.model.person.Person
import com.procurement.orchestrator.domain.model.requirement.response.RequirementResponses
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractExternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import com.procurement.orchestrator.infrastructure.client.reply.Reply
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction
import org.springframework.stereotype.Component

@Component
class AccessResponderProcessingDelegate(
    logger: Logger,
    private val accessClient: AccessClient,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractExternalDelegate<Unit, ResponderProcessingAction.Result>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        commandId: CommandId,
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Reply<ResponderProcessingAction.Result>, Fail.Incident> {

        val responder = buildResponder(context)
            .orForwardFail { fail -> return fail }

        val processInfo = context.processInfo
        val cpid: Cpid = processInfo.cpid
        val ocid: Ocid = processInfo.ocid

        val requestInfo = context.requestInfo
        return accessClient.responderProcessing(
            id = commandId,
            params = ResponderProcessingAction.Params(
                cpid = cpid,
                ocid = ocid,
                date = requestInfo.timestamp,
                responder = responder
            )
        )
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: ResponderProcessingAction.Result
    ): MaybeFail<Fail.Incident> {

        val award = context.getAwardIfOnlyOne()
            .orReturnFail { return MaybeFail.fail(it) }

        val requirementResponse = award.getRequirementResponseIfOnlyOne()
            .orReturnFail { return MaybeFail.fail(it) }

        val updatedRequirementResponse = requirementResponse.copy(
            responder = Person(
                identifier = data.identifier
                    .let { identifier ->
                        Identifier(
                            scheme = identifier.scheme,
                            id = identifier.id
                        )
                    },
                name = data.name
            )
        )
        val updatedAward = award.copy(
            requirementResponses = RequirementResponses(updatedRequirementResponse)
        )
        context.awards = Awards(updatedAward)

        return MaybeFail.none()
    }

    private fun buildResponder(context: CamundaGlobalContext): Result<ResponderProcessingAction.Params.Responder, Fail.Incident> {
        val award = context.getAwardIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val requirementResponse = award.getRequirementResponseIfOnlyOne()
            .orForwardFail { fail -> return fail }

        val responder = requirementResponse.responder
            ?.let { responder ->
                ResponderProcessingAction.Params.Responder(
                    title = responder.title,
                    name = responder.name,
                    identifier = responder.identifier
                        .let { identifier ->
                            ResponderProcessingAction.Params.Responder.Identifier(
                                scheme = identifier.scheme,
                                id = identifier.id,
                                uri = identifier.uri
                            )
                        },
                    businessFunctions = responder.businessFunctions
                        .map { businessFunction ->
                            ResponderProcessingAction.Params.Responder.BusinessFunction(
                                id = businessFunction.id,
                                type = businessFunction.type,
                                jobTitle = businessFunction.jobTitle,
                                period = businessFunction.period
                                    ?.let { period ->
                                        ResponderProcessingAction.Params.Responder.BusinessFunction.Period(
                                            startDate = period.startDate
                                        )
                                    },
                                documents = businessFunction.documents
                                    .map { document ->
                                        ResponderProcessingAction.Params.Responder.BusinessFunction.Document(
                                            documentType = document.documentType,
                                            id = document.id,
                                            title = document.title,
                                            description = document.description
                                        )
                                    }
                            )
                        }
                )
            }
            ?: return failure(
                Fail.Incident.Bpms.Context.Missing(name = "responder", path = "award.requirementResponses")
            )

        return success(responder)
    }
}
