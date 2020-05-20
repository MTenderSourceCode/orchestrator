package com.procurement.orchestrator.infrastructure.bpms.delegate.mapping

import com.procurement.orchestrator.domain.model.tender.Tender
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.DelegateVariableMapping
import org.camunda.bpm.engine.delegate.VariableScope
import org.camunda.bpm.engine.variable.VariableMap
import org.springframework.stereotype.Component

@Component
class UpdateMSMappingContextDelegate : DelegateVariableMapping {

    companion object {
        private const val VARIABLE_NAME = "tender"
    }

    override fun mapInputVariables(superExecution: DelegateExecution, subVariables: VariableMap) {
        val tender = superExecution.getVariable(VARIABLE_NAME) as Tender
        val subTender = Tender(status = tender.status, statusDetails = tender.statusDetails)
        subVariables.putValue(VARIABLE_NAME, subTender)
    }

    override fun mapOutputVariables(superExecution: DelegateExecution, subInstance: VariableScope) {
    }
}
