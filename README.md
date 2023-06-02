# Aniyomi extensions tester

This is a fork of [tachiyomi-extensions-inspector](https://github.com/tachiyomiorg/tachiyomi-extensions-inspector)(a headless fork of [Tachidesk](https://github.com/Suwayomi/Tachidesk)) modified to be able to test [aniyomi extensions](https://github.com/jmir1/aniyomi-extensions/tree/repo/apk).

## Features
- Can help a LOT to debug extensions
- Easy to use
- Works on pure Linux and Android
- Has enough options to meet most of your needs.

## Compiling

Just run:
```bash
$ ./gradlew :server:shadowJar
```
output file path: server/build/aniyomi-extensions-tester-\<version\>.jar

## Usage
```bash
$ java -jar server/build/aniyomi-extensions-tester-v2.4.1.jar -h

Usage: aniyomi-extensions-tester options_list
Arguments:
    apksPath -> Apk file or directory with apks { String }
Options:
    --anime-url, -a -> Target anime url { String }
    --check-thumbnails, -T [false] -> Check if thumbnails are loading
    --check-videos, -V [false] -> Check if videos are playing
    --complete-results, -C [false] -> Output JSON files with complete result data
    --cookies -> Load cookies from specified netscape cookie-jar file { String }
    --date-format, -f [dd/MM/yyyy] -> Format to use when printing episode date { String }
    --debug, -d [false] -> Enable okHttp debug
    --episode-number, -n -> Target episode number { Int }
    --episode-url, -e -> Target episode url { String }
    --increment-pages, -i [false] -> Try using pagination when possible
    --json-dir, -D -> Directory to put the JSON result files { String }
    --prefs -> Special Json file with shared preferences to extensions. Read the README to understand how it works. { String }
    --pretty-json, -P [false] -> Dumps prettified JSON data to files
    --json, -j -> Show JSON data instead of tables
    --proxy -> Proxy address to use when doing the requests. Like <protocol>://<host>:<port> { String }
    --results-count, -c [2] -> Amount of items to print from result lists { Int }
    --search, -s [world] -> Text to use when testing the search { String }
    --show-all, -A -> Show all items of lists, instead of the first ~2
    --stop-on-error, -X -> Stop the tests on the first error
    --tests, -t [popular,latest,search,anidetails,eplist,videolist] -> Tests to be made(in order), delimited by commas { String }
    --timeout [90] -> Maximum amount of time(in seconds) spent in each test { Int }
    --tmp-dir [/tmp] -> Directory to put temporary data { String }
    --user-agent, -U -> Set and use a specific user agent { String }
    --help, -h -> Usage info
```

## About custom preferences
The json model that the `--prefs` option expects is:
```json
{
    "preference_filename": {
        "preference_key": "preference_value",
    },
    "another_preferences_file": {
        "some": ["prefs"]
    }
}
```

For extensions preferences(common usage), you can use `source_<ExtensionName>/<language>`
as the preference filename, or `source_<id>` if you somehow have the id in hands.

Example:
```json
{
    "source_GogoAnime/en": {
        "string_preference_key": "some string",
        "boolean_preference": false,
        "int_preference": 70,
        "float_preference": 3.141592,
        "long_num_preference": 2147483641,
        "strset_pref": ["some", "items"],
        "preferred_domain": "https://duckduckgo.com"
    }
}
```
## TODOs
- [ ] Read configs/arguments from a file
- [ ] Support search filters
- [x] [Support custom preferences](#about-custom-preferences)
- [x] Support custom cookies
- [x] Support webview-related interceptors
> - [x] Implement a proper and functional Cloudflare bypasser/interceptor (Needs a GUI, and doesnt work on CI well)

- [x] Implement all main functions from extensions-lib
- [x] Add options to test and check thumbnail/video URLs
> - [ ] Fix false-negatives on video checks due to the server being unable to accept HEAD requests
- [x] Show time spent on every test
- [x] Honor all CLI options
- [x] Support custom http/https/socks5 proxies
- [x] Support custom User-Agent
- [x] Document some functions, classes and operations
- [x] Dump tests results as JSON

## Credits

The `AndroidCompat` module was originally developed by [@null-dev](https://github.com/null-dev) for [TachiWeb-Server](https://github.com/Tachiweb/TachiWeb-server) and is licensed under `Apache License Version 2.0`.

Parts of [Tachiyomi](https://github.com/tachiyomiorg/tachiyomi) and [Aniyomi](https://github.com/jmir1/aniyomi) are adopted into this codebase, both licensed under `Apache License Version 2.0`.

You can obtain a copy of `Apache License Version 2.0` from  http://www.apache.org/licenses/LICENSE-2.0

Changes to these codebases is licensed under `MPL 2.0` as the rest of this project.

## License
```
Copyright (C) The Aniyomi Open Source Project

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at http://mozilla.org/MPL/2.0/.
```

```
Copyright (C) The Tachiyomi Open Source Project

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at http://mozilla.org/MPL/2.0/.
```

```
Copyright (C) Contributors to the Suwayomi project

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this
file, You can obtain one at http://mozilla.org/MPL/2.0/.
```
