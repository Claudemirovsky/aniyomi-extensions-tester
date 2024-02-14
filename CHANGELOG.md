# [v2.6.1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.6.0...v2.6.1) - (2024-02-14)

### Bug Fixes

- (**test**) Change cookie testing url ([e465fec](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e465fecf0e1c78d15b22fa9f7fb6ad0758a94ac2))
- (**anitester**) Add forgotten class - Fixes crash with jellyfin ([d862f22](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/d862f22d450f52e83db935db094c990122c717b8))

### Miscellaneous Tasks

- Bump dependencies ([7845c03](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7845c034eda91ec5c6a9ecd498f3790eee2a158a))
- (**release**) Prepare for new release ([1080e5d](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/1080e5d8365fee92cc4f29e526ba207f0c13fbb7))

### Refactor

- Asynchronous testMediaResult function ([afe5ff1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/afe5ff11bf62a0e0b7a34484374f3d903b8a9a16))
- (**anitester**) Move assets-extraction step to AnimeExtension class ([61d1035](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/61d10352b2604ebcead8f8a073828694348f92ae))

# [v2.6.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.5.0...v2.6.0) - (2024-01-16)

### Bug Fixes

- Fix CloudflareInterceptor AGAIN ([8efdf28](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/8efdf2876b0e0a6296893daf960366af80cb06d1))
- Check more video playlist mimetypes in testMediaResult function ([382baf6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/382baf61716fb7102d2fe73c5e2996528ea45d9a))
- Update & fix proguard rules ([8731eb5](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/8731eb5af13d52094799307d3049411c90d7a216))
- (**proguard-rules**) Don't remove org.json.* classes/methods ([d1f1c67](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/d1f1c67f0730fed46ba4508859f7a79039530f6d))
- (**proguard-rules**) Fix fake webview in optimized jars ([2a8b05f](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2a8b05f0df0a4e2d70a70a38a8e8e02d4dcf03a7))
- Fix build in jdk 11 ([d2936ae](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/d2936ae9de80953c5a790e5e9f1016163c83c8e7))
- Rename UpdateStrategy to AnimeUpdateStrategy ([bd4509f](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/bd4509f2ffde2d93e787f7974e418e6c062c3928))
- Workaround to prevent hanging in the end of all tests ([3d29777](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/3d29777481f31468ed7dcabc721a08e12360539e))

### Features

- Show audio tracks ([11d7316](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/11d7316180f29381c97004f9dc43e176e777584a))
- Add initial support for extensions-lib v15 functions ([65ebb0a](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/65ebb0ac3590e4c1c5dc4754d3d4bbcecd1a5a29))
- Pull some network-related changes from ani/tachiyomi ([c9043bb](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c9043bbfffa28a1c67005d1402b68909a9900c8f))
- Add required functions for extlib v14 ([e2fb55c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e2fb55cc0777695638476fb1b2d66a4d161274f8))

### Miscellaneous Tasks

- Bump dependencies ([60d3d2b](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/60d3d2b6fd48d5c3ccbf9b37c444b88a24d8df3f))
- Bump dependencies ([f0cc162](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f0cc1624448e6a8c7bd3f07c830d92cb5efac78d))
- Remove (now) useless files ([347a6bd](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/347a6bd24b24a7593085bd1f6ebbaea976d20247))
- (**CI**) Update actions ([2af1727](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2af1727e7ab2d12fe6e65ac9cafac9b21f6c70c9))
- Remove some unnecessary code ([811377a](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/811377aaf69604c528c0df2bcfeb2ea1e9a77b05))
- Add Detekt gradle plugin ([4322592](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4322592406eab87d87e78cfa6c1d2e66bccda1c4))
- Bump CI JDK version anyway ([be38cb1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/be38cb13c9e272c04a26a41cd17fc971a265a942))
- Bump Java version ([c7853ca](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c7853cade6ca1b2658869d62e82cc18140407ad8))
- Bump dependencies ([06c7328](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/06c73284f55a9b2bcd511457f96e3cc7c8dddfe5))

### Other

- [skip ci] chore(changelog): Update changelog ([dd50622](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/dd50622b25b795e31e883494192443eb7c4c2e70))

### Refactor

