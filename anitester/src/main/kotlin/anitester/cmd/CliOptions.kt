package anitester.cmd

import anitester.BuildConfig
import anitester.cmd.dto.ConfigsDto
import anitester.cmd.dto.OptionsDto
import anitester.tester.models.TestsEnum
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default

object CliOptions {

    fun parseArgs(args: Array<String>): OptionsDto {
        val parser = ArgParser(BuildConfig.NAME)

        val apksPath by parser.argument(
            ArgType.String,
            description = "Apk file or directory with apks",
        )

        val animeUrl by parser.option(
            ArgType.String,
            "anime-url",
            "a",
            description = "Target anime url",
        )

        val checkThumbnails by parser.option(
            ArgType.Boolean,
            "check-thumbnails",
            "T",
            description = "Check if thumbnails are loading",
        ).default(false)

        val checkVideos by parser.option(
            ArgType.Boolean,
            "check-videos",
            "V",
            description = "Check if videos are playing",
        ).default(false)

        val completeResults by parser.option(
            ArgType.Boolean,
            "complete-results",
            "C",
            description = "Output JSON files with complete result data",
        ).default(false)

        val cookieJar by parser.option(
            ArgType.String,
            "cookies",
            description = "Load cookies from specified netscape cookie-jar file",
        )

        val dateFormat by parser.option(
            ArgType.String,
            "date-format",
            "f",
            description = "Format to use when printing episode date",
        ).default("dd/MM/yyyy")

        val debug by parser.option(
            ArgType.Boolean,
            "debug",
            "d",
            description = "Enable okHttp debug",
        ).default(false)

        val episodeNumber by parser.option(
            ArgType.Int,
            "episode-number",
            "n",
            description = "Target episode number",
        )

        val episodeUrl by parser.option(
            ArgType.String,
            "episode-url",
            "e",
            description = "Target episode url",
        )

        val increment by parser.option(
            ArgType.Boolean,
            "increment-pages",
            "i",
            description = "Try using pagination when possible",
        ).default(false)

        val jsonDir by parser.option(
            ArgType.String,
            "json-dir",
            "D",
            description = "Directory to put the JSON result files",
        )

        val preferencesFile by parser.option(
            ArgType.String,
            "prefs",
            description =
            "Special Json file with shared preferences to extensions. Read the README to understand how it works.",
        )

        val prettyJson by parser.option(
            ArgType.Boolean,
            "pretty-json",
            "P",
            description = "Dumps prettified JSON data to files",
        ).default(false)

        val printJson by parser.option(
            ArgType.Boolean,
            "json",
            "j",
            description = "Show JSON data instead of tables",
        )

        val proxy by parser.option(
            ArgType.String,
            "proxy",
            description = "Proxy address to use when doing the requests. Like <protocol>://<host>:<port>",
        )

        val resultsCount by parser.option(
            ArgType.Int,
            "results-count",
            "c",
            description = "Amount of items to print from result lists",
        ).default(2)

        val searchStr by parser.option(
            ArgType.String,
            "search",
            "s",
            description = "Text to use when testing the search",
        ).default("world")

        val showAll by parser.option(
            ArgType.Boolean,
            "show-all",
            "A",
            description = "Show all items of lists, instead of the first ~2",
        )

        val stopOnError by parser.option(
            ArgType.Boolean,
            "stop-on-error",
            "X",
            description = "Stop the tests on the first error",
        )

        val tests by parser.option(
            ArgType.String,
            "tests",
            "t",
            description = "Tests to be made(in order), delimited by commas",
        ).default(TestsEnum.testList())

        val timeoutSeconds by parser.option(
            ArgType.Int,
            "timeout",
            description = "Maximum amount of time(in seconds) spent in each test",
        ).default(90)

        val tmpDir by parser.option(
            ArgType.String,
            "tmp-dir",
            description = "Directory to put temporary data",
        ).default(System.getProperty("java.io.tmpdir"))

        val useChromium by parser.option(
            ArgType.Boolean,
            "chromium",
            description = "Use chromium for webview (it uses firefox by default)",
        )

        val userAgent by parser.option(
            ArgType.String,
            "user-agent",
            "U",
            description = "Set and use a specific user agent",
        )

        parser.parse(args)

        val configs = ConfigsDto(
            animeUrl.orEmpty(),
            checkThumbnails,
            checkVideos,
            completeResults,
            dateFormat,
            episodeUrl.orEmpty(),
            episodeNumber ?: -1,
            increment,
            printJson == true,
            resultsCount,
            searchStr,
            showAll == true,
            stopOnError == true,
            tests,
            timeoutSeconds.toLong(),
        )

        val options = OptionsDto(
            apksPath,
            configs,
            cookieJar,
            debug,
            jsonDir,
            preferencesFile,
            prettyJson,
            proxy,
            tmpDir,
            useChromium == true,
            userAgent,
        )

        return options
    }
}
