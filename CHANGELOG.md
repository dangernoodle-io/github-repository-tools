## ChangeLog

* **0.6.0-SNAPSHOT**
  - Expose and use shared `OkHttpClient` instance
  - Added 'pre/post' workflow execution hooks

* **0.5.0** 
  - Added `--all` to `repository` command

* **0.4.0**
  - Added `getFullName` that returns name as as `<org>/<repo>`
  - Improved handling/merging of plugin settings
  - Added 'CompositeStatusCheckProvider' and renamed existing related classes (breaking change)
  - Serialize to / return `JsonObject.NULL` instead of 'null' (may be breaking change)
  - Update `github-api` to `1.107`
  - Update `weld-se` to `3.1.3.Final`

* **0.3.1** (06/07/2018)
  - Fix NPE merging 'requireUpToDate'

* **0.3.0** (06/07/2018)
  - Support for setting description / homepage
  - Support for additional repository settings during creation
  - Create 'other' branches / change default branch

* **0.2.2** (05/25/2018)
  - Schema documentation updates, allow `labels`, `teams`, and `collaborators` to be `null`
  - Return an empty collection from the `DefaultStatusCheckFactory` if no contexts are found

* **0.2.1** (05/22/2018)
  - Added `-XX:+TieredCompilation` `-XX:TieredStopAtLevel=1` to container java args
  - Fixed plugin merging 

* **0.2.0** (05/17/2018)
  - Added additional methods to `JsonObject`
  - Refactored `RepositoryMerger` to merge against empty defaults
  - Re-enabled `UserRepositoryIT` 

* **0.1.2** (05/17/2018)
  - Bug fixes

* **0.1.1** (05/16/20178)
  - Fix push to docker hub
  
* **0.1.0** (05/16/2018)
  - Initial Release

