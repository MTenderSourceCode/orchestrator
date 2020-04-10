package com.procurement.orchestrator.infrastructure.client.web.revision

import com.procurement.orchestrator.infrastructure.client.web.revision.action.CheckAccessToAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.CreateAmendmentAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.DataValidationAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.FindAmendmentIdsAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.GetMainPartOfAmendmentByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.SetStateForAmendmentAction

object RevisionCommands {

    object CheckAccessToAmendment : CheckAccessToAmendmentAction()

    object CreateAmendment : CreateAmendmentAction()

    object DataValidation : DataValidationAction()

    object FindAmendmentIds : FindAmendmentIdsAction()

    object GetMainPartOfAmendmentByIds : GetMainPartOfAmendmentByIdsAction()

    object SetStateForAmendment : SetStateForAmendmentAction()
}
