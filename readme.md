# Questionn [![Build Status](https://travis-ci.org/Palmr/questionn.svg?branch=master)](https://travis-ci.org/Palmr/questionn)

The n stands for knowledge

## Current State

Old hacky code is in the old-hacky branch.

This is a shell of a ratpack service.
Compared to before this has nice routing and config handling, and some nice jdbc-bits.

Still needs all the old functionality bringing in, perhaps with some thought where things should go.
If "doing it right" we should probably have a little more testing on the go than we did.

## TODO

 - [x] Basic server backend
 - [x] Add vue front end
 - [ ] Sort out where the YAML goes
 - [ ] Parse all the YAML again
 - [ ] Get runnable answers again
   - [x] First answer acceptance test
   - [ ] Answers with different result shapes
   - [ ] Answers with query parameters
   - [ ] Work out if answers are really distinct from queries
 - [ ] Work out how to completely evict all traces of Guice
 - [ ] Add working vue front end
 - [ ] Bring back the selenium tests?
 - [ ] ...have some tests
 - [x] Probably some of the config, e.g. datasources should live outside yaml?
 - [ ] What was reading from a security.properties should probably be a security.yaml
 - [ ] Dashboards
 - [ ] Query Building
 - [ ] Error Handling
 - [ ] All the other things a metabase replacement would need to be useful
   - [ ] Fine grained permissions
   - [ ] LDAP?
   - [ ] Shiny charts & Dashboards
   - [ ] ...
