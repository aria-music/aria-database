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
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "docs")
class AriaApplicationTests {
    @Autowired
    lateinit var mvc: MockMvc

    @Test
    @Order(1)
    fun testCacheInsert() {
        this.mvc.perform(
                post("/cache")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "entries": [
                                    {
                                        "uri": "https://www.youtube.com/watch?v=5BnhQbcPrBg",
                                        "provider": "youtube",
                                        "title": "【新衣装で】ビースト・ダンス　歌ってみた【相羽ういは/にじさんじ】",
                                        "thumbnail": "https://i.ytimg.com/vi/5BnhQbcPrBg/maxresdefault.jpg",
                                        "liked": false,
                                        "meta": "UihaCute"
                                    },
                                    {
                                        "uri": "https://www.youtube.com/watch?v=z3tukOqkjyc",
                                        "provider": "youtube",
                                        "title": "【】歌ってみた 疑心暗鬼 葛葉 にじさん【】",
                                        "thumbnail": "https://i.ytimg.com/vi/z3tukOqkjyc/maxresdefault.jpg",
                                        "liked": false,
                                        "meta": "KuzuhaCool"
                                    }
                                ]
                            }
                        """.trimIndent())
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andDo(document("cache", requestFields(
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
                                    {
                                        "uri": "https://www.youtube.com/watch?v=5BnhQbcPrBg",
                                        "provider": "youtube",
                                        "title": "【新衣装で】ビースト・ダンス　歌ってみた【相羽ういは/にじさんじ】",
                                        "thumbnail": "https://i.ytimg.com/vi/5BnhQbcPrBg/maxresdefault.jpg",
                                        "liked": false,
                                        "meta": "UihaCute"
                                    }
                                ]
                            }
                        """.trimIndent())
        )
                .andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    fun testCacheInsertEmptyBody() {
        this.mvc.perform(
                post("/cache")
        )
                .andDo(print())
                .andExpect(status().isBadRequest)
    }

    @Test
    @Order(3)
    fun testCacheGet() {
        this.mvc.perform(
                get("/cache")
                        .param("uri", "https://www.youtube.com/watch?v=5BnhQbcPrBg")
        )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("""
                    {
                        "uri": "https://www.youtube.com/watch?v=5BnhQbcPrBg",
                        "provider": "youtube",
                        "title": "【新衣装で】ビースト・ダンス　歌ってみた【相羽ういは/にじさんじ】",
                        "thumbnail": "https://i.ytimg.com/vi/5BnhQbcPrBg/maxresdefault.jpg",
                        "liked": false,
                        "meta": "UihaCute"
                    }
                """.trimIndent()))
    }

    @Test
    fun testCacheGetNotExist() {
        this.mvc.perform(
                get("/cache")
                        .param("uri", "https://not.found.sarisia.cc/watch?v=naidesu")
        )
                .andDo(print())
                .andExpect(status().isNotFound)
        // TODO: check error json response has "message" key
    }

//    @Test
//    fun testGPMUpdate() {
//        this.mvc.perform(
//                post("/gpm/update")
//                        .param("name", "sarisia")
//                        .content("""
//
//                        """.trimIndent())
//        )
//                .andDo()
//    }

}
