package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
import com.cydeo.dto.TestResponseDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.enums.Gender;
import com.cydeo.enums.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    static String token;   // I want to use these fields in before method, then it should be static.

    static UserDTO manager;
    static ProjectDTO project;

    @BeforeAll
    static void setUp() {

        //    token = "Bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXLTdFc0kzVjVTRk5acUZuY201WW8zVVBkQjVIblNtTm9GX0d5WjJjVk44In0.eyJleHAiOjE2NzE2MDQ5NDYsImlhdCI6MTY3MTU4Njk0NiwianRpIjoiYmE5OWFjMGUtMGNhOC00Mzk4LTkwNmYtMmUyYWRhMWY1MDAyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL2N5ZGVvLWRldiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJjYTcyZTU2MS05ZjFlLTQzNTQtOTdlNC0xOWFlN2NhY2NlY2EiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ0aWNrZXRpbmctYXBwIiwic2Vzc2lvbl9zdGF0ZSI6ImQwYmMyN2QyLTI4MTQtNDc3ZS05MmExLTQwNTIzYmMzOTg0NSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgxIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtY3lkZW8tZGV2IiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJ0aWNrZXRpbmctYXBwIjp7InJvbGVzIjpbIk1hbmFnZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJzaWQiOiJkMGJjMjdkMi0yODE0LTQ3N2UtOTJhMS00MDUyM2JjMzk4NDUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicHJlZmVycmVkX3VzZXJuYW1lIjoib3p6eSJ9.W_-xNGcrUl5cwvCFiewY8IX-PUZ7DMlihCGFy0Mvk-kR-Uu-XWOuL3KZddlcO55C23xAAG3gjvsyxd50D1btOppAbfoVCsueWzcAO6a2c0ulIIx2yTDjOuUje2aWB4QKsH1YH93N41qeO0t9G3qUImhiNQWFGo-RKVbnc2YuJ_mf_5QbVfxPOPPcOiePmnBAf99re3ZQ5oR-XVZq3wGKWt6eJ62AdTRa82lRwY3E6SBClipHn63Q0Wdp_vGZOfM_3qYuRS3Bj2Mda3S6f847zOMk0RlvfKcW8hBTm_KhF1hpx9D_x1Z9HZwC8h57u7C2tWhxvX523eJhwp9BiPLttA";
        token = "Bearer " + getToken();  // I used this token for last method ( getToken()) that don't need postman

        manager = new UserDTO(2L,
                "",
                "",
                "ozzy",
                "abc1",
                "",
                true,
                "",
                new RoleDTO(2L, "Manager"),
                Gender.MALE);


        project = new ProjectDTO(
                "API Project",
                "PR001",
                manager,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                "Some details",
                Status.OPEN
        );

    }

    @Test
    void givenNoToken_getProjects() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/project")).andExpect(status().is4xxClientError());

    }

    @Test
    void givenToken_getProjects() throws Exception {     // hard coding for token
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/project")
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].projectCode").exists())
                .andExpect(jsonPath("$.data[0].assignedManager.userName").exists())  //if we have a userName field or not?
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isNotEmpty())  //if userNameValue is empty or not
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isString())  //is userName coming with string format or not?
                .andExpect(jsonPath("$.data[0].assignedManager.userName").value("ozzy")); //value of userName is ozzy or not ?

    }

    @Test
    void givenToken_createProject() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/project")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJsonString(project)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Project is successfully created"));
    }

    @Test
    void givenToken_updateProject() throws Exception {

        project.setProjectName("API Project-2");

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/project")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJsonString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project is successfully updated"));
    }

    @Test
    void givenToken_deleteProject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/project/" + project.getProjectCode())
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project is successfully deleted"));


    }


    // below method we don't need to get token from postman
    private static String getToken() {

        RestTemplate restTemplate = new RestTemplate();  // unlike MockMvc, RestTemplate is real API requested from keycloak

        HttpHeaders headers = new HttpHeaders();         // we need this header to get a token from keycloak
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("grant_type", "password");
        map.add("client_id", "ticketing-app");
        map.add("client_secret", "E1Q8zbuazFOUYmFCWv6bgMVyEGXbOldC");
        map.add("username", "ozzy");
        map.add("password", "abc1");
        map.add("scope", "openid");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<TestResponseDTO> response =
                restTemplate.exchange("http://localhost:8080/auth/realms/cydeo-dev/protocol/openid-connect/token",
                        HttpMethod.POST,
                        entity,
                        TestResponseDTO.class);

        if (response.getBody() != null) {
            return response.getBody().getAccess_token();
        }

        return "";

    }


    // Below code is ready code and i can google it for String to Json or reverse.
    private String toJsonString(final Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);   // only Date
        objectMapper.registerModule(new JavaTimeModule());                                //  2022,12,18  ->  2022/12/18
        return objectMapper.writeValueAsString(obj);                                       //  {"projectCode": "Code", ......}
    }


}