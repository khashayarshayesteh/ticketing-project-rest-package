package com.cydeo.controller;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.RoleDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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

        token = "Bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXLTdFc0kzVjVTRk5acUZuY201WW8zVVBkQjVIblNtTm9GX0d5WjJjVk44In0.eyJleHAiOjE2NzE1MzYwNDIsImlhdCI6MTY3MTUxODA0MiwianRpIjoiNWRiODM3MzAtNTIxMS00NzNkLTk2OTMtYWNmMDI3OTExMjdiIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL2N5ZGVvLWRldiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJjYTcyZTU2MS05ZjFlLTQzNTQtOTdlNC0xOWFlN2NhY2NlY2EiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ0aWNrZXRpbmctYXBwIiwic2Vzc2lvbl9zdGF0ZSI6IjJlYTkwMDgwLTRmNzEtNDhiMS1iODY3LTU3YWRkZDJlNjFjNyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgxIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtY3lkZW8tZGV2IiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJ0aWNrZXRpbmctYXBwIjp7InJvbGVzIjpbIk1hbmFnZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJzaWQiOiIyZWE5MDA4MC00ZjcxLTQ4YjEtYjg2Ny01N2FkZGQyZTYxYzciLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicHJlZmVycmVkX3VzZXJuYW1lIjoib3p6eSJ9.FQ5AyiZ4JFDT4D_fFvRRh1HCLnVDRuCKdMzNOFzVt4xtTZ_KNKNNQOdD1DmIb4dZjNJP1ZzhYKII5pZUJv4jnjvTtyJ6Yhz8A14asMIuRzJFraWPY2bvgSEmGy2qaaU26HcQaahxy-9fqwxsrxBKejAF780rsK5HKw926k-QvnQNa0X5tfTDbeQVlBxuxLCXJG2hsEH2V43AsARGjls8xHCQMv6G_6dXb-qbyp6mHnVmD3sfEm3Qdas3CQ-UpS9K-QixVwUKX00SRkWB216qp8mWIUTL8ZEA4Bab_xLv6LvetT1buGgePy-WHNcqscvBJYTGvqlnnDJfEf7pM436ag";
//        token = "Bearer " + getToken();

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
                    .header("Authorization" , token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(toJsonString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project is successfully updated"));
    }

    @Test
    void givenToken_deleteProject() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/project/"+ project.getProjectCode())
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project is successfully deleted"));


    }




    // Below code is ready code and i can google it for String to Json or reverse.
    private String toJsonString(final Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);   // only Date
        objectMapper.registerModule(new JavaTimeModule());                                //  2022,12,18  ->  2022/12/18
        return objectMapper.writeValueAsString(obj);                                       //  {"projectCode": "Code", ......}
    }


}