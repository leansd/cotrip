package cn.leansd.cotrip.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

class LayerDependencyTest {
    @Test
    void enforceLayeredArchitecture() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("cn.leansd.cotrip");

        // 领域层不应该依赖任何其他层
        ArchRule domainShouldNotAccessApplicationOrController = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application..", "..controller..");

        // 应用层不应该依赖控制器层
        ArchRule applicationShouldNotAccessController = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..controller..");

        domainShouldNotAccessApplicationOrController.check(importedClasses);
        applicationShouldNotAccessController.check(importedClasses);
    }
}

