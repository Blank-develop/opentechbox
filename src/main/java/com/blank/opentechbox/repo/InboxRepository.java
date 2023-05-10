package com.blank.opentechbox.repo;

import com.blank.opentechbox.entity.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, Long> {

    List<Inbox> findByReceiverAndIsReadOrderByIdDesc(String username, boolean b);
}
