package patches.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.ui.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the vcsRoot with id = 'ReleasesVcsRoot'
accordingly, and delete the patch script.
*/
changeVcsRoot(RelativeId("ReleasesVcsRoot")) {
    val expected = GitVcsRoot({
        id("ReleasesVcsRoot")
        name = "IdeaVim Releases"
        url = "git@github.com:JetBrains/ideavim.git"
        branchSpec = "+:refs/head/releases/*"
        authMethod = uploadedKey {
            uploadedKey = "IdeaVim ssh keys"
        }
    })

    check(this == expected) {
        "Unexpected VCS root settings"
    }

    (this as GitVcsRoot).apply {
        branch = "refs/heads/master"
        branchSpec = "+:*"
    }

}