- Get rid of RXJava in tests ([c2602fe](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c2602fefc39e2a93caa8b3bfbc8c2de6074e6d86))
- Minor refactoration ([487e584](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/487e5849a8a2c950be9db84292076d30b2395552))
- Address Detekt complains ([dc6ee06](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/dc6ee06b36277c0f7a9b70da6502da292bc55491))
- Backport cleanup from tachiyomi-extensions-inspector ([bf8f590](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/bf8f5901a46fd9fe137401dd6b66b1b74f3305da))

# [v2.5.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.4.2...v2.5.0) - (2023-08-25)

### Bug Fixes

- (**build**) Update dex2jar ([e4e040b](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e4e040b942201ff5bc867add9df6ef76ac09d9c8))
- Fix cloudflare challenge solving in firefox ([061a29a](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/061a29a01e21041110379df3bfc468db29e9392b))

### Features

- (**cmd**) Show scanlator on SAnime entries ([a9f5718](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a9f57187eca9bca137a7f89856a0d548752ca417))
- Add support to use firefox/chromium on webview ([6870ce5](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6870ce55a76e69b1bc19a6f841486f4d83eb415c))
- Show the source name and language in tests ([f93ce41](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f93ce4120a5a1fdd442f09f7eb48212241ec1cb2))

### Miscellaneous Tasks

- Bump dependencies ([55b7770](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/55b77700cbc087574f5ac053259c99885674d48a))
- **BREAKING:** Remove Duktape wrapper ([2ce2d55](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2ce2d55a84afec23227b26f128452443b1ca1207))
- Bump dependencies ([a990610](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a9906104e2db8f4ddef310c6d038afce69bd7a8d))
- Add serialization-json-okio lib ([29d01d8](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/29d01d8bc81431129f40df65e853c482b7f67b62))
- Bump dependencies ([9a1bb17](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/9a1bb17e073f30fcf23f96aaffb465840871c6fe))
- Update KtLogging to v5 ([161b8c2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/161b8c29d4ff28e14e7e7e22dac5239488788a0e))
- (**release**) Prepare for new release ([1729bac](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/1729baca0ca59cc7f7889c7fd48a074af8881a50))

### Refactor

- Convert QuickJS wrapper to kotlin ([1c1e298](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/1c1e298ac961d2de9295fdbb4078acae6fa4a3e0))

# [v2.4.2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.4.1...v2.4.2) - (2023-06-21)

### Bug Fixes

- (**build**) Dex2jar moment ([cd79982](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/cd79982640b11db0c6945eefd4888fe56ab3aae3))
- (**tests/Cloudflaretests**) Remove previously-added cf-clearance cookies ([23def18](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/23def18450ac122ac4906a859674f6a02f18c673))
- (**CloudflareInterceptor**) Fix turnstile selector ([6fc6706](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6fc67060183f344ff050da03df9d2ab9e650a7cb))

# [v2.4.1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.4.0...v2.4.1) - (2023-06-02)

### Bug Fixes

- (**AndroidCompat**) Fix restoration of saved cookies in StubbedCookieManager ([02d501e](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/02d501ea9af8e9cb4082b235058f9e883f7c098f))
- (**tester**) Prevent not writing episode list test result ([681a6ab](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/681a6abccf8849480c1e84bfb7fba649b87af0c7))

### Miscellaneous Tasks

- Bump dependencies ([93fb3af](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/93fb3afd9c19c436d9757a918c3892f877422459))

### Refactor

- Ktlint moment ([a8f519f](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a8f519fd7384c5e8a587b53dd23e17900fe4742f))

# [v2.4.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.3.1...v2.4.0) - (2023-05-28)

### Bug Fixes

- Copy extension assets to generated .jar file ([65d2495](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/65d24956399a58d8b28f033c03677fdda8f96fd1))
- (**playwright-utils**) Fix forced/not needed browser download ([ef0cdc1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/ef0cdc1670859685d909c0a64527ace1701185f2))
- Fix proguard rules ([a1ab663](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a1ab663983a428b4641f86860e7bec8078e98e03))
- (**FakeWebView**) Fix exception with abstract lists ([4f8e10c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4f8e10c23819599ca790a6c332f7dc1ae558d304))
- (**FakeWebView**) Ignore playwright navigation errors ([3d0b560](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/3d0b560369b6d583470c51871204c4cc102f8bfe))
- (**FakeWebView**) Prevent type casting error ([563acbb](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/563acbb68cdb2c2a547e3180fe3d95a75077cdec))
- Fix websettings on optimized jars ([a73ea2a](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a73ea2a2d71185ec6e3fdef9f22104f4addc06bf))
- Ensure interoperability between OkHttp and android's CookieManager ([21414e6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/21414e61873ea16eec7cb6287678bc1c795789c9))

