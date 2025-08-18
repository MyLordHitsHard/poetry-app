package com.poetry.poetry;


import org.junit.jupiter.api.BeforeEach;
import com.poetry.poetry.model.Poem;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.poetry.poetry.model.User;
import com.poetry.poetry.repository.UserRepository;
import com.poetry.poetry.repository.PoemRepository;

import java.util.List;
import  java.util.Optional;

@SpringBootTest
class PoetryApplicationTests {


	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepository;
	@MockBean
	private PoemRepository poemRepository;
//
//	@BeforeEach
//	void setUp() {
//		// Load environment variables from .env file
//		Dotenv dotenv = Dotenv.configure().load();
//		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
//
//		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//	}
	@Test
	void testGetPoems() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		mockMvc.perform(get("/api/poems")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void testCreatePoem() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();


		Authentication authentication = mock(Authentication.class);
		when(authentication.getName()).thenReturn("testuser");


		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);


		User mockUser = new User();
		mockUser.setId(1L);
		mockUser.setUsername("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

		Poem mockPoem = new Poem();
		mockPoem.setId(1L);
		mockPoem.setTitle("Test Poem");
		mockPoem.setContent("This is a test poem.");
		mockPoem.setUser(mockUser);
		when(poemRepository.save(any(Poem.class))).thenReturn(mockPoem);

		String poemJson = """
				{
				"title": "Test Poem",
					"content": "This is a test poem."
				}
				""";

		mockMvc.perform(post("/api/poems")
				.contentType(MediaType.APPLICATION_JSON)
				.content(poemJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value("Test Poem"))
				.andExpect(jsonPath("$.content").value("This is a test poem."));

	}

	@Test
	void testGetPoemById() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();


		Poem mockPoem = new Poem();
		mockPoem.setId(1L);
		mockPoem.setTitle("Test Poem");
		mockPoem.setContent("This is a test poem.");
		when(poemRepository.findById(1L)).thenReturn(Optional.of(mockPoem));


		mockMvc.perform(get("/api/poems/1")
						.accept(org.springframework.http.MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists());
	}

	@Test
	void testDeletePoem() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

		Authentication authentication = mock(Authentication.class);
		when(authentication.getName()).thenReturn("testuser");

		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		User mockUser = new User();
		mockUser.setId(1L);
		mockUser.setUsername("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

		Poem mockPoem = new Poem();
		mockPoem.setId(1L);
		mockPoem.setTitle("Test Poem");
		mockPoem.setContent("This is a test poem.");
		mockPoem.setUser(mockUser);
		when(poemRepository.findById(1L)).thenReturn(Optional.of(mockPoem));

		mockMvc.perform(delete("/api/poems/1")
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isNoContent());
	}

}
