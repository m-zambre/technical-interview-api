package com.ing.be.tia.repository;

 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ing.be.tia.model.Lists;

@Repository
public interface ListRepository extends JpaRepository<Lists, Long> {

	void save(List<Lists> lists);

}
