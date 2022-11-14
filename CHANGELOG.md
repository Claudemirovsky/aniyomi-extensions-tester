# [](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.1.0...v) (2022-11-14)



# [2.1.0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.0.1...v2.1.0) (2022-11-14)


### Bug Fixes

* Prevent false-negatives on sources that doesnt support HEAD requests ([7b111b0](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/7b111b0b8c2a963c8febb308a26abbcc061372da))
* Use source's filter list to prevent errors on search ([2fd362b](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/2fd362bf56633d85afc5c1c81af9584d33f72be1))


### Features

* **cli:** Show progress ([73855eb](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/73855ebb81f38f0cca6232b366696a99946a339f))
* Dump tests results on-demand ([a1e8ff7](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/a1e8ff709d4cfb8af2f1cb95ccf053166cbe0b06))
* Reduce default timeout limit ([92dad25](https://github.com/Claudemirovsky/aniyomi-extensions-tester/commit/92dad25e31d617d34ef2393a6a3bdf6d8e3c1c5a))



# [](https://github.com/Claudemirovsky/aniyomi-extensions-tester/compare/v2.0.1...v) (2022-11-13)



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