### Features

- Implement playwright-based webview ([db0ea7a](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/db0ea7a64c5ca940cba8d9b1356b0495156672f8))
- Add a single, simple CloudflareInterceptor test ([185d393](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/185d393212d863e6712e97e9e455735e0a64db60))
- (**FakeWebView**) Implement onPageStarted event ([19459f5](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/19459f541337addf2229649feaa5c280ed167f5f))
- (**AndroidCompat/AndroidWebView**) Load and save cookies in webview ([6bfcfa1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6bfcfa1252e1507c20280d8f55449a044686f1ff))
- (**cli**) Add option to load cookies from netscape cookiejar file ([a732752](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a732752df104fcedced8db6c9681d2c7e6e702fa))
- (**cli**) Add option to read shared prefs from a special json file ([e602d51](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e602d5182afba3154aba4bb3ed2a27448674c72c))

### Miscellaneous Tasks

- Suppress some compilation warnings ([3233153](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/3233153c06361553eebd4f6bf000fa3bb652f64b))
- Bump dependencies ([369ce23](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/369ce23fec4ceaf8c6967a048f0ee916067feaf0))
- (**CI**) Re-activate optimized builds on releases ([64a2709](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/64a27098676d2c76b32851ad11ea16b1f8359df9))
- Update dependencies ([111eab4](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/111eab4187686a91ec9a8153841f6dbe06dae175))

### Refactor

- Move playwright-related classes to a separate subproject ([2b25ba6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2b25ba61e2fe85d119b81a5f4235c1b8130bc048))
- (**settings.build.kts**) Remove unnecessary repositories ([0df3131](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/0df3131f2981f92150d8a727bc97e9b550ec789a))
- Ktlint go brrrrrr ([e79d2b8](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e79d2b86eeb8dd9121c219b48a896c65f8083ab7))
- Reutilize more code between cookie managers ([4887ca2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4887ca2380717f32bc8fbba553e4e99ebdb850ea))
- (**tests/AndroidWebView**) Use blank page in JavascriptInterface test ([08c244e](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/08c244e482d5537f6ba2769418d39aba313202a7))

### Testing

- Add two AndroidWebView tests ([2ae61da](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2ae61da8692948c73ca1ed0054aa20a27072aa79))
- Add SharedPrefs tests ([49787ad](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/49787ad35ed365ef4a1edfaeaef3e950f467ece1))

# [v2.3.1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.3.0...v2.3.1) - (2023-05-10)

### Bug Fixes

- Show errors correctly ([5f36bdb](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/5f36bdbf4e730ad52d169af2354fd3dc9c422e20))

### Features

- Add status_name attribute to dumped json ([fa1889b](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/fa1889b0a599927173f38837ee0dc7456c321b83))
- Add timeout option ([9e0d70c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/9e0d70cf441c4e21382c1cd6ef338cedfcd72d86))
- Only deserialize results when needed ([309f5a5](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/309f5a5ec52ad2e0d999048999b56595f8628825))

### Miscellaneous Tasks

- Update README ([4f15fc8](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4f15fc8f0eba9d20abf0f9221dc78dfb22b495b5))

### Refactor

- Explicitly set a dispatcher to timeout coroutine ([96c53c0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/96c53c0bf8321d72d14c756d5e47e7c75cf9a34d))

# [v2.3.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.2.0...v2.3.0) - (2023-05-01)

### Bug Fixes

- Use the same timeout as the main app ([e6c9bce](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e6c9bced2d35daee8b50baab4b9d6c99f8c78bbc))
- Prevent catastrophic failure on initial IO operations ([4f3f423](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4f3f4232fe4f16465ff437b094ef2b47672cc465))
- (**build**) Update Dex2Jar ([52cb440](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/52cb440b70f31792a9715f2aa4cc967664bc49b1))
- (**CloudflareInterceptor**) Update evasions and methods to match official cf-clearance package ([1c54dfe](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/1c54dfed4599f2b70022192eb3af9277f2408ea8))

### Features

