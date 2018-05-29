## ChangeLog

* **0.3.0-SNAPSHOT**
  - Support for setting description / homepage

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

