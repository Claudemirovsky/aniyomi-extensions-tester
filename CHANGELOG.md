# [2.2.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.1.0...v2.2.0) (2023-01-17)


### Bug Fixes

* **AndroidCompat:** Solve build problems ([71386e4](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/71386e45390079968169470a0cbec53d8ae3645f))
* **CloudflareInterceptor:** Add cf_clearance cookie to all domains ([7174d1e](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7174d1ee133fa52be3cc8a041e0ee283416ba7c0))
* **network:** Get CloudflareInterceptor from tachidesk/tachiweb ([fd1d766](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/fd1d766d10a92e80f82a4b1b742dc75a67759924))
* Prevent memory errors while building ([a823163](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a823163f29d22e9caca4cb9decdba923c015205a))
* Update proguard rules ([27081c6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/27081c6f5297a91928ce16fc4cc782a77cb657e1))


### Features

* [WIP] Simulate android webview ([123d5df](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/123d5dfc8ec52d65d1ef0d1f55d089b8ff6c0c7d))
* Add task to create a proguard-optimized jar ([79cf4cf](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/79cf4cf259f9fbd6d55796e7dfa981f9772077ee))
* **AndroidCompat:** Reduce android.jar size ([a6411ec](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a6411ecb81b88b32516080803599c6d4cb916e29))
* **AndroidCompat:** Simulate android.os.Handler with coroutines ([40af430](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/40af43085f69ec7fa086e02c1d6de932ec6b5fa5))
* **AndroidCompat:** Some tweaks on webview ([85a20fa](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/85a20fac0dbadf2d07a31ef91f9653ee0a8e0114))
* **CloudflareInterceptor:** Prevent opening the browser unnecessarily ([52a84b0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/52a84b016650d8783098ee19bc0d6543238d1f36))
* Download nodejs binaries instead of bundling them ([89ded79](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/89ded79b07a24a8ad539cae84cd3152a5466ab7e))
* Implement a working CookieManager ([6366c77](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6366c77e50939e714589e29bad9f6b427b700f6c))
* Remove unused icu4j files ([0d69bf2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/0d69bf27f41c8045f65e6218d52cadf05dbb6f7f))
* Show error reason when video list is empty ([375d6f1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/375d6f1dcd5dc504ccd46845cbced3fc5dac74b3))
* Stole(again) CloudflareInterceptor from TachiDesk ([f5b9840](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f5b9840703acbdbd00c8b6b04fd5728c95bfecc8))
* Use a custom CookieManager on WebClient instances ([682fb71](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/682fb71daf04223ebf3e2990a84b00236eefd7f3))
* Use preinstalled chromium if possible ([ff92003](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/ff92003bc31086c3995b34bea025b21b8db9481b))
* Use the new android-jar lib ([dc71b46](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/dc71b46078d703bf1948c9f5a83c153835cc9cb3))



# [2.1.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.0.1...v2.1.0) (2022-11-14)


### Bug Fixes

* Prevent false-negatives on sources that doesnt support HEAD requests ([7b111b0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7b111b0b8c2a963c8febb308a26abbcc061372da))
* Use source's filter list to prevent errors on search ([2fd362b](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2fd362bf56633d85afc5c1c81af9584d33f72be1))


### Features

* **cli:** Show progress ([73855eb](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/73855ebb81f38f0cca6232b366696a99946a339f))
* Dump tests results on-demand ([a1e8ff7](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a1e8ff709d4cfb8af2f1cb95ccf053166cbe0b06))
* Reduce default timeout limit ([92dad25](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/92dad25e31d617d34ef2393a6a3bdf6d8e3c1c5a))



## [2.0.1](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.0.0...v2.0.1) (2022-11-13)


### Bug Fixes

