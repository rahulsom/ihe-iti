plugins {
  id("com.gradle.develocity").version("4.2")
}

develocity {
  buildScan {
    termsOfUseUrl.set("https://gradle.com/terms-of-service")
    termsOfUseAgree.set("yes")
  }
}

rootProject.name = "ihe-iti"
