package com.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.community.mapper.CommunityMapper;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final CommunityMapper bookmarkMapper;

    public BookmarkController(CommunityMapper bookmarkMapper) {
        this.bookmarkMapper = bookmarkMapper;
    }

    @GetMapping
    public ResponseEntity<List<Long>> getBookmarkedPosts(@RequestParam Long userId) {
        List<Long> bookmarkedPostIds = bookmarkMapper.findBookmarkedPostIdsByUserId(userId);
        return ResponseEntity.ok(bookmarkedPostIds);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam Long userId, @RequestParam Long postId) {
        boolean isBookmarked = bookmarkMapper.isBookmarked(userId, postId);
        return ResponseEntity.ok(isBookmarked);
    }

    @PostMapping
    public ResponseEntity<Void> addBookmark(@RequestParam Long userId, @RequestParam Long postId) {
        boolean added = bookmarkMapper.addBookmark(userId, postId);
        if (!added) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeBookmark(@RequestParam Long userId, @RequestParam Long postId) {
        boolean removed = bookmarkMapper.removeBookmark(userId, postId);
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/toggle")
    public ResponseEntity<Boolean> toggleBookmark(@RequestParam Long userId, @RequestParam Long postId) {
        bookmarkMapper.toggleBookmark(userId, postId);
        boolean isBookmarked = bookmarkMapper.isBookmarked(userId, postId);
        return ResponseEntity.ok(isBookmarked);
    }
}