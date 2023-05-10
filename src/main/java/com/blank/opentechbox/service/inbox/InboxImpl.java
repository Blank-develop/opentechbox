package com.blank.opentechbox.service.inbox;

import com.blank.opentechbox.entity.Inbox;
import com.blank.opentechbox.repo.InboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InboxImpl implements InboxService{
    @Autowired
    private InboxRepository inboxRepository;

    @Override
    public void sendInbox(String receiver, String message) {
        Inbox inbox = new Inbox(receiver, message);
        inboxRepository.save(inbox);
    }

    @Override
    public List<Inbox> getInboxes(String username) {
        return inboxRepository.findByReceiverAndIsReadOrderByIdDesc(username, false);
    }

    @Override
    public void markAsRead(Long id) {
        Optional<Inbox> optionalInbox = inboxRepository.findById(id);
        if (optionalInbox.isPresent()) {
            Inbox inbox = optionalInbox.get();
            inbox.setRead(true);
            inbox.setCreateDate(inbox.getCreateDate());
            inboxRepository.save(inbox);
        }
    }

    //promotion

}
