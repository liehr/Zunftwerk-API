package com.zunftwerk.app.zunftwerkapi.repository;

import com.zunftwerk.app.zunftwerkapi.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByModuleName(String moduleName);
}
