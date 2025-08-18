package com.poetry.poetry.controller;


import com.poetry.poetry.model.Poem;
import com.poetry.poetry.model.User;
import com.poetry.poetry.repository.PoemRepository;
import com.poetry.poetry.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/poems")
public class PoemController {

    @Autowired
    private PoemRepository poemRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public List<Poem> getAllPoems() {
        return poemRepository.findAll();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Poem> getPoemById(@PathVariable Long id) {
        Optional<Poem> poem = poemRepository.findById(id);
        return poem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Poem> createPoem(@RequestBody Poem poem) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("User is not authenticated.");
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        poem.setUser(user);
        Poem savedPoem = poemRepository.save(poem);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPoem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePoem(@PathVariable Long id, @RequestBody Poem poemDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("User is not authenticated.");
        }
        Optional<Poem> optionalPoem = poemRepository.findById(id);
        if (optionalPoem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Poem poem = optionalPoem.get();
        String username = authentication.getName();
        if (!poem.getUser().getUsername().equals(username)) {
            return ResponseEntity.status(403).body("You can only update your own poems.");
        }

        poem.setTitle(poemDetails.getTitle());
        poem.setContent(poemDetails.getContent());
        Poem updatedPoem = poemRepository.save(poem);
        return ResponseEntity.ok(updatedPoem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("User is not authenticated.");
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        Poem poem = poemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Poem not found: " + id));

        if (!poem.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not authorized
        }

        poemRepository.delete(poem);
        return ResponseEntity.noContent().build();
    }


//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deletePoem(@PathVariable Long id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication.getName() == null) {
//            throw new IllegalStateException("User is not authenticated.");
//        }
//        Optional<Poem> optionalPoem = poemRepository.findById(id);
//        if (optionalPoem.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Poem poem = optionalPoem.get();
//        String username = authentication.getName();
//        if (!poem.getUser().getUsername().equals(username)) {
//            return ResponseEntity.status(403).body("You can only delete your own poems.");
//        }
//
//        poemRepository.delete(poem);
//        return ResponseEntity.ok().build();
//    }
}
