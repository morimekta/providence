Providence CLI Tool : Config Helper
===================================

Providence can be used as a base for generating config too. Take a look at the
`providence-config` module for an in depth example of the config markup syntax.
But using the config is not always preferrable, or trivial to utilize in it's raw
form. So in order to transform / build the config, and in order to validate or test
the config written before deployed, we have a "config helper tool", or `pvdcfg`.

The tool have three main functions:

- `pvdcfg print`: Parse and compile the target config and print it out to standard out.

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

- `pvdcfg params`: Show a list of params that can be used to augment the config, and in
  which config file its defined.
  
    ```sh
    $ pvdcfg -I providence/ params resources/my_service.cfg
    http_port = 8080 (resources/my_service.cfg)
    db_host = "localhost:1234" (resources/db.cfg)
    ```

- `pvdcfg validate`: Validate a set of config files from a base config. This is similar to
  the 'print' command, but prints nothing on success, and only the error message if the parsing
  fails at any step.

    ```sh
    $ pvdcfg -I providence/ validate resources/good.cfg
    $ pvdcfg -I providence/ validate resources/*.cfg
    Error in my_service.cfg line 23 position 6
        No such field 'blah' in config.Service
          blah = "nothing"
    ------^
    ```

Included configs can be found in two ways from each file (and it's checked in this order):

- Relative path from the parsed file. This includes parent directories (`..`).
- Relative path from a config root. This does *not* allow parent directories.

Note that the config's inclusion paths can *NOT* be parametrized. The only way to
parametrize the included config is to set up the config roots or symlinks to have the
expected files at the included locations.
