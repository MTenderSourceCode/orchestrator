package com.procurement.orchestrator.infrastructure.client.web.access

import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckAccessToTenderAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.CheckPersonsStructureAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetLotStateByIdsAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.GetTenderStateAction
import com.procurement.orchestrator.infrastructure.client.web.access.action.ResponderProcessingAction
import com.procurement.orchestrator.infrastructure.client.web.revision.action.SetStateForTenderAction

object AccessCommands {

    object CheckAccessToTender : CheckAccessToTenderAction()

    object CheckPersonsStructure : CheckPersonsStructureAction()

    object GetLotByIds : GetLotIdsAction()

    object GetLotStateByIds : GetLotStateByIdsAction()

    object ResponderProcessing : ResponderProcessingAction()

    object SetStateForTender : SetStateForTenderAction()

    object GetTenderState : GetTenderStateAction()
}
