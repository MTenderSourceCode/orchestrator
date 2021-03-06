package com.procurement.orchestrator.infrastructure.bpms.delegate.bpe

import com.procurement.orchestrator.application.model.context.CamundaGlobalContext
import com.procurement.orchestrator.application.model.context.extension.getAmendmentsIfNotEmpty
import com.procurement.orchestrator.application.model.context.extension.tryGetTender
import com.procurement.orchestrator.application.service.Logger
import com.procurement.orchestrator.application.service.Transform
import com.procurement.orchestrator.domain.fail.Fail
import com.procurement.orchestrator.domain.functional.MaybeFail
import com.procurement.orchestrator.domain.functional.Option
import com.procurement.orchestrator.domain.functional.Result
import com.procurement.orchestrator.domain.functional.Result.Companion.success
import com.procurement.orchestrator.domain.model.amendment.AmendmentId
import com.procurement.orchestrator.domain.model.amendment.Amendments
import com.procurement.orchestrator.infrastructure.bpms.delegate.AbstractInternalDelegate
import com.procurement.orchestrator.infrastructure.bpms.delegate.ParameterContainer
import com.procurement.orchestrator.infrastructure.bpms.repository.OperationStepRepository
import org.springframework.stereotype.Component

@Component
class BpeCreateIdsForAmendmentDelegate(
    logger: Logger,
    operationStepRepository: OperationStepRepository,
    transform: Transform
) : AbstractInternalDelegate<Unit, Unit>(
    logger = logger,
    transform = transform,
    operationStepRepository = operationStepRepository
) {

    override fun parameters(parameterContainer: ParameterContainer): Result<Unit, Fail.Incident.Bpmn.Parameter> =
        success(Unit)

    override suspend fun execute(
        context: CamundaGlobalContext,
        parameters: Unit
    ): Result<Option<Unit>, Fail.Incident> {
        val tender = context.tryGetTender()
            .orForwardFail { fail -> return fail }

        val amendments = tender.getAmendmentsIfNotEmpty()
            .orForwardFail { fail -> return fail }

        context.tender = tender.copy(
            amendments = amendments
                .map { amendment ->
                    amendment.copy(
                        id = AmendmentId.generate()
                    )
                }
                .let { Amendments(it) }
        )

        return success(Option.none())
    }

    override fun updateGlobalContext(
        context: CamundaGlobalContext,
        parameters: Unit,
        data: Unit
    ): MaybeFail<Fail.Incident.Bpmn> = MaybeFail.none()
}
