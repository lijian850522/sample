package sample.repo;

import org.springframework.data.repository.CrudRepository;

import sample.entity.User;

public interface UserRepo extends CrudRepository<User,String>{

}