- Initial support to extensions-lib 14 ([fef41a4](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/fef41a41f1a66ca6e1fc3748cd25dc0d8d62a75d))
- Parallelize video checking ([09d3fdd](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/09d3fdd814285215402ce21d7e54ab7fc637eaa9))
- Make video checking optional ([fad2f00](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/fad2f003737f23eba7023d9184d3a074d90f448e))
- Parallelize thumbnails checking ([b9571ef](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/b9571ef59f301f8ea4f46be72590d599e365f7e2))
- Put time limit to each test ([f92d9d9](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f92d9d96fa4e7e07ec5baef7356fce8a014bc06a))

### Miscellaneous Tasks

- Bump dependencies ([c89ffa4](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c89ffa4293609c9f860a479b99fd187ca9584b70))
- Upgrade gradle to 8.1 ([e7135df](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e7135df3533a0e0fbb1dac1ceb4e13106ecbd0c8))
- (**README**) Update usage arguments and TODO's ([f5b3ec9](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f5b3ec9617df74f9c539294e1c31423b88918c45))
- (**CI**) Update workflows ([c01e5e4](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c01e5e4a4fa566e2294afa176edfb1f6f2d0d8bb))
- (**CI**) Add changelog-generators ([d5ccef5](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/d5ccef548a3a6390f5f984faaf6aefdc3106e307))
- (**CI**) Temporarily disable optimized builds ([094d44e](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/094d44e05556985e58fd3ce23fb5790b83228f68))

### Refactor

- Use callable references in some places ([8684aca](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/8684acae4e60df2c74d645338a59643351232c39))
- General refactoration and more callable references ([92b5472](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/92b5472562c5f980f4946d7a3692057381d818b4))

# [v2.2.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.1.0...v2.2.0) - (2023-01-17)

### Bug Fixes

- (**network**) Get CloudflareInterceptor from tachidesk/tachiweb ([fd1d766](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/fd1d766d10a92e80f82a4b1b742dc75a67759924))
- (**AndroidCompat**) Solve build problems ([71386e4](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/71386e45390079968169470a0cbec53d8ae3645f))
- Prevent memory errors while building ([a823163](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a823163f29d22e9caca4cb9decdba923c015205a))
- (**CloudflareInterceptor**) Add cf_clearance cookie to all domains ([7174d1e](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7174d1ee133fa52be3cc8a041e0ee283416ba7c0))
- Update proguard rules ([27081c6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/27081c6f5297a91928ce16fc4cc782a77cb657e1))

### Features

- (**AndroidCompat**) Simulate android.os.Handler with coroutines ([40af430](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/40af43085f69ec7fa086e02c1d6de932ec6b5fa5))
- [WIP] Simulate android webview ([123d5df](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/123d5dfc8ec52d65d1ef0d1f55d089b8ff6c0c7d))
- (**AndroidCompat**) Some tweaks on webview ([85a20fa](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/85a20fac0dbadf2d07a31ef91f9653ee0a8e0114))
- Implement a working CookieManager ([6366c77](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6366c77e50939e714589e29bad9f6b427b700f6c))
- Use a custom CookieManager on WebClient instances ([682fb71](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/682fb71daf04223ebf3e2990a84b00236eefd7f3))
- Show error reason when video list is empty ([375d6f1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/375d6f1dcd5dc504ccd46845cbced3fc5dac74b3))
- (**AndroidCompat**) Reduce android.jar size ([a6411ec](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a6411ecb81b88b32516080803599c6d4cb916e29))
- Use the new android-jar lib ([dc71b46](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/dc71b46078d703bf1948c9f5a83c153835cc9cb3))
- Remove unused icu4j files ([0d69bf2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/0d69bf27f41c8045f65e6218d52cadf05dbb6f7f))
- Add task to create a proguard-optimized jar ([79cf4cf](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/79cf4cf259f9fbd6d55796e7dfa981f9772077ee))
- Stole(again) CloudflareInterceptor from TachiDesk ([f5b9840](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f5b9840703acbdbd00c8b6b04fd5728c95bfecc8))
- Download nodejs binaries instead of bundling them ([89ded79](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/89ded79b07a24a8ad539cae84cd3152a5466ab7e))
- Use preinstalled chromium if possible ([ff92003](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/ff92003bc31086c3995b34bea025b21b8db9481b))
- (**CloudflareInterceptor**) Prevent opening the browser unnecessarily ([52a84b0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/52a84b016650d8783098ee19bc0d6543238d1f36))

### Miscellaneous Tasks

