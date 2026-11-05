package com.pierre.tasks;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createTasksAndRetrievesIt() throws Exception{
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"Study MockMvc"}
                        """
                ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Study MockMvc"))
                .andExpect(jsonPath("$.accomplished").value("false"))
                .andExpect(header().exists("Location"))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        result.getResponse().getContentAsString()
                ));
    }

    @Test
    void rejectBlankTitle() throws Exception{
         mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":" "}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("One or more fields are invalid"))
                .andExpect(jsonPath("$.errors")
                        .value(Matchers.hasItem(
                                "title: Title must not be blank"
                        )));
    }

    @Test
    void rejectsNonNumericId() throws Exception{
        mockMvc.perform(get("/tasks/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Parameter 'id' has an invalid value"));
    }

    @Test
    void updateTaskAccomplishment() throws Exception{
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"Test PATCH"}
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        mockMvc.perform(patch(location)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"accomplished":true}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accomplished").value(true));

        mockMvc.perform(patch(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"accomplished":false}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accomplished").value(false));

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accomplished").value(false))
                .andExpect(jsonPath("$.title").value("Test PATCH"));
    }

    @Test
    void deletesTask() throws Exception{
        MvcResult result = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"title":"Test delete"}
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void rejectMissingAccomplishment() throws Exception{
        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"Reject missing accomplishment"}
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        mockMvc.perform(patch(location)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_PROBLEM_JSON
                ))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(
                        "One or more fields are invalid"
                ))
                .andExpect(jsonPath("$.errors")
                        .value(Matchers.hasItem(
                                "accomplished: Accomplished must be provided"
                        )));

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accomplished").value(false));
    }
}
