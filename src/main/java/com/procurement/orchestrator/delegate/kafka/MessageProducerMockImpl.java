package com.procurement.orchestrator.delegate.kafka;

import com.procurement.orchestrator.domain.Notification;
import com.procurement.orchestrator.domain.chronograph.ScheduleTask;

public class MessageProducerMockImpl implements MessageProducer {

    public boolean sendToChronograph(final ScheduleTask task) {
        return true;
    }

    public boolean sendToPlatform(final Notification notification) {
        return true;
    }
}