- (**AndroidCompat**) Update AndroidCompat to recent Tachidesk version ([62cee1a](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/62cee1abf2ae29ce0680ba09bf367ed74dbd2b3f))
- Update README ([3b5a123](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/3b5a123dfacc798a71550ffd1463e9fd9e827f04))
- Update .gitignore ([4c210d2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4c210d26d3171fab8705f6d0219026fd88f3c1e1))
- Update gradle wrapper to 7.6 ([ca4e0a5](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/ca4e0a5e515170e4f119b2f9963d29e04e5dcb3f))
- Bump dependencies ([72a3cbc](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/72a3cbc0164110ab3a56b4f0e8a7fba49bfec3de))
- Silent some warnings on build ([c6d5acb](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c6d5acb768284188715d7b033056b72361a5d765))
- Make kotlin compiler happy ([3a8f390](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/3a8f3909dc8ca294d94586c6aa5138f885ee933e))

### Other

- Update release.yml workflow ([29e7ea6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/29e7ea61752f439aa180ed8c06186a184e8a7b21))

### Refactor

- Ktlint go brrrr ([d3be44e](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/d3be44eadec5342b71cb936628b87f50eaa0078b))
- Use version catalogs on gradle ([85decd9](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/85decd9e7b8deb7c1c6cf62579452379b24ad5ce))
- Convert DriverJar to Kotlin ([fe1f867](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/fe1f8670dfb083fbec9944a07fb5b14fe92d655d))
- (**CloudflareInterceptor**) Reduce repeated lines a bit ([55b28d4](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/55b28d4df7268721663fcb2beb0b0adf3537ffea))
- Remove a println ([152ca7d](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/152ca7dac59d7114fb52f787d3e4102eaf4d7bbb))
- Ktlint 3.13.0 moment ([319a201](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/319a201df729c6940943f9d1095435c77437ecd6))
- (**CloudflareInterceptor**) Use a event instead of timeouts ([e685aff](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e685aff309c559901ddb438e9f3559cecaedc9c0))

# [v2.1.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.0.1...v2.1.0) - (2022-11-14)

### Bug Fixes

- Use source's filter list to prevent errors on search ([2fd362b](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2fd362bf56633d85afc5c1c81af9584d33f72be1))
- Prevent false-negatives on sources that doesnt support HEAD requests ([7b111b0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7b111b0b8c2a963c8febb308a26abbcc061372da))

### Features

- Reduce default timeout limit ([92dad25](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/92dad25e31d617d34ef2393a6a3bdf6d8e3c1c5a))
- Dump tests results on-demand ([a1e8ff7](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a1e8ff709d4cfb8af2f1cb95ccf053166cbe0b06))
- (**cli**) Show progress ([73855eb](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/73855ebb81f38f0cca6232b366696a99946a339f))

### Miscellaneous Tasks

- Update dependencies to become closer to main app ([1667268](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/166726812f932bd2926a8527fcd5e8863bdc0883))

# [v2.0.1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.0.0...v2.0.1) - (2022-11-13)

### Bug Fixes

