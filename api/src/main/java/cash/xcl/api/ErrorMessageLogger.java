package cash.xcl.api;

import cash.xcl.api.dto.ApplicationMessageEvent;
import cash.xcl.api.dto.CommandFailedEvent;

public interface ErrorMessageLogger {
    void applicationMessageEvent(ApplicationMessageEvent applicationMessageEvent);

    void commandFailedEvent(CommandFailedEvent commandFailedEvent);

}
