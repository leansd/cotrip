package cn.leansd.cotrip.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.jupiter.api.Test;

class NoCircularDependencyTest {

    @Test
    void detectCircularDependencies() {
        JavaClasses importedClasses = new ClassFileImporter()
                .importPackages("cn.leansd.cotrip");

        ArchRule rule = SlicesRuleDefinition.slices()
                .matching("cn.leansd.cotrip.(*)..")
                .should().beFreeOfCycles()
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }
}