- Prevent crash on runtime errors (#2) ([b5540be](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/b5540bef02dddfbc9af553210b2c7944975a3be7))
- Fix NoSuchMethodError on Video class ([6388ae8](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6388ae86373fe19130c483484f673e46e273b6d2))

# [v2.0.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v1.1.0...v2.0.0) - (2022-11-13)

### Bug Fixes

- (**tester**) Prevent crash due to ProtocolException ([1715133](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/1715133f48ce07f156753c942ee66a0115805365))
- (**tester**) Dont ignore episode-url option if a test ran before ([46361fc](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/46361fcf4b3f06da713b0e044ff416dacfebb0a2))

### Documentation

- Document most of test-related functions and operations ([757e98d](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/757e98d0be054cb2c4098c9ed0bbf1afb70e9432))

### Features

- Add option to change the user-agent ([0291eef](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/0291eef1587f4942c153df1bad6168a8877a56ed))
- Add option to use a proxy in OkHttpClient ([6b388a2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6b388a237ad72a141f13c1f2a5ee8ce3b2b3b628))
- Use system properties to set proxies and user-agent ([2011102](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/20111024dcfd7c3830772ae004078e861bd76978))
- Add JSON file logging ([575ff88](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/575ff8822abf035ea04c40aa7feb00faac639974))
- **BREAKING:** Dump only "passed" json key unless -C is specified ([6fc28c0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6fc28c086b497f658efaf7826ed8ccbea2da147a))
- (**cli**) Add option to dump prettified JSON to files ([c4be195](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c4be195541f9ef2f9ccd7a584f1ce936c38f0fe7))

### Other

- Add JSON file logging ([76447ec](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/76447ecec7870de5989ac30b12eba129fe81b0d6))
- Merge branch 'master' into master ([037a001](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/037a0013d1ce2dc22097d2fdee855487d09374fb))

### Refactor

- Use data classes instead of pure JsonObjects ([6fc8390](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6fc8390c6c254a648c311162b77a619de268b6c1))

# [v1.1.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v1.0.0...v1.1.0) - (2022-11-09)

### Features

- (**tester**) Check if video links are working ([b0dda90](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/b0dda9047afcba62f1f3aed5417a6206621cf340))
- (**cli/tester**) Add option to check if thumbnails are loading ([b6ebdda](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/b6ebdda6fa99972363f441c925b217b8922bd5dc))

# [v1.0.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/tree/v1.0.0) - (2022-11-09)

### Bug Fixes

- (**cli**) Fix JSON printing ([7612c2b](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7612c2b1a9a41374c0066906acd2b8a81e00c846))

### Features

- (**cli**) Add CLI Options for tests ([e4f3e66](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e4f3e66af067434b8f83e14afad4a31559280ae9))
- (**network**) Make possible to debug HTTP requests ([fe3ae41](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/fe3ae41310c494e719f777f61a133ed9fb821a09))
- (**model**) Make some model classes to be [de]serializable ([e4fdfcd](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e4fdfcdc0454e1def86d045ef3be0a7bc59d5747))
- Initial test engine implementation ([4d693ab](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4d693ab74949a7cf7763fe9708fb00894a6995e6))
- (**cli**) Add custom search option ([4cef49d](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4cef49d0a17958290458ddf0cd7402592f897cb4))
- (**cli**) Add utilities to "pretty-print" data ([5220e25](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/5220e2560f27ff0052b4f3df53f9b9e60c21eb65))
- (**tester**) Implement popularAnimesPage test ([d375f66](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/d375f66bdd038ec1ddf5f5b72119286aba61eb32))
- (**tester**) Implement latestAnimesPage test ([f9bd23c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f9bd23c1f84c69254de5b6a6538270195a648dbd))
- (**tester**) Implement searchAnimes test ([4c46a60](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4c46a60d79ab45c375b174141f0bf79ca72ce3ff))
- (**tester**) Implement animeDetails test ([f98578c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f98578c6ba89c44237a58fbd89a03ca0093c81a3))
- (**tester**) Implement episodeList test ([90a72dd](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/90a72dd4d698cf63b335536f173a4f07fe2abc60))
- (**cli**) Make printed results count customizable ([332233c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/332233cc7e441cf5413d5c247000158bc6461108))
- (**tester**) Implement videoList test ([2cc252d](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2cc252d4e77433c24f7bd9b99c0a5dfc8788b5f6))
- (**cli**) Better error printing ([e618cd6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e618cd6dc603b9705cb8149c4dc4f1079120daee))
- (**network**) Readable http debug logs ([816cdd2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/816cdd2362a3e142627c8831ca4d9f397714bcd9))
- (**cli**) Format timestamp of episodes ([c2919a2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c2919a25180fc4f9bd8a5a34f4edef8f8e423979))
- (**cli**) Show time spent on each test ([33e4fa6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/33e4fa677b0093ae72f0377a71d83fca83d33f8f))

### Miscellaneous Tasks

- Change project name ([3cca8d0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/3cca8d083b3f0717b25c57438f1bda91e6384149))
- Change project name again ([7c64973](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7c649737b2b69dc307294652130ac2990ef57b07))
- Modify README.md ([3f9397c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/3f9397c9bc4539491498649a4a76e74b750fff0b))
- Update README ([26b7665](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/26b76657977138d0cc7003aea60fd86d8a4ca333))

### Other

- "Initial" commit, using aniyomi-extensions-inspector as base ([3d2a436](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/3d2a4369b8e5df4809dcd68c0d848db2284360c7))

### Refactor

- (**cli**) Reorder CLI commands ([1aeb1a5](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/1aeb1a58ba7d81a391e6ae182953794f46a8673f))

<!-- generated by git-cliff -->
