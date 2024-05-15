package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.reviews.ReviewsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewsController {

    private final ReviewsService reviewsService;

    @PostMapping
    public Review createReview(@RequestBody @Valid Review review) {
        return reviewsService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        return reviewsService.updateReview(review);
    }

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewsService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsByFilmId(@RequestParam(required = false) Integer filmId,
                                           @RequestParam(defaultValue = "10", required = false) Integer count) {
        return reviewsService.getReviewsByFilmId(filmId, count);
    }

    @DeleteMapping
    public void deleteAllReviews() {
        reviewsService.deleteAllReviews();
    }

    @DeleteMapping("{id}")
    public void deleteReviewById(@PathVariable int id) {
        reviewsService.deleteReviewById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewsService.addLike(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        reviewsService.addDislike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        reviewsService.deleteLike(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId) {
        reviewsService.deleteDislike(id, userId);
    }

}
