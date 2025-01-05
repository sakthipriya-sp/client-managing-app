package com.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crud.model.Client;


@Repository
public interface ClientRepo extends JpaRepository<Client,Integer>{

	public Client findByEmail(String email);
}
