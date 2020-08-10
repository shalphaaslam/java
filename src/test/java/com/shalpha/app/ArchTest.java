package com.shalpha.app;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.shalpha.app");

        noClasses()
            .that()
                .resideInAnyPackage("com.shalpha.app.service..")
            .or()
                .resideInAnyPackage("com.shalpha.app.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.shalpha.app.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
