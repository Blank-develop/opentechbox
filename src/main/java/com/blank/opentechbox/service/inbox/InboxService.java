package com.blank.opentechbox.service.inbox;

import com.blank.opentechbox.entity.Inbox;

import java.util.List;

public interface InboxService {
    void sendInbox(String receiver, String message);
    List<Inbox> getInboxes(String username);
    void markAsRead(Long id);
}
