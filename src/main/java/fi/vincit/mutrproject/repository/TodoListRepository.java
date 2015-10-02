package fi.vincit.mutrproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fi.vincit.mutrproject.domain.TodoList;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long> {
}