* Fix NoSuchMethodError on Video class ([6388ae8](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6388ae86373fe19130c483484f673e46e273b6d2))
* Prevent crash on runtime errors ([#2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/issues/2)) ([b5540be](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/b5540bef02dddfbc9af553210b2c7944975a3be7))



# [2.0.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v1.1.0...v2.0.0) (2022-11-13)


### Bug Fixes

* **tester:** Dont ignore episode-url option if a test ran before ([46361fc](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/46361fcf4b3f06da713b0e044ff416dacfebb0a2))
* **tester:** Prevent crash due to ProtocolException ([1715133](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/1715133f48ce07f156753c942ee66a0115805365))

### Documentation
* Document most of test-related functions and operations ([757e98d](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/757e98d0be054cb2c4098c9ed0bbf1afb70e9432))

### Features

* Add option to change the user-agent ([0291eef](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/0291eef1587f4942c153df1bad6168a8877a56ed))
* Add option to use a proxy in OkHttpClient ([6b388a2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6b388a237ad72a141f13c1f2a5ee8ce3b2b3b628))
* **cli:** Add option to dump prettified JSON to files ([c4be195](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c4be195541f9ef2f9ccd7a584f1ce936c38f0fe7))
* Use system properties to set proxies and user-agent ([2011102](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/20111024dcfd7c3830772ae004078e861bd76978))
* Dump only "passed" json key unless -C is specified ([6fc28c0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6fc28c086b497f658efaf7826ed8ccbea2da147a))
* Add JSON file logging ([76447ec](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/76447ecec7870de5989ac30b12eba129fe81b0d6))

### Refactor

* Use data classes instead of pure JsonObjects ([6fc8390](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/6fc8390c6c254a648c311162b77a619de268b6c1))


# [1.1.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v1.0.0...v1.1.0) (2022-11-09)


### Features

* **cli/tester:** Add option to check if thumbnails are loading ([b6ebdda](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/b6ebdda6fa99972363f441c925b217b8922bd5dc))
* **tester:** Check if video links are working ([b0dda90](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/b0dda9047afcba62f1f3aed5417a6206621cf340))



# [1.0.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/e4f3e66af067434b8f83e14afad4a31559280ae9...v1.0.0) (2022-11-09)


### Bug Fixes

* **cli:** Fix JSON printing ([7612c2b](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7612c2b1a9a41374c0066906acd2b8a81e00c846))


### Features

* **cli:** Add CLI Options for tests ([e4f3e66](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e4f3e66af067434b8f83e14afad4a31559280ae9))
* **cli:** Add custom search option ([4cef49d](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4cef49d0a17958290458ddf0cd7402592f897cb4))
* **cli:** Add utilities to "pretty-print" data ([5220e25](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/5220e2560f27ff0052b4f3df53f9b9e60c21eb65))
* **cli:** Better error printing ([e618cd6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e618cd6dc603b9705cb8149c4dc4f1079120daee))
* **cli:** Format timestamp of episodes ([c2919a2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/c2919a25180fc4f9bd8a5a34f4edef8f8e423979))
* **cli:** Make printed results count customizable ([332233c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/332233cc7e441cf5413d5c247000158bc6461108))
* **cli:** Show time spent on each test ([33e4fa6](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/33e4fa677b0093ae72f0377a71d83fca83d33f8f))
* Initial test engine implementation ([4d693ab](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4d693ab74949a7cf7763fe9708fb00894a6995e6))
* **model:** Make some model classes to be [de]serializable ([e4fdfcd](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/e4fdfcdc0454e1def86d045ef3be0a7bc59d5747))
* **network:** Make possible to debug HTTP requests ([fe3ae41](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/fe3ae41310c494e719f777f61a133ed9fb821a09))
* **network:** readable http debug logs ([816cdd2](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/816cdd2362a3e142627c8831ca4d9f397714bcd9))
* **tester:** Implement animeDetails test ([f98578c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f98578c6ba89c44237a58fbd89a03ca0093c81a3))
* **tester:** Implement episodeList test ([90a72dd](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/90a72dd4d698cf63b335536f173a4f07fe2abc60))
* **tester:** Implement latestAnimesPage test ([f9bd23c](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/f9bd23c1f84c69254de5b6a6538270195a648dbd))
* **tester:** Implement popularAnimesPage test ([d375f66](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/d375f66bdd038ec1ddf5f5b72119286aba61eb32))
* **tester:** Implement searchAnimes test ([4c46a60](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/4c46a60d79ab45c375b174141f0bf79ca72ce3ff))
* **tester:** Implement videoList test ([2cc252d](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2cc252d4e77433c24f7bd9b99c0a5dfc8788b5f6))



