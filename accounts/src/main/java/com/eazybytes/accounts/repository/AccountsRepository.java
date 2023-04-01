package com.eazybytes.accounts.repository;

import com.eazybytes.accounts.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eazybytes.accounts.model.Accounts;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {

	Accounts findByCustomerId(int customerId);



}
