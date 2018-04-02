---
layout: page
toc_title: "Config Helper"
title: "Providence CLI Tool : Config Helper"
category: cli
date: 2018-06-04 12:00:00
order: 4
---

Providence can be used as a base for generating config too. Take a look at the
`providence-config` module for an in depth example of the config markup syntax.
But using the config is not always preferrable, or trivial to utilize in it's raw
form. So in order to transform / build the config, and in order to validate or test
the config written before deployed, we have a "config helper tool", or `pvdcfg`.

The tool have two main functions:

- `pvdcfg resolve`: Parse and compile the target config or configs and print the end
  result out to standard output.

    ```sh
    $ pvdcfg -I providence/ print resources/my_service.cfg
    {
      http {
        port = 8080
      }
      db {
        host = "localhost:1234"
      }
    }
    ```

- `pvdcfg validate`: Validate a set of config files from a base config. This is similar to
  the 'print' command, but prints nothing on success, and only the error message if the parsing
  fails at any step. It will only print the first error it finds.

    ```sh
    $ pvdcfg -I providence/ validate resources/good.cfg
    $ pvdcfg -I providence/ validate --strict resources/*.cfg
    Error in my_service.cfg line 23 position 6
        No such field 'blah' in config.Service
          blah = "nothing"
    ------^
    ```

Included configs must be found using relative path from the parsed file.
This includes parent directories (`..`). Absolute path includes are not allowed.
