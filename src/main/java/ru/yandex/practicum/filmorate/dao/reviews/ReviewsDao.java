package ru.yandex.practicum.filmorate.dao.reviews;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsDao {

    Review createReview(Review review);

    Review updateReview(Review review);

    List<Review> getAllReviews();

    List<Review> getReviewsByFilmId(Integer filmId, Integer count);

    Review getReviewById(Integer id);

    void deleteAllReviews();

    void deleteReviewById(Integer id);

}