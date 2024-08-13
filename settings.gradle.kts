rootProject.name = "koh"
include("core", "auth", "mail", "datahub", "remote")
project(":core").projectDir = file("core")
project(":auth").projectDir = file("auth")
project(":datahub").projectDir = file("datahub")
