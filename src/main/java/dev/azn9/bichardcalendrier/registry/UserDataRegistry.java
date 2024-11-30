package dev.azn9.bichardcalendrier.registry;

import dev.azn9.bichardcalendrier.data.UserData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRegistry extends CrudRepository<UserData, Long> {

    UserData findByUserId(Long userId);

    UserData findByThreadId(Long threadId);

}
