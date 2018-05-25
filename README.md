# github-repository-tools

[![Maven Central](https://img.shields.io/maven-central/v/io.dangernoodle/github-repository-tools.svg)](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22github-repository-tools%22)
[![Build Status](https://travis-ci.com/dangernoodle-io/github-repository-tools.svg?branch=master)](https://travis-ci.com/dangernoodle-io/github-repository-tools)
[![Coverage Status](https://coveralls.io/repos/github/dangernoodle-io/github-repository-tools/badge.svg?branch=master)](https://coveralls.io/github/dangernoodle-io/github-repository-tools)

This utility was born out of the need to create and configure multiple `Github` repositories in an easy, consistent and 
repeatable manner, yay automation! :tada:

## Quick Start

* Create a `Github` [oauth token](https://github.com/settings/tokens) that has the `repo`, `admin:public_key`, and 
`admin:repo_hook` scopes attached to it.

* Create a [defintion repository](https://github.com/dangernoodle-io/github-repository-tools/wiki/Definition-Repository) 
and your first [repository definition](https://github.com/dangernoodle-io/github-repository-tools/wiki/Repository-Definitions)

* Create the repository

```bash
docker run -it --rm \
  -v <PATH TO LOCAL DEFINITION REPOSITORY>:/definition-repository \
  dangernoodle/github-repository-tools \
  --repoDir /definition-repository \
  repository <NAME OF REPOSITORY>
```

See the [wiki](https://github.com/dangernoodle-io/github-repository-tools/wiki) for futher details.
