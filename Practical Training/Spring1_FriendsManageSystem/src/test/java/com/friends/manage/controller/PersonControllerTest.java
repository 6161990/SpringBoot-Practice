package com.friends.manage.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.friends.manage.controller.dto.PersonDto;
import com.friends.manage.domain.Person;
import com.friends.manage.domain.dto.Birthday;
import com.friends.manage.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Slf4j
@Transactional
class PersonControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    void beforeEach(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .alwaysDo(print())
                .build();
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/person")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.content.[0].name").value("dennis"))
                .andExpect(jsonPath("$.content.[1].name").value("sophia"));
    }

    @Test
    void getPerson() throws Exception{
   //     mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("martin"))
                .andExpect(jsonPath("hobby").isEmpty())
                .andExpect(jsonPath("address").isEmpty())
                .andExpect(jsonPath("$.birthday").value("1991-08-15"))
                /* .andExpect(jsonPath("$.birthday.yearOfBirthday").value(1991))
                .andExpect(jsonPath("$.birthday.monthOfBirthday").value(8))
                .andExpect(jsonPath("$.birthday.dayOfBirthday").value(15)) */
                .andExpect(jsonPath("$.job").isEmpty())
                .andExpect(jsonPath("$.phoneNumber").isEmpty())
                .andExpect(jsonPath("$.deleted").value(false))
                .andExpect(jsonPath("$.age").isNumber())
                .andExpect(jsonPath("$.birthdayToday").isBoolean());

        /* but, test??? ?????? ??? ?????? ????????? ??????
           1. age : ????????? ?????? ??? ????????? ??????. ????????? ?????? ??? ?????? ????????? .. ?????? how? .value(false)) -> .isNumber())
           2. birthdayToday : ????????? ?????? ??????  test??? ????????? ??? ?????? ????????? ?????? ?????? : .value(false)) -> .isBoolean());
           3. birthday : ????????? ????????? ????????? 1991-08-15 ?????? ????????? ??????. JsonSerialization??? ????????? 1991-08-15 ????????? ????????? ????????????
         */
        //(jsonPath("$.name").value("martin")); ??? json????????? ?????? ??????
        //assertThat(result.getName()).isEqualsTo("martin"); ??? ?????? ????????? ?????? ?????? , ?????? ?????????.
        /*jsonPath
            $ : ????????? ?????????
            .name : ????????? name attribute??? ?????????, getName()??? ??????????????? ???????????? ???
            . ???????????? ????????? recursive?????? ????????? ????????? ??????
            value(A) ?????? A??? ???????????? ?????????
            imEmpty() ?????? ??? ????????? ?????????
            isNumber() ?????? ??????????????? ?????????
            isBoolean() true/false ????????? ?????????
             */
    }

    @Test
    void postPerson() throws Exception{
     /*   mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
        mockMvc.perform(
                 MockMvcRequestBuilders.post("/api/person")
      //          MockMvcRequestBuilders.post("/api/person?name=martin2&age=20&bloodType=A"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\n" +
                        "    \"name\":\"martin2\",\n" +
                        "    \"age\": 20,\n" +
                        "    \"bloodType\": \"A\"\n" +
                        "}"))
                .andDo(print())
               // .andExpect(status().isOk());
                .andExpect(status().isCreated());*/
        PersonDto dto = PersonDto.of("martin", "programming", "??????", LocalDate.now(), "programmer", "010-1111-2222");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/person")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(toJsonString(dto)))
                .andExpect(status().isCreated());

        Person result = personRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).get(0);

        assertAll(
                () -> assertThat(result.getName()).isEqualTo("martin"),
                () -> assertThat(result.getHobby()).isEqualTo("programming"),
                () -> assertThat(result.getAddress()).isEqualTo("??????"),
                () -> assertThat(result.getBirthday()).isEqualTo(Birthday.of(LocalDate.now())),
                () -> assertThat(result.getJob()).isEqualTo("programmer"),
                () -> assertThat(result.getPhoneNumber()).isEqualTo("010-1111-2222")
        );
    }

    @Test
    void postPersonIfNameIsNull() throws Exception{
        PersonDto dto = new PersonDto();
        dto.setName("");
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("????????? ??????????????????"));
    }

    @Test
    void postPersonIfNameIsEmptyString() throws Exception {
        PersonDto dto = new PersonDto();
        dto.setName("");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/person")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(toJsonString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("????????? ??????????????????"));
    }

    @Test
    void postPersonIfNameIsBlankString() throws Exception {
        PersonDto dto = new PersonDto();
        dto.setName(" ");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/person")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(toJsonString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("????????? ??????????????????"));
    }

    @Test
    void modifyPerson() throws Exception {
        PersonDto dto  = PersonDto.of("martin", "programming", "??????", LocalDate.now(), "programmer", "010-1111-2222");

    //    mockMvc = MockMvcBuilders.standaloneSetup(personController).build();

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/person/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                /*.content("{\n" +
                        "    \"name\":\"martin\""+
                        "}"))*/
                .content(toJsonString(dto)))
                .andExpect(status().isOk());

        //body??? content??? ????????? ?????????????????? ObjectMapper ??????.

        Person result = personRepository.findById(1L).get();

        assertAll( // ??? ????????? test??? ?????? ????????????  error??? ????????? ?????? ??????
                () -> assertThat(result.getName()).isEqualTo("martin"),
                () -> assertThat(result.getHobby()).isEqualTo("programming"),
                () -> assertThat(result.getAddress()).isEqualTo("??????"),
                () -> assertThat(result.getBirthday()).isEqualTo(Birthday.of(LocalDate.now())),
                () -> assertThat(result.getJob()).isEqualTo("programmer"),
                () ->assertThat(result.getPhoneNumber()).isEqualTo("010-1111-2222")
        );

    }

    @Test
    void modifyPersonIfNameIsDifferent() throws Exception {
        PersonDto dto  = PersonDto.of("james", "programming", "??????", LocalDate.now(), "programmer", "010-1111-2222");

     //   assertThrows(NestedServletException.class, () ->
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/person/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)

                        .content(toJsonString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("?????? ????????? ???????????? ????????????."));
    }

    @Test
    void modifyPersonIfPersonNotFound() throws Exception{
        PersonDto dto = PersonDto.of("martin", "programming", "??????", LocalDate.now(), "programmer", "010-1111-2222");

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/person/10")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(toJsonString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Person Entity??? ???????????? ????????????."));

    }



    private String toJsonString(PersonDto personDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(personDto); //personDto??? json???????????? serialization??????
    }

    @Test
    void checkJsonString() throws  JsonProcessingException{
        PersonDto dto = new PersonDto();
        dto.setName("martin");
        dto.setBirthday(LocalDate.now());
        dto.setAddress("??????");
        System.out.println(">>>"+ toJsonString(dto));
    }

    @Test
    void modifyName() throws Exception{
    //    mockMvc = MockMvcBuilders.standaloneSetup(personController).build();

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/person/1")
                .param("name", "martinModified"))
                .andExpect(status().isOk());
        assertThat(personRepository.findById(1L).get().getName()).isEqualTo("martinModified");
    }

    @Test
    void deletePerson() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/person/1"))
                .andExpect(status().isOk());
       //         .andExpect(content().string("true"));

        log.info("people deleted : {}", personRepository.findPeopleDeleted());

        //?????? ?????? ???????????? ??????????????? ????????? ????????? ???
        //?????? 1 : PersonContoller?????? return ?????? boolean true??? ??????
        //?????? 2 : ????????? ???????????? ???????????? ?????? return ????????? ??????( ?????? ???????????? ????????? ??????????????? ??? ??? ??????)
        //?????? 3 : test??? ?????? ?????? ????????? ???????????? ?????? ?????? ????????? ????????????, ????????? ?????? ????????????.
        assertTrue(personRepository.findPeopleDeleted().stream().anyMatch(person -> person.getId().equals(1L)));
    }

}