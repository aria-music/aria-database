package cc.sarisia.aria

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "docs-artifact/snippets")
class AriaApplicationTests {
    @Autowired
    lateinit var mvc: MockMvc

    private val entry1 = """
        {
            "uri": "https://www.youtube.com/watch?v=5BnhQbcPrBg",
            "provider": "youtube",
            "title": "【新衣装で】ビースト・ダンス　歌ってみた【相羽ういは/にじさんじ】",
            "thumbnail": "https://i.ytimg.com/vi/5BnhQbcPrBg/maxresdefault.jpg",
            "liked": false,
            "meta": "UihaCute"
        }
    """.trimIndent()
    private val entry2 = """
        {
            "uri": "https://www.youtube.com/watch?v=z3tukOqkjyc",
            "provider": "youtube",
            "title": "【】歌ってみた 疑心暗鬼 葛葉 にじさん【】",
            "thumbnail": "https://i.ytimg.com/vi/z3tukOqkjyc/maxresdefault.jpg",
            "liked": false,
            "meta": "KuzuhaCool"
        }
    """.trimIndent()

    @Test
    @Order(1)
    fun testCacheInsert() {
        this.mvc.perform(
                post("/cache")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "entries": [
                                    $entry1,
                                    $entry2
                                ]
                            }
                        """.trimIndent())
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andDo(document("cache/insert", requestFields(
                        fieldWithPath("entries")
                                .type(JsonFieldType.ARRAY)
                                .description("An Array of Entry object"),
                        fieldWithPath("entries[].uri")
                                .type(JsonFieldType.STRING)
                                .description("Entry URI"),
                        fieldWithPath("entries[].provider")
                                .type(JsonFieldType.STRING)
                                .description("Entry provider"),
                        fieldWithPath("entries[].title")
                                .type(JsonFieldType.STRING)
                                .description("Entry title"),
                        fieldWithPath("entries[].thumbnail")
                                .type(JsonFieldType.STRING)
                                .description("Entry thumbnail url"),
                        fieldWithPath("entries[].liked")
                                .type(JsonFieldType.BOOLEAN)
                                .description("Entry like state"),
                        fieldWithPath("entries[].meta")
                                .type(JsonFieldType.STRING)
                                .description("Entry meta information used in core server")
                )))
    }

    @Test
    @Order(2)
    fun testCacheInsertDuplicate() {
        this.mvc.perform(
                post("/cache")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "entries": [
                                    $entry1
                                ]
                            }
                        """.trimIndent())
        )
                .andDo(print())
                .andExpect(status().isConflict)
    }

    @Test
    @Order(3)
    fun testCacheInsertEmptyBody() {
        this.mvc.perform(
                post("/cache")
        )
                .andDo(print())
                .andExpect(status().isBadRequest)
    }

    @Test
    @Order(4)
    fun testCacheGet() {
        this.mvc.perform(
                get("/cache")
                        .param("uri", "https://www.youtube.com/watch?v=5BnhQbcPrBg")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json(entry1))
                .andDo(document("cache/get",
                        requestParameters(
                                parameterWithName("uri")
                                        .description("URI of entry")
                        ),
                        responseFields(
                                fieldWithPath("uri")
                                        .type(JsonFieldType.STRING)
                                        .description("entry URI"),
                                fieldWithPath("provider")
                                        .type(JsonFieldType.STRING)
                                        .description("entry provider"),
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("entry title"),
                                fieldWithPath("thumbnail")
                                        .type(JsonFieldType.STRING)
                                        .description("entry thumbnail URL"),
                                fieldWithPath("liked")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("entry is liked or not"),
                                fieldWithPath("meta")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("entry meta info")
                            )
                ))
    }

    @Test
    @Order(5)
    fun testCacheGetNotExist() {
        this.mvc.perform(
                get("/cache")
                        .param("uri", "https://not.found.sarisia.cc/watch?v=naidesu")
        )
                .andDo(print())
                .andExpect(status().isNotFound)
        // TODO: check error json response has "message" key
    }

    private val gpmEntry1 = """
        {
            "uri": "gpm:track:sarisia:abc-def-ghi",
            "gpmUser": "sarisia",
            "id": "abc-def-ghi",
            "title": "Kosmos, Cosmos",
            "artist": "萩原雪歩 (浅倉杏美)",
            "album": "THE IDOLM@STER MASTER ARTIST 3 09",
            "thumbnail": null
        }
    """.trimIndent()
    private val gpmBaseEntry1 = """
        {
            "uri": "gpm:track:sarisia:abc-def-ghi",
            "provider": "gpm",
            "title": "Kosmos, Cosmos - 萩原雪歩 (浅倉杏美)",
            "thumbnail": "",
            "liked": false,
            "meta": $gpmEntry1
        }
    """.trimIndent()
    private val gpmEntry2 = """
        {
            "uri": "gpm:track:sarisia:jkl-mno-pqr",
            "gpmUser": "sarisia",
            "id": "jkl-mno-pqr",
            "title": "Funny Logic",
            "artist": "高槻やよい (仁後真耶子) & 双海亜美 / 真美 (下田麻美)",
            "album": "THE IDOLM@STER MASTER PRIMAL DANCIN' BLUE",
            "thumbnail": null
        }
    """.trimIndent()

    @Test
    @Order(11)
    fun testGPMUpdate() {
        this.mvc.perform(
                post("/gpm/update")
                        .param("name", "sarisia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "entries": [
                                    $gpmEntry1,
                                    $gpmEntry2
                                ]
                            }
                        """.trimIndent())
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andDo(document("gpm/update",
                        requestFields(
                                fieldWithPath("entries")
                                        .type(JsonFieldType.ARRAY)
                                        .description("array of GPMEntry"),
                                fieldWithPath("entries[].uri")
                                        .type(JsonFieldType.STRING)
                                        .description("entry uri"),
                                fieldWithPath("entries[].gpmUser")
                                        .type(JsonFieldType.STRING)
                                        .description("entry user"),
                                fieldWithPath("entries[].id")
                                        .type(JsonFieldType.STRING)
                                        .description("song id"),
                                fieldWithPath("entries[].title")
                                        .type(JsonFieldType.STRING)
                                        .description("entry title"),
                                fieldWithPath("entries[].artist")
                                        .type(JsonFieldType.STRING)
                                        .description("entry artist"),
                                fieldWithPath("entries[].album")
                                        .type(JsonFieldType.STRING)
                                        .description("entry album"),
                                fieldWithPath("entries[].thumbnail")
                                        .type(JsonFieldType.STRING)
                                        .optional()
                                        .description("entry thumbnail URL")
                        )
                ))
    }

    @Test
    @Order(12)
    fun testGPMGet() {
        this.mvc.perform(
                get("/gpm")
                        .param("uri", "gpm:track:sarisia:abc-def-ghi")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json(gpmBaseEntry1))
    }

    @Test
    @Order(13)
    fun testGPMGetNotExist() {
        this.mvc.perform(
                get("/gpm")
                        .param("uri", "gpm:track:sarisia:not-exist-id")
        )
                .andDo(print())
                .andExpect(status().isNotFound)
    }

    @Test
    @Order(14)
    fun testGPMSearch() {
        this.mvc.perform(
                get("/gpm/search")
                        .param("query", "Cosmos")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("""
                    {
                        "results": [
                            $gpmEntry1
                        ]
                    }
                """.trimIndent()))
    }

    @Test
    @Order(15)
    fun testGPMSearchNotExist() {
        this.mvc.perform(
                get("/gpm/search")
                        .param("query", "NotExistEntry")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("""
                    {
                        "results": []
                    }
                """.trimIndent()))
    }

    @Test
    @Order(16)
    fun testGPMSearchCaseInsensitive() {
        this.mvc.perform(
                get("/gpm/search")
                        .param("query", "cosmos")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("""
                    {
                        "results": [
                            $gpmEntry1
                        ]
                    }
                """.trimIndent()))
    }

    val playlistName = "helesta"

    @Test
    @Order(21)
    fun testPlaylistGetPlaylistsEmpty() {
        this.mvc.perform(
                get("/playlist")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("""
                    {
                        "playlists": []
                    }
                """.trimIndent()))
    }

    @Test
    @Order(22)
    fun testPlaylistCreate() {
        this.mvc.perform(
                post("/playlist")
                        .param("name", playlistName)
        )
                .andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    @Order(23)
    fun testPlaylistGetEmpty() {
        this.mvc.perform(
                get("/playlist/$playlistName")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("""
                    {
                        "name": "helesta",
                        "length": 0,
                        "entries": []
                    }
                """.trimIndent()))
    }

    @Test
    @Order(24)
    fun testPlaylistInsert() {
        this.mvc.perform(
                post("/playlist/$playlistName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "entries": [
                                    "https://www.youtube.com/watch?v=5BnhQbcPrBg",
                                    "gpm:track:sarisia:abc-def-ghi"
                                ]
                            }
                        """.trimIndent())
        )
                .andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    @Order(25)
    fun testPlaylistGet() {
        this.mvc.perform(
                get("/playlist/$playlistName")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("""
                    {
                        "name": "helesta",
                        "length": 2
                    }
                """.trimIndent()))
    }

    @Test
    @Order(26)
    fun testPlaylistDelete() {
        this.mvc.perform(
                delete("/playlist")
                        .param("name", "helesta")
        )
                .andDo(print())
                .andExpect(status().isOk)
    }
}
