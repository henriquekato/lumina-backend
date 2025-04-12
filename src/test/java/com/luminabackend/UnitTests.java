package com.luminabackend;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages({"com.luminabackend.services"})
@SuiteDisplayName("Unit tests")
@IncludeTags({"UnitTest"})
public class UnitTests {
}
